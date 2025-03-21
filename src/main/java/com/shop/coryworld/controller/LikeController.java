package com.shop.coryworld.controller;

import com.shop.coryworld.dto.LikeItemDto;
import com.shop.coryworld.service.LikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/like")
    @ResponseBody
    public ResponseEntity<?> addLike(@RequestBody @Valid LikeItemDto likeItemDto, BindingResult bindingResult,
                                     Principal principal) {

        if (bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            bindingResult.getFieldErrors().forEach(fe ->
                sb.append(fe.getDefaultMessage())
            );

            return new ResponseEntity<>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        String email = principal.getName();
        Long likeItemId;
        try {
            likeItemId = likeService.addLike(likeItemDto, email);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(likeItemId, HttpStatus.OK);
    }

    @DeleteMapping("/like")
    @ResponseBody
    public ResponseEntity<?> deleteLike(@RequestBody @Valid LikeItemDto likeItemDto, Principal principal) {
        String email = principal.getName();
        long itemId = likeItemDto.getId();
        if (!likeService.validateLikeItem(itemId, email)) {
            return new ResponseEntity<>("삭제 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        likeService.deleteLikeItem(itemId);
        return new ResponseEntity<>(itemId, HttpStatus.OK);
    }


}
