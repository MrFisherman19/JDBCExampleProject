package Employee;

import Jdbc.JdbcUtils;
import Jdbc.LogicalOperation;
import Jdbc.annotation.InOutParametersProcedure;
import Jdbc.annotation.InParametersProcedure;
import Jdbc.annotation.OutParametersProcedure;

import java.io.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/*
 * This class contains methods which should be called only within current package
 */
public class EmployeesRepository {

    private final Connection connection;

    EmployeesRepository(Connection connection) {
        this.connection = connection;
    }

    Set<Employee> getAll() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM employees");

        Set<Employee> employees = getEmployeesFromResultSet(resultSet);

        JdbcUtils.close(statement, resultSet);

        return employees;
    }

    private Set<Employee> getEmployeesFromResultSet(ResultSet resultSet) throws SQLException {
        Set<Employee> employees = new HashSet<>();
        if (resultSet != null) {
            while (resultSet.next()) {
                employees.add(
                        new Employee(
                                resultSet.getInt("id"),
                                resultSet.getString("last_name"),
                                resultSet.getString("first_name"),
                                resultSet.getString("email"),
                                resultSet.getString("department"),
                                resultSet.getDouble("salary")));
            }
        }
        return employees;
    }

    int insert(Employee employee) throws SQLException {
        String query = "INSERT INTO employees " +
                "(last_name, first_name, email, department, salary) VALUES (?,?,?,?,?)";

        PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

        ps.setString(1, employee.getLast_name());
        ps.setString(2, employee.getFirst_name());
        ps.setString(3, employee.getEmail());
        ps.setString(4, employee.getDepartment());
        ps.setDouble(5, employee.getSalary());

        int rowAffected = ps.executeUpdate();

        printRowAffected(query, rowAffected);

        int newUserId = 0;
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            newUserId = rs.getInt(1);
        }
        JdbcUtils.close(rs);


        JdbcUtils.close(ps);

        return newUserId;
    }

    void update(Map<String, Serializable> valuesToChange, LinkedHashMap<String, LogicalOperation> conditions) throws SQLException {
        String query = JdbcUtils.createUpdateQuery("employees", valuesToChange, conditions);

        Statement statement = connection.createStatement();

        int rowAffected = statement.executeUpdate(query);

        printRowAffected(query, rowAffected);
    }

    void delete(LinkedHashMap<String, LogicalOperation> conditions) throws SQLException {
        String query = JdbcUtils.createDeleteQuery("employees", conditions);

        Statement statement = connection.createStatement();

        int rowAffected = statement.executeUpdate(query);

        printRowAffected(query, rowAffected);
    }

    @InParametersProcedure
    void increaseSalariesForTheDepartment(String department, BigDecimal salary) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("{call increase_salaries_for_department(?, ?)}");

        callableStatement.setString(1, department);
        callableStatement.setBigDecimal(2, salary);

        System.out.println(String.format("Salaries for employees in department %s increased by %.2f", department, salary));

        callableStatement.execute();

        JdbcUtils.close(callableStatement);
    }

    @InOutParametersProcedure
    void greetDepartment(String department) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("{call greet_the_department(?)}");

        callableStatement.registerOutParameter(1, Types.VARCHAR);
        callableStatement.setString(1, department);

        callableStatement.execute();

        String result = callableStatement.getString(1);

        System.out.println("The result of greeting: " + result);

        JdbcUtils.close(callableStatement);
    }

    @OutParametersProcedure
    int getCountForTheDepartment(String department) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("{call get_count_for_department(?, ?)}");

        callableStatement.setString(1, department);
        callableStatement.registerOutParameter(2, Types.INTEGER);

        callableStatement.execute();

        return callableStatement.getInt(2);
    }

    @InOutParametersProcedure
    Set<Employee> getEmployeesForDepartment(String department) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("{call get_employees_for_department(?)}");

        callableStatement.setString(1, department);

        callableStatement.execute();

        ResultSet resultSet = callableStatement.getResultSet();

        return getEmployeesFromResultSet(resultSet);
    }

    public void addEmployeeResume(File file, int employeeId) throws IOException, SQLException {
        FileInputStream fileInputStream = new FileInputStream(file);

        String query = "UPDATE employees set resume=? where id = " + employeeId;

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setBinaryStream(1, fileInputStream);
        statement.executeUpdate();

        JdbcUtils.close(statement);
    }

    public void getEmployeeResume(int employeeId) throws SQLException, IOException {
        String query = "SELECT first_name, last_name, resume FROM employees WHERE id = " + employeeId;
        Statement statement = connection.createStatement();

        ResultSet resume = statement.executeQuery(query);

        if (resume.next()) {
            File fileCopy = new File(resume.getString(1) + "_" +
                    resume.getString(2) +  "_COPY.pdf");
            FileOutputStream fileOutputStream = new FileOutputStream(fileCopy);
            InputStream inputStream = resume.getBinaryStream("resume");

            byte[] buffer = new byte[1024];

            while(inputStream.read(buffer) > 0) {
                fileOutputStream.write(buffer);
            }
        }

        JdbcUtils.close(resume, statement);
    }

    public void addEmployeeBigNote(File file, int employeeId) throws FileNotFoundException, SQLException {
        FileReader fileReader = new FileReader(file);

        String query = "UPDATE employees set big_note=? where id = " + employeeId;

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setCharacterStream(1, fileReader);
        statement.executeUpdate();

        JdbcUtils.close(statement);
    }

    public void getEmployeeBigNote(int employeeId) throws SQLException, IOException {
        String query = "SELECT first_name, last_name, big_note FROM employees WHERE id = " + employeeId;
        Statement statement = connection.createStatement();

        ResultSet resume = statement.executeQuery(query);

        if (resume.next()) {
            File fileCopy = new File(resume.getString(1) + "_" +
                    resume.getString(2) +  "_BIG_NOTE_COPY.txt");
            FileWriter fileWriter = new FileWriter(fileCopy);
            Reader reader = resume.getCharacterStream("big_note");

            int character;
            while ((character = reader.read()) > 0) {
                fileWriter.write(character);
            }

            fileWriter.close();
        }

        JdbcUtils.close(resume, statement);
    }

    private void printRowAffected(String query, int rowAffected) {
        System.out.println("Row(s) affected: (" + rowAffected + ") by query: " + query);
    }
}
