# ecommerce: E-Commerce Website

This is a **Java Spring Boot** backend project for an **E-Commerce platform**, designed for high performance and scalability. The project integrates multiple **cloud services**, follows **best coding practices**, and ensures **efficient API performance**.

## 🚀 Features & Tech Stack

### 📌 Cloud Services & Infrastructure:
- **AWS RDS**: Used for database management.
- **AWS EC2**: Deployed for scaling and handling traffic efficiently.
- **AWS MSK**: Used for Apache Kafka.


### 🛠 Tech Stack:
- **Java, Spring Boot, Hibernate, JPA**
- **BcryptEncoder, Kafka**
- **AWS EC2, RDS, MSK**


## 🎯 Design & Best Practices

### ✅ SOLID Principles:
The project follows **SOLID principles** to ensure maintainability and scalability:
- **S**: Single Responsibility Principle – Each class (e.g., `AuthService`, `RoleService`, `UserService`) has a **single well-defined responsibility**.
- **O**: Open-Closed Principle – Implemented **interfaces and abstraction** .
- **L**: Liskov Substitution Principle – Used **interface-driven design** .
- **I**: Interface Segregation Principle – Segmented service layer interfaces (`AuthService`, `RoleService`, `UserService`).
- **D**: Dependency Inversion Principle – Used **constructor-based dependency injection** in controllers.

### 🛠 Exception Handling:
- Implemented **exception at class level** 


## 📂 Project Structure
```
src/main/java/com/UserService
├── clients/
│   └── KafkaProducerClient.java   # Kafa Producer for User Events
├── controller/
│   ├── AuthController.java    # API for Authentication
│   ├── RoleController.java   # API for Roles
│   ├── UserController.java    # API for User
├── dto/
│   ├── CreateRoleRequestDto.java                # DTO for Creating Roles
│   ├── LoginRequestDto.java                     # DTO for Login Requests
│   ├── LogoutRequestDto.java                    # DTO for Logout Requests
│   ├── SendEmailMessageDto.java                 # DTO for Send Email Messages
│   ├── SetUserRolesRequestDto.java              # DTO for Setting User Roles
│   ├── SignUpRequestDto.java                    # DTO for Sign Up Requests
│   ├── UserDto.java                             # DTO for User Data
│   ├── ValidateTokenRequestDto.java             # DTO for Validating Tokens
├── models/
│   ├── BaseModel.java      # Base Model Entity
│   ├── Role.java           # Role Entity
│   ├── Session.java        # Session Entity
│   ├── SessionStatus.java  # Session Status Entity
│   ├── User.java           # User Entity
├── repositories/
│   ├── RoleRepo.java      # JPA Repository for Roles
│   ├── SessionRepo.java   # JPA Repository for Sessions
│   ├── UserRepo.java      # JPA Repository for Users
├── security/
│   ├── SpringConfig.java    # Spring Config
│   ├── SpringSecurity.java  # Spring Security Config
├── service/
│   ├── AuthService.java       # Authentication Service
│   ├── RoleService.java       # Role Service
│   ├── UserService.java       # Useer Service

```

## 🚀 How to Run Locally
1. **Clone the repository**  
   ```sh
   git clone https://github.com/SaranshSharma30/ecommerce.git
   cd UserService
   ```
2. **Set up database** using AWS RDS or MySQL locally.
3. **Configure Apache Kafka** (Apache Kafka is running).
4. **Run the application**  
   ```sh
   mvn spring-boot:run
   ```
5. **Access API** via `http://localhost:8080`

## 📌 API Endpoints

| Method | Endpoint                      | Description           |
|--------|-------------------------------|-----------------------|
| `POST` | `/auth/login`                 | User login            |
| `POST` | `/auth/logout`                | User logout           |
| `POST` | `/auth/signup`                | Signup user           |
| `POST` | `/auth/validate`              | Validate user token   |
| `POST` | `/roles`                      | Create roles          |
| `GET` | `/users/{id}`                 | Get user details      |
| `POST` | `/users/{id}/roles`           | Set user roles        |


---
