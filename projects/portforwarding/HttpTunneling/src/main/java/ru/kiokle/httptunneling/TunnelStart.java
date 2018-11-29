package ru.kiokle.httptunneling;

import com.some.tcp.TCPForwardServer;
import com.some.tcp.TCPForwardServerR;

/**
 * @author NAnishhenko
 */
public class TunnelStart {

    public static void main(String[] args) throws Exception {
        if (args[0].equals("L")) {
//        new TCPForwardServer().init(22888, "192.168.192.215", 22);
            new TCPForwardServer().init(Integer.valueOf(args[1]), args[2], Integer.valueOf(args[3]));
        } else if (args[0].equals("R")) {
//            new TCPForwardServerR().init(22888, "172.29.4.26", 22);
            new TCPForwardServerR().init(Integer.valueOf(args[1]), args[2], Integer.valueOf(args[3]));
        }
    }
}
