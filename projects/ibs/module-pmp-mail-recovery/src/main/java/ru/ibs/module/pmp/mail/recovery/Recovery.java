package ru.ibs.module.pmp.mail.recovery;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import ru.ibs.module.pmp.mail.recovery.classes.MailServiceImplExt;
import ru.ibs.pmp.api.model.msk.dto.AttachmentWrapper;
import ru.ibs.pmp.api.model.msk.dto.MessageWrapper;
import ru.ibs.pmp.api.model.msk.export.Folder;
import ru.ibs.pmp.api.model.msk.export.MailGwAttachment;
import ru.ibs.pmp.api.model.msk.export.MailGwLogEntry;
import ru.ibs.pmp.api.model.msk.export.MailGwLogEntry.Direction;
import ru.ibs.pmp.module.pmp.bill.recreate.bean.CallBackBeanForSerialization;

/**
 *
 * @author NAnishhenko
 */
public class Recovery {

    Date MAIL_DATE;
    private static final Direction DIRECTION = Direction.IN;
    private static final String MAIL_NAME = "all";
    private static final String MAIL_OBJECT_NAME = "mail_messages";
    private static final Folder FOLDER = Folder.TEMP;
    private static final int SLICE_SIZE = 100;
    ClassPathXmlApplicationContext applicationContext;
    MailServiceImplExt mailService;
    SessionFactory sessionFactory;
    TransactionTemplate tx;

    final static CallBackBeanForSerialization serializationBean = new CallBackBeanForSerialization() {

        @Override
        public void apply(Exception ex) {
            ex.printStackTrace();
        }
    };
    final static CallBackBeanForSerialization deSerializationBean = new CallBackBeanForSerialization() {

        @Override
        public void apply(Exception ex) {
            ex.printStackTrace();
        }
    };

    public static void serializeObject(Object obj, String objectName, Date period, String lpuId, CallBackBeanForSerialization callBackBeanForSerialization) {
        String periodStr = new SimpleDateFormat("yyyy-MM").format(period);
        File serfile = new File(System.getProperty("java.io.tmpdir") + "/serializedObjects/" + objectName + "_" + (lpuId != null ? lpuId + "_" + periodStr : periodStr));
        if (serfile.exists()) {
            serfile.delete();
        }
        if (!serfile.getParentFile().exists()) {
            serfile.getParentFile().mkdirs();
        }
        //serialize the List
        try (
                OutputStream file = new FileOutputStream(serfile);
                OutputStream buffer = new BufferedOutputStream(file);
                ObjectOutput output = new ObjectOutputStream(buffer);) {
            output.writeObject(obj);
        } catch (IOException ioe) {
//            logMessage("Error in object serialization!", false, null, null, Recreate.LogType.ERROR, ioe);
            callBackBeanForSerialization.apply(ioe);
        }
    }

    public static <T> T deSerializeObject(Class<T> objClass, String objectName, Date period, String lpuId, CallBackBeanForSerialization callBackBeanForSerialization) {
        String periodStr = new SimpleDateFormat("yyyy-MM").format(period);
        File serfile = new File(System.getProperty("java.io.tmpdir") + "/serializedObjects/" + objectName + "_" + (lpuId != null ? lpuId + "_" + periodStr : periodStr));
        if (!serfile.exists()) {
            return null;
        }
        //deserialize the quarks.ser file
        try (
                InputStream file = new FileInputStream(serfile);
                InputStream buffer = new BufferedInputStream(file);
                ObjectInput input = new ObjectInputStream(buffer);) {
            //deserialize the List
            T obj = (T) input.readObject();
            //display its data
            return obj;
        } catch (ClassNotFoundException | IOException e) {
//            logMessage("Error while object deserialization!", false, null, null, Recreate.LogType.ERROR, e);
            callBackBeanForSerialization.apply(e);
        }
        return null;
    }

    public void init() throws ParseException {
        applicationContext = new ClassPathXmlApplicationContext("module.xml");
        mailService = applicationContext.getBean(MailServiceImplExt.class);
        MAIL_DATE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss_SSS").parse("2017-01-20 00:00:00_000");

        sessionFactory = applicationContext.getBean("sessionFactory", SessionFactory.class);
        tx = applicationContext.getBean("transactionTemplate", TransactionTemplate.class);
    }

    public List<MessageWrapper> getMailsFromCache() {
        return (List<MessageWrapper>) deSerializeObject(List.class, MAIL_OBJECT_NAME, MAIL_DATE, MAIL_NAME, deSerializationBean);
    }

    public List<MessageWrapper> getMails() {
        List<MessageWrapper> messages = new ArrayList<>();
        try {
            int messageCount = mailService.getMessageCount(FOLDER);
            int startIndex = messageCount - SLICE_SIZE;
            int endIndex = messageCount;
            if (startIndex <= 0) {
                startIndex = 1;
            }
//            boolean condition = true;

//            while (condition) {
//                mailService.setStartIndex(startIndex);
//                mailService.setEndIndex(endIndex);
            List<MessageWrapper> messages_ = mailService.getMessages(FOLDER);
            for (MessageWrapper messageWrapper : messages_) {
                if (messageWrapper.getDate().after(MAIL_DATE)) {
                    messages.add(messageWrapper);
                }
//                    else {
//                        condition = false;
//                    }
//                }
//                startIndex = startIndex - SLICE_SIZE;
//                endIndex = endIndex - SLICE_SIZE;
//                if (startIndex <= 0) {
//                    condition = false;
//                }
            }
        } catch (Exception ex) {
            Logger.getLogger(Recovery.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
        serializeObject(messages, MAIL_OBJECT_NAME, MAIL_DATE, MAIL_NAME, deSerializationBean);
        return messages;
    }

    public void recover(List<MessageWrapper> mails) {
        for (final MessageWrapper messageWrapper : mails) {
            if (messageWrapper.getAttachments() == null || messageWrapper.getAttachments().isEmpty()) {
                continue;
            }
            String mailMessageId = messageWrapper.getMailMessageId();
            if (mailMessageId == null || mailMessageId.length() <= 3 || !mailMessageId.contains("<") || !mailMessageId.contains(">")) {
                continue;
            }
            int length = mailMessageId.length();
            final String messageId = mailMessageId.substring(1, length - 1);
            final String correlationId = messageWrapper.getCorrelationId();
            final String id = messageWrapper.getId();
            tx.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus ts) {
                    Session session = sessionFactory.openSession();

                    try {
//                        List<MailGwLogEntry> list = session.createCriteria(MailGwLogEntry.class).add(Restrictions.eq("externalId", messageId)).list();
//                        List<MailGwLogEntry> list = session.createCriteria(MailGwLogEntry.class).add(Restrictions.eq("correlationId", correlationId)).list();
                        List<MailGwLogEntry> list = session.createCriteria(MailGwLogEntry.class).add(Restrictions.eq("externalId", id)).list();
                        if (list != null && list.size() == 1 && list.get(0).getDirection().equals(DIRECTION)) {
                            MailGwLogEntry entry = list.get(0);
                            if (entry.getAttachments() == null || entry.getAttachments().isEmpty()) {
                                for (AttachmentWrapper attachmentWrapper : messageWrapper.getAttachments()) {
                                    MailGwAttachment mailGwAttachment = new MailGwAttachment();
                                    mailGwAttachment.setLogEntry(entry);
                                    mailGwAttachment.setName(attachmentWrapper.getName());
                                    mailGwAttachment.setPayload(attachmentWrapper.getPayload());
                                    MailGwAttachment mailGwAttachmentDb = (MailGwAttachment) session.merge(mailGwAttachment);
                                    System.out.println("mailGwLogId=" + entry.getId().toString() + " mailGwAttachment saved with id=" + mailGwAttachmentDb.getId().toString() + "!!!");
                                }
                            }
                        } else {
                            System.out.println("Logic error!!! "+"messageId = " + messageId+" correlationId = "+correlationId);
                        }
                    } catch (Exception e) {
                        System.out.println("messageId = " + messageId + " Exception: " + e.getMessage());
                    }
                    session.flush();
                    session.close();
                }
            });
        }
    }

    public static void main(String[] args) throws ParseException {
        Recovery recovery = new Recovery();
        recovery.init();
//        List<MessageWrapper> mails = recovery.getMails();
        List<MessageWrapper> mails = recovery.getMailsFromCache();
        recovery.recover(mails);
        String hello = "";
    }
}
