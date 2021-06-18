package com.himanshudabas.springboot.travelticketing.constant;

public class EmailConstant {
    public static final String FROM_EMAIL = "himanshudabas5@gmail.com";
    public static final String EMAIL_SUBJECT = "Travel Ticketing Information";
    public static final String EMAIL_BODY_TEMPLATE =
            "Following are your account credentials. Please exercise caution and do not share these with anyone else:\n\n" +
                    "Username: %s\n" +
                    "Password: %s\n\n" +
                    "Please contact the Travel team if you have any questions.\n\n" +
                    "Thank you,\n" +
                    "Travel Ticketing Team";
    public static final String EMAIL_EXCEPTION_MESSAGE =
            "Server error occurred. Unable to send credentials email. Please contact administration";
    public static final String EMAIL_SENT_TO = "An email with a new password was sent to: ";
}
