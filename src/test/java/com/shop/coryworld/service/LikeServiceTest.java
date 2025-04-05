package com.shop.coryworld.service;

import com.shop.coryworld.config.WebMvcConfig;
import com.shop.coryworld.entity.Item;
import com.shop.coryworld.entity.Like;
import com.shop.coryworld.entity.Member;
import com.shop.coryworld.repository.ItemRepository;
import com.shop.coryworld.repository.LikeRepository;
import com.shop.coryworld.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations="classpath:application-test.properties")
class LikeServiceTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ItemRepository itemRepository;

    private final ArrayList<Long> likeIds = new ArrayList<>();
    private final ArrayList<Long> memberIds = new ArrayList<>();

    @BeforeEach
    void setUp() {

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void validateLikeItem() {
        System.out.println("=====================테스트 스타트======================");
        Member curMember = memberRepository.findById(memberIds.get(0)).get();

        Like curLike = likeRepository.findById(curMember.getId()).get();

        curLike.getMember().getId();

    }
}