package com.damian.whatsapp.common.exception;

public class Exceptions {
    public static class AUTH {
        public static final String NOT_ADMIN = "You are not an admin.";
        public static final String BAD_CREDENTIALS = "Bad credentials.";
    }

    public static class CONTACT_LIST {
        public static final String ACCESS_FORBIDDEN = "You cannot access this contact.";
        public static final String NOT_FOUND = "Contact not found.";
        public static final String MAX_CONTACTS = "You have reached the maximum number of contacts.";
    }


    public static class CUSTOMER {
        public static final String DISABLED = "Customer is disabled.";
        public static final String EMAIL_TAKEN = "Email is already taken.";
        public static final String NOT_FOUND = "Customer not found.";
    }

    public static class ROOM {
        public static final String NOT_FOUND = "Room not found.";
    }

    public static class PROFILE {
        public static final String NOT_FOUND = "Profile not found.";
        public static final String INVALID_FIELD = "Field is invalid.";
        public static final String ACCESS_FORBIDDEN = "You are not authorized to access this profile.";

        public static class IMAGE {
            public static final String NOT_FOUND = "Profile photo not found.";
            public static final String FILE_SIZE_LIMIT = "Profile photo is too large.";
            public static final String ONLY_IMAGES_ALLOWED = "Profile photo must be an image.";
            public static final String EMPTY_FILE = "File is empty.";
            public static final String UPLOAD_FAILED = "Profile photo upload failed.";
        }
    }
}
