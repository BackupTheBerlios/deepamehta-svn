package de.deepamehta.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.PropertyDefinition;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.Session;
//
import java.util.*;



/**
 * Last functional change: 7.3.2004 (2.0b3-pre1)<BR>
 * Last documentation update: 7.3.2004 (2.0b3-pre1)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class PersonTopic extends LiveTopic {



	// *******************
	// *** Constructor ***
	// *******************



	public PersonTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}



	// **********************
	// *** Defining Hooks ***
	// **********************



	// ---------------------------
	// --- Handling Properties ---
	// ---------------------------



	public String getNameProperty() {
		return null;
	}

	/**
	 * @param	props		contains only changed properties
	 *
	 * @see		de.deepamehta.service.ApplicationService#setTopicData
	 */
	public String getTopicName(Hashtable props, Hashtable oldProps) {
		// ### Note: a non-null value is only returned if topic name actually changes
		// ### probably the code tend to be too complicated and we should always return the name
		String forename = (String) props.get(PROPERTY_FIRST_NAME);
		String surname = (String) props.get(PROPERTY_NAME);
		String oldfore = (String) oldProps.get(PROPERTY_FIRST_NAME);
		String oldsur = (String) oldProps.get(PROPERTY_NAME);
		boolean f = forename != null;
		boolean s = surname != null;
		boolean of = oldfore != null;
		boolean os = oldsur != null;
		if (f && s) {
			return forename + " " + surname;
		} else if (f) {
			return forename + (os ? " " + oldsur : "");
		} else if (s) {
			return (of ? oldfore + " " : "") + surname;
		} else {
			return null;
		}
	}

	public static void propertyLabel(PropertyDefinition propertyDef, ApplicationService as, Session session) {
		String propName = propertyDef.getPropertyName();
		if (propName.equals(PROPERTY_NAME)) {
			propertyDef.setPropertyLabel("Last Name");
		}
	}
}
