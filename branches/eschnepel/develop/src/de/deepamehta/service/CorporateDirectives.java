package de.deepamehta.service;

import de.deepamehta.AmbiguousSemanticException;
import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.DeepaMehtaUtils;
import de.deepamehta.Detail;
import de.deepamehta.Directive;
import de.deepamehta.Directives;
import de.deepamehta.Commands;
import de.deepamehta.TopicInitException;
import de.deepamehta.PresentableAssociation;
import de.deepamehta.PresentableTopic;
import de.deepamehta.PresentableType;
import de.deepamehta.topics.LiveTopic;
import de.deepamehta.assocs.LiveAssociation;
//
import java.awt.Point;
import java.io.*;
import java.util.*;



/**
 * ### Note: other as the name suggests ("Corporate") a <CODE>CorporateDirectives</CODE>
 * object isn't aware of the LCM (the application is not required to pass the LCM
 * with every constructor call).
 * <P>
 * <HR>
 * Last functional change: 15.1.2006 (2.0b6-post3)<BR>
 * Last documentation update: 17.11.2000 (2.0a7-pre3)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class CorporateDirectives extends Directives {



	// ********************
	// *** Constructors ***
	// ********************



	/**
	 * Default constructor, needed because must be public.
	 *
	 * @see		ApplicationService#deleteLiveTopic
	 * @see		ApplicationService#performTopicAction
	 * @see		de.deepamehta.topics.TopicContainerTopic#processQuery
	 * @see		de.deepamehta.topics.ElementContainerTopic#processQuery
	 * @see		de.deepamehta.topics.ElementContainerTopic#autoSearch
	 */
	public CorporateDirectives() {
	}

	/**
	 * Covenience constructor.
	 *
	 * @param	directives	array of 2 vectors:<BR>
	 *			Vector 1: Vector of {@link de.deepamehta.PresentableTopic}s<BR>
	 *			Vector 2: Vector of {@link de.deepamehta.PresentableAssociation}s
	 *
	 * @see		de.deepamehta.topics.LiveTopic#navigateByTopictype
	 * @see		de.deepamehta.topics.LiveTopic#navigateByAssoctype
	 */
	public CorporateDirectives(Vector[] directives) {
		add(DIRECTIVE_SHOW_TOPICS, directives[0]);
		add(DIRECTIVE_SHOW_ASSOCIATIONS, directives[1]);
	}



	// ***************
	// *** Methods ***
	// ***************



	public void add(int type) {
		directives.addElement(new Directive(type));
		if ((type == DIRECTIVE_CHOOSE_FILE) || 
			(type == DIRECTIVE_CHOOSE_COLOR)) {
			setChained(type);
		}
	}

	/**
	 * Adds a directive with one parameters to this <CODE>Directives</CODE> object.
	 *
	 * @param	type	The directive type
	 * @param	param	The parameter depending on the directive type
	 */
	public void add(int type, Object param) throws DeepaMehtaException {
		if (type == DIRECTIVE_SHOW_TOPIC || type == DIRECTIVE_SHOW_ASSOCIATION) {
			// Note: param2 and param3 are optional
			// if param2 (the evoke flag) is not set the server uses FALSE
			// if param3 (the topicmap ID) is "" the current topicmap is used
			add(type, param, Boolean.FALSE, "");
		} else if (type == DIRECTIVE_SHOW_TOPICS || type == DIRECTIVE_SHOW_ASSOCIATIONS) {
			// Note: param2 is optional.
			// if param2 (the evoke flag) is not set the server uses FALSE
			add(type, param, Boolean.FALSE);
		} else {
			directives.addElement(new Directive(type, param));
		}
	}

	public void add(int type, Object param1, Object param2) {
		if (type == DIRECTIVE_SHOW_TOPIC || type == DIRECTIVE_SHOW_ASSOCIATION) {
			// Note: param3 is optional
			// if param3 (the topicmap ID) is "" the current topicmap is used
			add(type, param1, param2, "");
		} else {
			directives.addElement(new Directive(type, param1, param2));
		}
	}

	public void add(int type, Object param1, Object param2, Object param3) throws DeepaMehtaException {
		directives.addElement(new Directive(type, param1, param2, param3));
	}

	public void add(int type, Object param1, Object param2, Object param3, Object param4) {
		directives.addElement(new Directive(type, param1, param2, param3, param4));
	}

	public void add(int type, Object param1, Object param2, Object param3, Object param4, Object param5) {
		directives.addElement(new Directive(type, param1, param2, param3, param4, param5));
	}

	// ---

	public void add(Directives directives) {
		// error check
		if (directives == null) {
			throw new DeepaMehtaException("null passed for \"directives\"");
		}
		//
		Enumeration e = directives.directives.elements();
		Directive directive;
		while (e.hasMoreElements()) {
			directive = (Directive) e.nextElement();
			this.directives.addElement(directive);
		}
		// Note: if the specified directives are chained this directives adopts their
		// chained status
		if (directives.isChained()) {
			setChained(directives.chainedResultType());
		}
	}

	// ---

	/**
	 * Updates live corporate memory and updates view according to this directives.
	 *
	 * @param	session		current user (user who requests this directives)<BR>
	 *						only needed for DIRECTIVE_SHOW_VIEW
	 * @param	topicMapID	current topicmap (view the user currently manipulates)
	 * @param	viewMode	current viewmode (viewmode the user is currently working in)
	 *
	 * @see		#updateCorporateMemory		(called recursively)
	 * @see		InteractionConnection#performLogin
	 * @see		InteractionConnection#performImportTopicmap
	 * @see		InteractionConnection#performAddTopicType
	 * @see		InteractionConnection#performAddAssociationType
	 * @see		InteractionConnection#performShowAssociations
	 * @see		InteractionConnection#performHideAssociations
	 * @see		InteractionConnection#performPerformTopicAction
	 * @see		InteractionConnection#performHideTopic
	 * @see		InteractionConnection#performDeleteTopic
	 * @see		InteractionConnection#performAddTopic
	 * @see		InteractionConnection#performChangeTopicName
	 * @see		InteractionConnection#performShowTopicsByType
	 * @see		InteractionConnection#performHideTopicsByType
	 * @see		InteractionConnection#performHideAssociation
	 * @see		InteractionConnection#performDeleteAssociation
	 */
	public void updateCorporateMemory(ApplicationService as, Session session, String topicMapID, String viewMode) {
		Enumeration e = directives.elements();
		Directive directive = null;
		int dirType = 0;
		Object param1, param2, param3, param4;//, param5;
		// directive parameters
		String topicmapID;
		boolean evoke;
		PresentableTopic viewMetadata;
		//
		Vector syncList = new Vector();		// list of topic IDs to be synchronized
		//
		// loop through all directives
		while (e.hasMoreElements()) {
			try {
				directive = (Directive) e.nextElement();
				dirType = directive.type;
				param1 = directive.param1;
				param2 = directive.param2;
				param3 = directive.param3;
				param4 = directive.param4;
				// param5 is currently not needed
				// param5 = directive.param5;
				
				// switch by directive type
				switch (dirType) {
				case DIRECTIVE_SHOW_TOPIC:
					evoke = ((Boolean) param2).booleanValue();
					topicmapID = (String) param3;
					// Note: if the server did not set the topicmap ID or viewmode ID, the client uses the
					// topicmap resp. viewmode in which editing took place as the default.
					// This must regarded also by the server because topicmap ID and viewmode are required here.
					//
					// see Directives
					// see PresentationDirectives
					if (topicmapID.equals("")) {
						topicmapID = topicMapID;
					}
					showTopic(as, session, (PresentableTopic) param1, evoke, topicmapID, 1);
					break;
				case DIRECTIVE_SHOW_TOPICS:
					// all manipulation is performed in personal workspace and therefore
					// the version number is 1 (constant)
					evoke = ((Boolean) param2).booleanValue();
					showTopics(as, session, (Vector) param1, evoke, topicMapID, 1);
					break;
				case DIRECTIVE_SHOW_ASSOCIATION:
					evoke = ((Boolean) param2).booleanValue();
					topicmapID = (String) param3;
					// Note: if the server did not set the topicmap ID or viewmode ID, the client uses the
					// topicmap resp. viewmode in which editing took place as the default.
					// This must regarded also by the server because topicmap ID and viewmode are required here.
					//
					// see Directives
					// see PresentationDirectives
					if (topicmapID.equals("")) {
						topicmapID = topicMapID;
					}
					showAssociation(as, session, (PresentableAssociation) param1, evoke, topicmapID, 1);
					break;
				case DIRECTIVE_SHOW_ASSOCIATIONS:
					evoke = ((Boolean) param2).booleanValue();
					showAssociations(as, session, (Vector) param1, evoke, topicMapID, 1);
					break;
				case DIRECTIVE_HIDE_TOPIC:
					String topicID = (String) param1;
					boolean die = ((Boolean) param2).booleanValue();
					if (die) {
						// ### the version is set to 1
						add(deleteTopic(topicID, 1, as));
						// Note: param3 (topicmapID) is not respected for "delete"
					} else {
						as.deleteViewTopic((String) param3, topicID);
					}
					break;
				case DIRECTIVE_HIDE_ASSOCIATION:
					String assocID = (String) param1;
					die = ((Boolean) param2).booleanValue();
					if (die) {
						// ### the version is set to 1
						add(as.deleteAssociation(assocID, 1, session));
						// Note: param3 (topicmapID) is not respected for "delete"
					} else {
						as.deleteViewAssociation((String) param3, assocID);
					}
					break;
				case DIRECTIVE_HIDE_TOPICS:
					Vector topicIDs = (Vector) param1;
					die = ((Boolean) param2).booleanValue();
					if (die) {
						add(deleteLiveTopics(topicIDs, as));
						// Note: param3 (topicmapID) is not respected for "delete"
					} else {
						as.deleteViewTopics((String) param3, topicIDs);
					}
					break;
				case DIRECTIVE_HIDE_ASSOCIATIONS:
					Vector assocIDs = (Vector) param1;
					die = ((Boolean) param2).booleanValue();
					if (die) {
						add(deleteLiveAssociations(assocIDs, as, session));
						// Note: param3 (topicmapID) is not respected for "delete"
					} else {
						as.deleteViewAssociations((String) param3, assocIDs);
					}
					break;
				case DIRECTIVE_SELECT_TOPIC:
					topicID = (String) param1;
					// Note: param2 - param5 are set programatically
					directive.param2 = as.getTopicProperties(topicID, 1);		// ### versions
					directive.param3 = as.disabledProperties(topicID, 1, session);
					directive.param4 = new Boolean(as.retypeTopicIsAllowed(topicID, 1, session));
					directive.param5 = as.getTopicPropertyBaseURLs(topicID, 1);
					break;
				case DIRECTIVE_SELECT_ASSOCIATION:
					assocID = (String) param1;
					// Note: param2 - param5 are set programatically
					directive.param2 = as.getAssocProperties(assocID, 1);		// ### versions
					// ### directive.param3 = as.disabledProperties(assocID, 1);
					directive.param4 = new Boolean(as.retypeAssociationIsAllowed(assocID, 1, session));
					directive.param5 = as.getAssocPropertyBaseURLs(assocID, 1);
					break;
				case DIRECTIVE_SELECT_TOPICMAP:
					// Note: param1 - param4 are set programatically
					directive.param1 = as.getTopicProperties(topicMapID, 1);	// ### versions
					directive.param2 = as.disabledProperties(topicMapID, 1, session);
					// ### directive.param3 = new Boolean(as.retypeIsAllowed(topicMapID, 1, session));
					directive.param4 = as.getTopicPropertyBaseURLs(topicMapID, 1);
					break;
				case DIRECTIVE_UPDATE_TOPIC_TYPE:
				case DIRECTIVE_UPDATE_ASSOC_TYPE:
					PresentableType type = (PresentableType) param1;
					as.initTypeTopic(type, true, session);
					addToSyncList(syncList, type.getID());
					break;
				case DIRECTIVE_SHOW_TOPIC_PROPERTIES:
					topicID = (String) param1;
					setTopicProperties(topicID, ((Integer) param3).intValue(), (Hashtable) param2, as, session, topicMapID, viewMode);
					// Note: param4 is set programatically
					directive.param4 = as.getTopicPropertyBaseURLs(topicID, 1);
                    // ### was null, null but "topicMapID", "viewMode" is required
                    // ### DIRECTIVE_SHOW_TOPIC_PROPERTIES does not require, but it can cause DIRECTIVE_SET_TOPIC_ICON
                    // ### which requires. These 2 params could be dropped from DIRECTIVE_SET_TOPIC_ICON definition
					break;
				case DIRECTIVE_SHOW_ASSOC_PROPERTIES:
					assocID = (String) param1;
					setAssociationProperties(assocID, ((Integer) param3).intValue(), (Hashtable) param2, as, session, topicMapID, viewMode);
					// Note: param4 is set programatically
					directive.param4 = as.getAssocPropertyBaseURLs(assocID, 1);
                    // ### was null, null but "topicMapID", "viewMode" is required
                    // ### DIRECTIVE_SHOW_TOPIC_PROPERTIES does not require, but it can cause DIRECTIVE_SET_TOPIC_ICON
                    // ### which requires. These 2 params could be dropped from DIRECTIVE_SET_TOPIC_ICON definition
					break;
				case DIRECTIVE_SET_TOPIC_TYPE:
					changeTopicType((String) param1, ((Integer) param3).intValue(), (String) param2, as,
						session, topicMapID, viewMode);
					break;
				case DIRECTIVE_SET_TOPIC_NAME:
					topicID = (String) param1;
					changeTopicName(topicID, ((Integer) param3).intValue(), (String) param2, as);
					addToSyncList(syncList, topicID);
					break;
				case DIRECTIVE_SET_TOPIC_LABEL:
					setTopicProperties((String) param1, ((Integer) param3).intValue(),
						(Hashtable) param4, as, session, topicMapID, viewMode);
					break;
				case DIRECTIVE_SET_TOPIC_ICON:
					topicID = (String) param1;
					addToSyncList(syncList, topicID);
					break;
				case DIRECTIVE_SET_TOPIC_GEOMETRY:
					Point p = (Point) param2;
					add(as.moveTopic((String) param3, 1, (String) param1, p.x, p.y, false, session));	// triggerMovedHook=false
					break;
				case DIRECTIVE_SET_ASSOC_TYPE:
					changeAssociationType((String) param1, ((Integer) param3).intValue(), (String) param2, as);
					break;
				case DIRECTIVE_SET_ASSOC_NAME:
					changeAssociationName((String) param1, ((Integer) param3).intValue(), (String) param2, as);
					break;
				case DIRECTIVE_SHOW_WORKSPACE:
					// Note: param3 (editor context) is not involved here
					viewMetadata = (PresentableTopic) param1;
					((CorporateTopicMap) param2).createLiveTopicmap(session, this);
					break;
				case DIRECTIVE_SHOW_VIEW:
					viewMetadata = (PresentableTopic) param1;
					((CorporateTopicMap) param2).createLiveTopicmap(session, this);
					// --- personlize view ---
					String personalWorkspaceID = (String) param3;
					if (personalWorkspaceID != null || session.isDemo()) {
						((CorporateTopicMap) param2).personalize(viewMetadata.getID());
					}
					break;
				case DIRECTIVE_CLOSE_EDITOR:
					as.removeViewInUse((String) param1, session);
					break;
				case DIRECTIVE_QUEUE_DIRECTIVES:
					((CorporateDirectives) param1).updateCorporateMemory(as, session, topicMapID, viewMode);	// (called recursively)
					// ### Note: the corporate memory is not updated for the queued directives
					// ### only directives which requires no corporate memory updating can be
					// ### queued, these are actually the bulk of directives listed below
					// ### -- Not true anymore, 13.8.2002 (2.0a15)
					break;
				case DIRECTIVE_FOCUS_TYPE:
				case DIRECTIVE_FOCUS_NAME:
				case DIRECTIVE_FOCUS_PROPERTY:
				case DIRECTIVE_SET_TOPIC_LOCK:
				case DIRECTIVE_SHOW_MENU:
				case DIRECTIVE_SHOW_DETAIL:
				case DIRECTIVE_SELECT_EDITOR:
				case DIRECTIVE_RENAME_EDITOR:
				case DIRECTIVE_SET_EDITOR_BGIMAGE:
				case DIRECTIVE_SET_EDITOR_BGCOLOR:
				case DIRECTIVE_SET_EDITOR_ICON:
				case DIRECTIVE_SHOW_MESSAGE:
				case DIRECTIVE_PLAY_SOUND:
				case DIRECTIVE_CHOOSE_FILE:
				case DIRECTIVE_CHOOSE_COLOR:
				case DIRECTIVE_COPY_FILE:
				case DIRECTIVE_DOWNLOAD_FILE:
				case DIRECTIVE_UPLOAD_FILE:
				case DIRECTIVE_SET_LAST_MODIFIED:
				case DIRECTIVE_OPEN_FILE:
				case DIRECTIVE_QUEUE_MESSAGE:
				case DIRECTIVE_LAUNCH_APPLICATION:
				case DIRECTIVE_OPEN_URL:
					// do nothing
					break;
				default:
					System.out.println("*** CorporateDirectives.updateCorporateMemory(): unexpected directive type: " + dirType);
					add(DIRECTIVE_SHOW_MESSAGE, "Server error while updating the corporate memory according to a directive " +
						"of type " + dirType + " (unexpected directive type)", new Integer(NOTIFICATION_ERROR));
				} // end of directivce type switch
			} catch (DeepaMehtaException e2) {
				System.out.println("*** CorporateDirectives.updateCorporateMemory(): " + e2 + " (directive type: " + dirType + ")");
				add(DIRECTIVE_SHOW_MESSAGE, "Server error while updating the corporate memory according to a directive " +
					"of type " + dirType + " (" + e2.getMessage() + ")", new Integer(NOTIFICATION_ERROR));
				// ### e2.printStackTrace();
			} catch (TopicInitException e2) {
				// Note: may happen e.g. when a map containing a failed-to-open datasource is re-opened
				System.out.println("*** CorporateDirectives.updateCorporateMemory(): " + e2 + " (directive type: " + dirType + ")");
				add(DIRECTIVE_SHOW_MESSAGE, "Server error while updating the corporate memory according to a directive " +
					"of type " + dirType + " (" + e2.getMessage() + ")", new Integer(NOTIFICATION_WARNING));
			}
		} // end while
		//
		synchronizeTopics(syncList, as);
	}



	// ---------------------
	// --- Serialization ---
	// ---------------------



	public void write(DataOutputStream out) throws IOException, DeepaMehtaException {
		int dirCount = directives.size();
		Enumeration dirs = directives.elements();
		System.out.println("> sending " + dirCount + " directives ...");
		// write number of directives
		out.writeInt(dirCount);
		// write directives
		Directive directive;
		int type = 0;
		Object param1, param2, param3, param4, param5;
		// loop through all directives
		try {
			while (dirs.hasMoreElements()) {
				directive = (Directive) dirs.nextElement();
				type = directive.type;
				param1 = directive.param1;
				param2 = directive.param2;
				param3 = directive.param3;
				param4 = directive.param4;
				param5 = directive.param5;
				// write directive type
				out.writeInt(type);
				// write parameter
				switch (type) {
				case DIRECTIVE_SHOW_TOPIC:
					((PresentableTopic) param1).write(out);			// presentable topic
					// Note: param2 (evoke flag) is not sent to client
					out.writeUTF((String) param3);					// topicmap ID
					break;
				case DIRECTIVE_SHOW_TOPICS:
					DeepaMehtaUtils.writeTopics((Vector) param1, out);
					// Note: param2 (evoke flag) is not sent to client
					break;
				case DIRECTIVE_SHOW_ASSOCIATION:
					((PresentableAssociation) param1).write(out);			// presentable association
					// Note: param2 (evoke flag) is not sent to client
					out.writeUTF((String) param3);					// topicmap ID
					break;
				case DIRECTIVE_SHOW_ASSOCIATIONS:
					DeepaMehtaUtils.writeAssociations((Vector) param1, out);
					// Note: param2 (evoke flag) is not sent to client
					break;
				case DIRECTIVE_HIDE_TOPIC:
				case DIRECTIVE_HIDE_ASSOCIATION:
					out.writeUTF((String) param1);					// topic/association ID
					out.writeUTF((String) param3);					// topicmap ID
					break;
				case DIRECTIVE_HIDE_TOPICS:
				case DIRECTIVE_HIDE_ASSOCIATIONS:
					DeepaMehtaUtils.writeStrings((Vector) param1, out);	// topic/assoc IDs
					out.writeUTF((String) param3);					// topicmap ID
					break;
				case DIRECTIVE_SELECT_TOPIC:
					out.writeUTF((String) param1);								// topic ID
					DeepaMehtaUtils.writeHashtable((Hashtable) param2, out);	// topic properties
					DeepaMehtaUtils.writeStrings((Vector) param3, out);			// disabled properties
					out.writeBoolean(((Boolean) param4).booleanValue());		// retype allowed?
					DeepaMehtaUtils.writeHashtable((Hashtable) param5, out);	// base URLs
					break;
				case DIRECTIVE_SELECT_ASSOCIATION:
					out.writeUTF((String) param1);								// association ID
					DeepaMehtaUtils.writeHashtable((Hashtable) param2, out);	// association properties
					// ### DeepaMehtaUtils.writeStrings((Vector) param3, out);	// disabled properties
					out.writeBoolean(((Boolean) param4).booleanValue());		// retype allowed?
					DeepaMehtaUtils.writeHashtable((Hashtable) param5, out);	// base URLs
					break;
				case DIRECTIVE_SELECT_TOPICMAP:
					DeepaMehtaUtils.writeHashtable((Hashtable) param1, out);	// topic properties
					DeepaMehtaUtils.writeStrings((Vector) param2, out);			// disabled properties
					// ### out.writeBoolean(((Boolean) param3).booleanValue());	// retype allowed?
					DeepaMehtaUtils.writeHashtable((Hashtable) param4, out);	// base URLs
					break;
				case DIRECTIVE_UPDATE_TOPIC_TYPE:
				case DIRECTIVE_UPDATE_ASSOC_TYPE:
					((PresentableType) param1).write(out);		// type
					break;
				case DIRECTIVE_SHOW_TOPIC_PROPERTIES:
				case DIRECTIVE_SHOW_ASSOC_PROPERTIES:
					out.writeUTF((String) param1);								// topic ID
					DeepaMehtaUtils.writeHashtable((Hashtable) param2, out);	// properties
					// Note: param3 (topic version) is not sent to client
					DeepaMehtaUtils.writeHashtable((Hashtable) param4, out);	// base URLs
					break;
				case DIRECTIVE_FOCUS_TYPE:
				case DIRECTIVE_FOCUS_NAME:
					// no parameters
					break;
				case DIRECTIVE_FOCUS_PROPERTY:
					// ### out.writeUTF((String) param1);					// property name
					break;
				case DIRECTIVE_SET_TOPIC_TYPE:
				case DIRECTIVE_SET_TOPIC_NAME:
				case DIRECTIVE_SET_TOPIC_LABEL:
				case DIRECTIVE_SET_TOPIC_ICON:
				case DIRECTIVE_SET_ASSOC_TYPE:
				case DIRECTIVE_SET_ASSOC_NAME:
					out.writeUTF((String) param1);					// topic ID / assoc ID
					out.writeUTF((String) param2);					// type ID / label / iconfile
					// Note: param3 (topic / association version) is not sent to client
					// Note: param4 (properties) is not sent to client (SET_TOPIC_LABEL)
					break;
				case DIRECTIVE_SET_TOPIC_GEOMETRY:
					Point p = (Point) param2;
					out.writeUTF((String) param1);					// topic ID / assoc ID
					out.writeInt(p.x);								// geometry x
					out.writeInt(p.y);								// geometry y
					out.writeUTF((String) param3);					// topicmap ID
					break;
				case DIRECTIVE_SET_TOPIC_LOCK:
					out.writeUTF((String) param1);					// topic ID
					out.writeBoolean(((Boolean) param2).booleanValue());		// is locked?
					out.writeUTF((String) param3);					// topicmap ID
					break;
				case DIRECTIVE_SHOW_MENU:
					p = (Point) param3;
					out.writeUTF((String) param1);					// menu ID
					((CorporateCommands) param2).write(out);		// commands
					out.writeInt(p.x);								// x
					out.writeInt(p.y);								// y
					break;
				case DIRECTIVE_SHOW_DETAIL:
					out.writeUTF((String) param1);					// topic ID
					((Detail) param2).write(out);
					break;
				case DIRECTIVE_SHOW_WORKSPACE:
					((PresentableTopic) param1).write(out);			// topicmap metadata
					// Note: param3 (editor context) is send before param2 (topicmap)
					out.write(((Integer) param3).intValue());		// editor context
					((CorporateTopicMap) param2).write(out);		// topicmap
					break;
				case DIRECTIVE_SHOW_VIEW:
					((PresentableTopic) param1).write(out);			// topicmap metadata
					((CorporateTopicMap) param2).write(out);		// topicmap
					// Note: param3 and param4 are not sent to client
					break;
				case DIRECTIVE_SELECT_EDITOR:
				case DIRECTIVE_CLOSE_EDITOR:
					out.writeUTF((String) param1);					// topicmap ID
					break;
				case DIRECTIVE_RENAME_EDITOR:
					out.writeUTF((String) param1);					// topicmap ID
					out.writeUTF((String) param2);					// name
					break;
				case DIRECTIVE_SET_EDITOR_BGIMAGE:
				case DIRECTIVE_SET_EDITOR_BGCOLOR:
					out.writeUTF((String) param1);					// topicmap ID
					out.writeUTF((String) param2);					// image / color
					break;
				case DIRECTIVE_SET_EDITOR_ICON:
					out.writeUTF((String) param1);					// topicmap ID
					out.writeUTF((String) param2);					// iconfile
					break;
				case DIRECTIVE_SHOW_MESSAGE:
					out.writeUTF((String) param1);					// message
					out.write(((Integer) param2).intValue());		// notification type
					break;
				case DIRECTIVE_PLAY_SOUND:
					out.writeUTF((String) param1);					// soundfile
					break;
				case DIRECTIVE_CHOOSE_FILE:
					// no parameters
					break;
				case DIRECTIVE_CHOOSE_COLOR:
					out.writeUTF((String) param1);					// current color
					break;
				case DIRECTIVE_COPY_FILE:
					out.writeUTF((String) param1);					// path
					out.write(((Integer) param2).intValue());		// filetype
					break;
				case DIRECTIVE_DOWNLOAD_FILE:
				case DIRECTIVE_UPLOAD_FILE:
				case DIRECTIVE_SET_LAST_MODIFIED:
					out.writeUTF((String) param1);					// filename
					out.writeLong(((Long) param2).longValue());		// last modified
					out.write(((Integer) param3).intValue());		// filetype
					break;
				case DIRECTIVE_OPEN_FILE:
					out.writeUTF((String) param1);					// command
					out.writeUTF((String) param2);					// filename
					break;
				case DIRECTIVE_QUEUE_MESSAGE:
					out.writeUTF((String) param1);					// server message
					break;
				case DIRECTIVE_QUEUE_DIRECTIVES:
					((CorporateDirectives) param1).write(out);		// client directives
					break;
				case DIRECTIVE_LAUNCH_APPLICATION:
					out.writeUTF((String) param1);					// command
					break;
				case DIRECTIVE_OPEN_URL:
					out.writeUTF((String) param1);					// URL
					break;
				default:
					throw new DeepaMehtaException("unexpected directive type: " + type);
				}
			} // end loop through all directives
		} catch (NullPointerException e) {
			throw new DeepaMehtaException("error while writing directive of type " +
				type + " (" + e + " -- probably caused by wrong usage " +
				"through application programmer)");
		} catch (ClassCastException e) {
			throw new DeepaMehtaException("error while writing directive of type " +
				type + " (" + e + " -- probably caused by wrong usage " +
				"through application programmer)");
		}
	}



	// ***********************
	// *** Private Methods ***
	// ***********************



	/**
	 * Called for DIRECTIVE_SHOW_TOPICS
	 *
	 * @see		#updateCorporateMemory
	 */
	private void showTopics(ApplicationService as, Session session, Vector topics, boolean evoke,
												String topicmapID, int topicmapVersion) {
		Enumeration e = topics.elements();
		while (e.hasMoreElements()) {
			PresentableTopic topic = (PresentableTopic) e.nextElement();
			showTopic(as, session, topic, evoke, topicmapID, topicmapVersion);
		}
	}

	/**
	 * Called for DIRECTIVE_SHOW_ASSOCIATIONS
	 *
	 * @see		#updateCorporateMemory
	 */
	private void showAssociations(ApplicationService as, Session session, Vector assocs, boolean evoke,
												String topicmapID, int topicmapVersion) {
		Enumeration e = assocs.elements();
		while (e.hasMoreElements()) {
			PresentableAssociation assoc = (PresentableAssociation) e.nextElement();
			showAssociation(as, session, assoc, evoke, topicmapID, topicmapVersion);
		}
	}

	/**
	 * Called for {@link #DIRECTIVE_SHOW_TOPIC} and {@link #DIRECTIVE_SHOW_TOPICS} (indirectly).
	 *
	 * @throws	TopicInitException	### if initialization level 2 or 3 fails. Note: if
	 *								initialization level 1 fails a warning directive
	 *								is added to this directives set and creation of the
	 *								live topic is continued.
	 *
	 * @see		#updateCorporateMemory	package
	 * @see		#showTopics		private
	 */
	private int showTopic(ApplicationService as, Session session, PresentableTopic topic, boolean evoke,
								String topicmapID, int topicmapVersion) throws TopicInitException {
		String topicID = topic.getID();
		int topicVersion = topic.getVersion();
		// --- update view ---
		addTopicToView(topic, topicmapID, VIEWMODE_USE, as);
		// --- update corporate memory ---
		if (evoke) {
			Hashtable props = topic.getProperties();
			if (props == null) {
				props = new Hashtable();
			}
			// set owner
			if (!topic.isVirtual() && !session.isDemo()) {		// Note: a demo user has no user ID (null)
				props.put(PROPERTY_OWNER_ID, session.getUserID());
			}
			//
			as.setTopicProperties(topicID, topicVersion, props);
		}
		//
		as.createLiveTopic(topicID, topic.getType(), topic.getName(), false, evoke,
			topicmapID, VIEWMODE_USE, session, this);		// override=false
		//
		as.initTopicLock(topic);
		// --- set topic appearance ---
		as.initTopicAppearance(topic);
		//
		return directives != null ? 1 : 0;
	}

	/**
	 * Called for {@link #DIRECTIVE_SHOW_ASSOCIATION} and {@link #DIRECTIVE_SHOW_ASSOCIATIONS} (indirectly).
	 *
	 * @see		#updateCorporateMemory		package
	 * @see		#showAssociations		private
	 */
	private void showAssociation(ApplicationService as, Session session, PresentableAssociation assoc,
									boolean evoke, String topicmapID, int topicmapVersion) {
		//
		String assocID = assoc.getID();
		int assocVersion = assoc.getVersion();
		// --- update view ---
		// Note: an existence check is performed to prevent an association from being added more than once to a view
		as.createViewAssociation(topicmapID, topicmapVersion, VIEWMODE_USE, assocID, assocVersion, true);	// performExistenceCheck=true
		// --- update corporate memory ---
		if (evoke) {
			// set owner
			if (!session.isDemo()) {		// Note: a demo user has no user ID (null)
				as.cm.setAssociationData(assocID, assocVersion, PROPERTY_OWNER_ID, session.getUserID());
			}
		}
		//
		as.createLiveAssociation(assoc, false, evoke, this);
	}

	// ---

	private void addTopicToView(PresentableTopic topic, String topicmapID, String viewmode,
																	ApplicationService as) {
		Point p;
		switch (topic.getGeometryMode()) {
		case GEOM_MODE_ABSOLUTE:
			p = topic.getGeometry();
			break;
		case GEOM_MODE_NEAR:
			// Note: also for topics with GEOM_MODE_NEAR (means: no actual coordinates
			// known yet) a view topic entry is made -- needed while geometry update
			// when view is closed ### still true?
			p = new Point(0, 0);
			break;
		default:
			p = new Point(0, 0);
		}
		// Note: "performExistenceCheck" is set "true" to prevent a topic from
		// being added to a view more than once ### versions
		as.createViewTopic(topicmapID, 1, viewmode, topic.getID(), 1, p.x, p.y, true);
	}

	// ---

	/**
	 * Called for {@link #DIRECTIVE_SET_TOPIC_NAME}
	 *
	 * @see		#updateCorporateMemory
	 */
	private void changeTopicName(String topicID, int version, String name, ApplicationService as) {
		LiveTopic topic = as.getLiveTopic(topicID, version);
		// --- change name in live corporate memory ---
		topic.setName(name);
		// --- change name in corporate memory ---
		as.cm.changeTopicName(topicID, version, name);
	}

	/**
	 * Called for {@link #DIRECTIVE_SET_ASSOC_NAME}
	 *
	 * @see		#updateCorporateMemory
	 */
	private void changeAssociationName(String assocID, int version, String name, ApplicationService as) {
		LiveAssociation assoc = as.getLiveAssociation(assocID, version);
		// --- change name in live corporate memory ---
		assoc.setName(name);
		// --- change name in corporate memory ---
		as.cm.changeAssociationName(assocID, version, name);
	}

	// ---

	/**
	 * Called for {@link #DIRECTIVE_SET_TOPIC_TYPE}
	 *
	 * @see		#updateCorporateMemory
	 */
	private void changeTopicType(String topicID, int version, String typeID, ApplicationService as, Session session,
														String topicmapID, String viewmode) throws TopicInitException {
		LiveTopic topic = as.getLiveTopic(topicID, version);
		// recreate the topic
		topic.setType(typeID);
		as.cm.deleteTopic(topicID);
		as.createLiveTopic(topic, topicmapID, viewmode, session, this);		// Note: default is override=true, evoke=true
	}

	/**
	 * Called for {@link #DIRECTIVE_SET_ASSOC_TYPE}
	 *
	 * @see		#updateCorporateMemory
	 */
	private void changeAssociationType(String assocID, int version, String typeID, ApplicationService as) {
		LiveAssociation assoc = as.getLiveAssociation(assocID, version);
		// recreate the association
		assoc.setType(typeID);
		as.cm.deleteAssociation(assocID);
		as.createLiveAssociation(assoc, true, true, this);			// override=true, evoke=true
	}

	// ---

	/**
	 * Called for {@link #DIRECTIVE_SHOW_TOPIC_PROPERTIES} and {@link #DIRECTIVE_SET_TOPIC_LABEL}
	 *
	 * @see		#updateCorporateMemory
	 */
	private void setTopicProperties(String topicID, int version, Hashtable props, ApplicationService as,
                                        			Session session, String topicmapID, String viewmode) {
		if (props != null) {
			// ### triggerPropertiesChangedHook?
			// ### true required while changing e.g. "Icon" property
			// ### false required while moving e.g. an Instituton topic with YADE coordinares ### true works also (but creates one unnecessary DIRECTIVE_GEOMETRY)
			add(as.setTopicProperties(topicID, version, props, topicmapID, true, session));	// ### triggerPropertiesChangedHook=false
		}
	}

	/**
	 * Called for {@link #DIRECTIVE_SHOW_ASSOC_PROPERTIES}
	 *
	 * @see		#updateCorporateMemory
	 */
	private void setAssociationProperties(String assocID, int version, Hashtable props,
														ApplicationService as, Session session,
														String topicmapID, String viewmode) {
		if (props != null) {
			add(as.setAssocProperties(assocID, version, props, topicmapID, viewmode, session));
		}
	}

	// ---

	/**
	 * Called for {@link #DIRECTIVE_HIDE_TOPICS}
	 *
	 * @see		#updateCorporateMemory
	 */
	private CorporateDirectives deleteLiveTopics(Vector topicIDs, ApplicationService as) {
		CorporateDirectives directives = new CorporateDirectives();
		Enumeration e = topicIDs.elements();
		while (e.hasMoreElements()) {
			// ### the version is set to 1
			directives.add(deleteTopic((String) e.nextElement(), 1, as));
		}
		return directives;
	}

	/**
	 * Called for {@link #DIRECTIVE_HIDE_ASSOCIATIONS}
	 *
	 * @see		#updateCorporateMemory
	 */
	private CorporateDirectives deleteLiveAssociations(Vector assocIDs, ApplicationService as, Session session) {
		CorporateDirectives directives = new CorporateDirectives();
		Enumeration e = assocIDs.elements();
		while (e.hasMoreElements()) {
			// ### the version is set to 1
			directives.add(as.deleteAssociation((String) e.nextElement(), 1, session));
		}
		return directives;
	}

	// ---

	/**
	 * Returns the directives to delete the specified topic. Note: the directives to delete the
	 * associations the specified topic is involved are not build by this method.
	 * <P>
	 * Called for {@link #DIRECTIVE_HIDE_TOPIC} and {@link #DIRECTIVE_HIDE_TOPICS} (indirectly)
	 * if the <CODE>die</CODE> parameter set to <CODE>Boolean.TRUE</CODE>.
	 * <P>
	 * Triggers the die() hook of the specified topic and returns the resulting directives.
	 *
	 * @see		CorporateDirectives#updateCorporateMemory
	 * @see		CorporateDirectives#deleteLiveTopics
	 */
	private CorporateDirectives deleteTopic(String topicID, int version, ApplicationService as) {
		CorporateDirectives directives = new CorporateDirectives();
		try {
			// --- trigger die() hook ---
			directives.add(as.getLiveTopic(topicID, version).die());
		} catch (DeepaMehtaException e) {
			// ### add to directives
			System.out.println("*** ApplicationService.deleteTopic(): " + e);
		}
		return directives;
	}

	// ---

	private void addToSyncList(Vector syncList, String topicID) {
		if (!syncList.contains(topicID)) {
			syncList.addElement(topicID);
		}
	}

	private void synchronizeTopics(Vector syncList, ApplicationService as) {
		Enumeration e = syncList.elements();
		while (e.hasMoreElements()) {
			String topicID = (String) e.nextElement();
			as.getHostObject().broadcastChangeNotification(topicID);
		}
	}
}
