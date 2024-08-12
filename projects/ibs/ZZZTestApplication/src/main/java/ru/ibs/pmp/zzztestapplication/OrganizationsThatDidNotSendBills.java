package ru.ibs.pmp.zzztestapplication;

import java.io.File;
import java.nio.file.Files;
import static java.nio.file.Files.lines;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import ru.ibs.pmp.zzztestapplication.bean.OrganizationsThatDidNotSendBillsBean;
import ru.ibs.pmp.zzztestapplication.bean.OrganizationsThatDidNotSendBillsBean2;

/**
 * @author NAnishhenko
 */
public class OrganizationsThatDidNotSendBills {

    public static void getReport() throws Exception {
        Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@scan.mgf.msk.oms:1528/PUMPN", "PMP_PROD", "PMP_PROD");
        List<OrganizationsThatDidNotSendBillsBean> sendList = executeQuery(connection, "select re.mo_id,re.period,st.parameters\n"
                + "from PMP_BILL_STATISTICS st\n" + "inner join pmp_requirement re on st.REQUIREMENT_ID=re.id\n"
                + "where st.created>=to_date('2020-03-12 17:00:00','yyyy-MM-dd HH24:mi:ss') and re.period=to_date('2020-02-01','yyyy-MM-dd') \n"
                + "and st.type='SEND_BILL_MODULE'\n" + "order by created desc");
        List<OrganizationsThatDidNotSendBillsBean> recreateList = executeQuery(connection, "select re.mo_id,re.period,st.parameters\n"
                + "from PMP_BILL_STATISTICS st\n"
                + "inner join pmp_requirement re on st.REQUIREMENT_ID=re.id\n"
                + "where st.created>=to_date('2020-03-12 17:00:00','yyyy-MM-dd HH24:mi:ss') and re.period=to_date('2020-02-01','yyyy-MM-dd') \n"
                + "and st.type='RECREATE_BILL_MODULE'\n"
                + "order by created desc");
        Map<Long, Set<Long>> sendMap = getMap(sendList);
        Map<Long, Set<Long>> recreateMap = getMap(recreateList);
        Map<Long, Set<Long>> resultMap = recreateMap.entrySet().stream().filter(entry -> {
            Long lpuId = entry.getKey();
            Set<Long> billIdSet = entry.getValue();
            if (sendMap.containsKey(lpuId)) {
                Set<Long> sendBillIdSet = sendMap.get(lpuId);
                billIdSet.removeAll(sendBillIdSet);
                if (billIdSet.isEmpty()) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
//        System.out.println(resultMap.toString());
        List<OrganizationsThatDidNotSendBillsBean2> executeQuery2 = executeQuery2(connection, resultMap.entrySet().stream().flatMap(entry -> entry.getValue().stream()).collect(Collectors.toSet()));
        Optional<String> resultString = executeQuery2.stream().map(obj -> obj.getLpuId().toString() + " " + obj.getBillId().toString() + " " + obj.getOgrn() + "\n").reduce((str1, str2) -> str1 + str2);
        File file = new File("D:\\tmp\\parcels\\report.txt");
        if (file.exists()) {
            file.delete();
        }
        Files.write(file.toPath(), resultString.get().getBytes(), StandardOpenOption.CREATE_NEW);
        System.out.println("Finished!");
    }

    private static Map<Long, Set<Long>> getMap(List<OrganizationsThatDidNotSendBillsBean> objList) {
        return objList.stream().collect(Collectors.toMap(obj -> obj.getLpuId(), obj -> {
            Set<Long> billIdSet = Arrays.stream(obj.getParameters().replaceAll("\\[.+?\\]", "").trim().split(",")).map(Long::valueOf).collect(Collectors.toSet());
            return billIdSet;
        }, (obj1, obj2) -> {
            obj1.addAll(obj2);
            return obj1;
        }));
    }

    private static List<OrganizationsThatDidNotSendBillsBean> executeQuery(Connection connection, String sqlQuery) throws SQLException {
        ResultSet executeQuery = connection.prepareStatement(sqlQuery).executeQuery();
        List<OrganizationsThatDidNotSendBillsBean> dataList = new ArrayList<>();
        while (executeQuery.next()) {
            Long lpuId = executeQuery.getLong("mo_id");
            Date period = executeQuery.getDate("period");
            String parameters = executeQuery.getString("parameters");
            dataList.add(new OrganizationsThatDidNotSendBillsBean(lpuId, period, parameters));
        }
        return dataList;
    }

    private static List<OrganizationsThatDidNotSendBillsBean2> executeQuery2(Connection connection, Set<Long> billIdSet) throws SQLException {
        String str = billIdSet.stream().map(obj -> obj.toString() + ",").reduce((obj1, obj2) -> obj1 + obj2).get();
        str = str.substring(0, str.length() - 1);
        try {
            ResultSet executeQuery = connection.prepareStatement("select re.mo_id,b.id,b.payer_ogrn,b.bill_type,b.status,smo.name,smo.cod_foms,b.amount,b.error_amount,b.error_amount_flk_bill,b.invoices_count from pmp_bill b\n"
                    + "inner join pmp_requirement re on re.id=b.requirement_id\n"
                    + "left join MOSPRSMO smo on smo.q_ogrn=b.payer_ogrn\n"
                    + "where re.period=to_date('2020-02-01','yyyy-MM-dd') and b.id in(" + str + ")\n"
                    + "order by re.mo_id,smo.cod_foms").executeQuery();
            List<OrganizationsThatDidNotSendBillsBean2> dataList = new ArrayList<>();
            while (executeQuery.next()) {
                Long lpuId = executeQuery.getLong("mo_id");
                Long billId = executeQuery.getLong("id");
                String ogrn2 = executeQuery.getString("payer_ogrn");
                String ogrn = executeQuery.getString("name");
                dataList.add(new OrganizationsThatDidNotSendBillsBean2(lpuId, billId, ogrn != null ? ogrn : ogrn2));
            }
            return dataList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
