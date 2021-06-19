package com.desafio.awsdesafio.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.config.QueueMessageHandlerFactory;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.PayloadMethodArgumentResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.desafio.awsdesafio.config.property.AwsDesafioApiProperty;

import java.util.Collections;



@Configuration
public class MessagingConfig {
	
	@Autowired
	private AwsDesafioApiProperty property;
	
	// @Bean annotation tells that a method produces a bean that is to be managed by the spring container.
    @Bean
    public QueueMessagingTemplate queueMessagingTemplate() {
        return new QueueMessagingTemplate(amazonSQSAsync());
    }
    
    @Bean
    public QueueMessageHandlerFactory queueMessageHandlerFactory() {
        QueueMessageHandlerFactory factory = new QueueMessageHandlerFactory();
        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
        messageConverter.setStrictContentTypeMatch(false);
        factory.setArgumentResolvers(Collections.singletonList(new PayloadMethodArgumentResolver(messageConverter)));
        return factory;
    }
    
    
    
    @Bean
    // @Primary annotation gives a higher preference to a bean (when there are multiple beans of the same type).
    @Primary
    // AmazonSQSAsync is an interface for accessing the SQS asynchronously. 
    // Each asynchronous method will return a Java Future object representing the asynchronous operation.
    public AmazonSQSAsync amazonSQSAsync() {
    	
    	AWSCredentials credenciais = new BasicAWSCredentials(
    			property.getS3().getAccessKeyId(), property.getS3().getSecretAccessKey());

    	
        return AmazonSQSAsyncClientBuilder.standard()
        		.withCredentials(new AWSStaticCredentialsProvider(credenciais))
        		.withRegion(Regions.US_EAST_2)                
                .build();
    }
}