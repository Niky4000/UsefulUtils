/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.my.portmapper;

import com.offbynull.portmapper.PortMapperFactory;
import com.offbynull.portmapper.gateway.Bus;
import com.offbynull.portmapper.gateway.Gateway;
import com.offbynull.portmapper.gateways.network.NetworkGateway;
import com.offbynull.portmapper.gateways.network.internalmessages.KillNetworkRequest;
import com.offbynull.portmapper.gateways.process.ProcessGateway;
import com.offbynull.portmapper.gateways.process.internalmessages.KillProcessRequest;
import com.offbynull.portmapper.mapper.MappedPort;
import com.offbynull.portmapper.mapper.PortMapper;
import com.offbynull.portmapper.mapper.PortType;
import com.offbynull.portmapper.mappers.pcp.PcpPortMapper;
import com.offbynull.portmapper.mappers.upnpigd.PortMapperUpnpIgdPortMapper;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.List;

/**
 *
 * @author NAnishhenko
 */
public class MyPortMapper {

    public static void main(String[] args) throws InterruptedException, UnknownHostException {
        System.out.println("Started...");
        boolean shutdown = false;

// Start gateways
        Gateway network = NetworkGateway.create();
        Gateway process = ProcessGateway.create();
        Bus networkBus = network.getBus();
        Bus processBus = process.getBus();

// Discover port forwarding devices and take the first one found
//        List<PortMapper> mappers = PortMapperFactory.discover(networkBus, processBus,Inet4Address.getByName("172.29.4.25"));
//        List<PcpPortMapper> mappers = PcpPortMapper.identify(networkBus, processBus, Inet4Address.getByName("172.29.4.25"));
//        PortMapper mapper = mappers.get(0);
        
        PortMapper portMapper=new PortMapperUpnpIgdPortMapper(networkBus, Inet4Address.getByName("172.29.4.25"), null, null, null, null, null, false);
        

// Map internal port 12345 to some external port (55555 preferred)
//
// IMPORTANT NOTE: Many devices prevent you from mapping ports that are <= 1024
// (both internal and external ports). Be mindful of this when choosing which
// ports you want to map.
        MappedPort mappedPort = portMapper.mapPort(PortType.TCP, 12345, 55555, 60);
        System.out.println("Port mapping added: " + mappedPort);

// Refresh mapping half-way through the lifetime of the mapping (for example,
// if the mapping is available for 40 seconds, refresh it every 20 seconds)
        while (!shutdown) {
            mappedPort = portMapper.refreshPort(mappedPort, mappedPort.getLifetime() / 2L);
            System.out.println("Port mapping refreshed: " + mappedPort);
            Thread.sleep(mappedPort.getLifetime() * 1000L);
        }

// Unmap port 12345
        portMapper.unmapPort(mappedPort);

// Stop gateways
        networkBus.send(new KillNetworkRequest());
        processBus.send(new KillProcessRequest()); // can kill this after discovery
    }
}
