package de.deepamehta.client;

import de.deepamehta.BaseAssociation;
import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.Detail;
import de.deepamehta.Directive;
import de.deepamehta.Directives;
import de.deepamehta.FileServer;
import de.deepamehta.PresentableTopic;
import de.deepamehta.service.DeepaMehta;
import de.deepamehta.util.DeepaMehtaUtils;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.TransferHandler;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;



/**
 * Main controller of the graphical DeepaMehta client.
 * <p>
 * <hr>
 * Last change: 25.1.2009 (2.0b9)<br>
 * J&ouml;rg Richter<br>
 * jri@deepamehta.de
 */
public final class PresentationService implements DeepaMehtaConstants, GraphPanelControler, PropertyPanelControler,
																							ActionListener, Runnable {



	// **************
	// *** Fields ***
	// **************



	private static Logger logger = Logger.getLogger("de.deepamehta");

	/**
	 * The application service.
	 */
	ApplicationService as;

	// --- Type Model ---

	// The collection of types currently known by the presentation service.
	// Types are added on-demand by using the application service (type service).
	// These 2 fields are redundant and are always updated together.

	private Vector globalTopicTypesV = new Vector();
	private Hashtable globalTopicTypes = new Hashtable();

	private Vector globalAssocTypesV = new Vector();
	private Hashtable globalAssocTypes = new Hashtable();

	// --- File Service ---

	// The fileservice request queue. Element type is QueuedRequest.
	// Note: the constructor creates the thread responsible for processing the queued requests
	private Vector requestQueue = new Vector();

	private DefaultBoundedRangeModel progressModel = new DefaultBoundedRangeModel();
	private FileServer fileServer;

	// ---

	/**
	 * Initialized by {@link InteractionConnection#InteractionConnection}.<br>
	 * Initialized by {@link de.deepamehta.service.DeepaMehta#initApplication}.<br>
	 * Initialized by {@link de.deepamehta.service.DeepaMehta#init}.
	 */
	public Hashtable installationProps;

	boolean runsAsApplet, runsAsDemo;
	Applet applet; String demoMapID;

	public String hostAddress;	// used only for reporting
	public String platform;

	/**
	 * Reflects weather the user is logged in.
	 * <p>
	 * ### Initialized with <code>false</code>, set to <code>true</code> by the login thread ({@link #run}).
	 */
	boolean loggedIn;

	/**
	 * All currently presented topimap editors (workspaces as well as view).
	 * <p>
	 * ### The topicmap geometry of all editors in this vector is send to the server
	 * when the user logs out. Also the vector is needed when the client reveives an
	 * {@link de.deepamehta.DeepaMehtaConstants#DIRECTIVE_CLOSE_EDITOR}.
	 * <p>
	 * ### The vectors element type is {@link TopicmapEditorModel}.
	 * <p>
	 * Key: topicmap ID (String)
	 * Value: TopicMapEditorModel
	 * <p>
	 * Accessed by {@link #getEditor(String topicmapID)}<br>
	 * Added by {@link #createTopicMapEditor}<br>
	 * Removed by {@link #removeEditor}
	 */
	private Hashtable editors = new Hashtable();

	/**
	 * ID of currently selected topicmap.
	 * <p>
	 * Updated in {@link #selectTopicmap} (private)
	 */
	private String selectedTopicmapID;

	// --- History ---

	// updated with
	//	- addToHistory(String topicmapID)
	//	- removeFromHistory(String topicmapID)
	private Vector topicmapHistory = new Vector();

	// updated in
	//	- addToHistory(String topicmapID)
	//	- back()
	//	- forward()
	private int historyIndex;

	// ---

	/**
	 * The number of displayed group workspaces (topicmap editors with {@link #EDITOR_CONTEXT_WORKGROUP}).
	 * <p>
	 * Incremented in {@link #showWorkspace}.<br>
	 * Decremented in {@link #removeEditor}.
	 */
	private int workgroupCount;

	Font[][] font = new Font[FONT_COUNT][2];
	private int textsize = TEXTSIZE_BIG;	// ### or TEXTSIZE_SMALL
	//
	private String currentPath;
	private boolean firstPaint = true;

	// --- GUI ---

	/**
	 * The main window.
	 * <p>
	 * Note: there is no other window. The main window is also used for the login dialog when
	 * running as (monolithic) application
	 * <p>
	 * Initialized by {@link #createMainWindow}.
	 */
	public JFrame mainWindow;

	/**
	 * Content pane of main window.
	 * <p>
	 * Initialized by {@link #createMainWindow}.<br>
	 * Accessed by {@link #createMainGUI(Directives directives)}, {@link #createMainGUI()}.
	 */
	public Container cp;

	private JTextField usernameField;
	private JPasswordField passwordField;
	private JLabel statusLabel;
	private JButton startDemoButton;

			GraphPanel graphPanel;		// ### accessed from MessagePanel
	private PropertyPanel propertyPanel;
	private MessagePanel messagePanel;
	private JProgressBar progressBar;
	private JPanel cardPanel;
	private CardLayout cardLayout;

	private JComboBox topicmapChoice;
	private JButton backButton;
	private JButton forwardButton;
	private JButton messagePanelBackButton;



	// *******************
	// *** Constructor ***
	// *******************



	/**
	 * @see		de.deepamehta.service.DeepeMehta#initApplication
	 * @see		de.deepamehta.service.DeepeMehta#init
	 * @see		DeepeMehtaClient#initApplication
	 * @see		DeepeMehtaClient#init
	 */
	public PresentationService() {
		// --- init "hostAddress" and "platform" ---
		// >>> compare to ApplicationService constructor
		try {
			this.hostAddress = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			logger.warning("can't get localhost's address (" + e + ")");
		}
		this.platform = System.getProperty("os.name");
		// --- File Service ---
		String baseDir = null;
		try {
			baseDir = System.getProperty("user.home") + "/" + FILE_REPOSITORY_PATH;
			logger.info("file repository path: " + baseDir);
		} catch (Throwable e) {
			logger.warning("can't get user's home directory (" + e + ") -- disable client-side file repository");
		}
		fileServer = new FileServer(baseDir, progressModel);
		//
		// create the thread responsible for processing the fileservice request queue
		new Thread(this).start();
	}



	// ***************
	// *** Methods ***
	// ***************



	/**
	 * Performs initialization that is common to both, applet and application
	 *
	 * @see		de.deepamehta.service.DeepeMehta#initApplication
	 * @see		de.deepamehta.service.DeepeMehta#init
	 * @see		DeepeMehtaClient#initApplication
	 * @see		DeepeMehtaClient#init
	 */
	public void initialize() {
		// reporting
		if (LOG_MEM_STAT) {
			DeepaMehtaUtils.memoryStatus();
		}
		// --- set GUI defaults ---
		UIDefaults defaults = UIManager.getDefaults();
		// reporting
		/* ### System.out.println("> User Interface Defaults:");
		Enumeration e = defaults.keys();
		while (e.hasMoreElements()) {
			Object key = e.nextElement();
			System.out.println(">    \"" + key + "\" (" + key.getClass() + ")");
		} */
		// fonts
		String fontname = ((Font) defaults.get("Label.font")).getName();
		for (int i = 0; i < FONT_COUNT; i++) {
			this.font[i][Font.PLAIN] = new Font(fontname, Font.PLAIN, FONT_SIZES[i]);
			this.font[i][Font.BOLD] = new Font(fontname, Font.BOLD, FONT_SIZES[i]);
		}
		// set UIManager default properties
		/* ### defaults.put("TabbedPane.font", new FontUIResource(font[textsize + 1][Font.BOLD]));
        if (!platform.equals(PLATFORM_MACOSX)) {
            defaults.put("TabbedPane.selected", new ColorUIResource(COLOR_SELECTION));
            defaults.put("TabbedPane.focus", new ColorUIResource(COLOR_DARK_SHADOW));
            defaults.put("TabbedPane.highlight", new ColorUIResource(Color.white));
            defaults.put("TabbedPane.darkShadow", new ColorUIResource(COLOR_DARK_SHADOW));
        } */
		defaults.put("Label.font", new FontUIResource(font[textsize][Font.PLAIN]));
		defaults.put("Label.foreground", new ColorUIResource(COLOR_DARK_SHADOW));
		defaults.put("Panel.background", new ColorUIResource(DEFAULT_BGCOLOR));
		defaults.put("Button.font", new FontUIResource(font[textsize + 1][Font.PLAIN]));
		defaults.put("Button.background", new ColorUIResource(COLOR_PROPERTY_PANEL));
		defaults.put("Button.select", new ColorUIResource(COLOR_SELECTION));
		defaults.put("Button.focus", new ColorUIResource(COLOR_DARK_SHADOW));
		defaults.put("RadioButton.font", new FontUIResource(font[textsize + 1][Font.PLAIN]));
		defaults.put("RadioButton.background", new ColorUIResource(COLOR_PROPERTY_PANEL));
		defaults.put("RadioButton.select", new ColorUIResource(COLOR_SELECTION));
		defaults.put("RadioButton.focus", new ColorUIResource(COLOR_DARK_SHADOW));
		defaults.put("ComboBox.font", new FontUIResource(font[textsize + 1][Font.BOLD]));
		defaults.put("ComboBox.background", new ColorUIResource(DEFAULT_BGCOLOR));
		defaults.put("ComboBox.selectionBackground", new ColorUIResource(COLOR_SELECTION));
		// ### Note: there is no ComboBox.focus property
		// ### defaults.put("ComboBox.focus", new ColorUIResource(COLOR_DARK_SHADOW));
		defaults.put("Menu.font", new FontUIResource(font[textsize + 1][Font.BOLD]));
		defaults.put("Menu.background", new ColorUIResource(DEFAULT_BGCOLOR));
		defaults.put("Menu.selectionBackground", new ColorUIResource(COLOR_SELECTION));
		defaults.put("MenuItem.font", new FontUIResource(font[textsize + 1][Font.BOLD]));
		defaults.put("MenuItem.background", new ColorUIResource(DEFAULT_BGCOLOR));
		defaults.put("MenuItem.selectionBackground", new ColorUIResource(COLOR_SELECTION));
		defaults.put("MenuItem.disabledForeground", new ColorUIResource(COLOR_DARK_SHADOW));
		defaults.put("RadioButtonMenuItem.font", new FontUIResource(font[textsize + 1][Font.BOLD]));
		defaults.put("RadioButtonMenuItem.background", new ColorUIResource(DEFAULT_BGCOLOR));
		defaults.put("RadioButtonMenuItem.selectionBackground", new ColorUIResource(COLOR_SELECTION));
		defaults.put("TextField.font", new FontUIResource(font[textsize + 1][Font.PLAIN]));
		defaults.put("TextField.background", new ColorUIResource(COLOR_VIEW_BGCOLOR));
		defaults.put("TextField.selectionBackground", new ColorUIResource(COLOR_SELECTION));
		defaults.put("TextField.inactiveForeground", new ColorUIResource(COLOR_DARK_SHADOW));
		defaults.put("TextArea.font", new FontUIResource(font[textsize + 1][Font.PLAIN]));
		defaults.put("TextArea.background", new ColorUIResource(COLOR_VIEW_BGCOLOR));
		defaults.put("TextArea.selectionBackground", new ColorUIResource(COLOR_SELECTION));
		defaults.put("TextArea.inactiveForeground", new ColorUIResource(COLOR_DARK_SHADOW));
		defaults.put("TextPane.font", new FontUIResource(font[textsize + 1][Font.PLAIN]));		// ### no effect!
		defaults.put("TextPane.background", new ColorUIResource(COLOR_VIEW_BGCOLOR));
		defaults.put("TextPane.selectionBackground", new ColorUIResource(COLOR_SELECTION));
		defaults.put("TextPane.inactiveForeground", new ColorUIResource(COLOR_DARK_SHADOW));	// ### any effect?
		defaults.put("PasswordField.font", new FontUIResource(font[textsize + 1][Font.PLAIN]));
		defaults.put("PasswordField.background", new ColorUIResource(COLOR_VIEW_BGCOLOR));
		defaults.put("PasswordField.selectionBackground", new ColorUIResource(COLOR_SELECTION));
		defaults.put("Separator.foreground", new ColorUIResource(COLOR_DARK_SHADOW));
		defaults.put("TitledBorder.font", new FontUIResource(font[textsize][Font.PLAIN]));		// ### still in use?
		defaults.put("TitledBorder.titleColor", new ColorUIResource(COLOR_DARK_SHADOW));		// ### still in use?
		// --- create login GUI components ---
		usernameField = new JTextField(12);
		usernameField.addActionListener(this);
		usernameField.setActionCommand("password");
		passwordField = new JPasswordField(12);
		passwordField.addActionListener(this);
		passwordField.setActionCommand("login");
		statusLabel = new JLabel(" ");
		statusLabel.setForeground(Color.black);
		//
		DeepaMehtaUtils.reportVMProperties();
	}

	// ---

	/**
	 * @param	demoMapID	may be <code>null</code>
	 *
	 * @see		DeepeMehtaClient#init				(running as applet)
	 */
	public void setDemoMap(String demoMapID) {
		if (demoMapID != null) {
			this.runsAsDemo = true;
			this.demoMapID = demoMapID;
		}
	}

	/**
	 * References checked: 11.12.2006 (2.0b8)
	 *
	 * @param	applet		may be <code>null</code>
	 *
	 * @see		DeepeMehtaClient#init				(running as applet)
	 * @see		de.deepamehta.service.DeepeMehta#init
	 */
	public void setApplet(Applet applet) {
		if (applet != null) {
			this.runsAsApplet = true;
			this.applet = applet;
		}
	}

	/**
	 * @param	as		the application service (not <code>null</code>)
	 *
	 * @see		DeepeMehtaClient#initApplication	(running as application)
	 * @see		DeepeMehtaClient#init				(running as applet)
	 */
	public void setService(ApplicationService as) {
		this.as = as;
	}

	// ---

	// ### copy in ApplicationService
	public int getLanguage() {
		String lang = (String) installationProps.get(PROPERTY_LANGUAGE);
		return lang == null || lang.equals("English") ? LANGUAGE_ENGLISH : LANGUAGE_GERMAN;	// ###
	}

	/**
	 * @see		#run								(running as applet)
	 * @see		DeepeMehtaClient#initApplication	(running as application)
	 * @see		de.deepamehta.service.DeepeMehta#initApplication			(running as application)
	 */
	public String getClientName() {
		return (String) installationProps.get(PROPERTY_CLIENT_NAME);
	}

	/**
	 * @see		#run								(running as applet)
	 * @see		DeepeMehtaClient#initApplication	(running as application)
	 * @see		de.deepamehta.service.DeepeMehta#initApplication			(running as application)
	 */
	public void createMainWindow(String title) {
		if (runsAsApplet) {
			mainWindow = new JFrame(title);
		} else {
			mainWindow = new JFrame(title) {
				public void paint(Graphics g) {
					try {
						super.paint(g);
						if (firstPaint) {
							focusUsername();
							firstPaint = false;
						}
					} catch (Throwable t) {
						// ### happens?
						System.out.println("*** mainWindow paint(): " + t);
					}
				}
			};
			mainWindow.setSize(WIDTH_LOGIN, HEIGHT_LOGIN);
		}
		// --- add listener ---
		mainWindow.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				close();
			}
		});
		// --- initialize cp ---
		cp = mainWindow.getContentPane();
	}

	void focusUsername() {
		usernameField.requestFocus();
	}

	/**
	 * References checked: 6.9.2008 (2.0b8)
	 *
	 * @see		#showNodeMenu
	 * @see		#showEdgeMenu
	 * @see		#showGraphMenu
	 * @see		#processNodeCommand
	 * @see		#processEdgeCommand
	 * @see		#processGraphCommand
	 *
	 * @see		#executeTopicCommand
	 * @see		#executeAssocCommand
	 *
	 * @see		#selectTopicmap
	 * @see		#close
	 */
	private void storeProperties() {
		// Note: propertyPanel is null if not logged in
		if (propertyPanel == null) {
			return;
		}
		//
		propertyPanel.storeProperties();
	}

	// ---

	FileServer getFileServer() {
		return fileServer;
	}

	/**
	 * Processes <code>DIRECTIVE_SET_LAST_MODIFIED</code>.
	 *
	 * @see		#processDirectives
	 * @see		SocketService#downloadFile
	 */
	void setLastModifiedLocally(String filename, int filetype, long lastModified) {
		System.out.println(">>> PresentationService.setLastModifiedLocally(): " +
			"filename=\"" + filename + "\" lastModified=" + lastModified + " type=" +
			filetype);
		//
		File file = new File(FileServer.repositoryPath(filetype) + filename);
		file.setLastModified(lastModified);
	}



	// ****************************************************************************
	// *** Implementation of interface de.deepamehta.client.GraphPanelControler ***
	// ****************************************************************************



	// ---------------------------
	// --- Providing the model ---
	// ---------------------------



	/* ### public Hashtable getNodes() {
		return getTopicmap().getTopics();
	}

	public Hashtable getEdges() {
		return getTopicmap().getAssociations();
	} */

	// ---

	// Note: this methods are already provided by the PropertyPanelControler implemention
	//
	// Hashtable getTopicTypes()
	// Hashtable getAssociationTypes()
	// Vector getTopicTypesV()
	// Vector getAssociationTypesV()

	// ---

	/**
	 * Checks if the specified topic type is known in the topicmap this controller
	 * controls.
	 * <p>
	 * ### If the topic type is not known it is retrieved from the list of corporate wide
	 * topic types and is added to the editor who deploys this controller and the server
	 * is notified by means of a (<code>addTypeToView</code>) message.
	 * <p>
	 * References checked: 2.12.2001 (2.0a14-pre1)
	 *
	 * @return	### <code>true</code> if the specified topic type is known in the topicmap
	 *			this controller controls, <code>false</code> if the type was unknown (and
	 *			thus is retrieved now)
	 *
	 * @see		#showTopic
	 * @see		#updateTopicType
	 * @see		#changeTopicType
	 */
	public boolean checkTopicType(String typeID) throws DeepaMehtaException {
		// --- check if topic type is known ---
		PresentationType type = getTopicType(typeID);
		if (type != null) {
			return true;
		}
		// --- retrieve it through application service ---
		type = as.getTopicType(typeID);		// throws DME
		addTopicType(type);
		//
		return false;
	}

	/**
	 * Checks if the specified association type is known in the topicmap this controller
	 * controls.
	 * <p>
	 * ### If the association type is not known it is retrieved from the list of corporate
	 * wide association types and is added to the editor who deploys this controller and
	 * the server is notified by means of a (<code>addTypeToView</code>) message.
	 * <p>
	 * References checked: 2.12.2001 (2.0a14-pre1)
	 *
	 * @return	### <code>true</code> if the specified association type is known in the
	 *			topicmap this controller controls, <code>false</code> if the type was
	 *			unknown (and thus is retrieved now)
	 *
	 * @see		PresentationService#showTopic
	 * @see		PresentationService#showAssociation
	 * @see		PresentationService#updateAssociationType
	 * @see		PresentationService#changeAssociationType
	 */
	public boolean checkAssociationType(String typeID) throws DeepaMehtaException {
		// --- check if association type is known ---
		PresentationType type = getAssociationType(typeID);
		if (type != null) {
			return true;
		}
		// --- retrieve it through type connection ---
		type = as.getAssociationType(typeID);
		addAssociationType(type);
		//
		return false;
	}

	// ---

	/**
	 * @see		GraphPanel#GraphPanel
	 */
	private boolean creatingEdgesEnabled(PresentationTopicMap topicmap) {
		return topicmap.getEditorContext() == EDITOR_CONTEXT_VIEW;
	}

	// ---

	// Note: getImage() is found below

	// ---

	private Font getFont() {
		return font[textsize + 1][Font.PLAIN];
	}

	public Image getBackgroundImage() {
		return getTopicmap().getBackgroundImage();
	}

	public Color getBackgroundColor() {
		return getTopicmap().getBackgroundColor();
	}

	public Point getTranslation() {
		return getTopicmap().getTranslation();
	}

	// --- corporate images ---

	private Image corporateImage() {
		String iconfile = (String) installationProps.get(PROPERTY_CORPORATE_ICON);
		return getImage(FILESERVER_IMAGES_PATH + iconfile);
	}

	/**
	 * May return <code>null</code>.
	 */
	private Image customerImage() {
		// Note: the custumer icon may be not set
		String iconfile = (String) installationProps.get(PROPERTY_CUSTOMER_ICON);
		return iconfile == null || iconfile.equals("") ?
			null : getImage(FILESERVER_IMAGES_PATH + iconfile);
	}

	// --- base URL ---

	public String getBaseURL() {
		return (String) installationProps.get(PROPERTY_CW_BASE_URL);
	}

	// --- border images ---

	private Image topImage() {
		return getImage(FILESERVER_IMAGES_PATH + BORDER_IMAGES[0]);
	}

	private Image bottomImage() {
		return getImage(FILESERVER_IMAGES_PATH + BORDER_IMAGES[1]);
	}

	private Image leftImage() {
		return getImage(FILESERVER_IMAGES_PATH + BORDER_IMAGES[2]);
	}

	private Image rightImage() {
		return getImage(FILESERVER_IMAGES_PATH + BORDER_IMAGES[3]);
	}

	// --- text editor images ---

	public Icon boldIcon() {
		return getIcon(FILESERVER_IMAGES_PATH, TEXT_EDITOR_IMAGES[0]);
	}

	public Icon italicIcon() {
		return getIcon(FILESERVER_IMAGES_PATH, TEXT_EDITOR_IMAGES[1]);
	}

	public Icon underlineIcon() {
		return getIcon(FILESERVER_IMAGES_PATH, TEXT_EDITOR_IMAGES[2]);
	}



	// -----------------
	// --- Callbacks ---
	// -----------------



	/**
	 * @see		GraphPanel#nodeClicked
	 */
	public void nodeSelected(PresentationTopicMap topicmap, GraphNode node) {
		processNodeCommand(topicmap, node, CMD_SELECT_TOPIC);
	}

	/**
	 * @see		GraphPanel#edgeClicked
	 */
	public void edgeSelected(PresentationTopicMap topicmap, GraphEdge edge) {
		processEdgeCommand(topicmap, edge, CMD_SELECT_ASSOC);
	}

	/**
	 * References checked: 29.3.2003 (2.0a18-pre8)
	 *
	 * @see		GraphPanel#graphClicked
	 */
	public void graphSelected(PresentationTopicMap topicmap) {
		processGraphCommand(topicmap, CMD_SELECT_TOPICMAP);
	}

	// ---
	
	public void nodeDoubleClicked(PresentationTopicMap topicmap, GraphNode node) {
		processNodeCommand(topicmap, node, CMD_DEFAULT);
	}

	public void edgeDoubleClicked(PresentationTopicMap topicmap, GraphEdge edge) {
		processEdgeCommand(topicmap, edge, CMD_DEFAULT);
	}

	// ---

	/**
	 * @see		GraphPanel#nodeClicked
	 */
	public void showNodeMenu(PresentationTopicMap topicmap, GraphNode node, int x, int y) {
		storeProperties();
		BaseTopic topic = (BaseTopic) node;
		String command = CMD_GET_TOPIC_COMMANDS + COMMAND_SEPARATOR + topic.getID() + COMMAND_SEPARATOR + topic.getVersion() +
			COMMAND_SEPARATOR + x + COMMAND_SEPARATOR + y;
		processTopicCommand(topic.getID(), topic.getVersion(), command, topicmap.getID());
	}

	/**
	 * @see		GraphPanel#edgeClicked
	 */
	public void showEdgeMenu(PresentationTopicMap topicmap, GraphEdge edge, int x, int y) {
		storeProperties();
		BaseAssociation assoc = (BaseAssociation) edge;
		String command = CMD_GET_ASSOC_COMMANDS + COMMAND_SEPARATOR + assoc.getID() + COMMAND_SEPARATOR + assoc.getVersion() +
			COMMAND_SEPARATOR + x + COMMAND_SEPARATOR + y;
		processAssociationCommand(assoc.getID(), assoc.getVersion(), command, topicmap.getID());
	}

	/**
	 * @see		GraphPanel#edgeClicked
	 */
	public void showGraphMenu(PresentationTopicMap topicmap, int x, int y) {
		storeProperties();
		String command = CMD_GET_VIEW_COMMANDS + COMMAND_SEPARATOR + x + COMMAND_SEPARATOR + y;
		processTopicCommand(topicmap.getID(), 1, command, topicmap.getID());
	}

	// ---

	/**
	 * @see		#nodeSelected
	 * @see		#nodeDoubleClicked
	 * @see		GraphPanel#actionPerformed
	 */
	public void processNodeCommand(PresentationTopicMap topicmap, GraphNode node, String command) {
		storeProperties();
		BaseTopic topic = (BaseTopic) node;
		processTopicCommand(topic.getID(), topic.getVersion(), command, topicmap.getID());
	}

	/**
	 * @see		#edgeSelected
	 * @see		#edgeDoubleClicked
	 * @see		GraphPanel#actionPerformed
	 */
	public void processEdgeCommand(PresentationTopicMap topicmap, GraphEdge edge, String command) {
		storeProperties();
		BaseAssociation assoc = (BaseAssociation) edge;
		processAssociationCommand(assoc.getID(), assoc.getVersion(), command, topicmap.getID());
	}

	/**
	 * References checked: 29.3.2003 (2.0a18-pre8)
	 *
	 * @see		#graphSelected
	 * @see		GraphPanel#actionPerformed
	 * @see		GraphPanel#thisPanelReleased
	 */
	public void processGraphCommand(PresentationTopicMap topicmap, String command) {
		storeProperties();
		processTopicCommand(topicmap.getID(), 1, command, topicmap.getID());	// ### version 1
	}

	// ---

	/**
	 * References checked: 3.2.2008 (2.0b8)
	 *
	 * @see		TopicmapEditorModel#updateDetail
	 */
	public void processNodeDetail(PresentationTopicMap topicmap, String topicID, Detail detail) {
		processTopicDetail(topicID, 1, detail, topicmap.getID());	// ### version 1
	}

	/**
	 * References checked: 3.2.2008 (2.0b8)
	 *
	 * @see		TopicmapEditorModel#updateDetail
	 */
	public void processEdgeDetail(PresentationTopicMap topicmap, String assocID, Detail detail) {
		processAssociationDetail(assocID, 1, detail, topicmap.getID());	// ### version 1
	}

	// ---

	/**
	 * Notifies the application service about changed topic geometry.
	 * <p>
	 * Called in 3 situations
	 * <ul>
	 * <li>A single topic has been moved by the user
	 * <li>A cluster of topics has been moved by the user
	 * <li>A topic about to be shown gots its geometry at client side programatically
	 * </ul>
	 * References checked: 5.8.2002 (2.0a15-pre11)
	 *
	 * @see		#updateGeometry
	 * @see		GraphPanel#thisPanelReleased
	 */
	public void nodesMoved(PresentationTopicMap topicmap, Hashtable nodes) {
		Directives directives = as.setGeometry(topicmap.getID(), VIEWMODE_USE, nodes);
		processDirectives(directives, topicmap);
	}

	/**
	 * Notifies the application service about changed topicmap translation.
	 * <p>
	 * Called in 1 situation
	 * <ul>
	 * <li>A topicmap has been moved by the user
	 * </ul>
	 * References checked: 30.1.2002 (2.0a14-pre7)
	 *
	 * @see		GraphPanel#thisPanelReleased
	 */
	public void graphMoved(PresentationTopicMap topicmap, int tx, int ty) {
		as.setTranslation(topicmap.getID(), VIEWMODE_USE, tx, ty);
		// there is no reply
	}

	public void repaint() {
		cardPanel.repaint();
	}

	public void updateBounds(PresentationTopicMap topicmap) {
		getEditor(topicmap.getID()).setBounds();
	}

	// ###
	public void detailWindowClosed(String key) {
		// update model
		getEditor().removeNodeDetail(key);
		// update view
		repaint();	// update guides
	}

	// ---

	public void beginTranslation() {
		setHandCursor();
	}

	public void beginCreatingEdge() {
		setCrosshairCursor();
	}



	// *******************************************************************************
	// *** Implementation of interface de.deepamehta.client.PropertyPanelControler ***
	// *******************************************************************************



	// ---------------------------
	// --- Providing the model ---
	// ---------------------------



	public Hashtable getTopicTypes() {
		return globalTopicTypes;
	}

	public Hashtable getAssociationTypes() {
		return globalAssocTypes;
	}

	public Vector getTopicTypesV() {
		return globalTopicTypesV;
	}

	public Vector getAssociationTypesV() {
		return globalAssocTypesV;
	}

	// ---

	public ImageIcon getIcon(String iconfile) {
		return getIcon(FILESERVER_ICONS_PATH, iconfile);
	}

	// ### copy in ApplicationService
	public String string(int item) {
		return strings[item][getLanguage()];
	}



	// -----------------
	// --- Callbacks ---
	// -----------------



	/**
	 * @see		PropertyPanel#storeProperties
	 */
	public void changeTopicData(PresentationTopicMap topicmap, BaseTopic topic, Hashtable newData) {
		System.out.println(">>> save topic properties of " + topic);
		Directives directives = as.setTopicProperties(topicmap.getID(), VIEWMODE_USE, topic.getID(), topic.getVersion(), newData);
		processDirectives(directives, topicmap);
	}

	/**
	 * @see		PropertyPanel#storeProperties
	 */
	public void changeAssocData(PresentationTopicMap topicmap, BaseAssociation assoc, Hashtable newData) {
		System.out.println(">>> save association properties of " + assoc);
		Directives directives = as.setAssocProperties(topicmap.getID(), VIEWMODE_USE, assoc.getID(), assoc.getVersion(), newData);
		processDirectives(directives, topicmap);
	}

	// ---

	/**
	 * @see		PropertyPanel#actionPerformed
	 * @see		PropertyPanel#itemStateChanged
	 */
	public void executeTopicCommand(PresentationTopicMap topicmap, BaseTopic topic, String command) {
		storeProperties();
		processTopicCommand(topic.getID(), topic.getVersion(), command, topicmap.getID());
	}

	/**
	 * @see		PropertyPanel#actionPerformed
	 * @see		PropertyPanel#itemStateChanged
	 */
	public void executeAssocCommand(PresentationTopicMap topicmap, BaseAssociation assoc, String command) {
		storeProperties();
		processAssociationCommand(assoc.getID(), assoc.getVersion(), command, topicmap.getID());
	}

	// ---

	/**
	 * References checked: 13.8.2001 (2.0a11)
	 *
	 * @see		GraphPanel#actionPerformed
	 * @see		GraphPanel#nodeClicked
	 * @see		GraphPanel#edgeClicked
	 * @see		GraphPanel#backgroundClicked
	 * @see		PropertyPanel#itemStateChanged
	 */
	public void beginLongTask() {
		setWaitCursor();
	}

	public void endTask() {
		setDefaultCursor();
	}



	// *****************************************************************
	// *** Implementation of interface java.awt.event.ActionListener ***
	// *****************************************************************



	/**
	 * Processes the action events triggered by the login screen.
	 * <p>
	 * Once the enter key is pressed inside the password field a server side password
	 * check is performed. If the password check is successfull the login procedure is
	 * run in its own thread.
	 */
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("password")) {
			statusLabel.setText("");
			passwordField.requestFocus();
		} else if (command.equals("login")) {
			statusLabel.setText("");
			// --- try login ---
			String username = usernameField.getText();
			String password = new String(passwordField.getPassword());
			Directives directives = as.login(username, password);
			if (directives != null) {
				usernameField.setEnabled(false);
				passwordField.setEnabled(false);
				statusLabel.setText("Login OK");
				startSession(username, directives);
				// Note: once logged in username and password are forgotton
			} else {
				statusLabel.setText("Login failed");
				System.out.println(">>> login failed");
			}
		} else if (command.equals("startdemo")) {
			// --- start demo ---
			startDemoButton.setEnabled(false);
			Directives directives = as.startDemo(demoMapID);
			startDemo(demoMapID, directives);
		} else if (command.equals("selectTopicmap")) {
			ComboBoxItem item = (ComboBoxItem) topicmapChoice.getSelectedItem();
			// ### error check
			if (item == null) {
				System.out.println("*** PresentationService.performAction(): action \"selectTopicmap\" " +
					"can't perform (topicmap choice has no selection)");
				return;
			}
			//
			String topicmapID = item.topicID;
			addToHistory(topicmapID);
			//
			beginLongTask();	// ### no effect
			selectTopicmap(topicmapID);
			endTask();
		} else if (command.equals("back")) {
			selectTopicmap(back());
		} else if (command.equals("forward")) {
			selectTopicmap(forward());
		} else if (command.equals("backToGraphPanel")) {
			showGraphPanel();
		} else {
			throw new DeepaMehtaException("unexpected action command: \"" + command + "\"");
		}
	}

	// ---

	void notificationPanelClicked() {
		showMessagePanel();
		messagePanel.stopNotification();
	}

	// ---

	private void showGraphPanel() {
		// ### System.out.println(">>> show graphPanel");
		cardLayout.show(cardPanel, "graphPanel");
	}

	private void showMessagePanel() {
		// ### System.out.println(">>> show messagePanel");
		cardLayout.show(cardPanel, "messagePanel");
	}



	// ******************************************************
	// *** Implementation of Interface java.lang.Runnable ***
	// ******************************************************



	public void run() {
		while (true) {
			if (requestQueue.size() == 0) {
				// no further request in queue -- fileserver thread goes sleeping now
				waitForRequest();
			} else {
				QueuedRequest request = (QueuedRequest) requestQueue.firstElement();
				performRequest(request);
			}
		}
	}



	// ***************
	// *** Methods ***
	// ***************



	/**
	 * @see		#actionPerformed
	 */
	private void startSession(String username, Directives directives) {
		System.out.println(">>> user \"" + username + "\" logged in successfully");
		//
		this.loggedIn = true;
		new GUIBuilder(directives);
	}

	/**
	 * @see		#actionPerformed
	 */
	private void startDemo(String demoMapID, Directives directives) {
		System.out.println(">>> starting demo with topicmap \"" + demoMapID + "\"");
		//
		// ### this.loggedIn = true;	// required?
		new GUIBuilder(directives);
	}

	// ---

	/**
	 * Delegates the execution of a topic command to the application service and processes the resulting directives.
	 * If the directives contains a chained directive another execution-processing-cycle is performed.
	 *
	 * References checked: 3.2.2008 (2.0b8)
	 *
	 * @see		#showNodeMenu
	 * @see		#showGraphMenu
	 * @see		#processNodeCommand
	 * @see		#processGraphCommand
	 * @see		#executeTopicCommand
	 */
	private void processTopicCommand(String topicID, int version, String command, String topicmapID) {
		// compare to de.deepamehta.service.InteractionConnection.performProcessTopicCommand()
		Directives directives = as.executeTopicCommand(topicmapID, VIEWMODE_USE, topicID, version, command);
		String result = processDirectives(directives, topicmapID, VIEWMODE_USE);
		//
		if (directives.isChained()) {
			directives = as.executeChainedTopicCommand(topicmapID, VIEWMODE_USE, topicID, version, command, result);
			processDirectives(directives, topicmapID, VIEWMODE_USE);
		}
	}

	/**
	 * Delegates the execution of an association command to the application service and processes the resulting directives.
	 * If the directives contains a chained directive another execution-processing-cycle is performed.
	 *
	 * References checked: 3.2.2008 (2.0b8)
	 *
	 * @see		#showEdgeMenu
	 * @see		#processEdgeCommand
	 * @see		#executeAssocCommand
	 */
	private void processAssociationCommand(String assocID, int version, String command, String topicmapID) {
		Directives directives = as.executeAssocCommand(topicmapID, VIEWMODE_USE, assocID, version, command);
		String result = processDirectives(directives, topicmapID, VIEWMODE_USE);
		//
		if (directives.isChained()) {
			directives = as.executeChainedAssocCommand(topicmapID, VIEWMODE_USE, assocID, version, command, result);
			processDirectives(directives, topicmapID, VIEWMODE_USE);
		}
	}

	// ---

	/**
	 * @see		#processNodeDetail
	 */
	private void processTopicDetail(String topicID, int version, Detail detail, String topicmapID) {
		Directives directives = as.processTopicDetail(topicmapID, VIEWMODE_USE, topicID, version, detail);
		processDirectives(directives, topicmapID, VIEWMODE_USE);
	}

	/**
	 * @see		PresentationService#processNodeDetail
	 */
	private void processAssociationDetail(String assocID, int version, Detail detail, String topicmapID) {
		Directives directives = as.processAssocDetail(topicmapID, VIEWMODE_USE, assocID, version, detail);
		processDirectives(directives, topicmapID, VIEWMODE_USE);
	}

	// --- processDirectives (3 forms) ---

	/**
	 * References checked: 3.2.2008 (2.0b8)
	 *
	 * @see		#performRequest
	 * @see		#createMainGUI
	 * @see		MessagingConnection#processMessage
	 * @see		de.deepamehta.service.DeepaMehta#sendDirectives
	 */
	public String processDirectives(Directives directives) {
		return processDirectives(directives, null);
	}

	/**
	 * References checked: 3.2.2008 (2.0b8)
	 *
	 * @see		#processTopicCommand
	 * @see		#processAssociationCommand
	 * @see		#processTopicDetail
	 * @see		#processAssociationDetail
	 */
	String processDirectives(Directives directives, String topicmapID, String viewmode) {
		// ### topicmapID, viewmode should be known also for asynchronously send directives
		PresentationTopicMap topicmap = getEditor(topicmapID).getTopicMap();
		return processDirectives(directives, topicmap);
	}

	/**
	 * References checked: 3.2.2008 (2.0b8)
	 *
	 * @param	topicmap	The topicmap in which the action was triggered resp.
	 *						<code>null</code> if the directives has been received
	 *						asynchronously.
	 *
	 * @return	processing result, used for chained directives:<br>
	 *			DIRECTIVE_CHOOSE_FILE: result is the path of the choosen file, resp. "" if file
	 *				choosing has been aborted by the user
	 *			DIRECTIVE_CHOOSE_COLOR: result is the chosen color representation, resp. "" if color
	 *				choosing has been aborted by the user
	 *
	 * @see		#nodesMoved
	 * @see		#changeTopicData
	 * @see		#changeAssocData
	 */
	String processDirectives(Directives directives, PresentationTopicMap topicmap) {
		String result = null;	// processing result, used for chained directives (DIRECTIVE_CHOOSE_FILE, DIRECTIVE_CHOOSE_COLOR)
		String viewMode = null;
		//
		// Used to collect topics which have got their geometry at client side
		// Key: String of format "topicmapID:viewmode:topicID"
		// Value: topic (PresentationTopic)
		Hashtable updateGeometry = new Hashtable();
		//
		Enumeration dirs = directives.getDirectives();
		int dirCount = directives.getDirectiveCount();
		//
		String topicmapID = null;
		if (topicmap != null) {
			topicmapID = topicmap.getID();
			viewMode = topicmap.getViewmode();
		}
		// helper variables for directive parameters
		String mapID, topicID, typeID, viewmode;
		//
		StringBuffer log = new StringBuffer();	// diagnosis log
		// loop through all directives
		Directive directive;
		int dirType;
		Object param1, param2, param3, param4, param5;
		while (dirs.hasMoreElements()) {
			directive = (Directive) dirs.nextElement();
			dirType = directive.type;
			param1 = directive.param1;
			param2 = directive.param2;
			param3 = directive.param3;
			param4 = directive.param4;
			param5 = directive.param5;
			// log
			log.append(' ');
			log.append(dirType);
			//
			try {
				switch (dirType) {
				case DIRECTIVE_SHOW_TOPIC:
					PresentationTopic topic = (PresentationTopic) param1;
					mapID = (String) param2;
					// Note: if the server did not set the topicmap ID or viewmode ID, the client uses the
					// topicmap resp. viewmode in which the action was triggered
					if (mapID.equals("")) {
						// error check
						if (topicmap == null) {
							String hint = ">>> Hint to application programmer: to use DIRECTIVE_SHOW_TOPIC " +
								"asynchronously you must set \"topicmap ID\" and \"viewmode\"";
							showMessage(hint, NOTIFICATION_ERROR);
							System.out.println("*** PresentationService.processDirectives(): " + hint);
							break;
						}
						//
						mapID = topicmapID;
					}
					showTopic(topic, mapID, updateGeometry);
					getEditor(mapID).setBounds();
					break;
				case DIRECTIVE_SHOW_TOPICS:
					showTopics((Vector) param1, topicmapID, updateGeometry);
					getEditor(topicmapID).setBounds();
					break;
				case DIRECTIVE_SHOW_ASSOCIATION:
					PresentationAssociation assoc = (PresentationAssociation) param1;
					mapID = (String) param2;
					// Note: if the server did not set the topicmap ID or viewmode ID, the client uses the
					// topicmap resp. viewmode in which the action was triggered
					if (mapID.equals("")) {
						// error check
						if (topicmap == null) {
							String hint = ">>> Hint to application programmer: to use DIRECTIVE_SHOW_ASSOCIATION " +
								"asynchronously you must set \"topicmap ID\" and \"viewmode\"";
							showMessage(hint, NOTIFICATION_ERROR);
							System.out.println("*** PresentationService.processDirectives(): " + hint);
							break;
						}
						//
						mapID = topicmapID;
					}
					showAssociation(assoc, mapID);
					break;
				case DIRECTIVE_SHOW_ASSOCIATIONS:
					showAssociations((Vector) param1, topicmapID);
					break;
				case DIRECTIVE_HIDE_TOPIC:
					topicID = (String) param1;
					mapID = (String) param2;
					hideTopic(topicID, mapID);
					getEditor(mapID).setBounds();
					break;
				case DIRECTIVE_HIDE_TOPICS:
					Vector topicIDs = (Vector) param1;
					mapID = (String) param2;
					hideTopics(topicIDs, mapID);
					getEditor(mapID).setBounds();
					break;
				case DIRECTIVE_HIDE_ASSOCIATION:
					String assocID = (String) param1;
					mapID = (String) param2;
					hideAssociation(assocID, mapID);
					break;
				case DIRECTIVE_HIDE_ASSOCIATIONS:
					Vector assocIDs = (Vector) param1;
					mapID = (String) param2;
					hideAssociations(assocIDs, mapID);
					break;
				case DIRECTIVE_SELECT_TOPIC:
					// error check
					if (topicmapID == null) {
						String hint = ">>> Hint to application programmer: DIRECTIVE_SELECT_TOPIC can't be used asynchronously";
						showMessage(hint, NOTIFICATION_ERROR);
						System.out.println("*** PresentationService.processDirectives(): " + hint);
						break;
					}
					//
					topicID = (String) param1;
					Hashtable props = (Hashtable) param2;
					Vector disabledProps = (Vector) param3;
					boolean retypeIsAllowed = ((Boolean) param4).booleanValue();
					Hashtable baseURLs = (Hashtable) param5;
					selectTopic(topicID, props, baseURLs, disabledProps, retypeIsAllowed, topicmapID, viewMode);
					break;
				case DIRECTIVE_SELECT_ASSOCIATION:
					// error check
					if (topicmapID == null) {
						String hint = ">>> Hint to application programmer: DIRECTIVE_SELECT_ASSOCIATION can't be used asynchronously";
						showMessage(hint, NOTIFICATION_ERROR);
						System.out.println("*** PresentationService.processDirectives(): " + hint);
						break;
					}
					//
					assocID = (String) param1;
					props = (Hashtable) param2;
					disabledProps = (Vector) param3;
					retypeIsAllowed = ((Boolean) param4).booleanValue();
					baseURLs = (Hashtable) param5;
					selectAssociation(assocID, props, baseURLs, disabledProps, retypeIsAllowed, topicmapID, viewMode);
					break;
				case DIRECTIVE_SELECT_TOPICMAP:
					// error check
					if (topicmapID == null) {
						String hint = ">>> Hint to application programmer: DIRECTIVE_SELECT_TOPICMAP can't be used asynchronously";
						showMessage(hint, NOTIFICATION_ERROR);
						System.out.println("*** PresentationService.processDirectives(): " + hint);
						break;
					}
					//
					props = (Hashtable) param1;
					disabledProps = (Vector) param2;
					// ### retypeIsAllowed = ((Boolean) param3).booleanValue();
					baseURLs = (Hashtable) param3;
					selectTopicMap(props, baseURLs, disabledProps, topicmapID, viewMode);
					break;
				case DIRECTIVE_UPDATE_TOPIC_TYPE:
					updateTopicType(topicmapID, (PresentationType) param1);
					break;
				case DIRECTIVE_UPDATE_ASSOC_TYPE:
					updateAssociationType(topicmapID, (PresentationType) param1);
					break;
				case DIRECTIVE_SHOW_TOPIC_PROPERTIES:
					topicID = (String) param1;
					props = (Hashtable) param2;
					baseURLs = (Hashtable) param3;
					showTopicProperties(topicID, props, baseURLs);
					break;
				case DIRECTIVE_SHOW_ASSOC_PROPERTIES:
					assocID = (String) param1;
					props = (Hashtable) param2;
					baseURLs = (Hashtable) param3;
					showAssociationProperties(assocID, props, baseURLs);
					break;
				case DIRECTIVE_FOCUS_TYPE:
					focusType(topicmapID, viewMode);
					break;
				case DIRECTIVE_FOCUS_NAME:
					focusName(topicmapID, viewMode);
					break;
				case DIRECTIVE_FOCUS_PROPERTY:
					// ### String prop = (String) param1;
					focusProperty(topicmapID, viewMode /* ###, prop */);
					break;
				case DIRECTIVE_SET_TOPIC_NAME:
					topicID = (String) param1;
					String name = (String) param2;
					changeTopicName(topicID, name);
					break;
				case DIRECTIVE_SET_ASSOC_NAME:
					assocID = (String) param1;
					name = (String) param2;
					changeAssociationName(assocID, name);
					break;
				case DIRECTIVE_SET_TOPIC_LABEL:
					topicID = (String) param1;
					String label = (String) param2;
					changeTopicLabel(topicmapID, viewMode, topicID, label);
					break;
				case DIRECTIVE_SET_TOPIC_ICON:
					topicID = (String) param1;
					String iconfile = (String) param2;
					changeTopicIcon(topicID, iconfile);
					break;
				case DIRECTIVE_SET_TOPIC_GEOMETRY:
					topicID = (String) param1;
					Point p = (Point) param2;
					mapID = (String) param3;
					setTopicGeometry(topicID, p, mapID);
					break;
				case DIRECTIVE_SET_TOPIC_LOCK:
					topicID = (String) param1;
					boolean isLocked = ((Boolean) param2).booleanValue();
					mapID = (String) param3;
					setTopicLock(topicID, isLocked, mapID);
					break;
				case DIRECTIVE_SET_TOPIC_TYPE:
					topicID = (String) param1;
					typeID = (String) param2;
					changeTopicType(topicmapID, viewMode, topicID, typeID);
					break;
				case DIRECTIVE_SET_ASSOC_TYPE:
					assocID = (String) param1;
					typeID = (String) param2;
					changeAssociationType(topicmapID, viewMode, assocID, typeID);
					break;
				case DIRECTIVE_SHOW_MENU:
					String menuID = (String) param1;
					PresentationCommands commands = (PresentationCommands) param2;
					p = (Point) param3;
					showMenu(menuID, commands, p.x, p.y);
					break;
				case DIRECTIVE_SHOW_DETAIL:
					String obbjectID = (String) param1;
					Detail detail = (Detail) param2;
					showDetail(topicmapID, viewMode, obbjectID, detail);
					break;
				case DIRECTIVE_SHOW_WORKSPACE:
					PresentableTopic topicmapMetadata = (PresentableTopic) param1;
					PresentationTopicMap topicmapUse = (PresentationTopicMap) param2;
					// ### PresentationTopicMap topicmapBuild = (PresentationTopicMap) param3;
					int editorContext = ((Integer) param3).intValue();
					showWorkspace(topicmapMetadata, topicmapUse, editorContext);
					// Note: there is no addInitAssociations() here because there are no associations in workspaces
					break;
				case DIRECTIVE_SHOW_VIEW:
					topicmapMetadata = (PresentableTopic) param1;
					mapID = topicmapMetadata.getID();
					topicmapUse = (PresentationTopicMap) param2;
					// ### topicmapBuild = (PresentationTopicMap) param3;
					showView(topicmapMetadata, topicmapUse);
					break;
				case DIRECTIVE_SELECT_EDITOR:
					mapID = (String) param1;
					selectEditor(mapID);
					break;
				case DIRECTIVE_RENAME_EDITOR:
					mapID = (String) param1;
					name = (String) param2;
					renameEditor(mapID, name);
					break;
				case DIRECTIVE_CLOSE_EDITOR:
					mapID = (String) param1;
					closeEditor(mapID);
					break;
				case DIRECTIVE_SET_EDITOR_BGIMAGE:
					mapID = (String) param1;
					String imagefile = (String) param2;
					changeEditorBackgroundImage(mapID, imagefile);
					break;
				case DIRECTIVE_SET_EDITOR_BGCOLOR:
					mapID = (String) param1;
					Color color = (Color) param2;
					changeBackgroundColor(mapID, color);
					// ### Note: no addRefreshTopicmap() required here, because the
					// corresponding GraphPanel is accessed directly and
					// Component.setBackground() is called
					break;
				case DIRECTIVE_SET_EDITOR_ICON:
					mapID = (String) param1;
					iconfile = (String) param2;
					changeEditorIcon(mapID, iconfile);
					break;
				case DIRECTIVE_SHOW_MESSAGE:
					String message = (String) param1;
					int notificationType = ((Integer) param2).intValue();
					showMessage(message, notificationType);
					break;
				case DIRECTIVE_PLAY_SOUND:
					String soundfile = (String) param1;
					playSound(soundfile);
					break;
				case DIRECTIVE_CHOOSE_FILE:
					String path = chooseFile();
					result = path != null ? path : "";
					break;
				case DIRECTIVE_CHOOSE_COLOR:
					String currentColor = (String) param1;
					String newColor = chooseColor(currentColor);
					result = newColor != null ? newColor : "";
					break;
				case DIRECTIVE_COPY_FILE:
					path = (String) param1;
					int filetype = ((Integer) param2).intValue();
					copyFile(path, filetype);
					break;
				case DIRECTIVE_DOWNLOAD_FILE:
					String filename = (String) param1;
					long lastModified = ((Long) param2).longValue();
					filetype = ((Integer) param3).intValue();
					downloadFile(filename, lastModified, filetype);
					break;
				case DIRECTIVE_UPLOAD_FILE:
					filename = (String) param1;
					lastModified = ((Long) param2).longValue();
					filetype = ((Integer) param3).intValue();
					uploadFile(filename, lastModified, filetype);
					break;
				case DIRECTIVE_SET_LAST_MODIFIED:
					filename = (String) param1;
					lastModified = ((Long) param2).longValue();
					filetype = ((Integer) param3).intValue();
					setLastModifiedLocally(filename, filetype, lastModified);
					break;
				case DIRECTIVE_OPEN_FILE:
					String command = (String) param1;
					filename = (String) param2;
					executeSystemCommand(command, filename);
					break;
				case DIRECTIVE_QUEUE_MESSAGE:
					queueMessage((String) param1);
					break;
				case DIRECTIVE_QUEUE_DIRECTIVES:
					queueDirectives((PresentationDirectives) param1);
					break;
				case DIRECTIVE_LAUNCH_APPLICATION:
					executeSystemCommand((String) param1);
					break;
				case DIRECTIVE_OPEN_URL:
					showWebpage((String) param1);
					break;
				default:
					throw new DeepaMehtaException("unexpected directive type: " + dirType);
				}
			} catch (Exception e) {
				System.out.println("*** PresentationService.processDirectives(): " +
					"directive type " + dirType + ": " + e);
				showMessage("Client error while processing directive of type " +
					dirType + " (" + e + ")", NOTIFICATION_ERROR);
				e.printStackTrace();
			}	// end try
		}	// end loop through all directives
		// diagnosis output
		System.out.println("> " + dirCount + " directives processed, types:" + log);
		if (LOG_MEM_STAT) {
			DeepaMehtaUtils.memoryStatus();
		}
		//
		repaint();
		// update collected topic geometry
		if (updateGeometry.size() > 0) {
			updateGeometry(updateGeometry);
		}
		return result;
	}

	// ---

	/**
	 * @return	the topic type specified by its ID or <code>null</code> if no such
	 *			topic type is known.
	 */
	PresentationType getTopicType(String typeID) {
		return (PresentationType) globalTopicTypes.get(typeID);
	}

	/**
	 * @return	the association type specified by its ID or <code>null</code> if no such
	 *			association type is known.
	 */
	PresentationType getAssociationType(String typeID) {
		return (PresentationType) globalAssocTypes.get(typeID);
	}

	// ---

	/**
	 * ### to be dropped
	 */
	void showMessage(String text) {
		showMessage(text, text.startsWith("***") || text.startsWith("###") ?
			NOTIFICATION_ERROR : NOTIFICATION_DEFAULT);
	}

	// ---

	/**
	 * @see		#run
	 * @see		TopicmapEditorModel#normalViewActivated
	 */
	void setWaitCursor() {
		setMouseCursor(Cursor.WAIT_CURSOR);
	}

	void setHandCursor() {
		setMouseCursor(Cursor.HAND_CURSOR);
	}

	void setCrosshairCursor() {
		setMouseCursor(Cursor.CROSSHAIR_CURSOR);
	}

	/**
	 * @see		#run
	 * @see		TopicmapEditorModel#normalViewActivated
	 */
	void setDefaultCursor() {
		setMouseCursor(Cursor.DEFAULT_CURSOR);
	}

	// ---

	private PresentationTopicMap getTopicmap() {
		return getEditor().getTopicMap();
	}

	// --- getEditor (2 forms) ---
	
	private TopicmapEditorModel getEditor() throws DeepaMehtaException {
		return getEditor(selectedTopicmapID);
	}

	/**
	 * @throws	DeepaMehtaException		if no editor is opened for the specified topicmap
	 * @throws	NullPointerException	if "topicmapID" is null
	 */
	private TopicmapEditorModel getEditor(String topicmapID) throws DeepaMehtaException {
		// error check
		if (topicmapID == null) {
			throw new NullPointerException("\"topicmapID\" not set (null)");
		}
		//
		TopicmapEditorModel editor = (TopicmapEditorModel) editors.get(topicmapID);
		// error check
		if (editor == null) {
			throw new DeepaMehtaException("editor \"" + topicmapID + "\" not found");
		}
		//
		return editor;
	}



	// -----------------------------
	// --- Processing Directives ---
	// -----------------------------



	/**
	 * Processes {@link #DIRECTIVE_SHOW_TOPIC} and {@link #DIRECTIVE_SHOW_TOPICS} (called repeatedly).
	 * <p>
	 * If the topic's type don't exists in the specified viewmode the topic type is copied
	 * from the list of corporate wide topic types before the topic is added ###.
	 * <p>
	 * References checked: 16.4.2002 (2.0a14-post1)
	 *
	 * @param	updateGeometry			return parameter to collect the topics which have got their
	 *									geometry at client side
	 *
	 * @see		#processDirectives		package private
	 * @see		#showTopics				private
	 */
	private boolean showTopic(PresentationTopic topic, String topicmapID, Hashtable updateGeometry) {
		boolean typeIsKnown = false;
		try {
			typeIsKnown = checkTopic(topic);
			boolean inited = getEditor(topicmapID).showTopic(topic);		// throws DME
			//
			if (inited) {
				addUpdateGeometry(topic, topicmapID, updateGeometry);
			}
		} catch (DeepaMehtaException e) {
			String errText = "Topic " + topic + " not visible in view \"" + topicmapID + "\" (" + e.getMessage() + ")";
			showMessage(errText, NOTIFICATION_ERROR);
			System.out.println("*** PresentationService.showTopic(): " + errText);
		}
		return typeIsKnown;
	}

	/**
	 * Processes {@link #DIRECTIVE_SHOW_ASSOCIATION} and {@link #DIRECTIVE_SHOW_ASSOCIATIONS}
	 * (called repeatedly).
	 *
	 * @see		#processDirectives
	 * @see		#showAssociations
	 */
	private boolean showAssociation(PresentationAssociation assoc, String topicmapID) {
		boolean typeIsKnown = false;
		try {
			TopicmapEditorModel editor = getEditor(topicmapID);
			typeIsKnown = checkAssociationType(assoc.getType());
			editor.showAssociation(assoc);
		} catch (DeepaMehtaException e) {
			String errText = "Association " + assoc + " not visible in view \"" + topicmapID + "\" (" + e.getMessage() + ")";
			showMessage(errText, NOTIFICATION_ERROR);
			System.out.println("*** PresentationService.showAssociation(): " + errText);
		}
		return typeIsKnown;
	}

	// ---

	/**
	 * Ensures the type is loaded for showing the specified topic.
	 *
	 * @see		#showTopic
	 */
	private boolean checkTopic(BaseTopic topic) {
		String topicID = topic.getID();
		String typeID = topic.getType();
		boolean typeIsKnown = checkTopicType(typeID);	// may throw DME
		// if the topic represents type itself, this type must also be known
		// in order to access the type-appearance
		if (typeID.equals(TOPICTYPE_TOPICTYPE)) {
			checkTopicType(topicID);
		} else if (typeID.equals(TOPICTYPE_ASSOCTYPE)) {
			checkAssociationType(topicID);
		}
		return typeIsKnown;
	}

	// ---

	/**
	 * Processes {@link #DIRECTIVE_SHOW_TOPICS}.
	 *
	 * @see		#processDirectives
	 */
	private boolean showTopics(Vector topics, String topicmapID, Hashtable updateGeometry) {
		boolean typesAreKnown = true;
		//
		Enumeration e = topics.elements();
		while (e.hasMoreElements()) {
			PresentationTopic topic = (PresentationTopic) e.nextElement();
			if (!showTopic(topic, topicmapID, updateGeometry)) {
				typesAreKnown = false;
			}
		}
		//
		return typesAreKnown;
	}

	/**
	 * Processes {@link #DIRECTIVE_SHOW_ASSOCIATIONS}.
	 *
	 * @see		#processDirectives
	 */
	private boolean showAssociations(Vector assocs, String topicmapID) {
		boolean typesAreKnown = true;
		//
		Enumeration e = assocs.elements();
		while (e.hasMoreElements()) {
			PresentationAssociation assoc = (PresentationAssociation) e.nextElement();
			if (!showAssociation(assoc, topicmapID)) {
				typesAreKnown = false;
			}
		}
		//
		return typesAreKnown;
	}

	// ---

	/**
	 * Processes {@link #DIRECTIVE_HIDE_TOPICS}.
	 *
	 * @see		#processDirectives
	 */
	private void hideTopics(Vector topicIDs, String topicmapID) {
		Enumeration e = topicIDs.elements();
		while (e.hasMoreElements()) {
			String topicID = (String) e.nextElement();
			hideTopic(topicID, topicmapID);
		}
	}

	/**
	 * Processes {@link #DIRECTIVE_HIDE_ASSOCIATIONS}.
	 *
	 * @see		#processDirectives
	 */
	private void hideAssociations(Vector assocIDs, String topicmapID) {
		Enumeration e = assocIDs.elements();
		while (e.hasMoreElements()) {
			String assocID = (String) e.nextElement();
			hideAssociation(assocID, topicmapID);
		}
	}

	// ---

	/**
	 * Processes {@link #DIRECTIVE_HIDE_TOPIC}.
	 *
	 * @see		#processDirectives
	 */
	private void hideTopic(String topicID, String topicmapID) {
		try {
			getEditor(topicmapID).hideTopic(topicID);
			//
			propertyPanel.removeTopicSelection(topicID);
		} catch (DeepaMehtaException e) {
			// ### actually this is not an exceptional case but regular application behavoir for the moment
			System.out.println(">>> no need to hide topic (" + e.getMessage() + ")");
		}
	}

	/**
	 * Processes {@link #DIRECTIVE_HIDE_ASSOCIATION}.
	 *
	 * @see		#processDirectives
	 */
	private void hideAssociation(String assocID, String topicmapID) {
		try {
			getEditor(topicmapID).hideAssociation(assocID);	// throws DME
			//
			propertyPanel.removeAssociationSelection(assocID);
		} catch (DeepaMehtaException e) {
			// ### actually this is not an exceptional case but regular application behavoir for the moment
			System.out.println(">>> no need to hide association (" + e.getMessage() + ")");
		}
	}

	// ---

	/**
	 * Processes {@link #DIRECTIVE_SELECT_TOPIC}.
	 *
	 * @see		#processDirectives
	 */
	private void selectTopic(String topicID, Hashtable props, Hashtable baseURLs, Vector disabledProps, boolean retypeIsAllowed,
																				String topicmapID, String viewmode) {
		TopicmapEditorModel editor = getEditor(topicmapID);	// may throw DME
		editor.selectTopic(topicID);
		// --- update property panel ---
		PresentationTopicMap topicmap = editor.getTopicMap();
		BaseTopic topic = (BaseTopic) topicmap.getTopic(topicID);
		propertyPanel.topicSelected(topic, topicmap, viewmode, props, baseURLs, disabledProps, retypeIsAllowed);
	}

	/**
	 * Processes {@link #DIRECTIVE_SELECT_ASSOCIATION}.
	 *
	 * @see		#processDirectives
	 */
	private void selectAssociation(String assocID, Hashtable props, Hashtable baseURLs, Vector disabledProps, boolean retypeIsAllowed,
															String topicmapID, String viewmode) {
		TopicmapEditorModel editor = getEditor(topicmapID);
		editor.selectAssociation(assocID);
		// --- update property panel ---
		PresentationTopicMap topicmap = editor.getTopicMap();
		BaseAssociation assoc = (BaseAssociation) topicmap.getAssociation(assocID);
		propertyPanel.assocSelected(assoc, topicmap, viewmode, props, baseURLs, disabledProps, retypeIsAllowed);
	}

	/**
	 * Processes {@link #DIRECTIVE_SELECT_TOPICMAP}.
	 *
	 * @see		#processDirectives
	 */
	private void selectTopicMap(Hashtable props, Hashtable baseURLs, Vector disabledProps, String topicmapID, String viewmode) {
		TopicmapEditorModel editor = getEditor(topicmapID);	// may throw DME
		editor.selectTopicmap();
		// --- update property panel ---
		PresentationTopicMap topicmap = editor.getTopicMap();
		checkTopicType(topicmap.getEditor().getTopicmap().getType());
		propertyPanel.topicmapSelected(topicmap, viewmode, props, baseURLs, disabledProps);
	}

	// ---

	/**
	 * Processes {@link #DIRECTIVE_UPDATE_TOPIC_TYPE}.
	 *
	 * @see		#processDirectives
	 */
	private void updateTopicType(String topicmapID, PresentationType type) {
		boolean typeIsKnown = false;
		try {
			checkTopicType(type.getID());			// ### check required?	// may throw DME
			// --- update model ---
			updateTopicType(type);
			// --- update view ---
			propertyPanel.buildPropertyForm(type);
		} catch (DeepaMehtaException e) {
			showMessage(type + " not updated (" + e.getMessage() + ")", NOTIFICATION_ERROR);
		}
	}

	/**
	 * Processes {@link #DIRECTIVE_UPDATE_ASSOC_TYPE}.
	 *
	 * @see		#processDirectives
	 */
	private void updateAssociationType(String topicmapID, PresentationType type) {
		boolean typeIsKnown = false;
		try {
			checkAssociationType(type.getID());		// ### check required?	// may throw DME
			// --- update model ---
			updateAssociationType(type);
			// --- update view ---
			propertyPanel.buildPropertyForm(type);
		} catch (DeepaMehtaException e) {
			showMessage(type + " not updated (" + e.getMessage() + ")", NOTIFICATION_ERROR);
		}
	}

	// ---

	/**
	 * Processes {@link #DIRECTIVE_SHOW_TOPIC_PROPERTIES}.
	 *
	 * @see		#processDirectives
	 */
	private void showTopicProperties(String topicID, Hashtable props, Hashtable baseURLs) {
		propertyPanel.showTopicProperties(topicID, props, baseURLs);
	}

	/**
	 * Processes {@link #DIRECTIVE_SHOW_ASSOC_PROPERTIES}.
	 *
	 * @see		#processDirectives
	 */
	private void showAssociationProperties(String assocID, Hashtable props, Hashtable baseURLs) {
		propertyPanel.showAssociationProperties(assocID, props, baseURLs);
	}

	// ---

	/**
	 * Processes {@link #DIRECTIVE_FOCUS_TYPE}.
	 *
	 * @see		#processDirectives
	 */
	private void focusType(String topicmapID, String viewmode) {
		// ### if (userPrefs.showSidebar) {
		int selected = getEditor(topicmapID).getSelected();
		switch (selected) {
		case SELECTED_TOPIC:
			propertyPanel.focusTopicTypeChoice();
			break;
		case SELECTED_ASSOCIATION:
			propertyPanel.focusAssociationTypeChoice();
			break;
		default:
			throw new DeepaMehtaException("TopicmapEditorModel has unexpected state: " + selected);
		}
		// ### }
	}

	/**
	 * ### params not needed
	 * <p>
	 * Processes {@link #DIRECTIVE_FOCUS_NAME}.
	 *
	 * @see		#processDirectives
	 */
	private void focusName(String topicmapID, String viewmode) {
		// ### if (userPrefs.showSidebar) {
		propertyPanel.focusTopicNameField();
		// ### }
	}

	/**
	 * ### params not needed
	 * <p>
	 * Processes {@link #DIRECTIVE_FOCUS_PROPERTY}.
	 *
	 * @see		#processDirectives
	 */
	private void focusProperty(String topicmapID, String viewmode /* ###, String prop */) {
		// ### if (userPrefs.showSidebar) {
		propertyPanel.focusProperty(/* ### prop */);
		// ### }
		// ### for "Search by Topic Type":
		// prop can be "Topic Name" for normal topics
		// prop for topics obtained from datasources?
	}

	// ---

	/**
	 * Processes {@link #DIRECTIVE_SET_TOPIC_NAME}.
	 *
	 * @exception	DeepaMehtaException		if the specidied topicmap isn't openend
	 *				Note: this is a regular condition (related to delegated triggers)
	 *
	 * @see		#processDirectives
	 */
	private void changeTopicName(String topicID, String name) throws DeepaMehtaException {
		// ### TopicmapEditorModel editor = getEditor(topicmapID);
		// ### PresentationTopicMap topicmap = editor.getTopicMap();
		Enumeration e = editors.elements();
		while (e.hasMoreElements()) {
			TopicmapEditorModel editor = (TopicmapEditorModel) e.nextElement();
			PresentationTopicMap topicmap = editor.getTopicMap();
			// update model
			if (topicmap.topicExists(topicID)) {
				topicmap.changeTopicName(topicID, name);		// changeTopicName() throws DME
				// Note: changeTopicName() is defined in BaseTopicMap
			}
			if (topicmap.getID().equals(topicID)) {
				// Note: the topic could represent the topicmap itself
				editor.getTopicmap().setName(name);
				// ### pending: conditionally update of property panel view
			}
		}
	}

	/**
	 * Processes {@link #DIRECTIVE_SET_ASSOC_NAME}.
	 *
	 * @exception	DeepaMehtaException		if the specidied topicmap isn't openend
	 *				Note: this is a regular condition (related to delegated triggers)
	 *
	 * @see		#processDirectives
	 */
	private void changeAssociationName(String assocID, String name) throws DeepaMehtaException {
		Enumeration e = editors.elements();
		while (e.hasMoreElements()) {
			TopicmapEditorModel editor = (TopicmapEditorModel) e.nextElement();
			PresentationTopicMap topicmap = editor.getTopicMap();
			// update model
			if (topicmap.associationExists(assocID)) {
				topicmap.changeAssociationName(assocID, name);	// changeAssociationName() throws DME
				// Note: changeAssociationName() is defined in BaseTopicMap
			}
		}
	}

	// ---

	/**
	 * Processes {@link #DIRECTIVE_SET_TOPIC_LABEL}.
	 *
	 * @see		#processDirectives
	 */
	private void changeTopicLabel(String topicmapID, String viewmode, String topicID, String label) {
		try {
			PresentationTopicMap topicmap = getEditor(topicmapID).getTopicMap();
			topicmap.changeTopicLabel(topicID, label);
		} catch (DeepaMehtaException e) {
			System.out.println(">>> label of topic \"" + topicID + "\" not set to \"" +
				label + "\" (" + e.getMessage() + ")");
		}
	}

	/**
	 * Processes {@link #DIRECTIVE_SET_TOPIC_ICON}.
	 *
	 * @see		#processDirectives
	 */
	private void changeTopicIcon(String topicID, String iconfile) {
		Enumeration e = editors.elements();
		while (e.hasMoreElements()) {
			TopicmapEditorModel editor = (TopicmapEditorModel) e.nextElement();
			PresentationTopicMap topicmap = editor.getTopicMap();
			// update model
			if (topicmap.topicExists(topicID)) {
				topicmap.changeTopicIcon(topicID, iconfile);		// changeTopicIcon() throws DME
			}
		}
	}

	/**
	 * Processes {@link #DIRECTIVE_SET_TOPIC_GEOMETRY}.
	 *
	 * @see		#processDirectives
	 */
	private void setTopicGeometry(String topicID, Point p, String topicmapID) {
		PresentationTopicMap topicmap = getEditor(topicmapID).getTopicMap();
		topicmap.setTopicGeometry(topicID, p);
	}

	/**
	 * Processes {@link #DIRECTIVE_SET_TOPIC_LOCK}.
	 *
	 * @see		#processDirectives
	 */
	private void setTopicLock(String topicID, boolean isLocked, String topicmapID) {
		// ### topicmapID is ignored, instead all topicmaps are used
		Enumeration e = editors.elements();
		while (e.hasMoreElements()) {
			PresentationTopicMap topicmap = ((TopicmapEditorModel) e.nextElement()).getTopicMap();
			if (topicmap.topicExists(topicID)) {
				topicmap.setTopicLock(topicID, isLocked);
			}
		}
	}

	// ---

	/**
	 * Processes {@link #DIRECTIVE_SET_TOPIC_TYPE}.<br>
	 * ### return value not used
	 *
	 * @see		#processDirectives
	 */
	private boolean changeTopicType(String topicmapID, String viewmode, String topicID, String typeID) {
		boolean typeIsKnown = false;
		try {
			typeIsKnown = checkTopicType(typeID);		// may throw DME ### still needed?
			//
			PresentationTopicMap topicmap = getEditor(topicmapID).getTopicMap();
			topicmap.changeTopicType(topicID, typeID);		// from BaseTopicMap
			// updating property panel not required because DIRECTIVE_SET_TOPIC_TYPE is followed by a DIRECTIVE_SELECT_TOPIC
			// ### propertyPanel.topicTypeChanged(typeID);
		} catch (DeepaMehtaException e) {
			showMessage("Type of topic \"" + topicID + "\" not changed to \"" + typeID +
				"\" -- topic will vanish from view \"" + topicmapID + "\", mode \"" +
				viewmode + "\" (" + e.getMessage() + ")", NOTIFICATION_ERROR);
		}
		return typeIsKnown;
	}

	/**
	 * Processes {@link #DIRECTIVE_SET_ASSOC_TYPE}.<br>
	 * ### return value not used
	 *
	 * @see		#processDirectives
	 */
	private boolean changeAssociationType(String topicmapID, String viewmode, String assocID, String typeID) {
		boolean typeIsKnown = false;
		try {
			typeIsKnown = checkAssociationType(typeID);		// may throw DME ### still needed?
			//
			PresentationTopicMap topicmap = getEditor(topicmapID).getTopicMap();
			topicmap.changeAssociationType(assocID, typeID);
			// updating property panel not required because DIRECTIVE_SET_ASSOC_TYPE is followed by a DIRECTIVE_SELECT_ASSOCIATION
			// ### propertyPanel.assocTypeChanged(typeID);
		} catch (DeepaMehtaException e) {
			showMessage("Type of association \"" + assocID + "\" not changed to \"" +
				typeID + "\" -- association will vanish from view \"" + topicmapID +
				"\", mode \"" + viewmode + "\" (" + e.getMessage() + ")", NOTIFICATION_ERROR);
		}
		return typeIsKnown;
	}

	// ---

	/**
	 * Processes {@link #DIRECTIVE_SHOW_MENU}.
	 *
	 * @see		#processDirectives
	 */
	private void showMenu(String menuID, PresentationCommands commands, int x, int y) {
		graphPanel.showMenu(menuID, commands, x, y);
	}

	/**
	 * Processes {@link #DIRECTIVE_SHOW_DETAIL}.
	 *
	 * @param	objectID	ID of the topic/association/topicmap the detail window is bound to
	 *
	 * @see		#processDirectives
	 */
	private void showDetail(String topicmapID, String viewmode, String objectID, Detail detail) {
		if (detail.getContentType() == DETAIL_CONTENT_NONE) {
			return;
		}
		// check weather the detail of the specified topic is already shown
		TopicmapEditorModel editor = getEditor(topicmapID);
		String key = getKey(objectID, detail.getCommand());
		PresentationDetail pd = editor.getDetail(key);
		JInternalFrame detailWindow;
		if (pd == null) {
			// --- create detail window ---
			// ### copy in GraphPanel.paintDetails()
			int x, y;
			switch (detail.getType()) {
			case DETAIL_TOPIC:
				PresentationTopic topic = editor.getTopic(objectID);				// throws DME
				Point p = topic.getGeometry();
				x = p.x;
				y = p.y;
				break;
			case DETAIL_ASSOCIATION:
				PresentationAssociation assoc = editor.getAssociation(objectID);	// throws DME
				Point p1 = editor.getTopic(assoc.getTopicID1()).getGeometry();
				Point p2 = editor.getTopic(assoc.getTopicID2()).getGeometry();
				x = (p1.x + p2.x) / 2;
				y = (p1.y + p2.y) / 2;
				break;
			case DETAIL_TOPICMAP:
				x = detail.getGuideAnchor().x;
				y = detail.getGuideAnchor().y;
				break;
			default:
				throw new DeepaMehtaException("unexpected detail type: " + detail.getType());
			}
			//
			Point translation = editor.getTopicMap().getTranslation();
			pd = new PresentationDetail(detail, x + translation.x, y + translation.y, this);
			detailWindow = pd.getWindow();
			detailWindow.setName(key);		// component name is used to store key
			editor.addDetailListeners(detailWindow);
			// --- update model ---
			editor.putDetail(key, pd);
			// --- update view ---
			graphPanel.addDetailWindow(detailWindow);
		} else {
			detailWindow = pd.getWindow();
			System.out.println(">>> detail \"" + detailWindow.getName() + "\" reopened");
			// ### possibly update content
			// adjust guide anchor position
			if (detail.getType() == DETAIL_TOPICMAP) {
				pd.setGuideAnchor(detail.getGuideAnchor());
			}
		}
		// select detail window
		try {
			detailWindow.setSelected(true);
		} catch (PropertyVetoException e) {
			System.out.println("*** " + e);
		}
	}

	// ---

	/**
	 * Processes {@link #DIRECTIVE_SHOW_WORKSPACE}.
	 *
	 * @see		#processDirectives
	 */
	private void showWorkspace(PresentableTopic topicmapMetadata, PresentationTopicMap topicMap, int editorContext) {
		//
		int index;
		switch (editorContext) {
		case EDITOR_CONTEXT_PERSONAL:
			index = 0;
			break;
		case EDITOR_CONTEXT_WORKGROUP:
			workgroupCount++;
			index = workgroupCount;
			break;
		default:
			throw new DeepaMehtaException("unexpected editor context: " + editorContext);
		}
		//
		createTopicMapEditor(editorContext, topicmapMetadata, topicMap, index);
	}

	/**
	 * Processes {@link #DIRECTIVE_SHOW_VIEW}.
	 *
	 * @see		#processDirectives
	 */
	private void showView(PresentableTopic topicmapMetadata, PresentationTopicMap topicMap) {
		//
		int index = topicmapChoice.getItemCount();	// ### -1 once message panel appears in topicmap choice
		//
		createTopicMapEditor(EDITOR_CONTEXT_VIEW, topicmapMetadata, topicMap, index);
	}

	// ---

	private String getKey(String topicID, String command) {
		return topicID + ":" + command;
	}

	// ---

	/**
	 * Processes {@link #DIRECTIVE_SELECT_EDITOR}
	 *
	 * @see		#processDirectives
	 */
	private void selectEditor(String topicmapID) {
		topicmapChoice.setSelectedItem(getEditor(topicmapID).item);
	}

	// --- Notifying the user ---

	/**
	 * Processes <code>DIRECTIVE_SHOW_MESSAGE</code>.
	 *
	 * @see		#processDirectives
	 */
	void showMessage(String text, int notificationType) {
		// ### check not needed anymore
		if (messagePanel == null) {
			throw new DeepaMehtaException("message panel not yet created");
		}
		//
		messagePanel.addMessage(text, notificationType);
	}

	/**
	 * @see		#setWaitCursor
	 * @see		#setHandCursor
	 * @see		#setCrosshairCursor
	 * @see		#setDefaultCursor
	 */
	private void setMouseCursor(int cursorType) {
		mainWindow.setCursor(new Cursor(cursorType));
	}

	/**
	 * Processes <code>DIRECTIVE_PLAY_SOUND</code>.
	 *
	 * @see		#processDirectives
	 */
	private void playSound(String soundfile) {
		if (PLAY_SOUNDS) {
			if (runsAsApplet) {
				System.out.println("> PresentationService.playSound(): \"" +
					soundfile + "\"");
				applet.play(applet.getCodeBase(), FILESERVER_SOUNDS_PATH + soundfile);
			} else {
				// ###
				showMessage("Playing sounds not yet supported if running as application (" + soundfile +
					" not played) >>> Use the applet instead", NOTIFICATION_WARNING);
				System.out.println("*** PresentationService.playSound(): not yet supported if running as " +
					"application -- \"" + soundfile + "\" not played");
			}
		}
	}

	// ---

	/**
	 * Processes {@link #DIRECTIVE_SET_EDITOR_BGIMAGE}
	 *
	 * @see		#processDirectives
	 */
	private void changeEditorBackgroundImage(String topicmapID, String imagefile) {
		try {
			Image image = null;
			if (!imagefile.equals("")) {
				image = getImage(FILESERVER_BACKGROUNDS_PATH + imagefile);
			}
			getEditor(topicmapID).setBackgroundImage(image);
		} catch (DeepaMehtaException e) {
			System.out.println(">>> PresentationService.changeEditorBackgroundImage(): " + e);
		}
	}

	/**
	 * Processes {@link #DIRECTIVE_SET_EDITOR_BGCOLOR}
	 *
	 * @see		#processDirectives
	 */
	private void changeBackgroundColor(String topicmapID, Color color) {
		try {
			// update model
			getEditor(topicmapID).setBackgroundColor(color);
			// update view
			if (isSelected(topicmapID)) {
				graphPanel.setBackgroundColor(color);
			}
		} catch (DeepaMehtaException e) {
			System.out.println(">>> PresentationService.changeBackgroundColor(): " + e);
		}
	}

	/**
	 * Processes {@link #DIRECTIVE_SET_EDITOR_ICON}
	 *
	 * @see		#processDirectives
	 */
	private void changeEditorIcon(String topicmapID, String iconfile) {
		try {
			Icon icon = getIcon(FILESERVER_ICONS_PATH, iconfile);
			getEditor(topicmapID).setIcon(icon);
		} catch (DeepaMehtaException e) {
			System.out.println(">>> PresentationService.changeEditorIcon(): " + e);
		}
	}

	/**
	 * Processes <code>DIRECTIVE_RENAME_EDITOR</code>
	 *
	 * @see		#processDirectives
	 */
	private void renameEditor(String topicmapID, String name) {
		try {
			getEditor(topicmapID).setName(name);
		} catch (DeepaMehtaException e) {
			System.out.println(">>> editor not renamed to \"" + name + "\" (" + e.getMessage() + ")");
		}
	}

	// ---

	/**
	 * Processes {@link #DIRECTIVE_OPEN_URL}
	 *
	 * @see		#processDirectives
	 */
	private void showWebpage(String urlStr) {
		try {
			if (runsAsApplet) {
				URL url = new URL(urlStr);
				logger.info("\"" + url + "\"");
				applet.getAppletContext().showDocument(url, "extern");	// ###
			} else {
				// ###
				showMessage("Showing webpages not yet supported if running as application (" + urlStr + " not shown) " +
					">>> Use the applet instead", NOTIFICATION_WARNING);
				logger.warning("not yet supported if running as application -- \"" + urlStr + "\" not shown");
			}
		} catch (Exception e) {
			logger.warning("webpage \"" + urlStr + "\" can't be shown (" + e + ")");
		}
	}

	// --- executeSystemCommand (2 forms) ---

	/**
	 * Processes <code>DIRECTIVE_LAUNCH_APPLICATION</code>.
	 *
	 * @see		#processDirectives
	 */
	private void executeSystemCommand(String command) {
		String cmdStr = "\"" + command + "\"";	// just for reporting
		try {
			logger.info("executing " + cmdStr);
			Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			showMessage("Application \"" + command + "\" not launched because it is not found", NOTIFICATION_WARNING);
			logger.warning("application \"" + command + "\" not found -- application not opened");
		} catch (Exception e) {
			showMessage("Application \"" + command + "\" not launched because of security restrictions " +
				">>> Use the signed applet or the application", NOTIFICATION_WARNING);
			logger.warning(cmdStr + " can't be executed (" + e + ")");
		}
	}

	/**
	 * Processes <code>DIRECTIVE_OPEN_FILE</code>
	 *
	 * @see		#processDirectives
	 */
	private void executeSystemCommand(String command, String param) {
		// ### hack for Mac OS X: the application is ignored, the file is opened with "open" instead.
		// ### I don't know how to start Mac OS X applications with Runtime.exec().
		if (platform.equals(PLATFORM_MACOSX)) {
			command = "open";
		}
		//
		String[] cmd = {command, FileServer.repositoryPath(FILE_DOCUMENT) + param};
		String cmdStr = "\"" + cmd[0] + " " + cmd[1] + "\"";	// just for reporting
		try {
			logger.info("executing " + cmdStr);
			Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			showMessage("File \"" + param + "\" can not be opened because application \"" +
				command + "\" not found", NOTIFICATION_WARNING);
			logger.warning("application \"" + command + "\" not found -- \"" + param + "\" not opened");
		} catch (Exception e) {
			showMessage("File \"" + param + "\" not opened because of security restrictions " +
				">>> Use the signed applet or the application", NOTIFICATION_WARNING);
			logger.warning(cmdStr + " can't be executed (" + e + ")");
		}
	}

	// ---

	/**
	 * Processes <code>DIRECTIVE_CHOOSE_FILE</code>
	 * <p>
	 * Presents the filechooser dialog to let the user choose one file from the local
	 * filesystem. If a file has been selected the path is returned. If the
	 * user aborts the selection or if an error occurs <code>null</code> is returned.
	 *
	 * @see		#processDirectives
	 */
	private String chooseFile() {
		String path = null;
		try {
			JFileChooser chooser = new JFileChooser(currentPath);
			int returnVal = chooser.showOpenDialog(mainWindow);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				path = file.getPath().replace('\\', '/');	// ###
				// remember file chooser path
				currentPath = file.getParent();
			}
		} catch (Exception e) {
			System.out.println("*** PresentationService.chooseFile(): " + e +
				" -- filechooser dialog not displayed");
			showMessage("Filechooser dialog can't be displayed because of security " +
				"restrictions >>> Use the signed applet or the application",
				NOTIFICATION_WARNING);
		}
		return path;
	}

	// ---

	/**
	 * Processes <code>DIRECTIVE_CHOOSE_COLOR</code> and <code>DIRECTIVE_CHOOSE_BACKGROUND_COLOR</code>
	 * <p>
	 * Presents the color chooser dialog to let the user choose a color.
	 * If a color has been selected the hexadecimal representation is returned. If the
	 * user aborts the selection or if an error occurs <code>null</code> is returned.
	 *
	 * @see         #processDirectives
	 */
	private String chooseColor(String currentColor) {
		String colText = null;
		Color current, col;
		try {
			current = Color.decode(currentColor);
		} catch (Exception e) {
			current = Color.white;
		}
		col = JColorChooser.showDialog(mainWindow, "Choose color", current);
		if (col != null)
		{
			int r = col.getRed();
			int g = col.getGreen();
			int b = col.getBlue();
			String cv;
			colText = "#";
			
			cv = Integer.toHexString(r);
			if (cv.length() == 1) {
				colText = colText + "0" + cv;
			} else {
				colText = colText + cv;
			}
			
			cv = Integer.toHexString(g);
			if (cv.length() == 1) {
				colText = colText + "0" + cv;
			} else {
				colText = colText + cv;
			}
			
			cv = Integer.toHexString(b);
			if (cv.length() == 1) {
				colText = colText + "0" + cv;
			} else {
				colText = colText + cv;
			}
		} 
		return colText;
	}

	// ---
	
	/**
	 * Processes <code>DIRECTIVE_COPY_FILE</code>.
	 * <p>
	 * Queues the specified file for being copied from local filesystem into the local
	 * document repository.
	 * <p>
	 * The file is expected to exist on local filesystem. ### If not so an error
	 * is reported and the copy request is <i>not</i> queued.
	 *
	 * @see		#processDirectives
	 */
	private void copyFile(String path, int filetype) {
		queueCopyRequest(new File(path), filetype);
	}

	/**
	 * Processes <code>DIRECTIVE_DOWNLOAD_FILE</code>.
	 *
	 * @see		#processDirectives
	 */
	private void downloadFile(String filename, long lastModified, int filetype) {
		try {
			// error check
			if (lastModified == 0) {
				// ### regular case, consider: a never uploaded document is opened
				// ### and exists locally -- no download required
				System.out.println("*** PresentationService.downloadFile(): file is " +
					"missing in corporate document repository -- \"" + filename +
					"\" not downloaded");
				showMessage("File <U>\"" + filename + "\" not downloaded</U> " +
					"because it is missing at server side", NOTIFICATION_WARNING);
				return;
			}
			// --- access file in local document repository ---
			File file = new File(FileServer.repositoryPath(filetype) + filename);
			long lastModifiedLocally = file.lastModified();
			// --- check weather download is required ---
			boolean download = false;
			// if the file is missing a download request is queued
			if (lastModifiedLocally == 0) {
				download = true;
				if (LOG_FILESERVER) {
					System.out.println("> PresentationService.downloadFile(): file \"" +
						filename + "\" not in local document repository -- download " +
						"required");
				}
			// downloading is only performed if the local file is _older_ than the one in
			// corporate document repository ### operator was != now >
			} else if (lastModified > lastModifiedLocally) {
				download = true;
				if (LOG_FILESERVER) {
					System.out.println("> PresentationService.downloadFile(): local " +
						"file \"" + filename + "\" is not the one in corporate " +
						"document repository -- download required\n    local:     " +
						new Date(lastModifiedLocally) + " (" + lastModifiedLocally +
						")\n    corporate: " + new Date(lastModified) + " (" +
						lastModified + ")");
				}
			} else {
				if (LOG_FILESERVER) {
					System.out.println("> PresentationService.downloadFile(): local " +
						"file \"" + filename + "\" is the same as in corporate " +
						"document repository -- no download required");
				}
			}
			// --- queue request ---
			if (download) {
				queueDownloadRequest(filename, filetype, lastModified);
			}
		} catch (Exception e) {
			showMessage("File <U>\"" + filename + "\" not downloaded</U> because " +
				"of security restrictions >>> Use the signed applet or the application",
				NOTIFICATION_WARNING);
			System.out.println("*** PresentationService.downloadFile(): " + e + " -- " +
				"\"" + filename + "\" not downloaded");
			e.printStackTrace();
		}
	}

	/**
	 * Processes <code>DIRECTIVE_UPLOAD_FILE</code>.
	 * <p>
	 * Queues the specified file for being uploaded if it has changed against the
	 * specified timestamp.
	 * <p>
	 * ### If the specified timestamp is <code>0</code> the upload request <i>is</i> queued
	 * (regardless of the local files timestamp).
	 * <p>
	 * The file is expected to exist in the local document repository. If not so, an
	 * error is reported and the upload request is <i>not</i> queued.
	 * ### true again, now we can queue directives
	 * jri 3.5.2001 (2.0a10-pre8)]
	 *
	 * @see		#processDirectives
	 */
	private void uploadFile(String filename, long lastModified, int filetype) {
		queueUploadRequest(filename, filetype, lastModified);
	}

	// ---

	/**
	 * Processes <code>DIRECTIVE_QUEUE_MESSAGE</code>.
	 *
	 * @see		#processDirectives
	 */
	private void queueMessage(String message) {
		// Note: the message is queued in the file transfer queue for being send back to the server
		queueRequest(new QueuedRequest(FS_REQUEST_QUEUE_MESSAGE, message));
	}

	/**
	 * Processes <code>DIRECTIVE_QUEUE_DIRECTIVES</code>.
	 *
	 * @see		#processDirectives
	 */
	private void queueDirectives(PresentationDirectives directives) {
		// Note: the directives are queued in the file transfer queue for being processed later by the client
		queueRequest(new QueuedRequest(FS_REQUEST_QUEUE_DIRECTIVES, directives));
	}

	// --- Image Utilities ---

	/**
	 * ### Also application should retrieve image from server (instead of accessing them locally)<br>
	 * ### Java Plugin workaraound?
	 *
	 * @see		PresentationTopic#setIcon
	 * @see		PresentationType#setIcon
	 */
	public Image getImage(String imagefile) {
		// ### System.out.println(">>> PresentationService.getImage(): \"" + imagefile + "\"");
		if (runsAsApplet) {
			return applet.getImage(applet.getCodeBase(), imagefile);
		} else {
			return Toolkit.getDefaultToolkit().createImage(imagefile);
		}
	}

	/**
	 * Workaround for Sun's Java Plugin: returns <code>null</code> if imagefile is empty.
	 *
	 * @see		#changeEditorIcon
	 * @see		#editorIcon
	 * @see		PresentationCommands#menuItem
	 * @see		PresentationCommands#menu
	 * @see		TopicMapEditorControler#createIcon
	 */
	ImageIcon getIcon(String path, String imagefile) {
		if (imagefile.equals("")) {
			return null;
		} else {
			return new ImageIcon(getImage(path + imagefile));
		}
	}



	// --------------------
	// --- File Service ---
	// --------------------



	/**
	 * @see		#uploadFile
	 */
	private void queueUploadRequest(String filename, int filetype, long lastModified) {
		queueRequest(new QueuedRequest(FS_REQUEST_UPLOAD_FILE, filename, filetype, lastModified));
	}

	/**
	 * @see		#downloadFile
	 */
	private void queueDownloadRequest(String filename, int filetype, long lastModified) {
		queueRequest(new QueuedRequest(FS_REQUEST_DOWNLOAD_FILE, filename, filetype, lastModified));
	}

	/**
	 * @see		#copyFile
	 */
	private void queueCopyRequest(File file, int filetype) {
		queueRequest(new QueuedRequest(FS_REQUEST_COPY_FILE, file, filetype));
	}

	// ---

	/**
	 * @see		#run
	 */
	private synchronized void waitForRequest() {
		try {
			progressModel.setValue(0);
			progressModel.setMaximum(0);
			wait();
		} catch (InterruptedException e) {
			System.out.println("*** PresentationService.waitForRequest(): " + e);
		}
	}

	// ---

	private synchronized void queueRequest(QueuedRequest request) {
		requestQueue.addElement(request);
		// Note: ...
		if (request.type != FS_REQUEST_QUEUE_MESSAGE && request.type != FS_REQUEST_QUEUE_DIRECTIVES) {
			progressModel.setMaximum(progressModel.getMaximum() + request.getBlockCount());
		}
		notify();	
	}

	private synchronized void queueMessageRequest(QueuedRequest request) {
		requestQueue.addElement(request);
		notify();	
	}

	private void unqueueRequest(QueuedRequest request) {
		requestQueue.removeElementAt(0);
	}

	// ---

	/**
	 * @see		#run
	 */
	private void performRequest(QueuedRequest request) {
		switch (request.type) {
		case FS_REQUEST_UPLOAD_FILE:
			System.out.println(">>> PresentationService.performRequest(): FS_REQUEST_UPLOAD_FILE");
			performUploadRequest(request.filename, request.filetype, request.lastModified);
			unqueueRequest(request);
			break;
		case FS_REQUEST_DOWNLOAD_FILE:
			System.out.println(">>> PresentationService.performRequest(): FS_REQUEST_DOWNLOAD_FILE");
			performDownloadRequest(request.filename, request.filetype, request.lastModified);
			unqueueRequest(request);
			break;
		case FS_REQUEST_COPY_FILE:
			System.out.println(">>> PresentationService.performRequest(): FS_REQUEST_COPY_FILE");
			performCopyRequest(request.file, request.filetype);
			unqueueRequest(request);
			break;
		case FS_REQUEST_QUEUE_MESSAGE:
			System.out.println(">>> PresentationService.performRequest(): FS_REQUEST_QUEUE_MESSAGE");
			sendMessage(request.message);
			unqueueRequest(request);
			break;
		case FS_REQUEST_QUEUE_DIRECTIVES:
			System.out.println(">>> PresentationService.performRequest(): FS_REQUEST_QUEUE_DIRECTIVES");
			processDirectives(request.directives);
			unqueueRequest(request);
			break;
		default:
			System.out.println("*** PresentationService.performRequest(): unexpected request type: " + request.type);
		}
	}

	// ---

	/**
	 * @see		#performRequest
	 */
	private void performUploadRequest(String filename, int filetype, long lastModified) {
		// ### for queued upload requests the time stamp might be not correct.
		// it is 0 in the following situation: client and server are running
		// at the same machine, sharing the document repository, the file to import
		// was choosen from outside document repository and no version of the file is
		// in document repository -- thus the client will performs the upload request
		// and corrupts the archive file (0 bytes).
		//
		// ### compare to LiveTopic.copyAndUpload()
		// ### compare to TopicMapTopic.doImport()
		// ### compare to CMImportExportTopic.executeChainedCommand()
		try {
			// --- access file in local document repository ---
			File file = new File(FileServer.repositoryPath(filetype) + filename);
			long lastModifiedLocally = file.lastModified();
			// error check
			if (lastModifiedLocally == 0) {
				System.out.println("*** PresentationService.performUploadRequest(): file is " +
					"missing in local document repository -- \"" + filename + "\" not uploaded");
				showMessage("File <U>\"" + filename + "\" not uploaded</U> " +
					"because it is missing at <U>client side</U>", NOTIFICATION_WARNING);
				return;
			}
			// --- check weather upload is required ---
			boolean upload = false;
			// a lastModified value of 0 means the file not yet exists in corporate
			// document repository -- upload is required
			if (lastModified == 0) {
				upload = true;
				//
				if (LOG_FILESERVER) {
					System.out.println("> PresentationService.performUploadRequest(): file \"" +
						filename + "\" was never uploaded -- upload required");
				}
			// uploading is only performed if the local file has changed against the
			// version in corporate document repository
			} else if (lastModified != lastModifiedLocally) {	// ### operator?
				upload = true;
				//
				if (LOG_FILESERVER) {
					System.out.println("> PresentationService.performUploadRequest(): file \"" +
						filename + "\" has been changed locally -- upload required\n" +
						"    local:     " + new Date(lastModifiedLocally) + " (" +
						lastModifiedLocally + ")\n    corporate: " +
						new Date(lastModified) + " (" + lastModified + ")");
				}
			} else {
				if (LOG_FILESERVER) {
					System.out.println("> PresentationService.performUploadRequest(): file \"" +
						filename + "\" hasn't been changed locally -- no upload required");
				}
			}
			// --- perform upload ---
			if (upload) {
				as.uploadFile(filename, filetype);
			}
		} catch (Exception e) {
			showMessage("File <U>\"" + filename + "\" not uploaded</U> because " +
				"of security restrictions >>> Use the signed applet or the application",
				NOTIFICATION_WARNING);
			System.out.println("*** PresentationService.performUploadRequest(): " + e + " -- " +
				"\"" + filename + "\" not uploaded");
		}
	}

	/**
	 * @see		#performRequest
	 */
	private void performDownloadRequest(String filename, int filetype, long lastModified) {
		as.downloadFile(filename, filetype, lastModified);
	}

	// ---

	/**
	 * Copies the specified file into the local document repository.
	 *
	 * @see		#performRequest
	 */
	private void performCopyRequest(File srcFile, int filetype) {
		try {
			fileServer.copyFile(srcFile, filetype);
		} catch (IOException e) {
			System.out.println("*** PresentationService.performCopyRequest(): " + e +
				" -- \"" + srcFile + "\" not copied");
		}
	}

	/**
	 * @see		#performRequest
	 */
	private void sendMessage(String message) {
		as.processMessage(message);
	}



	// ***********************
	// *** Private Methods ***
	// ***********************



	/**
	 * References checked: 11.12.2006 (2.0b8)
	 *
	 * @see		DeepaMehtaClient#initApplication
	 * @see		DeepaMehtaClient#init
	 * @see		de.deepamehta.service.DeepeMehta#initApplication
	 * @see		de.deepamehta.service.DeepeMehta#init
	 */
	public Component createLoginGUI() {
		JPanel p1 = new JPanel();
		p1.add(new JLabel(string(LABEL_USERNAME)));
		p1.add(usernameField);
		//
		JPanel p2 = new JPanel();
		p2.add(new JLabel(string(LABEL_PASSWORD)));
		p2.add(passwordField);
		//
		JPanel p3 = new JPanel();
		p3.setBackground(COLOR_PROPERTY_PANEL);
		p3.setLayout(new BoxLayout(p3, BoxLayout.Y_AXIS));
		p3.add(p1);
		p3.add(p2);
		p3.add(statusLabel);
		//
		JPanel p4 = new JPanel();
		p4.add(p3);
		//
		return p4;
	}

	/**
	 * References checked: 11.12.2006 (2.0b8)
	 *
	 * @see		DeepaMehtaClient#init
	 * @see		de.deepamehta.service.DeepeMehta#init
	 */
	public Component createStartDemoGUI() {
		String label = applet.getParameter("BUTTON_LABEL");
		String color = applet.getParameter("BACKGROUND_COLOR");
		startDemoButton = new JButton(label != null ? label : START_DEMO_LABEL);
		startDemoButton.setBackground(DeepaMehtaUtils.parseHexColor(color, COLOR_PROPERTY_PANEL));
		startDemoButton.setActionCommand("startdemo");
		startDemoButton.addActionListener(this);
		return startDemoButton;
	}

	// --- createErrorGUI (2 forms) ---

	/**
	 * @see		DeepaMehta#init
	 */
	public Component createErrorGUI(Throwable e) {
		return createErrorGUI(e, null, -1);
	}

	/**
	 * @param	host	<code>null</code> is allowed
	 *
	 * @see		DeepaMehtaClient#initApplication
	 * @see		DeepaMehtaClient#init
	 */
	Component createErrorGUI(Throwable e, String host, int port) {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		if (runsAsDemo) {
			p.add(new JLabel("Demo not available"));
		} else {
			p.add(new JLabel("Login not available"));
			p.add(new JLabel(" "));
			if (host != null && e instanceof IOException) {
				// Note: host="" happens if the applets HTML page is loaded via filesystem (file:)
				addTextToPanel("The DeepaMehta server at " + (host.equals("") ? "port " : host + ":") + port +
					" can't be connected (" + e + ")", p, 39);	// ### width is 39 chars
			} else {
				addTextToPanel(e.toString(), p, 39);		// ### width is 39 chars
			}
		}
		//
		return p;
	}

	// --- createMainGUI (2 forms) ---

	/**
	 * Removes the login screen and creates the main GUI.
	 * <p>
	 * ### The workspaces and the message panel is created in the upper screen area.
	 * The progress bar used to reflect background file transfers is created in the
	 * lower screen end.
	 *
	 * @see		#run
	 */
	private void createMainGUI(Directives directives) {
		// --- remove login screen ---
		mainWindow.setSize(WIDTH_WINDOW, HEIGHT_WINDOW);
		if (runsAsApplet) {
			mainWindow.setVisible(true);
		} else {
			cp.removeAll();
			cp.setLayout(new BorderLayout());
		}
		// --- create main GUI ---
		createMainGUI();
		processDirectives(directives);	// creates workspaces and views
		//
		propertyPanel.init();
		//
		progressBar = new JProgressBar(progressModel);
		cp.add(progressBar, BorderLayout.SOUTH);
		//
		topicmapChoice.setSelectedIndex(topicmapChoice.getItemCount() - 1);
		//
		mainWindow.validate();
	}

	private void createMainGUI() {
		topicmapChoice = new JComboBox();
		topicmapChoice.setSize(220, 25);
		topicmapChoice.setLocation(98, 10);
		topicmapChoice.setMaximumRowCount(32);
		topicmapChoice.setBackground(COLOR_VIEW_BGCOLOR);
		topicmapChoice.setRenderer(new ComboBoxRenderer());
		// topicmapChoice.setOpaque(false);	// ###
		topicmapChoice.setActionCommand("selectTopicmap");
		// Note: the listener is set once workspaces and views are added, see createTopicMapEditor()
		backButton = new JButton("<");
		backButton.setSize(42, 20);		// ### width 38, 40 are to small for windows
		backButton.setLocation(10, 11);
		backButton.setEnabled(false);
		backButton.setActionCommand("back");
		backButton.addActionListener(this);
		//
		forwardButton = new JButton(">");
		forwardButton.setSize(42, 20);		// ### width 38, 40 are to small for windows
		forwardButton.setLocation(54, 11);
		forwardButton.setEnabled(false);
		forwardButton.setActionCommand("forward");
		forwardButton.addActionListener(this);
		//
		graphPanel = new GraphPanel(this);
		graphPanel.setTypes(globalTopicTypesV, globalTopicTypes, globalAssocTypesV, globalAssocTypes);
		graphPanel.setImages(corporateImage(), customerImage(), topImage(), leftImage(), bottomImage(), rightImage());
		graphPanel.setLayer(backButton,     JLayeredPane.PALETTE_LAYER.intValue() + 10);
		graphPanel.setLayer(forwardButton,  JLayeredPane.PALETTE_LAYER.intValue() + 10);
		graphPanel.setLayer(topicmapChoice, JLayeredPane.PALETTE_LAYER.intValue() + 10);
		graphPanel.setTransferHandler(new GraphPanelTransferHandler());	// ### requires Java 1.4
																		// ### NoClassDefFoundError not catchable here
		graphPanel.add(backButton);
		graphPanel.add(forwardButton);
		graphPanel.add(topicmapChoice);
		//
		propertyPanel = new PropertyPanel(this);
		// Note: the message panel must be created _before_ the workspaces and views are created
		// 1) put error messages arising while processing the directives into it
		// 2) views are added left from the message panel
		createMessagePanel();
		//
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(WIDTH_WINDOW - WIDTH_VIEW_CONTROLS);
		splitPane.setDividerSize(8);
		splitPane.setLeftComponent(graphPanel);
		splitPane.setRightComponent(propertyPanel);
		//
		cardLayout = new CardLayout();
		cardPanel = new JPanel();
		cardPanel.setLayout(cardLayout);
		cardPanel.add(splitPane, "graphPanel");
		cardPanel.add(messagePanel, "messagePanel");
		//
		cp.add(cardPanel);
	}

	// ---

	/**
	 * @see		#createGUI
	 */
	private void createMessagePanel() {
		messagePanelBackButton = new JButton("<-");
		messagePanelBackButton.setSize(22, 22);
		messagePanelBackButton.setLocation(10, 10);
		messagePanelBackButton.setActionCommand("backToGraphPanel");
		messagePanelBackButton.addActionListener(this);
		//
		messagePanel = new MessagePanel(this);
		messagePanel.add(messagePanelBackButton);
	}

	// ---

	/**
	 * Opens a editor for the specified topicmap.
	 *
	 * @see		#showWorkspace
	 * @see		#showView
	 */
	private void createTopicMapEditor(int editorContext, PresentableTopic topicmapTopic,
																PresentationTopicMap topicMap, int index) {
		// create editor model
		TopicmapEditorModel editor = new TopicmapEditorModel(this, editorContext, topicmapTopic, topicMap);
		topicMap.setEditor(editor);	// ###
		// update model
		String topicmapID = topicmapTopic.getID();
		editors.put(topicmapID, editor);
		// update view
		ImageIcon editorIcon = editorIcon(topicmapTopic);
		ComboBoxItem item = new ComboBoxItem(editorIcon, topicmapTopic.getName(), topicmapID);
		editor.setItem(item);
		topicmapChoice.removeActionListener(this);
		topicmapChoice.insertItemAt(item, index);		// ### causes selection of this item
		topicmapChoice.addActionListener(this);
		// Note: the server is expected to send a DIRECTIVE_SELECT_EDITOR directive afterwards,
		// see TopicMapTopic.open(), compare to closeEditor()
	}

	/**
	 * Updates the model to reflect the specified topicmap is selected now.
	 *
	 * @see		#actionPerformed
	 */
	private void selectTopicmap(String topicmapID) {
		storeProperties();	// ### only if ...
		//
		System.out.println(">>> topicmap \"" + topicmapID + "\" selected");
		selectedTopicmapID = topicmapID;
		// graph panel
		TopicmapEditorModel editor = getEditor();
		PresentationTopicMap topicmap = editor.getTopicMap();
		graphPanel.setModel(topicmap, editor.getSelection(), editor.getBounds(), editor.getDetails(),
			creatingEdgesEnabled(topicmap), getFont());
		// property panel
		propertyPanel.update(topicmapID, VIEWMODE_USE);
		//
		repaint();
	}

	private boolean isSelected(String topicmapID) {
		return topicmapID.equals(selectedTopicmapID);
	}

	/**
	 * Closes the editor that is opened for the specified view.
	 * <p>
	 * Processes {@kink #DIRECTIVE_CLOSE_EDITOR}.
	 *
	 * @see		#processDirectives
	 */
	private void closeEditor(String topicmapID) {
		try {
			TopicmapEditorModel editor = getEditor(topicmapID);		// throws DME
			// --- update model ---
			if (editors.remove(editor.getTopicMapID()) == null) {
				throw new DeepaMehtaException("editor \"" + topicmapID + "\" not found");
			}
			//
			removeFromHistory(topicmapID);
			// ### System.out.println("> topicmap history (" + topicmapHistory.size() + " items): " + topicmapHistory + ", index=" + historyIndex);
			//
			if (editor.getContext() == EDITOR_CONTEXT_WORKGROUP) {
				workgroupCount--;
			}
			// --- update view ---
			topicmapChoice.removeActionListener(this);
			topicmapChoice.removeItem(editor.item);	// causes selection of previous item
			topicmapChoice.addActionListener(this);
			// Note: the server is expected to send a DIRECTIVE_SELECT_EDITOR directive afterwards,
			// see TopicMapTopic.close()
		} catch (DeepaMehtaException e) {
			// ### Note: this is not an error
			System.out.println(">>> editor not closed (" + e.getMessage() + ")");
		}
	}



	// ------------------------
	// --- Topicmap History ---
	// ------------------------



	/**
	 * Called while performing "selectTopicmap" action.
	 *
	 * @see		#actionPerformed
	 */
	private void addToHistory(String topicmapID) {
		// update model
		removeFromHistory(topicmapID);
		//
		topicmapHistory.addElement(topicmapID);
		historyIndex = topicmapHistory.size() - 1;
		// ### System.out.println("> topicmap history (" + topicmapHistory.size() + " items): " + topicmapHistory + ", index=" + historyIndex);
		// update view
		if (historyIndex > 0) {
			backButton.setEnabled(true);
		}
		forwardButton.setEnabled(false);
	}

	/**
	 * @see		#closeEditor
	 * @see		#addToHistory
	 */
	private void removeFromHistory(String topicmapID) {
		int index = topicmapHistory.indexOf(topicmapID);
		if (index == -1) {
			return;
		}
		//
		topicmapHistory.removeElement(topicmapID);
		//
		if (index < historyIndex) {
			historyIndex--;
			if (historyIndex == 0) {
				backButton.setEnabled(false);
			}
		} else {
			if (historyIndex == topicmapHistory.size() - 1) {
				forwardButton.setEnabled(false);
			}
		}
		//
	}

	// ---

	private String back() {
		String topicmapID = (String) topicmapHistory.elementAt(--historyIndex);
		// ### System.out.println("> topicmap history (" + topicmapHistory.size() + " items): " + topicmapHistory + ", index=" + historyIndex);
		//
		if (historyIndex == 0) {
			backButton.setEnabled(false);
		}
		if (historyIndex == topicmapHistory.size() - 2) {
			forwardButton.setEnabled(true);
		}
		//
		topicmapChoice.removeActionListener(this);
		topicmapChoice.setSelectedItem(getEditor(topicmapID).item);
		topicmapChoice.addActionListener(this);
		//
		return topicmapID;
	}

	private String forward() {
		String topicmapID = (String) topicmapHistory.elementAt(++historyIndex);
		// ### System.out.println("> topicmap history (" + topicmapHistory.size() + " items): " + topicmapHistory + ", index=" + historyIndex);
		//
		if (historyIndex == topicmapHistory.size() - 1) {
			forwardButton.setEnabled(false);
		}
		if (historyIndex == 1) {
			backButton.setEnabled(true);
		}
		//
		topicmapChoice.removeActionListener(this);
		topicmapChoice.setSelectedItem(getEditor(topicmapID).item);
		topicmapChoice.addActionListener(this);
		//
		return topicmapID;
	}

	// ---

	/**
	 * @see		#checkTopicType
	 */
	private void addTopicType(PresentationType type) {
		globalTopicTypes.put(type.getID(), type);
		globalTopicTypesV.addElement(type);
		//
		if (/* ### userPrefs.showSidebar && */ !type.isSearchType()) {
			propertyPanel.addTopicTypeToChoice(type);
		}
	}

	/**
	 * @see		#checkAssociationType
	 */
	private void addAssociationType(PresentationType type) {
		globalAssocTypes.put(type.getID(), type);
		globalAssocTypesV.addElement(type);
		//
		// ### if (userPrefs.showSidebar) {
		propertyPanel.addAssociationTypeToChoice(type);
		// ### }
	}

	// ---

	private void updateTopicType(PresentationType type) {
		//
		PresentationType oldType = getTopicType(type.getID());
		// error check
		if (oldType == null) {
			throw new DeepaMehtaException("topic type \"" + type.getID() + "\" is unknown");
		}
		//
		int i = globalTopicTypesV.indexOf(oldType);
		// error check
		if (i == -1) {
			throw new DeepaMehtaException("topic type \"" + type.getID() + "\" not found in type vector");
		}
		// --- update type vector ---
		globalTopicTypesV.setElementAt(type, i);
		// --- update type hashtable ---
		globalTopicTypes.put(type.getID(), type);
	}

	private void updateAssociationType(PresentationType type) {
		//
		PresentationType oldType = getAssociationType(type.getID());
		// error check
		if (oldType == null) {
			throw new DeepaMehtaException("association type \"" + type.getID() + "\" is unknown");
		}
		//
		int i = globalAssocTypesV.indexOf(oldType);
		// error check
		if (i == -1) {
			throw new DeepaMehtaException("association type \"" + type.getID() + "\" not found in type vector");
		}
		// --- update type vector ---
		globalAssocTypesV.setElementAt(type, i);
		// --- update type hashtable ---
		globalAssocTypes.put(type.getID(), type);
	}

	// ---

	private void addUpdateGeometry(PresentationTopic topic, String topicmapID, Hashtable updateGeometry) {
		updateGeometry.put(topicmapID + ":" + VIEWMODE_USE + ":" + topic.getID(), topic);
	}

	// ---

	/**
	 * @see		#processDirectives
	 */
	private void updateGeometry(Hashtable updateGeometry) {
		Enumeration e = updateGeometry.keys();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			//
			StringTokenizer st = new StringTokenizer(key, ":");
			String topicmapID = st.nextToken();
			String viewmode = st.nextToken();
			String topicID = st.nextToken();
			PresentationTopicMap topicmap = getEditor(topicmapID).getTopicMap();
			PresentationTopic topic = (PresentationTopic) updateGeometry.get(key);
			//
			Hashtable topics = new Hashtable();
			topics.put(topicID, topic);
			nodesMoved(topicmap, topics);
		}
	}

	// ---

	/**
	 * Registered callback.
	 * Triggered when the user closes the main window (see {@link #createMainWindow}).
	 *
	 * @see		DeepaMehtaClient#stop
	 */
	void close() {
		storeProperties();
        // Note: if the main window shows the error GUI the application service is not available 
		if (as != null) {
            as.logout();
        }
        //
		System.out.println("--- presentation service stopped ---");
		if (!runsAsApplet) {
			System.exit(0);
		}
	}	

	// ---

	/**
	 * @see		#createErrorGUI
	 */
	private void addTextToPanel(String text, Container p, int charWidth) {
		int pos;
		while (text.length() > charWidth && (pos = text.lastIndexOf(' ', charWidth)) != -1) {
			p.add(new JLabel(text.substring(0, pos)));
			text = text.substring(pos + 1);
		}
		p.add(new JLabel(text));
	}

	// ---

	/**
	 * @see		#createTopicMapEditor
	 */
	private ImageIcon editorIcon(PresentableTopic topicmapMetadata) {
		String iconfile = topicmapMetadata.getAppearanceParam();
		return getIcon(FILESERVER_ICONS_PATH, iconfile);
	}



	// *********************
	// *** Inner Classes ***
	// *********************



	// ### Note: a thread is used because creating main GUI is a lengthy operation -- otherwise
	// the "Login OK" message will not become visible
	private class GUIBuilder extends Thread {

		private Directives directives;

		GUIBuilder(Directives directives) {
			this.directives = directives;
			setPriority(Thread.MIN_PRIORITY);
			start();
		}

		/**
		 * ### Creates the fileserver connection and the messaging connection, then creates
		 * main GUI.
		 * <p>
		 * Performed for normal login as well as demo login.
		 *
		 * @see		#startSession
		 * @see		#startDemo
		 */
		public void run() {
			if (runsAsApplet) {
				createMainWindow(getClientName());
			}
			setWaitCursor();
			//
			createMainGUI(directives);
			//
			setDefaultCursor();
		}
	}

	private class GraphPanelTransferHandler extends TransferHandler {

		// --- import methods ---

		public boolean canImport(JComponent c, DataFlavor[] flavors) {
			return true;
		}

		public boolean importData(JComponent c, Transferable t) {
			try {
				DataFlavor[] flavors = t.getTransferDataFlavors();
				boolean hasStringFlavor = t.isDataFlavorSupported(DataFlavor.stringFlavor);
				boolean hasImageFlavor = t.isDataFlavorSupported(DataFlavor.imageFlavor);
				boolean hasFilelistFlavor = t.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
				String os = java.lang.System.getProperties().getProperty("os.name");
				//
				System.out.println(">>> data dropped to graph panel (" + flavors.length + " flavors) from " + os);
				System.out.println("  >   string flavor supported: " + hasStringFlavor);
				System.out.println("  >    image flavor supported: " + hasImageFlavor);
				System.out.println("  > filelist flavor supported: " + hasFilelistFlavor);
				// 
				if(os.equals("Linux")) {
					// linux
					reader:
					for (int zz = 0; zz < flavors.length; zz++) {
						// checks if DataFlavor is a subclass of java.io.Reader
						if (flavors[zz].isRepresentationClassReader()) {
							DataFlavor bestFlavor = DataFlavor.selectBestTextFlavor(flavors);
							// System.out.println("	flavor check, says the best result comes with: " +
							//	"" + bestFlavor.getMimeType() + " instead of: " + flavors[zz].getMimeType());
							Reader reader = bestFlavor.getReaderForText(t);
							BufferedReader br = new BufferedReader(reader);
							File[] files = createFileArray(br);
							StringBuffer cmdBuf = new StringBuffer(CMD_PROCESS_FILELIST);
							System.out.println("    " + files.length + " files:");
							for (int i = 0; i < files.length; i++) {
								File file = (File) files[i];
								System.out.println("    " + file);
								cmdBuf.append(COMMAND_SEPARATOR);
								cmdBuf.append(file);
							}
							processGraphCommand(getTopicmap(), cmdBuf.toString());
							break reader;
						}
					}
					return true;
				} else {
					// just not linux
					if (hasImageFlavor) {
						// ### not yet implemented
					}
					if (hasStringFlavor) {
						String str = (String) t.getTransferData(DataFlavor.stringFlavor);
						System.out.println("    string=\"" + str + "\"");
						processGraphCommand(getTopicmap(), CMD_PROCESS_STRING + COMMAND_SEPARATOR + str);
					} else if (hasFilelistFlavor) {
						java.util.List files = (java.util.List) t.getTransferData(DataFlavor.javaFileListFlavor);
						System.out.println("    " + files.size() + " files:");
						StringBuffer cmdBuf = new StringBuffer(CMD_PROCESS_FILELIST);
						for (int i = 0; i < files.size(); i++) {
							File file = (File) files.get(i);
							System.out.println("    " + file);
							cmdBuf.append(COMMAND_SEPARATOR);
							cmdBuf.append(file);
						}
						processGraphCommand(getTopicmap(), cmdBuf.toString());
					}
					return true;
				}
            } catch (UnsupportedFlavorException ufe) {
                System.out.println("*** while dropping to graph panel: " + ufe);
            } catch (IOException ioe) {
                System.out.println("*** while dropping to graph panel: " + ioe);
            }
			//
			return super.importData(c, t);
		}
		
		/** 
		 *  Adopted from http://iharder.sourceforge.net/current/java/filedrop/ licensed under GPL
		 *  BEGIN 2007-09-12 Nathan Blomquist -- Linux (KDE/Gnome) support added.
		 *  KDE seems to append a 0 char to the end of the reader
		 */
		private String ZERO_CHAR_STRING = "" + (char)0;
		private File[] createFileArray(BufferedReader bReader)
		{
			try { 
				java.util.List list = new java.util.ArrayList();
				java.lang.String line = null;
				while ((line = bReader.readLine()) != null) {
					try {
						// kde
						if(ZERO_CHAR_STRING.equals(line)) continue; 
						java.io.File file = new java.io.File(new java.net.URI(line));
						list.add(file);
					} catch (Exception ex) {
						System.out.println("Error with " + line + ": " + ex.getMessage());
					}
				}
				return (java.io.File[]) list.toArray(new File[list.size()]);
			} catch (IOException ex) {
				System.out.println("GraphPanelTransferHandler@createFileArray: IOException");
			}
			return new File[0];
		}
		/** 
		 * END 2007-09-12 Nathan Blomquist -- Linux (KDE/Gnome) support added.
		 */
	}

	private class QueuedRequest {

		private int type;
		private File file;
		private String filename;
		
		// only used for requets
		//		FS_REQUEST_DOWNLOAD_FILE
		private long lastModified;
		
		// only used for requets
		//		FS_REQUEST_UPLOAD_FILE
		//		FS_REQUEST_COPY_FILE
		private int filetype;

		// only used for requets
		//		FS_REQUEST_QUEUE_MESSAGE
		private String message;

		// only used for requets
		//		FS_REQUEST_QUEUE_DIRECTIVES
		private PresentationDirectives directives;

		// --- Constructors ---

		/**
		 * @see		#queueCopyRequest
		 */
		QueuedRequest(int type, File file, int filetype) {
			this.type = type;
			this.filename = file.getName();
			this.file = file;
			this.filetype = filetype;
		}

		/**
		 * @see		#queueDownloadRequest
		 * @see		#queueUploadRequest
		 */
		QueuedRequest(int type, String filename, int filetype, long lastModified) {
			this.type = type;
			this.filename = filename;
			this.file = new File(FileServer.repositoryPath(filetype) + filename);
			this.filetype = filetype;
			this.lastModified = lastModified;
		}

		/**
		 * @see		#queueMessage
		 */
		QueuedRequest(int type, String param) {
			this.type = type;
			this.message = param;
		}

		/**
		 * @see		#queueDirectives
		 */
		QueuedRequest(int type, PresentationDirectives directives) {
			this.type = type;
			this.directives = directives;
		}

		// --- Methods ---

		long getFilesize() {
			return file.length();
		}

		int getBlockCount() {
			return (int) ((getFilesize() + FILE_BUFFER_SIZE - 1) / FILE_BUFFER_SIZE);
		}
	}
}
