package be.viaa;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
		String properties_file = args.length == 2 && args[0].equals("-p") ? args[1] : "./fxp.properties";
		Properties properties = new Properties();
		properties.load(new FileReader(new File(properties_file)));
		String host = properties.getProperty("mq.rabbit.host");
		String username = properties.getProperty("mq.rabbit.username");
		String password = properties.getProperty("mq.rabbit.password");

		/*
		 * If there is no host specified, exit the program
		 */
		if (host == null || host.equals("")) {
			throw new IOException("no host specified");
		}

		try {
			AmqpService service = new RabbitMQService(host, username, password);
			AmqpPulseService poller = new AmqpPulseService(service);

			poller.addListener("viaa-test", new FxpConsumer());
			poller.start();
		} catch (Exception exception) {
			System.out.println("Could not connect to the MQ server: " + exception.getMessage());
		}
	}

}
