package de.deepamehta;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;



/**
 * Type Model.
 * A type is a topic type or an association type.
 * <P>
 * A <CODE>PresentableType</CODE> can be serialized and send through an output stream.
 * A <CODE>PresentableType</CODE> is created at server side and send to the client
 * who builds a {@link de.deepamehta.client.PresentationType} upon it.
 * <P>
 * <HR>
 * Last functional change: 7.4.2003 (2.0a18-pre9)<BR>
 * Last documentation update: 9.10.2002 (2.0a16-pre5)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class PresentableType implements Type, DeepaMehtaConstants {



	// **************
	// *** Fields ***
	// **************



	protected String id;
	protected String name;
	protected int version;

	/**
	 * The type definition, used for implementation of the {@link Type} interface.
	 * <P>
	 * ### Key: property name (<CODE>String</CODE>)<BR>
	 * ### Value: property definition ({@link PropertyDefinition})
	 * <P>
	 * Initialized by {@link #setTypeDefinition}.<BR>
	 * Accessed by {@link #getTypeDefinition}.
	 */
	protected Vector typeDefinition = new Vector();

	/**
	 * The the name of the iconfile used to draw this type.
	 * <P>
	 * Initialized by {@link #setTypeIconfile}.<BR>
	 * Accessed by {@link #getTypeIconfile}.
	 */
	protected String typeIconfile;

	/**
	 * Association type color.
	 * <P>
	 * Initialized by {@link #setAssocTypeColor}.<BR>
	 * Accessed by {@link #getAssocTypeColor}.
	 */
	protected String assocTypeColor;

	/**
	 * Initialized by {@link #setDisabled}.<BR>
	 * Accessed by {@link #getDisabled}.
	 */
	protected boolean disabled;

	/**
	 * Initialized by {@link #setHiddenTopicNames}.<BR>
	 * Accessed by {@link #getHiddenTopicNames}.
	 */
	protected boolean hiddenTopicNames;

	/**
	 * Initialized by {@link #setSearchType}.<BR>
	 * Accessed by {@link #isSearchType}.
	 */
	protected boolean isSearchType;



	// ********************
	// *** Constructors ***
	// ********************



	public PresentableType() {
	}

	public PresentableType(String id, String name, int version) {
		this.id = id;
		this.name = name;
		this.version = version;
	}

	public PresentableType(PresentableType type) {
		this(type.getID(), type.getName(), type.getVersion());
	}

	public PresentableType(BaseTopic topic) {
		this(topic.getID(), topic.getName(), topic.getVersion());
	}



	// ******************************************************
	// *** Implementation of interface de.deepamehta.Type ***
	// ******************************************************



	public String getID() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getVersion() {
		return version;
	}

	/**
	 * @see		de.deepamehta.service.ApplicationService#initTypeTopic
	 * @see		de.deepamehta.topics.LiveTopic#getDetail
	 * @see		de.deepamehta.topics.ContainerTopic#getDetail
	 */
	public Vector getTypeDefinition() {
		return typeDefinition;
	}

	public boolean isSearchType() {
		return isSearchType;
	}



	// ***************
	// *** Methods ***
	// ***************



	/**
	 * References checked: 27.12.2001 (2.0a14-pre5)
	 *
	 * @see		de.deepamehta.client.PresentationType(PresentableType, PresentationService)
	 */
	public String getTypeIconfile() {
		return typeIconfile;
	}

	/**
	 * References checked: 24.12.2001 (2.0a14-pre5)
	 *
	 * @see		de.deepamehta.client.PresentationType(PresentableType, PresentationService)
	 */
	public String getAssocTypeColor() {
		return assocTypeColor;
	}

	/**
	 * References checked: 9.10.2002 (2.0a16-pre5)
	 *
	 * @see		de.deepamehta.client.PresentationType(PresentableType, PresentationService)
	 * @see		de.deepamehta.client.GraphPanel#nodeClicked
	 * @see		de.deepamehta.client.GraphPanel#edgeClicked
	 */
	public boolean isDisabled() {
		return disabled;
	}

	/**
	 * References checked: 9.10.2002 (2.0a16-pre5)
	 *
	 * @see		de.deepamehta.client.PresentationType(PresentableType, PresentationService)
	 * @see		de.deepamehta.client.GraphPanel#paintNode
	 */
	public boolean getHiddenTopicNames() {
		return hiddenTopicNames;
	}

	// ---

	/**
	 * References checked: 30.7.2001 (2.0a11)
	 *
	 * @see		de.deepamehta.service.ApplicationService#initTypeTopic
	 */
	public void setTypeDefinition(Vector typeDefinition) {
		if (typeDefinition == null) {
			throw new DeepaMehtaException("\"null\" is passed for \"typeDefinition\"");
		}
		this.typeDefinition = typeDefinition;
	}

	/**
	 * References checked: 30.7.2001 (2.0a11)
	 *
	 * @see		de.deepamehta.service.ApplicationService#initTypeTopicAppearance
	 */
	public void setTypeIconfile(String typeIconfile) {
		this.typeIconfile = typeIconfile;
	}

	/**
	 * References checked: 30.7.2001 (2.0a11)
	 *
	 * @see		de.deepamehta.service.ApplicationService#initTypeTopicAppearance
	 */
	public void setAssocTypeColor(String color) {
		this.assocTypeColor = color;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public void setHiddenTopicNames(boolean hidden) {
		this.hiddenTopicNames = hidden;
	}

	public void setSearchType(boolean isSearchType) {
		this.isSearchType = isSearchType;
	}



	// ---------------------
	// --- Serialization ---
	// ---------------------



	/**
	 * Serializes this <CODE>PresentableType</CODE> and writes it to the specified
	 * output stream.
	 * <P>
	 * First calls <CODE>super.write(out)</CODE>, then adds type definition, the
	 * appearance data and the "disabled" and "hiddenTopicNames" flags.
	 */
	public void write(DataOutputStream out) throws IOException {
		// --- id, name, version ---
		out.writeUTF(getID());
		out.writeUTF(getName());
		out.writeInt(getVersion());
		// --- type definition ---
		out.writeInt(getTypeDefinition().size());
		Enumeration e = getTypeDefinition().elements();
		while (e.hasMoreElements()) {
			((PropertyDefinition) e.nextElement()).write(out);
		}
		// --- type icon ---
		out.writeUTF(typeIconfile);
		// --- association type color ---
		// Note: assocTypeColor is only initialized for association types.
		// For topic types assocTypeColor remains uninitialized (null)
		out.writeUTF(assocTypeColor != null ? assocTypeColor : "");
		// --- flags ---
		out.writeBoolean(disabled);
		out.writeBoolean(hiddenTopicNames);
		out.writeBoolean(isSearchType);
	}
}
