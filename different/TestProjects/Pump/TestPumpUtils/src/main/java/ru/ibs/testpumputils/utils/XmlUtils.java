package ru.ibs.testpumputils.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

/**
 * @author NAnishhenko
 */
public class XmlUtils {

    public static String jaxbObjectToXML(Object obj, String tag) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            JAXBContext jaxbContext = JAXBContext.newInstance(obj.getClass());
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            JAXBElement jaxbElement = new JAXBElement(new QName("", tag), obj.getClass(), obj);
            jaxbMarshaller.marshal(jaxbElement, byteArrayOutputStream);
            return byteArrayOutputStream.toString().replace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>", "");
        } catch (JAXBException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T unmarshall(byte[] bytes, Class<T> objClass) throws JAXBException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        JAXBContext jaxbContext = JAXBContext.newInstance(objClass);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        T obj = (T) jaxbUnmarshaller.unmarshal(byteArrayInputStream);
        return obj;
    }
}
