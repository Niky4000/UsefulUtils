//package ru.ibs.updater.configs;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import org.springframework.beans.factory.config.PropertiesFactoryBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.ImportResource;
//import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
//import org.springframework.core.io.InputStreamResource;
//
///**
// *
// * @author NAnishhenko
// */
//@org.springframework.context.annotation.Configuration
//@ImportResource("file:src/main/resources/module.xml")
//@ComponentScan("ru.ibs.updater")
//public class MainConfiguration {
//
////    @Bean
////    public PropertiesFactoryBean myProperties() throws FileNotFoundException {
////        String configPath = System.getProperty("pmp.config.path");
////        PropertiesFactoryBean myProperties = new PropertiesFactoryBean();
//////        myProperties.setLocation(new InputStreamResource(new FileInputStream(new File((configPath == null ? "" : new File(configPath).getAbsolutePath()) + "/updater.cfg"))));
////        myProperties.setLocation(new InputStreamResource(new FileInputStream(new File((configPath == null ? "" : new File(configPath).getAbsolutePath()) + "/runtime.properties"))));
////        return myProperties;
////    }
////
////    @Bean
////    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
////        return new PropertySourcesPlaceholderConfigurer();
////    }
//}
