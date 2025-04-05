package com.shop.coryworld.controller;

import com.shop.coryworld.auth.PrincipalDetails;
import com.shop.coryworld.dto.ItemSearchDto;
import com.shop.coryworld.dto.MainItemDto;
import com.shop.coryworld.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class BaseController {

    private final ItemService itemService;

    @GetMapping("/")
    public String mainPage(ItemSearchDto itemSearchDto, Optional<Integer> page, Model model,
                           @AuthenticationPrincipal PrincipalDetails user) {

        PageRequest pageRequest = PageRequest.of(page.orElse(0), 6);
        Page<MainItemDto> mainItemPage = itemService.getMainItemPage(itemSearchDto, pageRequest, user);

        if (model.containsAttribute("errorMessage")) {
            model.addAttribute("errorMessage", model.asMap().get("errorMessage"));
        }

        model.addAttribute("items", mainItemPage);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);

        return "/main";
    }
}
