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

	public DeepaMehtaException(String string, Exception e) {
		super(string, e);
	}
}
