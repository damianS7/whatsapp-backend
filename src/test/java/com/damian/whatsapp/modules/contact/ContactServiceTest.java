package com.damian.whatsapp.modules.contact;

import com.damian.whatsapp.modules.contact.exception.ContactAlreadyExistException;
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

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ContactServiceTest extends AbstractServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private ContactService contactService;

    @Test
    @DisplayName("Should get all friends")
    void shouldGetAllFriends() {
        // given
        User loggedCustomer = new User(
                1L, "user@test.com",
                passwordEncoder.encode("password")
        );
        setUpContext(loggedCustomer);

        User contact1 = new User(
                2L, "user1@test.com", passwordEncoder.encode("password")
        );

        User contact2 = new User(
                3L, "user2@test.com", passwordEncoder.encode("password")
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
                "user@test.com",
                passwordEncoder.encode("password")
        );
        setUpContext(loggedCustomer);

        User contactCustomer = new User(
                2L, "user1@test.com", passwordEncoder.encode("password")
        );

        Contact givenContact = new Contact(loggedCustomer, contactCustomer);

        // when
        when(userRepository.findById(contactCustomer.getId())).thenReturn(Optional.of(contactCustomer));
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
                1L, "user@test.com",
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
                "user@test.com",
                passwordEncoder.encode("password")
        );
        setUpContext(loggedCustomer);

        User contact1 = new User(
                2L, "user1@test.com", passwordEncoder.encode("password")
        );

        // when
        when(userRepository.findById(contact1.getId())).thenReturn(Optional.of(contact1));
        when(contactRepository.contactExists(loggedCustomer.getId(), contact1.getId())).thenReturn(true);
        ContactAlreadyExistException exception = assertThrows(
                ContactAlreadyExistException.class,
                () -> contactService.addContact(contact1.getId())
        );

        // then
        assertEquals(Exceptions.CONTACT_LIST.ALREADY_EXISTS, exception.getMessage());
    }

    @Test
    @DisplayName("Should not add a contact when user not found")
    void shouldNotAddContactWhenUserNotFound() {
        // given
        User loggedCustomer = new User(
                1L,
                "user@test.com",
                passwordEncoder.encode("password")
        );
        setUpContext(loggedCustomer);

        User contact1 = new User(
                2L, "user1@test.com", passwordEncoder.encode("password")
        );

        // when
        when(userRepository.findById(contact1.getId())).thenReturn(Optional.empty());
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
        User currentUser = new User(
                1L, "user@test.com",
                passwordEncoder.encode("password")
        );
        setUpContext(currentUser);

        User contact = new User(
                2L, "user1@test.com", passwordEncoder.encode("password")
        );

        Contact givenCC = new Contact(currentUser, contact);
        givenCC.setId(1L);

        // when
        when(contactRepository.findByUser_IdAndContact_Id(currentUser.getId(), contact.getId())).thenReturn(Optional.of(
                givenCC));
        doNothing().when(contactRepository).deleteById(givenCC.getId());

        contactService.deleteContact(givenCC.getContact().getId());

        // then
        verify(contactRepository, times(1)).deleteById(givenCC.getId());
    }

    @Test
    @DisplayName("Should not delete a contact when not found")
    void shouldNotDeleteContactWhenNotFound() {
        // given
        User loggedCustomer = new User(1L, "user@test.com", passwordEncoder.encode("password"));
        setUpContext(loggedCustomer);

        // when
        when(contactRepository.findByUser_IdAndContact_Id(anyLong(), anyLong())).thenReturn(Optional.empty());
        ContactNotFoundException exception = assertThrows(
                ContactNotFoundException.class,
                () -> contactService.deleteContact(0L)
        );

        // then
        assertEquals(Exceptions.CONTACT_LIST.NOT_FOUND, exception.getMessage());
    }
}
