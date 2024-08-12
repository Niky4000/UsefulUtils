package ru.ibs.pmp.zzztestapplication;

import com.skype.Chat;
import com.skype.Skype;

/**
 * @author NAnishhenko
 */
public class SkypeTest3 {

    public static void testSkype() throws Exception {
        Chat[] allChats = Skype.getAllChats();
        for (Chat chat : allChats) {
            System.out.println(chat.getId());
        }
    }
}
