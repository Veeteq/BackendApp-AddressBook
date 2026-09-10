package com.veeteq.addressbook.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Address {

    @Column(name = "addr_city_tx")
    private String city;

    @Column(name = "addr_post_tx")
    private String postcode;

    @Column(name = "addr_stre_tx")
    private String street;

    @Column(name = "addr_cntr_tx")
    private String country;

    public Address() {}

    public String getCity() {
        return city;
    }

    public Address setCity(String city) {
        this.city = city;
        return this;
    }

    public String getPostcode() {
        return postcode;
    }

    public Address setPostcode(String postcode) {
        this.postcode = postcode;
        return this;
    }

    public String getStreet() {
        return street;
    }

    public Address setStreet(String street) {
        this.street = street;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public Address setCountry(String country) {
        this.country = country;
        return this;
    }

    @Override
    public String toString() {
        return "AddressEntity{city='" + city + ", postcode='" + postcode + ", street='" + street + ", country='" + country + '}';
    }

}