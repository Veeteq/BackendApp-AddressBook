package com.veeteq.addressbook.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("company")
public class Company extends Contact<Company> {

    @Column(name = "cont_name_tx")
    private String name;

    @Column(name = "cont_txid_tx")
    private String taxId;

    public Company() {}

    public String getName() {
        return name;
    }

    public Company setName(String name) {
        this.name = name;
        return this;
    }

    public String getTaxId() {
        return taxId;
    }

    public Company setTaxId(String taxId) {
        this.taxId = taxId;
        return this;
    }
}
