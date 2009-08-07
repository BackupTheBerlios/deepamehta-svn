package de.deepamehta.service;

import de.deepamehta.BaseAssociation;
import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.PresentableAssociation;
import de.deepamehta.PresentableTopic;
import de.deepamehta.PresentableType;
import de.deepamehta.assocs.LiveAssociation;
import de.deepamehta.service.db.DatabaseProvider;
import de.deepamehta.service.db.OracleDatabaseProvider;
import de.deepamehta.topics.LiveTopic;
import de.deepamehta.util.CaseInsensitveHashtable;
import de.deepamehta.util.DeepaMehtaUtils;

import java.awt.Point;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;



/**
 * A RDBMS implementation of {@link CorporateMemory}.
 * <p>
 * <hr>
 * Last change: 22.9.2008 (2.0b8)<br>
 * J&ouml;rg Richter<br>
 * jri@deepamehta.de
 */
class RelationalCorporateMemory implements CorporateMemory, DeepaMehtaConstants {



	// *****************
	// *** Constants ***
	// *****************



	private static final int IMPLIED_NO = 1;
	private static final int IMPLIED_1 = 2;		// default
	private static final int IMPLIED_N = 3;



	// **************
	// *** Fields ***
	// **************



	/**
	 * The connection to the database.
	 */
	private DatabaseProvider provider;

	private String dbmsHint;



	// *******************
	// *** Constructor ***
	// *******************



	/**
	 * @see		ApplicationServiceInstance#createCorporateMemory
	 */
	RelationalCorporateMemory(DatabaseProvider dbProvider) throws Exception {
		provider=dbProvider;
		// new DatabaseSweeper(provider).sweep();	// ### temporarily switched off
		provider.getDatabaseOptimizer().optimize();

		this.dbmsHint = dbProvider.getDbmsHint();
		System.out.println(">    DBMS hint: \"" + dbmsHint + "\"");
	}

	
	
	// *************************************************************************
	// *** Implementation of Interface de.deepamehta.service.CorporateMemory ***
	// *************************************************************************



	// -------------------------
	// --- Retrieving Topics ---
	// -------------------------



	// --- getTopic (2 forms) ---

	public BaseTopic getTopic(String id, int version) {
		Vector topics = queryBaseTopics("SELECT * FROM Topic WHERE ID = ? AND Version = ?", new Object[] {id, i(version)});
		if (topics.size() == 0) {
			return null;	// ### throw instead
		}
		BaseTopic topic = (BaseTopic) topics.firstElement();
		if (topics.size() > 1) {
			System.out.println("*** RelationalCorporateMemory.getTopic(): there are " +
				"more than one topics -- only " + topic + " is respected");
		}
		return topic;
	}

	/**
	 * @see		ApplicationService#loginCheck(String username, String password)
	 */
	public BaseTopic getTopic(String typeID, String name, int topicVersion) {
		Vector topics = queryBaseTopics("SELECT * FROM Topic WHERE " +
			"TypeID= ? AND Name= ? AND Version= ?",
			new Object[]{typeID,name, i(topicVersion)});
		if (topics.size() == 0) {
			return null;	// ### throw instead
		}
		BaseTopic topic = (BaseTopic) topics.firstElement();
		if (topics.size() > 1) {
			System.out.println("*** RelationalCorporateMemory.getTopic(): there are " +
				"more than one topics -- only " + topic + " is respected");
		}
		return topic;
	}

	// --- getTopics (13 forms) ---

	public Vector getTopics() {
		String query = "SELECT * FROM Topic";
		return queryBaseTopics(query, new Object[0]);
	}

	/**
	 * @see		ApplicationService#getAllTopics(String typeID, int x, int y)
	 */
	public Vector getTopics(String typeID) {
		return queryBaseTopics(topicQuery(typeID));
	}

	public Vector getTopics(String typeID, String nameFilter) {
		return queryBaseTopics(topicQuery(typeID, nameFilter));
	}

	public Vector getTopics(String typeID, Hashtable propertyFilter) {
		return getTopics(typeID, null, propertyFilter);
	}

	public Vector getTopics(String typeID, Hashtable propertyFilter, boolean caseSensitiv) {
		return getTopics(typeID, null, null, propertyFilter, null, null, null, caseSensitiv, false);
	}

	public Vector getTopics(String typeID, Hashtable propertyFilter, String topicmapID) {
		return getTopics(typeID, null, null, propertyFilter, null, null, topicmapID, false, false);	// caseSensitiv=false
	}

	public Vector getTopics(String typeID, Hashtable propertyFilter, String topicmapID, boolean caseSensitiv) {
		return getTopics(typeID, null, null, propertyFilter, null, null, topicmapID, caseSensitiv, false);
	}

	/**
	 * @see		de.deepamehta.topics.TopicContainerTopic#getContent
	 * @see		de.deepamehta.topics.TopicContainerTopic#performQuery
	 */
	public Vector getTopics(String typeID, String nameFilter, Hashtable propertyFilter) {
		return getTopics(typeID, nameFilter, propertyFilter, null);
	}

	public Vector getTopics(String typeID, String nameFilter, Hashtable propertyFilter, String relatedTopicID) {
		return getTopics(typeID, nameFilter, propertyFilter, relatedTopicID, null);
	}

	public Vector getTopics(String typeID, String nameFilter, Hashtable propertyFilter,
															String relatedTopicID, String assocTypeID) {
		return getTopics(typeID, null, nameFilter, propertyFilter, relatedTopicID, assocTypeID, null, false, false);	// caseSensitiv=false
	}

	public Vector getTopics(String typeID, String nameFilter, Hashtable propertyFilter,
															String relatedTopicID, String assocTypeID, boolean sortByTopicName) {
		return getTopics(typeID, null, nameFilter, propertyFilter, relatedTopicID, assocTypeID, null, false, sortByTopicName);	// caseSensitiv=false
	}

	public Vector getTopics(Vector typeIDs) {
		String query = "SELECT * FROM Topic WHERE " + topicTypeFilter(typeIDs);
		return queryBaseTopics(query, new Object[0]);
	}

	public Vector getTopics(Vector typeIDs, Hashtable propertyFilter, boolean caseSensitiv) {
		return getTopics(null, typeIDs, null, propertyFilter, null, null, null, caseSensitiv, false);
	}

	// ---

	/**
	 * Private unified form of getTopics().
	 * <p>
	 * Note: either typeID or typeIDs must be set (to a non-<code>null</code> value).
	 *
	 * @param	typeID			the ID of the topic type to match
	 * @param	typeIDs			the IDs of the topic types to match, if a non-
	 *							<code>null</code> value is passed the value of the
	 *							<code>typeID</code> parameter is ignored
	 */
	private Vector getTopics(String typeID, Vector typeIDs, String nameFilter, Hashtable propertyFilter, String relatedTopicID,
											String assocTypeID, String topicmapID, boolean caseSensitiv, boolean sortByTopicName) {
		PreparedStatement query = topicQuery(typeID, typeIDs, nameFilter, propertyFilter, relatedTopicID, assocTypeID,
																				topicmapID, caseSensitiv, sortByTopicName);
		if (LOG_CM_QUERIES) {
			System.out.println("> RelationalCorporateMemory.getTopics(): typeID=\"" + typeID + "\" name filter=\"" +
				nameFilter + "\" property filter=" + propertyFilter + "\n\"" + query + "\"");
		}
		return queryBaseTopics(query);
	}

	// ---

	public Vector getTopicsByName(String nameFilter) {
		return queryBaseTopics("SELECT * FROM Topic WHERE Name LIKE ?", new Object[]{"%" + nameFilter + "%"});
	}

	/**
	 * Searches for all topics of any types, whose leastways one property value contain
	 * specified substring.
	 *
	 * @param	searchString		specified substring (property filter)
	 *
	 * @return	hashtable where:<br>
	 * 				<ul><li>key is BaseTopic 
	 * 				<li>value is name of property, which contains given substring</ul>
	 *
	 * @see		de.deepamehta.topics.CorporateSearchTopic#searchByPropertyValue
	*/
	public Hashtable getTopicsByProperty(String searchString) {
		String query = "SELECT Topic.*, PropName FROM Topic, TopicProp " +
			"WHERE TopicProp.PropValue LIKE '%" + searchString + "%' AND Topic.ID = TopicProp.TopicID";
		System.out.println(">>> getTopicsByProperty(): \"" + query + "\"");
		Hashtable result = new CaseInsensitveHashtable();
		try {
			Statement stmt = createStatement();
			ResultSet resultSet = stmt.executeQuery(query);
			while (resultSet.next()) {
				String topicID = resultSet.getString("ID");
				String topicName = resultSet.getString("Name");
				// Oracle workaround
				if (topicName == null) {
					topicName = "";
				}
				// ###
				result.put(topicID + ":" + resultSet.getString("PropName"),
					new BaseTopic(topicID, resultSet.getInt("Version"),
					resultSet.getString("TypeID"), resultSet.getInt("TypeVersion"), topicName));
			}
			stmt.close();
		} catch (SQLException e) {
			System.out.println(
				"*** RelationalCorporateMemory.getTopicsByProperty(): " + e + " -- nothing returned");
		}
		return result;
	}

	// --- getRelatedTopics (10 forms) ---

	/**
	 * @return	vector of {@link de.deepamehta.BaseTopic}
	 */
	public Vector getRelatedTopics(String topicID) {
		return queryBaseTopics("SELECT Topic.* FROM Topic, Association WHERE " +
			"Association.TopicID1=? AND Association.TopicID2 = Topic.ID OR " +
			"Association.TopicID2=? AND Association.TopicID1 = Topic.ID", new Object[]{topicID,topicID});
	}

	/**
	 * @return	vector of {@link de.deepamehta.BaseTopic}
	 */
	public Vector getRelatedTopics(String topicID, String assocType, int relTopicPos) {
		return getRelatedTopics(topicID, assocType, null, null, null, relTopicPos, null, null, null, null, null, false, false);
	}

	/* ###
	/ **
	 * @return	vector of {@link de.deepamehta.BaseTopic}
	 * /
	public Vector getRelatedTopics(String topicID, String assocType, String relTopicType) {
		return queryBaseTopics("SELECT Topic.* FROM Topic, Association WHERE " +
			"(Association.TopicID1='" + topicID + "' AND Association.TopicID2 = Topic.ID OR " +
			"Association.TopicID2='" + topicID + "' AND Association.TopicID1 = Topic.ID) AND " +
			"Topic.TypeID='" + relTopicType + "' AND Association.TypeID='" + assocType + "'");
	} */

	/**
	 * @return	vector of {@link de.deepamehta.BaseTopic}
	 */
	public Vector getRelatedTopics(String topicID, String assocType, String relTopicType, int relTopicPos) {
		return getRelatedTopics(topicID, assocType, null, relTopicType, null, relTopicPos, null, null, null,
			null, null, false, false);
	}

	/**
	 * @return	vector of {@link de.deepamehta.BaseTopic}
	 *
	 * @see		ApplicationService#getTopicTypes
	 * @see		ApplicationService#getAssociationTypes
	 */
	public Vector getRelatedTopics(String topicID, String assocType, String relTopicType,
									int relTopicPos, String assocProp, String propValue, boolean sortAssociations) {
		return getRelatedTopics(topicID, assocType, null, relTopicType, null, relTopicPos, null, assocProp, propValue,
			null, null, sortAssociations, false);	// topicmapID=null, sortTopicProps=null, descending=false
	}

	/**
	 * @return	vector of {@link de.deepamehta.BaseTopic}
	 */
	public Vector getRelatedTopics(String topicID, String assocType, String relTopicType,
									int relTopicPos, String[] sortTopicProps, boolean descending) {
		return getRelatedTopics(topicID, assocType, null, relTopicType, null, relTopicPos, null, null, null,
			null, sortTopicProps, false, descending);
	}

	/**
	 * @return	vector of {@link de.deepamehta.BaseTopic}
	 */
	public Vector getRelatedTopics(String topicID, String assocType, String relTopicType,
									int relTopicPos, boolean sortAssociations) {
		return getRelatedTopics(topicID, assocType, null, relTopicType, null, relTopicPos, null, null, null,
			null, null, sortAssociations, false);
	}

	/**
	 * @return	vector of {@link de.deepamehta.BaseTopic}
	 */
	public Vector getRelatedTopics(String topicID, String assocType, String relTopicType,
									int relTopicPos, Vector relTopicIDs, boolean sortAssociations) {
		return getRelatedTopics(topicID, assocType, null, relTopicType, null, relTopicPos, relTopicIDs, null, null,
			null, null, sortAssociations, false);
	}

	/**
	 * @return	vector of {@link de.deepamehta.BaseTopic}
	 */
	public Vector getRelatedTopics(String topicID, String assocType, Vector relTopicTypeIDs,
									int relTopicPos, boolean sortAssociations) {
		return getRelatedTopics(topicID, assocType, null, null, relTopicTypeIDs, relTopicPos, null, null, null,
			null, null, sortAssociations, false);
	}

	/**
	 * @return	vector of {@link de.deepamehta.BaseTopic}
	 *
	 * @see		ApplicationService#mimeTypeTopic
	 * @see		ApplicationService#applicationTopic
	 */
	public Vector getRelatedTopics(String topicID, String assocType, int relTopicPos, String topicmapID) {
		return getRelatedTopics(topicID, assocType, null, null, null, relTopicPos, null, null, null,
			topicmapID, null, false, false);
	}

	/**
	 * @return	vector of {@link de.deepamehta.BaseTopic}
	 *
	 * @see		ApplicationService#getWorkspaces
	 * @see		ApplicationService#workgroupMembers
	 */
	public Vector getRelatedTopics(String topicID, Vector assocTypes, int relTopicPos) {
		return getRelatedTopics(topicID, null, assocTypes, null, null, relTopicPos, null, null, null,
			null, null, false, false);
	}

	// ---

	/**
	 * Private unified form for 9 of latter 10 getRelatedTopics() forms.
	 * <p>
	 * Note: either assocType or assocTypes must be set (to a non-<code>null</code> value).
	 *
	 * @param	assocType		the ID of the association type to match
	 * @param	assocTypes		the IDs of the association types to match, if a non-
	 *							<code>null</code> value is passed the value of the
	 *							<code>assocType</code> parameter is ignored
	 * @param	relTopicType	the ID of the topic type to match, if <code>null</code>
	 *							all topic types are matching
	 * @param	relTopicTypeIDs	the IDs of topic types to match, if a non-
	 *							<code>null</code> value is passed the value of the
	 *							<code>relTopicType</code> parameter is ignored
	 * @param	relTopicPos		position nr of the related topic, must be <code>1</code> or <code>2</code>
	 * @param	relTopicIDs		ID filter. If <code>null</code> no ID filter is applied.
	 */
	private Vector getRelatedTopics(String topicID, String assocType, Vector assocTypes, String relTopicType, Vector relTopicTypeIDs,
									int relTopicPos, Vector relTopicIDs, String assocProp, String propValue, String topicmapID,
									String[] sortTopicProps, boolean sortAssociations, boolean descending) {
		StringBuffer queryBuffer = new StringBuffer();
		List queryParams=new ArrayList();
		queryBuffer.append("SELECT Topic.*");
		queryBuffer.append(sortAssociations ? ", AssociationProp2.PropValue" : "");
		queryBuffer.append(" FROM Topic INNER JOIN Association ON ");
		queryBuffer.append("Association.TopicID");
		queryBuffer.append(3 - relTopicPos);
		queryBuffer.append("= ?");
		queryParams.add(topicID);
		queryBuffer.append(" AND ");
		// association type condition
		if (assocTypes != null) {
			applyAssociationTypeFilter(assocTypes, queryBuffer, queryParams);
		} else {
			queryBuffer.append("Association.TypeID = ?");
			// ### Note: assocType is expected to be non-null
			queryParams.add(assocType);
		}
		queryBuffer.append(" AND ");
		queryBuffer.append("Association.TopicID");
		queryBuffer.append(relTopicPos);
		queryBuffer.append("=Topic.ID");
		// topic type condition
		if (relTopicTypeIDs != null) {
			queryBuffer.append(" AND ");
			applyTopicTypeFilter(relTopicTypeIDs, queryBuffer, queryParams);
		} else if (relTopicType != null) {
			queryBuffer.append(" AND Topic.TypeID = ?");
			queryParams.add(relTopicType);
		}
		// topic ID condition
		if (relTopicIDs != null) {
			queryBuffer.append(" AND ");
			applyTopicIdFilter(relTopicIDs, queryBuffer, queryParams);
		}
		if (topicmapID != null) {
			queryBuffer.append(" INNER JOIN ViewAssociation ON ");
			queryBuffer.append("ViewAssociation.ViewTopicID = ? AND ");
			queryParams.add(topicmapID);
			queryBuffer.append("ViewAssociation.AssociationID=Association.ID");
		}
		if (assocProp != null){
			queryBuffer.append(" INNER JOIN AssociationProp AssociationProp1 ON ");
            queryBuffer.append("AssociationProp1.AssociationID=Association.ID AND ");
            queryBuffer.append("AssociationProp1.PropName = ? AND ");
			queryParams.add(assocProp);
            queryBuffer.append("AssociationProp1.PropValue = ?");
			queryParams.add(propValue);
		}
		queryBuffer.append(sortClause(sortTopicProps, descending));
		if (sortAssociations){
			queryBuffer.append(" LEFT OUTER JOIN AssociationProp AssociationProp2 ON ");
			queryBuffer.append("AssociationProp2.AssociationID=Association.ID AND ");
			queryBuffer.append("AssociationProp2.PropName='Ordinal Number' ORDER BY AssociationProp2.PropValue");
		}
		// --- perform query ---
		// ### System.out.println(">>> " + query);
		return queryBaseTopics(queryBuffer.toString(),queryParams.toArray(), sortAssociations);
	}



	// -------------------------------
	// --- Retrieving Associations ---
	// -------------------------------



	// --- getAssociation (3 forms) ---

	public BaseAssociation getAssociation(String id, int version) {
		Vector assocs = queryBaseAssociations("SELECT * FROM Association WHERE ID='" + id + "' AND Version=" + version);
		if (assocs.size() == 0) {
			return null;
		}
		BaseAssociation assoc = (BaseAssociation) assocs.firstElement();
		if (assocs.size() > 1) {
			System.out.println("*** RelationalCorporateMemory.getAssociation(): there " +
				"are more than one associations -- only " + assoc + " is respected");
		}
		return assoc;
	}

	/**
	 * @see		ApplicationService#createPresentableAssociation
	 */
	public BaseAssociation getAssociation(String assocTypeID, String topicID1, String topicID2) {
		Vector assocs = queryBaseAssociations("SELECT * FROM Association WHERE TypeID='" + assocTypeID + "' AND " +
			"TopicID1='" + topicID1 + "' AND TopicID2='" + topicID2 + "'");
		if (assocs.size() == 0) {
			return null;
		}
		BaseAssociation assoc = (BaseAssociation) assocs.firstElement();
		if (assocs.size() > 1) {
			System.out.println("*** RelationalCorporateMemory.getAssociation(): there " +
				"are more than one associations -- only " + assoc + " is respected");
		}
		return assoc;
	}

	public BaseAssociation getAssociation(Vector assocTypeIDs, String topicID1, String topicID2) {
		Vector assocs = queryBaseAssociations("SELECT * FROM Association WHERE " +
			associationTypeFilter(assocTypeIDs) + " AND " +
			"TopicID1='" + topicID1 + "' AND TopicID2='" + topicID2 + "'");
		if (assocs.size() == 0) {
			return null;
		}
		BaseAssociation assoc = (BaseAssociation) assocs.firstElement();
		if (assocs.size() > 1) {
			System.out.println("*** RelationalCorporateMemory.getAssociation(): there " +
				"are more than one associations -- only " + assoc + " is respected");
		}
		return assoc;
	}

	// ---

	public Vector getAssociations() {
		return queryBaseAssociations("SELECT * FROM Association");
	}

	public Vector getAssociations(String typeID) {
		return queryBaseAssociations("SELECT * FROM Association WHERE TypeID='" + typeID + "'");
	}

	public Vector getAssociations(String topicID1, String topicID2, boolean ignoreDirection) {
		return queryBaseAssociations("SELECT * FROM Association WHERE " +
			"TopicID1='" + topicID1 + "' AND TopicID2='" + topicID2 + "'" + (ignoreDirection ?
			" OR TopicID1='" + topicID2 + "' AND TopicID2='" + topicID1 + "'" : ""));
	}

	// --- getRelatedAssociations (2 forms) ---

	/**
	 * @see		InteractionConnection#removeViewsInUse
	 *
	 * @return	vector of BaseAssociation
	 */
	public Vector getRelatedAssociations(String topicID, String assocType, int relTopicPos) {
		String query = "SELECT * FROM Association WHERE Association.TopicID" +
			(3 - relTopicPos) + "='" + topicID + "' AND Association.TypeID='" + assocType + "'";
		return queryBaseAssociations(query);
	}

	/**
	 * @see		ApplicationService#getAssociation
	 *
	 * @return	vector of BaseAssociation
	 */
	public Vector getRelatedAssociations(String topicID, String assocType, int relTopicPos,
																	String relTopicType) {
		String query = "SELECT Association.* FROM Association, Topic WHERE " +
			"Association.TopicID" + (3 - relTopicPos) + "='" + topicID + "' AND " +
			"Association.TopicID" + relTopicPos + "=Topic.ID AND Topic.TypeID='" +
			relTopicType + "' AND Association.TypeID='" + assocType + "'";
		return queryBaseAssociations(query);
	}

	// --- getTopicTypes (3 forms) ---

	/**
	 * Retrieves all topic types existing in corporate memory, sorted by topic name.
	 *
	 * @return	The topic types as vector of
	 *			{@link de.deepamehta.PresentableType}
	 */
	public Vector getTopicTypes() {
		return queryPresentableTypes(topicQuery(TOPICTYPE_TOPICTYPE, null, "Name"));
	}

	/**
	 * @see		LiveTopic#revealTopicTypes
	 */
	public Hashtable getTopicTypes(String topicID) {
		String query = "SELECT Topic.TypeID, Count(*) AS \"Count\" FROM Topic, Association " +
			"WHERE " +
		"(Association.TopicID1='" + topicID + "' AND Association.TopicID2=Topic.ID) OR " +
		"(Association.TopicID2='" + topicID + "' AND Association.TopicID1=Topic.ID) " +
			"GROUP BY Topic.TypeID";
		return queryHashtable(query, "TypeID", "Count");
	}

	public Hashtable getTopicTypes(String topicmapID, int version, String viewmode) {
		String query = "SELECT TypeID, Count(*) AS \"Count\" FROM Topic, ViewTopic " +
			"WHERE " +
			"ViewTopic.ViewTopicID='" + topicmapID + "' AND " +
			"ViewTopic.ViewTopicVersion=" + version + " AND " +
			"ViewTopic.TopicID=Topic.ID AND " +
			"ViewTopic.TopicVersion=Topic.Version " +
			"GROUP BY TypeID";
		return queryHashtable(query, "TypeID", "Count");
	}

	// --- getAssociationTypes (2 forms) ---

	/**
	 * Retrieves all association types existing in corporate memory.
	 *
	 * @return	The topic types as vector of
	 *			{@link de.deepamehta.PresentableType}
	 */
	public Vector getAssociationTypes() {
		return queryPresentableTypes(topicQuery(TOPICTYPE_ASSOCTYPE, null, "Name"));
	}

	public Hashtable getAssociationTypes(String topicID) {
		String query = "SELECT TypeID, Count(*) AS \"Count\" FROM Association WHERE " +
					   "(Association.TopicID1='" + topicID + "' OR " +
						"Association.TopicID2='" + topicID + "') GROUP BY TypeID";
		return queryHashtable(query, "TypeID", "Count");
	}

	// ---

	/**
	 * @see		LiveTopic#evoke
	 * @see		de.deepamehta.topics.UserTopic#evoke
	 * @see		de.deepamehta.topics.WorkspaceTopic#evoke
	 * @see		de.deepamehta.topics.DataConsumerTopic#createNewTopic(String id, de.deepamehta.topics.DataConsumerTopic.Relation rel)
	 * @see		de.deepamehta.topics.ElementContainerTopic#createNewContainer(String containerName, String containerType, String groupingField, String groupingValue, String queryElements, Hashtable formData, String foundStr)
	 */
	public void createTopic(String id, int version, String typeID, int typeVersion, String name) {
		if (LOG_CM) {
			System.out.println("> (#) " + typeID + ":" + typeVersion + " \"" +
				name + "\" (" + id + ":" + version + ")");
		}
		update("INSERT INTO Topic (ID, Version, TypeID, TypeVersion, Name) " +
				"VALUES (?, ?, ?, ?, ?)",
		       new Object[]{id, i(version), typeID, i(typeVersion), name});
	}

	/**
	 * @see		LiveAssociation#evoke
	 * @see		de.deepamehta.topics.UserTopic#evoke
	 * @see		de.deepamehta.topics.WorkspaceTopic#evoke
	 * @see		de.deepamehta.topics.DataConsumerTopic#createNewTopic
	 */
	public void createAssociation(String id, int version, String typeID, int typeVersion,
								String topicID1, int topicVersion1, String topicID2, int topicVersion2) {
		if (LOG_CM) {
			System.out.println("> (#) " + typeID + ":" + typeVersion + " (" +
				topicID1 + ":" + topicVersion1 + ", " +
				topicID2 + ":" + topicVersion2+ ")");
		}
		update("INSERT INTO Association (ID, Version, TypeID, TypeVersion, TopicID1, TopicVersion1, TopicID2, TopicVersion2, Name)" +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
				new Object[]{id, i(version), typeID, i(typeVersion), topicID1, i(topicVersion1), topicID2, i(topicVersion2), ""});
	}

	// ---

	/**
	 * @see		ApplicationService#changeTopicName
	 */
	public void changeTopicName(String topicID, int version, String name) {
		update("UPDATE Topic SET Name = ? WHERE ID = ? AND Version = ?",
		       new Object[]{name, topicID, i(version)});
	}

	/**
	 * @see		ApplicationService#changeTopicType
	 */
	public void changeTopicType(String topicID, int version, String typeID, int typeVersion) {
		update("UPDATE Topic SET TypeID = ?, TypeVersion = ? WHERE ID = ? AND Version = ?",
	       new Object[]{typeID, i(typeVersion), topicID, i(version)});
	}

	// ---

	/**
	 * @see		ApplicationService#changeTopicName
	 */
	public void changeAssociationName(String assocID, int version, String name) {
		update("UPDATE Association SET Name = ? WHERE ID = ? AND Version = ?",
	       new Object[]{name, assocID, i(version)});
	}

	/**
	 * @see		ApplicationService#changeAssociationType
	 */
	public void changeAssociationType(String assocID, int version, String typeID, int typeVersion) {
		update("UPDATE Association SET TypeID = ?, TypeVersion = ? WHERE ID = ? AND Version = ?",
	       new Object[]{typeID, i(typeVersion), assocID, i(version)});
	}

	// --- deleting ---

	public void deleteTopic(String topicID) {
		if (LOG_CM) {
			System.out.println("> (-) " + topicID);
		}
		//
		delete("DELETE FROM Topic WHERE ID='" + topicID + "'");
		// ### delete refering records
	}

	public void deleteAssociation(String assocID) {
		if (LOG_CM) {
			System.out.println("> (-) " + assocID);
		}
		delete("DELETE FROM Association WHERE ID='" + assocID + "'");
	}



	// -----------------------
	// --- existence check ---
	// -----------------------



	/**
	 * @see		de.deepamehta.topics.DataConsumerTopic#createNewTopic
	 * @see		de.deepamehta.topics.ContainerTopic#createNewTopic
	 */
	public boolean topicExists(String topicID) {
		return exists("SELECT ID FROM Topic WHERE ID='" + topicID + "'");
	}

	// --- associationExists (2 forms) ---

	/* ### not yet needed
	public boolean associationExists(String topicID1, String topicID2, String assocTypeID) {
		String query = "SELECT ID FROM Association WHERE TopicID1='" + topicID1 + "' AND TopicID2='" + topicID2 + "'" +
			" AND TypeID='" + assocTypeID + "'");
		return exists(query);
	} */

	public boolean associationExists(String topicID1, String topicID2, Vector assocTypeIDs) {
		String query = "SELECT ID FROM Association WHERE TopicID1='" + topicID1 + "' AND TopicID2='" + topicID2 + "'" +
			" AND " + associationTypeFilter(assocTypeIDs);
		return exists(query);
	}

	/**
	 * @see		ApplicationService#createPresentableAssociation
	 * @see		de.deepamehta.topics.DataConsumerTopic#createNewTopic
	 */
	public boolean associationExists(String topicID1, String topicID2, boolean ignoreDirection) {
		String query = "SELECT ID FROM Association WHERE TopicID1='" + topicID1 + "' AND TopicID2='" + topicID2 + "'";
		if (ignoreDirection) {
			query += " OR TopicID1='" + topicID2 + "' AND TopicID2='" + topicID1 + "'";
		}
		return exists(query);
	}



	// -------------
	// --- Views ---
	// -------------



	/**
	 * References checked: 15.5.2002 (2.0a15-pre1)
	 *
	 * @see		ApplicationService#loginCheck(String username, String password)
	 */
	public int getViewTopicVersion(String topicmapID, int topicmapVersion,
								   String viewMode, String topicID) {
		return queryVersion("SELECT TopicVersion FROM ViewTopic WHERE " +
			"ViewTopicID='" + topicmapID + "' AND " +
			"ViewTopicVersion=" + topicmapVersion + " AND " +
			"TopicID='" + topicID + "'", "TopicVersion");
	}

	/**
	 * References checked: 15.5.2002 (2.0a15-pre1)
	 *
	 * @see		ApplicationService#loginCheck(String username, String password)
	 * @see		ApplicationService#documentTypeTopic(String configurationMapID, String ext)
	 */
	public BaseTopic getViewTopic(String topicmapID, int topicmapVersion, String viewMode,
												String topicTypeID, String topicName) {
		Vector topics = queryBaseTopics("SELECT Topic.* FROM Topic, ViewTopic WHERE " +
			"ViewTopicID = ? AND " +
			"ViewTopicVersion = ? AND " +
			"TopicID=Topic.ID AND " +
			"Topic.TypeID = ? AND " +
			"Topic.Name = ?",
			new Object[]{topicmapID, i(topicmapVersion), topicTypeID, topicName});
		if (topics.size() == 0) {
			return null;
		}
		BaseTopic topic = (BaseTopic) topics.firstElement();
		if (topics.size() > 1) {
			System.out.println("*** RelationalCorporateMemory.getViewTopic(): there " +
				"are more than one " + topic + " topics -- only one returned");
		}
		return topic;
	}

	// --- getViewTopics (4 forms) ---

	/**
	 * @return 	Vector of {@link de.deepamehta.PresentableTopic}
	 *
	 * @see		ApplicationService#createUserView
	 */
	public Vector getViewTopics(String topicmapID, int version) {
		return getViewTopics(topicmapID, version, null, null);
	}

	public Vector getViewTopics(String topicmapID, int version, String typeID) {
		return getViewTopics(topicmapID, version, typeID, null);
	}

	public Vector getViewTopics(String topicmapID, int version, String typeID, String nameFilter) {
		String query = topicQuery(topicmapID, version, typeID, null, nameFilter);
		return queryPresentableTopics(query);
	}

	public Vector getViewTopics(String topicmapID, int version, Vector typeIDs) {
		String query = topicQuery(topicmapID, version, null, typeIDs, null);
		return queryPresentableTopics(query);
	}

	// ---

	/**
	 * @return 	Vector of {@link de.deepamehta.PresentableAssociation}
	 *
	 * @see		ApplicationService#createUserView
	 */
	public Vector getViewAssociations(String topicmapID, int version) {
		String query = associationQuery(topicmapID, version);
		return queryPresentableAssociations(query);
	}

	// ---

	public Vector getRelatedViewTopics(String topicmapID, int version, String topicID, String assocTypeID, String relTopicTypeID) {
		String query = topicQuery(topicmapID, version, topicID, relTopicTypeID, 0, assocTypeID);
		return queryPresentableTopics(query);
	}

	public Vector getRelatedViewTopics(String topicmapID, int version, String topicID, String assocTypeID, String relTopicTypeID,
																												int relTopicPos) {
		String query = topicQuery(topicmapID, version, topicID, relTopicTypeID, relTopicPos, assocTypeID);
		return queryPresentableTopics(query);
	}

	// --- getRelatedViewTopicsByTopictype (2 forms) ---

	/**
	 * ### Never called.
	 * <p>
	 * References checked: 22.9.2008 (2.0b8)
	 *
	 * @return	2-element array:<br>
	 *				element 1: vector of {@link de.deepamehta.PresentableTopic}<br>
	 *				element 2: vector of {@link de.deepamehta.PresentableAssociation}
	 */
	public Vector[] getRelatedViewTopicsByTopictype(String topicID, String relTopicType) {
		return getRelatedViewTopicsByTopictype(topicID, relTopicType, 0, null);
	}

	/**
	 * References checked: 22.9.2008 (2.0b8)
	 *
	 * @return	2-element array:<br>
	 *				element 1: vector of {@link de.deepamehta.PresentableTopic}<br>
	 *				element 2: vector of {@link de.deepamehta.PresentableAssociation}
	 *
	 * @see		LiveTopic#navigateByTopictype
	 */
	public Vector[] getRelatedViewTopicsByTopictype(String topicID, String relTopicType, int relTopicPos, String assocType) {
		// Note: there is no "topicVersion"/"assocTypeVersion" involved here
		String query = "SELECT " +
			"Topic.ID AS TopicID, " +
			"Topic.Version AS TopicVersion, " +
			"Topic.TypeID AS TopicTypeID, " +
			"Topic.TypeVersion AS TopicTypeVersion, " +
			"Topic.Name AS TopicName, " +
			"Association.* " +
			"FROM Topic, Association WHERE " + (relTopicPos == 0 ?
	   "(Association.TopicID1='" + topicID + "' AND Association.TopicID2=Topic.ID OR " +
		"Association.TopicID2='" + topicID + "' AND Association.TopicID1=Topic.ID) AND " :
		"Association.TopicID" + (3 - relTopicPos) + "='" + topicID + "' AND " +
		"Association.TopicID" + relTopicPos + "=Topic.ID AND " +
		"Association.TypeID='" + assocType + "' AND ") +
		"Topic.TypeID='" + relTopicType + "' ORDER BY Topic.Name";
		//
		return queryPresentableTopics(query, topicID);
	}

	// ---

	/**
	 * References checked: 22.9.2008 (2.0b8)
	 *
	 * @return	2-element array:<br>
	 *				element 1: vector of {@link de.deepamehta.PresentableTopic}<br>
	 *				element 2: vector of {@link de.deepamehta.PresentableAssociation}
	 *
	 * @see		LiveTopic#navigateByAssoctype
	 */
	public Vector[] getRelatedViewTopicsByAssoctype(String topicID, String assocType) {
		// Note: there is no "topicVersion"/"assocTypeVersion" involved here
		String query = "SELECT " +
			"Topic.ID AS TopicID, " +
			"Topic.Version AS TopicVersion, " +
			"Topic.TypeID AS TopicTypeID, " +
			"Topic.TypeVersion AS TopicTypeVersion, " +
			"Topic.Name AS TopicName, " +
			"Association.* " +
			"FROM Topic, Association WHERE " +
	   "(Association.TopicID1='" + topicID + "' AND Association.TopicID2 = Topic.ID OR " +
		"Association.TopicID2='" + topicID + "' AND Association.TopicID1 = Topic.ID) AND " +
		"Association.TypeID='" + assocType + "' ORDER BY Topic.Name";
	  	//
		return queryPresentableTopics(query, topicID);
	}

	// --- getTopicIDs (2 forms) ---

	/**
	 * Returns all IDs of topics of the specified type who are contained in the
	 * specified topicmap and viewmode.
	 *
	 * @return	Vector of <code>String</code>s.
	 *
	 * @see		ApplicationService#getAllTopics(String typeID, String topicmapID, String viemode)
	 */
	public Vector getTopicIDs(String typeID, String topicmapID) {
		return getTopicIDs(typeID, topicmapID, false);
	}

	public Vector getTopicIDs(String typeID, String topicmapID, boolean sortByTopicName) {
		return queryIDs(topicIDsQuery(typeID, topicmapID, 1, sortByTopicName));
	}

	// --- getAssociationIDs (2 forms) ---

	/**
	 * @return	Vector of <code>String</code>s.
	 *
	 * @see		ApplicationService#getAssociationIDs(String topicmapID, String viewMode, String userID, String topicID, int version, String assocType)
	 */
	public Vector getAssociationIDs(String topicID, int topicVersion) {
		return queryIDs(associationIDsQuery(topicID, topicVersion));
	}

	/**
	 * @return	Vector of <code>String</code>s.
	 *
	 * @see		ApplicationService#getAssociationIDs(String topicmapID, String viewMode, String userID, String topicID, int version, String assocType)
	 */
	public Vector getAssociationIDs(String topicID, int topicVersion, String topicmapID,
													int topicmapVersion, String viewmode) {
		return queryIDs(associationIDsQuery(topicID, topicVersion, topicmapID, topicmapVersion, viewmode));
	}

	// ---

	/**
	 * @see		ApplicationService#createViewTopic
	 */
	public void createViewTopic(String topicmapID, int topicmapVersion, String viewMode, String topicID,
											int topicVersion, int x, int y, boolean performExistenceCheck) {
		if (!performExistenceCheck || !viewTopicExists(topicmapID, topicmapVersion, viewMode, topicID)) {
			if (LOG_CM) {
				System.out.println("> (#) " + topicmapID + ":" + topicmapVersion + " " + topicID + ":" + topicVersion +
					" (" + x + ", " + y + ")");
			}
			update("INSERT INTO ViewTopic VALUES (?, ?, ?, ?, ?, ?)", new Object[]{topicmapID, i(topicmapVersion),
				topicID, i(topicVersion), i(x), i(y)});
		}
	}

	/**
	 * @see		ApplicationService#createViewAssociations
	 * @see		ApplicationService#createViewAssociation
	 */
	public void createViewAssociation(String topicmapID, int topicmapVersion, String viewMode, String assocID,
													int assocVersion, boolean performExistenceCheck) {
		if (!performExistenceCheck || !viewAssociationExists(topicmapID, topicmapVersion, viewMode, assocID)) {
			if (LOG_CM) {
				System.out.println("> (#) " + topicmapID + ":" + topicmapVersion + " " + assocID + ":" + assocVersion);
			}
			update("INSERT INTO ViewAssociation VALUES (?, ?, ?, ?)", new Object[]{topicmapID, i(topicmapVersion), assocID, i(assocVersion)});
		} else {
			// ### System.out.println("> RelationalCorporateMemory.createViewAssociation(): \"" +
			//	assocID + "\" already in view \"" + topicmapID + "\"");
		}
	}

	// --- deleteViewTopic (2 forms) ---

	/**
	 * @see		ApplicationService#deleteViewTopic(String topicID)
	 */
	public void deleteViewTopic(String topicID) {
		int c1 = delete("DELETE FROM ViewTopic WHERE TopicID='" + topicID + "'");
		if (LOG_CM) {
			System.out.println("> (-) " + c1 + " view topic deleted");
		}
		if (c1 == 0) {
			// ###
			System.out.println("*** RelationalCorporateMemory.deleteViewTopic(String): \"" +
				topicID + "\": " + c1 + " view topics deleted");
		}
	}

	/**
	 * @see		ApplicationService#deleteViewTopic(String topicmapID, String topicID)
	 */
	public void deleteViewTopic(String topicmapID, String viewMode, String topicID) {
		int c1 = delete("DELETE FROM ViewTopic WHERE ViewTopicID='" + topicmapID + "' AND TopicID='" + topicID + "'");
		if (LOG_CM) {
			System.out.println("> (-) " + c1 + " view topic deleted");
		}
		if (c1 != 1) {
			// ###
			System.out.println("*** RelationalCorporateMemory.deleteViewTopic(): \"" +
				topicID + "\": " + c1 + " view topics deleted");
		}
	}

	// --- deleteViewAssociation (2 forms) ---

	/**
	 * @see		ApplicationService#deleteViewAssociation
	 * @see		ApplicationService#deleteViewAssociations
	 */
	public void deleteViewAssociation(String assocID) {
		int c;
		c = delete("DELETE FROM ViewAssociation WHERE AssociationID='" + assocID + "'");
		if (LOG_CM) {
			System.out.println("> (-) " + c + " view association entry deleted");
		}
	}

	/**
	 * @see		ApplicationService#deleteViewAssociation
	 * @see		ApplicationService#deleteViewAssociations
	 */
	public void deleteViewAssociation(String topicmapID, String viewMode, String assocID) {
		int c;
		c = delete("DELETE FROM ViewAssociation WHERE ViewTopicID='" + topicmapID +
			"' AND AssociationID='" + assocID + "'");
		if (LOG_CM) {
			System.out.println("> (-) " + c + " view association entry deleted");
		}
		if (c != 1) {
			System.out.println("*** RelationalCorporateMemory.deleteViewAssociation():" +
				" \"" + assocID + "\": " + c + " view associations deleted");
		}
	}

	// ---

	/**
	 * @see		ApplicationService#deleteUserView
	 */
	public void deleteView(String topicmapID, int topicmapVersion) {
		// Note: the record from the "Topic" table representing the view (type tt-topicmap) is not
		// deleted here, but while LiveTopic.die()
		int c1 = delete("DELETE FROM ViewTopic WHERE ViewTopicID='" + topicmapID + "' AND " +
			"ViewTopicVersion=" + topicmapVersion);
		int c2 = delete("DELETE FROM ViewAssociation WHERE ViewTopicID='" + topicmapID + "' AND " +
			"ViewTopicVersion=" + topicmapVersion);
		if (LOG_CM) {
			System.out.println("> (-) " + c1 + " view topics, " + c2 + " view associations deleted");
		}
		// ### delete associations -- which?
	}

	// ---

	/**
	 * @see		ApplicationService#updateView
	 */
	public void updateView(String srcTopicmapID, int srcTopicmapVersion, String destTopicmapID, int destTopicmapVersion) {
		String setClause = " SET ViewTopicID = ?, ViewTopicVersion = ? WHERE ViewTopicID = ? AND ViewTopicVersion = ?";
		Object[] setParams = new Object[]{destTopicmapID, i(destTopicmapVersion), srcTopicmapID, i(srcTopicmapVersion)};
		int c1 = update("UPDATE ViewTopic" + setClause, setParams, IMPLIED_N);
		int c2 = update("UPDATE ViewAssociation" + setClause, setParams, IMPLIED_N);
		if (LOG_CM) {
			System.out.println("> (>) \"" + srcTopicmapID + "\"," + srcTopicmapVersion + " --> \"" + destTopicmapID +
				"\"," + destTopicmapVersion + ": " + c1 + " view topics, " + c2 + " view associations");
		}
	}

	/**
	 * References checked: 24.6.2008 (2.0b8)
	 *
	 * @see		ApplicationService#moveTopic
	 */
	public void updateViewTopic(String topicmapID, int topicmapVersion, String topicID, int x, int y) {
		update("UPDATE ViewTopic SET x = ?, y = ? WHERE ViewTopicID = ? AND ViewTopicVersion = ? AND TopicID = ?",
		       new Object[]{i(x), i(y), topicmapID, i(topicmapVersion), topicID});
	}

	// ---

	/**
	 * @see		#createViewTopic
	 */
	public boolean viewTopicExists(String topicmapID, int topicmapVersion, String viewMode, String topicID) {
		// note: there is no "topicVersion" involved here
		return exists("SELECT ViewTopicID FROM ViewTopic WHERE " +
			"ViewTopicID='" + topicmapID + "' AND " +
			"ViewTopicVersion=" + topicmapVersion + " AND " +
			"TopicID='" + topicID + "'");
	}

	// --- viewAssociationExists (3 forms) ---

	/**
	 * @see		#createViewAssociation
	 */
	public boolean viewAssociationExists(String topicmapID, int topicmapVersion, String viewMode, String assocID) {
		return exists("SELECT ViewTopicID FROM ViewAssociation WHERE " +
			"ViewTopicID='" + topicmapID + "' AND " +
			"ViewTopicVersion=" + topicmapVersion + " AND " +
			"AssociationID='" + assocID + "'");
	}

	/**
	 * ### still needed?
	 */
	public boolean viewAssociationExists(String topicmapID, String viewMode,
									 	 String assocType,
										 String topicID1, String topicID2) {
		return exists("SELECT ViewTopicID FROM ViewAssociation, Association WHERE " +
			"ViewAssociation.ViewTopicID='" + topicmapID + "' AND " +
			"ViewAssociation.AssociationID=Association.ID AND " +
			"Association.TypeID='" + assocType + "' AND " +
			"Association.TopicID1='" + topicID1 + "' AND " +
			"Association.TopicID2='" + topicID2 + "'");
	}

	/**
	 * @see		ApplicationService#isMemberOf
	 */
	public boolean viewAssociationExists(String topicmapID, String viewMode,
									 	 Vector assocTypes,
										 String topicID1, String topicID2) {
		return exists("SELECT ViewTopicID FROM ViewAssociation, Association WHERE " +
			"ViewAssociation.ViewTopicID='" + topicmapID + "' AND " +
			"ViewAssociation.AssociationID=Association.ID AND " +
			associationTypeFilter(assocTypes) + " AND " +
			"Association.TopicID1='" + topicID1 + "' AND " +
			"Association.TopicID2='" + topicID2 + "'");
	}

	// ---

	public Vector getViews(String topicID, int version, String viewmode) {
		return queryBaseTopics("SELECT Topic.* FROM Topic, ViewTopic WHERE " +
			"ViewTopic.ViewTopicID=Topic.ID AND " +
			"ViewTopic.ViewTopicVersion=Topic.Version AND " +
			"ViewTopic.TopicID = ? AND " +
			"ViewTopic.TopicVersion = ?",
			new Object[]{topicID, i(version)});
	}




	// ------------------------
	// --- Topic Properties ---
	// ------------------------



	// --- getTopicData (2 forms) ---

	public Hashtable getTopicData(String topicID, int version) {
		return queryProperties("SELECT PropName, PropValue FROM TopicProp WHERE " +
			"TopicID=? AND TopicVersion=?", new Object[]{topicID, i(version)});
	}

	public String getTopicData(String topicID, int version, String propName) {
		return queryProperty("SELECT PropValue FROM TopicProp WHERE " +
			"TopicID=? AND TopicVersion=? AND PropName=?", new Object[]{topicID, i(version), propName});
	}

	// --- setTopicData (2 forms) ---

	/**
	 * @param	topicData	Topic data to set<br>
	 *						Key: field name (<code>String</code>)<br>
	 *						Value: value (<code>String</code>)
	 */
	public void setTopicData(String topicID, int version, Hashtable topicData) {
		Enumeration e = topicData.keys();
		String field;
		String value;
		while (e.hasMoreElements()) {
			field = (String) e.nextElement();
			value = (String) topicData.get(field);
			setTopicData(topicID, version, field, value);
		}
	}

	public void setTopicData(String topicID, int version, String field, String value) {
		if (field.length() == 0) {
			System.out.println("*** RelationalCorporateMemory.setTopicData(): " + topicID + ":" + version +
				" property name is empty (value: \"" + value + "\") -- property not stored");
			return;
		}
		if (LOG_CM) {
			System.out.println("> (>) " + topicID + ":" + version + " (update \"" + field + "\" to \"" + value + "\")");
		}
		//
		int rowCount = update("UPDATE TopicProp SET PropValue = ? WHERE TopicID = ?" +
			" AND TopicVersion = ? AND PropName = ?",
			new Object[]{value, topicID, i(version), field}, IMPLIED_NO);
		if (rowCount == 0) {
			// the topic has no topic data yet -- insert
			if (LOG_CM) {
				System.out.println("> (#) " + topicID + ":" + version + " (insert \"" + field + "\" to \"" + value + "\")");
			}
			update("INSERT INTO TopicProp (TopicID, TopicVersion, PropName, PropValue) VALUES (?, ?, ?, ?)",
			       new Object[]{topicID, i(version), field, value});
		} else if (rowCount > 1) {
			System.out.println("*** RelationalCorporateMemory.setTopicData(): " +
				rowCount + " rows updated, \"" + topicID + "\", " + version + ", \"" + field + "\"");
		}
	}

	// ---

	public void deleteTopicData(String topicID, int version) {
		int count = delete("DELETE FROM TopicProp WHERE " +
											"TopicID='" + topicID + "' AND " +
											"TopicVersion=" + version);
		if (LOG_CM) System.out.println("> (-) " + count + " topic properties deleted");
	}



	// ------------------------------
	// --- Association Properties ---
	// ------------------------------



	// --- getAssociationData (2 forms) ---

	public Hashtable getAssociationData(String assocID, int version) {
		return queryProperties("SELECT PropName, PropValue FROM AssociationProp WHERE " +
			"AssociationID=? AND AssociationVersion=?", new Object[]{assocID, i(version)});
	}

	public String getAssociationData(String assocID, int version, String propName) {
		return queryProperty("SELECT PropValue FROM AssociationProp WHERE " +
			"AssociationID=? AND AssociationVersion=? AND PropName=?", new Object[]{assocID, i(version), propName});
	}

	// --- setAssociationData (2 forms) ---

	/**
	 * @param	assocData	Association data to set<br>
	 *						Key: field name (<code>String</code>)<br>
	 *						Value: value (<code>String</code>)
	 */
	public void setAssociationData(String assocID, int version, Hashtable assocData) {
		Enumeration e = assocData.keys();
		String field;
		String value;
		while (e.hasMoreElements()) {
			field = (String) e.nextElement();
			value = (String) assocData.get(field);
			setAssociationData(assocID, version, field, value);
		}
	}

	public void setAssociationData(String assocID, int version, String field, String value) {
		if (LOG_CM) {
			System.out.println("> (>) " + assocID + ":" + version + " (update \"" +
				field + "\" to \"" + value + "\")");
		}
		//
		int rowCount = update("UPDATE AssociationProp SET PropValue = ? WHERE AssociationID = ? AND " +
			"AssociationVersion = ? AND PropName = ?", new Object[]{value, assocID, i(version), field}, IMPLIED_NO);
		if (rowCount == 0) {
			// the association has no association data yet -- insert
			if (LOG_CM) {
				System.out.println("> (#) " + assocID + ":" + version + " (insert \"" +
					field + "\" to \"" + value + "\")");
			}
			update("INSERT INTO AssociationProp " +
				"(AssociationID, AssociationVersion, PropName, PropValue) VALUES " +
				"(?, ?, ?, ?)", new Object[]{assocID, i(version), field, value});
		} else if (rowCount > 1) {
			System.out.println("*** RelationalCorporateMemory.setAssociationData():" +
				" " + rowCount + " rows updated, \"" + assocID + "\", " + version +
				", \"" + field + "\"");
		}
	}

	// ---

	public void deleteAssociationData(String assocID, int version) {
		int count = delete("DELETE FROM AssociationProp WHERE " +
											"AssociationID='" + assocID + "' AND " +
											"AssociationVersion=" + version);
		if (LOG_CM) System.out.println("> (-) " + count + " association properties deleted");
	}



	// ------------
	// --- Misc ---
	// ------------



	// --- ID generator ---

	// TODO common procedure with transaction
	public synchronized String getNewTopicID() {
		int newID = queryNewID("SELECT NextKey FROM KeyGenerator WHERE Relation = 'Topic'");	// throws DME
		update("UPDATE KeyGenerator SET NextKey = ? WHERE Relation = 'Topic'", new Object[]{i(newID + 1)});
        //
		return "t-" + newID;
	}

	// TODO common procedure with transaction
	public synchronized String getNewAssociationID() {
		int newID = queryNewID("SELECT NextKey FROM KeyGenerator WHERE Relation = 'Association'");	// throws DME
		update("UPDATE KeyGenerator SET NextKey = ? WHERE Relation = 'Association'", new Object[]{i(newID + 1)});
        //
		return "a-" + newID;
	}

	// --- Counting Topics / Associations ---

	public int getTopicCount() {
		return queryCount("SELECT COUNT(ID) AS \"Count\" FROM Topic");
	}

	public int getAssociationCount() {
		return queryCount("SELECT COUNT(ID) AS \"Count\" FROM Association");
	}

	// --- Getting version info ---

    /**
     * @see		ApplicationServiceInstance#createCorporateMemory
     */
	public int getModelVersion() throws DeepaMehtaException {
		String query = "SELECT NextKey FROM KeyGenerator WHERE Relation = 'DB-Model Version'";
		int modelVersion = queryNewID(query);	// throws DME
		return modelVersion;
	}

	public int getContentVersion() throws DeepaMehtaException {
		String query = "SELECT NextKey FROM KeyGenerator WHERE Relation = 'DB-Content Version'";
		int modelVersion = queryNewID(query);	// throws DME
		return modelVersion;
	}



	// --------------------------------------------------------------
	// --- End of Interface de.deepamehta.service.CorporateMemory ---
	// --------------------------------------------------------------

	

	// -----------------------
	// --- Private Methods ---
	// -----------------------



	/**
	 * @return	Vector of <code>String</code>s.
	 *
	 * @see		#getTopicIDs(String typeID, String topicmapID)
	 * @see		#getAssociationIDs(String assocTypeID, String topicmapID, String viewMode,
	 *							 String topicID, int topicVersion)
	 */
	private Vector queryIDs(String query) {
		return queryStrings(query, "ID");
	}

	/**
	 * @see		#getTopicTypes
	 * @see		#getAssociationTypes
	 * @see		#queryIDs
	 */
	private Vector queryStrings(String query, String field) {
		Vector result = new Vector();
		try {
			Statement stmt = createStatement();
			ResultSet resultSet = stmt.executeQuery(query);
			while (resultSet.next()) {
				result.addElement(resultSet.getString(field));
			}
			stmt.close();
		} catch (SQLException e) {
			System.out.println("*** RelationalCorporateMemory.queryStrings(): " + e +
				" -- nothing returned");
		}
		return result;
	}

	/**
	 * Executes the specified query and returns the result set as vector of 2-element
	 * String arrays. From every record the 2 specified fields are returned.
	 *
	 * @see		#getTopicTypes
	 * @see		#getAssociationTypes
	 */
	private Hashtable queryHashtable(String query, String field1, String field2) {
		Hashtable result = new Hashtable();
		try {
			Statement stmt = createStatement();
			ResultSet resultSet = stmt.executeQuery(query);
			while (resultSet.next()) {
				result.put(resultSet.getString(field1), resultSet.getString(field2));
			}
			stmt.close();
		} catch (SQLException e) {
			System.out.println("*** RelationalCorporateMemory.queryHashtable(): " + e +
				" -- nothing returned");
		}
		return result;
	}

	/**
	 * @see		#getViewTopicVersion(String topicmapID, int topicmapVersion, String viewMode, String topicID)
	 * @see		#getViewTopicVersion(String topicmapID, int topicmapVersion, String viewMode, String topicType, String topicName)
	 */
	private int queryVersion(String query, String field) {
		try {
			Statement stmt = createStatement();
			ResultSet resultSet = stmt.executeQuery(query);
			if (!resultSet.next()) {
				System.out.println("*** RelationalCorporateMemory.queryVersion(): no " +
					"rows found:\n\"" + query + "\"");			
			}
			int version = resultSet.getInt(field);
			if (resultSet.next()) {
				System.out.println("*** RelationalCorporateMemory.queryVersion(): more " +
					"than one row found:\n\"" + query + "\"\n-- only consider version " +
					version);
			}
			stmt.close();
			return version;
		} catch (SQLException e) {
			System.out.println("*** RelationalCorporateMemory.queryVersion(): " + e +
				" -- nothing returned");
			return 0;
		}
	}
	
	/**
	 * @see		#getNewTopicID
	 * @see		#getNewAssociationID
	 * @see		#getModelVersion
	 * @see		#getContentVersion
	 *
	 * @throws	DeepaMehtaException	if an error occurrs
	 */
	private int queryNewID(String query) throws DeepaMehtaException {
		try {
            int id;
            //
			Statement stmt = createStatement();
			ResultSet resultSet = stmt.executeQuery(query);
			if (resultSet.next()) {
				id = resultSet.getInt("NextKey");
			} else {
                throw new DeepaMehtaException("empty result set: \"" + query + "\"");
            }
			stmt.close();
            //
            return id;
		} catch (SQLException e) {
            throw new DeepaMehtaException(e.getMessage());
		}
	}

	// --- queryBaseTopics (2 forms) ---

	private Vector queryBaseTopics(String query, Object[] params) {
		return queryBaseTopics(query, params, false);
	}

	private Vector queryBaseTopics(PreparedStatement stmt) {
		return queryBaseTopics(stmt, false);
	}

	/**
	 * @return	the query result as vector of {@link de.deepamehta.BaseTopic}
	 *
	 * @see		#getTopic(String id, int version)
	 * @see		#getTopic(String typeID, String name, int topicVersion)
	 * @see		#getTopics(String typeID, Hashtable propertyFilter)
	 * @see		#getRelatedTopics(String topicID, String assocType, int relTopicPos)
	 * @see		#getRelatedTopics(String topicID, String assocType, int relTopicPos, String topicmapID)
	 */
	private Vector queryBaseTopics(String query, Object[] params, boolean hasOrdinalNumber) {
		try {
			PreparedStatement stmt = createPreparedStatement(query, params);
			return queryBaseTopics(stmt, hasOrdinalNumber);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @return	the query result as vector of {@link de.deepamehta.BaseTopic}
	 *
	 * @see		#getTopic(String id, int version)
	 * @see		#getTopic(String typeID, String name, int topicVersion)
	 * @see		#getTopics(String typeID, Hashtable propertyFilter)
	 * @see		#getRelatedTopics(String topicID, String assocType, int relTopicPos)
	 * @see		#getRelatedTopics(String topicID, String assocType, int relTopicPos, String topicmapID)
	 */
	private Vector queryBaseTopics(PreparedStatement stmt, boolean hasOrdinalNumber) {
		Vector result = new Vector();
		try {
			ResultSet resultSet = stmt.executeQuery();
			while (resultSet.next()) {
				String topicName = resultSet.getString("Name");
				// ### Oracle workaround
				if (topicName == null) {
					topicName = "";
				}
				//
				BaseTopic topic = new BaseTopic(resultSet.getString("ID"), resultSet.getInt("Version"),
					resultSet.getString("TypeID"), resultSet.getInt("TypeVersion"), topicName);
				//
				if (hasOrdinalNumber) {
					String ordNr = resultSet.getString("PropValue");
					if (ordNr != null && !ordNr.equals("")) {
						try {
							// ### System.out.println(">>> RelationalCorporateMemory.queryBaseTopics(): " + topic + " -- ord \"" + ordNr + "\"");
							topic.setOrdinalNr(Integer.parseInt(ordNr));
						} catch (NumberFormatException e) {
							System.out.println("*** RelationalCorporateMemory.queryBaseTopics(): " + topic +
								" has illegal ordinal number:  \"" + ordNr + "\" -- ignored");
						}
					}
				}
				//
				result.addElement(topic);
			}
			stmt.close();
		} catch (SQLException e) {
			System.out.println("*** RelationalCorporateMemory.queryBaseTopics(): " + e + " -- nothing returned");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return result;
	}


	// ---

	/**
	 * @return	the query result as vector of {@link de.deepamehta.BaseAssociation}
	 *
	 * @see		#getAssociation
	 * @see		#getAllAssociations
	 */
	private Vector queryBaseAssociations(String query) {
		Vector result = new Vector();
		try {
			Statement stmt = createStatement();
			ResultSet resultSet = stmt.executeQuery(query);
			while (resultSet.next()) {
				result.addElement(new BaseAssociation(resultSet.getString("ID"),
					resultSet.getInt("Version"), resultSet.getString("TypeID"),
					resultSet.getInt("TypeVersion"), resultSet.getString("Name"),
					resultSet.getString("TopicID1"), resultSet.getInt("TopicVersion1"),
					resultSet.getString("TopicID2"), resultSet.getInt("TopicVersion2")));
			}
			stmt.close();
		} catch (SQLException e) {
			System.out.println("*** RelationalCorporateMemory.queryBaseAssociations(): " +
				e + " -- nothing returned");
		}
		return result;
	}

	// --- queryPresentableTopics (2 forms) ---

	/**
	 * The presentable topics have geometry mode GEOM_MODE_NEAR.
	 * <p>
	 * References checked: 21.9.2008 (2.0b8)
	 *
	 * @return	array of 2 vectors:<br>
	 *				vector 1: vector of {@link de.deepamehta.PresentableTopic}<br>
	 *				vector 2: vector of {@link de.deepamehta.PresentableAssociation}
	 *
	 * @see		#getRelatedViewTopicsByTopictype
	 * @see		#getRelatedViewTopicsByAssoctype
	 */
	private Vector[] queryPresentableTopics(String query, String nearTopicID) {
		Vector result[] = {new Vector(), new Vector()};
		ResultSet resultSet = null;
		try {
			Statement stmt = createStatement();
			resultSet = stmt.executeQuery(query);			
			while (resultSet.next()) {
				result[0].addElement(createPresentableTopic(resultSet, nearTopicID));
				result[1].addElement(createPresentableAssociation(resultSet));
			}
			stmt.close();
		} catch (SQLException e) {
			System.out.println("*** RelationalCorporateMemory.queryPresentableTopics(2)" +
				": " + e + " -- nothing returned, query:\n" + query);
			if (resultSet != null) {
				System.out.println("> result set columns:" + columnNames(resultSet));
			}
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * The presentable topics have geometry mode GEOM_MODE_ABSOLUTE.
	 *
	 * @return	Vector of {@link de.deepamehta.PresentableTopic}
	 *
	 * @see		#getViewTopics
	 * @see		#getRelatedViewTopics
	 */
	private Vector queryPresentableTopics(String query) {
		Vector result = new Vector();
		try {
			Statement stmt = createStatement();
			ResultSet resultSet = stmt.executeQuery(query);
			//
			while (resultSet.next()) {
				String topicID = resultSet.getString("ID");
				int topicVersion = resultSet.getInt("Version");
				String topicTypeID = resultSet.getString("TypeID");
				int topicTypeVersion = resultSet.getInt("TypeVersion");
				String topicName = resultSet.getString("Name");
				Point geometry = new Point(resultSet.getInt("x"), resultSet.getInt("y"));
				String locked = resultSet.getString("PropValue");	// Note: may be null (retrieved through outer join)
				// Oracle workaround
				if (topicName == null) {
					topicName = "";
				}
				//
				PresentableTopic topic = new PresentableTopic(topicID, topicVersion, topicTypeID,
					topicTypeVersion, topicName, geometry);
				if (SWITCH_ON.equals(locked)) {
					topic.setLocked(true);
				}
				//
				result.addElement(topic);
			}
			stmt.close();
		} catch (SQLException e) {
			System.out.println("*** RelationalCorporateMemory.queryPresentableTopics(1): \"" + query + "\" " + e +
				" -- nothing returned");
		}
		return result;
	}

	// ---

	/**
	 * @return	Vector of {@link de.deepamehta.PresentableAssociation}
	 *
	 * @see		#getViewAssociations
	 */
	private Vector queryPresentableAssociations(String query) {
		Vector result = new Vector();
		try {
			Statement stmt = createStatement();
			ResultSet resultSet = stmt.executeQuery(query);
			while (resultSet.next()) {
				result.addElement(createPresentableAssociation(resultSet));
			}
			stmt.close();
		} catch (SQLException e) {
			System.out.println("*** RelationalCorporateMemory.queryPresentableAssociations(): " +
				e + " -- nothing returned");
		}
		return result;
	}

	/**
	 * @return	Vector of {@link de.deepamehta.PresentableType}.<br>
	 *			Note: the geometry mode of the topics is set to {@link #GEOM_MODE_FREE}.
	 *
	 * @see		#getTopicTypes
	 * @see		#getAssociationTypes
	 */
	private Vector queryPresentableTypes(PreparedStatement stmt) {
		Vector result = new Vector();
		try {
			ResultSet resultSet = stmt.executeQuery();
			String topicName;
			PresentableType type;
			while (resultSet.next()) {
				topicName = resultSet.getString("Name");
				// Oracle workaround
				if (topicName == null) {
					topicName = "";
				}
				//
				type = new PresentableType(resultSet.getString("ID"), topicName, resultSet.getInt("Version"));
				result.addElement(type);
			}
			stmt.close();
		} catch (SQLException e) {
			System.out.println("*** RelationalCorporateMemory.queryPresentableTypes(): " + e + " -- nothing returned");
		}
		return result;
	}

	// ---

	/**
	 * Executes the specified query and returns the property value.
	 * If the property doesn't exists resp. is <code>null</code> an empty string is returned.
	 * <p>
	 * References checked: 13.8.2001 (2.0a11)
	 *
	 * @see		#getTopicData(String topicID, int version, String propName)
	 * @see		#getAssociationData(String assocID, int version, String propName)
	 */
	private String queryProperty(String query, Object[] params) {
		String value = "";
		try {
			PreparedStatement stmt = createPreparedStatement(query, params);
			ResultSet resultSet = stmt.executeQuery();
			if (resultSet.next()) {
				value = resultSet.getString("PropValue");
				// Oracle workaround
				if (value == null) {
					value = "";
				}
			}
			stmt.close();
		} catch (SQLException e) {
			System.out.println("*** RelationalCorporateMemory.queryProperty(): " + e + " -- nothing returned");
		}
		return value;
	}

	/**
	 * @see		#getTopicData
	 * @see		#getAssociationData
	 */
	private Hashtable queryProperties(String query, Object[] params) {
		Hashtable topicData = new CaseInsensitveHashtable();	// the result
		try {
			PreparedStatement stmt = createPreparedStatement(query, params);
			ResultSet resultSet = stmt.executeQuery();
			while (resultSet.next()) {
				String propName = resultSet.getString("PropName");
				String propVal = resultSet.getString("PropValue");
				// Oracle workaround
				if (propVal == null) {
					propVal = "";
				}
				topicData.put(propName, propVal);
			}
			stmt.close();
		} catch (SQLException e) {
			System.out.println("*** RelationalCorporateMemory.queryProperties(): " + e + " -- nothing returned");
		}
		return topicData;
	}

	// ---

	private int queryCount(String query) {
		int count = -1;
		try {
			Statement stmt = createStatement();
			ResultSet resultSet = stmt.executeQuery(query);
			if (resultSet.next()) {
				count = resultSet.getInt("Count");
			}
			stmt.close();
		} catch (SQLException e) {
			System.out.println("*** RelationalCorporateMemory.queryCount(): " + e +
				" -- -1 returned");
		}
		return count;
	}

	// --- update (3 forms) ---

	/**
	 * Executes an INSERT, UPDATE or DELETE statement.
	 */
	private int update(String query, Object[] params) {
		return update(query, params, IMPLIED_1);
	}

	/**
	 * Executes an INSERT, UPDATE or DELETE statement.
	 */
	private int update(String query, Object[] params, int implied) {
		try {
			return update(createPreparedStatement(query, params), query, params, implied);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * Executes an INSERT, UPDATE or DELETE statement.
	 *
	 * @param	query	for diagnosis only
	 * @param	params	for diagnosis only
	 */
	private int update(PreparedStatement stmt, String query, Object[] params, int implied) {
		try {
			int rowCount = stmt.executeUpdate();
			if (implied == IMPLIED_1 && rowCount != 1) {
				System.out.println("*** RelationalCorporateMemory.update(): " +
					rowCount + " rows updated while 1 is expected, query and paramters:");
				System.out.println("    " + query);
				printObjectArray(params);
			}
			return rowCount;
		} catch (SQLException e) {
			if (implied != IMPLIED_NO) {
				System.out.println("*** RelationalCorporateMemory.update(): " + e +
					" -- INSERT, UPDATE or DELETE statement failed, query and paramters:");
				System.out.println("    " + query);
				printObjectArray(params);
			}
			return 0;
		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
			}
		}
	}

	// ---

	private int delete(String query) {
		try {
			Statement stmt = createStatement();
			int rowCount = stmt.executeUpdate(query);
			stmt.close();
			return rowCount;
		} catch (SQLException e) {
			System.out.println("*** RelationalCorporateMemory.delete(): " + e + " -- not deleted");
			return 0;
		}
	}

	/**
	 * Executes the query and returns true if a result exists.
	 *
	 * @return	true if the result set has at least one record, false otherwise.
	 *
	 * @see		#topicExists
	 * @see		#associationExists
	 * @see		#viewTopicExists
	 * @see		#viewAssociationExists
	 * @see		#viewAssociationExists
	 */
	private boolean exists(String query) {
		try {
			Statement stmt = createStatement();
			ResultSet resultSet = stmt.executeQuery(query);
			boolean exists = resultSet.next();
			stmt.close();
			return exists;
		} catch (SQLException e) {
			System.out.println("*** RelationalCorporateMemory.exists(): " + e + " -- false returned");
			return false;
		}
	}



	// ----------------------
	// --- Helper Methods ---
	// ----------------------



	private Statement createStatement() throws SQLException {
		return provider.getStatement();
	}

	private PreparedStatement createPreparedStatement(String sql) throws SQLException {
		return provider.getPreparedStatement(sql);
	}

	private PreparedStatement createPreparedStatement(String query, Object[] params) throws SQLException {
		PreparedStatement stmt = createPreparedStatement(query);
		//
		for (int i = 0; i < params.length; i++) {
			Object param = params[i];
			if (param instanceof String) {
				stmt.setString(i + 1, (String) param);
			} else if (param instanceof Integer) {
				stmt.setInt(i + 1, ((Integer) param).intValue());
			} else if (param == null) {
				System.out.println("*** RelationalCorporateMemory.createPreparedStatement(): " +
					"parameter " + i + " is missing (null), query and paramters:");
				System.out.println("    " + query);
				printObjectArray(params);
				throw new DeepaMehtaException("parameter is missing in statement (see DeepaMehta log for details)");
			} else {
				throw new DeepaMehtaException("parameter has unexpected type (" + param.getClass().toString() + ")");
			}
		}
		return stmt;
	}

	// ---
	
	private Integer i(int i) {
		return new Integer(i);
	}

	private void printObjectArray(Object[] array) {
		System.out.print("    [");
		for (int i = 0; i < array.length; i++) {
			System.out.print(i > 0 ? ", " : "");
			System.out.print(array[i]);
		}
		System.out.println("]");
	}

	// ---

	/**
	 * The <code>resultSet</code> is expected to contain the topic fields aliased as
	 * <code>TopicID</code>, <code>TopicVersion</code>, <code>TopicTypeID</code>,
	 * <code>TopicTypeVersion</code> and <code>TopicName</code>
	 * (see {@link #getRelatedViewTopicsByTopictype},
	 * {@link #getRelatedViewTopicsByAssoctype}).
	 * <p>
	 * References checked: 21.9.2008 (2.0b8)
	 *
	 * @see		#queryPresentableTopics(String query, String nearTopicID)
	 */
	private PresentableTopic createPresentableTopic(ResultSet resultSet, String nearTopicID) throws SQLException {
		String topicID  = resultSet.getString("TopicID");
		int version = resultSet.getInt("TopicVersion");
		String type = resultSet.getString("TopicTypeID");
		int typeVersion = resultSet.getInt("TopicTypeVersion");
		String name = resultSet.getString("TopicName");
		// Oracle workaround
		if (name == null) {
			name = "";
		}
		//
		PresentableTopic topic = new PresentableTopic(topicID, version, type, typeVersion, name, nearTopicID);
		return topic;
	}

	/**
	 * @see		#queryPresentableTopics
	 * @see		#queryPresentableAssociations
	 */
	private PresentableAssociation createPresentableAssociation(ResultSet resultSet) throws SQLException {
		return new PresentableAssociation(
			resultSet.getString("ID"),
			resultSet.getInt("Version"),
			resultSet.getString("TypeID"),
			resultSet.getInt("TypeVersion"),
			resultSet.getString("Name"),
			resultSet.getString("TopicID1"),
			resultSet.getInt("TopicVersion1"),
			resultSet.getString("TopicID2"),
			resultSet.getInt("TopicVersion2")
		);
	}

	// --- topicQuery (6 forms) ---

	/**
	 * @see		#getTopics(String typeID)
	 * @see		#getTopicTypes()
	 * @see		#getAssociationTypes()
	 */
	private PreparedStatement topicQuery(String typeID) {
		return topicQuery(typeID, null);
	}

	private PreparedStatement topicQuery(String typeID, String name) {
		return topicQuery(typeID, name, null);
	}

	/**
	 * @see		#getTopics(String typeID)
	 * @see		#getTopicTypes()
	 * @see		#getAssociationTypes()
	 */
	private PreparedStatement topicQuery(String typeID, String name, String order) {
		String query = "SELECT * FROM Topic WHERE TypeID = ?";
		Vector params=new Vector();
		params.add(typeID);
		if (name != null) {
			query += " AND Name LIKE ?";
			params.add("%" + name + "%");
		}
		if (order != null) {
			query += " ORDER BY " + order;
		}
		try {
			return createPreparedStatement(query,params.toArray());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @see		#getViewTopics
	 */
	private String topicQuery(String topicmapID, int version, String typeID, Vector typeIDs, String nameFilter) {
		String query = "SELECT Topic.*, ViewTopic.x, ViewTopic.y, TopicProp.PropValue FROM Topic" +
			" INNER JOIN ViewTopic ON " +
			"ViewTopic.ViewTopicID='" + topicmapID + "' AND " +
			"ViewTopic.ViewTopicVersion=" + version + " AND " +
			"ViewTopic.TopicID=Topic.ID AND " +
			"ViewTopic.TopicVersion=Topic.Version" +
			" LEFT OUTER JOIN TopicProp ON " +
			"TopicProp.TopicID=Topic.ID AND " +
			"TopicProp.TopicVersion=Topic.Version AND " +
			"TopicProp.PropName='" + PROPERTY_LOCKED_GEOMETRY + "'" +
			(typeID != null || typeIDs != null || nameFilter != null ? " WHERE " : "") +
			(typeIDs != null ? topicTypeFilter(typeIDs) + (nameFilter != null ? " AND " : "") : "") + 
			(typeID != null ? "TypeID='" + typeID + "'" + (nameFilter != null ? " AND " : "") : "") +
			(nameFilter != null ? "Name LIKE '%" + nameFilter + "%'" : "") +
			" ORDER BY Topic.Name";		// ### ORDER hardcoded (not always required)
		return query;
	}

	/**
	 * @param	relTopicPos		the association position you request (1 or 2).<br>
	 *							0 means arbitrary position.
	 */
	private String topicQuery(String topicmapID, int version, String topicID, String relTopicTypeID, int relTopicPos,
																								String assocTypeID) {
		String query = "SELECT Topic.*, ViewTopic.x, ViewTopic.y, TopicProp.PropValue FROM Topic" +
			" INNER JOIN ViewTopic ON " +
			"ViewTopic.ViewTopicID='" + topicmapID + "' AND " +
			"ViewTopic.ViewTopicVersion=" + version + " AND " +
			"ViewTopic.TopicID=Topic.ID AND " +
			"ViewTopic.TopicVersion=Topic.Version" +
			" INNER JOIN Association ON " +
			(relTopicPos == 0 ? "(Association.TopicID1='" + topicID + "' AND Association.TopicID2=Topic.ID OR " +
			"Association.TopicID2='" + topicID + "' AND Association.TopicID1=Topic.ID)" :
			"Association.TopicID" + (3 - relTopicPos) + "='" + topicID + "' AND " +
			"Association.TopicID" + relTopicPos + "=Topic.ID") + " AND Topic.TypeID='" +
			relTopicTypeID + "' AND Association.TypeID='" + assocTypeID + "'" +
			" LEFT OUTER JOIN TopicProp ON " +
			"TopicProp.TopicID=Topic.ID AND " +
			"TopicProp.TopicVersion=Topic.Version AND " +
			"TopicProp.PropName='" + PROPERTY_LOCKED_GEOMETRY + "' ORDER BY Topic.Name";	// ### ORDER hardcoded
		return query;
	}

	/**
	 * @see		#getTopics(String typeID, String nameFilter, Hashtable propertyFilter,
	 *					   String relatedTopicID, String assocTypeID, String topicmapID)
	 */
	private PreparedStatement topicQuery(String typeID, Vector typeIDs, String nameFilter, Hashtable propertyFilter,
					String relatedTopicID, String assocTypeID, String topicmapID, boolean caseSensitiv, boolean sortByTopicName) {
        StringBuffer query = new StringBuffer("SELECT Topic.*");
		StringBuffer fromClause = new StringBuffer(" FROM Topic");
		StringBuffer whereClause = new StringBuffer(" WHERE ");
		List whereParams = new ArrayList();
		if (typeIDs != null) {
			applyTopicTypeFilter(typeIDs, whereClause, whereParams);
		} else {
			whereClause.append("Topic.TypeID = ?");
			whereParams.add(typeID);
		}
		// --- relationship filter (preamble) ---
		if (relatedTopicID != null) {
			fromClause.append(", Association");
		}
		// --- topicmap filter (preamble) ---
		if (topicmapID != null) {
			fromClause.append(", ViewTopic");
		}
        // --- property filter ---
		appendPropertyFilter(propertyFilter, fromClause, whereClause, whereParams, caseSensitiv);
		// --- name filter ---
		if (nameFilter != null) {
			whereClause.append(" AND Topic.Name LIKE ?");
			whereParams.add("%" + nameFilter + "%");
		}
		// --- relationship filter ---
		if (relatedTopicID != null) {
			whereClause.append(" AND (Association.TopicID1 = ? AND Association.TopicID2 = Topic.ID " +
				" OR Association.TopicID2 = ? AND Association.TopicID1 = Topic.ID)");
			whereParams.add(relatedTopicID);
			whereParams.add(relatedTopicID);
			if (assocTypeID != null) {
				whereClause.append(" AND Association.TypeID = ?");
				whereParams.add(assocTypeID);
			}
		}
		// --- topicmap filter ---
		if (topicmapID != null) {
			whereClause.append(" AND ViewTopic.ViewTopicID = ? AND ViewTopic.TopicID=Topic.ID");
			whereParams.add(topicmapID);
		}
		// --- sorting ---
		if (sortByTopicName) {
			whereClause.append(" ORDER BY Name");
		}
		//
		query.append(fromClause);
		query.append(whereClause);
		//
		try {
			return createPreparedStatement(query.toString(), whereParams.toArray());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	// ---

	/**
	 * @see		#getViewAssociations
	 */
	private String associationQuery(String topicmapID, int version) {
		String query = "SELECT Association.* FROM Association, ViewAssociation WHERE " +
			"ViewAssociation.ViewTopicID='" + topicmapID + "' AND " +
			"ViewAssociation.ViewTopicVersion=" + version + " AND " +
			"ViewAssociation.AssociationID=Association.ID";
		return query;
	}

	// ---

	private void appendPropertyFilter(Hashtable propertyFilter, StringBuffer fromClause, StringBuffer whereClause,
																				List whereParams, boolean caseSensitiv) {
		Enumeration e = propertyFilter.keys();
		int nIndex = 0;
		while (e.hasMoreElements()) {
			String propName = (String) e.nextElement();
			String propValue = (String) propertyFilter.get(propName);
			if (!propValue.equals("")) {
				fromClause.append(", TopicProp td" + nIndex);
				whereClause.append(" AND Topic.ID=td" + nIndex + ".TopicID");
				whereClause.append(" AND Topic.Version=td" + nIndex + ".TopicVersion");
				whereClause.append(" AND td" + nIndex + ".PropName = ?");
				whereParams.add(propName);
				if (caseSensitiv) {
					whereClause.append(" AND td" + nIndex + ".PropValue = ?");
					whereParams.add(propValue);
				} else {
					whereClause.append(" AND LOWER(td" + nIndex + ".PropValue) LIKE ?");
					whereParams.add("%" + propValue.toLowerCase() + "%");
				}
				nIndex++;
			}
		}
	}

	// ---

	private void applyTopicIdFilter(Vector IDs, StringBuffer whereClause, List whereParams) {
		whereClause.append("Topic.ID IN (");
		applyIdList(IDs, whereClause, whereParams);
		whereClause.append(")");
	}

	// TODO prepared statement 
	private String topicIdFilter(Vector topicIDs) {
		return "Topic.ID IN (" + idList(topicIDs) + ")";
	}

	private void applyTopicTypeFilter(Vector typeIDs, StringBuffer whereClause, List whereParams) {
		whereClause.append("Topic.TypeID IN (");
		applyIdList(typeIDs, whereClause, whereParams);
		whereClause.append(")");
	}

	// TODO prepared statement 
	private String topicTypeFilter(Vector typeIDs) {
		return "Topic.TypeID IN (" + idList(typeIDs) + ")";
	}

	private void applyAssociationTypeFilter(Vector typeIDs, StringBuffer whereClause, List whereParams) {
		whereClause.append("Association.TypeID IN (");
		applyIdList(typeIDs, whereClause, whereParams);
		whereClause.append(")");
	}

	// TODO prepared statement 
	private String associationTypeFilter(Vector typeIDs) {
		return "Association.TypeID IN (" + idList(typeIDs) + ")";
	}

	// TODO prepared statement 
	private String idList(Vector typeIDs) {
		StringBuffer b = new StringBuffer();
		//
		for (int i = 0; i < typeIDs.size(); i++) {
			if (i > 0) {
				b.append(", ");
			}
			String typeID = (String) typeIDs.elementAt(i);
			b.append("'" + typeID + "'");
		}
		//
		return b.toString();
	}

	private void applyIdList(Vector typeIDs, StringBuffer whereClause, List whereParams) {
		for (int i = 0; i < typeIDs.size(); i++) {
			if (i > 0) {
				whereClause.append(", ");
			}
			String typeID = (String) typeIDs.elementAt(i);
			whereClause.append("?");
			whereParams.add(typeID);
		}
	}

	// ---

	/**
	 * @see		#getTopicIDs(String typeID, String topicmapID, boolean sortByTopicName)
	 */
	private String topicIDsQuery(String typeID, String topicmapID, int version, boolean sortByTopicName) {
		return "SELECT Topic.ID FROM Topic, ViewTopic WHERE " +
			"ViewTopic.ViewTopicID='" + topicmapID + "' AND " +
			"ViewTopic.ViewTopicVersion=" + version + " AND " +
			"ViewTopic.TopicID=Topic.ID AND " +
			"ViewTopic.TopicVersion=Topic.Version AND " +
			"Topic.TypeID='" + typeID + "'" +
			(sortByTopicName ? " ORDER BY Name" : "");
	}

	// --- associationIDsQuery (2 forms) ---

	/**
	 * @see		#getAssociationIDs(String assocTypeID, String topicmapID, String viewmode,
	 *							 String topicID, int topicVersion)
	 */
	private String associationIDsQuery(String topicID, int topicVersion) {
		// Note: there is no "topicVersion"/"assocTypeVersion" involved here ###
		String query ="SELECT Association.ID FROM Association WHERE " +
			"(Association.TopicID1='" + topicID + "' OR " +
			 "Association.TopicID2='" + topicID + "')";
		return query;
	}

	/**
	 * @see		#getAssociationIDs(String assocTypeID, String topicmapID, String viewmode,
	 *							 String topicID, int topicVersion)
	 */
	private String associationIDsQuery(String topicID, int topicVersion,
								String topicmapID, int topicmapVersion, String viewmode) {
		// Note: there is no "topicVersion"/"assocTypeVersion" involved here ###
		String query ="SELECT Association.ID FROM Association, ViewAssociation WHERE " +
			"ViewAssociation.ViewTopicID='" + topicmapID + "' AND " +
			"ViewAssociation.ViewTopicVersion=" + topicmapVersion + " AND " +
			"ViewAssociation.AssociationID=Association.ID AND " +
			"ViewAssociation.AssociationVersion=Association.Version AND " +
			"(Association.TopicID1='" + topicID + "' OR Association.TopicID2='" + topicID + "')";
		return query;
	}

	// ---

	private String columnNames(ResultSet resultSet) {
		try {
			ResultSetMetaData metaData = resultSet.getMetaData();
			StringBuffer result = new StringBuffer();
			int count = metaData.getColumnCount();
			for (int i = 1; i <= count; i++) {
				result.append(" ");
				result.append(metaData.getColumnName(i));
			}
			result.append(" (" + count + ")");
			return result.toString();
		} catch (SQLException e) {
			System.out.println("*** RelationalCorporateMemory.columnNames(): " + e +
				" -- column names not available");
			return null;
		}
	}

	// ---

	// ### sortTopicProps, may be null, but not empty
	private String sortClause(String[] sortTopicProps, boolean descending) {
		if (sortTopicProps == null) {
			return "";
		}
		StringBuffer buf = new StringBuffer();
		StringBuffer order = new StringBuffer(" ORDER BY ");
		for (int i = 0; i < sortTopicProps.length; i++) {
			String td = "TD" + i;
			buf.append(" LEFT OUTER JOIN TopicProp " + td + " ON " +
				td + ".TopicID=Topic.ID AND " +
				td + ".PropName='" + sortTopicProps[i] + "'");
			if (i > 0) {
				order.append(", ");
			}
			order.append(td + ".PropValue");
			if (descending) {
				order.append(" DESC");
			}
		}
		buf.append(order.toString());
		return buf.toString();
	}

	// ---

	public void release() {
		provider.release();
	}
}
