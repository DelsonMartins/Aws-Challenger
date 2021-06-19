package com.desafio.awsdesafio.athena;

import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.athena.AthenaClient;
import software.amazon.awssdk.services.athena.AthenaClientBuilder;

public class AthenaClientFactory {
	
	private final AthenaClientBuilder builder = AthenaClient.builder()
            .region(Region.US_EAST_2)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create());

    public AthenaClient createClient() {
        return builder.build();
    }

}
