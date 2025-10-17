package com.example.craftmessage;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JPA Repository for MessageEntity using Hibernate 6.x
 * Provides data access operations for message storage
 */
public class MessageRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        MessageRepository.class
    );

    private final EntityManagerFactory entityManagerFactory;

    public MessageRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    /**
     * Save a message entity to the database
     */
    public Optional<MessageEntity> save(MessageEntity message) {
        if (entityManagerFactory == null) {
            LOGGER.error("Cannot save message - EntityManagerFactory is null");
            return Optional.empty();
        }

        EntityManager entityManager = null;
        EntityTransaction transaction = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();

            entityManager.persist(message);
            transaction.commit();

            LOGGER.debug(
                "Message saved successfully with ID: {}",
                message.getId()
            );
            return Optional.of(message);
        } catch (Exception e) {
            LOGGER.error("Failed to save message", e);
            if (transaction != null && transaction.isActive()) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackEx) {
                    LOGGER.error("Failed to rollback transaction", rollbackEx);
                }
            }
            return Optional.empty();
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    /**
     * Find a message by its ID
     */
    public Optional<MessageEntity> findById(Long id) {
        if (entityManagerFactory == null) {
            LOGGER.error(
                "Cannot find message by ID - EntityManagerFactory is null"
            );
            return Optional.empty();
        }

        EntityManager entityManager = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            MessageEntity message = entityManager.find(MessageEntity.class, id);
            return Optional.ofNullable(message);
        } catch (Exception e) {
            LOGGER.error("Failed to find message by ID {}", id, e);
            return Optional.empty();
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    /**
     * Find all messages for a specific player UUID
     */
    public List<MessageEntity> findByPlayerUuid(UUID playerUuid) {
        if (entityManagerFactory == null) {
            LOGGER.error(
                "Cannot find messages by player UUID - EntityManagerFactory is null"
            );
            return List.of();
        }

        EntityManager entityManager = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            TypedQuery<MessageEntity> query = entityManager.createQuery(
                "SELECT m FROM MessageEntity m WHERE m.uuid = :playerUuid ORDER BY m.id DESC",
                MessageEntity.class
            );
            query.setParameter("playerUuid", playerUuid);
            return query.getResultList();
        } catch (Exception e) {
            LOGGER.error(
                "Failed to find messages for player {}",
                playerUuid,
                e
            );
            return List.of();
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    /**
     * Find all messages, ordered by ID descending (newest first)
     */
    public List<MessageEntity> findAll() {
        if (entityManagerFactory == null) {
            LOGGER.error(
                "Cannot find all messages - EntityManagerFactory is null"
            );
            return List.of();
        }

        EntityManager entityManager = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            TypedQuery<MessageEntity> query = entityManager.createQuery(
                "SELECT m FROM MessageEntity m ORDER BY m.id DESC",
                MessageEntity.class
            );
            return query.getResultList();
        } catch (Exception e) {
            LOGGER.error("Failed to find all messages", e);
            return List.of();
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    /**
     * Delete a message by its ID
     */
    public boolean deleteById(Long id) {
        if (entityManagerFactory == null) {
            LOGGER.error(
                "Cannot delete message by ID - EntityManagerFactory is null"
            );
            return false;
        }

        EntityManager entityManager = null;
        EntityTransaction transaction = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();

            MessageEntity message = entityManager.find(MessageEntity.class, id);
            if (message != null) {
                entityManager.remove(message);
                transaction.commit();
                LOGGER.debug("Message with ID {} deleted successfully", id);
                return true;
            } else {
                transaction.rollback();
                LOGGER.warn("Message with ID {} not found for deletion", id);
                return false;
            }
        } catch (Exception e) {
            LOGGER.error("Failed to delete message with ID {}", id, e);
            if (transaction != null && transaction.isActive()) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackEx) {
                    LOGGER.error("Failed to rollback transaction", rollbackEx);
                }
            }
            return false;
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    /**
     * Count total number of messages
     */
    public long count() {
        if (entityManagerFactory == null) {
            LOGGER.error(
                "Cannot count messages - EntityManagerFactory is null"
            );
            return 0L;
        }

        EntityManager entityManager = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(m) FROM MessageEntity m",
                Long.class
            );
            return query.getSingleResult();
        } catch (Exception e) {
            LOGGER.error("Failed to count messages", e);
            return 0L;
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    /**
     * Check if a message with the given ID exists
     */
    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }
}
