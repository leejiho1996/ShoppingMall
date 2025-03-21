package com.shop.coryworld.controller;

import com.shop.coryworld.entity.Member;
import com.shop.coryworld.dto.MemberFormDto;
import com.shop.coryworld.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/new")
    public String createMemberForm(@ModelAttribute MemberFormDto memberFormDto) {
        return "members/memberForm";
    }

    @PostMapping("/new")
    public String createMember(@ModelAttribute MemberFormDto memberFormDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "members/memberForm";
        }
        try{
            Member member = new Member(memberFormDto, passwordEncoder);
            memberService.saveMember(member);

        } catch (IllegalStateException e){
            model.addAttribute("errorMessage", e.getMessage());
            return "members/memberForm";
        }

        return "redirect:/";
    }

    @GetMapping("/login")
    public String login() {
        return "members/memberLoginForm";
    }

    @GetMapping("/login/error")
    public String loginError(Model model) {
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요");
        return "members/memberLoginForm";

    }

}
