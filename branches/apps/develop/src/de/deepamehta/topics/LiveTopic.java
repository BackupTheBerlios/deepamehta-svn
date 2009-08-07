package de.deepamehta.topics;

import de.deepamehta.BaseAssociation;
import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.Detail;
import de.deepamehta.FileServer;
import de.deepamehta.PresentableAssociation;
import de.deepamehta.PresentableTopic;
import de.deepamehta.PropertyDefinition;
import de.deepamehta.TopicInitException;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateCommands;
import de.deepamehta.service.CorporateDatasource;
import de.deepamehta.service.CorporateDetail;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.CorporateMemory;
import de.deepamehta.service.Session;
import de.deepamehta.topics.helper.ArchiveFileCollector;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.File;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.logging.Level;



/**
 * A "living" instance of a topic as existing in corporate memory.
 * Live topics are created by the
 * {@link de.deepamehta.service.ApplicationService application service}
 * are living in memory for the lifetime of the service.
 * <p>
 * A <code>LiveTopic</code> has access to the {@link de.deepamehta.service.ApplicationService} and
 * to the {@link CorporateMemory}.
 * <p>
 * The baseclass of the DeepaMehta Application Framework.
 * This class provides the hooks where application programmers "hook in" the application
 * specific behavoir.
 * The custom implementations making up the application may be build upon DeepaMehta's standard
 * topics as building blocks, thus application programmers directly or indirectly derive
 * their topics from <code>LiveTopic</code>.
 * <p>
 * <hr>
 * Last change: 29.9.2008 (2.0b8)<br>
 * J&ouml;rg Richter<br>
 * jri@deepamehta.de
 */
public class LiveTopic extends BaseTopic implements DeepaMehtaConstants {



	// **************
	// *** Fields ***
	// **************



	public static final int kernelTopicsVersion = 19;

	private static Logger logger = Logger.getLogger("de.deepamehta");

	public ApplicationService as;	// ### should be protected (WebBuilderLogin accesses it)
	protected CorporateMemory cm;	// as.cm

	// --- support for deploying a Corporate Datasource ---

	/**
	 * A live topic can deploy a {@link de.deepamehta.service.CorporateDatasource}.
	 * <p>
	 * Initialized by {@link #setDataSource}.
	 */
	protected CorporateDatasource dataSource;

	/**
	 * The topic which represents the {@link de.deepamehta.service.CorporateDatasource}.
	 * <p>
	 * Initialized by {@link #setDataSource}.
	 */
	protected DataSourceTopic dataSourceTopic;

	// --- appearance model ---

	/**
	 * Individual appearance mode.<br>
	 * {@link #APPEARANCE_DEFAULT} or {@link #APPEARANCE_CUSTOM_ICON}.
	 * <p>
	 * Initialized by {@link #setIndividualAppearance}.
	 */
	protected int appMode;

	/**
	 * Individual appearance parameter.
	 * <p>
	 * In case of {@link #APPEARANCE_CUSTOM_ICON} the name of the iconfile, in case of
	 * {@link #APPEARANCE_DEFAULT} this field remains uninitialized.
	 * <p>
	 * Initialized by {@link #setIndividualAppearance}.
	 */
	protected String appParam;

	/**
	 * The name of the iconfile used to display this topic.
	 * <p>
	 * Initialized by {@link #setIconfile}.<br>
	 * Accessed by {@link #getIconfile}.<br>
	 */
	protected String iconfile;



	// *******************
	// *** Constructor ***
	// *******************



	/**
	 * @see		de.deepamehta.service.ApplicationService#createLiveTopic
	 */
	public LiveTopic(BaseTopic topic, ApplicationService as) {
		super(topic);
		this.as = as;
		this.cm = as.cm;
	}



	// **********************
	// *** Defining Hooks ***
	// **********************



	// ------------------
	// --- Life Cycle ---
	// ------------------



	/**
	 * Subclasses can override this method to perform initialization at different initialization levels.
	 * <p>
	 * ### Think again about init levels.
	 * <p>
	 * <table>
	 * <tr><td><b>Called by</b></td><td><code>initLevel</code></td></tr>
	 * <tr><td>{@link de.deepamehta.service.ApplicationService#createLiveTopic(BaseTopic topic, boolean override, Session session)}</td><td>1</td></tr>
	 * <tr><td>{@link de.deepamehta.service.ApplicationService#createLiveTopic(String topicID, String typeID, String name, boolean override, boolean evoke, String topicmapID, String viewmode, Session session, CorporateDirectives directives)}</td><td>2, 3</td></tr>
	 * <tr><td>{@link de.deepamehta.service.ApplicationService#initTopic}</td><td><i>variable</i></td></tr>
	 * <tr><td>{@link de.deepamehta.service.ApplicationService#initTypeTopic}</td><td>1, 2, 3</td></tr>
	 * </table>
	 */
	public CorporateDirectives init(int initLevel, Session session) throws TopicInitException {
		CorporateDirectives directives = new CorporateDirectives();
		// Note: TypeTopic overrides init() and calls super.init() at tail -- the appearance aspect
		// is different for types.
		// Note: since 2.0a15-pre1, topic appearance is initialized at level 1, this is required
		// because appearance must be known before the topic is evoked, e.g. creating a workspace
		// immediateley opens the workspace and its appearance (icon) must be known for the editor tab ###
		if (initLevel == INITLEVEL_1) {		// ### was INITLEVEL_2
			setIndividualAppearance();
			setIconfile(directives);
		}
		//
		return directives;
	}

	/**
	 * Subclasses can override this method to perform some work when a topic is
	 * bring into live the very first time.
	 * <p>
	 * The default implementation creates the topic in corporate memory and returns
	 * empty directives.
	 *
	 * @see		de.deepamehta.service.ApplicationService#evokeLiveTopic
	 * @see		TopicMapTopic#evoke
	 * @see		TopicTypeTopic#evoke
	 * @see		UserTopic#evoke
	 * @see		WorkspaceTopic#evoke
	 */
	public CorporateDirectives evoke(Session session, String topicmapID, String viewmode) {
		// store in corporate memory
		cm.createTopic(getID(), getVersion(), getType(), getTypeVersion(), getName());
		// set default values
		Enumeration e = as.type(this).getTypeDefinition().elements();
		while (e.hasMoreElements()) {
			PropertyDefinition propDef = (PropertyDefinition) e.nextElement();
			String defaultValue = propDef.getDefaultValue();
			if (!defaultValue.equals("")) {
				String propName = propDef.getPropertyName();
				// existing values are not overridden ### see TypeTopic.evoke()
				if (cm.getTopicData(getID(), getVersion(), propName).equals("")) {
					cm.setTopicData(getID(), getVersion(), propName, defaultValue);
				}
			}			
		}
		//
		return new CorporateDirectives();
	}

	/**
	 * Subclasses can override this method to perform some work when a topic dies forever.
	 * <p>
	 * The default implementation deletes the topic from 3 spots ...
	 * <ul>
	 * <li>corporate memory
	 * <li>live corporate memory
	 * <li>all views
	 * </ul>
	 * ... and returns empty directives.
	 *
	 * @see		ApplicationService#deleteTopic
	 */
	public CorporateDirectives die(Session session) {
		// --- delete topic from all views ---
		as.deleteViewTopic(getID());
		// --- delete topic and its properties from corporate memory ---
		cm.deleteTopic(getID());
		cm.deleteTopicData(getID(), getVersion());
		// --- delete topic from live corporate memory ---
		// Note: deleteTopic() is derived from BaseTopicMap
		as.deleteTopic(getID() + ":" + getVersion());
		//
		return new CorporateDirectives();
	}



	// ------------------------------------------
	// --- Reacting upon dedicated situations ---
	// ------------------------------------------



	/**
	 * Subclasses can override this method to react upon publishing this topic.
	 * <p>
	 * The default implementation returns performs nothing.
	 *
	 * @see		de.deepamehta.service.ApplicationService#addPublishAction
	 * @see		DocumentTopic#published
	 */
	public void published(Session session, CorporateDirectives directives) {
	}

	/**
	 * Subclasses can override this method to react upon renaming this topic.
	 * <p>
	 * The default implementation returns a <code>CorporateDirectives</code> containing
	 * one <code>DIRECTIVE_SET_TOPIC_NAME</code> directive.
	 *
	 * @see		de.deepamehta.service.ApplicationService#changeTopicName
	 * @see		TopicMapTopic#nameChanged
	 * @see		TopicTypeTopic#nameChanged
	 * @see		UserTopic#nameChanged
	 * @see		WorkspaceTopic#nameChanged
	 */
	public CorporateDirectives nameChanged(String name, String topicmapID, Session session) {
		return new CorporateDirectives();
	}

	/**
	 * The default implementation returns empty <code>CorporateDirectives</code>.
	 */
	public CorporateDirectives nameChangedChained(String name, Session session, String result) {
		return new CorporateDirectives();
	}

	/**
	 * Subclasses can override this method to react upon retyping this topic.
	 * <p>
	 * The default implementation performs the retyping, it should be called from subclassss.
	 *
	 * @see		de.deepamehta.service.ApplicationService#changeTopicType
	 */
	public CorporateDirectives typeChanged(String typeID) {
		CorporateDirectives directives = new CorporateDirectives();
		directives.add(DIRECTIVE_SET_TOPIC_TYPE, getID(), typeID, new Integer(getVersion()));
		directives.add(DIRECTIVE_SELECT_TOPIC, getID());
		return directives;
	}

	public void clicked(String topicmapID, Session session, CorporateDirectives directives) {
		directives.add(DIRECTIVE_SELECT_TOPIC, getID());
	}

	/**
	 * @see		de.deepamehta.service.ApplicationService#moveTopic
	 */
	public CorporateDirectives moved(String topicmapID, int topicmapVersion, int x, int y, Session session) {
		return new CorporateDirectives();
	}

	/**
	 * @see		de.deepamehta.service.ApplicationService#deleteTopicIsAllowed
	 */
	public boolean deleteAllowed(Session session) {
		return true;
	}

	public boolean retypeAllowed(Session session) {
		return true;
	}



	// -----------------------------------
	// --- Revealing topics on-the-fly ---
	// -----------------------------------



	// ### Note: "revealing" here means "Extend the corporate memory on-the-fly by retrieving elements 
	// ### from a datasource" -- "revealing" here means NOT "make things visible to the user".
	// ### Probably the names of this 3 hooks should be changed



	/**
	 * Subclasses can override this method to reveal topic types on-the-fly.
	 * <p>
	 * The default implementation retrieves the topic types from corporate memory.
	 *
	 * @return	Vector of type IDs (<code>String</code>s)
	 *
	 * @see		de.deepamehta.service.ApplicationService#revealTopicTypes
	 */
	public Hashtable revealTopicTypes() {
		return cm.getTopicTypes(getID());
	}

	/**
	 * Subclasses can override this method to reveal association types on-the-fly.
	 * <p>
	 * The default implementation retrieves the association types from corporate memory.
	 *
	 * @return	Vector of type IDs (<code>String</code>s)
	 *
	 * @see		de.deepamehta.service.ApplicationService#revealAssociationTypes
	 */
	public Hashtable revealAssociationTypes() {
		return cm.getAssociationTypes(getID());
	}

	/**
	 * Subclasses can override this method to reveal topics and associations on the fly.
	 * <p>
	 * The default implementation does nothing.
	 *
	 * @see		de.deepamehta.service.ApplicationService#getRelatedTopics(
	 *								String topicmapID, String viewMode, String userID,
	 *								String topicID, int version, String topicTypeID)
	 */
	public void revealRelatedTopics(String topicTypeID) {
	}



	// --------------------------
	// --- Providing Commands ---
	// --------------------------



	/**
	 * This hook customizes the topic context menu.
	 * <p>
	 * ### The default implementation provides the standard topic commands, in case
	 * the triggering topicmap represents a <i>view</i>:
	 * <ol>
	 * <li>"Navigate By Topic"
	 * <li>"Navigate By Association"
	 * <li>"Hide"
	 * <li>"Retype"
	 * <li>"Delete"
	 * </ol>
	 * ### If the triggering topicmap represents a <i>workspace</i> an empty command set is
	 * returned.
	 * <p>
	 * References checked: 30.12.2001 (2.0a14-pre5)<br>
	 * Documentation updated: 11.10.2001 (2.0a12)
	 *
	 * @param	session		the triggering client session
	 *
	 * @return	the provided context commands
	 *
	 * @see		de.deepamehta.service.ApplicationService#showTopicMenu
	 */
	public CorporateCommands contextCommands(String topicmapID, String viewmode,
								Session session, CorporateDirectives directives) {
		// >>> compare to LiveAssociation
		CorporateCommands commands = new CorporateCommands(as);
		//
		int editorContext = as.editorContext(topicmapID);
		commands.addNavigationCommands(this, editorContext, session);
		commands.addStandardCommands(this, editorContext, viewmode, session, directives);
		//
		return commands;
	}

	/**
	 * Applications can use this hook to customize the personal workspace menu.
	 * <p>
	 * This hook is triggered for every type supposed being creatable in personal workspace.
	 * <p>
	 * The default implementation adds a command for creating a instance of the respective type
	 * to the personal workspace menu.
	 * <p>
	 * References checked: 5.2.2002 (2.0a14-pre7)
	 *
	 * @see		de.deepamehta.service.CorporateCommands#addWorkspaceTopicTypeCommands(Vector types, Session session, CorporateDirectives directives)
	 */
	public static CorporateCommands workspaceCommands(TypeTopic type, ApplicationService as,
											Session session, CorporateDirectives directives) {
		CorporateCommands commands = new CorporateCommands(as);
		commands.addCommand(as.string(ITEM_CREATE_IN_WORKSPACE, type.getName()),
			CMD_CREATE_TOPIC + COMMAND_SEPARATOR + type.getID(), type.getCreationIconPath(), type.getCreationIconfile());
		return commands;
	}

	/**
	 * ### Subclasses can override this method to provide context commands.
	 * <p>
	 * The default implementation returns an empty command set.
	 *
	 * @param	session		the requesting client session
	 *
	 * @return	the provided context commands
	 *
	 * @see		de.deepamehta.service.ApplicationService#showViewMenu
	 */
	public CorporateCommands viewCommands(String topicmapID, String viewmode,
								Session session, CorporateDirectives directives) {
		return new CorporateCommands(as);
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
		if (propName.equals(PROPERTY_ICON)) {
			propDef.setActionButton(as.string(BUTTON_ASSIGN_FILE), CMD_ASSIGN_ICON);
		} else if (propDef.getVisualization().equals(VISUAL_COLOR_CHOOSER)) {
			propDef.setActionButton(as.string(BUTTON_CHOOSE_COLOR), CMD_CHOOSE_COLOR + COMMAND_SEPARATOR + propName);
		}
	}



	// --------------------------
	// --- Executing Commands ---
	// --------------------------



	/**
	 * Subclasses can override this method to handle custom commands.
	 * <p>
	 * The default implementation handles the standard commands.
	 *
	 * @see		de.deepamehta.service.ApplicationService#executeTopicCommand
	 */
	public CorporateDirectives executeCommand(String command, Session session, String topicmapID, String viewmode) {
		// >>> compare to LiveAssociation.executeCommand()
		CorporateDirectives directives = new CorporateDirectives();
		//
		StringTokenizer st = new StringTokenizer(command, COMMAND_SEPARATOR);
		String cmd = st.nextToken();
		if (cmd.equals(CMD_DEFAULT)) {
			// --- trigger getDetail() hook ---
			directives.add(DIRECTIVE_SHOW_DETAIL, getID(), getDetail());
		} else if (cmd.equals(CMD_GET_TOPIC_COMMANDS)) {
			String topicID = st.nextToken();
			int version = Integer.parseInt(st.nextToken());
			int x = Integer.parseInt(st.nextToken());
			int y = Integer.parseInt(st.nextToken());
			directives.add(as.showTopicMenu(topicID, version, topicmapID, viewmode, x, y, session));
		} else if (cmd.equals(CMD_GET_VIEW_COMMANDS)) {
			int x = Integer.parseInt(st.nextToken());
			int y = Integer.parseInt(st.nextToken());
			directives.add(as.showViewMenu(topicmapID, viewmode, x, y, session));
		} else if (cmd.equals(CMD_SELECT_TOPIC)) {
			clicked(topicmapID, session, directives);
		} else if (cmd.equals(CMD_SELECT_TOPICMAP)) {
			directives.add(DIRECTIVE_SELECT_TOPICMAP);
		} else if (cmd.equals(CMD_ASSIGN_ICON)) {
			directives.add(DIRECTIVE_CHOOSE_FILE);
		} else if (cmd.equals(CMD_CHOOSE_COLOR)) {
			String prop = st.nextToken();
			String currentColor = getProperty(prop);
			directives.add(DIRECTIVE_CHOOSE_COLOR, currentColor);
		} else if (cmd.equals(CMD_NAVIGATION_BY_TOPIC)) {
			String typeID = st.nextToken();
			directives.add(navigateByTopictype(typeID));
		} else if (cmd.equals(CMD_NAVIGATION_BY_ASSOCIATION)) {
			String typeID = st.nextToken();
			directives.add(navigateByAssoctype(typeID));
		} else if (cmd.equals(CMD_CHANGE_TOPIC_TYPE)) {
			String typeID = st.nextToken();
			directives.add(as.changeTopicType(getID(), getVersion(), typeID, 1)); // ### typeVersion=1
		} else if (cmd.equals(CMD_CHANGE_TOPIC_TYPE_BY_NAME)) {
			String typeName = st.nextToken();
			as.changeTopicType(getID(), getVersion(), typeName, topicmapID, viewmode,
				session, directives);
		} else if (cmd.equals(CMD_NEW_TOPIC_TYPE)) {
			directives.add(DIRECTIVE_FOCUS_TYPE);
		} else if (cmd.equals(CMD_HIDE_TOPIC)) {
			hide(topicmapID, viewmode, false, directives);
		} else if (cmd.equals(CMD_DELETE_TOPIC)) {
			delete(topicmapID, viewmode, directives);
		} else if (cmd.equals(CMD_SET_PROPERTY)) {
			String prop = st.nextToken();
			String value = st.nextToken();
			directives.add(setTopicData(prop, value, topicmapID, viewmode));
		} else if (cmd.equals(CMD_ASSIGN_TOPIC)) {
			String assocTypeID = st.nextToken();
			String cardinality = st.nextToken();
			String topicID = st.nextToken();
			assignTopic(topicID, assocTypeID, cardinality, topicmapID, viewmode, directives);
		} else if (cmd.equals(CMD_ASSIGN_NEW_TOPIC)) {
			String topicTypeID = st.nextToken();
			String assocTypeID = st.nextToken();
			String cardinality = st.nextToken();
			assignNewTopic(topicTypeID, assocTypeID, cardinality, topicmapID, viewmode, session, directives);
		} else if (cmd.equals(CMD_SHOW_HELP)) {
			String typeID = st.nextToken();
			showTypeHelp(typeID, session, directives);
		} else if (cmd.equals(CMD_SEARCH_INTERNET)) {
			as.performGoogleSearch(getName(), getID(), topicmapID, viewmode, session, directives);
		} else if (cmd.equals(CMD_SUBMIT_FORM)) {
			// Note: there is no standard behavoir for "submitForm" command -- do nothing
			// just avoid "command not implemented" exception
		} else if (cmd.equals(CMD_FOLLOW_HYPERLINK)) {
			String url = st.nextToken();
			String urlPrefix = "http://";
			// error check
			if (!url.startsWith(urlPrefix)) {
				logger.warning("URL \"" + url + "\" not recognized by CMD_FOLLOW_HYPERLINK");
				return directives;
			}
			//
			String action = url.substring(urlPrefix.length());
			if (action.startsWith(ACTION_REVEAL_TOPIC)) {
				String topicID = action.substring(ACTION_REVEAL_TOPIC.length() + 1);	// +1 to skip /
				revealTopic(topicID, directives);
			} else {
				throw new DeepaMehtaException("hyperlink action \"" + action + "\" not recognized");
			}
		} else {
			if (as.editorContext(topicmapID) == EDITOR_CONTEXT_PERSONAL) {
				handleWorkspaceCommand(command, topicmapID, viewmode, session, directives);
			} else {
				throw new DeepaMehtaException("command not recognized");
			}
		}
		//
		return directives;
	}

	/**
	 * Subclasses can override this method to process result of chanined actions.<br>
	 * [### explain]
	 * <p>
	 * ### The default implementation returns null.
	 *
	 * @see		de.deepamehta.service.ApplicationService#performChainedTopicCommand
	 */
	public CorporateDirectives executeChainedCommand(String command, String result,
												String topicmapID, String viewmode, Session session) {
		CorporateDirectives directives = new CorporateDirectives();
		//
		StringTokenizer st = new StringTokenizer(command, COMMAND_SEPARATOR);
		String cmd = st.nextToken();
		//
		if (cmd.equals(CMD_ASSIGN_ICON)) {
			// Note: the result of a DIRECTIVE_CHOOSE_FILE contains the absolute
			// path of the (client side) selected icon file
			copyAndUpload(result, FILE_ICON, PROPERTY_ICON, session, directives);
			return directives;
		} else if (cmd.equals(CMD_CHOOSE_COLOR)) {
			if (!result.equals("")) {
				String prop = st.nextToken();
				setTopicData(prop, result, session, topicmapID, viewmode);
			}
			return directives;
		} else {
			throw new DeepaMehtaException("no chained command handler implemented");
		}
	}

	public static CorporateDirectives executeWorkspaceCommand(String command,
										Session session, ApplicationService as,
										String topicmapID, String viewmode) {
		return null;
	}



	// ---------------------------
	// --- Handling Properties ---
	// ---------------------------



	/**
	 * [###]
	 * <p>
	 * To reject the new properties (means: preventing them from being
	 * stored in corporate memory) the application must return <code>false</code>.
	 *
	 * @see		de.deepamehta.service.ApplicationService#setTopicProperties
	 */
	public boolean propertyChangeAllowed(String propName, String propValue, Session session, CorporateDirectives directives) {
		return true;
	}

	/**
	 * The "properties changed" hook.
	 * <p>
	 * By this hook an application programmer can supply an "properties changed" behavoir.
	 * This hook is triggered by the framework if one (### zero) or more properties of this topic
	 * has been changed. The framework passes the old and the new (means: changed)
	 * properties.
	 * <p>
	 * ### The default implementation returns empty <code>CorporateDirectives</code>.
	 *
	 * @see		de.deepamehta.service.ApplicationService#setTopicProperties
	 * @see		TopicMapTopic#propertiesChanged
	 */
	public CorporateDirectives propertiesChanged(Hashtable newProps, Hashtable oldProps,
											String topicmapID, String viewmode, Session session) {
		// >>> see TopicMapTopic.propertiesChanged()
		CorporateDirectives directives = new CorporateDirectives();
		// --- "Icon" ---
		String prop = (String) newProps.get(PROPERTY_ICON);
		if (prop != null) {
			logger.info("\"" + PROPERTY_ICON + "\" property has changed -- sending DIRECTIVE_SET_TOPIC_ICON (queued), " +
				"topicmapID=\"" + topicmapID + "\"");
			// reset appearance
			as.initTopic(getID(), 1);
			// Note: the DIRECTIVE_SET_TOPIC_ICON must be queued, because
			// the icon upload must be completed before the icon can be shown
			CorporateDirectives iconDirective = new CorporateDirectives();
			iconDirective.add(DIRECTIVE_SET_TOPIC_ICON, getID(), prop);
			directives.add(DIRECTIVE_QUEUE_DIRECTIVES, iconDirective);
		}
		// --- "Locked Geometry" ---
		String locked = (String) newProps.get(PROPERTY_LOCKED_GEOMETRY);
		if (locked != null) {
			logger.info("\"" + PROPERTY_LOCKED_GEOMETRY + "\" property has changed to \"" + locked +
				"\" -- sending DIRECTIVE_SET_TOPIC_LOCK");
			directives.add(DIRECTIVE_SET_TOPIC_LOCK, getID(), new Boolean(locked.equals(SWITCH_ON)), topicmapID);
		}
		return directives;
	}

	/**
	 * ### could be defined interactively
	 *
	 * @see		de.deepamehta.service.ApplicationService#setTopicProperties
	 */
	public String getNameProperty() {
		return PROPERTY_NAME;
	}

	/**
	 * ### only consulted if latter returns null
	 *
	 * @see		de.deepamehta.service.ApplicationService#setTopicProperties
	 */
	public String getTopicName(Hashtable props, Hashtable oldProps) {
		return null;
	}

	public Hashtable getPropertyBaseURLs() {
		Hashtable baseURLs = new Hashtable();
		baseURLs.put(PROPERTY_DESCRIPTION, as.getCorporateWebBaseURL());
		return baseURLs;
	}

	/**
	 * Transfers element data retrieved from one data source entitiy into topic data.
	 * <p>
	 * Subclasses can override this method to supply topic data e.g. retrieved from more
	 * than one data source entity. The default implementation returns the element data
	 * directly.
	 */
	public static Hashtable makeProperties(Hashtable elementData) {
		return elementData;
	}

	/**
	 * Applications can use this hook to disable certain properties of this live topics
	 * type definition. Disabled properties are visible but not editable by the user.
	 * <p>
	 * This hook can be utilized to implement an access control mechanism.
	 * The default implementation realizes the following rule: a topic is only editable
	 * if the current user
	 * 1) is the owner of the topic, or<br>
	 * 2) is a DeepaMehta administrator, or<br>
	 * 3) has the "Editor" role within a workspace the resp. topic type is assigned to.
	 * <p>
	 * This hook is triggered every time this topic is selected.
	 *
	 * @see			de.deepamehta.service.ApplicationService#disabledProperties
	 *
	 * @return		A vector of property names (<code>String</code>s)
	 */
	public Vector disabledProperties(Session session) {
		Vector disabledProps = new Vector();
		//
		TypeTopic type = as.type(this);
		String userID = session.getUserID();
		if (!type.isSearchType() && !as.isTopicOwner(getID(), session) &&
									!as.isAdministrator(userID) && !as.hasEditorRole(userID, getType())) {
			// disable all properties
			Enumeration e = type.getTypeDefinition().elements();
			while (e.hasMoreElements()) {
				PropertyDefinition propDef = (PropertyDefinition) e.nextElement();
				disabledProps.addElement(propDef.getPropertyName());
			}
		}
		//
		return disabledProps;
	}

	/**
	 * Applications can use this hook to hide certain properties of this live topics
	 * type definition. Hidden properties are not visible to the user. Note: also
	 * "derived" properties can be hidden by this hook.
	 * <p>
	 * The default implementation hides no properties.
	 * <p>
	 * This hook is triggered statically when the type definition for this live topic
	 * is build.
	 *
	 * @return	Vector of property names (<code>String</code>s) or <code>null</code>
	 *
	 * @see		de.deepamehta.service.ApplicationService#triggerHiddenProperties(TypeTopic type)
	 */
	public static Vector hiddenProperties(TypeTopic type) {
		return null;
	}

	/**
	 * Only used by de.deepamehta.service.web.HTMLGenerator.
	 * Triggered when creating a topic info or form with embedded subforms.
	 *
	 * @see		de.deepamehta.service.ApplicationService#triggerHiddenProperties(TypeTopic type, String relTopicTypeID)
	 */
	public static Vector hiddenProperties(TypeTopic type, String relTopicTypeID) {
		return null;
	}

	/**
	 * Subclasses can override this method to rename derived properties.
	 * <p>
	 * ### The default implementation does nothing.
	 *
	 * @see		TypeTopic#setPropertyLabel
	 */
	public static void propertyLabel(PropertyDefinition propDef, ApplicationService as, Session session) {
		if (as.getLanguage() == LANGUAGE_GERMAN) {
			String propName = propDef.getPropertyName();
			if (propName.equals(PROPERTY_DESCRIPTION)) {
				propDef.setPropertyLabel("Beschreibung");
			}
		}
	}

	public static String propertyLabel(PropertyDefinition propDef, String relTopicTypeID, ApplicationService as) {
		return propDef.getPropertyLabel();
	}



	// -----------------------------
	// --- Handling Associations ---
	// -----------------------------



	/**
	 * Before an association is created or retyped, the associationAllowed() hook is triggered for both involved topics.
	 * By returning <code>null</code> a topic can prohibit the creation resp. retyping.
	 * Additionally, any involved topic is able to change the proposed association type.
	 * <p>
	 * The default implementation returns the proposed association type directly.
	 * <p>
	 * References checked: 13.9.2008 (2.0b8)
	 *
	 * @param	assocTypeID		the proposed association type
	 * @param	relTopicID		the other involved topic
	 *
	 * @return	ID of the association type, actually used for retyping, resp. <code>null</code> if retyping is prohibited.
	 *
	 * @see		de.deepamehta.service.ApplicationService#triggerAssociationAllowed
	 */
	public String associationAllowed(String assocTypeID, String relTopicID, int relTopicPos, CorporateDirectives directives) {
		return assocTypeID;
	}

	/**
	 * Once retyping an association is allowed (see associationAllowed hook), both involved topics are notified about the new semantic.
	 * ### since 2.0b6-post3 also triggered for create operation
	 * <p>
	 * The default implementation does nothing.
	 * <p>
	 * References checked: 25.3.2008 (2.0b8)
	 *
	 * @param	assocTypeID		the new association type
	 * @param	relTopicID		the other involved topic
	 *
	 * @see		de.deepamehta.service.ApplicationService#createLiveAssociation
	 */
	public void associated(String assocTypeID, String relTopicID, Session session, CorporateDirectives directives) {
	}

	/**
	 * Once retyping an association is allowed (see associationAllowed hook), both involved topics are notified about loss of old semantic.
	 * <p>
	 * The default implementation does nothing.
	 *
	 * @param	assocTypeID		the old association type
	 * @param	relTopicID		the other involved topic
	 *
	 * @see		de.deepamehta.service.ApplicationService#deleteAssociation
	 * @see		de.deepamehta.service.ApplicationService#changeAssociationType
	 */
	public void associationRemoved(String assocTypeID, String relTopicID, Session session, CorporateDirectives directives) {
	}



	// ----------------------------------------
	// --- Providing additional topic label ---
	// ----------------------------------------



	/**
	 * Subclasses can override this method to provide an additional label about to be
	 * shown above the topic.
	 * <p>
	 * The default implementation returns the empty string.
	 *
	 * @see		de.deepamehta.service.ApplicationService#setTopicLabels(Enumeration topics)
	 */
	public String getLabel() {
		return "";
	}



	// ------------------------------
	// --- Handling Detail Window ---
	// ------------------------------



	/**
	 * Subclasses can override this method to provide a topic detail window.
	 * <p>
	 * The default implementation returns the help text for this topic's type.
	 *
	 * @see		#executeCommand
	 */
	public Detail getDetail() {
		return createTopicHelp(getType());
	}

	/**
	 * @see		de.deepamehta.service.ApplicationService#processTopicDetail
	 */
	public CorporateDirectives processDetailHook(CorporateDetail detail, Session session, String topicmapID, String viewmode) {
		return detail.process(session, getID(), 1, topicmapID, viewmode);
	}



	// ----------------------
	// --- Topicmap Hooks ---
	// ----------------------



	// >>> Note 1: these hooks affecting topicmaps only. Topicmaps are topics of type tt-topicmap and its subclasses.
	// >>> Note 2: implementations here are empty. The standard implementations are found in TopicMapTopic.
	// >>> Note 3: there is another topicmap hook: viewCommands(), this hook is found in hook group "Providing Commands"



	public String openTopicmap(Session session, CorporateDirectives directives) throws DeepaMehtaException {
		return null;
	}

	/**
	 * References checked: 28.11.2004 (2.0b4)
	 * <p>
	 * @see		de.deepamehta.service.ApplicationService#createPresentableTopic(BaseTopic topic, String nearTopicID, String topicmapID)
	 */
	public PresentableTopic getPresentableTopic(BaseTopic topic, String nearTopicID) {
		return new PresentableTopic(topic, nearTopicID);
	}

	// --- Exporting ---

	/**
	 * This hook can be used to modify the topicmap export behaviour.
	 * <p>
	 * The default implementation does nothing, the actual default implementation is
	 * in {@link TopicMapTopic#exportTopicmap TopicMapTopic}.
	 * <p>
	 * The application KS-Editor modifies the default export behavoir.
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
	}

	// ---

	/**
	 * Applications can use this hook to modify the SVG rendering of a topicmap.
	 * <p>
	 * The default implementation returns <code>null</code>, the actual default
	 * implementation is in {@link TopicMapTopic#getSVGStylesheetName TopicMapTopic}.
	 *
	 * @return	the name of the stylesheet for SVG creation
	 *
	 * @see		de.deepamehta.topics.helper.TopicMapExporter#createSVGFile
	 * @see		de.deepamehta.topics.helper.TopicMapExporter#createPDFFile
	 */
	public String getSVGStylesheetName() {
		return null;
	}

	/**
	 * Applications can use this hook to modify the PDF layout of a topicmap.
	 * <p>
	 * The default implementation returns <code>null</code>, the actual default
	 * implementation is in {@link TopicMapTopic#getFOStylesheetName TopicMapTopic}.
	 *
	 * @return	the name of the stylesheet for FO creation
	 *
	 * @see		de.deepamehta.topics.helper.TopicMapExporter#createPDFFile
	 */
	public String getFOStylesheetName() {
		return null;
	}

	// ---

	/**
	 * Applications can use this hook to specify the type of the topic
	 * representing the exported file.
	 * <p>
	 * The default implementation returns <code>null</code>, the actual default
	 * implementation is in {@link TopicMapTopic#getExportDocumentType TopicMapTopic}.
	 *
	 * @return  the ID of the document type topic representing the exported file
	 *
	 * @see     TopicMapTopic#export
	 */
	public String getExportDocumentType() {
		return null;
	}

	/**
	 * ###
	 *
	 * @return  the name of the document topic representing the exported file
	 *
	 * @see     #export
	 */
	public String getExportDocumentName() {
		return null;
	}

	// ---

	/**
	 * ###
	 *
	 * @return  the base file name of the exported file (excluding the file type suffix)
	 *
	 * @see     #export
	 */
	public String getExportFileBaseName() {
		return null;
	}



	// ------------------------
	// --- Topic Type Hooks ---
	// ------------------------



	// >>> Note 1: these hooks affecting topic types only. Topic types are topics of type TOPICTYPE_TOPICTYPE ("tt-topictype").
	// >>> Note 2: implementations here are empty. The standard implementations are found in TopicTypeTopic.



	/**
	 * @return  the ID of the search type
	 *
	 * @see     TopicTypeTopic#createSearchType
	 */
	public static String getSearchTypeID() {
		return TOPICTYPE_TOPIC_SEARCH;
	}



	// ***************
	// *** Methods ***
	// ***************



	/**
	 * Returns the individual appearance mode.
	 *
	 * @see		#setIconFile
	 * @see		de.deepamehta.service.ApplicationService#initAppearance
	 */
	public int getIndividualAppearanceMode() {
		return appMode;
	}

	/**
	 * Returns the individual appearance mode.
	 *
	 * @see		#setIconFile
	 * @see		de.deepamehta.service.ApplicationService#initAppearance
	 */
	public String getIndividualAppearanceParam() {
		return appParam;
	}

	// ---

	/**
	 * Returns the name of the iconfile used to display this topic.
	 * <p>
	 * Throws a DeepaMehtaException if the iconfile is unknown, which means this
	 * LiveTopic isn't initialized properly. Regularily the iconfile is initialized
	 * by setIconfile which is called at initialization level 2 (see init() hook).
	 * <p>
	 * References checked: 27.9.2001 (2.0a12-pre6)
	 *
	 * @throws	DeepaMehtaException		if the iconfile is unknown, which means
	 *									this LiveTopic isn't initialized properly
	 *
	 * @see		#setIconFile
	 * @see		#workspaceCommands
	 * @see		CorporateCommands#addTopicCommands
	 * @see		CorporateCommands#addTypeCommands
	 * @see		CorporateCommands#addTypeCommand
	 * @see		de.deepamehta.service.ApplicationService#getIconfile(BaseTopic topic)
	 * @see		de.deepamehta.service.ApplicationService#getIconfile(String topicID, int version)
	 * @see		de.deepamehta.service.ApplicationService#initTypeTopicAppearance
	 * @see		de.deepamehta.topics.TopicMapTopic#exportTopics
	 */
	public String getIconfile() throws DeepaMehtaException {
		if (iconfile == null) {
			throw new DeepaMehtaException("icon of " + this + " is unknown");
		}
		return iconfile;
	}

	/* ### public String getIconfile(boolean asURL) throws DeepaMehtaException {
		if (iconfile == null) {
			throw new DeepaMehtaException("icon of " + this + " is unknown");
		}
		return (asURL ? as.getCorporateWebBaseURL() + FILESERVER_ICONS_PATH : "") + iconfile;
	} */

	// ---

	/**
	 * Extends the specified directives to delete this topic as well as any associations
	 * this topic is involed in.
	 * <p>
	 * Handles the {@link #CMD_DELETE_TOPIC} command.
	 * <p>
	 * ### Hypothesis: topicmapID and viewmode are only needed when called by client,
	 * ### when called by servlet they can be set to null
	 * <p>
	 * References checked: 20.8.2007 (2.0b8)
	 *
	 * @see		#executeCommand
	 * @see		de.deepamehta.service.ApplicationService#deleteTopic
	 */
	public void delete(String topicmapID, String viewmode, CorporateDirectives directives) {
		hide(topicmapID, viewmode, true, directives);	
	}

	/**
	 * Extends the specified directives to hide/delete this topic as well as any associations
	 * this topic is involed in.
	 * <p>
	 * Handles the commands {@link #CMD_HIDE_TOPIC} and {@link #CMD_DELETE_TOPIC} (indirectly).
	 * <p>
	 * ### Hypothesis: topicmapID and viewmode are only needed when called by client,
	 * ### when called by servlet they can be set to null
	 * <p>
	 * References checked: 20.8.2007 (2.0b8)
	 *
	 * @see		#executeCommand
	 * @see		#delete
	 * @see		de.deepamehta.topics.TopicMapTopic#hideAll
	 * @see		de.deepamehta.kompetenzstern.topics.KriteriumTopic#hideRecursively
	 */
	public void hide(String topicmapID, String viewmode, boolean delete, CorporateDirectives directives) {
		Boolean doDelete = new Boolean(delete);
		String topicID = getID();
		// --- hide associations ---
		Vector assocIDs;
		if (delete) {
			assocIDs = cm.getAssociationIDs(topicID, 1);							// ### version=1
		} else {
			assocIDs = cm.getAssociationIDs(topicID, 1, topicmapID, 1, viewmode);	// ### versions=1
		}
		directives.add(DIRECTIVE_HIDE_ASSOCIATIONS, assocIDs, doDelete, topicmapID);
		// --- hide topic ---
		directives.add(DIRECTIVE_HIDE_TOPIC, topicID, doDelete, topicmapID);
	}



	// ***********************
	// *** Utility Methods ***
	// ***********************



	/**
	 * @see		de.deepamehta.topics.ContainerTopic#executeCommand
	 * @see		de.deepamehta.topics.ContainerTopic#initContainer
	 */
	public final Hashtable getProperties() {
		return as.getTopicProperties(getID(), getVersion());
	}

	// --- getProperty (3 forms) ---

	/**
	 * @see		de.deepamehta.topics.ContainerTopic#getLabel
	 * @see		de.deepamehta.topics.ContainerTopic#initContainer
	 */
	public final String getProperty(String propName) {
		return getProperty(getID(), getVersion(), propName);
	}

	public final String getProperty(BaseTopic topic, String propName) {
		return getProperty(topic.getID(), topic.getVersion(), propName);
	}

	public final String getProperty(String topicID, int version, String propName) {
		return as.getTopicProperty(topicID, version, propName);
	}

	// ---

	/**
	 * Sets a property value for this topic directly in corporate memory.
	 * <p>
	 * Note: the application service is bypassed (no hooks are triggered) and the GUI is not updated (no directives are send).
	 * Use this method deliberately.
	 */
	public final void setProperty(String propName, String propValue) {
		as.setTopicProperty(getID(), getVersion(), propName, propValue);
	}

	// --- setTopicData (2 forms) ---

	/**
	 * Sets the specified properties for this topic and sends a
	 * {@link #DIRECTIVE_SHOW_TOPIC_PROPERTIES} via the specified session.
	 */
	public final void setTopicData(String field, String value, Session session, String topicmapID, String viewmode) {
		CorporateDirectives directives = setTopicData(field, value, topicmapID, viewmode);
		as.getHostObject().sendDirectives(session, directives, as, topicmapID, viewmode);
	}

	// ### topicmapID, viewmode not needed anymore
	// ### method must be renamed, actually it constructs directives
	public final CorporateDirectives setTopicData(String field, String value, String topicmapID, String viewmode) {
		CorporateDirectives directives = new CorporateDirectives();
		Hashtable props = new Hashtable();
		props.put(field, value);
		directives.add(DIRECTIVE_SHOW_TOPIC_PROPERTIES, getID(), props, new Integer(1));
		return directives;
	}

	// --- navigateByTopictype (2 forms) ---

	/**
	 * @see		#executeCommand
	 */
	protected final CorporateDirectives navigateByTopictype(String topicTypeID) {
		return navigateByTopictype(topicTypeID, 0, null);
	}

	/**
	 * Application utility method. Called e.g. by the "Kompetenzstern" application.
	 *
	 * @param	relTopicPos		association position of the related topic (1 or 2), 0 if the postion doesn't natter
	 * @param	semantic		association type, <code>null</code> if the association type doesn't natter
	 *
	 * @see		#navigateByTopictype(String topicTypeID)
	 */
	public final CorporateDirectives navigateByTopictype(String topicTypeID, int relTopicPos, String semantic) {
		// subclasses have the opportunity to create topics and associations on the fly -- that means
		// before the related topics are retrieved from corporate memory (storage layer)
		//
		// --- trigger revealRelatedTopics() hook ---
		revealRelatedTopics(topicTypeID);
		//
		Vector[] relatedItems = cm.getRelatedViewTopicsByTopictype(getID(), topicTypeID, relTopicPos, semantic);
		// show result if not too large
		int topicCount = relatedItems[0].size();
		if (topicCount <= MAX_REVEALING) {
			return new CorporateDirectives(relatedItems);
		} else {
			TypeTopic type = as.type(topicTypeID, 1);
			String containerName = type.getPluralNaming();	// ### should be default naming behavoir of a container
			return as.createNewContainer(this, type.getSearchType().getID(), null, new Hashtable(), getID(), null,
				topicCount, relatedItems[0], false, containerName, true);
				// relatedTopicSemantic=null, evokeContent=false, revealContent=true
		}
	}

	// ---

	/**
	 * @see		#executeCommand
	 */
	protected final CorporateDirectives navigateByAssoctype(String assocTypeID) {
		// subclasses have the opportunity to create topics and associations on the fly -- that means
		// before the related topics are retrieved from corporate memory (storage layer)
		//
		// --- trigger revealRelatedTopics() hook ---
		// revealRelatedTopics(topicTypeID);	### pending for association types
		//
		Vector[] relatedItems = cm.getRelatedViewTopicsByAssoctype(getID(), assocTypeID);
		// show result if not too large
		int topicCount = relatedItems[0].size();
		if (topicCount <= MAX_REVEALING) {
			return new CorporateDirectives(relatedItems);
		} else {
			// group topics by type
			Hashtable topics = new Hashtable();
			Enumeration e = relatedItems[0].elements();
			while (e.hasMoreElements()) {
				PresentableTopic topic = (PresentableTopic) e.nextElement();
				String topicTypeID = topic.getType();
				Vector typedTopics = (Vector) topics.get(topicTypeID);
				if (typedTopics == null) {
					typedTopics = new Vector();
					topics.put(topicTypeID, typedTopics);
				}
				typedTopics.addElement(topic);
			}
			// create a container for every topic type
			CorporateDirectives directives = new CorporateDirectives();
			e = topics.keys();
			while (e.hasMoreElements()) {
				String topicTypeID = (String) e.nextElement();
				TypeTopic type = as.type(topicTypeID, 1);
				String containerName = type.getPluralNaming();	// ### should be default naming behavoir of a container
				Vector typedTopics = (Vector) topics.get(topicTypeID);
				directives.add(as.createNewContainer(this, type.getSearchType().getID(), null, new Hashtable(),
					getID(), assocTypeID, typedTopics.size(), typedTopics, false, containerName, false));	// ### revealContent=false
			}
			//
			return directives;
		}
	}

	// --- createChildTopic (3 forms) ---

	/**
	 * Creates a child topic directly in corporate memory ### must create live topic instead (evoke())
	 */
	public final String createChildTopic(String typeID, String semantic) {
		String childID = as.getNewTopicID();
		String assocID = as.getNewAssociationID();
		cm.createTopic(childID, 1, typeID, 1, "");
		cm.createAssociation(assocID, 1, semantic, 1, getID(), 1, childID, 1);
		return childID;
	}

	public final LiveTopic createChildTopic(String typeID, String semantic, Session session, CorporateDirectives directives) {
		return createChildTopic(typeID, semantic, false, session, directives);
	}

	/**
	 * Builds the directives to create a new child topic to this topic. Creating a child
	 * topic consists of 4 steps:
	 * <ol>
	 * <li>Create a new topic of the specified type and reveal it in the near of this topic
	 * <li>Create a new association of the specified semantic from this topic to the child topic
	 * <li>Select the child topic
	 * <li>Setup GUI for naming the child topic (depends on user prefs)
	 * </ol>
	 * <p>
	 * References checked: 24.7.2002 (2.0a15-pre11)
	 *
	 * @see		TypeTopic#executeCommand
	 * @see		PropertyTopic#executeCommand
	 */
	public final LiveTopic createChildTopic(String typeID, String semantic, boolean reverseAssocDir,
																	Session session, CorporateDirectives directives) {
		String childID = as.getNewTopicID();
		//
		String topicID1, topicID2;
		if (reverseAssocDir) {
			topicID1 = childID;
			topicID2 = getID();
		} else {
			topicID1 = getID();
			topicID2 = childID;
		}
		//
		PresentableTopic child = new PresentableTopic(childID, 1, typeID, 1, "", getID(), "");
		PresentableAssociation assoc = as.createPresentableAssociation(semantic, topicID1, getVersion(), topicID2, 1, false);
		// Note: the association must be created directly in corporate memory, to let childs evoke() logic operate on it
		// ### this way the association's evoke() hook is not triggered and thus the association's default property values are
		// not set. This is an OPEN PROBLEM which affects e.g. the "Email" application: when a "Recipient" association is
		// created programatically via the "Compose Email" command, its "Recipient Type" property is not initialized ("To").
		cm.createAssociation(assoc.getID(), 1, semantic, 1, topicID1, 1, topicID2, 1);
		cm.setAssociationData(assoc.getID(), 1, PROPERTY_OWNER_ID, session.getUserID());
		// ### Note: creating a LiveAssociation doesn't work here because the topic's associated() hooks will be triggered
		// but the topics are not yet created
		// ### as.createLiveAssociation(assoc, session, directives);
		// ### as.setAssocProperty(assoc, PROPERTY_OWNER_ID, session.getUserID());
		//
		// Note: the child topic must be created now, to let the caller operate on it
		LiveTopic topic = as.createLiveTopic(child, session, directives);
		as.setTopicProperty(child, PROPERTY_OWNER_ID, session.getUserID());
		//
		directives.add(DIRECTIVE_SHOW_TOPIC, child       /* ###, Boolean.TRUE */);
		directives.add(DIRECTIVE_SHOW_ASSOCIATION, assoc /* ###, Boolean.TRUE */);
		directives.add(DIRECTIVE_SELECT_TOPIC, childID);
		//
		rename(childID, typeID, "", session, directives);	// ### default action is hardcoded
		//
		return topic;
	}

	// --- revealTopic (3 forms) ---

	public final void revealTopic(String topicID, String semantic, String topicmapID, CorporateDirectives directives) {
		revealTopic(topicID, semantic, topicmapID, true, directives);	// doSelectTopic=true
	}

	/**
	 * Extends the specified directives to perform this task: Create an association to the specified existing
	 * topic and reveal that association.
	 * <p>
	 * References checked: 14.11.2004 (2.0b4)
	 *
	 * @see		TopicContainerTopic#revealTopic
	 */
	public final void revealTopic(String topicID, String semantic, String topicmapID, boolean doSelectTopic,
																						CorporateDirectives directives) {
		// ### compare to ElementContainerTopic.revealTopic()
		PresentableTopic topic = as.createPresentableTopic(cm.getTopic(topicID, 1), getID(), topicmapID);
		PresentableAssociation assoc = as.createPresentableAssociation(semantic,
			getID(), getVersion(), topicID, 1, true);		// performExistenceCheck=true
		directives.add(DIRECTIVE_SHOW_TOPIC, topic);
		directives.add(DIRECTIVE_SHOW_ASSOCIATION, assoc, Boolean.TRUE);			// ### should evoke conditionally?
		if (doSelectTopic) {
			directives.add(DIRECTIVE_SELECT_TOPIC, topicID);
		}
	}

	/**
	 * Reveals a "virtual" realted topic in the near of this topic.
	 *
	 * @param	topicID		the ID of the topic to reveal.
	 */
	public final void revealTopic(String topicID, CorporateDirectives directives) {
		PresentableTopic topic = new PresentableTopic(as.getLiveTopic(topicID, 1), getID());
		Boolean evoke = Boolean.FALSE;
		// create a "virtual" association of type "Search Result" if not yet exist
		BaseAssociation a = cm.getAssociation(SEMANTIC_CONTAINER_HIERARCHY, getID(), topicID);
		if (a == null) {
			String assocID = as.getNewAssociationID();
			a = new BaseAssociation(assocID, 1, SEMANTIC_CONTAINER_HIERARCHY, 1, "", getID(), 1, topicID, 1);
			evoke = Boolean.TRUE;
		}
		//
		PresentableAssociation assoc = new PresentableAssociation(a);
		directives.add(DIRECTIVE_SHOW_TOPIC, topic);
		directives.add(DIRECTIVE_SHOW_ASSOCIATION, assoc, evoke);
		directives.add(DIRECTIVE_SELECT_TOPIC, topicID);
	}

	// ---

	/**
	 * References checked: 14.12.2001 (2.0a14-pre4)
	 *
	 * @see		DataConsumerTopic#init
	 * @see		ElementContainerTopic#init
	 */
	protected void setDataSource(Session session, CorporateDirectives directives) throws TopicInitException {
		Vector datasources = as.getRelatedTopics(getType(), SEMANTIC_DATA_SOURCE, 2);
		// error check 1
		if (datasources.size() == 0) {
			throw new TopicInitException("Topic \"" + getName() + "\" has no associated datasource " +
				"(there must be an association to a data source)");
		}
		// error check 2
		if (datasources.size() > 1) {
			throw new TopicInitException("Topic \"" + getName() + "\" has " +
				datasources.size() + " associated data sources (expected is 1)");
		}
		//
		LiveTopic datasource = as.getLiveTopic((BaseTopic) datasources.firstElement(), session, directives);
		// error check 3
		if (!(datasource instanceof DataSourceTopic)) {
			throw new TopicInitException(this +" has no data source (the \"" + SEMANTIC_DATA_SOURCE +
				"\" association doesn't lead to a data source but to a " + datasource.getClass() + ")");
		}
		// --- set the data source ---
		this.dataSourceTopic = (DataSourceTopic) datasource;
		this.dataSource = dataSourceTopic.getDataSource();
		//
		// error check 4
		if (dataSource == null) {
			throw new TopicInitException("The datasource of " + this + " is not properly inited");
		}
	}

	// --- copyAndUpload (2 forms) ---

	/**
	 * Utility method to process a file chooser result.
	 * The property value is set and the file is copied and uploaded.
	 * <p>
	 * This method is used if the filename is stored in a property.
	 * Used by standard topic types, can also be used by custom topic types.
	 * <p>
	 * References checked: 11.3.2004 (2.0b3-pre1)
	 *
	 * @param	filetype	specifies the local repository, see the 4 FILE_... constants
	 *
	 * @see		#executeChainedCommand						CMD_ASSIGN_ICON
	 * @see		TopicMapTopic#executeChainedCommand			CMD_ASSIGN_BACKGROUND
	 * @see		TopicTypeTopic#executeChainedCommand		CMD_ASSIGN_CREATION_ICON
	 * @see		PropertyTopic#executeChainedCommand			CMD_ASSIGN_EDIT_ICON
	 * @see		InstallationTopic#executeChainedCommand		CMD_ASSIGN_CORPORATE_ICON
	 *														CMD_ASSIGN_CUSTOMER_ICON
	 * @see		ImageTopic#executeChainedCommand			CMD_ASSIGN_FILE
	 */
	protected final void copyAndUpload(String path, int filetype, String propName, Session session,
																		CorporateDirectives directives) {
		// --- copy and upload ---
		String filename = copyAndUpload(path, filetype, session, directives);
		// --- show new property value ---
		if (filename != null) {
			Hashtable props = new Hashtable();
			props.put(propName, filename);
			// Note: the DIRECTIVE_SHOW_TOPIC_PROPERTIES triggers propertiesChanged() hook,
			// the DIRECTIVE_SET_TOPIC_ICON is queued there
			directives.add(DIRECTIVE_SHOW_TOPIC_PROPERTIES, getID(), props, new Integer(1));
		}
	}

	/**
	 * Extends the specified directives to let the client copy the specified file to its local
	 * repository and then upload it. 
	 * <p>
	 * If the path is empty the directives are not extended and null is returned.
	 * <p>
	 * This method is used if the filename is not stored in a property but the file is processed on-the-fly.
	 * This is a convenience method used by standard topic types, it should not be used by the application programmer.
	 * <p>
	 * References checked: 11.3.2004 (2.0b3-pre1)
	 *
	 * @param	path		the absolute path of the file, must not <code>null</code>
	 * @param	filetype	specifies the local repository, see the 4 FILE_... constants
	 *
	 * @return	the filename (last component of path) resp. <code>null</code> if path is empty
	 *
	 * @see		#copyAndUpload
	 * @see		TopicMapTopic#doImport
	 * @see		TopicMapTopic#importDocuments
	 * @see		CMImportExportTopic#importCM
	 */
	protected final String copyAndUpload(String path, int filetype, Session session, CorporateDirectives directives) {
		// Note: an empty path indicates the filechoosing has been canceled by the user
		// ### compare to TopicMapTopic.doImport()
		// ### compare to CMImportExportTopic.executeChainedCommand()
		if (path.equals("")) {
			logger.info("file operation has been canceled by user");
			return null;
		}
		//
		String filename = new File(path).getName();
		//
		// Note 1: the first directive (copy) is ignored at client side if the selected file originates from the clients
		// document repository.
		//
		// ### Note 2: the second directive (upload) is ignored at client side if the same file is already in corporate
		// document repository (check is based on the files last modification date).
		//
		directives.add(DIRECTIVE_COPY_FILE, path, new Integer(filetype));
		upload(filename, filetype, session, directives);
		//
		return filename;
	}

	/**
	 * Builds the directives to upload a file to the server.
	 */
	protected final void upload(String filename, int filetype, Session session, CorporateDirectives directives) {
		if (filename.equals("") || as.runsAtServerHost(session)) {
			return;
		}
		// --- upload the file ---
		// ### for queued upload requests the time stamp might be not correct. it is 0 in the following situation:
		// client and server are running at the same machine, sharing the document repository, the file to import
		// was choosen from outside document repository and no version of the file is in document repository -- thus
		// the client will perform the upload request and corrupts the archive file (0 bytes).
		//
		// ### compare to PresentationService.uploadFile()
		//
		File file = new File(FileServer.repositoryPath(filetype) + filename);
		long lastModified = file.lastModified();
		// reporting
		if (LOG_FILESERVER) {
			// check if document already exists in corporate document repository
			if (lastModified != 0) {
				logger.info("file \"" + filename + "\" already in corporate document repository -- upload required " +
					"only if client-side version is newer than " + new Date(lastModified) + " (" + lastModified + ")");
			} else {
				logger.info("file \"" + filename + "\" not yet in corporate document repository -- upload required");
			}
		}
		//
		// ### Queueing is not necessary because COPY and UPLOAD directives are queued at client side anyway.
		// Queueing is even not working when importing a topic map from a remote host (UPLOAD is queued double and the actual
		// upload is performed _after_ sending the import message)
		/* ### CorporateDirectives uploadDirective = new CorporateDirectives();
		uploadDirective.add(DIRECTIVE_UPLOAD_FILE, filename, new Long(lastModified), new Integer(filetype));
		directives.add(DIRECTIVE_QUEUE_DIRECTIVES, uploadDirective); */
		//
		directives.add(DIRECTIVE_UPLOAD_FILE, filename, new Long(lastModified), new Integer(filetype));
	}

	// ---

	/**
	 * Returns a name for a topic created from specified element charactristics.
	 * <p>
	 * The topicname is determined by getting the value of the specified field of the
	 * specified element data. If the element data doesn't contain such a field an error
	 * is reported on the console and the empty string ("") is returned. If the specified
	 * field contains more than one line only the first line is returned.
	 *
	 * @see		ElementContainerTopic#getContent
	 * @see		ElementContainerTopic#createTopicFromElement
	 * @see		DataConsumerTopic#createNewTopic
	 */
	protected String topicName(Hashtable elementData, String fieldname) {
		String name = (String) elementData.get(fieldname);
		if (name == null) {
			logger.warning("no name property (\"" + fieldname + "\") in " + elementData + " -- new topic has no name");
			return "";
		}
		int pos = name.indexOf('\n');
		if (pos != -1) {
			name = name.substring(0, pos);
		}
		return name;
	}



	// ***********************
	// *** Private Methods ***
	// ***********************



	/**
	 * Handles the {@link #CMD_ASSIGN_TOPIC} command.
	 *
	 * @see		#executeCommand
	 */
	private void assignTopic(String topicID, String assocTypeID, String cardinality,
								String topicmapID, String viewmode, CorporateDirectives directives) {
		LiveTopic assignedTopic = as.getLiveTopic(topicID, 1);	//### version=1
		// get assignment
		BaseAssociation assoc;
		if (cardinality.equals(CARDINALITY_ONE)) {
			assoc = as.getAssociation(getID(), assocTypeID, 2, assignedTopic.getType(), true, directives);	// allowEmpty=true ### assocPos=2
		} else if (cardinality.equals(CARDINALITY_MANY)) {
			assoc = cm.getAssociation(assocTypeID, getID(), topicID);	// ### fixed order
		} else {
			throw new DeepaMehtaException("unexpected cardinality: \"" + cardinality + "\"");
		}
		// remove assignment, if exists
		if (assoc != null) {
			directives.add(DIRECTIVE_HIDE_ASSOCIATION, assoc.getID(), Boolean.TRUE, topicmapID);
		}
		// recreate assignment
		if (cardinality.equals(CARDINALITY_ONE) || assoc == null) {
			directives.add(DIRECTIVE_SHOW_TOPIC, new PresentableTopic(assignedTopic, getID()));
			directives.add(DIRECTIVE_SHOW_ASSOCIATION, as.createPresentableAssociation(assocTypeID,
				getID(), getVersion(), topicID, 1, false), Boolean.TRUE);
		}
	}

	/**
	 * Handles the {@link #CMD_ASSIGN_NEW_TOPIC} command.
	 *
	 * @see		#executeCommand
	 */
	private void assignNewTopic(String topicTypeID, String assocTypeID, String cardinality,
								String topicmapID, String viewmode,
								Session session, CorporateDirectives directives) {
		if (cardinality.equals(CARDINALITY_ONE)) {
			// get assignment
			BaseAssociation assoc = as.getAssociation(getID(), assocTypeID, 2, topicTypeID,
				true, directives);	// allowEmpty=true ### assocPos=2
			// remove assignment, if exists
			if (assoc != null) {
				directives.add(DIRECTIVE_HIDE_ASSOCIATION, assoc.getID(), Boolean.TRUE, topicmapID);
			}
		}
		// create new assignment
		String topicID = as.getNewTopicID();
		PresentableTopic topic = new PresentableTopic(topicID, 1, topicTypeID, 1, "", getID(), "");
		PresentableAssociation assoc = as.createPresentableAssociation(assocTypeID,
			getID(), getVersion(), topicID, 1, false);
		directives.add(DIRECTIVE_SHOW_TOPIC, topic, Boolean.TRUE);
		directives.add(DIRECTIVE_SHOW_ASSOCIATION, assoc, Boolean.TRUE);
		directives.add(DIRECTIVE_SELECT_TOPIC, topicID);
		rename(topicID, topicTypeID, "", session, directives);
	}

	// ---

	private void showTypeHelp(String typeID, Session session, CorporateDirectives directives) {
		Detail detail = createTopicHelp(typeID);
		directives.add(DIRECTIVE_SHOW_DETAIL, getID(), detail);
	}

	private Detail createTopicHelp(String typeID) {
		TypeTopic type = as.type(typeID, 1);
		String html = type.getProperty(PROPERTY_DESCRIPTION);
		String title = as.string(ITEM_SHOW_HELP, type.getName());
		Detail detail = new Detail(DETAIL_TOPIC, DETAIL_CONTENT_HTML, html, Boolean.FALSE, title, "??");	// ### command="??"
		return detail;
	}

	// ---

	/**
	 * Adds directives to initiate topic (re)naming.
	 * <p>
	 * References checked: 23.6.2002 (2.0a15-pre8)
	 *
	 * @param	session		is null when used by web interface
	 *
	 * @see		#createChildTopic
	 * @see		#assignNewTopic
	 * @see		#rename(BaseTopic topic, CorporateDirectives directives)
	 * @see		TopicMapTopic#createTopic
	 * @see		TypeTopic#executeCommand
	 * @see		PropertyTopic#executeCommand
	 */
	protected final void rename(String topicID, String typeID, String name, Session session, CorporateDirectives directives) {
		if (session == null) {
			return;
		}
		directives.add(DIRECTIVE_FOCUS_PROPERTY);
	}

	// ---

	/**
	 * @see		#init
	 */
	private void setIndividualAppearance() {
		String iconProp = getProperty(PROPERTY_ICON);
		if (iconProp.equals("")) {
			this.appMode = APPEARANCE_DEFAULT;
		} else {
			this.appMode = APPEARANCE_CUSTOM_ICON;
			this.appParam = iconProp;
		}
	}

	/**
	 * Initializes {@link #iconfile}.
	 * <p>
	 * Note: this method is overridden by {@link TopicTypeTopic#setIconfile TopicTypeTopic} and
	 * {@link AssociationTypeTopic#setIconfile AssociationTypeTopic}.
	 *
	 * @see		#init
	 */
	protected void setIconfile(CorporateDirectives directives) {
		switch (getIndividualAppearanceMode()) {
		case APPEARANCE_DEFAULT:
			String typeID = getType();
			// Note: in the moment when properties and constants are inited the type
			// topics are not yet inited (see de.deepamehta.service.ApplicationService.loadKernelTopics()).
			// Thus we must perform some bootstrapping here (not nice). ### Perhaps this
			// could be avoided by different usage of the initialization levels.
			if (typeID.equals(TOPICTYPE_PROPERTY)) {
				this.iconfile = "property.gif";		// ### not nice
			} else if (typeID.equals(TOPICTYPE_PROPERTY_VALUE)) {
				this.iconfile = "constant.gif";		// ### not nice
			} else {
				this.iconfile = as.getLiveTopic(getType(), 1).getIconfile();
			}
			break;
		case APPEARANCE_CUSTOM_ICON:
			this.iconfile = getIndividualAppearanceParam();
			break;
		default:
			throw new DeepaMehtaException("unexpected appearance mode: " + getIndividualAppearanceMode());
		}
		// error check 1
		if (iconfile == null) {
			throw new DeepaMehtaException("icon of " + this + " is unknown");
		}
		// error check 2
		if (!new File(FILESERVER_ICONS_PATH + iconfile).exists()) {
			logger.warning(this + ": " + iconfile + " doesn't exist");
		}
	}

	// ---

	/**
	 * @see		#setIconfile
	 */
	private String getColorProperty(String topicID) {
		return cm.getTopicData(topicID, 1, PROPERTY_COLOR);
	}

	// ---

	/**
	 * @see		#executeCommand
	 */
	private void handleWorkspaceCommand(String command, String topicmapID, String viewmode, Session session,
																						CorporateDirectives directives) {
		String userID = session.getUserID();
		// --- handle workspace types ---
		Enumeration e = as.getWorkspaces(userID).elements();
		while (e.hasMoreElements()) {
			String workspaceID = ((BaseTopic) e.nextElement()).getID();
			Vector types = as.getTopicTypes(workspaceID, PERMISSION_CREATE_IN_WORKSPACE);
			directives.add(handleWorkspaceCommand(command, types, session, topicmapID, viewmode));
		}
		// --- handle user types ---
		Vector types = as.getTopicTypes(userID, PERMISSION_CREATE_IN_WORKSPACE);
		directives.add(handleWorkspaceCommand(command, types, session, topicmapID, viewmode));
	}

	private CorporateDirectives handleWorkspaceCommand(String command, Vector types, Session session, String topicmapID,
																										String viewmode) {
		CorporateDirectives directives = new CorporateDirectives();
		//
		Enumeration e = types.elements();
		while (e.hasMoreElements()) {
			String typeID = ((BaseTopic) e.nextElement()).getID();
			TypeTopic type = as.type(typeID, 1);	// ### typeVersion=1
			// --- trigger executeWorkspaceCommand() hook ---
			Class[] paramTypes = {String.class, Session.class, ApplicationService.class, String.class, String.class};
			Object[] paramValues = {command, session, as, topicmapID, viewmode};
			CorporateDirectives directives2 = (CorporateDirectives) as.triggerStaticHook(
				type.getImplementingClass(), "executeWorkspaceCommand", paramTypes, paramValues, false);
			//
			if (directives2 != null) {
				directives.add(directives2);
			}
		}
		//
		return directives;
	}
}
