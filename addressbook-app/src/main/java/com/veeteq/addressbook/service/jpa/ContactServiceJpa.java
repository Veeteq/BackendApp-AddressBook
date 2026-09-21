package com.veeteq.addressbook.service.jpa;

import com.veeteq.addressbook.mapper.ContactMapper;
import com.veeteq.addressbook.model.Company;
import com.veeteq.addressbook.model.Contact;
import com.veeteq.addressbook.model.Person;
import com.veeteq.addressbook.repository.ContactRepository;
import com.veeteq.addressbook.repository.UtilityRepository;
import com.veeteq.addressbook.rest.dto.*;
import com.veeteq.addressbook.service.ContactService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ContactServiceJpa implements ContactService {

    private final ContactRepository contactRepository;
    private final ContactMapper contactMapper;
    private final UtilityRepository utilityRepository;

    public ContactServiceJpa(ContactRepository contactRepository, ContactMapper contactMapper, UtilityRepository utilityRepository) {
        this.contactRepository = contactRepository;
        this.contactMapper = contactMapper;
        this.utilityRepository = utilityRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ContactsResponseDto getContacts(Pageable pageable) {
        var result = contactRepository.findAll(pageable);
        var response = contactMapper.toDto(result);
        return response;
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
        var entity = contactMapper.toEntity(dto);
        var id = utilityRepository.nextId();
        entity.setId(id);
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
        var saved = contactRepository.save(updated);
        return contactMapper.toDto(saved);
    }

    @Override
    public void deleteContactById(Long id) {
        if (!contactRepository.existsById(id)) throw new IllegalArgumentException("Contact %d not found".formatted(id));
        contactRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContactDto> searchContacts(String searchText) {
        if (!StringUtils.hasText(searchText)) return List.of();
        var result = contactRepository.findByNameContainingIgnoreCase(searchText);
        var response = result.stream()
                .map(contactMapper::toDto)
                .collect(Collectors.toList());
        return response;
    }

    private void validateType(Contact contact, ContactRequestDto dto) {
        if (!Objects.equals(contact.getVersion(), dto.getVersion())) throw new ConcurrentModificationException("Contact has been already modified");

        if (contact instanceof Person person && dto instanceof PersonRequestDto personDto) return;
        if (contact instanceof Company company && dto instanceof CompanyRequestDto companyDto) return;
        throw new IllegalArgumentException("Changing contact type is not supported");
    }

}
