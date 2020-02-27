package io.naztech.jobharvestar.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.naztech.jobharvestar.crawler.StartScraperFromQueue;

/**
 * RabbitMQ spring configuration.
 * 
 * @author Mahmud Rana
 * @since 2019-03-25
 */
@Configuration
@EnableRabbit
public class RabbitConfig {

	@Bean
	Queue jobQueue() {
		return QueueBuilder.durable("job-queue").build();
	}

	@Bean
	Exchange jobExchange() {
		return ExchangeBuilder.topicExchange("job-exchange").build();
	}

	@Bean
	Binding jobBinding(Queue jobQueue, TopicExchange jobExchange) {
		return BindingBuilder.bind(jobQueue).to(jobExchange).with(jobQueue.getName());
	}
	
	@Bean
	Queue scraperQueue() {
		return QueueBuilder.durable("scraper-queue").build();
	}
	
	@Bean
	Exchange scraperExchange() {
		return ExchangeBuilder.topicExchange("scraper-exchange").build();
	}
	
	@Bean
	Binding scraperBinding(Queue scraperQueue, TopicExchange scraperExchange) {
		return BindingBuilder.bind(scraperQueue).to(scraperExchange).with(scraperQueue.getName());
	}

	@Bean @Profile("prod")
	MessageListenerAdapter listenerAdapter(StartScraperFromQueue receiver) {
		return new MessageListenerAdapter(receiver, "executeScraper");
	}

	//https://stackoverflow.com/questions/37557622/is-it-possible-to-set-prefetch-count-on-rabbitlistener
	@Bean @Profile("prod")
	SimpleMessageListenerContainer container(ConnectionFactory factory, MessageListenerAdapter adapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(factory);
		container.setQueueNames("scraper-queue");
		container.setMessageListener(adapter);
		container.setPrefetchCount(1);
		return container;
	}
}
