package com.desafio.awsdesafio.controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.desafio.awsdesafio.dto.InfoArqDTO;
import com.desafio.awsdesafio.services.S3Service;

@RestController
@RequestMapping(value = "/aws/file")
public class S3Controller {
	
	private Logger logger = LoggerFactory.getLogger(S3Controller.class);
	
	@Autowired
	private S3Service service;
	
	@PostMapping("/anexo")
	//@PreAuthorize("hasAuthority('ROLE_UPLOAD_ARQUIVO') and #oauth2.hasScope('write')")
	public InfoArqDTO uploadAnexo(@RequestParam MultipartFile anexo) throws IOException {
		
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    String name = auth.getName();
		
	    logger.info("TESTANDO UPLOAD USUARIO LOGADO [" + name + "]");
	      	
		
		String nome = service.salvarTemporariamente(anexo, name);
		InfoArqDTO infoArqDTO = new InfoArqDTO();
		infoArqDTO.setName(nome);
		infoArqDTO.setSize(anexo.getSize());
		infoArqDTO.setUrl(service.configurarUrl(nome));
		
		return infoArqDTO;
	}
	
	//@PreAuthorize("hasAuthority('ROLE_REMOVER_ARQUIVO') and #oauth2.hasScope('write')")
	@DeleteMapping("/{keyname}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
		public void remover(@PathVariable String keyname) {
		
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    String name = auth.getName();
		
	    logger.info("TESTANDO REMOCAO USUARIO LOGADO [" + name + "]");
	      	
	    
		service.remover(keyname, name);
		
	}
	
	//@PreAuthorize (String test = principal.username)
	@GetMapping(value = "/all")
	public ResponseEntity<List<InfoArqDTO>> listAllFiles(){
		
		
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    String name = auth.getName();
		
	    logger.info("TESTANDO PESQUISA USUARIO LOGADO [" + name + "]");
	      	
		List<InfoArqDTO> list = service.listFiles2(name);
		return ResponseEntity.ok().body(list);
	}
	
	
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_ARQUIVO') and #oauth2.hasScope('read')")
	@GetMapping(value = "/{keyname}")
	public ResponseEntity<byte[]> downloadFile(@PathVariable String keyname) {
		ByteArrayOutputStream downloadInputStream = service.downloadFile(keyname);
	
		return ResponseEntity.ok()
					.contentType(contentType(keyname))
					.header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\"" + keyname + "\"")
					.body(downloadInputStream.toByteArray());	
		
	}
	
	 @PostMapping(value = "/upload")
	    public String uploadMultipartFile(@RequestParam("file") MultipartFile file) {
	    	String keyName = file.getOriginalFilename();
	    	service.uploadFile(keyName, file);
			return "Upload Successfully -> KeyName = " + keyName;
	    } 
	
	private MediaType contentType(String keyname) {
		String[] arr = keyname.split("\\.");
		String type = arr[arr.length-1];
		switch(type) {
			case "txt": return MediaType.TEXT_PLAIN;
			case "png": return MediaType.IMAGE_PNG;
			case "jpg": return MediaType.IMAGE_JPEG;
			default: return MediaType.APPLICATION_OCTET_STREAM;
		}
	}

}
