package com.himanshudabas.springboot.travelticketing.enumeration;

import lombok.Getter;

@Getter
public enum TicketType {
    TRAVEL_TICKET("Travel Tickets"),
    HOTEL_STAYS("Hotel Stays"),
    VISA("Visa"),
    WORK_PERMIT("Work Permit");

    private final String value;

    TicketType(String str) {
        this.value = str;
    }
}
