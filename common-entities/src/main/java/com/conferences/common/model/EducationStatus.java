package com.conferences.common.model;

import lombok.Getter;

@Getter
public enum EducationStatus {
    ENROLLEE("Enrollee"),
    STUDENT("Student"),
    GRADUATE("Graduate");

    private final String value;

    EducationStatus(String value){
        this.value = value;
    }
}
