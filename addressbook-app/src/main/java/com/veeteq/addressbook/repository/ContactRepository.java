package com.veeteq.addressbook.repository;

import com.veeteq.addressbook.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Long> {

    @Query(value = """
            SELECT c
              FROM Contact c
             WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))
                OR LOWER(c.displayName) LIKE LOWER(CONCAT('%', :name, '%'))
                OR LOWER(CONCAT(c.firstName, ' ', c.lastName)) LIKE LOWER(CONCAT('%', :name, '%'))""")
    List<Contact> findByNameContainingIgnoreCase(@Param("name") String name);

}