package com.shop.coryworld.controller;

import com.shop.coryworld.repository.dto.ItemSearchDto;
import com.shop.coryworld.repository.dto.MainItemDto;
import com.shop.coryworld.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class BaseController {

    private final ItemService itemService;

    @GetMapping("/")
    public String mainPage(ItemSearchDto itemSearchDto, Optional<Integer> page, Model model) {

        PageRequest pageRequest = PageRequest.of(page.orElse(0), 6);
        Page<MainItemDto> mainItemPage = itemService.getMainItemPage(itemSearchDto, pageRequest);

        if (model.containsAttribute("errorMessage")) {
            model.addAttribute("errorMessage", model.asMap().get("errorMessage"));
        }

        model.addAttribute("items", mainItemPage);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);
        return "/main";
    }
}
