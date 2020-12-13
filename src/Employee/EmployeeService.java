package Employee;

import Jdbc.LogicalOperation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class EmployeeService {

    private final EmployeesRepository employeesRepository;

    public EmployeeService(Connection connection) {
        this.employeesRepository = new EmployeesRepository(connection);
    }

    //CRUD

    public Set<Employee> getAll() throws SQLException {
        return employeesRepository.getAll();
    }

    public int insert(Employee employee) throws SQLException {
        return employeesRepository.insert(employee);
    }

    public void update(Map<String, Serializable> values, LinkedHashMap<String, LogicalOperation> conditionList) throws SQLException {
        employeesRepository.update(values, conditionList);
    }

    public void delete(LinkedHashMap<String, LogicalOperation> conditionList) throws SQLException {
        employeesRepository.delete(conditionList);
    }

    //Stored Procedures

    public Set<Employee> getAllByDepartment(String department) throws SQLException {
        return employeesRepository.getEmployeesForDepartment(department);
    }

    public void greetDepartment(String department) throws SQLException {
        employeesRepository.greetDepartment(department);
    }

    public int getCountForDepartment(String department) throws SQLException {
        return employeesRepository.getCountForTheDepartment(department);
    }

    public void increaseSalariesForDepartment(String department, BigDecimal salary) throws SQLException {
        employeesRepository.increaseSalariesForTheDepartment(department, salary);
    }

    //BLOB AND CLOB handling

    public void addEmployeeResume(File file, int id) throws SQLException, IOException {
        employeesRepository.addEmployeeResume(file, id);
    }

    public void getEmployeeResume(int id) throws IOException, SQLException {
        employeesRepository.getEmployeeResume(id);
    }

    public void addEmployeeBigNote(File file, int id) throws FileNotFoundException, SQLException {
        employeesRepository.addEmployeeBigNote(file, id);
    }

    public void getEmployeeBigNote(int id) throws IOException, SQLException {
        employeesRepository.getEmployeeBigNote(id);
    }
}
