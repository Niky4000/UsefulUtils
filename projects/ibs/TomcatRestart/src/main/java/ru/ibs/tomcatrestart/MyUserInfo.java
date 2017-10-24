package ru.ibs.tomcatrestart;

import com.jcraft.jsch.UserInfo;

/**
 *
 * @author NAnishhenko
 */
public class MyUserInfo implements UserInfo {

    private final String password;

    public MyUserInfo(String password) {
        this.password = password;
    }

    @Override
    public String getPassphrase() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean promptPassword(String message) {
        System.out.println("promptPassword: " + message);
        return true;
    }

    @Override
    public boolean promptPassphrase(String message) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean promptYesNo(String message) {
        return true;
    }

    @Override
    public void showMessage(String message) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
