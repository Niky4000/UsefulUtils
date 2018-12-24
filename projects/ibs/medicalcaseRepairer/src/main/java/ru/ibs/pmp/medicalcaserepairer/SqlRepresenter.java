package ru.ibs.pmp.medicalcaserepairer;

import com.google.common.base.Strings;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import ru.ibs.pmp.api.model.FlkChecks;

/**
 * @author NAnishhenko
 */
@Service
public class SqlRepresenter {

    @Autowired
    @Qualifier("pmpTransactionTemplate")
    TransactionTemplate tx;
    @Autowired
    @Qualifier("pmpSessionFactory")
    SessionFactory sessionFactory;

    public void representPmpFlkChecksAsInserts() throws Exception {
        List<FlkChecks> flkChecksList = tx.execute(status -> {
            List<FlkChecks> flkChecksListDb = null;
            Session session = sessionFactory.openSession();
            try {
                flkChecksListDb = session.createCriteria(FlkChecks.class)
                        //                        .add(Restrictions.in("code", new String[]{"PN", "PK", "POST_PROCESSOR"}))
                        .addOrder(Order.asc("id")).list();
            } finally {
                session.close();
            }
            return flkChecksListDb;
        });
        if (!flkChecksList.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("truncate table pmp_flk_checks;\n\n");
            for (FlkChecks flkChecks : flkChecksList) {

                String[] formatSqlValue1 = formatSqlValue("id", flkChecks.getId().toString());
                String idStr = formatSqlValue1[0];
                String idValue = formatSqlValue1[1];

                String[] formatSqlValue2 = formatSqlValue("cnum", flkChecks.getCnum().toString());
                String cnumStr = formatSqlValue2[0];
                String cnumValue = formatSqlValue2[1];

                String[] formatSqlValue3 = formatSqlValue("enabled", flkChecks.getEnabled() ? "1" : "0");
                String enabledStr = formatSqlValue3[0];
                String enabledValue = formatSqlValue3[1];

                String[] formatSqlValue4 = formatSqlValue("name", "'" + handleEncoding(flkChecks.getName()) + "'");
                String nameStr = formatSqlValue4[0];
                String nameValue = formatSqlValue4[1];

                String[] formatSqlValue5 = formatSqlValue("code", "'" + flkChecks.getCode() + "'");
                String codeStr = formatSqlValue5[0];
                String codeValue = formatSqlValue5[1];

                String[] formatSqlValue6 = formatSqlValue("type", "'" + flkChecks.getType().toString() + "'");
                String typeStr = formatSqlValue6[0];
                String typeValue = formatSqlValue6[1];

                String[] formatSqlValue7 = formatSqlValue("flush_data", "'" + (flkChecks.getFlushData() ? "1" : "0") + "'");
                String flushDataStr = formatSqlValue7[0];
                String flushDataValue = formatSqlValue7[1];

                String[] formatSqlValue8 = formatSqlValue("is_mo", flkChecks.getMoCheck() ? "1" : "0");
                String isMoStr = formatSqlValue8[0];
                String isMoValue = formatSqlValue8[1];

                String[] formatSqlValue9 = formatSqlValue("is_smo", flkChecks.getSmoCheck() ? "1" : "0");
                String isSmoStr = formatSqlValue9[0];
                String isSmoValue = formatSqlValue9[1];

                String[] formatSqlValue10 = formatSqlValue("sql2_text", "l_sql_text");
                String sql2TextStr = formatSqlValue10[0];
                String sql2TextValue = formatSqlValue10[1];

                sb.append(handleSql2(handleEncoding(flkChecks.getSql2Text()), flkChecks.getCode()));

                sb.append("insert into pmp_flk_checks (").append(idStr).append(", ")
                        .append(cnumStr).append(", ")
                        .append(enabledStr).append(", ")
                        .append(nameStr).append(", ")
                        .append(codeStr).append(", ")
                        .append(typeStr).append(", ")
                        .append(flushDataStr).append(", ")
                        .append(isMoStr).append(", ")
                        .append(isSmoStr).append(", ")
                        .append(sql2TextStr).append(")\n")
                        .append("                     values(");

//                sb.append("insert into pmp_flk_checks (id, cnum, enabled, name, code, type, flush_data, is_mo, is_smo, sql2_text)\n"
//                        + "                     values(");
                sb.append(idValue).append(", ")
                        .append(cnumValue).append(", ")
                        .append(enabledValue).append(", ")
                        .append(nameValue).append(", ")
                        .append(codeValue).append(", ")
                        .append(typeValue).append(", ")
                        .append(flushDataValue).append(", ")
                        .append(isMoValue).append(", ")
                        .append(isSmoValue).append(", ")
                        .append(sql2TextValue)
                        .append(");\ncommit;\n")
                        .append("end;\n/\n")
                        .append("-------------------------- END " + flkChecks.getCode() + " ----------------------------\n\n\n\n");

//                sb.append("update pmp_flk_checks set sql2_text = ")
//                        .append(handleSql(handleEncoding(flkChecks.getSql2Text())))
//                        .append(" where id = ").append(flkChecks.getId()).append(";\ncommit;\n\n\n\n");
            }
//            System.out.println(sb.toString());
            File file = new File("D:\\GIT\\pmp\\db\\packages\\update_pmp_flk_checks_as_inserts.sql");
            if (!file.exists()) {
                file.createNewFile();
            }
            Files.write(file.toPath(), sb.toString().getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
        }
    }

    private String[] formatSqlValue(String sqlCode, String sqlValue) {
        if (sqlCode.length() > sqlValue.length()) {
            return new String[]{sqlCode, Strings.repeat(" ", sqlCode.length() - sqlValue.length()) + sqlValue};
        } else if (sqlCode.length() < sqlValue.length()) {
            return new String[]{Strings.repeat(" ", sqlValue.length() - sqlCode.length()) + sqlCode, sqlValue};
        } else {
            return new String[]{sqlCode, sqlValue};
        }
    }

    private String handleEncoding(String sql) {
//        try {
//            return new String(sql.getBytes("utf8"), "cp1251");
//        } catch (UnsupportedEncodingException ex) {
//            return null;
//        }
        return sql;
    }

    private String handleSql2(String sql, String code) {
        return "-------------------------- BEGIN " + code + " --------------------------\n"
                + "declare\n"
                + "l_sql_text varchar2(32000) :='"
                + removeNewLinesInTheBeggining(sql).replaceAll("\\n+", "\n")
                        .replace("'", "''") + "';\n"
                + "begin\n";
    }

    private String removeNewLinesInTheBeggining(String sql) {
        int i = 0;
        for (i = 0; i < sql.length(); i++) {
            if (!sql.substring(i, i + 1).equals("\n")) {
                break;
            }
        }
        return sql.substring(i);
    }

    private String handleSql(String sql) {
        String toClob = "TO_CLOB(q'[";
        String concat = "||";
        String endClob = "]')";
        if (sql.length() < 4000) {
            return toClob + sql.replace("'", "''") + endClob;
        } else {
            int max = 3000;
            StringBuilder ret = new StringBuilder();
            int j = 0;
            for (int i = j + max; i < sql.length(); i += max) {
                ret.append(toClob).append(sql.substring(j, Math.min(sql.length(), i)).replace("'", "''")).append(endClob).append(concat);
                j += max;
            }
            String retStr = ret.toString();
            return retStr.substring(0, retStr.length() - concat.length());
        }
    }
}
