package com.bellj.springBootDemoService.testUtilities;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@ActiveProfiles("test")
public class DataSource {

    private final String URL;
    private final String driver;
    private final String username;
    private final String password;

    public DataSource(String URL, String driver, String username, String password){
        this.URL = URL;
        this.driver = driver;
        this.username = username;
        this.password = password;
    }


    public Connection getConnection() throws ClassNotFoundException, SQLException {
        System.out.println(URL);
        // STEP 1: Register JDBC driver
        Class.forName(driver);

        //STEP 2: Open a connection
        System.out.println("Connecting to database...");
        return DriverManager.getConnection(URL,username,password);
    }
}
