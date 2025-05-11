package com.shop.coryworld.service;

import com.shop.coryworld.constant.ItemSellStatus;
import com.shop.coryworld.dto.OrderDto;
import com.shop.coryworld.entity.Item;
import com.shop.coryworld.entity.ItemImg;
import com.shop.coryworld.entity.Member;
import com.shop.coryworld.repository.ItemImgRepository;
import com.shop.coryworld.repository.ItemRepository;
import com.shop.coryworld.repository.MemberRepository;
import com.shop.coryworld.repository.OrderRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
@Slf4j
@Transactional
class OrderServiceTest {

    @Autowired
    private  ItemRepository itemRepository;

    @Autowired
    private OrderService orderService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        itemRepository.save(Item.getTestItem());
        entityManager.flush();

        for (long userId = 1; userId <= 100; userId++) {
            Member dummy = Member.getDummyMember(userId);
            memberRepository.save(dummy);
        }
        entityManager.flush();

        // 테스트 트랜잭션을 커밋하고 새로 시작
        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestTransaction.start();
    }

    @Test
    void orderTest() throws Exception {

        List<Item> allById = itemRepository.findAllById(List.of(1L));
        log.info("saveId = {}", allById);

        final int purchaseAttempts = 100;
        ExecutorService executor = Executors.newFixedThreadPool(purchaseAttempts);
        CountDownLatch readyLatch = new CountDownLatch(purchaseAttempts);
        CountDownLatch startLatch = new CountDownLatch(1);

        List<Future<Boolean>> futures = new ArrayList<>();
        for (int i = 0; i < purchaseAttempts; i++) {
            final long userId = i + 1;
            futures.add(executor.submit(() -> {
                // 스레드 준비 완료
                readyLatch.countDown();
                // 모두 준비될 때까지 대기
                startLatch.await();

                try {
                    // 한 건씩만 주문
                    orderService.orderCartItems(
                            List.of(new OrderDto(1L, 1)), userId
                    );
                    return true;   // 성공
                } catch (Exception ex) {
                    log.info("예외발생: {}", ex);
                    return false;  // 재고 부족 등 실패
                }
            }));
        }

        // 모든 스레드 준비 완료 후 동시에 시작
        readyLatch.await();
        startLatch.countDown();

        // 결과 집계
        int successCount = 0;
        for (Future<Boolean> f : futures) {
            if (f.get(5, TimeUnit.SECONDS)) {
                successCount++;
            }
        }

        // 1) 성공 주문 수가 20건이어야 한다
        assertEquals(20, successCount,
                "동시성 상황에도 최대 20명만 주문에 성공해야 합니다");

//        // 2) DB 재고는 20 - 20 = 0
//        Item item = itemRepository.findById(1L).orElseThrow();
//        assertEquals(0, item.getStockNumber(),
//                "최종 재고가 0이어야 합니다");

        executor.shutdownNow();
    }
}