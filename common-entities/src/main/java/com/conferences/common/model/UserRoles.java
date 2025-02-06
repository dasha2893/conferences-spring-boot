package com.conferences.common.model;

import lombok.Getter;

@Getter
public enum UserRoles {
    ADMIN("Admin"), USER("User");

    private final String value;

    UserRoles(String value) {
        this.value = value;
    }

    public String getRole(){
        return "ROLE_" + value;
    }
}
