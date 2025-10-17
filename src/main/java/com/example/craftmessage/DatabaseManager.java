package com.example.craftmessage;

import jakarta.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hibernate 6.x Database Manager using JPA with Repository pattern
 * Modern implementation with better performance and cleaner architecture
 */
public class DatabaseManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        DatabaseManager.class
    );

    // Single thread executor for database operations
    private static final ExecutorService DATABASE_EXECUTOR =
        Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r, "craftmessage-hibernate6");
            thread.setDaemon(true);
            return thread;
        });

    private static EntityManagerFactory entityManagerFactory;
    private static MessageRepository messageRepository;
    private static boolean initialized = false;
    private static boolean databaseAvailable = false;

    /**
     * Initialize Hibernate with JPA configuration
     */
    public static synchronized void initialize() {
        if (initialized) {
            LOGGER.debug("Hibernate already initialized, skipping");
            return;
        }

        LOGGER.info("Starting Hibernate initialization...");

        try {
            // Use database.properties for configuration
            Map<String, Object> properties = new HashMap<>();

            // Database connection settings from properties file
            properties.put(
                "jakarta.persistence.jdbc.url",
                DatabaseConfig.getUrl()
            );
            properties.put(
                "jakarta.persistence.jdbc.user",
                DatabaseConfig.getUsername()
            );
            properties.put(
                "jakarta.persistence.jdbc.password",
                DatabaseConfig.getPassword()
            );
            properties.put(
                "jakarta.persistence.jdbc.driver",
                DatabaseConfig.getDriver()
            );

            // Hibernate settings from properties file
            properties.put("hibernate.dialect", DatabaseConfig.getDialect());
            properties.put(
                "hibernate.hbm2ddl.auto",
                DatabaseConfig.getHbm2ddlAuto()
            );
            properties.put(
                "hibernate.show_sql",
                String.valueOf(DatabaseConfig.getShowSql())
            );
            properties.put("hibernate.format_sql", "true");

            // Connection pool settings from properties file
            properties.put(
                "hibernate.connection.pool_size",
                String.valueOf(DatabaseConfig.getMaximumPoolSize())
            );

            // Create EntityManagerFactory using HibernatePersistenceProvider
            try {
                HibernatePersistenceProvider persistenceProvider =
                    new HibernatePersistenceProvider();

                // Add explicit entity classes to properties
                properties.put(
                    "hibernate.ejb.entitymanager_factory_name",
                    "craftmessage"
                );

                LOGGER.info(
                    "Using database configuration: {}",
                    DatabaseConfig.getUrl()
                );

                try {
                    entityManagerFactory =
                        persistenceProvider.createEntityManagerFactory(
                            "craftmessage",
                            properties
                        );

                    if (entityManagerFactory == null) {
                        throw new RuntimeException(
                            "No persistence units found - EntityManagerFactory is null"
                        );
                    }
                } catch (Exception e) {
                    LOGGER.error(
                        "Failed to create EntityManagerFactory: {}",
                        e.getMessage()
                    );
                    throw e;
                }
            } catch (Exception e) {
                LOGGER.error(
                    "Failed to create EntityManagerFactory: {}",
                    e.getMessage()
                );
                throw e;
            }

            // Create repository only if EntityManagerFactory was created successfully
            if (entityManagerFactory != null) {
                messageRepository = new MessageRepository(entityManagerFactory);

                // Test database connection
                try {
                    messageRepository.count();
                    databaseAvailable = true;
                } catch (Exception e) {
                    LOGGER.error(
                        "Database connection test failed: {}",
                        e.getMessage()
                    );
                    databaseAvailable = false;
                }
            } else {
                databaseAvailable = false;
            }

            initialized = true;

            if (databaseAvailable) {
                LOGGER.info("Hibernate initialization completed successfully");
            } else {
                LOGGER.warn(
                    "Hibernate initialized but database connection unavailable"
                );
            }
        } catch (Exception e) {
            LOGGER.error("Failed to initialize Hibernate: {}", e.getMessage());
            entityManagerFactory = null;
            messageRepository = null;
            initialized = true;
            databaseAvailable = false;
        }
    }

    /**
     * Save message asynchronously using JPA Repository
     */
    public static CompletableFuture<Boolean> saveMessageAsync(
        String playerUuid,
        String messageText
    ) {
        LOGGER.debug(
            "Scheduling async message save for player: {}",
            playerUuid
        );
        return CompletableFuture.supplyAsync(
            () -> saveMessage(playerUuid, messageText),
            DATABASE_EXECUTOR
        );
    }

    /**
     * Synchronous save method using JPA Repository
     */
    public static boolean saveMessage(String playerUuid, String messageText) {
        // Ensure initialization
        if (!initialized) {
            initialize();
        }

        if (!databaseAvailable || messageRepository == null) {
            LOGGER.warn("Cannot save message - database unavailable");
            return false;
        }

        try {
            // Create and save message entity using repository
            MessageEntity message = new MessageEntity();
            message.setUuid(java.util.UUID.fromString(playerUuid));
            message.setText(messageText);

            var savedMessage = messageRepository.save(message);

            if (savedMessage.isPresent()) {
                LOGGER.info(
                    "Message saved successfully. ID: {}",
                    savedMessage.get().getId()
                );
                return true;
            } else {
                LOGGER.error(
                    "Failed to save message - repository returned empty"
                );
                return false;
            }
        } catch (Exception e) {
            LOGGER.error("Failed to save message: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Find messages by player UUID using JPA Repository
     */
    public static java.util.List<MessageEntity> findMessagesByPlayer(
        String playerUuid
    ) {
        if (!initialized) {
            initialize();
        }

        if (!databaseAvailable || messageRepository == null) {
            LOGGER.warn("Cannot find messages - database unavailable");
            return java.util.List.of();
        }

        try {
            return messageRepository.findByPlayerUuid(
                java.util.UUID.fromString(playerUuid)
            );
        } catch (Exception e) {
            LOGGER.error("Failed to find messages: {}", e.getMessage());
            return java.util.List.of();
        }
    }

    /**
     * Get all messages using JPA Repository
     */
    public static java.util.List<MessageEntity> findAllMessages() {
        if (!initialized) {
            initialize();
        }

        if (!databaseAvailable || messageRepository == null) {
            return java.util.List.of();
        }

        try {
            return messageRepository.findAll();
        } catch (Exception e) {
            return java.util.List.of();
        }
    }

    /**
     * Get message count using JPA Repository
     */
    public static long getMessageCount() {
        if (!initialized) {
            initialize();
        }

        if (!databaseAvailable || messageRepository == null) {
            return 0L;
        }

        try {
            return messageRepository.count();
        } catch (Exception e) {
            return 0L;
        }
    }

    /**
     * Check if database is available
     * This will initialize Hibernate if not already initialized
     */
    public static boolean isDatabaseAvailable() {
        if (!initialized) {
            initialize();
        }
        return databaseAvailable && entityManagerFactory != null;
    }

    /**
     * Get the message repository for advanced operations
     */
    public static MessageRepository getMessageRepository() {
        if (!initialized) {
            initialize();
        }
        if (messageRepository == null) {
            throw new IllegalStateException(
                "MessageRepository not available - Hibernate initialization failed"
            );
        }
        return messageRepository;
    }

    /**
     * Close resources
     */
    public static void close() {
        LOGGER.info("Closing Hibernate resources...");

        if (entityManagerFactory != null) {
            try {
                entityManagerFactory.close();
                LOGGER.info("EntityManagerFactory closed successfully");
            } catch (Exception e) {
                LOGGER.error(
                    "Failed to close EntityManagerFactory: {}",
                    e.getMessage()
                );
            } finally {
                entityManagerFactory = null;
            }
        }

        // Reset state
        initialized = false;
        databaseAvailable = false;
        messageRepository = null;

        // Shutdown executor
        DATABASE_EXECUTOR.shutdown();
        LOGGER.info("Database executor shutdown initiated");
    }
}
