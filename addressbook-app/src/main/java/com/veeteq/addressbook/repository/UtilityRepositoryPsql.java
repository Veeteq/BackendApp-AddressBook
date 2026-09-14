package com.veeteq.addressbook.repository;

import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
@Profile(value = "dev")
public class UtilityRepositoryPsql implements UtilityRepository {

    private final EntityManager entityManager;

    public UtilityRepositoryPsql(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Long nextId() {
        return ((Number) entityManager
                .createNativeQuery("SELECT nextval('contacts_seq')")
                .getSingleResult())
                .longValue();
    }
}
