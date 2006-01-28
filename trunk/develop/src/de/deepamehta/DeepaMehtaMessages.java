/*
 * Created on 18.12.2005
 *
 * This file is part of the DeepaMehta framework.
 */
package de.deepamehta;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * This is the main adapter class to access localized message texts from within the
 * DeepaMehta core.  
 * @author vwegert
 */
public class DeepaMehtaMessages {
    
	private static final String BUNDLE_NAME = "de.deepamehta.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	/**
	 * This class is not to be instantiated, so we make the constructor private.
	 */
	private DeepaMehtaMessages() {
	}

	/**
	 * Retrieve a single message text from the resource file.
	 * @param key A unique key identifying the message text.
	 * @return The text specified by the resource file.
	 */
	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}