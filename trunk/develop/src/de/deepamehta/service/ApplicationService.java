package de.deepamehta.service;

import de.deepamehta.AmbiguousSemanticException;
import de.deepamehta.Association;
import de.deepamehta.BaseAssociation;
import de.deepamehta.BaseTopic;
import de.deepamehta.BaseTopicMap;
import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.FileServer;
import de.deepamehta.OrderedItem;
import de.deepamehta.PresentableAssociation;
import de.deepamehta.PresentableTopic;
import de.deepamehta.PresentableTopicMap;
import de.deepamehta.PresentableType;
import de.deepamehta.PropertyDefinition;
import de.deepamehta.Relation;
import de.deepamehta.Topic;
import de.deepamehta.TopicInitException;
import de.deepamehta.assocs.LiveAssociation;
import de.deepamehta.service.web.DeepaMehtaServlet;
import de.deepamehta.topics.AssociationTypeTopic;
import de.deepamehta.topics.AuthentificationSourceTopic;
import de.deepamehta.topics.ChatTopic;
import de.deepamehta.topics.ContainerTopic;
import de.deepamehta.topics.LiveTopic;
import de.deepamehta.topics.LoginTopic;
import de.deepamehta.topics.TopicMapTopic;
import de.deepamehta.topics.TopicTypeTopic;
import de.deepamehta.topics.TypeTopic;
import de.deepamehta.topics.helper.HTMLParser;
import de.deepamehta.topics.helper.TopicMapImporter;
import de.deepamehta.util.CaseInsensitveHashtable;
import de.deepamehta.util.DeepaMehtaUtils;

import com.google.soap.search.GoogleSearch;
import com.google.soap.search.GoogleSearchFault;
import com.google.soap.search.GoogleSearchResult;
import com.google.soap.search.GoogleSearchResultElement;

import java.awt.Point;
import java.io.CharArrayWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.logging.Level;



/**
 * The <code>ApplicationService</code> serves application logic that is encoded into live topics.
 * <p>
 * <img src="../../../../../images/3-tier-lcm.gif">
 * <p>
 * <hr>
 * Last change: 26.1.2009 (2.0b9)<br>
 * J&ouml;rg Richter / Malte Rei&szlig;ig<br>
 * jri@deepamehta.de / mre@deepamehta.de
 */
public final class ApplicationService extends BaseTopicMap implements LoginCheck, DeepaMehtaConstants {



	// **************
	// *** Fields ***
	// **************



	private static Logger logger = Logger.getLogger("de.deepamehta");

	private ApplicationServiceHost host;

	/**
	 * The IP address of the server machine.
	 * <p>
	 * Initialized by {@link #ApplicationService constructor}.
	 * Accessed by {@link #runsAtServerHost}.<br>
	 */
	private String hostAddress;

	/**
	 * The connection to the storage layer.
	 * Currently a {@link RelationalCorporateMemory} object is created.
	 */
	public CorporateMemory cm;	// ### accessed by
								//		- InteractionConnection.login()
								//		- UserTopic.evoke()

	// ---

	/**
	 * The active installation (type <code>tt-installation</code>)
	 * <p>
	 * Initialized by by {@link #ApplicationService constructor}.<br>
	 * The installation name is accessed by {@link #getInstallationName}.
	 */
	private BaseTopic installation;

	/**
	 * The properties of the active installation.
	 * <p>
	 * Initialized by {@link #ApplicationService constructor}.<br>
	 * Accessed by {@link #getInstallationProps}.<br>
	 * Written to stream by {@link #writeInstallationProps}.
	 */
	private Hashtable installationProps;

	// ---

	/**
	 * The datasource used for user authentification.
	 * <p>
	 * Initialized by {@link #setAuthentificationSourceTopic}.<br>
	 * Accessed by {@link #getAuthentificationSourceTopic}.
	 */
	private AuthentificationSourceTopic authSourceTopic;

	/**
	 * An array holding the logged in clients. The index is the session ID.
	 */
	private Session[] clientSessions = new Session[MAX_CLIENTS + 1];

	private ServerConsole serverConsole;	// only initialized if running as server

	private TimerTask statisticsThread;

	private static ApplicationServiceInstance applicationServiceInstance;



	// *****************************
	// *** Constructor (private) ***
	// *****************************



	/**
	 * References checked: 13.1.2002 (2.0a14-pre6)
	 *
	 * @see		#create
	 */
	private ApplicationService(ApplicationServiceHost host, CorporateMemory cm) {
		// >>> compare to PresentationService constructor
		try {
			this.hostAddress = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			logger.log(Level.SEVERE, "Error creating application service ...", e);
		}
		//
		this.host = host;
		this.cm = cm;
		this.installation = getActiveInstallation();
		this.installationProps = getTopicProperties(installation);
		installationProps.put(PROPERTY_CW_BASE_URL, getCorporateWebBaseURL());	// ### not really an installation property
		//
		logger.info("active installation: \"" + installation.getName() + "\"");
	}

	
	
	// ***************
	// *** Methods ***
	// ***************



	/**
	 * References checked: 28.11.2004 (2.0b4)
	 *
	 * @throws	DeepaMehtaException	if an error accurrs while establishing access to corporate memory
	 *
	 * @see		DeepaMehtaServer#main
	 * @see		DeepaMehtaServlet#init
	 * @see		DeepaMehta#initApplication
	 * @see		DeepaMehta#init
	 */
	public static ApplicationService create(ApplicationServiceHost host, ApplicationServiceInstance instance)
																			throws DeepaMehtaException {
		ApplicationService.applicationServiceInstance = instance;
		// ### compare to client.DeepaMehta.createApplicationService()
		// ### compare to service.DeepaMehtaServer.main()
		logger.info("DeepaMehta Application Service\n" +
			"    version: " + SERVER_VERSION + "\n" +
			"    standard topics version: " + LiveTopic.kernelTopicsVersion + "\n" +
			"    communication: " + host.getCommInfo() + "\n" +
			"    selected instance: \"" + instance.name + "\"\n" +
			"Corporate Memory\n" +
			"    implementation: \"" + instance.cmClass + "\"");
		// establish access to corporate memory
		CorporateMemory cm = instance.createCorporateMemory();	// throws DME
		// create application service
		ApplicationService as = new ApplicationService(host, cm);
		// set authorisation source
		as.setAuthentificationSourceTopic();
		// start statictics thread, basically to keep the CM connection alive,
		// compare to DataSourceTopic.startIdleThread()
		as.statisticsThread = as.new StatisticsThread();
		int rate = 5 * 60 * 1000;	// interval is 5 min.
		new Timer().scheduleAtFixedRate(as.statisticsThread, rate, rate);
		//
		return as;
	}

	/**
	 * @see		DeepaMehtaServer#main
	 * @see		DeepaMehta#createApplicationService
	 */
	public void setAuthentificationSourceTopic() throws TopicInitException {
		logger.info("setting authentification source ... ");
		BaseTopic auth = cm.getTopic("t-useraccounts", 1);	// ### hardcoded
		this.authSourceTopic = (AuthentificationSourceTopic) getLiveTopic(auth);
		// Note: just called to report the current authentification method
		// ### do non-CM authentification methods proper reporting?
		getLoginCheck();
	}

	public ApplicationServiceHost getHostObject() {
		return host;
	}

	public void shutdown() {
		cm.release();
	}

	// ---

	/**
	 * @see		#createLiveTopic
	 *
	 * Overrides {@link de.deepamehta.BaseTopicMap#addTopic(Topic topic)}
	 * to hash a {@link LiveTopic} based on ID and version.
	 */
	public void addTopic(Topic topic) {
		BaseTopic bt = (BaseTopic) topic;
		String key = bt.getID() + ":" + bt.getVersion();
		addTopic(key, topic);
	}

	/**
	 * @see		#createLiveAssociation
	 *
	 * Overrides {@link de.deepamehta.BaseTopicMap#addAssociation(Association association)}
	 * to hash a {@link LiveAssociation} based on ID and version.
	 */
	public void addAssociation(Association association) {
		BaseAssociation assoc = (BaseAssociation) association;
		String key = assoc.getID() + ":" + assoc.getVersion();
		addAssociation(key, association);
	}



	// -------------------------------------------------
	// --- Accessing Live Topics / Live Associations ---
	// -------------------------------------------------



	// --- getLiveTopic (4 forms) ---

	public LiveTopic getLiveTopic(String topicID, int version) throws DeepaMehtaException {
		return getLiveTopic(topicID, version, null, null);
	}

	public LiveTopic getLiveTopic(String topicID, int version, Session session, CorporateDirectives directives) throws DeepaMehtaException {
		// error check
		if (topicID == null) {
			throw new DeepaMehtaException("null passed as topic ID");
		}
		//
		return checkLiveTopic(topicID, version, session, directives);
	}

	public LiveTopic getLiveTopic(BaseTopic topic) throws DeepaMehtaException {
		return getLiveTopic(topic, null, null);
	}

	public LiveTopic getLiveTopic(BaseTopic topic, Session session, CorporateDirectives directives) throws DeepaMehtaException {
		// error check
		if (topic == null) {
			throw new DeepaMehtaException("null passed instead a BaseTopic");
		}
		//
		return getLiveTopic(topic.getID(), topic.getVersion(), session, directives);
	}

	// --- getLiveAssociation (2 forms) ---

	public LiveAssociation getLiveAssociation(BaseAssociation assoc) throws DeepaMehtaException {
		if (assoc == null) {
			throw new DeepaMehtaException("null passed instead a BaseAssociation");
		}
		return getLiveAssociation(assoc.getID(), assoc.getVersion());
	}

	/**
	 * @see		#changeAssociationType
	 * @see		#deleteLiveAssociation
	 */
	public LiveAssociation getLiveAssociation(String id, int version) throws DeepaMehtaException {
		LiveAssociation assoc = null;
		try {
			// Note: getAssociation() is from BaseTopicMap, throws DME
			assoc = (LiveAssociation) getAssociation(id + ":" + version);
		} catch (DeepaMehtaException e) {
			throw new DeepaMehtaException("association \"" + id + ":" + version + "\" not loaded");
		}
		return assoc;
	}

	// ---

	/**
	 * References checked: 24.6.2008 (2.0b8)
	 *
	 * @param	session		passed to init() hook (init levels 1-3)
	 *
	 * @see		#getLiveTopic(String id, int version, Session session, CorporateDirectives directives)
	 */
	private LiveTopic checkLiveTopic(String id, int version, Session session, CorporateDirectives directives)
																	throws DeepaMehtaException, TopicInitException {
		// >>> compare to initTopics()
		if (!liveTopicExists(id, version)) {
			try {
				BaseTopic topic = cm.getTopic(id, version);
				if (topic == null) {
					throw new DeepaMehtaException("topic \"" + id + "\" is missing in corporate memory");
				}
				createLiveTopic(topic, false, session);		// ### process returned directives ### throws TIE
				// Note: the topic is not evoked here
				initTopic(topic, INITLEVEL_2, session);
				initTopic(topic, INITLEVEL_3, session);
			} catch (TopicInitException e) {
				logger.log(Level.SEVERE, "Error loading topic ...", e);
			}
		}
		//
		try {
			// Note: getTopic() is from BaseTopicMap
			return (LiveTopic) getTopic(id + ":" + version);	// throws DME ### can not happen anymore
		} catch (DeepaMehtaException e) {
			throw new DeepaMehtaException("topic \"" + id + ":" + version + "\" not loaded");
		}
	}

	// ---

	/**
	 * @see		#deleteAssociation(String assocID, int version)
	 */
	private LiveAssociation checkLiveAssociation(String assocID, int version, Session session, CorporateDirectives directives) {
		if (!liveAssociationExists(assocID, version)) {
			BaseAssociation assoc = cm.getAssociation(assocID, version);
			// error check
			if (assoc == null) {
				throw new DeepaMehtaException("association \"" + assocID + ":" + version + "\" not in corporate memory");
			}
			//
			createLiveAssociation(assoc, false, false, session, directives);
		}
		return getLiveAssociation(assocID, version);
	}



	// -----------------------
	// --- Creating Topics ---
	// -----------------------



	/**
	 * ### still usefull?
	 *
	 * @return	The number of created topics
	 *
	 * @see		CorporateTopicMap#createLiveTopics
	 */
	int createLiveTopics(BaseTopicMap topicmap, CorporateDirectives directives, Session session) {
		return createLiveTopics(topicmap.getTopics().elements(), directives, session);
	}

	// Note: there are also 2 private createLiveTopics() methods

	// --- createLiveTopic (5 forms) ---

	/**
	 * Instantiates a {@link de.deepamehta.topics.LiveTopic} based on the specified {@link de.deepamehta.BaseTopic},
	 * stores it in the live corporate memory, and finally triggers the init() hook at init level 1.
	 * <p>
	 * Note: this is a private method. The application developer is supposed to use the other forms of this method, which
	 * performs full initialization in terms of triggering the evoke() hook and the init() hook at init levels 2 and 3.
	 * <p>
	 * If a topic with same ID and version already exists in live corporate memory and the
	 * <code>override</code> parameter is set to <code>false</code> nothing is performed.
	 * <p>
	 * If the topic has a custom implementation the corresponmding class is instantiated (a direct or indirect subclass of
	 * {@link de.deepamehta.topics.LiveTopic}), the actual classname is determined by the "Implementing Class" property of
	 * the corresponding type topic. If the "Implementing Class" property is empty, there is no custom implementation and
	 * a generic {@link de.deepamehta.topics.LiveTopic} is instantiated.
	 * <p>
	 * ### should return a LiveTopic and have "directives" as a parameter<br>
	 * <p>
	 * References checked: 3.7.2008 (2.0b8)
	 *
	 * @param	session		passed to init() hook (init level 1)
	 *
	 * @return	If the topic has not been created because it exists already in live corporate memory <code>null</code> is
	 *			returned. Otherwise possible error directives resulted from creation of an active topic are returned (may
	 *			be emtpy). Note: if a live topic without custom implementation was created always empty CorporateDirectives
	 *			are returned.
	 *
	 * @see		#checkLiveTopic
	 * @see		#createLiveTopic
	 * @see		#createLiveTopics
	 */
	private CorporateDirectives createLiveTopic(BaseTopic topic, boolean override, Session session) throws TopicInitException {
		// ### error check
		if (topic == null) {
			throw new DeepaMehtaException("null is passed instead a BaseTopic");
		}
		//
		String topicID = topic.getID();
		int version = topic.getVersion();
		// --- check weather live topic already exists ---
		if (!override && liveTopicExists(topicID, version)) {
			// topic exists and is not supposed to be overridden
			return null;
		}
		//
		CorporateDirectives directives = new CorporateDirectives();
		// --- instantiate live topic ---
		String implementingClass = getImplementingClass(topic);
		LiveTopic newTopic = createCustomLiveTopic(topic, implementingClass, directives);
		// --- store in live corporate memory ---
		addTopic(newTopic);
		// --- trigger init() hook ---
		directives.add(newTopic.init(INITLEVEL_1, session));	// throws TopicInitException
		//
		return directives;
	}

	// 5 Utility wrappers for createLiveTopic() above

	public LiveTopic createLiveTopic(String topicID, String typeID, String name, Session session) {
		// ### directives are ignored ### must not null
		return createLiveTopic(topicID, typeID, name, null, null, session, new CorporateDirectives());	// topicmapID=null, viewmode=null
	}

	public LiveTopic createLiveTopic(BaseTopic topic, Session session, CorporateDirectives directives) {
		return createLiveTopic(topic, null, null, session, directives);		// topicmapID=null, viewmode=null
	}

	/**
	 * @see		de.deepamehta.topics.TopicMapTopic#searchByTopicType
	 */
	public LiveTopic createLiveTopic(BaseTopic topic, String topicmapID, String viewmode,
																			Session session, CorporateDirectives directives) {
		return createLiveTopic(topic.getID(), topic.getType(), topic.getName(), topicmapID, viewmode, session, directives);
	}

	/**
	 * @see		#changeTopicType
	 * @see		de.deepamehta.topics.TopicTypeTopic#createContainerType
	 */
	public LiveTopic createLiveTopic(String topicID, String typeID, String name, String topicmapID, String viewmode,
																			Session session, CorporateDirectives directives) {
		return createLiveTopic(topicID, typeID, name, true, true, topicmapID, viewmode, session, directives);
		// override=true, evoke=true
	}

	/**
	 * Creates a topic in corporate memory and loads it into memory.
	 * The topic is fully inited (evoke() and init() hooks are triggered).
	 * All Exceptions are catched.
	 * <p>
	 * References checked: 6.7.2002 (2.0a15-pre9)
	 *
	 * @param	topicmapID	passed to evoke() hook
	 * @param	viewmode	passed to evoke() hook
	 * @param	session		passed to init() and evoke() hooks
	 */
	public LiveTopic createLiveTopic(String topicID, String typeID, String name, boolean override, boolean evoke,
								String topicmapID, String viewmode, Session session, CorporateDirectives directives) {
		LiveTopic newTopic = null;
		try {
			BaseTopic topic = new BaseTopic(topicID, 1, typeID, 1, name);
			// --- create and init(1) ---
			CorporateDirectives d = createLiveTopic(topic, override, session);		// throws TIE
			if (d != null) {
				directives.add(d);
			}
			//
			newTopic = getLiveTopic(topic);
			// --- evoke ---
			// ### Note: a topic is evoked also if init(1) fails. Consider this case: interactively created datasource topics
			// have no URL and driver set, thus the datasource can't be opened and init(1) will fail. However he datasource must
			// be evoked() to get stored in corporate memory.
			// ### Note 2: consider this case: reveal a topic from an ElementContainer will trigger evoke, but the topic could
			// already be in CM if revealed before
			if (evoke) {
				try {
					directives.add(newTopic.evoke(session, topicmapID, viewmode));	// DME, ASE
				} catch (DeepaMehtaException e) {
					logger.log(Level.SEVERE, "Error evoking topic ...", e);
					directives.add(DIRECTIVE_SHOW_MESSAGE, e.getMessage(), new Integer(NOTIFICATION_ERROR));
				} catch (AmbiguousSemanticException e) {
					logger.log(Level.SEVERE, "Error evoking topic ...", e);
					directives.add(DIRECTIVE_SHOW_MESSAGE, e.getMessage(), new Integer(NOTIFICATION_ERROR));
				}
			}
			// --- init(2) and init(3) ---
			directives.add(newTopic.init(INITLEVEL_2, session));	// throws TopicInitException
			directives.add(newTopic.init(INITLEVEL_3, session));	// throws TopicInitException
		} catch (TopicInitException e) {
			System.out.println("*** ApplicationService.createLiveTopic(): " + e);
			directives.add(DIRECTIVE_SHOW_MESSAGE, e.getMessage(), new Integer(NOTIFICATION_ERROR));
		}
		return newTopic;
	}

	// ---

	/**
	 * Builds the directives to create and show a topic at a given coordinate.
	 * <p>
	 * Called to handle the <code>CMD_CREATE_TOPIC</code> command.<br>
	 * Can also be called by custom applications.
	 * <p>
	 * References checked: 13.9.2008 (2.0b8)
	 *
	 * @see		de.deepamehta.topics.TopicMapTopic#createTopic
	 */
	public CorporateDirectives createTopic(String topicID, String typeID, int x, int y, String topicmapID,
																		Session session) throws TopicInitException {
		CorporateDirectives directives = new CorporateDirectives();
		PresentableTopic topic = new PresentableTopic(topicID, 1, typeID, 1, "", new Point(x, y));	// name=""
		directives.add(DIRECTIVE_SHOW_TOPIC, topic, Boolean.TRUE);									// evoke=TRUE
		//
		return directives;
	}

	/**
	 * Builds the directives to create and show an association.
	 * <p>
	 * Called to handle the <code>CMD_CREATE_ASSOC</code> command.<br>
	 * Can also be called by custom applications.
	 * <p>
	 * References checked: 13.9.2008 (2.0b8)
	 *
	 * @see		de.deepamehta.topics.TopicMapTopic#executeCommand
	 */
	public CorporateDirectives createAssociation(String typeID, String topicID1, String topicID2, Session session) {
		CorporateDirectives directives = new CorporateDirectives();
		//
		// check weather retyping is allowed and which type is finally to be used
		typeID = triggerAssociationAllowed(typeID, topicID1, topicID2, session, directives);
		if (typeID != null) {
			String assocID = getNewAssociationID();
			PresentableAssociation assoc = new PresentableAssociation(assocID, 1, typeID, 1, "", topicID1, 1, topicID2, 1);
			directives.add(DIRECTIVE_SHOW_ASSOCIATION, assoc, Boolean.TRUE);
			directives.add(DIRECTIVE_SELECT_ASSOCIATION, assocID);
		}
		//
		return directives;
	}

	// ---

	/**
	 * Creates a topic with specified type and name directly in corporate memory and returns the topic ID.
	 * Before an existence check is performed. If the topic exists already its ID is returned.
	 * <p>
	 * ### Note: the topic is NOT evoked here -> can be only called for types which have no evoke() implementation
	 * ### should create a live topic instead
	 * ### This method is to be dropped
	 * <p>
	 * References checked: 21.3.2003 (2.0a18-pre7)
	 *
	 * @see		de.deepamehta.topics.WebpageTopic#propertiesChanged
	 */
	public String createTopic(String typeID, String name) {
		// check if topic already exists in corporate memory -- the check is based on type and name
		Vector topics = cm.getTopics(typeID, name);
		int count = topics.size();
		if (count == 0) {
			// create topic
			String topicID = getNewTopicID();
			cm.createTopic(topicID, 1, typeID, 1, name);
			cm.setTopicData(topicID, 1, PROPERTY_NAME, name);	// ### only standard naming behavoir here
			return topicID;
		} else {
			// topic exists already
			BaseTopic topic = (BaseTopic) topics.firstElement();
			if (count > 1) {
				System.out.println("*** ApplicationService.createTopic(): there're " + count + " \"" + name + "\" (" + typeID + ") topics");
			}
			return topic.getID();
		}
	}

	/**
	 * Creates an association with specified type and topic IDs directly in corporate memory and returns the association ID.
	 * Before an existence check is performed. If the association exists already its ID is returned.
	 * <p>
	 * ### no evoke() is triggered ### method is to be dropped
	 *
	 * @see		de.deepamehta.topics.WebpageTopic#propertiesChanged
	 */
	public String createAssociation(String typeID, String topicID1, String topicID2) {
		BaseAssociation assoc = cm.getAssociation(typeID, topicID1, topicID2);
		if (assoc == null) {
			// create association
			String assocID = getNewAssociationID();
			cm.createAssociation(assocID, 1, typeID, 1, topicID1, 1, topicID2, 1);
			return assocID;
		} else {
			// association exists already
			return assoc.getID();
		}
	}

	// ---

	// ### bypasses the application service
	public final void toggleAssociation(String topicID1, String topicID2, String assocTypeID) {
		BaseAssociation assoc = cm.getAssociation(assocTypeID, topicID1, topicID2);
		if (assoc != null) {
			cm.deleteAssociation(assoc.getID());
		} else {
			String assocID = getNewAssociationID();
			cm.createAssociation(assocID, 1, assocTypeID, 1, topicID1, 1, topicID2, 1);
		}
	}



	// -----------------------------
	// --- Creating Associations ---
	// -----------------------------



	// --- createLiveAssociations (2 forms) ---

	/**
	 * @return	the number of created associations.
	 *
	 * @see		CorporateTopicMap#createLiveAssociations
	 */
	void createLiveAssociations(BaseTopicMap topicmap, Session session, CorporateDirectives directives) {
		createLiveAssociations(topicmap.getAssociations().elements(), false, session, directives);
	}

	/**
	 * @return	the number of created associations.
	 *
	 * @see		#createLiveAssociations(PresentableTopicMap)
	 * @see		CorporateDirectives#createLiveAssociations
	 */
	void createLiveAssociations(Enumeration assocs, boolean evoke, Session session, CorporateDirectives directives) {
		while (assocs.hasMoreElements()) {
			BaseAssociation assoc = (BaseAssociation) assocs.nextElement();
			createLiveAssociation(assoc, false, evoke, session, directives);
		}
	}

	// --- createLiveAssociation (4 forms) ---

	/**
	 * Instantiates a {@link de.deepamehta.assocs.LiveAssociation} based on the specified {@link de.deepamehta.BaseAssociation},
	 * stores it in the live corporate memory, and finally triggers its evoke() hook provided the evoke flag is set.
	 * <p>
	 * References checked: 25.3.2008 (2.0b8)
	 *
	 * @param	directives	may be <code>null</code>
	 *
	 * @see 	#checkLiveAssociation(BaseAssociation, Session, CorporateDirectives)																false	false
	 * @see 	#checkLiveAssociation(String assocID, int version, Session, CorporateDirectives)													false	false
	 * @see 	#createLiveAssociations(Enumeration, boolean evoke, Session, CorporateDirectives)													false	variable
	 * @see 	#createLiveAssociation(String assocID, String typeID, String name, String topicID1, String topicID2, Session, CorporateDirectives)	false	true
	 * @see 	CorporateDirectives#showAssociation																									false	variable
	 * @see 	CorporateDirectives#changeAssociationType																							true	true
	 */
	LiveAssociation createLiveAssociation(BaseAssociation assoc, boolean override, boolean evoke, Session session,
																								CorporateDirectives directives) {
		// ### compare to createLiveTopic(), topicmapID, viewmode, session, directives parameters?
		if (!override && liveAssociationExists(assoc.getID(), assoc.getVersion())) {
			// association exists and is not supposed to be overridden
			return null;
		}
		// --- instantiate live association ---
		String implementingClass = type(assoc).getImplementingClass();
		LiveAssociation newAssoc = createCustomLiveAssociation(assoc, implementingClass, directives);
		// --- store in live corporate memory ---
		addAssociation(newAssoc);
		//
		if (evoke) {
			// --- trigger evoke() hook ---
			newAssoc.evoke();
			// --- trigger associated() hook ---
			String topicID1 = assoc.getTopicID1();
			String topicID2 = assoc.getTopicID2();
			getLiveTopic(topicID1, 1).associated(assoc.getType(), topicID2, session, directives);
			getLiveTopic(topicID2, 1).associated(assoc.getType(), topicID1, session, directives);
		}
		//
		return newAssoc;
	}

	// 3 utility wrapper for createLiveAssociation() above.

	/**
	 * Creates a new association. All hooks are triggered.
	 * <p>
	 * Utility method for the application developer.
	 * <p>
	 * References checked: 27.9.2007 (2.0b8)
	 *
	 * @see 	de.deepamehta.topics.EmailTopic#evoke
	 * @see 	de.deepamehta.service.web.DeepaMehtaServlet#processForm(String typeID, String assocID, Hashtable params,
	 *														boolean doCreate, String topicID1, String topicID2, Session session)
	 */
	public LiveAssociation createLiveAssociation(String assocID, String typeID, String topicID1, String topicID2,
																				Session session, CorporateDirectives directives) {
		return createLiveAssociation(assocID, typeID, "", topicID1, topicID2, session, directives);
	}

	public LiveAssociation createLiveAssociation(String assocID, String typeID, String name, String topicID1, String topicID2,
																				Session session, CorporateDirectives directives) {
		BaseAssociation assoc = new BaseAssociation(assocID, 1, typeID, 1, name, topicID1, 1, topicID2, 1);
		return createLiveAssociation(assoc, session, directives);
	}

	public LiveAssociation createLiveAssociation(BaseAssociation assoc, Session session, CorporateDirectives directives) {
		return createLiveAssociation(assoc, false, true, session, directives);		// override=false, evoke=true
	}



	// --------------------------------------
	// --- Navigating in Corporate Memory ---
	// --------------------------------------



	/**
	 * @see		InteractionConnection#performRevealTopictypes
	 */
	Hashtable revealTopicTypes(String topicID, int version) {
		try {
			// --- trigger revealTopicTypes() hook ---
			return getLiveTopic(topicID, version).revealTopicTypes();
		} catch (DeepaMehtaException e) {
			// ### there is no way to put something to directives (for this request there
			// are no directives send back, but a vector of strings)
			System.out.println("*** ApplicationService.revealTopictypes(): " + e +
				" -- topic types not available");
			e.printStackTrace();
			return new Hashtable();
		}
	}

	/**
	 * @see		InteractionConnection#performRevealAssoctypes
	 */
	Hashtable revealAssociationTypes(String topicID, int version) {
		try {
			// --- trigger revealAssociationTypes() hook ---
			return getLiveTopic(topicID, version).revealAssociationTypes();
		} catch (DeepaMehtaException e) {
			// ### there is no way to put something to directives (for this request there
			// are no directives send back, but a vector of strings)
			System.out.println("*** ApplicationService.revealAssoctypes(): " + e +
				" -- association types not available");
			return new Hashtable();
		}
	}

	// --- getRelatedTopic (3 forms) ---

	public BaseTopic getRelatedTopic(String topicID, String assocTypeID, int relTopicPos) throws
											DeepaMehtaException, AmbiguousSemanticException {
		return getRelatedTopic(topicID, assocTypeID, null, relTopicPos, false);		// emptyAllowed=false
	}

	public BaseTopic getRelatedTopic(String topicID, String assocTypeID, int relTopicPos, boolean emptyAllowed) throws
											DeepaMehtaException, AmbiguousSemanticException {
		return getRelatedTopic(topicID, assocTypeID, null, relTopicPos, emptyAllowed);
	}

	/**
	 * Parametric semantic.
	 * <p>
	 * References checked: 5.5.2002 (2.0a15-pre1)
	 *
	 * @throws	DeepaMehtaException			if no matching topics were found
	 *										in corporate memory
	 * @throws	AmbiguousSemanticException	if more than one matching topics were found
	 *										in corporate memory
	 *
	 * @see		#getUserPreferences
	 * @see		#getExportFormat
	 * @see		#getMembershipType
	 */
	public BaseTopic getRelatedTopic(String topicID, String assocTypeID, String relTopicTypeID,
											int relTopicPos, boolean emptyAllowed) throws
											DeepaMehtaException, AmbiguousSemanticException {
		Vector topics = cm.getRelatedTopics(topicID, assocTypeID, relTopicTypeID, relTopicPos);
		// error check 1
		if (topics.size() == 0) {
			if (!emptyAllowed) {
				throw new DeepaMehtaException("Topic \"" + topicID + "\" has no " +
					"related \"" + relTopicTypeID + "\" topic (assoc type \"" + assocTypeID +
					"\", topic pos " + relTopicPos + ")");
			}
			return null;
		}
		//
		BaseTopic topic = (BaseTopic) topics.firstElement();
		// error check 2
		if (topics.size() > 1) {
			throw new AmbiguousSemanticException("Topic \"" + topicID + "\" has " +
				topics.size() + " related \"" + relTopicTypeID + "\" topics (assoc " +
				"type \"" + assocTypeID + "\", topic pos " + relTopicPos + ") -- only " +
				topic + " is considered", topic);
		}
		//
		return topic;
	}

	// --- getRelatedTopics (4 forms) ---

	/**
	 * @return	Vector of {@link de.deepamehta.BaseTopic}
	 *
	 * @see		LiveTopic#getSuperTopic
	 * @see		de.deepamehta.topics.PropertyTopic#init
	 * @see		de.deepamehta.topics.DataConsumerTopic#setDataSource
	 */
	public Vector getRelatedTopics(String topicID, String assocTypeID, int relTopicPos) {
		return cm.getRelatedTopics(topicID, assocTypeID, relTopicPos);
	}

	/**
	 * @return	Vector of {@link de.deepamehta.BaseTopic}
	 */
	public Vector getRelatedTopics(String topicID, String assocTypeID, String relTopicTypeID, int relTopicPos) {
		return cm.getRelatedTopics(topicID, assocTypeID, relTopicTypeID, relTopicPos);
	}

	/**
	 * References checked: 10.8.2001 (2.0a11)
	 *
	 * @return	Vector of {@link de.deepamehta.BaseTopic}
	 *
	 * @see		de.deepamehta.topics.TypeTopic#makeTypeDefinition
	 * @see		de.deepamehta.topics.PropertyTopic#setPropertyDefinition
	 */
	public Vector getRelatedTopics(String topicID, String assocTypeID, String relTopicTypeID, int relTopicPos,
																				boolean sortAssociations) {
		return cm.getRelatedTopics(topicID, assocTypeID, relTopicTypeID, relTopicPos, sortAssociations);
	}

	/**
	 * @param	relTopicTypeID	the ID of the topic type to match,
	 *							if <code>null</code> is passed all topic types are matching
	 *
	 * @return	the topics as vector of {@see de.deepamehta.BaseTopic}s
	 *
	 * @throws	DeepaMehtaException	if no matching topics were found in corporate
	 *							memory while <code>emptyAllowed</code> is not set.
	 */
	public Vector getRelatedTopics(String topicID, String assocTypeID,
									String relTopicTypeID, int relTopicPos,
									boolean sortAssociations, boolean emptyAllowed)
									throws DeepaMehtaException {
		Vector topics = cm.getRelatedTopics(topicID, assocTypeID, relTopicTypeID, relTopicPos, sortAssociations);
		// error check
		if (!emptyAllowed && topics.size() == 0) {
			throw new DeepaMehtaException("Topic \"" + topicID + "\" has no " +
				"related \"" + relTopicTypeID + "\" topics (assoc type \"" + assocTypeID +
				"\", topic pos " + relTopicPos + ", " + (sortAssociations ? "" : "not ") +
				"ordered, empty " + (emptyAllowed ? "" : "not ") + "allowed)");
		}
		//
		return topics;
	}

	// --- createNewContainer (2 forms) ---

	/**
	 * Creates a container that results from a "Search" command (### this container has a standard name).
	 * <p>
	 * References checked: 6.8.2008 (2.0b8)
	 * <p>
	 * <table>
	 * <tr><td><b>Called by</b></td>												<td><code>evokeContent</code></td></tr>
	 * <tr><td>{@link #performGoogleSearch}</td>									<td><code>false</code></td></tr>
	 * <tr><td>{@link de.deepamehta.topics.TopicContainerTopic#performQuery}</td>	<td><code>false</code></td></tr>
	 * <tr><td>{@link de.deepamehta.topics.ElementContainerTopic#performQuery}</td>	<td><code>true</code></td></tr>
	 * </table>
	 */
	public CorporateDirectives createNewContainer(LiveTopic relatedTopic, String containerTypeID, String nameFilter,
											Hashtable propertyFilter, String relatedTopicID, String relatedTopicSemantic,
											int topicCount, Vector topics, boolean evokeContent) {
		return createNewContainer(relatedTopic, containerTypeID, nameFilter, propertyFilter, relatedTopicID,
			relatedTopicSemantic, topicCount, topics, evokeContent, null, true);	// name=null, revealContent=true
	}

	/**
	 * Creates a container that results from a "What's related?" command (### this container has a custom name).
	 * <p>
	 * Creates and returns the client directives to perform the following task: create a new container, associate
	 * the container with the specified <code>relatedTopic</code> by means of an association of type
	 * {@link #SEMANTIC_CONTAINER_HIERARCHY} and reveal the contents of the container if they are sufficiently small.
	 * "Revealing the contents" means showing the contained topics and associating them with the container by means
	 * of associations of type {@link #SEMANTIC_CONTAINER_HIERARCHY}.
	 * <p>
	 * If the <code>relatedTopic</code> is itself a container and it has the same content, no new container is created,
	 * but the content is revealed as described.
	 * <p>
	 * References checked: 6.8.2008 (2.0b8)
	 * <p>
	 * <table>
	 * <tr><td><b>Called by</b></td>													<td><code>evokeContent</code></td></tr>
	 * <tr><td>{@link de.deepamehta.topics.LiveTopic#navigateByTopictype}</td>			<td><code>false</code></td></tr>
	 * <tr><td>{@link de.deepamehta.topics.LiveTopic#navigateByAssoctype}</td>			<td><code>false</code></td></tr>
	 * </table>
	 *
	 * @param	relatedTopic			the topic the container is visually associated. This can be<br>
	 *									1) another container (in case of "Search" command)<br>
	 *									2) an arbitrary topic (in case of "What's related?" command)<br>
	 *									Note: don't confuse this paramter with the relation filter
	 * @param	containerTypeID			type ID of the container to be created
	 * @param	nameFilter				name filter, if <code>null</code> no name filter is set
	 * @param	propertyFilter			property filter, if empty no property filter is set ### must not be <code>null</code>
	 * @param	relatedTopicID			relation filter, if <code>null</code> no relation filter is set
	 * @param	relatedTopicSemantic	part of relation filter, if <code>null</code> the association type doesn't matter
	 * @param	topicCount				number of topics contained in the container
	 * @param	topics					topics contained in the container (vector of {@link de.deepamehta.PresentableTopic}s),
	 *									Note: only used if topicCount <= MAX_LISTING
	 *
	 */
	public CorporateDirectives createNewContainer(LiveTopic relatedTopic, String containerTypeID, String nameFilter,
							Hashtable propertyFilter, String relatedTopicID, String relatedTopicSemantic,
							int topicCount, Vector topics, boolean evokeContent, String name, boolean revealContent) {
		CorporateDirectives directives = new CorporateDirectives();
		//
		String containerLabel = Integer.toString(topicCount);
		Hashtable containerProps = buildContainerProperties(nameFilter, propertyFilter, relatedTopicID, relatedTopicSemantic,
																							topicCount, topics, containerLabel);
		// only create container if the query has been narrowed against the query of this container
		String containerID;
		if (relatedTopic instanceof ContainerTopic &&
				((ContainerTopic) relatedTopic).equalsQuery(nameFilter, propertyFilter, relatedTopicID, relatedTopicSemantic)) {
			// nothing added to the query -- don't create new container
			logger.info("re-triggering query -- don't create new container");
			containerID = relatedTopic.getID();
			// Note: both directives stores the properties. The properties are stored twice, but is not a problem.
			directives.add(DIRECTIVE_SHOW_TOPIC_PROPERTIES, containerID, containerProps, new Integer(1));			// ### version=1
			directives.add(DIRECTIVE_SET_TOPIC_LABEL, containerID, containerLabel, new Integer(1), containerProps);	// ### version=1
		} else {
			logger.info("refining query (name filter=\"" + nameFilter + "\" property filter=" + propertyFilter +
				") -- create new container");
			// --- create new container ---
			containerID = cm.getNewTopicID();
			String containerName = containerName(name, nameFilter, propertyFilter);
			PresentableTopic containerTopic = new PresentableTopic(containerID, 1, containerTypeID, 1, containerName,
				relatedTopic.getID(), containerLabel);	// version=1, typeVersion=1
			// set properties of new container
			containerTopic.setProperties(containerProps);
			directives.add(DIRECTIVE_SHOW_TOPIC, containerTopic, Boolean.TRUE);		// evoke=TRUE
			// --- associate container with related topic ---
			PresentableAssociation assoc = createPresentableAssociation(SEMANTIC_CONTAINER_HIERARCHY,
				relatedTopic.getID(), relatedTopic.getVersion(), containerID, 1, false);
			directives.add(DIRECTIVE_SHOW_ASSOCIATION, assoc, Boolean.TRUE);
		}
		// --- reveal contents of container ---
		if (revealContent && topicCount <= MAX_REVEALING) {
			Vector assocs = createPresentableAssociations(containerID, topics, SEMANTIC_CONTAINER_HIERARCHY);
			directives.add(DIRECTIVE_SHOW_TOPICS, topics, new Boolean(evokeContent));
			directives.add(DIRECTIVE_SHOW_ASSOCIATIONS, assocs, Boolean.TRUE);
		}
		return directives;
	}

	/**
	 * Builds the (mostly hidden) properties to represent a container's query and the result rendering. These comprise of:
	 * PROPERTY_SEARCH ("Search")<br>
	 * PROPERTY_QUERY_ELEMENTS ("QueryElements"), hidden<br>
	 * PROPERTY_ELEMENT_COUNT ("ElementCount"), hidden<br>
	 * PROPERTY_RELATED_TOPIC_ID ("RelatedTopicID"), hidden<br>
	 * PROPERTY_RELATED_TOPIC_SEMANTIC ("AssociationTypeID"), hidden<br>
	 * PROPERTY_RESULT ("Result")<br>
	 * <p>
	 * References checked: 23.9.2008 (2.0b8)
	 *
	 * @see		#createNewContainer
	 */
	private Hashtable buildContainerProperties(String nameFilter, Hashtable propertyFilter, String relatedTopicID,
											String relatedTopicSemantic, int topicCount, Vector topics, String containerLabel) {
		Hashtable containerProps = (Hashtable) propertyFilter.clone();
		// query elements
		String queryElements = queryElements(propertyFilter);
		if (nameFilter != null && nameFilter.length() > 0) {
			if (queryElements.length() > 0) {
				queryElements += ",";
			}
			queryElements += PROPERTY_SEARCH;
			containerProps.put(PROPERTY_SEARCH, nameFilter);
		}
		containerProps.put(PROPERTY_QUERY_ELEMENTS, queryElements);
		containerProps.put(PROPERTY_ELEMENT_COUNT, containerLabel);
		// relation filter
		if (relatedTopicID != null) {
			containerProps.put(PROPERTY_RELATED_TOPIC_ID, relatedTopicID);
			if (relatedTopicSemantic != null) {
				containerProps.put(PROPERTY_RELATED_TOPIC_SEMANTIC, relatedTopicSemantic);
			}
		}
		// result
		containerProps.put(PROPERTY_RESULT, renderResultList(topicCount, topics));
		//
		return containerProps;
	}

	/**
	 * References checked: 12.9.2008 (2.0b8)
	 *
	 * @see		#buildContainerProperties
	 */
	private String renderResultList(int topicCount, Vector topics) {
		StringBuffer html = new StringBuffer("<html><body>");
		if (topicCount <= MAX_LISTING) {
			Enumeration e = topics.elements();
			while (e.hasMoreElements()) {
				PresentableTopic topic = (PresentableTopic) e.nextElement();
				String iconfile = getIconfile(topic);
				String id = topic.fromDatasource() ? topic.getOriginalID() : topic.getID();
				String link = "<a href=\"http://" + ACTION_REVEAL_TOPIC + "/" + id + "\">";
				html.append(link + "<img src=\"" + FILESERVER_ICONS_PATH + iconfile + "\" border=\"0\"></a> ");
				html.append(link + topic.getName() + "</a><br>");
				// Note: style="border-style: none" instead of border="0" doesn't work
			}
			html.append("<br><a href=\"http://" + ACTION_REVEAL_ALL + "\">" +
				"<img src=\"" + FILESERVER_IMAGES_PATH + BUTTON_REVEAL_ALL + "\" border=\"0\"></a>");
		} else {
			html.append("<p><i>Search result too large to be shown (" + topicCount + " entries).</i></p>");
			html.append("<p><i>Refine your search and press return inside an input field.</i></p>");
		}
		html.append("</body></html>");
		return html.toString();
	}

	// --- getTopicProperty (2 forms) ---

	// ### should be named "getProperty"
	public String getTopicProperty(BaseTopic topic, String propName) {
		return getTopicProperty(topic.getID(), topic.getVersion(), propName);
	}

	/**
	 * @see		de.deepamehta.topics.LiveTopic#getTopicProperty(String fieldName)
	 */
	public String getTopicProperty(String topicID, int version, String propName) {
		return cm.getTopicData(topicID, version, propName);
	}

	// --- getTopicProperties (2 forms) ---

	public Hashtable getTopicProperties(BaseTopic topic) {
		return getTopicProperties(topic.getID(), topic.getVersion());
	}

	/**
	 * @see		CorporateDirectives#updateCorporateMemory
	 * @see		de.deepamehta.topics.LiveTopic#getTopicProperties()
	 */
	public Hashtable getTopicProperties(String topicID, int version) {
		return cm.getTopicData(topicID, version);
	}

	// --- getAssocProperty (2 forms) ---

	public String getAssocProperty(BaseAssociation assoc, String propName) {
		return getAssocProperty(assoc.getID(), assoc.getVersion(), propName);
	}

	public String getAssocProperty(String assocID, int version, String propName) {
		return cm.getAssociationData(assocID, version, propName);
	}

	// --- getAssocProperties (2 forms) ---

	public Hashtable getAssocProperties(BaseAssociation assoc) {
		return getAssocProperties(assoc.getID(), assoc.getVersion());
	}

	public Hashtable getAssocProperties(String assocID, int version) {
		return cm.getAssociationData(assocID, version);
	}

	// ---

	/**
	 * Returns the properties to be disabled for the specified topic.
	 * <p>
	 * Triggers the {@link de.deepamehta.topics.LiveTopic#disabledProperties disabledProperties() hook}
	 * and returns the resulting property names.
	 * <p>
	 * Called for <code>DIRECTIVE_SELECT_TOPIC</code> and <code>DIRECTIVE_SELECT_TOPICMAP</code>.
	 * <p>
	 * References checked: 7.6.2007 (2.0b8)
	 *
	 * @return	A vector of property names (<code>String</code>s)
	 *
	 * @see		CorporateDirectives#updateCorporateMemory
	 */
	Vector disabledTopicProperties(String topicID, int version, Session session) {
		// --- trigger disabledProperties() hook ---
		return getLiveTopic(topicID, version).disabledProperties(session);
	}

	/**
	 * Returns the properties to be disabled for the specified association.
	 * <p>
	 * Triggers the {@link de.deepamehta.assocs.LiveAssociation#disabledProperties disabledProperties() hook}
	 * and returns the resulting property names.
	 * <p>
	 * Called for <code>DIRECTIVE_SELECT_ASSOCIATION</code>.
	 * <p>
	 * References checked: 7.6.2007 (2.0b8)
	 *
	 * @return	A vector of property names (<code>String</code>s)
	 *
	 * @see		CorporateDirectives#updateCorporateMemory
	 */
	Vector disabledAssocProperties(String assocID, int version, Session session) {
		// --- trigger disabledProperties() hook ---
		return getLiveAssociation(assocID, version).disabledProperties(session);
	}

	// ---

	Hashtable getTopicPropertyBaseURLs(String topicID, int version) {
		// --- trigger getPropertyBaseURLs() hook ---
		return getLiveTopic(topicID, version).getPropertyBaseURLs();
	}

	Hashtable getAssocPropertyBaseURLs(String assocID, int version) {
		Hashtable baseURLs = new Hashtable();
		baseURLs.put(PROPERTY_DESCRIPTION, getCorporateWebBaseURL());
		return baseURLs;
		// ### return getLiveTopic(topicID, version).getPropertyBaseURLs();
	}

	// ---

	/**
	 * @see		retypeTopicIsAllowed
	 * @see		deleteTopicIsAllowed
	 * @see		de.deepamehta.topics.LiveTopic#disabledProperties
	 */
	public boolean isTopicOwner(String topicID, Session session) {
		String userID = session.getUserID();
		return getTopicProperty(topicID, 1, PROPERTY_OWNER_ID).equals(userID);
	}

	/**
	 * @see		retypeAssociationIsAllowed
	 * @see		deleteAssociationIsAllowed
	 */
	public boolean isAssocOwner(String assocID, Session session) {
		String userID = session.getUserID();
		return getAssocProperty(assocID, 1, PROPERTY_OWNER_ID).equals(userID);
	}

	// ---

	/**
	 * Consulted while server side processing of <code>DIRECTIVE_SELECT_TOPIC</code>.
	 * <p>
	 * References checked: 18.8.2008 (2.0b8)
	 *
	 * @see		CorporateCommands#addRetypeTopicCommand
	 * @see		CorporateDirectives#updateCorporateMemory
	 */
	boolean retypeTopicIsAllowed(String topicID, int version, Session session) {
		// --- trigger retypeAllowed() hook ---
		LiveTopic topic = getLiveTopic(topicID, version);
		boolean allowed = topic.retypeAllowed(session);
		//
		boolean isSearch = type(topic).isSearchType();
		String userID = session.getUserID();
		return allowed && !isSearch &&
			(isTopicOwner(topicID, session) || isAdministrator(userID) || hasEditorRole(userID, topic.getType()));
	}

	/**
	 * Consulted while server side processing of <code>DIRECTIVE_SELECT_ASSOCIATION</code>.
	 * <p>
	 * References checked: 18.8.2008 (2.0b8)
	 *
	 * @see		CorporateCommands#addRetypeAssociationCommand
	 * @see		CorporateDirectives#updateCorporateMemory
	 */
	boolean retypeAssociationIsAllowed(String assocID, int version, Session session) {
		// ### --- trigger retypeAllowed() hook ---
		LiveAssociation assoc = getLiveAssociation(assocID, version);
		// ### boolean allowed = topic.retypeAllowed(session);
		// ### hook not yet exists for associations
		//
		boolean isSearch = type(assoc).isSearchType();
		String userID = session.getUserID();
		return !isSearch &&
			(isAssocOwner(assocID, session) || isAdministrator(userID) || hasEditorRole(userID, assoc.getType()));
	}

	// ---

	/**
	 * References checked: 18.8.2008 (2.0b8)
	 *
	 * @see		CorporateCommands#addDeleteTopicCommand
	 */
	boolean deleteTopicIsAllowed(BaseTopic topic, Session session) {
		// --- trigger deleteAllowed() hook ---
		boolean allowed = getLiveTopic(topic).deleteAllowed(session);
		//
		String userID = session.getUserID();
		return allowed &&
			(isTopicOwner(topic.getID(), session) || isAdministrator(userID) || hasEditorRole(userID, topic.getType()));
	}

	/**
	 * References checked: 18.8.2008 (2.0b8)
	 *
	 * @see		CorporateCommands#addDeleteAssociationCommand
	 */
	boolean deleteAssociationIsAllowed(BaseAssociation assoc, Session session) {
		// ### --- trigger deleteAllowed() hook ---
		// ### boolean allowed = getLiveTopic(topic).deleteAllowed(session);
		// ### hook not yet exists for associations
		//
		String userID = session.getUserID();
		return isAssocOwner(assoc.getID(), session) || isAdministrator(userID) || hasEditorRole(userID, assoc.getType());
	}

	// ---

	boolean publishIsAllowed(String userID, String workspaceID) {
		return hasRole(userID, workspaceID, PROPERTY_ROLE_PUBLISHER) || isAdministrator(userID);
	}



	// -----------------------------------------------
	// --- Set Properties (corporate memory level) ---
	// -----------------------------------------------



	// Note: there are 2 other methods who set properties at application service level
	// - setTopicProperty()
	// - setTopicProperties()

	// --- setTopicProperty (2 forms) ---

	/**
	 * Sets a topic property value directly in corporate memory.
	 * <p>
	 * Note: the application service is bypassed (no hooks are triggered) and the GUI is not updated (no directives are send).
	 * Use this method deliberately.
	 * <p>
	 * If you want set a property value at application service level use
	 * {@link #setTopicProperty(String topicID, int version, String propName, String propValue, String topicmapID, String viewmode, Session)}
	 */
	public void setTopicProperty(BaseTopic topic, String propName, String propValue) {
		setTopicProperty(topic.getID(), topic.getVersion(), propName, propValue);
	}

	/**
	 * Sets a topic property value directly in corporate memory.
	 * <p>
	 * Note: the application service is bypassed (no hooks are triggered) and the GUI is not updated (no directives are send).
	 * Use this method deliberately.
	 * <p>
	 * If you want set a property value at application service level use
	 * {@link #setTopicProperty(String topicID, int version, String propName, String propValue, String topicmapID, String viewmode, Session)}
	 *
	 * @see		LiveTopic#setProperty(String propName, String propValue)
	 */
	public void setTopicProperty(String topicID, int version, String propName, String propValue) {
		cm.setTopicData(topicID, version, propName, propValue);
	}

	// ---

	/**
	 * Sets multiple topic property values directly in corporate memory.
	 * <p>
	 * Note: the application service is bypassed (no hooks are triggered) and the GUI is not updated (no directives are send).
	 * Use this method deliberately.
	 * <p>
	 * If you want set property values at application service level use
	 * {@link #setTopicProperties(String topicID, int version, Hashtable props, String topicmapID, Session)}
	 *
	 * @see		CorporateDirectives#createLiveTopic
	 */
	public void setTopicProperties(String topicID, int version, Hashtable props) {
		cm.setTopicData(topicID, version, props);
	}

	// ---

	// Note: there is another method who set properties at application service level
	// - setAssocProperties()

	// --- setAssocProperty (2 forms) ---

	public void setAssocProperty(BaseAssociation assoc, String propName, String propValue) {
		setAssocProperty(assoc.getID(), assoc.getVersion(), propName, propValue);
	}

	public void setAssocProperty(String assocID, int version, String propName, String propValue) {
		cm.setAssociationData(assocID, version, propName, propValue);
	}

	// ---

	/**
	 * Sets multiple association property values directly in corporate memory.
	 * <p>
	 * Note: the application service is bypassed (no hooks are triggered) and the GUI is not updated (no directives are send).
	 * Use this method deliberately.
	 * <p>
	 * If you want set property values at application service level use
	 * {@link #setAssocProperties(String assocID, int version, Hashtable props, String topicmapID, String viewmode, Session)}
	 *
	 * @see		InteractionConnection#performChangeAssociationData
	 */
	void setAssocProperties(String assocID, int version, Hashtable props) {
		cm.setAssociationData(assocID, version, props);
	}



	// ------------------------
	// --- Triggering Hooks ---
	// ------------------------



	// --- initTopic (3 forms) ---

	/**
	 * @throws	TopicInitException	Exceptions occurring while topic initialization (DeepaMehtaException,
	 *								AmbiguousSemanticException) are transformed into a TopicInitException.
	 *
	 * @see		de.deepamehta.topics.LiveTopic#propertiesChanged
	 * @see		de.deepamehta.service.web.ExternalConnection#processNotification
	 */
	public void initTopic(String topicID, int version) throws TopicInitException {
		initTopic(topicID, version, INITLEVEL_1, null);
		initTopic(topicID, version, INITLEVEL_2, null);
		initTopic(topicID, version, INITLEVEL_3, null);
	}

	/**
	 * Initializes the specified live topic by triggering its
	 * {@link de.deepamehta.topics.LiveTopic#init init()} hook with the
	 * specified initialization level.
	 *
	 * @throws	TopicInitException	Exceptions occurring while topic initialization (DeepaMehtaException,
	 *								AmbiguousSemanticException) are transformed into a TopicInitException.
	 *
	 * @see		#checkLiveTopic										2x (level 2 and 3)
	 * @see		#initTopics											1x (variable)
	 */
	public void initTopic(BaseTopic topic, int initLevel, Session session) throws TopicInitException {
		initTopic(topic.getID(), topic.getVersion(), initLevel, session);
	}

	/**
	 * @throws	TopicInitException	Exceptions occurring while topic initialization (DeepaMehtaException,
	 *								AmbiguousSemanticException) are transformed into a TopicInitException.
	 */
	public void initTopic(String topicID, int version, int initLevel, Session session) throws TopicInitException {
		try {
			// --- trigger init() hook ---
			getLiveTopic(topicID, version).init(initLevel, session);	// throws TopicInitException ### directives returned by init() are ignored
		} catch (DeepaMehtaException e) {
			throw new TopicInitException(e.getMessage());
		} catch (AmbiguousSemanticException e) {
			throw new TopicInitException(e.getMessage());
		}
	}

	// ---

	/**
	 * @see		InteractionConnection#performProcessTopicCommand
	 * @see		EmbeddedService#executeTopicCommand
	 */
	public CorporateDirectives executeTopicCommand(String topicID, int version, String command,
											String topicmapID, String viewmode, Session session) {
		LiveTopic topic = null;
		try {
			topic = getLiveTopic(topicID, version);
			// --- trigger executeCommand() hook ---
			return topic.executeCommand(command, session, topicmapID, viewmode);
		} catch (DeepaMehtaException e) {
			String errText = "Topic " + topic + " can't execute command \"" + command +
				"\" (" + e.getMessage() + ")";
			System.out.println("*** ApplicationService.executeTopicCommand(): " + errText);
			e.printStackTrace();
			// if a DeepaMehtaException is thrown by the executeCommand hook
			// only one directive is send to the client
			CorporateDirectives directives = new CorporateDirectives();
			directives.add(DIRECTIVE_SHOW_MESSAGE, errText, new Integer(NOTIFICATION_ERROR));
			return directives;
		}
	}

	/**
	 * @see		InteractionConnection#performProcessAssociationCommand
	 * @see		EmbeddedService#executeAssocCommand
	 */
	public CorporateDirectives executeAssociationCommand(String assocID, int version, String command,
											String topicmapID, String viewmode, Session session) {
		LiveAssociation assoc = null;
		try {
			// --- trigger executeCommand() hook ---
			assoc = getLiveAssociation(assocID, version);
			return assoc.executeCommand(command, session, topicmapID, viewmode);
		} catch (DeepaMehtaException e) {
			String errText = "Association " + assoc + " can't execute command \"" + command +
				"\" (" + e.getMessage() + ")";
			System.out.println("*** ApplicationService.executeAssociationCommand(): " + errText);
			// if a DeepaMehtaException is thrown by the executeCommand hook
			// only one directive is returned
			CorporateDirectives directives = new CorporateDirectives();
			directives.add(DIRECTIVE_SHOW_MESSAGE, errText, new Integer(NOTIFICATION_ERROR));
			return directives;
		}
	}

	// ---

	/**
	 * @see		InteractionConnection#executeChainedTopicCommand
	 * @see		EmbeddedService#executeChainedTopicCommand
	 */
	public CorporateDirectives executeChainedTopicCommand(String topicID, int version,
						String command, String result, String topicmapID, String viewmode,
						Session session) {
		LiveTopic topic = null;
		try {
			// --- trigger executeChainedCommand() hook ---
			topic = getLiveTopic(topicID, version);
			return topic.executeChainedCommand(command, result, topicmapID, viewmode, session);
		} catch (DeepaMehtaException e) {
			String errText = "Topic " + topic + " can't execute chained command \"" + command +
				"\" (" + e.getMessage() + ")";
			System.out.println("*** ApplicationService.executeChainedTopicCommand(): " + errText);
			// if a DeepaMehtaException is thrown by the executeChainedCommand hook
			// only one directive is returned
			CorporateDirectives directives = new CorporateDirectives();
			directives.add(DIRECTIVE_SHOW_MESSAGE, errText, new Integer(NOTIFICATION_ERROR));
			return directives;
			// ### parametric notification needed
			// ### e.g. NOTIFICATION_ERROR: "no chained command handler implemented"
		}
	}

	/**
	 * @see		InteractionConnection#performExecuteChainedAssociationCommand
	 * @see		EmbeddedService#executeChainedAssocCommand
	 */
	public CorporateDirectives executeChainedAssociationCommand(String assocID, int version,
						String command, String result, String topicmapID, String viewmode, Session session) {
		LiveAssociation assoc = null;
		try {
			// --- trigger executeChainedCommand() hook ---
			assoc = getLiveAssociation(assocID, version);
			return assoc.executeChainedCommand(command, result, topicmapID, viewmode, session);
		} catch (DeepaMehtaException e) {
			String errText = "Association " + assoc + " can't execute chained command \"" +
				command + "\" (" + e.getMessage() + ")";
			System.out.println("*** ApplicationService.executeChainedAssociationCommand(): " + errText);
			// if a DeepaMehtaException is thrown by the executeChainedCommand hook
			// only one directive is returned
			CorporateDirectives directives = new CorporateDirectives();
			directives.add(DIRECTIVE_SHOW_MESSAGE, errText, new Integer(NOTIFICATION_ERROR));
			return directives;
			// ### parametric notification needed
			// ### e.g. NOTIFICATION_ERROR: "no chained command handler implemented"
		}
	}

	// ---

	/**
	 * Returns the directives to delete the specified topic as well as any associations
	 * this topic is involed in.
	 * <p>
	 * Used by DeepaMehtaServlet.
	 */
	public CorporateDirectives deleteTopic(String topicID, int version) {
		CorporateDirectives directives = new CorporateDirectives();
		//
		LiveTopic topic = getLiveTopic(topicID, version);
		topic.delete(null, null, directives);
		//
		return directives;
	}

	// --- deleteAssociation (2 forms) ---

	/**
	 * @see		#removeViewsInUse
	 */
	public CorporateDirectives deleteAssociation(BaseAssociation assoc) {
		return deleteAssociation(assoc.getID(), assoc.getVersion(), null);
	}

	/**
	 * The server side processing of {@link #DIRECTIVE_HIDE_ASSOCIATION} resp. {@link #DIRECTIVE_HIDE_ASSOCIATIONS}.
	 * <p>
	 * ### Should not be called directly by the application developer. Should be package private.
	 * <p>
	 * ### Deletes the specified association and returns the resulting directives.
	 * <p>
	 * ### The association is deleted by triggering its {@link de.deepamehta.assocs.LiveAssociation#die die()} hook.
	 * The association is loaded first, if necessary.
	 *
	 * @see		#deleteAssociation(BaseAssociation)
	 * @see		CorporateDirectives#updateCorporateMemory
	 * @see		CorporateDirectives#deleteLiveAssociations
	 */
	public CorporateDirectives deleteAssociation(String assocID, int version, Session session) {
		CorporateDirectives directives = new CorporateDirectives();
		try {
			LiveAssociation assoc = checkLiveAssociation(assocID, version, null, directives);
			LiveTopic topic1 = getLiveTopic(assoc.getTopicID1(), 1);
			LiveTopic topic2 = getLiveTopic(assoc.getTopicID2(), 1);
			// --- trigger die() hook ---
			directives.add(assoc.die());
			// --- trigger associationRemoved() hook ---
			topic1.associationRemoved(assoc.getType(), topic2.getID(), session, directives);
			topic2.associationRemoved(assoc.getType(), topic1.getID(), session, directives);
		} catch (DeepaMehtaException e) {
			// ### add to directives
			// System.out.println("*** ApplicationService.deleteAssociation(): " +
				// e.getMessage() + " -- die() hook not triggered");
		}
		return directives;
	}

	// ---

	/**
	 * @see		InteractionConnection#performChangeTopicName
	 */
	CorporateDirectives changeTopicNameChained(String topicID, int version, String name, Session session, String result) {
		// trigger nameChangedChained() hook
		return getLiveTopic(topicID, version).nameChangedChained(name, session, result);
	}

	// ---

	/**
	 * Triggers the nameChanged() hook of the specified association.
	 * <p>
	 * @see		#setAssocProperties
	 */
	public CorporateDirectives changeAssociationName(String assocID, int version, String name) {
		// --- trigger nameChanged() hook ---
		return getLiveAssociation(assocID, version).nameChanged(name);
	}

	// --- changeTopicType (2 forms) ---

	/**
	 * Changes the type of the specified topic. The type is specified by name.
	 * If no such type is found it is created first.
	 * <p>
	 * References checked: 9.9.2004 (2.0b3)
	 *
	 * @see		de.deepamehta.topics.LiveTopic#executeCommand		CMD_CHANGE_TOPIC_TYPE_BY_NAME
	 */
	public void changeTopicType(String topicID, int version, String typeName,
								String topicmapID, String viewmode,
								Session session, CorporateDirectives directives) {
		// create new topic type if not already exists
		String typeID = getTopicTypeID(typeName);
		if (typeID == null) {
			typeID = createLiveTopic(getNewTopicID(), TOPICTYPE_TOPICTYPE, typeName, topicmapID,
				viewmode, session, directives).getID();
			// set properties
			cm.setTopicData(typeID, 1, PROPERTY_NAME, typeName);
			cm.setTopicData(typeID, 1, PROPERTY_OWNER_ID, session.getUserID());
			System.out.println(">>> new topic type created: \"" + typeName + "\" (" + typeID + ")");
		} else {
			System.out.println(">>> retype topic " + topicID + " to \"" + typeName + "\" (" + typeID + ")");
		}
		// retype
		directives.add(changeTopicType(topicID, version, typeID, 1));	// ### typeVersion=1
	}

	/**
	 * Changes the type of the specified topic. The type is specified by ID.
	 * <p>
	 * References checked: 9.9.2004 (2.0b3)
	 *
	 * @see		#changeTopicType(String topicID, int version, String typeName, CorporateDirectives directives)
	 * @see		de.deepamehta.topics.LiveTopic#executeCommand		CMD_CHANGE_TOPIC_TYPE
	 */
	public CorporateDirectives changeTopicType(String topicID, int version, String typeID, int typeVersion) {
		// --- trigger typeChanged() hook ---
		return getLiveTopic(topicID, version).typeChanged(typeID);	// ### typeVersion not used
	}

	// --- changeAssociationType (2 forms) ---

	/**
	 * Handles the command <code>CMD_CHANGE_ASSOC_TYPE_BY_NAME</code>.
	 * <p>
	 * References checked: 28.5.2006 (2.0b6-post3)
	 *
	 * @see		de.deepamehta.assocs.LiveAssociation#executeCommand
	 */
	public void changeAssociationType(String assocID, int version, String typeName, String topicmapID, String viewmode,
																	Session session, CorporateDirectives directives) {
		String typeID = getAssocTypeID(typeName);	// ###
		// create new association type if not already exists
		if (typeID == null) {
			typeID = createLiveTopic(getNewTopicID(), TOPICTYPE_ASSOCTYPE, typeName, topicmapID,
				viewmode, session, directives).getID();
			// set properties
			cm.setTopicData(typeID, 1, PROPERTY_NAME, typeName);
			cm.setTopicData(typeID, 1, PROPERTY_OWNER_ID, session.getUserID());
			System.out.println(">>> new association type created: \"" + typeName + "\" (" + typeID + ")");
		} else {
			System.out.println(">>> retype association " + assocID + " to \"" + typeName + "\" (" + typeID + ")");
		}
		// retype
		directives.add(changeAssociationType(assocID, version, typeID, 1, session));	// typeVersion=1
	}

	/**
	 * Handles the commands <code>CMD_CHANGE_ASSOC_TYPE</code> and <code>CMD_CHANGE_ASSOC_TYPE_BY_NAME</code> (indirectly).
	 * <p>
	 * References checked: 28.5.2006 (2.0b6-post3)
	 *
	 * @see		#changeAssociationType(String assocID, int version, String typeName, String topicmapID, String viewmode,
	 *																Session session, CorporateDirectives directives)
	 * @see		de.deepamehta.assocs.LiveAssociation#executeCommand
	 */
	public CorporateDirectives changeAssociationType(String assocID, int version, String typeID, int typeVersion,
																								Session session) {
		CorporateDirectives directives = new CorporateDirectives();
		//
		LiveAssociation assoc = getLiveAssociation(assocID, version);
		String topicID1 = assoc.getTopicID1();
		String topicID2 = assoc.getTopicID2();
		// check weather retyping is allowed and which type is finally to be used
		typeID = triggerAssociationAllowed(typeID, topicID1, topicID2, session, directives);
		if (typeID != null) {
			// --- trigger typeChanged() hook ---
			directives.add(assoc.typeChanged(typeID));		// ### typeVersion not used
		}
		//
		return directives;
	}



	// --------------------------------------------------
	// --- Set Properties (application service level) ---
	// --------------------------------------------------



	// Note: there are 3 other methods who set properties at corporate memory level
	// - setTopicProperty()
	// - setTopicProperty()
	// - setTopicProperties()



	// --- setTopicProperty (2 forms) ---

	public CorporateDirectives setTopicProperty(BaseTopic topic, String propName, String propValue,
																		String topicmapID, Session session) {
		return setTopicProperty(topic.getID(), topic.getVersion(), propName, propValue, topicmapID, session);
	}

	/**
	 * Sets a topic property at application service level.
	 * <p>
	 * References checked: 6.9.2002 (2.0a16-pre2)
	 *
	 * @see		de.deepamehta.kompetenzstern.topics.KompetenzsternTopic#propertiesChanged
	 */
	public CorporateDirectives setTopicProperty(String topicID, int version, String propName, String propValue,
																		String topicmapID, Session session) {
		Hashtable props = new Hashtable();
		props.put(propName, propValue);
		return setTopicProperties(topicID, version, props, topicmapID, session);
	}

	// ---

	/**
	 * Sets topic properties at application service level.
	 * <p>
	 * The 5 topic hooks <code>propertyChangeAllowed()</code>, <code>propertiesChanged()</code>, <code>getNameProperty()</code>,
	 * <code>getTopicName()</code>, and <code>nameChanged()</code> are potentially triggered.
	 * <p>
	 * References checked: 17.4.2008 (2.0b8)
	 *
	 * @param	topicmapID						### may be null
	 * @param	session							### may be null
	 *
	 * @see		#setTopicProperty
	 * @see		#performGoogleSearch
	 * @see		CorporateDirectives#setTopicProperties
	 * @see		InteractionConnection#setTopicData
	 * @see		EmbeddedService#setTopicProperties
	 * @see		de.deepamehta.topics.ChatTopic#evoke
	 * @see		de.deepamehta.service.web.DeepaMehtaServlet#processForm
	 */
	public CorporateDirectives setTopicProperties(String topicID, int version, Hashtable props, String topicmapID,
																									Session session) {
		CorporateDirectives directives = new CorporateDirectives();
		//
		try {
			Hashtable oldProps = getTopicProperties(topicID, version);
			LiveTopic topic = getLiveTopic(topicID, version);	// may throw DME
			// only consider changed properties
			props = removeUnchangedProperties(props, oldProps);
			// ask application weather property change is allowed (trigger propertyChangeAllowed() hook)
			removeProhibitedProperties(props, topic, session, directives);
			// write changed properties to database (corporate memory)
			cm.setTopicData(topicID, version, props);
			// trigger topic naming behavoir
			performTopicNamingBehavior(topic, props, oldProps, topicmapID, session, directives);
			// inform application about changed properties (trigger propertiesChanged() hook)
			directives.add(topic.propertiesChanged(props, oldProps, topicmapID, VIEWMODE_USE, session));
		} catch (DeepaMehtaException e) {
			directives.add(DIRECTIVE_SHOW_MESSAGE, e.getMessage(), new Integer(NOTIFICATION_WARNING));
			System.out.println("*** ApplicationService.setTopicProperties(): " + e);
		}
		//
		return directives;
	}

	private void removeProhibitedProperties(Hashtable props, LiveTopic topic, Session session, CorporateDirectives directives) {
		Enumeration e = props.keys();
		while (e.hasMoreElements()) {
			String propName = (String) e.nextElement();
			String propValue = (String) props.get(propName);
			// --- trigger propertyChangeAllowed() hook ---
			if (!topic.propertyChangeAllowed(propName, propValue, session, directives)) {
				props.remove(propName);
			}
		}
	}

	private void performTopicNamingBehavior(LiveTopic topic, Hashtable props, Hashtable oldProps, String topicmapID,
																			Session session, CorporateDirectives directives) {
		String name;
		// trigger getNameProperty() hook
		String nameProp = topic.getNameProperty();
		if (nameProp != null) {
			name = (String) props.get(nameProp);
		} else {
			// trigger getTopicName() hook
			name = topic.getTopicName(props, oldProps);
		}
		if (name != null) {
			// trigger nameChanged() hook
			directives.add(changeTopicName(topic, name, topicmapID, session));
		}
	}

	/**
	 * Updates the topic name at all 3 spots (memory, database, and client) and triggers the nameChanged() hook.
	 * <p>
	 * Note: this is not directly called by application developers but always as a reaction of changed topic properties.
	 * <p>
	 * References checked: 20.5.2008 (2.0b8)
	 *
	 * @see		#setTopicProperties
	 */
	private CorporateDirectives changeTopicName(LiveTopic topic, String name, String topicmapID, Session session) {
		String topicID = topic.getID();
		int version = topic.getVersion();
		// update memory
		topic.setName(name);
		// update corporate memory (database)
		cm.changeTopicName(topicID, version, name);
		// update client
		CorporateDirectives directives = new CorporateDirectives();
		directives.add(DIRECTIVE_SET_TOPIC_NAME, topicID, name, new Integer(version));
		// --- trigger nameChanged() hook ---
		directives.add(topic.nameChanged(name, topicmapID, session));
		//
		return directives;
	}

	// ---

	/**
	 * Sets association properties.
	 * <p>
	 * References checked: 6.9.2002 (2.0a16-pre2)
	 *
	 * @see		CorporateDirectives#setAssociationProperties
	 * @see		InteractionConnection#setAssocData
	 * @see		EmbeddedService#setAssocProperties
	 */
	public CorporateDirectives setAssocProperties(String assocID, int version, Hashtable props,
											String topicmapID, String viewmode, Session session) {
		CorporateDirectives directives = new CorporateDirectives();
		//
		try {
			Hashtable oldProps = getAssocProperties(assocID, version);
			props = removeUnchangedProperties(props, oldProps);
			LiveAssociation assoc = getLiveAssociation(assocID, version);	// may throw DME
			// --- trigger propertiesChangeAllowed() hook ---
			if (assoc.propertiesChangeAllowed(oldProps, props, directives)) {
				setAssocProperties(assocID, version, props);
				// --- trigger propertiesChanged() hook ---
				directives.add(assoc.propertiesChanged(props, oldProps, topicmapID, viewmode, session));
				//
				// association name behavoir
				String name;
				// --- trigger getNameProperty() hook ---
				String nameProp = assoc.getNameProperty();
				if (nameProp != null) {
					name = (String) props.get(nameProp);
				} else {
					// --- trigger getAssociationName() hook ---
					name = assoc.getAssociationName(props, oldProps);
				}
				if (name != null) {
					directives.add(changeAssociationName(assocID, version, name));
				}
			}
		} catch (DeepaMehtaException e) {
			directives.add(DIRECTIVE_SHOW_MESSAGE, e.getMessage(), new Integer(NOTIFICATION_ERROR));
			System.out.println("*** ApplicationService.setAssocProperties(): " + e);
			// ### e.printStackTrace();
		}
		//
		return directives;
	}

	// --- triggerHiddenProperties (2 form) ---

	/**
	 * References checked: 26.9.2002 (2.0a16-pre4)
	 *
	 * @return	may return <code>null</code>
	 *
	 * @see		de.deepamehta.topics.TypeTopic#makeTypeDefinition
	 */
	public Vector triggerHiddenProperties(TypeTopic type) {
		Class[] paramTypes = {TypeTopic.class};
		Object[] paramValues = {type};
		Vector hiddenProperties = (Vector) triggerStaticHook(type.getImplementingClass(), "hiddenProperties",
			paramTypes, paramValues, false);	// throwIfNoSuchHookExists=false
		return hiddenProperties;
	}

	/**
	 * References checked: 26.9.2002 (2.0a16-pre4)
	 *
	 * @return	may return <code>null</code>
	 *
	 * @see		de.deepamehta.service.web.HTMLGenerator#formFields
	 * @see		de.deepamehta.service.web.HTMLGenerator#infoFields
	 * @see		de.deepamehta.service.web.HTMLGenerator#infoFieldsHeading
	 */
	public Vector triggerHiddenProperties(TypeTopic type, String relTopicTypeID) {
		Class[] paramTypes = {TypeTopic.class, String.class};
		Object[] paramValues = {type, relTopicTypeID};
		Vector hiddenProperties = (Vector) triggerStaticHook(type.getImplementingClass(), "hiddenProperties",
			paramTypes, paramValues, false);	// throwIfNoSuchHookExists=false
		return hiddenProperties;
	}

	// ---

	public String triggerPropertyLabel(PropertyDefinition propDef, TypeTopic type, String relTopicTypeID) {
		Class[] paramTypes = {PropertyDefinition.class, String.class, ApplicationService.class};
		Object[] paramValues = {propDef, relTopicTypeID, this};
		try {	
			String propLabel = (String) triggerStaticHook(type.getImplementingClass(), "propertyLabel",
				paramTypes, paramValues, true);	// throwIfNoSuchHookExists=true
			return propLabel;
		} catch (DeepaMehtaException e2) {
			// Note: the type may have an custom implementation but no static propertyLabel() hook
			// defined, in this case the default implementation in LiveTopic must be triggered
			return LiveTopic.propertyLabel(propDef, relTopicTypeID, this);
		}
	}

	// ---

	/**
	 * @return	may return <code>null</code>
	 *
	 * @see		de.deepamehta.topics.ElementContainerTopic#createTopicFromElement
	 * @see		de.deepamehta.topics.DataConsumerTopic#createTopicFromElement
	 */
	public Hashtable triggerMakeProperties(String typeID, Hashtable attributes) {
		TypeTopic type = type(typeID, 1);
		Class[] paramTypes = {Hashtable.class};
		Object[] paramValues = {attributes};
		// --- trigger makeProperties() hook ---
		try {
			Hashtable properties = (Hashtable) triggerStaticHook(type.getImplementingClass(), "makeProperties",
				paramTypes, paramValues, true);		// throwIfNoSuchHookExists=true
			return properties;
		} catch (DeepaMehtaException e2) {
			// Note: the type may have an custom implementation but no static makeProperties() hook
			// defined, in this case the default implementation in LiveTopic must be triggered
			return LiveTopic.makeProperties(attributes);
		}
	}

	// ---

	/**
	 * @see		de.deepamehta.topics.TopicTypeTopic#createSearchType
	 */
	public String triggerGetSearchTypeID(String typeID) {
		TypeTopic type = type(typeID, 1);
		Class[] paramTypes = {};
		Object[] paramValues = {};
		// --- trigger getSearchTypeID() hook ---
		try {
			String searchTypeID = (String) triggerStaticHook(type.getImplementingClass(), "getSearchTypeID",
				paramTypes, paramValues, true);		// throwIfNoSuchHookExists=true
			return searchTypeID;
		} catch (DeepaMehtaException e2) {
			// Note: the type may have an custom implementation but no static getSearchTypeID() hook
			// defined, in this case the default implementation in LiveTopic must be triggered
			return LiveTopic.getSearchTypeID();
		}
	}

	// ---

	/**
	 * @see		de.deepamehta.topics.LiveTopic#executeCommand
	 */
	public CorporateDirectives showTopicMenu(String topicID, int version, String topicmapID, String viewmode,
																				int x, int y, Session session) {
		CorporateDirectives directives = new CorporateDirectives();
		LiveTopic topic = getLiveTopic(topicID, version);
		try {
			// --- trigger contextCommands() hook ---
			CorporateCommands commands = topic.contextCommands(topicmapID, viewmode, session, directives);
			//
			commands.addSeparator();
			// google search
			if (editorContext(topicmapID) == EDITOR_CONTEXT_VIEW && !type(topic).isSearchType()) {
				commands.addSearchInternetCommand(topic, session);
			}
			// help
			commands.addHelpCommand(topic, session);
			//
			directives.add(DIRECTIVE_SHOW_MENU, MENU_TOPIC, commands, new Point(x, y));
		} catch (DeepaMehtaException e) {
			System.out.println("*** ApplicationService.showTopicMenu(): " + e + " -- topic commands not available");
			directives.add(DIRECTIVE_SHOW_MESSAGE, "Menu for " + topic + " not available (" + e.getMessage() + ")",
							new Integer(NOTIFICATION_WARNING));
		}
		return directives;
	}

	/**
	 * @see		de.deepamehta.assocs.LiveAssociation#executeCommand
	 */
	public CorporateDirectives showAssociationMenu(String assocID, int version, String topicmapID, String viewmode,
																					int x, int y, Session session) {
		CorporateDirectives directives = new CorporateDirectives();
		LiveAssociation assoc = getLiveAssociation(assocID, version);
		try {
			// --- trigger contextCommands() hook ---
			CorporateCommands commands = assoc.contextCommands(topicmapID, viewmode, session, directives);
			//
			// help
			commands.addSeparator();
			commands.addHelpCommand(assoc, session);
			//
			directives.add(DIRECTIVE_SHOW_MENU, MENU_ASSOC, commands, new Point(x, y));
		} catch (DeepaMehtaException e) {
			System.out.println("*** ApplicationService.showAssociationMenu(): " + e + " -- association commands not available");
			directives.add(DIRECTIVE_SHOW_MESSAGE, "Menu for " + assoc + " not available (" + e.getMessage() + ")",
							new Integer(NOTIFICATION_WARNING));
		}
		return directives;
	}

	/**
	 * @see		de.deepamehta.topics.LiveTopic#executeCommand
	 */
	public CorporateDirectives showViewMenu(String topicmapID, String viewmode, int x, int y, Session session) {
		CorporateDirectives directives = new CorporateDirectives();
		LiveTopic topicmap = getLiveTopic(topicmapID, 1);
		try {
			// --- trigger viewCommands() hook ---
			CorporateCommands commands = topicmap.viewCommands(topicmapID, viewmode, session, directives);
			directives.add(DIRECTIVE_SHOW_MENU, MENU_VIEW, commands, new Point(x, y));
			//
		} catch (DeepaMehtaException e) {
			System.out.println("*** ApplicationService.showViewMenu(): " + e + " -- view commands not available");
			directives.add(DIRECTIVE_SHOW_MESSAGE, "Menu  for view \"" + topicmap.getName() + "\" (" + topicmapID +
											") not available (" + e.getMessage() + ")", new Integer(NOTIFICATION_WARNING));
		}
		return directives;
	}

	// ---

	/**
	 * Checks weather a new association is allowed by triggering the associationAllowed() hook of both involved topics.
	 * Called once an association is about to be created resp. to be retyped.
	 * Both topics are asked to propose another association type to be used or to prohibit the operation at all.
	 * <p>
	 * References checked: 27.9.2007 (2.0b8)
	 *
	 * @param	assocTypeID		the association type of the association in question
	 * @param	topicID1		first involved topic
	 * @param	topicID2		second involved topic
	 *
	 * @return	the association type to be used, or <code>null</code> to prohibit the operation.
	 *
	 * @see		#createAssociation
	 * @see		#changeAssociationType
	 */
	private String triggerAssociationAllowed(String assocTypeID, String topicID1, String topicID2, Session session,
																						CorporateDirectives directives) {
		LiveTopic topic1 = getLiveTopic(topicID1, 1);
		LiveTopic topic2 = getLiveTopic(topicID2, 1);
		// --- trigger associationAllowed() hook ---
		String assocType1 = topic1.associationAllowed(assocTypeID, topicID2, 2, directives);
		String assocType2 = topic2.associationAllowed(assocTypeID, topicID1, 1, directives);
		//
		if (assocType1 == null || assocType2 == null) {
			directives.add(DIRECTIVE_SHOW_MESSAGE, "Association retyping not possible (prohibited by involved topic)",
							new Integer(NOTIFICATION_WARNING));
			return null;
		}
		//
		boolean retype1 = !assocType1.equals(assocTypeID);
		boolean retype2 = !assocType2.equals(assocTypeID);
		//
		if (retype1 && retype2) {
			if (assocType1.equals(assocType2)) {
				System.out.println(">>> " + topic1 + " modified the assoc retyping (\"" + assocTypeID + "\" -> \"" + assocType1 + "\")");
				assocTypeID = assocType1;
			} else {
				directives.add(DIRECTIVE_SHOW_MESSAGE, "Association retyping not possible (involved topics have contradictive bahavoir)",
							new Integer(NOTIFICATION_WARNING));
				return null;
			}
		} else if (retype1) {
			System.out.println(">>> " + topic1 + " modified the assoc retyping (\"" + assocTypeID + "\" -> \"" + assocType1 + "\")");
			assocTypeID = assocType1;
		} else if (retype2) {
			System.out.println(">>> " + topic2 + " modified the assoc retyping (\"" + assocTypeID + "\" -> \"" + assocType2 + "\")");
			assocTypeID = assocType2;
		}
		//
		return assocTypeID;
	}

	// ---

	/**
	 * References checked: 10.5.2002 (2.0a15-pre1)
	 *
	 * @see		InteractionConnection#processTopicDetail
	 * @see		EmbeddedService#processTopicDetail
	 */
	public CorporateDirectives processTopicDetail(String topicID, int version,
											CorporateDetail detail, Session session,
											String topicmapID, String viewmode) {
		// --- trigger processDetailHook() ---
		return getLiveTopic(topicID, version).processDetailHook(detail, session,
			topicmapID, viewmode);
	}

	/**
	 * References checked: 10.5.2002 (2.0a15-pre1)
	 *
	 * @see		InteractionConnection#processAssociationDetail
	 * @see		EmbeddedService#processAssocDetail
	 */
	public CorporateDirectives processAssociationDetail(String assocID, int version,
											CorporateDetail detail, Session session,
											String topicmapID, String viewmode) {
		// --- trigger processDetailHook() ---
		return getLiveAssociation(assocID, version).processDetailHook(detail, session, topicmapID, viewmode);
	}



	// ----------------------
	// --- Handling Views ---
	// ----------------------



	/**
	 * Retrieves the specified view from corporate memory and creates
	 * a {@link de.deepamehta.PresentableTopicMap} object from it.
	 * <p>
	 * References checked: 5.4.2007 (2.0b8)
	 *
	 * @param	topicmapID	the view
	 * @param	version		the version of the view
	 *
	 * @see		CorporateTopicMap#CorporateTopicMap
	 */
	PresentableTopicMap createUserView(String topicmapID, int version,
								String bgImage, String bgColor, String translation) {
		// vector of PresentableTopic
		// vector of PresentableAssociation
		Vector topics = cm.getViewTopics(topicmapID, version);
		Vector associations = cm.getViewAssociations(topicmapID, version);
		return new PresentableTopicMap(topics, associations, bgImage, bgColor, translation);
	}

	/**
	 * Duplicates the specified viewmode of the specified view in corporate memory.
	 * <p>
	 * References checked: 17.2.2003 (2.0a18-pre2)
	 *
	 * @param	topicmap		view to duplicate
	 * @param	topicmapID		original view ID
	 * @param	viewMode		view mode of <code>topicmap</code> to duplicate
	 * @param	destTopicmapID	destination view ID
	 *
	 * @see		CorporateTopicMap#personalize(String topicmapID)
	 */
	void personalizeView(BaseTopicMap topicmap, String topicmapID, String viewMode, String destTopicmapID) {
		if (VERSIONING) {
			// ### not yet implemented
		} else {
			// ### the view isn't really duplicated yet
			// ### only the view references are duplicated but not the actual topics/associations and their properties
			personalizeTopics(destTopicmapID, 1, viewMode, topicmap.getTopics().elements(), topicmapID, false);
			// ### System.out.println("### ApplicationService.personalizeView(): view \"" + topicmapID + "\" contains itself -- special handling no yet implemented");
			personalizeAssociations(destTopicmapID, 1, viewMode, topicmap.getAssociations().elements(), topicmapID, false);
		}
	}

	// ---

	/**
	 * @see		de.deepamehta.topics.TopicMapTopic#publishTo
	 */
	public void updateView(String srcTopicmapID, int srcTopicmapVersion, String destTopicmapID, int destTopicmapVersion) {
		cm.updateView(srcTopicmapID, srcTopicmapVersion, destTopicmapID, destTopicmapVersion);
	}

	/**
	 * Moves a topic to a specified position.
	 * <p>
	 * The corporate memory is updated.<br>
	 * The <code>moved()</code> hook is triggered and resulting directives are returned.
	 * <p>
	 * Note: this is the only place where <code>cm.updateViewTopic()</code> is called.
	 * <p>
	 * References checked: 14.11.2004 (2.0b3)
	 *
	 * @param	triggerMovedHook	the <code>moved()</code> hook is only triggered if set to <code>true</code>
	 *
	 * @see		CorporateDirectives#updateCorporateMemory			programatic		triggerMovedHook=false
	 * @see		InteractionConnection#setGeometry					interactive		triggerMovedHook=true
	 * @see		EmbeddedService#setGeometry							interactive		triggerMovedHook=true
	 */
	public CorporateDirectives moveTopic(String topicmapID, int topicmapVersion, String topicID, int x, int y,
																				boolean triggerMovedHook, Session session) {
		CorporateDirectives directives = new CorporateDirectives();
		//
		try {
			// update cm
			cm.updateViewTopic(topicmapID, topicmapVersion, topicID, x, y);
			// --- trigger moved() hook ---
			if (triggerMovedHook) {
				directives.add(getLiveTopic(topicID, 1).moved(topicmapID, topicmapVersion, x, y, session));
			}
		} catch (DeepaMehtaException e) {
			directives.add(DIRECTIVE_SHOW_MESSAGE, e.getMessage(), new Integer(NOTIFICATION_WARNING));
			System.out.println("*** ApplicationService.moveTopic(): " + e);
		}
		//
		return directives;
	}

	/**
	 * References checked: 3.1.2002 (2.0a14-pre5)
	 *
	 * @see		InteractionConnection#setTranslation
	 * @see		EmbeddedService#setTranslation
	 */
	public void updateViewTranslation(String topicmapID, int topicmapVersion, String viewmode, int tx, int ty) {
		// ### consider new topic type PointTopic
		setTopicProperty(topicmapID, topicmapVersion, PROPERTY_TRANSLATION_USE, tx + ":" + ty);
	}

	// --- deleteViewTopic (2 forms) ---

	/**
	 * @see		de.deepamehta.topics.LiveTopic#die
	 */
	public void deleteViewTopic(String topicID) {
		cm.deleteViewTopic(topicID);
	}

	/**
	 * @see		CorporateDirectives#updateCorporateMemory	DIRECTIVE_HIDE_TOPIC
	 */
	public void deleteViewTopic(String topicmapID, String topicID) {
		cm.deleteViewTopic(topicmapID, VIEWMODE_USE, topicID);
	}

	// ---

	/**
	 * @see		CorporateDirectives#updateCorporateMemory	DIRECTIVE_HIDE_TOPICS
	 */
	void deleteViewTopics(String topicmapID, Vector topicIDs) {
		Enumeration e = topicIDs.elements();
		while (e.hasMoreElements()) {
			String topicID = (String) e.nextElement();
			cm.deleteViewTopic(topicmapID, VIEWMODE_USE, topicID);
		}
	}

	// --- deleteViewAssociation (2 forms) ---

	/**
	 * @see		de.deepamehta.assocs.LiveAssociation#die
	 */
	public void deleteViewAssociation(String assocID) {
		cm.deleteViewAssociation(assocID);
	}

	/**
	 * @see		CorporateDirectives#updateCorporateMemory
	 */
	void deleteViewAssociation(String topicmapID, String assocID) {
		cm.deleteViewAssociation(topicmapID, VIEWMODE_USE, assocID);
	}

	// ---

	/**
	 * @see		CorporateDirectives#updateCorporateMemory
	 */
	void deleteViewAssociations(String topicmapID, Vector assocIDs) {
		Enumeration e = assocIDs.elements();
		while (e.hasMoreElements()) {
			String assocID = (String) e.nextElement();
			cm.deleteViewAssociation(topicmapID, VIEWMODE_USE, assocID);
		}
	}

	// ---

	/**
	 * @see		de.deepamehta.topics.TopicMapTopic#die
	 * @see		de.deepamehta.topics.TopicMapTopic#publishTo
	 */
	public void deleteUserView(String topicmapID, int topicmapVersion) {
		cm.deleteView(topicmapID, topicmapVersion);
	}

	// ---

	/**
	 * @see		CorporateTopicMap#setAppearance
	 */
	void setAppearance(BaseTopicMap topicmap) {
		Enumeration e = topicmap.getTopics().elements();
		while (e.hasMoreElements()) {
			initTopicAppearance((PresentableTopic) e.nextElement());
		}
	}

	/**
	 * @see		CorporateTopicMap#setTopicLabels
	 */
	void setTopicLabels(BaseTopicMap topicmap) {
		setTopicLabels(topicmap.getTopics().elements());
	}

	/**
	 * @see		#setTopicLabels(PresentableTopicMap topicmap)
	 */
	private void setTopicLabels(Enumeration topics) {
		PresentableTopic topic;
		LiveTopic ct;
		while (topics.hasMoreElements()) {
			topic = (PresentableTopic) topics.nextElement();
			ct = getLiveTopic(topic);
			// --- trigger getLabel() hook ---
			topic.setLabel(ct.getLabel());
		}
	}

	/**
	 * References checked: 10.9.2008 (2.0b8)
	 *
	 * @see		CorporateTopicMap#addPublishDirectives
	 */
	void addPublishDirectives(BaseTopicMap topicmap, Session session, CorporateDirectives directives) {
		Enumeration e = topicmap.getTopics().elements();
		while (e.hasMoreElements()) {
			BaseTopic topic = (BaseTopic) e.nextElement();
			// --- trigger published() hook ---
			getLiveTopic(topic).published(session, directives);
		}
	}



	// --------------------------------
	// --- Initializing Live Topics ---
	// --------------------------------



	/**
	 * @see		#loadKernelTopics					private
	 * @see		CorporateTopicMap#initUserView
	 */
	void initTopics(Enumeration topics, int initLevel, CorporateDirectives directives,
																Session session) {
		// >>> compare to initTypeTopics()
		// >>> compare to checkLiveTopic()
		while (topics.hasMoreElements()) {
			try {
				BaseTopic topic = (BaseTopic) topics.nextElement();
				initTopic(topic, initLevel, session);
			} catch (TopicInitException e) {
				System.out.println("*** ApplicationService.initTopics(): " + e.getMessage());
				if (directives != null) {
					directives.add(DIRECTIVE_SHOW_MESSAGE, e.getMessage(), new Integer(NOTIFICATION_WARNING));
				}
			}
		}
	}

	/**
	 * Initializes the type definition and appearance of the specified enumerated
	 * presentable type topics.
	 *
	 * @param	triggerInit		if <code>true</code> the <code>init(2)</code> and
	 *							<code>init(3)</code> hooks of the corresponding
	 *							type topics are triggered ###
	 *
	 * @see		#loadKernelTopics							2x	true
	 * @see		CorporateTopicMap#initLiveTopics			4x	false
	 * @see		CorporateDirectives#updateCorporateMemory		false
	 */
	void initTypeTopics(Enumeration typeTopics, boolean triggerInit, Session session, CorporateDirectives directives) {
		// >>> compare to initTopics()
		PresentableType typeTopic;
		while (typeTopics.hasMoreElements()) {
			typeTopic = (PresentableType) typeTopics.nextElement();
			try {
				initTypeTopic(typeTopic, triggerInit, session);
			} catch (TopicInitException e) {
				System.out.println("*** ApplicationService.initTypeTopics(): " + e.getMessage());
				if (directives != null) {
					directives.add(DIRECTIVE_SHOW_MESSAGE, e.getMessage(), new Integer(NOTIFICATION_WARNING));
				}
			} catch (Exception e) {
				System.out.println("*** ApplicationService.initTypeTopics(): " + e + " -- " + typeTopic + " not properly inited");
				e.printStackTrace();
			}
		}
	}

	// ---

	/**
	 * Initializes the type definition, the type appearance and the 2
	 * <code>disabled</code>, <code>hiddenTopicNames</code> flags of the
	 * specified presentable type topic.
	 * <p>
	 * References checked: 8.4.2003 (2.0a18-pre9)
	 *
	 * @param	session		the client session (passed to <code>init()</code> hook).
	 *						Note: can be <code>null</code> if <code>triggerInit</code>
	 *						is not set.
	 *
	 * @see		#initTypeTopics (above)											variable
	 * @see		CorporateDirectives#updateCorporateMemory						true		DIRECTIVE_UPDATE_TOPIC_TYPE, DIRECTIVE_UPDATE_ASSOC_TYPE
	 * @see		TypeConnection#performTopicType									false
	 * @see		TypeConnection#performAssociationType							false
	 * @see		EmbeddedService#getTopicType									false
	 * @see		EmbeddedService#getAssociationType								false
	 */
	public void initTypeTopic(PresentableType typeTopic, boolean triggerInit, Session session) throws TopicInitException {
		TypeTopic type = null;
		try {
			type = type(typeTopic.getID(), 1);
			// --- trigger init() hook ---
			if (triggerInit) {
				type.init(INITLEVEL_1, session); // may throw TopicInitException ### directives returned by init() are ignored
				type.init(INITLEVEL_2, session); // may throw TopicInitException ### directives returned by init() are ignored
				type.init(INITLEVEL_3, session); // may throw TopicInitException ### directives returned by init() are ignored
			}
			// --- initialize presentable type ---
			typeTopic.setTypeDefinition(type.getTypeDefinition());		// may throw DeepaMehtaException
			initTypeAppearance(typeTopic, type);
			// flags
			typeTopic.setDisabled(type.getDisabled());
			typeTopic.setHiddenTopicNames(type.getHiddenTopicNames());
			typeTopic.setSearchType(type.isSearchType());
		} catch (ClassCastException e) {
			System.out.println("*** ApplicationService.initTypeTopic(): type is not active (" +
				type.getClass() + ") -- " + typeTopic + " not properly inited");
		} catch (DeepaMehtaException e) {
			System.out.println("*** ApplicationService.initTypeTopic(): " + e +
				" -- " + typeTopic + " not properly inited");
		} catch (TopicInitException e) {
			// --- initialize type definition ---
			typeTopic.setTypeDefinition(type.getTypeDefinition());		// may throw DeepaMehtaException
			// --- initialize type appearance ---
			initTypeAppearance(typeTopic, type);
			throw e;
		}
	}



	// -------------------------------------------
	// --- Query corporate memory semantically ---
	// -------------------------------------------



	/**
	 * @return	true if login succeeded, false otherwise.
	 *
	 * @see		LoginCheck#loginCheck
	 */
	public boolean loginCheck(String username, String password) {
		// check if user exists in corporate memory
		Hashtable props = new Hashtable();
		props.put(PROPERTY_USERNAME, username);
		Vector users = cm.getTopics(TOPICTYPE_USER, props, true);		// caseSensitiv=true
		if (users.size() == 0) {
			return false;
		}
		// error check
		if (users.size() > 1) {
			throw new DeepaMehtaException("there are " + users.size() + " users named \"" + username + "\"");
		}
		// password must match
		BaseTopic user = (BaseTopic) users.firstElement();
		return cm.getTopicData(user.getID(), user.getVersion(), PROPERTY_PASSWORD).equals(password);
	}

	/**
	 * @see		#getLoginCheck
	 * @see		InteractionConnection#login
	 */
	public AuthentificationSourceTopic getAuthentificationSourceTopic() {
		return authSourceTopic;
	}

	public Vector getAllUsers() {
		return cm.getTopics(TOPICTYPE_USER);
	}

	/**
	 * References checked: 9.9.2001 (2.0a12-pre3)
	 *
	 * @return	the LoginCheck of the LoginTopic associated with the
	 *			AuthentificationSourceTopic resp. this object if retrieval of the
	 *			LoginCheck object fails ### ??? rather conceptual commenting is required
	 *
	 * @see		#setAuthentificationSourceTopic
	 * @see		de.deepamehta.topics.AuthentificationSourceTopic#loginCheck
	 */
	public LoginCheck getLoginCheck() {
		try {
			Vector baseTopics = getRelatedTopics(getAuthentificationSourceTopic().getID(),
				SEMANTIC_AUTHENTIFICATION_SOURCE, 2);
			if (baseTopics.size() == 0) {
				// Note: this is a regular case. If no futher LoginCheck specified,
				// the corporate memory is used for authentification
				System.out.println(">>> using corporate memory for authentification");
				return this;
			}
			BaseTopic baseTopic = (BaseTopic) baseTopics.firstElement();
			// error check
			if (baseTopics.size() > 1) {
				throw new AmbiguousSemanticException("more than one LoginTopic " +
					"associated with the authentification source", baseTopic);
			}
			return ((LoginTopic) getLiveTopic(baseTopic)).getLoginCheck();	// ### session, directives
		} catch (Exception e) {
			System.out.println("*** ApplicationService.getLoginCheck(): " + e +
				" -- using corporate memory for authentification");
			return this;
		}
	}

	// ---

	/**
	 * Returns all workspaces the specified user is a member of.
	 * <p>
	 * References checked: 7.6.2007 (2.0b8)
	 *
	 * @return	The workspaces as vector of {@link de.deepamehta.BaseTopic}s
	 *			(type <code>tt-workspace</code>)
	 *
	 * @see		#addGroupWorkspaces
	 * @see		CorporateCommands#addPublishCommand
	 * @see		CorporateCommands#addTopicTypeCommands
	 * @see		CorporateCommands#addAssocTypeCommands
	 * @see		CorporateCommands#addWorkspaceTopicTypeCommands
	 * @see		de.deepamehta.topics.LiveTopic#handleWorkspaceCommand
	 * @see		de.deepamehta.topics.UserTopic#contextCommands
	 * @see		de.deepamehta.browser.BrowserServlet#preparePage
	 * @see		de.deepamehta.webfrontend.WebFrontendServlet#preparePage
	 */
	public Vector getWorkspaces(String userID) {
		// Note: also subtypes of association type "at-membership" are respected
		Vector subtypes = type(SEMANTIC_MEMBERSHIP, 1).getSubtypeIDs();
		// ### System.out.println(">>> respect " + subtypes.size() + " association subtypes ...");
		return cm.getRelatedTopics(userID, subtypes, 2);	// ### relTopicTypeID?
	}

	/**
	 * Returns all members of the specified workspace.
	 * <p>
	 * References checked: 15.2.2005 (2.0b5)
	 *
	 * @return	The members as vector of {@link de.deepamehta.BaseTopic}s
	 *			(type <code>tt-user</code>)
	 *
	 * @see		#activeWorkspaceSessions
	 * @see		de.deepamehta.topics.TopicMapTopic#publish
	 */
	public Vector workgroupMembers(String workspaceID) {
		// Note: also subtypes of association type "at-membership" are respected
		Vector subtypes = type(SEMANTIC_MEMBERSHIP, 1).getSubtypeIDs();
		return cm.getRelatedTopics(workspaceID, subtypes, 1);	// ### relTopicTypeID?
	}

	// ---

	/**
	 * Returns the accessible topic types of the specified user resp. workspace.
	 * <p>
	 * References checked: 9.10.2001 (2.0a12)
	 *
	 * @param	id					topic ID of a user resp. workspace
	 * @param	permissionMode		{@link #PERMISSION_VIEW} /
	 *								{@link #PERMISSION_CREATE} /
	 *								{@link #PERMISSION_CREATE_IN_WORKSPACE}
	 *
	 * @return	The topic types as vector of {@link de.deepamehta.BaseTopic}s
	 *			(type <code>tt-topictype</code>)
	 *
	 * @see		CorporateCommands#addTopicTypeCommands						3x
	 * @see		CorporateCommands#addWorkspaceTopicTypeCommands				3x
	 * @see		de.deepamehta.topics.LiveTopic#handleWorkspaceCommand		3x
	 */
	public Vector getTopicTypes(String id, String permissionMode) {
		String assocProp = null;
		String propValue = null;
		// Note: create permission implies view permission
		if (!permissionMode.equals(PERMISSION_VIEW)) {
			assocProp = PROPERTY_ACCESS_PERMISSION;
			propValue = permissionMode;
		}
		return cm.getRelatedTopics(id, SEMANTIC_WORKSPACE_TYPES, TOPICTYPE_TOPICTYPE, 2, assocProp, propValue, true);
		// sortAssociations=true
	}

	/**
	 * Returns the accessible association types of the specified user resp. workspace.
	 * <p>
	 * References checked: 10.8.2001 (2.0a11)
	 *
	 * @param	id					the topic ID of the user resp. workspace
	 * @param	permissionMode		{@link #PERMISSION_VIEW} /
	 *								{@link #PERMISSION_CREATE} /
	 *								{@link #PERMISSION_CREATE_IN_WORKSPACE} (not used for association types)
	 *
	 * @return	The association types as vector of {@link de.deepamehta.BaseTopic}s
	 *			(type <code>tt-assoctype</code>)
	 *
	 * @see		CorporateCommands#addAssocTypeCommands	3x
	 */
	Vector getAssociationTypes(String id, String permissionMode) {
		String assocProp = null;
		String propValue = null;
		// Note: create permission implies view permission
		if (!permissionMode.equals(PERMISSION_VIEW)) {
			assocProp = PROPERTY_ACCESS_PERMISSION;
			propValue = permissionMode;
		}
		return cm.getRelatedTopics(id, SEMANTIC_WORKSPACE_TYPES, TOPICTYPE_ASSOCTYPE, 2, assocProp, propValue, true);
		// sortAssociations=true
	}

	// ---

	/**
	 * Returns all views in use of the specified user.
	 *
	 * @return	The views as vector of {@link de.deepamehta.BaseTopic}s
	 *			(type <code>tt-topicmap</code>)
	 *
	 * @see		InteractionConnection#addViewsInUse
	 */
	public Vector getViewsInUse(String userID) {
		return cm.getRelatedTopics(userID, SEMANTIC_VIEW_IN_USE, 2);
	}

	public String getMailboxURL(String userID) {
		Vector urls = cm.getRelatedTopics(userID, SEMANTIC_EMAIL_ADDRESS, TOPICTYPE_EMAIL_ADDRESS, 2);
		if (urls.size() == 0) {
			return null;
		}
		String url = getTopicProperty((BaseTopic) urls.firstElement(), PROPERTY_MAILBOX_URL);
		if (urls.size() > 1) {
			System.out.println("*** user \"" + userID + "\" has " + urls.size() + " email addresses -- only \"" + url + "\" is respected");
		}
		return url;
	}

	/**
	 * Returns the email address of a person or an institution.
	 *
	 * @param	topicID		the ID of a person topic or an institution topic.
	 *
	 * @return	the email address, or <code>null</code> if no email address is assigned.
	 */
	public String getEmailAddress(String topicID) {
		Vector adrs = cm.getRelatedTopics(topicID, SEMANTIC_EMAIL_ADDRESS, TOPICTYPE_EMAIL_ADDRESS, 2);
		if (adrs.size() == 0) {
			return null;
		}
		String adr = getTopicProperty((BaseTopic) adrs.firstElement(), PROPERTY_EMAIL_ADDRESS);
		if (adrs.size() > 1) {
			System.out.println("*** person/institution \"" + topicID + "\" has " + adrs.size() +
				" email addresses -- only \"" + adr + "\" is respected");
		}
		return adr;
	}

	/**
	 * Checks weather the specified user has a preference of the specified type.
	 *
	 * @return	true, if a preference exists, false otherwise.
	 */
	public boolean userPreferenceExists(String userID, String prefTypeID, CorporateDirectives directives) {
		try {
			getUserPreferences(userID, prefTypeID, directives);
			return true;
		} catch (DeepaMehtaException e) {
			return false;
		}
	}

	/**
	 * References checked: 25.1.2002 (2.0a14-pre7)
	 *
	 * @see		#startSession
	 */
	public BaseTopic getUserPreferences(String userID, String prefTypeID,
								CorporateDirectives directives) throws DeepaMehtaException {
		try {
			return getRelatedTopic(userID, SEMANTIC_PREFERENCE, prefTypeID, 2, false);	// DME, ASE
		} catch (AmbiguousSemanticException e) {
			System.out.println("*** ApplicationService.getUserPreferences(): " + e);
			directives.add(DIRECTIVE_SHOW_MESSAGE, e.getMessage(),
				new Integer(NOTIFICATION_WARNING));
			return e.getDefaultTopic();
		}
	}

	/**
	 * References checked: 20.3.2003 (2.0a18-pre7)
	 *
	 * @param	id	workspace ID resp. installation ID
	 *
	 * @see		de.deepamehta.topics.UserTopic#evoke
	 * @see		de.deepamehta.topics.WorkspaceTopic#transferPreferences
	 */
	public Vector getPreferences(String id) {
		return cm.getRelatedTopics(id, SEMANTIC_PREFERENCE, 2);
	}

	/**
	 * References checked: 20.3.2003 (2.0a18-pre7)
	 *
	 * @see		de.deepamehta.topics.UserTopic#evoke
	 * @see		de.deepamehta.topics.WorkspaceTopic#transferPreferences
	 */
	public void setUserPreferences(String userID, Vector prefs, CorporateDirectives directives) {
		// ### Note: for now the preferences are NOT duplicated
		Enumeration e = prefs.elements();
		while (e.hasMoreElements()) {
			BaseTopic pref = (BaseTopic) e.nextElement();
			// Note: the user can already have such a preference
			System.out.print("  > " + pref);
			if (userPreferenceExists(userID, pref.getType(), directives)) {
				System.out.println(" >>> user preference is NOT overridden");
				continue;
			}
			System.out.println();
			// create "preference" association
			cm.createAssociation(getNewAssociationID(), 1, SEMANTIC_PREFERENCE, 1, userID, 1, pref.getID(), 1);
		}
	}

	// ---

	/**
	 * Returns the sessions of all logged in users that are members of the specified workspace.
	 *
	 * @return	The sessions as vector of {@link Session}s
	 *
	 * @see		ChatTopic#activeSessions
	 */
	public Vector activeWorkspaceSessions(String workspaceID) {
		Vector sessions = new Vector();
		//
		Vector members = workgroupMembers(workspaceID);
		Enumeration e = members.elements();
		while (e.hasMoreElements()) {
			BaseTopic member = (BaseTopic) e.nextElement();
			sessions.addAll(userSessions(member));
		}
		//
		return sessions;
	}

	/**
	 * Returns the sessions of the specified user.
	 * <p>
	 * Note: a user can login from different client machines.
	 *
	 * @return	The sessions as vector of {@link Session}s
	 *
	 * @see		#activeWorkspaceSessions
	 * @see		#broadcast
	 */
	public Vector userSessions(BaseTopic user) {
		Vector sessions = new Vector();
		for (int id = 0; id < MAX_CLIENTS; id++) {
			if (clientSessions[id] != null) {
				if (user.getID().equals(clientSessions[id].getUserID())) {
					sessions.addElement(clientSessions[id]);
				}
			}
		}
		return sessions;
	}

	/**
	 * Checks weather the specified user is an administrator.
	 *
	 * @return	true if the specified user is an administrator, false otherwise.
	 *
	 * @see		de.deepamehta.topics.TopicMapTopic#contextCommands
	 */
	public boolean isAdministrator(String userID) {
		return isMemberOf(userID, "t-administrationgroup");
	}

	public boolean isMemberOf(String userID, String workspaceID) {
		return associationExists(userID, workspaceID, SEMANTIC_MEMBERSHIP);	// Note: also membership subtypes are respected
	}

	public boolean hasRole(String userID, String workspaceID, String rolename) {
		Vector subtypes = type(SEMANTIC_MEMBERSHIP, 1).getSubtypeIDs();
		BaseAssociation assoc = cm.getAssociation(subtypes, userID, workspaceID);
		if (assoc == null) {
			return false;
		}
		return cm.getAssociationData(assoc.getID(), assoc.getVersion(), rolename).equals(SWITCH_ON);
	}

	/**
	 * References checked: 18.8.2008 (2.0b8)
	 *
	 * @param	typeID		a topic type ID or an association type ID
	 *
	 * @see		#retypeTopicIsAllowed
	 * @see		#retypeAssociationIsAllowed
	 * @see		#deleteTopicIsAllowed
	 * @see		#deleteAssociationIsAllowed
	 * @see		de.deepamehta.topics.LiveTopic#disabledProperties
	 * @see		de.deepamehta.assocs.LiveAssociation#disabledProperties
	 */
	public boolean hasEditorRole(String userID, String typeID) {
		Vector workspaces = cm.getRelatedTopics(typeID, SEMANTIC_WORKSPACE_TYPES, TOPICTYPE_WORKSPACE, 1);
		Enumeration e = workspaces.elements();
		while (e.hasMoreElements()) {
			BaseTopic workspace = (BaseTopic) e.nextElement();
			if (hasRole(userID, workspace.getID(), PROPERTY_ROLE_EDITOR)) {
				return true;
			}
		}
		return false;
	}

	public boolean isViewOpen(String viewID, String userID) {
		return associationExists(userID, viewID, SEMANTIC_VIEW_IN_USE);
	}

	public boolean associationExists(String topicID1, String topicID2, String assocTypeID) {
		// Note: also association subtypes are respected
		Vector subtypes = type(assocTypeID, 1).getSubtypeIDs();
		return cm.associationExists(topicID1, topicID2, subtypes);
	}

	// --- getChatboard (2 forms) ---

	/**
	 * Returns the chatboard (type <code>tt-chatboard</code>) of the specified workspace.
	 * <p>
	 * References checked: 8.4.2007 (2.0b8)
	 *
	 * @return	the chatboard or <code>null</code> if there is no chatboard.
	 *
	 * @see		#getChatboard(BaseTopic, CorporateDirectives)
	 */
	public BaseTopic getChatboard(String workspaceID) throws AmbiguousSemanticException {
		BaseTopic topicmap = getWorkspaceTopicmap(workspaceID);
		Vector chatboardIDs = cm.getTopicIDs(TOPICTYPE_CHAT_BOARD, topicmap.getID());
		// error check 1
		if (chatboardIDs.size() == 0) {
			return null;
		}
		//
		String chatboardID = (String) chatboardIDs.firstElement();
		BaseTopic chatboard = cm.getTopic(chatboardID, 1);	// ### version is set to 1
		// error check 2
		if (chatboardIDs.size() > 1) {
			throw new AmbiguousSemanticException("Workspace \"" + workspaceID + "\" has " + chatboardIDs.size() +
				" chatboards (expected is 1) -- considering only " + chatboard, chatboard);
		}
		//
		return chatboard;
	}

	/**
	 * Convenience method as a wrapper for {@link #getChatboard(String workspaceID)} that
	 * handles the exceptional cases by adding corresponding <code>DIRECTIVE_SHOW_MESSAGE</code>
	 * directives to the specified directives object.
	 * <p>
	 * References checked: 8.4.2007 (2.0b8)
	 *
	 * @param	workspace	a workspace topic
	 *
	 * @see		de.deepamehta.topics.WorkspaceTopic#nameChanged
	 */
	public BaseTopic getChatboard(BaseTopic workspace, CorporateDirectives directives) {
		try {
			BaseTopic chatboard = getChatboard(workspace.getID());
			// error check
			if (chatboard == null) {
				String errText = "Workspace \"" + workspace.getName() + "\" has no chatboard";
				System.out.println("*** ApplicationService.getChatboard(2): " + errText);
				directives.add(DIRECTIVE_SHOW_MESSAGE, errText, new Integer(NOTIFICATION_WARNING));
			}
			return chatboard;
		} catch (AmbiguousSemanticException e) {
			System.out.println("*** ApplicationService.getChatboard(2): " + e);
			directives.add(DIRECTIVE_SHOW_MESSAGE, e.getMessage(), new Integer(NOTIFICATION_WARNING));
			return e.getDefaultTopic();
		}
	}

	// --- getMessageboard (2 forms) ---

	/**
	 * Returns the messageboard (type <code>tt-messageboard</code>) of the specified workspace.
	 * <p>
	 * References checked: 8.4.2007 (2.0b8)
	 *
	 * @return	the messageboard or <code>null</code> if there is no messageboard.
	 *
	 * @see		#getMessageboard(BaseTopic, CorporateDirectives)
	 */
	public BaseTopic getMessageboard(String workspaceID) throws AmbiguousSemanticException {
		BaseTopic topicmap = getWorkspaceTopicmap(workspaceID);
		Vector messageboardIDs = cm.getTopicIDs(TOPICTYPE_MESSAGE_BOARD, topicmap.getID());
		// error check 1
		if (messageboardIDs.size() == 0) {
			return null;
		}
		//
		String messageboardID = (String) messageboardIDs.firstElement();
		BaseTopic messageboard = cm.getTopic(messageboardID, 1);	// ### version is set to 1
		// error check 2
		if (messageboardIDs.size() > 1) {
			throw new AmbiguousSemanticException("Workspace \"" + workspaceID + "\" has " + messageboardIDs.size() +
				" messageboards (expected is 1) -- considering only " + messageboard, messageboard);
		}
		//
		return messageboard;
	}

	/**
	 * Convenience method as a wrapper for {@link #getMessageboard(String workspaceID)} that
	 * handles the exceptional cases by adding corresponding <code>DIRECTIVE_SHOW_MESSAGE</code>
	 * directives to the specified directives object.
	 * <p>
	 * References checked: 8.4.2007 (2.0b8)
	 *
	 * @param	workspace	a workspace topic
	 *
	 * @see		de.deepamehta.topics.WorkspaceTopic#nameChanged
	 */
	public BaseTopic getMessageboard(BaseTopic workspace, CorporateDirectives directives) {
		try {
			BaseTopic messageboard = getMessageboard(workspace.getID());
			// error check
			if (messageboard == null) {
				String errText = "Workspace \"" + workspace.getName() + "\" has no messageboard";
				System.out.println("*** ApplicationService.getMessageboard(2): " + errText);
				directives.add(DIRECTIVE_SHOW_MESSAGE, errText, new Integer(NOTIFICATION_WARNING));
			}
			return messageboard;
		} catch (AmbiguousSemanticException e) {
			System.out.println("*** ApplicationService.getMessageboard(2): " + e);
			directives.add(DIRECTIVE_SHOW_MESSAGE, e.getMessage(), new Integer(NOTIFICATION_WARNING));
			return e.getDefaultTopic();
		}
	}

	// --- getWorkspaceTopicmap (2 forms) ---

	/**
	 * Returns the workspace topicmap (type <code>tt-topicmap</code>) of the specified user
	 * resp. workspace or <code>null</code> if the specified user resp. workspace has no
	 * workspace topicmap.
	 * <p>
	 * References checked: 8.4.2007 (2.0b8)
	 *
	 * @param	id		The ID of a user (<code>type tt-user</code>) resp. workspace (type <code>tt-workspace</code>)
	 *
	 * @return	the workspace topicmap (type <code>tt-topicmap</code>)
	 *
	 * @throws	DeepaMehtaException		if the specified user resp. workspace is not in ApplicationService resp.
	 *									is not properly initialized (iconfile is unknown)
	 * @see		#getChatboard
	 * @see		#getMessageboard
	 * @see		#getWorkspaceTopicmap(String id, CorporateDirectives directives)
	 * @see		#addPersonalWorkspace
	 * @see		#addGroupWorkspaces
	 * @see		de.deepamehta.topics.TopicMapTopic#publishTo
	 * @see		de.deepamehta.topics.UserTopic#propertiesChanged
	 * @see		de.deepamehta.topics.UserTopic#createPersonalWorkspace
	 * @see		de.deepamehta.topics.WorkspaceTopic#propertiesChanged
	 */
	public PresentableTopic getWorkspaceTopicmap(String id) throws DeepaMehtaException, AmbiguousSemanticException {
		Vector topicmaps = cm.getRelatedTopics(id, SEMANTIC_WORKSPACE_TOPICMAP, TOPICTYPE_TOPICMAP, 2);
		if (topicmaps.size() == 0) {
			// Note: this is not an exceptional condition because getWorkspaceTopicmap() is
			// also used for workspace topicmap existence check
			return null;
		}
		//
		PresentableTopic topicmap = createPresentableTopic((BaseTopic) topicmaps.firstElement(), id);	// may throw DME
		// error check
		if (topicmaps.size() > 1) {
			throw new AmbiguousSemanticException("User resp. workspace \"" + id + "\" has " + topicmaps.size() +
				" workspace topicmaps (expected is 1) -- considering only " + topicmap, topicmap);
		}
		//
		return topicmap;
	}

	/**
	 * Convenience method as a wrapper for {@link #getWorkspaceTopicmap(String id)} that handles the exceptional cases
	 * by adding corresponding <code>DIRECTIVE_SHOW_MESSAGE</code> directives to the specified directives object.
	 * <p>
	 * References checked: 8.4.2007 (2.0b8)
	 *
	 * @param	id		The ID of a user (<code>type tt-user</code>) resp. workspace (type <code>tt-workspace</code>)
	 *
	 * @see		de.deepamehta.topics.UserTopic#nameChanged
	 * @see		de.deepamehta.topics.WorkspaceTopic#nameChanged
	 * @see		de.deepamehta.topics.WorkspaceTopic#joinUser
	 * @see		de.deepamehta.topics.WorkspaceTopic#leaveUser
	 */
	public BaseTopic getWorkspaceTopicmap(String id, CorporateDirectives directives) {
		try {
			BaseTopic topicmap = getWorkspaceTopicmap(id);	// may throw DME
			// error check
			if (topicmap == null) {
				String errText = "User resp. workspace \"" + id + "\" has no workspace topicmap";
				System.out.println("*** ApplicationService.getWorkspaceTopicmap(2): " + errText);
				directives.add(DIRECTIVE_SHOW_MESSAGE, errText, new Integer(NOTIFICATION_WARNING));
			}
			//
			return topicmap;
		} catch (AmbiguousSemanticException e) {
			System.out.println("*** ApplicationService.getWorkspaceTopicmap(2): " + e);
			directives.add(DIRECTIVE_SHOW_MESSAGE, e.getMessage(), new Integer(NOTIFICATION_WARNING));
			return e.getDefaultTopic();
		}
	}

	// ---

	/**
	 * @see		#showTopicMenu
	 * @see		de.deepamehta.topics.LiveTopic#contextCommands
	 * @see		de.deepamehta.topics.LiveTopic#executeCommand
	 */
	public int editorContext(String topicmapID) {
		BaseTopic deployer = getWorkspaceTopimapDeployer(topicmapID);
		if (deployer == null) {
			return EDITOR_CONTEXT_VIEW;
		} else if (deployer.getType().equals(TOPICTYPE_USER)) {
			return EDITOR_CONTEXT_PERSONAL;
		} else if (deployer.getType().equals(TOPICTYPE_WORKSPACE)) {
			return EDITOR_CONTEXT_WORKGROUP;
		} else {
			throw new DeepaMehtaException("topicmap \"" + topicmapID + "\" has unexpected deployer: " + deployer);
		}
	}

	/**
	 * Returns <code>true</code> if the specified topic is contained in a published topicmap, <code>false</code> otherwise.
	 * <p>
	 * References checked: 29.1.2008 (2.0b8)
	 *
	 * @param	topicID		the ID of the topic.
	 *
	 * @see		de.deepamehta.topics.ContainerTopic#contextCommands
	 */
	public boolean isContainedInPublishedTopicmap(String topicID) {
		Enumeration e = cm.getViews(topicID, 1, VIEWMODE_USE).elements();
		while (e.hasMoreElements()) {
			BaseTopic topicmap = (BaseTopic) e.nextElement();
			BaseTopic owner = getTopicmapOwner(topicmap.getID());
			if (owner != null && owner.getType().equals(TOPICTYPE_WORKSPACE)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the owner of the specified topicmap. The specified topicmap is expected to exist either
	 * in a personal workspace or in a shared workspace. Otherwise <code>null</code> is returned, which
	 * is the case e.g. for personalized demo topicmaps.
	 * <p>
	 * References checked: 24.8.2006 (2.0b8)
	 *
	 * @param	topicmapID	the ID of the topicmap. Note: this method works also for non-topicmap
	 *						topics existing in a workspace (e.g. "CorporateWeb Settings") but is
	 *						not used in this fashion.
	 *
	 * @return	The user resp. workspace as {@link de.deepamehta.BaseTopic}
	 *			(type <code>tt-user</code> resp. <code>tt-workspace</code>)
	 *
	 * @see		de.deepamehta.topics.ChatTopic#initWorkspace
	 * @see		de.deepamehta.topics.TopicMapTopic#updateOwnership
	 */
	public BaseTopic getTopicmapOwner(String topicmapID) throws DeepaMehtaException {
		BaseTopic owner = null;
		//
		Enumeration e = cm.getViews(topicmapID, 1, VIEWMODE_USE).elements();
		while (e.hasMoreElements()) {
			BaseTopic workspaceTopicmap = (BaseTopic) e.nextElement();
			BaseTopic o = getWorkspaceTopimapDeployer(workspaceTopicmap.getID());
			if (o == null) {
				// not a workspace topicmap
				continue;
			}
			//
			if (o.getType().equals(TOPICTYPE_WORKSPACE)) {
				// error check
				if (owner != null) {
					throw new DeepaMehtaException("Owner ambigouty for " + this);
				}
				// shared workspace
				owner = o;
			} else if (o.getType().equals(TOPICTYPE_USER)) {
				// error check
				if (owner != null) {
					throw new DeepaMehtaException("Owner ambigouty for " + this);
				}
				// personal workspace
				owner = o;
			}
		}
		//
		return owner;
	}

	/**
	 * Returns the user resp. workspace who deploys the specified workspace topicmap
	 * (type <code>tt-topicmap</code>) resp. <code>null</code> if the specified
	 * topicmap doesn't represent a workspace topicmap.
	 * <p>
	 * References checked: 24.8.2006 (2.0b8)
	 *
	 * @param	the ID of the workspace topicmap.
	 *
	 * @return	The the user resp. workspace as {@link de.deepamehta.BaseTopic}
	 *			(type <code>tt-user</code> resp. <code>tt-workspace</code>)
	 *
	 * @see		#editorContext
	 * @see		#getTopicmapOwner
	 */
	public BaseTopic getWorkspaceTopimapDeployer(String topicmapID) {
		Vector deployers = cm.getRelatedTopics(topicmapID, SEMANTIC_WORKSPACE_TOPICMAP_DEPLOYER, 1);
		if (deployers.size() == 0) {
			return null;
		}
		if (deployers.size() > 1) {
			throw new DeepaMehtaException("*** ApplicationService.getWorkspaceTopimapDeployer(): workspace topicmap \"" +
				topicmapID + "\" has " + deployers.size() + " deployers (expected is 1)");
		}
		return (BaseTopic) deployers.firstElement();
	}

	// ---

	/**
	 * Returns the view the specified topic is involved in.
	 *
	 * @return	The view as {@link de.deepamehta.BaseTopic}
	 *			(type <code>tt-topicmap</code>)
	 */
	public BaseTopic getView(String topicID, int version, String viewmode) {
		Vector views = cm.getViews(topicID, version, viewmode);
		if (views.size() == 0) {
			return null;
		}
		BaseTopic view = (BaseTopic) views.firstElement();
		if (views.size() > 1) {
			throw new AmbiguousSemanticException("topic \"" + topicID + ":" + version +
				"\" is contained in " +	views.size() + " views (expected is 1) -- " +
				"considering only " + view, view);
		}
		return view;
	}

	// ---

	// 2 Application helper methods which are operating on corporate memory

	// public wrappers for the 2 private wrappers below which catches the ASE and adds
	// a notification to the client directives

	// Note: 2 more forms of getTopic() and getAssociation() are derived from
	// BaseTopicMap (with one id parameter)

	public BaseTopic getTopic(String typeID, Hashtable properties, String topicmapID,
							CorporateDirectives directives) throws DeepaMehtaException {
		try {
			return getTopic(typeID, properties, topicmapID);
			// throws DME, ASE
		} catch (AmbiguousSemanticException e) {
			System.out.println("*** ApplicationService.getTopic(4): " + e);
			directives.add(DIRECTIVE_SHOW_MESSAGE, e.getMessage(),
				new Integer(NOTIFICATION_WARNING));
			return e.getDefaultTopic();
		}
	}

	/**
	 * @return	the matching association or <code>null</code> if <code>emptyAllowed</code>
	 *			is set and no matching association is found
	 *
	 * @see		de.deepamehta.topics.TopicMapTopic#executeCommand
	 */
	public BaseAssociation getAssociation(String topicID, String assocTypeID, int relTopicPos,
							String relTopicTypeID, boolean emptyAllowed,
							CorporateDirectives directives) throws DeepaMehtaException {
		try {
			return getAssociation(topicID, assocTypeID, relTopicPos, relTopicTypeID, emptyAllowed);
			// throws DME, ASE
		} catch (AmbiguousSemanticException e) {
			System.out.println("*** ApplicationService.getAssociation(6): " + e);
			directives.add(DIRECTIVE_SHOW_MESSAGE, e.getMessage(),
				new Integer(NOTIFICATION_WARNING));
			return e.getDefaultAssociation();
		}
	}

	// ---

	/**
	 * Wrapper for {@link CorporateMemory#getTopics(String type, Hashtable propertyFilter, String topicmapID)}
	 * which throws semantical exceptions.
	 * <p>
	 * References checked: 30.12.2001 (2.0a14-pre5)
	 *
	 * @throws	DeepaMehtaException			if no matching topic is found in corporate
	 *										memory
	 * @throws	AmbiguousSemanticException	if more than one matching topic are found in
	 *										corporate memory
	 */
	private BaseTopic getTopic(String typeID, Hashtable properties, String topicmapID)
								throws DeepaMehtaException, AmbiguousSemanticException {
		Vector topics = cm.getTopics(typeID, properties, topicmapID);
		if (topics.size() == 0) {
			throw new DeepaMehtaException("there are no \"" + typeID + "\" topics " +
				"with properties " + properties + " in view \"" + topicmapID + "\"");
		}
		BaseTopic topic = (BaseTopic) topics.firstElement();
		if (topics.size() > 1) {
			throw new AmbiguousSemanticException("there are " + topics.size() +
				" \"" + typeID + "\" topics with properties " + properties +
				" in view \"" + topicmapID + "\" (expected is 1) -- considering " +
				"only " + topic, topic);
		}
		return topic;
	}

	/**
	 * Wrapper for {@link CorporateMemory#getRelatedAssociations(String topicID, String assocTypeID, int relTopicPos, String relTopicTypeID)}
	 * which throws semantical exceptions.
	 * <p>
	 * References checked: 30.12.2001 (2.0a14-pre5)
	 *
	 * @param	emptyAllowed	### required? -- currently null is passed always
	 *
	 * @return	the matching association or <code>null</code> if <code>emptyAllowed</code>
	 *			is set and no matching association is found
	 *
	 * @throws	DeepaMehtaException			if <code>emptyAllowed</code> is not set and
	 *										no matching association is found
	 * @throws	AmbiguousSemanticException	if more than one matching association are
	 *										found
	 */
	private BaseAssociation getAssociation(String topicID, String assocTypeID, int relTopicPos,
								String relTopicTypeID, boolean emptyAllowed)
								throws DeepaMehtaException, AmbiguousSemanticException {
		Vector assocs = cm.getRelatedAssociations(topicID, assocTypeID, relTopicPos, relTopicTypeID);
		if (assocs.size() == 0) {
			// error check
			if (!emptyAllowed) {
				throw new DeepaMehtaException("topic \"" + topicID + "\" has no " +
					"matching \"" + assocTypeID + "\" association (relTopicTypeID=\"" +
					relTopicTypeID + "\", relTopicPos=" + relTopicPos + ")");
			}
			//
			return null;
		}
		BaseAssociation assoc = (BaseAssociation) assocs.firstElement();
		if (assocs.size() > 1) {
			throw new AmbiguousSemanticException("topic \"" + topicID + "\" has " +
				assocs.size() + " \"" + assocTypeID + "\" associations (expected is 1) " +
				"-- considering only " + assoc, assoc);
		}
		return assoc;
	}

	// ---

	public BaseTopic getActiveInstallation() {
		Hashtable props = new Hashtable();
		props.put(PROPERTY_INSTALLATION, SWITCH_ON);
		Vector installations = cm.getTopics(TOPICTYPE_INSTALLATION, props);
		if (installations.size() == 0) {
			throw new DeepaMehtaException("unknown installation\n>>>there must be an " +
				"\"Installation\" topic with enabled \"" + PROPERTY_INSTALLATION +
				"\" property");
		}
		BaseTopic installation = (BaseTopic) installations.firstElement();
		if (installations.size() > 1) {
			throw new AmbiguousSemanticException("there are " + installations.size() +
				" enabled installations -- considering only " + installation,
				installation);
		}
		return installation;
	}

	// ---

	/**
	 * @see		ServerConsole#ServerConsole
	 */
	String getInstallationName() {
		return (String) installationProps.get(PROPERTY_SERVER_NAME);
	}

	/**
	 * @see		DeepaMehta#initApplication
	 * @see		DeepaMehta#init
	 */
	public Hashtable getInstallationProps() {
		return installationProps;
	}

	/**
	 * @see		DeepaMehtaServer#runServer
	 */
	void writeInstallationProps(DataOutputStream out) throws IOException {
		DeepaMehtaUtils.writeHashtable(installationProps, out);
	}

	// ---

	/**
	 * Returns the users default workspace resp. null if the user has no one.
	 * <p>
	 * References checked: 31.1.2008 (2.0b8)
	 *
	 * @return	the users default workspace as {@link de.deepamehta.BaseTopic}
	 *			(type <code>tt-workspace</code>) or <code>null</code>
	 *
	 * @see		CorporateCommands#addPublishCommand
	 * @see		CorporateCommands#addTopicTypeCommands
	 * @see		CorporateCommands#addAssocTypeCommands
	 * @see		CorporateCommands#addWorkspaceTopicTypeCommands
	 */
	public BaseTopic getUsersDefaultWorkspace(Session session, CorporateDirectives directives) throws DeepaMehtaException {
		try {
			if (session.isDemo()) {
				return getTopicmapOwner(session.getDemoTopicmapID());
			} else {
				String userID = session.getUserID();
				BaseTopic workspace = getRelatedTopic(userID, SEMANTIC_PREFERENCE, TOPICTYPE_WORKSPACE, 2, true);	// emptyAllowed=true
				//
				// fallback: the system-wide default workspace is used, provided the user is a member
				if (workspace == null) {
					BaseTopic dws = getDefaultWorkspace(directives);
					if (dws != null && isMemberOf(userID, dws.getID())) {
						workspace = dws;
					}
				}
				//
				return workspace;
			}
		} catch (AmbiguousSemanticException e) {
			System.out.println("*** ApplicationService.getUsersDefaultWorkspace(): " + e);
			directives.add(DIRECTIVE_SHOW_MESSAGE, e.getMessage(), new Integer(NOTIFICATION_WARNING));
			return e.getDefaultTopic();
		}
	}

	// ---

	/**
	 * Returns the system-wide default workspace resp. null if there is no one.
	 * <p>
	 * Convenience method as a wrapper for {@link #corporateGroup} that
	 * handles the exceptional cases by ... ###
	 * <p>
	 * References checked: 3.8.2004 (2.0b3)
	 *
	 * @return	the system-wide default workspace as {@link de.deepamehta.BaseTopic}
	 *			(type <code>tt-workspace</code>) or <code>null</code>
	 *
	 * @see		#getUsersDefaultWorkspace
	 * @see		de.deepamehta.topics.UserTopic#evoke
	 */
	public BaseTopic getDefaultWorkspace(CorporateDirectives directives) {
		try {
			return corporateGroup();	// throws ASE
		} catch (AmbiguousSemanticException e) {
			System.out.println("*** ApplicationService.getDefaultWorkspace(): " + e);
			directives.add(DIRECTIVE_SHOW_MESSAGE, e.getMessage(), new Integer(NOTIFICATION_WARNING));
			return e.getDefaultTopic();
		}
	}

	/**
	 * Returns the system-wide default workspace resp. null if there is no one.
	 * <p>
	 * ### rename
	 *
	 * @throws	AmbiguousSemanticException	if there are more than one default workspaces
	 */
	private BaseTopic corporateGroup() throws AmbiguousSemanticException{
		Hashtable props = new Hashtable();
		props.put(PROPERTY_DEFAULT_WORKSPACE, SWITCH_ON);
		Vector workgroups = cm.getTopics(TOPICTYPE_WORKSPACE, props);
		//
		if (workgroups.size() == 0) {
			return null;
		}
		//
		BaseTopic workgroup = (BaseTopic) workgroups.firstElement();
		// error check
		if (workgroups.size() > 1) {
			throw new AmbiguousSemanticException("there are " +	workgroups.size() +
				" default workspaces -- only " + workgroup + " is considered", workgroup);
		}
		//
		return workgroup;
	}

	// ---

	/* ### public String getCorporateWebPath() {
		return getTopicProperty("t-corpwebadm", 1, PROPERTY_CW_ROOT_DIR);
	} */

	public String getCorporateWebBaseURL() {
		return getTopicProperty("t-corpwebadm", 1, PROPERTY_CW_BASE_URL);
	}

	public String getSMTPServer() throws DeepaMehtaException {
		String smtp = getTopicProperty("t-corpwebadm", 1, PROPERTY_SMTP_SERVER);
		// error check
		if (smtp.equals("")) {
			throw new DeepaMehtaException("SMTP Server is not set");
		}
		//
		return smtp;
	}

	public String getGoogleKey() throws DeepaMehtaException {
		String key = getTopicProperty("t-corpwebadm", 1, PROPERTY_GOOGLE_KEY);
		// error check
		if (key.equals("")) {
			throw new DeepaMehtaException("Google Key is not set");
		}
		//
		return key;
	}

	// ---

	/**
	 * References checked: 11.12.2001 (2.0a14-pre4)
	 *
	 * @throws	DeepaMehtaException
	 *
	 * @see		CorporateCommands#addExportCommand
	 * @see		de.deepamehta.topics.TopicMapTopic#export
	 */
	public BaseTopic getExportFormat(String userID, CorporateDirectives directives)
															throws DeepaMehtaException {
		try {
			return getRelatedTopic(userID, SEMANTIC_PREFERENCE, TOPICTYPE_EXPORT_FORMAT, 2, false);
			// throws DME, ASE
		} catch (AmbiguousSemanticException e) {
			System.out.println("*** ApplicationService.getExportFormat(): " + e);
			directives.add(DIRECTIVE_SHOW_MESSAGE, e.getMessage(),
				new Integer(NOTIFICATION_WARNING));
			return e.getDefaultTopic();
		}
	}

	// ---

	/**
	 * Returns the workspace from which the specified topicmap was opened or
	 * <code>null</code> if the specified topicmap was never published.
	 * <p>
	 * References checked: 24.8.2008 (2.0b8)
	 *
	 * @return	the workspace as a {@link de.deepamehta.BaseTopic} (type <code>tt-workspace</code>)
	 *
	 * @see		CorporateCommands#addPublishCommand
	 * @see		de.deepamehta.topics.TopicMapTopic#publish
	 */
	public BaseTopic getOriginWorkspace(String topicmapID) {
		Vector workspaces = cm.getRelatedTopics(topicmapID, SEMANTIC_ORIGIN_WORKSPACE, 2);
		if (workspaces.size() == 0) {
			return null;
		}
		BaseTopic workspace = (BaseTopic) workspaces.firstElement();
		if (workspaces.size() > 1) {
			System.out.println("*** ApplicationService.getOriginWorkspace():" +
				" topicmap \"" + topicmapID + "\" has " + workspaces.size() +
				" SEMANTIC_ORIGIN_WORKSPACE associations -- considering only " + workspace);
		}
		return workspace;
	}

	/**
	 * Returns the origin topicmap of the specified personalized topicmap.
	 *
	 * @see		de.deepamehta.topics.TopicMapTopic#publishTo
	 */
	public BaseTopic originTopicmap(String personalTopicmapID) {
		Vector topicmaps = cm.getRelatedTopics(personalTopicmapID, SEMANTIC_ORIGIN_MAP, 1);
		if (topicmaps.size() == 0) {
			return null;
		}
		BaseTopic topicmapTopic = (BaseTopic) topicmaps.firstElement();
		if (topicmaps.size() > 1) {
			System.out.println("*** ApplicationService.originTopicMapID(): \"" +
				personalTopicmapID + "\" has " + topicmaps.size() +" matching " +
				"derivation associations -- considering only " + topicmapTopic);
		}
		return topicmapTopic;
	}

	public BaseTopic configurationTopicmap(String userID) {
		Vector topicmaps = cm.getRelatedTopics(userID, SEMANTIC_CONFIGURATION_MAP, 2);
		if (topicmaps.size() == 0) {
			return null;
		}
		BaseTopic topicmapTopic = (BaseTopic) topicmaps.firstElement();
		if (topicmaps.size() > 1) {
			System.out.println("*** ApplicationService.configurationTopicmap(): " +
				 "user \"" + userID + "\" has " + topicmaps.size() +" matching " +
				 "usage associations -- considering only " + topicmapTopic);
		}
		return topicmapTopic;
	}

	/**
	 * Returns the shell command for opening the specified file for the specified user,
	 * resp. throws a <code>DeepaMehtaException</code> if an error occurs.
	 *
	 * @see		de.deepamehta.service.FileserverConnection#performDownload
	 * @see		de.deepamehta.topics.DocumentTopic#executeCommand
	 */
	public String openCommand(String userID, String filename) throws DeepaMehtaException {
		// error check 1
		if (filename.equals("")) {
			System.out.println("*** ApplicationService.openCommand(): there is " +
				"no filename available (empty) -- \"" + filename + "\" can't be opened");
			throw new DeepaMehtaException("The document can't be opened because there " +
				"is no file assigned yet");
		}
		// --- get start of filenames extension ---
		int pos = filename.lastIndexOf(".");
		// error check 2
		if (pos == -1) {
			System.out.println("*** ApplicationService.openCommand(): there is " +
				"no extension -- \"" + filename + "\" can't be opened");
			throw new DeepaMehtaException("The document (" + filename + ") can't be " +
				"opened because the file has no extension");
		}
		// --- get MIME Configuration of the user ---
		BaseTopic confMapTopic = configurationTopicmap(userID);
		String confMapID = confMapTopic.getID();
		// --- get the filenames extension ---
		String ext = filename.substring(pos);
		BaseTopic occTypeTopic = documentTypeTopic(confMapID, ext);
		// error check 3
		if (occTypeTopic == null) {
			System.out.println("*** ApplicationService.openCommand(): there is " +
				"no DocumentType topic \"" + ext + "\" in the MIME Configuration " +
				"-- \"" + filename + "\" can't be opened");
			throw new DeepaMehtaException("The document (" + filename + ") can't be " +
				"opened because there is no document type \"" + ext + "\" in your " +
				"MIME Configuration");
		}
		// --- get corresponding MIME type ---
		BaseTopic mimeTypeTopic = mimeTypeTopic(confMapID, occTypeTopic.getID());
		// error check 4
		if (mimeTypeTopic == null) {
			System.out.println("*** ApplicationService.openCommand(): there is " +
				"no MIME type assigned to \"" + ext + "\" -- \"" + filename +
				"\" can't be opened");
			throw new DeepaMehtaException("The document (" + filename + ") can't be " +
				"opened because there is no MIME type assigned to \"" + ext + "\" in " +
				"your MIME Configuration");
		}
		// --- get corresponding application ---
		BaseTopic applicationTopic = applicationTopic(confMapID, mimeTypeTopic.getID());
		// error check 5
		if (applicationTopic == null) {
			String mimeType = mimeTypeTopic.getName();
			System.out.println("*** ApplicationService.openCommand(): there is " +
				"no application assigned to \"" + mimeType +
				"\" -- \"" + filename + "\" can't be opened");
			throw new DeepaMehtaException("The document (" + filename + ") can't be " +
				"opened because there is no application assigned to \"" + mimeType +
				"\" in your MIME Configuration");
		}
		// --- get command ---
		String path = cm.getTopicData(applicationTopic.getID(),
			applicationTopic.getVersion(), PROPERTY_FILE);
		// error check 6
		if (path.equals("")) {
			String appName = applicationTopic.getName();
			System.out.println("*** ApplicationService.openCommand(): there is " +
				"no path set to \"" + appName +
				"\" -- \"" + filename + "\" can't be opened");
			throw new DeepaMehtaException("The document (" + filename + ") can't be " +
				"opened because there is no path set to application \"" + appName +
				"\" in your MIME Configuration");
		}
		// --- return command ---
		return path;
	}

	/**
	 * @see		#openCommand
	 */
	public BaseTopic documentTypeTopic(String configurationMapID, String ext) {
		return cm.getViewTopic(configurationMapID, 1, VIEWMODE_USE, TOPICTYPE_DOCUMENT_TYPE, ext);
	}

	/**
	 * @see		#openCommand
	 */
	public BaseTopic mimeTypeTopic(String configurationMapID, String occurrenceTypeID) {
		Vector mimeTypes = cm.getRelatedTopics(occurrenceTypeID, SEMANTIC_MIMETYPE, 1, configurationMapID);
		if (mimeTypes.size() == 0) {
			return null;
		}
		BaseTopic mimeTypeTopic = (BaseTopic) mimeTypes.firstElement();
		if (mimeTypes.size() > 1) {
			System.out.println("*** ApplicationService.mimeTypeTopic(): " +
				"occurrence type \"" + occurrenceTypeID + "\" has " + mimeTypes.size() +
				" matching aggregation associations -- considering only " +
				mimeTypeTopic);
		}
		return mimeTypeTopic;
	}

	/**
	 * @see		#openCommand
	 */
	public BaseTopic applicationTopic(String configurationMapID, String mimeTypeID) {
		Vector applications = cm.getRelatedTopics(mimeTypeID, SEMANTIC_APPLICATION, 1, configurationMapID);
		// error check
		if (applications.size() == 0) {
			return null;
		}
		//
		BaseTopic applicationTopic = (BaseTopic) applications.firstElement();
		if (applications.size() > 1) {
			System.out.println("*** ApplicationService.applicationTopic(): " +
				 "MIME type \"" + mimeTypeID + "\" has " + applications.size() +
				 " matching aggregation associations -- considering only " +
				 applicationTopic);
		}
		return applicationTopic;
	}

	/**
	 * @return	The corresponding container-type to the specfied type as a
	 *			{@link de.deepamehta.BaseTopic} (type <code>tt-topictype</code>)
	 *			or <code>null</code> if no container-type exists.
	 *
	 * @throws	AmbiguousSemanticException	if more than one container-type exists
	 *
	 * @see		#getAllTopics(String typeID, int x, int y)
	 * @see		de.deepamehta.topics.TopicTypeTopic#nameChanged
	 */
	public BaseTopic getSearchType(String typeID) {
		Vector types = cm.getRelatedTopics(typeID, SEMANTIC_CONTAINER_TYPE, 1);
		// error check 1
		if (types.size() == 0) {
			return null;
		}
		//
		BaseTopic containerType = (BaseTopic) types.firstElement();
		// error check 2
		if (types.size() > 1) {
			throw new AmbiguousSemanticException("type \"" + typeID + "\" has " +
				types.size() + " container-types -- considering only " +
				containerType, containerType);
		}
		//
		return containerType;
	}

	/**
	 * References checked: 15.2.2005 (2.0b5)
	 * 
	 * @return	The membership association-type to use for joining a user to the specified
	 *			workgroup as {@link de.deepamehta.BaseTopic} (type <code>tt-topictype</code>).
	 *
	 * @throws	AmbiguousSemanticException	if more than one association-type is assigned
	 *
	 * @see		de.deepamehta.topics.WorkspaceTopic#associationAllowed
	 * @see		de.deepamehta.topics.WorkspaceTopic#joinUser
	 */
	public String getMembershipType(String workspaceID, CorporateDirectives directives) {
		try {
			BaseTopic type = getRelatedTopic(workspaceID, SEMANTIC_MEMBERSHIP_TYPE,
				TOPICTYPE_ASSOCTYPE, 2, true);	// allowEmpty=true, throws ASE
			if (type != null) {
				return type.getID();
			}
			return SEMANTIC_MEMBERSHIP;
		} catch (AmbiguousSemanticException e) {
			System.out.println("*** ApplicationService.getMembershipType(): " + e);
			directives.add(DIRECTIVE_SHOW_MESSAGE, e.getMessage(), new Integer(NOTIFICATION_WARNING));
			return e.getDefaultTopic().getID();
		}
	}

	// ---

	/**
	 * Returns all topics types that consumes from the specified data source.
	 *
	 * @return	The types as vector of {@link de.deepamehta.BaseTopic}s
	 *			(type <code>tt-topictype</code>).
	 *
	 * @see		de.deepamehta.topics.DataSourceTopic#propertiesChanged
	 */
	public Vector dataConsumerTypes(String dataSourceID) {
		return cm.getRelatedTopics(dataSourceID, SEMANTIC_DATA_CONSUMER, 1);
	}



	// ---------------------
	// --- Miscellaneous ---
	// ---------------------



	public void performGoogleSearch(String searchText, String topicID, String topicmapID, String viewmode,
																	Session session, CorporateDirectives directives) {
		try {
			GoogleSearch s = new GoogleSearch();
			s.setKey(getGoogleKey());		// throws DME
			s.setQueryString(searchText);
			// ### s.setMaxResults(10);		// Note: more than 10 doesn't work
			GoogleSearchResult r = s.doSearch();
			GoogleSearchResultElement[] elems = r.getResultElements();
			// --- process result ---
			Vector webpages = new Vector();
			// ### Downloader downloader = new Downloader(this, session, topicmapID, viewmode);
			for (int i = 0; i < elems.length; i++) {
				GoogleSearchResultElement elem = elems[i];
				PresentableTopic webpage = createWebpageTopic(elem.getURL(), elem.getTitle(), topicID);
				String webpageID = webpage.getID();
				// ### must evoke here, because createNewContainer() evokes only if < 7
				if (webpage.getEvoke()) {
					createLiveTopic(webpage, topicmapID, viewmode, session, directives);
					setTopicProperties(webpageID, 1, webpage.getProperties(), topicmapID, session);
				}
				if (!cm.associationExists(topicID, webpageID, false)) {	// ignoreDirection=false
					String assocID = getNewAssociationID();
					cm.createAssociation(assocID, 1, SEMANTIC_WEBSEARCH_RESULT, 1, topicID, 1, webpageID, 1);
					cm.setAssociationData(assocID, 1, PROPERTY_NAME, elem.getTitle());
					cm.setAssociationData(assocID, 1, PROPERTY_DESCRIPTION, elem.getSnippet());
				}
				webpages.addElement(webpage);	// ### complete?
				// ### downloader.addURL(elem.getURL(), webpageID);
			}
			// --- show result ---
			directives.add(createNewContainer(getLiveTopic(topicID, 1), "tt-webpagecontainer", null, new Hashtable(),
				topicID, SEMANTIC_WEBSEARCH_RESULT, webpages.size(), webpages, false));
			// ---
			// ### downloader.startDownload();
		} catch (DeepaMehtaException dme) {
			System.out.println("*** ApplicationService.performGoogleSearch(): call to the Google Web APIs failed: " + dme);
			directives.add(DIRECTIVE_SHOW_MESSAGE, "To perform a Google search you need a key from www.google.com/apis/. " +
				"Then as a DeepaMehta administrator enter the key into \"CorporateWeb Settings\"",
				new Integer(NOTIFICATION_WARNING));
		} catch (GoogleSearchFault f) {
			System.out.println("*** ApplicationService.performGoogleSearch(): call to the Google Web APIs failed: " + f);
			directives.add(DIRECTIVE_SHOW_MESSAGE, "Error while performing Google search for \"" + searchText + "\" (" + f.getMessage() + ")",
				new Integer(NOTIFICATION_WARNING));
		}
	}

	// ---

	/* ### public String downloadFile(String url) throws IOException {
		return downloadFile(new URL(url));
	} */

	// ### for text content only
	public String downloadFile(URL url) throws IOException {
		System.out.println("  > \"" + url + "\" -- begin download");
		//
		Reader in = new InputStreamReader(url.openStream());
		CharArrayWriter out = new CharArrayWriter(1024);
		char buffer[] = new char[1024];
		int num;
		while ((num = in.read(buffer)) != -1) {
			out.write(buffer, 0, num);
		}
		in.close();
		String html = new String(out.toCharArray());
		System.out.println("  > \"" + url + "\" -- (" + html.length() + " bytes read)");
		//
		return html;
	}



	// ----------------------------
	// --- Creating Topic Beans ---
	// ----------------------------



	public TopicBean createTopicBean(String topicID, int version) {
		TopicBean bean = new TopicBean();
		//
		LiveTopic topic = getLiveTopic(topicID, version);
		TypeTopic type = getTopicType(topicID, version);
		bean.id = topicID;
		bean.name = topic.getName();
		bean.typeID = type.getID();
		bean.typeName = type.getName();
		bean.icon = topic.getIconfile();
		//
		addFieldsToTopicBean(type, topicID, version, "", "", true, bean);
		return bean;
	}

	/**
	 * Adds all fields of a topic to a bean.
	 *
	 * @param	type		the type of the topic. Acts as "template": all fields of this type definition are added.
	 * @param	topicID		the ID of the topic. If null, the added fields are empty.
	 * @param   fieldPrefix contains the type hierarchy as name of a TopicBeanField 
	 * @param   labelPrefix contains the naming hierarchy as labelof a TopicBeanField 
	 */
	private void addFieldsToTopicBean(TypeTopic type, String topicID, int version, String fieldPrefix, String labelPrefix, boolean deep, TopicBean bean) {
		// ### compare to HTMLGenerator.infoFields()
		// ### compare to HTMLGenerator.formFields()
		Enumeration items = type.getDefinition().elements();
		while (items.hasMoreElements()) {
			OrderedItem item = (OrderedItem) items.nextElement();
			if (item instanceof PropertyDefinition) {
				PropertyDefinition propDef = (PropertyDefinition) item;
				String propName = propDef.getPropertyName();
				String visualMode = propDef.getVisualization();
				String propValue = topicID != null ? getTopicProperty(topicID, 1, propName) : "";
				// add TYPE_SINGLE bean field
				bean.fields.addElement(new TopicBeanField(fieldPrefix + propName, labelPrefix + propName, visualMode, propValue));
			} else if (item instanceof Relation) {
				if (deep) {
					Relation rel = (Relation) item;
					// query related topics
					Vector relTopics = topicID != null ? getRelatedTopics(topicID, rel.assocTypeID, rel.relTopicTypeID, 2,
						false, true) : new Vector();	// ordered=false, emptyAllowed=true ### relTopicPos=2 hardcoded
					// add fields
					if (rel.webInfo.equals(WEB_INFO_TOPIC_NAME)) {
						addRelationFieldToTopicBean(rel, relTopics, fieldPrefix, labelPrefix, bean);
					} else if (rel.webInfo.equals(WEB_INFO) || rel.webInfo.equals(WEB_INFO_DEEP)) {
						addRelationFieldsToTopicBean(rel, relTopics, fieldPrefix, bean);
					} else {
						throw new DeepaMehtaException("unexpected web info mode: \"" + rel.webInfo + "\"");
					}
				}
			} else {
				throw new DeepaMehtaException("unexpected object in type definition: " + item);
			}
		}
	}

	/**
	 * Adds the name and the label of related topics as TopicBeanFields to a TopicBean.
	 * Hint: called when the Relation setting is WEB_INFO_TOPIC_NAME
	 */
	private void addRelationFieldToTopicBean(Relation rel, Vector topics, String fieldPrefix, String labelPrefix, TopicBean bean) {
		// ### compare to HTMLGenerator.relationInfoField()
		// ### compare to HTMLGenerator.relationFormField()
		//
		TypeTopic relTopicType = type(rel.relTopicTypeID, 1);
		boolean many = rel.cardinality.equals(CARDINALITY_MANY);
		String fieldLabel = !rel.name.equals("") ? rel.name : many ? relTopicType.getPluralNaming() : relTopicType.getName();
		String fieldName = relTopicType.getName();
		// ### System.out.println(">>> addRelationFieldToTopicBean() label is: " + labelPrefix + fieldLabel + ", name is: " + fieldPrefix + fieldName);
		// add TYPE_MULTI bean field
		bean.fields.addElement(new TopicBeanField(fieldPrefix + fieldName, labelPrefix + fieldLabel, topics));
	}

	/**
	 * Adds all fields of related topics to a bean.
	 * Hint: called when the Relation setting is WEB_INFO || WEB_INFO_DEEP
	 */
	private void addRelationFieldsToTopicBean(Relation rel, Vector topics, String fieldPrefix, TopicBean bean) {
		// ### compare to HTMLGenerator.relationInfoFields()
		// ### compare to HTMLGenerator.relationFormFields()
		//
		String relTopicID = null;
		if (topics.size() > 0) {
			// ### only the first related topic is considered
			relTopicID = ((BaseTopic) topics.firstElement()).getID();
		}
		TypeTopic relTopicType = type(rel.relTopicTypeID, 1);
		boolean many = rel.cardinality.equals(CARDINALITY_MANY);
		String labelPrefix = !rel.name.equals("") ? rel.name + TopicBean.FIELD_SEPARATOR : many ? relTopicType.getPluralNaming() + 
			TopicBean.FIELD_SEPARATOR : relTopicType.getName() + TopicBean.FIELD_SEPARATOR;
		fieldPrefix = fieldPrefix + relTopicType.getName() + TopicBean.FIELD_SEPARATOR;
		boolean deep = rel.webInfo.equals(WEB_INFO_DEEP);
		// recursive call
		addFieldsToTopicBean(relTopicType, relTopicID, 1, fieldPrefix, labelPrefix, deep, bean);		// ### version=1
	}



	// --------------------
	// --- Broadcasting ---
	// --------------------



	/**
	 * Sends the specified directives <i>asynchronously</i> to every enumerated user.
	 *
	 * @param	storeForLaterDelivery		### not yet used
	 *
	 * @see		de.deepamehta.topics.TopicMapTopic#publish
	 */
	public void broadcast(CorporateDirectives directives, Enumeration users, boolean storeForLaterDelivery) {
		while (users.hasMoreElements()) {
			BaseTopic user = (BaseTopic) users.nextElement();
			Vector sessions = userSessions(user);
			System.out.println(">>> ApplicationService.broadcast(): \"" + user.getName() +
				"\" (" + sessions.size() + " sessions)");
			if (sessions.size() > 0) {
				// the user is logged in
				broadcastSessions(directives, sessions.elements(), storeForLaterDelivery);
			} else {
				// the user is not logged in
				// ### store for later delivery
			}
		}
	}

	/**
	 * Sends the specified directives <i>asynchronously</i> to every enumerated session.
	 *
	 * @param	storeForLaterDelivery		### not yet used
	 *
	 * @see		de.deepamehta.topics.TopicMapTopic#publish
	 * @see		de.deepamehta.topics.ChatTopic#executeCommand
	 */
	public void broadcastSessions(CorporateDirectives directives, Enumeration sessions, boolean storeForLaterDelivery) {
		while (sessions.hasMoreElements()) {
			Session session = (Session) sessions.nextElement();
			host.sendDirectives(session, directives, this, null, null);		// topicmapID=null, viewmode=null
		}
	}

	// ---

	/**
	 * ### to be dropped
	 *
	 * References checked: 25.1.2009 (2.0b9)
	 *
	 * @see		de.deepamehta.topics.LiveTopic#upload
	 */
	public boolean runsAtServerHost(Session session) {
		String clientAddress = session.getAddress();
		return clientAddress.equals("127.0.0.1") || clientAddress.equals(hostAddress);
	}

	// --- 4 PersonalWeb Helper Methods ### move to DeepaMehtaServiceUtils ---

	/**
	 * @see		de.deepamehta.topics.WebpageTopic#executeCommand
	 * @see		de.deepamehta.topics.personalweb.FetchThread#run
	 */
	public boolean localFileExists(URL url) {
		File file = new File(localFile(url, true));
		return file.exists();
	}

	/**
	 * Returns the filename for saving the contents of the URL on the local filesystem
	 * <p>
	 * References checked: 4.2.2005 (2.0b5)
	 *
	 * @see		#localFileExists											true
	 * @see		de.deepamehta.topics.personalweb.FetchThread#getContent		true
	 * @see		de.deepamehta.topics.personalweb.PersonalWeb#saveFile		true
	 * @see		de.deepamehta.topics.personalweb.PersonalWeb#isHTMLPage		false
	 * @see		de.deepamehta.topics.personalweb.PersonalWeb#relativeURL	false
	 */
	public String localFile(URL url, boolean withLocalPath) {
		String file = url.getFile();
		if (containsCGICall(url)) {
			file += ".html";
		} else {
			file = completeURL(url).getFile();
			// append "index.html" if the URL represents a directory
			if (file.endsWith("/")) {
				file += "index.html";
			}
		}
		String localFile = (withLocalPath ? FILESERVER_WEBPAGES_PATH : "") + url.getHost() + file;		// ### port?
		return localFile;
	}

	/**
	 * @see		#localFile
	 * @see		de.deepamehta.topics.personalweb.ParseThread#parseHTML
	 */
	public boolean containsCGICall(URL url) {
		String file = url.getFile();
		return file.indexOf("cgi-bin") != -1 || file.indexOf('?') != -1;
	}

	/**
	 * Adds a "/" to the URL if it is considered as a directory and returns the URL.
	 *
	 * @see		#localFile
	 * @see		de.deepamehta.topics.personalweb.ParseThread#parseHTML
	 */
	public URL completeURL(URL url) {
		try {
			String file = url.getFile();
			// is the URL a directory?
			if (!file.endsWith("/")) {
				// is the URL a directory but does not end with a / ?
				int pos1 = file.lastIndexOf('/');
				int pos2 = file.lastIndexOf('.');
				boolean isDirectory = false;
				if (pos1 == -1) {
					// ### Note: can't use writeLog() because we're static
					if (!file.equals("")) {
						System.out.println("*** completeURL(): no \"/\" in \"" + file + '"');
					}
					// Note: regular case in case file is empty (index page of the website)
					isDirectory = true;
				} else if (pos2 < pos1) {
					// the path doesn't have an extension -- it is considered as a directory ###
					isDirectory = true;
				} else {
					// the path have an extension -- it is considered as a directory
					// if the extension is unknown ### list possibly incomplete
					String extension = file.substring(pos2);

					// TODO ### use static hashset for comparism
					if (!extension.equalsIgnoreCase(".html") &&
						!extension.equalsIgnoreCase(".htm") &&
						!extension.equalsIgnoreCase(".phtml") &&
						!extension.equalsIgnoreCase(".shtml") &&
						!extension.equalsIgnoreCase(".css") &&
						!extension.equalsIgnoreCase(".js") &&
						!extension.equalsIgnoreCase(".rdf") &&
						!extension.equalsIgnoreCase(".gif") &&
						!extension.equalsIgnoreCase(".jpg") &&
						!extension.equalsIgnoreCase(".jpeg") &&
						!extension.equalsIgnoreCase(".png") &&
						!extension.equalsIgnoreCase(".ico") &&
						!extension.equalsIgnoreCase(".wav") &&
						!extension.equalsIgnoreCase(".ra") &&
						!extension.equalsIgnoreCase(".ram") &&
						!extension.equalsIgnoreCase(".au") &&
						!extension.equalsIgnoreCase(".mp3") &&
						!extension.equalsIgnoreCase(".mpg") &&
						!extension.equalsIgnoreCase(".mov") &&
						!extension.equalsIgnoreCase(".pdf") &&
						!extension.equalsIgnoreCase(".ps") &&
						!extension.equalsIgnoreCase(".exe") &&
						!extension.equalsIgnoreCase(".tar") &&
						!extension.equalsIgnoreCase(".zip") &&
						!extension.equalsIgnoreCase(".gz") &&
						!extension.equalsIgnoreCase(".Z") &&
						!extension.equalsIgnoreCase(".sit") &&
						!extension.equalsIgnoreCase(".bin") &&
						!extension.equalsIgnoreCase(".hqx") &&
						!extension.equalsIgnoreCase(".txt")) {
						System.out.println(">>> \"" + extension + "\" isn't a known " +
							"filetype -- \"" + url + "\" is considered as a directory");
						isDirectory = true;
					}
				}
				if (isDirectory) {
					file += "/";
					url = new URL(url.getProtocol(), url.getHost(), url.getPort(), file);
				}
			}
		} catch (MalformedURLException e) {
			// ### Note: can't use writeLog() because we're static
			System.out.println("*** completeURL(): " + e);
		}
		return url;
	}

	// ---

	/**
	 * @see		de.deepamehta.topics.TopicMapTopic#executeChainedCommand
	 */
	public String getNewTopicID() {
		return cm.getNewTopicID();
	}

	/**
	 * @see		de.deepamehta.topics.TopicTypeTopic#evoke
	 */
	public String getNewAssociationID() {
		return cm.getNewAssociationID();
	}



	// --------------------------
	// --- Session Management ---
	// --------------------------



	/**
	 * Returns a new session ID or <code>-1</code> if the server is overloaded.
	 * Valid session are in the range of <code>1</code> to
	 * {@link DeepaMehtaConstants#MAX_CLIENTS}.
	 *
	 * @see		DeepaMehtaServer#runServer
	 */
	public int getNewSessionID() {
		int id = 1;
		while (id <= MAX_CLIENTS) {
			if (getSession(id) == null) {
				return id;
			}
			id++;
		}
		return -1;
	}

	/**
	 * References checked: 13.8.2002 (2.0a15)
	 *
	 * @see		DeepaMehtaServer#runServer
	 * @see		DeepaMehta#initApplication
	 * @see		DeepaMehta#init
	 */
	public Session createSession(int sessionID, String clientName, String clientAddress) {
		Session session = new DeepaMehtaSession(sessionID, clientName, clientAddress);
		clientSessions[sessionID] = session;
		updateServerConsole();
		return session;
	}

	/**
	 * @see		InteractionConnection#run
	 */
	void removeSession(Session session) {
		clientSessions[session.getSessionID()] = null;
		updateServerConsole();
	}

	/**
	 * References checked: 17.12.2001 (2.0a14-pre5)
	 *
	 * @see		DeepaMehtaServer#runServer
	 * @see		ServerConsole#ServerConsole
	 */
	Session[] getSessions() {
		return clientSessions;
	}

	/**
	 * @see		InteractionConnection#login
	 */
	public BaseTopic tryLogin(String username, String password, Session session) {
		// --- user authentification ---
		AuthentificationSourceTopic authSource = getAuthentificationSourceTopic();
		BaseTopic userTopic = authSource.loginCheck(username, password, session);
		if (userTopic != null) {
			System.out.println(">>> [" + username + "] LOGIN -- successfull <<<");
			return userTopic;
		} else {
			System.out.println(">>> (" + username + ") LOGIN -- failed <<<");
			return null;
		}
	}

	/**
	 * Extends the specified directives to let the client create the initial GUI.
	 * <p>
	 * The initial GUI consists of the users <i>workspaces</i> as well as the <i>views</i>
	 * from previous session.
	 * <p>
	 * Called once a user logged in sucessfully.
	 *
	 * @return	The user preferences of the specified user.
	 *
	 * @see		InteractionConnection#login
	 */
	public void startSession(BaseTopic userTopic, Session session, CorporateDirectives directives) {
		String userID = userTopic.getID();
		// --- initialize session ---
		session.setDemo(false);
		session.setLoggedIn(true);
		session.setUserID(userID);
		session.setUserName(userTopic.getName());
		// ### email checking is disabled. ### threads are not stopped / creates too many threads on the server
		// ### session.setEmailChecker(new EmailChecker(userID, 1, this));
		// --- report on server console ---
		updateServerConsole();
		// --- let client create the initial  GUI ---
		addPersonalWorkspace(session, directives);	// adding workspaces
		addGroupWorkspaces(session, directives);	// ### workspace order required
		addViewsInUse(session, directives);			// open views from previous session
	}

	/**
	 * @see		InteractionConnection#login
	 */
	public void startDemo(String demoMapID, Session session, CorporateDirectives directives) {
		// --- initialize session ---
		String userName = "Guest " + session.getSessionID();
		session.setDemo(true);
		session.setDemoTopicmapID(demoMapID);
		session.setLoggedIn(true);
		// Note: a demo user has no ID (there is no tt-user topic for a demo user)
		session.setUserName(userName);
		// --- report on server console ---
		updateServerConsole();
		//
		System.out.println(">>> [" + userName + "] Demo LOGIN successfull (demomap: \"" + demoMapID + "\") <<<");
		// --- let client present the initial GUI ---
		try {
			LiveTopic demoMap = getLiveTopic(demoMapID, 1, session, directives);	// throws DME
			//
			// - trigger openTopicmap() hook -
			demoMap.openTopicmap(session, directives);	// throws DME
			//
		} catch (DeepaMehtaException dme) {
			System.out.println("*** ApplicationService.startDemo(): " + dme);
			directives.add(DIRECTIVE_SHOW_MESSAGE, "The demo is not available (" + dme.getMessage() + ")",
				new Integer(NOTIFICATION_ERROR));
		}
	}

	// ---

	public Session getSession(int sessionID) {
		return clientSessions[sessionID];
	}



	// ----------------------------------------
	// --- Messaging Service (asynchronous) ---
	// ----------------------------------------



	/**
	 * @throws	DeepaMehtaException	if the specified message is unknown,
	 *								known messages are <code>import</code>,
	 *								<code>export</code> and <code>importCM</code>
	 *
	 * @see		MessagingConnection#processMessage
	 */
	public void processMessage(String message, Session session) {
		StringTokenizer st = new StringTokenizer(message, ":");
		String msg = st.nextToken();
		//
		if (msg.equals("import")) {
			// Note: message is created by TopicMapTopic.doImport()
			String filename = st.nextToken();
			int x = Integer.parseInt(st.nextToken());
			int y = Integer.parseInt(st.nextToken());
			importTopicmap(filename, x, y, session);
		} else if (msg.equals("export")) {
			// Note: message is created by TopicMapTopic.doExport()
			String mapID = st.nextToken();
			String topicmapID = st.nextToken();
			String viewmode = st.nextToken();
			int x = Integer.parseInt(st.nextToken());
			int y = Integer.parseInt(st.nextToken());
			exportTopicmap(mapID, session, topicmapID, viewmode, x, y);
		} else if (msg.equals("importCM")) {
			// Note: message is created by CMImportExport.importCMChained()
			String filename = st.nextToken();
			importCM(filename, session);
		} else {
			throw new DeepaMehtaException("unexpected message: \"" + message + "\"");
		}
	}

	// ---

	/**
	 * Extends the specified directives to let the client create the personal workspace
	 * for the specified client session.
	 *
	 * @see		#startSession
	 */
	private void addPersonalWorkspace(Session session, CorporateDirectives directives) {
		PresentableTopic personalWorkspace;
		try {
			String userID = session.getUserID();
			personalWorkspace = getWorkspaceTopicmap(userID);
			// error check
			if (personalWorkspace == null) {
				System.out.println("*** InteractionConnection.addPersonalWorkspace(): " +
					"user \"" + userID + "\" has no workspace");
				directives.add(DIRECTIVE_SHOW_MESSAGE, "user \"" + userID +
					"\" has no workspace", new Integer(NOTIFICATION_WARNING));
			}
		} catch (AmbiguousSemanticException e) {
			System.out.println("*** InteractionConnection.addPersonalWorkspace(): " + e);
			directives.add(DIRECTIVE_SHOW_MESSAGE, e.getMessage(), new Integer(NOTIFICATION_WARNING));
			personalWorkspace = (PresentableTopic) e.getDefaultTopic();
		}
		if (personalWorkspace != null) {
			session.setPersonalWorkspace(personalWorkspace);
			// causes the client to show the users personal workspace
			CorporateTopicMap topicmap = new CorporateTopicMap(this, personalWorkspace.getID(), 1);
			directives.add(DIRECTIVE_SHOW_WORKSPACE, personalWorkspace, topicmap, new Integer(EDITOR_CONTEXT_PERSONAL));	// ###
			// reporting
			if (LOG_MAPS) {
				System.out.println("> personal workspace: " + personalWorkspace);
			}
		}
	}

	/**
	 * Extends the specified directives to let the client create the group workspaces
	 * for the specified client session.
	 *
	 * @see		#startSession
	 */
	private void addGroupWorkspaces(Session session, CorporateDirectives directives) {
		Enumeration e = getWorkspaces(session.getUserID()).elements();
		while (e.hasMoreElements()) {
			PresentableTopic groupWorkspace;
			try {
				BaseTopic workgroup = (BaseTopic) e.nextElement();
				String workgroupID = workgroup.getID();
				groupWorkspace = getWorkspaceTopicmap(workgroupID);
				if (groupWorkspace == null) {
					System.out.println("*** InteractionConnection.addGroupWorkspaces():" +
						" workgroup \"" + workgroupID + "\" has no workspace");
					directives.add(DIRECTIVE_SHOW_MESSAGE, "workgroup \"" + workgroupID +
						"\" has no workspace", new Integer(NOTIFICATION_WARNING));
				}
			} catch (AmbiguousSemanticException e2) {
				System.out.println("*** InteractionConnection.addGroupWorkspaces(): " + e2);
				directives.add(DIRECTIVE_SHOW_MESSAGE, e2.getMessage(), new Integer(NOTIFICATION_WARNING));
				groupWorkspace = (PresentableTopic) e2.getDefaultTopic();
			}
			// open group workspace
			if (groupWorkspace != null) {
				CorporateTopicMap topicmap = new CorporateTopicMap(this, groupWorkspace.getID(), 1);
				directives.add(DIRECTIVE_SHOW_WORKSPACE, groupWorkspace, topicmap, new Integer(EDITOR_CONTEXT_WORKGROUP));
			}
		}
	}

	/**
	 * Extend the specified directives to let the client open the views from previous session.
	 *
	 * @see		#startSession
	 */
	private void addViewsInUse(Session session, CorporateDirectives directives) {
		Vector topicmaps = getViewsInUse(session.getUserID());
		//
		if (topicmaps.size() == 0) {
			if (LOG_MAPS) {
				System.out.println("> no views in use");
			}
			return;
		}
		//
		Enumeration e = topicmaps.elements();
		BaseTopic topicmapBase;
		LiveTopic topicmap;
		while (e.hasMoreElements()) {
			topicmapBase = (BaseTopic) e.nextElement();
			// reporting
			if (LOG_MAPS) {
				System.out.println(">     view in use: " + topicmapBase);
			}
			topicmap = getLiveTopic(topicmapBase, session, directives);
			// --- trigger openTopicmap() hook ---
			try {
				topicmap.openTopicmap(session, directives);
			} catch (DeepaMehtaException e2) {
				System.out.println("*** ApplicationService.addViewsInUse(): " +
					topicmap.getClass() + ": " + e2.getMessage());
				// if a DeepaMehtaException is thrown by the open hook only one
				// directive is added
				directives.add(DIRECTIVE_SHOW_MESSAGE, e2.getMessage(),
					new Integer(NOTIFICATION_WARNING));	// ### parametric notification needed
			}
		}
	}

	// ---

	/**
	 * @see		de.deepamehta.topics.TopicMapTopic#open
	 */
	public void addViewInUse(String viewID, Session session) {
		System.out.println(">>> remember open view \"" + viewID + "\" for user \"" + session.getUserName() + "\"");
		cm.createAssociation(getNewAssociationID(), 1, SEMANTIC_VIEW_IN_USE, 1, session.getUserID(), 1, viewID, 1);
	}

	/**
	 * @see		CorporateDirectives#updateCorporateMemory
	 */
	void removeViewInUse(String viewID, Session session) {
		// ### also called for closing workspaces (not really a problem)
		// ### could establish DIRECTIVE_CLOSE_VIEW and DIRECTIVE_CLOSE_WORKSPACE instead of DIRECTIVE_CLOSE_EDITOR
		BaseAssociation assoc = cm.getAssociation(SEMANTIC_VIEW_IN_USE, session.getUserID(), viewID);
		// ### error check
		if (assoc == null) {
			System.out.println(">>> view \"" + viewID + "\" is currently not opened for user \"" + session.getUserName() + "\"");
			return;
		}
		//
		System.out.println(">>> forget open view \"" + viewID + "\" for user \"" + session.getUserName() + "\"");
		deleteAssociation(assoc);
	}

	// ---

	/**
	 * @see		#processMessage
	 */
	private void exportTopicmap(String mapID, Session session, String topicmapID, String viewmode, int x, int y) {
		TopicMapTopic topicmap = null;
		try {
			topicmap = (TopicMapTopic) getLiveTopic(mapID, 1);
			CorporateDirectives directives = topicmap.export(session, topicmapID, viewmode, x, y);	// may throw DME
			host.sendDirectives(session, directives, this, mapID, VIEWMODE_USE);
		} catch (DeepaMehtaException e) {
			CorporateDirectives directives = new CorporateDirectives();
			directives.add(DIRECTIVE_SHOW_MESSAGE, "<U>Export</U> of topicmap \"" +
				topicmap.getName() + "\" (" + mapID + ":1) was <U>not successful</U>" +
				" (" + e.getMessage() + ")", new Integer(NOTIFICATION_WARNING));
			host.sendDirectives(session, directives, null, null, null);
		}
	}

	/**
	 * @see		#processMessage
	 */
	private void importTopicmap(String filename, int x, int y, Session session) throws DeepaMehtaException {
		File topicmapFile = new File(FileServer.repositoryPath(FILE_DOCUMENT) + filename);
		String topicmapID = session.getPersonalWorkspace().getID();
		TopicMapImporter importer = new TopicMapImporter(this);
		CorporateDirectives directives = importer.doImport(topicmapFile, session, x, y);
		host.sendDirectives(session, directives, this, topicmapID, VIEWMODE_USE);
		// reporting
		System.out.println(">>> topicmap import complete");
		importer.report();
	}

	/**
	 * ### better static in CMImportExportTopic?
	 *
	 * @see		#processMessage
	 */
	private void importCM(String filename, Session session) throws DeepaMehtaException {
		File cmFile = new File(FileServer.repositoryPath(FILE_DOCUMENT) + filename);
		String topicmapID = session.getPersonalWorkspace().getID();
		TopicMapImporter importer = new TopicMapImporter(this);
		CorporateDirectives directives = importer.doImport(cmFile, null, 0, 0);	// may throw DME
		host.sendDirectives(session, directives, this, topicmapID, VIEWMODE_USE);
		// reporting
		System.out.println(">>> CM import complete");
		importer.report();
	}

	// ---

	/**
	 * References checked: 7.6.2006 (2.0b7)
	 *
	 * @see		#createSession
	 * @see		#removeSession
	 * @see		#startSession
	 * @see		#startDemo
	 */
	private void updateServerConsole() {
		if (serverConsole != null) {
			serverConsole.updateSessions();
		}
	}

	/**
	 * References checked: 7.6.2006 (2.0b7)
	 *
	 * @see		DeepaMehtaServer#main
	 */
	public void setServerConsole(ServerConsole serverConsole) {
		this.serverConsole = serverConsole;
	}

	// ---

	/**
	 * References checked: 20.5.2008 (2.0b8)
	 *
	 * @see		de.deepamehta.topics.TopicTypeTopic#nameChanged
	 * @see		de.deepamehta.topics.TopicTypeTopic#createSearchType
	 */
	public String searchTypeName(String typeName) throws DeepaMehtaException {
		String searchTypeName = typeName + " " + CONTAINER_SUFFIX_NAME;
		// error check
		int len = searchTypeName.length();
		if (len > MAX_NAME_LENGTH) {
			throw new DeepaMehtaException("search type name for \"" + typeName + "\" too long: \"" +
				searchTypeName + "\", " + len + " characters, maximum is " + MAX_NAME_LENGTH + " characters");
		}
		//
		return searchTypeName;
	}



	// -----------------------------------
	// --- Creating Presentable Topics ---
	// -----------------------------------



	/**
	 * Creates {@link de.deepamehta.PresentableTopic}s from a set of {@link de.deepamehta.BaseTopic}s.
	 * The geometry is set to be "near" (<code>GEOM_MODE_NEAR</code>) to a reference topic.
	 * The appearance is set to "individual" (<code>APPEARANCE_CUSTOM_ICON</code>).
	 * ### The appearance must not be set this way. If a PresentableTopic with APPEARANCE_DEFAULT is about to
	 * ### be displayed inside a topicmap it would loose its icon-change behavoir if the type-icon changes.
	 * ### APPEARANCE_DEFAULT must not be overridden with APPEARANCE_CUSTOM_ICON.
	 * <p>
	 * References checked: 21.9.2008 (2.0b8)
	 *
	 * @return	Vector of {@link de.deepamehta.PresentableTopic}
	 *
	 * @see		de.deepamehta.topics.TopicContainerTopic#performQuery
	 */
	public Vector createPresentableTopics(String topicmapID, Vector baseTopics, String nearTopicID) {
		Vector topics = new Vector();
		//
		Enumeration e = baseTopics.elements();
		while (e.hasMoreElements()) {
			BaseTopic t = (BaseTopic) e.nextElement();
			PresentableTopic topic = createPresentableTopic(t, nearTopicID, topicmapID);
			// ### init appearance ###
			// ### topic.setIcon(getIconfile(t));
			//
			topics.addElement(topic);
		}
		return topics;
	}

	// --- createPresentableTopic (6 forms) ---

	public PresentableTopic createPresentableTopic(String topicID, int version) throws DeepaMehtaException {
		PresentableTopic topic = createPresentableTopic(cm.getTopic(topicID, version));
		return topic;
	}

	/**
	 * ### Actually creates an PresentableTopic based on the specified BaseTopic
	 * <i>and</i> initializes its appearance. Note: the appearance initialization
	 * is based on the corresponding LiveTopic, thus an DeepaMehtaException is
	 * thrown if the topic dosn't exist in live corporate memory. The geometry of
	 * the created PresentableTopic remains uninitialized.
	 *
	 * @throws	DeepaMehtaException		if the specified topic is not in
	 *									ApplicationService resp. is not
	 *									properly initialized (iconfile is unknown)
	 *
	 * @see		de.deepamehta.topics.TopicMapTopic#openPersonalTopicmap
	 */
	public PresentableTopic createPresentableTopic(BaseTopic topic) throws DeepaMehtaException {
		return createPresentableTopic(topic, topic.getID());
	}

	/**
	 * @throws	DeepaMehtaException		if the specified topic is not loaded resp. not
	 *									properly initialized (iconfile is unknown)
	 * @see		#getWorkspaceTopicmap
	 * @see		#getCorporateSpace
	 * @see		#createPresentableTopic(BaseTopic topic)	(above)
	 * @see		de.deepamehta.topics.TopicMapTopic#openSharedTopicmap
	 */
	public PresentableTopic createPresentableTopic(BaseTopic topic, String appTopicID) throws DeepaMehtaException {
		PresentableTopic presentableTopic = new PresentableTopic(topic);
		// ### version is set to 1
		String iconfile = getIconfile(appTopicID, 1);	// may throw DME
		presentableTopic.setIcon(iconfile);
		return presentableTopic;
	}

	public PresentableTopic createPresentableTopic(String topicID, String nearTopicID) {
		// ### version is set to 1
		return new PresentableTopic(cm.getTopic(topicID, 1), nearTopicID);
	}

	public PresentableTopic createPresentableTopic(String typeID, String name, String nearTopicID) {
		// check if topic already exists in corporate memory
		Vector topics = cm.getTopics(typeID, name);
		int count = topics.size();
		if (count == 0) {
			PresentableTopic topic = new PresentableTopic(getNewTopicID(), 1, typeID, 1, name, nearTopicID);
			topic.setEvoke(true);
			return topic;
		} else {
			BaseTopic topic = (BaseTopic) topics.firstElement();
			if (count > 1) {
				System.out.println("*** ApplicationService.createPresentableTopic(): there're " +
					count + " \"" + name + "\" (" + typeID + ") topics");
			}
			return new PresentableTopic(topic, nearTopicID);
		}
	}

	/**
	 * Creates a PresentableTopic for the specified topic that is about to appear in the near of another topic.
	 * This is done by triggering the <code>getPresentableTopic()</code> hook of the specified topic map.
	 * This way a custom topic map is able to adjust e.g. the geometry of the appearing topic.
	 *
	 * @see		#createPresentableTopics
	 * @see		de.deepamehta.topics.LiveTopic#revealTopic
	 */
	public PresentableTopic createPresentableTopic(BaseTopic topic, String nearTopicID, String topicmapID) {
		// --- trigger getPresentableTopic() hook ---
		return getLiveTopic(topicmapID, 1).getPresentableTopic(topic, nearTopicID);
	}

	// --- createPresentableAssociation (2 forms) ---

	public PresentableAssociation createPresentableAssociation(String assocTypeID, String topicID1, int topicVersion1,
													String topicID2, int topicVersion2, boolean performExistenceCheck) {
		return createPresentableAssociation(assocTypeID, "",
			topicID1, topicVersion1, topicID2, topicVersion2, performExistenceCheck);
	}

	/**
	 * <table>
	 * <tr><td><b>Called by</b><td><code>performExistenceCheck</code>
	 * <tr><td>{@link #createNewContainer}<td><code>false</code>
	 * <tr><td>{@link #createPresentableAssociations}<td><code>true</code>
	 * <tr><td>{@link de.deepamehta.topics.ElementContainerTopic#autoSearch}<td><code>false</code>
	 * <tr><td>{@link de.deepamehta.topics.ElementContainerTopic#revealTopic}<td><code>false</code>
	 * </table>
	 */
	public PresentableAssociation createPresentableAssociation(String assocTypeID, String assocName,
									String topicID1, int topicVersion1,
									String topicID2, int topicVersion2,
									boolean performExistenceCheck) {
		String assocID;
		if (!performExistenceCheck || !cm.associationExists(topicID1, topicID2, false)) {
			assocID = cm.getNewAssociationID();
		} else {
			Association assoc = cm.getAssociation(assocTypeID, topicID1, topicID2);
			if (assoc == null) {
				assocID = cm.getNewAssociationID();
			} else {
				assocID = assoc.getID();
			}
		}
		return new PresentableAssociation(assocID, 1, assocTypeID, 1, assocName,
			topicID1, topicVersion1, topicID2, topicVersion2);
	}

	// --- createWebpageTopic (2 forms) ---

	// ###
	public PresentableTopic createWebpageTopic(URL url, String nearTopicID) {
		return createWebpageTopic(url.toString(), null, nearTopicID);
	}

	/**
	 * ### Note: LCM.createPresentableTopic(typeID, name, nearTopicID) is not appropriate
	 * because for webpages the existence check is based on the "URL" property.
	 *
	 * @see		#revealLink
	 */
	public PresentableTopic createWebpageTopic(String url, String title, String nearTopicID) {
		// compare to LiveTopic.createPresentableTopic()
		Hashtable props = new Hashtable();
		props.put("URL", url);	// ### ignore ref -- still an issue?
		// check if webpage already exists in corporate memory
		Vector webpages = cm.getTopics(TOPICTYPE_WEBPAGE, props);
		int count = webpages.size();
		if (count == 0) {
			PresentableTopic webpage = new PresentableTopic(getNewTopicID(), 1,
				TOPICTYPE_WEBPAGE, 1, title != null ? title : "", nearTopicID);
			if (title != null) {
				props.put(PROPERTY_NAME, title);
			}
			webpage.setProperties(props);
			webpage.setEvoke(true);
			return webpage;
		} else {
			BaseTopic webpage = (BaseTopic) webpages.firstElement();
			if (count > 1) {
				System.out.println("*** PersonalWeb.createWebpageTopic(): there're " + count +
					" Webpage topics for \"" + url + "\"");
			}
			return new PresentableTopic(webpage, nearTopicID);
		}
	}

	// ---

	public void setWebpageTopicName(String webpageID, String html, String topicmapID, String viewmode, Session session) {
		HTMLParser parser = new HTMLParser(html);
		String title = parser.textRange("<TITLE>", "</TITLE>");	// ### attributes?
		//
		CorporateDirectives directives = new CorporateDirectives();
		// ### directives.add(DIRECTIVE_SET_TOPIC_NAME, webpageID, title, new Integer(1), topicmapID, viewmode);	// ###
		//
		Hashtable props = new Hashtable();
		props.put(PROPERTY_NAME, title);
		props.put(PROPERTY_DESCRIPTION, html);
		directives.add(DIRECTIVE_SHOW_TOPIC_PROPERTIES, webpageID, props, new Integer(1));
		//
		getHostObject().sendDirectives(session, directives, this, topicmapID, viewmode);
	}

	// --- getIconfile (2 forms) ---

	/**
	 * References checked: 18.10.2001 (2.0a13-pre1)
	 *
	 * @throws	DeepaMehtaException		if the specified topic is not in
	 *									ApplicationService resp. is not
	 *									properly initialized (iconfile is unknown)
	 *
	 * @see		de.deepamehta.topics.TopicMapTopic#addPublishCommand
	 */
	public String getIconfile(BaseTopic topic) throws DeepaMehtaException {
		return getIconfile(topic.getID(), topic.getVersion());	// throws DME
	}

	/**
	 * References checked: 18.10.2001 (2.0a13-pre1)
	 *
	 * @throws	DeepaMehtaException		if the specified topic is not in
	 *									ApplicationService resp. is not
	 *									properly initialized (iconfile is unknown)
	 *
	 * @see		#createPresentableTopic
	 * @see		de.deepamehta.topics.TopicContainerTopic#getAppearance
	 * @see		de.deepamehta.topics.ElementContainerTopic#getAppearance
	 */
	public String getIconfile(String topicID, int version) throws DeepaMehtaException {
		return getLiveTopic(topicID, version).getIconfile();	// throws DME
	}

	// ---

	/**
	 * @see		CorporateDirectives#showTopic
	 */
	void initTopicLock(PresentableTopic topic) {
		if (getTopicProperty(topic, PROPERTY_LOCKED_GEOMETRY).equals(SWITCH_ON)) {
			topic.setLocked(true);
		}
	}

	// ---

	/**
	 * References checked: 3.12.2001 (2.0a14-pre1)
	 *
	 * @see		#setAppearance
	 * @see		CorporateDirectives#showTopic
	 */
	void initTopicAppearance(PresentableTopic topic) throws DeepaMehtaException {
		LiveTopic t = getLiveTopic(topic);					// may throw DME
		if (t.getIndividualAppearanceMode() == APPEARANCE_CUSTOM_ICON) {
			topic.setIcon(t.getIndividualAppearanceParam());
		}
	}

	/**
	 * @see		#initTypeTopic
	 */
	private void initTypeAppearance(PresentableType type, TypeTopic typeTopic) {
		type.setTypeIconfile(typeTopic.getIconfile());		// throws DME
		type.setAssocTypeColor(typeTopic.getAssocTypeColor());
	}

	// ---

	/**
	 * Returns a string containing the comma separated property names
	 * involved in the specified query.
	 * <p>
	 * This value is used for the "QueryElements" property.
	 * The value may be the empty string.
	 *
	 * @see		#createNewContainer
	 * @see		de.deepamehta.topics.ElementContainerTopic#autoSearch
	 */
	public String queryElements(Hashtable query) {
		Enumeration e = query.keys();
		StringBuffer queryElements = new StringBuffer();
		String sep = "";
		String fieldname;
		String value;
		while (e.hasMoreElements()) {
			fieldname = (String) e.nextElement();
			value = (String) query.get(fieldname);
			if (!value.equals("")) {
				queryElements.append(sep + fieldname);
				sep = ",";	// >>> thanks to LUM
			} else {
				System.out.println("*** ContainerTopic.queryElements(): \"" + fieldname +
					"\" of " + this + " is empty -- ignored");
			}
		}
		return queryElements.toString();
	}

	/**
	 * Returns a string containing the comma separated property values
	 * involved in the specified query.
	 * <p>
	 * This string is used as container name.
	 *
	 * @see		#createNewContainer
	 * @see		de.deepamehta.topics.ElementContainerTopic#autoSearch
	 */
	public String queryString(Hashtable query) {
		Enumeration e = query.keys();
		StringBuffer queryString = new StringBuffer();
		String sep = "";
		String fieldname;
		String value;
		while (e.hasMoreElements()) {
			fieldname = (String) e.nextElement();
			value = (String) query.get(fieldname);
			if (!value.equals("")) {
				queryString.append(sep + value);
				sep = ", ";
			} else {
				System.out.println("*** ApplicationService.queryString(): \"" + fieldname +
					"\" of " + this + " is empty -- ignored");
			}
		}
		return queryString.toString();
	}

	// ---

	// ### copy in PresentationService
	public int getLanguage() {
		String lang = (String) installationProps.get(PROPERTY_LANGUAGE);
		return lang == null || lang.equals("English") ? LANGUAGE_ENGLISH : LANGUAGE_GERMAN;	// ###
	}

	/* ### public int getLanguage(Session session) {
		if (session == null) {
			// ### System.out.println("*** getLanguage(): no session exists --> LANGUAGE_ENGLISH is used");
			return LANGUAGE_ENGLISH;	// ###
		}
		return session.getUserPreferences().language;
	} */

	// --- string (2 forms) ---

	// ### copy in PresentationService
	public String string(int item) {
		return strings[item][getLanguage()];
	}

	public String string(int item, String param) {
		String str = string(item);
		//
		int pos = str.indexOf("\\1");
		if (pos == -1) {
			System.out.println("*** ApplicationService.string(): \"\\1\" not found in \"" + str + "\"");
			return str;
		}
		//
		return str.substring(0, pos) + param + str.substring(pos + 2);
	}

	// --- typeName (2 forms) ---

	/**
	 * Returns the type name of the specified topic.
	 * <p>
	 * May be empty, but never <code>null</code>.
	 */
	public String typeName(String typeID) {
		return type(typeID, 1).getName();
	}

	/**
	 * Returns the type name of the specified topic.
	 * <p>
	 * May be empty, but never <code>null</code>.
	 */
	public String typeName(BaseTopic topic) {
		return type(topic).getName();
	}

	// --- type (3 forms) ---

	/**
	 * Returns the type of the specified topic as live topic (type <code>tt-topictype</code>).
	 *
	 * @see		#typeName
	 * @see		#types
	 * @see		#isInstanceOf
	 * @see		#getImplementingClass
	 */
	public TopicTypeTopic type(BaseTopic topic) throws DeepaMehtaException {
		String typeID = topic.getType();
		try {
			BaseTopic type = cm.getTopic(typeID, 1);			// ### type version 1 ### avoid cm access
			// error check
			if (type == null) {
				throw new DeepaMehtaException("type of " + topic + " is missing in corporate memory");
			}
			//
			return (TopicTypeTopic) getLiveTopic(type);			// ### may throw DME ### session, directives?;
		} catch (ClassCastException e) {
			throw new DeepaMehtaException("error while accessing the type topic of " + topic + ": " + e);
		}
	}

	/**
	 * Returns the type of the specified association as live topic (type <code>tt-assoctype</code>).
	 *
	 * @see		de.deepamehta.topics.TopicMapTopic#makeAssociationsXML
	 */
	public AssociationTypeTopic type(BaseAssociation assoc) throws DeepaMehtaException {
		String typeID = assoc.getType();
		try {
			BaseTopic type = cm.getTopic(typeID, 1);			// ### type version 1 ### avoid cm access
			// error check
			if (type == null) {
				throw new DeepaMehtaException("type of " + assoc + " is missing in corporate memory");
			}
			return (AssociationTypeTopic) getLiveTopic(type);	// ### may throw DME ### session, directives?
		} catch (ClassCastException e) {
			throw new DeepaMehtaException("error while accessing the type topic of " + assoc + ": " + e);
		}
	}

	/**
	 * Returns the specified type as live topic (type <code>tt-topictype</code> or <code>tt-assoctype</code>).
	 *
	 * @see		CorporateCommands#addTypeCommands
	 * @see		CorporateCommands#addTypeCommand
	 * @see		CorporateCommands#addTopicTypeCommands
	 */
	public TypeTopic type(String typeID, int typeVersion) throws DeepaMehtaException {
		try {
			// error check 1
			if (typeID == null) {
				throw new DeepaMehtaException("null is passed as \"typeID\"");
			}
			BaseTopic type = cm.getTopic(typeID, typeVersion);		// ### avoid cm access
			// error check 2
			if (type == null) {
				throw new DeepaMehtaException("type \"" + typeID + "\" is missing in corporate memory");
			}
			//
			return (TypeTopic) getLiveTopic(type);				// ### may throw DME ### session, directives?
		} catch (ClassCastException e) {
			throw new DeepaMehtaException("error while accessing the type \"" + typeID + ":" + typeVersion +"\": " + e);
		}
	}

	// ---

	public TopicTypeTopic getTopicType(String topicID, int version) {
		return (TopicTypeTopic) getLiveTopic(getLiveTopic(topicID, version).getType(), 1);
	}

	public AssociationTypeTopic getAssocType(String assocID, int version) {
		return (AssociationTypeTopic) getLiveTopic(getLiveAssociation(assocID, version).getType(), 1);
	}

	// ---

	private String getTopicTypeID(String typeName) {
		BaseTopic type = cm.getTopic(TOPICTYPE_TOPICTYPE, typeName, 1);	// ### version=1
		return type != null ? type.getID() : null;
	}

	private String getAssocTypeID(String typeName) {
		BaseTopic type = cm.getTopic(TOPICTYPE_ASSOCTYPE, typeName, 1);	// ### version=1
		return type != null ? type.getID() : null;
	}

	// ---

	/**
	 * Collect the type definitions for the given elements (topics or associations).
	 * <p>
	 * References checked: 18.4.2002 (2.0a14-post1)
	 *
	 * @param   elements    an enumeration of BaseTopics or BaseAssociations
	 *
	 * @return  a Hashtable containing the types
	 *
	 * @see     de.deepamehta.topics.TopicMapTopic#exportTopicmap
	 */
	public Hashtable types(Enumeration elements) {
		// --- collect all types of the given elements
		Hashtable types = new Hashtable();
		while (elements.hasMoreElements()) {
			Object element = elements.nextElement();
			TypeTopic type = element instanceof BaseTopic ?
				(TypeTopic) type((BaseTopic) element) :
				(TypeTopic) type((BaseAssociation) element);
			types.put(type.getID(), type);
		}
		// --- complete the type collection with supertypes and containertypes
		// ### combine both loops into one ### dont use two hashtables
		Hashtable allTypes = new Hashtable();
		Enumeration collectedTypes = types.elements();
		while (collectedTypes.hasMoreElements()) {
			((TypeTopic) collectedTypes.nextElement()).completeTypeDefinition(allTypes);
		}
		return allTypes;
	}

	// ---

	public boolean isInstanceOf(BaseTopic topic, String typeID) {
		TypeTopic type = type(topic);	// ### type version is 1
		return type.hasSupertype(typeID);
	}



	// ***********************
	// *** Private Methods ***
	// ***********************



	/**
	 * @param	topics	Vector of PresentableTopic
	 * @return	The number of created topics.
	 *
	 * @see		#loadKernelTopics
	 */
	/* ### private int createLiveTopics(Vector topics) {
		return createLiveTopics(topics.elements(), null, null);
	} */

	/**
	 * @return	The number of created topics
	 *
	 * @see		#createLiveTopics(Vector)	private (above)
	 * @see		#createLiveTopics(PresentableTopicMap, CorporateDirectives, Session)  package
	 */
	private int createLiveTopics(Enumeration topics, CorporateDirectives directives, Session session) {
		int count = 0;
		//
		while (topics.hasMoreElements()) {
			try {
				BaseTopic topic = (BaseTopic) topics.nextElement();
				// Note: evoke=false, even though directives can be returned, actually
				// DIRECTIVE_SHOW_MESSAGE for displaying possible error messages
				CorporateDirectives dirs = createLiveTopic(topic, false, session);
				if (dirs != null) {
					if (directives != null) {
						directives.add(dirs);
					}
					count++;
				}
			} catch (TopicInitException e) {
				System.out.println("*** ApplicationService.createLiveTopics(): " + e.getMessage());
				if (directives != null) {
					directives.add(DIRECTIVE_SHOW_MESSAGE, e.getMessage(), new Integer(NOTIFICATION_ERROR));
				}
			}
		}
		//
		return count;
	}

	// ---

	/**
	 * @see		#personalizeView
	 */
	private void personalizeTopics(String topicmapID, int topicmapVersion, String viewMode, Enumeration topics,
																String origTopicmapID, boolean performExistenceCheck) {
		while (topics.hasMoreElements()) {
			PresentableTopic topic = (PresentableTopic) topics.nextElement();
			Point p = topic.getGeometry();
			// ### Note: if the view to duplicate contains itself the ID must be mapped, e.g. Kompetenzstern Template
			if (topic.getID().equals(origTopicmapID)) {
				topic.setID(topicmapID);
			}
			createViewTopic(topicmapID, topicmapVersion, viewMode, topic.getID(), 1, p.x, p.y, performExistenceCheck);	// ### version=1
		}
	}

	/**
	 * @see		#personalizeView
	 */
	private void personalizeAssociations(String topicmapID, int topicmapVersion, String viewMode, Enumeration assocs,
																String origTopicmapID, boolean performExistenceCheck) {
		// ### System.out.println(">>> ApplicationService.personalizeAssociations(): view \"" + origTopicmapID + "\" -> personal view \"" + topicmapID + "\"");
		while (assocs.hasMoreElements()) {
			PresentableAssociation assoc = (PresentableAssociation) assocs.nextElement();
			boolean doMapping = false;
			//
			if (assoc.getTopicID1().equals(origTopicmapID)) {
				// ### System.out.print("> assoc \"" + assoc.getID() + "\" maps topic at pos 1 ... ");
				assoc.setTopicID1(topicmapID);
				doMapping = true;
			} else if (assoc.getTopicID2().equals(origTopicmapID)) {
				// ### System.out.print("> assoc \"" + assoc.getID() + "\" maps topic at pos 2 ... ");
				assoc.setTopicID2(topicmapID);
				doMapping = true;
			}
			if (doMapping) {
				assoc.setID(getNewAssociationID());
				cm.createAssociation(assoc.getID(), 1, assoc.getType(), 1, assoc.getTopicID1(), 1, assoc.getTopicID2(), 1);
			}
			//
			createViewAssociation(topicmapID, topicmapVersion, viewMode, assoc.getID(), assoc.getVersion(),
				performExistenceCheck);
		}
	}

	/* ### 
	/ **
	 * @see		#personalizeView
	 * /
	private void personalizeAssociations(String topicmapID, int topicmapVersion, String viewMode, Enumeration assocs,
																					boolean performExistenceCheck) {
		while (assocs.hasMoreElements()) {
			PresentableAssociation assoc = (PresentableAssociation) assocs.nextElement();
			createViewAssociation(topicmapID, topicmapVersion, viewMode, assoc.getID(), assoc.getVersion(),
				performExistenceCheck);
		}
	} */

	// ---

	/**
	 * ### just passed to corporate memory -- to be dropped
	 *
	 * @see		#personalizeTopics
	 * @see		CorporateDirectives#createLiveTopic	createLiveTopic() performExistenceCheck=true
	 * @see		de.deepamehta.topics.UserTopic#createPersonalWorkspace
	 * @see		de.deepamehta.topics.UserTopic#createConfigurationMap
	 */
	public void createViewTopic(String topicmapID, int topicmapVersion, String viewMode,
											String topicID, int topicVersion,
											int x, int y, boolean performExistenceCheck) {
		cm.createViewTopic(topicmapID, topicmapVersion, viewMode, topicID, topicVersion,
			x, y, performExistenceCheck);
	}

	/**
	 * ### just passed to corporate memory -- to be dropped
	 *
	 * @see		#personalizeAssociations
	 * @see		InteractionConnection#performAddAssociation
	 * @see		CorporateDirectives#createLiveAssociation
	 */
	void createViewAssociation(String topicmapID, int topicmapVersion, String viewMode,
						String assocID, int assocVersion, boolean performExistenceCheck) {
		cm.createViewAssociation(topicmapID, topicmapVersion, viewMode, assocID,
			assocVersion, performExistenceCheck);
	}

	// ---

	/**
	 * Creates associations in corporate memory (storage layer) corresponding to the
	 * topics in the specified vector and returns a vector of corresponding
	 * {@link de.deepamehta.PresentableAssociation}s.
	 *
	 * @return	Vector of {@link de.deepamehta.PresentableAssociation}
	 *
	 * @see		#createNewContainer
	 */
	private Vector createPresentableAssociations(String containerID, Vector topics, String assocTypeID) {
		Vector assocs = new Vector();
		Enumeration e = topics.elements();
		while (e.hasMoreElements()) {
			Topic topic = (Topic) e.nextElement();
			PresentableAssociation assoc = createPresentableAssociation(assocTypeID, containerID, 1,
				topic.getID(), 1, true);
			assocs.addElement(assoc);
		}
		return assocs;
	}

	// ---

	/* ### private boolean liveTopicExists(BaseTopic topic) {
		return liveTopicExists(topic.getID(), topic.getVersion());
	} */

	/**
	 * @see		#checkLiveTopic
	 * @see		#createLiveTopic
	 */
	private boolean liveTopicExists(String topicID, int version) {
		// Note: topicExists() is from BaseTopicMap
		return topicExists(topicID + ":" + version);
	}

	// --- liveAssociationExists (2 forms) ---

	/**
	 * @see		#checkLiveAssociation(BaseAssociation assoc)
	 */
	private boolean liveAssociationExists(BaseAssociation assoc) {
		return liveAssociationExists(assoc.getID(), assoc.getVersion());
	}

	/**
	 * @see		#checkLiveAssociation(String assocID, int version)
	 */
	private boolean liveAssociationExists(String assocID, int version) {
		// Note: associationExists() is from BaseTopicMap
		return associationExists(assocID + ":" + version);
	}

	// ---

	/**
	 * @see		#setTopicProperties(String topicID, int version, Hashtable props, String topicmapID, String viewmode, Session session)
	 * @see		#setAssocProperties(String assocID, int version, Hashtable props, String topicmapID, String viewmode, Session session)
	 */
	private Hashtable removeUnchangedProperties(Hashtable newProps, Hashtable oldProps) {
		Hashtable result = (Hashtable) newProps.clone();
		//
		Enumeration e = result.keys();
		while (e.hasMoreElements()) {
			String propName = (String) e.nextElement();
			String oldValue = (String) oldProps.get(propName);
			String newValue = (String) result.get(propName);
			if (newValue.equals(oldValue) || oldValue == null && newValue.equals("")) {
				result.remove(propName);
			}
		}
		//
		return result;
	}

	// ---

	/**
	 * @see		#createNewContainer
	 */
	private String containerName(String defaultName, String nameFilter, Hashtable propertyFilter) {
		if (defaultName != null) {
			return defaultName;
		} else {
			String queryString = queryString(propertyFilter);
			if (nameFilter == null || nameFilter.length() == 0) {
				return queryString;
			} else {
				String containerName = "\"" + nameFilter + "\"";
				if (queryString.length() > 0) {
					containerName += ", ";
					containerName += queryString;
				}
				return containerName;
			}
		}
	}

	// ---

	/**
	 * @see		#createLiveTopic(BaseTopic topic, boolean override, Session session)
	 */
	private String getImplementingClass(BaseTopic topic) {
		String typeID = topic.getType();
		// --- bootstrap ### ---
		if (typeID.equals(TOPICTYPE_TOPICTYPE)) {
			return ACTIVE_TOPIC_PACKAGE + ".TopicTypeTopic";
		} else if (typeID.equals(TOPICTYPE_ASSOCTYPE)) {
			return ACTIVE_TOPIC_PACKAGE + ".AssociationTypeTopic";
		} else if (typeID.equals(TOPICTYPE_PROPERTY)) {
			return ACTIVE_TOPIC_PACKAGE + ".PropertyTopic";
		} else if (typeID.equals(TOPICTYPE_PROPERTY_VALUE)) {
			// Note: tt-constant has no custom implementation
			return ACTIVE_TOPIC_PACKAGE + ".LiveTopic";
		// --- normal case ---
		} else {
			return type(topic).getImplementingClass();
		}
	}

	// ---

	/**
	 * @see		#createLiveTopic
	 */
	private LiveTopic createCustomLiveTopic(BaseTopic topic, String implementingClass,
														CorporateDirectives directives) {
		try {
			String errorText = "Topic \"" + topic.getName() + "\" has no custom behavoir, " +
				"type: \"" + topic.getType() + "\", custom implementation: \"" +
				implementingClass + "\"";
			Class[] argClasses = {BaseTopic.class, ApplicationService.class};
			Object[] argObjects = {topic, this};
			//
			return (LiveTopic) instantiate(implementingClass, argClasses, argObjects, errorText, directives);
		} catch (DeepaMehtaException e) {
			System.out.println("*** ApplicationService.createCustomLiveTopic(): " + e);
			// fallback
			return new LiveTopic(topic, this);
		}
	}

	/**
	 * @param	directives	may be <code>null</code>
	 *
	 * @see		#createLiveAssociation
	 */
	private LiveAssociation createCustomLiveAssociation(BaseAssociation assoc, String implementingClass,
														CorporateDirectives directives) {
		try {
			String errorText = "Association \"" + assoc.getID() + "\" has no custom behavoir, type: \"" +
				assoc.getType() + "\", custom implementation: \"" + implementingClass + "\"";
			Class[] argClasses = {BaseAssociation.class, ApplicationService.class};
			Object[] argObjects = {assoc, this};
			//
			return (LiveAssociation) instantiate(implementingClass, argClasses, argObjects, errorText, directives);
		} catch (DeepaMehtaException e) {
			System.out.println("*** ApplicationService.createCustomLiveAssociation(): " + e);
			// fallback
			return new LiveAssociation(assoc, this);
		}
	}

	// ---

	/**
	 * @param	directives	may be <code>null</code>
	 *
	 * @see		#createCustomLiveTopic
	 * @see		#createCustomLiveAssociation
	 */
	private Object instantiate(String implementingClass, Class[] argClasses, Object[] argObjects,
						String errorText, CorporateDirectives directives) throws DeepaMehtaException {
		try {
			// create constructor
			Constructor cons = Class.forName(implementingClass).getConstructor(argClasses);
			// create instance
			Object obj = cons.newInstance(argObjects);
			//
			return obj;
		} catch (NoClassDefFoundError e) {
			String msg = e.getMessage();
			addErrorNotification("class \"" + msg + "\" not found", errorText, directives, msg);
		} catch (ClassNotFoundException e) {
			String msg = e.getMessage();
			addErrorNotification("class \"" + msg + "\" not found", errorText, directives, msg);
		} catch (NoSuchMethodException e) {
			addErrorNotification("no public \"" + implementingClass + "\" constructor found", errorText, directives);
		} catch (IllegalAccessException e) {
			addErrorNotification("class \"" + implementingClass + "\" not public", errorText, directives);
		} catch (InvocationTargetException e) {
			addErrorNotification("\"" + implementingClass + "\" constructor call failed", errorText, directives);
		} catch (InstantiationException e) {
			if (directives != null) {
				directives.add(DIRECTIVE_SHOW_MESSAGE, errorText + " (" + e + ")");
			}
			System.out.println("*** ApplicationService.instantiate(): " + e +
				" -- " + errorText);
		}
		throw new DeepaMehtaException("instantiation error");
	}

	/**
	 * References checked: 25.5.2006 (2.0b6-post3)
	 *
	 * @see		#triggerHiddenProperties(TypeTopic type)							false
	 * @see		#triggerHiddenProperties(TypeTopic type, String relTopicTypeID)		false
	 * @see		#triggerPropertyLabel												true
	 * @see		#triggerMakeProperties												true
	 * @see		#triggerGetSearchTypeID												true
	 * @see		CorporateCommands#addWorkspaceTopicTypeCommands						true
	 * @see		de.deepamehta.topics.LiveTopic#handleWorkspaceCommand				false
	 * @see		de.deepamehta.topics.TypeTopic#setPropertyLabel						false
	 * @see		de.deepamehta.topics.TypeTopic#addButton							true
	 */
	public Object triggerStaticHook(String className, String hookName, Class[] paramTypes, Object[] paramValues,
															boolean throwIfNoSuchHookExists) throws DeepaMehtaException {
		try {
			Class typeClass = Class.forName(className);							// throws ClassNotFoundException
			Method hook = typeClass.getDeclaredMethod(hookName, paramTypes);	// throws NoSuchMethodException
			return hook.invoke(null, paramValues);								// throws IllegalAccessException
																				// throws InvocationTargetException
		} catch (ClassNotFoundException e) {
			System.out.println("*** ApplicationService.triggerStaticHook(): className=\"" + className +
				"\" hookName=\"" + hookName + "\" --> " + e);
		} catch (NoSuchMethodException e) {
			if (throwIfNoSuchHookExists) {
				throw new DeepaMehtaException("class \"" + className + "\" has no static \"" + hookName + "\" hook");
			}
		} catch (IllegalAccessException e) {
			System.out.println("*** ApplicationService.triggerStaticHook(): className=\"" + className +
				"\" hookName=\"" + hookName + "\" --> " + e);
		} catch (InvocationTargetException e) {
			System.out.println("*** ApplicationService.triggerStaticHook(): className=\"" + className +
				"\" hookName=\"" + hookName + "\" --> " + e);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param	directives	may be <code>null</code>
	 *
	 * @see		#createCustomLiveTopic
	 */
	private void addErrorNotification(String errorText, String errorText2, CorporateDirectives directives, String className) {
		addErrorNotification(errorText, errorText2, directives);
		//
		if (className.startsWith("org/xml/")) {
			if (directives != null) {
				directives.add(DIRECTIVE_SHOW_MESSAGE, ">>> Make shure IBMs XML parser (xml4j.jar) " +
					"is installed correctly at server side", new Integer(NOTIFICATION_ERROR));
			}
			System.out.println(">>> Make shure IBMs XML parser (xml4j.jar) is installed correctly");
		} else if (className.startsWith("javax/activation/")) {
			if (directives != null) {
				directives.add(DIRECTIVE_SHOW_MESSAGE, ">>> Make shure Suns activation framework (activation.jar) " +
					"is installed correctly at server side", new Integer(NOTIFICATION_ERROR));
			}
			System.out.println(">>> Make shure Suns activation framework (activation.jar) is installed correctly");
		}
	}

	/**
	 * @param	directives	may be <code>null</code>
	 *
	 * @see		#createCustomLiveTopic
	 */
	private void addErrorNotification(String errorText, String errorText2, CorporateDirectives directives) {
		if (directives != null) {
			directives.add(DIRECTIVE_SHOW_MESSAGE, errorText2 + " (" + errorText + ")", new Integer(NOTIFICATION_ERROR));
		}
		System.out.println("*** ApplicationService.createCustomLiveTopic(): " + errorText + " -- " + errorText2);
	}

	// ---

	public String getConfigurationProperty(String property) {
		return applicationServiceInstance.getConfigurationProperty(property);
	}



	// *******************************************************
	// *** Implementation of interface java.util.TimerTask ***
	// *******************************************************



	private class StatisticsThread extends TimerTask {

		/**
		 * The body of the statistics thread.
		 */
		public void run() {
			System.out.println(DeepaMehtaUtils.getDate() + " " + DeepaMehtaUtils.getTime() + " statistics: " +
				cm.getTopicCount() + " topics, " + cm.getAssociationCount() + " associations");
		}
	}
}
