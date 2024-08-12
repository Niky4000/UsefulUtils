/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.pmp.zzztestapplication;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 * @author me
 */
public class BillSum {

    static String billSum = "07-DEC-2020 12.57.48	Unknown Bill 100714355 with invoicesSum = 4395.12 and invoiceCount = 37 was handled on Stage2!\n"
            + "07-DEC-2020 12.57.48	Bill 100714356 with invoicesSum = 352600.56 and invoiceCount = 6 was handled on Stage2!\n"
            + "04-DEC-2020 09.17.11	Unknown Bill 100714355 with invoicesSum = 4395.12 and invoiceCount = 37 was handled on Stage2!\n"
            + "04-DEC-2020 09.17.11	Bill 100714357 with invoicesSum = 773809.55 and invoiceCount = 752 was handled on Stage2!\n"
            + "03-DEC-2020 14.03.41	Unknown Bill 100714355 with invoicesSum = 4395.12 and invoiceCount = 37 was handled on Stage2!\n"
            + "03-DEC-2020 14.03.41	Bill 100714368 with invoicesSum = 6336376.64 and invoiceCount = 6127 was handled on Stage2!\n"
            + "03-DEC-2020 14.03.41	Bill 100714367 with invoicesSum = 2845965.12 and invoiceCount = 4624 was handled on Stage2!\n"
            + "03-DEC-2020 14.03.41	Bill 100714366 with invoicesSum = 3786123.05 and invoiceCount = 2210 was handled on Stage2!\n"
            + "03-DEC-2020 14.03.41	Bill 100714358 with invoicesSum = 1002060.68 and invoiceCount = 526 was handled on Stage2!\n"
            + "03-DEC-2020 14.03.41	Bill 100714357 with invoicesSum = 773809.55 and invoiceCount = 752 was handled on Stage2!\n"
            + "03-DEC-2020 14.03.41	Bill 100714356 with invoicesSum = 352600.56 and invoiceCount = 6 was handled on Stage2!\n"
            + "02-DEC-2020 13.07.24	Unknown Bill 100714355 with invoicesSum = 4070.63 and invoiceCount = 33 was handled on Stage2!\n"
            + "02-DEC-2020 13.07.24	Bill 100714368 with invoicesSum = 6205405.09 and invoiceCount = 4940 was handled on Stage2!\n"
            + "02-DEC-2020 13.07.24	Bill 100714367 with invoicesSum = 2759880.32 and invoiceCount = 3810 was handled on Stage2!\n"
            + "02-DEC-2020 13.07.24	Bill 100714366 with invoicesSum = 3704063.88 and invoiceCount = 1794 was handled on Stage2!\n"
            + "02-DEC-2020 13.07.24	Bill 100714365 with invoicesSum = 0 and invoiceCount = 0 was handled on Stage2!\n"
            + "02-DEC-2020 13.07.24	Bill 100714364 with invoicesSum = 0 and invoiceCount = 0 was handled on Stage2!\n"
            + "02-DEC-2020 13.07.24	Bill 100714363 with invoicesSum = 330130.73 and invoiceCount = 229 was handled on Stage2!\n"
            + "02-DEC-2020 13.07.24	Bill 100714362 with invoicesSum = 1760403.8 and invoiceCount = 834 was handled on Stage2!\n"
            + "02-DEC-2020 13.07.24	Bill 100714361 with invoicesSum = 0 and invoiceCount = 0 was handled on Stage2!\n"
            + "02-DEC-2020 13.07.24	Bill 100714360 with invoicesSum = 0 and invoiceCount = 0 was handled on Stage2!\n"
            + "02-DEC-2020 13.07.24	Bill 100714359 with invoicesSum = 1032415.52 and invoiceCount = 465 was handled on Stage2!\n"
            + "02-DEC-2020 13.07.24	Bill 100714358 with invoicesSum = 995062.87 and invoiceCount = 465 was handled on Stage2!\n"
            + "02-DEC-2020 13.07.24	Bill 100714357 with invoicesSum = 793911.66 and invoiceCount = 617 was handled on Stage2!\n"
            + "02-DEC-2020 13.07.24	Bill 100714356 with invoicesSum = 352600.56 and invoiceCount = 6 was handled on Stage2!\n"
            + "02-DEC-2020 12.46.49	Unknown Bill 100714355 with invoicesSum = 121604.15 and invoiceCount = 35 was handled on Stage2!\n"
            + "02-DEC-2020 12.46.49	Bill 100714368 with invoicesSum = 6205405.09 and invoiceCount = 4940 was handled on Stage2!\n"
            + "02-DEC-2020 12.46.49	Bill 100714367 with invoicesSum = 2759880.32 and invoiceCount = 3810 was handled on Stage2!\n"
            + "02-DEC-2020 12.46.49	Bill 100714366 with invoicesSum = 3704063.88 and invoiceCount = 1794 was handled on Stage2!\n"
            + "02-DEC-2020 12.46.49	Bill 100714365 with invoicesSum = 0 and invoiceCount = 0 was handled on Stage2!\n"
            + "02-DEC-2020 12.46.49	Bill 100714364 with invoicesSum = 0 and invoiceCount = 0 was handled on Stage2!\n"
            + "02-DEC-2020 12.46.49	Bill 100714363 with invoicesSum = 330130.73 and invoiceCount = 229 was handled on Stage2!\n"
            + "02-DEC-2020 12.46.49	Bill 100714362 with invoicesSum = 1760403.8 and invoiceCount = 834 was handled on Stage2!\n"
            + "02-DEC-2020 12.46.49	Bill 100714361 with invoicesSum = 0 and invoiceCount = 0 was handled on Stage2!\n"
            + "02-DEC-2020 12.46.49	Bill 100714360 with invoicesSum = 0 and invoiceCount = 0 was handled on Stage2!\n"
            + "02-DEC-2020 12.46.49	Bill 100714359 with invoicesSum = 1032415.52 and invoiceCount = 465 was handled on Stage2!\n"
            + "02-DEC-2020 12.46.49	Bill 100714358 with invoicesSum = 995062.87 and invoiceCount = 465 was handled on Stage2!\n"
            + "02-DEC-2020 12.46.49	Bill 100714357 with invoicesSum = 793911.66 and invoiceCount = 617 was handled on Stage2!\n"
            + "02-DEC-2020 12.46.49	Bill 100714356 with invoicesSum = 235067.04 and invoiceCount = 4 was handled on Stage2!";

    public static void analyze() {
        Pattern pattern = Pattern.compile("^(.+?)	([\\w\\s]+?)(\\d+?)\\swith invoicesSum = (.+?) and invoiceCount = (.+?) .+$");
        Map<Date, List<SumBean>> sumBeanMap = Arrays.stream(billSum.split("\n")).map(str -> {
            Matcher matcher = pattern.matcher(str);
            if (matcher.find()) {
                Date date;
                try {
                    date = new SimpleDateFormat("dd-MMM-yyyy HH.mm.ss").parse(matcher.group(1));
                } catch (ParseException ex) {
                    throw new RuntimeException(ex);
                }
                String billStr = matcher.group(2);
                Long billId = Long.valueOf(matcher.group(3));
                BigDecimal invoiceSum = BigDecimal.valueOf(Double.valueOf(matcher.group(4)));
                Long invoiceCount = Long.valueOf(matcher.group(5));
                return new SumBean(date, billId, billStr.contains("Unknown"), invoiceSum, invoiceCount);
            }
            return null;
        }
        ).collect(Collectors.groupingBy(SumBean::getDate));
        Date minDate = sumBeanMap.keySet().stream().min((d1, d2) -> d1.compareTo(d2)).get();
        ArrayList<Date> dateKeys = new ArrayList<>(sumBeanMap.keySet());
        Collections.sort(dateKeys);
        Set<Long> billIdSet = sumBeanMap.values().stream().flatMap(obj -> obj.stream()).map(SumBean::getBillId).collect(Collectors.toSet());
        for (int i = 0; i < dateKeys.size(); i++) {
            Date date = dateKeys.get(i);
            List<SumBean> sumBeanList = sumBeanMap.get(date);
            Map<Long, SumBean> map = sumBeanList.stream().collect(Collectors.toMap(SumBean::getBillId, obj -> obj));
            BigDecimal billSum = BigDecimal.ZERO;
            Long invoiceCount = 0L;
            for (Long billId : billIdSet) {
                SumBean sumBean = null;
                if (map.containsKey(billId)) {
                    sumBean = map.get(billId);
                } else {
                    for (int j = i - 1; j >= 0; j--) {
                        Date previousDate = dateKeys.get(j);
                        List<SumBean> previousSumBeanList = sumBeanMap.get(previousDate);
                        if (previousSumBeanList.stream().anyMatch(obj -> obj.getBillId().equals(billId))) {
                            sumBean = previousSumBeanList.stream().filter(obj -> obj.getBillId().equals(billId)).findFirst().get();
                            break;
                        } else {
                            continue;
                        }
                    }
                }
                billSum = billSum.add(sumBean.getSum());
                invoiceCount += sumBean.getInvoiceCount();
            }
            System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date) + " billSum = " + billSum.toString() + " invoiceCount = " + invoiceCount + "!");
        }
    }

    static class SumBean {

        private final Date date;
        private final Long billId;
        private final boolean unknown;
        private final BigDecimal sum;
        private final Long invoiceCount;

        public SumBean(Date date, Long billId, boolean unknown, BigDecimal sum, Long invoiceCount) {
            this.date = date;
            this.billId = billId;
            this.unknown = unknown;
            this.sum = sum;
            this.invoiceCount = invoiceCount;
        }

        public Date getDate() {
            return date;
        }

        public Long getBillId() {
            return billId;
        }

        public boolean isUnknown() {
            return unknown;
        }

        public BigDecimal getSum() {
            return sum;
        }

        public Long getInvoiceCount() {
            return invoiceCount;
        }

    }
}
