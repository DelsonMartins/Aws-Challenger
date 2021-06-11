package com.desafio.awsdesafio.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.ObjectTagging;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.SetObjectTaggingRequest;
import com.amazonaws.services.s3.model.Tag;
import com.desafio.awsdesafio.config.property.AwsDesafioApiProperty;
import com.desafio.awsdesafio.dto.InfoArqDTO;

@Service
public class S3Service {
	
	private Logger logger = LoggerFactory.getLogger(S3Service.class);
	
	@Autowired
	private AmazonS3 amazonS3;
	
	@Autowired
	private AwsDesafioApiProperty property;
	
	public String salvarArquivo(MultipartFile arquivo, String name) {
		AccessControlList acl = new AccessControlList();
		acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);
		
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(arquivo.getContentType());
		objectMetadata.setContentLength(arquivo.getSize());
		
		String diretorio = "USER_" + name.substring(0, name.indexOf("@")).toUpperCase() + "/";
		
		logger.info("Diretorio: [" + diretorio + "]");
		
		//String nomeUnico = gerarNomeUnico(arquivo.getOriginalFilename(), diretorio);
		
		String newName = diretorio + arquivo.getOriginalFilename();
		
		logger.info("Arquivo: [" + newName + "]");
		
		try {
			PutObjectRequest putObjectRequest = new PutObjectRequest(
					property.getS3().getBucket(),
					newName,
					arquivo.getInputStream(), 
					objectMetadata)
					.withAccessControlList(acl);
			
			putObjectRequest.setTagging(new ObjectTagging(
					Arrays.asList(new Tag("expirar", "true"))));
			
			amazonS3.putObject(putObjectRequest);
			
			if (logger.isDebugEnabled()) {
				logger.debug("Arquivo {} enviado com sucesso para o S3.", 
						arquivo.getOriginalFilename());
			}
			
			return newName;
		} catch (IOException e) {
			throw new RuntimeException("Problemas ao tentar enviar o arquivo para o S3.", e);
		}
	}
	
	public String configurarUrl(String objeto) {
		//return "\\\\" + property.getS3().getBucket() + 
			//	".s3.us-east-2.amazonaws.com/" + objeto;
		
		return "http://" + property.getS3().getBucket() + 
				".s3.us-east-2.amazonaws.com/" + objeto;
	}
	
	public void salvarOld(String objeto) {
		SetObjectTaggingRequest setObjectTaggingRequest = new SetObjectTaggingRequest(
				property.getS3().getBucket(), 
				objeto, 
				new ObjectTagging(Collections.emptyList()));
		
		amazonS3.setObjectTagging(setObjectTaggingRequest);
	}

	public void remover(String objeto, String name) {
		
		String diretorio = "USER_" + name.substring(0, name.indexOf("@")).toUpperCase() + "/";
		
		logger.info("Diretorio: [" + diretorio + "]");
		
		String arquivo  = diretorio + objeto;
		
		logger.info("Arquivo: [" + arquivo + "]");
		
		DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(
				property.getS3().getBucket(), arquivo);
		
		amazonS3.deleteObject(deleteObjectRequest);
		
		if (logger.isDebugEnabled()) {
			logger.debug("Arquivo sendo removido do S3.");
		}
	}
	
	/*public void substituir(String objetoAntigo, String objetoNovo) {
		if (StringUtils.hasText(objetoAntigo)) {
			this.remover(objetoAntigo);
		}
		
		salvar(objetoNovo);
	} */
	
	private String gerarNomeUnico(String originalFilename, String diretorio) {
		return diretorio + UUID.randomUUID().toString() + "_" + originalFilename;
	}
	
	public List<InfoArqDTO> listFiles2(String name, String historical) {
		
		String diretorio = "USER_" + name.substring(0, name.indexOf("@")).toUpperCase() + "/";
		
		logger.info("Diretorio: [" + diretorio + "]");
		
		ListObjectsRequest listObjectsRequest = 
	              new ListObjectsRequest()
	                    .withBucketName(property.getS3().getBucket())
	                    .withPrefix(diretorio);
			
		List<InfoArqDTO> keys = new ArrayList<>();
			
		ObjectListing objects = amazonS3.listObjects(listObjectsRequest);		
			
		while (true) {
			List<S3ObjectSummary> summaries = objects.getObjectSummaries();
			if (summaries.size() < 1) {
				break;
			}
				
				
			for (S3ObjectSummary item : summaries) {
				if (!item.getKey().endsWith("/")) {	
								
					// Buscar objetos com classe de storage nao-padrao
					if (historical.contains("true")) {
						if (item.getStorageClass().equalsIgnoreCase("STANDARD")) {
							continue;
						} 
					}
													
					InfoArqDTO infoArqDTO = new InfoArqDTO();
					infoArqDTO.setName(item.getKey());
					infoArqDTO.setSize(item.getSize());
					infoArqDTO.setLastModif(item.getLastModified().toString());
					infoArqDTO.setStorageClass(item.getStorageClass());
					infoArqDTO.setUrl(configurarUrl(item.getKey()));
					keys.add(infoArqDTO);
				}
			}
				
			objects = amazonS3.listNextBatchOfObjects(objects);
		}
			
		return keys;
	}
	
	public List<String> listFiles() {
		
		  ListObjectsRequest listObjectsRequest = 
	              new ListObjectsRequest()
	                    .withBucketName(property.getS3().getBucket());
	                    //.withPrefix("test" + "/");
			
			List<String> keys = new ArrayList<>();
			
			ObjectListing objects = amazonS3.listObjects(listObjectsRequest);
			
			while (true) {
				List<S3ObjectSummary> summaries = objects.getObjectSummaries();
				if (summaries.size() < 1) {
					break;
				}
				
				for (S3ObjectSummary item : summaries) {
		            if (!item.getKey().endsWith("/"))
		            	keys.add(item.getKey());
		        }
				
				objects = amazonS3.listNextBatchOfObjects(objects);
			}
			
			return keys;
		}


	public ByteArrayOutputStream downloadFile(String keyName) {
		try {
            S3Object s3object = amazonS3.getObject(new GetObjectRequest(property.getS3().getBucket(), keyName));
            
            InputStream is = s3object.getObjectContent();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int len;
            byte[] buffer = new byte[4096];
            while ((len = is.read(buffer, 0, buffer.length)) != -1) {
                baos.write(buffer, 0, len);
            }
            
            return baos;
		} catch (IOException ioe) {
			logger.error("IOException: " + ioe.getMessage());
        } catch (AmazonServiceException ase) {
        	logger.info("sCaught an AmazonServiceException from GET requests, rejected reasons:");
			logger.info("Error Message:    " + ase.getMessage());
			logger.info("HTTP Status Code: " + ase.getStatusCode());
			logger.info("AWS Error Code:   " + ase.getErrorCode());
			logger.info("Error Type:       " + ase.getErrorType());
			logger.info("Request ID:       " + ase.getRequestId());
			throw ase;
        } catch (AmazonClientException ace) {
        	logger.info("Caught an AmazonClientException: ");
            logger.info("Error Message: " + ace.getMessage());
            throw ace;
        }
		
		return null;
	}
 

	public void uploadFile(String keyName, MultipartFile file) {
		try {
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(file.getSize());
			amazonS3.putObject(property.getS3().getBucket(), keyName, file.getInputStream(), metadata);
		} catch(IOException ioe) {
			logger.error("IOException: " + ioe.getMessage());
		} catch (AmazonServiceException ase) {
			logger.info("Caught an AmazonServiceException from PUT requests, rejected reasons:");
			logger.info("Error Message:    " + ase.getMessage());
			logger.info("HTTP Status Code: " + ase.getStatusCode());
			logger.info("AWS Error Code:   " + ase.getErrorCode());
			logger.info("Error Type:       " + ase.getErrorType());
			logger.info("Request ID:       " + ase.getRequestId());
			throw ase;
        } catch (AmazonClientException ace) {
            logger.info("Caught an AmazonClientException: ");
            logger.info("Error Message: " + ace.getMessage());
            throw ace;
        }
	}
		
}
