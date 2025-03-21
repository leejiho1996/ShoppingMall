package com.shop.coryworld.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter @Setter
public class PageImplWithTotal<T> {

    Page<T> page;
    int total;

    public PageImplWithTotal(Page<T> page, int total) {
        this.page = page;
        this.total = total;
    }
}
