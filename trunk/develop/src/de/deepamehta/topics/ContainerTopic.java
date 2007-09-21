package de.deepamehta.topics;

import de.deepamehta.BaseAssociation;
import de.deepamehta.BaseTopic;
import de.deepamehta.Commands;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.Detail;
import de.deepamehta.PresentableAssociation;
import de.deepamehta.TopicInitException;
import de.deepamehta.movies.topics.MovieContainerTopic;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateCommands;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.Session;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;



/**
 * ### A <CODE>ContainerTopic</CODE> is an active topic that acts as a container for other
 * topics. All topics contained in a container have the same topic type. The topics
 * contained in a container are represented by means of a query that
 * is associated with that container. The property editor of a <CODE>ContainerTopic</CODE>
 * serves as input form by which the user can filter the topics contained in the
 * container. The empty query represents the "null" filter and retrieves all contained
 * topics.
 * <P>
 * The query associated with a container is immutable, that is, once the
 * <CODE>ContainerTopic</CODE> is initialized, its associated query will never change.
 * <P>
 * The user can narrow the query by filling in additional properties. If the resulting
 * set of topics is still too big to be viualized, the container will create (and
 * aggregate) another (sub)container.
 *
 * <H4>Active behavoir</H4>
 *
 * The active <I>initialization</I> behavoir of a <CODE>ContainerTopic</CODE> is
 * setting the associated query.
 * <P>
 * The active <I>labeling</I> behavoir of a <CODE>ContainerTopic</CODE> is
 * showing the number of elements contained in the container.
 * <P>
 * The active <I>property disabling</I> behavoir of a <CODE>ContainerTopic</CODE> is
 * disabling all properties that are involved in the associated query -- this way the
 * user is prevented to change the associated query, only narrowing is possible.
 *
 * <H4>Note to application programmers</H4>
 *
 * <CODE>ContainerTopic</CODE> is an abstract class -- it provides the behavoir described
 * above without "knowing" the origin of the contained topics. Actually there are two
 * subclasses of <CODE>ContainerTopic</CODE>:
 *
 * <OL>
 * <LI><CODE>TopicContainerTopic</CODE>
 * <P>
 * A {@link TopicContainerTopic} represents a set of topics of a specific topic type
 * existing in corporate memory.
 * <P>
 * For every topic type the user creates interactively, the corresponding type that
 * represents the container for the new topic type, is created automatically as a subclass
 * of <CODE>TopicContainerTopic</CODE> (this is active behavoir of a
 * {@link TopicTypeTopic}).
 * <P>
 * An application programmer usually will not derive an active topic from
 * <CODE>TopicContainerTopic</CODE>.
 * <P>
 * <LI><CODE>ElementContainerTopic</CODE>
 * <P>
 * An {@link ElementContainerTopic} represents a set of elements of a specific type
 * existing in a {@link de.deepamehta.service.CorporateDatasource}.
 * An <CODE>ElementContainerTopic</CODE> provides the behavoir of creating topics in
 * corporate memory based upon elements of a corporate datasource. The elements attributes
 * are replicated in form of topic properties.
 * <P>
 * For the time being to use an <CODE>ElementContainerTopic</CODE> the application
 * programmer is required to subclass <CODE>ElementContainerTopic</CODE> in order to
 * specify the element access, topic creation and attribute replication.
 * <P>
 * E.g. the {@link MovieContainerTopic} specifies the topic types used for created
 * topics and containers as well as the attribute used for the name of created topics.
 * <P>
 * In the future the user resp. administrator will be able to provide these essential
 * information directly in "Design"-mode without needing deploying an application
 * programmer.
 * </OL>
 * <P>
 * <HR>
 * Last functional change: 11.9.2007 (2.0b8)<BR>
 * Last documentation update: 13.3.2001 (2.0a10-pre1)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public abstract class ContainerTopic extends LiveTopic {



	// **************
	// *** Fields ***
	// **************



	/**
	 * ### The query represented by this container.
	 * The containers query is immutable -- once set it will not change over the lifetime
	 * of the container.
	 * <P>
	 * ### The containers query is determined by both, the (visible) containers properties
	 * and the (hidden) "QueryElements" property. The visible properties stores the last
	 * query that was submitted to this container. The hidden "QueryElements" property
	 * containes a space or comma separated list of the property names involved in the
	 * containers query.
	 * <P>
	 * <TABLE>
	 * <TR><TD><B>Set once by</B></TD></TR>
	 * <TR><TD>{@link #initContainer}</TD></TR>
	 * <TR><TD><B>Accessed by</B></TD></TR>
	 * <TR><TD>{@link #equalsQuery}</TD></TR>
	 * <TR><TD>{@link #executeCommand}</TD></TR>
	 * <TR><TD>{@link ElementContainerTopic#getContent}</TD></TR>
	 * <TR><TD>{@link ElementContainerTopic#autoSearch}</TD></TR>
	 * </TABLE>
	 */
	protected Hashtable containerPropertyFilter = new Hashtable();

	/**
	 * Name filter.
	 * <P>
	 * Note: Remains uninitialized if no name filter is set for this container
	 */
	protected String containerNameFilter;

	/**
	 * The topic to which an association must exists to the the topics of this container.
	 * <P>
	 * Initialized by {@link #initContainer}.<BR>
	 * Note: Remains uninitialized if no relation filter is set for this container
	 */
	protected BaseTopic containerRelatedTopic;

	protected String relatedTopicSemantic;

	// ---

	/**
	 * The names of the properties involved in the query represented by this container.
	 * <P>
	 * The property names are determined by the value of this containers "QueryElements" property.
	 * <P>
	 * Initialized by {@link #initContainer}.<BR>
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
				System.out.println(">>> ContainerTopic.init(" + initLevel + "): " + this +
					" -- name filter: \"" + containerNameFilter + "\" property filer: " +
					containerPropertyFilter + " disabled properties: " +
					disabledProperties + " related topic: " + containerRelatedTopic);
			}
		}
		//
		return directives;
	}

	/* ### public CorporateDirectives evoke(Session session, String topicmapID,
																String viewmode) {
		return super.evoke(session, topicmapID, viewmode);
		// ### the plural typename of a topic type is not known in evoke().
		// ### the plural typename of a topic type is initialized at level 3 but
		// ### only level 1 has been performed _before_ evoke() is called.
		//
		// ### the version number is set to 1
		TopicTypeTopic type = (TopicTypeTopic) as.getLiveTopic(getContentTypeID(), 1);
		CorporateDirectives directives = new CorporateDirectives();
		directives.add(DIRECTIVE_SET_TOPIC_NAME, getID(), type.getPluralName(),
			new Integer(1));
		return directives;
	} */



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
		// "Show Result"
		Commands cmdGroup = commands.addCommandGroup(as.string(ITEM_SHOW_CONTENT), FILESERVER_IMAGES_PATH, ICON_SHOW_RESULT);
		if (getContentSize() <= MAX_LISTING) {
			Enumeration e = getContent().elements();
			while (e.hasMoreElements()) {
				String[] topic = (String[]) e.nextElement();
				cmdGroup.addCommand(topic[1], CMD_SHOW_CONTENT + COMMAND_SEPARATOR + topic[0],
					FILESERVER_ICONS_PATH, getAppearance(topic[0], topic[1], session, directives));
			}
		}
		// "Group by"
		String[] groupingProps = getGroupingProperties();
		if (groupingProps != null) {
			cmdGroup = commands.addCommandGroup(as.string(ITEM_GROUP_BY), FILESERVER_IMAGES_PATH, ICON_GROUP_BY);
			for (int i = 0; i < groupingProps.length; i++) {
				cmdGroup.addCommand(groupingProps[i], CMD_GROUP_BY + COMMAND_SEPARATOR + groupingProps[i]);
			}
		}
		// "Remove"
		commands.addSeparator();
		commands.addDeleteTopicCommand(as.string(ITEM_REMOVE_TOPIC), FILESERVER_IMAGES_PATH, ICON_HIDE_TOPIC);
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
			// ### let the superclass show the topic detail
			// directives.add(super.executeCommand(command, session));
			return directives;
		} else if (cmd.equals(CMD_SUBMIT_FORM)) {
			// filtering
			Hashtable containerProps = getProperties();
			String nameFilter = (String) containerProps.get(PROPERTY_SEARCH);
			return processQuery(nameFilter, queryProperties(containerProps), null, null, topicmapID);
		} else if (cmd.equals(CMD_SHOW_CONTENT)) {
			// reveal one topic
			CorporateDirectives directives = new CorporateDirectives();
			String id = st.nextToken();
			String topicID = revealTopic(id, topicmapID, directives);
			// reveal association(s) if relation filter is set
			if (containerRelatedTopic != null) {
				Vector assocs = cm.getAssociations(topicID, containerRelatedTopic.getID(), true);	// ignoreDirection=true
				Enumeration e = assocs.elements();
				while (e.hasMoreElements()) {
					BaseAssociation assoc = (BaseAssociation) e.nextElement();
					directives.add(DIRECTIVE_SHOW_ASSOCIATION, new PresentableAssociation(assoc));
				}
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



	public Vector disabledProperties(Session session) {
		return disabledProperties;
	}



	// ----------------------------------------
	// --- Providing additional topic label ---
	// ----------------------------------------



	public String getLabel() {
		return getProperty("ElementCount");
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
	 * References checked: 27.11.2004 (2.0b4)
	 *
	 * @see		#executeCommand
	 * @see		TopicMapTopic#searchByTopicType
	 */
	public CorporateDirectives triggerQuery(String topicmapID) {
		return processQuery(containerNameFilter, containerPropertyFilter, containerRelatedTopic, relatedTopicSemantic, topicmapID);
	}

	/**
	 * @return	<CODE>true</CODE> if the specified query is the same as represented by
	 *			this container, <CODE>false</CODE> otherwise.
	 *
	 * @see		ApplicationService#createNewContainer
	 */
	public boolean equalsQuery(String nameFilter, Hashtable propertyFilter, String relatedTopicID,
																			String relTopicSemantic) {
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
	// ### Since CM 2.25 this is not needed to abstract anymore, see TopicContainerTopic.setContainerType()
	// ### think again about element container (see above)
	protected abstract String getContentType();

	// Topic type ID of the content topics
	//
	// ### Since CM 2.25 this is not needed to abstract anymore, see TopicContainerTopic.setContainerType()
	protected abstract String getContentTypeID();

	/**
	 * Returns a list of all topics contained in this container.
	 * 
	 * @return	all topics of this container as vector of 2-element <CODE>String</CODE>
	 *			arrays:<BR>
	 *			element 1: ID<BR>
	 *			element 2: topic name
	 *
	 * @see		#contextCommands
	 * @see		#getDetail
	 */
	protected abstract Vector getContent();

	protected abstract int getContentSize();

	/**
	 * @see		#contextCommands
	 */
	protected abstract String getAppearance(String id, String name,
								Session session, CorporateDirectives directives);

	/**
	 * @see		#triggerQuery
	 * @see		#executeCommand
	 */
	protected abstract CorporateDirectives processQuery(String nameFilter, Hashtable propertyFilter,
												BaseTopic relatedTopic, String relatedTopicSemantic, String topicmapID);

	protected abstract CorporateDirectives autoSearch(String groupingProperty);

	// ### in case of a topic container "id" is a topic ID
	// ### in case of an element container "id" is an element ID
	protected abstract String revealTopic(String id, String topicmapID, CorporateDirectives directives);



	// *************************
	// *** Protected Methods ***
	// *************************



	protected String[] getGroupingProperties() {
		return null;
	}

	// ---

	/**
	 * ### Transforms container properties into a raw query.
	 * <P>
	 * ### A raw query contains all properties involved in the actual query.
	 * <P>
	 * ### Active topics have the opportunity to modify the properties before
	 * the topics are retrieved from the source.
	 * <P>
	 * ### This is done by removing the properties for internal use
	 * (<CODE>QueryElements</CODE>, <CODE>ElementCount</CODE>,
	 * <CODE>RelatedTopicID</CODE>, <CODE>Topic Name</CODE>)
	 * as well as empty properties.
	 *
	 * @see		#executeCommand
	 */
	protected Hashtable queryProperties(Hashtable containerProperties) {
		// The topic properties of a container acting as query values (and the property
		// editor acts as query form).
		//
		// --- remove internal used properties ---
		containerProperties.remove("QueryElements");
		containerProperties.remove("ElementCount");
		containerProperties.remove(PROPERTY_RELATED_TOPIC_ID);
		containerProperties.remove(PROPERTY_RELATED_TOPIC_SEMANTIC);
		containerProperties.remove(PROPERTY_SEARCH);
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
		this.disabledProperties = queryFields(getProperty("QueryElements"));
		// --- containerRelatedTopic, relatedTopicSemantic ---
		// Note: only initialized if a relation filter is used
		try {
			String relTopicID = getProperty(PROPERTY_RELATED_TOPIC_ID);
			if (!relTopicID.equals("")) {
				// ### topic must be loaded
				this.containerRelatedTopic = as.getLiveTopic(relTopicID, 1, session, directives);
				String relTopicSemantic = getProperty(PROPERTY_RELATED_TOPIC_SEMANTIC);
				if (!relTopicSemantic.equals("")) {
					this.relatedTopicSemantic = relTopicSemantic;
				}
			}
		} catch (DeepaMehtaException e) {
			String msg = "Container " + this + " is obsolete";
			throw new TopicInitException(msg);
			// ### directives.add(DIRECTIVE_SHOW_MESSAGE, msg, new Integer(NOTIFICATION_WARNING));
			// ### System.out.println("*** ContainerTopic.initContainer(): " + msg);
		}
		// --- containerPropertyFilter, containerNameFilter ---
		Enumeration propNames = disabledProperties.elements();
		Hashtable containerProps = getProperties();
		while (propNames.hasMoreElements()) {
			String propName = (String) propNames.nextElement();
			String propValue = (String) containerProps.get(propName);
			if (propName.equals(PROPERTY_SEARCH)) {		// ###
				this.containerNameFilter = propValue;
			} else {
				if (propValue != null) {
					this.containerPropertyFilter.put(propName, propValue);
				} else {
					System.out.println("*** ContainerTopic.initContainer(): " + this + ": search property \"" +
						propName + "\" has no value -- the property filter will not work properly");
					directives.add(DIRECTIVE_SHOW_MESSAGE, "Search property \"" + propName + "\" of " + this +
						" has no value -- the property filter will not work properly",
						new Integer(NOTIFICATION_WARNING));
				}
			}
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
