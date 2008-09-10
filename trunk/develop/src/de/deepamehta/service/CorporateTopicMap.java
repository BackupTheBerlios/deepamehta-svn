package de.deepamehta.service;

import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.PresentableTopicMap;
import de.deepamehta.TopicInitException;

import java.io.DataOutputStream;
import java.io.IOException;



/**
 * A short-living representation of a topicmap that is ready for presentation at client side.
 * <p>
 * A <CODE>CorporateTopicMap</CODE> holds a {@link de.deepamehta.PresentableTopicMap}.
 * A <CODE>CorporateTopicMap</CODE> is created at server side and send to the client
 * who builds a {@link de.deepamehta.client.PresentationTopicMap} upon it.<BR>
 * Note: Once a topicmap is send to the client this server-side representation is forgotten.
 * <p>
 * While constructing a <CODE>CorporateTopicMap</CODE> the specified topicmap is retrieved
 * from corporate memory.
 * <p>
 * <hr>
 * Last change: 10.9.2008 (2.0b8)<br>
 * J&ouml;rg Richter<br>
 * jri@deepamehta.de
 */
public class CorporateTopicMap implements DeepaMehtaConstants {



	// **************
	// *** Fields ***
	// **************



	private ApplicationService as;
	private String topicmapID;
	//
	private PresentableTopicMap topicmap;



	// *******************
	// *** Constructor ***
	// *******************



	/**
	 * References checked: 5.4.2007 (2.0b8)
	 *
	 * @see		ApplicationService#addPersonalWorkspace
	 * @see		ApplicationService#addGroupWorkspaces
	 * @see		de.deepamehta.topics.TopicMapTopic#exportTopicmap
	 * @see		de.deepamehta.topics.TopicMapTopic#addPublishDirectives
	 * @see		de.deepamehta.topics.TopicMapTopic#openPersonalTopicmap
	 * @see		de.deepamehta.topics.TopicMapTopic#openSharedTopicmap
	 * @see		de.deepamehta.topics.UserTopic#createConfigurationMap
	 * @see		de.deepamehta.topics.WorkspaceTopic#joinUser
	 */
	public CorporateTopicMap(ApplicationService as, String topicmapID, int version) {
		this.as = as;
		this.topicmapID = topicmapID;
		// --- backgroung image ---
		String bgImage = as.getTopicProperty(topicmapID, version, PROPERTY_BACKGROUND_IMAGE);
		// --- background color ---
		String bgColor = as.getTopicProperty(topicmapID, version, PROPERTY_BACKGROUND_COLOR);
		if (bgColor.equals("")) {
			bgColor = DEFAULT_VIEW_BGCOLOR;
		}
		//
		// --- translation ---
		String translation = as.getTopicProperty(topicmapID, version, PROPERTY_TRANSLATION_USE);
		if (translation.equals("")) {
			translation = "0:0";
		}
		// --- retrieve topicmaps ---
		topicmap  = as.createUserView(topicmapID, version, bgImage, bgColor, translation);
	}



	// ***************
	// *** Methods ***
	// ***************



	/**
	 * @see		de.deepamehta.topics.helper.TopicMapExporter#makeTopicmapXML
	 */
	public PresentableTopicMap getTopicMap() {
		return topicmap;
	}

	// ---

	/**
	 * Called for <CODE>DIRECTIVE_SHOW_WORKSPACE</CODE> and <CODE>DIRECTIVE_SHOW_VIEW</CODE>.
	 *
	 * @see		CorporateDirectives#updateCorporateMemory
	 */
	public void createLiveTopicmap(Session session, CorporateDirectives directives) throws TopicInitException {
		createLiveTopics(directives, session);
		createLiveAssociations(session, directives);
		initLiveTopics(directives, session);
		setAppearance();
		setTopicLabels();
	}

	/**
	 * @see		#createLiveTopicmap
	 */
	private void createLiveTopics(CorporateDirectives directives, Session session) throws TopicInitException {
		as.createLiveTopics(topicmap, directives, session);
	}

	/**
	 * @see		#createLiveTopicmap
	 */
	private void createLiveAssociations(Session session, CorporateDirectives directives) {
		as.createLiveAssociations(topicmap, session, directives);
	}

	/**
	 * Used for <CODE>DIRECTIVE_SHOW_WORKSPACE</CODE> and <CODE>DIRECTIVE_SHOW_VIEW</CODE>
	 *
	 * @see		#createLiveTopicmap
	 */
	private void initLiveTopics(CorporateDirectives directives, Session session) {
		initUserView(INITLEVEL_2, directives, session);
		initUserView(INITLEVEL_3, directives, session);
	}

	/**
	 * @see		#createLiveTopicmap
	 */
	private void setAppearance() {
		as.setAppearance(topicmap);
	}

	/**
	 * @see		#createLiveTopicmap
	 */
	private void setTopicLabels() {
		as.setTopicLabels(topicmap);
	}



	// ---------------------
	// --- Serialization ---
	// ---------------------



	/**
	 * @see		InteractionConnection#createCorporateTopicMap
	 * @see		de.deepamehta.Directives#write
	 */
	public void write(DataOutputStream out) throws IOException {
		// ### compare to client.PresentationTopicMap
		topicmap.write(out);
	}



	// ---------------------
	// --- Miscellaneous ---
	// ---------------------



	/**
	 * Duplicates this topicmap in corporate memory (<CODE>ViewTopic</CODE>,
	 * <CODE>ViewAssociation</CODE> and <CODE>ViewGeometry</CODE> entries).
	 * <P>
	 * References checked: 2.4.2003 (2.0a18-pre8)
	 *
	 * @param	destTopicmapID	the destination topicmap ID
	 *
	 * @see		CorporateDirectives#updateCorporateMemory
	 * @see		de.deepamehta.topics.UserTopic#createConfigurationMap
	 */
	public void personalize(String destTopicmapID) {
		as.personalizeView(topicmap, topicmapID, VIEWMODE_USE, destTopicmapID);
	}

	/**
	 * References checked: 10.9.2008 (2.0b8)
	 *
	 * @see		de.deepamehta.topics.TopicMapTopic#addPublishDirectives
	 */
	public void addPublishDirectives(Session session, CorporateDirectives directives) {
		as.addPublishDirectives(topicmap, session, directives);
	}



	// ***********************
	// *** Private Methods ***
	// ***********************



	/**
	 * @see		#initLiveTopics	2x (initlevel 2 and 3)
	 */
	private void initUserView(int initLevel, CorporateDirectives directives,
																Session session) {
		// init topics
		as.initTopics(topicmap.getTopics().elements(), initLevel, directives, session);
	}
}
