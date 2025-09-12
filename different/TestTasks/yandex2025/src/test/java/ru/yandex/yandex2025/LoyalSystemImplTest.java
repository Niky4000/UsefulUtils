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
import org.junit.Assert;
import org.junit.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import ru.yandex.yandex2025.bean.Purchase;

/**
 *
 * @author me
 */
public class LoyalSystemImplTest {
    
    @Test
    public void handleBucket() {
        Long customerId = 1L;
        LoyalSystemImpl loyalSystem = spy(LoyalSystemImpl.class);
        List<Purchase> purchaseList = List.of(new Purchase(customerId, BigDecimal.valueOf(100)),
                new Purchase(customerId, BigDecimal.valueOf(200)));
        doReturn(BigDecimal.valueOf(0.9)).when(loyalSystem).getDiscount(eq(customerId), any());
        
        List<Purchase> actual = loyalSystem.handleBucket(1L, purchaseList);
        
        Assert.assertTrue(actual.get(0).getFinalPrice().equals(BigDecimal.valueOf(90)));
        Assert.assertTrue(actual.get(1).getFinalPrice().equals(BigDecimal.valueOf(160)));
    }
    
    @Test
    public void handleBucket2() {
        Long customerId = 1L;
        LoyalSystemImpl loyalSystem = new LoyalSystemImpl();
        List<Purchase> purchaseList = List.of(new Purchase(customerId, BigDecimal.valueOf(100)),
                new Purchase(customerId, BigDecimal.valueOf(200)));
        
        List<Purchase> actual = loyalSystem.handleBucket(customerId, purchaseList);
        
        Assert.assertTrue(actual.get(0).getFinalPrice().equals(BigDecimal.valueOf(90.0)));
        Assert.assertTrue(actual.get(1).getFinalPrice().equals(BigDecimal.valueOf(180.0)));
    }
    
    @Test
    public void handleBucket3() {
        Long customerId = 3L;
        LoyalSystemImpl loyalSystem = new LoyalSystemImpl();
        List<Purchase> purchaseList = List.of(new Purchase(customerId, BigDecimal.valueOf(1, 2).divide(BigDecimal.valueOf(100, 2))));
        
        List<Purchase> actual = loyalSystem.handleBucket(customerId, purchaseList);
        
        Assert.assertTrue(actual.get(0).getFinalPrice().equals(BigDecimal.valueOf(0.01)));
    }
    
    @Test
    public void handleBucket4() {
        BigDecimal divide = BigDecimal.valueOf(100, 2).divide(BigDecimal.valueOf(3, 2), RoundingMode.HALF_UP);
        BigDecimal divider = BigDecimal.valueOf(1, 2).divide(BigDecimal.valueOf(70, 2));
        Assert.assertTrue(divide != null);
    }
}
