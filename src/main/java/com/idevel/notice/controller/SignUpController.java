package com.idevel.notice.controller;

import com.idevel.notice.dto.MemberSignupRequest;
import com.idevel.notice.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/signup")
public class SignUpController {

    private final MemberService memberService;

    @GetMapping
    public String signupForm() {
        return "signup";
    }

    @PostMapping
    public String signup( @ModelAttribute MemberSignupRequest request, Model model ) {
        try {
            memberService.signup(request);
            return "redirect:/login";

        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return "signup";
        }
    }
}