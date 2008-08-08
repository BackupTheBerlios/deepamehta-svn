package de.deepamehta.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.TopicInitException;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.Session;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;



/**
 * A container representing a set topics existing in
 * {@link de.deepamehta.service.CorporateMemory corporate memory}.
 * <P>
 * <HR>
 * Last change: 7.8.2008 (2.0b8)<BR>
 * J&ouml;rg Richter<BR>
 * jri@deepamehta.de
 */
public class TopicContainerTopic extends ContainerTopic {



	// *************
	// *** Field ***
	// *************



	/**
	 * The type of the contained topics in form of a type topic (type
	 * <CODE>tt-topictype</CODE>).
	 */
	private BaseTopic contentType;



	// *******************
	// *** Constructor ***
	// *******************



	public TopicContainerTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}



	// **********************
	// *** Defining Hooks ***
	// **********************



	// ------------------
	// --- Life Cycle ---
	// ------------------



	public CorporateDirectives init(int initLevel, Session session)
															throws TopicInitException {
		// Note: the content type is set at level 2 (after evocation) because ###
		if (initLevel == INITLEVEL_2) {
			setContainerType();
			if (LOG_TOPIC_INIT) {
				System.out.println("> TopicContainerTopic.init(" + initLevel + "): " +
					this + " -- content type is " + contentType);
			}
		}
		//
		return super.init(initLevel, session);
	}



	// ***********************************************************************
	// *** Implementation of abstract methods of superclass ContainerTopic ***
	// ***********************************************************************



	protected  String getContentType() throws DeepaMehtaException {
		// error check
		if (contentType == null) {
			throw new DeepaMehtaException(this + " has not been inited properly, the " +
				"content type is not available");
		}
		//
		return contentType.getName();
	}

	/**
	 * @see		#getContent
	 * @see		#performQuery
	 */
	protected  String getContentTypeID() throws DeepaMehtaException {
		// error check
		if (contentType == null) {
			throw new DeepaMehtaException(this + " has not been inited properly, the " +
				"content type is not available");
		}
		//
		return contentType.getID();
	}

	// ---

	/**
	 * Retriggers the search and returns the result topics.
	 * 
	 * @return	result topics as vector of 2-element <CODE>String</CODE> arrays:<BR>
	 *			element 1: topic ID<BR>
	 *			element 2: topic name
	 *
	 * @see		ContainerTopic#contextCommands
	 * @see		ContainerTopic#getDetail
	 */
	protected Vector getContent() {
		String relatedTopicID = containerRelatedTopic != null ? containerRelatedTopic.getID() : null;
		// --- query corporate memory ---
		Vector topics = cm.getTopics(getContentTypeID(), containerNameFilter, containerPropertyFilter, relatedTopicID,
			relatedTopicSemantic);
		int topicCount = topics.size();
		//
		System.out.println("> TopicContainerTopic.getContent(): " + containerPropertyFilter + " -- " + topicCount + " topics found");
		//
		// --- build list of topics ---
		Vector topicEntries = new Vector();
		Enumeration e = topics.elements();
		while (e.hasMoreElements()) {
			BaseTopic topic = (BaseTopic) e.nextElement();
			String[] topicEntry = new String[2];
			topicEntry[0] = topic.getID();
			topicEntry[1] = topic.getName();
			topicEntries.addElement(topicEntry);
		}
		//
		return topicEntries;
	}

	protected int getContentSize() {
		return getContent().size();		// ### not yet optimized
	}

	// ### to be dropped
	protected String getAppearance(String id, String name, Session session, CorporateDirectives directives) {
		// >>> compare to ElementContainerTopic.getAppearance()
		return as.getIconfile(id, 1);	// ### version=1
	}

	/**
	 * @throws	DeepaMehtaException	if this container doesn't know its content type
	 *
	 * @see		ContainerTopic#executeCommand
	 * @see		ContainerTopic#triggerQuery
	 */
	protected CorporateDirectives performQuery(String nameFilter, Hashtable propertyFilter,
											BaseTopic relatedTopic, String relatedTopicSemantic, String topicmapID)
											throws DeepaMehtaException {
		// error check
		if (contentType == null) {
			throw new DeepaMehtaException("container \"" + getID() + "\" doesn't know its content type");
		}
		//
		CorporateDirectives directives = new CorporateDirectives();
		String relatedTopicID = relatedTopic != null ? relatedTopic.getID() : null;
		// --- query corporate memory ---
		Vector topics = cm.getTopics(getContentTypeID(), nameFilter, propertyFilter, relatedTopicID, relatedTopicSemantic);
		int topicCount = topics.size();
		//
		System.out.println("> TopicContainerTopic.performQuery(): \"" + propertyFilter +
			"\" -- " + topicCount + " topics found");
		//
		Vector presentableTopics = as.createPresentableTopics(topicmapID, topics, getID());
		directives.add(as.createNewContainer(this, getType(), nameFilter, propertyFilter,
			relatedTopicID, relatedTopicSemantic, topicCount, presentableTopics, false));
		return directives;
	}

	protected CorporateDirectives autoSearch(String groupingProperty) {
		System.out.println("### TopicContainerTopic.autoSearch(): not yet implemented");
		return new CorporateDirectives();
	}

	protected String revealTopic(String topicID, String topicmapID, CorporateDirectives directives) {
		try {
			revealTopic(topicID, SEMANTIC_CONTAINER_HIERARCHY, topicmapID, directives);
			//
			return topicID;
		} catch (Exception e) {
			throw new DeepaMehtaException("*** TopicContainerTopic.revealTopic(): " +
				"topic \"" + topicID + "\" not revealed (" + e + ")");
		}
	}



	// **********************
	// *** Private Method ***
	// **********************



	/**
	 * @see		#init
	 */
	private void setContainerType() {
		// get associated type topic
		Vector topics = as.getRelatedTopics(getType(), SEMANTIC_CONTAINER_TYPE, 2);
		// error check
		if (topics.size() == 0) {
			// ### should throw TopicInitException
			System.out.println("*** TopicContainerTopic.setContainerType(): there is " +
				"no SEMANTIC_CONTAINER_TYPE -- " + this + " has no content type");
			return;
		}
		//
		BaseTopic topic = (BaseTopic) topics.firstElement();
		// error check
		if (!topic.getType().equals(TOPICTYPE_TOPICTYPE)) {
			// ### should throw TopicInitException
			System.out.println("*** TopicContainerTopic.setContainerType(): " +
				"invalid SEMANTIC_CONTAINER_TYPE: \"" + topic.getType() + "\" " +
				"(\"tt-topictype\" is expected) -- " + this + " has no content type");
			return;
		}
		// set container type
		this.contentType = topic;
	}
}
