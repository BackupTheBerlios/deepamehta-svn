package de.deepamehta.webfrontend;

import de.deepamehta.DeepaMehtaConstants;



/**
 * Interface for the "DeepaMehta Web Frontend" application.
 */
public interface WebFrontend extends DeepaMehtaConstants {



	// *****************
	// *** Constants ***
	// *****************



	// ---------------
	// --- Actions ---
	// ---------------



    public static final String ACTION_TRY_LOGIN = "tryLogin";
    public static final String ACTION_GO_HOME = "goHome";
	public static final String ACTION_SEARCH = "search";
	public static final String ACTION_SHOW_TOPIC_FORM = "showTopicForm";
	public static final String ACTION_SHOW_TOPIC_INFO = "showTopicInfo";
	public static final String ACTION_SHOW_TOPICS = "showTopics";
	public static final String ACTION_CREATE_TOPIC = "createTopic";
	public static final String ACTION_UPDATE_TOPIC = "updateTopic";
	public static final String ACTION_DELETE_TOPIC = "deleteTopic";



	// -------------
	// --- Pages ---
	// -------------



    static final String PAGE_LOGIN = "Login";
    static final String PAGE_HOME = "Home";
    static final String PAGE_TOPIC_FORM = "TopicForm";
    static final String PAGE_TOPIC_INFO = "TopicInfo";
    static final String PAGE_TOPIC_LIST = "TopicList";



	// -------------
	// --- Modes ---
	// -------------



    static final String MODE_BY_NAME = "byName";
    static final String MODE_BY_TYPE = "byType";
}
