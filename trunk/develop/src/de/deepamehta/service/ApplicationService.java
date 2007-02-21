package de.deepamehta.service;

import de.deepamehta.AmbiguousSemanticException;
import de.deepamehta.Association;
import de.deepamehta.BaseAssociation;
import de.deepamehta.BaseTopic;
import de.deepamehta.BaseTopicMap;
import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.DeepaMehtaUtils;
import de.deepamehta.PresentableAssociation;
import de.deepamehta.PresentableTopic;
import de.deepamehta.PresentableTopicMap;
import de.deepamehta.PresentableType;
import de.deepamehta.PropertyDefinition;
import de.deepamehta.Topic;
import de.deepamehta.TopicInitException;
import de.deepamehta.TopicMap;
import de.deepamehta.assocs.LiveAssociation;
import de.deepamehta.topics.AssociationTypeTopic;
import de.deepamehta.topics.AuthentificationSourceTopic;
import de.deepamehta.topics.ContainerTopic;
import de.deepamehta.topics.LiveTopic;
import de.deepamehta.topics.LoginTopic;
import de.deepamehta.topics.TypeTopic;
import de.deepamehta.topics.TopicMapTopic;
import de.deepamehta.topics.TopicTypeTopic;
import de.deepamehta.topics.helper.TopicMapImporter;
import de.deepamehta.topics.helper.EmailChecker;
import de.deepamehta.topics.helper.HTMLParser;
//
import com.google.soap.search.GoogleSearch;
import com.google.soap.search.GoogleSearchResult;
import com.google.soap.search.GoogleSearchResultElement;
import com.google.soap.search.GoogleSearchFault;
//
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.io.*;	// File
import java.net.*;	// URL
import java.awt.*;
import java.util.*;



/**
 * The <CODE>ApplicationService</CODE> serves application logic that is encoded into live topics.
 * <P>
 * <IMG SRC="../../../../../images/3-tier-lcm.gif">
 * <P>
 * <HR>
 * Last functional change: 24.8.2006 (2.0b8)<BR>
 * Last documentation update: 30.12.2001 (2.0a14-pre5)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public final class ApplicationService extends BaseTopicMap implements Runnable, LoginCheck, DeepaMehtaConstants {



	// **************
	// *** Fields ***
	// **************



	private ApplicationServiceHost host;

	/**
	 * The IP address of the server machine.
	 * <P>
	 * Initialized by {@link #ApplicationService constructor}.
	 * Accessed by {@link #runsAtServerHost}.<BR>
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
	 * The active installation (type <CODE>tt-installation</CODE>)
	 * <P>
	 * Initialized by by {@link #ApplicationService constructor}.<BR>
	 * The installation name is accessed by {@link #getInstallationName}.
	 */
	private BaseTopic installation;

	/**
	 * The properties of the active installation.
	 * <P>
	 * Initialized by {@link #ApplicationService constructor}.<BR>
	 * Accessed by {@link #getInstallationProps}.<BR>
	 * Written to stream by {@link #writeInstallationProps}.
	 */
	private Hashtable installationProps;

	// ---

	/**
	 * The datasource used for user authentification.
	 * <P>
	 * Initialized by {@link #setAuthentificationSourceTopic}.<BR>
	 * Accessed by {@link #getAuthentificationSourceTopic}.
	 */
	private AuthentificationSourceTopic authSourceTopic;

	/**
	 * An array holding the logged in clients. The index is the session ID.
	 */
	private Session[] clientSessions = new Session[MAX_CLIENTS + 1];

	private ServerConsole serverConsole;	// only initialized if running as server

	private Thread statisticsThread;



	// *****************************
	// *** Constructor (private) ***
	// *****************************



	/**
	 * References checked: 13.1.2002 (2.0a14-pre6)
	 *
	 * @see		#create
	 */
	private ApplicationService(ApplicationServiceHost host, CorporateMemory cm) {
		// >>> compare to PresentationService.initialize()
		try {
			this.hostAddress = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			System.out.println("*** ApplicationService(): " + e);
		}
		//
		this.host = host;
		this.cm = cm;
		this.installation = getActiveInstallation();
		this.installationProps = getTopicProperties(installation);
		installationProps.put(PROPERTY_CW_BASE_URL, getCorporateWebBaseURL());	// ### not really an installation property
		//
		System.out.println(">    active installation: \"" + installation.getName() + "\"");
	}



	// ******************************************************
	// *** Implementation of interface java.lang.Runnable ***
	// ******************************************************



	/**
	 * The body of the statistics thread.
	 */
	public void run() {
		while (true) {
			try {
				Thread.sleep(5 * 60 * 1000);	// interval is 5 min.
				System.out.println(DeepaMehtaUtils.getDate() + " " + DeepaMehtaUtils.getTime() + " statistics: " +
					cm.getTopicCount() + " topics, " + cm.getAssociationCount() + " associations");
			} catch (InterruptedException e) {
				System.out.println("*** ApplicationService.run(): " + e);
			}
		}
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
		// ### compare to client.DeepaMehta.createApplicationService()
		// ### compare to service.DeepaMehtaServer.main()
		System.out.println("> DeepaMehta Application Service");
		System.out.println(">    version: " + SERVER_VERSION);
		System.out.println(">    standard topics version: " + LiveTopic.kernelTopicsVersion);
		System.out.println(">    communication: " + host.getCommInfo());
		System.out.println(">    service name: \"" + instance.name + "\"");
		System.out.println("> Corporate Memory");
		System.out.println(">    implementation: \"" + instance.cmClass + "\"");
		System.out.println(">    URL: \"" + instance.cmURL + "\"");
		System.out.println(">    driver: \"" + instance.cmDriverClass + "\"");
		// establish access to corporate memory
		CorporateMemory cm = instance.createCorporateMemory();	// throws DME
		// create application service
		ApplicationService as = new ApplicationService(host, cm);
		// set authorisation source
		as.setAuthentificationSourceTopic();
		// start statictics thread, basically to keep the CM connection alive,
		// compare to DataSourceTopic.startIdleThread()
		as.statisticsThread = new Thread(as);
		as.statisticsThread.start();
		//
		return as;
	}

	/**
	 * @see		DeepaMehtaServer#main
	 * @see		DeepaMehta#createApplicationService
	 */
	public void setAuthentificationSourceTopic() throws TopicInitException {
		System.out.print("> Set authentification source ... ");
		BaseTopic auth = cm.getTopic("t-useraccounts", 1);	// ### hardcoded
		// ### createLiveTopic(auth, false, null);	// ### consider checkLiveTopic() ### throws TopicInitException
		// ### process returned directives
		this.authSourceTopic = (AuthentificationSourceTopic) getLiveTopic(auth);
		// Note: just called to report the current authentification method
		// ### do non-CM authentification methods proper reporting?
		getLoginCheck();
	}

	public ApplicationServiceHost getHostObject() {
		return host;
	}

	public void shutdown() {
		statisticsThread.stop();
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



	// ---------------------------------------------
	// --- Accessing Proxy Topics / Associations ---
	// ---------------------------------------------



	// --- getLiveTopic (4 forms) ---

	public LiveTopic getLiveTopic(String id, int version) throws DeepaMehtaException {
		return getLiveTopic(id, version, null, null);
	}

	public LiveTopic getLiveTopic(String id, int version, Session session, CorporateDirectives directives) throws DeepaMehtaException {
		return checkLiveTopic(id, version, session, directives);
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
			throw new DeepaMehtaException("null passed to getLiveAssociation()");
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
	 * ### should have parameters "topicID" and "version" instead of "topic"
	 * <P>
	 * References checked: 14.12.2001 (2.0a14-pre4)
	 *
	 * @param	session		passed to init() hook (init levels 1-3)
	 *
	 * @see		#getLoginCheck
	 * @see		#type
	 * @see		CorporateCommands#addTopicCommands
	 * @see		#startSession
	 * @see		#addGroupWorkspaces
	 * @see		#addCorporateSpace
	 * @see		#addViewsInUse
	 * @see		de.deepamehta.topics.LiveTopic#setDataSource
	 * @see		de.deepamehta.topics.PropertyTopic#setPropertyDefinition
	 * @see		de.deepamehta.topics.TopicContainerTopic#getAppearance
	 * @see		de.deepamehta.topics.TypeTopic#getSupertype
	 * @see		de.deepamehta.topics.TypeTopic#makeTypeDefinition
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
				System.out.println("*** ApplicationService.checkLiveTopic(): " + e.getMessage());
				e.printStackTrace();
				/* ### if (directives != null) {
					directives.add(DIRECTIVE_SHOW_MESSAGE, e.getMessage(),
						new Integer(NOTIFICATION_WARNING));
				} */
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

	// --- checkLiveAssociation (2 forms) ---

	/**
	 * ### should be private
	 *
	 * @see		#deleteAssociation(BaseAssociation assoc)
	 */
	public LiveAssociation checkLiveAssociation(BaseAssociation assoc, Session session, CorporateDirectives directives) {
		if (!liveAssociationExists(assoc)) {
			createLiveAssociation(assoc, false, false, directives);
		}
		return getLiveAssociation(assoc);
	}

	/**
	 * ### should be private
	 *
	 * @see		#deleteAssociation(String assocID, int version)
	 */
	public LiveAssociation checkLiveAssociation(String assocID, int version, Session session, CorporateDirectives directives) {
		if (!liveAssociationExists(assocID, version)) {
			BaseAssociation assoc = cm.getAssociation(assocID, version);
			// error check
			if (assoc == null) {
				throw new DeepaMehtaException("association \"" + assocID + ":" +
					version + "\" not in corporate memory");
			}
			//
			createLiveAssociation(assoc, false, false, directives);
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
	 * Creates a {@link de.deepamehta.topics.LiveTopic} based on the specified
	 * {@link de.deepamehta.BaseTopic} and adds it to this live corporate memory.
	 * <P>
	 * If a topic with same ID and version already exists in live corporate memory and the
	 * <CODE>override</CODE> parameter is set to <CODE>false</CODE> nothing is performed.
	 * <P>
	 * If the topic has a custom implementation the corresponmding
	 * class is instantiated (a direct or indirect subclass of
	 * {@link de.deepamehta.topics.LiveTopic}), the actual classname
	 * is determined by the "Implementing Class" property of the corresponding type
	 * topic. If the "Implementing Class" property is empty, there is no custom
	 * implementation and a generic {@link de.deepamehta.topics.LiveTopic} is
	 * instantiated.
	 * <P>
	 * After creating an active topic, its life-cycle method <CODE>init()</CODE>
	 * is triggered at INITLEVEL_1.
	 * <P>
	 * ### should return a LiveTopic and have "directives" as a parameter<BR>
	 * ### should be package private
	 *
	 * <TABLE>
	 * <TR><TD><B>Called by</B>																											<TD><CODE>override</CODE>
	 * <TR><TD>{@link #loadDemoTopicmaps()}																								<TD><CODE>false</CODE>
	 * <TR><TD>{@link #setAuthentificationSourceTopic()}																				<TD><CODE>false</CODE>
	 * <TR><TD>{@link #createLiveTopics(Enumeration, CorporateDirectives, Session)}												<TD><CODE>false</CODE>
	 * </TABLE>
	 *
	 * @param	session		passed to init() hook (init level 1)
	 *
	 * @return	If the topic has not been created because it exists already in live
	 *			corporate memory <CODE>null</CODE> is returned. Otherwise possible error
	 *			directives resulted from creation of an active topic are returned (may
	 *			be emtpy). Note: if a live topic without custom implementation was
	 *			created always empty CorporateDirectives are returned.
	 */
	private CorporateDirectives createLiveTopic(BaseTopic topic, boolean override, Session session)
																		throws TopicInitException {
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
			if (LOG_LCM) {System.out.println("> (.) " + topic);}
			return null;
		}
		//
		CorporateDirectives directives = new CorporateDirectives();
		// --- create live topic ---
		String implementingClass = getImplementingClass(topic);
		if (LOG_LCM) {System.out.println("> (*) " + topic);}
		LiveTopic newTopic = createCustomLiveTopic(topic, implementingClass, directives);
		// --- add to live corporate memory ---
		addTopic(newTopic);
		// --- trigger init() hook ---
		newTopic.init(INITLEVEL_1, session);	// throws TopicInitException
		//
		return directives;
	}

	// 4 Utility wrappers for createLiveTopic() above

	public LiveTopic createLiveTopic(String topicID, String typeID, String name, Session session) {
		// ### directives are ignored ### must not null
		return createLiveTopic(topicID, typeID, name, null, null, session, new CorporateDirectives());
	}

	/**
	 * @see		de.deepamehta.topics.TopicMapTopic#searchByTopicType
	 */
	public LiveTopic createLiveTopic(BaseTopic topic, String topicmapID, String viewmode,
											Session session, CorporateDirectives directives) {
		return createLiveTopic(topic.getID(), topic.getType(), topic.getName(),
			topicmapID, viewmode, session, directives);
	}

	/**
	 * @see		#changeTopicType
	 * @see		de.deepamehta.topics.TopicTypeTopic#createContainerType
	 */
	public LiveTopic createLiveTopic(String topicID, String typeID, String name, String topicmapID,
											String viewmode, Session session, CorporateDirectives directives) {
		return createLiveTopic(topicID, typeID, name, true, true, topicmapID, viewmode, session, directives);
		// override=true, evoke=true
	}

	/**
	 * The topic is created, loaded, evoked and fully inited.
	 * No existence check is performed. All Exceptions are catched.
	 * <P>
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
			// --- evoke ---
			// ### Note: a topic is evoked also if init(1) fails. Consider this case:
			// interactively created datasource topics have no URL and driver set, thus the
			// datasource can't be opened and init(1) will fail. However he datasource must
			// be evoked() to get stored in corporate memory.
			// ### Note 2: consider this case: reveal a topic from an ElementContainer
			// will trigger evoke, but the topic could already be in CM if revealed before
			if (evoke) {
				try {
					directives.add(evokeLiveTopic(topic, session, topicmapID, viewmode));	// DME, ASE
				} catch (DeepaMehtaException e) {
					System.out.println("*** ApplicationService.createLiveTopic(): " + e);
					directives.add(DIRECTIVE_SHOW_MESSAGE, e.getMessage(), new Integer(NOTIFICATION_ERROR));
				} catch (AmbiguousSemanticException e) {
					System.out.println("*** ApplicationService.createLiveTopic(): " + e);
					directives.add(DIRECTIVE_SHOW_MESSAGE, e.getMessage(), new Integer(NOTIFICATION_ERROR));
				}
			}
			// --- init(2) and init(3) ---
			newTopic = getLiveTopic(topic);
			newTopic.init(INITLEVEL_2, session);	// throws TopicInitException ### adding init directives
			newTopic.init(INITLEVEL_3, session);	// throws TopicInitException ### adding init directives
		} catch (TopicInitException e) {
			System.out.println("*** ApplicationService.createLiveTopic(): " + e);
			directives.add(DIRECTIVE_SHOW_MESSAGE, e.getMessage(), new Integer(NOTIFICATION_ERROR));
		}
		return newTopic;
	}

	// ---

	/**
	 * Returns the directives to create and show a new topic.
	 *
	 * References checked: 23.4.2004 (2.0b3-pre2)
	 *
	 * @see		de.deepamehta.topics.TopicMapTopic#createTopic
	 */
	public CorporateDirectives createTopic(String topicID, String typeID, int x, int y, String topicmapID,
																		Session session) throws TopicInitException {
		CorporateDirectives directives = new CorporateDirectives();
		PresentableTopic topic = new PresentableTopic(topicID, 1, typeID, 1, "", new Point(x, y));	// name=""
		directives.add(DIRECTIVE_SHOW_TOPIC, topic, Boolean.TRUE);									// evoke=TRUE
		// ### tiggerAddedToTopicmap(topicmapID, topic, directives);	// ### not used
		//
		return directives;
	}

	/**
	 * Handles the command <code>CMD_CREATE_ASSOC</code>.
	 * <p>
	 * Returns the directives to create and show a new association.
	 *
	 * References checked: 28.5.2006 (2.0b6-post3)
	 *
	 * @see		de.deepamehta.topics.TopicMapTopic#executeCommand
	 */
	public CorporateDirectives createAssociation(String typeID, String topicID1, String topicID2, Session session) {
		CorporateDirectives directives = new CorporateDirectives();
		//
		// check weather retyping is allowed and which type is finally to be used
		typeID = associationAllowed(typeID, null, topicID1, topicID2, session, directives);
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
	 * <P>
	 * ### Note: the topic is NOT evoked here -> can be only called for types which have no evoke() implementation
	 * ### should create a live topic instead
	 * ### This method is to be dropped
	 * <P>
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
	 * <P>
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
			createLiveAssociation(assoc, false, evoke, directives);
		}
	}

	// --- createLiveAssociation (3 forms) ---

	/**
	 * References checked: 30.3.2003 (2.0a18-pre8)
	 *
	 * @param	directives	may be <CODE>null</CODE>
	 *
	 * @see 	#checkLiveAssociation(BaseAssociation)														false
	 * @see 	#checkLiveAssociation(String assocID, int version)											false
	 * @see 	#createLiveAssociations(Enumeration, boolean)												variable
	 * @see 	#createLiveAssociation(String assocID, String typeID, String topicID1, String topicID2)		true
	 * @see 	CorporateDirectives#showAssociation()														variable
	 */
	public void createLiveAssociation(BaseAssociation assoc, boolean override, boolean evoke,
																				CorporateDirectives directives) {
		// ### compare to createLiveTopic(), topicmapID, viewmode, session, directives parameters?
		// Note: associationExists() is from BaseTopicMap
		if (!override && liveAssociationExists(assoc.getID(), assoc.getVersion())) {
			// association exists and is not supposed to be overridden
			if (LOG_LCM) {System.out.println("> (.) " + assoc);}
			return;
		}
		// --- create live association ---
		String implementingClass = type(assoc).getImplementingClass();
		LiveAssociation newAssoc = createCustomLiveAssociation(assoc, implementingClass, directives);
		// ### if (LOG_LCM) {System.out.println("> (*) " + assoc + " (" + newAssoc.getClass()  + ")");}
		// --- add to live corporate memory ---
		addAssociation(newAssoc);
		// --- trigger evoke() hook ---
		if (evoke) {
			newAssoc.evoke();
		}
	}

	// 2 Utility wrapper for createLiveAssociation() above.

	public LiveAssociation createLiveAssociation(String assocID, String typeID, String topicID1, String topicID2) {
		return createLiveAssociation(assocID, typeID, "", topicID1, topicID2);
	}

	public LiveAssociation createLiveAssociation(String assocID, String typeID, String name,
																			String topicID1, String topicID2) {
		// ### compare to createLiveTopic(), topicmapID, viewmode, session, directives parameters?
		BaseAssociation assoc = new BaseAssociation(assocID, 1, typeID, 1, name, topicID1, 1, topicID2, 1);
		createLiveAssociation(assoc, false, true, null);		// override=false, evoke=true, directives=null
		LiveAssociation newAssoc = getLiveAssociation(assoc);
		return newAssoc;
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
	 * <P>
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
	 *							if <CODE>null</CODE> is passed all topic types are matching
	 *
	 * @return	the topics as vector of {@see de.deepamehta.BaseTopic}s
	 *
	 * @throws	DeepaMehtaException	if no matching topics were found in corporate
	 *							memory while <CODE>emptyAllowed</CODE> is not set.
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
	 * ### Creates a container with standard name.
	 * <P>
	 * <TABLE>
	 * <TR><TD><B>Called by</B></TD>												<TD><CODE>evokeContent</CODE></TD></TR>
	 * <TR><TD>{@link #performGoogleSearch}</TD>									<TD><CODE>false</CODE></TD></TR>
	 * <TR><TD>{@link de.deepamehta.topics.TopicContainerTopic#processQuery}</TD>	<TD><CODE>false</CODE></TD></TR>
	 * <TR><TD>{@link de.deepamehta.topics.ElementContainerTopic#processQuery}</TD>	<TD><CODE>true</CODE></TD></TR>
	 * </TABLE>
	 */
	public CorporateDirectives createNewContainer(LiveTopic relatedTopic, String containerType, String nameFilter,
											Hashtable propertyFilter, String relatedTopicID, String relatedTopicSemantic,
											int topicCount, Vector topics, boolean evokeContent) {
		return createNewContainer(relatedTopic, containerType, nameFilter, propertyFilter, relatedTopicID,
			relatedTopicSemantic, topicCount, topics, evokeContent, null, true);	// name=null, revealContent=true
	}

	/**
	 * ### Creates a container with a custom name.
	 * <P>
	 * Creates and returns the client directives to perform the following task: create a new container, associate
	 * the container with the specified <CODE>relatedTopic</CODE> by means of an association of type
	 * {@link #SEMANTIC_CONTAINER_HIERARCHY} and reveal the contents of the container if they are sufficiently small.
	 * "Revealing the contents" means showing the contained topics and associating them with the container by means
	 * of associations of type {@link #SEMANTIC_CONTAINER_HIERARCHY}.
	 * <P>
	 * If the <CODE>relatedTopic</CODE> is itself a container and it has the same content no new container is created,
	 * but the content is revealed as described.
	 * <P>
	 * <TABLE>
	 * <TR><TD><B>Called by</B></TD>													<TD><CODE>evokeContent</CODE></TD></TR>
	 * <TR><TD>{@link de.deepamehta.topics.LiveTopic#navigateByTopictype}</TD>			<TD><CODE>false</CODE></TD></TR>
	 * <TR><TD>{@link de.deepamehta.topics.LiveTopic#navigateByAssoctype}</TD>			<TD><CODE>false</CODE></TD></TR>
	 * </TABLE>
	 *
	 * @param	relatedTopic			the topic the container is visually associated.
	 *									Note: don't confuse this paramter with the relation filter
	 * @param	containerTypeID			type ID of the container to be created
	 * @param	nameFilter				name filter, if <CODE>null</CODE> no name filter is set
	 * @param	propertyFilter			property filter, if empty no property filter is set ### must not be <CODE>null</CODE>
	 * @param	relatedTopicID			relation filter, if <CODE>null</CODE> no relation filter is set
	 * @param	relatedTopicSemantic	part of relation filter, if <CODE>null</CODE> the association type doesn't matter
	 * @param	topicCount				number of topics contained in the container
	 * @param	topics					topics contained in the container (vector of {@link de.deepamehta.PresentableTopic}s),
	 *									Note: only used if topicCount <= MAX_REVEALING
	 *
	 */
	public CorporateDirectives createNewContainer(LiveTopic relatedTopic, String containerTypeID, String nameFilter,
							Hashtable propertyFilter, String relatedTopicID, String relatedTopicSemantic,
							int topicCount, Vector topics, boolean evokeContent, String name, boolean revealContent) {
		CorporateDirectives directives = new CorporateDirectives();
		//
		String containerLabel = Integer.toString(topicCount);
		// --- create container properties ---
		Hashtable containerProps;
		containerProps = (Hashtable) propertyFilter.clone();
		// determine query elements
		String queryElements = queryElements(propertyFilter);
		if (nameFilter != null && nameFilter.length() > 0) {
			if (queryElements.length() > 0) {
				queryElements += ",";
			}
			queryElements += PROPERTY_SEARCH;
			containerProps.put(PROPERTY_SEARCH, nameFilter);
		}
		containerProps.put("QueryElements", queryElements);
		containerProps.put("ElementCount", containerLabel);
		if (relatedTopicID != null) {
			containerProps.put(PROPERTY_RELATED_TOPIC_ID, relatedTopicID);
			if (relatedTopicSemantic != null) {
				containerProps.put(PROPERTY_RELATED_TOPIC_SEMANTIC, relatedTopicSemantic);
			}
		}
		// only create container if the query has been narrowed against the query of this container
		String containerID;
		if (relatedTopic instanceof ContainerTopic &&
				((ContainerTopic) relatedTopic).equalsQuery(nameFilter, propertyFilter, relatedTopicID, relatedTopicSemantic)) {
			// nothing added to the query -- don't create new container
			System.out.println("> ContainerTopic.createNewContainer(): query not narrowed -- don't create new container");
			containerID = relatedTopic.getID();
			directives.add(DIRECTIVE_SET_TOPIC_LABEL, containerID, containerLabel, new Integer(1), containerProps);	// ### version=1
		} else {
			System.out.println("> ContainerTopic.createNewContainer(): query narrowed (now " + propertyFilter + ") -- create new container");
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

	// --- createPresentableAssociation (2 forms) ---

	public PresentableAssociation createPresentableAssociation(String assocTypeID, String topicID1, int topicVersion1,
													String topicID2, int topicVersion2, boolean performExistenceCheck) {
		return createPresentableAssociation(assocTypeID, "",
			topicID1, topicVersion1, topicID2, topicVersion2, performExistenceCheck);
	}

	/**
	 * <TABLE>
	 * <TR><TD><B>Called by</B><TD><CODE>performExistenceCheck</CODE>
	 * <TR><TD>{@link #createNewContainer}<TD><CODE>false</CODE>
	 * <TR><TD>{@link #createPresentableAssociations}<TD><CODE>true</CODE>
	 * <TR><TD>{@link de.deepamehta.topics.ElementContainerTopic#autoSearch}<TD><CODE>false</CODE>
	 * <TR><TD>{@link de.deepamehta.topics.ElementContainerTopic#revealTopic}<TD><CODE>false</CODE>
	 * </TABLE>
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
	 * <P>
	 * Triggers the {@link de.deepamehta.topics.LiveTopic#disabledProperties disabledProperties() hook}
	 * and returns the resulting property names.
	 * <P>
	 * Called for <CODE>DIRECTIVE_SELECT_TOPIC</CODE> and <CODE>DIRECTIVE_SELECT_TOPICMAP</CODE>.
	 *
	 * @return	A vector of property names (<CODE>String</CODE>s)
	 *
	 * @see		CorporateDirectives#updateCorporateMemory
	 * @see		CorporateCommands#addTopicPropertyCommands
	 */
	Vector disabledProperties(String topicID, int version, Session session) {
		// --- trigger disabledProperties() hook ---
		return getLiveTopic(topicID, version).disabledProperties(session);
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

	public boolean isTopicOwner(String topicID, Session session) {
		String userID = session.getUserID();
		return getTopicProperty(topicID, 1, PROPERTY_OWNER_ID).equals(userID) || isAdministrator(userID);
	}

	public boolean isAssocOwner(String assocID, Session session) {
		String userID = session.getUserID();
		return getAssocProperty(assocID, 1, PROPERTY_OWNER_ID).equals(userID) || isAdministrator(userID);
	}

	// ---

	/**
	 * Consulted while server side processing of <CODE>DIRECTIVE_SELECT_TOPIC</CODE>.
	 *
	 * @see		CorporateDirectives#updateCorporateMemory
	 * @see		CorporateCommands#addRetypeTopicCommand
	 */
	boolean retypeTopicIsAllowed(String topicID, int version, Session session) {
		TypeTopic type = type(getLiveTopic(topicID, version));
		boolean isSearch = type.isSearchType();
		boolean isTopicmap = type.hasSupertype(TOPICTYPE_TOPICMAP);
		return !isSearch && !isTopicmap && isTopicOwner(topicID, session);
	}

	boolean retypeAssociationIsAllowed(String assocID, int version, Session session) {
		boolean isSearch = type(getLiveAssociation(assocID, version)).isSearchType();
		return !isSearch && isAssocOwner(assocID, session);
	}

	// ---

	boolean deleteTopicIsAllowed(BaseTopic topic, Session session) {
		// --- trigger deleteAllowed() hook ---
		boolean allowed = getLiveTopic(topic).deleteAllowed(session);
		//
		return allowed && isTopicOwner(topic.getID(), session);
	}

	boolean deleteAssociationIsAllowed(BaseAssociation assoc, Session session) {
		// ### --- trigger deleteAllowed() hook ---
		// ### boolean allowed = getLiveTopic(topic).deleteAllowed(session);
		//
		return isAssocOwner(assoc.getID(), session);
	}

	// --- setTopicProperty (2 forms) ---

	// Note: there 2 other methods who are changing the properties NOT "blindly"
	// - setTopicProperty()
	// - setTopicProperties()

	/**
	 * Changes a topic property "blindly".
	 */
	public void setTopicProperty(BaseTopic topic, String propName, String propValue) {
		setTopicProperty(topic.getID(), topic.getVersion(), propName, propValue);
	}

	/**
	 * Changes a topic property "blindly".
	 *
	 * @see		LiveTopic#setTopicData(String propName, String propValue)
	 */
	public void setTopicProperty(String topicID, int version, String propName, String propValue) {
		cm.setTopicData(topicID, version, propName, propValue);
	}

	// ---

	/**
	 * Changes topic properties "blindly".
	 *
	 * @see		CorporateDirectives#createLiveTopic
	 */
	public void setTopicProperties(String topicID, int version, Hashtable props) {
		cm.setTopicData(topicID, version, props);
	}

	// ---

	/**
	 * Changes association properties "blindly".
	 *
	 * @see		InteractionConnection#performChangeAssociationData
	 */
	void setAssocProperties(String assocID, int version, Hashtable props) {
		cm.setAssociationData(assocID, version, props);
	}

	// Note: there is 1 other method who changes the properties NOT "blindly"
	// - setAssocProperties()

	// --- getAllTopics (2 forms) ---

	/**
	 * @see		InteractionConnection#performHideTopicsByType
	 */
	/* ### public CorporateDirectives getAllTopics(String typeID, String topicmapID,
																	String viewmode) {
		// vector of String
		Vector topicIDs = cm.getTopicIDs(typeID, topicmapID, viewmode);
		System.out.println("> ApplicationService.getAllTopics(): " + topicIDs.size() +
			" topics of type \"" + typeID + "\" found in view \"" + topicmapID + "\"");
		// create directives
		CorporateDirectives directives = new CorporateDirectives();
		directives.add(DIRECTIVE_HIDE_TOPICS, topicIDs, Boolean.FALSE, topicmapID,
			viewmode);
		return directives;
	} */



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
			getLiveTopic(topicID, version).init(initLevel, session);	// throws TopicInitException
		} catch (DeepaMehtaException e) {
			throw new TopicInitException(e.getMessage());
		} catch (AmbiguousSemanticException e) {
			throw new TopicInitException(e.getMessage());
		}
	}

	// ---

	/**
	 * Evokes the specified live topic.
	 * <P>
	 * Accesses the specified topic in live corporate memory and triggers its
	 * {@link de.deepamehta.topics.LiveTopic#evoke evoke()} hook with the
	 * specified parameters.
	 *
	 * @throws	DeepaMehtaException, AmbiguousSemanticException. Exceptions occurring
	 *			while topic evocation are propagated.
	 *
	 * @see		CorporateDirectives#evokeLiveTopic
	 * @see		CorporateDirectives#createProxyTypeTopic
	 * @see		CorporateDirectives#changeTopicType
	 * @see		### de.deepamehta.topics.AuthentificationSourceTopic#loginCheck
	 * @see		### de.deepamehta.topics.CorporateSearchTopic#addContainerByType
	 * @see		### de.deepamehta.topics.CorporateSearchTopic#createCopyOfSearchTopic
	 */
	public CorporateDirectives evokeLiveTopic(BaseTopic topic, Session session,
									String topicmapID, String viewmode) throws
									DeepaMehtaException, AmbiguousSemanticException {
		// --- trigger evoke() hook ---
		return getLiveTopic(topic).evoke(session, topicmapID, viewmode);
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
	 * <P>
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
	 * ### Should not be called directly by the application developer. Should be packacke private.
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
			// --- trigger associationRemoved() hook ---
			topic1.associationRemoved(assoc.getType(), topic2.getID(), session, directives);
			topic2.associationRemoved(assoc.getType(), topic1.getID(), session, directives);
			// --- trigger die() hook ---
			directives.add(assoc.die());
		} catch (DeepaMehtaException e) {
			// ### add to directives
			System.out.println("*** ApplicationService.deleteAssociation(): " +
				e.getMessage() + " -- die() hook not triggered");
		}
		return directives;
	}

	// ---

	/**
	 * Triggers the nameChanged() hook of the specified topic.
	 * <P>
	 * References checked: 4.3.2003 (2.0a18-pre4)
	 *
	 * @see		#setTopicProperties
	 * @see		CorporateDetail#process
	 */
	public CorporateDirectives changeTopicName(String topicID, int version, String name, String topicmapID, String viewmode) {
		// --- trigger nameChanged() hook ---
		return getLiveTopic(topicID, version).nameChanged(name, topicmapID, viewmode);
	}

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
	 * <P>
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
		typeID = associationAllowed(typeID, assoc.getType(), topicID1, topicID2, session, directives);
		if (typeID != null) {
			LiveTopic topic1 = getLiveTopic(topicID1, 1);
			LiveTopic topic2 = getLiveTopic(topicID2, 1);
			// --- trigger associationRemoved() hook ---
			topic1.associationRemoved(assoc.getType(), topicID2, session, directives);
			topic2.associationRemoved(assoc.getType(), topicID1, session, directives);
			//
			// perform the retyping
			//
			// --- trigger typeChanged() hook ---
			directives.add(assoc.typeChanged(typeID));		// ### typeVersion not used
		}
		//
		return directives;
	}

	// ---

	// Note: there are also 3 methods who are changing the properties "blindly"
	// - setTopicProperty()
	// - setTopicProperty()
	// - setTopicProperties()

	/**
	 * Sets a topic property.
	 * <P>
	 * References checked: 6.9.2002 (2.0a16-pre2)
	 *
	 * @see		KompetenzsternTopic#propertiesChanged
	 */
	public CorporateDirectives setTopicProperty(String topicID, int version, String propName, String propValue,
												String topicmapID, String viewmode, Session session) {
		Hashtable props = new Hashtable();
		props.put(propName, propValue);
		return setTopicProperties(topicID, version, props, topicmapID, true, session);	// triggerPropertiesChangedHook=true
	}

	/**
	 * Sets topic properties.
	 * <P>
	 * References checked: 13.2.2005 (2.0b5)
	 *
	 * @param	topicmapID						### may be null
	 * @param	triggerPropertiesChangedHook	### always true, not required anymore -- the <code>propertiesChanged()</code> hook is only triggered if set to <code>true</code>
	 * @param	session							### may be null
	 *
	 * @see		#setTopicProperty							true
	 * @see		#performGoogleSearch						true
	 * @see		CorporateDirectives#setTopicProperties		true
	 * @see		InteractionConnection#setTopicData			true
	 * @see		EmbeddedService#setTopicProperties			true
	 * @see		ChatTopic#evoke								true
	 * @see		DeepaMehtaServlet#processForm				true
	 */
	public CorporateDirectives setTopicProperties(String topicID, int version, Hashtable props, String topicmapID,
																	boolean triggerPropertiesChangedHook, Session session) {
		CorporateDirectives directives = new CorporateDirectives();
		//
		try {
			Hashtable oldProps = getTopicProperties(topicID, version);
			props = removeUnchangedProperties(props, oldProps);
			LiveTopic topic = getLiveTopic(topicID, version);	// may throw DME
			// trigger propertiesChangeAllowed() hook
			if (topic.propertiesChangeAllowed(oldProps, props, directives)) {
				// update cm
				setTopicProperties(topicID, version, props);
				// --- trigger propertiesChanged() hook ---
				if (triggerPropertiesChangedHook) {
					directives.add(topic.propertiesChanged(props, oldProps, topicmapID, VIEWMODE_USE, session));
				}
				// --- topic name behavoir ---
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
					directives.add(changeTopicName(topicID, version, name, topicmapID, VIEWMODE_USE));
				}
			}
		} catch (DeepaMehtaException e) {
			directives.add(DIRECTIVE_SHOW_MESSAGE, e.getMessage(), new Integer(NOTIFICATION_WARNING));
			System.out.println("*** ApplicationService.setTopicProperties(): " + e);
		}
		//
		return directives;
	}

	// ---

	/**
	 * Sets association properties.
	 * <P>
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
	 * @return	may return <CODE>null</CODE>
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
	 * @return	may return <CODE>null</CODE>
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
	 * @return	may return <CODE>null</CODE>
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
			// for every topic its type can be explained
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
	public CorporateDirectives showViewMenu(String topicmapID, String viewmode, int x, int y,
																						Session session) {
		CorporateDirectives directives = new CorporateDirectives();
		LiveTopic topicmap = getLiveTopic(topicmapID, 1);
		try {
			// --- trigger viewCommands() hook ---
			CorporateCommands commands = topicmap.viewCommands(topicmapID, viewmode, session, directives);
			directives.add(DIRECTIVE_SHOW_MENU, MENU_VIEW, commands, new Point(x, y));
			//
		} catch (DeepaMehtaException e) {
			System.out.println("*** ApplicationService.showViewMenu(): " + e +
				" -- view commands not available");
			directives.add(DIRECTIVE_SHOW_MESSAGE, "Menu  for view \"" +
				topicmap.getName() + "\" (" + topicmapID + ") not available (" +
				e.getMessage() + ")", new Integer(NOTIFICATION_WARNING));
		}
		return directives;
	}

	// ---

	/**
	 * @see		#createTopic
	 */
	/* ### void tiggerAddedToTopicmap(String topicmapID, BaseTopic topic, CorporateDirectives directives) {
		try {
			// --- trigger addedToTopicmap() hook ---
			LiveTopic topicmap = getLiveTopic(topicmapID, 1);	// ### version 1
			directives.add(topicmap.addedToTopicmap(topic));
		} catch (DeepaMehtaException e) {
			System.out.println("*** ApplicationService.tiggerAddedToTopicmap(): " +
				"the target topicmap isn't in LCM (" + e.getMessage() + ") -- " +
				"addedToTopicmap() hook not triggered");
		}
	} */

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
	 *
	 * @param	topicmapID	the view
	 * @param	version		the version of the view
	 * @param	viewMode	the view mode ({@link #VIEWMODE_USE}, {@link #VIEWMODE_BUILD})
	 *
	 * @see		CorporateTopicMap#CorporateTopicMap		2x
	 */
	PresentableTopicMap createUserView(String topicmapID, int version, String viewMode,
								String bgImage, String bgColor, String translation) {
		// vector of PresentableTopic
		// vector of PresentableAssociation
		Vector topics = cm.getViewTopics(topicmapID, version);
		Vector associations = cm.getViewAssociations(topicmapID, version);
		return new PresentableTopicMap(topics, associations, bgImage, bgColor, translation);
	}

	/**
	 * Duplicates the specified viewmode of the specified view in corporate memory.
	 * <P>
	 * References checked: 17.2.2003 (2.0a18-pre2)
	 *
	 * @param	topicmap		view to duplicate
	 * @param	topicmapID		original view ID
	 * @param	viewMode		view mode of <CODE>topicmap</CODE> to duplicate
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
	 * @see		CorporateTopicMap#addPublishDirectives
	 */
	void addPublishDirectives(BaseTopicMap topicmap, CorporateDirectives directives) {
		addPublishDirectives(topicmap.getTopics().elements(), directives);
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
	 * @param	triggerInit		if <CODE>true</CODE> the <CODE>init(2)</CODE> and
	 *							<CODE>init(3)</CODE> hooks of the corresponding proxy
	 *							type topics are triggered ###
	 *
	 * @see		#loadKernelTopics							2x	true
	 * @see		CorporateTopicMap#initLiveTopics			4x	false
	 * @see		CorporateDirectives#updateCorporateMemory		false
	 */
	void initTypeTopics(Enumeration typeTopics, boolean triggerInit,
								Session session, CorporateDirectives directives) {
		// >>> compare to initTopics()
		PresentableType typeTopic;
		while (typeTopics.hasMoreElements()) {
			typeTopic = (PresentableType) typeTopics.nextElement();
			try {
				initTypeTopic(typeTopic, triggerInit, session);
			} catch (TopicInitException e) {
				System.out.println("*** ApplicationService.initTypeTopics(): " +
					e.getMessage());
				if (directives != null) {
					directives.add(DIRECTIVE_SHOW_MESSAGE, e.getMessage(),
						new Integer(NOTIFICATION_WARNING));
				}
			} catch (Exception e) {
				System.out.println("*** ApplicationService.initTypeTopics(): " +
					e + " -- " + typeTopic + " not properly inited");
				e.printStackTrace();
			}
		}
	}

	// ---

	/**
	 * Initializes the type definition, the type appearance and the 2
	 * <CODE>disabled</CODE>, <CODE>hiddenTopicNames</CODE> flags of the
	 * specified presentable type topic.
	 * <P>
	 * References checked: 8.4.2003 (2.0a18-pre9)
	 *
	 * @param	session		the client session (passed to <CODE>init()</CODE> hook).
	 *						Note: can be <CODE>null</CODE> if <CODE>triggerInit</CODE>
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
				type.init(INITLEVEL_1, session); // may throw TopicInitException
				type.init(INITLEVEL_2, session); // may throw TopicInitException
				type.init(INITLEVEL_3, session); // may throw TopicInitException
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
	 * <P>
	 * References checked: 15.2.2005 (2.0b5)
	 *
	 * @return	The workspaces as vector of {@link de.deepamehta.BaseTopic}s
	 *			(type <CODE>tt-workspace</CODE>)
	 *
	 * @see		#addGroupWorkspaces
	 * @see		CorporateCommands#addPublishCommand
	 * @see		CorporateCommands#addTopicTypeCommands
	 * @see		CorporateCommands#addAssocTypeCommands
	 * @see		CorporateCommands#addWorkspaceTopicTypeCommands
	 * @see		de.deepamehta.topics.LiveTopic#handleWorkspaceCommand
	 * @see		de.deepamehta.topics.UserTopic#contextCommands
	 * @see		de.deepamehta.browser.BrowserServlet#preparePage
	 */
	public Vector getWorkgroups(String userID) {
		// Note: also subtypes of association type "at-membership" are respected
		Vector subtypes = type(SEMANTIC_MEMBERSHIP, 1).getSubtypeIDs();
		// ### System.out.println(">>> respect " + subtypes.size() + " association subtypes ...");
		return cm.getRelatedTopics(userID, subtypes, 2);	// ### relTopicTypeID?
	}

	/**
	 * Returns all members of the specified workspace.
	 * <P>
	 * References checked: 15.2.2005 (2.0b5)
	 *
	 * @return	The members as vector of {@link de.deepamehta.BaseTopic}s
	 *			(type <CODE>tt-user</CODE>)
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
	 * <P>
	 * References checked: 9.10.2001 (2.0a12)
	 *
	 * @param	id					topic ID of a user resp. workspace
	 * @param	permissionMode		{@link #PERMISSION_VIEW} /
	 *								{@link #PERMISSION_CREATE} /
	 *								{@link #PERMISSION_CREATE_IN_WORKSPACE}
	 *
	 * @return	The topic types as vector of {@link de.deepamehta.BaseTopic}s
	 *			(type <CODE>tt-topictype</CODE>)
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
		return cm.getRelatedTopics(id, SEMANTIC_WORKGROUP_TYPES, TOPICTYPE_TOPICTYPE, 2, assocProp, propValue, true);
		// sortAssociations=true
	}

	/**
	 * Returns the accessible association types of the specified user resp. workspace.
	 * <P>
	 * References checked: 10.8.2001 (2.0a11)
	 *
	 * @param	id					the topic ID of the user resp. workspace
	 * @param	permissionMode		{@link #PERMISSION_VIEW} /
	 *								{@link #PERMISSION_CREATE} /
	 *								{@link #PERMISSION_CREATE_IN_WORKSPACE} (not used for association types)
	 *
	 * @return	The association types as vector of {@link de.deepamehta.BaseTopic}s
	 *			(type <CODE>tt-assoctype</CODE>)
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
		return cm.getRelatedTopics(id, SEMANTIC_WORKGROUP_TYPES, TOPICTYPE_ASSOCTYPE, 2, assocProp, propValue, true);
		// sortAssociations=true
	}

	// ---

	/**
	 * Returns all views in use of the specified user.
	 *
	 * @return	The views as vector of {@link de.deepamehta.BaseTopic}s
	 *			(type <CODE>tt-topicmap</CODE>)
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
	 * ### move to PersonTopic?
	 *
	 * @return	the email address of the specified person, or <code>null</code> if no email address is assigned.
	 */
	public String getEmailAddress(String personID) {
		Vector adrs = cm.getRelatedTopics(personID, SEMANTIC_EMAIL_ADDRESS, TOPICTYPE_EMAIL_ADDRESS, 2);
		if (adrs.size() == 0) {
			return null;
		}
		String adr = getTopicProperty((BaseTopic) adrs.firstElement(), PROPERTY_EMAIL_ADDRESS);
		if (adrs.size() > 1) {
			System.out.println("*** person \"" + personID + "\" has " + adrs.size() + " email addresses -- only \"" + adr + "\" is respected");
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
	 * <P>
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

	public boolean isViewOpen(String viewID, String userID) {
		return associationExists(userID, viewID, SEMANTIC_VIEW_IN_USE);
	}

	public boolean associationExists(String topicID1, String topicID2, String assocTypeID) {
		// Note: also association subtypes are respected
		Vector subtypes = type(assocTypeID, 1).getSubtypeIDs();
		return cm.associationExists(topicID1, topicID2, subtypes);
	}

	// --- getChat (2 forms) ---

	/**
	 * @see		de.deepamehta.topics.WorkspaceTopic#init
	 */
	public BaseTopic getChat(String workgroupID) throws AmbiguousSemanticException {
		BaseTopic workspace = getWorkspace(workgroupID);
		Vector chatIDs = cm.getTopicIDs(TOPICTYPE_CHAT_BOARD, workspace.getID());
		// error check 1
		if (chatIDs.size() == 0) {
			return null;
		}
		//
		String chatID = (String) chatIDs.firstElement();
		BaseTopic chatTopic = cm.getTopic(chatID, 1);	// ### version is set to 1
		// error check 2
		if (chatIDs.size() > 1) {
			throw new AmbiguousSemanticException("Workgroup \"" + workgroupID +
				"\" has " + chatIDs.size() + " chats (expected is 1) -- " +
				"considering only " + chatTopic, chatTopic);
		}
		//
		return chatTopic;
	}

	/**
	 * Convenience method as a wrapper for {@link #getChat(String workgroupID)} that
	 * handles the exceptional cases by adding corresponding DIRECTIVE_SHOW_MESSAGE
	 * directives to the specified directives object.
	 *
	 * @param	workgroup	a workgroup topic
	 *
	 * @see		de.deepamehta.topics.WorkspaceTopic#init
	 * @see		de.deepamehta.topics.WorkspaceTopic#nameChanged
	 */
	public BaseTopic getChat(BaseTopic workgroup, CorporateDirectives directives) {
		try {
			BaseTopic chat = getChat(workgroup.getID());
			// error check
			if (chat == null) {
				System.out.println("*** ApplicationService.getChat(2): " + this +
					" has no chat");
				directives.add(DIRECTIVE_SHOW_MESSAGE,
					"Workgroup \"" + workgroup.getName() + "\" has no chat",
					new Integer(NOTIFICATION_WARNING));
			}
			return chat;
		} catch (AmbiguousSemanticException e) {
			System.out.println("*** ApplicationService.getChat(2): " + e);
			directives.add(DIRECTIVE_SHOW_MESSAGE, e.getMessage(),
				new Integer(NOTIFICATION_WARNING));
			return e.getDefaultTopic();
		}
	}

	// --- getMessageBoard (2 forms) ---

	/**
	 * @see		de.deepamehta.topics.WorkspaceTopic#init
	 */
	public BaseTopic getMessageBoard(String workgroupID) throws AmbiguousSemanticException {
		BaseTopic workspace = getWorkspace(workgroupID);
		Vector boardIDs = cm.getTopicIDs(TOPICTYPE_MESSAGE_BOARD, workspace.getID());
		// error check 1
		if (boardIDs.size() == 0) {
			return null;
		}
		//
		String boardID = (String) boardIDs.firstElement();
		BaseTopic messageBoard = cm.getTopic(boardID, 1);	// ### version is set to 1
		// error check 2
		if (boardIDs.size() > 1) {
			// ### should not be an error
			throw new AmbiguousSemanticException("Workgroup \"" + workgroupID +
				"\" has " + boardIDs.size() + " message boards (expected is 1) -- " +
				"considering only " + messageBoard, messageBoard);
		}
		//
		return messageBoard;
	}

	/**
	 * Convenience method as a wrapper for {@link #getMessageBoard(String workgroupID)} that
	 * handles the exceptional cases by adding corresponding DIRECTIVE_SHOW_MESSAGE
	 * directives to the specified directives object.
	 *
	 * @param	workgroup	a workgroup topic
	 *
	 * @see		de.deepamehta.topics.WorkspaceTopic#init
	 * @see		de.deepamehta.topics.WorkspaceTopic#nameChanged
	 */
	public BaseTopic getMessageBoard(BaseTopic workgroup, CorporateDirectives directives) {
		try {
			BaseTopic messageBoard = getMessageBoard(workgroup.getID());
			// error check
			if (messageBoard == null) {
				System.out.println("*** ApplicationService.getMessageBoard(2): " + this +
					" has no message board");
				directives.add(DIRECTIVE_SHOW_MESSAGE, "Workgroup \"" + workgroup.getName() +
					"\" has no message board", new Integer(NOTIFICATION_WARNING));
			}
			return messageBoard;
		} catch (AmbiguousSemanticException e) {
			System.out.println("*** ApplicationService.getMessageBoard(2): " + e);
			directives.add(DIRECTIVE_SHOW_MESSAGE, e.getMessage(), new Integer(NOTIFICATION_WARNING));
			return e.getDefaultTopic();
		}
	}

	// --- getWorkspace (2 forms) ---

	/**
	 * Returns the workspace (type <CODE>tt-topicmap</CODE>) of the specified user
	 * resp. group or <CODE>null</CODE> if the specified user resp. group has no
	 * workspace.
	 *
	 * @param	id		The ID of a user (<CODE>type tt-user</CODE>) resp. group
	 *					(type <CODE>tt-workspace</CODE>) topic
	 *
	 * @return	the topic representing the workspace (type <CODE>tt-topicmap</CODE>)
	 *
	 * @throws	DeepaMehtaException		if the specified user resp. group topic is not in
	 *									ApplicationService resp. is not
	 *									properly initialized (iconfile is unknown)
	 * @see		#getChat
	 * @see		#getWorkspace(String id, CorporateDirectives directives)
	 * @see		#getCorporateSpace
	 * @see		InteractionConnection#addPersonalMap
	 * @see		InteractionConnection#addGroupMaps
	 * @see		de.deepamehta.topics.TopicMapTopic#executeChainedCommand
	 * @see		de.deepamehta.topics.UserTopic#nameChanged
	 * @see		de.deepamehta.topics.WorkspaceTopic#nameChanged
	 */
	public PresentableTopic getWorkspace(String id) throws DeepaMehtaException,
															AmbiguousSemanticException {
		Vector workspaces = cm.getRelatedTopics(id, SEMANTIC_WORKSPACE, TOPICTYPE_TOPICMAP, 2);
		if (workspaces.size() == 0) {
			// Note: this is not an exceptional condition because getWorkspace() is
			// also used for workspace existence check
			return null;
		}
		//
		BaseTopic wsp = (BaseTopic) workspaces.firstElement();
		PresentableTopic workspace = createPresentableTopic(wsp, id);	// may throw DME
		// error check
		if (workspaces.size() > 1) {
			throw new AmbiguousSemanticException("User resp. workgroup \"" + id +
				"\" has " + workspaces.size() + " workspaces (expected is 1) -- " +
				"considering only " + workspace, workspace);
		}
		//
		return workspace;
	}

	/**
	 * Convenience method as a wrapper for {@link #getWorkspace(String id)} that handles the exceptional cases by
	 * adding corresponding DIRECTIVE_SHOW_MESSAGE directives to the specified directives object.
	 * <P>
	 * References checked: 29.3.2003 (2.0a18-pre8)
	 *
	 * @param	id		ID of a user (<CODE>type tt-user</CODE>) resp. workspace (type <CODE>tt-workspace</CODE>) topic
	 *
	 * @see		de.deepamehta.topics.UserTopic#nameChanged
	 * @see		de.deepamehta.topics.WorkspaceTopic#nameChanged
	 * @see		de.deepamehta.topics.WorkspaceTopic#joinUser
	 * @see		de.deepamehta.topics.WorkspaceTopic#leaveUser
	 */
	public BaseTopic getWorkspace(String id, CorporateDirectives directives) {
		try {
			BaseTopic workspace = getWorkspace(id);	// may throw DME
			// error check
			if (workspace == null) {
				String errText = "User resp. workgroup \"" + id + "\" has no workspace";
				System.out.println("*** ApplicationService.getWorkspace(2): " + errText);
				directives.add(DIRECTIVE_SHOW_MESSAGE, errText, new Integer(NOTIFICATION_WARNING));
			}
			//
			return workspace;
		} catch (AmbiguousSemanticException e) {
			System.out.println("*** ApplicationService.getWorkspace(2): " + e);
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
	 * Returns the owner of the specified topicmap. The specified topicmap is expected to exist either
	 * in a personal workspace or in a shared workspace. Otherwise <CODE>null</CODE> is returned.
	 * <p>
	 * References checked: 24.8.2006 (2.0b8)
	 *
	 * @param	topicmapID	the ID of the topicmap. Note: this method works also for non-topicmap
	 *						topics existing in a workspace (e.g. "CorporateWeb Settings") but is
	 *						not used in this fashion.
	 *
	 * @return	The user resp. workspace as {@link de.deepamehta.BaseTopic}
	 *			(type <CODE>tt-user</CODE> resp. <CODE>tt-workspace</CODE>)
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
	 * (type <CODE>tt-topicmap</CODE>) resp. <CODE>null</CODE> if the specified
	 * topicmap doesn't represent a workspace topicmap.
	 * <p>
	 * References checked: 24.8.2006 (2.0b8)
	 *
	 * @return	The the user resp. workspace as {@link de.deepamehta.BaseTopic}
	 *			(type <CODE>tt-user</CODE> resp. <CODE>tt-workspace</CODE>)
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
	 *			(type <CODE>tt-topicmap</CODE>)
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
	 * @return	the matching association or <CODE>null</CODE> if <CODE>emptyAllowed</CODE>
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
	 * <P>
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
	 * <P>
	 * References checked: 30.12.2001 (2.0a14-pre5)
	 *
	 * @param	emptyAllowed	### required? -- currently null is passed always
	 *
	 * @return	the matching association or <CODE>null</CODE> if <CODE>emptyAllowed</CODE>
	 *			is set and no matching association is found
	 *
	 * @throws	DeepaMehtaException			if <CODE>emptyAllowed</CODE> is not set and
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
	 *
	 * @return	the users default workspace as {@link de.deepamehta.BaseTopic}
	 *			(type <CODE>tt-workspace</CODE>) or <code>null</code>
	 *
	 * @see		CorporateCommands#addPublishCommand
	 * @see		CorporateCommands#addTopicTypeCommands
	 * @see		CorporateCommands#addAssocTypeCommands
	 * @see		CorporateCommands#addWorkspaceTopicTypeCommands
	 */
	public BaseTopic getUsersDefaultWorkspace(String userID, CorporateDirectives directives) throws DeepaMehtaException {
		try {
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
	 *			(type <CODE>tt-workspace</CODE>) or <code>null</code>
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
	 * <CODE>null</CODE> if the specified topicmap was never published.
	 * <p>
	 * References checked: 24.8.2008 (2.0b8)
	 *
	 * @return	the workspace as a {@link de.deepamehta.BaseTopic} (type <CODE>tt-workspace</CODE>)
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
	 * resp. throws a <CODE>DeepaMehtaException</CODE> if an error occurs.
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
	 *			{@link de.deepamehta.BaseTopic} (type <CODE>tt-topictype</CODE>)
	 *			or <CODE>null</CODE> if no container-type exists.
	 *
	 * @throws	AmbiguousSemanticException	if more than one container-type exists
	 *
	 * @see		#getAllTopics(String typeID, int x, int y)
	 * @see		de.deepamehta.topics.TopicTypeTopic#nameChanged
	 */
	public BaseTopic getContainerType(String typeID) {
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
	 *			workgroup as {@link de.deepamehta.BaseTopic} (type <CODE>tt-topictype</CODE>).
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
	 *			(type <CODE>tt-topictype</CODE>).
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
					setTopicProperties(webpageID, 1, webpage.getProperties(), topicmapID, true, session);	// triggerPropertiesChangedHook=true
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
		InputStream in = new BufferedInputStream(url.openStream(), 1024);
		ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
		byte buffer[] = new byte[1024];
		int num;
		while ((num = in.read(buffer)) != -1) {
			out.write(buffer, 0, num);
		}
		in.close();
		String html = out.toString(0);		// hibyte=0 ### deprecated, compare to DeepaMehtaUtils.readFile()
		System.out.println("  > \"" + url + "\" -- (" + html.length() + " bytes read)");
		//
		return html;
	}



	// --------------------
	// --- Broadcasting ---
	// --------------------



	/**
	 * Sends the specified directives <I>asynchronously</I> to every enumerated user.
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
	 * Sends the specified directives <I>asynchronously</I> to every enumerated session.
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
	 * Returns all client sessions.
	 *
	 * @see		de.deepamehta.topics.ChatTopic#executeCommand
	 *
	 * @return	vector of <CODE>Session</CODE> elements
	 */
	/* ### public Vector getAllSessions() {
		Vector sessions = new Vector();
		for (int id = 0; id < MAX_CLIENTS; id++) {
			if (clientSessions[id] != null) {
				sessions.addElement(clientSessions[id]);
			}
		}
		return sessions;
	} */

	/**
	 * ### to be dropped
	 *
	 * References checked: 1.9.2002 (2.0a16-pre2)
	 *
	 * @see		de.deepamehta.topics.LiveTopic#copyAndUpload
	 * @see		de.deepamehta.topics.TopicMapTopic#doImport
	 * @see		de.deepamehta.topics.CMImportExportTopic#executeChainedCommand
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
	 * Returns a new session ID or <CODE>-1</CODE> if the server is overloaded.
	 * Valid session are in the range of <CODE>1</CODE> to
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
	 * <P>
	 * The initial GUI consists of the users <I>workspaces</I> as well as the <I>views</I>
	 * from previous session.
	 * <P>
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
		// ### email checking is disabled. ### threads are not stopped / creates to many threads on the server
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
	 *								known messages are <CODE>import</CODE>,
	 *								<CODE>export</CODE> and <CODE>importCM</CODE>
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
			personalWorkspace = getWorkspace(userID);
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
		Enumeration e = getWorkgroups(session.getUserID()).elements();
		while (e.hasMoreElements()) {
			PresentableTopic groupWorkspace;
			try {
				BaseTopic workgroup = (BaseTopic) e.nextElement();
				String workgroupID = workgroup.getID();
				groupWorkspace = getWorkspace(workgroupID);
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
		cm.createAssociation(getNewAssociationID(), 1, SEMANTIC_VIEW_IN_USE, 1,
			session.getUserID(), 1, viewID, 1);
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
		File topicmapFile = new File(FILESERVER_DOCUMENTS_PATH + filename);
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
		File cmFile = new File(FILESERVER_DOCUMENTS_PATH + filename);
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
	 * References checked: 2.10.2002 (2.0a16-pre5)
	 *
	 * @see		de.deepamehta.topics.TopicTypeTopic#nameChanged
	 * @see		de.deepamehta.topics.TopicTypeTopic#createContainerType
	 */
	public String containerTypeName(String typeName) throws DeepaMehtaException {
		String containerTypeName = typeName + " " + CONTAINER_SUFFIX_NAME;
		// error check
		int len = containerTypeName.length();
		if (len > MAX_NAME_LENGTH) {
			throw new DeepaMehtaException("container type name for \"" + typeName + "\" too long: \"" +
				containerTypeName + "\", " + len + " characters, maximum is " + MAX_NAME_LENGTH + " characters");
		}
		//
		return containerTypeName;
	}



	// -----------------------------------
	// --- Creating Presentable Topics ---
	// -----------------------------------



	/**
	 * Creates a vector of {@link de.deepamehta.PresentableTopic}s corresponding to
	 * the {@link de.deepamehta.BaseTopic}s in the specified vector.
	 *
	 * @return	Vector of {@link de.deepamehta.PresentableTopic}
	 *
	 * @see		de.deepamehta.topics.TopicContainerTopic#processQuery
	 */
	public Vector createPresentableTopics(String topicmapID, Vector baseTopics, String nearTopicID) {
		Vector topics = new Vector();
		//
		Enumeration e = baseTopics.elements();
		while (e.hasMoreElements()) {
			BaseTopic topic = (BaseTopic) e.nextElement();
			topics.addElement(createPresentableTopic(topic, nearTopicID, topicmapID));
		}
		return topics;
	}

	// --- createPresentableTopic (6 forms) ---

	public PresentableTopic createPresentableTopic(String topicID, int version) throws DeepaMehtaException {
		PresentableTopic topic = createPresentableTopic(cm.getTopic(topicID, version));
		// ### topic.setFreeGeometry();
		return topic;
	}

	/**
	 * ### Actually creates an PresentableTopic based on the specified BaseTopic
	 * <I>and</I> initializes its appearance. Note: the appearance initialization
	 * is based on the corresponding LiveTopic, thus an DeepaMehtaException is
	 * thrown if the topic dosn't exist in live corporate memory. The geometry of
	 * the created PresentableTopic remains uninitialized.
	 *
	 * @throws	DeepaMehtaException		if the specified topic is not in
	 *									ApplicationService resp. is not
	 *									properly initialized (iconfile is unknown)
	 *
	 * @see		de.deepamehta.topics.TopicMapTopic#openPersonalView
	 */
	public PresentableTopic createPresentableTopic(BaseTopic topic) throws DeepaMehtaException {
		return createPresentableTopic(topic, topic.getID());
	}

	/**
	 * @throws	DeepaMehtaException		if the specified topic is not loaded resp. not
	 *									properly initialized (iconfile is unknown)
	 * @see		#getWorkspace
	 * @see		#getCorporateSpace
	 * @see		#createPresentableTopic(BaseTopic topic)	(above)
	 * @see		de.deepamehta.topics.TopicMapTopic#openGroupView
	 */
	public PresentableTopic createPresentableTopic(BaseTopic topic, String appTopicID)
															throws DeepaMehtaException {
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
			PresentableTopic topic = new PresentableTopic(getNewTopicID(), 1, typeID, 1,
				name, nearTopicID);
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
	 * @see		#createPresentableTopics
	 * @see		de.deepamehta.topics.LiveTopic#revealTopic
	 */
	public PresentableTopic createPresentableTopic(BaseTopic topic, String nearTopicID, String topicmapID) {
		// --- trigger getPresentableTopic() hook ---
		return getLiveTopic(topicmapID, 1).getPresentableTopic(topic, nearTopicID);
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
		LiveTopic proxyTopic = getLiveTopic(topic);					// may throw DME
		if (proxyTopic.getIndividualAppearanceMode() == APPEARANCE_CUSTOM_ICON) {
			topic.setIcon(proxyTopic.getIndividualAppearanceParam());
		}
	}

	/**
	 * @see		#initTypeTopic
	 */
	private void initTypeAppearance(PresentableType typeTopic, TypeTopic typeProxy) {
		typeTopic.setTypeIconfile(typeProxy.getIconfile());		// throws DME
		typeTopic.setAssocTypeColor(typeProxy.getAssocTypeColor());
	}

	// ---

	/**
	 * Returns a string containing the comma separated property names
	 * involved in the specified query.
	 * <P>
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
	 * <P>
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
	 * <P>
	 * May be empty, but never <CODE>null</CODE>.
	 */
	public String typeName(String typeID) {
		return type(typeID, 1).getName();
	}

	/**
	 * Returns the type name of the specified topic.
	 * <P>
	 * May be empty, but never <CODE>null</CODE>.
	 */
	public String typeName(BaseTopic topic) {
		return type(topic).getName();
	}

	// --- type (3 forms) ---

	/**
	 * Returns the type of the specified topic as live topic (type <CODE>tt-topictype</CODE>).
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
	 * Returns the type of the specified association as live topic (type <CODE>tt-assoctype</CODE>).
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
	 * Returns the specified type as live topic (type <CODE>tt-topictype</CODE> or <CODE>tt-assoctype</CODE>).
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
	 * <P>
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
				cm.createAssociation(assoc.getID(), 1, assoc.getType(), 1,
					assoc.getTopicID1(), 1, assoc.getTopicID2(), 1);
			}
			//
			createViewAssociation(topicmapID, topicmapVersion, viewMode, assoc.getID(), assoc.getVersion(),
				performExistenceCheck);
		}
	}

	/**
	 * @see		#personalizeView
	 */
	/* ### private void personalizeAssociations(String topicmapID, int topicmapVersion, String viewMode, Enumeration assocs,
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
	 * @see		#addTypeToView
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
	private Hashtable removeUnchangedProperties(Hashtable newData, Hashtable oldData) {
		Hashtable result = (Hashtable) newData.clone();
		//
		Enumeration e = result.keys();
		String prop;
		while (e.hasMoreElements()) {
			prop = (String) e.nextElement();
			String oldValue = (String) oldData.get(prop);
			String newValue = (String) result.get(prop);
			if (newValue.equals(oldValue)) {
				result.remove(prop);
				// ### oldData.remove(prop);
			} else if (oldValue == null && newValue.equals("")) {
				result.remove(prop);
			}
		}
		//
		return result;
	}

	// ---

	/**
	 * Checks weather an association of a certain type is allowed between certain two topics.
	 * Called once an association is about to be created resp. to be retyped.
	 * Both topics are asked to propose another association type to be used or to prohibit the operation at all.
	 *
	 * @param	currentAssocTypeID	the current association type in case of a retype operation.
	 *								<code>null</code> in case of a create operation.
	 * @return	the association type to be used, or <code>null</code> to prohibit the operation.
	 *
	 * @see		#createAssociation
	 * @see		#changeAssociationType
	 */
	private String associationAllowed(String assocTypeID, String currentAssocTypeID, String topicID1, String topicID2,
																	Session session, CorporateDirectives directives) {
		LiveTopic topic1 = getLiveTopic(topicID1, 1);
		LiveTopic topic2 = getLiveTopic(topicID2, 1);
		// --- trigger associationAllowed() hook ---
		String assocType1 = topic1.associationAllowed(assocTypeID, topicID2, directives);
		String assocType2 = topic2.associationAllowed(assocTypeID, topicID1, directives);
		//
		if (assocType1 == null || assocType2 == null) {
			directives.add(DIRECTIVE_SHOW_MESSAGE, "Association retyping not possible (prohibited by involved topic)", new Integer(NOTIFICATION_WARNING));
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
				directives.add(DIRECTIVE_SHOW_MESSAGE, "Association retyping not possible (involved topics have contradictive bahavoir)", new Integer(NOTIFICATION_WARNING));
				return null;
			}
		} else if (retype1) {
			System.out.println(">>> " + topic1 + " modified the assoc retyping (\"" + assocTypeID + "\" -> \"" + assocType1 + "\")");
			assocTypeID = assocType1;
		} else if (retype2) {
			System.out.println(">>> " + topic2 + " modified the assoc retyping (\"" + assocTypeID + "\" -> \"" + assocType2 + "\")");
			assocTypeID = assocType2;
		}
		// --- trigger associated() hook ---
		topic1.associated(assocTypeID, currentAssocTypeID, topicID2, session, directives);
		topic2.associated(assocTypeID, currentAssocTypeID, topicID1, session, directives);
		//
		return assocTypeID;
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
	 * @param	directives	may be <CODE>null</CODE>
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
	 * @param	directives	may be <CODE>null</CODE>
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
	 * @param	directives	may be <CODE>null</CODE>
	 *
	 * @see		#createCustomLiveTopic
	 */
	private void addErrorNotification(String errorText, String errorText2,
									CorporateDirectives directives, String className) {
		addErrorNotification(errorText, errorText2, directives);
		//
		if (className.startsWith("org/xml/")) {
			if (directives != null) {
				directives.add(DIRECTIVE_SHOW_MESSAGE, ">>> Make shure IBMs XML parser " +
					"(xml4j.jar) is installed correctly at server side",
					new Integer(NOTIFICATION_ERROR));
			}
			System.out.println(">>> Make shure IBMs XML parser (xml4j.jar) is " +
				"installed correctly");
		} else if (className.startsWith("javax/activation/")) {
			if (directives != null) {
				directives.add(DIRECTIVE_SHOW_MESSAGE, ">>> Make shure Suns activation " +
					"framework (activation.jar) is installed correctly at server side",
					new Integer(NOTIFICATION_ERROR));
			}
			System.out.println(">>> Make shure Suns activation framework " +
				"(activation.jar) is installed correctly");
		}
	}

	/**
	 * @param	directives	may be <CODE>null</CODE>
	 *
	 * @see		#createCustomLiveTopic
	 */
	private void addErrorNotification(String errorText, String errorText2,
														CorporateDirectives directives) {
		if (directives != null) {
			directives.add(DIRECTIVE_SHOW_MESSAGE, errorText2 + " (" + errorText + ")",
				new Integer(NOTIFICATION_ERROR));
		}
		System.out.println("*** ApplicationService.createCustomLiveTopic(): " + errorText +
			" -- " + errorText2);
	}

	// ---

	/**
	 * @see		#addPublishDirectives(PresentableTopicMap topicmap, CorporateDirectives directives)
	 */
	private void addPublishDirectives(Enumeration topics, CorporateDirectives directives) {
		PresentableTopic topic;
		while (topics.hasMoreElements()) {
			topic = (PresentableTopic) topics.nextElement();
			addPublishAction(topic, directives);
		}
	}

	/**
	 * @see		#addPublishDirectives
	 */
	private void addPublishAction(BaseTopic topic, CorporateDirectives directives) {
		// --- trigger published() hook ---
		directives.add(getLiveTopic(topic).published());
	}
}
