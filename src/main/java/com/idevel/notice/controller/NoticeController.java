package com.idevel.notice.controller;

import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;


@Controller 
@RequiredArgsConstructor 
public class NoticeController {
    @GetMapping({"/", "/index"})
    public String home() {
        return "index";
    }
    
}
