package de.deepamehta.service;

import de.deepamehta.BaseTopic;
import de.deepamehta.BaseAssociation;
import java.util.*;



/**
 * This interface specifies the storage layer.
 * <P>
 * <IMG SRC="../../../../../images/3-tier-cm.gif">
 * <P>
 * <HR>
 * Last functional change: 24.5.2006 (2.0b6-post3)<BR>
 * Last documentation update:  1.1.2000 (2.0a8)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public interface CorporateMemory {

	void release();


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
	 * @return	Vector of <CODE>String</CODE>s.
	 *
	 * @see		ApplicationService#getAllTopics(String typeID, String topicmapID, String viemode)
	 */
	Vector getTopicIDs(String typeID, String topicmapID);
	Vector getTopicIDs(String typeID, String topicmapID, boolean sortByTopicName);



	// -------------------------------
	// --- Retrieving Associations ---
	// -------------------------------



	BaseAssociation getAssociation(String id, int version);
	BaseAssociation getAssociation(String type, String topicID1, String topicID2);
	BaseAssociation getAssociation(Vector types, String topicID1, String topicID2);
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
	boolean associationExists(String topicID1, String topicID2, boolean ignoreDirection);
	boolean associationExists(String topicID1, String topicID2, Vector assocTypeIDs);



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
	 * @return	2-element array:<BR>
	 *				element 1: vector of {@link de.deepamehta.PresentableTopic}<BR>
	 *				element 2: vector of {@link de.deepamehta.PresentableAssociation}
	 */
	Vector[] getRelatedViewTopicsByTopictype(String topicID, String relTopicTypeID);
	Vector[] getRelatedViewTopicsByTopictype(String topicID, String relTopicTypeID, int relTopicPos, String assocTypeID);

	/**
	 * @return	2-element array:<BR>
	 *				element 1: vector of {@link de.deepamehta.PresentableTopic}<BR>
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
	 *			(type <CODE>tt-topicmap</CODE>)
	 */
	Vector getViews(String topicID, int version, String viewmode);



	// ------------------
	// --- Properties ---
	// ------------------



	// --- Topic Properties ---

	Hashtable getTopicData(String topicID, int version);
	String getTopicData(String topicID, int version, String fieldname);

	/**
	 * @param	topicData	The topic data to store<BR>
	 *						Key: field name (<CODE>String</CODE>)<BR>
	 *						Value: value (<CODE>String</CODE>)
	 */
	void setTopicData(String topicID, int version, Hashtable topicData);
	void setTopicData(String topicID, int version, String field, String value);

	void deleteTopicData(String topicID, int version);

	// --- Association Properties ---

	Hashtable getAssociationData(String assocID, int version);
	String getAssociationData(String assocID, int version, String fieldname);

	/**
	 * @param	assocData	The association data to store<BR>
	 *						Key: field name (<CODE>String</CODE>)<BR>
	 *						Value: value (<CODE>String</CODE>)
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
}
