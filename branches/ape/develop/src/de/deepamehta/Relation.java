package de.deepamehta;

import de.deepamehta.topics.TypeTopic;



/**
 * A model for a "Relation" (an association of type <code>at-relation</code>), as part of a type definition.
 * <p>
 * A <CODE>Relation</CODE> object is part of a type definition.
 * <P>
 * <HR>
 * Last functional change: 28.2.2004 (2.0b3-pre1)<BR>
 * Last documentation update: 23.2.2004 (2.0b3-pre1)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class Relation implements OrderedItem {

	public String id;				// association ID
	public String name;				// association name ("Name" property)
	public String relTopicTypeID;	// type ID of the related topic type
	public String cardinality;		// "Cardinality" property
	public String assocTypeID;		// "Association Type ID" property
	public String webInfo;			// "Web Info" property
	public String webForm;			// "Web Form" property
	public int ordNr;				// "Ordinal Number" property



	// *******************
	// *** Constructor ***
	// *******************



	/**
	 * References checked: 23.2.2003 (2.0b3-pre1)
	 *
	 * @see		TypeTopic#addRelations
	 */
	public Relation(String id, String name, String relTopicTypeID, String cardinality, String assocTypeID,
														String webInfo, String webForm, int ordNr) {
		this.id = id;
		this.name = name;
		this.relTopicTypeID = relTopicTypeID;
		this.cardinality = cardinality;
		this.assocTypeID = assocTypeID;
		this.webInfo = webInfo;
		this.webForm = webForm;
		this.ordNr = ordNr;
	}



	// *************************************************************
	// *** Implementation of Interface de.deepamehta.OrderedItem ***
	// *************************************************************



	public int getOrdinalNr() {
		return ordNr;
	}
}
