package com.revolute;

import com.revolute.exceptions.RegisterException;
import java.util.Collection;
import java.util.HashSet;

public class LoadBalanceService {

    private final int maxAmountOfAddresses;
    private final Collection<String> addresses; // http://someDomainName.com/somePage/
    // Map<String,ServerInstance>

    public LoadBalanceService(int maxAmountOfAddresses) {
        this.maxAmountOfAddresses = maxAmountOfAddresses;
        addresses = new HashSet<>();
//        new Socket(InetAddress.getByName("someHost"), 80);
//        new URL("");
    }

    public synchronized boolean registerNewAddress(String address) throws RegisterException {
        if (addresses.size() < maxAmountOfAddresses) {
            if (!addresses.add(address)) {
                throw new RegisterException("Address " + address + " is not unique!");
            }
            return true;
        } else {
            throw new RegisterException("Service is overloaded!");
        }
    }

    public synchronized boolean check(String address) {
        return addresses.contains(address); // set ~ O(1) // list ~ O(N) // NavigableSet ~ O(log(N))
    }
}
