package ru.ibs.pmp.SimpleQueryExecutor;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author NAnishhenko
 */
@Service
public class SimpleQueryExecutor {

    @Autowired
    @Qualifier("pmpTransactionTemplate")
    TransactionTemplate tx;
    @Autowired
    @Qualifier("pmpSessionFactory")
    SessionFactory sessionFactory;

    public static void main(String args[]) throws Exception {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("query_module.xml");
        SimpleQueryExecutor simpleQueryExecutor = applicationContext.getBean(SimpleQueryExecutor.class);
        if (args.length == 1) {
            if (args[0].contains("select")) {
                simpleQueryExecutor.executeSimpleQuery(args[0]);
            } else if (new File(args[0]).exists()) {
                String sql = new String(Files.readAllBytes(new File(args[0]).toPath()));
                if (sql.contains("select")) {
                    simpleQueryExecutor.executeSimpleQuery(sql);
                } else {
                    System.out.println("Bad file content!");
                }
            } else {
                System.out.println("Bad argument!");
            }
        }
    }

    public void executeSimpleQuery(String sql) {
        List<Object[]> list = tx.execute(status -> {
            Session session = sessionFactory.openSession();
            List<Object[]> listDb = session.createSQLQuery(sql).list();
            session.close();
            return listDb;
        });
        StringBuilder stringBuilder = new StringBuilder();
        list.stream().forEach(objArr -> {
            StringBuilder sb = new StringBuilder();
            Arrays.stream(objArr).forEach(obj -> {
                sb.append(obj + "   ");
            });
            stringBuilder.append(sb.toString() + "\n");
        });
        System.out.println(stringBuilder.toString());
    }

}
