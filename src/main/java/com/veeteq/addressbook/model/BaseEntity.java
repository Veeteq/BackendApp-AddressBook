package com.veeteq.addressbook.model;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseEntity<T extends BaseEntity<T>> {

    @Id
    private Long id;

    protected BaseEntity() {}

    public Long getId() {
        return id;
    }

    public T setId(Long id) {
        this.id = id;
        return (T) this;
    }
}