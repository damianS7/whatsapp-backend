package com.damian.whatsapp.contact;

import com.damian.whatsapp.common.exception.Exceptions;
import com.damian.whatsapp.common.utils.AuthHelper;
import com.damian.whatsapp.contact.exception.ContactAuthorizationException;
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

    // add a new friend for the logged customer
    public Contact addContact(Long friendCustomerId) {
        Customer loggedCustomer = AuthHelper.getLoggedCustomer();

        // check friendlist size limit
        if (this.getContacts().size() >= MAX_CONTACTS) {
            throw new MaxContactsLimitReachedException(Exceptions.CONTACT_LIST.MAX_CONTACTS);
        }

        // check if the customer we are trying to add as a friend exists
        Customer friend = customerRepository.findById(friendCustomerId).orElseThrow(
                () -> new CustomerNotFoundException(Exceptions.CUSTOMER.NOT_FOUND)
        );

        return contactRepository.save(
                new Contact(loggedCustomer, friend)
        );
    }

    // delete a friend from the friendlist of the logged customer
    public void deleteContact(Long id) {
        Customer loggedCustomer = AuthHelper.getLoggedCustomer();

        // check if the friendship exists
        Contact contact = contactRepository.findById(id).orElseThrow(
                () -> new CustomerNotFoundException(Exceptions.CUSTOMER.NOT_FOUND)
        );

        // check if the logged customer is the owner of the friendship
        if (!loggedCustomer.getId().equals(contact.getCustomer().getId())) {
            throw new ContactAuthorizationException(Exceptions.CONTACT_LIST.ACCESS_FORBIDDEN);
        }

        contactRepository.deleteById(id);
    }
}
