package ru.ibs.testpumputils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import ru.ibs.pmp.auth.dto.WsAuthInfo;
import ru.ibs.pmp.pmp.ws.CreateUpdateHospCaseRequestBean190601;

/**
 * @author NAnishhenko
 */
public class Utils10733 {

    public static void start(SessionFactory sessionFactory) throws ParseException {
        Session session = sessionFactory.openSession();
        try {
            java.sql.Clob parameters = (java.sql.Clob) session.createSQLQuery("select parameters from pmp_auth.audit_entries where datetime between :startDate and :endDate and id=:id")
                    .setParameter("startDate", new SimpleDateFormat("yyyy-MM-dd").parse("2019-10-17"))
                    .setParameter("endDate", new SimpleDateFormat("yyyy-MM-dd").parse("2019-10-19"))
                    .setParameter("id", 1571377913140L)
                    .uniqueResult();
            String parametersString = parameters.getSubString(1, Long.valueOf(parameters.length()).intValue()).replace("\n", "");
            parametersString = parametersString.replaceAll("\\s+([^\\s])", " $1").replace("{ ", "{");
            String authInfoKey = "authInfo = ";
            String createUpdateHospCaseRequestKey = "createUpdateHospCaseRequest = ";
            String authInfoString = parametersString.substring(parametersString.indexOf(authInfoKey) + authInfoKey.length(), parametersString.indexOf("}") + 1);
            String createUpdateHospCaseRequestString = parametersString.substring(parametersString.indexOf(createUpdateHospCaseRequestKey) + createUpdateHospCaseRequestKey.length(), parametersString.lastIndexOf("}") + 1);
            WsAuthInfo wsAuthInfo = deSerializeObject(authInfoString, new TypeReference<WsAuthInfo>() {
            });
            CreateUpdateHospCaseRequestBean190601 createUpdateHospCaseRequestBean190601 = deSerializeObject(createUpdateHospCaseRequestString, new TypeReference<CreateUpdateHospCaseRequestBean190601>() {
            });

            createUpdateHospCaseRequestBean190601.getHospitalDTO().getProgress().get(0).getOperations().get(0).setAnethesiaType(1);
            createUpdateHospCaseRequestBean190601.getHospitalDTO().setId(141564450359L);

            String wsAuthInfoXml = serializeRequest(WsAuthInfo.class, wsAuthInfo, "authInfo");
            String createUpdateHospCaseRequestBean190601Xml = serializeRequest(CreateUpdateHospCaseRequestBean190601.class, createUpdateHospCaseRequestBean190601, "createUpdateHospCaseRequest");

            System.out.println(wsAuthInfoXml + createUpdateHospCaseRequestBean190601Xml);
        } catch (Exception e) {
            e.printStackTrace();
            session.close();
        }
    }

    public static <T> String serializeRequest(Class<T> class_, T obj, String rootObjectName) {
        String ret = null;
        try {
            StringWriter writer = new StringWriter();
            JAXBContext context = JAXBContext.newInstance(class_);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            QName qName = new QName(rootObjectName);
            JAXBElement<T> root = new JAXBElement<T>(qName, class_, obj);
            marshaller.marshal(root, writer);
            ret = new String(writer.toString().replace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>", ""));
        } catch (JAXBException je) {
            je.printStackTrace();
        }
        return ret;
    }

//public String toXML(T object) throws JAXBException {
//  StringWriter stringWriter = new StringWriter();
//
//  JAXBContext jaxbContext = JAXBContext.newInstance(T.class);
//  Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
//
//  // format the XML output
//  jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//
//  QName qName = new QName("com.yourModel.t", "object");
//  JAXBElement<T> root = new JAXBElement<Bbb>(qName, T.class, object);
//
//  jaxbMarshaller.marshal(root, stringWriter);
//
//  String result = stringWriter.toString();
//  LOGGER.info(result);
//  return result;
//}
    private static <T> T deSerializeObject(String data, TypeReference<T> ref) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            T readValue = objectMapper.readValue(data, ref);
            return readValue;
        } catch (Exception e) {
            e.printStackTrace();
//            throw new RuntimeException(e);
            return null;
        }
    }
}
