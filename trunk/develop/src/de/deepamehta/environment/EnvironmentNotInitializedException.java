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
	
}
