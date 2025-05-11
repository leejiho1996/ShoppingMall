package com.shop.coryworld.controller.like;

import com.shop.coryworld.auth.PrincipalDetails;
import com.shop.coryworld.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class LikeApiController {
    private final LikeService likeService;

    @PostMapping("/like/{itemId}")
    public ResponseEntity<?> addLike(@PathVariable(name = "itemId")Long itemId,
                                     @AuthenticationPrincipal PrincipalDetails user) {

        Long userId = user.getId();
        Long likeItemId;

        try {
            likeItemId = likeService.addLike(itemId, userId);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(likeItemId, HttpStatus.OK);
    }

    @DeleteMapping("/like/{itemId}")
    public ResponseEntity<?> deleteLike(@PathVariable(name="itemId") Long itemId, @AuthenticationPrincipal PrincipalDetails user) {
        Long userId = user.getId();

//        if (!likeService.validateLikeItem(itemId, email)) {
//            return new ResponseEntity<>("삭제 권한이 없습니다.", HttpStatus.FORBIDDEN);
//        }

        likeService.deleteLike(itemId, userId);
        return new ResponseEntity<>(itemId, HttpStatus.OK);
    }


}
