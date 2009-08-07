package de.deepamehta.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.PresentableType;
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
 * <p>
 * <hr>
 * Last functional change: 28.10.2007 (2.0b6)<br>
 * Last documentation update: 13.11.2000 (2.0a7-pre2)<br>
 * J&ouml;rg Richter<br>
 * jri@freenet.de
 */
public class AssociationTypeTopic extends TypeTopic {



	// *****************
	// *** Constants ***
	// *****************



	private static final String ICON_UPDATE_TYPEDEF = "associationtype.gif";



	// *******************
	// *** Constructor ***
	// *******************



	public AssociationTypeTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}



	// **********************
	// *** Defining Hooks ***
	// **********************



	// ------------------
	// --- Life Cycle ---
	// ------------------



	/**
	 * Overridden to perform some work when an association type is bring into live the very
	 * first time.
	 *
	 * @see		de.deepamehta.service.ApplicationService#createLiveTopic(
	 *			BaseTopic topic, boolean evoke, boolean override, Session session)
	 */
	public CorporateDirectives evoke(Session session, String topicmapID, String viewmode) {
		CorporateDirectives directives = super.evoke(session, topicmapID, viewmode);
		// try { ### compare to TopicTypeTopic.evoke()
		// --- derive type from "Association" ---
		if (getSupertype() == null) {
			String assocID = as.getNewAssociationID();
			cm.createAssociation(assocID, 1, ASSOCTYPE_DERIVATION, 1, ASSOCTYPE_GENERIC, 1, getID(), 1);
			cm.setAssociationData(assocID, 1, PROPERTY_OWNER_ID, session.getUserID());
		}
		/* } catch (DeepaMehtaException e) {
			throw new TopicInitException("No search type created for \"" + getName() + "\" (" + e.getMessage() + ")");
		} */
		return directives;
	}



	// ------------------------------------------
	// --- Reacting upon dedicated situations ---
	// ------------------------------------------



	public boolean deleteAllowed(Session session) {
		// deleting this type is only allowed if there are no instances of this type
		// ### must include all the subtypes.
		return cm.getAssociations(getID()).size() == 0;
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



	// --------------------------
	// --- Executing Commands ---
	// --------------------------



	public CorporateDirectives executeCommand(String actionCommand, Session session, String topicmapID, String viewmode) {
		if (actionCommand.equals(CMD_UPDATE_TYPEDEF)) {
			CorporateDirectives directives = new CorporateDirectives();
			PresentableType typeTopic = new PresentableType(this);
			directives.add(DIRECTIVE_UPDATE_ASSOC_TYPE, typeTopic);
			return directives;
		} else {
			return super.executeCommand(actionCommand, session, topicmapID, viewmode);
		}
	}



	// *******************************************************
	// *** Implementation of abstract methods of TypeTopic ***
	// *******************************************************



	/**
	 * Returns the implementing class.
	 *
	 * @see		de.deepamehta.service.ApplicationService#createLiveAssociation
	 */
	public String getImplementingClass() {
		String implClass = getDerivedImplementation();
		if (implClass == null) {
			implClass = ACTIVE_ASSOC_PACKAGE + ".LiveAssociation";
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
		attributes.put("color", getProperty(PROPERTY_COLOR));
		attributes.put("disabled", getDisabled() ? SWITCH_ON : SWITCH_OFF);
		attributes.put("implementation", getCustomImplementation());
		TopicMapExporter.startElement(handler, "assoctype", attributes);
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
		exportPropertyDefinitions(handler, collector);
		TopicMapExporter.endElement(handler, "assoctype");
	}

	public void exportTypeDefinition(Document doc) {
		Element root = doc.getDocumentElement();
		Element assoctype = doc.createElement("assoctype"); 
		root.appendChild(assoctype);
		assoctype.setAttribute("ID", getID());
		// ### incomplete
	}

	// ---

	/**
	 * @see		de.deepamehta.service.CorporateCommands#addTypeCommands
	 */
	public boolean isSearchType() {
		return getID().equals(ASSOCTYPE_NAVIGATION);	// ###
	}

	// ---

	/**
	 * @see		TypeTopic#init
	 */
	protected void setTypeAppearance() {
		// ### compare to LiveTopic.setAppearance()
		// ### compare to TopicTypeTopic.setTypeAppearance()
		this.typeAppMode = APPEARANCE_DEFAULT;
		//
		TypeTopic typeTopic = this;	// type to check
		boolean performed = false;
		// loop through supertypes of this type
		do {
			String color = getProperty(typeTopic, PROPERTY_COLOR);
			if (!color.equals("")) {
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
		// >>> compare to TopicTypeTopic.setIconfile()
		switch (typeAppMode) {
		case APPEARANCE_DEFAULT:
			this.assocTypeColor = COLOR_DEFAULT;
			this.iconfile = "at-" + dynIcon(assocTypeColor);
			break;
		case APPEARANCE_CUSTOM_COLOR:
			this.assocTypeColor = typeAppParam;
			this.iconfile = "at-" + dynIcon(assocTypeColor);
			break;
		default:
			throw new DeepaMehtaException("unexpected appearance mode: " + typeAppMode);
		}
		// --- create type icon dynamically ---
		File file = new File(FILESERVER_ICONS_PATH + iconfile);
		boolean fileExists = file.exists();
		// Note: association type icons are always created dynamically
		if (!fileExists) {
			createIconfile(file, TYPE_AT, assocTypeColor, directives);
		}
	}
}
