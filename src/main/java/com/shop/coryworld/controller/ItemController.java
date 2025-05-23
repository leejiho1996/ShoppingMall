package com.shop.coryworld.controller;

import com.shop.coryworld.entity.Item;
import com.shop.coryworld.repository.ItemRepository;
import com.shop.coryworld.dto.ItemFormDto;
import com.shop.coryworld.dto.ItemSearchDto;
import com.shop.coryworld.service.ItemService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;
    private final ItemRepository itemRepository;

    @GetMapping("/admin/item/new")
    public String itemForm(@ModelAttribute ItemFormDto itemFormDto, @AuthenticationPrincipal User user) {
        return "item/itemForm";
    }

    // 아이템 추가
    @PostMapping(value = "/admin/item/new")
    public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult bindingResult, Model model,
                          @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList,
                          @AuthenticationPrincipal User user) {

        if (bindingResult.hasErrors()) {
            return "item/itemForm";
        }

        if (itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null) {
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값 입니다.");
            return "item/itemForm";
        }

        try {
            itemService.saveItem(itemFormDto, itemImgFileList);
            } catch (Exception e) {
                log.info("에러 메시지 : {}", e.getMessage());
                model.addAttribute("errorMessage", "상품 등록 중 오류가 발생하였습니다. 다시 시도해 주세요.");
                return "item/itemForm";
            }

        return "redirect:/";
    }

    // 아이템 상세정보
    @GetMapping("/admin/item/{itemId}")
    public String itemDtl(@PathVariable("itemId") Long itemId, Model model) {

        try {
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            model.addAttribute("itemFormDto", itemFormDto);
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", "존재하지 않는 상품입니다.");
            model.addAttribute("itemFormDto", new ItemFormDto());
            return "item/itemForm";
        }

        return "item/itemForm";
    }

    // 아이템 수정
    @PostMapping(value = "/admin/item/{itemId}")
    public String itemUpdate(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                             @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList, Model model,
                             @AuthenticationPrincipal User user) {

        if (bindingResult.hasErrors()) {
            return "redirect:/admin/item/" + itemFormDto.getId();
        }

        if (itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null) {
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값 입니다.");
            return "item/itemForm";
        }

        try {
            itemService.updateItem(itemFormDto, itemImgFileList);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "존재하지 않는 상품입니다.");
        }

        return "redirect:/";
    }

    // 아이템 삭제
    @PostMapping("/admin/item/delete/{itemId}")
    public String deleteItem(@PathVariable Long itemId, @AuthenticationPrincipal User user) {
        itemService.deleteItem(itemId, user);
        return "redirect:/";
    }

    // 아이템 관리창
    @GetMapping( value = {"/admin/items", "/admin/items/{page}"})
    public String itemManage(@ModelAttribute ItemSearchDto itemSearchDto, @PathVariable("page") Optional<Integer> page,
                             Model model) {
        if (itemSearchDto == null) {
            itemSearchDto = new ItemSearchDto();
        }

        PageRequest pageable = PageRequest.of(page.orElse(0), 5);
        Page<Item> items = itemService.getAdminItemPage(itemSearchDto, pageable);

        model.addAttribute("items", items);
        model.addAttribute("maxPage", 5);
        return "item/itemMng";
    }

    // 아이템 상세정보 (고객이 상품 눌렀을 때)
    @GetMapping("/item/{itemId}")
    public String itemDtl(Model model, @PathVariable("itemId") Long itemId) {
        ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
        model.addAttribute("item", itemFormDto);
        return "item/itemDtl";
    }

}
