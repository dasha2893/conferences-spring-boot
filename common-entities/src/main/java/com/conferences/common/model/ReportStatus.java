package com.conferences.common.model;

import lombok.Getter;

@Getter
public enum ReportStatus {
    UNDER_REVIEW("Under Review"),
    ACCEPTED("Accepted"),
    REJECTED("Rejected");

    private final String value;

    ReportStatus(String value) {
        this.value = value;
    }

}
