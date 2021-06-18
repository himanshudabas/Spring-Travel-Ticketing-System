package com.himanshudabas.springboot.travelticketing.enumeration;

import lombok.Getter;

@Getter
public enum TicketPriorityType {

    NORMAL("Normal"),
    URGENT("Urgent"),
    IMMEDIATE("Immediate");

    private final String value;

    TicketPriorityType(String str) {
        this.value = str;
    }

}
