package com.veeteq.addressbook.rest.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Set;
import java.util.TreeSet;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "contactType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PersonRequestDto.class, name = "person"),
        @JsonSubTypes.Type(value = CompanyRequestDto.class, name = "company")
})
public abstract class ContactRequestDto {

    private String displayName;
    private AddressDto address;
    private String bankAccountNumber;
    private Set<String> tags = new TreeSet<>();

    public String getDisplayName() {
        return displayName;
    }

    public ContactRequestDto setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public AddressDto getAddress() {
        return address;
    }

    public ContactRequestDto setAddress(AddressDto address) {
        this.address = address;
        return this;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public ContactRequestDto setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
        return this;
    }

    public Set<String> getTags() {
        return tags;
    }

    public ContactRequestDto setTags(Set<String> tags) {
        this.tags = tags;
        return this;
    }
}