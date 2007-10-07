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
public class MovieContainerTopic extends ElementContainerTopic {



	String[] groupingProperties = {"Year", "Country", "Genre"};



	// *******************
	// *** Constructor ***
	// *******************



	public MovieContainerTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}



	// *******************************************************
	// *** Implementing abstract methods of ContainerTopic ***
	// *******************************************************



	protected String getContentType() {
		return "Movie";
	}

	protected String getContentTypeID() {
		return "tt-movie";
	}



	// **************************************************************
	// *** Implementing abstract methods of ElementContainerTopic ***
	// **************************************************************



	public String getNameAttribute() {
		return "Title";
	}



	// ***************
	// *** Methods ***
	// ***************



	// overrides ContainerTopic
	protected String[] getGroupingProperties() {
		return groupingProperties;
	}
}
