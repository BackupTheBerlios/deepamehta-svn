package de.deepamehta;

import de.deepamehta.util.DeepaMehtaUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;



/**
 * A model for a "Property" (a topic of type <code>tt-property</code>).
 * <P>
 * A <CODE>PropertyDefinition</CODE> object is part of a type definition.
 * <P>
 * A <CODE>PropertyDefinition</CODE> can be serialized and send through an output stream.
 * A <CODE>PropertyDefinition</CODE> ("common" package) is created at server side and send to the client who
 * builds a {@link de.deepamehta.client PresentationPropertyDefinition} (<CODE>client</CODE> package) upon it.
 * <P>
 * <HR>
 * Last functional change: 27.9.2004 (2.0b3)<BR>
 * Last documentation update: 14.12.2000 (2.0a8-pre5)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class PropertyDefinition implements OrderedItem, DeepaMehtaConstants {

	public int ordNr;

	protected String propName;		// the name of the property

	/**
	 * The label to be displayed in the property panel, default is the property name.
	 * <P>
	 * Note: topics with a custom implementation can set property label by overriding
	 * the propertyLabel() hook.
	 */
	protected String propLabel;
	protected String dataType;		// "text", "numeric", "" (default: "text") ### to be dropped
	protected String visualization;	// property visualization mode (default: "Input Field")
	protected String defaultValue;	// ### only needed at server side
	protected String editIconfile;	// the filename of the image used to edit this property
	protected Vector options;		// predefined values for enumerated types
									// (vector of PresentableTopic)

	// ### this 3 fields are realizing an optional action button appearing next to the
	// property field. The concept of action buttons is about to be changed.
	protected boolean hasActionButton;
	protected String actionCommand;
	protected String actionButtonLabel;



	// ********************
	// *** Constructors ***
	// ********************



	/**
	 * Standard constructor.
	 * <p>
	 * References checked: 23.2.2003 (2.0b3-pre1)
	 *
	 * @see		de.deepamehta.topics.TypeTopic#addPropertyDefinitions
	 * @see		de.deepamehta.topics.PropertyTopic#initPropertyDefinition
	 */
	public PropertyDefinition(String propName, String dataType, String visualization, String defaultValue,
																		String editIconfile, Vector options) {
		this.propName = propName;
		this.propLabel = /* ### new String(*/ propName;	// default label is property name ### copy required?
		this.dataType = dataType;
		this.visualization = visualization;
		this.defaultValue = defaultValue;
		this.editIconfile = editIconfile;
		this.options = options;
	}

	/**
	 * Copy constructor.
	 *
	 * @see		de.deepamehta.client.PresentationPropertyDefinition#PresentationPropertyDefinition(PropertyDefinition propDef)
	 */
	public PropertyDefinition(PropertyDefinition propDef) {
		this.propName = propDef.propName;
		this.propLabel = /* ### new String(*/ propDef.propLabel;	// ### copy required?
		this.dataType = propDef.dataType;
		this.visualization = propDef.visualization;
		this.defaultValue = propDef.defaultValue;
		this.editIconfile = propDef.editIconfile;
		this.options = propDef.options;
		//
		this.hasActionButton = propDef.hasActionButton;
		this.actionCommand = propDef.actionCommand;
		this.actionButtonLabel = propDef.actionButtonLabel;
	}

	/**
	 * Stream constructor.
	 *
	 * @see		de.deepamehta.client.PresentationPropertyDefinition#PresentationPropertyDefinition(DataInputStream in)
	 */
	protected PropertyDefinition(DataInputStream in) throws IOException {
		// --- read property definition ---
		propName = in.readUTF();
		propLabel = in.readUTF();
		dataType = in.readUTF();
		visualization = in.readUTF();
		defaultValue = in.readUTF();
		options = DeepaMehtaUtils.readTopics(in);
		// --- read action button ---
		hasActionButton = in.readBoolean();
		if (hasActionButton) {
			actionButtonLabel = in.readUTF();
			actionCommand = in.readUTF();
		}
	}



	// *************************************************************
	// *** Implementation of Interface de.deepamehta.OrderedItem ***
	// *************************************************************



	public int getOrdinalNr() {
		return ordNr;
	}



	// ***************
	// *** Methods ***
	// ***************



	/**
	 * @see		de.deepamehta.topics.FileTopic#buttonCommand
	 * @see		de.deepamehta.topics.MovieContainerTopic#buttonCommand
	 */
	public void setActionButton(String buttonLabel, String actionCommand) {
		this.hasActionButton = true;
		this.actionButtonLabel = buttonLabel;
		this.actionCommand = actionCommand;
	}

	public void setOrdinalNr(int ordNr) {
		this.ordNr = ordNr;
	}

	public String toString() {
		return "PropertyDefinition: name=\"" + getPropertyName() + "\" " +
			"visualization=\"" + getVisualization() + "\" defaultValue=\"" + getDefaultValue() + "\" " +
			"(" + options.size() + " options)";
	}

	// --- access methods ---
	
	/**
	 * @see		#toString
	 * @see		de.deepamehta.client.PresentationType#PresentationType(DataInputStream)
	 * @see		de.deepamehta.client.PropertyPanel#createPropertyFields
	 * @see		de.deepamehta.topics.FileTopic#buttonCommand
	 */
	public String getPropertyName() {
		return propName;
	}

	/**
	 * References checked: 11.11.2001 (2.0a13-pre5)
	 *
	 * @see		de.deepamehta.service.CorporateCommands#addPropertyCommamds
	 * @see		de.deepamehta.client.PropertyPanel.PropertyField#getLabel
	 */
	public String getPropertyLabel() {
		return propLabel;
	}

	/**
	 * @see		de.deepamehta.topics.TopicTypeTopic#propertyLabel
	 */
	public void setPropertyLabel(String propLabel) {
		this.propLabel = propLabel;
	}

	/**
	 * @see		de.deepamehta.client.PropertyPanel.PropertyField#isHidden
	 */
	public boolean getHidden() {
		return getVisualization().equals(VISUAL_HIDDEN);
	}

	/**
	 * @see		de.deepamehta.client.PropertyPanel.PropertyField#getText
	 * @see		de.deepamehta.client.PropertyPanel.PropertyField#setText
	 * @see		de.deepamehta.client.PropertyPanel.PropertyField#setEnabled
	 */
	public String getVisualization() {
		return visualization;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @see		de.deepamehta.service.CorporateCommands#addPropertyCommamds
	 */
	public String getEditIconfile() {
		return editIconfile;
	}

	/**
	 * References checked: 19.10.2001 (2.0a13-pre1)
	 *
	 * @return	Vector of {@link de.deepamehta.PresentableTopic}.
	 */
	public Vector getOptions() {
		return options;
	}

	public boolean hasActionButton() {
		return hasActionButton;
	}

	// ---

	/**
	 * Serialization.
	 */
	public void write(DataOutputStream out) throws IOException {
		// error check ### should never happen
		if (dataType == null) {
			throw new DeepaMehtaException("property \"" + propName + "\" not properly inited (dataType is null)");
		}
		// --- write property definition ---
		out.writeUTF(propName);
		out.writeUTF(propLabel);
		out.writeUTF(dataType);
		out.writeUTF(visualization);
		out.writeUTF(defaultValue);
		DeepaMehtaUtils.writeTopics(options, out);
		// --- write action button ---
		out.writeBoolean(hasActionButton);
		if (hasActionButton) {
			out.writeUTF(actionButtonLabel);
			out.writeUTF(actionCommand);
		}
	}
}
