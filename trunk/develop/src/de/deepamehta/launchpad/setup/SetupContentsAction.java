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

// FIXME Problem with default instance data: 
//*** RelationalCorporateMemory.update(): java.sql.SQLException: Duplicate entry 'a-128-1' for key 1 -- INSERT, UPDATE or DELETE statement failed
//INSERT INTO Association (ID, Version, TypeID, TypeVersion, TopicID1, TopicVersion1, TopicID2, TopicVersion2) VALUES ('a-128', 1, 'at-derivation', 1, 'tt-topiccontainer', 1, 'tt-whoistopiccontainer', 1)


/**
 * This class is responsible for populating a new and empty Corporate Memory with the initial
 * contents required by the DeepaMehta core.
 * @author vwegert
 */
class SetupContentsAction extends AbstractSetupAction {

    private static Log logger = LogFactory.getLog(SetupStorageAction.class);
    
    private String workingDir = null;
    private CorporateMemoryConfiguration cmConfig;
    private CorporateMemory cm = null;
    
    /**
     * Default constructor.
     * @param config The instance configuration to populate.
     */
    public SetupContentsAction(InstanceConfiguration config) {
    	super(config);
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

        // startup CM in bootstrapping mode
        try {
        	this.cm.startup(this.cmConfig, true);
        } catch (DeepaMehtaException e) {
        	addErrorMessage("Unable to startup Corporate Memory.", e);
			this.cm = null;
			return false;
        }
            
        // load basic instance data from file 
        String filename = this.workingDir + this.env.getFileSeparator() + "DefaultContents.xml";
        // FIXME Is there any way to load the XML file directory from the zipped distribution?
        ContentsLoader loader = new ContentsLoader(this.cm);
        try {
			loader.loadFromFile(filename);
		} catch (ContentsLoaderException e) {
			addErrorMessage("Problem occurred while loading instance data.", e);
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

}
