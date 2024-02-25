package com.bellj.springBootDemoServiceAcceptanceTests.testUtilities;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Bunch of utilities to support integration testing where we need the database to be in a specific state.
 */
public class DatabaseUtils {
    private static final String ADD_CLIENT_SQL = "INSERT INTO client (name, email) VALUES ('test_name', 'test_email');";
    private static final String COUNT_CLIENTS_SQL = "SELECT * FROM client;";


    public static void addClient(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(ADD_CLIENT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.executeUpdate();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void wipeAllTableData(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement();) {

           statement.execute("DROP database if exists client_db");
           statement.execute("CREATE database client_db");
            statement.execute("USE client_db");
            statement.execute("CREATE TABLE IF NOT EXISTS client (id BIGINT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255), email VARCHAR(255))");

        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void readClients(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(COUNT_CLIENTS_SQL, Statement.RETURN_GENERATED_KEYS);
             ResultSet resultSet = statement.executeQuery()) {
         while(resultSet.next()){
             System.out.printf("%s, %s, %s%n",resultSet.getString(1),resultSet.getString(2),resultSet.getString(3));
         }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
