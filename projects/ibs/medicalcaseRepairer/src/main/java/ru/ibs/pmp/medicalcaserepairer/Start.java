package ru.ibs.pmp.medicalcaserepairer;

import java.text.ParseException;
import java.util.Arrays;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author NAnishhenko
 */
public class Start {

    private static final Mode mode = Mode.ParcelUpdate;

    @Autowired
    protected DbInit dbInit;

    public static void main(String args[]) throws Exception {
//        handleMedicalCases(args);
        handleParcels(args);
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
