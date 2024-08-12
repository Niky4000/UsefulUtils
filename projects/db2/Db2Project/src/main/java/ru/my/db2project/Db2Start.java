package ru.my.db2project;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.ibs.pmp.api.model.BillStatisticsTime;
import ru.my.db2project.config.H2Config;

/**
 * @author NAnishhenko
 */
public class Db2Start {

    public static void main(String[] args) {
        System.out.println("Hello World!!!");
//        final String dataBaseFilePath = "D:\\tmp\\h2\\h2\\";
        final String dataBaseFilePath = "D:\\tmp\\h2\\h2\\";
        final String databaseFilePath = new File(dataBaseFilePath + H2Config.H2_OBJECT_NAME).getAbsolutePath();
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("module_simple.xml");
        H2Config h2Config = new H2Config(applicationContext) {
            @Override
            protected String getAndInitDatabaseFilePath() {
                return databaseFilePath;
            }

            @Override
            public String getPath() {
                return dataBaseFilePath;
            }
        };
        h2Config.init();
        h2Config.insertIntoDatabase(createData());
        h2Config.readDatabase();
        h2Config.deleteH2Database(true);
    }

    private static List<BillStatisticsTime> createData() {
        List<BillStatisticsTime> billStatisticsTimeList = new ArrayList<>();
        billStatisticsTimeList.add(createBillStatisticsTime(777L, "Hello World!"));
        billStatisticsTimeList.add(createBillStatisticsTime(888L, "Hello World2!"));
        return billStatisticsTimeList;
    }

    private static BillStatisticsTime createBillStatisticsTime(long alapsedTime, String description) {
        BillStatisticsTime billStatisticsTime = new BillStatisticsTime();
        billStatisticsTime.setAlapsedTime(alapsedTime);
        billStatisticsTime.setOperationDescription(description);
        billStatisticsTime.setCreated(new Date());
        return billStatisticsTime;
    }
}
