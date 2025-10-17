package com.example.craftmessage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to read database configuration from database.properties file
 */
public class DatabaseConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConfig.class);
    private static final Properties properties = new Properties();
    private static boolean loaded = false;

    static {
        loadProperties();
    }

    /**
     * Load database properties from database.properties file
     */
    private static void loadProperties() {
        try (InputStream input = DatabaseConfig.class.getClassLoader()
                .getResourceAsStream("database.properties")) {

            if (input == null) {
                LOGGER.warn("database.properties file not found, using default values");
                setDefaultProperties();
                return;
            }

            properties.load(input);
            loaded = true;
            LOGGER.debug("Database properties loaded successfully");

        } catch (IOException e) {
            LOGGER.error("Failed to load database.properties, using default values: {}", e.getMessage());
            setDefaultProperties();
        }
    }

    /**
     * Set default database properties
     */
    private static void setDefaultProperties() {
        properties.setProperty("database.url", "jdbc:postgresql://localhost:5433/minecraft");
        properties.setProperty("database.username", "minecraft");
        properties.setProperty("database.password", "password");
        properties.setProperty("jdbc.driver", "org.postgresql.Driver");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.setProperty("hibernate.hbm2ddl.auto", "update");
        properties.setProperty("hibernate.show_sql", "false");
        properties.setProperty("database.pool.maximumPoolSize", "10");
        properties.setProperty("database.pool.minimumIdle", "2");
    }

    /**
     * Get database connection URL
     */
    public static String getUrl() {
        return properties.getProperty("database.url", "jdbc:postgresql://localhost:5433/minecraft");
    }

    /**
     * Get database username
     */
    public static String getUsername() {
        return properties.getProperty("database.username", "minecraft");
    }

    /**
     * Get database password
     */
    public static String getPassword() {
        return properties.getProperty("database.password", "password");
    }

    /**
     * Get JDBC driver class
     */
    public static String getDriver() {
        return properties.getProperty("jdbc.driver", "org.postgresql.Driver");
    }

    /**
     * Get Hibernate dialect
     */
    public static String getDialect() {
        return properties.getProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
    }

    /**
     * Get Hibernate DDL auto setting
     */
    public static String getHbm2ddlAuto() {
        return properties.getProperty("hibernate.hbm2ddl.auto", "update");
    }

    /**
     * Get Hibernate show SQL setting
     */
    public static boolean getShowSql() {
        return Boolean.parseBoolean(properties.getProperty("hibernate.show_sql", "false"));
    }

    /**
     * Get connection pool maximum size
     */
    public static int getMaximumPoolSize() {
        return Integer.parseInt(properties.getProperty("database.pool.maximumPoolSize", "10"));
    }

    /**
     * Get connection pool minimum idle connections
     */
    public static int getMinimumIdle() {
        return Integer.parseInt(properties.getProperty("database.pool.minimumIdle", "2"));
    }

    /**
     * Check if properties were successfully loaded from file
     */
    public static boolean isLoadedFromFile() {
        return loaded;
    }
}
