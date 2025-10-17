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

    private static final Logger LOGGER = LoggerFactory.getLogger(
        DatabaseConfig.class
    );
    private static final Properties properties = new Properties();
    private static boolean loaded = false;

    static {
        loadProperties();
    }

    /**
     * Load database properties from database.properties file
     */
    private static void loadProperties() {
        try (
            InputStream input =
                DatabaseConfig.class.getClassLoader().getResourceAsStream(
                    "database.properties"
                )
        ) {
            if (input == null) {
                LOGGER.error(
                    "database.properties file not found - database configuration required"
                );
                throw new RuntimeException(
                    "database.properties file not found"
                );
            }

            properties.load(input);
            loaded = true;
            LOGGER.debug("Database properties loaded successfully from file");
            LOGGER.debug(
                "Database URL: {}",
                properties.getProperty("database.url")
            );
            LOGGER.debug(
                "Database username: {}",
                properties.getProperty("database.username")
            );
        } catch (IOException e) {
            LOGGER.error(
                "Failed to load database.properties: {}",
                e.getMessage()
            );
            throw new RuntimeException(
                "Failed to load database.properties: " + e.getMessage()
            );
        }
    }

    /**
     * Get database connection URL
     */
    public static String getUrl() {
        return properties.getProperty("database.url");
    }

    /**
     * Get database username
     */
    public static String getUsername() {
        return properties.getProperty("database.username");
    }

    /**
     * Get database password
     */
    public static String getPassword() {
        return properties.getProperty("database.password");
    }

    /**
     * Get JDBC driver class
     */
    public static String getDriver() {
        return properties.getProperty("jdbc.driver");
    }

    /**
     * Get Hibernate dialect
     */
    public static String getDialect() {
        return properties.getProperty("hibernate.dialect");
    }

    /**
     * Get Hibernate DDL auto setting
     */
    public static String getHbm2ddlAuto() {
        return properties.getProperty("hibernate.hbm2ddl.auto");
    }

    /**
     * Get Hibernate show SQL setting
     */
    public static boolean getShowSql() {
        return Boolean.parseBoolean(
            properties.getProperty("hibernate.show_sql")
        );
    }

    /**
     * Get connection pool maximum size
     */
    public static int getMaximumPoolSize() {
        return Integer.parseInt(
            properties.getProperty("database.pool.maximumPoolSize")
        );
    }

    /**
     * Get connection pool minimum idle connections
     */
    public static int getMinimumIdle() {
        return Integer.parseInt(
            properties.getProperty("database.pool.minimumIdle")
        );
    }

    /**
     * Check if properties were successfully loaded from file
     */
    public static boolean isLoadedFromFile() {
        return loaded;
    }
}
