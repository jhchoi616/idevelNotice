package com.idevel.notice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;


@Controller 
@RequestMapping("/login")
@RequiredArgsConstructor 
public class LoginController {

    @GetMapping
    public String login() {
        return "login";
    }
    
    
}
