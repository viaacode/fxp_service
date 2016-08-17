package be.viaa.fxp;

/**
 * Contains the information required to connect to a remote FTP service
 * 
 * @author Hannes Lowette
 *
 */
public class Host {

	/**
	 * The default port for the FTP service
	 */
	private static final int DEFAULT_FTP_PORT = 21;

	/**
	 * Address to the remote
	 */
	private final String host;
	
	/**
	 * The port the remote host is listening on
	 */
	private final int port;

	/**
	 * The username used to authenticate
	 */
	private final String username;

	/**
	 * The password used to authenticate
	 */
	private final String password;

	/**
	 * @param host
	 * @param username
	 * @param password
	 */
	public Host(String host, String username, String password) {
		this (host, DEFAULT_FTP_PORT, username, password);
	}

	/**
	 * @param host
	 * @param port
	 * @param username
	 * @param password
	 */
	public Host(String host, int port, String username, String password) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Host [host=" + host + ", port=" + port + ", username=" + username + ", password=" + password + "]";
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	
}
