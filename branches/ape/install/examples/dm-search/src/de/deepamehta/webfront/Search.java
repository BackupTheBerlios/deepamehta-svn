package de.deepamehta.webfront;

import de.deepamehta.DeepaMehtaConstants;



/**
 * Interface for the "DeepaMehta Web Frontend" application.
 */
public interface Search extends DeepaMehtaConstants {



	// *****************
	// *** Constants ***
	// *****************



	// ---------------
	// --- Actions ---
	// ---------------



	public static final String ACTION_SEARCH = "search";
	public static final String ACTION_SHOW_TOPIC_INFO = "showTopicInfo";



	// -------------
	// --- Pages ---
	// -------------



    static final String PAGE_SEARCH_FORM = "SearchForm";
    static final String PAGE_SEARCH_RESULT = "SearchResult";
    static final String PAGE_TOPIC_INFO = "TopicInfo";
}
