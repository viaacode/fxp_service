package be.viaa.amqp;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Periodically checks to see if there are new messages available on the queue
 * 
 * @author Hannes Lowette
 *
 */
public class AmqpBatchService {

	/**
	 * The logger for this class
	 */
	private static final Logger logger = LogManager.getLogger(AmqpBatchService.class);

	/**
	 * The AMQP service
	 */
	private final AmqpService service;

	/**
	 * The collection of consumers
	 */
	private final Map<String, List<AmqpConsumer>> consumers = new HashMap<>();

	/**
	 * @param service
	 * @param consumer
	 * @param canceled
	 * @param interval
	 */
	public AmqpBatchService(AmqpService service) {
		this.service = service;
	}

	/**
	 * Starts the periodic polling service
	 */
	public void start() {
		logger.info("Listening to message from service: " + service.toString());
		try {
			for (Iterator<Entry<String, List<AmqpConsumer>>> iterator = consumers.entrySet().iterator(); iterator.hasNext();) {
				Entry<String, List<AmqpConsumer>> entry = iterator.next();

				for (AmqpConsumer consumer : entry.getValue()) {
					service.read(entry.getKey(), consumer);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public Map<String, List<AmqpConsumer>> getConsumers() {
		return consumers;
	}

	/**
	 * Adds a listener to the collection
	 * 
	 * @param queue
	 * @param consumer
	 */
	public void addListener(String queue, AmqpConsumer consumer) {
		if (!consumers.containsKey(queue)) {
			consumers.put(queue, new LinkedList<AmqpConsumer>());
		}
		consumers.get(queue).add(consumer);
		logger.info("Added listener to queue '" + queue + "'");
	}

}
