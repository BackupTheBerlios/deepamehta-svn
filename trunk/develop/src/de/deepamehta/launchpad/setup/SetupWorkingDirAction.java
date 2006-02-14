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

    static final int BUFFER = 2048;  
    
    private static Log logger = LogFactory.getLog(SetupWorkingDirAction.class);
    
    private String workingDir;
    private File target;
    
    /**
     * Default constructor
     * @param config The instance configuration to use.
     */
    public SetupWorkingDirAction(InstanceConfiguration config) {
    	super(config);
    	this.env = Environment.getEnvironment();
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
                String zipFileName = this.env.getInstanceDataSourceFile(); 
                BufferedOutputStream dest = null;
                BufferedInputStream is = null;
                ZipEntry entry;
                
                try {
                    ZipFile zipfile = new ZipFile(zipFileName);
                    
                    // first pass: create directories
                    Enumeration ed = zipfile.entries();
                    while (ed.hasMoreElements()) {
                        entry = (ZipEntry) ed.nextElement();
                        if (entry.isDirectory()) {
                            logger.debug("Creating directory " + this.workingDir + entry.getName());
                            File dir = new File(this.workingDir + entry.getName());
                            dir.mkdir();
                        }
                    }
                    
                    // second pass: extract files
                    Enumeration ef = zipfile.entries();
                    while (ef.hasMoreElements()) {
                        entry = (ZipEntry) ef.nextElement();
                        if (!entry.isDirectory()) {
                            logger.debug("Extracting file " + this.workingDir
                                    + entry.getName());
                            try {
                                is = new BufferedInputStream(zipfile.getInputStream(entry));
                                int count;
                                byte data[] = new byte[BUFFER];
                                FileOutputStream fos = new FileOutputStream(this.workingDir + entry.getName());
                                dest = new BufferedOutputStream(fos, BUFFER);
                                while ((count = is.read(data, 0, BUFFER)) != -1) {
                                    dest.write(data, 0, count);
                                }
                                dest.flush();
                                dest.close();
                                is.close();
                            } catch (Exception e) {
                                addErrorMessage("Unable to extract file " + this.workingDir + entry.getName(), e);
                                return false;
                            }
                        }
                    }
                } catch (Exception e) {
                    addErrorMessage("Unable to extract instance data from " + zipFileName);
                    return false;
                }
            }
        }
        
        return true;
    }

}
