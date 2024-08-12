package ru.ibs.testpumputils;

import org.hibernate.SessionFactory;
import ru.ibs.pmp.common.lib.Db;

/**
 *
 * @author me
 */
public class SmoFileNameGenerationTest {

    static SessionFactory smoSessionFactory;

    public static void test() throws Exception {
        smoSessionFactory = TestPumpUtilsMain.buildSmoSessionFactory();
        try {
            String filename = Db.select(smoSessionFactory, session -> (String) session.createSQLQuery("SELECT PMP_SMO1.CALC_ACT_NUM_DOC(:smoExaminationId) || '_act_cancell_atach' || '.docx' FROM DUAL").setParameter("smoExaminationId", 41874L).uniqueResult());
            System.out.println(filename);
        } finally {
            smoSessionFactory.close();
        }
    }
}
