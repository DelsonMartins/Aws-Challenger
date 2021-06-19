package com.desafio.awsdesafio.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.s3.event.S3EventNotification;
import com.desafio.awsdesafio.services.QueueService;

@RestController
public class QueueController {

    private Logger logger = LoggerFactory.getLogger(QueueController.class);
    
    @Autowired
	private QueueService service;
    
    
    private static final String QUEUE = "DesafioAwsQueue";
    private static final String ENDPOINT = "https://sqs.us-east-2.amazonaws.com/894937511598/DesafioAwsQueue";

    @Autowired
    private QueueMessagingTemplate queueMessagingTemplate;

    @GetMapping("/send/{message}")
    public void enviaMensagemQueue(@PathVariable String message) {
        queueMessagingTemplate.send(ENDPOINT, MessageBuilder.withPayload(message).build());
    }

    @SqsListener(QUEUE)
    public void pesquisaMensagemQueue (S3EventNotification event) {
    	logger.info("Recebendo notificacoes de subida de arquivos: " + event.toJson());
    	
    	if (event.getRecords().size() > 0) {
            String bucket = event.getRecords().get(0).getS3().getBucket().getName();
            String key = event.getRecords().get(0).getS3().getObject().getKey();
            
            logger.info ("Bucket: " + bucket + " Forder/Arquivo: " + key + "\n");
            
            service.enviarEmailUsuarios(bucket, key);

          
        }

    }

}