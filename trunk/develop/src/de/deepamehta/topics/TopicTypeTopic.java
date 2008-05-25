package de.deepamehta.topics;

import de.deepamehta.AmbiguousSemanticException;
import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.PresentableType;
import de.deepamehta.PropertyDefinition;
import de.deepamehta.TopicInitException;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateCommands;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.Session;
import de.deepamehta.topics.helper.ArchiveFileCollector;
import de.deepamehta.topics.helper.TopicMapExporter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.File;
import java.util.Hashtable;



/**
 * A Topic Type.
 * <p>
 * <hr>
 * Last functional change: 20.5.2008 (2.0b8)<br>
 * Last documentation update: 29.11.2000 (2.0a7)<br>
 * J&ouml;rg Richter<br>
 * jri@freenet.de
 */
public class TopicTypeTopic extends TypeTopic {




	// *****************
	// *** Constants ***
	// *****************



	private static final String ICON_UPDATE_TYPEDEF = "topictype.gif";
	//
	private static final String  CMD_ASSIGN_CREATION_ICON = "assignCreationIcon";



	// **************
	// *** Fields ***
	// **************



	/**
	 * Reflects the value of the "Unique Topic Names" property.
	 */
	private boolean uniqueTopicNames;



	// *******************
	// *** Constructor ***
	// *******************



	public TopicTypeTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}



	// **********************
	// *** Defining Hooks ***
	// **********************



	// ------------------
	// --- Life Cycle ---
	// ------------------



	public CorporateDirectives init(int initLevel, Session session) throws TopicInitException {
		if (initLevel == INITLEVEL_1) {
			uniqueTopicNames = getProperty(PROPERTY_UNIQUE_TOPIC_NAMES).equals(SWITCH_ON);
		}
		//
		return super.init(initLevel, session);
	}

	/**
	 * Overridden to perform some work when a topic type is bring into live the very
	 * first time.
	 * <p>
	 * If a new topic type is created a corresponding search type is created too.
	 *
	 * @see		de.deepamehta.service.ApplicationService#createLiveTopic(
	 *			BaseTopic topic, boolean evoke, boolean override, Session session)
	 */
	public CorporateDirectives evoke(Session session, String topicmapID, String viewmode) throws TopicInitException {
		CorporateDirectives directives = super.evoke(session, topicmapID, viewmode);
		try {
			// --- derive type from "Topic" ---
			if (getSupertype() == null) {
				String assocID = as.getNewAssociationID();
				cm.createAssociation(assocID, 1, ASSOCTYPE_DERIVATION, 1, TOPICTYPE_TOPIC, 1, getID(), 1);
				cm.setAssociationData(assocID, 1, PROPERTY_OWNER_ID, session.getUserID());
			}
			// --- create search type ---
			createSearchType(topicmapID, session, directives);
		} catch (DeepaMehtaException e) {
			throw new TopicInitException("No search type created for \"" + getName() + "\" (" + e.getMessage() + ")");
		}
		return directives;
	}



	// ------------------------------------------
	// --- Reacting upon dedicated situations ---
	// ------------------------------------------



	/**
	 * @see		de.deepamehta.service.ApplicationService#changeTopicName
	 */
	public CorporateDirectives nameChanged(String name, String topicmapID, Session session) {
		// >>> compare to UserTopic.nameChanged()
		// >>> compare to WorkspaceTopic.nameChanged()
		//
		CorporateDirectives directives = super.nameChanged(name, topicmapID, session);
		//
		if (isSearchType()) {
			System.out.println("> TopicTypeTopic.nameChanged(): " + this + " is a search type -- do nothing");
			return directives;
		}
		// --- rename search type ---
		BaseTopic searchType = as.getSearchType(getID());
		// error check ### move to getSearchType()
		if (searchType == null) {
			System.out.println("*** TopicTypeTopic.nameChanged(): the search-" +
				"type of type " + this + " is unknown -- search-type not renamed");
			directives.add(DIRECTIVE_SHOW_MESSAGE, "The search-type of type \"" + getName() +
				"\" is unknown", new Integer(NOTIFICATION_WARNING));
			return directives;
		}
		//
		String searchTypeName = as.searchTypeName(name);
		directives.add(as.setTopicProperty(searchType, PROPERTY_NAME, searchTypeName, topicmapID, session));
		//
		return directives;
	}

	public boolean deleteAllowed(Session session) {
		// deleting this type is only allowed if there are no instances of this type.
		// ### must include all the subtypes.
		// ### reconsider deletion of search types.
		return !isSearchType() && cm.getTopics(getID()).size() == 0;
	}



	// --------------------------
	// --- Providing Commands ---
	// --------------------------



	public CorporateCommands contextCommands(String topicmapID, String viewmode, Session session,
																	CorporateDirectives directives) {
		CorporateCommands commands = super.contextCommands(topicmapID, viewmode, session, directives);
		int editorContext = as.editorContext(topicmapID);
		//
		// --- "Update" command ---
		commands.addSeparator();
		commands.addCommand(ITEM_UPDATE_TYPEDEF, CMD_UPDATE_TYPEDEF, FILESERVER_ICONS_PATH, ICON_UPDATE_TYPEDEF);
		//
		commands.addStandardCommands(this, editorContext, viewmode, session, directives);
		//
		return commands;
	}

	public static void buttonCommand(PropertyDefinition propDef, ApplicationService as, Session session) {
		String propName = propDef.getPropertyName();
		if (propName.equals(PROPERTY_CREATION_ICON)) {
			propDef.setActionButton(as.string(BUTTON_ASSIGN_FILE), CMD_ASSIGN_CREATION_ICON);
		} else {
			TypeTopic.buttonCommand(propDef, as, session);
		}
	}



	// --------------------------
	// --- Executing Commands ---
	// --------------------------



	public CorporateDirectives executeCommand(String actionCommand, Session session, String topicmapID, String viewmode) {
		if (actionCommand.equals(CMD_UPDATE_TYPEDEF)) {
			CorporateDirectives directives = new CorporateDirectives();
			PresentableType typeTopic = new PresentableType(this);
			directives.add(DIRECTIVE_UPDATE_TOPIC_TYPE, typeTopic);
			return directives;
		} else if (actionCommand.equals(CMD_ASSIGN_CREATION_ICON)) {
			CorporateDirectives directives = new CorporateDirectives();
			directives.add(DIRECTIVE_CHOOSE_FILE);
			return directives;
		} else {
			return super.executeCommand(actionCommand, session, topicmapID, viewmode);
		}
	}

	public CorporateDirectives executeChainedCommand(String command, String result,
															String topicmapID, String viewmode, Session session) {
		if (command.equals(CMD_ASSIGN_CREATION_ICON)) {
			CorporateDirectives directives = new CorporateDirectives();
			// Note: the result of a DIRECTIVE_CHOOSE_FILE contains the absolute
			// path of the (client side) selected icon file
			copyAndUpload(result, FILE_IMAGE, PROPERTY_CREATION_ICON, session, directives);
			return directives;
		} else {
			return super.executeChainedCommand(command, result, topicmapID, viewmode, session);
		}
	}



	// ---------------------------
	// --- Handling Properties ---
	// ---------------------------



	/**
	 * @see		TypeTopic#makeTypeDefinition
	 */
	/* ### public static void propertyLabel(PropertyDefinition propertyDef, ApplicationService as, Session session) {
		if (propertyDef.getPropertyName().equals(PROPERTY_ICON)) {
			propertyDef.setPropertyLabel("Type Icon");
		}
	} */



	// *******************************************************
	// *** Implementation of abstract methods of TypeTopic ***
	// *******************************************************



	/**
	 * Returns the implementing class.
	 *
	 * @see		de.deepamehta.service.ApplicationService#createLiveTopic
	 */
	public String getImplementingClass() {
		String implClass = getDerivedImplementation();
		if (implClass == null) {
			implClass = ACTIVE_TOPIC_PACKAGE + ".LiveTopic";
		}
		return implClass;
	}

	// --- exportTypeDefinition (2 forms) ---

	/**
	 * Exports the type definition to a SAX handler.
	 *
	 * @param   handler	    this object will get the generated SAX events
	 * @param   collector   collects document and icon files for export
	 *
	 * @see     TopicMapTopic#exportTypeDefinitions
	 */
	public void exportTypeDefinition(ContentHandler handler, ArchiveFileCollector collector) throws SAXException {
		TypeTopic supertype = getSupertype();
		TypeTopic containertype = getSearchType();
		Hashtable attributes = new Hashtable();
		attributes.put("ID", getID());
		if (supertype != null) {
			attributes.put("supertypeID", supertype.getID());
		}
		if (containertype != null) {
			attributes.put("containertypeID", containertype.getID());
		}
		attributes.put("icon", getProperty(PROPERTY_ICON));
		attributes.put("creationicon", getCreationIcon());
		attributes.put("color", getProperty(PROPERTY_COLOR));
		attributes.put("disabled", getDisabled() ? SWITCH_ON : SWITCH_OFF);
		attributes.put("hiddennames", getHiddenTopicNames() ? SWITCH_ON : SWITCH_OFF);
		attributes.put("unified", getUniqueTopicNames() ? SWITCH_ON : SWITCH_OFF);
		attributes.put("implementation", getCustomImplementation());
		TopicMapExporter.startElement(handler, "topictype", attributes);
		TopicMapExporter.startElement(handler, "name", null);
		TopicMapExporter.characters(handler, getName());
		TopicMapExporter.endElement(handler, "name");
		TopicMapExporter.startElement(handler, "pluralname", null);
		TopicMapExporter.characters(handler, getPluralName());
		TopicMapExporter.endElement(handler, "pluralname");
		TopicMapExporter.startElement(handler, "description", null);
		TopicMapExporter.characters(handler, getProperty(PROPERTY_DESCRIPTION));
		TopicMapExporter.endElement(handler, "description");
		TopicMapExporter.startElement(handler, "descriptionquery", null);
		TopicMapExporter.characters(handler, getProperty(PROPERTY_TYPE_DESCRIPTION_QUERY));
		TopicMapExporter.endElement(handler, "descriptionquery");
		//
		exportPropertyDefinitions(handler, collector);
		//
		TopicMapExporter.endElement(handler, "topictype");
		// --- save icon and creation icon
		if (collector != null) {
			collector.putIcon(getIconfile());
//			collector.putIcon(getCreationIconfile());       // ### in image subdirectory
		}
	}

	public void exportTypeDefinition(Document doc) {
		Element root = doc.getDocumentElement();
		Element topictype = doc.createElement("topictype"); 
		root.appendChild(topictype);
		// --- attributes ---
		TypeTopic supertype = getSupertype();
		TypeTopic containertype = getSearchType();
		topictype.setAttribute("ID", getID());
		if (supertype != null) {
			topictype.setAttribute("supertypeID", supertype.getID());
		}
		if (containertype != null) {
			topictype.setAttribute("containertypeID", containertype.getID());
		}
		topictype.setAttribute("icon", getProperty(PROPERTY_ICON));
		topictype.setAttribute("creationicon", getCreationIcon());
		topictype.setAttribute("color", getProperty(PROPERTY_COLOR));
		topictype.setAttribute("disabled", getDisabled() ? SWITCH_ON : SWITCH_OFF);
		topictype.setAttribute("hiddennames", getHiddenTopicNames() ? SWITCH_ON : SWITCH_OFF);
		topictype.setAttribute("unified", getUniqueTopicNames() ? SWITCH_ON : SWITCH_OFF);
		topictype.setAttribute("implementation", getCustomImplementation());
		// --- elements ---
		// name
		Element nameElement = doc.createElement("name");
		topictype.appendChild(nameElement);
		nameElement.appendChild(doc.createTextNode(getName()));
		// pluralname
		Element pluralname = doc.createElement("pluralname");
		topictype.appendChild(pluralname);
		pluralname.appendChild(doc.createTextNode(getPluralName()));
		// description
		Element description = doc.createElement("description");
		topictype.appendChild(description);
		description.appendChild(doc.createTextNode(getProperty(PROPERTY_DESCRIPTION)));
		// descriptionquery
		Element descriptionquery = doc.createElement("descriptionquery");
		topictype.appendChild(descriptionquery);
		descriptionquery.appendChild(doc.createTextNode(getProperty(PROPERTY_TYPE_DESCRIPTION_QUERY)));
		// property definitions
		exportPropertyDefinitions(doc, topictype);
	}

	// ---

	/**
	 * @see		#nameChanged
	 * @see		#deleteAllowed
	 * @see		de.deepamehta.service.CorporateCommands#addTypeCommands
	 */
	public boolean isSearchType() {
		return hasSupertype(TOPICTYPE_SEARCH);
	}

	// ---

	/**
	 * @see		TypeTopic#init
	 */
	protected void setTypeAppearance() throws AmbiguousSemanticException {
		// ### compare to LiveTopic.setAppearance()
		// ### compare to AssociationTypeTopic.setTypeAppearance()
		this.typeAppMode = APPEARANCE_DEFAULT;
		//
		TypeTopic typeTopic = this;	// type to check
		boolean performed = false;
		// loop through supertypes of this type
		do {
			String color = getProperty(typeTopic, PROPERTY_COLOR);
			String icon = getProperty(typeTopic, PROPERTY_ICON);
			if (!icon.equals("")) {
				this.typeAppMode = APPEARANCE_CUSTOM_ICON;
				this.typeAppParam = icon;
				performed = true;
			} else if (!color.equals("")) {
				this.typeAppMode = APPEARANCE_CUSTOM_COLOR;
				this.typeAppParam = color;
				performed = true;
			}
			// set type to check to supertype
			typeTopic = typeTopic.getSupertype();  // may throw AmbiguousSemanticException
		} while (!performed && typeTopic != null);
	}



	// ***************
	// *** Methods ***
	// ***************



	/**
	 * Note: this method overrides {@link de.deepamehta.topics.LiveTopic#setIconfile}.
	 *
	 * @see		de.deepamehta.topics.LiveTopic#init
	 */
	protected void setIconfile(CorporateDirectives directives) {
		// >>> compare to LiveTopic.setIconfile()
		// >>> compare to AssociationTypeTopic.setIconfile()
		boolean isDynamicIcon = false;
		String color = null;
		switch (typeAppMode) {
		case APPEARANCE_DEFAULT:
			color = COLOR_DEFAULT;
			isDynamicIcon = true;
			this.iconfile = "tt-" + dynIcon(color);
			break;
		case APPEARANCE_CUSTOM_COLOR:
			color = typeAppParam;
			isDynamicIcon = true;
			this.iconfile = "tt-" + dynIcon(color);
			break;
		case APPEARANCE_CUSTOM_ICON:
			this.iconfile = typeAppParam;
			break;
		default:
			throw new DeepaMehtaException("unexpected type appearance mode: " + typeAppMode);
		}
		// --- create type icon dynamically ---
		File file = new File(FILESERVER_ICONS_PATH + iconfile);
		boolean fileExists = file.exists();
		// error check
		if (!fileExists && !isDynamicIcon) {
			System.out.println("*** TopicTypeTopic.setIconfile(): " + this + ": " +
				iconfile + " doesn't exist");
			return;
		}
		//
		if (!fileExists && isDynamicIcon) {
			createIconfile(file, TYPE_TT, color, directives);
		}
	}

	public boolean getUniqueTopicNames() {
		return uniqueTopicNames;
	}



	// ***********************
	// *** Private Methods ***
	// ***********************



	/**
	 * Creates the corresponding search type for this type.
	 *
	 * @see		#evoke
	 */
	private void createSearchType(String topicmapID, Session session, CorporateDirectives directives) throws DeepaMehtaException {
		String containerTypeID = as.getNewTopicID();
		String searchTypeName = as.searchTypeName(getName());		// may throw
		// Note: the search type must be created directly in CM, especially evoke() is not called
		cm.createTopic(containerTypeID, 1, TOPICTYPE_TOPICTYPE, 1, searchTypeName);
		//
		TypeTopic superType = getSupertype();
		System.out.println(">>> TopicTypeTopic.createSearchType(): supertype of " + this + " is " + superType);
		// --- trigger getSearchTypeID() ---
		String searchTypeID = as.triggerGetSearchTypeID(superType.getID());
		//
		// derive search type from TOPICTYPE_TOPIC_SEARCH ("tt-topiccontainer")
		cm.createAssociation(as.getNewAssociationID(), 1, ASSOCTYPE_DERIVATION, 1, searchTypeID, 1, containerTypeID, 1);
		// associate this type with search type
		cm.createAssociation(as.getNewAssociationID(), 1, SEMANTIC_CONTAINER_TYPE, 1, containerTypeID, 1, getID(), 1);
		// set properties of search type
		cm.setTopicData(containerTypeID, 1, PROPERTY_NAME, searchTypeName);
		cm.setTopicData(containerTypeID, 1, PROPERTY_OWNER_ID, session.getUserID());
	}
}
