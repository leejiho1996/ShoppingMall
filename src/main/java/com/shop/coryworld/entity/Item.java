package com.shop.coryworld.entity;

import com.shop.coryworld.constant.ItemSellStatus;
import com.shop.coryworld.entity.base.BaseEntity;
import com.shop.coryworld.exception.OutOfStockException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@ToString
@Table(name = "item")
@Setter
public class Item extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String itemName;

    @Column(name="price", nullable = false)
    private int price;

    @Column(name="likes", nullable = false)
    private int like = 0;

    @Lob
    @Column(nullable = false)
    private String itemDetail;

    @Column(nullable = false)
    private int stockNumber;

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus;

    @OneToMany(mappedBy = "item", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ItemImg> itemImgList = new ArrayList<>();

    // 상품 수정
    public void update(String name, int price, int stockNumber, String itemDetail, ItemSellStatus itemSellStatus) {
        this.itemName = name;
        this.price = price;
        this.stockNumber = stockNumber;
        this.itemDetail = itemDetail;
        this.itemSellStatus = itemSellStatus;
    }

    // 상품 갯수 조정
    public void removeStock(int stockNumber) {
        int restStock = this.stockNumber - stockNumber;

        if (restStock < 0) {
            throw new OutOfStockException("재고가 부족합니다. (현재 재고 수량 : " + this.stockNumber + ")");
        }

        this.stockNumber = restStock;
    }

    public void addStock(int stockNumber) {
        this.stockNumber += stockNumber;
    }

}
