package com.shop.coryworld.controller;

import com.shop.coryworld.repository.dto.CartDetailDto;
import com.shop.coryworld.repository.dto.CartItemDto;
import com.shop.coryworld.repository.dto.CartOrderDto;
import com.shop.coryworld.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> order(@RequestBody @Valid CartItemDto cartItemDto, BindingResult bindingResult,
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
    public String orderHist(Principal principal, Model model) {
        List<CartDetailDto> cartDetailList = cartService.getCartList(principal.getName());
        log.info("cartDetailList: {}", cartDetailList.size());
        model.addAttribute("cartItems", cartDetailList);
        return "cart/cartList";
    }

    @PatchMapping("/cartItem/{cartItemId}")
    @ResponseBody
    public ResponseEntity<?> updateCartItem(@PathVariable Long cartItemId, @RequestParam int count, Principal principal) {
        String email = principal.getName();

        if (count <= 0) {
            return new ResponseEntity<>("최소 1개 이상의 상품을 담아주세요.", HttpStatus.BAD_REQUEST);
        }

        if (!cartService.validateCartItem(cartItemId, email)) {
            return new ResponseEntity<>("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        cartService.updateCartItem(cartItemId, count);
        return new ResponseEntity<>(cartItemId, HttpStatus.OK);
    }

    @DeleteMapping("/cart/{cartItemId}")
    @ResponseBody
    public ResponseEntity<?> deleteCartItem(@PathVariable Long cartItemId, Principal principal) {
        String email = principal.getName();
        if (!cartService.validateCartItem(cartItemId, email)) {
            return new ResponseEntity<>("삭제 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        cartService.deleteCartItem(cartItemId);
        return new ResponseEntity<>(cartItemId, HttpStatus.OK);
    }

    @PostMapping("/cart/orders")
    @ResponseBody
    public ResponseEntity<?> orderCartItem(@RequestBody CartOrderDto cartOrderDto, Principal principal) {

        List<CartOrderDto> cartOrderDtoList = cartOrderDto.getCartOrderDtoList();
        String email = principal.getName();

        if (cartOrderDtoList.isEmpty()) {
            return new ResponseEntity<>("주문할 상품을 선택해 주세요", HttpStatus.FORBIDDEN);
        }

        for (CartOrderDto cartOrder : cartOrderDtoList) {
            if (!cartService.validateCartItem(cartOrder.getCartItemId(), email)) {
                return new ResponseEntity<>("주문권한이 없습니다.", HttpStatus.FORBIDDEN);
            }
        }

        Long orderId = cartService.orderCartItem(cartOrderDtoList, email);

        return new ResponseEntity<>(orderId, HttpStatus.OK);
    }
}
