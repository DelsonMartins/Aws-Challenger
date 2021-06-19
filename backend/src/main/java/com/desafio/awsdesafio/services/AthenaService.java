package com.desafio.awsdesafio.services;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.desafio.awsdesafio.athena.AthenaClientFactory;
import com.desafio.awsdesafio.config.property.AwsDesafioApiProperty;
import com.desafio.awsdesafio.mail.Mailer;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import software.amazon.awssdk.services.athena.AthenaClient;
import software.amazon.awssdk.services.athena.model.ColumnInfo;
import software.amazon.awssdk.services.athena.model.Datum;
import software.amazon.awssdk.services.athena.model.GetQueryExecutionRequest;
import software.amazon.awssdk.services.athena.model.GetQueryExecutionResponse;
import software.amazon.awssdk.services.athena.model.GetQueryResultsRequest;
import software.amazon.awssdk.services.athena.model.GetQueryResultsResponse;
import software.amazon.awssdk.services.athena.model.QueryExecutionContext;
import software.amazon.awssdk.services.athena.model.QueryExecutionState;
import software.amazon.awssdk.services.athena.model.ResultConfiguration;
import software.amazon.awssdk.services.athena.model.Row;
import software.amazon.awssdk.services.athena.model.StartQueryExecutionRequest;
import software.amazon.awssdk.services.athena.model.StartQueryExecutionResponse;
import software.amazon.awssdk.services.athena.paginators.GetQueryResultsIterable;


@Service
public class AthenaService {
	
	private static final Logger logger = LoggerFactory.getLogger(AthenaService.class);
	
	private static final String ATHENA_DATABASE = "default";

    private static final String ATHENA_OUTPUT_S3_FOLDER_PATH = "s3://desafio-aws-s3-athena-query-results/resultados/";

    private static final String SIMPLE_ATHENA_QUERY = "SELECT * FROM cloudtrail_logs_aws_cloudtrail_logs_894937511598_9f24bb21 WHERE awsregion LIKE '%us-east-2%'AND "
    		+ "requestparameters LIKE '%desafio-aws-s3-files%prefix%SEARCH_USER%';";
    
    private static final long SLEEP_AMOUNT_IN_MS = 1000;
	
	@Autowired
	private AwsDesafioApiProperty property;
	
	@Autowired
	private Mailer mailer;
	
	public String infoEvents (String name) throws InterruptedException, IOException, DocumentException {
		
		
		String userName = "USER_" + name.substring(0, name.indexOf("@")).toUpperCase();
		
		logger.info("Username: [" + userName + "]");
		
		String queryUser = SIMPLE_ATHENA_QUERY.replace("SEARCH_USER", userName);
		
		logger.info("Pesquisa: [" + queryUser + "]");
		
		AthenaClientFactory factory = new AthenaClientFactory();
        AthenaClient athenaClient = factory.createClient();

        String queryExecutionId = submitAthenaQuery(athenaClient, queryUser);

        logger.info("Query submitted: " + System.currentTimeMillis());

        waitForQueryToComplete(athenaClient, queryExecutionId);

        logger.info("Query finished: " + System.currentTimeMillis());

        processResultRows(athenaClient, queryExecutionId, name);
        
		return null;
	}
	
	private static String submitAthenaQuery(AthenaClient athenaClient, String queryUser) {

        QueryExecutionContext queryExecutionContext = QueryExecutionContext.builder()
                .database(ATHENA_DATABASE).build();

        ResultConfiguration resultConfiguration = ResultConfiguration.builder()
                .outputLocation(ATHENA_OUTPUT_S3_FOLDER_PATH).build();

        StartQueryExecutionRequest startQueryExecutionRequest = StartQueryExecutionRequest.builder()
                .queryString(queryUser)
                .queryExecutionContext(queryExecutionContext)
                .resultConfiguration(resultConfiguration).build();

        StartQueryExecutionResponse startQueryExecutionResponse = athenaClient.startQueryExecution(startQueryExecutionRequest);

        return startQueryExecutionResponse.queryExecutionId();
    }

    private static void waitForQueryToComplete(AthenaClient athenaClient, String queryExecutionId) throws InterruptedException {

        GetQueryExecutionRequest getQueryExecutionRequest = GetQueryExecutionRequest.builder()
                .queryExecutionId(queryExecutionId).build();

        GetQueryExecutionResponse getQueryExecutionResponse;

        boolean isQueryStillRunning = true;

        while (isQueryStillRunning) {
            getQueryExecutionResponse = athenaClient.getQueryExecution(getQueryExecutionRequest);
            String queryState = getQueryExecutionResponse.queryExecution().status().state().toString();

            if (queryState.equals(QueryExecutionState.FAILED.toString())) {
                throw new RuntimeException("Query Failed to run with Error Message: " + getQueryExecutionResponse
                        .queryExecution().status().stateChangeReason());
            } else if (queryState.equals(QueryExecutionState.CANCELLED.toString())) {
                throw new RuntimeException("Query was cancelled.");
            } else if (queryState.equals(QueryExecutionState.SUCCEEDED.toString())) {
                isQueryStillRunning = false;
            } else {
                Thread.sleep(SLEEP_AMOUNT_IN_MS);
            }

            logger.info("Current Status is: " + queryState);
            
        }
    }

    public void processResultRows(AthenaClient athenaClient, String queryExecutionId, String userName) throws IOException, DocumentException {

        GetQueryResultsRequest getQueryResultsRequest = GetQueryResultsRequest.builder()
                .queryExecutionId(queryExecutionId).build();

        GetQueryResultsIterable getQueryResultsResults = athenaClient.getQueryResultsPaginator(getQueryResultsRequest);

        for (GetQueryResultsResponse Resultresult : getQueryResultsResults) {
            List<ColumnInfo> columnInfoList = Resultresult.resultSet().resultSetMetadata().columnInfo();

            int resultSize = Resultresult.resultSet().rows().size();
            logger.info("Result size: " + resultSize);

            List<Row> results = Resultresult.resultSet().rows();
            processRow(results, columnInfoList, userName);
        }
    }

    public void processRow(List<Row> rowList, List<ColumnInfo> columnInfoList, 
    								String name)  throws IOException, DocumentException {

        List<String> columns = new ArrayList<>();
        
        String template = "mail/aviso-audit-atividades";
        
        String userName = "USER_" + name.substring(0, name.indexOf("@")).toLowerCase();
        
        String nomeAnexo = "auditActivities_" +	userName + ".pdf";
        
        Document pdfDoc = new Document(PageSize.A4);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(pdfDoc, baos); 
        
        pdfDoc.open();
        
        Font myfont = new Font();
        
    	myfont.setStyle(Font.NORMAL);
		myfont.setSize(20);
        pdfDoc.add(new Paragraph("Registro de atividades: " + name +"\n", myfont));
        
		myfont.setStyle(Font.NORMAL);
		myfont.setSize(5);
		pdfDoc.add(new Paragraph("\n"));


        for (ColumnInfo columnInfo : columnInfoList) {
            columns.add(columnInfo.name());
        }

        for (Row row: rowList) {
            int index = 0; // Verificar por que linha 0 so apresenta o nome da coluna

            for (Datum datum : row.data()) {
                logger.info(columns.get(index) + ": " + datum.varCharValue());
                
                Paragraph para = new Paragraph(columns.get(index) + ": " + datum.varCharValue() + "\n", myfont);
                para.setAlignment(Element.ALIGN_JUSTIFIED);
                pdfDoc.add(para);
                index++;
            }
   
            Paragraph para = new Paragraph("===================================================="
            		+ "=========================================================================="
            		+ "====================================================="+ "\n", myfont);
            							
            para.setAlignment(Element.ALIGN_JUSTIFIED);
            pdfDoc.add(para);
            logger.info("===================================");
        }
        
        pdfDoc.close();
        
        DataSource source = new ByteArrayDataSource(baos.toByteArray(), "application/pdf");
        
        mailer.enviarEmailAudit ("aws.desafio.manager@gmail.com", Arrays.asList(name), 
				"Registros de atividades no sistema",
				template,
				nomeAnexo,
				source);
  
    }
    
   
}
