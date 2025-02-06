package com.conferences.common.model;

import lombok.Getter;

@Getter
public enum Gender {
    M("Male"), F("Female");

    private final String value;

    Gender(String value) {
        this.value = value;
    }
}
