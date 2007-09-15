package de.deepamehta.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.service.ApplicationService;

/**
 * 
 */
@SuppressWarnings("serial")
public class MailmanContainerTopic extends ElementContainerTopic {

	public MailmanContainerTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}

	protected String getContentType() {
		return "mailman";
	}

	protected String getContentTypeID() {
		return "tt-mailman";
	}

	// ***************************************************************
	// *** Implementation of abstract ElementContainerTopic method ***
	// ***************************************************************

	public String getNameAttribute() { // ### was getNameProperty()
		return "mailmanName";
	}
	
}
