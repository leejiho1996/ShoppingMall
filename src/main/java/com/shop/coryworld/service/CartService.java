package com.shop.coryworld.service;

import com.shop.coryworld.entity.*;
import com.shop.coryworld.repository.*;
import com.shop.coryworld.dto.CartDetailDto;
import com.shop.coryworld.dto.CartItemDto;
import com.shop.coryworld.dto.CartOrderDto;
import com.shop.coryworld.dto.OrderDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CartService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderService orderService;

    @Transactional
    public Long addCart(CartItemDto cartItemDto, String email) {
        Item item = itemRepository.findById(cartItemDto.getItemId())
                .orElseThrow(EntityNotFoundException::new);

        Member member = memberRepository.findByEmail(email);
        Cart cart = cartRepository.findByMemberId(member.getId());

        if (cart == null) {
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }

        CartItem savedCartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId());

        if (savedCartItem != null) {
            savedCartItem.addCount(cartItemDto.getCount());
            return savedCartItem.getId();
        } else {
            CartItem cartItem = CartItem.createCartItem(item, cartItemDto.getCount(), cart);
            cartItemRepository.save(cartItem);
            return cartItem.getId();
        }
    }

    /**
     * PrincipalDetails를 활용하여 Member 찾는 과정을 생략한 메서드
     */
    public List<CartDetailDto> getCartListByUserId(Long userId) {
        List<CartDetailDto> cartDetailDtoList = new ArrayList<>();
        Cart cart = cartRepository.findByMemberId(userId);

        if (cart == null) {
            return cartDetailDtoList;
        }

        cartDetailDtoList = cartItemRepository.findCartDetailDtoList(cart.getId());
        return cartDetailDtoList;
    }

    @Deprecated
    public List<CartDetailDto> getCartList(String email) {
        Member member = memberRepository.findByEmail(email);
        List<CartDetailDto> cartDetailDtoList = new ArrayList<>();
        Cart cart = cartRepository.findByMemberId(member.getId());

        if (cart == null) {
            return cartDetailDtoList;
        }

        cartDetailDtoList = cartItemRepository.findCartDetailDtoList(cart.getId());
        log.info("cartDetailDtoList: {}", cartDetailDtoList);
        log.info("cartDetailDtoList: {}", cartDetailDtoList.size());
        return cartDetailDtoList;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public boolean validateCartItemByUserId(Long cartItemId, Long userId) {
        CartItem cartItem = cartItemRepository.findCartItemByIdWithCart(cartItemId)
                .orElseThrow(EntityNotFoundException::new);

        Long savedMemberId = cartItem.cart.getMember().getId();

        if (savedMemberId != userId) {
            return false;
        }
        return true;
    }

    @Deprecated
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public boolean validateCartItem(Long cartItemId, String email) {
        Member curMember = memberRepository.findByEmail(email);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);

        Member savedMember = cartItem.getCart().getMember();

        if (curMember != savedMember) {
            return false;
        }
        return true;
    }

    @Transactional
    public Long orderCartItems(List<CartOrderDto> cartOrderDtoList, Long userId) {
        List<OrderDto> orderDtoList = new ArrayList<>();

        List<Long> cartItemIdList = cartOrderDtoList.stream()
                .map(CartOrderDto::getCartItemId)
                .toList();

        List<CartItem> cartItemList = cartItemRepository.findAllById(cartItemIdList);

        if (cartItemList.size() != cartItemIdList.size()) {
            throw new EntityNotFoundException();
        }

        for (CartItem cartItem : cartItemList) {
            OrderDto orderDto = new OrderDto();
            orderDto.setItemId(cartItem.getItem().getId());
            orderDto.setCount(cartItem.getCount());
            orderDtoList.add(orderDto);
        }

        Long orderId = orderService.orderCartItems(orderDtoList, userId);

        this.deleteByIdList(cartItemIdList);

        return orderId;
    }

    @Deprecated
    @Transactional
    public Long orderCartItem(List<CartOrderDto> cartOrderDtoList, String email) {
        List<OrderDto> orderDtoList = new ArrayList<>();

        for (CartOrderDto cartOrderDto : cartOrderDtoList) {
            CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartItemId())
                    .orElseThrow(EntityNotFoundException::new);

            OrderDto orderDto = new OrderDto();
            orderDto.setItemId(cartItem.getItem().getId());
            orderDto.setCount(cartItem.getCount());
            orderDtoList.add(orderDto);
        }

        Long orderId = orderService.orders(orderDtoList, email);

        for (CartOrderDto cartOrderDto : cartOrderDtoList) {
            CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartItemId())
                    .orElseThrow(EntityNotFoundException::new);
            cartItemRepository.delete(cartItem);
        }

        return orderId;

    }

    @Transactional
    public void updateCartItem(Long cartItemId, int count) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);

        cartItem.updateCount(count);
    }

    @Transactional
    public void deleteCartItem(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
        cartItemRepository.delete(cartItem);
    }

    @Transactional
    public void deleteByIdList(List<Long> idList) {
        cartItemRepository.deleteAllById(idList);
    }
}
