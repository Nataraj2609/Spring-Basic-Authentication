package com.learn.springlearn.controller;

import com.learn.springlearn.model.User;
import com.learn.springlearn.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pat")
public class PatientController {

    @Autowired
    UserRepository userRepository;

    @GetMapping
    public User getH() {
        User user = userRepository.findByUsername("nat");
        return user;
    }
}
