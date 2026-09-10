package com.veeteq.addressbook.rest.dto;

public class AddressDto {

    private String city;
    private String postCode;
    private String street;
    private String country;

    public String getCity() {
        return city;
    }

    public AddressDto setCity(String city) {
        this.city = city;
        return this;
    }

    public String getPostCode() {
        return postCode;
    }

    public AddressDto setPostCode(String postCode) {
        this.postCode = postCode;
        return this;
    }

    public String getStreet() {
        return street;
    }

    public AddressDto setStreet(String street) {
        this.street = street;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public AddressDto setCountry(String country) {
        this.country = country;
        return this;
    }
}
