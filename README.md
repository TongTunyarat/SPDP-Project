# 🌱 Spring Boot Senior Project

โปรเจกต์นี้เป็นแอปพลิเคชันที่พัฒนาด้วย [Spring Boot](https://spring.io/projects/spring-boot) พร้อมเชื่อมต่อกับฐานข้อมูล MySQL และใช้ฐานข้อมูลจากไฟล์ `Final_Database_SeniorProject.sql`

## 🔧 ความต้องการก่อนใช้งาน

คุณต้องติดตั้งสิ่งต่อไปนี้ก่อนเริ่ม:

- Java 17 หรือสูงกว่า
- Maven
- Git
- MySQL 8.x
- IDE เช่น IntelliJ IDEA, VS Code หรืออื่น ๆ

---

## 🗄 การติดตั้ง MySQL

### 1. ติดตั้ง MySQL

#### ▪️ สำหรับ Windows/macOS:
ดาวน์โหลดจาก: [https://dev.mysql.com/downloads/mysql/](https://dev.mysql.com/downloads/mysql/)

---

### 2. สร้างฐานข้อมูลและผู้ใช้งาน

เข้าสู่ MySQL CLI:

```bash
mysql -u root -p
```

จากนั้นรันคำสั่งต่อไปนี้ใน MySQL:

```sql
CREATE DATABASE springbootdb;
CREATE USER 'springuser'@'localhost' IDENTIFIED BY 'springpass';
GRANT ALL PRIVILEGES ON springbootdb.* TO 'springuser'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

---

### 3. นำเข้าไฟล์ฐานข้อมูล

ให้แน่ใจว่าไฟล์ `Final_Database_SeniorProject.sql` อยู่ในโฟลเดอร์ `database/` ของโปรเจกต์ แล้วรันคำสั่งนี้ใน terminal:

```bash
mysql -u springuser -p springbootdb < database/Final_Database_SeniorProject.sql
```

เมื่อระบบถามรหัสผ่าน ให้กรอก: `springpass`

---

## 🚀 วิธีการติดตั้งและรันโปรเจกต์

### 1. Clone โปรเจกต์

```bash
git clone https://github.com/TongTunyarat/SPDP-Project.git
cd SPDP-Project
```

### 2. ตั้งค่าไฟล์ `application.properties`

เปิดไฟล์ `src/main/resources/application.properties` และแก้ไขการตั้งค่าดังนี้:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/springbootdb
spring.datasource.username=springuser
spring.datasource.password=springpass
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

### 3. Build และ Run

#### ▪️ Maven:

```bash
./mvnw spring-boot:run
```

---

## 🌐 การเข้าถึงระบบ

หลังจากรันแอปแล้ว ให้เข้าใช้งานได้ที่:

```
http://localhost:8080/login
```

