# SQL-Java-Client 🚀

A **Java-based MySQL client** that allows users to execute SQL queries through a **graphical user interface (GUI)**. The project supports different user roles with specific access permissions and logs executed queries in a MySQL database.

## 📜 Project Overview
This project consists of **two Java GUI applications** that connect to a **remote MySQL database** via **JDBC**:
1. **SQL Client Application (MainGUI)** – Allows general users to execute MySQL commands.
2. **Specialized Accountant Application (AccountantGUI)** – Restricted to accountant-level users for monitoring database query logs.

🔹 **Database:** MySQL  
🔹 **GUI Framework:** Java Swing  
🔹 **Connection Handling:** JDBC  
🔹 **Logging:** Query execution is recorded in an operations log  

---

## ⚙️ Features
✅ **Connect to MySQL and execute SQL queries**  
✅ **Supports MySQL DDL (CREATE, DROP, ALTER) and DML (SELECT, INSERT, UPDATE, DELETE)**  
✅ **Restricts access based on user roles** (`client1`, `client2`, `theaccountant`)  
✅ **Records all executed queries in an `operationslog` database**  
✅ **Uses properties files for database configuration**  
✅ **Implements a GUI using Java Swing for better user interaction**  
 
