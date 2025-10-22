package com.example.craftmessage;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Transactional
public class MessageRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public MessageEntity save(MessageEntity message) {
        if (message.getId() == null) {
            entityManager.persist(message);
            return message;
        } else {
            return entityManager.merge(message);
        }
    }

    public MessageEntity findById(Long id) {
        return entityManager.find(MessageEntity.class, id);
    }

    public List<MessageEntity> findAll() {
        TypedQuery<MessageEntity> query = entityManager.createQuery(
            "SELECT m FROM MessageEntity m ORDER BY m.id DESC",
            MessageEntity.class
        );
        return query.getResultList();
    }

    public List<MessageEntity> findByUuid(UUID uuid) {
        TypedQuery<MessageEntity> query = entityManager.createQuery(
            "SELECT m FROM MessageEntity m WHERE m.uuid = :uuid ORDER BY m.id DESC",
            MessageEntity.class
        );
        query.setParameter("uuid", uuid);
        return query.getResultList();
    }

    public void delete(MessageEntity message) {
        if (entityManager.contains(message)) {
            entityManager.remove(message);
        } else {
            entityManager.remove(entityManager.merge(message));
        }
    }

    public long count() {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(m) FROM MessageEntity m",
            Long.class
        );
        return query.getSingleResult();
    }
}
