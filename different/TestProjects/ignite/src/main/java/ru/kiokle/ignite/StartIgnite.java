/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kiokle.ignite;

import java.util.Collections;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteCluster;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;
import org.apache.ignite.client.ClientCache;
import org.apache.ignite.client.IgniteClient;
import org.apache.ignite.configuration.ClientConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.lang.IgniteRunnable;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.spi.IgniteSpiException;
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;

/**
 *
 * @author me
 */
public class StartIgnite {

    public static void main(String[] args) throws Exception {
        System.out.println("Hello World from Ignite!");
        StartIgnite startIgnite = new StartIgnite();
        String ipRange = "127.0.0.1:48500..48520";
        try (Ignite ignite = startIgnite.createServerInstance(ipRange, "first", 48500, 48100)) {
            try (Ignite ignite2 = startIgnite.createServerInstance(ipRange, "second", 48500, 48100)) {
                try (Ignite ignite3 = startIgnite.createServerInstance(ipRange, "third", 48500, 48100)) {
                    try (Ignite ignite4 = startIgnite.createServerInstance(ipRange, "fourth", 48500, 48100)) {
                        IgniteCluster cluster = ignite.cluster();
                        int totalNodes = cluster.metrics().getTotalNodes();
                        System.out.println("totalNodes = " + totalNodes);
                        IgniteCache<Object, Object> cache = ignite.getOrCreateCache("cache");
                        cache.put("1", "2");
                        String get = (String) cache.get("1");
                        System.out.println("Value1 = " + get);
                        IgniteCache<Object, Object> cache2 = ignite2.getOrCreateCache("cache");
                        String get2 = (String) cache2.get("1");
                        System.out.println("Value2 = " + get2);
                    }
                    IgniteCluster cluster = ignite.cluster();
                    int totalNodes = cluster.metrics().getTotalNodes();
                    System.out.println("totalNodes = " + totalNodes);
                }
                IgniteCluster cluster = ignite.cluster();
                int totalNodes = cluster.metrics().getTotalNodes();
                System.out.println("totalNodes = " + totalNodes);
            }
            IgniteCluster cluster = ignite.cluster();
            int totalNodes = cluster.metrics().getTotalNodes();
            System.out.println("totalNodes = " + totalNodes);
        }

//        IgniteConfiguration cfg = new IgniteConfiguration();
//        try (Ignite ignite = Ignition.start(cfg)) {
//            IgniteConfiguration cfg2 = new IgniteConfiguration();
//            // Enable client mode.
//            cfg2.setClientMode(true);
//            // Start a client
//            Ignite igniteClient = Ignition.start(cfg2);
//        }
    }

    public Ignite createServerInstance(String ipRange, String instanceName, int initialLocalPort, int localPort) {
        IgniteConfiguration firstCfg = new IgniteConfiguration();
        firstCfg.setIgniteInstanceName(instanceName);
        // Explicitly configure TCP discovery SPI to provide list of initial nodes
        // from the first cluster.
        TcpDiscoverySpi firstDiscoverySpi = new TcpDiscoverySpi();
        // Initial local port to listen to.
        firstDiscoverySpi.setLocalPort(initialLocalPort);
        // Changing local port range. This is an optional action.
        firstDiscoverySpi.setLocalPortRange(20);
        TcpDiscoveryVmIpFinder firstIpFinder = new TcpDiscoveryVmIpFinder();
        // Addresses and port range of the nodes from the first cluster.
        // 127.0.0.1 can be replaced with actual IP addresses or host names.
        // The port range is optional.
        firstIpFinder.setAddresses(Collections.singletonList(ipRange));
        // Overriding IP finder.
        firstDiscoverySpi.setIpFinder(firstIpFinder);
        // Explicitly configure TCP communication SPI by changing local port number for
        // the nodes from the first cluster.
        TcpCommunicationSpi firstCommSpi = new TcpCommunicationSpi();
        firstCommSpi.setLocalPort(localPort);
        // Overriding discovery SPI.
        firstCfg.setDiscoverySpi(firstDiscoverySpi);
        // Overriding communication SPI.
        firstCfg.setCommunicationSpi(firstCommSpi);
        // Starting a node.
        Ignite ignite = Ignition.start(firstCfg);
        return ignite;
    }

//    public void createClientInstance2(String ipRange) throws IgniteException {
//        ClientConfiguration cfg = new ClientConfiguration().setAddresses(ipRange);
//        try (IgniteClient client = Ignition.startClient(cfg)) {
//            System.out.println("Hello World!");
////                ClientCache<Integer, String> cache = client.cache("myCache");
//            // Get data from the cache
//        }
//    }
    public void createClientInstance(String ipRange) throws IgniteException {
        // Preparing IgniteConfiguration using Java APIs
        IgniteConfiguration cfg = new IgniteConfiguration();
        // The node will be started as a client node.
        cfg.setClientMode(true);
        // Classes of custom Java logic will be transferred over the wire from this app.
        cfg.setPeerClassLoadingEnabled(true);
        // Setting up an IP Finder to ensure the client can locate the servers.
        TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
        ipFinder.setAddresses(Collections.singletonList(ipRange));
        cfg.setDiscoverySpi(new TcpDiscoverySpi().setIpFinder(ipFinder));
        // Starting the node
        Ignite ignite = Ignition.start(cfg);
        // Create an IgniteCache and put some values in it.
        IgniteCache<Integer, String> cache = ignite.getOrCreateCache("myCache");
        cache.put(1, "Hello");
        cache.put(2, "World!");
        System.out.println(">> Created the cache and add the values.");
        // Executing custom Java compute task on server nodes.
        ignite.compute(ignite.cluster().forServers()).broadcast(new RemoteTask());
        System.out.println(">> Compute task is executed, check for output on the server nodes.");
        // Disconnect from the cluster.
        ignite.close();
    }

    /**
     * A compute tasks that prints out a node ID and some details about its OS
     * and JRE. Plus, the code shows how to access data stored in a cache from
     * the compute task.
     */
    private static class RemoteTask implements IgniteRunnable {

        @IgniteInstanceResource
        Ignite ignite;

        @Override
        public void run() {
            System.out.println(">> Executing the compute task");
            System.out.println(
                    "   Node ID: " + ignite.cluster().localNode().id() + "\n"
                    + "   OS: " + System.getProperty("os.name")
                    + "   JRE: " + System.getProperty("java.runtime.name"));
            IgniteCache<Integer, String> cache = ignite.cache("myCache");
            System.out.println(">> " + cache.get(1) + " " + cache.get(2));
        }
    }
}
