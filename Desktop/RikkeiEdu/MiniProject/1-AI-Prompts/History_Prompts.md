

---

### PROMPT 1: VAI TRÒ SENIOR BA & BÓC TÁCH YÊU CẦU THÔ
**Mục tiêu:** Phân tích yêu cầu thô từ buổi họp với Giám đốc TechNova để bóc tách các tác nhân (Actors), yêu cầu chức năng (FR) và yêu cầu phi chức năng (NFR).

```markdown
Đóng vai là một Senior Business Analyst (Senior BA) và System Analyst. Tôi có một bản ghi chép yêu cầu thô từ cuộc họp với Giám đốc của công ty bán lẻ thiết bị điện tử "TechNova" như sau:

"Tôi muốn một cái hệ thống web để bán hàng. Khách hàng có thể lên đó xem điện thoại, laptop, bỏ vào giỏ hàng rồi thanh toán. Cơ mà trước khi mua họ phải đăng ký tài khoản và đăng nhập nhé. Hệ thống phải bảo mật, không được để lộ thông tin khách.
Về phần nhân viên của tôi, họ cần một trang quản trị (Admin). Trong đó, nhân viên kho thì chỉ được thêm, sửa, xóa sản phẩm. Còn quản lý (Manager) thì được xem thống kê doanh thu và cấp quyền cho nhân viên khác. Khách hàng tuyệt đối không được mò vào trang của Admin. Hệ thống sau này dự kiến có hàng chục ngàn người truy cập cùng lúc nên API phải chạy nhanh, cơ sở dữ liệu không được sập."

Hãy giúp tôi:
1. Xác định và mô tả rõ các Tác nhân (Actors) tham gia vào hệ thống.
2. Bóc tách và chuẩn hóa các Yêu cầu chức năng (Functional Requirements - FR) chi tiết cho từng tác nhân.
3. Phân tích các Yêu cầu phi chức năng (Non-functional Requirements - NFR) về hiệu năng, bảo mật và khả năng mở rộng.
Trình bày kết quả phân tích một cách có cấu trúc, khoa học, chuyên nghiệp để phục vụ cho việc làm tài liệu đặc tả SRS.
```

---

### PROMPT 2: THIẾT KẾ SƠ ĐỒ USE CASE & ERD BẰNG MERMAID
**Mục tiêu:** Thiết kế Sơ đồ Use Case tổng quan và Sơ đồ thực thể kết hợp (ERD) bằng cú pháp Mermaid.

```markdown
Dựa trên kết quả bóc tách yêu cầu của dự án Smart E-Shop TechNova ở trên, hãy:
1. Sinh mã code Mermaid để vẽ Sơ đồ Use Case tổng quan thể hiện rõ System Boundary (Ranh giới hệ thống), các Actors và các Use Case cốt lõi của họ.
2. Sinh mã code Mermaid để vẽ Sơ đồ thực thể kết hợp (ERD - erDiagram) mô tả cấu trúc Cơ sở dữ liệu. Sơ đồ ERD cần bao gồm các bảng cốt lõi: Users, Roles, Products, Orders, Order_Items. Hãy chỉ rõ khóa chính (PK), khóa ngoại (FK), các trường dữ liệu quan trọng, kiểu dữ liệu phù hợp để ánh xạ sang các Entity Java Spring Boot (như Long, String, BigDecimal, Integer, LocalDateTime) và mối quan hệ giữa các bảng.
```

---

### PROMPT 3: TỔNG HỢP TÀI LIỆU SRS CHUẨN IEEE 830
**Mục tiêu:** Tổng hợp toàn bộ các phần phân tích, sơ đồ trên thành tài liệu SRS chuẩn IEEE 830 hoàn chỉnh.

```markdown
Hãy đóng vai là một Senior System Analyst. Hãy tổng hợp toàn bộ kết quả phân tích nghiệp vụ, danh sách tác nhân, các yêu cầu chức năng (FR) dưới dạng Agile User Stories (gồm bảng có mã ID, vai trò, hành động, lợi ích, và tiêu chí chấp nhận Acceptance Criteria), các yêu cầu phi chức năng (NFR), cùng với mã Mermaid của Sơ đồ Use Case và ERD thành một tài liệu Đặc tả Yêu cầu Phần mềm (SRS - Software Requirements Specification) hoàn chỉnh theo tiêu chuẩn IEEE 830.
Tài liệu phải có các mục rõ ràng:
1. Introduction (Mục đích, Phạm vi dự án, Định nghĩa).
2. Overall Description (Bối cảnh hệ thống, Các tác nhân và vai trò, Các chức năng chính, Giới hạn hệ thống).
3. Specific Requirements (Yêu cầu chức năng chi tiết dưới dạng bảng User Stories, Sơ đồ Use Case, Sơ đồ ERD, Yêu cầu phi chức năng).
Lưu ý trình bày chuyên nghiệp, mạch lạc, trực tiếp vào nội dung tài liệu để có thể xuất trực tiếp ra file PDF/Word phục vụ nộp bài.
```

---

### PROMPT 4: PHÁT TRIỂN ENTITY JPA TRÁNH LỖI N+1 QUERY
**Mục tiêu:** Sinh mã nguồn Java cho các Entity JPA ánh xạ từ ERD và áp dụng các quy chuẩn để tránh lỗi N+1 Query.

```markdown
Tôi đang phát triển Backend API cho dự án Smart E-Shop bằng Spring Boot 3.x và JPA/Hibernate. 
Dựa trên sơ đồ ERD của dự án gồm các bảng: Users, Roles, Products, Orders, Order_Items.
Hãy sinh mã nguồn cho các Java Entity Classes tương ứng: User, Role, Product, Order, OrderItem.
Yêu cầu kỹ thuật bắt buộc:
1. Áp dụng FetchType.LAZY cho toàn bộ các quan hệ (như @ManyToMany giữa User-Role, @OneToMany/@ManyToOne giữa Order-OrderItem-Product) để tránh tải thừa dữ liệu.
2. Sử dụng các JPA Annotation đúng chuẩn, cấu hình khóa tự tăng, timestamps (created_at, updated_at).
3. Viết các lớp Repository Spring Data JPA tương ứng cho từng Entity. Trong đó, chỉ ra cách viết @EntityGraph (hoặc JOIN FETCH JPQL) để tải thông tin liên kết một cách tối ưu, giải quyết triệt để bài toán N+1 Query khi cần lấy User kèm Roles hoặc Order kèm OrderItems.
```

---

### PROMPT 5: CẤU HÌNH SPRING SECURITY 6 & JWT PHÂN QUYỀN ENDPOINTS
**Mục tiêu:** Sinh mã cấu hình bảo mật Spring Security 6 & JWT, thực hiện phân quyền API theo RBAC và custom JSON exception cho các lỗi 401, 403.

```markdown
Hãy sinh mã cấu hình bảo mật Spring Security 6 kết hợp JWT (JSON Web Token) cho dự án Smart E-Shop với các yêu cầu:
1. Hỗ trợ việc phân quyền dựa trên vai trò (Role-based Access Control - RBAC). Các Role bao gồm: ROLE_CUSTOMER, ROLE_STAFF, ROLE_MANAGER.
2. Thiết lập quyền truy cập cho các API endpoints:
   - GET `/api/v1/public/**` (Xem sản phẩm, Đăng nhập/Đăng ký) -> Mọi người đều truy cập được (Permit All).
   - `/api/v1/admin/products/**` (Thêm, sửa, xóa sản phẩm) -> Chỉ cho phép người dùng có vai trò STAFF hoặc MANAGER.
   - `/api/v1/admin/users/**` (Xem thống kê doanh thu, gán vai trò nhân viên) -> Chỉ cho phép người dùng có vai trò MANAGER.
3. Viết các class hỗ trợ JWT: JwtTokenProvider (generate/validate token), JwtAuthenticationFilter (lọc và giải mã token từ Authorization header Bearer).
4. Thực hiện cấu hình Custom AuthenticationEntryPoint (cho lỗi 401 Unauthorized) và AccessDeniedHandler (cho lỗi 403 Forbidden) để trả về phản hồi định dạng JSON chứa mã lỗi, thời gian và thông tin lỗi chi tiết thay vì trang HTML mặc định của Spring Boot.
```

---

### PROMPT 6: PHÁT TRIỂN CORE BUSINESS LOGIC (CONTROLLER, SERVICE, DTO) VỚI VALIDATION
**Mục tiêu:** Sinh mã cho DTOs, Services, Controllers và tích hợp Validation cho dữ liệu đầu vào.

```markdown
Hãy xây dựng mã nguồn đầy đủ cho các tầng Controller, Service và DTO của các chức năng chính trong dự án Smart E-Shop TechNova:
1. Chức năng Đăng ký & Đăng nhập (Auth).
2. Chức năng xem danh sách sản phẩm public, quản lý thêm/sửa/xóa sản phẩm trong admin.
3. Chức năng tạo đơn hàng (đặt hàng) dành cho khách hàng đã đăng nhập.
4. Chức năng xem doanh thu và gán vai trò nhân viên dành cho quản trị viên (Manager).
Yêu cầu:
- Sử dụng các DTO để truyền nhận dữ liệu.
- Áp dụng Java Bean Validation trên các DTO để xác thực dữ liệu đầu vào (ví dụ: tên sản phẩm không được trống, giá sản phẩm > 0, email đúng định dạng, mật khẩu tối thiểu 6 ký tự).
- Xử lý các nghiệp vụ kiểm tra logic (ví dụ: kiểm tra số lượng tồn kho trước khi đặt hàng, trừ số lượng tồn kho khi đặt hàng thành công).
```
