package com.damian.whatsapp.modules.contact.http.web.rest.dto.mapper;

import com.damian.whatsapp.modules.contact.http.web.rest.dto.response.ContactDto;
import com.damian.whatsapp.shared.domain.Contact;

import java.util.Set;
import java.util.stream.Collectors;

public class ContactDtoMapper {
    public static ContactDto toCustomerFriendDTO(Contact contact) {
        return new ContactDto(
                contact.getId(),
                contact.getContact().getId(),
                contact.getContact().getFullName(),
                contact.getContact().getImageFilename()
        );
    }

    public static Set<ContactDto> toContactDTOList(Set<Contact> customers) {
        return customers
                .stream()
                .map(
                        ContactDtoMapper::toCustomerFriendDTO
                ).collect(Collectors.toSet());
    }
}
