package ru.ibs.testpumputils.utils;

import com.github.fluent.hibernate.cfg.scanner.EntityScanner;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

/**
 * @author NAnishhenko
 */
public class ObjectUtils {

    public static SessionFactory buildAuthSessionFactory() throws FileNotFoundException, IOException {
        Properties p = new Properties();
        p.load(new FileInputStream(new File(System.getProperty("pmp.config.path"))));
        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connection.url", p.getProperty("runtime.smo.db.url"));
        configuration.setProperty("hibernate.connection.username", p.getProperty("runtime.pmp.auth.schema.name"));
        configuration.setProperty("hibernate.connection.password", p.getProperty("runtime.pmp.auth.schema.name"));
        EntityScanner.scanPackages("ru.ibs.pmp.auth.model").addTo(configuration);
        configuration.configure();
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        return sessionFactory;
    }

    public static LocalContainerEntityManagerFactoryBean getEntityManagerFactory() throws FileNotFoundException, IOException {
//        new org.apache.commons.dbcp2.BasicDataSource

        Properties p = new Properties();
        p.load(new FileInputStream(new File(System.getProperty("pmp.config.path"))));

        Map<String, String> properties = new HashMap<>();
        properties.put("javax.persistence.jdbc.driver", p.getProperty("db.driver"));
        properties.put("javax.persistence.jdbc.url", p.getProperty("runtime.pmp.db.url"));
        properties.put("javax.persistence.jdbc.user", p.getProperty("runtime.pmp.db.username")); //if needed
        properties.put("javax.persistence.jdbc.password", p.getProperty("runtime.pmp.db.password"));

        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
//        emf.setPersistenceProviderClass(org.eclipse.persistence.jpa.PersistenceProvider.class); //If your using eclipse or change it to whatever you're using
        emf.setJpaVendorAdapter(new org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter());
        emf.setPackagesToScan("ru.ibs.pmp.lpu.model.mo", "ru.ibs.pmp.auth.model", "ru.ibs.pmp.lpu.model.audit.mo", "ru.ibs.pmp.api.practitioners.model.practitioner", "ru.ibs.pmp.api.practitioners.model.audit.practitioner", "ru.ibs.pmp.smo.dto.pdf", "ru.ibs.pmp.smo.report.model"); //The packages to search for Entities, line required to avoid looking into the persistence.xml
//        emf.setPersistenceUnitName(SysConstants.SysConfigPU);
        emf.setPersistenceUnitName("SysConfigPU");
        emf.setJpaPropertyMap(properties);
//        emf.setLoadTimeWeaver(new ReflectiveLoadTimeWeaver()); //required unless you know what your doing
        emf.afterPropertiesSet();
        return emf;
    }

    public static LocalContainerEntityManagerFactoryBean getSmoEntityManagerFactory() throws FileNotFoundException, IOException {
        Properties p = new Properties();
        p.load(new FileInputStream(new File(System.getProperty("pmp.config.path"))));
        Map<String, String> properties = new HashMap<>();
        properties.put("javax.persistence.jdbc.driver", p.getProperty("db.driver"));
        properties.put("javax.persistence.jdbc.url", p.getProperty("runtime.smo.db.url"));
        properties.put("javax.persistence.jdbc.user", p.getProperty("runtime.smo.db.username")); //if needed
        properties.put("javax.persistence.jdbc.password", p.getProperty("runtime.smo.db.password"));
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setJpaVendorAdapter(new org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter());
        emf.setPackagesToScan("ru.ibs.pmp.lpu.model.mo", "ru.ibs.pmp.auth.model", "ru.ibs.pmp.lpu.model.audit.mo", "ru.ibs.pmp.api.practitioners.model.practitioner", "ru.ibs.pmp.api.practitioners.model.audit.practitioner", "ru.ibs.pmp.smo.dto.pdf", "ru.ibs.pmp.smo.report.model"); //The packages to search for Entities, line required to avoid looking into the persistence.xml
        emf.setPersistenceUnitName("SysConfigPU");
        emf.setJpaPropertyMap(properties);
        emf.afterPropertiesSet();
        return emf;
    }
}
