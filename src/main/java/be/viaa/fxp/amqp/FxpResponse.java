package be.viaa.fxp.amqp;

/**
 * Response when the FXP transfer has been carried out
 * 
 * @author Hannes Lowette
 *
 */
public class FxpResponse {

	/**
	 * The correlation Id of the processed file
	 */
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
	private boolean sourceFileRemoved;
	
	/**
	 * The comment on why the status was NOK (Not OK). If the status returned
	 * OK, this should be null.
	 */
	private String comment;

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

}
