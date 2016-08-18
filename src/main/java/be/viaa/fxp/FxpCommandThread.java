package be.viaa.fxp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import be.viaa.util.VerboseCallable;

/**
 * Thread to write an FXP command asynchronously
 * 
 * @author Hannes Lowette
 *
 */
public class FxpCommandThread extends VerboseCallable<Integer> {
	
	/**
	 * The logger for this class, mainly used to log the exceptions that occur during the execution of the command
	 */
	private static final Logger logger = LogManager.getLogger(FxpCommandThread.class);

	/**
	 * The client that this service needs to send the command to
	 */
	private final FTPClient client;
	
	/**
	 * The command that needs to be sent to 
	 */
	private final String command;

	/**
	 * @param client
	 * @param command
	 */
	public FxpCommandThread(FTPClient client, String command) {
		this.client = client;
		this.command = command;
	}

	@Override
	public Integer run() throws Exception {
		return client.sendCommand(command);
	}

	@Override
	protected void exception(Exception ex) {
		logger.catching(ex);
	}

}
