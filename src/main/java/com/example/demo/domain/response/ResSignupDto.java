package com.example.demo.domain.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResSignupDto {
    private String access_token;
    private String name;
    private String address;
    private String email;
}
