/*
 * Created on 10.01.2006
 *
 * This file is part of the DeepaMehta framework.
 */
package de.deepamehta.environment.instance;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import de.deepamehta.environment.Environment;

/**
 * This class is responsible for managing the instance configuration data at runtime. Its
 * capabilites include loading and storing the instance configuration file.
 * @author vwegert
 */
public class InstanceManager {
    
    public static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    public static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	
    private static Log logger = LogFactory.getLog(InstanceManager.class);
    private Environment env;
    private Hashtable instances;
    private InstanceTableModel model = null;
    
    /**
     * The default constructor to create a new instance manager. 
     * @param parent The parent environment.
     */
    public InstanceManager(Environment parent) {
        super();
        this.env = parent;
        this.instances = new Hashtable();
        this.model = new InstanceTableModel(this);
    }

    /**
     * This method parses an instance configuration file and adds the instance configurations
     * to the current set.
     * @param filename The input file to read.
     */
    public void loadFromFile(String filename) {
        
        Vector fileContents;
        
        logger.debug("Trying to load instances from file " + filename + "...");

        Digester digester = new Digester();
        digester.setNamespaceAware(true);
        digester.setValidating(true);
        digester.setSchema(env.getHomeDirectory() + "/bin/schema/InstanceDefinitions.xsd"); // TODO Remove hard-coded path.
        
        digester.addObjectCreate("instances", Vector.class );
        digester.addObjectCreate("instances/instance", InstanceConfiguration.class);
        digester.addSetProperties("instances/instance/", "id", "id");
        digester.addSetProperties("instances/instance/", "description", "description");

//    	TODO do not attempt to parse CM configuration during client startup
//        if (this.env.getEnvironmentType() == EnvironmentType.FAT) {
        	digester.addCallMethod("instances/instance/monolithic", "setInstanceTypeMonolithic");
        	digester.addObjectCreate("instances/instance/monolithic/cm", CorporateMemoryConfiguration.class);
        	digester.addSetProperties("instances/instance/monolithic/cm/", "class", "implementingClassName");
        	digester.addCallMethod("instances/instance/monolithic/cm/property", "setProperty", 2);
        	digester.addCallParam("instances/instance/monolithic/cm/property", 0, "name");
        	digester.addCallParam("instances/instance/monolithic/cm/property", 1, "value");
        	digester.addSetNext("instances/instance/monolithic/cm", "setCMConfig");
        	
        	digester.addCallMethod("instances/instance/server", "setInstanceTypeServer");
        	digester.addSetProperties("instances/instance/server/", "interface", "serverInterface");
        	digester.addSetProperties("instances/instance/server/", "port", "serverPort");
        	digester.addObjectCreate("instances/instance/server/cm", CorporateMemoryConfiguration.class);
        	digester.addSetProperties("instances/instance/server/cm/", "class", "implementingClassName");
        	digester.addCallMethod("instances/instance/server/cm/property", "setProperty", 2);
        	digester.addCallParam("instances/instance/server/cm/property", 0, "name");
        	digester.addCallParam("instances/instance/server/cm/property", 1, "value");
        	digester.addSetNext("instances/instance/server/cm", "setCMConfig");
//        }

        digester.addCallMethod("instances/instance/client", "setInstanceTypeClient");
        digester.addSetProperties("instances/instance/client/", "host", "clientHost");
        digester.addSetProperties("instances/instance/client/", "port", "clientPort");
        
        digester.addSetNext("instances/instance", "add" );

        try {
            fileContents = (Vector) digester.parse(filename);
        } catch (IOException e) {
            logger.error("Unable to parse instance definition file " + filename + " because of I/O error: " + e.getLocalizedMessage());
            fileContents = null;
        } catch (SAXException e) {
            logger.error("Unable to parse instance definition file " + filename + " because of XML parser error.", e);
            fileContents = null;
        }
        
        if (fileContents != null) 
        {
            for (int i = 0; i < fileContents.size(); i++) {
                add((InstanceConfiguration) fileContents.get(i));
            }
        }
        
    }
    
    /**
     * This method stores the current set of configuration data into a file.
     * @param filename The name of the file to write.
     */
    public void saveToFile(String filename) {
    
        logger.debug("Saving instances to file " + filename);
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);
		factory.setNamespaceAware(true);
		factory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);

        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            logger.error("Unable to initialize XML output components.", e);
            return;
        }

        Document doc = builder.newDocument();

        // root element <dm:instances>
        Node root = doc.createElement("dmi:instances");
        ((Element) root).setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        ((Element) root).setAttribute("xsi:schemaLocation", "http://www.deepamehta.de/schema/InstanceDefinition.xsd InstanceDefinition.xsd");
        ((Element) root).setAttribute("xmlns:dmi", "http://www.deepamehta.de/schema/InstanceDefinition.xsd");
        doc.appendChild(root);
        
        // instance elements
        for (Enumeration e = instances.elements(); e.hasMoreElements();) {
            InstanceConfiguration spec = (InstanceConfiguration) e.nextElement();
            root.appendChild(spec.toNode(doc));
        }
        
        // Use a XSLT transformer for writing the new XML file 
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            DOMSource source = new DOMSource(doc);
            FileOutputStream os = new FileOutputStream(new File(filename));
            StreamResult result = new StreamResult( os );
            transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, result);
            logger.info("Wrote instance configuration to " + filename + ".");
        } catch (TransformerConfigurationException e1) {
            logger.error("Unable to initialize output XML transformer.", e1);
        } catch (FileNotFoundException e1) {
            logger.error("Unable to write to file " + filename + ".");
        } catch (TransformerFactoryConfigurationError e1) {
            logger.error("Unable to initialize output XML transformer.", e1);
        } catch (TransformerException e1) {
            logger.error("Unable to transform output file.", e1);
        }
         
        model.fireTableDataChanged();

    }
    
    /**
     * Add a new instance configuration to the current set. 
     * @param configuration The configuration to add.
     */
    public void add(InstanceConfiguration configuration) {
        if (!this.instances.containsKey(configuration.getId())) {
            logger.debug("Registering instance " + configuration.getId() + "...");
            this.instances.put(configuration.getId(), configuration);
        } else {
            logger.debug("Instance " + configuration.getId() + " already registered.");
        }
    }    
    
    /**
     * Retrieves an instance configuration from the current set. 
     * @param id The ID of the configuration
     * @return Returns the instance configuration.
     * @throws UnknownInstanceException This unchecked exception is thrown if the caller
     * tries to retrieve an unknown instance configuration
     */
    public InstanceConfiguration get(String id) throws UnknownInstanceException {
        if (this.instances.containsKey(id)) {
            return (InstanceConfiguration) this.instances.get(id);
        } else {
            throw new UnknownInstanceException(id);
        }
    }

    /**
     * Retrieves an instance configuration by its position as specified by the
     * table model.
     * @param pos
     * @return
     */
    public InstanceConfiguration get(int pos) {
    	try {
			return get((String)this.instances.keySet().toArray()[pos]);
		} catch (UnknownInstanceException e) {
			// This should never happen.
			logger.error("Failed to read an instance configuration specified in the key set.", e);
			return null;
		}
    }
 
    /**
     * @return Returns the number of instances in the current set.
     */
    public int size() {
        return this.instances.size();
    }

    /**
     * @return Returns the model.
     */
    public InstanceTableModel getModel() {
    	return this.model;
    }

	/**
	 * MISSDOC No documentation for method keys of type InstanceManager
	 * @return
	 * @see java.util.Hashtable#keys()
	 */
	public Enumeration keys() {
		return instances.keys();
	}
}
