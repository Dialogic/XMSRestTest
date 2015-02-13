XMSRestTest2
================
This is a simple "Hello world" type application to showcase the use of the XMS Rest Interface.  
http://www.dialogic.com/webhelp/XMS/2.3/XMS_RESTfulAPIUser.pdf
The source files are commented to show the usage of the Appache HTTP libraries to interface with the XMS.

This application will
- Allocate an object (XMSEventListener) to establish the connection via a LongPoll.  This will wait for events in the HttpStream and then notify all observers on the reception of a new event

- Create an object that can be used to generate and send the Http request (XMSRestSender)

- Use the XMSRestSender to create a new call via POST
- Wait for the Stream to establish and notification from the EventListener
- Play out a file to the call by using the XMSRestSender and a PUT
- Wait for the END_PLAY notification from the Event Listener
- Hangup/Terminate the call via a DELETE


The XMS IP, Playfile and call destination can be modified by changing the config.properties file
PlayFile=file\://verification/play_menu.wav
MakecallDestination=sip\:softphone@10.20.123.30\:5070
XMSIP=10.20.123.11


Output from application:
run:
Application Properties Set to:
{PlayFile=file://verification/play_menu.wav, XMSIP=10.20.123.11, MakecallDestination=sip:softphone@10.20.123.30:5070}
HTTP/1.1 201 Created
   Location: http://10.20.123.11:81/default/eventhandlers/cb94b4b6-dcae-461f-8ad6-e56a705f151d
   Content-Type: application/xml
   Transfer-Encoding: chunked
   Date: Fri, 13 Feb 2015 21:19:24 GMT
   Server: lighttpd/1.4.28
****** Message Contents *******
<web_service version="1.0">
<eventhandler_response identifier="cb94b4b6-dcae-461f-8ad6-e56a705f151d" appid="app" href="/default/eventhandlers/cb94b4b6-dcae-461f-8ad6-e56a705f151d"
>
<eventsubscribe type="any" resource_id="any" resource_type="any"/>
</eventhandler_response>
</web_service>

XMSEventHandler Connected Successfully
href=/default/eventhandlers/cb94b4b6-dcae-461f-8ad6-e56a705f151d
Starting WaitEventThread()


===========================================================
Making outbound call via POST:
POST /default/calls?appid=app
 XMLPayload:
<web_service version="1.0"> <call media="audiovideo" signaling="yes" dtmf_mode="rfc2833" async_dtmf="yes" async_tone="yes" destination_uri="sip:softphone@10.20.123.30:5070" cpa="no" /></web_service>


Received Event:
<web_service version="1.0">
<event type="keepalive"/></web_service>
HTTP/1.1 201 Created
   Location: http://10.20.123.11:81/default/calls/c2dcaa4f-2b31-425c-b491-f2537d4c8738
   Content-Type: application/xml
   Transfer-Encoding: chunked
   Date: Fri, 13 Feb 2015 21:19:24 GMT
   Server: lighttpd/1.4.28
****** Message Contents *******
<web_service version="1.0">
<call_response identifier="c2dcaa4f-2b31-425c-b491-f2537d4c8738" appid="app" href="/default/calls/c2dcaa4f-2b31-425c-b491-f2537d4c8738"
 connected="no" signaling="yes" cpa="no" call_type="outbound"
 media="audiovideo"
 dtmf_mode="rfc2833"
 destination_uri="sip:softphone@10.20.123.30:5070"
 async_dtmf="yes" async_tone="yes" cleardigits="no" encryption="none" ice="no" info_ack_mode="automatic" hangup_ack_mode="automatic" early_media="no">
</call_response>
</web_service>

POST XML response
<web_service version="1.0">
<call_response identifier="c2dcaa4f-2b31-425c-b491-f2537d4c8738" appid="app" href="/default/calls/c2dcaa4f-2b31-425c-b491-f2537d4c8738"
 connected="no" signaling="yes" cpa="no" call_type="outbound"
 media="audiovideo"
 dtmf_mode="rfc2833"
 destination_uri="sip:softphone@10.20.123.30:5070"
 async_dtmf="yes" async_tone="yes" cleardigits="no" encryption="none" ice="no" info_ack_mode="automatic" hangup_ack_mode="automatic" early_media="no">
</call_response>
</web_service>

href=/default/calls/c2dcaa4f-2b31-425c-b491-f2537d4c8738


Received Event:
<web_service version="1.0">
<event type="ringing" resource_id="c2dcaa4f-2b31-425c-b491-f2537d4c8738" resource_type="call">
<event_data name="call_id" value="c2dcaa4f-2b31-425c-b491-f2537d4c8738" />
<event_data name="type" value="RINGING" />
</event>
</web_service>


Received Event:
<web_service version="1.0">
<event type="connected" resource_id="c2dcaa4f-2b31-425c-b491-f2537d4c8738" resource_type="call">
<event_data name="call_id" value="c2dcaa4f-2b31-425c-b491-f2537d4c8738" />
<event_data name="called_uri" value="&lt;sip:softphone@10.20.123.30:5070&gt;;tag=fBUu9Cx" />
<event_data name="caller_uri" value="&lt;sip:10.20.123.11&gt;;tag=f24803a8-b7b140a-13c4-50022-3d5c-66f79ede-3d5c" />
<event_data name="media" value="audiovideo" />
<event_data name="reason" value="unknown" />
<event_data name="type" value="CONNECTED" />
</event>
</web_service>


Received Event:
<web_service version="1.0">
<event type="stream" resource_id="c2dcaa4f-2b31-425c-b491-f2537d4c8738" resource_type="call">
<event_data name="call_id" value="c2dcaa4f-2b31-425c-b491-f2537d4c8738" />
<event_data name="state" value="streaming" />
<event_data name="type" value="STREAM" />
</event>
</web_service>
STREAMING event detected, setting isStreaming to true


===========================================================
Play a file using a PUT
PUT /default/calls/c2dcaa4f-2b31-425c-b491-f2537d4c8738?appid=app
 XMLPayload:
<web_service version="1.0"> <call> <call_action> <play offset="0s" repeat="0" delay="1s" terminate_digits="#" skip_interval="1s"> <play_source audio_uri="file://verification/play_menu.wav" audio_type="audio/x-wav" /> </play> </call_action></call></web_service>
HTTP/1.1 200 OK
   Location: http://10.20.123.11:81/default/calls/c2dcaa4f-2b31-425c-b491-f2537d4c8738
   Content-Type: application/xml
   Transfer-Encoding: chunked
   Date: Fri, 13 Feb 2015 21:19:28 GMT
   Server: lighttpd/1.4.28
****** Message Contents *******
<web_service version="1.0">
<call_response identifier="c2dcaa4f-2b31-425c-b491-f2537d4c8738" appid="app" href="/default/calls/c2dcaa4f-2b31-425c-b491-f2537d4c8738"
 connected="yes" signaling="yes" cpa="no" call_type="outbound"
 media="audiovideo"
 dtmf_mode="rfc2833"
 destination_uri="sip:softphone@10.20.123.30:5070"
 async_dtmf="yes" async_tone="yes" cleardigits="no" encryption="none" ice="no" info_ack_mode="automatic" hangup_ack_mode="automatic" early_media="no">
<call_action>
<play transaction_id="da603b6d-e092-45cf-9711-75211cc45c71"
	max_time="infinite"
	fetch_timeout="300s"
	offset="0s"
	delay="1s"
	repeat="0"
	skip_interval="1s"
	terminate_digits="#">
	<play_source audio_uri="file://verification/play_menu.wav"
		audio_type="audio/x-wav"
/>
</play></call_action>
</call_response>
</web_service>

PUT XML response
<web_service version="1.0">
<call_response identifier="c2dcaa4f-2b31-425c-b491-f2537d4c8738" appid="app" href="/default/calls/c2dcaa4f-2b31-425c-b491-f2537d4c8738"
 connected="yes" signaling="yes" cpa="no" call_type="outbound"
 media="audiovideo"
 dtmf_mode="rfc2833"
 destination_uri="sip:softphone@10.20.123.30:5070"
 async_dtmf="yes" async_tone="yes" cleardigits="no" encryption="none" ice="no" info_ack_mode="automatic" hangup_ack_mode="automatic" early_media="no">
<call_action>
<play transaction_id="da603b6d-e092-45cf-9711-75211cc45c71"
	max_time="infinite"
	fetch_timeout="300s"
	offset="0s"
	delay="1s"
	repeat="0"
	skip_interval="1s"
	terminate_digits="#">
	<play_source audio_uri="file://verification/play_menu.wav"
		audio_type="audio/x-wav"
/>
</play></call_action>
</call_response>
</web_service>



Received Event:
<web_service version="1.0">
<event type="end_play" resource_id="c2dcaa4f-2b31-425c-b491-f2537d4c8738" resource_type="call">
<event_data name="duration" value="6210" />
<event_data name="id" value="c2dcaa4f-2b31-425c-b491-f2537d4c8738" />
<event_data name="media_id" value="da603b6d-e092-45cf-9711-75211cc45c71" />
<event_data name="reason" value="end" />
<event_data name="status" value="0 No Error" />
<event_data name="transaction_id" value="da603b6d-e092-45cf-9711-75211cc45c71" />
<event_data name="type" value="END_PLAY" />
</event>
</web_service>
STREAMING event detected, setting isDonePlaying to true


===========================================================
Hangup/Terminate the call with DELETE
DELETE /default/calls/c2dcaa4f-2b31-425c-b491-f2537d4c8738?appid=app
HTTP/1.1 204 No Content
   Date: Fri, 13 Feb 2015 21:19:35 GMT
   Server: lighttpd/1.4.28
****** Message Contents *******
DELETE XML response

