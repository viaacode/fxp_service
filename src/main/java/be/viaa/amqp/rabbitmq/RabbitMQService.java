package be.viaa.amqp.rabbitmq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import be.viaa.amqp.AmqpConsumer;
import be.viaa.amqp.AmqpService;

/**
 * RabbitMQ AMQP implementation
 * 
 * @author Hannes Lowette
 *
 */
public class RabbitMQService implements AmqpService {

	/**
	 * The connection factory
	 */
	private final ConnectionFactory factory;
	
	/**
	 * The connection to the RabbitMQ service
	 */
	private final Connection connection;
	
	/**
	 * @param factory
	 */
	public RabbitMQService(ConnectionFactory factory) throws TimeoutException, IOException {
		this.factory = factory;
		this.connection = factory.newConnection();
	}

	/**
	 * @param host
	 * @param username
	 * @param password
	 * @throws IOException 
	 */
	public RabbitMQService(String host, String username, String password) throws TimeoutException, IOException {
		this.factory = new ConnectionFactory();
		this.factory.setHost(host);
		this.factory.setUsername(username);
		this.factory.setPassword(password);
		this.connection = factory.newConnection();
	}

	/**
	 * @param host
	 * @throws IOException 
	 */
	public RabbitMQService(String host) throws TimeoutException, IOException {
		this.factory = new ConnectionFactory();
		this.factory.setHost(host);
		this.connection = factory.newConnection();
	}
	
	/**
	 * Gets a new channel from 
	 * @return
	 * @throws IOException
	 * @throws TimeoutException
	 */
	private final Channel channel() throws IOException {
		return connection.createChannel();
	}

	@Override
	public void read(String queue, AmqpConsumer consumer) throws IOException {
		Channel channel = this.channel();
		channel.basicConsume(queue, new RabbitMQConsumer(channel, this, consumer));
	}

	@Override
	public void write(String queue, byte[] data) throws IOException {
		this.channel().basicPublish(null, queue, null, data);
	}

}
