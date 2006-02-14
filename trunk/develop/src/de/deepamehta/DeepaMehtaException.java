package de.deepamehta;



public class DeepaMehtaException extends RuntimeException {

	/**
     * Constructs a <CODE>DeepaMehtaException</CODE> with the specified 
     * detail message. 
     *
     * @param   s   the detail message
     */
    public DeepaMehtaException(String s) {
		super(s);
    }

    /**
     * Constructs a <CODE>DeepaMehtaException</CODE> with the specified 
     * detail message and cause. 
     * @param  message the detail message (which is saved for later retrieval
     * @param  cause the cause (which is saved for later retrieval
     */
    public DeepaMehtaException(String message, Throwable cause) {
		super(message, cause);
	}

}
