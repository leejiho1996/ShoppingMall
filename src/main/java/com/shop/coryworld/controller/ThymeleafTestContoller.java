package com.shop.coryworld.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/thymeleaf")
public class ThymeleafTestContoller {

    @GetMapping("/ex07")
    public String thymeleafEx07() {
        return "thymeleafEx";
    }
}
