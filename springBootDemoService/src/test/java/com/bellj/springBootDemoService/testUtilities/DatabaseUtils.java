package com.bellj.springBootDemoService.testUtilities;

import java.sql.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Bunch of utilities to support integration testing where we need the database to be in a specific state.
 */
public class DatabaseUtils {
    private static final String ADD_CLIENT_SQL = "INSERT INTO CLIENT (NAME, EMAIL) VALUES ('test_name', 'test_email');";
    private static final String COUNT_CLIENTS_SQL = "SELECT COUNT(*) FROM CLIENT;";


    public static void addClient(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(ADD_CLIENT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.executeUpdate();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void wipeAllTableData(DataSource dataSource) {
        try (Connection c = dataSource.getConnection(); Statement s = c.createStatement();) {

            // Disable FK
            s.execute("SET REFERENTIAL_INTEGRITY FALSE");

            // Find all tables and truncate them
            Set<String> tables = new HashSet<String>();
            ResultSet rs = s.executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES  where TABLE_SCHEMA='PUBLIC'");
            while (rs.next()) {
                tables.add(rs.getString(1));
            }
            rs.close();
            for (String table : tables) {
                s.executeUpdate("TRUNCATE TABLE " + table);
            }

            // Idem for sequences
            Set<String> sequences = new HashSet<String>();
            rs = s.executeQuery("SELECT SEQUENCE_NAME FROM INFORMATION_SCHEMA.SEQUENCES WHERE SEQUENCE_SCHEMA='PUBLIC'");
            while (rs.next()) {
                sequences.add(rs.getString(1));
            }
            rs.close();
            for (String seq : sequences) {
                s.executeUpdate("ALTER SEQUENCE " + seq + " RESTART WITH 1");
            }

            // Enable FK
            s.execute("SET REFERENTIAL_INTEGRITY TRUE");
            s.close();
            c.close();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static int countClients(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(COUNT_CLIENTS_SQL, Statement.RETURN_GENERATED_KEYS);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            } else {
                throw new RuntimeException("No result set");
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
