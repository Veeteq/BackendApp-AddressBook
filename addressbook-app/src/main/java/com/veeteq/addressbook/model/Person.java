package com.veeteq.addressbook.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("person")
public class Person extends Contact {

    @Column(name = "cont_firs_name_tx")
    private String firstName;

    @Column(name = "cont_last_name_tx")
    private String lastName;

    public Person() {}

    public String getFirstName() {
        return firstName;
    }

    public Person setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public Person setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }
}