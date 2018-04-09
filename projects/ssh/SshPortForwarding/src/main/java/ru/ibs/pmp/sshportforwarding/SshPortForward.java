package ru.ibs.pmp.sshportforwarding;

import com.jcraft.jsch.JSchException;

/**
 * @author NAnishhenko
 */
public class SshPortForward {

    public static void main(String[] args) throws JSchException {
        SshClient sshClient = new SshClient("192.168.192.116", "hello", "world", 9084);
//        sshClient.forwardL(22, "192.168.192.211", 22777);
//        sshClient.forwardL(22, "192.168.192.213", 22888);
//        sshClient.forwardL(22, "192.168.192.210", 22999);
//        sshClient.forwardL(21044, "192.168.192.213", 21044);
        if (args[0].equals("L")) {
            sshClient.forwardL(Integer.valueOf(args[1]), args[2], Integer.valueOf(args[3]));
        }
    }
}
