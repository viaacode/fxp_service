package be.viaa;

import be.viaa.amqp.AmqpPulseService;
import be.viaa.amqp.AmqpService;
import be.viaa.amqp.rabbitmq.RabbitMQService;
import be.viaa.fxp.amqp.FxpConsumer;

/**
 * Contains the entry point of the application
 * 
 * @author Hannes Lowette
 *
 */
public class Application {

	/**
	 * 
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		AmqpService service = new RabbitMQService("localhost", "", "");
		AmqpPulseService poller = new AmqpPulseService(service);
		
		poller.addListener("viaa-test", new FxpConsumer());
		poller.start();
	}

}
