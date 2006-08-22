package de.deepamehta;

import java.io.*;



/**
 * Basis model of a {@link Topic} as stored in corporate memory.
 * <P>
 * <CODE>BaseTopic</CODE> adds the <CODE>version</CODE> field and the
 * <CODE>getVersion()</CODE> method -- the base for version control.
 * <P>
 * <CODE>BaseTopic</CODE> adds the <CODE>write()</CODE> method -- the base for
 * transmitting a topic over a network connection.
 * <P>
 * <HR>
 * Last functional change: 28.6.2002 (2.0a15-pre9)<BR>
 * Last documentation update: 15.12.2001 (2.0a14-pre5)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class BaseTopic implements Topic, Serializable {



	// **************
	// *** Fields ***
	// **************



	protected String id;
	protected String type;
	protected String name;
	//
	protected int version;
	protected int typeVersion;

	// transient
	public int ordNr;



	// ********************
	// *** Constructors ***
	// ********************



	/**
	 * Standard constructor.
	 *
	 * @see		de.deepamehta.service.RelationalCorporateMemory#queryBaseTopics(String query)
	 * @see		de.deepamehta.topics.TopicMapTopic#openGroupView
	 */
	public BaseTopic(String id, int version, String type, int typeVersion, String name) {
		this.id = id;
		this.version = version;
		this.type = type;
		this.typeVersion = typeVersion;
		this.name = name;
	}

	/**
	 * Stream constructor.
	 *
	 * @see		de.deepamehta.client.DeepaMehtaClient#showWorkspace
	 * @see		de.deepamehta.client.DeepaMehtaClient#showView
	 * @see		de.deepamehta.client.DeepaMehtaClientUtils#readTopics
	 */
	public BaseTopic(DataInputStream in) throws IOException {
		this.id = in.readUTF();
		this.version = in.readInt();
		this.type = in.readUTF();
		this.typeVersion = in.readInt();
		this.name = in.readUTF();
	}

	/**
	 * Copy constructor.
	 *
	 * @see		PresentableTopic#PresentableTopic(BaseTopic topic, Point geometry)
	 */
	public BaseTopic(BaseTopic topic) {
		this.id = topic.getID();
		this.version = topic.getVersion();
		this.type = topic.getType();
		this.typeVersion = topic.getTypeVersion();
		this.name = topic.getName();
	}



	// ***************
	// *** Methods ***
	// ***************



	public boolean equals(Object obj) {
		return obj != null && ((BaseTopic) obj).id.equals(id);
	}

	// ---

	/**
	 * Initializer.
	 */
	/* ### public void set(BaseTopic topic) {
		this.id = topic.id;
		this.type = topic.type;
		this.name = topic.name;
		this.version = topic.version;
		this.typeVersion = topic.typeVersion;
	} */

	public void setOrdinalNr(int ordNr) {
		this.ordNr = ordNr;
	}
/*
	public BaseTopic cloneSelf() throws CloneNotSupportedException {
		return (BaseTopic) clone();
	}
*/
	public String toString() {
		return type + ":" + typeVersion + " \"" + name + "\" (" + id + ":" + version + ")";
	}

	public int getVersion() {
		return version;
	}

	public int getTypeVersion() {
		return typeVersion;
	}



	// ---------------------
	// --- Serialization ---
	// ---------------------



	public void write(DataOutputStream out) throws IOException {
		out.writeUTF(getID());
		out.writeInt(getVersion());
		out.writeUTF(getType());
		out.writeInt(getTypeVersion());
		out.writeUTF(getName());
	}



	// *******************************************************
	// *** Implementation of interface de.deepamehta.Topic ***
	// *******************************************************



	public String getID() {
		return id;
	}

	public String getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	// ---

	public void setID(String id) {
		this.id = id;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setName(String name) {
		this.name = name;
	}
}
