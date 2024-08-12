/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.idataconnect.jdbfdriver.testjson;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import ru.ibs.pmp.arm.oms.model.HospitalDTO;

/**
 *
 * @author NAnishhenko
 */
public class TestJson {

    public static void main(String[] args) throws IOException {
//        Object ob = new ObjectMapper().readValue("", Object.class);
        HospitalDTO ob2 = new ObjectMapper().readValue("{\"id\":\"74700\",\"medCardNumber\":\"5\",\"doctorJobId\":23255,\"caseDate\":\"2016-02-24T06:00:00Z\",\"cureResult\":\"101\",\"patientId\":\"593161512\",\"injury\":null,\"specialCase\":\"0\",\"moId\":null,\"caseNumber\":null,\"directionDate\":null,\"directionLpuId\":null,\"vmpNumber\":null,\"directionDoctorSpec\":null,\"cureEndDate\":\"2016-03-03T03:00:00Z\",\"hospitalizationType\":\"1\",\"smpNumber\":null,\"hospAnnualType\":\"1\",\"interraptMs\":\"0\",\"newbornWeightGr\":null,\"directionNumber\":null,\"medcaseDirectionDTOs\":[],\"diagnosisSend\":null,\"diagnosisReception\":\"A01.0\",\"clinicalDiagnosis\":{\"main\":\"A03.3\",\"additional\":null,\"accompanying\":null,\"death\":null},\"pathologicalDiagnosis\":{\"main\":null,\"additional\":null,\"accompanying\":null,\"death\":null},\"diseaseOutcome\":\"101\",\"progress\":[{\"id\":10250,\"doctorId\":\"29951\",\"doctorJobId\":23253,\"departmentId\":\"18551\",\"msCode\":\"051054\",\"diagnosisCode\":\"A03.3\",\"interruptCode\":\"0\",\"paymentSource\":\"1\",\"startDate\":\"2016-02-24T04:00:00Z\",\"endDate\":\"2016-03-03T09:00:00Z\",\"operations\":[{\"id\":1,\"doctorId\":29951,\"doctorJobId\":23253,\"technologyType\":\"A03.16.001.001\",\"date\":\"2016-03-10T12:00:00Z\",\"anethesiaType\":\"1\",\"paymentSource\":\"1\",\"isMain\":true,\"useSpecLaser\":true,\"userSpecEndoscopic\":true,\"useSpecCryogenic\":true}],\"specialCase\":\"0\",\"skipInvoice\":false}],\"patientType\":\"0\",\"canEdit\":true}", HospitalDTO.class);
        HospitalDTO ob = new ObjectMapper().readValue("{\"id\":\"128762\",\"medCardNumber\":\"7453\",\"doctorJobId\":\"21064\",\"caseDate\":\"2016-03-03T00:00:00Z\",\"cureResult\":\"201\",\"patientId\":\"25572671\",\"injury\":null,\"specialCase\":\"0\",\"moId\":null,\"caseNumber\":null,\"directionDate\":\"2016-01-15T00:00:00Z\",\"directionLpuId\":\"1933\",\"vmpNumber\":null,\"directionDoctorSpec\":null,\"cureEndDate\":\"2016-03-06T02:59:59Z\",\"hospitalizationType\":\"1\",\"smpNumber\":null,\"hospAnnualType\":\"1\",\"interraptMs\":\"0\",\"newbornWeightGr\":null,\"directionNumber\":\"610\",\"medcaseDirectionDTOs\":[],\"diagnosisSend\":null,\"diagnosisReception\":\"N97.1\",\"clinicalDiagnosis\":{\"main\":\"N97.1\",\"additional\":null,\"accompanying\":null,\"death\":null},\"pathologicalDiagnosis\":{\"main\":null,\"additional\":null,\"accompanying\":null,\"death\":null},\"diseaseOutcome\":\"201\",\"progress\":[{\"id\":7153,\"doctorId\":\"25693\",\"doctorJobId\":\"21064\",\"departmentId\":\"20400\",\"msCode\":\"097041\",\"diagnosisCode\":\"N97.1\",\"interruptCode\":\"0\",\"paymentSource\":\"1\",\"startDate\":\"2016-03-03T10:00:00Z\",\"endDate\":\"2016-03-03T15:00:00Z\",\"operations\":[],\"specialCase\":\"0\",\"skipInvoice\":false},\"patientType\":\"0\",\"canEdit\":true}", HospitalDTO.class);
        String hello="";
    }
}
