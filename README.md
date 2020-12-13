# JDBCExampleProject
This project aims to show that I can operate in pure Java JDBC.

The project shows several classes operating on the local SQL database (from the local mysql server). 
It uses pure JDBC for contact and modification of data in the database.

The project contains a class dealing with connecting to the database, one site, one repository.
It does not include tests and advanced error handling because it was supposed to show only the ability to operate on JDBC.

# Things that I've learned

- connecting to database with pure Java
- CRUD operations in jdbc
- using the statements, prepared statements 
- calls stored procedures (IN, OUT, INOUT)
- simple transactions
- database and resultset metadata
- reading and writing BLOB and CLOB data files
- using property file to configure database connection

# Example output of program

```Successfully connect to database: jdbc:mysql://localhost:3306/demo?serverTimezone=UTC&useSSL=false
JDBC driver name: MySQL Connector/J
JDBC driver version: mysql-connector-java-8.0.21 (Revision: 33f65445a1bcc544eb0120491926484da168f199)
Product name: MySQL
Product version: 8.0.22

------------------------------------

TABLES IN DATABASE:

employees

------------------------------------

COLUMNS IN Employee TABLE:

resume
big_note
last_name
id
department
salary
first_name
email

------------------------------------

Row(s) affected: (0) by query: UPDATE employees SET last_name='Nowak',first_name='Robert' WHERE first_name = 'John' OR first_name = 'Jan' AND last_name = 'Kowalski' 

------------------------------------

Row(s) affected: (1) by query: INSERT INTO employees (last_name, first_name, email, department, salary) VALUES (?,?,?,?,?)
New user id: 24

------------------------------------

Row(s) affected: (1) by query: DELETE FROM employees WHERE first_name = 'John' OR first_name = 'Jan' AND last_name = 'Kowalski' 

------------------------------------

Employee.Employee{id=6, last_name='Smith', first_name='Paul', email='paul.smith@foo.com', department='Legal', salary=100000.0}
Employee.Employee{id=2, last_name='Public', first_name='Mary', email='mary.public@foo.com', department='Engineering', salary=75000.0}
Employee.Employee{id=5, last_name='Johnson', first_name='Lisa', email='lisa.johnson@foo.com', department='Engineering', salary=50000.0}
Employee.Employee{id=8, last_name='Brown', first_name='Bill', email='bill.brown@foo.com', department='Engineering', salary=50000.0}
Employee.Employee{id=7, last_name='Adams', first_name='Carl', email='carl.adams@foo.com', department='HR', salary=3000.0}
Employee.Employee{id=9, last_name='Thomas', first_name='Susan', email='susan.thomas@foo.com', department='Legal', salary=80000.0}
Employee.Employee{id=11, last_name='Fowler', first_name='Mary', email='mary.fowler@foo.com', department='Engineering', salary=65000.0}
Employee.Employee{id=12, last_name='Waters', first_name='David', email='david.waters@foo.com', department='Legal', salary=90000.0}
Employee.Employee{id=1, last_name='Nowak', first_name='Robert', email='john.doe@foo.com', department='HR', salary=3000.0}
Employee.Employee{id=3, last_name='Queue', first_name='Susan', email='susan.queue@foo.com', department='Legal', salary=130000.0}
Employee.Employee{id=10, last_name='Nowak', first_name='Robert', email='john.davis@foo.com', department='HR', salary=3000.0}
Employee.Employee{id=4, last_name='Williams', first_name='David', email='david.williams@foo.com', department='HR', salary=3000.0}

------------------------------------

The result of greeting: Hello to the awesome HR team!

------------------------------------

Salaries for employees in department HR increased by 3000,00

------------------------------------

Employee.Employee{id=11, last_name='Fowler', first_name='Mary', email='mary.fowler@foo.com', department='Engineering', salary=65000.0}
Employee.Employee{id=2, last_name='Public', first_name='Mary', email='mary.public@foo.com', department='Engineering', salary=75000.0}
Employee.Employee{id=5, last_name='Johnson', first_name='Lisa', email='lisa.johnson@foo.com', department='Engineering', salary=50000.0}
Employee.Employee{id=8, last_name='Brown', first_name='Bill', email='bill.brown@foo.com', department='Engineering', salary=50000.0}

------------------------------------

Engineering department has 4 employees!

------------------------------------

Column name: id
Column type: INT
Is nullable: 0
Is autoincrement: true
Is read only: false

Column name: last_name
Column type: VARCHAR
Is nullable: 1
Is autoincrement: false
Is read only: false

Column name: first_name
Column type: VARCHAR
Is nullable: 1
Is autoincrement: false
Is read only: false

Column name: email
Column type: VARCHAR
Is nullable: 1
Is autoincrement: false
Is read only: false

Column name: department
Column type: VARCHAR
Is nullable: 1
Is autoincrement: false
Is read only: false

Column name: salary
Column type: DECIMAL
Is nullable: 1
Is autoincrement: false
Is read only: false

Column name: resume
Column type: BLOB
Is nullable: 1
Is autoincrement: false
Is read only: false

Column name: big_note
Column type: LONGTEXT
Is nullable: 1
Is autoincrement: false
Is read only: false


------------------------------------

Successfully added resume!
Successfully received resume copy!

------------------------------------

Successfully added resume!
Successfully received resume copy!

------------------------------------

Row(s) affected: (4) by query: UPDATE employees SET salary=3000.0 WHERE department = 'HR' 
Are you sure that you want to update salary? Type "yes" for commit!
```
