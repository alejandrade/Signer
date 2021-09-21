package com.gigamog.Signer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.aws.messaging.config.annotation.EnableSqs;

@EnableSqs
@SpringBootApplication
public class SignerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SignerApplication.class, args);
	}

}
