package com.damian.whatsapp.modules.contact;

import com.damian.whatsapp.modules.contact.exception.ContactAlreadyExistException;
import com.damian.whatsapp.modules.contact.exception.ContactAuthorizationException;
import com.damian.whatsapp.modules.contact.exception.ContactNotFoundException;
import com.damian.whatsapp.modules.contact.exception.MaxContactsLimitReachedException;
import com.damian.whatsapp.modules.contact.service.ContactService;
import com.damian.whatsapp.modules.user.user.exception.UserNotFoundException;
import com.damian.whatsapp.modules.user.user.repository.UserRepository;
import com.damian.whatsapp.shared.AbstractServiceTest;
import com.damian.whatsapp.shared.domain.Contact;
import com.damian.whatsapp.shared.domain.User;
import com.damian.whatsapp.shared.exception.Exceptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ContactServiceTest extends AbstractServiceTest {

    @Mock
    private UserRepository customerRepository;

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private ContactService contactService;

    @Test
    @DisplayName("Should get all friends")
    void shouldGetAllFriends() {
        // given
        User loggedCustomer = new User(
                1L, "customer@test.com",
                passwordEncoder.encode("password")
        );
        setUpContext(loggedCustomer);

        User contact1 = new User(
                2L, "customer1@test.com", passwordEncoder.encode("password")
        );

        User contact2 = new User(
                3L, "customer2@test.com", passwordEncoder.encode("password")
        );

        Set<Contact> contactList = Set.of(
                new Contact(loggedCustomer, contact1),
                new Contact(loggedCustomer, contact2)
        );

        // when
        when(contactRepository.findAllByUserId(loggedCustomer.getId()))
                .thenReturn(contactList);
        Set<Contact> result = contactService.getContacts();

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(contactRepository, times(1)).findAllByUserId(loggedCustomer.getId());
    }

    @Test
    @DisplayName("Should add a contact")
    void shouldAddContact() {
        // given
        User loggedCustomer = new User(
                1L,
                "customer@test.com",
                passwordEncoder.encode("password")
        );
        setUpContext(loggedCustomer);

        User contactCustomer = new User(
                2L, "customer1@test.com", passwordEncoder.encode("password")
        );

        Contact givenContact = new Contact(loggedCustomer, contactCustomer);

        // when
        when(customerRepository.findById(contactCustomer.getId())).thenReturn(Optional.of(contactCustomer));
        when(contactRepository.save(any(Contact.class)))
                .thenReturn(givenContact);

        Contact result = contactService.addContact(contactCustomer.getId());

        // then
        assertNotNull(result);
        verify(contactRepository, times(1)).save(any(Contact.class));
    }

    @Test
    @DisplayName("Should not add a contact when limit reached")
    void shouldNotAddContactWhenLimitReached() {
        // given
        User loggedCustomer = new User(
                1L, "customer@test.com",
                passwordEncoder.encode("password")
        );

        setUpContext(loggedCustomer);
        short MAX_CONTACTS = 3;

        Field field = null;
        try {
            field = ContactService.class.getDeclaredField("MAX_CONTACTS");
            field.setAccessible(true);
            MAX_CONTACTS = (short) field.get(contactService); // null porque es static
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }


        Set<Contact> contactList = new HashSet<>();
        for (int i = 0; i <= MAX_CONTACTS; i++) {
            contactList.add(new Contact());
        }

        // when
        when(contactRepository.findAllByUserId(loggedCustomer.getId())).thenReturn(contactList);
        MaxContactsLimitReachedException exception = assertThrows(
                MaxContactsLimitReachedException.class,
                () -> contactService.addContact(0L)
        );

        // then
        assertEquals(Exceptions.CONTACT_LIST.MAX_CONTACTS, exception.getMessage());
    }

    @Test
    @DisplayName("Should not add a contact when already exists")
    void shouldNotAddContactWhenAlreadyExists() {
        // given
        User loggedCustomer = new User(
                1L,
                "customer@test.com",
                passwordEncoder.encode("password")
        );
        setUpContext(loggedCustomer);

        User contact1 = new User(
                2L, "customer1@test.com", passwordEncoder.encode("password")
        );

        // when
        when(customerRepository.findById(contact1.getId())).thenReturn(Optional.of(contact1));
        when(contactRepository.contactExists(loggedCustomer.getId(), contact1.getId())).thenReturn(true);
        ContactAlreadyExistException exception = assertThrows(
                ContactAlreadyExistException.class,
                () -> contactService.addContact(contact1.getId())
        );

        // then
        assertEquals(Exceptions.CONTACT_LIST.ALREADY_EXISTS, exception.getMessage());
    }

    @Test
    @DisplayName("Should not add a contact when customer not found")
    void shouldNotAddContactWhenCustomerNotFound() {
        // given
        User loggedCustomer = new User(
                1L,
                "customer@test.com",
                passwordEncoder.encode("password")
        );
        setUpContext(loggedCustomer);

        User contact1 = new User(
                2L, "customer1@test.com", passwordEncoder.encode("password")
        );

        // when
        when(customerRepository.findById(contact1.getId())).thenReturn(Optional.empty());
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> contactService.addContact(contact1.getId())
        );

        // then
        assertEquals(Exceptions.USER.NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("Should delete a contact")
    void shouldDeleteContact() {
        // given
        User loggedCustomer = new User(
                1L, "customer@test.com",
                passwordEncoder.encode("password")
        );
        setUpContext(loggedCustomer);

        User contact1 = new User(
                2L, "customer1@test.com", passwordEncoder.encode("password")
        );

        Contact givenCC = new Contact(loggedCustomer, contact1);
        givenCC.setId(1L);

        // when
        when(contactRepository.findById(givenCC.getId())).thenReturn(Optional.of(givenCC));
        doNothing().when(contactRepository).deleteById(givenCC.getId());

        contactService.deleteContact(givenCC.getId());

        // then
        verify(contactRepository, times(1)).deleteById(givenCC.getId());
    }

    @Test
    @DisplayName("Should not delete a contact when not found")
    void shouldNotDeleteContactWhenNotFound() {
        // given
        User loggedCustomer = new User(1L, "customer@test.com", passwordEncoder.encode("password"));
        setUpContext(loggedCustomer);

        // when
        when(contactRepository.findById(anyLong())).thenReturn(Optional.empty());
        ContactNotFoundException exception = assertThrows(
                ContactNotFoundException.class,
                () -> contactService.deleteContact(0L)
        );

        // then
        assertEquals(Exceptions.CONTACT_LIST.NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("Should not delete a contact when not authorized")
    void shouldNotDeleteContactWhenNotAuthorized() {
        // given
        User loggedCustomer = new User(1L, "customer@test.com", passwordEncoder.encode("password"));
        setUpContext(loggedCustomer);

        Contact givenCC = new Contact(
                new User(5L, "customer1@test.com", passwordEncoder.encode("password")),
                new User(8L, "customer2@test.com", passwordEncoder.encode("password"))
        );
        givenCC.setId(1L);

        // when
        when(contactRepository.findById(givenCC.getId())).thenReturn(Optional.of(givenCC));
        ContactAuthorizationException exception = assertThrows(
                ContactAuthorizationException.class,
                () -> contactService.deleteContact(givenCC.getId())
        );

        // then
        assertEquals(Exceptions.CONTACT_LIST.ACCESS_FORBIDDEN, exception.getMessage());
    }

}
