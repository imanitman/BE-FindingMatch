package com.example.demo.config;

import java.io.IOException;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;

import com.example.demo.domain.response.RestResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final AuthenticationEntryPoint delegate = new BearerTokenAuthenticationEntryPoint();
    private final ObjectMapper objectMapper;
    public CustomAuthenticationEntryPoint (ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest httpRequest, HttpServletResponse httpResponse, AuthenticationException authException) throws IOException, ServletException{
        this.delegate.commence(httpRequest, httpResponse, authException);
        httpResponse.setContentType("application/json;charset=UTF-8");

        RestResponse<Object> response = new RestResponse<Object>();
        response.setStatusCode(HttpStatus.UNAUTHORIZED.value());

        String errorMessage = Optional.ofNullable(authException.getCause())
            .map(Throwable::getMessage)
            .orElse(authException.getMessage());
        response.setError((errorMessage));
        response.setMessage("Token is invalid");
        objectMapper.writeValue(httpResponse.getWriter(),response);
    }
}
