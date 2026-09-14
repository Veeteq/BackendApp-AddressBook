package com.veeteq.addressbook.service;

import com.veeteq.addressbook.model.Company;
import com.veeteq.addressbook.repository.ContactRepository;
import com.veeteq.addressbook.repository.UtilityRepository;
import com.veeteq.addressbook.rest.dto.CompanyRequestDto;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ConcurrentModificationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class ContactServiceIT {

    @Autowired
    private ContactService service;

    @Autowired
    private ContactRepository repository;

    @Autowired
    private UtilityRepository utilityRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PlatformTransactionManager txManager;

    @Test
    @Transactional
    void shouldCreateCompany() {

        var dto = new CompanyRequestDto()
                .companyName("ACME")
                .taxId("123456");

        var result = service.create(dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
    }

    @Test
    @Transactional
    void shouldUpdateCompany() {

        var company = new Company();
        company.setId(utilityRepository.nextId());
        company.setName("ACME");
        company.setTaxId("123");
        company = repository.saveAndFlush(company);

        var dto = new CompanyRequestDto()
                .companyName("UPDATED")
                .taxId("999")
                .version(company.getVersion());

        var result = service.updateContactById(company.getId(), dto);
        assertThat(result).isNotNull();

        var updated = (Company) repository.findById(company.getId()) .orElseThrow();
        assertThat(updated.getName()).isEqualTo("UPDATED");
        assertThat(updated.getTaxId()).isEqualTo("999");
    }

    @Test
    @Transactional
    void shouldDeleteCompany() {

        var company = new Company();
        company.setId(utilityRepository.nextId());
        company.setName("ACME");
        company.setTaxId("123");
        company = repository.saveAndFlush(company);

        service.deleteContactById(company.getId());
        assertThat(repository.findById(company.getId())).isEmpty();
    }

    @Test
    void shouldThrowSpringOptimisticLockException() {
        // given
        var company = new Company();
        company.setId(utilityRepository.nextId());
        company.setName("ACME");
        company.setTaxId("123");
        company = repository.saveAndFlush(company);

        Long id = company.getId();
        entityManager.clear();

        // simulate client A
        TransactionTemplate tx1 = new TransactionTemplate(txManager);
        Company first = tx1.execute(status -> (Company) repository.findById(id).orElseThrow());
        assertThat(first.getVersion()).isEqualTo(0);

        // simulate client B
        TransactionTemplate tx2 = new TransactionTemplate(txManager);
        Company second = tx2.execute(status -> (Company) repository.findById(id).orElseThrow());
        assertThat(second.getVersion()).isEqualTo(0);

        // client A updates
        tx1.executeWithoutResult(status -> {
            first.setName("UPDATED BY A");
            repository.saveAndFlush(first);
        });

        // client B updates
        assertThatThrownBy(() ->
                tx2.executeWithoutResult(status -> {
                    second.setName("UPDATED BY B");
                    repository.saveAndFlush(second);
                }))
                .isInstanceOf(ObjectOptimisticLockingFailureException.class);
    }

    @Test
    void shouldRejectStaleVersion() {
        // given
        var contact = new Company();
        contact.setId(utilityRepository.nextId());
        contact.setName("ACME");
        contact.setTaxId("123");
        var saved = repository.saveAndFlush(contact);

        var staleVersion = saved.getVersion();

        contact.setName("Updated");
        repository.saveAndFlush(contact);

        var dto = new CompanyRequestDto();
        dto.setVersion(staleVersion);
        dto.setCompanyName("Another Update");

        assertThatThrownBy(() -> service.updateContactById(saved.getId(), dto)).isInstanceOf(ConcurrentModificationException.class);
    }

    @Test
    void shouldRejectStaleClientVersion() {

        // given
        var company = new Company();
        company.setId(utilityRepository.nextId());
        company.setName("ACME");
        company.setTaxId("123456");

        var savedCompany = repository.saveAndFlush(company);

        Company firstView = (Company) repository.findById(savedCompany.getId()).orElseThrow();
        Company secondView = (Company) repository.findById(savedCompany.getId()).orElseThrow();

        // first user saves
        firstView.setName("User A");
        firstView = repository.saveAndFlush(firstView);

        // second user still holds stale version
        var dto = new CompanyRequestDto();
        dto.setVersion(secondView.getVersion());
        dto.setCompanyName("User B");

        assertThatThrownBy(() -> service.updateContactById(savedCompany.getId(), dto)).isInstanceOf(ConcurrentModificationException.class);
    }
}
