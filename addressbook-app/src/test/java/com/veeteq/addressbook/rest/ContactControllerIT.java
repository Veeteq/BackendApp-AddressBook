package com.veeteq.addressbook.rest;

import com.veeteq.addressbook.model.Address;
import com.veeteq.addressbook.model.Company;
import com.veeteq.addressbook.model.Contact;
import com.veeteq.addressbook.model.Person;
import com.veeteq.addressbook.repository.ContactRepository;
import com.veeteq.addressbook.repository.UtilityRepository;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ContactControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ContactRepository repository;

    @Autowired
    private UtilityRepository utilityRepository;

    @Test
    void shouldCreatePerson() throws Exception {
        var payload = personJson();
        mockMvc.perform(post("/api/addressbook/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.contactType").value("PERSON"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.version").value("0"))
                .andExpect(jsonPath("$.tags.length()").value(2));

        List<Contact> contacts = repository.findAll();

        assertThat(contacts).hasSize(4);
        assertThat(contacts.getFirst()).isInstanceOf(Person.class);

        Person person = (Person) contacts.getFirst();

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
                .andExpect(jsonPath("$.contactType").value("COMPANY"))
                .andExpect(jsonPath("$.companyName").value("ACME Ltd."))
                .andExpect(jsonPath("$.version").value("0"))
                .andExpect(jsonPath("$.taxId").value("US123456789"));

        List<Contact> contacts = repository.findAll();

        assertThat(contacts).hasSize(4);
        assertThat(contacts.getFirst()).isInstanceOf(Company.class);

        Company company = (Company) contacts.getFirst();

        assertThat(company.getName()).isEqualTo("ACME Ltd.");
        assertThat(company.getTaxId()).isEqualTo("US123456789");
    }

    @Test
    void shouldReturnMixedContactTypes() throws Exception {
        var address = new Address().setCity("Philadelphia")
                .setCountry("United States")
                .setStreet("105 Main Street")
                .setPostcode("19092");

        var person = new Person();
        person.setId(1L);
        person.setFirstName("John");
        person.setLastName("Smith");
        person.setAddress(address);
        repository.save(person);

        var company = new Company();
        company.setId(2L);
        company.setName("Plava Laguna Inc");
        company.setAddress(address);
        repository.save(company);

        mockMvc.perform(get("/api/addressbook/contacts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].contactType").exists())
                .andExpect(jsonPath("$.data[1].contactType").exists());
    }

    @Test
    void shouldUpdateCompany() throws Exception {
        // given
        var company = new Company();
        company.setId(100L);
        company.setName("Old Company");
        company.setTaxId("OLD-TAX");
        repository.save(company);

        String requestBody = updateCompanyJson();

        // when
        mockMvc.perform(put("/api/addressbook/contacts/{id}", 100L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.companyName").value("New Company Name"))
                .andExpect(jsonPath("$.taxId").value("NEW-TAX"));

        // then
        Company persisted =(Company) repository.findById(100L).orElseThrow();
        assertThat(persisted.getName()).isEqualTo("New Company Name");
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
        var company = new Company();
        company.setId(200L);
        company.setName("Company");
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

    @Test
    void shouldRejectUpdateWhenVersionIsStale() throws Exception {

        // given
        var company = new Company();
        company.setId(utilityRepository.nextId());
        company.setName("ACME");
        company.setTaxId("123456");
        repository.saveAndFlush(company);
        var id = company.getId();
        var staleVersion = company.getVersion();

        // simulate concurrent update
        company.setName("Updated by another user");
        repository.saveAndFlush(company);

        // stale DTO still contains old version
        String requestBody = updateCompany_WithVersion().formatted(staleVersion);

        // when + then
        mockMvc.perform(put("/api/addressbook/contacts/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type").value("about:blank"))
                .andExpect(jsonPath("$.title").value("Concurrent modification"))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.detail").exists())
                .andExpect(jsonPath("$.instance").exists());
    }

    @Test
    void shouldUpdateContactWhenVersionMatches() throws Exception {

        // given
        var company = new Company();
        company.setId(utilityRepository.nextId());
        company.setName("ACME");
        company.setTaxId("123456");
        company = repository.saveAndFlush(company);

        String requestBody = updateCompany_WithVersion().formatted(company.getVersion()); //My update for ACME

        mockMvc.perform(put("/api/addressbook/contacts/{id}", company.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyName").value("My update for ACME"));

        var persisted = (Company) repository.findById(company.getId()).orElseThrow();
        assertThat(persisted.getName()).isEqualTo("My update for ACME");
    }

    @Test
    void shouldReturnProblemDetailWhenContactDoesNotExist() throws Exception {
        var id = 999999L;

        mockMvc.perform(get("/api/addressbook/contacts/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type").value("about:blank"))
                .andExpect(jsonPath("$.title").value("Contact not found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("Contact not found: " + id))
                .andExpect(jsonPath("$.instance").value("/api/addressbook/contacts/" + id))
                .andExpect(jsonPath("$.code").value("CONTACT_NOT_FOUND"));;
    }

    @Test
    void shouldReturnProblemDetailWhenChangingContactType() throws Exception {
        var contact = repository.findAll().stream()
                .filter(c -> c.getContactType().equalsIgnoreCase("Person"))
                .findFirst()
                .orElseThrow();
        var request = updateCompany_WithVersion();

        mockMvc.perform(put("/api/addressbook/contacts/{id}", contact.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type").value("about:blank"))
                .andExpect(jsonPath("$.title").value("Contact type change not allowed"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("Changing contact type is not supported"))
                .andExpect(jsonPath("$.instance").exists())
                .andExpect(jsonPath("$.code").value("CONTACT_TYPE_CHANGE_NOT_ALLOWED"));;
    }

    @Test
    void shouldReturnBadRequestProblemDetailWhenContactValidationFails() throws Exception {
        var request = """
            {
              "contactType": "PERSON",
              "firstName": "A",
              "lastName": "Smith"
            }
            """;

        mockMvc.perform(post("/api/addressbook/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.detail").value("Request validation failed"))
                .andExpect(jsonPath("$.errors.firstName").exists())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"));;
    }

    @Test
    void shouldReturnBadRequestWhenContactTypeIsInvalid() throws Exception {
        var request = """
            {
              "contactType": "abc"
            }
            """;

        mockMvc.perform(post("/api/addressbook/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Invalid request body"))
                .andExpect(jsonPath("$.detail").value("Request body could not be read"))
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST_BODY"));;
    }

    private String personJson() {
        return """
                {
                  "contactType": "PERSON",
                  "firstName": "John",
                  "lastName": "Smith",
                  "displayName": "John Smith",
                  "bankAccountNumber": "123456",
                  "tags": ["friend", "vip"],
                  "address": {
                    "city": "Wroclaw",
                    "postcode": "50-001",
                    "street": "Market Square",
                    "country": "Poland"
                  }
                }
                """;
    }

    private String companyJson() {
        return """
                {
                  "contactType": "COMPANY",
                  "companyName": "ACME Ltd.",
                  "taxId": "US123456789",
                  "displayName": "ACME Limited",
                  "bankAccountNumber": "987654",
                  "tags": ["supplier", "partner"],
                  "address": {
                    "city": "Kansas City",
                    "postcode": "50001",
                    "street": "1001 Sun Valley",
                    "country": "United States"
                  }
                }
                """;
    }

    private String updateCompanyJson() {
        return """
                {
                  "contactType": "COMPANY",
                  "companyName": "New Company Name",
                  "taxId": "NEW-TAX",
                  "address": {
                    "city": "Wroclaw",
                    "postcode": "50-001",
                    "street": "Grabiszynska",
                    "country": "Poland"
                  },
                  "version": 0
                }
                """;
    }

    private String updateCompany_WithVersion() {
        return """
        {
          "contactType": "COMPANY",
          "version": 0,
          "companyName": "My update for ACME",
          "taxId": "999999",
          "displayName": "ACME",
          "bankAccountNumber": "PL123",
          "address": {
            "country": "PL",
            "postcode": "50-001",
            "city": "Wroclaw",
            "street": "Market Square"
          },
          "tags": ["A","B"]
        }
        """;
    }
}