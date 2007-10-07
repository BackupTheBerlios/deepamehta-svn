package de.deepamehta.movies.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.topics.ElementContainerTopic;



/**
 * Part of the "Movies" example.
 * <P>
 * <HR>
 * Last functional change: 16.3.2004 (2.0b3-pre1)<BR>
 * Last documentation update: 8.11.2000 (2.0a7-pre2)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class ActorContainerTopic extends ElementContainerTopic {



	// *******************
	// *** Constructor ***
	// *******************



	public ActorContainerTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}



	// **********************
	// *** Defining Hooks ***
	// **********************



	// ---------------------------
	// --- Handling Properties ---
	// ---------------------------



	// overridden because "Actor Container" has its own "Name" property (the search field)
	public String getNameProperty() {
		return null;	// ### container name doesn'r rely on any property
	}



	// *********************************************************
	// *** Implementation of abstract ContainerTopic methods ***
	// *********************************************************



	protected String getContentType() {
		return "Actor";
	}

	protected String getContentTypeID() {
		return "tt-actor";
	}



	// ***************************************************************
	// *** Implementation of abstract ElementContainerTopic method ***
	// ***************************************************************



	public String getNameAttribute() {
		return "Name";
	}
}
