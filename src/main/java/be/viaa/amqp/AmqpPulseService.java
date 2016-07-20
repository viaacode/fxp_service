package be.viaa.amqp;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Periodically checks to see if there are new messages available on the queue
 * 
 * @author Hannes Lowette
 *
 */
public class AmqpPulseService {

	private static final long DEFAULT_INTERVAL = 5000;

	/**
	 * The AMQP service
	 */
	private final AmqpService service;
	
	/**
	 * The collection of consumers
	 */
	private final Map<String, List<AmqpConsumer>> consumers = new HashMap<>();
	
	/**
	 * Indicates the service has been canceled
	 */
	private boolean canceled;
	
	/**
	 * 
	 */
	private long interval;

	/**
	 * @param service
	 */
	public AmqpPulseService(AmqpService service) {
		this (service, DEFAULT_INTERVAL);
	}

	/**
	 * @param service
	 * @param consumer
	 * @param canceled
	 * @param interval
	 */
	public AmqpPulseService(AmqpService service, long interval) {
		this.service = service;
		this.interval = interval;
	}

	/**
	 * Starts the periodic polling service
	 */
	public void start() {
		if (canceled)
			throw new IllegalStateException("attempting to start canceled service");
		
		while (!canceled) {
			try {
				for (Iterator<Entry<String, List<AmqpConsumer>>> iterator = consumers.entrySet().iterator(); iterator.hasNext(); ) {
					Entry<String, List<AmqpConsumer>> entry = iterator.next();
					
					for (AmqpConsumer consumer : entry.getValue()) {
						service.read(entry.getKey(), consumer);
					}
				}
				
				Thread.sleep(interval);
			} catch (InterruptedException exception) {
				shutdown();
			} catch (Exception ex) {
				ex.printStackTrace();
				shutdown();
			}
		}
	}

	/**
	 * Cancels this service
	 */
	public void shutdown() {
		this.canceled = true;
	}
	
	/**
	 * Sets the interval between 2 AMQP get requests
	 */
	public void setInterval(long interval) {
		this.interval = interval;
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
	}

}
