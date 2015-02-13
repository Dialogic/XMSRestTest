/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xmsresttest2;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author dwolansk
 */
public class XMSRestTest2 implements Observer{
   
    String xmsip = "10.20.123.11";
    String makecalldest = "sip:softphone@10.20.123.30:5070";
    String playfile = "file://verification/play_menu.wav";
    int port=81;
    
    
    boolean isStreaming=false;
    boolean isDonePlaying=false;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        XMSRestTest2 app = new XMSRestTest2();
        app.start();
    }
    
    public void start(){
        // Read in the properties file
        Properties prop = new Properties();
        
    	try {
         //Uncomment this block to generate the config.properties file if none is available
          //  prop.setProperty("XMSIP", xmsip);
          //  prop.setProperty("MakecallDestination",makecalldest);
          //  prop.setProperty("PlayFile",playfile);
          //  prop.store(new FileOutputStream("config.properties"), null);
          //  return;  
            
            //This will load the properties from the config file
            prop.load(new FileInputStream("config.properties"));
            xmsip = prop.getProperty("XMSIP");
            makecalldest = prop.getProperty("MakecallDestination");
            playfile=prop.getProperty("PlayFile");

            System.out.println("Application Properties Set to:\n"+prop);
            
        } catch (IOException ex) {
            //If the file is not availabe it will run with default parms above
            System.out.println("config.properties can't be found, running with default properties");
        }
        
        try {
            
        //Step 1: Create the EventListener 
        XMSEventListener el = new XMSEventListener();
        
        //Step 2: Have the EventListener connect to the XMS the 
        // destination ip address and port are configurable above
        el.ConnectToXMS(xmsip , port);
        
        //Step 3: Setup this class as an Observer of the EL so that it can 
        // be notified when the events come in on the stream.  When an
        // event is received the Update Method will be called
        el.addObserver(this);       
        
        //Step 4: Start the EL listening for new events
        el.StartListening();
        
        
        //Setp 5: We will setup the XMSRestSender object to be used when sending requests
        // to the xms server and returning the xms responses.
        XMSRestSender sender = new XMSRestSender(xmsip);
        //sender.GET("/default/calls");
        
        System.out.println("\n\n===========================================================\nMaking outbound call via POST:");
        
        //Step 6: Here we will construct the XMS payload to make the call to the destination.  Destination
        // is stored in the makecalldest above and can be modified as needed.
        String  makecall_xmlpayload = "<web_service version=\"1.0\">" +
                            " <call media=\"audiovideo\" signaling=\"yes\" dtmf_mode=\"rfc2833\" async_dtmf=\"yes\" async_tone=\"yes\"" +
                            " destination_uri=\""+makecalldest+"\"" +
                            " cpa=\"no\" />" +
                            "</web_service>";
        //Step 7: Create the new call with the POST
        String xmlresponse=sender.POST("/default/calls",makecall_xmlpayload);
        System.out.println("POST XML response\n"+xmlresponse);
         
        //Step 8: need to extract the href from the XML response to be used in later DELETE or PUTs
        String href=GetHrefFromResponse(xmlresponse);
        
        //Step 9: Next we need to wait for the notification from the EventListener that the stream is connected and Streaming
        //Here we will put a sleep in waiting for the EVENT Thread to generate the STREAMING
        while(!isStreaming)
            Thread.sleep(500);
        
        System.out.println("\n\n===========================================================\nPlay a file using a PUT");
        //Step 10: We will now create the XML for the play.  The playfile can be configured above
        String play_xmlpayload = "<web_service version=\"1.0\">" +
                            " <call>" +
                                " <call_action>" +
                                    " <play offset=\"0s\" repeat=\"0\" delay=\"1s\" terminate_digits=\"#\" skip_interval=\"1s\">" +
                                    " <play_source audio_uri=\""+playfile+"\" audio_type=\"audio/x-wav\" />" +
                                    " </play>" +
                                " </call_action>" +
                            "</call>" +
                        "</web_service>";
        
        //Step 11: send out the play request with a PUT because it is updating the existing call
        xmlresponse = sender.PUT(href, play_xmlpayload);
        System.out.println("PUT XML response\n"+xmlresponse);
        
        //Step 12: Now the play is currently playing, we need to wait for the EventListener to provide the END_PLAY event
        //Here we will put a sleep in waiting for the EVENT Thread to generate the END_PLAY       
        while(!isDonePlaying)
            Thread.sleep(500);
  
        //Step 13: Here we can Terminate or hangup the call by issuing a DELETE on the href
        System.out.println("\n\n===========================================================\nHangup/Terminate the call with DELETE");
        xmlresponse = sender.DELETE(href);
        System.out.println("DELETE XML response\n"+xmlresponse);
        
        
        } catch (Exception e){
            System.out.println(e);
        }
    }
    
    @Override
    public void update(Observable o, Object arg) {
        System.out.println("\n\nReceived Event:\n"+arg);
        if(arg.toString().contains("<event_data name=\"type\" value=\"STREAM\" />")){
            System.out.println("STREAMING event detected, setting isStreaming to true");
            isStreaming=true;
        }
        else if(arg.toString().contains("<event_data name=\"type\" value=\"END_PLAY\" />")){
            System.out.println("STREAMING event detected, setting isDonePlaying to true");
            isDonePlaying=true;
        }
    }
    
    private String GetHrefFromResponse(String xmlresponse){
        
            //Here we need to pull out the href or callID
            Pattern pattern = Pattern.compile("href=\"(.*?)\"");
            Matcher matcher = pattern.matcher(xmlresponse);
            String href="";
            if(matcher.find()){
                href=matcher.group(1);
                System.out.println("href="+href);
            } else {
                System.out.println("No href found!");
            }
            return href;
    }
    
    
}
