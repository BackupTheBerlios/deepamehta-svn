package de.deepamehta;

import java.awt.Color;
import java.util.Vector;



/**
 * A collection of constants.
 * <p>
 * There are two types of constants:
 * <ol>
 * <li>Settings
 * <p>
 * These constants are meant to be changed to configure the server, e.g. port number,
 * log status, max number of clients, default database host, used JDBC-driver as well as
 * the client e.g. default server, labels appearing in GUI, default geometry and colors
 * ...
 * <p>
 * <li>Symbolic constants
 * <p>
 * These constants are not meant to be changed. They serve as symbolic to make the
 * sourcecode both easier to write and easier to read. E.g. crucial for application
 * programmers are the {@link #DIRECTIVE_SHOW_TOPIC DIRECTIVE_XXX} constants which
 * denotes the existing client directives.
 * </ol>
 * <p>
 * <hr>
 * Last change: 25.1.2009 (2.0b9)<br>
 * J&ouml;rg Richter<br>
 * jri@deepamehta.de
 */
public interface DeepaMehtaConstants {



	// ----------------
	// --- Settings ---
	// ----------------



	// --- diagnosis log switches ---
	static final boolean LOG_REQUESTS = true;		// server
	static final boolean LOG_CM = false;			// server
	static final boolean LOG_CM_QUERIES = false;	// server
	static final boolean LOG_LCM = false;			// server
	static final boolean LOG_CDS = false;			// server
	static final boolean LOG_GEOM = false;			// server
	static final boolean LOG_TOPIC_INIT = false;	// server
	static final boolean LOG_IMPORT_EXPORT = false;	// server
	static final boolean LOG_PW = false;			// server
	static final boolean LOG_TIMING = false;		// server & client
	static final boolean LOG_MAPS = false;			// server & client
	static final boolean LOG_FILESERVER = true;		// server & client
	static final boolean LOG_TYPES = true;			// server & client
	static final boolean LOG_MEM_STAT = false;		// server & client
	// --- switches ---
	static final boolean VERSIONING = false;		// server
	static final boolean PLAY_SOUNDS = true;		// client
	// --- configuration management ---
	static final String CLIENT_VERSION = "2.0b8";
	static final String SERVER_VERSION = "2.0b8";
	static final String REQUIRED_SERVER_VERSION = "2.0b8";
	static final int REQUIRED_STANDARD_TOPICS = 19;
	static final int REQUIRED_DB_MODEL = 2;
	static final int REQUIRED_DB_CONTENT = 20;
	// --- application server ---
	static final int DEFAULT_PORT = 7557;			// used by DeepaMehtaClient
	static final int MAX_CLIENTS = 150;
	static final String ACTIVE_TOPIC_PACKAGE = "de.deepamehta.topics";
	static final String ACTIVE_ASSOC_PACKAGE = "de.deepamehta.assocs";
	static final String CONTAINER_SUFFIX_NAME = "Search";

	// --- properties ---
	static final String PROPERTY_LANGUAGE = "Language";
	static final String PROPERTY_SERVER_NAME = "Server Name";
	static final String PROPERTY_CLIENT_NAME = "Client Name";
	static final String PROPERTY_INSTALLATION = "Active";
	static final String PROPERTY_CORPORATE_ICON = "Corporate Icon";
	static final String PROPERTY_CUSTOMER_ICON = "Customer Icon";
	static final String PROPERTY_USERNAME = "Username";
	static final String PROPERTY_PASSWORD = "Password";
	static final String PROPERTY_OWNER_ID = "Owner ID";
	static final String PROPERTY_NAME = "Name";
	static final String PROPERTY_DESCRIPTION = "Description";
	static final String PROPERTY_TEXT = "Text";
	static final String PROPERTY_DOMAIN_INFORMATION = "Domain Information";
	static final String PROPERTY_TYPE_DESCRIPTION_QUERY = "Description Query";
	static final String PROPERTY_EMAIL_ADDRESS = "Email Address";
	static final String PROPERTY_MAILBOX_URL = "Mailbox URL";
	static final String PROPERTY_SUBJECT = "Subject";
	static final String PROPERTY_FROM = "From";
	static final String PROPERTY_TO = "To";
	static final String PROPERTY_STATUS = "Status";
	static final String PROPERTY_RECIPIENT_TYPE = "Recipient Type";
	static final String PROPERTY_DATE = "Date";
	static final String PROPERTY_BEGIN_DATE = "Begin Date";
	static final String PROPERTY_BEGIN_TIME = "Begin Time";
	static final String PROPERTY_END_DATE = "End Date";
	static final String PROPERTY_END_TIME = "End Time";
	static final String PROPERTY_LAST_REPLY_DATE = "Last Reply Date";
	static final String PROPERTY_LAST_REPLY_TIME = "Last Reply Time";
	static final String PROPERTY_BEGIN = "Begin";
	static final String PROPERTY_DURATION = "Duration";
	static final String PROPERTY_WEB_ALIAS = "Web Alias";
	static final String PROPERTY_YOUR_REMARK = "Your Remark";
	static final String PROPERTY_CHAT_FLOW = "Chat Flow";
	static final String PROPERTY_UNIQUE_TOPIC_NAMES = "Unique Topic Names";
	static final String PROPERTY_ICON = "Icon";
	static final String PROPERTY_CREATION_ICON = "Creation Icon";
	static final String PROPERTY_EDIT_PROPERTY_ICON = "Edit Icon";
	static final String PROPERTY_COLOR = "Color";
	static final String PROPERTY_FILE = "File";
	static final String PROPERTY_URL = "URL";
	static final String PROPERTY_PLURAL_NAME = "Plural Name";
	static final String PROPERTY_FIRST_NAME = "First Name";
	static final String PROPERTY_GENDER = "Gender";
	static final String PROPERTY_BIRTHDAY = "Birthday";
	static final String PROPERTY_STREET = "Street";
	static final String PROPERTY_POSTAL_CODE = "Postal Code";
	static final String PROPERTY_IMPLEMENTATION = "Custom Implementation";
	static final String PROPERTY_DISABLED = "Disabled";
	static final String PROPERTY_HIDDEN_TOPIC_NAMES = "Hidden Topic Names";
	static final String PROPERTY_LOCKED_GEOMETRY = "Locked Geometry";
	static final String PROPERTY_BACKGROUND_IMAGE = "Background Image";
	static final String PROPERTY_BACKGROUND_COLOR = "Background Color";
	static final String PROPERTY_TRANSLATION_USE = "Translation";
	static final String PROPERTY_VISUALIZATION = "Visualization";
	static final String PROPERTY_DEFAULT_VALUE = "Default Value";
	static final String PROPERTY_ORDINAL_NUMBER = "Ordinal Number";
	static final String PROPERTY_ACCESS_PERMISSION = "Access Permission";
	static final String PROPERTY_CARDINALITY = "Cardinality";
	static final String PROPERTY_ASSOCIATION_TYPE_ID = "Association Type ID";	// "Relation"
	static final String PROPERTY_WEB_INFO = "Web Info";							// "Relation"
	static final String PROPERTY_WEB_FORM = "Web Form";							// "Relation"
	static final String PROPERTY_PUBLIC = "Public";
	static final String PROPERTY_DEFAULT_WORKSPACE = "Default";
	static final String PROPERTY_CW_BASE_URL = "Base URL";						// "CorporateWeb Settings"
	static final String PROPERTY_SMTP_SERVER = "SMTP Server";					// "CorporateWeb Settings"
	static final String PROPERTY_GOOGLE_KEY = "Google Key";						// "CorporateWeb Settings"
	static final String PROPERTY_SEARCH = "Search";								// "Search"
	static final String PROPERTY_QUERY_ELEMENTS = "QueryElements";				// "Search"
	static final String PROPERTY_ELEMENT_COUNT = "ElementCount";				// "Search"
	static final String PROPERTY_RELATED_TOPIC_ID = "RelatedTopicID";			// "Search"
	static final String PROPERTY_RELATED_TOPIC_SEMANTIC = "AssociationTypeID";	// "Search"
	static final String PROPERTY_RESULT = "Result";								// "Search"
	static final String PROPERTY_ROLE_EDITOR = "Editor";						// "Membership"
	static final String PROPERTY_ROLE_PUBLISHER = "Publisher";					// "Membership"

	// --- property values ---
	static final String PERMISSION_VIEW = "view";
	static final String PERMISSION_CREATE = "create";
	static final String PERMISSION_CREATE_IN_WORKSPACE = "create in workspace";
	static final String SWITCH_ON = "on";
	static final String SWITCH_OFF = "off";
	static final String WEB_INFO_TOPIC_NAME = "Related Topic Name";
	static final String WEB_INFO = "Related Info";
	static final String WEB_INFO_DEEP = "Deeply Related Info";
	static final String WEB_FORM_TOPIC_SELECTOR = "Related Topic Selector";
	static final String WEB_FORM = "Related Form";
	static final String WEB_FORM_DEEP = "Deeply Related Form";
	static final String GENDER_MALE = "Male";
	static final String GENDER_FEMALE = "Female";
	static final String RECIPIENT_TYPE_TO = "To";
	static final String RECIPIENT_TYPE_CC = "Cc";
	static final String RECIPIENT_TYPE_BCC = "Bcc";
	//
	static final String COMMAND_SEPARATOR = "|";	// Note: was ":", but appears in URLs and Windows-Paths
	static final String VALUE_NOT_SET = "-";
	static final String DATE_SEPARATOR = "/";
	static final String TIME_SEPARATOR = ":";
	static final String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
	static final String[] monthNamesLong = {"January", "February", "March", "April", "May", "June", "July",
											"August", "September", "October", "November", "December"};
	static final String[] dayNames = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
	static final int YEAR_MIN = 1850;
	static final int YEAR_MAX = 2014;
	static final String PARAM_SEPARATOR = "_";		// ### was "." clashed with "Course No."
	static final String LEVEL_SEPARATOR = ":";
	static final String PARAM_RELATION = "Rel";

	// --- file service ---
	static final String FILE_REPOSITORY_PATH = "deepamehta-files/";
	static final String FILESERVER_DOCUMENTS_PATH = "documents/";
	static final String FILESERVER_ICONS_PATH = "icons/";
	static final String FILESERVER_IMAGES_PATH = "images/";
	static final String FILESERVER_BACKGROUNDS_PATH = "backgrounds/";
	static final String FILESERVER_SOUNDS_PATH = "sounds/";
	static final String FILESERVER_WEBPAGES_PATH = "webpages/";
	//
	static final int FILE_BUFFER_SIZE = 32768;
	// --- webcrawler ---
	static final int WEBCRAWLER_NICENESS = 30;		// sleep time between downloads in seconds
	// --- storage ---
	static final int MAX_ID_LENGTH = 40;
	static final int MAX_NAME_LENGTH = 255;
	// --- presentation ---
	static final String SERVER_DEFAULT_HOST = "localhost";	// only used when run as application
	static final int WIDTH_WINDOW = 960;
	static final int HEIGHT_WINDOW = 580;
	static final int WIDTH_LOGIN = 300;
	static final int HEIGHT_LOGIN = 105;
	static final int CONSOLE_WIDTH = 600;
	static final int CONSOLE_HEIGHT = 280;
	static final int WORKSPACES_HEIGHT = 195;
	static final int WIDTH_VIEW_CONTROLS = 232;
	static final int TEXT_EDITOR_WIDTH = 300;
	static final int TEXT_EDITOR_HEIGHT = 200;
	static final int INPUT_LINE_WIDTH = 220;
	static final int INPUT_LINE_HEIGHT = 64;
	static final int MAX_REVEALING = 7;	// cognitive limit 1
	static final int MAX_LISTING = 300;	// cognitive limit 2 ### was 150
	static final int NEW_TOPIC_X = 20;	// ### to be dropped
	static final int NEW_TOPIC_Y = 20;	// ### to be dropped
	static final int NEAR_MIN = 40;
	static final int NEAR_MAX = 100;
	static final int FREE_MIN = 20;
	static final int FREE_MAX = 80;
	static final int IMAGE_SIZE = 20;
	static final int ICON_SIZE = 12;	// should be an even value ### still required?
	static final int TOPIC_BORDER = (IMAGE_SIZE - ICON_SIZE) / 2;
	// colors
	static final Color EDGE_COLOR = Color.gray;				// edge in progress color
	static final Color TEXT_COLOR = Color.black;
	static final Color COLOR_DARK_SHADOW = new Color(104, 104, 88);
	static final Color COLOR_SELECTION = new Color(168, 168, 152);
	static final Color DEFAULT_BGCOLOR = new Color(232, 232, 216);			// default background color
	static final Color COLOR_VIEW_BGCOLOR = new Color(248, 248, 232);
	static final Color COLOR_PROPERTY_PANEL = new Color(224, 224, 208);		// default background color of property panel
	static final Color DEFAULT_BGCOLOR_MESSAGE = new Color(232, 232, 232);	// default background color of message panel
	static final String COLOR_DEFAULT = "#D8D8D8";			// default topic/association color
	static final String DEFAULT_VIEW_BGCOLOR = "#F8F8E8";	// default topicmap background color
	static final String DEFAULT_BGCOLOR_DESIGN = "#E8E8F8";	// default topicmap background color (build mode)
	// fonts
	static final int FONT_COUNT = 3;
	static final int FONT_SIZES[] = {9, 10, 12};
	// labels
	static final String MIME_CONF_MAPNAME = "MIME Configuration";
	static final String EMAIL_MAP_NAME = "Mails";				// ### to be dropped
	static final String START_DEMO_LABEL = "Start Demo";
	// message panel
	static final String[] NOTIFICATION_ICONS = {"empty.gif", "info.gif", "warning.gif", "error.gif"};
	static final int NOTIFICATION_COUNT = 3;
	static final int NOTIFICATION_RATE = 1000;
	// audio feedback
	static final String[] NOTIFICATION_SOUNDS = {"ip.au", "bird.au", "bird.au", "bird.au"};
	// graph border
	static final String[] BORDER_IMAGES = {"up.gif", "down.gif", "left.gif", "right.gif"};
	static final int BORDER_IMAGE_WIDTH = 84;
	static final int BORDER_IMAGE_HEIGHT = 12;
	static final int AUTO_SCROLL_DISTANCE = 20;					// ### not yet used
	// text editor controls
	static final String[] TEXT_EDITOR_IMAGES = {"bold.gif", "italic.gif", "underline.gif"};
	// search topics
	static final String BUTTON_REVEAL_ALL = "button-revealall.png";

	static final String[][] strings = {
		// --- Commands ---
		{"Create", "Erzeugen"},
		{"Create Topic Type ...", "Topictyp erzeugen ..."},
		{"Create Association Type ...", "Assoziationstyp erzeugen ..."},
		{"Create \\1", "\\1 erzeugen"},
		{"Delete", "Löschen"},
		{"Delete", "Löschen"},
		{"Remove", "Entfernen"},
		{"Remove", "Entfernen"},
		{"Hide", "Ausblenden"},
		{"Hide", "Ausblenden"},
		{"Hide all", "Alle ausblenden"},
		{"Close", "Schließen"},
		{"Publish to", "Freigeben für"},
		{"Import Topic Map ...", "Topic Map importieren ..."},
		{"Export", "Exportieren"},
		{"Export Format", "Export-Format"},
		{"What's related?", "Assoziationen anzeigen"},
		{"by Topic Type", "Topictypen"},
		{"by Association Type", "Assoziationstypen"},
		{"Search", "Suchen"},
		{"Search", "Suchen"},
		{"Rename", "Umbenennen"},
		{"Name It", "Benennen"},
		{"Retype", "Umtypen"},
		{"Retype", "Umtypen"},
		{"Set \\1", "\\1 setzen"},
		{"Edit \\1", "\\1 bearbeiten"},
		{"Show \\1", "\\1 anzeigen"},
		{"Choose \\1", "\\1 auswählen"},
		{"Assign \\1", "\\1 zuordnen"},
		{"Create \\1", "\\1 erzeugen"},
		{"What is a \"\\1\"?", "Hilfe zu \"\\1\""},
		{"Show Result", "Ergebnis anzeigen"},
		{"Group by", "Gruppieren nach"},
		{"Import Corporate Memory ...", "Corporate Memory importieren ..."},
		{"Export Corporate Memory", "Corporate Memory exportieren"},
		{"Google \"\\1\"", "Google \"\\1\""},
		// --- GUI ---
		{"Username", "Benutzer"},
		{"Password", "Passwort"},
		{"Type", "Typ"},
		{"Type", "Typ"},
		{"Use", "Inhalt"},
		{"Build", "Struktur"},
		{"Messages", "Mitteilungen"},
		{"Choose ...", "Auswählen ..."},
		{"Choose ...", "Auswählen ..."},
		// --- "Workspace" commands ---
		{"Join", "Beitreten"},
		{"Leave", "Austreten"},
		{"Assign Topic Type", "Topictyp zuordnen"},
		{"Assign Association Type", "Assoziationstyp zuordnen"},
		// --- "User" commands ---
		{"Set Standard Workspace", "Standard Workspace setzen"},
		// --- "Email" commands ---
		{"Compose Email", "Email verfassen"}
	};

	static final int ITEM_NEW_TOPIC    = 0;
	static final int ITEM_NEW_TOPIC_TYPE = 1;
	static final int ITEM_NEW_ASSOC_TYPE = 2;
	static final int ITEM_CREATE_IN_WORKSPACE = 3;
	static final int ITEM_DELETE_TOPIC = 4;
	static final int ITEM_DELETE_ASSOC = 5;
	static final int ITEM_REMOVE_TOPIC = 6;
	static final int ITEM_REMOVE_ASSOC = 7;
	static final int ITEM_HIDE_TOPIC = 8;
	static final int ITEM_HIDE_ASSOC   = 9;
	static final int ITEM_HIDE_ALL     = 10;
	static final int ITEM_CLOSE_VIEW   = 11;
	static final int ITEM_PUBLISH = 12;
	static final int ITEM_IMPORT_TOPICMAP = 13;
	static final int ITEM_EXPORT_TOPICMAP = 14;
	static final int ITEM_PREFERENCES_EXPORT = 15;
	static final int ITEM_NAVIGATION = 16;
	static final int ITEM_NAVIGATION_BY_TOPIC = 17;
	static final int ITEM_NAVIGATION_BY_ASSOCIATION = 18;
	static final int ITEM_SEARCH_BY_TOPICTYPE = 19;
	static final int ITEM_SEARCH_BY_PROPERTY = 20;
	static final int ITEM_CHANGE_TOPIC_NAME = 21;
	static final int ITEM_SET_TOPIC_NAME = 22;
	static final int ITEM_CHANGE_TOPIC_TYPE = 23;
	static final int ITEM_CHANGE_ASSOC_TYPE = 24;
	static final int ITEM_SET_PROPERTY = 25;
	static final int ITEM_EDIT_PROPERTY = 26;
	static final int ITEM_VIEW_PROPERTY = 27;
	static final int ITEM_CHOOSE_FILE_PROPERTY = 28;
	static final int ITEM_ASSIGN_TOPIC = 29;
	static final int ITEM_ASSIGN_NEW_TOPIC = 30;
	static final int ITEM_SHOW_HELP = 31;
	// ### static final int ITEM_SHOW_CONTENT = 32;		// ### not used anymore
	static final int ITEM_GROUP_BY = 33;
	static final int ITEM_IMPORT_CM = 34;
	static final int ITEM_EXPORT_CM = 35;
	static final int ITEM_SEARCH_INTERNET = 36;
	// --- GUI ---
	static final int LABEL_USERNAME = 37;
	static final int LABEL_PASSWORD = 38;
	static final int LABEL_TOPIC_TYPE = 39;
	static final int LABEL_ASSOC_TYPE = 40;
	static final int VIEWMODE_USE_LABEL = 41;
	static final int VIEWMODE_BUILD_LABEL = 42;
	static final int MESSAGE_LABEL = 43;
	static final int BUTTON_ASSIGN_FILE = 44;
	static final int BUTTON_CHOOSE_COLOR = 45;
	// --- "Workspace" commands ---
	static final int ITEM_JOIN_WORKSPACE = 46;
	static final int ITEM_LEAVE_WORKSPACE = 47;
	static final int ITEM_ASSIGN_TOPIC_TYPE = 48;
	static final int ITEM_ASSIGN_ASSOC_TYPE = 49;
	// --- "User" commands ---
	static final int ITEM_SET_WORKSPACE = 50;
	// --- "Email" commands ---
	static final int ITEM_COMPOSE_EMAIL = 51;
	//
	static final String ICON_CHANGE_ASSOC_TYPE = "changing.gif";
	static final String ICON_NEW_ASSOC_TYPE = "create.gif";
	static final String ICON_HIDE_ASSOC = "eye-closed.gif";
	static final String ICON_DELETE_ASSOC = "trash.gif";
	static final String ICON_NEW_TOPIC = "create.gif";
	static final String ICON_HIDE_ALL = "eye-obscured.gif";
	static final String ICON_CLOSE_VIEW = "eye-closed.gif";
	static final String ICON_PUBLISH = "publishing.gif";
	static final String ICON_IMPORT_TOPICMAP = "import.gif";
	static final String ICON_EXPORT_TOPICMAP = "export.gif";
	static final String ICON_PREFERENCES = "settings.gif";
	static final String ICON_NEW_TOPIC_TYPE = "create.gif";
	static final String ICON_NAVIGATION = "eye-grey.gif";
	static final String ICON_SEARCH_BY_TOPICTYPE = "eye-grey.gif";
	static final String ICON_SEARCH_BY_PROPERTY = "show.gif";
	static final String ICON_CHANGE_TOPIC_NAME = "rename.gif";
	static final String ICON_CHANGE_TOPIC_TYPE = "retype.gif";
	static final String ICON_HIDE_TOPIC = "eye-closed.gif";
	static final String ICON_DELETE_TOPIC = "trash.gif";
	static final String ICON_SHOW_HELP = "viewhilfe.gif";
	static final String ICON_SEARCH_INTERNET = "google.gif";
	static final String ICON_GROUP_BY = "eye-grey.gif";
	static final String ICON_COMPOSE_EMAIL = "composeEmail.gif";

	// --- commands ---
	static final String CMD_NAVIGATION_BY_TOPIC = "navByTopic";
	static final String CMD_NAVIGATION_BY_ASSOCIATION = "navByAssoc";
	static final String CMD_SEARCH_BY_TOPICTYPE = "searchByTopicType";
	static final String CMD_CHANGE_TOPIC_NAME = "changeTopicName";		// ### opens detail
	static final String CMD_CHANGE_TOPIC_TYPE = "changeTopicType";
	static final String CMD_CHANGE_ASSOC_TYPE = "changeAssociationType";
	static final String CMD_CHANGE_TOPIC_TYPE_BY_NAME = "changeTopicTypeByName";
	static final String CMD_CHANGE_ASSOC_TYPE_BY_NAME = "changeAssocTypeByName";
	static final String CMD_HIDE_TOPIC = "hideTopic";
	static final String CMD_HIDE_ASSOC   = "hideAssociation";
	static final String CMD_DELETE_TOPIC = "deleteTopic";
	static final String CMD_DELETE_ASSOC = "deleteAssociation";
	static final String CMD_DELETE_TOPICMAP = "deleteTopicmap";
	static final String CMD_SET_PROPERTY = "setProperty";				// changes model
	static final String CMD_ASSIGN_TOPIC = "assignTopic";
	static final String CMD_ASSIGN_NEW_TOPIC = "assignNewTopic";
	static final String CMD_SHOW_HELP = "showHelp";						// changes model
	static final String CMD_GROUP_BY = "groupByProperty";
	static final String CMD_IMPORT_CM = "importCM";
	static final String CMD_EXPORT_CM = "exportCM";
	static final String CMD_SEARCH_INTERNET = "searchInternet";
	static final String CMD_SUBMIT_FORM = "submitForm";
	static final String CMD_ASSIGN_ICON = "assignIcon";
	static final String CMD_ASSIGN_FILE = "assignFile";
	static final String CMD_ASSIGN_BACKGROUND = "assignBackground";
	static final String CMD_PUBLISH = "publishToGroup";
	static final String CMD_EXPORT_TOPICMAP = "exportTopicmap";
	static final String CMD_IMPORT_TOPICMAP = "importTopicmap";
	static final String CMD_SET_EXPORT_FORMAT = "setExportFormat";
	static final String CMD_NEW_TOPIC_TYPE = "newTopictype";
	static final String CMD_NEW_ASSOC_TYPE = "newAssoctype";
	static final String CMD_DEFAULT = "doubleClicked";
	static final String CMD_SELECT_TOPIC = "selectTopic";
	static final String CMD_SELECT_ASSOC = "selectAssoc";
	static final String CMD_SELECT_TOPICMAP = "selectTopicmap";
	static final String CMD_GET_TOPIC_COMMANDS = "getTopicCommands";
	static final String CMD_GET_ASSOC_COMMANDS = "getAssocCommands";
	static final String CMD_GET_VIEW_COMMANDS = "getViewCommands";
	static final String CMD_CREATE_TOPIC    = "createTopic";
	static final String CMD_CREATE_ASSOC    = "createAssoc";
	static final String CMD_HIDE_ALL     = "hideAll";
	static final String CMD_CLOSE_VIEW   = "closeView";
	static final String CMD_PROCESS_FILELIST = "processDroppedFiles";
	static final String CMD_PROCESS_STRING = "processDroppedString";
	static final String CMD_CHOOSE_COLOR = "chooseColor";
	static final String CMD_FOLLOW_HYPERLINK = "followHyperlink";
	static final String CMD_COMPOSE_EMAIL = "composeEmail";

	// hyperlink actions
	static final String ACTION_REVEAL_TOPIC = "revealTopic";
	static final String ACTION_REVEAL_ALL = "revealAll";



	// ----------------------
	// --- HTML Interface ---
	// ----------------------

	// CSS File
	// static final String DEEPAMEHTA_STYLE_FILE = "deepaMehtaStyle.css";

	// --- Layouts ---
    static final int LAYOUT_ROWS = 1;
    static final int LAYOUT_COLS = 2;
    
    // --- Layout Styles (for bean-based info generator) ---
    static final int LAYOUT_STYLE_2COLUMN = 1;
    static final int LAYOUT_STYLE_FLOW = 2;

	// --- Form Layout Settings ---
    static final int INPUTFIELD_WIDTH = 50;
    static final int TEXTAREA_WIDTH = 50;
    static final int TEXTAREA_HEIGHT = 5;
    // ### static final int MULTIPLE_SELECT_HEIGHT = 5;

	// --- Actions ---
	static final String ACTION_COLLAPSE_NODE = "collapseNode";
	static final String ACTION_EXTEND_NODE = "extendNode";
	static final String ACTION_NEXT_PAGE = "nextPage";
	static final String ACTION_PREV_PAGE = "prevPage";
	// task manager
    static final String ACTION_SELECT_CASE = "selectCase";
    static final String ACTION_SELECT_TOPIC = "selectTopic";
    static final String ACTION_PROCESS_TYPEFORM = "processTypeform";
    static final String ACTION_PROCESS_FREEFORM = "processFreeform";

	static Vector ALLOWED_TAGS = new Vector() {{
		addElement("b");
		addElement("i");
		addElement("u");
		addElement("a");
		addElement("ul");
		addElement("ol");
		addElement("li");
		addElement("code");
		addElement("blockquote");
	}};



	// -------------------
	// --- Topic Types ---
	// -------------------



	static final String TOPICTYPE_TOPIC = "tt-generic";
	static final String TOPICTYPE_TOPICMAP = "tt-topicmap";
	static final String TOPICTYPE_TOPICTYPE = "tt-topictype";
	static final String TOPICTYPE_ASSOCTYPE = "tt-assoctype";
	static final String TOPICTYPE_PROPERTY = "tt-property";
	static final String TOPICTYPE_PROPERTY_VALUE = "tt-constant";
	static final String TOPICTYPE_SEARCH = "tt-container";
	static final String TOPICTYPE_TOPIC_SEARCH = "tt-topiccontainer";
	static final String TOPICTYPE_DATASOURCE = "tt-datasource";
	static final String TOPICTYPE_WORKSPACE = "tt-workspace";
	static final String TOPICTYPE_USER = "tt-user";
	static final String TOPICTYPE_PERSON = "tt-person";
	static final String TOPICTYPE_INSTITUTION = "tt-institution";
	static final String TOPICTYPE_ADDRESS = "tt-address";
	static final String TOPICTYPE_PHONE_NUMBER = "tt-phonenumber";
	static final String TOPICTYPE_FAX_NUMBER = "tt-faxnumber";
	static final String TOPICTYPE_EMAIL_ADDRESS = "tt-emailaddress";
	static final String TOPICTYPE_EMAIL = "tt-email";
	static final String TOPICTYPE_RECIPIENT_LIST = "tt-recipientlist";
	static final String TOPICTYPE_CALENDAR = "tt-calendar";
	static final String TOPICTYPE_APPOINTMENT = "tt-event";	// topic type "Appointment" has ID "tt-event" for historical reasons
	static final String TOPICTYPE_EVENT = "tt-alldayevent";
	static final String TOPICTYPE_LOCATION = "tt-location";
	static final String TOPICTYPE_DOCUMENT = "tt-document";
	static final String TOPICTYPE_IMAGE = "tt-image";
	static final String TOPICTYPE_WEBPAGE = "tt-webpage";
	static final String TOPICTYPE_WEBSITE = "tt-website";
	static final String TOPICTYPE_INTERNET_DOMAIN = "tt-internetdomain";
	static final String TOPICTYPE_MESSAGE = "tt-message";
	static final String TOPICTYPE_MESSAGE_BOARD = "tt-messageboard";
	static final String TOPICTYPE_CHAT = "tt-chat";
	static final String TOPICTYPE_CHAT_BOARD = "tt-chatboard";
	static final String TOPICTYPE_INSTALLATION = "tt-installation";
	static final String TOPICTYPE_EXPORT_FORMAT = "tt-exportformat";
	static final String TOPICTYPE_DOCUMENT_TYPE = "tt-documenttype";



	// -------------------------
	// --- Association Types ---
	// -------------------------



	static final String ASSOCTYPE_GENERIC = "at-generic";
	static final String ASSOCTYPE_ASSOCIATION = "at-association";	// ### new type name: "Assignment", was: "Association"
	static final String ASSOCTYPE_AGGREGATION = "at-aggregation";
	static final String ASSOCTYPE_COMPOSITION = "at-composition";
	static final String ASSOCTYPE_DERIVATION = "at-derivation";
	static final String ASSOCTYPE_RELATION = "at-relation";
	static final String ASSOCTYPE_MEMBERSHIP = "at-membership";
	static final String ASSOCTYPE_PUBLISHING = "at-publishing";
	static final String ASSOCTYPE_PUBLISH_PERMISSION = "at-publishpermission";
	static final String ASSOCTYPE_VIEW_IN_USE = "at-viewinuse";
	static final String ASSOCTYPE_USES = "at-uses";
	static final String ASSOCTYPE_NAVIGATION = "at-navigation";
	static final String ASSOCTYPE_PREFERENCE = "at-preference";
	static final String ASSOCTYPE_GOOGLE_RESULT = "at-googleresult";
	// email feature
	static final String ASSOCTYPE_RECIPIENT = "at-recipient";
	static final String ASSOCTYPE_SENDER = "at-sender";
	static final String ASSOCTYPE_ATTACHMENT = "at-attachment";
  	// form-helptext
	static final String ASSOCTYPE_HELPTEXT = "at-form-helptext";



	// ----------------------------------------------
	// --- Semantic of standard Association Types ---
	// ----------------------------------------------



	// direction is from origin topic map to personal topic map
	static final String SEMANTIC_ORIGIN_MAP = ASSOCTYPE_DERIVATION;

	// direction is from personal topic map to workspace
	static final String SEMANTIC_ORIGIN_WORKSPACE = ASSOCTYPE_PUBLISHING;

	// *** User --> Workgroup, Workspace, Configuration Map ***

	// direction is from user to workspace
	static final String SEMANTIC_MEMBERSHIP = ASSOCTYPE_MEMBERSHIP;

	// direction is from user to MIME Configuration
	static final String SEMANTIC_CONFIGURATION_MAP = ASSOCTYPE_ASSOCIATION;

	// direction is from user to topic map
	static final String SEMANTIC_VIEW_IN_USE = ASSOCTYPE_VIEW_IN_USE;

	// direction is from authentification source to login topic
	static final String SEMANTIC_AUTHENTIFICATION_SOURCE = ASSOCTYPE_ASSOCIATION;

	// direction is from user resp. workspace to workspace topicmap
	static final String SEMANTIC_WORKSPACE_TOPICMAP = ASSOCTYPE_AGGREGATION;

	// *** Deployer (User resp. Workspace) --> Workspace Topicmap ***

	// direction is from user resp. workspace to workspace topicmap
	static final String SEMANTIC_WORKSPACE_TOPICMAP_DEPLOYER = ASSOCTYPE_AGGREGATION;

	// *** Workgroup resp. User --> Type ***

	// direction is from user resp. workspace to topic type
	static final String SEMANTIC_WORKSPACE_TYPES = ASSOCTYPE_USES;

	// direction is from workspace to association type
	static final String SEMANTIC_MEMBERSHIP_TYPE = ASSOCTYPE_ASSOCIATION;

	// *** Document Type --> MIME Type --> Application ***

	// direction is from MIME type (container) to document type (element)
	static final String SEMANTIC_MIMETYPE = ASSOCTYPE_AGGREGATION;

	// direction is from application (container) to MIME type (element)
	static final String SEMANTIC_APPLICATION = ASSOCTYPE_AGGREGATION;

	// ---

	// direction is from container type to content type
	static final String SEMANTIC_CONTAINER_TYPE = ASSOCTYPE_AGGREGATION;

    // direction is from supertype to type
    static final String SEMANTIC_TYPE_DERIVATION = ASSOCTYPE_DERIVATION;

	// direction is from type (container) to property (element)
	static final String SEMANTIC_PROPERTY_DEFINITION = ASSOCTYPE_COMPOSITION;

	// direction is from property (container) to property value (element)
	static final String SEMANTIC_OPTION_DEFINITION = ASSOCTYPE_COMPOSITION;

	// direction is from topic type to topic type
	static final String SEMANTIC_RELATION_DEFINITION = ASSOCTYPE_RELATION;

	// *** Workspace --> Owner (User resp. Workgroup) ***

	// direction is from data consumer to datasource
	static final String SEMANTIC_DATA_SOURCE = ASSOCTYPE_ASSOCIATION;

	// direction is from data consumer to datasource
	static final String SEMANTIC_DATA_CONSUMER = ASSOCTYPE_ASSOCIATION;

	// *** Programatically created container associations ***

	// direction is from container to subcontainer resp. to contained topics
	static final String SEMANTIC_CONTAINER_HIERARCHY = ASSOCTYPE_NAVIGATION;

	// *** Websearch Result ***

	// direction is from topic to webpage
	static final String SEMANTIC_WEBSEARCH_RESULT = ASSOCTYPE_GOOGLE_RESULT;

	// *** Preferences ***

	// direction is from user resp. workspace to somewhat
	static final String SEMANTIC_PREFERENCE = ASSOCTYPE_PREFERENCE;

	// ---

	// *** Message Board ***

	// direction is from message board to message resp. from message to reply
	static final String SEMANTIC_MESSAGE_HIERARCHY = ASSOCTYPE_ASSOCIATION;

	// direction is from message board to webpage
	static final String SEMANTIC_WEBPAGE = ASSOCTYPE_ASSOCIATION;

	// *** Chat Board ***

	// direction is from chat board to chat
	static final String SEMANTIC_CHAT = ASSOCTYPE_ASSOCIATION;

	// *** Calendar ***

	// direction is from calendar to person
	static final String SEMANTIC_CALENDAR_PERSON = ASSOCTYPE_ASSOCIATION;

	// direction is from appointment to location
	static final String SEMANTIC_APPOINTMENT_LOCATION = ASSOCTYPE_ASSOCIATION;

	// direction is from appointment to person
	static final String SEMANTIC_APPOINTMENT_ATTENDEE = ASSOCTYPE_ASSOCIATION;

	// *** Email ***

	// direction is from user to email address
	static final String SEMANTIC_EMAIL_ADDRESS = ASSOCTYPE_ASSOCIATION;

	// direction is from recipient list to recipient (person or institution)
	static final String SEMANTIC_SELECTED_RECIPIENT = ASSOCTYPE_ASSOCIATION;

	// direction is from email to recipient (person, institution, or recipient list)
	static final String SEMANTIC_EMAIL_RECIPIENT = ASSOCTYPE_RECIPIENT;

	// direction is from email to user
	static final String SEMANTIC_EMAIL_SENDER = ASSOCTYPE_SENDER;

	// direction is from email to document
	static final String SEMANTIC_EMAIL_ATTACHMENT = ASSOCTYPE_ATTACHMENT;



	// ----------------
	// --- Requests ---
	// ----------------



	// --- session control requests ---
	static final int REQUEST_LOGIN = 1;
	static final int REQUEST_LOGOUT = 2;
	// --- topic requests ---
	static final int REQUEST_EXEC_TOPIC_COMMAND = 3;
	static final int REQUEST_EXEC_TOPIC_COMMAND_CHAINED = 4;
	static final int REQUEST_PROCESS_TOPIC_DETAIL = 5;
	static final int REQUEST_SET_TOPIC_DATA = 6;
	// --- association requests ---
	static final int REQUEST_EXEC_ASSOC_COMMAND = 7;
	static final int REQUEST_EXEC_ASSOC_COMMAND_CHAINED = 8;
	static final int REQUEST_PROCESS_ASSOC_DETAIL = 9;
	static final int REQUEST_SET_ASSOC_DATA = 10;
	// --- topic map requests ---
	static final int REQUEST_SET_GEOMETRY = 11;		// ### should be a message
	static final int REQUEST_SET_TRANSLATION = 12;	// ### should be a message



	// ------------------
	// --- Directives ---
	// ------------------



	// The directives represent the possible behavoir of the thin DeepaMehta
	// client -- the presentation logik as determined by DeepaMehta's presentation
	// and user interaction paradigm.
	//
	// It is always the DeepaMehta server who sends directives to the client (means:
	// instructing the client to "present" some things). Mostly the server sends
	// directives synchronously to the client (as reply to a request), however
	// some directives may be send asynchronously to the client by means of the
	// messaging connection.

	public static final int DIRECTIVE_SHOW_TOPIC = 1;
	public static final int DIRECTIVE_SHOW_TOPICS = 2;
	public static final int DIRECTIVE_SHOW_ASSOCIATION = 3;
	public static final int DIRECTIVE_SHOW_ASSOCIATIONS = 4;
	public static final int DIRECTIVE_HIDE_TOPIC = 5;
	public static final int DIRECTIVE_HIDE_TOPICS = 6;	// ### not yet complete, currently not used
	public static final int DIRECTIVE_HIDE_ASSOCIATION = 7;
	public static final int DIRECTIVE_HIDE_ASSOCIATIONS = 8;
	public static final int DIRECTIVE_SELECT_TOPIC = 9;
	public static final int DIRECTIVE_SELECT_ASSOCIATION = 10;
	public static final int DIRECTIVE_SELECT_TOPICMAP = 11;
	public static final int DIRECTIVE_UPDATE_TOPIC_TYPE = 12;
	public static final int DIRECTIVE_UPDATE_ASSOC_TYPE = 13;
	public static final int DIRECTIVE_SHOW_TOPIC_PROPERTIES = 14;
	public static final int DIRECTIVE_SHOW_ASSOC_PROPERTIES = 15;
	public static final int DIRECTIVE_FOCUS_TYPE = 16;
	public static final int DIRECTIVE_FOCUS_NAME = 17;	// ### not required anymore
	public static final int DIRECTIVE_FOCUS_PROPERTY = 18;
	public static final int DIRECTIVE_SET_TOPIC_TYPE = 19;
	public static final int DIRECTIVE_SET_TOPIC_NAME = 20;
	public static final int DIRECTIVE_SET_TOPIC_LABEL = 21;
	public static final int DIRECTIVE_SET_TOPIC_ICON = 22;
	public static final int DIRECTIVE_SET_TOPIC_GEOMETRY = 23;
	public static final int DIRECTIVE_SET_TOPIC_LOCK = 48;	// ### 48
	public static final int DIRECTIVE_SET_ASSOC_TYPE = 24;
	public static final int DIRECTIVE_SET_ASSOC_NAME = 25;
	public static final int DIRECTIVE_SHOW_MENU = 26;
	public static final int DIRECTIVE_SHOW_DETAIL = 27;
	public static final int DIRECTIVE_SHOW_WORKSPACE = 28;
	public static final int DIRECTIVE_SHOW_VIEW = 29;
	public static final int DIRECTIVE_SELECT_EDITOR = 30;
	public static final int DIRECTIVE_RENAME_EDITOR = 31;
	public static final int DIRECTIVE_CLOSE_EDITOR = 32;
	public static final int DIRECTIVE_SET_EDITOR_BGIMAGE = 33;
	public static final int DIRECTIVE_SET_EDITOR_BGCOLOR = 34;
	public static final int DIRECTIVE_SET_EDITOR_ICON = 35;
	public static final int DIRECTIVE_SHOW_MESSAGE = 36;
	public static final int DIRECTIVE_PLAY_SOUND = 37;
	public static final int DIRECTIVE_CHOOSE_FILE = 38;
	public static final int DIRECTIVE_COPY_FILE = 39;
	public static final int DIRECTIVE_DOWNLOAD_FILE = 40;
	public static final int DIRECTIVE_UPLOAD_FILE = 41;
	public static final int DIRECTIVE_SET_LAST_MODIFIED = 42;
	public static final int DIRECTIVE_OPEN_FILE = 43;
	public static final int DIRECTIVE_QUEUE_MESSAGE = 44;
	public static final int DIRECTIVE_QUEUE_DIRECTIVES = 45;
	public static final int DIRECTIVE_LAUNCH_APPLICATION = 46;
	public static final int DIRECTIVE_OPEN_URL = 47;
	public static final int DIRECTIVE_CHOOSE_COLOR = 49;
	


	// ---------------------
	// --- Message Types ---
	// ---------------------



	// ### public static final int MESSAGE_TEXT = 1;
	public static final int MESSAGE_DIRECTIVES = 2;



	// --------------------------------
	// --- Fileserver Request Types ---
	// --------------------------------



	static final int FS_REQUEST_UPLOAD_FILE = 1;
	static final int FS_REQUEST_DOWNLOAD_FILE = 2;
	static final int FS_REQUEST_COPY_FILE = 3;
	static final int FS_REQUEST_QUEUE_MESSAGE = 4;
	static final int FS_REQUEST_QUEUE_DIRECTIVES = 5;
	static final int FS_REQUEST_LOGOUT = 6;



	// --------------------------
	// --- Type Request Types ---
	// --------------------------



	static final int TYPE_REQUEST_TOPIC_TYPE = 1;
	static final int TYPE_REQUEST_ASSOC_TYPE = 2;
	static final int TYPE_REQUEST_LOGOUT = 3;



	// -----------------------------
	// --- Fileserver File Types ---
	// -----------------------------



	static final int FILE_DOCUMENT = 1;
	static final int FILE_ICON = 2;
	static final int FILE_IMAGE = 3;
	static final int FILE_BACKGROUND = 4;



	// ------------------------------
	// --- External Request Types ---
	// ------------------------------



	static final int EXTERNAL_REQUEST_REINIT = 1;
	static final int EXTERNAL_REQUEST_LOGOUT = 2;



	// -------------------
	// --- Login Modes ---
	// -------------------



	static final int LOGIN_USER = 1;
	static final int LOGIN_DEMO = 2;



	// ---------------------
	// --- Session Types ---
	// ---------------------



	static final int SESSION_JAVA_CLIENT = 1;
	static final int SESSION_WEB_INTERFACE = 2;



	// ------------------------
	// --- Connection Types ---
	// ------------------------



	static final int CONNECTION_INTERACTION = 1;	// synchronous request/reply
	static final int CONNECTION_FILESERVER = 2;		// background file transfers
	static final int CONNECTION_MESSAGING = 3;		// asynchronous messaging
	static final int CONNECTION_TYPE = 4;			// type information
	static final int CONNECTION_EXTERNAL = 5;		// external interface (server)



	// -------------------------
	// --- Connection States ---
	// -------------------------



	static final int STATE_OK = 1;
	static final int STATE_ERROR = 2;
	static final int STATE_TERMINATE = 3;



	// ---------------------
	// --- Service Types ---
	// ---------------------



	static final int SERVICE_EMBEDDED = 1;
	static final int SERVICE_TCP_SOCKET = 2;



	// ------------------
	// --- Menu Types ---
	// ------------------



	static final String MENU_TOPIC = "topic";
	static final String MENU_ASSOC = "assoc";
	static final String MENU_VIEW  = "view";



	// ------------------
	// --- View Modes ### ---
	// ------------------



	static final String VIEWMODE_USE      = "U";
	static final String VIEWMODE_BUILD    = "B";
	static final String VIEWMODE_HIDDEN   = "H";	// ### pseudo viewmode



	// -------------
	// --- Types ---
	// -------------



	static final String TYPE_TOPIC       = "T";
	static final String TYPE_ASSOCIATION = "A";

	static final int TYPE_TT = 1;
	static final int TYPE_AT = 2;



	// -----------------------
	// --- Selection Modes ---
	// -----------------------



	static final int SELECTED_NONE = 1;
	static final int SELECTED_TOPIC = 2;
	static final int SELECTED_ASSOCIATION = 3;
	static final int SELECTED_TOPICMAP  = 4;



	// --------------------
	// --- Detail Types ---
	// --------------------



	static final int DETAIL_TOPIC = 1;
	static final int DETAIL_ASSOCIATION = 2;
	static final int DETAIL_TOPICMAP = 3;



	// ----------------------------
	// --- Detail Content Types ---
	// ----------------------------



	// Note: To implement a further detail content type the following methods must be extended
	//  - Detail.Detail(DataInputStream in)
	//  - Detail.write(DataOutputStream out)
	//  - PresentationDetail.PresentationDetail()
	//  - PresentationDetail.isDirty()
	//  - PresentationDetail.updateModel()
	//  - PresentationDetail.createWindow()
	static final int DETAIL_CONTENT_NONE = 1;
	static final int DETAIL_CONTENT_TEXT = 2;
	static final int DETAIL_CONTENT_IMAGE = 3;
	static final int DETAIL_CONTENT_TABLE = 4;
	static final int DETAIL_CONTENT_HTML = 5;



	// -----------------
	// --- Languages ---
	// -----------------



	static final int LANGUAGE_ENGLISH = 0;
	static final int LANGUAGE_GERMAN = 1;



	// -------------------------
	// --- Topic Init Levels ---
	// -------------------------



	static final int INITLEVEL_1 = 1;
	static final int INITLEVEL_2 = 2;
	static final int INITLEVEL_3 = 3;



	// ------------------------
	// --- Appearance Modes ---
	// ------------------------



	static final int APPEARANCE_DEFAULT = 1;
	static final int APPEARANCE_CUSTOM_ICON = 2;
	static final int APPEARANCE_CUSTOM_COLOR = 3;	// ### not a true appearance mode
								// ### only used for types, types are not "presentable"



	// ---------------------
	// --- Command Types ---
	// ---------------------



	static final int TYPE_COMMAND = 1;
	static final int TYPE_COMMAND_SEPARATOR = 2;
	static final int TYPE_COMMAND_GROUP = 3;



	// ----------------------
	// --- Command States ---
	// ----------------------



	static final int COMMAND_STATE_DEFAULT = 0;
	static final int COMMAND_STATE_DISABLED = 1;
	static final int COMMAND_STATE_SELECTED = 2;
	static final int COMMAND_STATE_RADIOBUTTON = 4;
	static final int COMMAND_STATE_CHECKBOX = 8;



	// --------------------------
	// --- Notification Types ---
	// --------------------------



	// used in client.MessagePanel
	static final int NOTIFICATION_DEFAULT = 1;	// ### rename to NOTIFICATION_INFO
	static final int NOTIFICATION_WARNING = 2;
	static final int NOTIFICATION_ERROR = 3;



	// -------------------
	// --- Sound Types ---
	// -------------------



	static final int SOUND_NO_RESULT = 0;
	static final int SOUND_INFO = 1;
	static final int SOUND_WARNING = 2;
	static final int SOUND_ERROR = 3;



	// --------------------------------
	// --- Topicmap Editor Contexts ---
	// --------------------------------



	static final int EDITOR_CONTEXT_PERSONAL = 1;	// personal workspace topicmap 
	static final int EDITOR_CONTEXT_WORKGROUP = 2;	// shared workspace topicmap
	static final int EDITOR_CONTEXT_VIEW = 3;		// open topicmap



	// ----------------------
	// --- Relation Types ---
	// ----------------------



	public static final int ASSOC_1 = 1;	// for single foreign key relations
	public static final int ASSOC_N = 2;	// for relations via separate relation table
	public static final int ASSOC_X = 3;	// for relations where references are stored
											// as multiple attribute values

	/**
	 *  Separates multiple attribute values.
	 *
	 *  @see de.deepamehta.service.CorporateLDAPSource#transferAttributes
	 *  @see de.deepamehta.topics.DataConsumerTopic#revealTopicTypes
	 *  @see de.deepamehta.topics.DataConsumerTopic#revealRelatedTopics
	 */
	public static final String MULTIPLE_VALUE_DELIMITER = "\n";



	// -------------------
	// --- Cardinality ---
	// -------------------



	public static final String CARDINALITY_ONE = "one";
	public static final String CARDINALITY_MANY = "many";



	// ------------------------
	// --- Client Platforms ---
	// ------------------------



	public static final String PLATFORM_MACOSX = "Mac OS X";



	// -------------------------
	// --- GUI Configuration ---
	// -------------------------



	public static final int TEXTSIZE_SMALL = 0;
	public static final int TEXTSIZE_BIG = 1;



	// ----------------------
	// --- Geometry Modes ---
	// ----------------------



	public static final int GEOM_MODE_ABSOLUTE = 1;
	public static final int GEOM_MODE_RELATIVE = 2;
	public static final int GEOM_MODE_NEAR = 3;
	public static final int GEOM_MODE_FREE = 4;



	// ------------------------------------
	// --- Property Visualization Modes ---
	// ------------------------------------



	public static final String VISUAL_FIELD = "Input Field";
	public static final String VISUAL_AREA = "Multiline Input Field";
	public static final String VISUAL_TEXT_EDITOR = "Text Editor";
	public static final String VISUAL_CHOICE = "Options Menu";
	public static final String VISUAL_RADIOBUTTONS = "Option Buttons";
	public static final String VISUAL_SWITCH = "Switch";
	public static final String VISUAL_PASSWORD_FIELD = "Password Field";
	public static final String VISUAL_DATE_CHOOSER = "Date Chooser";
	public static final String VISUAL_TIME_CHOOSER = "Time Chooser";
	public static final String VISUAL_FILE_CHOOSER = "File Chooser";
	public static final String VISUAL_COLOR_CHOOSER = "Color Chooser";
	public static final String VISUAL_HIDDEN = "hidden";	// ### not a real mode



	// -----------------------
	// --- ### Display Objects ---
	// -----------------------



	public static final int DISPLAY_NONE = 0;	// ### pseudo display object
	public static final int DISPLAY_MULTIPLE_CHOICE = 1;
	public static final int DISPLAY_TYPEFORM = 2;
	public static final int DISPLAY_FREEFORM = 3;
	public static final int DISPLAY_TOPIC_CHOOSER = 4;
	public static final int DISPLAY_TEXT = 5;
	public static final int DISPLAY_HEADLINE = 6;
	public static final int DISPLAY_LINK = 7;
	public static final int DISPLAY_STATIC_LINK = 8;



	// -----------------------
	// --- Layout Elements ---
	// -----------------------



    static final String LAYOUT_ELEMENT_SEPARATOR = "Trennlinie";
    static final String LAYOUT_ELEMENT_SPACE = "Freiraum";
    static final String LAYOUT_ELEMENT_COMMENT = "Kommentar";



	// -------------------------
	// --- Text Editor Types ---
	// -------------------------



	public static final int EDITOR_TYPE_DEFAULT = 1;
	public static final int EDITOR_TYPE_STYLED = 2;
	public static final int EDITOR_TYPE_SINGLE_LINE = 3;



	// ------------------------------
	// --- HTML Generator Methods ---
	// ------------------------------



	public static final String HTML_GENERATOR_JSP = "jsp";		// default
	public static final String HTML_GENERATOR_XSLT = "xslt";	// experimental



	// -------------------------------
	// --- Membership Reveal Modes ---
	// -------------------------------



	public static final int REVEAL_MEMBERSHIP_NONE = 1;
	public static final int REVEAL_MEMBERSHIP_USER = 2;
	public static final int REVEAL_MEMBERSHIP_WORKSPACE = 3;
}
