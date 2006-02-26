package de.deepamehta.launchpad.setup;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.deepamehta.environment.Environment;
import de.deepamehta.environment.instance.InstanceConfiguration;

public abstract class AbstractSetupAction implements SetupAction {

    protected static final int BUFFER = 2048;  

	private static Log logger = LogFactory.getLog(AbstractSetupAction.class);
	
    protected Environment env;
    protected InstanceConfiguration config;
    protected ArrayList messages;

	/**
	 * Default constructor
	 * @param conf
	 */
	public AbstractSetupAction(InstanceConfiguration conf) {
		this.env = Environment.getEnvironment();
		this.config = conf;
		this.messages = new ArrayList();
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

	/**
	 * Extracts the contents of a ZIP file into a target directory
	 * @param sourceFile The file to extract
	 * @param targetPath The target path to extract the files into. Note that the path needs a trailing separator!
	 * @return
	 */
	protected boolean extractZipFile(String sourceFile, String targetPath) {
		
        BufferedOutputStream dest;
        BufferedInputStream is;
        ZipEntry entry;
                
        try {
            ZipFile zipfile = new ZipFile(sourceFile);
            
            // first pass: create directories
            Enumeration ed = zipfile.entries();
            while (ed.hasMoreElements()) {
                entry = (ZipEntry) ed.nextElement();
                if (entry.isDirectory()) {
                    logger.debug("Creating directory " + targetPath + entry.getName());
                    File dir = new File(targetPath + entry.getName());
                    dir.mkdir();
                }
            }
            
            // second pass: extract files
            Enumeration ef = zipfile.entries();
            while (ef.hasMoreElements()) {
                entry = (ZipEntry) ef.nextElement();
                if (!entry.isDirectory()) {
                    logger.debug("Extracting file " + targetPath + entry.getName());
                    try {
                        is = new BufferedInputStream(zipfile.getInputStream(entry));
                        int count;
                        byte data[] = new byte[BUFFER];
                        FileOutputStream fos = new FileOutputStream(targetPath + entry.getName());
                        dest = new BufferedOutputStream(fos, BUFFER);
                        while ((count = is.read(data, 0, BUFFER)) != -1) {
                            dest.write(data, 0, count);
                        }
                        dest.flush();
                        dest.close();
                        is.close();
                    } catch (Exception e) {
                        addErrorMessage("Unable to extract file " + targetPath + entry.getName(), e);
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            addErrorMessage("Unable to extract contents of  " + sourceFile);
            return false;
        }
        return true;
	}
	
}
