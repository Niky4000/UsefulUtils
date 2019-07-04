//package ru.ibs.pmp.zzztestapplication;
//
//import fr.delthas.skype.Group;
//import fr.delthas.skype.Presence;
//import fr.delthas.skype.Role;
//import fr.delthas.skype.Skype;
//import fr.delthas.skype.User;
//import java.io.IOException;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//
///**
// * @author NAnishhenko
// */
//public class SkypeTest {
//
//    public static void testSkype() throws InterruptedException {
//        Skype skype = new Skype("solarpenguin7", "viking7");
//        try {
//            // If you want to report a bug, enable logging
//            // Skype.setDebug(path);
//            skype.connect(); // Will block until we're connected
//        } catch (IOException e) {
//            System.err.println("An error occured while connecting...");
//            e.printStackTrace();
//        }
//
//// Set the error callback (will be called if any exception is thrown)
//// When it is called, you'll be automatically disconnected
//        skype.setErrorListener(Exception::printStackTrace);
//
//// We're connected and ready to go
//// Say hello to all your contacts
//        for (User user : skype.getContacts()) {
//            user.sendMessage("Hi, " + user.getDisplayName() + ", what's up?");
//        }
//
//// Better: let's say hello whenever a user connects
//        skype.addUserPresenceListener((user, oldPresence, presence) -> {
//            if (oldPresence == Presence.OFFLINE) {
//                user.sendMessage("Hi, " + user.getFirstname());
//            }
//        });
//
//// Create a simple time bot
//        skype.addUserMessageListener((user, message) -> {
//            if (message.toLowerCase().contains("time")) {
//                user.sendMessage("The current time is: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
//            }
//        });
//
//// Invite everyone to the groups you own/are admin on
//        for (Group group : skype.getGroups()) {
//            if (group.isSelfAdmin()) {
//                for (User user : skype.getContacts()) {
//                    group.addUser(user, Role.USER);
//                }
//            }
//        }
//
//// Kick a user whenever he rants about Java
//        skype.addGroupMessageListener((group, user, message) -> {
//            if (message.toLowerCase().contains("java") && message.toLowerCase().contains("bad")) {
//                group.removeUser(user);
//                group.changeTopic("No Java bashing allowed here!");
//            }
//        });
//
//// Let's disconnect
//        skype.disconnect();
//    }
//}
