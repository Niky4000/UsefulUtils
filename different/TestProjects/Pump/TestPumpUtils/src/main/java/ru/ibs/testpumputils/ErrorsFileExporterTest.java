/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.testpumputils;

import java.lang.reflect.Proxy;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;
import org.hibernate.Session;
import ru.ibs.pmp.api.model.dbf.parcel.ErrorRecord;
import ru.ibs.testpumputils.interceptors.SqlRewriteInterceptorExt;
import ru.ibs.testpumputils.interfaces.SessionFactoryInterface;
import ru.ibs.testpumputils.interfaces.SessionFactoryInvocationHandler;

/**
 *
 * @author me
 */
public class ErrorsFileExporterTest {

    static SessionFactoryInterface sessionFactory;

    public static void test() throws Exception {
        sessionFactory = (SessionFactoryInterface) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SessionFactoryInterface.class}, new SessionFactoryInvocationHandler(TestPumpUtilsMain.buildSessionFactory(), new SqlRewriteInterceptorExt()));
        try {
	    Session session = sessionFactory.openSession();
	    try {
		List<Object[]> resultList = session.createSQLQuery("select period,mo_id,parcel_id,flk_version,file_name,recid,error_code,e_cod,e_ku,e_tip,refreason,et230,osn230,lpu_id,fil_id from table (pmp_smo1.pmp_ctrl_file.GET_TABLE(:period,:lpuId,:rev))").setParameter("period", new SimpleDateFormat("yyyy-MM-dd").parse("2020-05-01")).setParameter("lpuId", 4102L).setParameter("rev", 1008650L).list();
		List<ErrorRecord> errorRecordList = resultList.stream().map(objArray -> {
		    ErrorRecord errorRecord = new ErrorRecord();
		    errorRecord.setMoId(objArray[1] != null ? ((Number) objArray[1]).intValue() : null); // mo_id
		    errorRecord.setParcelId(objArray[2] != null ? ((Number) objArray[2]).longValue() : null); // parcel_id
		    errorRecord.setFile((String) objArray[4]); // file_name VARCHAR2(20)
		    errorRecord.setRecordId((String) objArray[5]); // recid VARCHAR2(20)
		    errorRecord.setReasonCode((String) objArray[6]); // error_code VARCHAR2(20)
		    errorRecord.setReplacementServiceCode(objArray[7] != null ? ((Number) objArray[7]).intValue() : null); // e_cod NUMBER
		    errorRecord.setReplacementServiceQuantity(objArray[8] != null ? ((Number) objArray[8]).intValue() : null); // e_ku NUMBER
		    errorRecord.setInterruptionTypeCode((String) objArray[9]); // e_tip VARCHAR2(20)
		    errorRecord.setFomsCode((String) objArray[10]); // refreason VARCHAR2(20)
		    errorRecord.setExpertiseStage(objArray[11] != null ? ((Number) objArray[11]).intValue() : null); // et230 NUMBER
		    errorRecord.setReasonCode((String) objArray[12]); // osn230 VARCHAR2(20)
		    return errorRecord;
		}).collect(Collectors.toList());
		System.out.println("Size = " + errorRecordList.size() + "!");
	    } finally {
		session.close();
	    }
        } finally {
            sessionFactory.cleanSessions();
            sessionFactory.close();
        }
//    PERIOD                      DATE          -- ПЕРИОД
//    , MO_ID                     NUMBER        -- МО
//    , PARCEL_ID                 NUMBER        -- PARCEL_ID
//    , FLK_VERSION               NUMBER        -- FLK_VERSION
//
//    , FILE_NAME                 VARCHAR2(20)  -- Имя файла, в котором обнаружена ошибка
//    , RECID                     VARCHAR2(20)  -- Идентификатор ошибочной записи по файлу
//    , ERROR_CODE                VARCHAR2(20)  -- Код ошибки
//    , E_COD                     NUMBER        -- Код услуги, принятой к оплате
//    , E_KU                      NUMBER        -- Количество услуг/фактических койко-дней, принятых к оплате
//    , E_TIP                     VARCHAR2(20)  -- Код типа прерывания МС, принятого к оплате
//    , REFREASON                 VARCHAR2(20)  -- Код ошибки по классификатору ФОМС F014
//    , ET230                     NUMBER        -- Этап экспертизы по приказу ФОМС № 230
//    , Osn230                    VARCHAR2(20)  -- Цифровая идентификации оснований для отказа в оплате медицинской помощи в свете приложения 8 к приказу ФФОМС №230
//
//    , LPU_ID                    NUMBER        -- Идентификатор МО
//    , FIL_ID                    NUMBER        -- Идентификатор филиала МО, счет которого признан ошибочным (по файлу счета)

    }
}
