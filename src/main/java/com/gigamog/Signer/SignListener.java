package com.gigamog.Signer;

import com.amazonaws.services.s3.event.S3EventNotification;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.config.QueueMessageHandlerFactory;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.support.PayloadArgumentResolver;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class SignListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(SignListener.class);

    @Value("${signer.script}")
    private String signerScript;

    @SqsListener(value = "signs", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void onS3UploadEvent(@Payload Metadata metadata) {
        try {
            ProcessBuilder pb = new ProcessBuilder(signerScript);
            Process p = pb.start();     // Start the process.
            p.waitFor();                // Wait for the process to finish.
            LOGGER.info("Script executed successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Bean
    public AmazonSQSAsync amazonSQS() {
        return AmazonSQSAsyncClient.asyncBuilder().build();
    }

    @Bean
    public QueueMessageHandlerFactory queueMessageHandlerFactory() {
        QueueMessageHandlerFactory factory = new QueueMessageHandlerFactory();
        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();

        //set strict content type match to false
        messageConverter.setStrictContentTypeMatch(false);
        factory.setArgumentResolvers(Collections.<HandlerMethodArgumentResolver>singletonList(new PayloadArgumentResolver(messageConverter)));
        return factory;
    }

    @Bean
    public QueueMessagingTemplate queueMessagingTemplate(AmazonSQSAsync amazonSQSAsync) {
        return new QueueMessagingTemplate(amazonSQSAsync);
    }

}
