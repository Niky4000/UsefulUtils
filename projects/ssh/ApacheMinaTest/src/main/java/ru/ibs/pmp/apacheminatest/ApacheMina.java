/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.pmp.apacheminatest;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import static java.lang.Compiler.command;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ChannelExec;
import org.apache.sshd.client.future.ConnectFuture;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.CommandFactory;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.auth.password.PasswordChangeRequiredException;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.scp.ScpCommandFactory;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.shell.ProcessShellFactory;

/**
 *
 * @author NAnishhenko
 */
public class ApacheMina {

    private static final int BUFFER_SIZE = 4096;

//    public static void main(String args[]) throws Exception {
//        ApacheMina apacheMina = new ApacheMina();
//        apacheMina.createSshServer();
////        apacheMina.createMinaSshClient();
//        apacheMina.createSshClient();
//        System.out.println("Hello!");
//    }

    public void createSshClient() throws Exception {
        JSch jsch = new JSch();
        String user = "hello";
        String password = "world";
        String host = "localhost";
        Session session = jsch.getSession(user, host, 10000);
        session.setPassword(password);

        UserInfo ui = new MyUserInfo("");
        session.setUserInfo(ui);

        session.connect();

//        channel.connect();
//        execCommand(session.openChannel("exec"), "D:");
//        execCommand(session.openChannel("exec"), "cd D:\\tmp");
//        execCommand((ChannelShell) session.openChannel("shell"), new String[]{"cd D:\\tmp", "mkdir zzzxx0", "dir"});
//        Channel channel = session.openChannel("exec");
//        ((com.jcraft.jsch.ChannelExec) channel).setEnv("M2_HOME", "C:\\apache-maven-3.3.3");
//        execCommand(channel, "mvn -f D:\\GIT\\pmp\\pmp_core -Pdev,dist -T 8 -D skipTests=true clean install");
        execCommand(session.openChannel("exec"), "mkdir D:\\tmp\\zzzz0");
        execCommand(session.openChannel("exec"), "mkdir D:\\tmp\\zzzz1");
        execCommand(session.openChannel("exec"), "mkdir D:\\tmp\\zzzz2");
        session.disconnect();
    }

    private void execCommand(ChannelShell channel, String[] commands) throws Exception {

//        channel.setOutputStream(System.out);
        OutputStream ops = channel.getOutputStream();
        PrintStream ps = new PrintStream(ops, true);
//        channel.connect();
//        channel.start();
        for (String command : commands) {
            ps.println(command);
        }

//        channel.setInputStream(null);
        InputStream in = channel.getInputStream();
        channel.connect();
        channel.start();
        byte[] tmp = new byte[1024];
        while (true) {
            while (in.available() > 0) {
                int i = in.read(tmp, 0, 1024);
                if (i < 0) {
                    break;
                }
                System.out.print(new String(tmp, 0, i));
            }
            if (channel.isClosed()) {
                if (in.available() > 0) {
                    continue;
                }
                System.out.println("exit-status: " + channel.getExitStatus());
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
        ps.close();
        channel.disconnect();
    }

    private void execCommand(Channel channel, String command) throws JSchException, IOException {
        ((com.jcraft.jsch.ChannelExec) channel).setCommand(command);
        channel.setInputStream(null);
        ((com.jcraft.jsch.ChannelExec) channel).setErrStream(System.err);
        InputStream in = channel.getInputStream();
        channel.connect();
        byte[] tmp = new byte[1024];
        while (true) {
            while (in.available() > 0) {
                int i = in.read(tmp, 0, 1024);
                if (i < 0) {
                    break;
                }
                System.out.print(new String(tmp, 0, i));
            }
            if (channel.isClosed()) {
                if (in.available() > 0) {
                    continue;
                }
                System.out.println("exit-status: " + channel.getExitStatus());
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
        channel.disconnect();
    }

    public void createSshServer() throws IOException {
        SshServer sshServer = SshServer.setUpDefaultServer();
        sshServer.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());
        sshServer.setPasswordAuthenticator(new PasswordAuthenticator() {

            @Override
            public boolean authenticate(String username, String password, ServerSession session) throws PasswordChangeRequiredException {
                if (username.equals("hello") && password.equals("world")) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        sshServer.setPort(10000);
        sshServer.setShellFactory(new ProcessShellFactory(new String[]{"/bin/sh"}));

        ScpCommandFactory scpCommandFactory = new ScpCommandFactory();
        scpCommandFactory.setDelegateCommandFactory(new CommandFactory() {
            @Override
            public Command createCommand(String command) {
                return new ProcessShellFactory(command.split(" ")).create();
            }
        });
        sshServer.setCommandFactory(scpCommandFactory);

        try {
            System.out.println("Server starting!");
            sshServer.start();
            System.out.println("Server started!");
        } catch (IOException ex) {
            ex.printStackTrace();
            Logger.getLogger(ApacheMina.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void createMinaSshClient() throws Exception {
        SshClient sshClient = SshClient.setUpDefaultClient();
        sshClient.addPasswordIdentity("world");
        sshClient.start();
        ConnectFuture connect = sshClient.connect("hello", "127.0.0.1", 10000);
        connect.await();
        if (connect.isConnected()) {
            ClientSession session = connect.getSession();
//            session.auth().await().isSuccess();
            ChannelExec channel = session.createExecChannel("mkdir D:\\zzzx0");
//            channel.setInputStream(null);
            channel.open();
//            ExecutorService executorService = channel.getExecutorService();
//            executorService.awaitTermination(10, TimeUnit.SECONDS);
            InputStream inputStream = channel.getIn();
            StringBuilder buffer = new StringBuilder("");
            byte[] bytes = new byte[BUFFER_SIZE];
            int read = inputStream.read(bytes);
            buffer.append(new String(bytes));
            while (read == BUFFER_SIZE) {
                read = inputStream.read(bytes);
                buffer.append(new String(bytes));
            }
            System.out.println(buffer.toString());
            channel.close();
            session.close();
        }
        sshClient.stop();
    }


}
