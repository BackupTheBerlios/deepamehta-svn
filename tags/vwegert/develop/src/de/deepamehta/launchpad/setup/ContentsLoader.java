package de.deepamehta.launchpad.setup;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.deepamehta.service.CorporateMemory;

public class ContentsLoader extends DefaultHandler {

	private static final int LAST_TOPIC = 1;
    private static final int LAST_ASSOC = 2;
    private static final int LAST_MAP   = 3;
    
    private static Log logger = LogFactory.getLog(ContentsLoader.class);
	
    private CorporateMemory cm;
    private int lastObject;
    private String lastID;
    private int lastVersion;
	
	public ContentsLoader(CorporateMemory target) {
		this.cm = target;
	}
	
	public void loadFromFile(String filename) throws ContentsLoaderException {
		
		this.lastObject  = 0;
		this.lastID      = "";
		this.lastVersion = 1;
		
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
        	throw new ContentsLoaderException("Unable to configure XML parser.", e);
        } catch (SAXException e) {
        	throw new ContentsLoaderException("Problem parsing XML file.", e);
        } catch (IOException e) {
        	throw new ContentsLoaderException("I/O error while parsing XML file.", e);
        }
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
	
	private void setCMVersions(Attributes attributes) {
		
		String strModel = attributes.getValue("modelVersion");
		String strContent = attributes.getValue("contentVersion");
		
		if (strModel != null && strContent != null) { 
			int modelVersion = Integer.parseInt(strModel);
			int contentVersion = Integer.parseInt(strContent);
			this.cm.setKeyGenerator("DB-Model Version", modelVersion);
			this.cm.setKeyGenerator("DB-Content Version", contentVersion);
		}	
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
	

}
