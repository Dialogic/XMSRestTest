/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xmsresttest2;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author dwolansk
 */
public class XMSRestSender {
    HttpHost host;
    String address="";
    int port=81;
    String appid="app";
    DefaultHttpClient httpclient;
    
    public XMSRestSender(String addr){
        address=addr;
        
        //HTTPHost - Holds all of the variables needed to describe an HTTP connection to a host. 
        //  This includes remote host name, port and scheme.
        // http://hc.apache.org/httpcomponents-core-ga/httpcore/apidocs/org/apache/http/HttpHost.html
         host = new HttpHost(address,port,"http");
         
         //DefaultHttpClient - Default implementation of HttpClient 
         //  pre-configured for most common use scenarios.
         // http://hc.apache.org/httpcomponents-client-ga/httpclient/apidocs/org/apache/http/client/HttpClient.html
          httpclient= new DefaultHttpClient();
    }
    
    
    public String GET(String dest){
        
        //HttpResponse - After receiving and interpreting a request message, a server responds with an HTTP response message.
        //Response      = Status-Line
        //             *(( general-header
        //              | response-header
        //              | entity-header ) CRLF)
        //             CRLF
        //             [ message-body ]
        //http://hc.apache.org/httpcomponents-core-ga/httpcore/apidocs/org/apache/http/HttpResponse.html?is-external=true
        HttpResponse response= null;
        
        //This string is used to store the XML extracted from the Response
        String xmlresponse = "";
        try {
            System.out.println("GET "+dest+"?appid="+appid    );      
            
            //HttpGet - Container for the HTTP GET method.
            //  The HTTP GET method is defined in section 9.3 of RFC2616:
            //  The GET method means retrieve whatever information (in the form of an entity) 
            //  is identified by the Request-URI. If the Request-URI refers to a data-producing process, 
            //  it is the produced data which shall be returned as the entity in the response and not the 
            //  source text of the process, unless that text happens to be the output of the process.
            
            HttpGet getRequest = new HttpGet(dest+"?appid="+appid);
            
            //Here you are issueing the GetRequest, to the provided host via the HttpClient and storing
            //  the response in the HpptResponse
            response = httpclient.execute(host,getRequest);
            
            //HttpEntity - An entity that can be sent or received with an HTTP message. 
            // Entities can be found in some requests and in responses, where they are optional.
            // http://hc.apache.org/httpcomponents-core-ga/httpcore/apidocs/org/apache/http/HttpEntity.html
            HttpEntity entity = response.getEntity();
            
            System.out.println(response.getStatusLine());
            Header[] headers = response.getAllHeaders();
            for (int i = 0; i < headers.length; i++) {
                System.out.println("   "+headers[i]);
            }
            System.out.println("****** Message Contents *******");
            if (entity != null) {
                xmlresponse=EntityUtils.toString(entity);
                System.out.println(EntityUtils.toString(entity));
            } 
            
            
        } catch (IOException ex) {
            Logger.getLogger(XMSRestSender.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return xmlresponse;
    }
    public String POST(String dest,String xmlpayload){
        //HttpResponse - After receiving and interpreting a request message, a server responds with an HTTP response message.
        //Response      = Status-Line
        //             *(( general-header
        //              | response-header
        //              | entity-header ) CRLF)
        //             CRLF
        //             [ message-body ]
        //http://hc.apache.org/httpcomponents-core-ga/httpcore/apidocs/org/apache/http/HttpResponse.html?is-external=true
        HttpResponse response =null;
        
        //This string is used to store the XML extracted from the Response
        String xmlresponse = "";
        try {
            
            System.out.println("POST "+dest+"?appid="+appid+"\n XMLPayload:\n"+xmlpayload);
    
            //HttpPost - HTTP POST method.
           // The HTTP POST method is defined in section 9.5 of RFC2616:
           // IN XMS REST Interface POST are used to create new Calls/Confs etc
           // http://hc.apache.org/httpcomponents-client-ga/httpclient/apidocs/org/apache/http/client/methods/HttpPost.html          
            
            // The POST requires an XML payload.  This block will convert the passed XML String to an http Entitiy and 
            // attach it to the post message.  Will also update the header to indicate the format of the payload is XML
            StringEntity se = new StringEntity(xmlpayload, HTTP.UTF_8);
           
           HttpPost httppost = new HttpPost(dest+"?appid="+appid);
           httppost.setHeader("Content-Type","text/xml;charset=UTF-8");
           httppost.setEntity(se);
           
           //Here you are issueing the POST Request, to the provided host via the HttpClient and storing
            //  the response in the HpptResponse
           response = httpclient.execute(host,httppost);
           
            //HttpEntity - An entity that can be sent or received with an HTTP message. 
            // Entities can be found in some requests and in responses, where they are optional.
            // http://hc.apache.org/httpcomponents-core-ga/httpcore/apidocs/org/apache/http/HttpEntity.html
           HttpEntity entity = response.getEntity();

           //This block will dump out the Status, headers and message contents if available
            System.out.println(response.getStatusLine());
            Header[] headers = response.getAllHeaders();
            for (int i = 0; i < headers.length; i++) {
                System.out.println("   "+headers[i]);
            }
            System.out.println("****** Message Contents *******");
           
           if (entity != null) {
               xmlresponse =EntityUtils.toString(entity);
                System.out.println(xmlresponse);
            } 
        } catch (IOException ex) {
            Logger.getLogger(XMSRestSender.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return xmlresponse;
    }
    public String PUT(String dest,String xmlpayload){
        
        //HttpResponse - After receiving and interpreting a request message, a server responds with an HTTP response message.
        //Response      = Status-Line
        //             *(( general-header
        //              | response-header
        //              | entity-header ) CRLF)
        //             CRLF
        //             [ message-body ]
        //http://hc.apache.org/httpcomponents-core-ga/httpcore/apidocs/org/apache/http/HttpResponse.html?is-external=true
        HttpResponse response =null;
        
        //This string is used to store the XML extracted from the Response
        String xmlresponse = "";
        try {
            
           System.out.println("PUT "+dest+"?appid="+appid+"\n XMLPayload:\n"+xmlpayload);
           //HttpPut -  HTTP PUT method.
            // The HTTP PUT method is defined in section 9.6 of RFC2616:
           //http://hc.apache.org/httpcomponents-client-ga/httpclient/apidocs/org/apache/http/client/methods/HttpPut.html
           // In XMS the PUT is used to cause action or update on an existing resource (ie media functionality, joining etc)
           
            // The PUT requires an XML payload.  This block will convert the passed XML String to an http Entitiy and 
            // attach it to the post message.  Will also update the header to indicate the format of the payload is XML
           StringEntity se = new StringEntity(xmlpayload, HTTP.UTF_8);
           HttpPut httpput = new HttpPut(dest+"?appid="+appid);
           httpput.setHeader("Content-Type","text/xml;charset=UTF-8");
           httpput.setEntity(se);
           
            //Here you are issueing the PUT Request, to the provided host via the HttpClient and storing
            //  the response in the HpptResponse
           response = httpclient.execute(host,httpput);
           
            //HttpEntity - An entity that can be sent or received with an HTTP message. 
            // Entities can be found in some requests and in responses, where they are optional.
            // http://hc.apache.org/httpcomponents-core-ga/httpcore/apidocs/org/apache/http/HttpEntity.html
           HttpEntity entity = response.getEntity();

           //This block will dump out the Status, headers and message contents if available
            System.out.println(response.getStatusLine());
            Header[] headers = response.getAllHeaders();
            for (int i = 0; i < headers.length; i++) {
                System.out.println("   "+headers[i]);
            }
            System.out.println("****** Message Contents *******");
           
           if (entity != null) {
               xmlresponse =EntityUtils.toString(entity);
                System.out.println(xmlresponse);
            } 
        } catch (IOException ex) {
            Logger.getLogger(XMSRestSender.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return xmlresponse;
    }
    public String DELETE(String dest){
        //HttpResponse - After receiving and interpreting a request message, a server responds with an HTTP response message.
        //Response      = Status-Line
        //             *(( general-header
        //              | response-header
        //              | entity-header ) CRLF)
        //             CRLF
        //             [ message-body ]
        //http://hc.apache.org/httpcomponents-core-ga/httpcore/apidocs/org/apache/http/HttpResponse.html?is-external=true
        HttpResponse response =null;
        
        //This string is used to store the XML extracted from the Response
        String xmlresponse = "";
        try {
            System.out.println("DELETE "+dest+"?appid="+appid    );
           // HttpDelete - The HTTP DELETE method is defined in section 9.7 of RFC2616:
           // The DELETE method requests that the origin server delete the resource identified by the Request-URI. [...] 
           
           // https://hc.apache.org/httpcomponents-client-ga/httpclient/apidocs/org/apache/http/client/methods/HttpDelete.html
           HttpDelete httpdelete = new HttpDelete(dest+"?appid="+appid);
                      
            //Here you are issueing the DELETE Request, to the provided host via the HttpClient and storing
            //  the response in the HpptResponse
           response = httpclient.execute(host,httpdelete);
           
           //HttpEntity - An entity that can be sent or received with an HTTP message. 
            // Entities can be found in some requests and in responses, where they are optional.
            // http://hc.apache.org/httpcomponents-core-ga/httpcore/apidocs/org/apache/http/HttpEntity.html
           HttpEntity entity = response.getEntity();

           //This block will dump out the Status, headers and message contents if available
            System.out.println(response.getStatusLine());
            Header[] headers = response.getAllHeaders();
            for (int i = 0; i < headers.length; i++) {
                System.out.println("   "+headers[i]);
            }
            System.out.println("****** Message Contents *******");
           
           if (entity != null) {
               xmlresponse =EntityUtils.toString(entity);
                System.out.println(xmlresponse);
            } 
        } catch (IOException ex) {
            Logger.getLogger(XMSRestSender.class.getName()).log(Level.SEVERE, null, ex);
        }
        return xmlresponse;
    }
    
}
