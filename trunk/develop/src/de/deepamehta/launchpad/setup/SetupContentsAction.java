/*
 * Created on 25.01.2006
 *
 * This file is part of the DeepaMehta framework.
 */
package de.deepamehta.launchpad.setup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.deepamehta.DeepaMehtaException;
import de.deepamehta.DeepaMehtaMessages;
import de.deepamehta.environment.Environment;
import de.deepamehta.environment.EnvironmentException;
import de.deepamehta.environment.instance.CorporateMemoryConfiguration;
import de.deepamehta.environment.instance.InstanceConfiguration;
import de.deepamehta.service.CorporateMemory;

/**
 * This class is responsible for populating a new and empty Corporate Memory with the initial
 * contents required by the DeepaMehta core.
 * @author vwegert
 */
class SetupContentsAction extends DefaultHandler implements SetupAction {

    private static Log logger = LogFactory.getLog(SetupStorageAction.class);
    
    protected Environment env;
    protected InstanceConfiguration config;
    protected ArrayList messages;
    
    private String workingDir = null;
    private CorporateMemoryConfiguration cmConfig;
    private CorporateMemory cm = null;
    
    private static final int LAST_TOPIC = 1;
    private static final int LAST_ASSOC = 2;
    private static final int LAST_MAP   = 3;
    private int lastObject = 0;
    private String lastID = "";
    private int lastVersion = 1;

    /**
     * Default constructor.
     * @param config The instance configuration to populate.
     */
    public SetupContentsAction(InstanceConfiguration config) {
		this.env = Environment.getEnvironment();
		this.config = config;
		this.messages = new ArrayList();
        try {
            this.cmConfig = config.getCMConfig();
            this.workingDir = config.getWorkingDirectory();
        } catch (EnvironmentException e) {
            logger.error("Unable to get CM configuration.", e);
            this.cmConfig = null;
        }
    }

    /* (non-Javadoc)
     * @see de.deepamehta.launchpad.setup.SetupAction#getDescription()
     */
    public String getDescription() {
        return DeepaMehtaMessages.getString("SetupContentsAction.SetupDescription"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see de.deepamehta.launchpad.setup.SetupAction#canExecute()
     */
    public boolean canExecute() {
        this.messages.clear();

        // we need a CM configuration to continue
        if (this.cmConfig == null) {
        	addErrorMessage("Unable to determine Corporate Memory configuration.");
        	return false;
        }
        
        return true;
    }

    /* (non-Javadoc)
     * @see de.deepamehta.launchpad.setup.SetupAction#execute()
     */
    public boolean execute() {
        this.messages.clear();

        // create CM instance
        this.cm = this.cmConfig.getInstance();
        if (this.cm == null) {
            addErrorMessage("Unable to instantiate CM implementation.");
            return false;
        }

        // populate database
        try {
        	this.cm.startup(this.cmConfig, true);
        } catch (DeepaMehtaException e) {
        	addErrorMessage("Unable to startup Corporate Memory.", e);
			this.cm = null;
			return false;
        }
            
        // get input data file 
        String filename = this.workingDir + this.env.getFileSeparator() + "DefaultContents.xml";
        logger.debug("Loading contents from file " + filename);
        File input = new File(filename);
        
        // prepare SAX parser
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(true);
            
        // parse the input file
        try {
        	SAXParser parser = factory.newSAXParser();
        	parser.parse(input, this);
        } catch (ParserConfigurationException e) {
        	addErrorMessage("Unable to configure XML parser.", e);
        	this.cm.shutdown();
        	this.cm = null;
        	return false;
        } catch (SAXException e) {
        	addErrorMessage("Problem parsing XML file.", e);
        	this.cm.shutdown();
        	this.cm = null;
        	return false;
        } catch (IOException e) {
        	addErrorMessage("I/O error while parsing XML file.", e);
        	this.cm.shutdown();
        	this.cm = null;
        	return false;
        }
        
        // setup the key generators
        //     1 -  500 Kernel
        //   501 - 1000 included examples 
        //   600 -  799 Kompetenzstern
        this.cm.setKeyGenerator("Topic", 1001);
        this.cm.setKeyGenerator("Association", 1001);
        
        this.cm.shutdown();
        this.cm = null;
        
        return true;
    }

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		
		if (qName.equals("dmc:contents")) {
			setCMVersions(attributes);
		}
		if (qName.equals("topic")) {
			ensureTopicExists(attributes);
		}
		if (qName.equals("association")) {
			ensureAssociationExists(attributes);
		}
		if (qName.equals("topicmap")) {
			ensureTopicmapExists(attributes);
		}
		if (qName.equals("property")) {
			switch(this.lastObject) {
			case LAST_TOPIC: ensureTopicProperty(attributes); break;
			case LAST_ASSOC: ensureAssociationProperty(attributes); break;
			case LAST_MAP  : ensureTopicProperty(attributes); break;
			}
		}
		if (qName.equals("viewtopic")) {
			ensureTopicVisible(attributes);
		}
		if (qName.equals("viewassociation")) {
			ensureAssociationVisible(attributes);
		}
	}
	
	private void setCMVersions(Attributes attributes) {
		
		int modelVersion = Integer.parseInt(attributes.getValue("modelVersion"));
		int contentVersion = Integer.parseInt(attributes.getValue("contentVersion"));
		
		this.cm.setKeyGenerator("DB-Model Version", modelVersion);
		this.cm.setKeyGenerator("DB-Content Version", contentVersion);
	
	}

	private int getVersion(Attributes attributes, String key) {
		int i;
		try {
			i = Integer.parseInt(attributes.getValue(key));
		} catch (NumberFormatException e) {
			i = 1;
		}
		return i;
	}
	
	private void ensureTopicExists(Attributes attributes) {
		String topicName, topicID, typeID;
		int topicVersion, typeVersion;
		
		// parse XML attributes
		topicName    = attributes.getValue("name");
		topicID      = attributes.getValue("id");
		topicVersion = getVersion(attributes, "version");
		typeID       = attributes.getValue("type");
		typeVersion  = getVersion(attributes, "typeversion");
		
		// perform action if necessary
		logger.debug("Checking topic '" + topicName + "'...");
		if (!this.cm.topicExists(topicID)) {
			logger.debug("Topic does not exist, creating...");
			this.cm.createTopic(topicID, topicVersion, typeID, typeVersion, topicName);
		} else {
			logger.debug("Topic already present.");
		}
		
		this.lastObject = LAST_TOPIC;
		this.lastID      = topicID;
		this.lastVersion = topicVersion;
	}
    
	private void ensureAssociationExists(Attributes attributes) {
		String assocID, assocName, assocTypeID, topic1ID, topic2ID;
		int assocVersion, assocTypeVersion, topic1Version, topic2Version;
		
		// parse XML attributes
		assocID          = attributes.getValue("id");
		assocName        = attributes.getValue("name");
		assocVersion     = getVersion(attributes, "version");
		assocTypeID      = attributes.getValue("type");
		assocTypeVersion = getVersion(attributes, "typeversion");
		topic1ID         = attributes.getValue("from");
		topic1Version    = getVersion(attributes, "fromversion");
		topic2ID         = attributes.getValue("to");
		topic2Version    = getVersion(attributes, "toversion");
		
		// perform action if necessary
		logger.debug("Checking association of type '" + assocTypeID + "' between '" + topic1ID + "' and '" + topic2ID + "'...");
		// TODO Check for existence and add association only if necessary. 
		logger.debug("Association does not exist, creating...");
		this.cm.createAssociation(assocID, assocVersion, assocTypeID, assocTypeVersion, topic1ID, topic1Version, topic2ID, topic2Version);
		if (assocName != null)
			this.cm.changeAssociationName(assocID, assocVersion, assocName);

		this.lastObject  = LAST_ASSOC;
		this.lastID      = assocID;
		this.lastVersion = assocVersion;
}
	
	private void ensureTopicmapExists(Attributes attributes) {
		String topicName, topicID, typeID;
		int topicVersion, typeVersion;
		
		// parse XML attributes
		topicName    = attributes.getValue("name");
		topicID      = attributes.getValue("id");
		topicVersion = getVersion(attributes, "version");
		typeID       = "tt-topicmap";
		typeVersion  = 1;
		
		// perform action if necessary
		logger.debug("Checking topic map '" + topicName + "'...");
		if (!this.cm.topicExists(topicID)) {
			logger.debug("Topic map does not exist, creating...");
			this.cm.createTopic(topicID, topicVersion, typeID, typeVersion, topicName);
		} else {
			logger.debug("Topic map already present.");
		}
		
		this.lastObject  = LAST_MAP;
		this.lastID      = topicID;
		this.lastVersion = topicVersion;
}
	
	private void ensureTopicProperty(Attributes attributes) {
		
		// parse XML attributes
		String property = attributes.getValue("name");
		String value    = attributes.getValue("value");
		
		// perform action if necessary
		logger.debug("Checking property '" + property + "' of topic '" + this.lastID + "'...");
    	Hashtable properties = this.cm.getTopicData(this.lastID, this.lastVersion);
    	if (properties.containsKey(property)) {
    		String oldvalue = (String) properties.get(property);
    		if (oldvalue.equals(value)) {
    			logger.debug("Property value is already set to '" + value + "'.");
    		} else {
    			logger.debug("Changing property value from '" + oldvalue + "' to '" + value + "'...");
    			properties.put(property, value);
    			this.cm.setTopicData(this.lastID, this.lastVersion, properties);
    		}
    	} else {
    		logger.debug("Property value is not yet set - setting to '" + value + "'...");
    		properties.put(property, value);
    		this.cm.setTopicData(this.lastID, this.lastVersion, properties);
    	}		
	}
	
	private void ensureAssociationProperty(Attributes attributes) {

		// parse XML attributes
		String property = attributes.getValue("name");
		String value    = attributes.getValue("value");
		
		// perform action if necessary
    	logger.debug("Checking property '" + property + "' of association '" + this.lastID + "'...");
    	Hashtable properties = this.cm.getAssociationData(this.lastID, this.lastVersion);
    	if (properties.containsKey(property)) {
    		String oldvalue = (String) properties.get(property);
    		if (oldvalue.equals(value)) {
    			logger.debug("Property value is already set to '" + value + "'.");
    		} else {
    			logger.debug("Changing property value from '" + oldvalue + "' to '" + value + "'...");
    			properties.put(property, value);
    			this.cm.setAssociationData(this.lastID, this.lastVersion, properties);
    		}
    	} else {
    		logger.debug("Property value is not yet set - setting to '" + value + "'...");
    		properties.put(property, value);
    		this.cm.setAssociationData(this.lastID, this.lastVersion, properties);
    	}
		
	}
	
	private void ensureTopicVisible(Attributes attributes) {
		String topicID;
		int topicVersion, x, y;
		
		// parse XML attributes
		topicID      = attributes.getValue("id");
		topicVersion = getVersion(attributes, "version");
		x            = Integer.parseInt(attributes.getValue("x"));
		y            = Integer.parseInt(attributes.getValue("y"));
		
		// perform action if necessary			
		logger.debug("Checking visibility of topic '" + topicID + "' in view '" + this.lastID + "'...");
		if (this.cm.viewTopicExists(this.lastID, this.lastVersion, null, topicID)) {
			logger.debug("Topic is already visible.");
			// TODO check and if necessary update position
		} else {
			logger.debug("Creating view topic...");
			this.cm.createViewTopic(this.lastID, this.lastVersion, null, topicID, topicVersion, x, y, false);
		}        

	}
	
	private void ensureAssociationVisible(Attributes attributes) {
		
		// parse XML attributes
		String assocID   = attributes.getValue("id");
		int assocVersion = getVersion(attributes, "version");

		// perform action if necessary
		logger.debug("Checking visibility of association '" + assocID + "' in view '" + this.lastID + "'...");
		if (this.cm.viewAssociationExists(this.lastID, this.lastVersion, null, assocID)) {
			logger.debug("Association is already visible.");
		} else {
			logger.debug("Creating view association...");
			this.cm.createViewAssociation(this.lastID, this.lastVersion, null, assocID, assocVersion, false);
		}        

	}
    
	public String[] getErrorMessage() {
		return (String[]) this.messages.toArray(new String [0]);
	}
	
	protected void addErrorMessage(String msg) {
		this.messages.add(msg);
	}
	
	protected void addErrorMessage(Throwable err) {
		Throwable curr = err;
		while (curr != null) {
			addErrorMessage(curr.toString());
			curr = curr.getCause();
		}
	}
	
	protected void addErrorMessage(String msg, Throwable err) {
		addErrorMessage(msg);
		addErrorMessage(err);
	}

}
