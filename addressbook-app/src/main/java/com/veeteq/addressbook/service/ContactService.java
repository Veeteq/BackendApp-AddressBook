package com.veeteq.addressbook.service;

import com.veeteq.addressbook.rest.dto.ContactDto;
import com.veeteq.addressbook.rest.dto.ContactRequestDto;

import java.util.List;

public interface ContactService {

    List<ContactDto> getContacts();
    ContactDto create(ContactRequestDto dto);
    ContactDto getContactById(Long id);
    ContactDto updateContactById(Long id, ContactRequestDto dto);
    void deleteContactById(Long id);
}
