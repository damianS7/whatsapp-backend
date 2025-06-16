package com.damian.whatsapp.contact;

import com.damian.whatsapp.common.exception.Exceptions;
import com.damian.whatsapp.contact.exception.ContactAuthorizationException;
import com.damian.whatsapp.contact.exception.MaxContactsLimitReachedException;
import com.damian.whatsapp.customer.Customer;
import com.damian.whatsapp.customer.CustomerRepository;
import com.damian.whatsapp.customer.exception.CustomerNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ContactServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private ContactService contactService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        customerRepository.deleteAll();
    }

    @AfterEach
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    void setUpContext(Customer customer) {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(customer);
    }

    @Test
    @DisplayName("Should get all friends")
    void shouldGetAllFriends() {
        // given
        Customer loggedCustomer = new Customer(1L, "customer@test.com", passwordEncoder.encode("password"));
        setUpContext(loggedCustomer);

        Customer contact1 = new Customer(
                2L, "customer1@test.com", passwordEncoder.encode("password")
        );

        Customer contact2 = new Customer(
                3L, "customer2@test.com", passwordEncoder.encode("password")
        );

        Set<Contact> contactList = Set.of(
                new Contact(loggedCustomer, contact1),
                new Contact(loggedCustomer, contact2)
        );

        // when
        when(contactRepository.findAllByCustomerId(loggedCustomer.getId()))
                .thenReturn(contactList);
        Set<Contact> result = contactService.getContacts();

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(contactRepository, times(1)).findAllByCustomerId(loggedCustomer.getId());
    }

    @Test
    @DisplayName("Should add a friend")
    void shouldAddContact() {
        // given
        Customer loggedCustomer = new Customer(1L, "customer@test.com", passwordEncoder.encode("password"));
        setUpContext(loggedCustomer);

        Customer friend1 = new Customer(
                2L, "customer1@test.com", passwordEncoder.encode("password")
        );

        Contact givenCC = new Contact(loggedCustomer, friend1);

        // when
        when(customerRepository.findById(friend1.getId())).thenReturn(Optional.of(friend1));
        when(contactRepository.save(any(Contact.class)))
                .thenReturn(givenCC);

        Contact result = contactService.addContact(friend1.getId());

        // then
        assertNotNull(result);
        verify(contactRepository, times(1)).save(any(Contact.class));
    }

    @Test
    @DisplayName("Should not add a contact when limit reached")
    void shouldNotAddContactWhenLimitReached() {
        // given
        Customer loggedCustomer = new Customer(1L, "customer@test.com", passwordEncoder.encode("password"));
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
        when(contactRepository.findAllByCustomerId(loggedCustomer.getId())).thenReturn(contactList);
        MaxContactsLimitReachedException exception = assertThrows(
                MaxContactsLimitReachedException.class,
                () -> contactService.addContact(0L)
        );

        // then
        assertEquals(Exceptions.CONTACT_LIST.MAX_CONTACTS, exception.getMessage());
    }

    @Test
    @DisplayName("Should not add a contact when customer not found")
    void shouldNotAddContactWhenCustomerNotFound() {
        // given
        Customer loggedCustomer = new Customer(1L, "customer@test.com", passwordEncoder.encode("password"));
        setUpContext(loggedCustomer);

        Customer contact1 = new Customer(
                2L, "customer1@test.com", passwordEncoder.encode("password")
        );

        // when
        when(customerRepository.findById(contact1.getId())).thenReturn(Optional.empty());
        CustomerNotFoundException exception = assertThrows(
                CustomerNotFoundException.class,
                () -> contactService.addContact(contact1.getId())
        );

        // then
        assertEquals(Exceptions.CUSTOMER.NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("Should delete a contact")
    void shouldDeleteContact() {
        // given
        Customer loggedCustomer = new Customer(1L, "customer@test.com", passwordEncoder.encode("password"));
        setUpContext(loggedCustomer);

        Customer contact1 = new Customer(
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
    @DisplayName("Should not delete a contact when customer not found")
    void shouldNotDeleteContactWhenCustomerNotFound() {
        // given
        Customer loggedCustomer = new Customer(1L, "customer@test.com", passwordEncoder.encode("password"));
        setUpContext(loggedCustomer);

        // when
        when(contactRepository.findById(anyLong())).thenReturn(Optional.empty());
        CustomerNotFoundException exception = assertThrows(
                CustomerNotFoundException.class,
                () -> contactService.deleteContact(0L)
        );

        // then
        assertEquals(Exceptions.CUSTOMER.NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("Should not delete a contact when not authorized")
    void shouldNotDeleteContactWhenNotAuthorized() {
        // given
        Customer loggedCustomer = new Customer(1L, "customer@test.com", passwordEncoder.encode("password"));
        setUpContext(loggedCustomer);

        Contact givenCC = new Contact(
                new Customer(5L, "customer1@test.com", passwordEncoder.encode("password")),
                new Customer(8L, "customer2@test.com", passwordEncoder.encode("password"))
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
