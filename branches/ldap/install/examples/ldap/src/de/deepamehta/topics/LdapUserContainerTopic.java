package de.deepamehta.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.service.ApplicationService;

@SuppressWarnings("serial")
public class LdapUserContainerTopic extends ElementContainerTopic {

	String[] groupingProperties = {}; // ###

	// *******************
	// *** Constructor ***
	// *******************

	public LdapUserContainerTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}

	// *********************************************************
	// *** Implementation of abstract ContainerTopic methods ***
	// *********************************************************

	protected String getContentType() {
		return "person";
	}

	protected String getContentTypeID() {
		return "tt-ldapuser";
	}

	// ***************************************************************
	// *** Implementation of abstract ElementContainerTopic method ***
	// ***************************************************************

	public String getNameAttribute() { // ### was getNameProperty()
		return "cn"; // ###
	}

	// ###

	protected String[] getGroupingProperties() {
		return groupingProperties;
	}

	// Overrides ElementContainer
	protected String createTopicID(String elementID) {
		return "t-" + getContentType() + "-" + elementID.hashCode();
	}
}
