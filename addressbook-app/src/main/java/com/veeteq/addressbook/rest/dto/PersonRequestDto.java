package com.veeteq.addressbook.rest.dto;

public class PersonRequestDto extends ContactRequestDto {

    private String firstName;
    private String lastName;

    public String getFirstName() {
        return firstName;
    }

    public PersonRequestDto setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public PersonRequestDto setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }
}
