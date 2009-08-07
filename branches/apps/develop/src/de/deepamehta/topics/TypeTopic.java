package de.deepamehta.topics;

import de.deepamehta.AmbiguousSemanticException;
import de.deepamehta.BaseAssociation;
import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.OrderedItem;
import de.deepamehta.PropertyDefinition;
import de.deepamehta.Relation;
import de.deepamehta.TopicInitException;
import de.deepamehta.Type;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateCommands;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.DeepaMehtaServiceUtils;
import de.deepamehta.service.Session;
import de.deepamehta.topics.helper.ArchiveFileCollector;
import de.deepamehta.topics.helper.TopicMapExporter;
import de.deepamehta.util.DeepaMehtaUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;



/**
 * Abstract class as basis for types -- implementations are {@link TopicTypeTopic topic type} and
 * {@link AssociationTypeTopic association type}.
 * <p>
 * The active behavior of a <code>TypeTopic</code> is building its type definition.
 * The type definition is made up by the composed {@link PropertyTopic}s.
 * <p>
 * Furthermore a <code>TypeTopic</code> maintains the data that describes the appearance
 * of the type.
 * <p>
 * ### this class is public because it is used in
 * {@link de.deepamehta.service.ApplicationService#initTypeTopic}<br>
 * <p>
 * <hr>
 * Last functional change: 20.5.2008 (2.0b8)<br>
 * Last documentation update: 17.12.2001 (2.0a14-pre5)<br>
 * J&ouml;rg Richter<br>
 * jri@freenet.de
 */
public abstract class TypeTopic extends LiveTopic implements Type {



	// *****************
	// *** Constants ***
	// *****************



	private static final String ITEM_ASSIGN_NEW_PROPERTY = "Create Property";
	private static final String  CMD_ASSIGN_NEW_PROPERTY = "assignNewProperty";
	private static final String ICON_ASSIGN_NEW_PROPERTY = "createProperty.gif";
	//
	private static final String ITEM_CREATE_SUBTYPE = "Create Subtype";
	private static final String  CMD_CREATE_SUBTYPE = "createSubtype";
	// ### private static final String ICON_CREATE_SUBTYPE = "associationtype.gif";
	//
	protected static final String ITEM_UPDATE_TYPEDEF = "Update";
	protected static final String  CMD_UPDATE_TYPEDEF = "updateTypedef";
	// Note: icons are defined in subclasses



	// **************
	// *** Fields ***
	// **************



	// IMPORTANT: think about fields in live topics.
	// Do they represent mutable values? are they updated correctly?



	private Vector typeDefinition;

	// ---

	// Note: these 2 fields are redundant

	/**
	 * The type definition as ordered set of {@link de.deepamehta.PropertyDefinition}s.
	 * <p>
	 * Initialized by {@link #makeTypeDefinition}.<br>
	 * Accessed by {@link #getTypeDefinition}
	 * which enumerates the single <code>PropertyDefinition</code>s.
	 */
	private Vector propDefinition;

	/**
	 * The type definition.
	 * <p>
	 * Key:   property name (<code>String</code>)<br>
	 * Value: property definition ({@link de.deepamehta.PropertyDefinition})
	 * <p>
	 * Initialized by {@link #makeTypeDefinition}.<br>
	 * Accessed by {@link #getPropertyDefinition}
	 */
	private Hashtable propDefinitionTable;

	// ---

	// Note: these 2 fields are redundant

	/**
	 * Part of type definition.
	 * <p>
	 * Element type is {@link de.deepamehta.Relation}.
	 * <p>
	 * Initialized by {@link #makeTypeDefinition}.<br>
	 * Accessed by {@link #getRelations}
	 */
	private Vector relations;

	/**
	 * Part of type definition.
	 * <p>
	 * Key:   association ID (<code>String</code>)<br>
	 * Value: Relation ({@link de.deepamehta.Relation})
	 */
	private Hashtable relationsTable;

	// ---

	private Vector ownPropNames;

	// ---

	/**
	 * Type IDs of all supertypes of this type, including this type.
	 * <p>
	 * Initialized by {@link #makeTypeDefinition}.<br>
	 * Accessed by {@link #hasSupertype}
	 */
	private Vector supertypes;

	/**
	 * Type IDs of all subtypes of this type, including this type
	 * <p>
	 * Initialized by {@link #makeTypeDefinition}.<br>
	 * Accessed by {@link #getSubtypeIDs}
	 */
	private Vector subtypes;

	// ---

	/**
	 * Reflects the value of the "Custom Implementation" property.
	 * <p>
	 * Initialized by {@link #setCustomImplementation}.<br>
	 * Accessed by {@link #getCustomImplementation}
	 */
	private String customImplementation;

	/**
	 * The (derived) implementing class resp. <code>null</code> if there is no (derived) implementation.
	 * <p>
	 * Initialized by {@link #setDerivedImplementation}.<br>
	 * Accessed by {@link #getDerivedImplementation}
	 */
	private String derivedImplementation;

	// ---

	/**
	 * Reflects the value of the "Plural Name" property.
	 * <p>
	 * Initialized by {@link #setPluralName}.<br>
	 * Acessed by {@link #getPluralName}.<br>
	 */
	private String pluralTypename;

	/**
	 * Reflects the value of the "Creation Icon" property.
	 * <p>
	 * Initialized by {@link #setCreationIcon}.<br>
	 * Acessed by {@link #getCreationIcon}.<br>
	 */
	private String creationIcon;

	private boolean disabled;

	/**
	 * Reflects the value of the "Hidden Topic Names" property.
	 */
	private boolean hiddenTopicNames;

	// ### private String propertyLayout;

	// --- Appearance ---

	/**
	 * The type appearance mode<br>
	 * ({@link #APPEARANCE_DEFAULT}, {@link #APPEARANCE_CUSTOM_COLOR} or
	 * {@link #APPEARANCE_CUSTOM_ICON}). Note: the latter one is only used for topic types,
	 * not for association types.
	 * <p>
	 * Initialized by <code>init()</code> of the <code>TypeTopic</code>'s subclasses
	 * ({@link TopicTypeTopic#init} and {@link AssociationTypeTopic#init}).
	 * <p>
	 * Accessed by {@link #getTypeAppearanceMode}
	 */
	protected int typeAppMode;

	/**
	 * The type appearance parameter.
	 * <p>
	 * Initialized by <code>init()</code> of the <code>TypeTopic</code>'s subclasses
	 * ({@link TopicTypeTopic#init} and {@link AssociationTypeTopic#init}).
	 * <p>
	 * Accessed by {@link #getTypeAppearanceParam}
	 */
	protected String typeAppParam;

	/**
	 * Initialized by sublass
	 * {@link AssociationTypeTopic#setIconfile AssociationTypeTopic}.
	 * <p>
	 * Accessed by {@link #getAssocTypeColor}
	 */
	protected String assocTypeColor;



	// *******************
	// *** Constructor ***
	// *******************



	TypeTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}



	// ************************
	// *** Abstract Methods ***
	// ************************



	abstract public String getImplementingClass();

	/**
	 * @see		#init
	 */
	abstract protected void setTypeAppearance();

	// --- exportTypeDefinition (2 forms) ---

	abstract public void exportTypeDefinition(ContentHandler handler, ArchiveFileCollector collector) throws SAXException;
	abstract public void exportTypeDefinition(Document doc);



	// **********************
	// *** Defining Hooks ***
	// **********************



	// ------------------
	// --- Life Cycle ---
	// ------------------



	public CorporateDirectives init(int initLevel, Session session) throws TopicInitException {
		CorporateDirectives directives = new CorporateDirectives();
		//
		if (initLevel == INITLEVEL_1) {
			try {
				setDerivedImplementation();		// may throw ASE
				setTypeAppearance();			// may throw ASE	// Note: setTypeAppearance() is declared abstract
				// ### if one throws type is not properly inited
			} catch (Exception e) {
				e.printStackTrace();
				throw new TopicInitException("error at init level 1 (" + e.getMessage() + ")");
			}
		} else if (initLevel == INITLEVEL_2) {
			try {
				setPluralName();
				setCreationIcon();
				setCustomImplementation();
				setDisabled();
				setHiddenTopicNames();
			} catch (Exception e) {
				throw new TopicInitException("error at init level 2 (" + e.getMessage() + ")");
			}
		} else if (initLevel == INITLEVEL_3) {
			// Note: the type definition is initialized at level 3 because it relies on
			// involved property topics beeing in live corporate memory and PropertyTopic
			// initializes at level 2
			// ### since the constants are preloaded we could actually init at level 1
			try {
				makeTypeDefinition(session, directives);
			} catch (Exception e) {
				e.printStackTrace();
				throw new TopicInitException("error at init level 3 (" + e.getMessage() + ")");
			}
		}
		// Note: setTypeAppearance() must perform before TopicTypeTopic.setIconfile()
		directives.add(super.init(initLevel, session));
		//
		return directives;
	}

	public CorporateDirectives evoke(Session session, String topicmapID, String viewmode) {
		// --- assign type to user ---
		String assocID = as.getNewAssociationID();
		String userID = session.getUserID();
		cm.createAssociation(assocID, 1, SEMANTIC_WORKSPACE_TYPES, 1, userID, 1, getID(), 1);
		cm.setAssociationData(assocID, 1, PROPERTY_ACCESS_PERMISSION, PERMISSION_CREATE);
		cm.setAssociationData(assocID, 1, PROPERTY_OWNER_ID, userID);
		// Note: programatically created "Type Access" associations (as here) get "create" permission by default
		// whereas manually created "Type Access" associations get "view" permission by default (via corporate
		// memory entry for "Default Value" property of the "Access Permission" property, see cm.sql). This is the
		// intended behavoir.
		// How this is realized: Corporate memory-based default values are set in LiveTopic.evoke() which is triggered here _after_
		// setting the default here, and LiveTopic.evoke() doesn't set a default value if a custom implementation
		// has already set another default value.
		// ### Another solution to consider: perform evoke() first in this method and strip the "already set" check in
		// LiveTopic.evoke().
		return super.evoke(session, topicmapID, viewmode);
	}



	// ------------------------------------------
	// --- Reacting upon dedicated situations ---
	// ------------------------------------------



	public boolean retypeAllowed(Session session) {
		return false;
	}



	// --------------------------
	// --- Providing Commands ---
	// --------------------------



	public CorporateCommands contextCommands(String topicmapID, String viewmode, Session session,
																	CorporateDirectives directives) {
		CorporateCommands commands = new CorporateCommands(as);
		int editorContext = as.editorContext(topicmapID);
		//
		commands.addNavigationCommands(this, editorContext, session);
		// --- "Create Property" command ---
		commands.addSeparator();
		int cmdState = session.isDemo() ? COMMAND_STATE_DISABLED : COMMAND_STATE_DEFAULT;
		commands.addCommand(ITEM_ASSIGN_NEW_PROPERTY, CMD_ASSIGN_NEW_PROPERTY,
			FILESERVER_IMAGES_PATH, ICON_ASSIGN_NEW_PROPERTY, cmdState);
		// --- "Create Subtype" command ---
		commands.addCommand(ITEM_CREATE_SUBTYPE, CMD_CREATE_SUBTYPE,
			FILESERVER_ICONS_PATH, getIconfile(), cmdState);	// ### icon
		// Note: standard commands are added by subclasses
		//
		return commands;
	}



	// --------------------------
	// --- Executing Commands ---
	// --------------------------



	public CorporateDirectives executeCommand(String actionCommand, Session session,
													String topicmapID, String viewmode) {
		CorporateDirectives directives = new CorporateDirectives();
		//
		if (actionCommand.equals(CMD_ASSIGN_NEW_PROPERTY)) {
			createChildTopic(TOPICTYPE_PROPERTY, SEMANTIC_PROPERTY_DEFINITION, session, directives);
			return directives;
		} else if (actionCommand.equals(CMD_CREATE_SUBTYPE)) {
			createChildTopic(getType(), SEMANTIC_TYPE_DERIVATION, session, directives);
			return directives;
		} else {
			return super.executeCommand(actionCommand, session, topicmapID, viewmode);
		}
	}



	// ******************************************************
	// *** Implementation of Interface de.deepamehta.Type ***
	// ******************************************************



	// Note: getID() is already implemented in BaseTopic
	// Note: getName() is already implemented in BaseTopic
	// Note: getVersion() is already implemented in BaseTopic

	/**
	 * @see		de.deepamehta.service.ApplicationService#initTypeTopic
	 * @see		LiveTopic#getDetail
	 * @see		ContainerTopic#getDetail
	 */
	public Vector getTypeDefinition() {
		return propDefinition;
	}

	// Note: isSearchType() is implemented in subclasses (TopicTypeTopic and AssociationTypeTopic)

	// --- ### not specified in interface

	public Vector getDefinition() {
		return typeDefinition;
	}

	/**
	 * References checked: 24.3.2003 (2.0a18-pre7)
	 *
	 * @see     #getPluralNaming
	 * @see     AssociationTypeTopic#exportTypeDefinition
	 * @see     TopicTypeTopic#exportTypeDefinition
	 */
	public String getPluralName() {
		return pluralTypename;
	}

	/**
	 * References checked: 24.3.2003 (2.0a18-pre7)
	 *
	 * @see		LiveTopic#navigateByTopictype
	 * @see		LiveTopic#navigateByAssoctype
	 * @see		TopicMapTopic#containerName
	 * @see		de.deepamehta.service.CorporateCommands#addRelationCommand
	 * @see		de.deepamehta.service.CorporateCommands#addTypeCommands
	 * @see		de.deepamehta.service.CorporateCommands#addTypeCommand
	 * @see		de.deepamehta.service.web.HTMLGenerator#relationField
	 * @see		de.deepamehta.service.web.HTMLGenerator#relationInfoField
	 * @see		de.deepamehta.service.web.HTMLGenerator#relationInfoFieldHeading
	 */
	public String getPluralNaming() {
		return getPluralName().equals("") ? getName() : getPluralName();
	}

	public PropertyDefinition getPropertyDefinition(int index) {
		return (PropertyDefinition) propDefinition.elementAt(index);
	}

	public PropertyDefinition getPropertyDefinition(String propertyName) {
		return (PropertyDefinition) propDefinitionTable.get(propertyName);
	}

	// ### quick hack for individual type definition
	private Enumeration getOwnPropNames() {
		return ownPropNames.elements();
	}



	// ***************
	// *** Methods ***
	// ***************



	/**
	 * References checked: 20.9.2002 (2.0a16-pre4)
	 *
	 * @see		TopicTypeTopic#exportTypeDefinition
	 * @see		AssociationTypeTopic#exportTypeDefinition
	 */
	protected final String getCustomImplementation() {
		return customImplementation;
	}

	/**
	 * References checked: 20.9.2002 (2.0a16-pre4)
	 *
	 * @see		TopicTypeTopic#getImplementingClass
	 * @see		AssociationTypeTopic#getImplementingClass
	 */
	protected final String getDerivedImplementation() {
		return derivedImplementation;
	}

	/**
	 * @see		de.deepamehta.service.ApplicationService#initTypeTopicAppearance
	 */
	public String getAssocTypeColor() {
		return assocTypeColor;
	}

	/**
	 * @see		TopicTypeTopic#exportTypeDefinition
	 */
	protected final String getCreationIcon() {
		return creationIcon;
	}

	/**
	 * @see		LiveTopic#workspaceCommands
	 */
	protected final String getCreationIconfile() {
		return getCreationIcon().equals("") ? getIconfile() : getCreationIcon();
	}

	/**
	 * @see		LiveTopic#workspaceCommands
	 */
	protected final String getCreationIconPath() {
		return creationIcon.equals("") ? FILESERVER_ICONS_PATH : FILESERVER_IMAGES_PATH;
	}

	/**
	 * @see		de.deepamehta.service.ApplicationService#initTypeTopic
	 */
	public boolean getDisabled() {
		return disabled;
	}

	/**
	 * @see		de.deepamehta.service.ApplicationService#initTypeTopic
	 */
	public boolean getHiddenTopicNames() {
		return hiddenTopicNames;
	}



	// **********************
	// *** Helper Methods ***
	// **********************



	/**
	 * Returns the supertype of this type.
	 *
	 * @return	The supertype as <code>LiveTopic</code> (type <code>tt-topictype</code>)
	 *			or <code>null</code> if this type has no supertype
	 *
	 * @throws	DeepaMehtaException			if the supertype is missing in corporate memory
	 * @throws	AmbiguousSemanticException	if this type has more than one supertype
	 *
	 * @see		#makeTypeDefinition
	 * @see		#setDerivedImplementation
	 * @see		#completeTypeDefinition
	 * @see		TopicTypeTopic#exportTypeDefinition
	 * @see		TopicTypeTopic#setTypeAppearance
	 * @see		AssociationTypeTopic#exportTypeDefinition
	 * @see		AssociationTypeTopic#setTypeAppearance
	 * @see     de.deepamehta.service.web.DeepaMehtaServlet#collectType
	 */
	public final TypeTopic getSupertype() throws DeepaMehtaException, AmbiguousSemanticException {
		Vector topics = as.getRelatedTopics(getID(), SEMANTIC_TYPE_DERIVATION, 1);
		if (topics.size() == 0) {
			return null;
		}
		BaseTopic superType = (BaseTopic) topics.elementAt(0);
		if (topics.size() > 1) {
			throw new AmbiguousSemanticException(this + " has " + topics.size() + " supertypes", superType);
		}
		return (TypeTopic) as.getLiveTopic(superType);		// ### session, directives?
	}

	/**
	 * Returns the corresponding search type of this type.
	 *
	 * @return	The container type as <code>LiveTopic</code> (type <code>tt-topictype</code>)
	 *			or <code>null</code> if this type has no container type
	 *
	 * @throws	DeepaMehtaException			### if the container type doesn't exist in live
	 *											corporate memory
	 * @throws	AmbiguousSemanticException	if this type has more than one container type
	 *
	 * @see     LiveTopic#navigateByTopictype
	 * @see     LiveTopic#navigateByAssoctype
	 * @see     TopicTypeTopic#exportTypeDefinition
	 * @see     AssociationTypeTopic#exportTypeDefinition
	 */
	protected final TypeTopic getSearchType() throws DeepaMehtaException, AmbiguousSemanticException {
		BaseTopic containertype = as.getSearchType(getID());
		return containertype != null ? (TypeTopic) as.getLiveTopic(containertype) : null;	// ### session, directives?
	}

	// ---

	/* ### public Vector getSupertypes() {
		return supertypes;
	} */

	public boolean hasSupertype(String typeID) {
		return supertypes.contains(typeID);
	}

	public Vector getSubtypeIDs() {
		return subtypes;
	}

	public Vector getRelations() {
		return relations;
	}

	public Relation getRelation(String relID) {
		Relation rel = (Relation) relationsTable.get(relID);
		// error check
		if (rel == null) {
			throw new DeepaMehtaException("type \"" + getName() + "\" (" + getID() + "): " +
				"unknown relation \"" + relID + "\"");
		}
		return rel;
	}

	// ---

	/**
	 * ### bad
	 * <p>
	 * Finds the supertypes for the given typetopic (recursively)
	 * and saves references in the given Hashtable. If the typetopic
	 * is a TopicTypeTopic, the according containertype will be
	 * collected as well.
	 *
	 * @param   typetopic a TopicTypeTopic or AssociationTypeTopic
	 * @param   collector this Object stores references to all supertypes
	 *          and containertypes that are found
	 *
	 * @see     #completeTypeDefinition
	 * @see     de.deepamehta.service.ApplicationService#types
	 */
	public final void completeTypeDefinition(Hashtable collector) {
		if (collector.get(getID()) == null) {
			collector.put(getID(), this);
			// supertypes
			TypeTopic supertype = getSupertype();
			if (supertype != null) {
				supertype.completeTypeDefinition(collector);
			}
			// container types
			if (this instanceof TopicTypeTopic) {
				TypeTopic containertype = getSearchType();
				if (containertype != null) {
					containertype.completeTypeDefinition(collector);
				}
			}
		}
	}

	/**
	 * Draws a generic icon for this type and save it as a ### file.
	 * <p>
	 * References checked: 18.1.2004 (2.0b3-pre1)
	 *
	 * @param	type	TYPE_TT or TYPE_AT
	 * @param	color	drawing color, e.g. #CC3333
	 *
	 * @see		TopicTypeTopic#setIconfile
	 * @see		AssociationTypeTopic#setIconfile
	 */
	protected final void createIconfile(File file, int type, String color, CorporateDirectives directives) {
		// compare to Kompetenzstern.createBackgroundImagefile()
		System.out.println(">>> TypeTopic.createIconfile(): \"" + file + "\" (type=" + type + ", color=" + color + ")");
		// --- create image ---
		BufferedImage icon = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = icon.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		//
		// ### g.setColor(COLOR_VIEW_BGCOLOR);
		g.setBackground(COLOR_VIEW_BGCOLOR);
		g.clearRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);	// ### Mac OS X, Java 1.4: fillRect() following drawing commands are ignored!
		g.setColor(DeepaMehtaUtils.parseHexColor(color));
		// --- paint icon ---
		switch (type) {
		case TYPE_TT:
			g.fillOval(TOPIC_BORDER, TOPIC_BORDER, ICON_SIZE, ICON_SIZE);
			break;
		case TYPE_AT:
			g.setStroke(new BasicStroke(3));
			g.drawLine(0, 11, 19, 9);
			break;
		default:
			new DeepaMehtaException("unexpected type: " + type);
		}
		// --- save icon as PNG file ---
		DeepaMehtaServiceUtils.createImageFile(icon, file);
	}

	protected final String dynIcon(String color) {
		return color.substring(1) + ".png";		// ### harcoded
	}



	// ***********************
	// *** Private Methods ***
	// ***********************



	/**
	 * @see		#init
	 */
	private void setPluralName() {
		pluralTypename = getProperty(PROPERTY_PLURAL_NAME);
	}

	/**
	 * @see		#init
	 */
	private void setCreationIcon() {
		creationIcon = getProperty(PROPERTY_CREATION_ICON);
	}

	/**
	 * @see		#init
	 */
	private void setCustomImplementation() {
		customImplementation = getProperty(PROPERTY_IMPLEMENTATION);
	}

	/**
	 * @see		#init
	 */
	private void setDerivedImplementation() throws AmbiguousSemanticException {
		TypeTopic typeTopic = this;		// begin with this type
		// loop through supertypes of this type
		do {
			String implClass = getProperty(typeTopic, PROPERTY_IMPLEMENTATION);
			if (!implClass.equals("")) {
				this.derivedImplementation = implClass;
				return;
			}
			// continue with supertype
			typeTopic = typeTopic.getSupertype();  // may throw AmbiguousSemanticException
		} while (typeTopic != null);
	}

	/**
	 * @see		#init
	 */
	private void setDisabled() {
		this.disabled = getProperty(PROPERTY_DISABLED).equals(SWITCH_ON);
	}

	/**
	 * @see		#init
	 */
	private void setHiddenTopicNames() {
		this.hiddenTopicNames = getProperty(PROPERTY_HIDDEN_TOPIC_NAMES).equals(SWITCH_ON);
	}

	// ---

	/**
	 * Exports the property definitions of this type to a SAX handler.
	 *
	 * @param   handler	    this object will get the generated SAX events
	 * @param   collector   collects document and icon files for export
	 *
	 * @see     TopicTypeTopic#exportTypeDefinition
	 * @see     AssociationTypeTopic#exportTypeDefinition
	 */
	protected final void exportPropertyDefinitions(ContentHandler handler, ArchiveFileCollector collector)
																						throws SAXException {
		Enumeration e = getOwnPropNames();
		while (e.hasMoreElements()) {
			String propName = (String) e.nextElement();
			PropertyDefinition propDef = getPropertyDefinition(propName);
			// propertydef
			Hashtable attributes = new Hashtable();
			attributes.put("name", propDef.getPropertyName());
			attributes.put("visualization", propDef.getVisualization());
			attributes.put("editicon", propDef.getEditIconfile());
			TopicMapExporter.startElement(handler, "propertydef", attributes);
			// value definitions
			Enumeration e2 = propDef.getOptions().elements();
			while (e2.hasMoreElements()) {
				BaseTopic option = (BaseTopic) e2.nextElement();
				TopicMapExporter.startElement(handler, "option", null);
				TopicMapExporter.characters(handler, option.getName());
				TopicMapExporter.endElement(handler, "option");
			}
			TopicMapExporter.endElement(handler, "propertydef");
			// --- save edit iconfile ###
//			if (collector != null) {
//				collector.putIcon(propDef.getEditIconfile(), FILESERVER_ICONS_PATH);
//			}
		}
	}

	protected final void exportPropertyDefinitions(Document doc, Element parent) {
		Enumeration e = getOwnPropNames();
		while (e.hasMoreElements()) {
			String propName = (String) e.nextElement();
			PropertyDefinition propDef = getPropertyDefinition(propName);
			// propertydef
			Element propertydef = doc.createElement("propertydef");
			parent.appendChild(propertydef);
			propertydef.setAttribute("name", propDef.getPropertyName());	// ### should be a text node instead an attribute
			propertydef.setAttribute("visualization", propDef.getVisualization());
			propertydef.setAttribute("editicon", propDef.getEditIconfile());
			// value definitions
			Enumeration e2 = propDef.getOptions().elements();
			while (e2.hasMoreElements()) {
				BaseTopic value = (BaseTopic) e2.nextElement();
				Element option = doc.createElement("option");
				propertydef.appendChild(option);
				option.appendChild(doc.createTextNode(value.getName()));
			}
		}
	}

	// ---

	/**
	 * Builds the type definition for this type.
	 * <p>
	 * ### The {@link #propDefinition} field is cleared and rebuild according the aggregated
	 * properties in corporate memory. Also the property definitions of all super types
	 * are respected.
	 *
	 * @see		#init
	 */
	private void makeTypeDefinition(Session session, CorporateDirectives directives) throws AmbiguousSemanticException {
		// clear existing type definition
		typeDefinition = new Vector();
		propDefinition = new Vector();
		propDefinitionTable = new Hashtable();
		relations = new Vector();
		relationsTable = new Hashtable();
		ownPropNames = new Vector();
		supertypes = new Vector();
		subtypes = new Vector();
		// --- trigger hiddenProperties() hook ---
		Vector hiddenProperties = as.triggerHiddenProperties(this);		// may return null
		//
		// respect all super types (beginning with this type)
		TypeTopic typeTopic = this;		// current type
		do {
			// build "supertypes"
			supertypes.addElement(typeTopic.getID());
			// add property definitions
			Enumeration properties = as.getRelatedTopics(typeTopic.getID(),
				SEMANTIC_PROPERTY_DEFINITION, TOPICTYPE_PROPERTY, 2, true).elements();  // sortAssociations=true
			addPropertyDefinitions(properties, typeTopic, hiddenProperties, session, directives);
			// add relations, sortAssociations=true
			Enumeration relTypes = as.getRelatedTopics(typeTopic.getID(),
				SEMANTIC_RELATION_DEFINITION, TOPICTYPE_TOPICTYPE, 2, true).elements(); // sortAssociations=true
			addRelations(relTypes, typeTopic);
			// set current type to supertype
			typeTopic = typeTopic.getSupertype();  // may throw AmbiguousSemanticException
		} while (typeTopic != null);
		//
		// ### propDefinitionTable = DeepaMehtaServiceUtils.fromTypeDefinition(propDefinition);
		// initialize "subtypes"
		// ### System.out.println(">>> subtypes of type \"" + getName() + "\"");
		addSubtypes(getID());
	}

	// ---

	/**
	 * Adds property definitions to the type definition.
	 *
	 * @param	properties			the properties, enumeration of <code>BaseTopic</code>s of type <code>tt-property</code>
	 * @param	typeTopic			the type the properties belong to
	 * @param	hiddenProperties	may be <code>null</code>
	 *
	 * @see		#makeTypeDefinition
	 */
	private void addPropertyDefinitions(Enumeration properties, TypeTopic typeTopic, Vector hiddenProperties,
															Session session, CorporateDirectives directives) {
		// ### Note: if no property layout is set, PROPERTY_LAYOUT_SUPER_FIRST is used as default
		// ### int index = propertyLayout.equals(PROPERTY_LAYOUT_SUB_FIRST) ? propDefinition.size() : 0;
		// add property definition for every enumerated property
		while (properties.hasMoreElements()) {
			BaseTopic bt = (BaseTopic) properties.nextElement();
			LiveTopic lt = as.getLiveTopic(bt, session, directives);
			// ### error check
			if (!checkProperty(lt, typeTopic)) {
				continue;
			}
			//
			PropertyTopic property = (PropertyTopic) lt;
			//
			property.initPropertyDefinition(session, directives);
			//
			String fieldName = property.getName();
			String visualization = hiddenProperties != null && hiddenProperties.contains(fieldName) ?
				VISUAL_HIDDEN : property.getVisualization();
			// create property definition
			PropertyDefinition propDef = new PropertyDefinition(fieldName, property.getDataType(),
				visualization, property.getDefaultValue(), property.getEditIconfile(), property.getOptions());
			propDef.setOrdinalNr(bt.ordNr);
			// --- trigger buttonCommand() hook ---
			addButton(propDef, typeTopic, session);
			// --- trigger propertyLabel() hook ---
			setPropertyLabel(propDef, typeTopic, session);
			// --- add property definition ---
			addToTypeDefinition(propDef, typeDefinition);
			addToTypeDefinition(propDef, propDefinition, propDefinitionTable, propDef.getPropertyName());
			//
			// build "ownPropNames"
			if (typeTopic == this) {
				// ### System.out.println(">>> TypeTopic.makeTypeDefinition: individual prop for " + getName() + ": " + propDef.getPropertyName());
				ownPropNames.addElement(propDef.getPropertyName());
			}
		}
	}

	/**
	 * Adds relations to the type definition.
	 *
	 * @param	relTypes			the related topic types, enumeration of <code>BaseTopic</code>s of type
	 *								<code>tt-topictype</code>
	 *
	 * @see		#makeTypeDefinition
	 */
	private void addRelations(Enumeration relTypes, TypeTopic typeTopic) {
		while (relTypes.hasMoreElements()) {
			BaseTopic type = (BaseTopic) relTypes.nextElement();
			String topicTypeID = type.getID();
			//
			BaseAssociation assoc = cm.getAssociation(SEMANTIC_RELATION_DEFINITION, typeTopic.getID(), topicTypeID);
			String name = as.getAssocProperty(assoc, PROPERTY_NAME);
			String cardinality = as.getAssocProperty(assoc, PROPERTY_CARDINALITY);
			String assocTypeID = as.getAssocProperty(assoc, PROPERTY_ASSOCIATION_TYPE_ID);
			String webInfo = as.getAssocProperty(assoc, PROPERTY_WEB_INFO);
			String webForm = as.getAssocProperty(assoc, PROPERTY_WEB_FORM);
			// set defaults
			if (webInfo.equals("")) {
				webInfo = WEB_INFO_TOPIC_NAME;
			}
			if (webForm.equals("")) {
				webForm = WEB_FORM_TOPIC_SELECTOR;
			}
			//
			// ### boolean isStrong = as.getAssocProperty(assoc, PROPERTY_STRONG).equals(SWITCH_ON);
			int ordNr = type.ordNr;
			//
			Relation rel = new Relation(assoc.getID(), name, topicTypeID, cardinality, assocTypeID,
				webInfo, webForm, ordNr);
			addToTypeDefinition(rel, typeDefinition);
			addToTypeDefinition(rel, relations, relationsTable, rel.id);
		}
	}

	// --- addToTypeDefinition (2 forms) ---

	private void addToTypeDefinition(OrderedItem item, Vector items) {
		addToTypeDefinition(item, items, null, null);
	}

	/**
	 * @param	item	a <code>PropertyDefinition</code> or a <code>Relation</code>
	 */
	private void addToTypeDefinition(OrderedItem item, Vector items, Hashtable itemTable, String hash) {
		boolean added = false;
		if (item.getOrdinalNr() > 0) {
			for (int i = 0; i < items.size(); i++) {
				if (((OrderedItem) items.elementAt(i)).getOrdinalNr() > item.getOrdinalNr()) {
					items.insertElementAt(item, i);
					added = true;
					break;
				}
			}
		}
		if (!added) {
			items.addElement(item);
		}
		if (itemTable != null) {
			itemTable.put(hash, item);
		}
	}

	// ---

	/**
	 * @see		#addPropertyDefinitions
	 */
	private void setPropertyLabel(PropertyDefinition propDef, TypeTopic type, Session session) {
		// --- trigger propertyLabel() hook ---
		Class[] paramTypes = {PropertyDefinition.class, ApplicationService.class, Session.class};
		Object[] paramValues = {propDef, as, session};
		// Note: this type must be able to relabel properties of supertypes
		as.triggerStaticHook(getImplementingClass(), "propertyLabel", paramTypes, paramValues, false);
		if (type != this) {
			as.triggerStaticHook(type.getImplementingClass(), "propertyLabel", paramTypes, paramValues, false);
		}
	}

	/**
	 * @see		#addPropertyDefinitions
	 */
	private void addButton(PropertyDefinition propDef, TypeTopic type, Session session) {
		// --- trigger buttonCommand() hook ---
		Class[] paramTypes = {PropertyDefinition.class, ApplicationService.class, Session.class};
		Object[] paramValues = {propDef, as, session};
		try {
			as.triggerStaticHook(type.getImplementingClass(), "buttonCommand", paramTypes, paramValues, true);
			// throwIfNoSuchHookExists=true
		} catch (DeepaMehtaException e) {
			// Note: the type may have an custom implementation but no static buttonCommand() hook
			// defined, in this case the default implementation in LiveTopic must be triggered
			LiveTopic.buttonCommand(propDef, as, session);
		}
	}

	/**
	 * @see		#makeTypeDefinition
	 * @see		#addSubtypes
	 */
	private void addSubtypes(String typeID) {
		// ### System.out.println(">>> " + typeID);
		this.subtypes.addElement(typeID);
		// --- get subtypes ---
		// Note: relTopicType is not set (null) to match topic types as well as association types
		// ### could pass vector with 2 entries ("tt-topictype", "tt-assoctype")
		// ### currently no suitable method in LCM
		Vector types = as.getRelatedTopics(typeID, SEMANTIC_TYPE_DERIVATION, null, 2,
			false, true);	// ordered=false, allowEmpty=true
		// --- add subtypes (recursively) ---
		Enumeration e = types.elements();
		while (e.hasMoreElements()) {
			String subtypeID = ((BaseTopic) e.nextElement()).getID();
			addSubtypes(subtypeID);
		}
	}

	/**
	 * ### should throw DeepaMehtaException instead of returning a boolean.
	 * ### still needed at all?
	 *
	 * @param	propertyProxy	the property to check
	 * @param	typeTopic		only needed for error report
	 *
	 * @see		#makeTypeDefinition
	 */
	private boolean checkProperty(LiveTopic propertyProxy, LiveTopic typeTopic) {
		// error check 1
		if (propertyProxy == null) {
			System.out.println("*** TypeTopic.checkProperty(): " + typeTopic + " composes " +
				propertyProxy + " but not in live corporate memory -- ignored");
			return false;
		}
		// error check 2
		if (!(propertyProxy instanceof PropertyTopic)) {
			System.out.println("*** TypeTopic.checkProperty(): " + typeTopic + " composes " +
				propertyProxy + " but not a property -- ignored");
			return false;
		}
		//
		return true;
	}
}
