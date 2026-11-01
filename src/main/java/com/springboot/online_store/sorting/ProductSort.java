package com.springboot.online_store.sorting;

import com.springboot.online_store.enums.SortType;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class ProductSort {
    public Sort getSort(SortType sortType) {
        if (sortType == null) {
            return Sort.unsorted();
        }

        return switch (sortType) {
            case PRICE_ASC ->
                    Sort.by("price").ascending();

            case PRICE_DESC ->
                    Sort.by("price").descending();

            case NEWEST ->
                    Sort.by("createdTime").descending();

            case NAME_ASC ->
                    Sort.by("name").ascending();
        };
    }
}
