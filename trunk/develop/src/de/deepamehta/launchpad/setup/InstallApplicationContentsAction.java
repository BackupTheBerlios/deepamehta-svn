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
import java.util.Iterator;

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
import de.deepamehta.environment.application.ApplicationSpecification;
import de.deepamehta.environment.instance.CorporateMemoryConfiguration;
import de.deepamehta.environment.instance.InstanceConfiguration;
import de.deepamehta.service.CorporateMemory;

/**
 * This class is responsible for importing the delivered contents of an application.
 * @author vwegert
 */
class InstallApplicationContentsAction extends AbstractSetupAction {

    private static Log logger = LogFactory.getLog(SetupStorageAction.class);
    
    private String workingDir = null;
    private CorporateMemoryConfiguration cmConfig;
    private ApplicationSpecification app;
    private CorporateMemory cm = null;
    
    /**
     * Default constructor.
     * @param config The instance configuration to populate.
     */
    public InstallApplicationContentsAction(InstanceConfiguration config, ApplicationSpecification application) {
    	super(config);
		try {
			this.app = application;
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
        return "Import the delivered contents into the application";
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
            
        // import contents
        for (Iterator iter = app.getContentFileIterator(); iter.hasNext();) {
			String filename = (String) iter.next();
			if (!filename.startsWith(Environment.getFileSeparator())) {
				// TODO What about Windoze?
				filename = this.workingDir + Environment.getFileSeparator() + filename;
			}
			ContentsLoader loader = new ContentsLoader(this.cm);
			try {
				loader.loadFromFile(filename);
			} catch (ContentsLoaderException e) {
				addErrorMessage("Problem occurred while importing contents.", e);
				this.cm.shutdown();
				this.cm = null;
				return false;
			}
		}

        this.cm.shutdown();
        this.cm = null;
        
        return true;
    }

}
