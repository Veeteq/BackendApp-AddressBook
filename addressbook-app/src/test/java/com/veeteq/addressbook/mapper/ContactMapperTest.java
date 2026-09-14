package com.veeteq.addressbook.mapper;

import com.veeteq.addressbook.model.*;
import com.veeteq.addressbook.rest.dto.CompanyRequestDto;
import com.veeteq.addressbook.rest.dto.ContactsResponseDto;
import com.veeteq.addressbook.rest.dto.PersonDto;
import com.veeteq.addressbook.rest.dto.PersonRequestDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ContactMapperTest {

    private final ContactMapper mapper = Mappers.getMapper(ContactMapper.class);

    @Test
    void shouldMapPersonToDto() {
        var person = new Person();
        person.setId(1L);
        person.setFirstName("John");
        person.setLastName("Smith");
        person.setDisplayName("John Smith");
        person.setBankAccountNumber("PL123");
        person.setAddress(new Address()
                        .setCity("Wroclaw")
                        .setCountry("PL"));
        person.setTags(Set.of("A", "B"));

        var dto = mapper.toDto(person);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getFirstName()).isEqualTo("John");
        assertThat(dto.getLastName()).isEqualTo("Smith");
        assertThat(dto.getDisplayName()).isEqualTo("John Smith");
        assertThat(dto.getBankAccountNumber()).isEqualTo("PL123");
        assertThat(dto.getTags()).containsExactlyInAnyOrder("A", "B");
        assertThat(dto.getAddress().getCity()).isEqualTo("Wroclaw");
    }

    @Test
    void shouldMapCompanyToDto() {
        var company = new Company();
        company.setId(10L);
        company.setName("ACME");
        company.setTaxId("123456");
        company.setDisplayName("ACME");
        company.setTags(Set.of("COMPANY"));

        var dto = mapper.toDto(company);

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getCompanyName()).isEqualTo("ACME");
        assertThat(dto.getTaxId()).isEqualTo("123456");
        assertThat(dto.getDisplayName()).isEqualTo("ACME");
    }

    @Test
    void shouldMapEmployeeToPersonDto() {
        var employee = new Employee();
        employee.setId(11L);
        employee.setFirstName("Adam");
        employee.setLastName("Nowak");
        employee.setJob("Developer");

        var dto = mapper.toDto(employee);

        assertThat(dto).isInstanceOf(PersonDto.class);

        PersonDto personDto = (PersonDto) dto;
        assertThat(personDto.getFirstName()).isEqualTo("Adam");
        assertThat(personDto.getLastName()).isEqualTo("Nowak");
    }

    @Test
    void shouldMapPersonDtoToEntity() {
        var dto = new PersonRequestDto();

        dto.setFirstName("John");
        dto.setLastName("Smith");
        dto.setTags(List.of("A", "B"));

        Person entity = mapper.toEntity(dto);

        assertThat(entity.getId()).isNull();
        assertThat(entity.getFirstName()).isEqualTo("John");
        assertThat(entity.getLastName()).isEqualTo("Smith");
        assertThat(entity.getTags()).containsExactlyInAnyOrder("A", "B");
    }

    @Test
    void shouldMapCompanyDtoToEntity() {
        var dto = new CompanyRequestDto();

        dto.setCompanyName("ACME");
        dto.setTaxId("123");

        Company entity = mapper.toEntity(dto);

        assertThat(entity.getName()).isEqualTo("ACME");
        assertThat(entity.getTaxId()).isEqualTo("123");
    }

    @Test
    void shouldMapPageToResponseDto() {
        var person = new Person()
                .setFirstName("John")
                .setLastName("Smith");

        Company company = new Company()
                .setName("ACME");

        Page<Contact> page = new PageImpl<>(List.of(person, company));

        ContactsResponseDto dto = mapper.toDto(page);
        assertThat(dto.getPageSize()).isEqualTo(2);
        assertThat(dto.getTotalItems()).isEqualTo(2);
    }

    @Test
    void shouldBuildDisplayNameWhenMissing() {
        var person = new Person()
                .setFirstName("John")
                .setLastName("Smith");

        PersonDto dto = mapper.toDto(person);

        assertThat(dto.getDisplayName()).isEqualTo("John Smith");
    }

    @Test
    void shouldUseCompanyNameAsDisplayName() {
        var company = new Company()
                .setName("ACME");

        var dto = mapper.toDto(company);
        assertThat(dto.getDisplayName()).isEqualTo("ACME");
    }

    @Test
    void shouldReplaceTagsDuringUpdate() {
         var person = new Person()
                .setTags(Stream.of("OLD").collect(Collectors.toSet()));

        PersonRequestDto dto = new PersonRequestDto();
        dto.setTags(List.of("NEW"));

        mapper.updateEntity(person, dto);
        assertThat(person.getTags()).containsExactly("NEW");
    }
}
