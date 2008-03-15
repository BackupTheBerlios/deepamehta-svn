package de.deepamehta.client;

import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.Directives;
import de.deepamehta.PresentableAssociation;

import java.io.DataInputStream;
import java.io.IOException;



/**
 * <P>
 * <HR>
 * Last functional change: 11.11.2002 (2.0a17-pre1)<BR>
 * Last documentation update: 8.11.2000 (2.0a7-pre2)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class PresentationAssociation extends PresentableAssociation implements GraphEdge, DeepaMehtaConstants {



	// **************
	// *** Fields ***
	// **************



	private PresentationTopic topic1;
	private PresentationTopic topic2;



	// ********************
	// *** Constructors ***
	// ********************



	/**
	 * @see		PresentationService#createPresentationTopics
	 * @see		PresentationDirectives#PresentationDirectives(Directives, PresentationService)
	 */
	public PresentationAssociation(PresentableAssociation assoc, PresentationService ps) {
		super(assoc);
	}

	/**
	 * Standard constructor.
	 *
	 * @see		PresentationTopicMap#createNewAssociation
	 */
	public PresentationAssociation(String id, int version, String type, int typeVersion, String name,
								String topicID1, int topicVersion1, String topicID2, int topicVersion2) {
		super(id, version, type, typeVersion, name, topicID1, topicVersion1, topicID2, topicVersion2);
	}

	/**
	 * Stream constructor.
	 *
	 * @see		PresentationTopicMap#readAssociations
	 * @see		PresentationDirectives#PresentationDirectives
	 */
	public PresentationAssociation(DataInputStream in) throws IOException {
		super(in);
	}



	// ******************************************************************
	// *** Implementation of Interface de.deepamehta.client.GraphEdge ***
	// ******************************************************************



	public GraphNode getNode1() {
		return topic1;
	}

	public GraphNode getNode2() {
		return topic2;
	}



	// ***************
	// *** Methods ***
	// ***************



	/**
	 * Chaines the related topics with this association.
	 * <P>
	 * On the one hand the {@link #topic1} and {@link #topic2} fields of this association
	 * are initialized by topics lookuped from the specified topicmap. At the other hand
	 * this association is registered at both of the topics.
	 * <P>
	 * References checked: 1.4.2003 (2.0a18-pre8)
	 *
	 * @see		TopicmapEditorModel#showAssociation
	 * @see		TopicmapEditorModel#initAssociations
	 */
	void registerTopics(PresentationTopicMap topicmap) {
		// --- topic 1 ---
		try {
			// - set related topic -
			// Note: getTopic() is from BaseTopicMap, may throw DeepaMehtaException
			this.topic1 = (PresentationTopic) topicmap.getTopic(topicID1);
			// - registers this association at related topic -
			topic1.addEdge(this);
		} catch (DeepaMehtaException e) {
			String errText = "Association " + this + " not visible in view \"" + topicmap.getID() +
				"\" (pos 1: " + e.getMessage() + ")";
			topicmap.ps.showMessage(errText, NOTIFICATION_ERROR);
			System.out.println("*** PresentationAssociation.registerTopics(): " + errText);
		}
		// --- topic 2 ---
		try {
			// - set related topic -
			// Note: getTopic() is from BaseTopicMap, may throw DeepaMehtaException
			this.topic2 = (PresentationTopic) topicmap.getTopic(topicID2);
			// - registers this association at related topic -
			topic2.addEdge(this);
		} catch (DeepaMehtaException e) {
			String errText = "Association " + this + " not visible in view \"" + topicmap.getID() +
				"\" (pos 2: " + e.getMessage() + ")";
			topicmap.ps.showMessage(errText, NOTIFICATION_ERROR);
			System.out.println("*** PresentationAssociation.registerTopics(): " + errText);
		}
	}

	/**
	 * @see		PresentationTopicMap#deleteAssociation
	 */
	void unregisterTopics() {
		// ### System.out.println(">>> " + this + " unregisters itself at both topics");
		topic1.removeEdge(this);
		topic2.removeEdge(this);
	}
}
