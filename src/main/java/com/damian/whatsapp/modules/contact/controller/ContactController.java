package com.damian.whatsapp.modules.contact.controller;

import com.damian.whatsapp.modules.contact.dto.mapper.ContactDtoMapper;
import com.damian.whatsapp.modules.contact.dto.request.ContactCreateRequest;
import com.damian.whatsapp.modules.contact.dto.response.ContactDto;
import com.damian.whatsapp.modules.contact.service.ContactService;
import com.damian.whatsapp.shared.domain.Contact;
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

    // endpoint to receive contacts from logged user
    @GetMapping("/contacts")
    public ResponseEntity<?> getContacts() {
        Set<Contact> contacts = contactService.getContacts();
        Set<ContactDto> contactsDto = ContactDtoMapper.toContactDtoList(contacts);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(contactsDto);
    }

    // endpoint to add a new contact for the logged user
    @PostMapping("/contacts")
    public ResponseEntity<?> addContact(
            @Validated @RequestBody
            ContactCreateRequest request
    ) {
        Contact contact = contactService.addContact(request.userId());
        ContactDto contactDto = ContactDtoMapper.toUserContactDto(contact);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(contactDto);
    }

    // endpoint to delete a contact from the logged user contact list
    @DeleteMapping("/contacts/{userId}")
    public ResponseEntity<?> deleteContact(
            @PathVariable @NotNull @Positive
            Long userId
    ) {
        contactService.deleteContact(userId);

        return ResponseEntity
                .noContent()
                .build();
    }
}

