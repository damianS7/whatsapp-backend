package com.damian.whatsapp.customer.profile.admin;

import com.damian.whatsapp.customer.profile.Profile;
import com.damian.whatsapp.customer.profile.ProfileDTO;
import com.damian.whatsapp.customer.profile.ProfileDTOMapper;
import com.damian.whatsapp.customer.profile.ProfileService;
import com.damian.whatsapp.customer.profile.http.request.ProfileUpdateRequest;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1")
@RestController
public class ProfileAdminController {
    private final ProfileService profileService;

    @Autowired
    public ProfileAdminController(ProfileService profileService) {
        this.profileService = profileService;
    }

    // endpoint to get the profile of a customer
    @GetMapping("/admin/profiles/{id}")
    public ResponseEntity<?> getCustomerProfile(
            @PathVariable @Positive
            Long id
    ) {
        Profile profile = profileService.getProfile(id);
        ProfileDTO profileDTO = ProfileDTOMapper.toProfileDTO(profile);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(profileDTO);
    }

    // endpoint to modify the fields profile
    @PatchMapping("/admin/profiles/{id}")
    public ResponseEntity<?> updateCustomerProfile(
            @PathVariable @Positive
            Long id,
            @Validated @RequestBody
            ProfileUpdateRequest request
    ) {
        Profile profile = profileService.updateProfile(id, request);
        ProfileDTO profileDTO = ProfileDTOMapper.toProfileDTO(profile);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(profileDTO);
    }
}

