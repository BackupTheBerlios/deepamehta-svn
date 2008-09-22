package de.deepamehta.service;

import de.deepamehta.BaseAssociation;
import de.deepamehta.BaseTopic;

import java.util.Hashtable;
import java.util.Vector;



/**
 * This interface specifies the storage layer.
 * <p>
 * <IMG SRC="../../../../../images/3-tier-cm.gif">
 * <p>
 * <hr>
 * Last change: 22.9.2008 (2.0b8)<br>
 * J&ouml;rg Richter<br>
 * jri@deepamehta.de
 */
public interface CorporateMemory {



	// -------------------------
	// --- Retrieving Topics ---
	// -------------------------



	BaseTopic getTopic(String id, int version);
	BaseTopic getTopic(String type, String name, int version);
	//
	Vector getTopics();
	Vector getTopics(String typeID);
	Vector getTopics(String typeID, String nameFilter);
	Vector getTopics(String typeID, Hashtable propertyFilter);
	Vector getTopics(String typeID, Hashtable propertyFilter, boolean caseSensitiv);
	Vector getTopics(String typeID, Hashtable propertyFilter, String topicmapID);
	Vector getTopics(String typeID, Hashtable propertyFilter, String topicmapID, boolean caseSensitiv);
	Vector getTopics(String typeID, String nameFilter, Hashtable propertyFilter);
	Vector getTopics(String typeID, String nameFilter, Hashtable propertyFilter, String relatedTopicID);
	Vector getTopics(String typeID, String nameFilter, Hashtable propertyFilter, String relatedTopicID, String assocTypeID);
	Vector getTopics(String typeID, String nameFilter, Hashtable propertyFilter, String relatedTopicID, String assocTypeID, boolean sortByTopicName);
	Vector getTopics(Vector typeIDs);	// ### not yet used
	Vector getTopics(Vector typeIDs, Hashtable propertyFilter, boolean caseSensitiv);
	//
	Vector getTopicsByName(String nameFilter);
	Hashtable getTopicsByProperty(String searchString);
	//
	Vector getRelatedTopics(String topicID);
	Vector getRelatedTopics(String topicID, String assocTypeID, int relTopicPos);
	// ### Vector getRelatedTopics(String topicID, String assocTypeID, String relTopicTypeID);	// ### not yet required
	Vector getRelatedTopics(String topicID, String assocTypeID, String relTopicTypeID, int relTopicPos);
	Vector getRelatedTopics(String topicID, String assocTypeID, String relTopicTypeID, int relTopicPos, String assocProp, String propValue, boolean sortAssociations);
	Vector getRelatedTopics(String topicID, String assocTypeID, String relTopicTypeID, int relTopicPos, String[] sortTopicProps, boolean descending);
	Vector getRelatedTopics(String topicID, String assocTypeID, String relTopicTypeID, int relTopicPos, boolean sortAssociations);
	Vector getRelatedTopics(String topicID, String assocTypeID, String relTopicTypeID, int relTopicPos, Vector relTopicIDs, boolean sortAssociations);
	Vector getRelatedTopics(String topicID, String assocTypeID, Vector relTopicTypeIDs, int relTopicPos, boolean sortAssociations);	// ### not yet used
	Vector getRelatedTopics(String topicID, String assocTypeID, int relTopicPos, String topicmapID);
	Vector getRelatedTopics(String topicID, Vector assocTypeIDs, int relTopicPos);

	/**
	 * Returns all IDs of topics of the specified type who are contained in the specified topicmap.
	 *
	 * @return	Vector of <code>String</code>s.
	 *
	 * @see		ApplicationService#getAllTopics(String typeID, String topicmapID, String viemode)
	 */
	Vector getTopicIDs(String typeID, String topicmapID);
	Vector getTopicIDs(String typeID, String topicmapID, boolean sortByTopicName);



	// -------------------------------
	// --- Retrieving Associations ---
	// -------------------------------



	BaseAssociation getAssociation(String id, int version);
	BaseAssociation getAssociation(String assocTypeID, String topicID1, String topicID2);
	BaseAssociation getAssociation(Vector assocTypeIDs, String topicID1, String topicID2);
	//
	Vector getAssociations();
	Vector getAssociations(String typeID);
	Vector getAssociations(String topicID1, String topicID2, boolean ignoreDirection);
	//
	Vector getRelatedAssociations(String topicID, String assocTypeID, int relTopicPos);
	Vector getRelatedAssociations(String topicID, String assocTypeID, int relTopicPos, String relTopicTypeID);

	// --- getAssociationIDs (2 forms) ---

	Vector getAssociationIDs(String topicID, int topicVersion);
	Vector getAssociationIDs(String topicID, int topicVersion, String topicmapID, int topicmapVersion, String viewmode);



	// --- getTopicTypes (3 forms) ---

	/**
	 * Returns all topic types.
	 *
	 * @return	Vector of {@link de.deepamehta.PresentableType}
	 */
	Vector getTopicTypes();

	Hashtable getTopicTypes(String topicID);

	/**
	 * Returns all topic types that are actually used in the specified view.
	 */
	Hashtable getTopicTypes(String topicmapID, int version, String viewmode);

	// --- getAssociationTypes (2 forms) ---

	/**
	 * Returns all association types.
	 *
	 * @return	Vector of {@link de.deepamehta.PresentableType}
	 */
	Vector getAssociationTypes();

	Hashtable getAssociationTypes(String topicID);

	// --- creating ---
	void createTopic(String id, int version, String type, int typeVersion, String name);
	void createAssociation(String id, int version, String type, int typeVersion,
							String topicID1, int topicVersion1, String topicID2, int topicVersion2);
	// --- changing name resp. type ---
	void changeTopicName(String topicID, int version, String name);
	void changeTopicType(String topicID, int version, String type, int typeVersion);
	void changeAssociationName(String assocID, int version, String name);
	void changeAssociationType(String assocID, int version, String type, int typeVersion);
	// --- deleting ---
	void deleteTopic(String topicID);
	void deleteAssociation(String assocID);
	// --- existence check ---
	boolean topicExists(String topicID);
	// boolean associationExists(String topicID1, String topicID2, String assocTypeID);	// ### not yet needed
	boolean associationExists(String topicID1, String topicID2, Vector assocTypeIDs);
	boolean associationExists(String topicID1, String topicID2, boolean ignoreDirection);



	// -------------
	// --- Views ---
	// -------------



	int getViewTopicVersion(String topicmapID, int topicmapVersion, String viewmode, String topicID);
	BaseTopic getViewTopic(String topicmapID, int topicmapVersion, String viewmode, String topicType, String topicName);

	// ---

	/**
	 * @return	Vector of {@link de.deepamehta.PresentableTopic}
	 */
	Vector getViewTopics(String topicmapID, int version);
	Vector getViewTopics(String topicmapID, int version, String typeID);
	Vector getViewTopics(String topicmapID, int version, String typeID, String nameFilter);
	Vector getViewTopics(String topicmapID, int version, Vector typeIDs);

	/**
	 * @return	Vector of {@link de.deepamehta.PresentableAssociation}
	 */
	Vector getViewAssociations(String topicmapID, int version);

	// ---

	/**
	 * @return	Vector of {@link de.deepamehta.PresentableTopic}
	 */
	Vector getRelatedViewTopics(String topicmapID, int version, String topicID, String assocTypeID, String relTopicTypeID);

	/**
	 * @return	Vector of {@link de.deepamehta.PresentableTopic}
	 */
	Vector getRelatedViewTopics(String topicmapID, int version, String topicID, String assocTypeID, String relTopicTypeID,
																											int relTopicPos);

	// ---

	/**
	 * @return	2-element array:<br>
	 *				element 1: vector of {@link de.deepamehta.PresentableTopic}<br>
	 *				element 2: vector of {@link de.deepamehta.PresentableAssociation}
	 */
	Vector[] getRelatedViewTopicsByTopictype(String topicID, String relTopicTypeID);
	Vector[] getRelatedViewTopicsByTopictype(String topicID, String relTopicTypeID, int relTopicPos, String assocTypeID);

	/**
	 * @return	2-element array:<br>
	 *				element 1: vector of {@link de.deepamehta.PresentableTopic}<br>
	 *				element 2: vector of {@link de.deepamehta.PresentableAssociation}
	 */
	Vector[] getRelatedViewTopicsByAssoctype(String topicID, String assocTypeID);

	// ---

	void createViewTopic(String topicmapID, int topicmapVersion, String viewmode,
										String topicID, int topicVersion, int x, int y,
										boolean performExistenceCheck);

	void createViewAssociation(String topicmapID, int topicmapVersion, String viewmode,
										String assocID, int assocVersion,
										boolean performExistenceCheck);

	// ---

	void deleteViewTopic(String topicID);
	void deleteViewTopic(String topicmapID, String viewmode, String topicID);
	void deleteViewAssociation(String assocID);
	void deleteViewAssociation(String topicmapID, String viewmode, String assocID);
	void deleteView(String topicmapID, int topicmapVersion);

	// ---

	void updateView(String srcTopicmapID, int srcTopicmapVersion, String destTopicmapID, int descTopicmapVersion);
	void updateViewTopic(String topicmapID, int topicmapVersion, String topicID, int x, int y);

	// ---

	boolean viewTopicExists(String topicmapID, int topicmapVersion, String viewmode, String topicID);

	// --- viewAssociationExists (3 forms) --- ### to be dropped

	boolean viewAssociationExists(String topicmapID, int topicmapVersion, String viewmode, String assocID);
	boolean viewAssociationExists(String topicmapID, String viewmode, String assocTypeID, String topicID1, String topicID2);
	boolean viewAssociationExists(String topicmapID, String viewmode, Vector assocTypeIDs, String topicID1, String topicID2);

	// ---

	/**
	 * Returns all views the specified topic is involved in.
	 *
	 * @return	The views as vector of {@link de.deepamehta.BaseTopic}s
	 *			(type <code>tt-topicmap</code>)
	 */
	Vector getViews(String topicID, int version, String viewmode);



	// ------------------
	// --- Properties ---
	// ------------------



	// --- Topic Properties ---

	Hashtable getTopicData(String topicID, int version);
	String getTopicData(String topicID, int version, String fieldname);

	/**
	 * @param	topicData	The topic data to store<br>
	 *						Key: field name (<code>String</code>)<br>
	 *						Value: value (<code>String</code>)
	 */
	void setTopicData(String topicID, int version, Hashtable topicData);
	void setTopicData(String topicID, int version, String field, String value);

	void deleteTopicData(String topicID, int version);

	// --- Association Properties ---

	Hashtable getAssociationData(String assocID, int version);
	String getAssociationData(String assocID, int version, String fieldname);

	/**
	 * @param	assocData	The association data to store<br>
	 *						Key: field name (<code>String</code>)<br>
	 *						Value: value (<code>String</code>)
	 */
	void setAssociationData(String assocID, int version, Hashtable assocData);
	void setAssociationData(String assocID, int version, String field, String value);

	void deleteAssociationData(String assocID, int version);



	// ------------
	// --- Misc ---
	// ------------



	String getNewTopicID();
	String getNewAssociationID();	

	int getTopicCount();
	int getAssociationCount();

	int getModelVersion();
	int getContentVersion();
	void release();
}
