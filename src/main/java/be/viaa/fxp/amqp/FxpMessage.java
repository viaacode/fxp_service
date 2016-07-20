package be.viaa.fxp.amqp;

/**
 * An FXP request
 * 
 * @author Hannes Lowette
 *
 */
public class FxpMessage {

	/**
	 * The path to the file on the source host (the directory tree)
	 */
	private String sourcePath;
	
	/**
	 * The filename on the source remote
	 */
	private String sourceFile;
	
	/**
	 * The source host address
	 */
	private String sourceHost;

	/**
	 * The username used to authenticate on the source host
	 */
	private String sourceUser;
	
	/**
	 * The password to authenticate on the source host
	 */
	private String sourcePassword;

	/**
	 * The path to the file on the destination host (the directory tree)
	 */
	private String destinationPath;
	
	/**
	 * The filename on the destination host
	 */
	private String destinationFile;
	
	/**
	 * The destination host address
	 */
	private String destinationHost;
	
	/**
	 * The username of the destination host
	 */
	private String destinationUser;
	
	/**
	 * The password of the destination FTP host
	 */
	private String destinationPassword;
	
	/**
	 * The PID
	 */
	private String pid;
	
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
	 * @return the pid
	 */
	public String getPid() {
		return pid;
	}

	/**
	 * @param pid
	 *            the pid to set
	 */
	public void setPid(String pid) {
		this.pid = pid;
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

}
