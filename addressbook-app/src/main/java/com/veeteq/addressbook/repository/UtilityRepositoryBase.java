package com.veeteq.addressbook.repository;

import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(value = "default")
public class UtilityRepositoryBase implements UtilityRepository {

    private final EntityManager entityManager;

    public UtilityRepositoryBase(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Long nextId() {
        return ((Number) entityManager
                .createNativeQuery("select next value for contacts_seq")
                .getSingleResult())
                .longValue();
    }
}
