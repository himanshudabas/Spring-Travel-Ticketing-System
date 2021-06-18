package com.himanshudabas.springboot.travelticketing.enumeration;

import lombok.Getter;

@Getter
public enum ExpenseBorneByType {
    COMPANY("Company"),
    CLIENT("Client");

    private final String value;

    ExpenseBorneByType(String str) {
        this.value = str;
    }
}
