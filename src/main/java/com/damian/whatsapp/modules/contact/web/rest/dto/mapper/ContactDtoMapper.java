package com.damian.whatsapp.modules.contact.web.rest.dto.mapper;

import com.damian.whatsapp.modules.contact.web.rest.dto.response.ContactDto;
import com.damian.whatsapp.shared.domain.Contact;

import java.util.Set;
import java.util.stream.Collectors;

public class ContactDtoMapper {
    public static ContactDto toUserContactDto(Contact contact) {
        return new ContactDto(
                contact.getId(),
                contact.getContact().getId(),
                contact.getContact().getFullName(),
                contact.getContact().getImageFilename()
        );
    }

    public static Set<ContactDto> toContactDtoList(Set<Contact> customers) {
        return customers
                .stream()
                .map(
                        ContactDtoMapper::toUserContactDto
                ).collect(Collectors.toSet());
    }
}
