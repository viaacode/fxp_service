package be.viaa;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import be.viaa.amqp.AmqpBatchService;
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
	 * The logger for this class
	 */
	private static final Logger logger = LogManager.getLogger(Application.class);

	/**
	 * 
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		logger.info("starting application...");

		/*
		 * Read the properties file
		 */
		String propertiesFile = args.length == 2 && args[0].equals("-p") ? args[1] : "./application.properties";
		Properties properties = new Properties();
		properties.load(new FileReader(new File(propertiesFile)));
		String host = properties.getProperty("mq.rabbit.host");
		String username = properties.getProperty("mq.rabbit.username");
		String password = properties.getProperty("mq.rabbit.password");
		String request_queue = properties.getProperty("mq.rabbit.request_queue");
		String response_queue = properties.getProperty("mq.rabbit.response_queue");

		/*
		 * If there is no host specified, exit the program
		 */
		if (host == null || host.equals("")) {
			throw new IOException("no host specified");
		}

		try {
			AmqpService service = new RabbitMQService(host, username, password);
			AmqpBatchService poller = new AmqpBatchService(service);
			
			service.createIfNotExists(request_queue);
			service.createIfNotExists(response_queue);

			poller.addListener(request_queue, new FxpConsumer());
			poller.start();
		} catch (Exception exception) {
			logger.fatal("Could not connect to the MQ server: " + exception.getMessage());
			logger.catching(exception);
		}
	}

}
