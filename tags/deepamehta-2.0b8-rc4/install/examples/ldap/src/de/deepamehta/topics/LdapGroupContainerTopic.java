package de.deepamehta.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.service.ApplicationService;

/**
 *
 */
public class LdapGroupContainerTopic extends ElementContainerTopic {

	String[] groupingProperties = {};

	public LdapGroupContainerTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}



	// *********************************************************
	// *** Implementation of abstract ContainerTopic methods ***
	// *********************************************************



	protected String getContentType() {
		return "group";
	}

	protected String getContentTypeID() {
		return "tt-ldapgroup";
	}



	// ***************************************************************
	// *** Implementation of abstract ElementContainerTopic method ***
	// ***************************************************************



	public String getNameAttribute() {		// ### was getNameProperty()
		return "cn";
	}



	// ###

	protected String[] getGroupingProperties() {
		return groupingProperties;
	}
	
	/**
	 *  Overwritten fro ElementContainer
	 */
	protected String createTopicID(String elementID) {
		return "t-" + getContentType() + "-" + elementID.hashCode();
	}
}
