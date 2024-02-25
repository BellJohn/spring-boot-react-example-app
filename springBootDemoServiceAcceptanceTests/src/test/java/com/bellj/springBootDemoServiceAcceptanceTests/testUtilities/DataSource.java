package com.bellj.springBootDemoServiceAcceptanceTests.testUtilities;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSource {

    private final String URL;
    private final String driver;
    private final String username;
    private final String password;

    public DataSource(String URL, String driver, String username, String password) {
        this.URL = URL;
        this.driver = driver;
        this.username = username;
        this.password = password;
    }


    public Connection getConnection() throws ClassNotFoundException, SQLException {
        // STEP 1: Register JDBC driver
        Class.forName(driver);

        //STEP 2: Open a connection
        return DriverManager.getConnection(URL, username, password);
    }
}
