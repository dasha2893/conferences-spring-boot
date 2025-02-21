package com.conferences.common.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuthDTO {
    @JsonProperty("email")
    private String email;
    @JsonProperty("password")
    private String password;
}
