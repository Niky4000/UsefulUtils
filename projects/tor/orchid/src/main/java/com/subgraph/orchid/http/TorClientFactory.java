package com.subgraph.orchid.http;

import com.subgraph.orchid.TorClient;
import com.subgraph.orchid.TorInitializationListener;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Map;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.subgraph.orchid.DirectoryServer;
import com.subgraph.orchid.Tor;
import com.subgraph.orchid.data.HexDigest;
import com.subgraph.orchid.data.IPv4Address;
import com.subgraph.orchid.directory.DocumentFieldParserImpl;
import com.subgraph.orchid.directory.TrustedAuthorities;
import com.subgraph.orchid.directory.parsing.DocumentFieldParser;
import com.subgraph.orchid.directory.parsing.DocumentParsingHandler;
import com.subgraph.orchid.logging.Logger;
import java.lang.reflect.Array;
import java.lang.reflect.Method;

public class TorClientFactory {
    private static final Logger logger = Logger.getInstance(TorClientFactory.class);
    private static final int PROXY_PORT = 9150;
    private static final String PROXY_HOST = "localhost";
    private static TorClient client;
    private static boolean isStarting = false;
    private static boolean isRunning = false;
    
    public static TorClient getTorClient(){
        return client;
    }
    
    public static boolean hasOpenTorTunnel(){
        return isStarting || isRunning;
    }
    
    public static void openTunnel(){
        if(!isRunning){
            isStarting = true;
            if(TorCryptography.hasRestrictedCryptography()){
                TorCryptography.removeCryptographyRestrictions();
            }
//            
//            TrustedAuthorities.getInstance().getAuthorityServers().clear();
//            try{
//                TrustedAuthorities ta = TrustedAuthorities.getInstance();
//            Method method = ta.getClass().getDeclaredMethod("initialize");
//            method.setAccessible(true);
//Object r = method.invoke(ta);
//            } catch(Exception e){
//              System.out.println(e);  
//            }
//            List<DirectoryServer> activeAuthority = new ArrayList();
//            for(DirectoryServer ds: TrustedAuthorities.getInstance().getAuthorityServers()){
//                System.out.println(ds.getNickname());
//                if(!ds.getNickname().equals("Tonga") && !ds.getNickname().equals("urras")){
//                    activeAuthority.add(ds);
//                }
//            }
//            TrustedAuthorities.getInstance().getAuthorityServers().clear();
//            TrustedAuthorities.getInstance().getAuthorityServers().addAll(activeAuthority);
//            
//            
//            try{
//                Field f = TrustedAuthorities.class.getDeclaredField("v3ServerCount");
//                f.setAccessible(true);
//                int n = 0;
//                for(DirectoryServer ds: TrustedAuthorities.getInstance().getAuthorityServers()) {
//                    if(ds.getV3Identity() != null) {
//                        n += 1;
//                    }
//                }
//                f.set(TrustedAuthorities.getInstance(), n);
//            } catch(Exception e){
//                System.out.println(e);
//            }
            client = new TorClient();
            client.enableSocksListener(PROXY_PORT);
            client.addInitializationListener(createInitalizationListner());
            client.start();
            client.enableSocksListener();
        }
        while(!isRunning){
            try{
                Thread.sleep(1000l);
            } catch(Exception e){
                //swallow
            }
        }
    }
    
    public static Proxy getProxy(){
        return new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(PROXY_HOST, PROXY_PORT));
    }
    
    public static void closeTunnel(){
        while(isStarting){
            try{
                Thread.sleep(100l);
            } catch(Exception e){
                //swallow
            }
        }
        client.stop();
        client = null;
        isRunning = false;
        isStarting = false;
    }
    
    private static TorInitializationListener createInitalizationListner() {
        return new TorInitializationListener() {
            @Override
            public void initializationProgress(String message, int percent) {
                logger.info(">>> [ " + percent + "% ]: " + message);
            }

            @Override
            public void initializationCompleted() {
                logger.info("Tor is ready to go!");
                isRunning = true;
                isStarting = false;
            }
        };
    }
}