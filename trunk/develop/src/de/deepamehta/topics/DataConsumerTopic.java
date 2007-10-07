package de.deepamehta.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.TopicInitException;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.Session;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;



/**
 * Abstract baseclass for application topics who can reveal topics and associations
 * from a {@link de.deepamehta.service.CorporateDatasource} on-the-fly .
 *
 * <H4>Active behavoir</H4>
 *
 * The active <I>initialization</I> behavoir of a <CODE>DataConsumerTopic</CODE> is
 * setting the associated <CODE>CorporateDatasource</CODE>.
 * <P>
 * The active <I>association type revealing</I> behavoir of a
 * <CODE>DataConsumerTopic</CODE> is checking which relation types exists by quering the
 * <CODE>CorporateDatasource</CODE> based upon registered relation
 * specifications.
 * <P>
 * The active <I>topic revealing</I> behavoir of a <CODE>DataConsumerTopic</CODE> is
 * quering the <CODE>CorporateDatasource</CODE> for elements based upon a
 * registered relation specification and creating corresponding topics and associations
 * in corporate memory. The properties of each element are replicated as topic properties.
 *
 * <H4>Note to application programmers</H4>
 *
 * <CODE>DataConsumerTopic</CODE> is an
 * <I>abstract</I> class -- it provides the behavoir described above without "knowing" an
 * actual relation specification. The actual relation specifications are provided by
 * active topics derived from <CODE>DataConsumerTopic</CODE>. They will register one or
 * more relation specifications as theirs <I>initialization</I> behavoir. Generally there
 * are 2 types of relations: <I>"to one"</I> and <I>"to many"</I>.
 * <P>
 * E.g. the {@link de.deepamehta.movies.topics.MovieTopic} registers a "to many" relation to specify how to retrieve
 * the related actors for the respective movie.
 * <P>
 * <HR>
 * Last functional change: 7.6.2005 (2.0b6)<BR>
 * Last documentation update: 12.11.2000 (2.0a7-pre2)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public abstract class DataConsumerTopic extends LiveTopic {



	// **************
	// *** Fields ***
	// **************



	/**
	 * The registered relations of this consumer.
	 * <P>
	 * Key: topic type ID (<CODE>String</CODE>)<BR>
	 * Value: Relation
	 */
	private Hashtable relations = new Hashtable();



	// *******************
	// *** Constructor ***
	// *******************



	public DataConsumerTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}



	// **********************
	// *** Defining Hooks ***
	// **********************



	// ------------------
	// --- Life Cycle ---
	// ------------------



	/**
	 * <P>
	 * <TABLE>
	 * <TR><TD><B>Called by</B></TD><TD><CODE>initLevel</CODE></TD></TR>
	 * <TR><TD>{@link de.deepamehta.service.ApplicationService#createLiveTopic}</TD><TD>1</TD></TR>
	 * <TR><TD>{@link de.deepamehta.service.ApplicationService#initTopic}</TD><TD>variable</TD></TR>
	 * <TR><TD>{@link de.deepamehta.service.ApplicationService#initTypeTopic}</TD><TD>3</TD></TR>
	 * </TABLE>
	 */
	public CorporateDirectives init(int initLevel, Session session) throws TopicInitException {
		CorporateDirectives directives = super.init(initLevel, session);
		//
		if (initLevel == INITLEVEL_2) {
			if (LOG_TOPIC_INIT) {
				System.out.println(">>> DataConsumerTopic.init(" + initLevel + "): " +
					this + " -- sets data source");
			}
			setDataSource(session, directives);		// defined in LiveTopic
		}
		//
		return directives;
	}



	// -----------------------------------
	// --- Revealing topics on-the-fly ---
	// -----------------------------------



	/**
	 * @see		de.deepamehta.service.ApplicationService#revealTopicTypes
	 */
	public Hashtable revealTopicTypes() throws DeepaMehtaException {
		// retrieve association types from corporate memory (storage layer)
		Hashtable topicTypes = super.revealTopicTypes();
		//
		if (relations.size() == 0) {
			// this data consumer has no registered active association types
			return topicTypes;
		}
		// error check
		if (dataSource == null) {
			throw new DeepaMehtaException("*** DataConsumerTopic.revealTopicTypes(): " + this +
				" has no data source (no association types revealed)");
		}
		//
		StringBuffer report = new StringBuffer(">>> DataConsumerTopic.revealTopicTypes(): " + this +
			" (" + relations.size() + " topic types registered) ...");
		//
		// go through registered association types and potentially add respective types
		try {
			Enumeration e = relations.elements();
			String[] typeEntry;
			while (e.hasMoreElements()) {
				Relation rel = (Relation) e.nextElement();
				String typeID = rel.topicType;
				report.append(" " + typeID + " ");
				// --- query the data source ---
				Vector topics;
				switch (rel.assocKind) {
				case ASSOC_1:
					String elementID = getProperty(rel.fieldName);
					topics = dataSource.queryElements(rel.entityName, "ID", elementID, true);	// ### "ID" is hardcoded
					break;
				case ASSOC_N:
					elementID = getProperty("ID");	// ### "ID" is hardcoded
					topics = dataSource.queryElements(rel.entityName, rel.fieldName, elementID, true);
					break;
				case ASSOC_X:
					topics = new Vector();
					Enumeration e2 = new StringTokenizer(getProperty(rel.fieldName), MULTIPLE_VALUE_DELIMITER);
					while (e2.hasMoreElements()) {					
						topics.add(e2.nextElement());
					}
					break;
				default:
					throw new DeepaMehtaException("unexpected relation kind: " + rel.assocKind);
				}
				//
				int found = topics.size();
				if (found > 0) {
					// add association type
					report.append("(+)");
					topicTypes.put(typeID, Integer.toString(found));
					// Note: if the type has been already revealed from corporate memory it is overridden to let
					// the number of instances (as shown in the navigation menu) reflect the state of the datasource
				} else {
					report.append("(-)");
				}
			}
			System.out.println(report);
			return topicTypes;
		} catch (Exception e) {
			throw new DeepaMehtaException("*** DataConsumerTopic.revealTopicTypes(): " +
				"topic types of " + this + " not revealed (" + e + ")", e);
		}
	}

	/**
	 * Overridden to extend the corporate memory on-the-fly by retrieving elements from a datasource.
	 *
	 * @see		de.deepamehta.topics.LiveTopic#navigateByTopictype
	 */
	public void revealRelatedTopics(String topicTypeID) throws DeepaMehtaException {
		Relation rel = getRelation(topicTypeID);
		if (rel == null) {
			// generic association types require no dynamic revealing ### think about
			if (topicTypeID != "at-generic") {
				System.out.println("*** DataConsumerTopic.revealRelatedTopics(): related topic type of " + this +
					" via \"" + topicTypeID + "\" not registered");
			}
			return;
		}
		// error check
		if (dataSource == null) {
			throw new DeepaMehtaException("*** DataConsumerTopic.revealRelatedTopics(): " + this +
				" has no data source (no related topics revealed)");
		}
		//
		System.out.print("> DataConsumerTopic.revealRelatedTopics(): related topics of " +
			this + " via \"" + topicTypeID + "\" ... ");
		//
		try {
			String elementID;
			switch (rel.assocKind) {
			case ASSOC_1:
				System.out.println("1 (:1)");
				// potentially create new topic (and corresponding association) in
				// corporate memory
				elementID = getProperty(rel.fieldName);
				createTopicFromElement(elementID, rel);
				//
				break;
			case ASSOC_N:
				// query the data source
				String selfID = getProperty("ID");
				Vector foundElements = dataSource.queryElements(rel.entityName, rel.fieldName, selfID, true);
				int found = foundElements.size();
				if (found > 0) {
					System.out.println(found + " (:N)");
					Enumeration e = foundElements.elements();
					// go through found elements
					while (e.hasMoreElements()) {
						Hashtable partData = (Hashtable) e.nextElement();
						// potentially create new topic (and corresponding association) in corporate memory
						elementID = (String) partData.get(rel.relatedFieldName);
						createTopicFromElement(elementID, rel);
					}
				} else {
					System.out.println("*** none");
				}
				break;
			case ASSOC_X:
				String ids = getProperty(rel.fieldName);
				Enumeration e2 = new StringTokenizer(ids, MULTIPLE_VALUE_DELIMITER);
				while (e2.hasMoreElements()) {
					elementID = (String) e2.nextElement();
					createTopicFromElement(elementID, rel);
				}
				break;				
			}
		} catch (Exception e) {
			throw new DeepaMehtaException("*** DataConsumerTopic.revealRelatedTopics():" +
				" related topics of " + this + " not revealed (" + e + ")", e);
		}
	}



	// ***************
	// *** Methods ***
	// ***************



	/**
	 * ### to be dropped
	 */
	protected final void registerRelation1(String assocType, String entityName, String fieldName,
									 String topicType, String relatedElementType, String nameField) {
		relations.put(topicType, new Relation(ASSOC_1, null, assocType, entityName, fieldName,
			null, topicType, relatedElementType, nameField));	// relatedFieldName=null
	}

	/**
	 * ### to be dropped
	 */
	protected final void registerRelationN(String elementType, String assocType, String entityName, String fieldName,
									 String relatedFieldName,
									 String topicType, String relatedElementType, String nameField) {
		relations.put(topicType, new Relation(ASSOC_N, elementType, assocType, entityName, fieldName,
			relatedFieldName, topicType, relatedElementType, nameField));
	}

	/**
	 * ### to be dropped
	 */
	protected final void registerRelationX(String assocType, String entityName, String fieldName,
									 String topicType, String relatedElementType, String nameField) {
		relations.put(topicType, new Relation(ASSOC_X, null, assocType, entityName, fieldName,
			null, topicType, relatedElementType, nameField));	// relatedFieldName=null
	}

	// ---

	protected final Vector getRelatedElements(String topicTypeID) {
		try {
			Relation rel = getRelation(topicTypeID);
			String selfID = getProperty("ID");
			Vector elements = dataSource.queryElements(rel.elementType, selfID, rel.relatedElementType, rel.entityName);
			return elements;
		} catch (Exception e) {
			throw new DeepaMehtaException("related \"" + topicTypeID + "\" topics of " + this + " can't be retrieved (" + e + ")");
		}
	}

	protected final int getRelatedElementCount(String topicTypeID) {
		try {
			Relation rel = getRelation(topicTypeID);
			String selfID = getProperty("ID");
			int count = dataSource.getElementCount(rel.elementType, selfID, rel.relatedElementType, rel.entityName);
			//
			return count;
		} catch (Exception e) {
			throw new DeepaMehtaException("related \"" + topicTypeID + "\" topics of " + this + " can't be retrieved (" + e + ")");
		}
	}



	// ***********************
	// *** Private Methods ***
	// ***********************


	private Relation getRelation(String topicTypeID) {
		return (Relation) relations.get(topicTypeID);
	}

	/**
	 * @see		#revealRelatedTopics
	 */
	private void createTopicFromElement(String elementID, Relation rel) throws Exception {
		// ### compare to ElementContainerTopic.createTopicFromElement()
		// build identifier as topic id
		String topicID = createTopicID(rel.relatedElementType, elementID);
		// check if the element is already in corporate memory
		// Note: no version number is involved in the existence check
		if (!cm.topicExists(topicID)) {
			// get the element data
			Hashtable elementData = dataSource.queryElement(rel.relatedElementType, elementID);
			if (elementData == null) {
				System.out.println("*** DataConsumerTopic.createTopicFromElement(): \"" + rel.relatedElementType +
					"\" with ID \"" + elementID + " not fetched from datasource -- no topic created in corporate memory");
				return;
			}
			//
			String name = topicName(elementData, rel.nameField);	// ### there is new naming policy
			// ### create new topic in corporate memory
			String typeID = rel.topicType;
			cm.createTopic(topicID, 1, typeID, 1, name);			// ### must create live topic instead
			// --- trigger makeProperties() hook ---
			Hashtable props = as.triggerMakeProperties(typeID, elementData);
			cm.setTopicData(topicID, 1, props);
		}
		// check if the association is already in corporate memory
		// ### Note: the direction is ignored
		if (!cm.associationExists(getID(), topicID, true)) {
			// create new association in corporate memory
			String assocID = cm.getNewAssociationID();
			cm.createAssociation(assocID, 1, rel.assocType, 1, getID(), 1, topicID, 1);		// ### versions
		}
	}

	/**
	 * Creates a unique topic ID for an element.
	 */
	protected String createTopicID(String elementType, String elementID) {
		// >>> compare to ElementContainerTopic.createTopicID()
		return "t-" + elementType + "-" + elementID;
	}



	// *******************
	// *** Inner Class ***
	// *******************



	/**
	 * ### to be dropped
	 * <P>
	 * A registered relation.
	 * <P>
	 * [### explain]
	 */
	private class Relation {

		// *** Fields ***

		int assocKind;					// ASSOC_1, ASSOC_N or ASSOC_X
		String elementType;				// the source element type
		String assocType;
		String entityName;
		String fieldName;
		String relatedFieldName;		// only used for N:M
		String topicType;				// related topic type
		String relatedElementType;		// related element type
		String nameField;

		// *** Constructor ***

		Relation(int assocKind, String elementType, String assocType, String entityName, String fieldName,
				 String relatedFieldName, String topicType, String relatedElementType, String nameField) {
			this.assocKind = assocKind;
			this.elementType = elementType;
			this.assocType = assocType;
			this.entityName = entityName;
			this.fieldName = fieldName;
			this.relatedFieldName = relatedFieldName;
			this.topicType = topicType;
			this.relatedElementType = relatedElementType;
			this.nameField = nameField;
		}
	}
}
