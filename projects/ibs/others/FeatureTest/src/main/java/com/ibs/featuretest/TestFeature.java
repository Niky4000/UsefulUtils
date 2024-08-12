/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ibs.featuretest;

import ru.ibs.pmp.common.ex.PmpTransportException;
import ru.ibs.pmp.registry.ModuleToken;
import ru.ibs.pmp.remote.HttpHelper;

/**
 *
 * @author NAnishhenko
 */
public class TestFeature {
    public static void main(String[] args) throws PmpTransportException{
        
        String request="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
"<businessActionContext ownerModuleName=\"arm-oms\" xmlns=\"http://pmp.ibs.ru/common/core/bac\">\n" +
"    <securityContext principal=\"IBS4\" organization=\"1863\"/>\n" +
"    <currentCall featureName=\"findNsiNsiDictionaryEntry\" domain=\"BUSINESS_COMMON\" invocationTime=\"2015-10-27T17:35:19.843+03:00\">\n" +
"        <incomingMessage>\n" +
"            <messageText>{\"dictionaryName\":\"mkb10.00\",\"pagingOffset\":0,\"pagingSize\":25,\"excludedEntities\":[],\"sortField\":\"\",\"sortAscending\":true}</messageText>\n" +
"            <serializerId>JSON</serializerId>\n" +
"            <type>ru.ibs.pmp.api.common.model.nsi.FindNsiDictionaryEntryRequest</type>\n" +
"        </incomingMessage>\n" +
"        <executionTime>\n" +
"            <requestSerialisation>1</requestSerialisation>\n" +
"            <requestDeserialisation>ll requestDeserialisation ll</requestDeserialisation>\n" +
"            <businessActionCall>ll businessActionCall ll</businessActionCall>\n" +
"            <responseSerialization>ll responseSerialization ll</responseSerialization>\n" +
"            <responseDeserialization>ll responseDeserialization ll</responseDeserialization>\n" +
"            <total>ll total ll</total>\n" +
"        </executionTime>\n" +
"    </currentCall>\n" +
"</businessActionContext>\n"
                + "";
        
        
        
//        String request="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
//"<businessActionContext ownerModuleName=\"arm-oms\" xmlns=\"http://pmp.ibs.ru/common/core/bac\">\n" +
//"    <securityContext principal=\"IBS4\" organization=\"1863\"/>\n" +
//"    <currentCall featureName=\"findNsiNsiDictionaryEntry\" domain=\"BUSINESS_COMMON\" invocationTime=\"2015-10-27T17:22:55.975+03:00\">\n" +
//"        <incomingMessage>\n" +
//"            <messageText>{\"dictionaryName\":\"kspec.02\",\"pagingOffset\":0,\"pagingSize\":25,\"excludedEntities\":[],\"sortField\":\"\",\"sortAscending\":true}</messageText>\n" +
//"            <serializerId>JSON</serializerId>\n" +
//"            <type>ru.ibs.pmp.api.common.model.nsi.FindNsiDictionaryEntryRequest</type>\n" +
//"        </incomingMessage>\n" +
//"        <executionTime>\n" +
//"            <requestSerialisation>0</requestSerialisation>\n" +
//"            <requestDeserialisation>ll requestDeserialisation ll</requestDeserialisation>\n" +
//"            <businessActionCall>ll businessActionCall ll</businessActionCall>\n" +
//"            <responseSerialization>ll responseSerialization ll</responseSerialization>\n" +
//"            <responseDeserialization>ll responseDeserialization ll</responseDeserialization>\n" +
//"            <total>ll total ll</total>\n" +
//"        </executionTime>\n" +
//"    </currentCall>\n" +
//"</businessActionContext>\n"
//                + "";
        
        
        
//        http://172.29.4.26:8080/module-business-common-features/
        
        ModuleToken token=new ModuleToken("module-business-common-features", "172.29.4.26", 8080);
//        ModuleToken token=new ModuleToken("module-business-common-features", "127.0.0.1", 8080);
        
        String toUrl = token.toUrl();
        
        boolean equals = toUrl.equals("http://127.0.0.1:8080/module-business-common-features/");
        
//        HttpHelperExt.init();
//        String sendPostRequest = HttpHelperExt.sendPostRequest(request, token);
        
        HttpHelper.init();
        String sendPostRequest = HttpHelper.sendPostRequest(request, token);
        
        String hello="";
    }
}
