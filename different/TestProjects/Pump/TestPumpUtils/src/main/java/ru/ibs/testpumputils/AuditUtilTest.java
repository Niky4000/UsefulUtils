package ru.ibs.testpumputils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import ru.ibs.pmp.api.model.RevisionEntity;
import ru.ibs.pmp.api.model.yar.HospDeptStay;
import ru.ibs.pmp.api.model.yar.HospReanimation;
import ru.ibs.pmp.api.model.yar.HospReanimationAud;
import ru.ibs.pmp.api.model.yar.SofaMeasure;
import ru.ibs.pmp.api.model.yar.SofaMeasureAud;
import ru.ibs.pmp.api.model.yar.SofaSysScore;
import ru.ibs.pmp.api.model.yar.SofaSysScoreAud;
import ru.ibs.pmp.features.bill.utils.CopyEntitiesUtil;

/**
 *
 * @author me
 */
public class AuditUtilTest {

    public static void test() throws Exception {
        Short revtype = (short) 3;
        final Long hospDeptStayId = 888L;
        RevisionEntity revinfo = new RevisionEntity();
        revinfo.setId(8888L);
        HospDeptStay hospDeptStay = new HospDeptStay();
        hospDeptStay.setId(hospDeptStayId);
        SofaMeasure sofaMeasure = new SofaMeasure();
        sofaMeasure.setId(7L);
        sofaMeasure.setMoId(1962L);
        sofaMeasure.setPeriod(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2022-05-01 12:00:00"));
        sofaMeasure.setHospDeptStay(hospDeptStay);
        sofaMeasure.setHospDeptstayId(hospDeptStayId);
        sofaMeasure.setMeasureDate(new Date());
        sofaMeasure.setMeasureSeq(8L);
        sofaMeasure.setScaleType((short) 8);
        sofaMeasure.setScore(22L);
        sofaMeasure.setVzst(54L);
        SofaMeasureAud sofaMeasureAud = new CopyEntitiesUtil<SofaMeasureAud, SofaMeasure>().copyEntities(sofaMeasure, revinfo, revtype);
        System.out.println();
        test2(sofaMeasure);
        test3(sofaMeasure);
    }

    public static void test2(SofaMeasure sofaMeasure) throws Exception {
        Short revtype = (short) 3;
        final Long hospDeptStayId = 888L;
        RevisionEntity revinfo = new RevisionEntity();
        revinfo.setId(8888L);
        HospDeptStay hospDeptStay = new HospDeptStay();
        hospDeptStay.setId(hospDeptStayId);
        SofaSysScore sofaSysScore = new SofaSysScore();
        sofaMeasure.setId(7L);
        sofaMeasure.setMoId(1962L);
        sofaMeasure.setPeriod(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2022-05-01 12:00:00"));
        sofaSysScore.setHospDeptstayId(hospDeptStayId);
        sofaSysScore.setMeasureId(44L);
        sofaSysScore.setScore(88888L);
        sofaSysScore.setSofaMeasure(sofaMeasure);
        sofaSysScore.setSystemCode("System Code");
        SofaSysScoreAud sofaSysScoreAud = new CopyEntitiesUtil<SofaSysScoreAud, SofaSysScore>().copyEntities(sofaSysScore, revinfo, revtype);
        System.out.println();
    }

    public static void test3(SofaMeasure sofaMeasure) throws ParseException {
        Short revtype = (short) 3;
        final Long hospDeptStayId = 888L;
        RevisionEntity revinfo = new RevisionEntity();
        revinfo.setId(8888L);
        HospDeptStay hospDeptStay = new HospDeptStay();
        hospDeptStay.setId(hospDeptStayId);
        HospReanimation hospReanimation = new HospReanimation();
        hospReanimation.setCalc(Boolean.FALSE);
        hospReanimation.setHospDeptStay(hospDeptStay);
        hospReanimation.setHospDeptStayId(hospDeptStayId);
        hospReanimation.setId(777L);
        hospReanimation.setMeasureId(888L);
        hospReanimation.setMeasureSeq(222L);
        hospReanimation.setMoId(1795L);
        hospReanimation.setPeriod(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2022-05-01 12:00:00"));
        hospReanimation.setSofaCode("DD");
        hospReanimation.setSofaValue(BigDecimal.TEN);
        hospReanimation.setSofaMeasure(sofaMeasure);
        HospReanimationAud hospReanimationAud = new CopyEntitiesUtil<HospReanimationAud, HospReanimation>().copyEntities(hospReanimation, revinfo, revtype);
        System.out.println();
    }
}
