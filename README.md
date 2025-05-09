# 🌱 SPDP: Senior Project Digital Platform

โปรเจกต์นี้เป็นแอปพลิเคชันที่พัฒนาด้วย [Spring Boot](https://spring.io/projects/spring-boot) พร้อมเชื่อมต่อกับฐานข้อมูล MySQL และใช้ฐานข้อมูลจากไฟล์ `Final_Database_SeniorProject.sql`

## 🔧 ความต้องการก่อนใช้งาน

ติดตั้งสิ่งต่อไปนี้ก่อนเริ่ม:

* Java 17 หรือสูงกว่า
* Maven
* Git
* MySQL 8.x
* IDE เช่น IntelliJ IDEA, VS Code หรืออื่น ๆ
  
---

### 📥 ดาวน์โหลดไฟล์ฐานข้อมูล

สามารถดาวน์โหลดไฟล์ `Final_Database_SeniorProject.sql` ได้จาก [ที่นี่](https://github.com/TongTunyarat/SPDP-Project/blob/main/database/Final_Database_SeniorProject.sql)

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

### 3. สร้างโฟลเดอร์ `database` และนำเข้าไฟล์ฐานข้อมูล

1. สร้างโฟลเดอร์ `database`:

   * ให้สร้างโฟลเดอร์ใหม่ชื่อว่า `database` ใน path ที่ใช้งาน
   * นำไฟล์ `Final_Database_SeniorProject.sql` ไปไว้ในโฟลเดอร์ `database`

2. นำเข้าไฟล์ฐานข้อมูลไปยังฐานข้อมูล `springbootdb`:
   
> 💡 **คำแนะนำ**: ให้แน่ใจว่าไฟล์ `.sql` อยู่ในโฟลเดอร์ `database/` ของโปรเจกต์

#### ▪️ กรณีใช้ **PowerShell (Windows)**:

```powershell
Get-Content .\database\Final_Database_SeniorProject.sql | mysql -u springuser -p springbootdb
```

เมื่อระบบถามรหัสผ่าน ให้ใส่: `springpass`

#### ▪️ กรณีใช้ **Git Bash / Linux / macOS**:

```bash
mysql -u springuser -p springbootdb < database/Final_Database_SeniorProject.sql
```

#### ▪️ กรณีใช้ **MySQL Workbench** (GUI):

1. เปิด MySQL Workbench
2. เชื่อมต่อฐานข้อมูลโดยใช้ โดยสร้าง MySQL Connection ใหม่ ดังนี้ user: `springuser` password: `springpass` และ database: `springbootdb`
3. ไปที่ **Server > Data Import** แล้วเลือกไฟล์ `Final_Database_SeniorProject.sql` ที่ดาวน์โหลดมา
4. เลือก Default Target Schema เป็น springbootdb
5. กด Start Import

---

## 🚀 วิธีการติดตั้งและรันโปรเจกต์

### 1. Clone โปรเจกต์

```bash
git clone https://github.com/TongTunyarat/SPDP-Project.git
cd SPDP-Project
```

---

### 2. ตั้งค่าไฟล์ `application.properties`

1. หลังจาก clone โปรเจกต์เสร็จแล้ว ให้เปิดโปรเจกต์ใน IDE ที่คุณใช้งาน เช่น IntelliJ IDEA หรือ VS Code
2. จากนั้น เปิดไฟล์ `src/main/resources/application.properties`
3. แก้ไขการตั้งค่าดังนี้:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/springbootdb
spring.datasource.username=springuser
spring.datasource.password=springpass
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

### 3. Build และ Run

#### ▪️ Maven:

* ใช้คำสั่งนี้ใน PowerShell / Git Bash / Terminal ใน IDE:

```bash
./mvnw spring-boot:run
```

* ในกรณีที่ใช้ IDE เช่น IntelliJ IDEA หรือ VS Code ให้กด "Run" จาก IDE ได้เลย
* ถ้าพบปัญหาเกี่ยวกับ Lombok annotations ให้ยอมรับการตั้งค่า "Enable annotation processing" ใน IDE ด้วย

---


## 🌐 การเข้าถึงระบบ

หลังจากรันแอปแล้ว ให้เข้าใช้งานได้ที่:

```
http://localhost:8080/login
```

