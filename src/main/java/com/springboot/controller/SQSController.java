package com.springboot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sqs")
public class SQSController {

	private static final Logger LOG = LoggerFactory.getLogger(SQSController.class);

	@Autowired
	private QueueMessagingTemplate queueMessagingTemplate;

	@Value("${cloud.aws.end-point.uri}")
	private String sqsEndPoint;

	@GetMapping
	public void sendMessage() {
		queueMessagingTemplate.send(sqsEndPoint, MessageBuilder.withPayload("hello from Spring Boot").build());
	}
	
	//@SqsListener(value = "ConsumeShree", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
	//@SendTo("ConsumeShree2")
	public void getMessage(String message) {
		LOG.info("Message from SQS Queue - "+message);
	}
	
	
	
	
}
