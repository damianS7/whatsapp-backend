package com.damian.whatsapp.contact;

import com.damian.whatsapp.common.exception.Exceptions;
import com.damian.whatsapp.common.utils.AuthHelper;
import com.damian.whatsapp.contact.exception.ContactAlreadyExistException;
import com.damian.whatsapp.contact.exception.ContactAuthorizationException;
import com.damian.whatsapp.contact.exception.ContactNotFoundException;
import com.damian.whatsapp.contact.exception.MaxContactsLimitReachedException;
import com.damian.whatsapp.customer.Customer;
import com.damian.whatsapp.customer.CustomerRepository;
import com.damian.whatsapp.customer.exception.CustomerNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ContactService {
    private final short MAX_CONTACTS = 10;
    private final ContactRepository contactRepository;
    private final CustomerRepository customerRepository;

    public ContactService(
            ContactRepository contactRepository,
            CustomerRepository customerRepository
    ) {
        this.contactRepository = contactRepository;
        this.customerRepository = customerRepository;
    }

    // get all the friends for the logged customer
    public Set<Contact> getContacts() {
        Customer loggedCustomer = AuthHelper.getLoggedCustomer();
        return contactRepository.findAllByCustomerId(loggedCustomer.getId());
    }

    // add a new contact for the logged customer
    public Contact addContact(Long customerId) {
        Customer loggedCustomer = AuthHelper.getLoggedCustomer();

        // check contact list size limit
        if (this.getContacts().size() >= MAX_CONTACTS) {
            throw new MaxContactsLimitReachedException(Exceptions.CONTACT_LIST.MAX_CONTACTS);
        }

        // check if the customer we want to add as a contact exists.
        Customer contactCustomer = customerRepository.findById(customerId).orElseThrow(
                () -> new CustomerNotFoundException(Exceptions.CUSTOMER.NOT_FOUND)
        );

        // check if that they are not already contact
        if (contactRepository.contactExists(loggedCustomer.getId(), contactCustomer.getId())) {
            throw new ContactAlreadyExistException(Exceptions.CONTACT_LIST.ALREADY_EXISTS);
        }

        return contactRepository.save(
                new Contact(loggedCustomer, contactCustomer)
        );
    }

    // delete a contact from the contact list of the logged customer.
    public void deleteContact(Long id) {
        Customer loggedCustomer = AuthHelper.getLoggedCustomer();

        // check if the contact exists
        Contact contact = contactRepository.findById(id).orElseThrow(
                () -> new ContactNotFoundException(Exceptions.CONTACT_LIST.NOT_FOUND)
        );

        // check if the logged customer is the owner of the contact.
        if (!loggedCustomer.getId().equals(contact.getCustomer().getId())) {
            throw new ContactAuthorizationException(Exceptions.CONTACT_LIST.ACCESS_FORBIDDEN);
        }

        contactRepository.deleteById(id);
    }
}
