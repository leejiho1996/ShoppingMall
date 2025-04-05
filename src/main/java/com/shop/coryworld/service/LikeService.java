package com.shop.coryworld.service;

import com.shop.coryworld.entity.*;
import com.shop.coryworld.exception.NoAuthorizationException;
import com.shop.coryworld.repository.ItemRepository;
import com.shop.coryworld.repository.LikeRepository;
import com.shop.coryworld.repository.MemberRepository;
import com.shop.coryworld.dto.LikeItemDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeService {
    private final MemberRepository memberRepository;
    private final LikeRepository likeRepository;
    private final ItemRepository itemRepository;

    // 찜하기 추가
    @Transactional
    public Long addLike(Long itemId, Long memberId) {

        Item item = itemRepository.findById(itemId)
                .orElseThrow(EntityNotFoundException::new);

        // addLike를 이중으로 호출 하게되면 잘못된 접근으로 예외 처리
        likeRepository.findByMemberAndItem(memberId, itemId)
                .ifPresent(ex -> {
                    throw new NoAuthorizationException("잘못된 접근입니다.");
                });

        // 쿼리 호출 필요없이 id만 세팅 후 like 저장
        Member dummyMember = Member.getDummyMember(memberId);
        Like newLike = Like.createLike(dummyMember, item);
        item.addLike();
        likeRepository.save(newLike);

        return newLike.getId();
    }

    // 찜하기 해제
    @Transactional
    public void deleteLike(Long likeItemId, Long userId) {
//        if (!validateLikeItem(likeItemId, loggedUserId)) {
//            throw new NoAuthorizationException("권한이 없습니다");
//        };

        Like likeItem = likeRepository.findByMemberAndItem(userId, likeItemId)
                .orElseThrow(EntityNotFoundException::new);

        likeItem.getItem().removeLike();
        likeRepository.delete(likeItem);
    }

    /**
     * 컨트롤러로부터 현재 로그인된 userId와 ItemId를 받아와서
     * 찜한 사용자와 현재 요청한 사용자가 같은지 확인
     */
    public Boolean validateLikeItem(Long likeId, String email) {
        Member curMember = memberRepository.findByEmail(email);
        Like like = likeRepository.findById(likeId)
                .orElseThrow(EntityNotFoundException::new);

        Member savedMember = like.getMember();

        if (curMember.getId() != savedMember.getId()) {
            return false;
        }
        return true;
    }
}
