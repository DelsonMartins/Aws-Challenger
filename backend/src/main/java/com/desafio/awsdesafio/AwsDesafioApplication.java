package com.desafio.awsdesafio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.desafio.awsdesafio.config.property.AwsDesafioApiProperty;

@SpringBootApplication
@EnableConfigurationProperties(AwsDesafioApiProperty.class)
public class AwsDesafioApplication {

	public static void main(String[] args) {
		SpringApplication.run(AwsDesafioApplication.class, args);
	}

}
