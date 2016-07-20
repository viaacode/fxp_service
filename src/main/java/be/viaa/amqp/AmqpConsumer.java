package be.viaa.amqp;

/**
 * Consumes an AMQP message
 * 
 * @author Hannes Lowette
 *
 */
public interface AmqpConsumer {

	/**
	 * Called the moment the AMQP service has received a message
	 * 
	 * @param service
	 * @param data
	 */
	void accept(AmqpService service, byte[] data) throws Exception;
	
	/**
	 * Called when the accept method has returned succesfully
	 * 
	 * @param service
	 */
	void success(AmqpService service, byte[] data) throws Exception;

	/**
	 * Called when an exception has occurred in the accept method
	 * 
	 * @param service
	 * @param exception
	 */
	void exception(AmqpService service, Exception exception, byte[] data);

}
