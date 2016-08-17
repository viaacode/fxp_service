package be.viaa.fxp;

import org.apache.commons.net.ftp.FTPClient;

import be.viaa.util.VerboseCallable;

/**
 * Thread to write an FXP command asynchronously
 * 
 * @author Hannes Lowette
 *
 */
public class FxpCommandThread extends VerboseCallable<Integer> {

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
		// TODO: Exception handling
		ex.printStackTrace();
	}

}
