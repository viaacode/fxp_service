package be.viaa.util;

import java.util.concurrent.Callable;

/**
 * Allows for easy exception handling inside a Callable when working with ExecutorServices
 * 
 * @author Hannes Lowette
 *
 * @param <T>
 */
public abstract class VerboseCallable<T> implements Callable<T> {

	@Override
	public T call() throws Exception {
		try {
			return run();
		} catch (Exception ex) {
			exception(ex);
			throw new InterruptedException();
		}
	}

	/**
	 * 
	 * @throws Exception
	 */
	protected abstract T run() throws Exception;

	/**
	 * Called when an exception has occurred in the run method
	 * 
	 * @param ex
	 */
	protected abstract void exception(Exception ex);

}
