package de.deepamehta.artfacts.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.artfacts.Artfacts;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.topics.ElementContainerTopic;



/**
 * Part of the "Artfacts" example.
 * <P>
 * <HR>
 * Last functional change: 30.1.2003 (2.0a18-pre1)<BR>
 * Last documentation update: 25.1.2003 (2.0a17-pre7)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class GallerySearchTopic extends ElementContainerTopic implements Artfacts {



	// *******************
	// *** Constructor ***
	// *******************



	public GallerySearchTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}



	// *********************************************************
	// *** Implementation of abstract ContainerTopic methods ***
	// *********************************************************



	protected String getContentType() {
		return "Institution";
	}

	protected String getContentTypeID() {
		return TOPICTYPE_GALLERY;
	}



	// ***************************************************************
	// *** Implementation of abstract ElementContainerTopic method ***
	// ***************************************************************



	public String getNameAttribute() {
		return "Name";
	}



	// **********************
	// *** Defining Hooks ***
	// **********************



	// ---------------------------
	// --- Handling Properties ---
	// ---------------------------



	// overridden because "Gallery Search" has its own "Name" property (the search field)
	public String getNameProperty() {
		return null;	// ### container name doesn'r rely on any property
	}
}
