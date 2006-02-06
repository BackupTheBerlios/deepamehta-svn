package de.deepamehta.service;

import de.deepamehta.BaseTopic;
import de.deepamehta.BaseAssociation;
import de.deepamehta.environment.instance.CorporateMemoryConfiguration;

import java.util.*;



/**
 * This interface specifies the storage layer.
 * <P>
 * <IMG SRC="../../../../../images/3-tier-cm.gif">
 * <P>
 * <HR>
 * Last functional change: 2.1.2005 (2.0b4)<BR>
 * Last documentation update:  1.1.2000 (2.0a8)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public interface CorporateMemory {

	// ----------------------------------------
	// --- Bootstrapping, Startup, Shutdown ---
	// ----------------------------------------
	
	/**
	 * This method determines the properties supported by this corpororate memory
	 * implementation. If no properties are supported, the implementation may return
	 * either an empty Vector or null.
	 * @return A Vector of Strings containing the names of the properties supported 
	 * by the implementation.
	 */
	Vector getSupportedProperties();
	
	/**
	 * This method determines the values that a single property may assume. If the property
	 * may asssume any value, the implementation should return null. Note that an empty vector
	 * returned may lead to inconsistencies in the user interface.
	 * @param propertyName The name of the property to examine.
	 * @return A Vector of Strings representing the valid values.
	 */
	Vector getSupportedPropertyValues(String propertyName);
	
	/**
	 * This method determines the default value for a property. The default value must be
	 * part of the supported values if the valid values of the property are restricted (that is,
	 * if getSupportedPropertyValues return a non-null value. 
	 * @param propertyName The name of the property to examine.
	 * @return The default value of the property.
	 */
	String getDefaultPropertyValue(String propertyName);
	
	/**
	 * This method is called during the setup process to check whether the storage
	 * area (e. g. the file, directory, database, ...) is accessible. If this method 
	 * returns true, the caller may safely assume that no further action is required 
	 * to prepare the storage area for further usage. If this method returns false, the 
	 * caller may try to call setupStorageArea to prepare the area. Please note that 
	 * any kind of connection required to check the storage area should be closed before 
	 * leaving the method.
	 * @param config The Corporate Memory configuration to check.
	 * @return true if the storage area described by the properties of config is 
	 * usable, false otherwise.
	 */
	boolean checkStorageArea(CorporateMemoryConfiguration config);

	/**
	 * This method is called during the setup proces if checkStorageArea returns false.
	 * It can be used to prepare the storage area (e. g. create database and user). 
	 * This method should return true if the storage area is usable after the call, 
	 * regardless whether any action has been taken. Note that returning false will 
	 * in all likelyhood cause the setup process to fail. Please note also that any 
	 * kind of connection required to check the storage area should be closed before 
	 * leaving the method.
	 * @param config The Corporate Memory Configuration to prepare.
	 * @return true if the storage area is usable after the call.
	 */
	boolean setupStorageArea(CorporateMemoryConfiguration config);
	
	/**
	 * This method is called during the setup process to check whether the storage
	 * area contains the structures (e. g. the database tables, ...) necessary to run 
	 * DeepaMehta. If this method returns true, the caller may safely assume that no 
	 * further action is required to prepare the structures for further usage. If 
	 * this method returns false, the caller may try to call setupStructure to prepare 
	 * the structures. Please note that any kind of connection required to check the 
	 * structures should be closed before leaving the method.  
	 * @param config The Corporate Memory configuration pointing to a storage location
	 * whose structures are to be checked.
	 * @return true if the structures are in a usable state.
	 */
	boolean checkStructure(CorporateMemoryConfiguration config);
	
	/**
	 * This method is called during the setup process if checkStructure returns false.
	 * It can be used to setup the structures required to run DeepaMehta (e. g. create 
	 * the database tables). This method should return true if the structures are 
	 * usable after the call, regardless whether any action has been taken. Note that 
	 * returning false will in all likelyhood cause the setup process to fail.  
	 * Please note also that any kind of connection required to check the structures 
	 * should be closed before leaving the method.
	 * @param config The Corporate Memory configuration pointing to a storage location
	 * whose structures are to be created.
	 * @return true if the structures are in a usable state.
	 */
	boolean setupStructure(CorporateMemoryConfiguration config);
	
	/**
	 * Changes the state of a key generator. <b>Warning:</b> This method is intended 
	 * for use during the bootstrap process <b>ONLY!</b>
	 * @param genName The generator to change.
	 * @param nextKey The value the generator should return upon next usage.
	 */
	public void setKeyGenerator(String genName, int nextKey);
	
	/**
	 * This method is used to start the Corporate Memory - whatever that might mean for 
	 * the actual implementation. A relational implementation may want to connect to the
	 * database server at this point, for example.
	 * @param config The Corporate Memory configuration defining the storage location.
	 * @param isBootstrap This parameter is set to <code>true</code> during the 
	 * bootstrapping process only; during normal operation it is set to <code>false</code>.
	 * This might be interesting for some implementations that may want to perform 
	 * additional checks during normal startup that cannot be performed during the
	 * bootstrapping process.  
	 * @return <code>true</code> if the Corporate Memory instance is up and running.
	 */
	boolean startup(CorporateMemoryConfiguration config, boolean isBootstrap);
	
	/**
	 * This method is used to shutdown the Corporate Memory and cleanup whatever resources
	 * have been allocated.
	 */
	void shutdown();


	// -------------------------
	// --- Retrieving Topics ---
	// -------------------------



	/**
	 * Retrieves a {@link de.deepamehta.BaseTopic} from the corporate memory specified 
	 * by ID and version.
	 * @param id The ID of the topic to retrieve.
	 * @param version The version of the topic to retrieve.
	 * @return The {@link de.deepamehta.BaseTopic} specified by <code>id</code> and 
	 * <code>version</code>, or <code>null</code> if no topic with this key 
	 * exists. Note that if multiple topics are found,
	 * it is not defined which of the topics is returned.
	 */
	BaseTopic getTopic(String id, int version);
	
	/**
	 * Retrieves a {@link de.deepamehta.BaseTopic} from the corporate memory specified
	 * by type, name and version
	 * @param type The ID of the topic type of the topic to retrieve.
	 * @param name The name of the topic to retrieve.
	 * @param version The version of the topic to retrieve.
	 * @return The {@link de.deepamehta.BaseTopic} specified by <code>type</code>, 
	 * <code>name</code> and <code>version</code>, or null if no topic meets
	 * the specified parameters. Note that if multiple topics are found,
	 * it is not defined which of the topics is returned.
	 */
	BaseTopic getTopic(String type, String name, int version);
	
	/**
	 * Retrieve all topics from the corporate memory. Note: This method will 
	 * very likely return a huge result set. Use only as a last resort and
	 * if you really know what you're doing (e. g. for exporting the entire
	 * corporate memory).
	 * @return A <code>Vector</code> of all {@link de.deepamehta.BaseTopic}s contained
	 * in the corporate memory.
	 */
	Vector getTopics();
	
	/**
	 * Retrieves all topics of a certain topic type.
	 * @param typeID The ID of the topic type to search for.
	 * @return A <code>Vector</code> of all {@link de.deepamehta.BaseTopic}s of type
	 * <code>typeID</code> contained in the corporate memory.
	 */
	Vector getTopics(String typeID);
	
	/**
	 * Retrieves all topics of a certain topic type that match a certain name 
	 * filter.
	 * @param typeID The ID of the topic type to search for.
	 * @param nameFilter The name pattern to search for.
	 * @return A <code>Vector</code> of all {@link de.deepamehta.BaseTopic}s of type
	 * <code>typeID</code> whose name contains the String 
	 * <code>nameFilter</code>.
	 */
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
	Vector getRelatedViewTopics(String topicmapID, int version, String topicID, String assocTypeID, String relTopicTypeID,
																											int relTopicPos);

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
