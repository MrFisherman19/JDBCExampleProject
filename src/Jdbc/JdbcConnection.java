package Jdbc;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class JdbcConnection {

    private static Connection connection = null;

    private static final String USER = "user";
    private static final String PASSWORD = "password";
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/demo?serverTimezone=UTC&useSSL=false";

    //hardcoded
    public static Connection openDatabaseConnectionHardcoded() {

        try{
            connection = DriverManager.getConnection(DATABASE_URL, USER , PASSWORD);
            System.out.println("Successfully connect to database: " + DATABASE_URL);

        } catch (SQLException e) {
            System.out.println("Problem with opening connection!");
            e.printStackTrace();
        }
        return connection;
    }

    //from properties
    public static Connection openDatabaseConnectionFromProperties(String propertiesPath) {

        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(propertiesPath));
        } catch (IOException e) {
            System.out.println("Something is wrong with given property file!");
            e.printStackTrace();
        }

        String user = properties.getProperty("user");
        String password = properties.getProperty("password");
        String url = properties.getProperty("url");

        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Successfully connect to database: " + url);
        } catch (SQLException e) {
            System.out.println("Problem with opening connection!");
            e.printStackTrace();
        }
        return connection;
    }

}

