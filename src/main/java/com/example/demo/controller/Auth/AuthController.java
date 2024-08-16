package com.example.demo.controller.Auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.User;
import com.example.demo.domain.request.ReqLoginDto;
import com.example.demo.domain.request.ReqSignupDto;
import com.example.demo.domain.response.ResLoginDto;
import com.example.demo.domain.response.ResSignupDto;
import com.example.demo.service.UserService;
import com.example.demo.util.SecurityUtil;
import com.example.demo.util.error.InvalidException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final SecurityUtil securityUtil;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;


    private final PasswordEncoder passwordEncoder;
    public AuthController(UserService userService, PasswordEncoder passwordEncoder, SecurityUtil securityUtil, AuthenticationManagerBuilder authenticationManagerBuilder){
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.securityUtil = securityUtil;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }
    @Value("${nam.jwt.base64-secret}")
    private String jwtKey;
    @Value("${nam.jwt.access-token-validity-in-seconds}")
    private long accessTokenExpitration;

    @PostMapping("/signup")
    public ResponseEntity<ResSignupDto> signupPage(@RequestBody ReqSignupDto reqSignup) throws InvalidException{
        String check_email = reqSignup.getEmail();
        if(this.userService.isEmailExistsInDB(check_email)){
            throw new InvalidException("Email has already existed");
        }
        reqSignup.setPassword(this.passwordEncoder.encode(reqSignup.getPassword()));
        User new_user = this.userService.convertReqSignupToUser(reqSignup);
        this.userService.createNewUser(new_user);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertUserToResSignupDto(new_user));
    }

    @PostMapping("/login")
    public ResponseEntity<ResLoginDto> loginPage(@Valid @RequestBody ReqLoginDto loginDto){
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDto.getUsername(), loginDto.getPassword());

        // xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);

        // set thông tin người dùng đăng nhập vào context (có thể sử dụng sau này)
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResLoginDto resLoginDto = new ResLoginDto();
        User currentUserDB = this.userService.fetchUserByEmail(loginDto.getUsername());
        if (currentUserDB != null){
            ResLoginDto.UserLogin userLogin = new ResLoginDto.UserLogin();
            userLogin.setEmail(currentUserDB.getEmail());
            userLogin.setId(currentUserDB.getId());
            userLogin.setName(currentUserDB.getName());
            resLoginDto.setUser(userLogin);
        }
        String access_token = this.securityUtil.createAccessToken(currentUserDB.getEmail(), resLoginDto);
        resLoginDto.setAccessToken(access_token);

        ResponseCookie responseCookie =  ResponseCookie
            .from("access_token", access_token)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(accessTokenExpitration)
            .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(resLoginDto);

    }

}
