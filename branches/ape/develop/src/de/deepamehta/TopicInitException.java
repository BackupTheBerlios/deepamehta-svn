package de.deepamehta;



public class TopicInitException extends RuntimeException {

    /**
     * Constructs an <CODE>TopicInitException</CODE> with the specified 
     * detail message. 
     *
     * @param   s   the detail message
     */
    public TopicInitException(String s) {
		super(s);
    }

	public TopicInitException(String string, Exception e) {
	    super(string, e);
    }
}
