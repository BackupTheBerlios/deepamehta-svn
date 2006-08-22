/*
 * Created on 24.01.2006
 *
 * This file is part of the DeepaMehta framework.
 */
package de.deepamehta.environment.instance;

/**
 * This exception is thrown if any component tries to retrieve an unknown 
 * instance configuration from the environment.
 * @author vwegert
 */
public class UnknownInstanceException extends Exception {

	private static final long serialVersionUID = 8636917214006572267L;
	private String instanceID;
    
    /**
     * The default constructor to create an exception signaling that the specified
     * instance configuration is unknown.
     * @param id The erroneous ID.
     */
    public UnknownInstanceException(String id) {
        this.instanceID = id;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Throwable#getMessage()
     */
    public String getMessage() {
        return "Unknown instance configuration: " + this.instanceID;
    }
}
