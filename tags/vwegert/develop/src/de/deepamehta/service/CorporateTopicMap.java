package de.deepamehta.service;

import de.deepamehta.PresentableTopicMap;
import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.TopicInitException;
//
import java.io.*;



/**
 * A short-living representation of a topicmap that is ready for presentation at client side.
 * <P
 * A <CODE>CorporateTopicMap</CODE> is always (### optimization possible)
 * composed of 2 {@link de.deepamehta.PresentableTopicMap}s -- the "Use" map and
 * the "Build" map -- regardless weather the "Build" mode is displayed at client side.
 * A <CODE>CorporateTopicMap</CODE> is created at server side and send to the client
 * who builds 2 {@link de.deepamehta.client.PresentationTopicMap}s upon it.<BR>
 * Note: Once a View is send to the client this server-side representation is forgotten.
 * <P>
 * While constructing a <CODE>CorporateTopicMap</CODE> the specified view is retrieved
 * from corporate memory.
 * <P>
 * <HR>
 * Last functional change: 29.10.2004 (2.0b3)<BR>
 * Last documentation update: 27.10.2001 (2.0a13-pre2)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class CorporateTopicMap implements DeepaMehtaConstants {



	// **************
	// *** Fields ***
	// **************



	private ApplicationService as;
	private String topicmapID;
	//
	private PresentableTopicMap useMap;



	// *******************
	// *** Constructor ***
	// *******************



	/**
	 * @see		InteractionConnection#addPersonalWorkspace
	 * @see		InteractionConnection#addGroupWorkspaces
	 * @see		InteractionConnection#addCorporateSpace
	 * @see		de.deepamehta.topics.TopicMapTopic#evoke
	 * @see		de.deepamehta.topics.TopicMapTopic#openPersonalView
	 * @see		de.deepamehta.topics.TopicMapTopic#openGroupView
	 * @see		de.deepamehta.topics.TopicMapTopic#publish
	 * @see		de.deepamehta.topics.TopicMapTopic#exportToFile
	 */
	public CorporateTopicMap(ApplicationService as, String topicmapID, int version) {
		this.as = as;
		this.topicmapID = topicmapID;
		// --- backgroung image ---
		String bgImage = as.getTopicProperty(topicmapID, version, PROPERTY_BACKGROUND_IMAGE);
		// --- background color ---
		String bgColorUse = as.getTopicProperty(topicmapID, version, PROPERTY_BACKGROUND_COLOR);
		if (bgColorUse.equals("")) {
			bgColorUse = DEFAULT_VIEW_BGCOLOR;
		}
		//
		// --- translation ---
		String translationUse = as.getTopicProperty(topicmapID, version, PROPERTY_TRANSLATION_USE);
		if (translationUse.equals("")) {
			translationUse = "0:0";
		}
		// --- retrieve topicmaps ---
		useMap  = as.createUserView(topicmapID, version, VIEWMODE_USE, bgImage, bgColorUse, translationUse);
	}



	// ***************
	// *** Methods ***
	// ***************



	/**
	 * @see		de.deepamehta.topics.helper.TopicMapExporter#makeTopicmapXML
	 */
	public PresentableTopicMap getTopicMap() {
		return useMap;
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
		as.createLiveTopics(useMap, directives, session);
	}

	/**
	 * @see		#createLiveTopicmap
	 */
	private void createLiveAssociations(Session session, CorporateDirectives directives) {
		as.createLiveAssociations(useMap, session, directives);
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
		as.setAppearance(useMap);
	}

	/**
	 * @see		#createLiveTopicmap
	 */
	private void setTopicLabels() {
		as.setTopicLabels(useMap);
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
		useMap.write(out);
	}



	// ---------------------
	// --- Miscellaneous ---
	// ---------------------



	/**
	 * Duplicates this view in corporate memory (<CODE>ViewTopic</CODE>,
	 * <CODE>ViewAssociation</CODE> and <CODE>ViewGeometry</CODE> entries).
	 * <P>
	 * References checked: 2.4.2003 (2.0a18-pre8)
	 *
	 * @param	destTopicmapID	the destination view ID
	 *
	 * @see		CorporateDirectives#updateCorporateMemory
	 * @see		de.deepamehta.topics.UserTopic#createConfigurationMap
	 */
	public void personalize(String destTopicmapID) {
		as.personalizeView(useMap, topicmapID, VIEWMODE_USE, destTopicmapID);
	}

	/**
	 * @see		de.deepamehta.topics.TopicMapTopic#publish
	 */
	public void addPublishDirectives(CorporateDirectives directives) {
		as.addPublishDirectives(useMap, directives);
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
		as.initTopics(useMap.getTopics().elements(), initLevel, directives, session);
	}
}
