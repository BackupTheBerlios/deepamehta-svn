package de.deepamehta.environment;

/**
 * This exception is thrown if some component tries to retrieve the current environment
 * before the environment has been initialized. This exception is unchecked because
 * it shouldn't happen at all - the very first thing any executable should do is
 * initialize the environment.  
 * @author vwegert
 *
 */
public class EnvironmentNotInitializedException extends RuntimeException {

	private static final long serialVersionUID = -150585692399216508L;

	/**
	 * @see RuntimeException#RuntimeException()
	 */
	public EnvironmentNotInitializedException() {
		super();
	}

	/**
	 * @see RuntimeException#RuntimeException(java.lang.String, java.lang.Throwable)
	 */
	public EnvironmentNotInitializedException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @see RuntimeException#RuntimeException(java.lang.String)
	 */
	public EnvironmentNotInitializedException(String message) {
		super(message);
	}

	/**
	 * @see RuntimeException#RuntimeException(java.lang.Throwable)
	 */
	public EnvironmentNotInitializedException(Throwable cause) {
		super(cause);
	}
	

}
