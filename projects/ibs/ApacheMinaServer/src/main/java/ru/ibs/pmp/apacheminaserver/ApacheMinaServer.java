/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.pmp.apacheminaserver;

import java.io.IOException;

/**
 *
 * @author NAnishhenko
 */
public interface ApacheMinaServer {

    void createSshServer() throws IOException;
    
    void createSshServer2() throws Exception;
    
}
