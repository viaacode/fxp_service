package be.viaa.amqp;

import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Abstract AMQP service representation
 * 
 * @author Hannes Lowette
 *
 */
public interface AmqpService {
	
	/**
	 * 
	 * @throws Exception
	 */
	void createIfNotExists(String name) throws Exception;

	/**
	 * 
	 * @param queue
	 * @param consumer
	 */
	void read(String queue, AmqpConsumer consumer) throws IOException, TimeoutException;

	/**
	 * 
	 * @param queue
	 * @param data
	 */
	void write(String queue, byte[] data, Channel channel) throws IOException;

}
