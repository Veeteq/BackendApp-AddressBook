package com.veeteq.addressbook.mapper;

import com.veeteq.addressbook.model.Address;
import com.veeteq.addressbook.model.Company;
import com.veeteq.addressbook.model.Contact;
import com.veeteq.addressbook.model.Person;
import com.veeteq.addressbook.rest.dto.*;
import org.mapstruct.*;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ContactMapper {

    @Mapping(target = "displayName", expression = "java(entity.getDisplayName() != null ? entity.getDisplayName() : entity.getFirstName() + \" \" + entity.getLastName())")
    PersonDto toDto(Person entity);

    @Mapping(source = "name", target = "companyName")
    @Mapping(target = "displayName", expression = "java(entity.getDisplayName() != null ? entity.getDisplayName() : entity.getName())")
    CompanyDto toDto(Company entity);

    @Mapping(target = "id",      ignore = true)
    @Mapping(target = "version", ignore = true)
    Person toEntity(PersonRequestDto dto);

    @Mapping(target = "id",      ignore = true)
    @Mapping(target = "name",    source = "companyName")
    @Mapping(target = "version", ignore = true)
    Company toEntity(CompanyRequestDto dto);

    @AfterMapping
    default void updateTags(ContactRequestDto source, @MappingTarget Contact target) {
        target.getTags().clear();
        if (source.getTags() != null) {
            target.getTags().addAll(source.getTags());
        }
    }

    AddressDto toDto(Address address);
    Address toEntity(AddressDto dto);

    @Mapping(target = "id",   ignore = true)
    @Mapping(target = "tags", ignore = true)
    Person updateEntity(PersonRequestDto source,  @MappingTarget Person target);

    @Mapping(target = "id",   ignore = true)
    @Mapping(target = "name", source = "companyName")
    @Mapping(target = "tags", ignore = true)
    Company updateEntity(CompanyRequestDto source, @MappingTarget Company target);

    @Mapping(target = "pageSize",    source = "size")
    @Mapping(target = "totalItems",  source = "totalElements")
    @Mapping(target = "totalPages",  source = "totalPages")
    @Mapping(target = "currentPage", source = "number")
    @Mapping(target = "data",        ignore = true)
    ContactsResponseDto toDto(Page<Contact> result);

    @AfterMapping
    default void fillData(Page<Contact> source, @MappingTarget ContactsResponseDto target) {
        var data = source.stream()
                .map(this::toDto)
                .toList();
        target.setData(data);
    }

    default ContactDto toDto(Contact entity) {
        return switch (entity) {
            case Person person -> toDto(person);
            case Company company -> toDto(company);
            default -> throw new IllegalArgumentException();
        };
    }

    default Contact toEntity(ContactRequestDto dto) {
        return switch (dto) {
            case PersonRequestDto person -> toEntity(person);
            case CompanyRequestDto company -> toEntity(company);
            default -> throw new IllegalArgumentException();
        };
    }

    default Contact updateEntity(Contact entity, ContactRequestDto dto) {
        return switch (entity) {
            case Person person
                    when dto instanceof PersonRequestDto personDto -> updateEntity(personDto, person);
            case Company company
                    when dto instanceof CompanyRequestDto companyDto -> updateEntity(companyDto, company);
            default -> throw new IllegalArgumentException();
        };
    }
}