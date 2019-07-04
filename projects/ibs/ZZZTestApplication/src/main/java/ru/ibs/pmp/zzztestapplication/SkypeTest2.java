//package ru.ibs.pmp.zzztestapplication;
//
//import in.kyle.ezskypeezlife.EzSkype;
//import in.kyle.ezskypeezlife.api.errors.SkypeCaptcha;
//import in.kyle.ezskypeezlife.api.errors.SkypeErrorHandler;
//import in.kyle.ezskypeezlife.api.skype.SkypeCredentials;
//import in.kyle.ezskypeezlife.api.skype.SkypeProperties;
//import java.io.IOException;
//
///**
// * @author NAnishhenko
// */
//public class SkypeTest2 implements SkypeErrorHandler {
//
//    public void testSkype() throws Exception {
//// Enter the Skype login info here and login
//
//        // Enter the Skype login info here
//        SkypeProperties properties = new SkypeProperties();
//        properties.setSessionPingInterval(30000L);
//        properties.setUpdateContacts(false);
//        System.out.println(properties);
//        EzSkype ezSkype = new EzSkype(new SkypeCredentials("solarpenguin7", "viking7"));
//        ezSkype.setDebug(false);
//        // A error handler is a class that will be called to solve issues with the bot
//        ezSkype.setErrorHandler(this);
//        ezSkype.login();
//        String hello = "";
//    }
//
//    @Override
//    public String solve(SkypeCaptcha sc) {
//        return "!!!";
//    }
//
//    @Override
//    public String setNewPassword() {
//        return "setNewPassword";
//    }
//
//    @Override
//    public void handleException(Exception excptn) {
//        excptn.printStackTrace();
//    }
//}
