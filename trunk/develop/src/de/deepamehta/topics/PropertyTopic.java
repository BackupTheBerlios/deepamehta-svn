package de.deepamehta.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.PropertyDefinition;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateCommands;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.Session;

import java.util.Enumeration;
import java.util.Vector;



/**
 * A topic resp. association property.
 * <P>
 * <HR>
 * Last functional change: 28.9.2004 (2.0b3)<BR>
 * Last documentation update: 12.11.2000 (2.0a7-pre2)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class PropertyTopic extends LiveTopic {



	// **************
	// *** Fields ***
	// **************



	// model
	private PropertyDefinition propDef;
	// ### drop this 4 variables
	private String type;	// ### to be dropped
	private String visualization;
	private String defaultValue;
	private String editIconfile;
	private Vector values = new Vector();	// element type is PresentableTopic

	// commands
	protected static String ITEM_ASSIGN_NEW_PROPERTY_VALUE = "Create Property Value";
	protected static final String  CMD_ASSIGN_NEW_PROPERTY_VALUE = "assignNewPropertyValue";
	protected static final String ICON_ASSIGN_NEW_PROPERTY_VALUE = "createProperty.gif";	// ###

	private static final String  CMD_ASSIGN_EDIT_ICON = "assignEditIcon";



	// *******************
	// *** Constructor ***
	// *******************



	public PropertyTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}



	// **********************
	// *** Defining Hooks ***
	// **********************



	// ------------------
	// --- Life Cycle ---
	// ------------------



	// actually not needed. the property model is inited (initPropertyDefinition()) while the type model is inited
	/* ### public CorporateDirectives init(int initLevel, Session session) throws TopicInitException {
		CorporateDirectives directives = super.init(initLevel, session);
		//
		if (initLevel == INITLEVEL_2) {
			// ### Note: A PropertyTopic initializes at level 2 because it relies on
			// ### involved constants beeing in live corporate memory
			// ### since the constants are preloaded we could actually init at level 1
			if (LOG_TOPIC_INIT) {
				System.out.println(">>> PropertyTopic.init(" + initLevel + "): " +
					this + " -- set property definition");
			}
			initPropertyDefinition(session, directives);
		}
		// Note: actually empty directives are returned
		return directives;
	} */

	// since 2.0b3: we use the default-value-facility instead (property "Default Value" of topic type "Property")
	/* ### public CorporateDirectives evoke(Session session, String topicmapID, String viewmode) {
		// --- set default property ---
		setProperty(PROPERTY_VISUALIZATION, VISUAL_FIELD);
		// Note: actually empty directives are returned
		return super.evoke(session, topicmapID, viewmode);
	} */



	// --------------------------
	// --- Providing Commands ---
	// --------------------------



	/**
	 * @see		de.deepamehta.service.ApplicationService#showTopicMenu
	 */
	public CorporateCommands contextCommands(String topicmapID, String viewmode,
								Session session, CorporateDirectives directives) {
		CorporateCommands commands = new CorporateCommands(as);
		int editorContext = as.editorContext(topicmapID);
		//
		commands.addNavigationCommands(this, editorContext, session);
		// --- "Create Property Value" ---
		commands.addSeparator();
		int cmdState = session.isDemo() ? COMMAND_STATE_DISABLED : COMMAND_STATE_DEFAULT;
		commands.addCommand(ITEM_ASSIGN_NEW_PROPERTY_VALUE, CMD_ASSIGN_NEW_PROPERTY_VALUE,
			FILESERVER_IMAGES_PATH, ICON_ASSIGN_NEW_PROPERTY_VALUE, cmdState);
		//
		commands.addStandardCommands(this, editorContext, viewmode, session, directives);
		//
		return commands;
	}

	public static void buttonCommand(PropertyDefinition propDef, ApplicationService as, Session session) {
		String propName = propDef.getPropertyName();
		if (propName.equals(PROPERTY_EDIT_PROPERTY_ICON)) {
			propDef.setActionButton(as.string(BUTTON_ASSIGN_FILE), CMD_ASSIGN_EDIT_ICON);
		}
	}



	// --------------------------
	// --- Executing Commands ---
	// --------------------------



	public CorporateDirectives executeCommand(String actionCommand, Session session,
													String topicmapID, String viewmode) {
		CorporateDirectives directives = new CorporateDirectives();
		//
		if (actionCommand.equals(CMD_ASSIGN_NEW_PROPERTY_VALUE)) {
			createChildTopic(TOPICTYPE_PROPERTY_VALUE, SEMANTIC_OPTION_DEFINITION, session, directives);
			return directives;
		} else if (actionCommand.equals(CMD_ASSIGN_EDIT_ICON)) {
			directives.add(DIRECTIVE_CHOOSE_FILE);
			return directives;
		} else {
			return super.executeCommand(actionCommand, session, topicmapID, viewmode);
		}
	}

	public CorporateDirectives executeChainedCommand(String command, String result,
													String topicmapID, String viewmode, Session session) {
		if (command.equals(CMD_ASSIGN_EDIT_ICON)) {
			CorporateDirectives directives = new CorporateDirectives();
			// Note: the result of a DIRECTIVE_CHOOSE_FILE contains the absolute
			// path of the (client side) selected icon file
			copyAndUpload(result, FILE_IMAGE, PROPERTY_EDIT_PROPERTY_ICON, session, directives);
			return directives;
		} else {
			return super.executeChainedCommand(command, result, topicmapID, viewmode, session);
		}
	}



	// **********************
	// *** Custom Methods ***
	// **********************



	public PropertyDefinition getDefinition() {
		return propDef;
	}

	// ---

	String getDataType() {
		return type;
	}

	String getVisualization() {
		return visualization;
	}

	String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @see		TypeTopic#makeTypeDefinition
	 */
	String getEditIconfile() {
		return editIconfile;
	}

	/**
	 * References checked: 19.10.2001 (2.0a13-pre1)
	 *
	 * @return	Vector of {@link de.deepamehta.PresentableTopic}.
	 *
	 * @see		TypeTopic#makeTypeDefinition
	 */
	Vector getOptions() {
		return values;
	}

	// ---

	/**
	 * Initializes/Updates the property definition.
	 *
	 * @see		TypeTopic#addPropertyDefinitions
	 */
	void initPropertyDefinition(Session session, CorporateDirectives directives) {
		this.type = getProperty("Type");	// ###
		this.visualization = getProperty(PROPERTY_VISUALIZATION);
		this.defaultValue = getProperty(PROPERTY_DEFAULT_VALUE);
		this.editIconfile = getProperty(PROPERTY_EDIT_PROPERTY_ICON);
		// --- property values ---		
		values.setSize(0);		// clear the values vector (needed because this topic is re-inted)
		Enumeration topics = as.getRelatedTopics(getID(), SEMANTIC_OPTION_DEFINITION,
			TOPICTYPE_PROPERTY_VALUE, 2, true).elements();		// get all values of this property
		while (topics.hasMoreElements()) {
			BaseTopic propertyValue = (BaseTopic) topics.nextElement();
			values.addElement(as.createPresentableTopic(propertyValue));
		}
		//
		this.propDef = new PropertyDefinition(getName(), type, visualization, defaultValue, editIconfile, values);
	}
}
