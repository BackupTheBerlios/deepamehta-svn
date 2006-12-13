package de.deepamehta.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.TopicInitException;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.Session;
import de.deepamehta.service.CorporateDatasource;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.CorporateXMLSource;
import de.deepamehta.service.CorporateSQLSource;
import de.deepamehta.service.CorporateLDAPSource;
//
import org.xml.sax.SAXException;
import java.io.IOException;
import java.util.*;



/**
 * This kernel topic represents a {@link de.deepamehta.service.CorporateDatasource}.
 * <P>
 * A <CODE>DataSourceTopic</CODE> is associated to its {@link DataConsumerTopic}s by
 * means of an association of type <CODE>at-association</CODE> (direction is from
 * data consumer to datasource).
 * <P>
 * <HR>
 * Last functional change: 8.11.2004 (2.0b3)<BR>
 * Last documentation update: 23.2.2001 (2.0a9-post1)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class DataSourceTopic extends LiveTopic implements Runnable {



	// **************
	// *** Fields ***
	// **************



	// Note: the XML path is relative to servers working directory

	protected String url;
	protected String driver;
	protected String elements;
	protected String idleElement;

	protected CorporateDatasource dataSource;

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
				System.out.println("> \"" + getName() + "\" statistics (\"" +
					idleElement + "\"): " + dataSource.getElementCount(idleElement));
			} catch (InterruptedException e) {
				System.out.println("*** DataSourceTopic.run(): " + e);
			} catch (Exception e) {
				System.out.println("*** DataSourceTopic.run(): " + e);
			}
		}
	}



	// ***************
	// *** Methods ***
	// ***************



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
		return dataSource;
	}



	// ----------------------
	// --- Defining Hooks ---
	// ----------------------



	/**
	 * <TABLE>
	 * <TR><TD><B>Called by</B></TD><TD><CODE>initLevel</CODE></TD></TR>
	 * <TR><TD>{@link de.deepamehta.service.ApplicationService#createLiveTopic}</TD><TD>1</TD></TR>
	 * <TR><TD>{@link de.deepamehta.service.ApplicationService#initTopic}</TD><TD>variable</TD></TR>
	 * <TR><TD>{@link de.deepamehta.service.ApplicationService#initTypeTopic}</TD><TD>3</TD></TR>
	 * </TABLE>
	 */
	public CorporateDirectives init(int initLevel, Session session)
															throws TopicInitException {
		CorporateDirectives directives = super.init(initLevel, session);
		//
		if (initLevel == INITLEVEL_1) {
			if (LOG_TOPIC_INIT) {
				System.out.println(">>> DataSourceTopic.init(" + initLevel + "): " +
					this + " -- open corporate datasource");
			}
			openCorporateDatasource();		// throws TopicInitException
		}
		//
		return directives;
	}

	public CorporateDirectives propertiesChanged(Hashtable newData, Hashtable oldData,
				String topicmapID, String viewmode, Session session) throws DeepaMehtaException {
		try {
			System.out.println(">>> DataSourceTopic.propertiesChanged(): connection " +
				"parameters changed from " + oldData + " to " + newData + " -- reopen " +
				"corporate datasource");
			//
			CorporateDirectives directives = super.propertiesChanged(newData, oldData,
				topicmapID, viewmode, session);
			// --- reopen the corporate datasource ---
			openCorporateDatasource();		// throws TopicInitException
			// --- inform all associated consumers of this datasource ---
			Vector consumerTypes = as.dataConsumerTypes(getID());
			Enumeration e = consumerTypes.elements();
			BaseTopic consumerType;
			Enumeration consumers;
			int topicCount;	// reporting only
			int initCount;	// reporting only
			String consumerID;
			LiveTopic consumer;
			// loop through all topic types who consumes from this datasource
			while (e.hasMoreElements()) {
				consumerType = (BaseTopic) e.nextElement();
				// get all instances of that type in the current topicmap
				consumers = cm.getTopicIDs(consumerType.getID(), topicmapID).elements();
				//
				topicCount = 0;
				initCount = 0;
				// loop through all instances
				while (consumers.hasMoreElements()) {
					topicCount++;
					consumerID = (String) consumers.nextElement();
					// ### version is set to 1
					// ### may throw DeepaMehtaException
					consumer = as.getLiveTopic(consumerID, 1);
					if (reinitializeDataconsumer(consumer, directives, session)) {
						initCount++;
					}
				}
				directives.add(DIRECTIVE_SHOW_MESSAGE, "Datasource of " +
					initCount + "/" + topicCount + " \"" + consumerType.getName() +
					"\" topics reinitialized", new Integer(NOTIFICATION_DEFAULT));
			}
			return directives;
		} catch (TopicInitException e2) {
			throw new DeepaMehtaException(e2.getMessage());
		}
	}



	// **********************
	// *** Private Method ***
	// **********************



	/**
	 * @see		#init
	 * @see		#propertiesChanged
	 */
	private void openCorporateDatasource() throws TopicInitException {
		this.url = getProperty("URL");
		this.driver = getProperty("Driver");
		this.elements = getProperty("Entities");
		this.idleElement = getProperty("Idle Elementtype");
		//
		String text = "Datasource \"" + getName() + "\" not available ";
		// error check 1
		if (url.equals("")) {
			throw new TopicInitException(text + "(URL not set)");
		}
		// error check 2
		if (driver.equals("")) {
			throw new TopicInitException(text + "(Driver not set)");
		}
		// ### passing text bad
		if (url.startsWith("xml:")) {
			this.dataSource = createXMLSource(url, elements, text);		// throws TopicInitException
		} else if (url.startsWith("jdbc:")) {
			this.dataSource = createSQLSource(url, driver, text);		// throws TopicInitException
		} else if (url.startsWith("ldap:")) {
			this.dataSource = createLDAPSource(url, text);				// throws TopicInitException
		} else {
			throw new TopicInitException(text + "(URL has unexpected protocol: \"" +
				url + "\"" + " >>> expected protocols are \"jdbc:\", \"xml:\" and \"ldap:\")");
		}
	}
	
	/** @return a new instance of CorporateXMLSource */
	private CorporateDatasource createXMLSource(String url, String elements, String errmsg)
																	throws TopicInitException {
		// Note: the XML path is relative to servers working directory
		String file = url.substring(4);
		// --- open XML datasource ---
		try {
			return new CorporateXMLSource(file, elements);
		} catch (SAXException e) {
			throw new TopicInitException(errmsg + "(Syntax error in \"" + file +
				"\": " + e + " >>> Make sure attribute values are enclosed in \"\")");
		} catch (IOException e) {
			throw new TopicInitException(errmsg + "(" + e + ")");
		}
	}

	/**
	 * @return	a new instance of CorporateSQLSource
	 */
	private CorporateDatasource createSQLSource(String url, String driver, String errmsg)
														throws TopicInitException {
		//  --- open SQL datasource ---
		try {
			CorporateDatasource source = new CorporateSQLSource(url, driver);
			startIdleThread();
			return source;
		} catch (ClassNotFoundException e) {
			throw new TopicInitException(errmsg + "(class not found: " +
				e.getMessage() + ")");
		} catch (Exception e) {
			throw new TopicInitException(errmsg + "(" + e + ")");
		}
	}

	/**
	 * @return	a new instance of CorporateLDAPSource
	 */
	private CorporateDatasource createLDAPSource(String url, String errmsg)
														throws TopicInitException {
		try {
			return new CorporateLDAPSource(url);
		} catch (Throwable e) {
			throw new TopicInitException(errmsg + "(" + e + ")");
		}		
	}

	/**
	 * @see		#propertiesChanged
	 */
	private boolean reinitializeDataconsumer(LiveTopic consumer,
								CorporateDirectives directives, Session session) {
		try {
			if (consumer instanceof DataConsumerTopic ||
				consumer instanceof ElementContainerTopic) {
				// reinitialize DataConsumerTopic
				consumer.init(INITLEVEL_2, session);
				return true;
			} else {
				String text = "\"" + consumer.getName() + "\" is neither a " +
					"DataConsumerTopic nor a ElementContainerTopic, but a " +
					consumer.getClass() + " -- datasource not reinitialized";
				System.out.println("*** DataSourceTopic.reinitializeDataconsumer(): " +
					text);
				directives.add(DIRECTIVE_SHOW_MESSAGE, text,
					new Integer(NOTIFICATION_WARNING));
			}
		} catch (DeepaMehtaException e) {
			System.out.println("*** DataSourceTopic.reinitializeDataconsumer(): " +
				e.getMessage() + " -- datasource of dataconsumer not " +
				"properly reinitialized");
			directives.add(DIRECTIVE_SHOW_MESSAGE, e.getMessage(),
				new Integer(NOTIFICATION_ERROR));
		} catch (TopicInitException e) {
			System.out.println("*** DataSourceTopic.reinitializeDataconsumer(): " +
				e.getMessage() + " -- datasource of dataconsumer not " +
				"properly reinitialized");
			directives.add(DIRECTIVE_SHOW_MESSAGE, e.getMessage(),
				new Integer(NOTIFICATION_ERROR));
		}
		return false;
	}

	/**
	 * @see		#openCorporateDatasource
	 */
	private void startIdleThread() {
		// error check
		if (idleElement.equals("")) {
			System.out.println("*** \"Idle Elementtype\" not set for datasource \"" +
				getName() + "\"");
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
