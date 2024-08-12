///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package ru.ibs.wsdlgenerator.wsdlservice;
//
//import javax.jws.WebService;
//import javax.jws.WebMethod;
//import javax.jws.WebParam;
//import mgfoms.ru.ws.FindMailGwLogs202110Response;
//import mgfoms.ru.ws.GetDocListRequest;
//import mgfoms.ru.ws.GetDocListResponse;
//import mgfoms.ru.ws.GetDocRequest;
//import mgfoms.ru.ws.GetDocResponse;
//import mgfoms.ru.ws.GetMailGw202110Request;
//import mgfoms.ru.ws.RegisterChangeRequest;
//import mgfoms.ru.ws.RegisterChangeResponse;
//import mgfoms.ru.ws.RegisterReceiptRequest;
//import mgfoms.ru.ws.RegisterReceiptResponse;
//
///**
// *
// * @author me
// */
//@WebService(serviceName = "ParusPortService", portName = "ParusPortSoap11", targetNamespace = "http://ws.ru.mgfoms")
//public class ParusWebService {
//
//	/**
//	 * This is a sample web service operation
//	 */
////	@WebMethod(operationName = "hello")
////	public String hello(@WebParam(name = "name") String txt) {
////		return "Hello " + txt + " !";
////	}
//
//	@WebMethod(operationName = "registerChange")
//	public RegisterChangeResponse registerChange(@WebParam(name = "registerChangeRequest") RegisterChangeRequest registerChangeRequest) {
//		return null;
//	}
//
//	@WebMethod(operationName = "registerReceipt")
//	public RegisterReceiptResponse registerReceipt(@WebParam(name = "registerReceiptRequest") RegisterReceiptRequest registerReceiptRequest) {
//		return null;
//	}
//
//	@WebMethod(operationName = "getDocList")
//	public GetDocListResponse getDocList(@WebParam(name = "getDocListRequest") GetDocListRequest getDocListRequest) {
//		return null;
//	}
//
//	@WebMethod(operationName = "getDoc")
//	public GetDocResponse getDoc(@WebParam(name = "getDocRequest") GetDocRequest getDocRequest) {
//		return null;
//	}
//
//	@WebMethod(operationName = "getMailGw202110")
//	public FindMailGwLogs202110Response getMailGw202110(@WebParam(name = "mailGw202110Request") GetMailGw202110Request mailGw202110Request) {
//		return null;
//	}
//}
