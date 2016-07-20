package be.viaa.amqp;

import com.google.gson.Gson;

/**
 * Converts an AMQP JSON message to a POJO
 *  
 * @author Hannes Lowette
 *
 * @param <T>
 */
public abstract class AmqpJsonConsumer<T> implements AmqpConsumer {

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
		this.accept(service, gson.fromJson(new String(data), type));
	}

	/* (non-Javadoc)
	 * @see be.viaa.amqp.AmqpConsumer#success(be.viaa.amqp.AmqpService, java.lang.byte[])
	 */
	@Override
	public void success(AmqpService service, byte[] data) throws Exception {
		this.success(service, gson.fromJson(new String(data), type));
	}

	/* (non-Javadoc)
	 * @see be.viaa.amqp.AmqpConsumer#exception(be.viaa.amqp.AmqpService, java.lang.Exception)
	 */
	@Override
	public void exception(AmqpService service, Exception exception, byte[] data) {
		exception(service, exception, gson.fromJson(new String(data), type));
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
	protected abstract void success(AmqpService service, T message) throws Exception;

	/**
	 * Called when the JSON message has been parsed into a POJO
	 * 
	 * @param service
	 * @param object
	 */
	protected abstract void exception(AmqpService service, Exception exception, T message);

}
