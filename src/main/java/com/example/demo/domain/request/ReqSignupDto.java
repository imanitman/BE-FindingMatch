package com.example.demo.domain.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqSignupDto {
    private String username;
    private String email;
    private String password;
    private String address;
}
