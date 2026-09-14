package com.veeteq.addressbook.repository;

import com.veeteq.addressbook.model.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContactRepository extends JpaRepository<Contact, Long> {

    @Query(value = """
            SELECT c
              FROM Contact c
             WHERE LOWER(c.name) LIKE %:name%
                OR LOWER(c.displayName) LIKE %:name%
                OR LOWER(CONCAT(c.firstName, c.lastName)) LIKE %:name%""")
    Page<Contact> findByNameContainingIgnoreCase(@Param("name") String name, PageRequest pageRequest);

}