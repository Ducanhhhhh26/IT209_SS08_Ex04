## 1. Phân tích & Khắc phục lỗi đặt tên file cấu hình

### 1.1. Lỗi đặt tên file hiện tại
* **Tên file do thực tập sinh tạo:** `config.yml`
* **Thuộc tính định danh của service:** `spring.application.name: restaurant-service`
* **Nguyên nhân lỗi:** Tên file `config.yml` không tuân theo quy tắc ánh xạ tài nguyên cấu hình (Resource Path Convention) của Spring Cloud Config Server.

### 1.2. Cơ chế phân giải cấu hình của Spring Cloud Config Server
Spring Cloud Config Server quản lý và cung cấp cấu hình qua các REST Endpoints theo chuẩn:
```http
GET /{application}/{profile}[/{label}]
GET /{application}-{profile}.yml
GET /{application}.yml
```
Khi một client microservice khởi động, nó gửi `spring.application.name` và `spring.profiles.active` lên Config Server. Config Server sẽ tìm kiếm các file cấu hình trong kho Git theo thứ tự ưu tiên:
1. `{application}-{profile}.yml` (hoặc `.properties`) — Cấu hình đặc thù theo môi trường (`dev`, `prod`,...).
2. `{application}.yml` (hoặc `.properties`) — Cấu hình mặc định cho chính service đó.
3. `application-{profile}.yml` — Cấu hình chung cho mọi service chạy profile tương ứng.
4. `application.yml` — Cấu hình dùng chung (global) cho toàn hệ thống.

### 1.3. Hậu quả khi đặt sai tên file (`config.yml`)
* **Không nhận được cấu hình riêng biệt:** Config Server không thể liên kết `restaurant-service` với file `config.yml`. Các thiết lập quan trọng như Datasource (`restaurants_db`), thông tin đăng nhập và cổng dịch vụ (`server.port: 8085`) sẽ bị bỏ qua hoàn toàn.
* **Lỗi khởi động dịch vụ (Application Startup Crash):** Service thiếu thông tin kết nối Cơ sở dữ liệu và các Bean cấu hình JPA/Datasource sẽ ném ngoại lệ không thể khởi tạo.
* **Xung đột cổng mạng (Port Conflict):** Service tự động fallback về cổng mặc định `8080`, gây xung đột cổng nếu chạy nhiều service trên cùng máy chủ vật lý hoặc container.

### 1.4. Đổi lại tên file đúng quy ước
* **Tên file chuẩn:** `restaurant-service.yml`

---

## 2. Phân tích Rủi ro Plaintext Password & Giải pháp Mã hóa `{cipher}`

### 2.1. Rủi ro khi lưu mật khẩu Plaintext (`RestaurantPass123`)
* **Lộ lọt thông tin xác thực nội bộ (Credential Leakage):** Bất kỳ thành viên nào có quyền truy cập repository (Developers, QA, DevOps, thực tập sinh) hoặc các bên thứ 3 tích hợp CI/CD đều có thể đọc được mật khẩu database production.
* **Nguy cơ tấn công nghiêm trọng khi rò rỉ mã nguồn:** Nếu kho Git bị xâm nhập hoặc vô tình công khai (public), kẻ tấn công dễ dàng xâm nhập database để đánh cắp, thao túng hoặc phá hủy dữ liệu.
* **Vi phạm các chuẩn bảo mật & tuân thủ:** Vi phạm nghiêm trọng các tiêu chuẩn bảo mật quốc tế như **PCI-DSS**, **ISO 27001**, **SOC 2** và các nguyên tắc bảo mật của **OWASP Top 10**.

### 2.2. Giải pháp mã hóa với Spring Cloud Config Encryption (`{cipher}`)
Spring Cloud Config Server hỗ trợ cơ chế mã hóa đối xứng (Symmetric Encryption) hoặc bất đối xứng (Asymmetric RSA Encryption).
* **Cú pháp quy ước:** Thêm tiền tố `{cipher}` trước chuỗi mã hóa và bọc trong dấu nháy đơn `'...'`:
  ```yaml
  password: '{cipher}AQB4k1l9m0N8vX7zY3w2P1q0R9s8T7u6V5w4X3y2Z1a0...'
  ```
* **Quy trình hoạt động:**
  1. Admin mã hóa mật khẩu thông qua endpoint của Config Server:
     ```bash
     curl -X POST http://localhost:8888/encrypt -d "RestaurantPass123"
     ```
  2. Kết quả mã hóa được thêm tiền tố `{cipher}` và lưu an toàn trên Git.
  3. Khi Client gửi yêu cầu lấy cấu hình, Config Server tự động giải mã mật khẩu bằng khóa bí mật và truyền payload bảo mật về Client qua đường truyền TLS/HTTPS.

---

## 3. Nội dung 3 File cấu hình hoàn chỉnh của FoodX

### 3.1. File `restaurant-service.yml`
```yaml
server:
  port: 8085

spring:
  application:
    name: restaurant-service
  datasource:
    url: jdbc:mysql://foodx-cluster.local:3306/restaurants_db
    username: restaurant_service_user
    password: '{cipher}AQB4k1l9m0N8vX7zY3w2P1q0R9s8T7u6V5w4X3y2Z1a0B9c8D7e6F5g4H3i2J1k0L9m8N7o6P5q4R3s2T1u0V9w8X7y6Z5a4B3c2D1e0F9g8H7i6J5k4L3m2N1o0P9q8R7s6T5u4V3w2X1y0Z9a8B7c6D5e4F3g2'
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
```

### 3.2. File `order-service.yml`
```yaml
server:
  port: 8081

spring:
  application:
    name: order-service
  datasource:
    url: jdbc:mysql://foodx-cluster.local:3306/orders_db
    username: order_service_user
    password: '{cipher}AQC8x2m0n1O9wY8aZ4x3Q2r1S0t9U8v7W6x5Y4z3A2b1C0d9E8f7G6h5I4j3K2l1M0n9O8p7Q6r5S4t3U2v1W0x9Y8z7A6b5C4d3E2f1G0h9I8j7K6l5M4n3O2p1Q0r9S8t7U6v5W4x3Y2z1A0b9C8d7E6f5G4h3'
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
```

### 3.3. File `delivery-service.yml`
```yaml
server:
  port: 8083

spring:
  application:
    name: delivery-service
  datasource:
    url: jdbc:mysql://foodx-cluster.local:3306/delivery_db
    username: delivery_service_user
    password: '{cipher}AQD9y3n1o2P0xZ9bA5y4R3s2T1u0V9w8X7y6Z5a4B3c2D1e0F9g8H7i6J5k4L3m2N1o0P9q8R7s6T5u4V3w2X1y0Z9a8B7c6D5e4F3g2H1i0J9k8L7m6N5o4P3q2R1s0T9u8V7w6X5y4Z3a2B1c0D9e8F7g6H5i4'
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
```

---

## 4. Mô tả Cấu trúc & Hướng dẫn Vận hành Kho cấu hình Git Repository

### 4.1. Cấu trúc thư mục (Repository Structure)
```text
config-repo/
├── application.yml              # Cấu hình chung cho tất cả các service (Eureka, Actuator,...)
├── restaurant-service.yml       # Cấu hình riêng cho Restaurant Service (Port: 8085, DB: restaurants_db)
├── order-service.yml            # Cấu hình riêng cho Order Service (Port: 8081, DB: orders_db)
└── delivery-service.yml         # Cấu hình riêng cho Delivery Service (Port: 8083, DB: delivery_db)
```

### 4.2. Bảng phân bổ cấu hình các Microservices

| Service Name | Port | Tên file cấu hình | Database | Trạng thái mật khẩu |
| :--- | :---: | :--- | :--- | :---: |
| **Common (Global)** | - | `application.yml` | - | - |
| **Restaurant Service** | `8085` | `restaurant-service.yml` | `restaurants_db` | `{cipher}` Encrypted |
| **Order Service** | `8081` | `order-service.yml` | `orders_db` | `{cipher}` Encrypted |
| **Delivery Service** | `8083` | `delivery-service.yml` | `delivery_db` | `{cipher}` Encrypted |

### 4.3. Quy ước và Nguyên tắc hoạt động
1. **Tách biệt cấu hình khỏi mã nguồn (Separation of Concerns):** Không lưu trữ bất kỳ cấu hình môi trường hoặc kết nối hạ tầng nào bên trong source code của service.
2. **Quy ước đặt tên file:** Tên file cấu hình bắt buộc phải trùng khớp với `spring.application.name` của microservice để Config Server tự động định tuyến.
3. **An toàn thông tin (Zero Plaintext Secrets):** Tất cả mật khẩu, khóa bí mật, API key bắt buộc phải được mã hóa dạng `{cipher}` trước khi commit lên Git.
4. **Cấu hình dùng chung (Shared Configuration):** Các thông số chung như Eureka Server discovery (`http://localhost:8761/eureka/`), cấu hình Spring Actuator endpoints (`/actuator/refresh`, `/actuator/health`) được đặt tập trung trong `application.yml`.
