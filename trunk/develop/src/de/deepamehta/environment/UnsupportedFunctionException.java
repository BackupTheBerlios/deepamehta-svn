/**
 * 
 */
package de.deepamehta.environment;

/**
 * MISSDOC No documentation for type UnsupportedFunctionException
 * @author vwegert
 *
 */
public class UnsupportedFunctionException extends RuntimeException {

	private static final long serialVersionUID = 2389771211543028268L;

	/**
	 * MISSDOC No documentation for constructor of UnsupportedFunctionException
	 */
	public UnsupportedFunctionException() {
		super();
	}

	/**
	 * MISSDOC No documentation for constructor of UnsupportedFunctionException
	 * @param message
	 */
	public UnsupportedFunctionException(String message) {
		super(message);
	}

	/**
	 * MISSDOC No documentation for constructor of UnsupportedFunctionException
	 * @param message
	 * @param cause
	 */
	public UnsupportedFunctionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * MISSDOC No documentation for constructor of UnsupportedFunctionException
	 * @param cause
	 */
	public UnsupportedFunctionException(Throwable cause) {
		super(cause);
	}

}
