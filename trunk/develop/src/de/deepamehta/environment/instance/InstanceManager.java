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
    
    private static Log logger = LogFactory.getLog(InstanceManager.class);
    private Environment env;
    private Hashtable instances;
//    private InstanceTableModel model = null;
    
    /**
     * The default constructor to create a new instance manager. 
     */
    public InstanceManager() {
        super();
        this.env = Environment.getEnvironment();
        this.instances = new Hashtable();
//        model = new InstanceTableModel(this);
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
        digester.setValidating(false); 
        // FIXME Use the XML Schema to validate the input file.
        // FIXME Move Schema file into .jar.
        
        digester.addObjectCreate("instances", Vector.class );
        digester.addObjectCreate("instances/instance", InstanceConfiguration.class);
        digester.addSetProperties("instances/instance/", "id", "id");
        digester.addSetProperties("instances/instance/", "description", "description");
        
        digester.addCallMethod("instances/instance/monolithic", "setInstanceTypeMonolithic");
        digester.addObjectCreate("instances/instance/monolithic/cm", CorporateMemoryConfiguration.class);
        digester.addSetProperties("instances/instance/monolithic/cm/", "class", "implementingClass");
        digester.addCallMethod("instances/instance/monolithic/cm/property", "setProperty", 2);
        digester.addCallParam("instances/instance/monolithic/cm/property", 0, "name");
        digester.addCallParam("instances/instance/monolithic/cm/property", 1, "value");
        digester.addSetNext("instances/instance/monolithic/cm", "setCMConfig");

        digester.addCallMethod("instances/instance/server", "setInstanceTypeServer");
        digester.addSetProperties("instances/instance/server/", "interface", "serverInterface");
        digester.addSetProperties("instances/instance/server/", "port", "serverPort");
        digester.addObjectCreate("instances/instance/server/cm", CorporateMemoryConfiguration.class);
        digester.addSetProperties("instances/instance/server/cm/", "class", "implementingClass");
        digester.addCallMethod("instances/instance/server/cm/property", "setProperty", 2);
        digester.addCallParam("instances/instance/server/cm/property", 0, "name");
        digester.addCallParam("instances/instance/server/cm/property", 1, "value");
        digester.addSetNext("instances/instance/server/cm", "setCMConfig");

        digester.addCallMethod("instances/instance/client", "setInstanceTypeClient");
        digester.addSetProperties("instances/instance/client/", "host", "host");
        digester.addSetProperties("instances/instance/client/", "port", "port");
        
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
                addInstance((InstanceConfiguration) fileContents.get(i));
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
		factory.setAttribute(Environment.JAXP_SCHEMA_LANGUAGE, Environment.W3C_XML_SCHEMA);

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
         
    }
    
    /**
     * Add a new instance configuration to the current set. Note: for internal use only,
     * external components please use <code>add</code>. 
     * @param configuration The configuration to add.
     */
    private void addInstance(InstanceConfiguration configuration) {
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
     * Retrieves an instance configuration by its position.
     * @param pos
     * @return
     * @throws UnknownInstanceException 
     */
    public InstanceConfiguration get(int pos) throws UnknownInstanceException {
    	// FIXME using this method is dangerous - remove ASAP
    	return get((String)this.instances.keySet().toArray()[pos]);
    }
 
    /**
     * @return Returns the number of instances in the current set.
     */
    public int size() {
        return this.instances.size();
    }

    /**
     * Adds an instance configurat to the current set and saves the set to the configuration file. 
     * @param instance The instance configuration to add.
     */
    public void add(InstanceConfiguration instance) {
        addInstance(instance);
//        model.fireTableDataChanged();
        // TODO check for duplicate IDs before adding
        saveToFile(this.env.getInstanceFile());
        
    }

//    /**
//     * @return Returns the model.
//     */
//    public InstanceTableModel getModel() {
//    	return model;
//    }
}
