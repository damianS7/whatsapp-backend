package com.damian.whatsapp.modules.contact.service;

import com.damian.whatsapp.modules.contact.ContactRepository;
import com.damian.whatsapp.modules.contact.exception.ContactAlreadyExistException;
import com.damian.whatsapp.modules.contact.exception.ContactNotFoundException;
import com.damian.whatsapp.modules.contact.exception.MaxContactsLimitReachedException;
import com.damian.whatsapp.modules.user.user.exception.UserNotFoundException;
import com.damian.whatsapp.modules.user.user.repository.UserRepository;
import com.damian.whatsapp.shared.domain.Contact;
import com.damian.whatsapp.shared.domain.User;
import com.damian.whatsapp.shared.exception.Exceptions;
import com.damian.whatsapp.shared.util.AuthHelper;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ContactService {
    private final short MAX_CONTACTS = 10;
    private final ContactRepository contactRepository;
    private final UserRepository userRepository;

    public ContactService(
            ContactRepository contactRepository,
            UserRepository userRepository
    ) {
        this.contactRepository = contactRepository;
        this.userRepository = userRepository;
    }

    // get all the friends for the logged user
    public Set<Contact> getContacts() {
        User loggedUser = AuthHelper.getLoggedUser();
        return contactRepository.findAllByUserId(loggedUser.getId());
    }

    // add a new contact for the logged user
    public Contact addContact(Long userId) {
        User loggedUser = AuthHelper.getLoggedUser();

        // check contact list size limit
        if (this.getContacts().size() >= MAX_CONTACTS) {
            throw new MaxContactsLimitReachedException(Exceptions.CONTACT_LIST.MAX_CONTACTS, loggedUser.getId(), null);
        }

        // check if the user we want to add as a contact exists.
        User contactUser = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(Exceptions.USER.NOT_FOUND, userId)
        );

        // check if that they are not already contact
        if (contactRepository.contactExists(loggedUser.getId(), contactUser.getId())) {
            throw new ContactAlreadyExistException(
                    Exceptions.CONTACT_LIST.ALREADY_EXISTS,
                    loggedUser.getId(),
                    contactUser.getId()
            );
        }

        return contactRepository.save(
                new Contact(loggedUser, contactUser)
        );
    }

    // delete a contact from the contact list of the logged user.
    public void deleteContact(Long contactUserId) {
        User loggedUser = AuthHelper.getLoggedUser();

        // check if the contact exists
        Contact contact = contactRepository
                .findByUser_IdAndContact_Id(loggedUser.getId(), contactUserId)
                .orElseThrow(
                        () -> new ContactNotFoundException(
                                Exceptions.CONTACT_LIST.NOT_FOUND,
                                loggedUser.getId(),
                                contactUserId
                        )
                );

        contactRepository.deleteById(contact.getId());
    }
}
