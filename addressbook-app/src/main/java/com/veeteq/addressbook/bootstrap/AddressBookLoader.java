package com.veeteq.addressbook.bootstrap;

import com.veeteq.addressbook.model.*;
import com.veeteq.addressbook.repository.ContactRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

@Component
@Profile(value = "default")
public class AddressBookLoader implements ApplicationRunner {

    private final ContactRepository contactRepository;

    public AddressBookLoader(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var contactPerson = loadPerson();
        System.out.println("Person loaded with id: " + contactPerson.getId());

        var contactCompany = loadCompany();
        System.out.println("Company loaded with id: " + contactCompany.getId());

        var contactEmployee = loadEmployee();
        System.out.println("Employee loaded with id: " + contactEmployee.getId());
    }

    private Contact loadPerson() {
        var person = new Person();
        person.setId(getRandomLong());
        person.setFirstName(getRandomString());
        person.setLastName(getRandomString());
        person.setDisplayName(getRandomString());
        person.setBankAccountNumber(getRandomString());
        person.setAddress(new Address()
                        .setCity(getRandomString())
                        .setPostcode(getRandomString().substring(0, 10))
                        .setStreet(getRandomString())
                        .setCountry(getRandomString()));
        person.setTags(Set.of("tag1", "tag2"));

        var saved = contactRepository.save(person);
        return saved;
    }

    private Company loadCompany() {
        var company = new Company();
        company.setId(getRandomLong());
        company.setName(getRandomString());
        company.setDisplayName(getRandomString());
        company.setBankAccountNumber(getRandomString());
        company.setTaxId(getRandomString());
        company.setAddress(new Address()
                        .setCity(getRandomString())
                        .setPostcode(getRandomString().substring(0, 10))
                        .setStreet(getRandomString())
                        .setCountry(getRandomString()));
        company.setTags(Set.of("tag3", "tag4"));
        var saved = contactRepository.save(company);
        return saved;
    }

    private Employee loadEmployee() {
        var employee = new Employee();
        employee.setId(getRandomLong());
        employee.setFirstName(getRandomString());
        employee.setLastName(getRandomString());
        employee.setBankAccountNumber(getRandomString());
        employee.setJob(getRandomString());
        employee.setSalary(getRandomDouble());
        employee.setAddress(new Address()
                        .setCity(getRandomString())
                        .setPostcode(getRandomString().substring(0, 10))
                        .setStreet(getRandomString())
                        .setCountry(getRandomString()));
        employee.setTags(Set.of("tag2", "tag4"));
        var saved = contactRepository.save(employee);
        return saved;
    }

    private static String getRandomString() {
        return UUID.randomUUID().toString();
    }

    private static Long getRandomLong() {
        return new Random().nextLong(0,Short.MAX_VALUE);
    }

    private static BigDecimal getRandomDouble() {
        var val = new Random().nextDouble(0, Short.MAX_VALUE);
        return BigDecimal.valueOf(val);
    }

}
