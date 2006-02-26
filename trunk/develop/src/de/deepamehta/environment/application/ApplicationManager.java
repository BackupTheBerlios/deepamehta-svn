package de.deepamehta.environment.application;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import de.deepamehta.environment.ClassSpecification;
import de.deepamehta.environment.Environment;
import de.deepamehta.environment.EnvironmentException;
import de.deepamehta.environment.instance.InstanceManager;

public class ApplicationManager implements ListModel {

    private static Log logger = LogFactory.getLog(InstanceManager.class);
    private Environment env;
    private Hashtable applications;

	public ApplicationManager(Environment parent) {
		super();
		this.env = parent;
		this.applications = new Hashtable();
	}

	public void scanApplicationPath(String appPath) throws EnvironmentException {
		
		logger.debug("Loading applications from " + appPath + "...");
		
		// ensure that appPath points to a directory
		File appRoot = new File(appPath);
		if (!appRoot.isDirectory())
			throw new EnvironmentException(appPath + " is not a directory.");
		
		// scan the path for subdirectories
		String[] subdirs = appRoot.list();
		for (int i = 0; i < subdirs.length; i++) {
			File appDir = new File(appPath + env.getFileSeparator() + subdirs[i]);
			if (appDir.isDirectory()) {
				// check whether this directory contains a file named application.xml
				File specFile = new File(appPath + env.getFileSeparator() + subdirs[i] + 
										           env.getFileSeparator() + "application.xml");
				if(specFile.exists()) 
					loadSpecification(specFile);
			}				
		}
	}

	private void loadSpecification(File specFile) {

        Vector fileContents;
        String filename = specFile.getAbsolutePath();
        
		logger.debug("Loading application specified by " + filename);

        Digester digester = new Digester();
        digester.setNamespaceAware(true);
        //digester.setValidating(true);
        // FIXME Re-enable XML validation
        
        digester.addObjectCreate("applications", Vector.class );
        digester.addObjectCreate("applications/application", ApplicationSpecification.class);
        digester.addSetProperties("applications/application/", "id", "id");
        digester.addSetProperties("applications/application/", "description", "description");

        digester.addObjectCreate("applications/application/implementation", ClassSpecification.class );
        digester.addSetProperties("applications/application/implementation", "class", "className" );
        digester.addSetProperties("applications/application/implementation", "loadFrom", "classSource" );
        digester.addSetNext("applications/application/implementation", "addImplementation" );

        digester.addCallMethod("applications/application/datafiles", "addDataFile", 1);
        digester.addCallParam("applications/application/datafiles", 0, "source");
        
        // FIXME add absolute path prefix to source files
        
        digester.addSetNext("applications/application", "add" );

        try {
            fileContents = (Vector) digester.parse(specFile);
        } catch (IOException e) {
            logger.error("Unable to parse application specification file " + filename + " because of I/O error: " + e.getLocalizedMessage());
            fileContents = null;
        } catch (SAXException e) {
            logger.error("Unable to parse instance definition file " + filename + " because of XML parser error.", e);
            fileContents = null;
        }
        
        if (fileContents != null) 
        {
            for (int i = 0; i < fileContents.size(); i++) {
            	ApplicationSpecification spec = (ApplicationSpecification) fileContents.get(i);
            	for (Iterator iter = spec.getImplementationIterator(); iter.hasNext();) {
					ClassSpecification element = (ClassSpecification) iter.next();
					String newSource = specFile.getParentFile().getAbsolutePath() + env.getFileSeparator() + element.getClassSource(); 
					element.setClassSource(newSource);
					// FIXME Ugly workaround to get an absolute path - make this more elegant somehow.
				}
                applications.put(spec.getId(), spec);
            }
        }
	}

	/* (non-Javadoc)
	 * @see java.util.Hashtable#containsKey(java.lang.Object)
	 */
	public boolean isApplicationPresent(Object key) {
		return applications.containsKey(key);
	}

	/* (non-Javadoc)
	 * @see java.util.Hashtable#elements()
	 */
	public Enumeration getApplications() {
		return applications.elements();
	}

	/* (non-Javadoc)
	 * @see java.util.Hashtable#get(java.lang.Object)
	 */
	public ApplicationSpecification getApplication(String id) {
		return (ApplicationSpecification) applications.get(id);
	}

	/* (non-Javadoc)
	 * @see java.util.Hashtable#isEmpty()
	 */
	public boolean isEmpty() {
		return applications.isEmpty();
	}

	/* (non-Javadoc)
	 * @see java.util.Hashtable#keys()
	 */
	public Enumeration getIDs() {
		return applications.keys();
	}

	/* (non-Javadoc)
	 * @see java.util.Hashtable#size()
	 */
	public int getSize() {
		return applications.size();
	}

	public Object getElementAt(int index) {
    	return this.applications.keySet().toArray()[index];
	}

	public void addListDataListener(ListDataListener l) {
		// TODO Auto-generated method stub
		
	}

	public void removeListDataListener(ListDataListener l) {
		// TODO Auto-generated method stub
		
	}

}
