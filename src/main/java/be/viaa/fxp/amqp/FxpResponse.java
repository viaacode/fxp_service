package be.viaa.fxp.amqp;

import com.google.gson.annotations.SerializedName;

/**
 * Response when the FXP transfer has been carried out
 * 
 * @author Hannes Lowette
 *
 */
public class FxpResponse {

	/**
	 * The format in which the timestamp is shown
	 */
	public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss,SSS";

	/**
	 * The correlation Id of the processed file
	 */
	@SerializedName("correlation_id")
	private String correlationId;
	
	/**
	 * The PID of the processed file
	 */
	private String pid;
	
	/**
	 * The status from the FxpResponse
	 */
	private String outcome;
	
	/**
	 * Indicates the source has been removed or not
	 */
	@SerializedName("source_file_removed")
	private boolean sourceFileRemoved;
	
	/**
	 * The comment on why the status was NOK (Not OK). If the status returned
	 * OK, this should be null.
	 */
	private String comment;
	
	/**
	 * The filename of the source file
	 */
	@SerializedName("source_name")
	private String filename;
	
	/**
	 * The directory of the source file
	 */
	@SerializedName("source_path")
	private String directory;
	
	/**
	 * The source FTP host
	 */
	@SerializedName("source_host")
	private String sourceHost;
	
	/**
	 * The destination FTP host
	 */
	@SerializedName("destination_host")
	private String destinationHost;
	
	/**
	 * The timestamp at which the response is created
	 */
	private String timestamp;

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

	/**
	 * @return the pid
	 */
	public String getPid() {
		return pid;
	}

	/**
	 * @param pid the pid to set
	 */
	public void setPid(String pid) {
		this.pid = pid;
	}

	/**
	 * @return the status
	 */
	public String getOutcome() {
		return outcome;
	}

	/**
	 * @param status the status to set
	 */
	public void setOutcome(String outcome) {
		this.outcome = outcome;
	}

	/**
	 * @return the sourceFileRemoved
	 */
	public boolean isSourceFileRemoved() {
		return sourceFileRemoved;
	}

	/**
	 * @param sourceFileRemoved the sourceFileRemoved to set
	 */
	public void setSourceFileRemoved(boolean sourceFileRemoved) {
		this.sourceFileRemoved = sourceFileRemoved;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @param filename the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * @return the directory
	 */
	public String getDirectory() {
		return directory;
	}

	/**
	 * @param directory the directory to set
	 */
	public void setDirectory(String directory) {
		this.directory = directory;
	}

	/**
	 * @return the sourceHost
	 */
	public String getSourceHost() {
		return sourceHost;
	}

	/**
	 * @param sourceHost the sourceHost to set
	 */
	public void setSourceHost(String sourceHost) {
		this.sourceHost = sourceHost;
	}

	/**
	 * @return the destinationHost
	 */
	public String getDestinationHost() {
		return destinationHost;
	}

	/**
	 * @param destinationHost the destinationHost to set
	 */
	public void setDestinationHost(String destinationHost) {
		this.destinationHost = destinationHost;
	}

	/**
	 * @return the timestamp
	 */
	public String getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

}
