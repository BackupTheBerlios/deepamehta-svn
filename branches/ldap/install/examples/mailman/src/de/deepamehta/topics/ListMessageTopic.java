package de.deepamehta.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.service.ApplicationService;

/**
 * 
 */
@SuppressWarnings("serial")
public class ListMessageTopic extends LiveTopic {

	/* types and properties */
	private static final String PROPERTY_LISTMESSAGE_NAME = "Subject";

	public ListMessageTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}

	public String getNameProperty() {
		return PROPERTY_LISTMESSAGE_NAME;
	}

}
