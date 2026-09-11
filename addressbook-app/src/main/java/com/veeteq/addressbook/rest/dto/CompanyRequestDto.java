package com.veeteq.addressbook.rest.dto;

public class CompanyRequestDto extends ContactRequestDto {

    private String companyName;
    private String taxId;

    public String getCompanyName() {
        return companyName;
    }

    public CompanyRequestDto setCompanyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    public String getTaxId() {
        return taxId;
    }

    public CompanyRequestDto setTaxId(String taxId) {
        this.taxId = taxId;
        return this;
    }
}
