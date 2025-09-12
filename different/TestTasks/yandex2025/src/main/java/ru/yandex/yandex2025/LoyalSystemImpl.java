/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.yandex.yandex2025;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import ru.yandex.yandex2025.bean.Purchase;

/**
 *
 * @author me
 */
public class LoyalSystemImpl implements LoyalSystem {

    @Override
    public List<Purchase> handleBucket(Long customerId, List<Purchase> bucket) {
        for (Purchase purchase : bucket) {
            BigDecimal discount = getDiscount(customerId, purchase);
            purchase.setFinalPrice(purchase.getPrice().multiply(discount, new MathContext(2, RoundingMode.HALF_UP)));
        }
        return bucket;
    }

    Map<Long, Integer> discountMap = Map.of(1L, 10, 2L, 15, 3L, 30);

    public LoyalSystemImpl() {
    }

    public LoyalSystemImpl(Map<Long, Integer> discountMap) {
        this.discountMap = discountMap;
    }

    BigDecimal getDiscount(Long customerId, Purchase purchase) {
        Integer discount = discountMap.getOrDefault(customerId, 0);
        return BigDecimal.valueOf(100 - discount, 2).divide(BigDecimal.valueOf(100, 2), RoundingMode.HALF_UP);
    }
}
