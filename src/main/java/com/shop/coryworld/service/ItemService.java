package com.shop.coryworld.service;

import com.shop.coryworld.auth.PrincipalDetails;
import com.shop.coryworld.entity.Item;
import com.shop.coryworld.entity.ItemImg;
import com.shop.coryworld.exception.InvalidImageException;
import com.shop.coryworld.exception.NoAuthorizationException;
import com.shop.coryworld.repository.ItemImgRepository;
import com.shop.coryworld.repository.ItemRepository;
import com.shop.coryworld.dto.ItemFormDto;
import com.shop.coryworld.dto.ItemImgDto;
import com.shop.coryworld.dto.ItemSearchDto;
import com.shop.coryworld.dto.MainItemDto;
import com.shop.coryworld.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ItemService {

    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final ItemImgService itemImgService;
    private final ItemImgRepository itemImgRepository;
    private final FileService fileService;

    @Transactional
    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {

        Item item = itemFormDto.createItem();
        item.setLike(0);
        log.info("item : {}", item);
        itemRepository.save(item);

        for (int i = 0; i < itemImgFileList.size(); i++) {
            ItemImg itemImg = new ItemImg(item);
            if (i == 0) {
                itemImg.setRepImgYn("Y");
            } else {
                itemImg.setRepImgYn("N");
            }
            itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));
        }

        return item.getId();
    }

    public ItemFormDto getItemDtl(Long itemId) {

        Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);
        ItemFormDto itemFormDto = ItemFormDto.of(item);

        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);
        List<ItemImgDto> itemImgDtoList = new ArrayList<>();

        for (ItemImg itemImg : itemImgList) {
            ItemImgDto itemImgDto = ItemImgDto.of(itemImg);
            itemImgDtoList.add(itemImgDto);
        }
        // 찾아온 itemImg set
        itemFormDto.setItemImgDtoList(itemImgDtoList);

        return itemFormDto;
    }

    @Transactional
    public Long updateItem (ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {

        if (itemImgFileList == null || itemImgFileList.isEmpty() || itemImgFileList.get(0).isEmpty()) {
            throw new InvalidImageException("첫 번째 상품 이미지는 필수입니다.");
        }

        if (!fileService.checkImgFile(itemImgFileList.get(0))) {
            throw new InvalidImageException("지원하지 않는 이미지 형식입니다.");
        }

        // 상품 수정
        Item item = itemRepository.findById(itemFormDto.getId()).orElseThrow(EntityNotFoundException::new);
        item.update(itemFormDto);

        List<Long> itemImgIds = itemFormDto.getItemImgIds();

        // 이미지 등록
        for (int i = 0; i < itemImgFileList.size(); i++) {
            itemImgService.updateItemImg(itemImgIds.get(i), itemImgFileList.get(i));
        }

        return item.getId();
    }

    public Page<Item> getAdminItemPage(ItemSearchDto itemsearchDto, Pageable pageable) {
        return itemRepository.getAdminItemPage(itemsearchDto, pageable);
    }

    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable, PrincipalDetails user) {
        Long memberId = -1L;

        if (user != null) {
            memberId = user.getId();
        }

        return itemRepository.getMainItemPage(itemSearchDto, pageable, memberId);
    }

    @Transactional
    public void deleteItem(Long itemId, User user) {
        if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new NoAuthorizationException("권한이 없습니다.");
        }
        itemRepository.deleteById(itemId);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Item findItem(Boolean eventMode, Long itemId) {
        return (eventMode
                ? itemRepository.findByIdForUpdate(itemId)
                : itemRepository.findById(itemId))
                .orElseThrow(EntityNotFoundException::new);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Item> findItemPessimistic(List<Long> itemIds) {
        return itemRepository.findItemByIdListForUpdate(itemIds);
    }
}
