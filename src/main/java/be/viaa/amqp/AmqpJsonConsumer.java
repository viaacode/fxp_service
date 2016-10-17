package be.viaa.amqp;

import com.rabbitmq.client.Channel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * Converts an AMQP JSON message to a POJO
 *  
 * @author Hannes Lowette
 *
 * @param <T>
 */
public abstract class AmqpJsonConsumer<T> implements AmqpConsumer {
	
	/**
	 * static logger for this class
	 */
	private static final Logger logger = LogManager.getLogger(AmqpJsonConsumer.class);

	/**
	 * The gson instance to convert POJO to JSON
	 */
	private final Gson gson = new Gson();
	
	/**
	 * The type
	 */
	private final Class<T> type;

	/**
	 * @param type
	 */
	public AmqpJsonConsumer(Class<T> type) {
		this.type = type;
	}

	/* (non-Javadoc)
	 * @see be.viaa.amqp.AmqpConsumer#accept(be.viaa.amqp.AmqpService, java.lang.byte[])
	 */
	@Override
	public void accept(AmqpService service, byte[] data) throws Exception {
		logger.info("Message received: {}", new String(data));
		this.accept(service, gson.fromJson(new String(data), type));
	}

	/* (non-Javadoc)
	 * @see be.viaa.amqp.AmqpConsumer#success(be.viaa.amqp.AmqpService, java.lang.byte[])
	 */
	@Override
	public void success(AmqpService service, byte[] data, Channel channel) throws Exception {
		this.success(service, gson.fromJson(new String(data), type), channel);
	}

	/* (non-Javadoc)
	 * @see be.viaa.amqp.AmqpConsumer#exception(be.viaa.amqp.AmqpService, java.lang.Exception)
	 */
	@Override
	public void exception(AmqpService service, Exception exception, byte[] data, Channel channel) {
		try {
			exception(service, exception, gson.fromJson(new String(data), type), channel);
		} catch (JsonSyntaxException ex) {
			logger.error("Malformed JSON received: {}\n{}", ex.getMessage(), new String(data));
		}
	}

	/**
	 * Called when the JSON message has been parsed into a POJO
	 * 
	 * @param service
	 * @param object
	 */
	protected abstract void accept(AmqpService service, T message) throws Exception;

	/**
	 * Called when the JSON message has been parsed into a POJO
	 * 
	 * @param service
	 * @param object
	 */
	protected abstract void success(AmqpService service, T message, Channel channel) throws Exception;

	/**
	 * Called when the JSON message has been parsed into a POJO
	 * 
	 * @param service
	 * @param object
	 */
	protected abstract void exception(AmqpService service, Exception exception, T message, Channel channel);

}
