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
public class ArtworkSearchTopic extends ElementContainerTopic implements Artfacts {



	// *******************
	// *** Constructor ***
	// *******************



	public ArtworkSearchTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}



	// *******************************************************
	// *** Implementing abstract methods of ContainerTopic ***
	// *******************************************************



	protected String getContentType() {
		return "Artwork";
	}

	protected String getContentTypeID() {
		return TOPICTYPE_ARTWORK;
	}



	// **************************************************************
	// *** Implementing abstract methods of ElementContainerTopic ***
	// **************************************************************



	public String getNameAttribute() {
		return "Title";
	}
}
