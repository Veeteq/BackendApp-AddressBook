package com.veeteq.addressbook.rest.dto;

public class PersonDto extends ContactDto {

    private String firstName;
    private String lastName;

    public String getFirstName() {
        return firstName;
    }

    public PersonDto setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public PersonDto setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }
}