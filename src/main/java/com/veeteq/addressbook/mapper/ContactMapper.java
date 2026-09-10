package com.veeteq.addressbook.mapper;

import com.veeteq.addressbook.model.*;
import com.veeteq.addressbook.repository.ContactRepository;
import com.veeteq.addressbook.rest.dto.*;
import org.springframework.stereotype.Component;

@Component
public class ContactMapper {

    private final ContactRepository contactRepository;

    public ContactMapper(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    public ContactDto toDto(Contact contact) {
        var address = toDto(contact.getAddress());

        if (contact instanceof Person<?> person) {
            var dto = new PersonDto();
            dto.setId(person.getId());
            dto.setContactType(person.getContactType());
            dto.setDisplayName(person.getDisplayName() != null ? person.getDisplayName() : person.getFirstName().concat(" ").concat(person.getLastName()));
            dto.setFirstName(person.getFirstName());
            dto.setLastName(person.getLastName());
            dto.setAddress(address);
            dto.setBankAccountNumber(person.getBankAccountNumber());
            dto.setVersion(person.getVersion());
            person.getTags().forEach(dto::addToTags);
            return dto;
        }

        if (contact instanceof Company company) {
            var dto = new CompanyDto();
            dto.setId(company.getId());
            dto.setContactType(company.getContactType());
            dto.setCompanyName(company.getName());
            dto.setDisplayName(company.getDisplayName() != null ? contact.getDisplayName() : company.getName());
            dto.setAddress(address);
            dto.setBankAccountNumber(company.getBankAccountNumber());
            dto.setTaxId(company.getTaxId());
            company.getTags().forEach(dto::addToTags);
            dto.setVersion(company.getVersion());
            return dto;
        }

        throw new IllegalArgumentException("Unsupported type");
    }

    public Contact<?> toEntity(ContactRequestDto dto) {
        var address = toEntity(dto.getAddress());

        if (dto instanceof PersonRequestDto personDto) {
            var entity = new Person<>()
                    .setId(contactRepository.getId())
                    .setFirstName(personDto.getFirstName())
                    .setLastName(personDto.getLastName())
                    .setDisplayName(personDto.getDisplayName())
                    .setAddress(address)
                    .setBankAccountNumber(personDto.getBankAccountNumber());
            personDto.getTags().forEach(entity::addToTags);
            return entity;
        }

        if (dto instanceof CompanyRequestDto companyDto) {
            var entity = new Company()
                    .setId(contactRepository.getId())
                    .setName(companyDto.getCompanyName())
                    .setDisplayName(companyDto.getDisplayName())
                    .setAddress(address)
                    .setBankAccountNumber(companyDto.getBankAccountNumber())
                    .setTaxId(companyDto.getTaxId());
            companyDto.getTags().forEach(entity::addToTags);
            return entity;
        }

        throw new IllegalArgumentException("Unsupported type");
    }

    private AddressDto toDto(Address entity) {
        var dto = new AddressDto()
                .setCity(entity.getCity())
                .setPostCode(entity.getPostcode())
                .setStreet(entity.getStreet())
                .setCountry(entity.getCountry());
        return dto;
    }

    private Address toEntity(AddressDto dto) {
        var entity = new Address()
                .setCity(dto.getCity())
                .setPostcode(dto.getPostCode())
                .setStreet(dto.getStreet())
                .setCountry(dto.getCountry());
        return entity;
    }

}
