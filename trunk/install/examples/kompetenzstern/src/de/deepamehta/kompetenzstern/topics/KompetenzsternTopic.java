package de.deepamehta.kompetenzstern.topics;

import de.deepamehta.AmbiguousSemanticException;
import de.deepamehta.BaseAssociation;
import de.deepamehta.BaseTopic;
import de.deepamehta.BaseTopicMap;
import de.deepamehta.Commands;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.Detail;
import de.deepamehta.PresentableTopic;
import de.deepamehta.PropertyDefinition;
import de.deepamehta.assocs.LiveAssociation;
import de.deepamehta.kompetenzstern.KS;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateCommands;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.CorporateTopicMap;
import de.deepamehta.service.DeepaMehtaServiceUtils;
import de.deepamehta.service.Session;
import de.deepamehta.topics.LiveTopic;
import de.deepamehta.topics.TopicMapTopic;
import de.deepamehta.topics.TypeTopic;
import de.deepamehta.topics.helper.ArchiveFileCollector;
import de.deepamehta.topics.helper.TopicMapExporter;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;



/**
 * Part of {@link KompetenzsternTopic Kompetenzstern} application.
 * <p>
 * ### Note: once a Kompetenzstern is created it should be independant from the
 * underlying {@link TemplateTopic Template}, but this isn't completely achieved yet.
 * <p>
 * <hr>
 * Last functional change: 20.5.2008 (2.0b8)<br>
 * Last documentation update: 17.12.2001 (2.0a14-pre5)<br>
 * J&ouml;rg Richter<br>
 * jri@freenet.de
 */
public class KompetenzsternTopic extends TopicMapTopic implements KS {



	// **************
	// *** Fields ***
	// **************



	static final String VERSION = "1.1.22";
	static {
		System.out.println(">>> Kompetenzstern " + VERSION);
	}

	BaseTopic template;
	String kriteriumTypeID;
	//
	Vector werte;
	Vector kriterien;
	Vector layers;
	//
	int werteCount;
	int kriteriumCount;
	int layerCount;



	// *******************
	// *** Constructor ***
	// *******************



	public KompetenzsternTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}



	// **********************
	// *** Defining Hooks ***
	// **********************



	// ------------------
	// --- Life Cycle ---
	// ------------------



	public CorporateDirectives init(int initLevel, Session session) throws
										DeepaMehtaException, AmbiguousSemanticException {
		CorporateDirectives directives = super.init(initLevel, session);
		//
		// --- initialize instance variables ---
		//
		// Note: the instance variables are initialized at level 2 because we're
		// relying on the "preference" associations created by the evoke() hook
		if (initLevel == INITLEVEL_2) {
			this.template = getTemplate();									// throws DME, ASE
			BaseTopic skala = getBewertungsskala();							// throws DME, ASE
			//
			BaseTopic type = getKriteriumType(skala.getID());				// throws DME, ASE
			this.kriteriumTypeID = type.getID();
			String templateID = template.getID();
			//
			this.kriterien = getKriterien(templateID, session, directives);	// throws DME
			this.layers = getBewertungsebenen(templateID);					// throws DME
			this.werte = getWerte(kriteriumTypeID);
			//
			this.kriteriumCount = kriterien.size();
			this.layerCount = layers.size();
			this.werteCount = werte.size();
			//
			if (LOG_TOPIC_INIT) {
				System.out.println(">>> KompetenzsternTopic.init(): kriterium type=\"" +
					kriteriumTypeID + "\", " + kriteriumCount + " Kriterien, " +
					layers.size() + " Ebenen, " + werteCount + " Werte");
			}
		}
		//
		return directives;
	}

	public CorporateDirectives evoke(Session session, String topicmapID, String viewmode) throws
												DeepaMehtaException, AmbiguousSemanticException {
		CorporateDirectives directives = super.evoke(session, topicmapID, viewmode);
		// --- determine template and skala ---
		// Note: evocation can't rely on instance variables because they're
		// not yet initialized (see init())
		String templateID;
		String skalaID;
		switch (session.getType()) {
		case SESSION_JAVA_CLIENT:
			// investigate user preferences
			String userID = session.getUserID();
			BaseTopic template = getTemplate(userID, as);			// throws ASE
			BaseTopic skala = getBewertungsskala(userID, as);		// throws DME, ASE
			//
			templateID = template.getID();
			skalaID = skala.getID();
			break;
		case SESSION_WEB_INTERFACE:
			// ### KS web interface is not yet personalized
			templateID = TEMPLATE_STANDARD;		// ### hardcoded
			skalaID = SKALA_5;					// ### hardcoded
			break;
		default:
			throw new DeepaMehtaException("unexpected session type: " + session.getType());
		}
		// transfer current preferences as preferences for this kompetenzstern
		// Note: this is performed before further preferences are determined to let
		// a kompetenzstern have this crucial preferences if an exception occurrs
		// while the remainder
		cm.createAssociation(cm.getNewAssociationID(), 1, SEMANTIC_PREFERENCE, 1, getID(), 1, templateID, 1);
		cm.createAssociation(cm.getNewAssociationID(), 1, SEMANTIC_PREFERENCE, 1, getID(), 1, skalaID, 1);
		BaseTopic type = getKriteriumType(skalaID);							// throws DME, ASE
		String kriteriumTypeID = type.getID();
		Vector kriterien = getKriterien(templateID, session, directives);	// throws DME
		Vector layers = getBewertungsebenen(templateID);					// throws DME
		Vector werte = getWerte(kriteriumTypeID);
		// --- create Kriterien, set their properties and place them into map ---
		createKompetenzsternTopics(kriterien, werte, kriteriumTypeID);
		createEbenenTopics(layers);
		// --- set background image ---
		String bgImage = backgroundFilename(kriterien, werte);
		cm.setTopicData(getID(), getVersion(), PROPERTY_BACKGROUND_IMAGE, bgImage);
		// --- set translation ---
		int tx = (WIDTH_WINDOW - BACKGROUND_WIDTH - WIDTH_VIEW_CONTROLS - 10) / 2;	// ### -10 for window decoration
		int ty = 40;
		cm.setTopicData(getID(), getVersion(), PROPERTY_TRANSLATION_USE, tx + ":" + ty);
		//
		return directives;
	}



	// --------------------------
	// --- Providing Commands ---
	// --------------------------



	/* ### public CorporateCommands contextCommands(String topicmapID, String viewmode,
								Session session, CorporateDirectives directives) {
		CorporateCommands commands = new CorporateCommands(as);
		//
		int editorContext = editorContext(topicmapID);
		if (editorContext == EDITOR_CONTEXT_PERSONAL) {
			commands = super.contextCommands(topicmapID, viewmode, session, directives);
		} else if (editorContext == EDITOR_CONTEXT_VIEW) {
			commands = new CorporateCommands(as);
			commands.addHideTopicCommand(session);
		}
		//
		return commands;
	} */

	/**
	 * @see		de.deepamehta.service.CorporateCommands#addWorkspaceTopicTypeCommands(Vector types, Session session, CorporateDirectives directives)
	 */
	public static CorporateCommands workspaceCommands(TypeTopic type, ApplicationService as,
					Session session, CorporateDirectives directives) throws DeepaMehtaException {
		try {
			String userID = session.getUserID();
			TemplateTopic template = (TemplateTopic) as.getLiveTopic(getTemplate(userID, as));
			// --- "Create Kompetenzstern" command ---
			TypeTopic starType = as.type(template.getKompetenzsternTypeID(), 1);
			CorporateCommands commands = LiveTopic.workspaceCommands(starType, as, session, directives);
			// --- "Einstellungen" menu ---
			Commands prefsGroup = commands.addCommandGroup(ITEM_PREFERENCES_KOMPETENZSTERN,
				FILESERVER_ICONS_PATH, ICON_PREFERENCES);
			// "Template"
			Vector templates = as.cm.getTopics(TOPICTYPE_KOMPETENZSTERN_TEMPLATE);
			Enumeration e = templates.elements();
			while (e.hasMoreElements()) {
				BaseTopic t = (BaseTopic) e.nextElement();
				BaseTopic owner = ((TopicMapTopic) as.getLiveTopic(t)).getOwner();
				String ownerName = owner != null ? owner.getName() : "?";
				t.setName(t.getName() + " (" + ownerName + ")");
			}
			commands.addTopicCommands(prefsGroup, templates,
				CMD_SET_TEMPLATE, COMMAND_STATE_RADIOBUTTON, template, "Template", session, directives);
			prefsGroup.addSeparator();
			// "Gegenstand"
			commands.addTopicCommands(prefsGroup, as.cm.getTopics(TOPICTYPE_BEWERTUNGS_GEGENSTAND),
				CMD_SET_BEWERTUNGS_GEGENSTAND, COMMAND_STATE_RADIOBUTTON,
				getBewertungsgegenstand(userID, as), "Gegenstand", session, directives);
			prefsGroup.addSeparator();
			// "Skala"
			commands.addTopicCommands(prefsGroup, as.cm.getTopics(TOPICTYPE_BEWERTUNGS_SKALA),
				CMD_SET_BEWERTUNGS_SKALA, COMMAND_STATE_RADIOBUTTON,
				getBewertungsskala(userID, as), "Skala", session, directives);
			//
			return commands;
		} catch (AmbiguousSemanticException e) {
			// ###
			throw new DeepaMehtaException(e.getMessage());
		}
	}

	/**
	 * @see		de.deepamehta.service.ApplicationService#showViewMenu
	 */
	public CorporateCommands viewCommands(String topicmapID, String viewmode,
								Session session, CorporateDirectives directives) {
		CorporateCommands commands = new CorporateCommands(as);
		//
		int editorContext = as.editorContext(topicmapID);
		if (editorContext == EDITOR_CONTEXT_PERSONAL) {
			commands.addWorkspaceTopicTypeCommands(session, directives);
			// ### commands.addImportCommand();
		} else if (editorContext == EDITOR_CONTEXT_VIEW) {
			addShowCompetenceStars(commands);
			commands.addSeparator();
			//
			commands.addCloseCommand(session);
			commands.addSeparator();
			//
			commands.addPublishCommand(getID(), session, directives);
			commands.addSeparator();
			//
			commands.addExportCommand(session, directives);
			commands.addSeparator();
			//
			commands.addHelpCommand(this, session);
		}
		//
		return commands;
	}



	// --------------------------
	// --- Executing Commands ---
	// --------------------------



	public CorporateDirectives executeCommand(String command, Session session,
						String topicmapID, String viewmode) throws DeepaMehtaException {

		CorporateDirectives directives = new CorporateDirectives();
		//
		StringTokenizer st = new StringTokenizer(command, COMMAND_SEPARATOR);
		String cmd = st.nextToken();
		if (cmd.equals(CMD_SHOW_COMPETENCE_STAR)) {
			String topicID = st.nextToken();
			int x = Integer.parseInt(st.nextToken());
			int y = Integer.parseInt(st.nextToken());
			BaseTopic topic = cm.getTopic(topicID, 1);
			PresentableTopic pres = new PresentableTopic(topic, new Point(x, y));
			directives.add(DIRECTIVE_SHOW_TOPIC, pres);
			directives.add(DIRECTIVE_SELECT_TOPIC, topicID);
		} else {
			return super.executeCommand(command, session, topicmapID, viewmode);
		}
		//
		return directives;
	}

	public static CorporateDirectives executeWorkspaceCommand(String command,
										Session session, ApplicationService as,
										String topicmapID, String viewmode) {
		CorporateDirectives directives = new CorporateDirectives();
		String userID = session.getUserID();
		//
		System.out.println(">>> KompetenzsternTopic.executeWorkspaceCommand(): \"" + command + "\"");
		//
		StringTokenizer st = new StringTokenizer(command, COMMAND_SEPARATOR);
		String cmd = st.nextToken();
		if (cmd.equals(CMD_SET_TEMPLATE)) {
			String templateID = st.nextToken();
			setPreference(userID, templateID, TOPICTYPE_KOMPETENZSTERN_TEMPLATE, directives, as);
		} else if (cmd.equals(CMD_SET_BEWERTUNGS_GEGENSTAND)) {
			String gegenstandID = st.nextToken();
			setPreference(userID, gegenstandID, TOPICTYPE_BEWERTUNGS_GEGENSTAND, directives, as);
		} else if (cmd.equals(CMD_SET_BEWERTUNGS_SKALA)) {
			String skalaID = st.nextToken();
			setPreference(userID, skalaID, TOPICTYPE_BEWERTUNGS_SKALA, directives, as);
		} else {
			// Note: there is no TopicMapTopic.executeWorkspaceCommand()
			// superclass not required to be called
			throw new DeepaMehtaException("no command handler implemented");
		}
		//
		return directives;
	}



	// ---------------------------
	// --- Handling Properties ---
	// ---------------------------



	public CorporateDirectives propertiesChanged(Hashtable newProps, Hashtable oldProps,
											String topicmapID, String viewmode, Session session) {
		CorporateDirectives directives;
		if (newProps.get(PROPERTY_FIRMA) != null ||
			newProps.get(PROPERTY_DATUM) != null ||
			newProps.get(PROPERTY_ERFASSER) != null) {
			// --- set background image ---
			directives = as.setTopicProperty(getID(), getVersion(), PROPERTY_BACKGROUND_IMAGE,
				backgroundFilename(kriterien, werte), topicmapID, session);
			checkBackground(directives);
		} else {
			directives = super.propertiesChanged(newProps, oldProps, topicmapID, viewmode, session);
		}
		return directives;
	}

	public static Vector hiddenProperties(TypeTopic type) {
		Vector props = new Vector();
		props.addElement(PROPERTY_ICON);
		props.addElement(PROPERTY_BACKGROUND_IMAGE);	// contains name of dynamically created file
		props.addElement(PROPERTY_BACKGROUND_COLOR);
		return props;
	}



	// ----------------------
	// --- Topicmap Hooks ---
	// ----------------------



	public String openTopicmap(Session session, CorporateDirectives directives) throws DeepaMehtaException {
		if (template == null) {
			throw new DeepaMehtaException("Kompetenzstern \"" + getName() + "\" kann " +
				"nicht angezeigt werden, da sein Template nicht bekannt ist");
		}
		if (layers == null) {
			throw new DeepaMehtaException("Kompetenzstern \"" + getName() + "\" kann " +
				"nicht angezeigt werden, da sein Template keine Bewertungsebenen enth�lt");
		}
		if (kriterien == null) {
			throw new DeepaMehtaException("Kompetenzstern \"" + getName() + "\" kann " +
				"nicht angezeigt werden, da sein Template keine Kriterien enth�lt");
		}
		//
		checkBackground(directives);
		// Note: DIRECTIVE_SHOW_VIEW must be queued because download of background image must conlude first.
		// ### DIRECTIVE_SELECT_EDITOR must also be queued, see TopicMapTopic.open()
		/* ### CorporateDirectives queued = new CorporateDirectives();
		String viewID = super.openTopicmap(session, queued);
		directives.add(DIRECTIVE_QUEUE_DIRECTIVES, queued);
		//
		return viewID; */
		return super.openTopicmap(session, directives);
	}



	// --- Exporting ---

	/**
	 * Exports this competence star. If other competence stars are displayed for
	 * comparison, they will be exported too (only for SVG and PDF export).
	 *
	 * @param   handler     this object will get the generated SAX events
	 * @param   collector   this object will collect document and icon files.
	 *                      This parameter may be <code>null</code>, which signalizes
	 *                      that this is an export to SVG or PDF.
	 */
	public void exportTopicmap(ContentHandler handler, ArchiveFileCollector collector) throws SAXException {
		exportCompetenceStar(handler, collector);
		if (collector == null) {
			// --- export other competence stars for comparison
			Enumeration e = getStarsToCompare();
			while (e.hasMoreElements()) {
				KompetenzsternTopic ks = (KompetenzsternTopic) e.nextElement();
				ks.exportCompetenceStar(handler, collector);
			}
		} else {
			// ### export Kompetenzstern template, but to a different ContentHandler?
		}
	}

	public String getSVGStylesheetName() {
		return "stylesheets/vergleich2svg.xsl";
	}

	public String getFOStylesheetName() {
		return "stylesheets/vergleich2fo.xsl";
	}

	// ---

	/**
	 * @return  the ID of the document type topic representing the exported file
	 *
	 * @see     #export
	 */
	public String getExportDocumentType() {
		return TOPICTYPE_EXPORTED_DOCUMENT;
	}

	public String getExportDocumentName() {
		String docname = super.getExportDocumentName();
		//
		Enumeration e = getStarsToCompare();
		while (e.hasMoreElements()) {
			docname += ", " + ((BaseTopic) e.nextElement()).getName();
		}
		//
		return docname;
	}

	// ---

	public String getExportFileBaseName() {
		String filename = "report_" + getID() + "-" + getVersion();
		Enumeration e = getStarsToCompare();
		while (e.hasMoreElements()) {
			BaseTopic topic = (BaseTopic) e.nextElement();
			filename += "_" + topic.getID() + "-" + topic.getVersion();
		}
		return filename;
	}



	// ***********************
	// *** Package Methods ***
	// ***********************



	/* ### 
	/ **
	 * Adds a "Reihenfolge festlegen"/"Reihenfolge ändern" command to the specified
	 * (topic) command set.
	 * <p>
	 * References checked: 30.12.2001 (2.0a14-pre5)
	 *
	 * @see		KriteriumTopic#contextCommands
	 * @see		BewertungsebeneTopic#contextCommands
	 * /
	static void addSetOrderCommand(BaseTopic topic, CorporateCommands commands,
								CorporateDirectives directives, ApplicationService as) {
		try {
			BaseAssociation assoc = as.getAssociation(topic.getID(),
				SEMANTIC_STERN_COMPOSITION, 1, TOPICTYPE_KOMPETENZSTERN_TEMPLATE, false, directives);	// throws DME
			// determine command item
			String order = as.getAssociationData(assoc, PROPERTY_ORDINAL_NUMBER);
			String item = order.equals("") ? ITEM_SET_ORDER : ITEM_CHANGE_ORDER;
			//
			commands.addCommand(item, CMD_SET_ORDER + COMMAND_SEPARATOR + assoc.getID(), FILESERVER_IMAGES_PATH, ICON_SET_ORDER);
		} catch (DeepaMehtaException e) {
			System.out.println("*** KompetenzsternTopic.addSetOrderCommand(): " + e);
			directives.add(DIRECTIVE_SHOW_MESSAGE, "Das Kommando \"" + ITEM_SET_ORDER + "\" steht nicht zur " +
				"Verfügung (" + e.getMessage() + ")", new Integer(NOTIFICATION_WARNING));
		}
	} */

	/**
	 * @see		KriteriumTopic#contextCommands
	 * @see		BewertungsebeneTopic#contextCommands
	 */
	static void addDocumentCommands(boolean includeShowCommand, CorporateCommands commands) {
		if (includeShowCommand) {
			commands.addCommand(ITEM_SHOW_DOCUMENTS, CMD_SHOW_DOCUMENTS, FILESERVER_IMAGES_PATH, ICON_SHOW_DOCUMENTS);
		}
		commands.addCommand(ITEM_ASSIGN_DOCUMENT, CMD_ASSIGN_DOCUMENT, FILESERVER_IMAGES_PATH, ICON_ASSIGN_DOCUMENT);
	}

	// ---

	/**
	 * @see		KriteriumTopic#executeCommand
	 * @see		BewertungsebeneTopic#executeCommand
	 */
	static boolean executeOrderCommand(LiveTopic topic, String command,
							CorporateDirectives directives, ApplicationService as) {
		StringTokenizer st = new StringTokenizer(command, COMMAND_SEPARATOR);
		String cmd = st.nextToken();
		if (cmd.equals(CMD_SET_ORDER)) {
			String assocID = st.nextToken();
			LiveAssociation assoc = as.getLiveAssociation(assocID, 1);	// ### version
			String order = assoc.getAssociationData(PROPERTY_ORDINAL_NUMBER);
			openTextEditor(topic.getID(), "Reihenfolge", order, directives, command);
			return true;
		}
		return false;
	}

	private static void openTextEditor(String topicID, String title, String initialText,
										CorporateDirectives directives, String command) {
		// >>> compare to LiveTopic.openTextEditor()
		// >>> compare to LiveAssociation.openTextEditor()
		Detail detail = new Detail(DETAIL_TOPIC, DETAIL_CONTENT_TEXT, initialText,
			Boolean.FALSE, title, command);
		directives.add(DIRECTIVE_SHOW_DETAIL, topicID, detail);
	}

	/**
	 * @see		KriteriumTopic#executeCommand
	 * @see		BewertungsebeneTopic#executeCommand
	 */
	static boolean executeDocumentCommand(LiveTopic topic, String commmand, Session session,
															CorporateDirectives directives) {
		if (commmand.equals(CMD_SHOW_DOCUMENTS)) {
			directives.add(topic.navigateByTopictype(TOPICTYPE_RELATED_DOCUMENT, 2,
				SEMANTIC_RELATED_DOCUMENT));
			return true;
		} else if (commmand.equals(CMD_ASSIGN_DOCUMENT)) {
			topic.createChildTopic(TOPICTYPE_RELATED_DOCUMENT, SEMANTIC_RELATED_DOCUMENT,
				session, directives);
			return true;
		}
		return false;
	}

	// ---

	/**
	 * @see		#getKriterien
	 * @see		#exportCompetenceStar
	 */
	public String getKriteriumTypeID() {
		return kriteriumTypeID;
	}

	// ### see KriteriumTopic
	// public String getSubkriteriumTypeID() {
	//     return kriteriumTypeID;
	//     return TOPICTYPE_BEWERTUNGS_KRITERIUM;
	// }

	// ---

	/**
	 * @see		#createKompetenzsternTopics
	 * @see		#createBackgrounImagefile
	 * @see		KriteriumTopic#propertiesChanged
	 */
	Point circlePosition(int nr, int count, int value) {
		float d = (float) (2 * Math.PI / count);
		float a = (float) (Math.PI / 2 + Math.PI / count) + nr * d;
		int r = DIST_CENTER + value * DIST_CIRCLES;
		int x = (int) (X_CENTER + r * Math.cos(a));
		int y = (int) (Y_CENTER - r * Math.sin(a));
		return new Point(x, y);
	}

	/**
	 * Returns the current <code>Kompetenzstern-Template</code> setting of the specified
	 * user. If the setting is not available (e.g. because the respective template has
	 * been deleted meanwhile) the standard template is returned.
	 *
	 * @throws	AmbiguousSemanticException
	 *
	 * @see		#evoke
	 * @see		#workspaceCommands
	 */
	public static BaseTopic getTemplate(String userID, ApplicationService as) throws AmbiguousSemanticException {
		try {
			return as.getRelatedTopic(userID, SEMANTIC_PREFERENCE, TOPICTYPE_KOMPETENZSTERN_TEMPLATE, 2, false);
		} catch (DeepaMehtaException e) {
			System.out.println("*** KompetenzsternTopic.getTemplate(): user \"" + userID +
				"\" has no valid template preference -- \"Business Check\" is assumed");
			return as.getLiveTopic(TEMPLATE_STANDARD, 1);
		}
	}



	// ***********************
	// *** Private Methods ***
	// ***********************



	/**
	 * Returns the <code>Kompetenzstern-Template</code> upon this <code>Kompetenzstern</code> is based.
	 * <p>
	 * ### Note: a created Kompetenzstern is not independant of its template.
	 *
	 * @return	the template as <code>BaseTopic</code> of type <code>tt-kompetenzsterntemplate</code>
	 *
	 * @throws	DeepaMehtaException
	 *
	 * @see		#init
	 */
	private BaseTopic getTemplate() throws DeepaMehtaException {
		try {
			return as.getRelatedTopic(getID(), SEMANTIC_PREFERENCE,
				TOPICTYPE_KOMPETENZSTERN_TEMPLATE, 2, false);	// throws DME, ASE
		} catch (DeepaMehtaException e) {
			throw new DeepaMehtaException("Kompetenzstern \"" + getName() + "\" kann " +
				"nicht angezeigt werden weil sein Template nicht bekannt ist");
		} catch (AmbiguousSemanticException e) {
			System.out.println("*** KompetenzsternTopic.getTemplate(): " + e);
			return e.getDefaultTopic();
		}
	}

	// ---

	/**
	 * @throws		DeepaMehtaException
	 * @throws		AmbiguousSemanticException
	 */
	static private BaseTopic getBewertungsgegenstand(String userID,
								ApplicationService as) throws DeepaMehtaException,
								AmbiguousSemanticException {
		return as.getRelatedTopic(userID, SEMANTIC_PREFERENCE,
			TOPICTYPE_BEWERTUNGS_GEGENSTAND, 2, false);
	}

	// --- getBewertungsskala (2 forms) ---

	/**
	 * @throws		DeepaMehtaException
	 * @throws		AmbiguousSemanticException
	 */
	private BaseTopic getBewertungsskala() throws DeepaMehtaException,
														AmbiguousSemanticException {
		return as.getRelatedTopic(getID(), SEMANTIC_PREFERENCE, TOPICTYPE_BEWERTUNGS_SKALA, 2,
			false);
	}

	/**
	 * @throws		DeepaMehtaException
	 * @throws		AmbiguousSemanticException
	 */
	static private BaseTopic getBewertungsskala(String userID, ApplicationService as)
								throws DeepaMehtaException, AmbiguousSemanticException {
		return as.getRelatedTopic(userID, SEMANTIC_PREFERENCE, TOPICTYPE_BEWERTUNGS_SKALA, 2, false);
	}

	// ---

	/**
	 * @throws		DeepaMehtaException
	 * @throws		AmbiguousSemanticException
	 *
	 * @see			#init
	 * @see			#evoke
	 */
	private BaseTopic getKriteriumType(String skalaID) throws DeepaMehtaException, AmbiguousSemanticException {
		return as.getRelatedTopic(skalaID, ASSOCTYPE_ASSOCIATION, TOPICTYPE_TOPICTYPE, 2, false);
	}

	private Vector getSubCriteria(String kriteriumID) throws DeepaMehtaException {
		KriteriumTopic crit = (KriteriumTopic) as.getLiveTopic(kriteriumID, 1);
		return crit.getSubCriteria();
	}

	// --- getKriterien (2 forms) ---

	public Vector getKriterien() throws DeepaMehtaException {
		try {
			return as.getRelatedTopics(getID(), SEMANTIC_STERN_COMPOSITION,
				getKriteriumTypeID(), 2, true, false);	// ordered=true, emptyAllowed=false // throws DME
		} catch (DeepaMehtaException e) {
			throw new DeepaMehtaException("Kompetenzstern \"" + getName() + "\" hat keine Kriterien");
		}
	}

	/**
	 * @see			#init
	 * @see			#evoke
	 */
	private Vector getKriterien(String templateID, Session session, CorporateDirectives directives) throws DeepaMehtaException {
		try {
			TemplateTopic template = (TemplateTopic) as.getLiveTopic(templateID, 1, session, directives);
			return template.getKriterien();
		} catch (DeepaMehtaException e) {
			throw new DeepaMehtaException("Kompetenzstern \"" + getName() + "\" kann " +
				"nicht angezeigt werden, da sein Template keine Kriterien enth�lt");
		}
	}

	// ---

	/**
	 * @see		#init
	 * @see		#evoke
	 */
	private Vector getBewertungsebenen(String templateID) throws DeepaMehtaException {
		try {
			return as.getRelatedTopics(templateID, SEMANTIC_STERN_COMPOSITION,
				TOPICTYPE_BEWERTUNGS_EBENE, 2, true, false);	// throws DME
		} catch (DeepaMehtaException e) {
			throw new DeepaMehtaException("Kompetenzstern \"" + getName() + "\" kann " +
				"nicht angezeigt werden, da sein Template keine Bewertungsebenen enth�lt");
		}
	}

	/**
	 * @see		#init
	 * @see		#evoke
	 */
	private Vector getWerte(String kriteriumTypeID) {
		TypeTopic kriteriumType = as.type(kriteriumTypeID, 1);	// ### type version (1)
		PropertyDefinition propDef = kriteriumType.getPropertyDefinition(PROPERTY_WERT);
		return propDef.getOptions();
	}

	// ---

	/**
	 * @see		#executeWorkspaceCommand
	 */
	private static void setPreference(String userID, String prefTopicID,
									String prefTypeID, CorporateDirectives directives,
									ApplicationService as) {
		// ### compare to TopicMapTopic.setExportFormat()
		// ### compare to TemplateTopic.setKompetenzsternTypeID()
		try {
			// remove existing preference
			as.deleteAssociation(as.getAssociation(userID, SEMANTIC_PREFERENCE, 2,
				prefTypeID, false, directives));	// throws DME
		} catch (DeepaMehtaException e) {
			System.out.println("*** KompetenzsternTopic.setPreference(): " + e);
			directives.add(DIRECTIVE_SHOW_MESSAGE, "User \"" + userID + "\" had no \"" +
				prefTypeID + "\" preference -- now set to \"" + prefTopicID + "\" (" +
				e.getMessage() + ")", new Integer(NOTIFICATION_WARNING));
		} finally {
			// add new preference
			as.cm.createAssociation(as.getNewAssociationID(), 1,
				SEMANTIC_PREFERENCE, 1, userID, 1, prefTopicID, 1);
		}
	}

	/**
	 * @param	kriterien		criterias of selected template
	 * @param	werte			values of selected scale
	 * @param	kriteriumTypeID	ID of criteria type that corresponds to the selected scale
	 *
	 * @see		#evoke
	 */
	private void createKompetenzsternTopics(Vector kriterien, Vector werte, String kriteriumTypeID) {
		int kriteriumCount = kriterien.size();
		int werteCount = werte.size();
		String assocID;
		String prevValueID = null;
		String firstValueID = null;
		for (int i = 0; i < kriteriumCount; i++) {
			BaseTopic kriterium = (BaseTopic) kriterien.elementAt(i);
			String kriteriumName = kriterium.getName();
			String helpText = getProperty(kriterium, PROPERTY_HELP);
			Point p = circlePosition(i, kriteriumCount, werteCount + 2);
			String ordNr = Integer.toString(i);
			String critID;
			int value;
			// --- create "Kriterium" topic ---
			critID = as.getNewTopicID();
			value = 0;	// ### initial value is minimum value
			cm.createTopic(critID, 1, kriteriumTypeID, 1, kriteriumName);
			cm.setTopicData(critID, 1, PROPERTY_NAME, kriteriumName);
			cm.setTopicData(critID, 1, PROPERTY_LOCKED_GEOMETRY, SWITCH_ON);
			cm.setTopicData(critID, 1, PROPERTY_ORDINAL_NUMBER, ordNr);
			cm.setTopicData(critID, 1, PROPERTY_WERT, ((PresentableTopic) werte.firstElement()).getName());
			cm.setTopicData(critID, 1, PROPERTY_HELP, helpText);
			// put criteria into view (this Kompetenzstern)
			cm.createViewTopic(getID(), 1, VIEWMODE_USE, critID, 1, p.x, p.y, false);
			// associate stern with criteria
			assocID = as.getNewAssociationID();
			cm.createAssociation(assocID, 1, SEMANTIC_STERN_COMPOSITION, 1, getID(), 1, critID, 1);
			cm.setAssociationData(assocID, 1, PROPERTY_ORDINAL_NUMBER, ordNr);
			// --- create refined "Kriterium" topics ---
			createSubCriteria(kriterium.getID(), kriteriumTypeID, critID);	// ### 2nd param was TOPICTYPE_BEWERTUNGS_KRITERIUM
			// --- create "Bewertung" topic ---
			String valueID = as.getNewTopicID();
			p = circlePosition(i, kriteriumCount, value);
			cm.createTopic(valueID, 1, TOPICTYPE_BEWERTUNG, 1, kriteriumName);
			cm.setTopicData(valueID, 1, PROPERTY_ORDINAL_NUMBER, ordNr);
			cm.createViewTopic(getID(), 1, VIEWMODE_USE, valueID, 1, p.x, p.y, false);
			// --- create association --- ### unify
			if (prevValueID != null) {
				assocID = as.getNewAssociationID();
				cm.createAssociation(assocID, 1, ASSOCTYPE_KOMPETENZSTERN, 1, prevValueID, 1, valueID, 1);
				cm.createViewAssociation(getID(), 1, VIEWMODE_USE, assocID, 1, false);
			}
			//
			if (i == 0) {
				firstValueID = valueID;
			}
			prevValueID = valueID;
		}
		// --- place star-closing association --- ### unify
		assocID = as.getNewAssociationID();
		cm.createAssociation(assocID, 1, ASSOCTYPE_KOMPETENZSTERN, 1, prevValueID, 1, firstValueID, 1);
		cm.createViewAssociation(getID(), 1, VIEWMODE_USE, assocID, 1, false);
	}

	/**
	 * @see		#evoke
	 */
	private void createEbenenTopics(Vector ebenen) {
		int ebenenCount = ebenen.size();
		int layerHeight = BACKGROUND_HEIGHT / ebenenCount - DIST_LAYER;
		int x = DIST_LAYER + IMAGE_SIZE / 2;
		int y = x;
		for (int i = 0; i < ebenenCount; i++) {
			BaseTopic ebene = (BaseTopic) ebenen.elementAt(i);
			// --- create "Bewertungsebene" topic ---
			String topicID = as.getNewTopicID();
			cm.createTopic(topicID, 1, TOPICTYPE_BEWERTUNGS_EBENE, 1, ebene.getName());
			cm.setTopicData(topicID, 1, PROPERTY_NAME, ebene.getName());
			cm.setTopicData(topicID, 1, PROPERTY_LOCKED_GEOMETRY, SWITCH_ON);
			cm.setTopicData(topicID, 1, PROPERTY_ORDINAL_NUMBER, Integer.toString(i));
			cm.createViewTopic(getID(), 1, VIEWMODE_USE, topicID, 1, x, y, false);
			y += layerHeight + DIST_LAYER;
		}
	}

	/**
	 * @see		#createKompetenzsternTopics
	 */
	private void createSubCriteria(String kriteriumID, String critTypeID, String id) {
		Vector kriterien = getSubCriteria(kriteriumID);
		//
		Enumeration e = kriterien.elements();
		while (e.hasMoreElements()) {
			BaseTopic kriterium = (BaseTopic) e.nextElement();
			String helpText = getProperty(kriterium, PROPERTY_HELP);
			// --- create refined kriterium ---
			String topicID = as.getNewTopicID();
			cm.createTopic(topicID, 1, critTypeID, 1, kriterium.getName());
			cm.setTopicData(topicID, 1, PROPERTY_NAME, kriterium.getName());
			cm.setTopicData(topicID, 1, PROPERTY_HELP, helpText);
			// --- create association ---
			String assocID = as.getNewAssociationID();
			cm.createAssociation(assocID, 1, SEMANTIC_STERN_COMPOSITION, 1, id, 1, topicID, 1);
		}
	}

	// ---

	/**
	 * @param	directives	used to notify user if creation of background image fails
	 *
	 * @see		#propertiesChanged
	 * @see		#openTopicMap
	 */
	private void checkBackground(CorporateDirectives directives) {
		File file = new File(FILESERVER_BACKGROUNDS_PATH + getProperty(PROPERTY_BACKGROUND_IMAGE));
		if (file.exists()) {
			System.out.println(">>> background image for " + this + " exists (" + file + ")");
		} else {
			System.out.print(">>> create background image for " + this + " (" + file + ") ... ");
			createBackgroundImagefile(file, directives);
			System.out.println("OK");
		}
		// let client download background image
		directives.add(DIRECTIVE_DOWNLOAD_FILE, file.getName(), new Long(file.lastModified()),
			new Integer(FILE_BACKGROUND));
	}

	/**
	 * @see		#evoke
	 * @see		#propertiesChanged
	 */
	private String backgroundFilename(Vector kriterien, Vector werte) {
		String company = getProperty(PROPERTY_FIRMA);
		String date = getProperty(PROPERTY_DATUM);
		String author = getProperty(PROPERTY_ERFASSER);
		int werteCount = werte.size();
		int kriteriumCount = kriterien.size();
		// Note: the background key must contain all user changable information that
		// affects the rendering of the background image
		String backgroundKey = company + "-" + date + "-" + author + "-" + werteCount +
			"-" + kriteriumCount;
		int hashCode = backgroundKey.hashCode();
		System.out.println(">>> " + this + ": background key=\"" + backgroundKey + "\"" +
			" --> hash code=" + hashCode);
		//
		return "ks-" + hashCode + ".png";
	}

	/**
	 * @see		#checkBackground
	 */
	private void createBackgroundImagefile(File file, CorporateDirectives directives) {
		// compare to TypeTopic.createIconfile()
		// --- create image ---
		BufferedImage image = new BufferedImage(BACKGROUND_WIDTH, BACKGROUND_HEIGHT + FOOTER_HEIGHT,
																			BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		// --- clear background ---
		g.setColor(COLOR_VIEW_BGCOLOR);
		g.fillRect(0, 0, BACKGROUND_WIDTH, BACKGROUND_HEIGHT + FOOTER_HEIGHT);
		// --- paint background ---
		// layers
		g.setColor(COLOR_LAYER);
		int layerHeight = BACKGROUND_HEIGHT / layerCount - DIST_LAYER;
		int x = DIST_LAYER / 2;
		int y = x;
		for (int i = 0; i < layerCount; i++) {
			g.fillRect(x, y, BACKGROUND_WIDTH - DIST_LAYER, layerHeight);
			y += layerHeight + DIST_LAYER;
		}
		// circles
		int r = DIST_CENTER + (werteCount - 1) * DIST_CIRCLES;
		g.setColor(Color.white);
		g.fillOval(X_CENTER - r, Y_CENTER - r, 2 * r, 2 * r);
		g.setColor(Color.gray);
		r = DIST_CENTER;
		for (int i = 0; i < werteCount; i++) {
			g.drawOval(X_CENTER - r, Y_CENTER - r, 2 * r, 2 * r);
			r += DIST_CIRCLES;
		}
		// rays
		Point p1, p2;
		for (int i = 0; i < kriteriumCount; i++) {
			p1 = circlePosition(i, kriteriumCount, -1);
			p2 = circlePosition(i, kriteriumCount, werteCount);
			g.drawLine(p1.x, p1.y, p2.x, p2.y);
		}
		// footer
		g.setColor(TEXT_COLOR);
		g.drawString(PROPERTY_FIRMA + ":", x, BACKGROUND_HEIGHT + 16);
		g.drawString(PROPERTY_DATUM + ":", x, BACKGROUND_HEIGHT + 36);
		g.drawString(PROPERTY_ERFASSER + ":", x, BACKGROUND_HEIGHT + 56);
		g.drawString(getProperty(PROPERTY_FIRMA), 80, BACKGROUND_HEIGHT + 16);
		g.drawString(getProperty(PROPERTY_DATUM), 80, BACKGROUND_HEIGHT + 36);
		g.drawString(getProperty(PROPERTY_ERFASSER), 80, BACKGROUND_HEIGHT + 56);
		// --- save background as PNG file ---
		DeepaMehtaServiceUtils.createImageFile(image, file);
	}

	// ---

	/**
	 * @see 	#viewCommands
	 */
	private void addShowCompetenceStars(CorporateCommands commands) {
		Commands cmdGroup = commands.addCommandGroup(ITEM_SHOW_COMPETENCE_STARS,
			FILESERVER_IMAGES_PATH, ICON_SHOW_COMPETENCE_STARS);
		//
		Enumeration e = cm.getTopics(TOPICTYPE_KOMPETENZSTERN).elements();
		while (e.hasMoreElements()) {
			BaseTopic topic = (BaseTopic) e.nextElement();
			String topicID = topic.getID();
			if (!getID().equals(topicID)) {
				cmdGroup.addCommand(topic.getName(), CMD_SHOW_COMPETENCE_STAR + COMMAND_SEPARATOR + topicID,
					FILESERVER_ICONS_PATH, ICON_SHOW_COMPETENCE_STAR);
			}
		}
	}



	// -----------------
	// --- Exporting ---
	// -----------------



	/**
	 * The application specific implementation of the exportTopicmap hook.
	 * Exports all criteria shown in the competence star, plus their hidden subcriteria.
	 *
	 * @param   handler     this object will get the generated SAX events
	 * @param   collector   this object will collect document and icon files.
	 *                      This parameter may be <code>null</code>, which signalizes
	 *                      that this is an export to SVG or PDF.
	 *
	 * @see		#exportTopicmap
	 */
	private void exportCompetenceStar(ContentHandler handler, ArchiveFileCollector collector) throws SAXException {
		Hashtable attribs = new Hashtable();
		attribs.put("name", getName());
		attribs.put("type", getType());
		TopicMapExporter.startElement(handler, "topicmap", attribs);
		TopicMapExporter.exportProperties(getProperties(), getID(), handler, collector != null);
		// retrieve and export topics and associations
		CorporateTopicMap cmap = new CorporateTopicMap(as, getID(), getVersion());
		BaseTopicMap map = cmap.getTopicMap();
		Hashtable topics = map.getTopics();
		Hashtable assocs = map.getAssociations();
		TopicMapExporter.exportTopics(topics.elements(), handler, true, as, collector);
		TopicMapExporter.exportAssociations(assocs.elements(), handler, true, as, collector);
		//
		Hashtable hiddenTopics = new Hashtable();
		Hashtable hiddenAssocs = new Hashtable();
		collectCriteria(getKriteriumTypeID(), map, hiddenTopics, hiddenAssocs);
		TopicMapExporter.exportTopics(hiddenTopics.elements(), handler, false, as, collector);
		TopicMapExporter.exportAssociations(hiddenAssocs.elements(), handler, false, as, collector);
		exportAdditionalInformation(handler);
		// for the export of type definitions, accumulate all topics and assocs
		topics.putAll(hiddenTopics);
		assocs.putAll(hiddenAssocs);
		// export type definitions for XML archiving only
		if (collector != null) {
			// save the type of the map for export
			topics.put(this.getID(), this);
			TopicMapExporter.exportTypes(as.types(topics.elements()).elements(), handler, collector, as);
			TopicMapExporter.exportTypes(as.types(assocs.elements()).elements(), handler, collector, as);
		}
		TopicMapExporter.endElement(handler, "topicmap");
	}

	/**
	 * Provides informations about the geometry of this competence star.
	 *
	 * @param   handler     this object will get the generated SAX events
	 *
	 * @see     #exportCompetenceStar
	 */
	private void exportAdditionalInformation(ContentHandler handler) throws SAXException {
		TopicMapExporter.startElement(handler, "Kompetenzstern", null);
		addElement(handler, "Template", getTemplate().getName());
		TopicMapExporter.startElement(handler, "Geometry", null);
		addElement(handler, "BACKGROUND_WIDTH", "" + BACKGROUND_WIDTH);
		addElement(handler, "BACKGROUND_HEIGHT", "" + BACKGROUND_HEIGHT);
		addElement(handler, "X_CENTER", "" + X_CENTER);
		addElement(handler, "Y_CENTER", "" + Y_CENTER);
		addElement(handler, "DIST_CENTER", "" + DIST_CENTER);
		addElement(handler, "DIST_CIRCLES", "" + DIST_CIRCLES);
		addElement(handler, "DIST_LAYER", "" + DIST_LAYER);
		TopicMapExporter.endElement(handler, "Geometry");
		TopicMapExporter.startElement(handler, "Colors", null);
		TopicMapExporter.startElement(handler, "COLOR_LAYER", null);
		addElement(handler, "R", "" + COLOR_LAYER.getRed());
		addElement(handler, "G", "" + COLOR_LAYER.getGreen());
		addElement(handler, "B", "" + COLOR_LAYER.getBlue());
		TopicMapExporter.endElement(handler, "COLOR_LAYER");
		TopicMapExporter.endElement(handler, "Colors");
		addElement(handler, "KriterienType", kriteriumTypeID);
		TopicMapExporter.startElement(handler, "Werte", null);
		for (int i = 0; i < werteCount; i++) {
			BaseTopic topic = (BaseTopic) werte.elementAt(i);
			addElement(handler, "Wert", topic.getName());
		}
		TopicMapExporter.endElement(handler, "Werte");
		TopicMapExporter.endElement(handler, "Kompetenzstern");
	}

	/**
	 * @return  the other competence stars that are to be compared
	 *          to this as Enumeration of KompetenzsternTopics
	 */
	private Enumeration getStarsToCompare() {
		Vector topicIDs = cm.getTopicIDs(TOPICTYPE_KOMPETENZSTERN, getID());
		Vector stars = new Vector();
		Enumeration e = topicIDs.elements();
		while (e.hasMoreElements()) {
			String topicID = (String) e.nextElement();
			stars.add(as.getLiveTopic(topicID, 1));
		}
		return stars.elements();
	}

	/**
	 * Utility method to export an XML element without attributes
	 * and with a single text node as child.
	 *
	 * @param   handler     this object will get the generated SAX events
	 * @param   elemName    the name of the XML element
	 * @param   value       the text of the child text node
	 *
	 * @see     #exportAdditionalInformation
	 */
	private static void addElement(ContentHandler handler, String elemName, String value)
															throws SAXException {
		TopicMapExporter.startElement(handler, elemName, null);
		TopicMapExporter.characters(handler, value);
		TopicMapExporter.endElement(handler, elemName);
	}

	/**
	 * Collects all invisible subcriteria of the criteria which are shown in the map.
	 *
	 * @param   criteriaTypeID  specifies the type of the top-level criteria
	 * @param   usemap  specifies from which map the criteria are collected
	 * @param   topics  this container collects all subcriteria found
	 * @param   assocs  this container collects all associations to subcriteria
	 *
	 * @see     #exportCompetenceStar
	 */
	private void collectCriteria(String criteriaTypeID, BaseTopicMap usemap,
													Hashtable topics, Hashtable assocs) {
		collectAllCriteria(criteriaTypeID, usemap, topics, assocs);
		removeDuplicates(usemap, topics, assocs);
	}

	/**
	 * Removes all those topics and associations from the given containers that are
	 * visible in the given map.
	 *
	 * @param   usemap  specifies which map contains the visible criteria
	 * @param   topics  this container contains all subcriteria
	 * @param   assocs  this container contains all associations to subcriteria
	 *
	 * @see     #collectCriteria
	 */
	private void removeDuplicates(BaseTopicMap usemap, Hashtable topics, Hashtable assocs) {
		Enumeration topix = usemap.getTopics().elements();
		while(topix.hasMoreElements()) {
			BaseTopic topic = (BaseTopic) topix.nextElement();
			topics.remove(topic.getID());
		}
		Enumeration assox = usemap.getAssociations().elements();
		while(assox.hasMoreElements()) {
			BaseAssociation assoc = (BaseAssociation) assox.nextElement();
			assocs.remove(assoc.getID());
		}
	}

	/**
	 * Collects all criteria and subcriteria in the given map.
	 *
	 * @param   criteriaTypeID  specifies the type of the top-level criteria
	 * @param   usemap  specifies from which map the criteria are collected
	 * @param   topics  this container collects all subcriteria found
	 * @param   assocs  this container collects all associations to subcriteria
	 *
	 * @see     #collectCriteria
	 */
	private void collectAllCriteria(String criteriaTypeID, BaseTopicMap usemap,
													Hashtable topics, Hashtable assocs) {
		Vector crits = getKriterien();
		// ### Vector topicsInUseMap = cm.getTopics(criteriaTypeID, new Hashtable(), getID());
		//
		Enumeration e = crits.elements();
		while (e.hasMoreElements()) {
			BaseTopic crit = (BaseTopic) e.nextElement();
			collectSubCriteria(crit.getID(), topics, assocs);
		}
	}

	/**
	 * Collects subcriteria of the specified criteria (recursively).
	 *
	 * @param   topic   a criterium topic
	 * @param   topics  this container collects all subcriteria found
	 * @param   assocs  this container collects all associations to subcriteria
	 *
	 * @see     #collectAllCriteria
	 */
	private void collectSubCriteria(String critID, Hashtable topics, Hashtable assocs) {
		Vector subCrits = getSubCriteria(critID);
		//
		Enumeration e = subCrits.elements();
		while (e.hasMoreElements()) {
			BaseTopic subCrit = (BaseTopic) e.nextElement();
			String subCritID = subCrit.getID();
			// ### if (subCrit.getType().equals(TOPICTYPE_BEWERTUNGS_KRITERIUM)) {
			BaseAssociation assoc = cm.getAssociation(SEMANTIC_STERN_COMPOSITION, critID, subCritID);
			assocs.put(assoc.getID(), assoc);
			if (topics.get(subCritID) == null) {  // avoid short circuits
				topics.put(subCritID, subCrit);
				collectSubCriteria(subCritID, topics, assocs);
			}
			// ### }
		}
	}
}
