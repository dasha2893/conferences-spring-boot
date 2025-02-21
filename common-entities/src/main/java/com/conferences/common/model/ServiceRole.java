package com.conferences.common.model;

import lombok.Getter;

@Getter
public enum ServiceRole {
    SERVICE_ROLE("SERVICE");

    private final String role;

    ServiceRole(String role){
        this.role = role;
    }
}
