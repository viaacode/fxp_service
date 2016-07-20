package be.viaa.fxp;

/**
 * The status of the FXP transfer sequence
 * 
 * @author Hannes Lowette
 */
public enum FxpStatus {
	
	/**
	 * indicates the FXP status is currently running
	 */
	RUNNING,
	
	/**
	 * Indicates the FXP file transfer has completed successfully
	 */
	OK,
	
	/**
	 * When an error has occurred in the FXP process
	 */
	ERROR;
}
