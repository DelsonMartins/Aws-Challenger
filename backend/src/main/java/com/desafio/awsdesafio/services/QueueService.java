package com.desafio.awsdesafio.services;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.desafio.awsdesafio.config.property.AwsDesafioApiProperty;
import com.desafio.awsdesafio.mail.Mailer;

@Service
public class QueueService {
	
	private static final Logger logger = LoggerFactory.getLogger(QueueService.class);
	
	@Autowired
	private AwsDesafioApiProperty property;
	
	@Autowired
	private Mailer mailer;
	
	public void enviarEmailUsuarios (String nomeBucket, String nomeArquivo) {
		
		String remetente = "aws.desafio.manager@gmail.com";
		
		mailer.enviarEmailAvisoNovosArquivos (remetente, nomeBucket, nomeArquivo );
	  
		
		
	}

}
