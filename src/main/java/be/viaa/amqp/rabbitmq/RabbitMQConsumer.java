package be.viaa.amqp.rabbitmq;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

import be.viaa.amqp.AmqpConsumer;
import be.viaa.amqp.AmqpService;

/**
 * RabbitMQ consumer that routes the body to the AMQPConsumer
 * 
 * @author Hannes Lowette
 *
 */
public class RabbitMQConsumer extends DefaultConsumer {

	/**
	 * The amqp consumer that needs to be invoked
	 */
	private final AmqpConsumer consumer;

	/**
	 * The amqp service that this consumer works for
	 */
	private final AmqpService service;

	/**
	 * @param channel
	 * @param consumer
	 */
	public RabbitMQConsumer(Channel channel, AmqpService service, AmqpConsumer consumer) {
		super(channel);
		this.consumer = consumer;
		this.service = service;
	}

	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) throws IOException {
		try {
			consumer.accept(service, body);
			consumer.success(service, body);
		} catch (Exception exception) {
			consumer.exception(service, exception, body);
		}
		finally {
			getChannel().basicAck(envelope.getDeliveryTag(), false);
		}
	}

}
