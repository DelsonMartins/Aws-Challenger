package com.desafio.awsdesafio.mail;


//import java.util.Arrays;
//import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

//import com.desafio.awsdesafio.entities.Usuario;
//import com.desafio.awsdesafio.repositories.UsuarioRepository;

@Component
public class Mailer {
	
	@Autowired
	private TemplateEngine thymeleaf;
	
	@Autowired
	private JavaMailSender mailSender;
	
//	@EventListener
//	private void teste(ApplicationReadyEvent event) {
//		this.enviarEmail("aws.desafio.manager@gmail.com", 
//				Arrays.asList("delson2004@yahoo.com.br"), 
//				"Testando", "Olá!<br/>Teste ok.");
//		System.out.println("Terminado o envio de e-mail...");
//	}
	
//	@Autowired
//	private UsuarioRepository repo;
//	@EventListener
//	private void teste(ApplicationReadyEvent event) {
//		String template = "mail/aviso-novos-arquivos";
		
//		List<Usuario> lista = repo.findAll();
		
//		Map<String, Object> variaveis = new HashMap<>();
//		variaveis.put("usuarios", lista);
		
//		this.enviarEmail("aws.desafio.manager@gmail.com", 
//				Arrays.asList("delson2004@yahoo.com.br"), 
//				"Testando", template, variaveis);
//		System.out.println("Terminado o envio de e-mail...");
//	}
	
	
	public void enviarEmail(String remetente, 
			List<String> destinatarios, String assunto, String template, 
			Map<String, Object> variaveis) {
		Context context = new Context(new Locale("pt", "BR"));
		
		variaveis.entrySet().forEach(
				e -> context.setVariable(e.getKey(), e.getValue()));
		
		String mensagem = thymeleaf.process(template, context);
		
		this.enviarEmail(remetente, destinatarios, assunto, mensagem);
	}
	
	public void enviarEmail(String remetente, 
			List<String> destinatarios, String assunto, String mensagem) {
		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
			helper.setFrom(remetente);
			helper.setTo(destinatarios.toArray(new String[destinatarios.size()]));
			helper.setSubject(assunto);
			helper.setText(mensagem, true);
			
			mailSender.send(mimeMessage);
		} catch (MessagingException e) {
			throw new RuntimeException("Problemas com o envio de e-mail!", e); 
		}
	}
}