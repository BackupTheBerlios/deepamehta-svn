package de.deepamehta;

import de.deepamehta.util.DeepaMehtaUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;



/**
 * Basis implementation of a topicmap.
 * <P>
 * <HR>
 * Last functional change: 2.4.2003 (2.0a18-pre8)<BR>
 * Last documentation update: 29.5.2001 (2.0a11-pre3)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class BaseTopicMap implements TopicMap {



	// **************
	// *** Fields ***
	// **************



	protected Hashtable topics;
	protected Hashtable associations;



	// ********************
	// *** Constructors ***
	// ********************



	/**
	 * Note: there is no stream constructor here.
	 *
	 * @see		PresentableTopicMap#PresentableTopicMap(DataInputStream)
	 * @see		de.deepamehta.service.ApplicationService#ApplicationService
	 */
	public BaseTopicMap() {
		topics = new Hashtable();
		associations = new Hashtable();
	}

	/**
	 * @see		PresentableTopicMap#PresentableTopicMap(PresentableTopicMap)
	 */
	public BaseTopicMap(BaseTopicMap topicmap) {
		this.topics = topicmap.topics;
		this.associations = topicmap.associations;
	}

	/**
	 * @see		PresentableTopicMap#PresentableTopicMap(Vector topics, Vector associations, String bgImagefile, String bgColorcode, String translationCoord)
	 */
	public BaseTopicMap(Vector topics, Vector associations) {
		this.topics = DeepaMehtaUtils.fromTopicVector(topics);
		this.associations = DeepaMehtaUtils.fromAssociationVector(associations);
	}



	// **********************************************************
	// *** Implementation of interface de.deepamehta.TopicMap ***
	// **********************************************************



	public Hashtable getTopics() {
		return topics;
	}

	public Hashtable getAssociations() {
		return associations;
	}

	/**
	 * @throws	DeepaMehtaException		if the specified topic is not in this topicmap
	 *
	 * @see		#changeTopicName
	 * @see		#changeTopicType
	 * @see		#topicExists
	 * @see		de.deepamehta.client.PresentationAssociation#initTopics
	 * @see		de.deepamehta.service.ApplicationService#getLiveTopic
	 */
	public Topic getTopic(String id) throws DeepaMehtaException {
		Topic topic = (Topic) topics.get(id);
		if (topic == null) {
			throw new DeepaMehtaException("topic \"" + id + "\" not found " +
				"(BaseTopicMap.getTopic())");
		}
		return topic;
	}

	/**
	 * @throws	DeepaMehtaException		if the specified association is not in this
	 *									topicmap
	 *
	 * @see		#changeAssociationType
	 * @see		de.deepamehta.client.PresentationTopicMap#deleteAssociation
	 */
	public Association getAssociation(String id) throws DeepaMehtaException {
		Association assoc = (Association) associations.get(id);
		if (assoc == null) {
			throw new DeepaMehtaException("association \"" + id + "\" not found " +
				"(BaseTopicMap.getAssociation())");
		}
		return assoc;
	}

	// ---

	/**
	 * Adds a topic to this topicmap. ### The topic is only added if it not already
	 * exists in this map (based on topic ID), in this case true is returned. Otherwise
	 * the topic is not added and false is returned.
	 * <P>
	 * References checked: 30.3.2003 (2.0a18-pre8)
	 *
	 * @see		de.deepamehta.service.ApplicationService#createLiveTopic(BaseTopic topic, boolean override, de.deepamehta.service.Session session)
	 * @see		de.deepamehta.client.TopicmapEditorModel#showTopic
	 */
	public void addTopic(Topic topic) throws DeepaMehtaException {
		addTopic(topic.getID(), topic);
	}

	/**
	 * Adds an association to this topicmap. ### The association is only added if it not
	 * already exists in this map (based on association ID), in this case true is
	 * returned. Otherwise the association is not added and false is returned.
	 */
	public void addAssociation(Association association) {
		addAssociation(association.getID(), association);
	}

	// ---

	/**
	 * @see		de.deepamehta.service.ApplicationService#deleteLiveTopic
	 */
	public void deleteTopic(String topicID) {
		if (topics.remove(topicID) == null) {
			System.out.println("*** BaseTopicMap.deleteTopic(): \"" + topicID + "\" not found -- topic not deleted");
		}
	}
	
	/**
	 * @see		de.deepamehta.service.ApplicationService#deleteLiveAssociation
	 */
	public void deleteAssociation(String assocID) {
		if (associations.remove(assocID) == null) {
			System.out.println("*** BaseTopicMap.deleteAssociation(): \"" + assocID + "\" not found -- association not deleted");
		}
	}

	// ---

	public void changeTopicType(String topicID, String type) throws DeepaMehtaException {
		getTopic(topicID).setType(type);
	}

	public void changeAssociationType(String assocID, String type) throws DeepaMehtaException {
		getAssociation(assocID).setType(type);
	}
	
	// ---
	
	/**
	 * @throws	DeepaMehtaException	if the specified topic is not contained in this topicmap
	 *
	 * @see		de.deepamehta.client.TopicmapEditorModel#changeTopicName
	 */
	public void changeTopicName(String topicID, String name) throws DeepaMehtaException {
		getTopic(topicID).setName(name);		// getTopic() throws DME
	}

	public void changeAssociationName(String assocID, String name) throws DeepaMehtaException {
		getAssociation(assocID).setName(name);	// getAssociation() throws DME
	}

	// ---

	/**
	 * @see		#addTopic
	 * @see		de.deepamehta.service.ApplicationService#createLiveTopic
	 * @see		de.deepamehta.client.PresentationTopicMap#initGeometry(de.deepamehta.client.PresentationTopic)
	 */
	public boolean topicExists(String id) {
		// Note: dont use getTopic() because it throws DeepaMehtaException
		return topics.get(id) != null;
	}

	public boolean associationExists(String id) {
		// Note: dont use getAssociation() because it throws DeepaMehtaException
		return associations.get(id) != null;
	}



	// ***************
	// *** Methods ***
	// ***************



	public String toString() {
		return topics.size() + " topics, " + associations.size() + " associations";
	}



	// ---------------------
	// --- Serialization ---
	// ---------------------



	public void write(DataOutputStream out) throws IOException {
		DeepaMehtaUtils.writeTopics(topics, out);
		DeepaMehtaUtils.writeAssociations(associations, out);
	}



	// *************************
	// *** Protected Methods ***
	// *************************



	/**
	 * @see		#addTopic(Topic topic)
	 * @see		de.deepamehta.service.ApplicationService#addTopic(Topic topic)
	 */
	protected final void addTopic(String id, Topic topic) {
		Object o = topics.put(id, topic);
	}

	/**
	 * @see		#addTopic(Topic topic, boolean override)
	 * @see		de.deepamehta.service.ApplicationService#addTopic(Topic topic, boolean override)
	 */
	protected final void addAssociation(String id, Association association) {
		Object o = associations.put(id, association);
	}
}
