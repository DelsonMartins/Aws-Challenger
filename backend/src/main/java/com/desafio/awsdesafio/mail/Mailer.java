package com.desafio.awsdesafio.mail;


import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/*import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;
import javax.mail.util.ByteArrayDataSource;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;*/


import com.desafio.awsdesafio.entities.Usuario;
import com.desafio.awsdesafio.repositories.UsuarioRepository;

@Component
public class Mailer {
	
	@Autowired
	private TemplateEngine thymeleaf;
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private UsuarioRepository repoUsuario;
	
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
	

	/*
	 * @EventListener private void teste(ApplicationReadyEvent event) throws
	 * DocumentException {
	 * 
	 * String template = "mail/aviso-novos-arquivos";
	 * 
	 * Document pdfDoc = new Document(PageSize.A4);
	 * 
	 * ByteArrayOutputStream baos = new ByteArrayOutputStream();
	 * PdfWriter.getInstance(pdfDoc, baos);
	 * 
	 * pdfDoc.open();
	 * 
	 * Font myfont = new Font();
	 * 
	 * myfont.setStyle(Font.NORMAL); myfont.setSize(20); pdfDoc.add(new
	 * Paragraph("Registro de atividades: delson2004@yahoo.com.br\n", myfont));
	 * 
	 * myfont.setStyle(Font.NORMAL); myfont.setSize(5); pdfDoc.add(new
	 * Paragraph("\n"));
	 * 
	 * pdfDoc.add(new Paragraph("Testando email auditoria ..."));
	 * 
	 * pdfDoc.close();
	 * 
	 * DataSource source = new ByteArrayDataSource(baos.toByteArray(),
	 * "application/pdf");
	 * 
	 * String nomeAnexo = "auditActivities_" + "delson2004" + ".pdf";
	 * 
	 * this.enviarEmailAudit ("aws.desafio.manager@gmail.com",
	 * Arrays.asList("delson2004@yahoo.com.br"),
	 * "Registros de atividades no sistema", template, nomeAnexo, source);
	 * 
	 * System.out.println("Terminado o envio de e-mail auditoria ..."); }
	 */
	
	public void enviarEmailAvisoNovosArquivos(String remetente, String nomeBucket, String key) {
		
		
		String template = "mail/aviso-novos-arquivos";
		String assunto = "Novo arquivo disponibilizado na nuvem";
		
		String folder = key.substring(0, key.indexOf("/")+1);
		String nomeArquivo = key.substring(key.indexOf("/")+1, key.length()).toLowerCase();
		
		
		
		Context context = new Context(new Locale("pt", "BR"));
		
		
		context.setVariable("bucket", nomeBucket);
		context.setVariable("folder", folder);
		context.setVariable("arquivo", nomeArquivo);
			
		List<Usuario> usuarios = repoUsuario.findAll();
		
		List<String> emailsDest = usuarios.stream()
				.map(u -> u.getEmail())
				.collect(Collectors.toList());
			
		String mensagem = thymeleaf.process(template, context);
		
		this.enviarEmail(remetente, emailsDest, assunto, mensagem, null, null);
	}
	
	public void enviarEmailAudit(String remetente, 
			List<String> destinatarios, String assunto, String template, String nomeAnexo, DataSource anexo) {
		
		Context context = new Context(new Locale("pt", "BR"));
		String mensagem = thymeleaf.process(template, context);
		
		this.enviarEmail(remetente, destinatarios, assunto, mensagem, nomeAnexo, anexo);
	}
	
	public void enviarEmail(String remetente, List<String> destinatarios, String assunto, String mensagem,
			String nomeAnexo, DataSource anexo) {
		try {
			
						
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true, "UTF-8");
			helper.setFrom(remetente);
			helper.setTo(destinatarios.toArray(new String[destinatarios.size()]));
			helper.setSubject(assunto);
			helper.setText(mensagem, true);
			
					
			if (anexo != null) {
				helper.addAttachment(nomeAnexo, anexo);
			}
			
			
			mailSender.send(mimeMessage);
			
		} catch (MessagingException e) {
			throw new RuntimeException("Problemas com o envio de e-mail!", e); 
		}
	}
}