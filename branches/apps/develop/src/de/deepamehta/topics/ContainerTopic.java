package de.deepamehta.topics;

import de.deepamehta.BaseAssociation;
import de.deepamehta.BaseTopic;
import de.deepamehta.Commands;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.Detail;
import de.deepamehta.PresentableAssociation;
import de.deepamehta.TopicInitException;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateCommands;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.Session;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Logger;



/**
 * ### A <code>ContainerTopic</code> is an active topic that acts as a container for other
 * topics. All topics contained in a container have the same topic type. The topics
 * contained in a container are represented by means of a query that
 * is associated with that container. The property editor of a <code>ContainerTopic</code>
 * serves as input form by which the user can filter the topics contained in the
 * container. The empty query represents the "null" filter and retrieves all contained
 * topics.
 * <p>
 * The query associated with a container is immutable, that is, once the
 * <code>ContainerTopic</code> is initialized, its associated query will never change.
 * <p>
 * The user can narrow the query by filling in additional properties. If the resulting
 * set of topics is still too big to be viualized, the container will create (and
 * aggregate) another (sub)container.
 *
 * <h4>Active behavoir</h4>
 *
 * The active <I>initialization</I> behavoir of a <code>ContainerTopic</code> is
 * setting the associated query.
 * <p>
 * The active <I>labeling</I> behavoir of a <code>ContainerTopic</code> is
 * showing the number of elements contained in the container.
 * <p>
 * The active <I>property disabling</I> behavoir of a <code>ContainerTopic</code> is
 * disabling all properties that are involved in the associated query -- this way the
 * user is prevented to change the associated query, only narrowing is possible.
 *
 * <h4>Note to application programmers</h4>
 *
 * <code>ContainerTopic</code> is an abstract class -- it provides the behavoir described
 * above without "knowing" the origin of the contained topics. Actually there are two
 * subclasses of <code>ContainerTopic</code>:
 *
 * <ol>
 * <li><code>TopicContainerTopic</code>
 * <p>
 * A {@link TopicContainerTopic} represents a set of topics of a specific topic type
 * existing in corporate memory.
 * <p>
 * For every topic type the user creates interactively, the corresponding type that
 * represents the container for the new topic type, is created automatically as a subclass
 * of <code>TopicContainerTopic</code> (this is active behavoir of a
 * {@link TopicTypeTopic}).
 * <p>
 * An application programmer usually will not derive an active topic from
 * <code>TopicContainerTopic</code>.
 * <p>
 * <li><code>ElementContainerTopic</code>
 * <p>
 * An {@link ElementContainerTopic} represents a set of elements of a specific type
 * existing in a {@link de.deepamehta.service.CorporateDatasource}.
 * An <code>ElementContainerTopic</code> provides the behavoir of creating topics in
 * corporate memory based upon elements of a corporate datasource. The elements attributes
 * are replicated in form of topic properties.
 * <p>
 * For the time being to use an <code>ElementContainerTopic</code> the application
 * programmer is required to subclass <code>ElementContainerTopic</code> in order to
 * specify the element access, topic creation and attribute replication.
 * <p>
 * E.g. the {@link de.deepamehta.movies.topics.MovieContainerTopic} specifies the topic types used for created
 * topics and containers as well as the attribute used for the name of created topics.
 * <p>
 * In the future the user resp. administrator will be able to provide these essential
 * information directly in "Design"-mode without needing deploying an application
 * programmer.
 * </ol>
 * <p>
 * <hr>
 * Last change: 29.9.2008 (2.0b8)<br>
 * J&ouml;rg Richter<br>
 * jri@deepamehta.de
 */
public abstract class ContainerTopic extends LiveTopic {



	// **************
	// *** Fields ***
	// **************



	private static Logger logger = Logger.getLogger("de.deepamehta");

	/**
	 * Part of the query represented by this container: the property filter.
	 * <p>
	 * The containers query is immutable -- once set it will not change over the lifetime
	 * of the container.
	 * <p>
	 * ### The containers query is determined by both, the (visible) containers properties
	 * and the (hidden) "QueryElements" property. The visible properties stores the last
	 * query that was submitted to this container. The hidden "QueryElements" property
	 * containes a space or comma separated list of the property names involved in the
	 * containers query.
	 * <p>
	 * <table>
	 * <tr><td><b>Set once by</b></td></tr>
	 * <tr><td>{@link #initContainer}</td></tr>
	 * <tr><td><b>Accessed by</b></td></tr>
	 * <tr><td>{@link #equalsQuery}</td></tr>
	 * <tr><td>{@link #executeCommand}</td></tr>
	 * <tr><td>{@link ElementContainerTopic#getContent}</td></tr>
	 * <tr><td>{@link ElementContainerTopic#autoSearch}</td></tr>
	 * </table>
	 */
	protected Hashtable containerPropertyFilter = new Hashtable();

	/**
	 * Part of the query represented by this container: the name filter.
	 * <p>
	 * Note: Remains uninitialized if no name filter is set for this container
	 */
	protected String containerNameFilter;

	/**
	 * Part of the query represented by this container: the relation filter.
	 * <p>
	 * The topic to which an association must exists to the the topics of this container.
	 * <p>
	 * Initialized by {@link #initContainer}.<br>
	 * Note: Remains uninitialized if no relation filter is set for this container
	 */
	protected BaseTopic containerRelatedTopic;

	/**
	 * Part of the query represented by this container: the relation filter
	 */
	protected String relatedTopicSemantic;

	// ---

	/**
	 * The names of the properties involved in the query represented by this container.
	 * <p>
	 * The property names are determined by the value of this containers "QueryElements" property.
	 * <p>
	 * Initialized by {@link #initContainer}.<br>
	 * Accessed by {@link #disabledProperties}
	 */
	protected Vector disabledProperties;



	// *******************
	// *** Constructor ***
	// *******************



	ContainerTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}



	// **********************
	// *** Defining Hooks ***
	// **********************



	// ------------------
	// --- Life Cycle ---
	// ------------------



	public CorporateDirectives init(int initLevel, Session session) {
		CorporateDirectives directives = super.init(initLevel, session);
		//
		if (initLevel == INITLEVEL_1) {
			initContainer(session, directives);
			if (LOG_TOPIC_INIT) {
				logger.info("init level " + initLevel + ": " + this + " -- name filter: \"" + containerNameFilter +
					"\" property filer: " + containerPropertyFilter + " disabled properties: " + disabledProperties +
					" related topic: " + containerRelatedTopic);
			}
		}
		//
		return directives;
	}



	// --------------------------
	// --- Providing Commands ---
	// --------------------------



	/**
	 * @see		de.deepamehta.service.ApplicationService#showTopicMenu
	 */
	public CorporateCommands contextCommands(String topicmapID, String viewmode,
														Session session, CorporateDirectives directives) {
		// Note: super.contextCommands() isn't called here because a container has no standard commands
		CorporateCommands commands = new CorporateCommands(as);
		// "Group by"
		String[] groupingProps = getGroupingProperties();
		if (groupingProps != null) {
			Commands cmdGroup = commands.addCommandGroup(as.string(ITEM_GROUP_BY), FILESERVER_IMAGES_PATH, ICON_GROUP_BY);
			for (int i = 0; i < groupingProps.length; i++) {
				cmdGroup.addCommand(groupingProps[i], CMD_GROUP_BY + COMMAND_SEPARATOR + groupingProps[i]);
			}
		}
		// "Remove"
		String cmd = as.isContainedInPublishedTopicmap(getID()) ? CMD_HIDE_TOPIC : CMD_DELETE_TOPIC;
		commands.addCommand(as.string(ITEM_REMOVE_TOPIC), cmd, FILESERVER_IMAGES_PATH, ICON_HIDE_TOPIC);
		//
		return commands;
	}



	// --------------------------
	// --- Executing Commands ---
	// --------------------------



	/**
	 * @see		de.deepamehta.service.ApplicationService#performTopicAction
	 */
	public CorporateDirectives executeCommand(String command, Session session,
											String topicmapID, String viewmode) throws DeepaMehtaException {
		StringTokenizer st = new StringTokenizer(command, COMMAND_SEPARATOR);
		String cmd = st.nextToken();
		//
		if (cmd.equals(CMD_DEFAULT)) {
			// reveal content
			CorporateDirectives directives = triggerQuery(topicmapID);
			return directives;
		} else if (cmd.equals(CMD_SUBMIT_FORM)) {
			// filtering
			Hashtable containerProps = getProperties();
			String nameFilter = (String) containerProps.get(PROPERTY_SEARCH);
			return performQuery(nameFilter, queryProperties(containerProps), null, null, topicmapID);
		} else if (cmd.equals(CMD_FOLLOW_HYPERLINK)) {
			CorporateDirectives directives = new CorporateDirectives();
			String url = st.nextToken();
			String urlPrefix = "http://";
			if (!url.startsWith(urlPrefix)) {
				logger.warning("url \"" + url + "\" not recognized by CMD_FOLLOW_HYPERLINK");
				return directives;
			}
			String action = url.substring(urlPrefix.length());
			if (action.startsWith(ACTION_REVEAL_TOPIC)) {
				String id = action.substring(ACTION_REVEAL_TOPIC.length() + 1);	// +1 to skip /
				revealResultTopic(id, topicmapID, true, directives);	// doSelectTopic=true
			} else if (action.startsWith(ACTION_REVEAL_ALL)) {
				revealAll(topicmapID, directives);
			} else {
				throw new DeepaMehtaException("hyperlink action \"" + action + "\" not recognized");
			}
			return directives;
		} else if (cmd.equals(CMD_GROUP_BY)) {
			// grouping
			String groupingProp = st.nextToken();
			return autoSearch(groupingProp);
		} else {
			return super.executeCommand(command, session, topicmapID, viewmode);
		}
	}



	// ---------------------------
	// --- Handling Properties ---
	// ---------------------------



	public Hashtable getPropertyBaseURLs() {
		Hashtable baseURLs = new Hashtable();
		baseURLs.put(PROPERTY_RESULT, as.getCorporateWebBaseURL());
		return baseURLs;
	}

	public Vector disabledProperties(Session session) {
		return disabledProperties;
	}



	// ----------------------------------------
	// --- Providing additional topic label ---
	// ----------------------------------------



	public String getLabel() {
		return getProperty(PROPERTY_ELEMENT_COUNT);
	}



	// -----------------------------
	// --- Handling Topic Detail ---
	// -----------------------------



	/**
	 * As detail view a container displays its contents in a 1-column table format,
	 * one line per topic.
	 *
	 * @see		de.deepamehta.topics.LiveTopic#executeCommand
	 */
	public Detail getDetail() {
		// ### ?
		if (as.type(this).getTypeDefinition().size() == 0) {
			return new Detail(DETAIL_TOPIC);
		}
		//
		Vector content = getContent();
		int contentSize = content.size();
		String[] columnNames = {"Nr", ""};
		String[][] values = new String[contentSize][2];
		String[] topic;
		for (int i = 0; i < contentSize; i++) {
			topic = (String[]) content.elementAt(i);
			values[i][0] = Integer.toString(i + 1);
			values[i][1] = topic[1];
		}
		//
		String title = getName().equals("") ? "Container Content" : "\"" + getName() + "\"";
		Detail detail = new Detail(DETAIL_TOPIC, DETAIL_CONTENT_TABLE, columnNames,
        	values, title, "setProperties");
		//
		return detail;
	}



	// ***************
	// *** Methods ***
	// ***************



	/**
	 * Triggers this search and builds the directives to show the result.
	 * <p>
	 * References checked: 6.8.2008 (2.0b8)
	 *
	 * @see		#executeCommand
	 * @see		TopicMapTopic#searchByTopicType
	 */
	CorporateDirectives triggerQuery(String topicmapID) {
		return performQuery(containerNameFilter, containerPropertyFilter, containerRelatedTopic, relatedTopicSemantic, topicmapID);
	}

	/**
	 * @return	<code>true</code> if the specified query is the same as represented by
	 *			this container, <code>false</code> otherwise.
	 *
	 * @see		ApplicationService#createNewContainer
	 */
	public boolean equalsQuery(String nameFilter, Hashtable propertyFilter, String relatedTopicID, String relTopicSemantic) {
		return (nameFilter == null || nameFilter.equals(containerNameFilter)) &&
			(containerRelatedTopic == null || containerRelatedTopic.getID().equals(relatedTopicID)) &&
			(relatedTopicSemantic == null || relatedTopicSemantic.equals(relTopicSemantic)) &&
			propertyFilter.equals(containerPropertyFilter);
	}



	// ------------------------
	// --- Abstract Methods ---
	// ------------------------



	// Implemention specific type description of the content topics
	// - A topic container returns the name of the topic type
	// - A element container returns the name of the element type
	//
	// ### Since CM 2.25 this is not needed to be abstract anymore, see TopicContainerTopic.setContainerType()
	// ### think again about element container (see above)
	protected abstract String getContentType();

	// Topic type ID of the content topics
	//
	// ### Since CM 2.25 this is not needed to be abstract anymore, see TopicContainerTopic.setContainerType()
	protected abstract String getContentTypeID();

	/**
	 * Triggers this search and returns the result.
	 * 
	 * @return	the result set as vector of 2-element <code>String</code> arrays:<br>
	 *			element 1: ID<br>
	 *			element 2: topic name
	 *
	 * @see		#contextCommands
	 * @see		#getDetail
	 */
	protected abstract Vector getContent();

	protected abstract int getContentSize();

	/**
	 * ### to be dropped
	 *
	 * @see		#contextCommands
	 */
	protected abstract String getAppearance(String id, String name, Session session, CorporateDirectives directives);

	/**
	 * Performs the specified search and returns the directives to reveal the result.
	 * <p>
	 * References checked: 6.8.2008 (2.0b8)
	 *
	 * @see		#executeCommand
	 * @see		#triggerQuery
	 */
	protected abstract CorporateDirectives performQuery(String nameFilter, Hashtable propertyFilter,
												BaseTopic relatedTopic, String relatedTopicSemantic, String topicmapID);

	protected abstract CorporateDirectives autoSearch(String groupingProperty);

	// ### in case of a topic container "id" is a topic ID
	// ### in case of an element container "id" is an element ID
	protected abstract String revealTopic(String id, String topicmapID, boolean doSelectTopic, CorporateDirectives directives);



	// *************************
	// *** Protected Methods ***
	// *************************



	protected String[] getGroupingProperties() {
		return null;
	}

	// ---

	/**
	 * ### Transforms container properties into a raw query.
	 * <p>
	 * ### A raw query contains all properties involved in the actual query.
	 * <p>
	 * ### Active topics have the opportunity to modify the properties before
	 * the topics are retrieved from the source.
	 * <p>
	 * ### This is done by removing the properties for internal use
	 * (<code>QueryElements</code>, <code>ElementCount</code>,
	 * <code>RelatedTopicID</code>, <code>Topic Name</code>)
	 * as well as empty properties.
	 *
	 * @see		#executeCommand
	 */
	protected Hashtable queryProperties(Hashtable containerProperties) {
		// The topic properties of a container acting as query values (and the property
		// editor acts as query form).
		//
		// --- remove internal used properties ---
		containerProperties.remove(PROPERTY_QUERY_ELEMENTS);
		containerProperties.remove(PROPERTY_ELEMENT_COUNT);
		containerProperties.remove(PROPERTY_RELATED_TOPIC_ID);
		containerProperties.remove(PROPERTY_RELATED_TOPIC_SEMANTIC);
		containerProperties.remove(PROPERTY_SEARCH);
		containerProperties.remove(PROPERTY_RESULT);
		containerProperties.remove(PROPERTY_OWNER_ID);
		// --- remove empty properties ---
		Enumeration e = containerProperties.keys();
		String fieldname;
		String value;
		while (e.hasMoreElements()) {
			fieldname = (String) e.nextElement();
			value = (String) containerProperties.get(fieldname);
			if (value.equals("")) {
				containerProperties.remove(fieldname);
				e = containerProperties.keys();		// ### workaround
			}
		}
		// --- return raw query properties ---
		return containerProperties;
	}



	// ***********************
	// *** Private Methods ***
	// ***********************



	/**
	 * Initializes
	 * {@link #containerPropertyFilter},
	 * {@link #containerNameFilter},
	 * {@link #disabledProperties} and
	 * {@link #containerRelatedTopic}.
	 *
	 * @see		#init
	 */
	private void initContainer(Session session, CorporateDirectives directives) {
		// --- disabledProperties ---
		this.disabledProperties = queryFields(getProperty(PROPERTY_QUERY_ELEMENTS));
		this.disabledProperties.addElement(PROPERTY_RESULT);
		// --- containerRelatedTopic, relatedTopicSemantic ---
		// Note: only initialized if a relation filter is used
		try {
			String relTopicID = getProperty(PROPERTY_RELATED_TOPIC_ID);
			if (!relTopicID.equals("")) {
				this.containerRelatedTopic = as.getLiveTopic(relTopicID, 1, session, directives);
				String relTopicSemantic = getProperty(PROPERTY_RELATED_TOPIC_SEMANTIC);
				if (!relTopicSemantic.equals("")) {
					this.relatedTopicSemantic = relTopicSemantic;
				}
			}
		} catch (DeepaMehtaException e) {
			String msg = "Container " + this + " is obsolete";
			throw new TopicInitException(msg);
		}
		// --- containerPropertyFilter, containerNameFilter ---
		Enumeration propNames = disabledProperties.elements();
		Hashtable containerProps = getProperties();
		while (propNames.hasMoreElements()) {
			String propName = (String) propNames.nextElement();
			String propValue = (String) containerProps.get(propName);
			if (propName.equals(PROPERTY_SEARCH)) {				// ###
				this.containerNameFilter = propValue;
			} else if (propName.equals(PROPERTY_RESULT)) {		// ###
				// do nothing
			} else {
				if (propValue != null) {
					this.containerPropertyFilter.put(propName, propValue);
				} else {
					logger.warning(this + ": search property \"" + propName +
						"\" has no value -- the property filter will not work properly");
					directives.add(DIRECTIVE_SHOW_MESSAGE, "Search property \"" + propName + "\" of " + this +
						" has no value -- the property filter will not work properly", new Integer(NOTIFICATION_WARNING));
				}
			}
		}
	}

	private void revealResultTopic(String id, String topicmapID, boolean doSelectTopic, CorporateDirectives directives) {
		String topicID = revealTopic(id, topicmapID, doSelectTopic, directives);	// call abstract method
		// --- reveal association(s) if relation filter is set ---
		if (containerRelatedTopic != null) {
			Vector assocs = cm.getAssociations(topicID, containerRelatedTopic.getID(), true);	// ignoreDirection=true
			Enumeration e = assocs.elements();
			while (e.hasMoreElements()) {
				BaseAssociation assoc = (BaseAssociation) e.nextElement();
				directives.add(DIRECTIVE_SHOW_ASSOCIATION, new PresentableAssociation(assoc));
			}
		}
	}

	private void revealAll(String topicmapID, CorporateDirectives directives) {
		Enumeration e = getContent().elements();
		while (e.hasMoreElements()) {
			String[] topic = (String[]) e.nextElement();
			revealResultTopic(topic[0], topicmapID, false, directives);		// doSelectTopic=false
		}
	}

	/**
	 * @see		#initContainer
	 */
	private Vector queryFields(String queryElements) {
		Vector fields = new Vector();
		StringTokenizer st = new StringTokenizer(queryElements, ",");
		while (st.hasMoreTokens()) {
			fields.addElement(st.nextToken());
		}
		return fields;
	}
}
