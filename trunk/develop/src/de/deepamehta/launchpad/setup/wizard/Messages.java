package de.deepamehta.launchpad.setup.wizard;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Message adapter class for the GUI setup wizard.
 * @author vwegert
 */
public class Messages {
	
	private static final String BUNDLE_NAME = "de.deepamehta.launchpad.setup.wizard.messages"; //$NON-NLS-1$
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private Messages() {
		// private constructor to prevent instantiation
	}

	/**
	 * @param key The message key to retrieve
	 * @return The localized message text.
	 */
	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
