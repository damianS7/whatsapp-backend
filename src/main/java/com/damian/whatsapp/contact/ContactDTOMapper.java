package com.damian.whatsapp.contact;

import java.util.Set;
import java.util.stream.Collectors;

public class ContactDTOMapper {
    public static ContactDTO toCustomerFriendDTO(Contact contact) {
        return new ContactDTO(
                contact.getId(),
                contact.getContact().getId(),
                contact.getContact().getFullName(),
                contact.getContact().getProfile().getAvatarFilename()
        );
    }

    public static Set<ContactDTO> toContactDTOList(Set<Contact> customers) {
        return customers
                .stream()
                .map(
                        ContactDTOMapper::toCustomerFriendDTO
                ).collect(Collectors.toSet());
    }
}
