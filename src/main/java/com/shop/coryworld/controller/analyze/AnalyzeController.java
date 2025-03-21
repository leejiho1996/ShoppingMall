package com.shop.coryworld.controller.analyze;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/analyze")
public class AnalyzeController {

    @GetMapping("/fish")
    public String fish() {
        return "/analyze/fish";
    }
}
