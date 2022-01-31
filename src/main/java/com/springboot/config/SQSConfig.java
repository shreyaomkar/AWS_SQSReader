package com.springboot.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.config.SimpleMessageListenerContainerFactory;
import org.springframework.cloud.aws.messaging.config.annotation.EnableSqs;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.ClassUtils;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.client.builder.ExecutorFactory;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;

@Configuration
@EnableSqs
public class SQSConfig {

	@Value("${cloud.aws.region.static}")
	private String region;

	@Value("${cloud.aws.credentials.access-key}")
	private String awsAccessKey;

	@Value("${cloud.aws.credentials.secret-key}")
	private String awsSecretKey;

	private static final String DEFAULT_THREAD_NAME_PREFIX = ClassUtils.getShortName(SimpleMessageListenerContainer.class) + "-";

	@Bean
	public QueueMessagingTemplate queueMessagingTemplate() {
		return new QueueMessagingTemplate(amazonSQSAsync());
	}

	public AmazonSQSAsync amazonSQSAsync() {
		return AmazonSQSAsyncClientBuilder.standard().withRegion(Regions.US_EAST_1)
				.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsAccessKey, awsSecretKey)))
				.build();
	}
	
	@Bean
    public ClientConfiguration sqsClientConfiguration() {
        return new ClientConfiguration()
                .withConnectionTimeout(30000)
                .withRequestTimeout(30000)
                .withClientExecutionTimeout(30000);
    }

    @Bean
    public ExecutorFactory sqsExecutorFactory() {
        return new ExecutorFactory() {
            @Override
            public ExecutorService newExecutor() {
                return new ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
            }
        };
    }


    @Bean
    public AsyncTaskExecutor queueContainerTaskEecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setThreadNamePrefix(DEFAULT_THREAD_NAME_PREFIX);
        threadPoolTaskExecutor.setCorePoolSize(2);
        threadPoolTaskExecutor.setMaxPoolSize(2);
        // No use of a thread pool executor queue to avoid retaining message to long in memory
        threadPoolTaskExecutor.setQueueCapacity(0);
        threadPoolTaskExecutor.afterPropertiesSet();
        return threadPoolTaskExecutor;
    }

    @Bean
    public SimpleMessageListenerContainerFactory simpleMessageListenerContainerFactory(AmazonSQSAsync amazonSqs, AsyncTaskExecutor queueContainerTaskEecutor) {
        SimpleMessageListenerContainerFactory factory = new SimpleMessageListenerContainerFactory();
        factory.setAmazonSqs(amazonSqs);
        factory.setAutoStartup(true);
        factory.setMaxNumberOfMessages(1);
        factory.setWaitTimeOut(20);
        factory.setTaskExecutor(queueContainerTaskEecutor);
        return factory;
    }
	
    @Bean
    public static AmazonS3Client amazonS3Client() {
        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .build();
    }
    
//	@Bean
//	public SimpleMessageListenerContainer simpleMessageListenerContainer() {
//	    SimpleMessageListenerContainer msgListenerContainer = simpleMessageListenerContainerFactory()
//	            .createSimpleMessageListenerContainer();
//	    msgListenerContainer.setMessageHandler(queueMessageHandler());
//	    return msgListenerContainer;
//	}
//
//	@Bean
//	public SimpleMessageListenerContainerFactory simpleMessageListenerContainerFactory() {
//	    SimpleMessageListenerContainerFactory msgListenerContainerFactory = new SimpleMessageListenerContainerFactory();
//	    msgListenerContainerFactory.setAmazonSqs(amazonSQSAsync());
//	    return msgListenerContainerFactory;
//	}
//
//	@Bean
//	public QueueMessageHandler queueMessageHandler() {
//	    QueueMessageHandlerFactory queueMsgHandlerFactory = new QueueMessageHandlerFactory();
//	    queueMsgHandlerFactory.setAmazonSqs(amazonSQSAsync());
//	    QueueMessageHandler queueMessageHandler = queueMsgHandlerFactory.createQueueMessageHandler();
//	    List<HandlerMethodArgumentResolver> list = new ArrayList<>();
//	    HandlerMethodArgumentResolver resolver = new PayloadArgumentResolver(new MappingJackson2MessageConverter());
//	    list.add(resolver);
//	    queueMessageHandler.setArgumentResolvers(list);
//	    return queueMessageHandler;
//	}
}