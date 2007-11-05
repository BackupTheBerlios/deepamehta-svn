package de.deepamehta;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;



/**
 * Basis model of an {@link Association} as stored in corporate memory.
 * <P>
 * <CODE>BaseAssociation</CODE> adds the <CODE>version</CODE> field and the
 * <CODE>getVersion()</CODE> method -- the base for version control.
 * <P>
 * <CODE>BaseAssociation</CODE> adds the <CODE>write()</CODE> method -- the base for
 * transmitting an association over a network connection.
 * <P>
 * <HR>
 * Last functional change: 17.2.2003 (2.0a18-pre2)<BR>
 * Last documentation update: 15.12.2001 (2.0a14-pre5)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class BaseAssociation implements Association {



	// **************
	// *** Fields ***
	// **************



	protected String id;
	protected String type;
	protected String name;
	//
	protected int version;
	protected int typeVersion;
	//
	protected String topicID1;
	protected String topicID2;
	protected int topicVersion1;
	protected int topicVersion2;



	// ********************
	// *** Constructors ***
	// ********************



	/**
	 * Standard constructor.
	 *
	 * @see		de.deepamehta.service.ApplicationService#createLiveAssociation(String assocID, String typeID, String topicID1, String topicID2)
	 * @see		de.deepamehta.service.RelationalCorporateMemory#queryBaseAssociations(String query)
	 */
	public BaseAssociation(String id, int version, String type, int typeVersion, String name,
						   String topicID1, int topicVersion1,
						   String topicID2, int topicVersion2) {
		this.id = id;
		this.version = version;
		this.type = type;
		this.typeVersion = typeVersion;
		this.name = name;
		//
		this.topicID1 = topicID1;
		this.topicVersion1 = topicVersion1;
		this.topicID2 = topicID2;
		this.topicVersion2 = topicVersion2;
	}

	/**
	 * Stream constructor.
	 */
	public BaseAssociation(DataInputStream in) throws IOException {
		this.id = in.readUTF();
		this.version = in.readInt();
		this.type = in.readUTF();
		this.typeVersion = in.readInt();
		this.name = in.readUTF();
		//
		this.topicID1 = in.readUTF();
		this.topicVersion1 = in.readInt();
		this.topicID2 = in.readUTF();
		this.topicVersion2 = in.readInt();
	}

	/**
	 * Copy constructor.
	 *
	 * @see		de.deepamehta.service.LiveAssociation#LiveAssociation
	 */
	public BaseAssociation(BaseAssociation assoc) {
		this.id = assoc.getID();
		this.version = assoc.getVersion();
		this.type = assoc.getType();
		this.typeVersion = assoc.getTypeVersion();
		this.name = assoc.getName();
		//
		this.topicID1 = assoc.getTopicID1();
		this.topicVersion1 = assoc.getTopicVersion1();
		this.topicID2 = assoc.getTopicID2();
		this.topicVersion2 = assoc.getTopicVersion2();
	}



	// ***************
	// *** Methods ***
	// ***************



	public String toString() {
		return getType() + ":" + getTypeVersion() + " (" + getTopicID1() + ", " + getTopicID2() + ") (" + getID() + ":" + getVersion() + ")";
	}

	// ---

	public int getVersion() {
		return version;
	}

	public int getTypeVersion() {
		return typeVersion;
	}

	public int getTopicVersion1() {
		return topicVersion1;
	}

	public int getTopicVersion2() {
		return topicVersion2;
	}

	// ---

	/**
	 * Serialization.
	 */
	public void write(DataOutputStream out) throws IOException {
		out.writeUTF(getID());
		out.writeInt(getVersion());
		out.writeUTF(getType());
		out.writeInt(getTypeVersion());
		out.writeUTF(getName());
		//
		out.writeUTF(getTopicID1());
		out.writeInt(getTopicVersion1());
		out.writeUTF(getTopicID2());
		out.writeInt(getTopicVersion2());
	}



	// *************************************************************
	// *** Implementation of interface de.deepamehta.Association ***
	// *************************************************************



	public String getID() {
		return id;
	}

	public String getType() {
		return type;
	}
 
	public String getName() {			// ### not part of interface
		return name;
	}

	public String getTopicID1() {
		return topicID1;
	}

	public String getTopicID2() {
		return topicID2;
	}

	// ---

	public void setID(String id) {
		this.id = id;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setName(String name) {	// ### not part of interface
		this.name = name;
	}

	public void setTopicID1(String id){
		this.topicID1 = id;
	}

	public void setTopicID2(String id){
		this.topicID2 = id;
	}
}
