package com.damian.whatsapp.customer.profile;

import com.damian.whatsapp.common.exception.Exceptions;
import com.damian.whatsapp.customer.Customer;
import com.damian.whatsapp.customer.profile.exception.ProfileAuthorizationException;

public class ProfileAuthorizationHelper {
    private Customer customer;
    private Profile profile;

    public static ProfileAuthorizationHelper authorize(Customer customer, Profile profile) {
        ProfileAuthorizationHelper helper = new ProfileAuthorizationHelper();
        helper.profile = profile;
        helper.customer = customer;
        return helper;
    }

    /**
     * Check if the BankingCard belongs to this customer
     *
     * @return ProfileAuthorizationHelper
     */
    public ProfileAuthorizationHelper checkOwner() {
        if (!profile.getOwner().getId().equals(customer.getId())) {
            // banking card does not belong to this customer
            throw new ProfileAuthorizationException(
                    Exceptions.PROFILE.ACCESS_FORBIDDEN
            );
        }
        return this;
    }
}
