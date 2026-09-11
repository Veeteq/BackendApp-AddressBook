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
        @JsonSubTypes.Type(value = PersonDto.class, name = "PERSON"),
        @JsonSubTypes.Type(value = CompanyDto.class, name = "COMPANY")
})
public abstract class ContactDto {

    private Long id;
    private String displayName;
    private String contactType;
    private AddressDto address;
    private String bankAccountNumber;
    private Integer version;
    private Set<String> tags = new TreeSet<>();

    public Long getId() {
        return id;
    }

    public ContactDto setId(Long id) {
        this.id = id;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ContactDto setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public String getContactType() {
        return contactType;
    }

    public ContactDto setContactType(String contactType) {
        this.contactType = contactType;
        return this;
    }

    public AddressDto getAddress() {
        return address;
    }

    public ContactDto setAddress(AddressDto address) {
        this.address = address;
        return this;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public ContactDto setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
        return this;
    }

    public Integer getVersion() {
        return version;
    }

    public ContactDto setVersion(Integer version) {
        this.version = version;
        return this;
    }

    public Set<String> getTags() {
        return tags;
    }

    public ContactDto setTags(Set<String> tags) {
        this.tags = tags;
        return this;
    }

    public ContactDto addToTags(String tag) {
        this.tags.add(tag);
        return this;
    }
}
