package com.example.demo.controller.User;

import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.User;
import com.example.demo.service.UserService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class UserController {
    
    private final UserService userService;
    public UserController(UserService userService){
        this.userService = userService;
    }
    
    @GetMapping("/user")
    public ResponseEntity<List<User>> getSingleUser() {
        List<User> allUsers = this.userService.fetchAllUsers();
        return ResponseEntity.ok().body(allUsers);
        
    }
    
}
