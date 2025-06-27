package com.finverse.profile.config;

//import com.rabbitmq.client.ConnectionFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@EnableRabbit
@Slf4j
public class RabbitConfig {
    @Value("${app.events.exchange}")
    private String exchange;

    @Value("${app.events.queue}")
    private String queue;

    @Value("${app.events.routing-key}")
    private String routingKey;

    @PostConstruct
    public void logStartup() {
        log.info("=== PROFILE SERVICE RABBIT CONFIG LOADING ===");
        log.info("Exchange: {}, Queue: {}, Routing Key: {}", exchange, queue, routingKey);
    }
    @Bean
    public TopicExchange userEventsExchange() {
        log.info("Creating exchange: {}", exchange);
        return new TopicExchange(exchange);
    }

    @Bean
    public Queue userRegisteredQueue() {
        log.info("Creating queue: {}", queue);
        return QueueBuilder.durable(queue).build();
    }

    @Bean
    public Binding userRegisteredBinding() {
        log.info("Creating binding: {} -> {} with routing key: {}", queue, exchange, routingKey);
        return BindingBuilder
                .bind(userRegisteredQueue())
                .to(userEventsExchange())
                .with(routingKey);
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate((ConnectionFactory) connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}