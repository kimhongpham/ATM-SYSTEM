# 🏧 ATM System - Service Oriented Architecture

A system that simulates an **ATM (automatic teller/deposit machine)** that helps users perform basic operations such as logging in, checking balances, withdrawing money, depositing money, and viewing transaction history.

---

## ✨ Main features
* **Login and authenticate** users with PIN or account.
* **Check current account balance**.
* **Withdraw**: select amount, confirm, and update balance.
* **Deposit** to account.
* **View transaction history**: display withdrawal/deposit transactions that have been made.
* **(Optional)**: Transfer money between accounts.
* **(Optional)**: Change PIN or change account information.

---

## 🛠️ Technology used
| Components | Technology |
|-------------|------------|
| **Language** | Java 21+ |
| **Framework** | Spring Boot |
| **Security** | Spring Security, JWT |
| **DB** | MySQL (or H2 for test) |
| **Build tool** | Gradle |
| **ORM** | Spring Data JPA |
| **OTP storage & add-on logic** | Service layer with `OtpService` class |
| **Global error handling** | `GlobalExceptionHandler` |

---

## 📂 Directory Structure
```
│ Main.java
│
├───config
├───controller
├───dto
├───exception
├───model
├───repository
├───service
└───util

````

---

## ⚙️ Install and run the project

### 1. Clone repository
```bash
git clone https://github.com/kimhongpham/ATM-SYSTEM.git
cd atm-system
````

### 2\. Install dependencies

```bash
./gradlew build

# Or run directly from IDE: click on Gradle icon.
```

### 3\. Configure environment variables / database connection
Create database in MySQL:
```
CREATE DATABASE atm_system;
```
Update file **application.properties** fill in database connection information:

```
db.url=jdbc:mysql://localhost:3306/atm_db
db.user=root
db.password=your_password
```

### 4\. Run the application

Run the main startup file of the application:

```bash
./gradlew bootRun

# Or run directly from IDE: open file Main.* and run the application.
```
The application will run at:
👉 http://localhost:8080 to access the account registration website
-----
### 5\. Run frontend (Java Swing)

Frontend is written in Java Swing, start directly from class:

src/main/java/com/frontend/LoginUI.java

How to run:
```bash
From IDE (IntelliJ / Eclipse):
Open file LoginUI.java → Run LoginUI.main()
```
Or from terminal:
```bash
cd src/main/java
javac com/frontend/LoginUI.java
java com.frontend.LoginUI
```

The GUI application will display the login screen (LoginUI), communicate with the backend via REST APIs.

## 🤝 Contribute

We welcome any contribution to improve this ATM system\!

1. Fork this repository.
2. Create a new feature branch (`git checkout -b feature/TenTinhNang`).
3. Commit the change (`git commit -m "Add Feature X"`).
4. Push to your branch (`git push origin feature/TenTinhNang`).
5. Create a **Pull Request (PR)** and clearly describe the change.

-----

## 📄 License

The project is released under the **MIT** license. See the file [LICENSE](https://www.google.com/search?q=LICENSE) for details.