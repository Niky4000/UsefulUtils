/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.pmp.apachemina;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSchException;
import java.io.IOException;

/**
 *
 * @author NAnishhenko
 */
public interface ApacheMinaClient {

    void createSshClient(String host, String user, String password, int port, String[] commands) throws Exception;

    void execCommand(Channel channel, String command) throws JSchException, IOException;

    void forwardL(String host, int port, String user, String password, String rhost, int rport, int lport);

    void forwardR(String host, int port, String user, String password, String lhost, int lport, int rport);

    void scpFrom(String host, int port, String user, String password, String rfile, String lfile);

    void scpTo(String host, int port, String user, String password, String rfile, String lfile);

}
