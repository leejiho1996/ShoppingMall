package com.shop.coryworld.controller.like;

import com.shop.coryworld.auth.PrincipalDetails;
import com.shop.coryworld.entity.Item;
import com.shop.coryworld.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class likeController {

    private final LikeService likeService;

    @RequestMapping
    public String getLikeItem(@AuthenticationPrincipal PrincipalDetails principalDetails, Model model) {
        List<Item> likeItem = likeService.getLikeItem(principalDetails.getId());
        model.addAttribute("likeItem", likeItem);
        return "item/likes";
    }
}
