/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.module.pmp.mail.recovery.classes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ibs.pmp.api.mailgw.service.impl.MailServiceImpl;
import ru.ibs.pmp.api.model.msk.dto.MessageWrapper;
import ru.ibs.pmp.api.model.msk.export.Folder;
import ru.ibs.pmp.common.ex.PmpFeatureException;
import ru.ibs.pmp.common.ex.PmpModuleInternalException;

/**
 *
 * @author IBS_ERZL
 */
public class MailServiceImplExt extends MailServiceImpl {

    private int startIndex = 0;
    private int endIndex = 0;
    
    private static final Logger LOG = LoggerFactory.getLogger(MailServiceImplExt.class);

    public int getMessageCount(Folder folderName) throws MessagingException {
        int messageCount = 0;
        List<MessageWrapper> messageWrappers;
        Store store = getStore();
        javax.mail.Folder folder = null;
        try {
            folder = store.getFolder(getFolderName(folderName));
            folder.open(javax.mail.Folder.READ_WRITE);
            messageCount = folder.getMessageCount();
        } finally {
            if (null != folder) {
                folder.close(true);
            }
            if (null != store) {
                store.close();
            }
        }
        return messageCount;
    }

    @Override
    public List<MessageWrapper> getMessages(Folder folderName)
            throws MessagingException, IOException, PmpModuleInternalException, PmpFeatureException {
        List<MessageWrapper> messageWrappers;
        Store store = getStore();
        javax.mail.Folder folder = null;
        try {
            folder = store.getFolder(getFolderName(folderName));
            folder.open(javax.mail.Folder.READ_WRITE);
            Message[] messages = getMessages(folder);

            Arrays.sort(messages, new Comparator<Message>() {
                @Override
                public int compare(Message m1, Message m2) {
                    int result = 0;
                    try {
                        result = m1.getReceivedDate().compareTo(m2.getReceivedDate());
                    } catch (MessagingException me) {
                        LOG.error(me.getMessage(), me);
                    }
                    return result;
                }
            });

            messageWrappers = new ArrayList<>(messages.length);
            for (Message msg : messages) {
                try {
                    messageWrappers.add(convert(msg));
//                    copyToFolder(store, MailService.ON_PROCESS_FOLDER_NAME, msg);
                    msg.setFlag(Flags.Flag.DELETED, true);
                } catch (Exception e) {
//                    copyToFolder(store, MailService.ERROR_FOLDER_NAME, msg);
                    LOG.error(e.getMessage(), e);
                    msg.setFlag(Flags.Flag.DELETED, true);
                }

            }

            return messageWrappers;
        } finally {
            if (null != folder) {
                folder.close(true);
            }
            if (null != store) {
                store.close();
            }
        }
    }
    
    protected Message[] getMessages(javax.mail.Folder folder) throws MessagingException {
        return folder.getMessages();
    }
    
    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

}
