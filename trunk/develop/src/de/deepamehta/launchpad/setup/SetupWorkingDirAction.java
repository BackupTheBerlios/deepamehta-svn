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
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.deepamehta.DeepaMehtaMessages;
import de.deepamehta.environment.Environment;
import de.deepamehta.environment.instance.InstanceConfiguration;

/**
 * This class creates the new working directory and unzips the initial contents required for
 * a newly created DeepaMehta instance to run.
 * @author vwegert
 */
class SetupWorkingDirAction extends AbstractSetupAction {

    private static Log logger = LogFactory.getLog(SetupWorkingDirAction.class);
    
    private String workingDir;
    private File target;
    
    /**
     * Default constructor
     * @param config The instance configuration to use.
     */
    public SetupWorkingDirAction(InstanceConfiguration config) {
    	super(config);
    	this.workingDir = config.getWorkingDirectory();
        this.target = new File(this.workingDir);
    }

    /* (non-Javadoc)
     * @see de.deepamehta.launchpad.setup.SetupAction#getDescription()
     */
    public String getDescription() {
    	return DeepaMehtaMessages.getString("SetupWorkingDirAction.SetupDescription"); //$NON-NLS-1$
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

        // Create new working directory in instances/ and extract files from ZIP archive.
        logger.debug("Trying to create new working directory " + this.workingDir + " for instance.");
        if (this.target.exists()) {
            logger.warn("Working directory " + this.workingDir + " already exists - won't touch it!");
            return true;
        } else {
            if (!this.target.mkdir()) {
                addErrorMessage("Unable to create working directory " + this.workingDir);
                return false;
            } else {
            	return extractZipFile(env.getHomeDirectory() + env.getFileSeparator() + "bin" 
                		+ env.getFileSeparator() + "instance-data.zip", this.workingDir);
            	// TODO Remove hard-coded ZIP file path.
            }
        }
    }

}
