package com.himanshudabas.springboot.travelticketing.enumeration;

import lombok.Getter;

public enum TicketStatus {
    SUBMITTED("Submitted"),
    RESUBMITTED("Resubmitted"),
    INPROCESS("InProcess"),
    COMPLETED("Completed"),
    REJECTED("Rejected");

    @Getter
    private final String value;

    TicketStatus(String msg) {
        this.value = msg;
    }
}
