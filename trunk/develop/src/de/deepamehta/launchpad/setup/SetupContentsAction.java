/*
 * Created on 25.01.2006
 *
 * This file is part of the DeepaMehta framework.
 */
package de.deepamehta.launchpad.setup;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

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
    
    private CorporateMemoryConfiguration cmConfig;

    /**
     * Default constructor.
     * @param config The instance configuration to populate.
     */
    public SetupContentsAction(InstanceConfiguration config) {
        try {
            this.cmConfig = config.getCMConfig();
        } catch (EnvironmentException e) {
            logger.error("Unable to get CM configuration.", e);
            this.cmConfig = null;
        }
    }

    /* (non-Javadoc)
     * @see de.deepamehta.launchpad.setup.SetupAction#getDescription()
     */
    public String getDescription() {
        // FIXME Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see de.deepamehta.launchpad.setup.SetupAction#canExecute()
     */
    public boolean canExecute() {

        // we need a CM configuration to continue
        if (this.cmConfig == null)
            return false;
        
        return true;
    }

    /* (non-Javadoc)
     * @see de.deepamehta.launchpad.setup.SetupAction#execute()
     */
    public boolean execute() {

        // create CM instance
        CorporateMemory cm = this.cmConfig.getInstance();
        if (cm == null) {
            logger.error("Unable to instantiate CM implementation.");
            return false;
        }

        // populate database
        if (cm.startup(this.cmConfig, true)) {
            logger.debug("Initializing Corporate Memory contents...");
            
            // get input data file 
            InputStream input = this.getClass().getClassLoader().getResourceAsStream("de/deepamehta/launchpad/setup/DefaultContents.xml");
            
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(true);
            factory.setNamespaceAware(true);
            
            try {
				SAXParser parser = factory.newSAXParser();
				parser.parse(input, this);
			} catch (ParserConfigurationException e) {
				logger.error("Unable to configure XML parser.", e);
				return false;
			} catch (SAXException e) {
				logger.error("Problem parsing XML file.", e);
				return false;
			} catch (IOException e) {
				logger.error("I/O error while parsing XML file.", e);
				return false;
			}
            
            
            
            
            // FIXME The bootstrapping process is still incomplete.
			// This can only be completed when the rest of the setup framework is in place.
          
            
            
            
//            !--   - -->
//            <!--   - Key Generator -->
//            <!--   - -->
//            <!--      1 -  500 Kernel -->
//            <!--    501 - 1000 included examples -->
//            <!--    600 -  799 Kompetenzstern -->
//            Line not recognized: INSERT INTO KeyGenerator VALUES ('Topic', 1001);
//            Line not recognized: INSERT INTO KeyGenerator VALUES ('Association', 1001);
//            <!--    Not a Key Generator but the (constant) version of the database model -->
//            Line not recognized: INSERT INTO KeyGenerator VALUES ('DB-Model Version', 2);
//            <!--    Not a Key Generator but the (constant) version of the database content -->
//            Line not recognized: INSERT INTO KeyGenerator VALUES ('DB-Content Version', 15);
//            
            
            cm.shutdown();
        } else {
            logger.error("Unable to startup Corporate Memory.");
            return false;
        }
        
        return true;
    }

}
