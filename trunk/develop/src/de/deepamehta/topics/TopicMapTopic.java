package de.deepamehta.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.BaseTopicMap;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.Detail;
import de.deepamehta.FileServer;
import de.deepamehta.PresentableAssociation;
import de.deepamehta.PresentableTopic;
import de.deepamehta.PresentableTopicMap;
import de.deepamehta.PropertyDefinition;
import de.deepamehta.TopicGeometry;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateCommands;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.CorporateTopicMap;
import de.deepamehta.service.DeepaMehtaServiceUtils;
import de.deepamehta.service.Session;
import de.deepamehta.topics.helper.ArchiveFileCollector;
import de.deepamehta.topics.helper.TopicMapExporter;
import de.deepamehta.util.DeepaMehtaUtils;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.ImageIcon;



/**
 * This active kernel topic represents a <i>Topicmap</i> (in DeepaMehta a synonym is
 * <i>View</i>). Topics representing topicmaps are mainly represented in the workspaces
 * in the upper display area.
 *
 * <h4>Active behavoir</h4>
 *
 * ### The active <i>evocation</i> behavoir of a <code>TopicMapTopic</code> is setting the
 * initial view by duplicating the generic view (<code>t-genericmap</code>) in corporate
 * memory.
 * <p>
 * The active <i>dying</i> behavoir of a <code>TopicMapTopic</code> is deleting the view
 * from corporate memory as well as from users personal workspace and causing the client
 * to close this view if opened.
 * <p>
 * The <i>default command</i> (triggered by doublecklicking) provided by a
 * <code>TopicMapTopic</code> is <i>opening</i> the topicmap.
 * <p>
 * The <i>context-commands</i> (about to appear in the topic context menu) provided by a
 * <code>TopicMapTopic</code> are <i>publishing</i> the topicmap resp. <i>exporting</i>
 * this topicmap to an archive file. In regard of publishing it is active behavoir of a
 * <code>TopicMapTopic</code> to determe to which workgroups this topicmap may published
 * by the user. This depends on the status of this view (newly created in personal
 * workspace vs. opened from a shared workspace), group memberships of the user, further
 * publish permissions of the user and administrator status of the user (yes/no).
 * <p>
 * The active <i>renaming</i> behavoir of a <code>TopicMapTopic</code> is causing the
 * client to rename the corresponding topicmap editor if this view is opened.
 * <p>
 * The active <i>property change</i> behavoir of a <code>TopicMapTopic</code> is causing
 * the client to update the background color of the corresponding topicmap editor (only
 * <code>VIEWMODE_USE</code> for now) if this view is opened.
 *
 * <h4>Importing of topicmaps</h4>
 * The importing of topicmaps on the server side is divided into three steps:
 * <p><ol>
 * <li>sending of directive for choosing archive file name ({@link #executeCommand})</li>
 * <li>sending of directives for copying of archive file to client's document repository
 *     and upload to the server ({@link #doImport})</li>
 * <li>importing from archive file stored in server's document repository
 *     ({@link #importFromFile})</li>
 * </ol>
 * <hr>
 * Last change: 26.1.2009 (2.0b9)<br>
 * J&ouml;rg Richter<br>
 * jri@deepamehta.de
 */
public class TopicMapTopic extends LiveTopic {



	// **************
	// *** Fields ***
	// **************



	// padding for topicmap export as PNG
	private final int PAD_TOP = 30;
	private final int PAD_LEFT = 30;
	private final int PAD_BOTTOM = 45;
	private final int PAD_RIGHT = 120;

	/**
	 * ### If this topicmap is part of a shared workspace this variable holds the respective
	 * workspace. May be null.
	 * <p>
	 * Initialized by {@link #updateOwnership}<br>
	 * Accessed by {@link #openTopicmap}
	 */
	private BaseTopic owner;



	// *******************
	// *** Constructor ***
	// *******************



	public TopicMapTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}



	// ***************
	// *** Methods ***
	// ***************



	/**
	 * Exports this topicmap to corporate document repository and instructs the client to
	 * <ol>
	 * <li>download the exported file
	 * <li>show the exported file as document topic
	 * <li>notify the user about completion
	 * </ol>
	 * Export performs in the background.
	 * Note: the returned directives are send <i>asynchronously</i>.
	 * <p>
	 * Presumption: all the document files are existing in corporate document repository.
	 * <p>
	 * References checked: 9.11.2004 (2.0b3)
	 *
	 * @throws	DeepaMehtaException	if an I/O error occurrs while export
	 *
	 * @see		de.deepamehta.service.ApplicationService#exportTopicmap
	 */
	public final CorporateDirectives export(Session session, String topicmapID, String viewmode, int x, int y)
																				throws DeepaMehtaException {
		try {
			// Note: only directives which can be send asynchronously are possible here
			CorporateDirectives directives = new CorporateDirectives();
			//
			// --- trigger getExportDocumentName() hook ---
			String fileBasename = getExportFileBaseName();
			//
			String formatID = as.getExportFormat(session.getUserID(), directives).getID();
			File exportFile;
			if (formatID.equals("t-xml")) {
				exportFile = new TopicMapExporter(this).createTopicmapArchive(fileBasename);
			} else if (formatID.equals("t-svg")) {
				exportFile = new TopicMapExporter(this).createSVGFile(fileBasename);
			} else if (formatID.equals("t-pdf")) {
				exportFile = new TopicMapExporter(this).createPDFFile(fileBasename);
			} else {
				throw new DeepaMehtaException("unexpected export format ID: \"" + formatID + "\"");
			}
			String filename = exportFile.getName();
			Long lastModified = new Long(exportFile.lastModified());
			// 1) download the exported file
			directives.add(DIRECTIVE_DOWNLOAD_FILE, filename, lastModified, new Integer(FILE_DOCUMENT));
			// 2) show the exported file as document topic
			//
			// --- trigger getExportDocumentName() hook ---
			String topicName = getExportDocumentName();
			// --- trigger getExportDocumentType() hook ---
			String typeID = getExportDocumentType();
			//
			PresentableTopic topic = new PresentableTopic(as.getNewTopicID(), 1, typeID , 1, topicName, new Point(x, y));
			Hashtable props = new Hashtable();
			props.put(PROPERTY_NAME, topicName);
			props.put(PROPERTY_FILE, filename);
			topic.setProperties(props);
			directives.add(DIRECTIVE_SHOW_TOPIC, topic, Boolean.TRUE, topicmapID);
			// 3) notify the user about completion
			directives.add(DIRECTIVE_SHOW_MESSAGE, "Export of topicmap \"" + getName() + "\" is complete (" +
				filename + ")", new Integer(NOTIFICATION_DEFAULT));
			//
			return directives;
		} catch (IOException e) {
			System.out.println("*** TopicMapTopic.export(): " + e);
			throw new DeepaMehtaException("I/O error: " + e.getMessage());
		} catch (NoClassDefFoundError e) {
			System.out.println("*** TopicMapTopic.export(): " + e);
			throw new DeepaMehtaException("class not found: " + e.getMessage());
		}
	}



	// **********************
	// *** Defining Hooks ***
	// **********************



	// ------------------
	// --- Life Cycle ---
	// ------------------



	public CorporateDirectives die(Session session) {
		CorporateDirectives directives = new CorporateDirectives();
		// causes the client to close this view in case it has opened it
		// ### directives.add(DIRECTIVE_CLOSE_EDITOR, getID());
		close(session, directives);
		// delete view from corporate memory
		as.deleteUserView(getID(), getVersion());
		// delete this topic from corporate memory and from live corporate memory
		// Note: super.die() must perform after
		directives.add(super.die(session));
		//
		return directives;
	}



	// ------------------------------------------
	// --- Reacting upon dedicated situations ---
	// ------------------------------------------



	public CorporateDirectives nameChanged(String name, String topicmapID, Session session) {
		CorporateDirectives directives = super.nameChanged(name, topicmapID, session);
		// rename editor
		directives.add(DIRECTIVE_RENAME_EDITOR, getID(), name);
		//
		return directives;
	}

	public boolean retypeAllowed(Session session) {
		return false;
	}



	// --------------------------
	// --- Providing Commands ---
	// --------------------------



	/**
	 * Creates the action commands for a <code>TopicMapTopic</code> and returns them.
	 * <p>
	 * A <code>TopicMapTopic</code> provides two commands: <i>publishing</i> and
	 * <i>exporting</i>.
	 *
	 * @see		de.deepamehta.service.ApplicationService#showTopicMenu
	 */
	public CorporateCommands contextCommands(String topicmapID, String viewmode,
								Session session, CorporateDirectives directives) {
		CorporateCommands commands = new CorporateCommands(as);
		//
		int editorContext = as.editorContext(topicmapID);
		commands.addNavigationCommands(this, editorContext, session);	// EDITOR_CONTEXT_VIEW only
		// --- publish ---
		if (editorContext == EDITOR_CONTEXT_PERSONAL) {
			commands.addPublishCommand(getID(), session, directives);
		}
		// --- export ---
		commands.addSeparator();
		commands.addExportCommand(session, directives);
		//
		commands.addStandardCommands(this, editorContext, viewmode, session, directives);
		//
		return commands;
	}

	/**
	 * @see		de.deepamehta.service.ApplicationService#showViewMenu
	 */
	public CorporateCommands viewCommands(String topicmapID, String viewmode,
								Session session, CorporateDirectives directives) {
		CorporateCommands commands = super.viewCommands(topicmapID, viewmode, session, directives);
		//
		int editorContext = as.editorContext(topicmapID);
		if (editorContext == EDITOR_CONTEXT_PERSONAL) {
			commands.addWorkspaceTopicTypeCommands(session, directives);
			commands.addImportCommand(session);
		} else if (editorContext == EDITOR_CONTEXT_VIEW) {
			commands.addSearchByTopictypeCommand(viewmode, session, directives);
			commands.addSeparator();
			commands.addCreateCommands(viewmode, session, directives);
			commands.addSeparator();
			//
			commands.addCloseCommand(session);
			commands.addHideAllCommands(topicmapID, viewmode, session);
			commands.addDeleteTopicmapCommand(session);
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

	/**
	 * Subclasses can override this method to customize the topic property form.
	 * <p>
	 * ### The default implementation does nothing.
	 *
	 * @see		TypeTopic#makeTypeDefinition
	 */
	public static void buttonCommand(PropertyDefinition propDef, ApplicationService as, Session session) {
		String propName = propDef.getPropertyName();
		if (propName.equals(PROPERTY_BACKGROUND_IMAGE)) {
			propDef.setActionButton(as.string(BUTTON_ASSIGN_FILE), CMD_ASSIGN_BACKGROUND);
		} else {
			LiveTopic.buttonCommand(propDef, as, session);
		}
	}



	// --------------------------
	// --- Executing Commands ---
	// --------------------------



	/**
	 * @see		de.deepamehta.service.ApplicationService#executeTopicCommand
	 */
	public CorporateDirectives executeCommand(String command, Session session,
						String topicmapID, String viewmode) throws DeepaMehtaException {
		// ### compare to GraphPanel.actionPerformed()
		CorporateDirectives directives = new CorporateDirectives();
		//
		StringTokenizer st = new StringTokenizer(command, COMMAND_SEPARATOR);
		String cmd = st.nextToken();
		// --- default command (opening) ---
		if (cmd.equals(CMD_DEFAULT)) {
			directives.add(open(session));
		// --- create topic ---
		} else if (cmd.equals(CMD_CREATE_TOPIC)) {
			String typeID = st.nextToken();
			int x = Integer.parseInt(st.nextToken());
			int y = Integer.parseInt(st.nextToken());
			createTopic(typeID, x, y, topicmapID, session, directives);
		// --- create associaion ---
		} else if (cmd.equals(CMD_CREATE_ASSOC)) {
			String topicID1 = st.nextToken();
			String topicID2 = st.nextToken();
			directives.add(as.createAssociation(ASSOCTYPE_GENERIC, topicID1, topicID2, session));
		// --- search by topic type ---
		} else if (cmd.equals(CMD_SEARCH_BY_TOPICTYPE)) {
			String typeID = st.nextToken();
			int x = Integer.parseInt(st.nextToken());
			int y = Integer.parseInt(st.nextToken());
			searchByTopicType(typeID, null, x, y, topicmapID, viewmode, session, directives);
		// --- hide all topics ---
		} else if (cmd.equals(CMD_HIDE_ALL)) {
			String typeID = st.nextToken();
			hideAll(typeID, topicmapID, viewmode, directives);
		// --- close view ---
		} else if (cmd.equals(CMD_CLOSE_VIEW)) {
			close(session, directives);
		// --- publish ---
		} else if (cmd.equals(CMD_PUBLISH)) {
			String workspaceID = st.nextToken();
			directives.add(publish(workspaceID, session));
		// --- import ---
		} else if (cmd.equals(CMD_IMPORT_TOPICMAP)) {
			directives.add(DIRECTIVE_CHOOSE_FILE);
		// --- export ---
		} else if (cmd.equals(CMD_EXPORT_TOPICMAP)) {
			int x, y;
			if (st.hasMoreTokens()) {
				x = Integer.parseInt(st.nextToken());
				y = Integer.parseInt(st.nextToken());
			} else {
				// ### triggered as context command
				// ### geom mode required here (NEAR)
				x = 100;
				y = 100;
			}
			doExport(topicmapID, viewmode, x, y, session, directives);
		// --- set export format ---
		} else if (cmd.equals(CMD_SET_EXPORT_FORMAT)) {
			String userID = session.getUserID();
			String formatID = st.nextToken();
			setExportFormat(userID, formatID, directives);
		// --- delete topicmap ---
		} else if (cmd.equals(CMD_DELETE_TOPICMAP)) {
			delete(session.getPersonalWorkspace().getID(), VIEWMODE_USE, directives);
		// --- help ---
		} else if (cmd.equals(CMD_SHOW_HELP)) {
			String typeID = st.nextToken();
			int x = Integer.parseInt(st.nextToken());
			int y = Integer.parseInt(st.nextToken());
			showTypeHelp(typeID, x, y, session, directives);
		//
		} else if (cmd.equals(CMD_ASSIGN_BACKGROUND)) {
			directives.add(DIRECTIVE_CHOOSE_FILE);
		} else if (cmd.equals(CMD_PROCESS_FILELIST)) {
			processFilelist(command, session, directives);
		} else if (cmd.equals(CMD_PROCESS_STRING)) {
			processString(command, directives);
		} else {
			return super.executeCommand(command, session, topicmapID, viewmode);
		}
		//
		return directives;
	}

	/**
	 * The chained action of a <code>TopicMapTopic</code> performs ### opening (the default
	 * action) and publishing of this topicmap.
	 *
	 * @see		de.deepamehta.service.ApplicationService#performChainedTopicCommand
	 */
	public CorporateDirectives executeChainedCommand(String command, String result,
								String topicmapID, String viewmode, Session session)
								throws DeepaMehtaException {
		StringTokenizer st = new StringTokenizer(command, COMMAND_SEPARATOR);
		String cmd = st.nextToken();
		//
		if (cmd.equals(CMD_IMPORT_TOPICMAP)) {
			// ### no way to pass the real geometry
			return doImport(result, session, NEW_TOPIC_X, NEW_TOPIC_Y);
		} else if (cmd.equals(CMD_ASSIGN_BACKGROUND)) {
			CorporateDirectives directives = new CorporateDirectives();
			// Note: the result of a DIRECTIVE_CHOOSE_FILE contains the absolute
			// path of the (client side) selected icon file
			copyAndUpload(result, FILE_BACKGROUND, PROPERTY_BACKGROUND_IMAGE, session, directives);
			return directives;
		} else {
			return super.executeChainedCommand(command, result, topicmapID, viewmode, session);
		}
	}



	// ---------------------------
	// --- Handling Properties ---
	// ---------------------------



	/**
	 * @see		de.deepamehta.service.ApplicationService#setTopicData
	 */
	public CorporateDirectives propertiesChanged(Hashtable newProps, Hashtable oldProps,
											String topicmapID, String viewmode, Session session) {
		// ### compare to LiveTopic.propertiesChanged(), UserTopic.propertiesChanged(),
		// ### WorkspaceTopic.propertiesChanged()
		CorporateDirectives directives = super.propertiesChanged(newProps, oldProps,
			topicmapID, viewmode, session);
		// --- "Background Image" ---
		String prop = (String) newProps.get(PROPERTY_BACKGROUND_IMAGE);
		if (prop != null) {
			System.out.println(">>> \"" + PROPERTY_BACKGROUND_IMAGE + "\" property has been " +
				"changed -- send DIRECTIVE_SET_EDITOR_BGIMAGE");
			// Note: the DIRECTIVE_SET_EDITOR_BGIMAGE must be queued, because
			// the image upload must be completed before the image can be shown
			CorporateDirectives imageDirective = new CorporateDirectives();
			imageDirective.add(DIRECTIVE_SET_EDITOR_BGIMAGE, getID(), prop);
			directives.add(DIRECTIVE_QUEUE_DIRECTIVES, imageDirective);
			// ### Note: the background image can only be changed for VIEWMODE_USE
		}
		// --- "Background Color" ---
		prop = (String) newProps.get(PROPERTY_BACKGROUND_COLOR);
		if (prop != null) {
			System.out.println(">>> \"" + PROPERTY_BACKGROUND_COLOR + "\" property has been " +
				"changed -- send DIRECTIVE_SET_EDITOR_BGCOLOR");
			//
			directives.add(DIRECTIVE_SET_EDITOR_BGCOLOR, getID(), prop);
			// ### Note: the background color can only be changed for VIEWMODE_USE
		}
		// --- "Icon" ---
		prop = (String) newProps.get(PROPERTY_ICON);
		if (prop != null) {
			System.out.println(">>> \"" + PROPERTY_ICON + "\" property has been changed " +
				"-- send DIRECTIVE_SET_EDITOR_ICON (queued)");
			// Note: the DIRECTIVE_SET_EDITOR_ICON must be queued, because
			// the icon upload must be completed before the icon can be shown
			CorporateDirectives iconDirective = new CorporateDirectives();
			iconDirective.add(DIRECTIVE_SET_EDITOR_ICON, getID(), prop);
			directives.add(DIRECTIVE_QUEUE_DIRECTIVES, iconDirective);
		}
		//
		return directives;
	}

	public static void propertyLabel(PropertyDefinition propDef, ApplicationService as, Session session) {
		if (as.getLanguage() == LANGUAGE_GERMAN) {
			String propName = propDef.getPropertyName();
			if (propName.equals(PROPERTY_BACKGROUND_IMAGE)) {
				propDef.setPropertyLabel("Hintergrund-Bild");
			} else if (propName.equals(PROPERTY_BACKGROUND_COLOR)) {
				propDef.setPropertyLabel("Hintergrund-Farbe");
			}
		}
	}



	// ----------------------
	// --- Topicmap Hooks ---
	// ----------------------



	/**
	 * Opens this topicmap. Precondition: this topicmap is currently not opened.
	 * <p>
	 * Adds the directives for opening this topicmap to the specified directives.
	 * Called in 3 situaltions:
	 * <ul>
	 * <li>(interactively) -- this view is double-clicked and is not already open
	 * <li>(programatically) -- this view is opened while restoring a user session
	 * <li>(programatically) -- this view is opened while starting a demo session
	 * </ul>
	 * <p>
	 * Note: overridden in KompetenzsternTopic (super implementation is called)
	 * <p>
	 * References checked: 23.5.2003 (2.0a18)
	 *
	 * @throws	DeepaMehtaException	if an error occurrs while opening
	 *
	 * @see		#open
	 * @see		de.deepamehta.service.ApplicationService#startDemo
	 * @see		de.deepamehta.service.ApplicationService#addViewsInUse
	 */
	public String openTopicmap(Session session, CorporateDirectives directives) throws DeepaMehtaException {
		if (isShared()) {
		 	// Note: this topicmap is about to be personalized
			return openSharedTopicmap(session, directives, owner);
		} else {
			openPersonalTopicmap(session, directives);
			return getID();
		}
	}

	// --- Exporting ---

	/**
	 * This hook can be used to modify the export behaviour of this topicmap.
	 * <p>
	 * The default implementation exports all topics and associations of this topicmap,
	 * along with their icon and document files.
	 * <p>
	 * Note: The application KS-Editor modifies the default export behavoir.
	 * <p>
	 * References checked: 26.9.2003 (2.0b2)
	 *
	 * @param   handler     this object will get the generated SAX events
	 * @param   collector   this object will collect document and icon files.
	 *                      This parameter may be <code>null</code>, which signalizes
	 *                      that this is an export to SVG or PDF.
	 *
	 * @see		de.deepamehta.topics.helper.TopicMapExporter#transformTopicmap
	 * @see		de.deepamehta.topics.helper.TopicMapExporter#exportViewMode
	 */
	public void exportTopicmap(ContentHandler handler, ArchiveFileCollector collector) throws SAXException {
		Hashtable attribs = new Hashtable();
		attribs.put("ID", getID());
		attribs.put("name", getName());
		attribs.put("type", getType());
		TopicMapExporter.startElement(handler, "topicmap", attribs);
		TopicMapExporter.exportProperties(getProperties(), getID(), handler, collector != null);
		if (collector != null) {
			collector.putIcon(getIconfile());
		}
		// retrieve topics and assocs from corporate memory
		CorporateTopicMap cmap = new CorporateTopicMap(as, getID(), getVersion());
		BaseTopicMap map = cmap.getTopicMap();
		Hashtable topics = map.getTopics();
		Hashtable assocs = map.getAssociations();
		// export topics and assocs
		TopicMapExporter.exportTopics(topics.elements(), handler, true, as, collector);
		TopicMapExporter.exportAssociations(assocs.elements(), handler, true, as, collector);
		// --- save the type of the map for export
		topics.put(getID(), this);
		// --- export type definitions
		TopicMapExporter.exportTypes(as.types(topics.elements()).elements(), handler, collector, as);
		TopicMapExporter.exportTypes(as.types(assocs.elements()).elements(), handler, collector, as);
		// finish the export of this viewmode
		TopicMapExporter.endElement(handler, "topicmap");
	}

	// ---

	/**
	 * @return	the name of the stylesheet for SVG creation
	 *
	 * @see		de.deepamehta.topics.helper.TopicMapExporter#createSVGFile
	 * @see		de.deepamehta.topics.helper.TopicMapExporter#createPDFFile
	 */
	public String getSVGStylesheetName() {
		return "stylesheets/xml2svg.xsl";
	}

	/**
	 * @return	the name of the stylesheet for FO creation
	 *
	 * @see		de.deepamehta.topics.helper.TopicMapExporter#createPDFFile
	 */
	public String getFOStylesheetName() {
		return "stylesheets/xml2fo.xsl";
	}

	// ---

	/**
	 * @return  the ID of the document type topic representing the exported file
	 *
	 * @see     #export
	 */
	public String getExportDocumentType() {
		return TOPICTYPE_DOCUMENT;
	}

	/**
	 * @return  the name of the document topic representing the exported file
	 *
	 * @see     #export
	 */
	public String getExportDocumentName() {
		return getName();
	}

	// ---

	/**
	 * @return  the base file name of the exported file (excluding the file type suffix)
	 *
	 * @see     #export
	 */
	public String getExportFileBaseName() {
		return "topicmap-" + getID() + "-" + getVersion();
	}



	// **********************
	// *** Public Methods ***
	// **********************



	/**
	 * Exports a visible redition of this topicmap as PNG image file.
	 *
	 * @return	Geometry data of the topics of this topicmap. Vector of {@link de.deepamehta.TopicGeometry}.
	 */
	public Vector exportToPNG() {
		Vector geometryData = new Vector();
		// --- retrieve topics and associations from corporate memory ---
		PresentableTopicMap topicmap = new CorporateTopicMap(as, getID(), getVersion()).getTopicMap();
		// --- create image ---
		Rectangle bounds = DeepaMehtaUtils.getBounds(topicmap);
		final int imageWidth = bounds.width + PAD_LEFT + PAD_RIGHT;
		final int imageHeight = bounds.height + PAD_TOP + PAD_BOTTOM;
		BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		//
		// ### g.setColor(COLOR_VIEW_BGCOLOR);
		g.setBackground(COLOR_VIEW_BGCOLOR);
		g.clearRect(0, 0, imageWidth, imageHeight);	// ### Mac OS X, Java 1.4: fillRect() following drawing commands are ignored!
		// ### g.setColor(DeepaMehtaUtils.parseHexColor(color));
		// --- paint topicmap ---
		g.translate(PAD_LEFT - bounds.x, PAD_TOP - bounds.y);
		//
		paintAssociations(topicmap.getAssociations().elements(), g, topicmap.getTopics());
		paintTopics(topicmap.getTopics().elements(), g, geometryData, bounds);
		// --- save image as PNG file ---
		File file = new File(FileServer.repositoryPath(FILE_DOCUMENT) + "topicmap-" + getID() + ".png");
		DeepaMehtaServiceUtils.createImageFile(image, file);
		//
		return geometryData;
	}

	private void paintTopics(Enumeration topics, Graphics g, Vector geometryData, Rectangle bounds) {
		// ### compare to GraphPanel.paintNode()
		while (topics.hasMoreElements()) {
			PresentableTopic topic = (PresentableTopic) topics.nextElement();
			Point p = topic.getGeometry();
			// --- paint icon ---
			String iconfile = as.getLiveTopic(topic).getIconfile();
			// Note 1: Toolkit.getImage() is used here instead of createImage() in order to utilize
			// the Toolkits caching mechanism
			// Note 2: ImageIcon is a kluge to make sure the image is fully loaded before we proceed
			Image icon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(FILESERVER_ICONS_PATH + iconfile)).getImage();
			int iconWidth = icon.getWidth(null);
			int iconHeight = icon.getHeight(null);
			int iw2 = iconWidth / 2;
			int ih2 = iconHeight / 2;
			int x1 = p.x - iw2;
			int y1 = p.y - ih2;
			g.drawImage(icon, x1, y1, null);
			// --- paint name ---
			int x2 = x1 + iconWidth;
			int y2 = y1 + iconHeight;
			// ### g.setFont(font);
			g.setColor(TEXT_COLOR);
			g.drawString(topic.getName(), x1, y2 + 12 /* ### font.getSize() */ + 2);
			// --- add geometry data ---
			geometryData.addElement(new TopicGeometry(topic.getID(), x1 + PAD_LEFT - bounds.x, y1 + PAD_TOP - bounds.y,
				x2 + PAD_LEFT - bounds.x, y2 + PAD_TOP - bounds.y));
		}
	}

	private void paintAssociations(Enumeration assocs, Graphics g, Hashtable topics) {
		// ### compare to GraphPanel.paintEdge()
		while (assocs.hasMoreElements()) {
			PresentableAssociation assoc = (PresentableAssociation) assocs.nextElement();
			Point p1 = ((PresentableTopic) topics.get(assoc.getTopicID1())).getGeometry();
			Point p2 = ((PresentableTopic) topics.get(assoc.getTopicID2())).getGeometry();
			//
			String assocTypeID = assoc.getType();
			TypeTopic type = as.type(assocTypeID, 1);
			Color color = DeepaMehtaUtils.parseHexColor(type.getAssocTypeColor());
			boolean directed = !assocTypeID.equals("at-generic") &&			
				!assocTypeID.equals("at-kompetenzstern");		// ### application specific
			//
			g.setColor(color);
			DeepaMehtaUtils.paintLine(g, p1.x, p1.y, p2.x, p2.y, directed);
		}
	}



	// *************************
	// *** Protected Methods ***
	// *************************



	/**
	 * Handles <code>CMD_CREATE_TOPIC</code>.
	 *
	 * @see		#executeCommand
	 * @see		ChatBoardTopic#executeCommand
	 */
	protected final String createTopic(String typeID, int x, int y, String topicmapID, Session session,
																		CorporateDirectives directives) {
		String topicID = as.getNewTopicID();
		directives.add(as.createTopic(topicID, typeID, x, y, topicmapID, session));
		directives.add(DIRECTIVE_SELECT_TOPIC, topicID);
		// ### standard action is hardcoded (rename)
		rename(topicID, typeID, "", session, directives);	// Note: rename() is from LiveTopic
		//
		return topicID;
	}

	/**
	 * @param	relTopicID	if set to a non-null value, the result set is restricted to topics who
	 *						have an association to the specified topic
	 *
	 * @see		#executeCommand
	 */
	protected final void searchByTopicType(String typeID, String relTopicID, int x, int y,
									String topicmapID, String viewmode,
									Session session, CorporateDirectives directives) {
		TypeTopic typeProxy = as.type(typeID, 1);	// version=1
		String typeName = typeProxy.getName();
		BaseTopic containerType = as.getSearchType(typeID);
		// error check 1
		if (containerType == null) {
			throw new DeepaMehtaException("the container-type of type \"" +
				typeName + "\" (" + typeID + ") is unknown");
		}
		// --- create container ---
		String containerName = containerName(typeProxy);
		String containerID = as.getNewTopicID();
		PresentableTopic container = new PresentableTopic(new BaseTopic(containerID, 1,
			containerType.getID(), 1, containerName));	// version=1, typeVersion=1
		container.setGeometry(new Point(x, y));
		// ### Note: property must be set before container is inited
		if (relTopicID != null) {
			as.setTopicProperty(containerID, 1, PROPERTY_RELATED_TOPIC_ID, relTopicID);
		}
		// Note: the container is created here to trigger the query, default is evoke=true, override=true
		LiveTopic containerProxy = as.createLiveTopic(container, topicmapID, viewmode, session, directives);
		// error check 2
		if (!(containerProxy instanceof ContainerTopic)) {
			// Note: catched by this method and reported as warning ### should error
			throw new DeepaMehtaException("container " + containerProxy + " is not of " +
				"a ContainerTopic subclass, but of \"" + containerProxy.getClass() + "\"");
		}
		// --- show container ---
		directives.add(DIRECTIVE_SHOW_TOPIC, container);	// Note: evoke=false ### was TRUE -> evoked twice
		directives.add(((ContainerTopic) containerProxy).triggerQuery(topicmapID));
		directives.add(DIRECTIVE_SELECT_TOPIC, containerID);
		directives.add(DIRECTIVE_FOCUS_PROPERTY);
	}



	// ***********************
	// *** Private Methods ***
	// ***********************



	/**
	 * Initializes the {@link #owner} field.
	 * <p>
	 * Checks wheather this topicmap is part of a shared workspace resp. part of the personal workspace.
	 *
	 * @see		#isShared
	 * @see		#getOwner
	 */
	private void updateOwnership() throws DeepaMehtaException {
		this.owner = as.getTopicmapOwner(getID());		// throws DME
	}

	// ---

	private boolean isShared() {
		updateOwnership();	// ### initializes "owner"
		return owner != null && owner.getType().equals(TOPICTYPE_WORKSPACE);
	}

	public BaseTopic getOwner() {
		updateOwnership();	// ### initializes "owner"
		return owner;
	}

	// ---

	private void hideAll(String typeID, String topicmapID, String viewmode, CorporateDirectives directives) {
		Vector topicIDs = cm.getTopicIDs(typeID, topicmapID);
		System.out.println(">>> TopicMapTopic.hideAll(): " + topicIDs.size() +
			" topics of type \"" + typeID + "\" found in view \"" + topicmapID + "\"");
		Enumeration e = topicIDs.elements();
		while (e.hasMoreElements()) {
			String topicID = (String) e.nextElement();
			as.getLiveTopic(topicID, 1).hide(topicmapID, viewmode, false, directives);
		}
	}

	/**
	 * References checked: 10.9.2008 (2.0b8)
	 *
	 * @see		#publish
	 * @see		#doExport
	 */
	private void addPublishDirectives(Session session, CorporateDirectives directives) {
		new CorporateTopicMap(as, getID(), 1).addPublishDirectives(session, directives);
	}

	/**
	 * References checked: 9.8.2001 (2.0a11)
	 *
	 * @see		#searchByTopicType
	 */
	private String containerName(TypeTopic type) {
		return type.getPluralNaming();
	}



	// ---------------
	// --- Opening ---
	// ---------------



	/**
	 * Handles CMD_DEFAULT (the user double-clicked this view)
	 *
	 * @see		#executeCommand
	 */
	private CorporateDirectives open(Session session) throws DeepaMehtaException {
		CorporateDirectives directives = new CorporateDirectives();
		//
		if (as.isViewOpen(getID(), session.getUserID())) {
			directives.add(DIRECTIVE_SELECT_EDITOR, getID());
		} else {
			// --- trigger openTopicmap() hook ---
			String viewID = openTopicmap(session, directives);
			//
			directives.add(DIRECTIVE_SELECT_EDITOR, viewID);
			// --- remember state ---
			as.addViewInUse(viewID, session);
		}
		//
		return directives;
	}

	// ---

	/**
	 * Opens this perosnal view.
	 *
	 * @see		#openTopicmap
	 */
	private void openPersonalTopicmap(Session session, CorporateDirectives directives) throws DeepaMehtaException {
		PresentableTopic metadata = as.createPresentableTopic(this);
		CorporateTopicMap topicmap = new CorporateTopicMap(as, getID(), getVersion());
		directives.add(DIRECTIVE_SHOW_VIEW, metadata, topicmap);
	}

	/**
	 * Opens this group view.
	 * <p>
	 * Note: This topicmap is about to be personalized.
	 *
	 * @return	the ID of the personal view
	 *
	 * @see		#openTopicmap
	 */
	private String openSharedTopicmap(Session session, CorporateDirectives directives, BaseTopic owner)
																				throws DeepaMehtaException {
		System.out.println(">>> topicmap " + this + " is about to be personalized");
		// --- retrieve this topicmap from corporate memory ---
		CorporateTopicMap topicmap = new CorporateTopicMap(as, getID(), getVersion());
		//
		// --- create personal view topic ---
		String workspaceID = owner.getID();
		// create metadata for personal view
		String personalViewID = as.getNewTopicID();
		BaseTopic personalView = new BaseTopic(personalViewID, 1, getType(), getTypeVersion(), getName());
		PresentableTopic personalViewMetadata = as.createPresentableTopic(personalView,	getID());	// Note: getID() is the appearanceTopicID
		cm.setTopicData(personalViewID, 1, cm.getTopicData(getID(), 1));		// ### copy properties
		if (session.isDemo()) {
			// Note: there is no DIRECTIVE_SHOW_TOPIC here (compare with else case), the personalized view
			// must be created and loaded here (otherwise view commands can't be executed)
			as.createLiveTopic(personalView, null, null, session, directives);	// topicmapID=null, viewmode=null, evoke=true, override=true
			directives.add(DIRECTIVE_SHOW_VIEW, personalViewMetadata, topicmap, null, null);
		} else {
			// ### create 2 associations to identify the original topicmap and the origin owner
			// ### directly in corpoaret memory
			cm.createAssociation(as.getNewAssociationID(), 1, SEMANTIC_ORIGIN_MAP, 1, getID(), 1, personalViewID, 1);
			cm.createAssociation(as.getNewAssociationID(), 1, SEMANTIC_ORIGIN_WORKSPACE, 1, personalViewID, 1, workspaceID, 1);
			// --- show personal view topic and open the view ---
			// ### personalViewMetadata.setProperties(getProperties());						// ### copy properties
			// Note: order is crucial. The personal view topic must be added _before_
			// the actual view, otherwise the graph context commands for the view can't
			// be determined ### still true?
			directives.add(DIRECTIVE_SHOW_TOPIC, personalViewMetadata, Boolean.TRUE,
				session.getPersonalWorkspace().getID());
			directives.add(DIRECTIVE_SHOW_VIEW, personalViewMetadata, topicmap,
				session.getPersonalWorkspace().getID(), workspaceID);
		}
		//
		return personalViewID;
	}



	// ---------------
	// --- Closing ---
	// ---------------



	/**
	 * ### Handles CMD_CLOSE_VIEW (user choosed "Close" from view menu).
	 *
	 * @see		#executeCommand
	 * @see		#publish
	 */
	private void close(Session session, CorporateDirectives directives) {
		directives.add(DIRECTIVE_CLOSE_EDITOR, getID());
		directives.add(DIRECTIVE_SELECT_EDITOR, session.getPersonalWorkspace().getID());
	}



	// ------------------
	// --- Publishing ---
	// ------------------



	/**
	 * Returns the client directives for publishing this topicmap to the specified workspace.
	 *
	 * @see		#executeCommand
	 */
	private CorporateDirectives publish(String workspaceID, Session session) throws DeepaMehtaException {
		String workspaceName = cm.getTopic(workspaceID, 1).getName();
		Vector users = as.workgroupMembers(workspaceID);
		boolean isFirstPublishing = as.getOriginWorkspace(getID()) == null;
		String notifyText = isFirstPublishing ?
			"A new topicmap \"" + getName() + "\" has been published to workspace \"" +
			workspaceName + "\" by user \"" + session.getUserName() + "\"" :
			"Topicmap \"" + getName() + "\" of workspace \"" + workspaceName + "\" has " +
			"been updated by user \"" + session.getUserName() + "\"";
		// --- send notification emails ---
		// Note: the notification emails are send before the publishing performs because the topic ID
		// may change while publishing and the list of topics could not generated easily
		String subject = isFirstPublishing ?
			"New topicmap \"" + getName() + "\" published by \"" + session.getUserName() + "\"" :
			"Topicmap \"" + getName() + "\" updated by \"" + session.getUserName() + "\"";
		sendNotificationEmails(users, workspaceName, subject, notifyText);
		// --- remove topicmap from personal workspace ---
		CorporateDirectives directives = new CorporateDirectives();
		directives.add(DIRECTIVE_HIDE_TOPIC, getID(), Boolean.FALSE, session.getPersonalWorkspace().getID());
		// ### Note: the personal topicmap remains in the corporate memory as ballast as well as the associations.
		// The die-flag should set to TRUE. But the associations are not removed this way.
		// Think again about the die() hook.
		// --- upload documents ---
		addPublishDirectives(session, directives);
		// --- publish this map ---
		CorporateDirectives notify = new CorporateDirectives();		// this directives are broadcasted to every workspace member
		publishTo(workspaceID, isFirstPublishing, notify);
		// --- close view ---
		if (as.isViewOpen(getID(), session.getUserID())) {
			close(session, directives);
		}
		// --- notify all workspace members ---
		notify.add(DIRECTIVE_SHOW_MESSAGE, notifyText, new Integer(NOTIFICATION_DEFAULT));
		as.broadcast(notify, users.elements(), true);
		//
		return directives;
	}

	/**
	 * @param	directives	this directives are broadcasted to every workspace member
	 *
	 * @see		#publish
	 */
	private void publishTo(String workspaceID, boolean isFirstPublishing, CorporateDirectives directives) {
		if (isFirstPublishing) {
			// --- create topic in shared workspace ---
			System.out.println("> TopicMapTopic.publishTo(): publish " + this + " to \"" + workspaceID +
				"\" (first publishing of this map into this group)");
			// adding DIRECTIVE_SHOW_TOPIC causes the client to creating a new topicmap topic in the shared workspace
			PresentableTopic topic = new PresentableTopic(this);
			directives.add(DIRECTIVE_SHOW_TOPIC, topic, Boolean.FALSE, as.getWorkspaceTopicmap(workspaceID).getID());
		} else {
			BaseTopic originTopicmap = as.originTopicmap(getID());
			String originTopicmapID = originTopicmap.getID();
			int originTopicmapVersion = originTopicmap.getVersion();	// ###
			System.out.println("> TopicMapTopic.publishTo(): publish " + this + " to \"" +
				workspaceID + "\" (supersedes " + originTopicmap + " of this group)");
			// delete origin topicmap
			as.deleteUserView(originTopicmapID, originTopicmapVersion);
			// --- override origin topicmap with this topicmap ---
			as.updateView(getID(), getVersion(), originTopicmapID, originTopicmapVersion);
			// transfer topicmap properties
			Hashtable props = getProperties();
			// ### cm.setTopicData(originTopicmapID, 1, props);
			// transfer topicmap name
			// ### directives.add(DIRECTIVE_SET_TOPIC_NAME, originTopicmapID, getName(), new Integer(1));
			// Note: the topicmap name must not be transfered explicitly because the property change will trigger
			// the name change
			// ### One problem: the properties of the original topicmap in the shared workspace are not updated
			// immedeatly if the original topicmap is still selected in the publishers view. No clue why!
			directives.add(DIRECTIVE_SHOW_TOPIC_PROPERTIES, originTopicmapID, props, new Integer(1));
		}
	}

	// ---

	private void sendNotificationEmails(Vector users, String workspace, String subject, String notification) {
		try {
			System.out.println("> send notification emails to " + users.size() + " workspace members:");
			// "from"
			String from = as.getEmailAddress("t-rootuser");		// ###
			if (from == null || from.equals("")) {
				throw new DeepaMehtaException("email address of root user is unknown");
			}
			// "body"
			String body = "\rDear DeepaMehta User,\r\r" +
				"This automatic notification is send to you as a member of the DeepaMehta workspace \"" +
				workspace + "\".\r\r" +
				notification + ".\r\r" +
				"The topicmap contains the following topics:\r\r" + topicList();
			//
			Enumeration e = users.elements();
			while (e.hasMoreElements()) {
				BaseTopic user = (BaseTopic) e.nextElement();
				String emailAddress = as.getEmailAddress(user.getID());
				System.out.println("    " + user.getName() + " (" + emailAddress + ")");
				if (emailAddress != null && !emailAddress.equals("") ) {
					EmailTopic.sendMail(as.getSMTPServer(), from, emailAddress, "DeepaMehta: " + subject, body);		// EmailTopic.sendMail() throws DME
				}
			}
		} catch (Exception e) {
			System.out.println("*** notification emails not send (" + e + ")");
		}
	}

	private String topicList() {
		Hashtable groupedTopics = new Hashtable();
		//
		Vector topics = cm.getViewTopics(getID(), getVersion());
		Enumeration e = topics.elements();
		while (e.hasMoreElements()) {
			BaseTopic topic = (BaseTopic) e.nextElement();
			String typeID = topic.getType();
			Vector topicGroup = (Vector) groupedTopics.get(typeID);
			if (topicGroup == null) {
				topicGroup = new Vector();
				groupedTopics.put(typeID, topicGroup);
			}
			topicGroup.addElement(topic.getName());
		}
		//
		StringBuffer topicList = new StringBuffer();
		//
		Enumeration keys = groupedTopics.keys();
		while (keys.hasMoreElements()) {
			String typeID = (String) keys.nextElement();
			Vector topicGroup = (Vector) groupedTopics.get(typeID);
			String typeName = as.type(typeID, 1).getPluralNaming();
			topicList.append(topicGroup.size() + " " + typeName + ":\r");
			Enumeration e2 = topicGroup.elements();
			while (e2.hasMoreElements()) {
				String topicName = (String) e2.nextElement();
				topicList.append("    " + topicName + "\r");
			}
		}
		//
		return topicList.toString();
	}



	// -----------------
	// --- Exporting ---
	// -----------------



	/**
	 * @see		#executeCommand
	 */
	private void doExport(String topicmapID, String viewmode, int x, int y, Session session, CorporateDirectives directives) {
		addPublishDirectives(session, directives);
		directives.add(DIRECTIVE_QUEUE_MESSAGE, "export:" + getID() + ":" + topicmapID + ":" + viewmode + ":" + x + ":" + y);
	}

	/**
	 * @see		#executeCommand
	 */
	private void setExportFormat(String userID, String formatID, CorporateDirectives directives) {
		// ### compare to KompetenzsternTopic.setPreference()
		// ### compare to TemplateTopic.setKompetenzsternTypeID()
		try {
			// remove existing preference
			as.deleteAssociation(as.getAssociation(userID, SEMANTIC_PREFERENCE, 2,
				TOPICTYPE_EXPORT_FORMAT, false, directives));		// throws DME
		} catch (DeepaMehtaException e) {
			System.out.println("*** TopicMapTopic.setExportFormat(): " + e);
			directives.add(DIRECTIVE_SHOW_MESSAGE, "User \"" + userID + "\" had no export preference -- " +
				"now set to \"" + formatID + "\" (" + e.getMessage() + ")", new Integer(NOTIFICATION_WARNING));
		} finally {
			// add new preference
			cm.createAssociation(as.getNewAssociationID(), 1, SEMANTIC_PREFERENCE, 1, userID, 1, formatID, 1);
		}
	}



	// -----------------
	// --- Importing ---
	// -----------------



	/**
	 * Returns the directives for copying the (client side) selected archive file to
	 * client's document repository and then uploading it to the server and triggering
	 * the (server side) import process.
	 *
	 * @param   path  	archive file path that was choosed by the user
	 * @param   session
	 * @param   x  		coordinate of the position where the new topicmap icon
	 *					will be inserted
	 * @param   y  		coordinate of the position where the new topicmap icon
	 *					will be inserted
	 *
	 * @return     		vector of directives for the client
	 *
	 * @see		#executeChainedCommand
	 */
	private CorporateDirectives doImport(String path, Session session, int x, int y) {
		// ### compare to CMImportExportTopic.executeChainedCommand()
		// ### compare to LiveTopic.copyAndUpload()
		CorporateDirectives directives = new CorporateDirectives();
		// --- copy and upload ---
		String filename = copyAndUpload(path, FILE_DOCUMENT, session, directives);
		// --- begin import ---
		if (filename != null) {
			// queue the message being send back to the server to trigger the (server side) import process.
			directives.add(DIRECTIVE_QUEUE_MESSAGE, "import:" + filename + ":" + x + ":" + y);
		}
		//
		return directives;
	}



	// --------------------
	// --- Process Drop ---
	// --------------------



	private void processString(String command, CorporateDirectives directives) {
		String str = command.substring(command.indexOf(COMMAND_SEPARATOR) + 1);
		String topicID = as.getNewTopicID();
		PresentableTopic topic = new PresentableTopic(topicID, 1, TOPICTYPE_TOPIC, 1, "");
		Hashtable props = new Hashtable();
		// ### Should possibly create more than one paragraph.
		// ### A double line break could be interpreted as paragraph separation.
		props.put(PROPERTY_DESCRIPTION, "<html><body><p>" + str + "</p></body></html>");
		topic.setProperties(props);
		directives.add(DIRECTIVE_SHOW_TOPIC, topic, Boolean.TRUE, getID());
	}

	// ---

	private void processFilelist(String command, Session session, CorporateDirectives directives) {
		StringTokenizer st = new StringTokenizer(command, COMMAND_SEPARATOR);
		String cmd = st.nextToken();	// ### skip command
		//
		Vector filenames = new Vector();
		while (st.hasMoreTokens()) {
			String file = st.nextToken();
			filenames.addElement(file);
		}
		//
		File[] files = new File[filenames.size()];
		for (int i = 0; i < filenames.size(); i++) {
			files[i] = new File((String) filenames.elementAt(i));
		}
		// create topic
		/* ### String topicID = as.getNewTopicID();
		PresentableTopic topic = new PresentableTopic(topicID, 1, TOPICTYPE_TOPIC, 1, topicName);
		directives.add(DIRECTIVE_SHOW_TOPIC, topic, Boolean.TRUE, getID(), VIEWMODE_USE); */
		//
		importDocuments(files, /* ### topicID,*/ session, directives);
	}

	private void importDocuments(File[] files, /* String parentTopicID,*/ Session session, CorporateDirectives directives) {
		// create documents
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			String filename = file.getName();
			if (file.isDirectory()) {
				/* ### create topic
				String topicID = as.getNewTopicID();
				PresentableTopic topic = new PresentableTopic(topicID, 1, TOPICTYPE_TOPIC, 1, filename, parentTopicID);
				directives.add(DIRECTIVE_SHOW_TOPIC, topic, Boolean.TRUE, getID(), VIEWMODE_USE);
				//
				PresentableAssociation assoc = new PresentableAssociation(as.getNewAssociationID(), 1,
					ASSOCTYPE_COMPOSITION, 1, "", parentTopicID, 1, topicID, 1);
				directives.add(DIRECTIVE_SHOW_ASSOCIATION, assoc, Boolean.TRUE);
				//
				importDocuments(file.listFiles(), topicID, directives); ### must perform at client side */
				System.out.println("  > \"" + filename + "\" is a directory -- ignored");
			} else {
				// create document
				String topicID = as.getNewTopicID();
				String typeID;
				if (DeepaMehtaUtils.isImage(filename)) {
					typeID = TOPICTYPE_IMAGE;
					// copy and upload
					copyAndUpload(file.getPath(), FILE_IMAGE, session, directives);
				} else {
					typeID = TOPICTYPE_DOCUMENT;
					// copy
					directives.add(DIRECTIVE_COPY_FILE, file.getPath(), new Integer(FILE_DOCUMENT));
				}
				PresentableTopic topic = new PresentableTopic(topicID, 1, typeID, 1, filename);
				Hashtable props = new Hashtable();
				props.put(PROPERTY_FILE, filename);
				topic.setProperties(props);
				directives.add(DIRECTIVE_SHOW_TOPIC, topic, Boolean.TRUE, getID());
				//
				/* ### PresentableAssociation assoc = new PresentableAssociation(as.getNewAssociationID(), 1,
					ASSOCTYPE_COMPOSITION, 1, "", parentTopicID, 1, topicID, 1);
				directives.add(DIRECTIVE_SHOW_ASSOCIATION, assoc, Boolean.TRUE); */
			}
		}
	}



	// ------------
	// --- Help ---
	// ------------



	private void showTypeHelp(String typeID, int x, int y, Session session, CorporateDirectives directives) {
		Detail detail = createTopicHelp(typeID, x, y);
		directives.add(DIRECTIVE_SHOW_DETAIL, getID(), detail);
	}

	private Detail createTopicHelp(String typeID, int x, int y) {
		TypeTopic type = as.type(typeID, 1);
		String html = type.getProperty(PROPERTY_DESCRIPTION);
		String title = as.string(ITEM_SHOW_HELP, type.getName());
		Detail detail = new Detail(DETAIL_TOPICMAP, DETAIL_CONTENT_HTML, html, Boolean.FALSE, title, "??", new Point(x, y));	// ### command="??"
		return detail;
	}
}
