package de.deepamehta.artfacts.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.artfacts.Artfacts;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.topics.ElementContainerTopic;



/**
 * Part of the "Artfacts" example.
 * <P>
 * <HR>
 * Last functional change: 31.1.2003 (2.0a18-pre1)<BR>
 * Last documentation update: 31.1.2003 (2.0a18-pre1)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class ExhibitionSearchTopic extends ElementContainerTopic implements Artfacts {



	// *******************
	// *** Constructor ***
	// *******************



	public ExhibitionSearchTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}



	// *********************************************************
	// *** Implementation of abstract ContainerTopic methods ***
	// *********************************************************



	protected String getContentType() {
		return "Exhibition";
	}

	protected String getContentTypeID() {
		return TOPICTYPE_EXHIBITION;
	}



	// ***************************************************************
	// *** Implementation of abstract ElementContainerTopic method ***
	// ***************************************************************



	public String getNameAttribute() {
		return "Title";
	}
}
