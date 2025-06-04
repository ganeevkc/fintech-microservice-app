package com.finverse.profile.config;

import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    @Value("${app.events.exchange}")
    private String exchangeName;

    @Value("${app.events.queue}")
    private String queueName;

    @Value("${app.events.routing-key}")
    private String routingKey;

    @Bean
    public TopicExchange userEventsExchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    public Queue userRegisteredQueue() {
        return new Queue(queueName);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(userRegisteredQueue())
                .to(userEventsExchange())
                .with(routingKey);
    }

    @Bean
    public CachingConnectionFactory connectionFactory(
            @Value("${spring.rabbitmq.host}") String host,
            @Value("${spring.rabbitmq.port}") int port,
            @Value("${spring.rabbitmq.username}") String username,
            @Value("${spring.rabbitmq.password}") String password
    ) {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setRequestedHeartBeat(30); // Keepalive
        return factory;
    }
//    @Bean
//    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
//            ConnectionFactory connectionFactory) {
//
//        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
//        factory.setConnectionFactory((org.springframework.amqp.rabbit.connection.ConnectionFactory) connectionFactory);
//        factory.setMissingQueuesFatal(false); // Survive queue deletions
//        factory.setRecoveryInterval(5000L); // 5s reconnect attempts
//        return factory;
//    }
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}