package ru.ibs.testpumputils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import ru.ibs.pmp.api.model.dbf.moparcel.InvoiceRecordWithoutOncology;
import ru.ibs.pmp.api.model.dbf.parcel.CuredPatient;
import ru.ibs.pmp.common.lib.Db;
import ru.ibs.testpumputils.interceptors.SqlRewriteInterceptorExt;
import ru.ibs.testpumputils.interfaces.SessionFactoryInterface;
import ru.ibs.testpumputils.interfaces.SessionFactoryInvocationHandler;

public class TestPmpParcelInsertion {

    static SessionFactoryInterface sessionFactory;

    public static void test(String args[]) throws Exception {
        Map<String, Field> fieldMap = Stream.of(InvoiceRecordWithoutOncology.class.getDeclaredFields()).filter(field -> !Modifier.isFinal(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())).peek(field -> field.setAccessible(true)).collect(Collectors.toMap(Field::getName, field -> field));
        Map<String, Field> fieldMap2 = Stream.of(CuredPatient.class.getDeclaredFields()).filter(field -> !Modifier.isFinal(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())).peek(field -> field.setAccessible(true)).collect(Collectors.toMap(Field::getName, field -> field));
        sessionFactory = (SessionFactoryInterface) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SessionFactoryInterface.class}, new SessionFactoryInvocationHandler(TestPumpUtilsMain.buildSessionFactory(), new SqlRewriteInterceptorExt()));
        try {
            System.out.println("----------------------pmp_parcel_s----------------------");
            Db.update(sessionFactory, session -> {
                InvoiceRecordWithoutOncology invoiceRecordDb = (InvoiceRecordWithoutOncology) session.get(InvoiceRecordWithoutOncology.class, 6092769068L);
                for (int i = 0; i < 254; i++) {
                    InvoiceRecordWithoutOncology invoiceRecord = new InvoiceRecordWithoutOncology();
                    for (Entry<String, Field> entry : fieldMap.entrySet()) {
                        String fieldName = entry.getKey();
                        Field field = entry.getValue();
                        try {
                            field.set(invoiceRecord, field.get(invoiceRecordDb));
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    invoiceRecord.setId(null);
                    session.persist(invoiceRecord);
                    System.out.println("id = " + invoiceRecord.getId() + "!");
                }
                return null;
            });
            System.out.println("----------------------pmp_parcel_r----------------------");
            Db.update(sessionFactory, session -> {
                CuredPatient curedPatientDb = (CuredPatient) session.get(CuredPatient.class, 1559536784L);
                for (int i = 0; i < 222; i++) {
                    CuredPatient curedPatient = new CuredPatient();
                    for (Entry<String, Field> entry : fieldMap2.entrySet()) {
                        String fieldName = entry.getKey();
                        Field field = entry.getValue();
                        try {
                            field.set(curedPatient, field.get(curedPatientDb));
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    curedPatient.setId(null);
                    curedPatient.setRecordId("" + i);
                    session.persist(curedPatient);
                    System.out.println("id = " + curedPatient.getId() + "!");
                }
                return null;
            });
        } finally {
            sessionFactory.cleanSessions();
            sessionFactory.close();
        }
    }
}
