///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package ru.ibs.pmp.apacheminatest;
//
//import java.io.ByteArrayOutputStream;
//import java.io.PipedInputStream;
//import java.io.PipedOutputStream;
//import java.net.SocketAddress;
//import org.apache.sshd.client.SshClient;
//import org.apache.sshd.client.channel.ClientChannel;
//import org.apache.sshd.client.session.ClientSession;
//import org.apache.sshd.common.util.buffer.BufferUtils;
//
///**
// *
// * @author NAnishhenko
// */
//public class ApacheMina2 {
//
//    public void testClientWithLengthyDialog(SocketAddress port) throws Exception {
//        SshClient client = SshClient.setUpDefaultClient();
//        // Reduce window size and packet size
////        client.getProperties().put(SshClient.WINDOW_SIZE, Integer.toString(0x20000));
////        client.getProperties().put(SshClient.MAX_PACKET_SIZE, Integer.toString(0x1000));
////        sshd.getProperties().put(SshServer.WINDOW_SIZE, Integer.toString(0x20000));
////        sshd.getProperties().put(SshServer.MAX_PACKET_SIZE, Integer.toString(0x1000));
//        client.start();
//        ClientSession session = client.connect("localhost", port).await().getSession();
//        session.authPassword("smx", "smx");
//        ClientChannel channel = session.createChannel(ClientChannel.CHANNEL_SHELL);
//        ByteArrayOutputStream sent = new ByteArrayOutputStream();
//        PipedOutputStream pipedIn = new TeePipedOutputStream(sent);
//        channel.setIn(new PipedInputStream(pipedIn));
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        ByteArrayOutputStream err = new ByteArrayOutputStream();
//        channel.setOut(out);
//        channel.setErr(err);
//        channel.open().await();
//
//        long t0 = System.currentTimeMillis();
//
//        int bytes = 0;
//        for (int i = 0; i < 10000; i++) {
//            byte[] data = "01234567890123456789012345678901234567890123456789\n".getBytes();
//            pipedIn.write(data);
//            pipedIn.flush();
//            bytes += data.length;
//            if ((bytes & 0xFFF00000) != ((bytes - data.length) & 0xFFF00000)) {
//                System.out.println("Bytes written: " + bytes);
//            }
//        }
//        pipedIn.write("exit\n".getBytes());
//        pipedIn.flush();
//
//        long t1 = System.currentTimeMillis();
//
//        System.out.println("Sent " + (bytes / 1024) + " Kb in " + (t1 - t0) + " ms");
//
//        System.out.println("Waiting for channel to be closed");
//
//        channel.waitFor(ClientChannel.CLOSED, 0);
//
//        channel.close(false);
//        client.stop();
//
//        assertTrue(BufferUtils.equals(sent.toByteArray(), out.toByteArray()));
//        //assertArrayEquals(sent.toByteArray(), out.toByteArray());
//    }
//
//}
