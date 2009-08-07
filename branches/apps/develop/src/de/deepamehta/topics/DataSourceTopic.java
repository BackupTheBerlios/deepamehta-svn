package de.deepamehta.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.TopicInitException;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateDatasource;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.CorporateLDAPSource;
import de.deepamehta.service.CorporateSQLSource;
import de.deepamehta.service.CorporateXMLSource;
import de.deepamehta.service.Session;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;



/**
 * This core topic type represents a {@link de.deepamehta.service.CorporateDatasource}.
 * <p>
 * A <code>DataSourceTopic</code> is associated to its {@link DataConsumerTopic}s by
 * means of an association of type <code>at-association</code> (direction is from
 * data consumer to datasource).
 * <p>
 * <hr>
 * Last functional change: 7.12.2007 (2.0b8)<br>
 * Last documentation update: 23.2.2001 (2.0a9-post1)<br>
 * J&ouml;rg Richter<br>
 * jri@freenet.de
 */
public class DataSourceTopic extends LiveTopic implements Runnable {



	// **************
	// *** Fields ***
	// **************



	// Note: the XML path is relative to servers working directory

	protected String dbtype;
	protected String url;
	private String username;
	private String password;
	protected String elements;
	protected String idleElement;

	protected CorporateDatasource myDataSource;

	/**
	 * The idle thread.
	 */
	Thread idleThread;



	// *******************
	// *** Constructor ***
	// *******************



	public DataSourceTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}



	// ******************************************************
	// *** Implementation of Interface java.lang.Runnable ***
	// ******************************************************



	/**
	 * The body of the idle thread.
	 */
	public void run() {
		while (true) {
			try {
				Thread.sleep(5 * 60 * 1000);	// interval is 5 min.
				System.out.println("> \"" + getName() + "\" statistics (\"" + idleElement + "\"): " +
					myDataSource.getElementCount(idleElement));
			} catch (InterruptedException e) {
				System.out.println("*** DataSourceTopic.run(): " + e);
			} catch (Exception e) {
				System.out.println("*** DataSourceTopic.run(): " + e);
			}
		}
	}



	// ************************
	// *** Overriding Hooks ***
	// ************************



	// ------------------
	// --- Life Cycle ---
	// ------------------



	public CorporateDirectives init(int initLevel, Session session) throws TopicInitException {
		CorporateDirectives directives = super.init(initLevel, session);
		//
		if (initLevel == INITLEVEL_1) {
			if (LOG_TOPIC_INIT) {
				System.out.println(">>> DataSourceTopic.init(" + initLevel + "): " +
					this + " -- open corporate datasource");
			}
			try {
				openCorporateDatasource();		// throws TopicInitException
			} catch (TopicInitException e) {
				System.out.println("*** DataSourceTopic.init(): " + e);
				directives.add(DIRECTIVE_SHOW_MESSAGE, e.getMessage(), new Integer(NOTIFICATION_WARNING));
			}
		}
		//
		return directives;
	}



	// ---------------------------
	// --- Handling Properties ---
	// ---------------------------



	public CorporateDirectives propertiesChanged(Hashtable newData, Hashtable oldData,
															String topicmapID, String viewmode, Session session) {
		CorporateDirectives directives = super.propertiesChanged(newData, oldData, topicmapID, viewmode, session);
		try {
			System.out.println(">>> DataSourceTopic.propertiesChanged(): properties changed from " +
				oldData + " to " + newData + " -- reopen corporate datasource");
			// --- reopen the corporate datasource ---
			openCorporateDatasource();		// throws TopicInitException
			// --- reinitialize all topics who consumes from this datasource ---
			reinitializeDataconsumers(topicmapID, session, directives);
		} catch (TopicInitException e) {
			System.out.println("*** DataSourceTopic.propertiesChanged(): " + e);
			directives.add(DIRECTIVE_SHOW_MESSAGE, e.getMessage(), new Integer(NOTIFICATION_WARNING));
		}
		return directives;
	}



	// **********************
	// *** Custom Methods ***
	// **********************



	/**
	 * @see		DataConsumerTopic#setDataSource
	 */
	public String getURL() {
		return url;
	}

	/**
	 * @see		DataConsumerTopic#setDataSource
	 */
	public CorporateDatasource getDataSource() {
		return myDataSource;
	}

	// ---

	/**
	 * @see		#init
	 * @see		#propertiesChanged
	 */
	private void openCorporateDatasource() throws TopicInitException {
		this.dbtype = getProperty("Database Type");
		this.url = getProperty("URL");
		this.username = getProperty("Username");
		this.password = getProperty("Password");
		this.idleElement = getProperty("Idle Elementtype");
		this.elements = getProperty("Entities");
		//
		String text = "Datasource " + this + " not available ";		// ### can't be static
		// error check 1
		if (url.equals("")) {
			throw new TopicInitException(text + "(URL not set)");
		}
		// error check 2
		if (dbtype.equals("")) {
			throw new TopicInitException(text + "(Database Type not set)");
		}
		// ### passing "text" bad
		if (url.startsWith("xml:")) {
			this.myDataSource = createXMLSource(url, elements, text);	// throws TopicInitException
		} else if (url.startsWith("jdbc:")) {
			this.myDataSource = createSQLSource(url, dbtype, text);		// throws TopicInitException
		} else if (url.startsWith("ldap:")) {
			this.myDataSource = createLDAPSource(url, text);			// throws TopicInitException
		} else {
			throw new TopicInitException(text + "(URL has unexpected protocol: \"" +
				url + "\"" + " >>> expected protocols are \"jdbc:\", \"xml:\" and \"ldap:\")");
		}
	}

	// ---
	
	/**
	 * @return	a new instance of CorporateXMLSource
	 */
	private CorporateDatasource createXMLSource(String url, String elements, String errmsg)
																	throws TopicInitException {
		// Note: the XML path is relative to servers working directory
		String file = url.substring(4);
		// --- open XML datasource ---
		try {
			return new CorporateXMLSource(file, elements);
		} catch (SAXException e) {
			throw new TopicInitException(errmsg + "(Syntax error in \"" + file +
				"\": " + e + " >>> Make sure attribute values are enclosed in \"\")", e);
		} catch (IOException e) {
			throw new TopicInitException(errmsg + "(" + e + ")", e);
		}
	}

	/**
	 * @return	a new instance of CorporateSQLSource
	 */
	private CorporateDatasource createSQLSource(String url, String dbtype, String errmsg)
														throws TopicInitException {
		//  --- open SQL datasource ---
		try {
			CorporateDatasource source = new CorporateSQLSource(url, dbtype, username, password);
			startIdleThread();
			return source;
		} catch (ClassNotFoundException e) {
			throw new TopicInitException(errmsg + "(class not found: " + e.getMessage() + ")", e);
		} catch (Exception e) {
			throw new TopicInitException(errmsg + "(" + e + ")", e);
		}
	}

	/**
	 * @return	a new instance of CorporateLDAPSource
	 */
	private CorporateDatasource createLDAPSource(String url, String errmsg) throws TopicInitException {
		try {
			return new CorporateLDAPSource(url);
		} catch (Throwable e) {
			throw new TopicInitException(errmsg + "(" + e + ")");
		}
	}

	// ---

	private void reinitializeDataconsumers(String topicmapID, Session session, CorporateDirectives directives) {
		Vector consumerTypes = as.dataConsumerTypes(getID());
		Enumeration e = consumerTypes.elements();
		int topicCount;		// reporting only
		int initCount;		// reporting only
		// loop through all topic types who consumes from this datasource
		while (e.hasMoreElements()) {
			BaseTopic consumerType = (BaseTopic) e.nextElement();
			// get all instances of that type in the current topicmap
			Enumeration consumers = cm.getTopicIDs(consumerType.getID(), topicmapID).elements();
			//
			topicCount = 0;
			initCount = 0;
			// loop through all instances
			while (consumers.hasMoreElements()) {
				topicCount++;
				String consumerID = (String) consumers.nextElement();
				// ### version is set to 1
				// ### may throw DeepaMehtaException
				LiveTopic consumer = as.getLiveTopic(consumerID, 1);
				if (reinitializeDataconsumer(consumer, session, directives)) {
					initCount++;
				}
			}
			directives.add(DIRECTIVE_SHOW_MESSAGE, "Datasource of " + initCount + "/" + topicCount + " \"" +
				consumerType.getName() + "\" topics reinitialized", new Integer(NOTIFICATION_DEFAULT));
		}
	}

	/**
	 * @see		#propertiesChanged
	 */
	private boolean reinitializeDataconsumer(LiveTopic consumer, Session session, CorporateDirectives directives) {
		try {
			if (consumer instanceof DataConsumerTopic || consumer instanceof ElementContainerTopic) {
				// reinitialize DataConsumerTopic
				consumer.init(INITLEVEL_2, session);
				return true;
			} else {
				String text = "\"" + consumer.getName() + "\" is neither a DataConsumerTopic nor a " +
					"ElementContainerTopic, but a " + consumer.getClass() + " -- datasource not reinitialized";
				System.out.println("*** DataSourceTopic.reinitializeDataconsumer(): " + text);
				directives.add(DIRECTIVE_SHOW_MESSAGE, text, new Integer(NOTIFICATION_WARNING));
			}
		} catch (DeepaMehtaException e) {
			System.out.println("*** DataSourceTopic.reinitializeDataconsumer(): " +
				e.getMessage() + " -- datasource of dataconsumer not properly reinitialized");
			directives.add(DIRECTIVE_SHOW_MESSAGE, e.getMessage(), new Integer(NOTIFICATION_ERROR));
		} catch (TopicInitException e) {
			System.out.println("*** DataSourceTopic.reinitializeDataconsumer(): " +
				e.getMessage() + " -- datasource of dataconsumer not properly reinitialized");
			directives.add(DIRECTIVE_SHOW_MESSAGE, e.getMessage(), new Integer(NOTIFICATION_ERROR));
		}
		return false;
	}

	// ---

	/**
	 * @see		#createSQLSource
	 */
	private void startIdleThread() {
		// error check
		if (idleElement.equals("")) {
			System.out.println("*** \"Idle Elementtype\" not set for datasource " + this);
		}
		//
		if (idleThread == null) {
			// ### System.out.print(">    starting idle thread ... ");
			//
			idleThread = new Thread(this);
			idleThread.start();
			//
			// ### System.out.println("OK");
		}
	}
}
