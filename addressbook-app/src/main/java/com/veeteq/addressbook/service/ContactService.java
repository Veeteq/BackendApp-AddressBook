package com.veeteq.addressbook.service;

import com.veeteq.addressbook.rest.dto.ContactDto;
import com.veeteq.addressbook.rest.dto.ContactRequestDto;
import com.veeteq.addressbook.rest.dto.ContactsResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ContactService {

    ContactsResponseDto getContacts(Pageable pageable);
    ContactDto create(ContactRequestDto dto);
    ContactDto getContactById(Long id);
    ContactDto updateContactById(Long id, ContactRequestDto dto);
    void deleteContactById(Long id);
    List<ContactDto> searchContacts(String searchText);
}
