package ru.ibs.docxtestproject;

import com.github.fluent.hibernate.cfg.scanner.EntityScanner;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class StartCreatingDocx {

	public static void main(String[] args) throws Exception {
//		MedicalAndEconomicControlActWordLauncher.start(args, () -> buildSessionFactory());
		RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlWordLauncher.start(args, null);
	}

	public static SessionFactory buildSessionFactory() {
		try {
			Properties p = new Properties();
			p.load(new FileInputStream(new File(System.getProperty("pmp.config.path"))));
			Configuration configuration = new Configuration();
			configuration.setProperty("hibernate.connection.url", p.getProperty("runtime.smo.db.url"));
			configuration.setProperty("hibernate.connection.username", p.getProperty("runtime.smo.db.username"));
			configuration.setProperty("hibernate.connection.password", p.getProperty("runtime.smo.db.password"));
			EntityScanner.scanPackages("ru.ibs.docxtestproject.bean").addTo(configuration);
			configuration.configure();
			SessionFactory sessionFactory = configuration.buildSessionFactory();
			return sessionFactory;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
