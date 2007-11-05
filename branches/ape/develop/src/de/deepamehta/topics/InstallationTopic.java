package de.deepamehta.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.PropertyDefinition;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.Session;

import java.util.Hashtable;
import java.util.StringTokenizer;



/**
 * <P>
 * <HR>
 * Last functional change: 11.9.2007 (2.0b8)<BR>
 * Last documentation update: 7.12.2000 (2.0a8-pre2)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class InstallationTopic extends LiveTopic {



	static final String  CMD_ASSIGN_CORPORATE_ICON = "assignCorporateIcon";
	static final String  CMD_ASSIGN_CUSTOMER_ICON = "assignCustomerIcon";



	// *******************
	// *** Constructor ***
	// *******************



	/**
	 * @see		de.deepamehta.service.ApplicationService#createLiveTopic
	 */
	public InstallationTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}



	// --------------------------
	// --- Providing Commands ---
	// --------------------------



	/**
	 * @see		TypeTopic#makeTypeDefinition
	 */
	public static void buttonCommand(PropertyDefinition propDef, ApplicationService as, Session session) {
		String propName = propDef.getPropertyName();
		if (propName.equals(PROPERTY_CORPORATE_ICON)) {
			propDef.setActionButton(as.string(BUTTON_ASSIGN_FILE), CMD_ASSIGN_CORPORATE_ICON);
		} else if (propName.equals(PROPERTY_CUSTOMER_ICON)) {
			propDef.setActionButton(as.string(BUTTON_ASSIGN_FILE), CMD_ASSIGN_CUSTOMER_ICON);
		}
	}



	// --------------------------
	// --- Executing Commands ---
	// --------------------------



	public CorporateDirectives executeCommand(String command, Session session,
													String topicmapID, String viewmode) {
		StringTokenizer st = new StringTokenizer(command, COMMAND_SEPARATOR);
		String cmd = st.nextToken();
		//
		if (cmd.equals(CMD_ASSIGN_CORPORATE_ICON) || cmd.equals(CMD_ASSIGN_CUSTOMER_ICON)) {
			CorporateDirectives directives = new CorporateDirectives();
			directives.add(DIRECTIVE_CHOOSE_FILE);
			return directives;
		} else {
			return super.executeCommand(command, session, topicmapID, viewmode);
		}
	}

	public CorporateDirectives executeChainedCommand(String command, String result,
											String topicmapID, String viewmode, Session session) {
		// Note: the result of a DIRECTIVE_CHOOSE_FILE contains the absolute
		// path of the (client side) selected icon file
		CorporateDirectives directives = new CorporateDirectives();
		//
		if (command.equals(CMD_ASSIGN_CORPORATE_ICON)) {
			copyAndUpload(result, FILE_IMAGE, PROPERTY_CORPORATE_ICON, session, directives);
			return directives;
		} else if (command.equals(CMD_ASSIGN_CUSTOMER_ICON)) {
			copyAndUpload(result, FILE_IMAGE, PROPERTY_CUSTOMER_ICON, session, directives);
			return directives;
		} else {
			return super.executeChainedCommand(command, result, topicmapID, viewmode, session);
		}
	}



	// ---------------------------
	// --- Handling Properties ---
	// ---------------------------



	public CorporateDirectives propertiesChanged(Hashtable newData, Hashtable oldData,
											String topicmapID, String viewmode, Session session) {
		CorporateDirectives directives = super.propertiesChanged(newData, oldData,
			topicmapID, viewmode, session);
		String value;
		// --- "Language" ---
		value = (String) newData.get(PROPERTY_LANGUAGE);
		if (value != null) {
			addHint(PROPERTY_LANGUAGE, value, directives);
		}
		// --- "Corporate Icon" ---
		value = (String) newData.get(PROPERTY_CORPORATE_ICON);
		if (value != null) {
			addHint(PROPERTY_CORPORATE_ICON, value, directives);
		}
		// --- "Customer Icon" ---
		value = (String) newData.get(PROPERTY_CUSTOMER_ICON);
		if (value != null) {
			addHint(PROPERTY_CUSTOMER_ICON, value, directives);
		}
		//
		return directives;
	}

	/**
	 * @see		de.deepamehta.topics.TypeTopic#makeTypeDefinition
	 */
	public static void propertyLabel(PropertyDefinition propDef, ApplicationService as, Session session) {
		if (as.getLanguage() == LANGUAGE_GERMAN) {
			String propName = propDef.getPropertyName();
			if (propName.equals(PROPERTY_LANGUAGE)) {
				propDef.setPropertyLabel("Sprache");
			} else if (propName.equals(PROPERTY_CORPORATE_ICON)) {
				propDef.setPropertyLabel("Firmen-Icon");
			} else if (propName.equals(PROPERTY_CUSTOMER_ICON)) {
				propDef.setPropertyLabel("Kunden-Icon");
			}
		}
	}



	// ***********************
	// *** Private Methods ***
	// ***********************



	// ### copy in UserPreferencesTopic
	private void addHint(String propName, String value, CorporateDirectives directives) {
		directives.add(DIRECTIVE_SHOW_MESSAGE, "\"" + propName + "\" will be " +
			"\"" + value + "\" in next session", new Integer(NOTIFICATION_DEFAULT));
	}
}
