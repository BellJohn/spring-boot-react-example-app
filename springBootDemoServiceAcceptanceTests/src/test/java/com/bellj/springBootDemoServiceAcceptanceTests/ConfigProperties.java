package com.bellj.springBootDemoServiceAcceptanceTests;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Properties;

public class ConfigProperties {

    private static String databaseUrl;
    private static String databaseUsername;
    private static String databasePassword;
    private static String driver;
    private static String apiUrl;

    private static int databasePort;
    private static int apiPort;


    static {
        Properties prop = new Properties();
        try (InputStream input = Files.newInputStream(Path.of("src/test/resources/config.properties"))) {
            prop.load(input);
            databaseUrl = prop.getProperty("datasource.url");
            databaseUsername = prop.getProperty("datasource.username");
            databasePassword = prop.getProperty("datasource.password");
            driver = prop.getProperty("datasource.driverClassName");
            databasePort = Integer.parseInt(prop.getProperty("datasource.port"));
            apiUrl = prop.getProperty("api.url");
            apiPort = Integer.parseInt(prop.getProperty("api.port"));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String getDatabaseUrl() {
        return databaseUrl;
    }

    public static String getDatabaseUsername() {
        return databaseUsername;
    }

    public static String getDatabasePassword() {
        return databasePassword;
    }

    public static String getDriver() {
        return driver;
    }

    public static int getDatabasePort() {
        return databasePort;
    }

    public static String getApiUrl() {
        return apiUrl;
    }

    public static int getApiPort() {
        return apiPort;
    }
}
