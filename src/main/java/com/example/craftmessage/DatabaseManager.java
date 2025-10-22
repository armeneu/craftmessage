package com.example.craftmessage;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        DatabaseManager.class
    );
    private static EntityManagerFactory entityManagerFactory;
    private static MessageRepository messageRepository;
    private static boolean isInitialized = false;

    public static void init() {
        try {
            LOGGER.info("Initializing Hibernate JPA with PostgreSQL...");

            // Create EntityManagerFactory using persistence.xml configuration
            entityManagerFactory = Persistence.createEntityManagerFactory(
                "craftmessage"
            );

            // Initialize repository
            messageRepository = new MessageRepository();

            isInitialized = true;
            LOGGER.info("Hibernate JPA initialized successfully");
        } catch (Exception e) {
            LOGGER.error(
                "Failed to initialize Hibernate JPA: {}",
                e.getMessage()
            );
            isInitialized = false;
            closeEntityManagerFactory();
        }
    }

    /**
     * Check if database is currently available by testing a simple connection
     */
    public static boolean isDatabaseAvailable() {
        if (!isInitialized || entityManagerFactory == null) {
            return false;
        }

        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();

            // Test connection with a simple query
            Object result = entityManager
                .createNativeQuery("SELECT 1")
                .setMaxResults(1)
                .getSingleResult();

            return result != null;
        } catch (Exception e) {
            LOGGER.debug(
                "Database connection check failed: {}",
                e.getMessage()
            );
            return false;
        } finally {
            if (entityManager != null) {
                try {
                    entityManager.close();
                } catch (Exception e) {
                    LOGGER.debug(
                        "Error closing test entity manager: {}",
                        e.getMessage()
                    );
                }
            }
        }
    }

    public static boolean saveMessage(UUID playerUuid, String messageText) {
        if (!isInitialized || entityManagerFactory == null) {
            LOGGER.warn("Cannot save message - Hibernate not initialized");
            return false;
        }

        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            injectEntityManager(messageRepository, entityManager);

            entityManager.getTransaction().begin();

            MessageEntity message = new MessageEntity(playerUuid, messageText);
            messageRepository.save(message);

            entityManager.getTransaction().commit();

            LOGGER.info(
                "Message saved successfully with ID: {}",
                message.getId()
            );
            return true;
        } catch (Exception e) {
            if (
                entityManager != null &&
                entityManager.getTransaction().isActive()
            ) {
                try {
                    entityManager.getTransaction().rollback();
                } catch (Exception rollbackEx) {
                    LOGGER.debug(
                        "Rollback failed: {}",
                        rollbackEx.getMessage()
                    );
                }
            }
            LOGGER.error(
                "Failed to save message using JPA: {}",
                e.getMessage()
            );
            return false;
        } finally {
            if (entityManager != null) {
                try {
                    entityManager.close();
                } catch (Exception e) {
                    LOGGER.debug(
                        "Error closing entity manager: {}",
                        e.getMessage()
                    );
                }
            }
        }
    }

    private static void injectEntityManager(
        MessageRepository repository,
        EntityManager entityManager
    ) {
        // Use reflection to inject EntityManager into the repository
        try {
            var field = MessageRepository.class.getDeclaredField(
                "entityManager"
            );
            field.setAccessible(true);
            field.set(repository, entityManager);
        } catch (Exception e) {
            throw new RuntimeException(
                "Failed to inject EntityManager into repository",
                e
            );
        }
    }

    private static void closeEntityManagerFactory() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            try {
                entityManagerFactory.close();
                LOGGER.info("Hibernate JPA connection closed");
            } catch (Exception e) {
                LOGGER.error(
                    "Error closing EntityManagerFactory: {}",
                    e.getMessage()
                );
            }
        }
        entityManagerFactory = null;
        messageRepository = null;
    }

    public static void shutdown() {
        isInitialized = false;
        closeEntityManagerFactory();
    }
}
