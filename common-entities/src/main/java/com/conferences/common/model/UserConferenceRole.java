package com.conferences.common.model;

import lombok.Getter;

@Getter
public enum UserConferenceRole {
    SPEAKER("Speaker"), ATTENDEE("Attendee");

    private final String value;

    UserConferenceRole(String value){
        this.value = value;
    }
}
