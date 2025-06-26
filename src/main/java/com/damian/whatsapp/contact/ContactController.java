package com.damian.whatsapp.contact;

import com.damian.whatsapp.contact.http.ContactCreateRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RequestMapping("/api/v1")
@RestController
public class ContactController {
    private final ContactService contactService;

    @Autowired
    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    // endpoint to receive contacts from logged customer
    @GetMapping("/contacts")
    public ResponseEntity<?> getContacts() {
        Set<Contact> contacts = contactService.getContacts();
        Set<ContactDTO> contactsDTO = ContactDTOMapper.toContactDTOList(contacts);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(contactsDTO);
    }

    // endpoint to add a new contact for the logged customer
    @PostMapping("/contacts")
    public ResponseEntity<?> addContact(
            @Validated @RequestBody
            ContactCreateRequest request
    ) {
        Contact contact = contactService.addContact(request.customerId());
        ContactDTO contactDTO = ContactDTOMapper.toCustomerFriendDTO(contact);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(contactDTO);
    }

    // endpoint to delete a contact from the logged customer contact list
    @DeleteMapping("/contacts/{id}")
    public ResponseEntity<?> deleteContact(
            @PathVariable @NotNull @Positive
            Long id
    ) {
        contactService.deleteContact(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}

