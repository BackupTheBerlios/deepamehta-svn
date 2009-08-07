package de.deepamehta.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.PresentableAssociation;
import de.deepamehta.PresentableTopic;
import de.deepamehta.TopicInitException;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.Session;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;



/**
 * A container representing a set of elements existing in a
 * {@link de.deepamehta.service.CorporateDatasource corporate datasource}.
 * <P>
 * An <CODE>ElementContainerTopic</CODE> provides the behavoir of replicating datasource
 * elements into topics along with properties.
 * <P>
 * <HR>
 * Last change: 7.8.2008 (2.0b8)<BR>
 * J&ouml;rg Richter<BR>
 * jri@deepamehta.de
 */
public abstract class ElementContainerTopic extends ContainerTopic {



	// *******************
	// *** Constructor ***
	// *******************



	public ElementContainerTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}



	// ***********************
	// *** Abstract Method ***
	// ***********************



	/**
	 * When creating a topic name from an element: returns the elements attribute
	 * to be used as name. ### meanwhile we have a general naming policy
	 */
	protected abstract String getNameAttribute();



	// **********************
	// *** Defining Hooks ***
	// **********************



	// ------------------
	// --- Life Cycle ---
	// ------------------



	public CorporateDirectives init(int initLevel, Session session) throws TopicInitException {
		CorporateDirectives directives = super.init(initLevel, session);
		//
		if (initLevel == INITLEVEL_2) {
			if (LOG_TOPIC_INIT) {
				System.out.println(">>> ElementContainerTopic.init(" + initLevel +
					"): " + this + " -- sets data source");
			}
			setDataSource(session, directives);
		}
		//
		return directives;
	}



	// ************************
	// *** Protected Method ***
	// ************************



	/**
	 * Sublasses can override to perform some transformation upon the enetered data e.g.
	 * perform lookup in foreign tables and replace foreign values by its foregin keys.
	 * <P>
	 * ### This is a hook method introduced in this class
	 * <P>
	 * Subclasses can return <CODE>null</CODE> in case of failed lookup to signalize
	 * the main query to halt.
	 * <P>
	 * Look inside the source of the {@link AdresseContainerTopic} to see how it performs
	 * the country lookup in a foreign table.
	 * <P>
	 * The default implementation returns the entered data directly.
	 *
	 * @see		#performQuery
	 */
	protected Hashtable queryData(Hashtable formData) {
		return formData;
	}



	// ************************************************************
	// *** Implementation of abstract methods of ContainerTopic ***
	// ************************************************************



	// Note: getContentType() and getContentTypeID() remain abstract in this class
	// and must be implemented in the subclasses of ElementContainerTopic
	// ### this isn't necessary anymore because meanwhile there is a association
	// (type "aggregation" between the container type and the element type)



	/**
	 * Retriggers the search and returns the result elements.
	 * 
	 * @return	result elements as vector of 2-element <CODE>String</CODE> arrays:<BR>
	 *			element 1: element ID<BR>
	 *			element 2: element name
	 *
	 * @see		ContainerTopic#contextCommands
	 * @see		ContainerTopic#getDetail
	 */
	protected Vector getContent() throws DeepaMehtaException {
		try {
			// error check
			if (dataSource == null) {
				throw new DeepaMehtaException("the datasource of " + this + " is not known");
			}
			// --- query the datasource ---
			Vector elements = getElements(containerPropertyFilter, containerRelatedTopic);	// ### queryData()
			int topicCount = elements.size();
			//
			System.out.println("> ElementContainerTopic.getContent(): " + containerPropertyFilter + " -- " + topicCount + " elements found");
			//
			// --- build list of elements ---
			Vector topicEntries = new Vector();
			Enumeration e = elements.elements();
			while (e.hasMoreElements()) {
				Hashtable attributes = (Hashtable) e.nextElement();
				String[] topicEntry = new String[2];
				topicEntry[0] = (String) attributes.get("ID");		// ### hardcoded
				topicEntry[1] = topicName(attributes, getNameAttribute());
				topicEntries.addElement(topicEntry);
			}
			//
			return topicEntries;
		} catch (Exception e) {
			throw new DeepaMehtaException("error while retrieving content of " + this + " (" + e + ")");
		}
	}

	protected int getContentSize() {
		try {
			return getElementCount(containerPropertyFilter, containerRelatedTopic);
		} catch (Exception e) {
			System.out.println("*** ElementContainerTopic.getContentSize(): " + e);
			return 0;
		}
	}

	// ### to be dropped
	// Note: id is the element ID, not a topicID
	protected String getAppearance(String id, String name, Session session, CorporateDirectives directives) {
		// ### inidividual appearance of already evoked elements is not considered.
		// >>> compare to TopicContainerTopic.getAppearance()
		return as.getIconfile(getContentTypeID(), 1);	// ### was (id, 1) ### version=1
	}

	/**
	 * @param	propertyFilter		must not be null
	 *
	 * @throws	DeepaMehtaException	if this container doesn't know its data source
	 *
	 * @see		ContainerTopic#executeCommand
	 * @see		ContainerTopic#triggerQuery
	 */
	protected CorporateDirectives performQuery(String nameFilter, Hashtable propertyFilter,
											BaseTopic relatedTopic, String relatedTopicSemantic, String topicmapID)
											throws DeepaMehtaException {
		// error check 1
		if (dataSource == null) {
			throw new DeepaMehtaException("the datasource of " + this + " is not known");
		}
		//
		CorporateDirectives directives = new CorporateDirectives();
		// ### perform possible tranformation upon eneterd form data to obtain the
		// actual query -- this feature is only used by subclasses
		Hashtable attributes = queryData(propertyFilter);
		//
		if (attributes == null) {
			System.out.println(">>> ElementContainerTopic.performQuery(): subquery of " + this +
				" returns nothing -- performing main query aborted");
			directives.add(DIRECTIVE_PLAY_SOUND, NOTIFICATION_SOUNDS[SOUND_NO_RESULT]);
			return directives;
		}
		// error check 2
		if (relatedTopic != null && attributes.size() > 0) {
			throw new DeepaMehtaException("property filter and relation filter of " + this +
				" are set at the same time -- not yet implemented");
		}
		//
		try {
			// --- query the data source ---
			int elementCount = getElementCount(attributes, relatedTopic);
			Vector topics = null;
			if (elementCount <= MAX_LISTING) {
				Vector elements = getElements(attributes, relatedTopic);
				topics = createTopicsFromElements(elements);
			}
			//
			System.out.println("> ElementContainerTopic.performQuery(): " + attributes +
				" -- " + elementCount + " elements found");
			//
			String relTopicID = relatedTopic == null ? "" : relatedTopic.getID();
			directives.add(as.createNewContainer(this, getType(), nameFilter, propertyFilter, relTopicID,
				relatedTopicSemantic, elementCount, topics, true));		// evokeContent=true
			//
			return directives;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DeepaMehtaException("error while retrieving content of " + this + " (" + e + ")");
		}
	}

	/**
	 * @see		de.deepamehta.movies.topics.MovieContainerTopic#executeCommand
	 */
	protected CorporateDirectives autoSearch(String groupingField) {
		CorporateDirectives directives = new CorporateDirectives();
		// check if this container is already narrowed by the grouping field
		// if so, no further grouping is possible
		if (containerPropertyFilter.containsKey(groupingField)) {
			System.out.println(">>> ElementContainerTopic.autoSearch(): The container " +
				"\"" + getName() + "\" is already narrowed by \"" + groupingField +
				"\" -- no further grouping possible");
			directives.add(DIRECTIVE_SHOW_MESSAGE, "The container \"" +
				getName() + "\" is already narrowed by \"" + groupingField + "\" -- " +
				"no further grouping possible", new Integer(NOTIFICATION_DEFAULT));
			return directives;
		}
		// error check
		if (dataSource == null) {
			throw new DeepaMehtaException("*** ElementContainerTopic.autoSearch(): " +
				this + " has no data source -- grouping not possible");
		}
		//
		try {
			// query the data source for groups
			Vector groups = dataSource.queryGroups(getContentType(),
				containerPropertyFilter, groupingField);
			int groupCount = groups.size();
			System.out.println(">>> ElementContainerTopic.autoSearch(): \"" +
				containerPropertyFilter + "\" -- " + groupCount + " groups found");
			// check if groups are found
			if (groupCount == 0) {
				return directives;
			}
			// check if the result is too extensive
			// ### if so, the visualization is just truncated for the moment -- required
			// is more intelligent grouping
			if (groupCount > MAX_REVEALING) {
				System.out.println(">>> ElementContainerTopic.autoSearch(): There are " +
					groupCount + " \"" + groupingField + "\" groups -- only " +
					MAX_REVEALING + " are shown");
				directives.add(DIRECTIVE_SHOW_MESSAGE, "There are " + groupCount + " \"" +
					groupingField + "\" groups -- only " + MAX_REVEALING + " are shown",
					new Integer(NOTIFICATION_DEFAULT));
			}
			// determine query elements
			String queryElements = as.queryElements(containerPropertyFilter);
			if (queryElements.length() > 0) {
				queryElements += ",";
			}
			queryElements += groupingField;
			// determine basename for group
			String groupname = as.queryString(containerPropertyFilter);
			if (groupname.length() > 0) {
				groupname += ", ";
			}
			// --- visualize result set ---
			System.out.println("> ElementContainerTopic.autoSearch(): " + groupingField +
				": query elements of subcontainers: \"" + queryElements + "\"");
			String[] group;
			PresentableTopic topic;
			PresentableAssociation assoc;
			// loop through the groups
			for (int i = 0; i < Math.min(groupCount, MAX_REVEALING); i++) {
				group = (String[]) groups.elementAt(i);
				// create new container representing the group
				topic = createNewContainer("\"" + groupname + group[0] + "\"", getType(),
					groupingField, group[0], queryElements, containerPropertyFilter, group[1]);
				// associate the group container with this container
				assoc = as.createPresentableAssociation(SEMANTIC_CONTAINER_HIERARCHY,
					getID(), getVersion(), topic.getID(), 1, false);
				// DIRECTIVE_SHOW_TOPIC causes the client to show the respective group as a
				// container. DIRECTIVE_SHOW_ASSOCIATION causes the client to associate
				// the group with this container
				directives.add(DIRECTIVE_SHOW_TOPIC, topic, Boolean.TRUE);
				directives.add(DIRECTIVE_SHOW_ASSOCIATION, assoc, Boolean.TRUE);
			}
			return directives;
		} catch (Exception e) {
			throw new DeepaMehtaException("*** ElementContainerTopic.autoSearch(): " +
				"content of " + this + " not available (" + e + ")");
		}
	}

	/**
	 * @see		ContainerTopic#executeCommand
	 */
	protected String revealTopic(String id, String topicmapID, boolean doSelectTopic, CorporateDirectives directives) throws DeepaMehtaException {
		try {
			// query the data source
			Hashtable elementData = dataSource.queryElement(getContentType(), id);
			//
			PresentableTopic topic = createTopicFromElement(id, elementData);
			String topicID = topic.getID();
			// ### could probably call LiveTopic.revealTopic() instead (slight modification required there)
			PresentableAssociation assoc = as.createPresentableAssociation(SEMANTIC_CONTAINER_HIERARCHY,
				getID(), getVersion(), topicID, 1, true);
			directives.add(DIRECTIVE_SHOW_TOPIC, topic, Boolean.TRUE);
			directives.add(DIRECTIVE_SHOW_ASSOCIATION, assoc, Boolean.TRUE);
			if (doSelectTopic) {
				directives.add(DIRECTIVE_SELECT_TOPIC, topicID);
			}
			//
			return topicID;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DeepaMehtaException("*** ElementContainerTopic.revealTopic(): " +
				"topic \"" + id + "\" not revealed (" + e + ")", e);
		}
	}



	// ***********************
	// *** Private Methods ***
	// ***********************



	/**
	 * @see		#getContent
	 * @see		#performQuery
	 */
	private Vector getElements(Hashtable attributes, BaseTopic relatedTopic) throws Exception {
		if (relatedTopic == null) {
			return dataSource.queryElements(getContentType(), attributes, false);	// caseSensitiv=false, throws Exception
		} else {
			return ((DataConsumerTopic) as.getLiveTopic(relatedTopic)).getRelatedElements(getContentTypeID());
		}
	}

	private int getElementCount(Hashtable attributes, BaseTopic relatedTopic) throws Exception {
		if (relatedTopic == null) {
			return dataSource.getElementCount(getContentType(), attributes, false);	// caseSensitiv=false, throws Exception
		} else {
			return ((DataConsumerTopic) as.getLiveTopic(relatedTopic)).getRelatedElementCount(getContentTypeID());
		}
	}

	// ---

	/**
	 * @return	The topic ID of the newly created container or <CODE>null</CODE> of no
	 *			container was created.
	 *
	 * @see		#autoSearch
	 */
	private PresentableTopic createNewContainer(String containerName, String containerType,
												String groupingField, String groupingValue,
												String queryElements, Hashtable formData, String foundStr) {
		// create topic data
		Hashtable topicData;
		topicData = (Hashtable) formData.clone();
		topicData.put(groupingField, groupingValue);
		topicData.put(PROPERTY_QUERY_ELEMENTS, queryElements);
		topicData.put(PROPERTY_ELEMENT_COUNT, foundStr);
		// create new container topic
		String containerID = cm.getNewTopicID();
		// the version of a new container topic is 1
		// ### the version of the type is set to 1
		// ### the container should not created in corporate memory here but while
		// CorporateDirectives.updateCorporateMemory() -- not possible until we
		// have further information in directives regarding the server only
		cm.createTopic(containerID, 1, containerType, 1, containerName);
		cm.setTopicData(containerID, 1, topicData);
		//
		return new PresentableTopic(containerID, 1, containerType, 1, containerName, getID(), foundStr);
	}

	// ---

	/**
	 * Creates {@link de.deepamehta.PresentableTopic}s corresponding to the
	 * elements in the specified vector.
	 * <P>
	 * Presumption 1: there are not more than {@link #MAX_LISTING} elements.<BR>
	 * Presumption 2: the element data contain a field <CODE>ID</CODE>.
	 *
	 * @return	Vector of {@link de.deepamehta.PresentableTopic}
	 *
	 * @see		#performQuery
	 */
	private Vector createTopicsFromElements(Vector elements) {
		Vector topics = new Vector();
		//
		Enumeration e = elements.elements();
		while (e.hasMoreElements()) {
			Hashtable elementData = (Hashtable) e.nextElement();
			String elementID = (String) elementData.get("ID");		// ### hardcoded
			// create topics in corporate memory (storage layer)
			PresentableTopic topic = createTopicFromElement(elementID, elementData);
			// add PresentableTopic to result
			topics.addElement(topic);
		}
		//
		return topics;
	}

	/**
	 * Creates a topic from an element.<BR>
	 * The elements attributes are stored as topic properties.
	 * <P>
	 * To possibly unifying the element with an existing topic the topic ID is constructed programatically
	 * involving the elements type as well as the elements ID.
	 * ### This is probably not sufficient resp. unique. The datasource could be involved too.
	 *
	 * @see		#revealTopic
	 * @see		#createTopicsFromElements
	 */
	private PresentableTopic createTopicFromElement(String elementID, Hashtable elementData) {
		// ### compare to DataConsumerTopic.createTopicFromElement()
		String topicID = createTopicID(elementID);					// build identifier as topic id
		String typeID = getContentTypeID();
		String name = topicName(elementData, getNameAttribute());	// ### there is new naming policy
		PresentableTopic topic = new PresentableTopic(topicID, 1, typeID, 1, name, getID(), "");
		// --- trigger makeProperties() hook ---
		Hashtable props = as.triggerMakeProperties(typeID, elementData);
		//
		topic.setOriginalID(elementID);
		topic.setProperties(props);
		topic.setIcon(as.getIconfile(getContentTypeID(), 1));		// ### inidividual appearance of already
																	// ### evoked elements is not considered
		return topic;
	}

	/**
	 * Creates a unique topic ID for an element.
	 *
	 * @see		#createTopicFromElement
	 */
	protected String createTopicID(String elementID) {
		// >>> compare to DataConsumerTopic.createTopicID()
		return "t-" + getContentType() + "-" + elementID;
	}
}
