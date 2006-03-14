/*
 * Created on 25.01.2006
 *
 * This file is part of the DeepaMehta framework.
 */
package de.deepamehta.launchpad.setup;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.deepamehta.DeepaMehtaMessages;
import de.deepamehta.environment.Environment;
import de.deepamehta.environment.application.ApplicationSpecification;
import de.deepamehta.environment.instance.InstanceConfiguration;

/**
 * This class creates the new working directory and unzips the initial contents required for
 * a newly created DeepaMehta instance to run.
 * @author vwegert
 */
class InstallApplicationFilesAction extends AbstractSetupAction {

    private static Log logger = LogFactory.getLog(InstallApplicationFilesAction.class);
    
    private String workingDir;
    private ApplicationSpecification app;
    
    /**
     * Default constructor
     * @param config The instance configuration to use.
     */
    public InstallApplicationFilesAction(InstanceConfiguration config, ApplicationSpecification application) {
    	super(config);
    	this.workingDir = config.getWorkingDirectory();
    	this.app = application;
    }

    /* (non-Javadoc)
     * @see de.deepamehta.launchpad.setup.SetupAction#getDescription()
     */
    public String getDescription() {
    	return "Install delivered files into working directory of instance."; 
    }

    /* (non-Javadoc)
     * @see de.deepamehta.launchpad.setup.SetupAction#canExecute()
     */
    public boolean canExecute() {
		this.messages.clear();
        // TODO add some checks here
        return true;
    }

    /* (non-Javadoc)
     * @see de.deepamehta.launchpad.setup.SetupAction#execute()
     */
    public boolean execute() {

    	this.messages.clear();

    	for (Iterator iter = app.getDataFileIterator(); iter.hasNext();) {
			String filename = (String) iter.next();
			if (!filename.startsWith(env.getFileSeparator())) {
				// TODO What about Windoze?
				filename = app.getSourcePath() + env.getFileSeparator() + filename;
			}
			if (!extractZipFile(filename, this.workingDir))
				return false;
		}
        return true;
    }

}
