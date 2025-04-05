# Hệ Thống ATM - Kiến Trúc Hướng Dịch Vụ

## 📋 Mục Tiêu  

### Đối Với Khách Hàng:
- Tạo ra hệ thống ATM **thân thiện và dễ sử dụng**.
- Đảm bảo **bảo mật dữ liệu** thông qua mã hóa.
- **Nâng cao tốc độ giao dịch** nhằm giảm thời gian xử lý khi rút tiền và chuyển khoản.
- Hỗ trợ **nhiều phương thức thanh toán**, cho phép rút tiền bằng thẻ hoặc OTP.
- Cung cấp **dịch vụ 24/7**, giúp khách hàng thực hiện giao dịch mọi lúc, mọi nơi.

---

## 🔨 Phương Pháp Thực Hiện  

Quá trình phát triển hệ thống ATM được thực hiện theo các bước toàn diện:  
1. **Phân Tích Yêu Cầu**: Thu thập dữ liệu về các hệ thống ATM hiện tại.  
2. **Thiết Kế Hệ Thống**: Xây dựng kiến trúc phần mềm dựa trên nguyên lý hướng dịch vụ, xác định các thành phần chính của hệ thống.  
3. **Triển Khai & Kiểm Thử**: Phát triển hệ thống theo từng module, đảm bảo tính ổn định và bảo mật.  
4. **Đánh Giá & Tối Ưu**: Kiểm tra hiệu suất, bảo mật và độ tin cậy trước khi đưa hệ thống vào vận hành.  

---

## 🏗 Kiến Trúc Được Chọn  

### Vì Sao Chọn Kiến Trúc Hướng Dịch Vụ?
- Cân bằng giữa **tính linh hoạt, hiệu suất và chi phí**.  
- Phù hợp với mục tiêu chuyển đổi số của ngân hàng và mô hình ngân hàng vừa và nhỏ.  
- Hỗ trợ mở rộng hệ sinh thái dịch vụ trong tương lai như **Internet Banking**, **ví điện tử**.

### Phân Tích Trade-Offs:

1. **Service-Based vs. Monolithic**:  
   - **Ưu Điểm**: Dễ mở rộng các dịch vụ như ngân hàng di động hoặc ví điện tử.  
   - **Nhược Điểm**: Độ phức tạp cao hơn, tiềm năng độ trễ lớn do giao tiếp qua API.  

2. **Service-Based vs. Microservices**:  
   - **Ưu Điểm**: Đơn giản và ít tốn kém hơn, dễ quản lý mà không cần DevOps mạnh.  
   - **Nhược Điểm**: Khả năng linh hoạt và chịu tải thấp hơn khi mở rộng quy mô lớn.  

---

## ⚙️ Công Nghệ  

### Môi Trường Phát Triển:  
- **Hệ Điều Hành**: Windows 11, macOS  
- **IDE**: IntelliJ IDEA kết hợp với Spring Boot  
- **Công Cụ Build**: Gradle 8.3  
- **Quản Lý Mã Nguồn**: GitHub  

### Công Nghệ:  
#### Frontend:  
- Java 21, Spring Boot 3.1, HTML, CSS, SCSS, JavaScript, Bootstrap  

#### Backend:  
- Java 21, Spring Boot 3.1 (Spring Security, Spring Data JPA)  
- RESTful API (JSON), xác thực JWT  
- Database: MySQL 8.0 với giao dịch ACID  

#### Kiểm Tra API:  
- Postman/Insomnia, HTTP Client trong IntelliJ  

---

## 🌐 Tầm Nhìn Tương Lai  

- Chuyển đổi sang **Kiến Trúc Microservices** nếu cần mở rộng mạnh mẽ hệ sinh thái.  
- Tích hợp các dịch vụ lớn như **ngân hàng di động**, **ví điện tử**, và **hệ thống phát hiện gian lận tiên tiến**.  

---
