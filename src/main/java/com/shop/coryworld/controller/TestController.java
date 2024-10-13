package com.shop.coryworld.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @GetMapping("/team/admin")
    public String admin() {
        return "main";
    }
}
