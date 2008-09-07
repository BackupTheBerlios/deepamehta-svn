package de.deepamehta.client;

import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.Directives;
import de.deepamehta.PresentableAssociation;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Hashtable;



/**
 * <p>
 * <hr>
 * Last change: 7.9.2008 (2.0b8)<br>
 * J&ouml;rg Richter<br>
 * jri@deepamehta.de
 */
public class PresentationAssociation extends PresentableAssociation implements GraphEdge, DeepaMehtaConstants {



	// **************
	// *** Fields ***
	// **************



	private PresentationTopic topic1;
	private PresentationTopic topic2;

	/**
	 * Key: property name (String)<br>
	 * Value: scrollbar value (Integer)
	 */
	private Hashtable scrollbarValues = new Hashtable();
	private Hashtable caretPositions = new Hashtable();



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

	// ### not part of interface
	public Hashtable getScrollbarValues() {
		return scrollbarValues;
	}

	// ### not part of interface
	public Hashtable getCaretPositions() {
		return caretPositions;
	}



	// ***************
	// *** Methods ***
	// ***************



	/**
	 * Chaines the related topics with this association.
	 * <p>
	 * On the one hand the {@link #topic1} and {@link #topic2} fields of this association
	 * are initialized by topics lookuped from the specified topicmap. At the other hand
	 * this association is registered at both of the topics.
	 * <p>
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
