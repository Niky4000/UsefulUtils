package com.revolute;

import com.revolute.exceptions.RegisterException;
import org.junit.Assert;
import org.junit.Test;

public class LoadBalanceServiceTest {

    @Test
    public void registerNewAddressTest() {
        LoadBalanceService loadBalancer = new LoadBalanceService(2);
        loadBalancer.registerNewAddress("str1");
        try {
            loadBalancer.registerNewAddress("str1");
            Assert.assertTrue(false);
        } catch (RegisterException ex) {
            Assert.assertTrue(ex.getMessage().equals("Address str1 is not unique!"));
        }
    }

    @Test
    public void registerNewAddressTest2() {
        LoadBalanceService loadBalancer = new LoadBalanceService(2);
        loadBalancer.registerNewAddress("str1");
        loadBalancer.registerNewAddress("str2");
        try {
            loadBalancer.registerNewAddress("str3");
            Assert.assertTrue(false);
        } catch (RegisterException ex) {
            Assert.assertTrue(ex.getMessage().equals("Service is overloaded!"));
        }
    }

    @Test
    public void checkTest() {
        LoadBalanceService loadBalancer = new LoadBalanceService(2);
        final String address1 = "str1"; // uidS() // random String..
        final String address2 = "str2";
        final String address3 = "str3";
        loadBalancer.registerNewAddress(address1);
        loadBalancer.registerNewAddress(address2);
        // assertSoftly(s->s.assertThat...);
        Assert.assertTrue(loadBalancer.check(address1));
        Assert.assertTrue(loadBalancer.check(address2));
        Assert.assertTrue(!loadBalancer.check(address3));
    }
}
