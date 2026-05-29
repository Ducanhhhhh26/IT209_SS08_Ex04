# Phần 1 - Phân tích

**Tại sao `spring-boot-starter-web` được gọi là một "Starter"?**
-  "Starter" là một tập hợp các dependency descriptor tiện lợi mà bạn có thể cấu hình bao gồm trong ứng dụng của mình. Thay vì phải đi tìm và thêm từng thư viện cụ thể một cách thủ công, bạn chỉ cần báo cho Spring Boot một Starter cụ thể nào đó (ở đây là `spring-boot-starter-web`) và nó sẽ tự động có mặt các thư viện cần thiết.

**Nó giúp chúng ta không phải khai báo những thư viện nhỏ lẻ nào?**
- Khi khai báo `spring-boot-starter-web`, nó tự động cấu hình và cung cấp các thư viện liên quan như:
  - **Spring MVC**: Cung cấp framework để xây dựng các ứng dụng web và RESTful.
  - **Embedded Tomcat**: Máy chủ HTTP Tomcat được tích hợp sẵn giúp ta có thể chạy ứng dụng mà không cần phải cài đặt máy chủ bên ngoài.
  - **Jackson**: Thư viện parse JSON để làm việc với dữ liệu JSON.
  - **Spring Beans, Spring Core, Spring Context**: Các core module của Spring Framework. 
  - **Validation**: Các công cụ để validate dữ liệu đầu vào.
