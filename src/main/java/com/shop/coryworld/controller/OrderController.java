package com.shop.coryworld.controller;

import com.shop.coryworld.dto.OrderDto;
import com.shop.coryworld.dto.OrderHistDto;
import com.shop.coryworld.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/order")
    @ResponseBody
    public ResponseEntity<?> order(@RequestBody @Valid OrderDto orderDto, BindingResult bindingResult,
                                              Principal principal) {
        if (bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            bindingResult.getAllErrors().forEach(error -> sb.append(error.getDefaultMessage()));
            return new ResponseEntity<>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        String email = principal.getName();
        Long orderId;

        try {
            orderId = orderService.order(orderDto, email);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(orderId, HttpStatus.OK);
    }

    @GetMapping(value = {"/orders", "/orders/{page}"})
    public String orderHist(@PathVariable("page") Optional<Integer> page, Principal principal, Model model,
                            RedirectAttributes redirectAttributes) {

        Integer pageInt = page.orElse(0);
        PageRequest pageRequest = PageRequest.of(page.orElse(0), 4);
        Page<OrderHistDto> orderHistList = orderService.getOrderList(principal.getName(), pageRequest);

        log.info("pageInt : {}" , pageInt);
        log.info("orderHistList : {}" , orderHistList.getContent());

        if (pageInt != 0 && orderHistList.getContent().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "잘못된 접근입니다.");
            return "redirect:/";
        }

        model.addAttribute("orders", orderHistList);
        model.addAttribute("page", pageRequest.getPageNumber());
        model.addAttribute("maxPage", 5);
        return "order/orderHist";
    }

    @PostMapping("/order/{orderId}/cancel")
    @ResponseBody
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId, Principal principal) {

        if (!orderService.validateOrder(orderId, principal.getName())) {
            return new ResponseEntity<>("주문 취소 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        orderService.cancelOrder(orderId);

        return new ResponseEntity<>(orderId, HttpStatus.OK);
    }
}
