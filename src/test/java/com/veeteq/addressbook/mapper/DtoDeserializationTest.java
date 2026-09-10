package com.veeteq.addressbook.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veeteq.addressbook.rest.dto.CompanyRequestDto;
import com.veeteq.addressbook.rest.dto.ContactRequestDto;
import com.veeteq.addressbook.rest.dto.PersonRequestDto;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DtoDeserializationTest {

    @Test
    void shouldDeserializePersonSubtype() throws Exception {
        var mapper = new ObjectMapper();

        String json = """
    {
      "contactType":"person",
      "firstName":"John",
      "lastName":"Smith"
    }
    """;

        var dto = mapper.readValue(json, ContactRequestDto.class);
        assertThat(dto).isInstanceOf(PersonRequestDto.class);
    }

    @Test
    void shouldDeserializeCompanySubtype() throws Exception {
        var mapper = new ObjectMapper();

        String json = """
    {
      "contactType":"company",
      "companyName":"Veeteq"
    }
    """;

        var dto = mapper.readValue(json, ContactRequestDto.class);
        assertThat(dto).isInstanceOf(CompanyRequestDto.class);
    }
}
