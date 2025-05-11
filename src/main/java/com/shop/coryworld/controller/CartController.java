package com.shop.coryworld.controller;

import com.shop.coryworld.auth.PrincipalDetails;
import com.shop.coryworld.dto.CartDetailDto;
import com.shop.coryworld.dto.CartItemDto;
import com.shop.coryworld.dto.CartOrderDto;
import com.shop.coryworld.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private final CartService cartService;

    @PostMapping("/cart")
    @ResponseBody
    public ResponseEntity<?> cartAdd(@RequestBody @Valid CartItemDto cartItemDto, BindingResult bindingResult,
                                   Principal principal) {

        if (bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            bindingResult.getFieldErrors().forEach(fe -> {
                sb.append(fe.getDefaultMessage());
            });
            return new ResponseEntity<>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        String email = principal.getName();
        Long cartItemId;

        try {
            cartItemId = cartService.addCart(cartItemDto, email);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(cartItemId, HttpStatus.OK);
    }

    @GetMapping("/cart")
    public String cartHist(@AuthenticationPrincipal PrincipalDetails principal, Model model) {
        List<CartDetailDto> cartDetailList = cartService.getCartListByUserId(principal.getId());
        log.info("cartDetailList: {}", cartDetailList.size());
        model.addAttribute("cartItems", cartDetailList);
        return "cart/cartList";
    }

    @PatchMapping("/cartItem/{cartItemId}")
    @ResponseBody
    public ResponseEntity<?> updateCartItem(@PathVariable Long cartItemId, @RequestParam int count,
                                            @AuthenticationPrincipal PrincipalDetails principal) {
        Long userId = principal.getId();

        if (count <= 0) {
            return new ResponseEntity<>("최소 1개 이상의 상품을 담아주세요.", HttpStatus.BAD_REQUEST);
        }

        if (!cartService.validateCartItemByUserId(cartItemId, userId)) {
            return new ResponseEntity<>("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        cartService.updateCartItem(cartItemId, count);
        return new ResponseEntity<>(cartItemId, HttpStatus.OK);
    }

    @DeleteMapping("/cart/{cartItemId}")
    @ResponseBody
    public ResponseEntity<?> deleteCartItem(@PathVariable Long cartItemId,
                                            @AuthenticationPrincipal PrincipalDetails principal) {
        Long userId = principal.getId();

        if (!cartService.validateCartItemByUserId(cartItemId, userId)) {
            return new ResponseEntity<>("삭제 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        cartService.deleteCartItem(cartItemId);
        return new ResponseEntity<>(cartItemId, HttpStatus.OK);
    }

    @PostMapping("/cart/orders")
    @ResponseBody
    public ResponseEntity<?> orderCartItem(@RequestBody CartOrderDto cartOrderDto,
                                           @AuthenticationPrincipal PrincipalDetails principal) {

        List<CartOrderDto> cartOrderDtoList = cartOrderDto.getCartOrderDtoList();
        Long userId = principal.getId();

        if (cartOrderDtoList.isEmpty()) {
            return new ResponseEntity<>("주문할 상품을 선택해 주세요", HttpStatus.FORBIDDEN);
        }

        for (CartOrderDto cartOrder : cartOrderDtoList) {
            if (!cartService.validateCartItemByUserId(cartOrder.getCartItemId(), userId)) {
                return new ResponseEntity<>("주문권한이 없습니다.", HttpStatus.FORBIDDEN);
            }
        }

        Long orderId = cartService.orderCartItems(cartOrderDtoList, userId);

        return new ResponseEntity<>(orderId, HttpStatus.OK);
    }
}
