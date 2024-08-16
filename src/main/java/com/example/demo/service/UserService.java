package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.domain.User;
import com.example.demo.domain.request.ReqSignupDto;
import com.example.demo.domain.response.ResSignupDto;
import com.example.demo.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService (UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public User fetchUserByEmail(String email){
        return this.userRepository.findByEmail(email);
    }

    public User createNewUser(User user){
        return this.userRepository.save(user);
    }
    public boolean isEmailExistsInDB(String email){
        return this.userRepository.existsByEmail(email);
    }
    public User convertReqSignupToUser(ReqSignupDto reqSignupDto){
        User new_user = new User();
        new_user.setEmail(reqSignupDto.getEmail());
        new_user.setPassword(reqSignupDto.getPassword());
        new_user.setName(reqSignupDto.getUsername());
        new_user.setAddress(reqSignupDto.getAddress());
        return new_user;
    }
    public ResSignupDto convertUserToResSignupDto(User user){
        ResSignupDto resSignupDto = new ResSignupDto();
        resSignupDto.setEmail(user.getEmail());
        resSignupDto.setAddress(user.getAddress());
        resSignupDto.setName(user.getName());
        return resSignupDto;
    }
}
