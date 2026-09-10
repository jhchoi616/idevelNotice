package com.idevel.notice.controller;

import com.idevel.notice.dto.MemberUpdateRequest;
import com.idevel.notice.entity.Member;
import com.idevel.notice.security.CustomUserDetails;
import com.idevel.notice.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MyPageController {

    private final MemberService memberService;

    @GetMapping
    public String myPage(
            Authentication authentication,
            Model model
    ) {

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        Member member = userDetails.getMember();

        model.addAttribute("member", member);

        return "mypage";
    }

    @PostMapping
    public String update(
            Authentication authentication,
            @Valid MemberUpdateRequest request,
            BindingResult bindingResult,
            Model model
    ) {

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        Member member = userDetails.getMember();

        if (bindingResult.hasErrors()) {
            model.addAttribute("member", member);
            return "mypage";
        }
        System.out.println("?? 와도 여기서 못 받아준거 아님? : "+request.getEmail());
        try {

            memberService.updateInfo(
                    member.getId(),
                    request
            );

            Member updatedMember = memberService.findById(member.getId());

            CustomUserDetails updatedUserDetails =
                    new CustomUserDetails(updatedMember);

            Authentication newAuthentication =
                new UsernamePasswordAuthenticationToken(
                            updatedUserDetails,
                            authentication.getCredentials(),
                            updatedUserDetails.getAuthorities()
                );

            SecurityContextHolder.getContext()
                    .setAuthentication(newAuthentication);


            } catch (IllegalStateException e) {

                bindingResult.rejectValue(
                        "email",
                        "duplicate",
                        e.getMessage()
                );

            model.addAttribute("member", member);

            return "mypage";
        }

        return "redirect:/mypage?success";
    }
}