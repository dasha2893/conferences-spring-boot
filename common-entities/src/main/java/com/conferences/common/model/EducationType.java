package com.conferences.common.model;

import lombok.Getter;

@Getter
public enum EducationType {
    FULL_TIME("Full-Time"), PART_TIME("Part-Time");

    private final String value;

    EducationType(String value) {
        this.value = value;
    }
}
