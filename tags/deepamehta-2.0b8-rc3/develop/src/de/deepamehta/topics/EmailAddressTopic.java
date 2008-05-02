package de.deepamehta.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.service.ApplicationService;



public class EmailAddressTopic extends LiveTopic {



	// *******************
	// *** Constructor ***
	// *******************



	public EmailAddressTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}



	// **********************
	// *** Defining Hooks ***
	// **********************



	// ---------------------------
	// --- Handling Properties ---
	// ---------------------------



	public String getNameProperty() {
		return PROPERTY_EMAIL_ADDRESS;
	}
}
