/*
 * Created on 30.12.2005
 *
 * This file is part of the DeepaMehta framework.
 */
package de.deepamehta.environment;

/**
 * This exception is used to signal various erroneous conditions which might 
 * occur inside the environment or associated components. Note that this is a 
 * checked exception class - exceptions of this type have to be handled by the 
 * developer somewhere.
 * @author vwegert
 */
public class EnvironmentException extends Exception {

	private static final long serialVersionUID = -8087744832564973166L;
	
	/**
	 * Creates an EnvironmentException object without parameters.
	 */
	public EnvironmentException() {
		super();
	}
	
	/**
	 * Creates an EnvironmentException object with a message.
	 * @param msg message string
	 */
	public EnvironmentException(String msg) {
		super(msg);
	}
	
	/**
	 * Creates an EnvironmentException object with a message, encapsulating another exception.
	 * @param msg message string
	 * @param t original exception
	 */
	public EnvironmentException(String msg, Throwable t) {
		super(msg, t);
	}
	
	/**
	 * Creates an EnvironmentException object encapsulating another exception.
	 * @param t original exception
	 */
	public EnvironmentException(Throwable t) {
		super(t);
	}
}
