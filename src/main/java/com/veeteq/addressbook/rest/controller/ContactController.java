package com.veeteq.addressbook.rest.controller;

import com.veeteq.addressbook.mapper.ContactMapper;
import com.veeteq.addressbook.rest.dto.ContactDto;
import com.veeteq.addressbook.rest.dto.ContactRequestDto;
import com.veeteq.addressbook.service.ContactService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/addressbook")
public class ContactController {

    private final ContactService contactService;
    private final ContactMapper contactMapper;

    public ContactController(ContactService contactService, ContactMapper contactMapper) {
        this.contactService = contactService;
        this.contactMapper = contactMapper;
    }

    @PostMapping("/contacts")
    public ResponseEntity<ContactDto> create(@RequestBody ContactRequestDto dto) {
        var response = contactService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(path = "/contacts")
    public ResponseEntity<List<ContactDto>> getContacts() {

        var response = contactService.getContacts();
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/contacts/{id}")
    public ResponseEntity<ContactDto> getContactById(@PathVariable(name = "id", required = true) Long id) {

        var response = contactService.getContactById(id);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping(path = "/contacts/{id}")
    public ResponseEntity<ContactDto> updateContactById(@PathVariable(name = "id", required = true) Long id, @RequestBody ContactRequestDto dto) {

        try {
            var response = contactService.updateContactById(id, dto);
            return ResponseEntity.ok().body(response);
        } catch (RuntimeException exc) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(path = "/contacts/{id}")
    public ResponseEntity<Void> deleteContactById(@PathVariable(name = "id", required = true) Long id) {

        try {
            contactService.deleteContactById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException exc) {
            return ResponseEntity.notFound().build();
        }

    }
}
