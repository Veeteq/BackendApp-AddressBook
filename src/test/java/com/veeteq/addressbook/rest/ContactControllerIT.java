package com.veeteq.addressbook.rest;

import com.veeteq.addressbook.model.Address;
import com.veeteq.addressbook.model.Company;
import com.veeteq.addressbook.model.Contact;
import com.veeteq.addressbook.model.Person;
import com.veeteq.addressbook.repository.ContactRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
GET
✓ shouldReturnContacts

UPDATE
✓ shouldUpdatePerson
✓ shouldUpdateCompany
✓ shouldReturn404WhenUpdatingMissingContact

DELETE
✓ shouldDeleteContact
✓ shouldReturn404WhenDeletingMissingContact

OPTIMISTIC LOCKING
✓ shouldRejectStaleVersion
 */

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ContactControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ContactRepository repository;

    @Test
    void shouldCreatePerson() throws Exception {
        var payload = personJson();
        mockMvc.perform(post("/api/addressbook/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.contactType").value("person"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.tags.length()").value(2));

        List<Contact> contacts = repository.findAll();

        assertThat(contacts).hasSize(4);
        assertThat(contacts.getFirst()).isInstanceOf(Person.class);

        Person<?> person = (Person<?>) contacts.getFirst();

        assertThat(person.getFirstName()).isEqualTo("John");
        assertThat(person.getLastName()).isEqualTo("Smith");
    }

    @Test
    void shouldCreateCompany() throws Exception {
        var payload = companyJson();
        mockMvc.perform(post("/api/addressbook/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.contactType").value("company"))
                .andExpect(jsonPath("$.companyName").value("Veeteq"))
                .andExpect(jsonPath("$.taxId").value("PL123456789"));

        List<Contact> contacts = repository.findAll();

        assertThat(contacts).hasSize(4);
        assertThat(contacts.getFirst()).isInstanceOf(Company.class);

        Company company = (Company) contacts.getFirst();

        assertThat(company.getName()).isEqualTo("Veeteq");
        assertThat(company.getTaxId()).isEqualTo("PL123456789");
    }

    @Test
    void shouldReturnMixedContactTypes() throws Exception {
        var address = new Address().setCity("Philadelphia")
                .setCountry("United States")
                .setStreet("105 Main Street")
                .setPostcode("19092");
        repository.save(new Person<>()
                .setId(1L)
                .setFirstName("John")
                .setLastName("Smith")
                .setAddress(address));

        repository.save(new Company()
                .setId(2L)
                .setName("Veeteq")
                .setAddress(address));

        mockMvc.perform(get("/api/addressbook/contacts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].contactType").exists())
                .andExpect(jsonPath("$[1].contactType").exists());
    }

    @Test
    void shouldUpdateCompany() throws Exception {
        // given
        Company company = new Company()
                .setId(100L)
                .setName("Old Company")
                .setTaxId("OLD-TAX");
        repository.save(company);

        String requestBody = updateCompanyJson();

        // when
        mockMvc.perform(put("/api/addressbook/contacts/{id}", 100L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.companyName")
                        .value("New Company"))
                .andExpect(jsonPath("$.taxId")
                        .value("NEW-TAX"));

        // then
        Company persisted =(Company) repository.findById(100L).orElseThrow();
        assertThat(persisted.getName()).isEqualTo("New Company");
        assertThat(persisted.getTaxId()).isEqualTo("NEW-TAX");
    }

    @Test
    void shouldReturn404WhenUpdatingMissingContact() throws Exception {
        String requestBody = updateCompanyJson();

        mockMvc.perform(put("/api/addressbook/contacts/{id}", 99999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteContact() throws Exception {
        // given
        Company company = new Company()
                .setId(200L)
                .setName("Company");
        repository.save(company);

        // sanity check
        assertThat(repository.existsById(200L)).isTrue();

        // when
        mockMvc.perform(delete("/api/addressbook/contacts/{id}", 200L))
                .andExpect(status().isNoContent());

        // then
        assertThat(repository.existsById(200L)).isFalse();
    }

    @Test
    void shouldReturn404WhenDeletingMissingContact() throws Exception {
        mockMvc.perform(delete("/api/addressbook/contacts/{id}", 99999L))
                .andExpect(status().isNotFound());
    }
    private String personJson() {
        return """
                {
                  "contactType": "person",
                  "firstName": "John",
                  "lastName": "Smith",
                  "displayName": "John Smith",
                  "bankAccountNumber": "123456",
                  "tags": ["friend", "vip"],
                  "address": {
                    "city": "Wroclaw",
                    "postCode": "50-001",
                    "street": "Market Square",
                    "country": "Poland"
                  }
                }
                """;
    }

    private String companyJson() {
        return """
                {
                  "contactType": "company",
                  "companyName": "Veeteq",
                  "taxId": "PL123456789",
                  "displayName": "Veeteq Sp. z o.o.",
                  "bankAccountNumber": "987654",
                  "tags": ["supplier", "partner"],
                  "address": {
                    "city": "Wroclaw",
                    "postCode": "50-001",
                    "street": "Grabiszynska",
                    "country": "Poland"
                  }
                }
                """;
    }

    private String updateCompanyJson() {
        return """
                {
                  "contactType": "company",
                  "companyName": "New Company",
                  "taxId": "NEW-TAX",
                  "address": {
                    "city": "Wroclaw",
                    "postCode": "50-001",
                    "street": "Grabiszynska",
                    "country": "Poland"
                  }
                }
                """;
    }
}