package be.viaa.fxp.amqp;

import com.google.gson.annotations.SerializedName;

/**
 * An FXP request
 *
 * @author Hannes Lowette
 *
 */
public class FxpRequest {

	/**
	 * The path to the file on the source host (the directory tree)
	 */
	@SerializedName("source_path")
	private String sourcePath;

	/**
	 * The filename on the source remote
	 */
	@SerializedName("source_file")
	private String sourceFile;

	/**
	 * The source host address
	 */
	@SerializedName("source_host")
	private String sourceHost;

	/**
	 * The username used to authenticate on the source host
	 */
	@SerializedName("source_user")
	private String sourceUser;

	/**
	 * The password to authenticate on the source host
	 */
	@SerializedName("source_password")
	private String sourcePassword;

	/**
	 * The path to the file on the destination host (the directory tree)
	 */
	@SerializedName("destination_path")
	private String destinationPath;

	/**
	 * The filename on the destination host
	 */
	@SerializedName("destination_file")
	private String destinationFile;

	/**
	 * The destination host address
	 */
	@SerializedName("destination_host")
	private String destinationHost;

	/**
	 * The username of the destination host
	 */
	@SerializedName("destination_user")
	private String destinationUser;

	/**
	 * The password of the destination FTP host
	 */
	@SerializedName("destination_password")
	private String destinationPassword;

	/**
	 * The PID
	 */
	@SerializedName("correlation_id")
	private String correlationId;

	/**
	 * The destination queue
	 */
	@SerializedName("dest_queue")
	private String destQueue;

	/**
	 * Indicates the source file needs to be removed or not
	 */
	private boolean move;

	/**
	 * @return the sourcePath
	 */
	public String getSourcePath() {
		return sourcePath;
	}

	/**
	 * @param sourcePath
	 *            the sourcePath to set
	 */
	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	/**
	 * @return the sourceFile
	 */
	public String getSourceFile() {
		return sourceFile;
	}

	/**
	 * @param sourceFile
	 *            the sourceFile to set
	 */
	public void setSourceFile(String sourceFile) {
		this.sourceFile = sourceFile;
	}

	/**
	 * @return the sourceHost
	 */
	public String getSourceHost() {
		return sourceHost;
	}

	/**
	 * @param sourceHost
	 *            the sourceHost to set
	 */
	public void setSourceHost(String sourceHost) {
		this.sourceHost = sourceHost;
	}

	/**
	 * @return the sourceUser
	 */
	public String getSourceUser() {
		return sourceUser;
	}

	/**
	 * @param sourceUser
	 *            the sourceUser to set
	 */
	public void setSourceUser(String sourceUser) {
		this.sourceUser = sourceUser;
	}

	/**
	 * @return the sourcePassword
	 */
	public String getSourcePassword() {
		return sourcePassword;
	}

	/**
	 * @param sourcePassword
	 *            the sourcePassword to set
	 */
	public void setSourcePassword(String sourcePassword) {
		this.sourcePassword = sourcePassword;
	}

	/**
	 * @return the destinationPath
	 */
	public String getDestinationPath() {
		return destinationPath;
	}

	/**
	 * @param destinationPath
	 *            the destinationPath to set
	 */
	public void setDestinationPath(String destinationPath) {
		this.destinationPath = destinationPath;
	}

	/**
	 * @return the destinationFile
	 */
	public String getDestinationFile() {
		return destinationFile;
	}

	/**
	 * @param destinationFile
	 *            the destinationFile to set
	 */
	public void setDestinationFile(String destinationFile) {
		this.destinationFile = destinationFile;
	}

	/**
	 * @return the destinationHost
	 */
	public String getDestinationHost() {
		return destinationHost;
	}

	/**
	 * @param destinationHost
	 *            the destinationHost to set
	 */
	public void setDestinationHost(String destinationHost) {
		this.destinationHost = destinationHost;
	}

	/**
	 * @return the destinationUser
	 */
	public String getDestinationUser() {
		return destinationUser;
	}

	/**
	 * @param destinationUser
	 *            the destinationUser to set
	 */
	public void setDestinationUser(String destinationUser) {
		this.destinationUser = destinationUser;
	}

	/**
	 * @return the destinationPassword
	 */
	public String getDestinationPassword() {
		return destinationPassword;
	}

	/**
	 * @param destinationPassword
	 *            the destinationPassword to set
	 */
	public void setDestinationPassword(String destinationPassword) {
		this.destinationPassword = destinationPassword;
	}

	/**
	 * @return the destQueue
	 */
	public String getDestQueue() {
		return destQueue;
	}

	/**
	 * @param destQueue
	 *            the destQueue to set
	 */
	public void setDestQueue(String destQueue) {
		this.destQueue = destQueue;
	}

	/**
	 * @return the move
	 */
	public boolean move() {
		return move;
	}

	/**
	 * @param move the move to set
	 */
	public void setMove(boolean move) {
		this.move = move;
	}

	/**
	 * @return the correlationId
	 */
	public String getCorrelationId() {
		return correlationId;
	}

	/**
	 * @param correlationId the correlationId to set
	 */
	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

}