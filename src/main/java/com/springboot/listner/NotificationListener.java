package com.springboot.listner;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.amazonaws.services.sns.message.SnsMessage;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.google.gson.Gson;
import com.springboot.GcGEvent;

@Component
@EnableScheduling
public class NotificationListener{
	
	private String sqsURL = "https://sqs.us-east-1.amazonaws.com/118198795607/ConsumeShree";
	
	private String sqsURLFifoQueue = "https://sqs.us-east-1.amazonaws.com/118198795607/gcgdemofifoqueue.fifo";

	private static final Logger log = LoggerFactory.getLogger(NotificationListener.class);
	final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
	final ReceiveMessageRequest receiveMessageRequest =
            new ReceiveMessageRequest(sqsURL)
            	.withMaxNumberOfMessages(10)
            	.withWaitTimeSeconds(5);
	
	Set<GcGEvent> bulkMessages = new TreeSet<GcGEvent>();
	
	private int iteration = 0;
	
	final Gson gson = new Gson();
	
	
	//@Value("${sqs.url}")
	//@Value("${cloud.aws.end-point.uri}")
	
	@Scheduled(fixedRate = 1000)
	public void testSchedulerInMessage() {
		
		/*Map<String,String> eventDetailsMap = new HashMap<>();  
		eventDetailsMap.put("FolderLocation", "/inbound/kariba/");
		
		GcGEvent event = new GcGEvent();
		event.setGcgMessageId(UUID.randomUUID().toString());
		event.setCounterPartyId("----C2FO---");
		event.setOriginSystem("C2FO");
		event.setCurrentSystem("GCG");
		event.setDestinationSystem("GPE");
		event.setContentType("xml");
		event.setMessageAttributes(eventDetailsMap);
		event.setGcgPayLoad("<RECEIVE_REQUEST>\r\n" + 
				"    <CLIENT>MVD</CLIENT>\r\n" + 
				"    <FILE_ID>MESSAGE-1</FILE_ID>\r\n" + 
				"    <REQUEST_STATE>1</REQUEST_STATE>\r\n" + 
				"</RECEIVE_REQUEST>");
		
		String jsonString = gson.toJson(event);*/
		iteration++;
		
        final List<com.amazonaws.services.sqs.model.Message> messages = sqs.receiveMessage(receiveMessageRequest)
                .getMessages();
        
        if(messages.size()>0) {
        	iteration=0;
        }
        
        for (final com.amazonaws.services.sqs.model.Message message : messages) {
        	log.debug("Message");
        	JSONObject messageMapJson = new JSONObject(message.getBody().toString());
        	JSONObject messageMapJson1 = new JSONObject(messageMapJson.getString("Message"));
        	
        	GcGEvent event1 = gson.fromJson(messageMapJson1.getJSONObject("msg").toString(), GcGEvent.class);
        	System.out.println("Message Received: "+event1.getGcgPayLoad());
        	System.out.println("Time Received: "+event1.getTimestamp());
        	bulkMessages.add(event1);
        	final String messageReceiptHandle = messages.get(0).getReceiptHandle();
            sqs.deleteMessage(new DeleteMessageRequest(sqsURL,
                    messageReceiptHandle));
        }
        System.out.println("--------Size----------"+messages.size());
        //bulkMessages.addAll(messages);
        System.out.println("--------iteration-----"+iteration);
        System.out.println("--------bulkMessages size-----"+bulkMessages.size());
        if(bulkMessages.size()<50 && iteration<10) {
        	return;
        }
        System.out.println("------------Going for Schuffling----------------");
        iteration = 0;
		
		 
		 
        for (final GcGEvent message : bulkMessages) {
        	String jsonString = gson.toJson(message);
        	System.out.println("event1 Message----->"+jsonString);
        	
        	 final SendMessageRequest sendMessageRequest =
                     new SendMessageRequest(sqsURLFifoQueue,jsonString);

             sendMessageRequest.setMessageGroupId("test");
             final SendMessageResult sendMessageResult = sqs
                     .sendMessage(sendMessageRequest);
             final String sequenceNumber = sendMessageResult.getSequenceNumber();
             final String messageId = sendMessageResult.getMessageId();
             System.out.println("SendMessage succeed with messageId "
                     + messageId + ", sequence number " + sequenceNumber + "\n");
        }
        bulkMessages.clear();
	}
	
	//@Scheduled(fixedRate = 1000)
	public void getMessage() {
		
		System.out.println("--------------------------TEST------------------------------");
        while(true) {
        	
        }
	}
}
