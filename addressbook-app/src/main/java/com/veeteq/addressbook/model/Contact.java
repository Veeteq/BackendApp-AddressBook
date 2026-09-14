package com.veeteq.addressbook.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.TreeSet;

@Entity
@Table(name = "contacts")
@AttributeOverride(name = "id", column = @Column(name = "cont_id"))
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type_tx", discriminatorType = DiscriminatorType.STRING)
public abstract class Contact extends BaseEntity {

    @Column(name = "cont_disp_name_tx")
    private String displayName;

    @Embedded
    private Address address;

    @Column(name = "cont_iban_tx")
    private String bankAccountNumber;

    @CreationTimestamp
    @Column(name = "crea_dt", nullable = false, updatable = false)
    private LocalDateTime createDateTime;

    @UpdateTimestamp
    @Column(name = "updt_dt", nullable = false, updatable = true)
    private LocalDateTime updateDateTime;

    @Version
    @Column(name = "vers_nm")
    private Integer version;

    @ElementCollection
    @CollectionTable(name = "contact_tags", joinColumns = @JoinColumn(name = "cont_id", referencedColumnName = "cont_id"))
    @Column(name = "ctag_tx", nullable = false)
    private Set<String> tags = new TreeSet<>();

    protected Contact() {}

    @Transient
    public String getContactType() {
        DiscriminatorValue discriminatorValue = this.getClass().getAnnotation(DiscriminatorValue.class);
        return discriminatorValue == null ? null : discriminatorValue.value();
    }

    public String getDisplayName() {
        return displayName;
    }

    public Contact setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public Address getAddress() {
        return address;
    }

    public Contact setAddress(Address address) {
        this.address = address;
        return this;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public Contact setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
        return this;
    }

    public Set<String> getTags() {
        return tags;
    }

    public Contact setTags(Set<String> tags) {
        this.tags = tags;
        return this;
    }

    public Contact addToTags(String tag) {
        this.tags.add(tag);
        return this;
    }

    public Integer getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "ContactEntity{id=" + getId() + ", address=" + address + ", bankAccount='" + bankAccountNumber + ", version=" + version + '}';
    }

}
