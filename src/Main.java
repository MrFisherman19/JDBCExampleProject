import Employee.Employee;
import Employee.EmployeeService;
import Jdbc.JdbcConnection;
import Jdbc.JdbcUtils;
import Jdbc.LogicalOperation;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

public class Main {

    private static final Connection connection;
    private static final EmployeeService employeeService;

    static {
        //open connection
        connection = JdbcConnection.openDatabaseConnectionFromProperties("database.properties");

        //create instance of employeeService
        employeeService = new EmployeeService(connection);
    }

    public static void main(String[] args) throws SQLException {

        //print metadata of database
        printMetadataOfDatabase();

        //print tables
        printTables();

        //print columns for "employees" table
        printColumnsForEmployeeTable();

        //UPDATE Employee
        updateEmployeeExample(getExampleValuesToChangeMap(),  getExampleConditionsMap());

        //INSERT Employee
        insertEmployeeExample();

        //DELETE Employee
        deleteEmployeeExample( getExampleConditionsMap());

        //SELECT Employees
        selectEmployeesExample();

        //Calling procedures
        callingProceduresExample();

        //print query metadata
        printQueryMetadataExample();

        //dealing with BLOB (Binary Large Object)
        blobExample();

        //dealing with CLOB (Character Large Object)
        clobExample();

        //transaction method
        transactionExample();

        closeConnection();
    }

    private static void clobExample() {
        //writing to db
        writingClobToDb();

        //reading from db
        readingClobFromDb();

        printSeparator();
    }

    private static void readingClobFromDb() {
        try {
            employeeService.getEmployeeBigNote(2);
        } catch (Exception e) {
            System.out.println("Something is wrong with the clob example [reading]!");
            e.printStackTrace();
        }
        System.out.println("Successfully received resume copy!");
    }

    private static void writingClobToDb() {
        try {
            File file = new File("big_note_sample.txt");
            employeeService.addEmployeeBigNote(file, 2);
        } catch (Exception e) {
            System.out.println("Something is wrong with the clob example [writing]!");
            e.printStackTrace();
        }
        System.out.println("Successfully added resume!");
    }

    private static void blobExample() {

        //writing to db
        writingBlobToDb();

        //reading from db
        readingBlobFromDb();

        printSeparator();
    }

    private static void readingBlobFromDb() {
        try {
            employeeService.getEmployeeResume(2);
        } catch (Exception e) {
            System.out.println("Something is wrong with the blob example [reading]!");
            e.printStackTrace();
        }
        System.out.println("Successfully received resume copy!");
    }

    private static void writingBlobToDb() {
        try {
            File file = new File("sample_resume.pdf");
            employeeService.addEmployeeResume(file, 2);
        } catch (Exception e) {
            System.out.println("Something is wrong with the blob example [writing]!");
            e.printStackTrace();
        }
        System.out.println("Successfully added resume!");
    }

    private static void printQueryMetadataExample() throws SQLException {
        ResultSetMetaData resultSetMetaData = JdbcUtils.getResultSetMetaDataForQuery(connection, "SELECT * FROM Employees");

        int columnCount = resultSetMetaData.getColumnCount();

        for(int i = 1; i <= columnCount; i++) {
            System.out.println("Column name: " + resultSetMetaData.getColumnName(i));
            System.out.println("Column type: " + resultSetMetaData.getColumnTypeName(i));
            System.out.println("Is nullable: " + resultSetMetaData.isNullable(i));
            System.out.println("Is autoincrement: " + resultSetMetaData.isAutoIncrement(i));
            System.out.println("Is read only: " + resultSetMetaData.isReadOnly(i));
            System.out.println();
        }

        printSeparator();
    }

    private static LinkedHashMap<String, LogicalOperation> getExampleConditionsMap() {
        LinkedHashMap<String, LogicalOperation> conditions = new LinkedHashMap<>();
        conditions.put("first_name = 'John'", LogicalOperation.OR);
        conditions.put("first_name = 'Jan'", LogicalOperation.AND);
        conditions.put("last_name = 'Kowalski'", LogicalOperation.WITHOUT_OPERATION);
        return conditions;
    }

    private static Map<String, Serializable> getExampleValuesToChangeMap() {
        Map<String, Serializable> valuesToChange = new HashMap<>();
        valuesToChange.put("last_name", "Nowak");
        valuesToChange.put("first_name", "Robert");
        return valuesToChange;
    }

    private static void updateEmployeeExample(Map<String, Serializable> valuesToChange, LinkedHashMap<String, LogicalOperation> conditions) throws SQLException {
        employeeService.update(valuesToChange,  conditions);
        printSeparator();
    }

    private static void callingProceduresExample() throws SQLException {
        employeeService.greetDepartment("HR");
        printSeparator();

        employeeService.increaseSalariesForDepartment("HR", new BigDecimal("3000.0"));
        printSeparator();

        String department = "Engineering";
        employeeService.getAllByDepartment(department).forEach(System.out::println);
        printSeparator();

        int countEngineeringDept = employeeService.getCountForDepartment(department);
        System.out.println(String.format("%s department has %d employees!",department, countEngineeringDept));
        printSeparator();
    }

    private static void selectEmployeesExample() throws SQLException {
        employeeService.getAll().forEach(System.out::println);
        printSeparator();
    }

    private static void deleteEmployeeExample(LinkedHashMap<String, LogicalOperation> conditions) throws SQLException {
        employeeService.delete(conditions);
        printSeparator();
    }

    private static void insertEmployeeExample() throws SQLException {
        Employee employee = new Employee(1, "Kowalski",
                "Jan",
                "jankowalski@gmail.com",
                "HR",
                3000.0d);
        System.out.println("New user id: " + employeeService.insert(employee));
        printSeparator();
    }

    private static void printColumnsForEmployeeTable() {
        System.out.println("COLUMNS IN Employee TABLE:\n");
        JdbcUtils.getColumnsNamesForTableInConnection("employees", connection, null, null, null)
        .forEach(System.out::println);

        printSeparator();
    }

    private static void printTables() {
        System.out.println("TABLES IN DATABASE:\n");
        JdbcUtils.getTablesNamesForConnection(connection, "demo", null,null,null)
                .forEach(System.out::println);

        printSeparator();
    }

    private static void printMetadataOfDatabase() throws SQLException {
        DatabaseMetaData metaData = JdbcUtils.getDatabaseMetaDataForConnection(connection);
        System.out.println("JDBC driver name: " + metaData.getDriverName());
        System.out.println("JDBC driver version: " + metaData.getDriverVersion());
        System.out.println("Product name: " + metaData.getDatabaseProductName());
        System.out.println("Product version: " + metaData.getDatabaseProductVersion());
        printSeparator();
    }

    private static void transactionExample() throws SQLException {
        Map<String, Serializable> valuesToChange = new HashMap<>();
        valuesToChange.put("salary", new BigDecimal("3000.0"));

        LinkedHashMap<String, LogicalOperation> conditions = new LinkedHashMap<>();
        conditions.put("department = 'HR'", LogicalOperation.WITHOUT_OPERATION);

        Scanner scanner = new Scanner(System.in);

        connection.setAutoCommit(false);

        employeeService.update(valuesToChange, conditions);

        System.out.println("Are you sure that you want to update salary? Type \"yes\" for commit!");
        if (scanner.nextLine().equals("yes")) {
            connection.commit();
            System.out.println("COMMIT!");
        } else {
            connection.rollback();
            System.out.println("ROLLBACK!");
        }
    }

    private static void printSeparator() {
        System.out.println("\n------------------------------------\n");
    }

    private static void closeConnection() throws SQLException {
        connection.close();
    }
}
