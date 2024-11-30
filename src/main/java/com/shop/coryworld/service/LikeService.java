package com.shop.coryworld.service;

import com.shop.coryworld.entity.*;
import com.shop.coryworld.repository.ItemRepository;
import com.shop.coryworld.repository.LikeItemRepository;
import com.shop.coryworld.repository.LikeRepository;
import com.shop.coryworld.repository.MemberRepository;
import com.shop.coryworld.repository.dto.LikeItemDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final MemberRepository memberRepository;
    private final LikeItemRepository likeItemRepository;
    private final LikeRepository likeRepository;
    private final ItemRepository itemRepository;

    public Long addLike(LikeItemDto likeItemDto, String email) {

        Item item = itemRepository.findById(likeItemDto.getId())
                .orElseThrow(EntityNotFoundException::new);

        Member member = memberRepository.findByEmail(email);
        Like like = likeRepository.findByMemberId(member.getId());

        if (like == null) {
            like = Like.createLike(member);
            likeRepository.save(like);
        }

        LikeItem likeItem = LikeItem.createLikeItem(like, item);
        likeItemRepository.save(likeItem);

        return likeItem.getId();
    }

    public void deleteLikeItem(Long likeItemId) {
        LikeItem likeItem = likeItemRepository.findById(likeItemId)
                .orElseThrow(EntityNotFoundException::new);
        likeItemRepository.delete(likeItem);
    }


    /**
     * 컨트롤러로부터 현재 로그인된 eamil과 likeItemId를 받아와서
     * 찜한 사용자와 현재 요청한 사용자가 같은지 확인
     */
    @Transactional(readOnly = true)
    public Boolean validateLikeItem(Long likeItemId, String email) {
        Member curMember = memberRepository.findByEmail(email);
        LikeItem likeItem = likeItemRepository.findById(likeItemId)
                .orElseThrow(EntityNotFoundException::new);
        Member savedMember = likeItem.getLike().getMember();

        if (curMember != savedMember) {
            return false;
        }
        return true;
    }
}
