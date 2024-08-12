package ru.ibs.zzgeneratesendrequests;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;
import ru.ibs.zzgeneratesendrequests.bean.InsertBean;

/**
 * @author NAnishhenko
 */
public class SendRequestsStart {

    public static void main(String[] args) throws SQLException, InterruptedException {
        while (true) {
            Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@omsdb-scan.mgf.msk.oms:1528/PUMPN", "PMP_PROD", "PMP_PROD");
            try {
                final String sqlQuery = "select re.mo_id,re.period,\n"
                        + "'\n"
                        + "insert into pmp_sync (LPU_ID,PERIOD,CALL_DATA,FEATURE_NAME,CREATED,FAILED,IN_PROGRESS,IS_PROCESS_ALIVE,BILL_STATISTICS_ID,SERVER_IP,PARAMETERS,USER_UNIQUEID,PROCESS_ID)\n"
                        + "values('||re.mo_id||',to_date(''2020-03-01'',''yyyy-MM-dd''),''SendBillsVirtualRequest'',''sendBillsFeature'',SYSDATE,''0'',null,null,null,null,\n"
                        + "(\n"
                        + "select \n"
                        + "listagg(b.id,'','') within group(order by b.id) as fds\n"
                        + "from pmp_bill b\n"
                        + "inner join pmp_requirement re on re.id=b.requirement_id\n"
                        + "left join MOSPRSMO smo on smo.q_ogrn=b.payer_ogrn\n"
                        + "where re.period=to_date(''2020-03-01'',''yyyy-MM-dd'') and re.mo_id='||re.mo_id||'\n"
                        + "and smo.name in(''ООО ВТБ МС'',''ОАО СК \"СОГАЗ-МЕД\"'') and b.amount=0\n"
                        + ")\n"
                        + ",''Me'',null)\n"
                        + "' as str\n"
                        + "from pmp_bill b\n"
                        + "inner join pmp_requirement re on re.id=b.requirement_id\n"
                        + "left join MOSPRSMO smo on smo.q_ogrn=b.payer_ogrn\n"
                        + "where re.period=to_date('2020-03-01','yyyy-MM-dd') \n"
                        + "--and mo_id in(1795, 1800, 1801, 1810, 1821, 1845, 1851, 1871, 1896, 1913, 1914, 1940, 1989, 1996,\n"
                        + "--2045, 2047, 2048, 2050, 2064, 2065, 2165, 2174, 2191, 2228, 2240, 2284, 2288, 2295, 2306, 2309, 2311, 2313, 2337, 2338, 2346, 2358, 2369,\n"
                        + "--3321, 3336, 3376, 3388, 3433, 3459, 3461, 3513, 3532, 3619, 3621, 3623,\n"
                        + "--4305, 4388, 4404, 4451, 4462, 4471, 4497, 4501, 4504, 4506, 4507, 4510, 4575, 4586, 4589, 4592, 4619, 4673, 4674, 4675, 4678, 4696, 4698, 4723, 4734, 4767, 4774, 4779, 4784, 4789, 4804, 4819, 4884, 4904, 4910, 4934, 4960, 4964, 4975,\n"
                        + "--5009, 5029, 5039, 5074, 5165, 5169, 5195, 5219, 5224, 5232, 5248, 5255, 5263, 5267, 5268, 5287, 5288, 5307, 5308, 5311, 5315, 5352, 5355, 5359, 5368, 5371, 5376, 5392, 5416, 5438, 5449, 5451, 5469)\n"
                        + "and smo.name in('ООО ВТБ МС','ОАО СК \"СОГАЗ-МЕД\"')\n"
                        + "and not exists (select 1 from pmp_sync sy where sy.lpu_id=re.mo_id and sy.period=re.period)\n"
                        + "--and not exists (\n"
                        + "--select 1 from PMP_BILL_STATISTICS st\n"
                        + "--inner join pmp_requirement re2 on st.REQUIREMENT_ID=re2.id\n"
                        + "--where st.created>=to_date('2020-04-02 00:00:00','yyyy-MM-dd HH24:mi:ss') and st.user_uniqueid='Me' and re2.id=re.id\n"
                        + "--)\n"
                        + "and b.amount=0 and b.status in('GENERATED','GENERATED_WFLK')\n"
                        + "";
                ResultSet executeQuery = connection.prepareStatement(sqlQuery).executeQuery();
                Set<InsertBean> insertSet = new HashSet<>();
                while (executeQuery.next()) {
                    Long lpuId = executeQuery.getLong("mo_id");
                    Date period = executeQuery.getDate("period");
                    String string = executeQuery.getString("str");
                    insertSet.add(new InsertBean(lpuId, period, string));
                }
                for (InsertBean insertBean : insertSet) {
                    connection.prepareStatement(insertBean.getInsertString()).executeUpdate();
                    connection.commit();
                    System.out.println(insertBean.getLpuId().toString() + " " + new SimpleDateFormat("yyyy-MM-dd").format(insertBean.getPeriod()) + " task was inserted!");
                }
            } finally {
                connection.close();
            }
            Thread.sleep(60 * 1000);
        }
    }
}
