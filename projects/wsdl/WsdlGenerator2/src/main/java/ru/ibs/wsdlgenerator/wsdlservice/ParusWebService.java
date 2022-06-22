package ru.ibs.wsdlgenerator.wsdlservice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.datatype.XMLGregorianCalendar;
import mgfoms.ru.ws.FindMailGwLogs202110Response;
import mgfoms.ru.ws.GetAttachmentRequest;
import mgfoms.ru.ws.GetAttachmentResponse;
import mgfoms.ru.ws.GetDocListResponse;
import mgfoms.ru.ws.GetDocResponse;
import mgfoms.ru.ws.GetMailGw202110Request;
import mgfoms.ru.ws.RegisterChangeResponse;
import mgfoms.ru.ws.RegisterReceiptResponse;
import mgfoms.ru.ws.UploadPaymentRequest;
import mgfoms.ru.ws.WsAuthInfo;

/**
 *
 * @author me
 */
@WebService(serviceName = "ParusPortService", portName = "ParusPortSoap11", targetNamespace = "http://ws.ru.mgfoms")
public class ParusWebService {

	@WebMethod(operationName = "registerChangeRequest", action = "registerChange")
	public RegisterChangeResponse registerChange(@WebParam(name = "authInfo", targetNamespace = "http://ws.ru.mgfoms") WsAuthInfo authInfo, @WebParam(name = "action", targetNamespace = "http://ws.ru.mgfoms") String action, @WebParam(name = "docId", targetNamespace = "http://ws.ru.mgfoms") String docId,
			@WebParam(name = "category", targetNamespace = "http://ws.ru.mgfoms") String category, @WebParam(name = "nschet", targetNamespace = "http://ws.ru.mgfoms") String nschet, @WebParam(name = "C_OKATO1", targetNamespace = "http://ws.ru.mgfoms") String cokato1,
			@WebParam(name = "OKATO_OMS", targetNamespace = "http://ws.ru.mgfoms") String okatooms, @WebParam(name = "year", targetNamespace = "http://ws.ru.mgfoms") int year, @WebParam(name = "month", targetNamespace = "http://ws.ru.mgfoms") int month) {
		return null;
	}

	@WebMethod(operationName = "registerReceiptRequest", action = "registerReceipt")
	public RegisterReceiptResponse registerReceipt(@WebParam(name = "authInfo", targetNamespace = "http://ws.ru.mgfoms") WsAuthInfo authInfo, @WebParam(name = "docId", targetNamespace = "http://ws.ru.mgfoms") String docId,
			@WebParam(name = "year", targetNamespace = "http://ws.ru.mgfoms") int year, @WebParam(name = "month", targetNamespace = "http://ws.ru.mgfoms") int month,
			@WebParam(name = "success", targetNamespace = "http://ws.ru.mgfoms") boolean success, @WebParam(name = "description", targetNamespace = "http://ws.ru.mgfoms") String description, @WebParam(name = "whenSigned", targetNamespace = "http://ws.ru.mgfoms") XMLGregorianCalendar whenSigned,
			@WebParam(name = "packNum", targetNamespace = "http://ws.ru.mgfoms") String packNum) {
		return null;
	}

	@WebMethod(operationName = "getDocListRequest", action = "getDocList")
	public GetDocListResponse getDocList(@WebParam(name = "authInfo", targetNamespace = "http://ws.ru.mgfoms") WsAuthInfo authInfo, @WebParam(name = "C_OKATO1", targetNamespace = "http://ws.ru.mgfoms") String cokato1,
			@WebParam(name = "OKATO_OMS", targetNamespace = "http://ws.ru.mgfoms") String okatooms, @WebParam(name = "year", targetNamespace = "http://ws.ru.mgfoms") int year, @WebParam(name = "month", targetNamespace = "http://ws.ru.mgfoms") int month) {
		return null;
	}

	@WebMethod(operationName = "getDocRequest", action = "getDoc")
	public GetDocResponse getDoc(@WebParam(name = "authInfo", targetNamespace = "http://ws.ru.mgfoms") WsAuthInfo authInfo, @WebParam(name = "docId", targetNamespace = "http://ws.ru.mgfoms") String docId,
			@WebParam(name = "year", targetNamespace = "http://ws.ru.mgfoms") int year, @WebParam(name = "month", targetNamespace = "http://ws.ru.mgfoms") int month
	) {
		return null;
	}

	@WebMethod(operationName = "getMailGwFin", action = "getMailGwFin")
	public FindMailGwLogs202110Response getMailGwFin(@WebParam(name = "mailGw202110Request") GetMailGw202110Request mailGw202110Request) {
		return null;
	}

	@WebMethod(operationName = "uploadPayment", action = "uploadPayment")
	public long uploadPayment(@WebParam(name = "uploadPaymentRequest") UploadPaymentRequest uploadPaymentRequest) {
		return 0L;
	}

	@WebMethod(operationName = "getAttachment", action = "getAttachment")
	public GetAttachmentResponse getAttachment(@WebParam(name = "getAttachmentRequest") GetAttachmentRequest request) {
//		AttachDto responseService = smoIOService.getAttachment(ConverterWS.toGetAttachmentRequest(request, authInfo));
//		return ConverterWS.toAttachment(responseService);
		return null;
	}
}
