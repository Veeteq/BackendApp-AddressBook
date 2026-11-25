package com.veeteq.addressbook.exception;

public class ContactTypeChangeException extends RuntimeException {

    public ContactTypeChangeException() {
        super("Changing contact type is not supported");
    }
}
