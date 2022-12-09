package ru.ibs.docxtestproject;

import com.github.fluent.hibernate.cfg.scanner.EntityScanner;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Properties;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ru.ibs.docxtestproject.bean.MedicalAndEconomicControlActWordFirstTableBean;
import ru.ibs.docxtestproject.bean.MedicalAndEconomicControlActWordHeadBean;
import ru.ibs.docxtestproject.bean.MedicalAndEconomicControlActWordSecondTableBean;
import ru.ibs.testpumputils.utils.FieldUtil;

public class StartCreatingDocx {

	public static void main(String[] args) throws Exception {
		SessionFactory sessionFactory = buildSessionFactory();
		try {
			MedicalAndEconomicControlActWordHeadBean medicalAndEconomicControlActWordHeadBean = getMedicalAndEconomicControlActWordHeadBean();
			Collection<MedicalAndEconomicControlActWordFirstTableBean> medicalAndEconomicControlActWordFirstTableBeanCollection = getMedicalAndEconomicControlActWordFirstTableBeanCollection();
			Collection<MedicalAndEconomicControlActWordSecondTableBean> medicalAndEconomicControlActWordSecondTableBeanCollection = getMedicalAndEconomicControlActWordSecondTableBeanCollection();
			MedicalAndEconomicControlActWord medicalAndEconomicControlActWord = new MedicalAndEconomicControlActWord();
			FieldUtil.setField(medicalAndEconomicControlActWord, sessionFactory, "sessionFactory");
			medicalAndEconomicControlActWord.create(1962L, new SimpleDateFormat("yyyy-MM-dd").parse("2022-02-01"), 1415550L);
//			new TestTable().create();
		} finally {
			sessionFactory.close();
		}
	}

	private static MedicalAndEconomicControlActWordHeadBean getMedicalAndEconomicControlActWordHeadBean() {
		MedicalAndEconomicControlActWordHeadBean head = new MedicalAndEconomicControlActWordHeadBean();
		head.setServiceSumAll(88888.88d);
//		head.setDateCrt(new Date());
//		head.setLastDay("2022.12.12");
//		head.setMoName("Очень классная ЛПУ");
//		head.setNum(77);
//		head.setPeriodStr("декабрь 2022 года");
//		head.setSmoName("Очень классаная СМО");
		return head;
	}

	private static Collection<MedicalAndEconomicControlActWordFirstTableBean> getMedicalAndEconomicControlActWordFirstTableBeanCollection() {
		Collection<MedicalAndEconomicControlActWordFirstTableBean> medicalAndEconomicControlActWordFirstTableBeanCollection = new ArrayList<>();
		medicalAndEconomicControlActWordFirstTableBeanCollection.add(createMedicalAndEconomicControlActWordFirstTableBean(2L, new Date(), 1795L, 77777777L, "prof2", "uslOk2", 500.54d, 524.54d, 24d, 24d, 20d, 600.82d, "lastDay", "Z58.0"));
		medicalAndEconomicControlActWordFirstTableBeanCollection.add(createMedicalAndEconomicControlActWordFirstTableBean(4L, new Date(), 1876L, 88888888L, "prof4", "uslOk4", 2500.54d, 2524.54d, 224d, 224d, 220d, 2600.82d, "lastDay2", "Z58.2"));
		return medicalAndEconomicControlActWordFirstTableBeanCollection;
	}

	private static MedicalAndEconomicControlActWordFirstTableBean createMedicalAndEconomicControlActWordFirstTableBean(Long rn, Date period, Long lpuId, Long parcelId, String prof, String uslOk, Double oplQt, Double oplSum, Double errQt, Double errSum, Double itogQt, Double itogSum, String lastDay, String ds) {
		MedicalAndEconomicControlActWordFirstTableBean bean = new MedicalAndEconomicControlActWordFirstTableBean();
		bean.setRn(rn);
		bean.setPeriod(period);
		bean.setLpuId(lpuId);
		bean.setParcelId(parcelId);
		bean.setProf(prof);
		bean.setUslOk(uslOk);
		bean.setOplQt(oplQt);
		bean.setOplSum(oplSum);
		bean.setErrQt(errQt);
		bean.setErrSum(errSum);
		bean.setItogQt(itogQt);
		bean.setItogSum(itogSum);
//		bean.setLastDay(lastDay);
//		bean.setDs(ds);
		return bean;
	}

	private static Collection<MedicalAndEconomicControlActWordSecondTableBean> getMedicalAndEconomicControlActWordSecondTableBeanCollection() {
		Collection<MedicalAndEconomicControlActWordSecondTableBean> medicalAndEconomicControlActWordSecondTableBeanCollection = new ArrayList<>();
		medicalAndEconomicControlActWordSecondTableBeanCollection.add(createMedicalAndEconomicControlActWordSecondTableBean(2L, new Date(), 1795L, 777777777L, "222222", "snPol", "uslOk", "dateBegin", "dateEnd", "profCode", "prof", "code", 20L, "errCode", "errCode1", "errCode2", "errCode3", "errCode4", "errCode5", "sankSum", 22d, "44", "lastDay", "Z58.4"));
		medicalAndEconomicControlActWordSecondTableBeanCollection.add(createMedicalAndEconomicControlActWordSecondTableBean(4L, new Date(), 1876L, 888888888L, "444444", "snPol2", "uslOk2", "dateBegin2", "dateEnd2", "profCode2", "prof2", "code2", 220L, "errCode2", "2errCode1", "2errCode2", "2errCode3", "2errCode4", "2errCode5", "sankSum2", 222d, "444", "lastDay2", "Z58.8"));
		return medicalAndEconomicControlActWordSecondTableBeanCollection;
	}

	private static MedicalAndEconomicControlActWordSecondTableBean createMedicalAndEconomicControlActWordSecondTableBean(Long rn, Date period, Long lpuId, Long parcelId, String recid, String snPol, String uslOk, String dateBegin, String dateEnd, String profCode, String prof, String code, Long countErr, String errCode, String errCode1, String errCode2, String errCode3, String errCode4, String errCode5, String sankSum, Double errSum, String fineSum, String lastDay, String ds) {
		MedicalAndEconomicControlActWordSecondTableBean bean = new MedicalAndEconomicControlActWordSecondTableBean();
		bean.setRn(rn);
		bean.setPeriod(period);
		bean.setLpuId(lpuId);
		bean.setParcelId(parcelId);
		bean.setRecid(recid);
		bean.setSnPol(snPol);
		bean.setUslOk(uslOk);
		bean.setDateBegin(dateBegin);
		bean.setDateEnd(dateEnd);
		bean.setProfCode(profCode);
		bean.setProf(prof);
		bean.setCode(code);
		bean.setCountErr(countErr);
		bean.setErrCode(errCode);
		bean.setErrCode1(errCode1);
		bean.setErrCode2(errCode2);
		bean.setErrCode3(errCode3);
		bean.setErrCode4(errCode4);
		bean.setErrCode5(errCode5);
		bean.setSankSum(sankSum);
		bean.setErrSum(errSum);
		bean.setFineSum(fineSum);
//		bean.setLastDay(lastDay);
		bean.setDs(ds);
		return bean;
	}

	public static SessionFactory buildSessionFactory() throws FileNotFoundException, IOException {
		// Create the SessionFactory from hibernate.cfg.xml
		Properties p = new Properties();
		p.load(new FileInputStream(new File(System.getProperty("pmp.config.path"))));
		Configuration configuration = new Configuration();
		configuration.setProperty("hibernate.connection.url", p.getProperty("runtime.pmp.db.url"));
		configuration.setProperty("hibernate.connection.username", p.getProperty("runtime.pmp.db.username"));
		configuration.setProperty("hibernate.connection.password", p.getProperty("runtime.pmp.db.password"));
		EntityScanner.scanPackages("ru.ibs.docxtestproject.bean").addTo(configuration);
		configuration.configure();
//        ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
//        return configuration.buildSessionFactory(serviceRegistry);
		SessionFactory sessionFactory = configuration.buildSessionFactory();
		return sessionFactory;
	}
}
