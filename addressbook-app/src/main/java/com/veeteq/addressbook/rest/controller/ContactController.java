package com.veeteq.addressbook.rest.controller;

import com.veeteq.addressbook.mapper.ContactMapper;
import com.veeteq.addressbook.rest.api.ContactsApi;
import com.veeteq.addressbook.rest.dto.ContactDto;
import com.veeteq.addressbook.rest.dto.ContactRequestDto;
import com.veeteq.addressbook.rest.dto.ContactsResponseDto;
import com.veeteq.addressbook.service.ContactService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/addressbook")
public class ContactController implements ContactsApi {
    private final static Logger LOGGER = LoggerFactory.getLogger(ContactController.class.getSimpleName());

    private final ContactService contactService;
    private final ContactMapper contactMapper;

    public ContactController(ContactService contactService, ContactMapper contactMapper) {
        this.contactService = contactService;
        this.contactMapper = contactMapper;
    }

    @Override
    public ResponseEntity<ContactDto> createContact(@RequestBody ContactRequestDto dto) {
        var response = contactService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<ContactDto> getContactById(@PathVariable(name = "id", required = true) Long id) {

        var response = contactService.getContactById(id);
        return ResponseEntity.ok().body(response);
    }

    @Override
    public ResponseEntity<ContactsResponseDto> getContacts(Integer pageNumber, Integer pageSize, String orderBy, String orderDirection) {
        LOGGER.info("Request received to list all contacts");

        var direction = Sort.Direction.fromString(orderDirection);
        var sort = Sort.by(direction, orderBy);
        var pageRequest = PageRequest.of(pageNumber, pageSize, sort);
        var response = contactService.getContacts(pageRequest);
        return ResponseEntity.ok()
                .body(response);
    }

    @Override
    public ResponseEntity<List<ContactDto>> searchContacts(String searchText) {
        return null;
    }

    @Override
    public ResponseEntity<ContactDto> updateContact(@PathVariable(name = "id", required = true) Long id, @RequestBody ContactRequestDto dto) {

        try {
            var response = contactService.updateContactById(id, dto);
            return ResponseEntity.ok().body(response);
        } catch (RuntimeException exc) {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<Void> deleteContact(@PathVariable(name = "id", required = true) Long id) {

        try {
            contactService.deleteContactById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException exc) {
            return ResponseEntity.notFound().build();
        }

    }
}
