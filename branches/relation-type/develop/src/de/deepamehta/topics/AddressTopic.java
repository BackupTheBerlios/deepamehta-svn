package de.deepamehta.topics;

import de.deepamehta.Topic;
import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.DeepaMehtaUtils;
import de.deepamehta.TopicInitException;
import de.deepamehta.service.Session;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.ApplicationService;
//
import java.util.*;



public class AddressTopic extends LiveTopic {



	// *******************
	// *** Constructor ***
	// *******************



	public AddressTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}



	// **********************
	// *** Defining Hooks ***
	// **********************



	// ---------------------------
	// --- Handling Properties ---
	// ---------------------------



	public String getNameProperty() {
		return PROPERTY_STREET;
	}

	public static Vector hiddenProperties(TypeTopic type) {
		Vector props = new Vector();
		props.addElement(PROPERTY_NAME);
		return props;
	}
}
