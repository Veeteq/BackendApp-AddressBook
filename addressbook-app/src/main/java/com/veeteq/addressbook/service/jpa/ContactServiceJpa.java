package com.veeteq.addressbook.service.jpa;

import com.veeteq.addressbook.mapper.ContactMapper;
import com.veeteq.addressbook.model.Address;
import com.veeteq.addressbook.model.Company;
import com.veeteq.addressbook.model.Contact;
import com.veeteq.addressbook.model.Person;
import com.veeteq.addressbook.repository.ContactRepository;
import com.veeteq.addressbook.rest.dto.*;
import com.veeteq.addressbook.service.ContactService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ContactServiceJpa implements ContactService {

    private final ContactRepository contactRepository;
    private final ContactMapper contactMapper;

    public ContactServiceJpa(ContactRepository contactRepository, ContactMapper contactMapper) {
        this.contactRepository = contactRepository;
        this.contactMapper = contactMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContactDto> getContacts() {
        return contactRepository.findAll()
                .stream()
                .map(contactMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ContactDto getContactById(Long id) {
        return contactRepository.findById(id)
                .map(contactMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Unable to find contact with id: " + id));
    }

    @Override
    @Transactional
    public ContactDto create(ContactRequestDto dto) {

        Contact<?> entity = contactMapper.toEntity(dto);
        var saved = contactRepository.save(entity);
        return contactMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ContactDto updateContactById(Long id, ContactRequestDto dto) {
        var contact = contactRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Contact %d not found".formatted(id)));
        validateType(contact, dto);

        var updated = contactMapper.updateEntity(contact, dto);
        var saved = contactRepository.save(contact);
        return contactMapper.toDto(saved);
    }

    @Override
    public void deleteContactById(Long id) {
        if (!contactRepository.existsById(id)) throw new IllegalArgumentException("Contact %d not found".formatted(id));
        contactRepository.deleteById(id);
    }

    private void validateType(Contact contact, ContactRequestDto dto) {
        if (contact instanceof Person<?> person && dto instanceof PersonRequestDto personDto) return;
        if (contact instanceof Company company && dto instanceof CompanyRequestDto companyDto) return;
        throw new IllegalArgumentException("Changing contact type is not supported");
    }
}
