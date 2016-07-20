package be.viaa.fxp.amqp;

/**
 * Exception for anything FXP related
 * 
 * @author Hannes Lowette
 *
 */
@SuppressWarnings("serial")
public class FxpException extends Exception {

	/**
	 * 
	 */
	public FxpException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public FxpException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public FxpException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public FxpException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public FxpException(Throwable cause) {
		super(cause);
	}

}
