/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xmsresttest2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author dwolansk
 */
public class XMSEventListener extends Observable implements Runnable  {

    //HTTPHost - Holds all of the variables needed to describe an HTTP connection to a host. 
     //  This includes remote host name, port and scheme.
     // http://hc.apache.org/httpcomponents-core-ga/httpcore/apidocs/org/apache/http/HttpHost.html
     HttpHost host;
     
     
     //DefaultHttpClient - Default implementation of HttpClient 
     //  pre-configured for most common use scenarios.
     // http://hc.apache.org/httpcomponents-client-ga/httpclient/apidocs/org/apache/http/client/HttpClient.html
     DefaultHttpClient httpclient= new DefaultHttpClient();
     Thread thread=null;
     String href="";
     String AppID="app";
     
     boolean isConnected=false;
     
    public void ConnectToXMS(String address, int port){
        // If the Listener is already connected, the simply return
        if(isConnected){
            return;
        }
        //Step 1: Create the Host
        host = new HttpHost(address,port,"http");
        
        //Step 2: Create the XML payload for creation of and event handler.  In this case will will be
        // listening for anyevent on any resource
        String xmlpayload = "<web_service version=\"1.0\">" +
                                    "<eventhandler>"+
                                        "<eventsubscribe type=\"any\" resource_id=\"any\" resource_type=\"any\"/>"+
                                    "</eventhandler>"+
                                "</web_service>";
        
        
         //HttpPost - HTTP POST method.
           // The HTTP POST method is defined in section 9.5 of RFC2616:
           // IN XMS REST Interface POST are used to create new event handler
           // http://hc.apache.org/httpcomponents-client-ga/httpclient/apidocs/org/apache/http/client/methods/HttpPost.html 
          HttpPost httppost = new HttpPost("/default/eventhandlers?appid=app");
          
          
        // The POST requires an XML payload.  This block will convert the passed XML String to an http Entitiy and 
        // attach it to the post message.  Will also update the header to indicate the format of the payload is XML
        StringEntity se = null;
         try {
             se = new StringEntity(xmlpayload, HTTP.UTF_8);
         } catch (UnsupportedEncodingException ex) {
             Logger.getLogger(XMSEventListener.class.getName()).log(Level.SEVERE, null, ex);
         }
              
           httppost.setHeader("Content-Type","text/xml;charset=UTF-8");
           httppost.setEntity(se);
           
      
           //Here you are issueing the POST Request, to the provided host via the HttpClient and storing
            //  the response in the HpptResponse
           HttpResponse httpResponse = null;
         try {
             httpResponse = httpclient.execute(host,httppost);
         } catch (IOException ex) {
             Logger.getLogger(XMSEventListener.class.getName()).log(Level.SEVERE, null, ex);
         }
         
            //HttpEntity - An entity that can be sent or received with an HTTP message. 
            // Entities can be found in some requests and in responses, where they are optional.
            // http://hc.apache.org/httpcomponents-core-ga/httpcore/apidocs/org/apache/http/HttpEntity.html
           HttpEntity entity = httpResponse.getEntity();

           
           //This block will dump out the Status, headers and message contents if available
            System.out.println(httpResponse.getStatusLine());
            Header[] headers = httpResponse.getAllHeaders();
            for (int i = 0; i < headers.length; i++) {
                System.out.println("   "+headers[i]);
            }
            System.out.println("****** Message Contents *******");
           String xmlresponse = "";
           if (entity != null) {
            try {
                xmlresponse =EntityUtils.toString(entity);
            } catch (IOException ex) {
                Logger.getLogger(XMSEventListener.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParseException ex) {
                Logger.getLogger(XMSEventListener.class.getName()).log(Level.SEVERE, null, ex);
            }
                System.out.println(xmlresponse);
            }
        
           // Next we want to check to see if the request was successful by lookin for a 2xx response,
           // in this case it will be a 201
           if(httpResponse.getStatusLine().getStatusCode() == 201){
               System.out.println("XMSEventHandler Connected Successfully");
               // If it is successful mark it as connected
               isConnected =true;
               //Here we need to pull out the href or callID
                Pattern pattern = Pattern.compile("href=\"(.*?)\"");
                Matcher matcher = pattern.matcher(xmlresponse);
                if(matcher.find()){
                    href=matcher.group(1);
                    System.out.println("href="+href);
                } else {
                    System.out.println("No href found!");
                }
           }
           
    }
    
    void StartListening(){
        
        // check to see if we already have a thread active
        if(thread!=null){
            System.out.println("Event thread already started, returning");
            return;
        }
        //if not start the new thread, this will call the run() method
        thread = new Thread(this);
        thread.start();
        
    }
    
    private void WaitEventThread(){
        
        //HttpGet - Container for the HTTP GET method.
            //  The HTTP GET method is defined in section 9.3 of RFC2616:
            //  The GET method means retrieve whatever information (in the form of an entity) 
            //  is identified by the Request-URI. If the Request-URI refers to a data-producing process, 
            //  it is the produced data which shall be returned as the entity in the response and not the 
            //  source text of the process, unless that text happens to be the output of the process.
            
                HttpGet httpget = new HttpGet(href+ "?appid=" + AppID);
                
                //HttpResponse - After receiving and interpreting a request message, a server responds with an HTTP response message.
                //Response      = Status-Line
                //             *(( general-header
                //              | response-header
                //              | entity-header ) CRLF)
                //             CRLF
                //             [ message-body ]
                //http://hc.apache.org/httpcomponents-core-ga/httpcore/apidocs/org/apache/http/HttpResponse.html?is-external=true
                HttpResponse httpResponse = null;
                try {
                    //Here you are issueing the GetRequest, to the provided host via the HttpClient and storing
                    //  the response in the HpptResponse
                    httpResponse = httpclient.execute(host,httpget);
                }   catch (IOException ex) {

                   
                }
                
                //We want to check that the get was successful by checking for a 2xx status code, in this case will be 200
                if ( httpResponse.getStatusLine().getStatusCode()!= 200){
                    System.out.println("Error connecting to Event Stream - Status line = "+httpResponse.getStatusLine().toString());
                    return;
                }

                //This GET differs from the typical get because the eventhander will not terminate the session on the receipt of 
                // this response.  Instead it will stay open and additional "chunks" of data well be sent in for each event
                HttpEntity httpentity = httpResponse.getEntity();
                InputStream         instream          = null;
         
                try {
                    //What we will be doing is get access to the InputStream via the httpentity we then assign this to the InputStream
                                       
                    instream = httpentity.getContent();
                } catch (IOException ex) {
                    Logger.getLogger(XMSEventListener.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalStateException ex) {
                    Logger.getLogger(XMSEventListener.class.getName()).log(Level.SEVERE, null, ex);
                }
                    
                 InputStreamReader isr;
                BufferedReader reader;
                String xmlString=null;


        /**
                 * Setting up input stream to read from the socket connection..  This StreamReader
                 * is setup to point to the input stream from the GetRequest that is left open
                 */
                isr = new InputStreamReader(instream);
                reader = new BufferedReader(isr);
                int i=0;
                xmlString = "";
                
                /********************************************************************
                 * The HTTP Connection remains open.
                 * Use a BufferedReader to grab data as it comes across the wire
                 ********************************************************************/
                String xmlStringPrime = null;
                String xmlStringCleaned;
                
                try {
                   
                   for(;;)
                   {
                       //While the reader is empty it will block here
                       String line = reader.readLine();
                       // We are reading in the first line of the message as this will contain 
                       // the legth of the message
                       // FOR details on this look at Event Streamin section of the
                       // http://www.dialogic.com/webhelp/XMS/2.3/XMS_RESTfulAPIUser.pdf

                       //System.out.println("**New Message **\n  Message size is "+line);
                       
                       //This block of code is here in case the inbound message is devided into
                       // multiple sections.  This code will keep reading till the entire
                       // length above is received
                       int ChunkSize = Integer.parseInt(line, 16);
                       char buff[] =new char[ChunkSize];
                       if (ChunkSize<=0)
                           break;
                       
                       xmlString = "";
                       int readsize = 0;
                  	
                       while(readsize < ChunkSize){
                            readsize = reader.read(buff,readsize,ChunkSize-readsize) + readsize;
                            //System.out.println("Reading "+readsize+" of "+ChunkSize);
                                                                      
                       }
                       xmlString +=new String(buff);
                       //System.out.println("****** Message Contents *******\n"+xmlString);
                       
                       //Here we will mark ourselves as updated and then Send the XMS event received
                       // to any of the Observers for processing
                       setChanged();
                       notifyObservers(xmlString);
                   }
                       
                } catch (IOException ex) {
                    Logger.getLogger(XMSEventListener.class.getName()).log(Level.SEVERE, null, ex);
                }
    }
    @Override
    public void run() {
        
        //This is the entery point to the Runnable.  We will simply call the WaitEventTrhead here.
        System.out.println("Starting WaitEventThread()");
        WaitEventThread();
        
    }
    
    
}
