/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.yandex.yandex2025;

import java.util.List;
import ru.yandex.yandex2025.bean.Purchase;

/**
 *
 * @author me
 */
public interface LoyalSystem {

    List<Purchase> handleBucket(Long customerId, List<Purchase> bucket);
}
