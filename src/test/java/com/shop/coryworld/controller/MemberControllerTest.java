package com.shop.coryworld.controller;

import com.shop.coryworld.entity.Member;
import com.shop.coryworld.repository.dto.MemberFormDto;
import com.shop.coryworld.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberService memberService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("로그인 테스트")
    public void login_success() throws Exception {
        createTestMember();

        mockMvc.perform(formLogin("/members/login").userParameter("email")
                .user("test@email.com")
                .password("123"))
                .andExpect(SecurityMockMvcResultMatchers.authenticated());
    }

    @Test
    @DisplayName("일반회원 접근 테스트")
    @WithMockUser(username = "user", roles = "ADMIN")
    public void adminTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/item/new"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("일반회원 접근 테스트")
    @WithMockUser(username = "user", roles = "USER")
    public void notAdminTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/item/new"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    private Member createTestMember() {

        MemberFormDto memberDto = new MemberFormDto();
        memberDto.setEmail("test@email.com");
        memberDto.setPassword("123");
        memberDto.setAddress("test address");
        memberDto.setName("testUser");

        Member member = new Member(memberDto, passwordEncoder);
        memberService.saveMember(member);

        return member;

    }


}