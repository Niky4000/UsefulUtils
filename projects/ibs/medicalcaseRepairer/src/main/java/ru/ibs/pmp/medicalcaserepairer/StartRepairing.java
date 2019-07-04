package ru.ibs.pmp.medicalcaserepairer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.ibs.pmp.module.pmp.bill.recreate.RecreateUtils;
import ru.ibs.pmp.module.pmp.bill.recreate.bean.StopwatchBean;
import ru.ibs.pmp.module.pmp.bill.recreate.stages.RecreateCommon;

/**
 * @author NAnishhenko
 */
public class StartRepairing {

    private static final Mode mode = Mode.ParcelUpdate;

    @Autowired
    protected DbInit dbInit;

    public static void main(String args[]) throws Exception {
//        handleMedicalCases(args);
//        handleParcels(args);
        handleParcelsInvoices(args);
//        handlePmpFlkChecks();
    }

    private static void handlePmpFlkChecks() throws Exception {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("repair_module.xml");
        SqlRepresenter sqlRepresenter = applicationContext.getBean(SqlRepresenter.class);
        sqlRepresenter.representPmpFlkChecksAsInserts();
    }

    // Usage: [period] [date_from] [date_to]
    // Usage example: 2018-10 2018-11-09_10:00:00 2018-11-09_20:00:00
    private static void handleParcelsInvoices(String[] args) throws ParseException, InterruptedException {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("repair_module.xml");
        ParcelInvoiceRepairer parcelInvoiceRepairer = applicationContext.getBean(ParcelInvoiceRepairer.class);
        RecreateCommon recreateCommon = applicationContext.getBean(RecreateCommon.class);
        RecreateUtils recreateUtils = applicationContext.getBean(RecreateUtils.class);
        DbInit dbInit = applicationContext.getBean(DbInit.class);
        StopwatchBean globalStopWatch = recreateCommon.createStopWatch();
        parcelInvoiceRepairer.init();
        String periodStr = (String) args[0];
        String dateFromStr = (String) args[1];
        String dateToStr = (String) args[2];
        Date period = new SimpleDateFormat("yyyy-MM").parse(periodStr);
        Date dateFrom = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").parse(dateFromStr);
        Date dateTo = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").parse(dateToStr);

        LocalDateTime dateFromTime = LocalDateTime.ofInstant(dateFrom.toInstant(), ZoneId.systemDefault());
        LocalDateTime dateToTime = LocalDateTime.ofInstant(dateTo.toInstant(), ZoneId.systemDefault());
        long hours = dateFromTime.until(dateToTime, ChronoUnit.HOURS);
        if (hours > 1) {
            for (long i = 0L; i < hours; i++) {
                LocalDateTime localDateTo = dateToTime.minus(i, ChronoUnit.HOURS);
                LocalDateTime localDateFrom = dateToTime.minus(i + 1, ChronoUnit.HOURS);
                Date to = Date.from(localDateTo.atZone(ZoneId.systemDefault()).toInstant());
                Date from = Date.from(localDateFrom.atZone(ZoneId.systemDefault()).toInstant());
                parcelInvoiceRepairer.repair(period, from, to);
            }
        } else {
            parcelInvoiceRepairer.repair(period, dateFrom, dateTo);
        }
        dbInit.finalizationActions(globalStopWatch, recreateUtils.getBillStatistics().getId());
    }

    private static void handleParcels(String[] args) throws ParseException, InterruptedException {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("repair_module.xml");
        ParcelUpdater parcelUpdater = applicationContext.getBean(ParcelUpdater.class);
        parcelUpdater.init();
        String arg1 = (String) args[0];
        String arg2 = (String) args[1];
        int year = Integer.valueOf(arg1.substring(0, arg1.indexOf("-")));
        int month = Integer.valueOf(arg1.substring(arg1.indexOf("-") + 1));
        int endYear = Integer.valueOf(arg2.substring(0, arg2.indexOf("-")));
        int endMonth = Integer.valueOf(arg2.substring(arg2.indexOf("-") + 1));
        parcelUpdater.updateParcels(year, month, endYear, endMonth);
    }

    private static void handleMedicalCases(String[] args) throws Exception, BeansException {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("repair_module.xml");
        MedicalCaseRepairer medicalCaseRepairer = applicationContext.getBean(MedicalCaseRepairer.class);

        boolean anyMatch = Arrays.stream(args).anyMatch(arg -> arg.equals("-d"));
        if (anyMatch) {
            medicalCaseRepairer.deleteCacheFile();
        }
        Long versionNumber = null;
        if (args.length == 1) {
            try {
                versionNumber = Long.valueOf(args[0]);
            } catch (Exception e) {
                // Ignore Exception!
            }
        }
        if (versionNumber == null) {
            medicalCaseRepairer.repairMedicalCase();
        } else {
            medicalCaseRepairer.handleSingleVersion(versionNumber);
        }
    }

}
