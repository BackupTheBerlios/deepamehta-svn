package de.deepamehta.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.PropertyDefinition;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.Session;



/**
 * Last functional change: 7.3.2004 (2.0b3-pre1)<BR>
 * Last documentation update: 7.3.2004 (2.0b3-pre1)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class FaxNumberTopic extends LiveTopic {



	// *******************
	// *** Constructor ***
	// *******************



	public FaxNumberTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}



	// **********************
	// *** Defining Hooks ***
	// **********************



	// ---------------------------
	// --- Handling Properties ---
	// ---------------------------



	public static void propertyLabel(PropertyDefinition propertyDef, ApplicationService as, Session session) {
		String propName = propertyDef.getPropertyName();
		if (propName.equals(PROPERTY_NAME)) {
			propertyDef.setPropertyLabel("Fax Number");
		}
	}
}
