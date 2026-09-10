package com.veeteq.addressbook.rest.dto;

public class CompanyDto extends ContactDto {

    private String companyName;
    private String taxId;

    public String getCompanyName() {
        return companyName;
    }

    public CompanyDto setCompanyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    public String getTaxId() {
        return taxId;
    }

    public CompanyDto setTaxId(String taxId) {
        this.taxId = taxId;
        return this;
    }
}