package com.veeteq.addressbook.repository;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;

@Component
@Profile(value = "prod")
public class UtilityRepositoryOracle implements UtilityRepository {

    private final EntityManager entityManager;

    public UtilityRepositoryOracle(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Long nextId() {
        return ((Number) entityManager
                .createNativeQuery("select cont_seq.nextval from dual")
                .getSingleResult())
                .longValue();
    }
}
