package com.damian.whatsapp.contact;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    // endpoint to receive logged customer
    @GetMapping("/contacts")
    public ResponseEntity<?> getContacts() {
        Set<Contact> contacts = contactService.getContacts();
        Set<ContactDTO> contactsDTO = ContactDTOMapper.toContactDTOList(contacts);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(contactsDTO);
    }

    @PostMapping("/contacts/{id}")
    public ResponseEntity<?> addContact(
            @PathVariable @NotNull @Positive
            Long id
    ) {
        Contact contact = contactService.addContact(id);
        ContactDTO contactDTO = ContactDTOMapper.toCustomerFriendDTO(contact);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(contactDTO);
    }

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

