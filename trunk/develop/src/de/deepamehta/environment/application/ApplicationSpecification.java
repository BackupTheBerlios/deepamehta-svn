package de.deepamehta.environment.application;

import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.deepamehta.environment.ClassSpecification;
import de.deepamehta.environment.Environment;

public class ApplicationSpecification {

	private static Log logger = LogFactory.getLog(ApplicationSpecification.class);
	
	private String id, description;
	private Vector implementations; // contains ClassSpecification instances pointing to the JARs to load
	private Vector dataFiles;
	
	// FIXME handle installation data, too
	
	public ApplicationSpecification() {
		implementations = new Vector();
		dataFiles = new Vector();
	}

	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return Returns the id.
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id The id to set.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see java.util.Vector#add(java.lang.Object)
	 */
	public boolean addImplementation(ClassSpecification f) {
		return implementations.add(f);
	}

	/* (non-Javadoc)
	 * @see java.util.Vector#get(int)
	 */
	public ClassSpecification getImplementation(int index) {
		return (ClassSpecification) implementations.get(index);
	}

	/* (non-Javadoc)
	 * @see java.util.AbstractList#iterator()
	 */
	public Iterator getImplementationIterator() {
		return implementations.iterator();
	}

	/* (non-Javadoc)
	 * @see java.util.Vector#size()
	 */
	public int numImplementations() {
		return implementations.size();
	}

	public void loadImplementations() {
		
		// load all JARs
		for (Iterator iter = implementations.iterator(); iter.hasNext();) {
			ClassSpecification element = (ClassSpecification) iter.next();
			try {
				Environment.getEnvironment().loadExternalJAR(element.getClassSource());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				logger.error("Unable to load JAR " + element.getClassSource(), e);
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Vector#add(java.lang.Object)
	 */
	public boolean addDataFile(String filename) {
		return dataFiles.add(filename);
	}

	/* (non-Javadoc)
	 * @see java.util.Vector#get(int)
	 */
	public String getDataFile(int index) {
		return (String) dataFiles.get(index);
	}

	/* (non-Javadoc)
	 * @see java.util.AbstractList#iterator()
	 */
	public Iterator getDataFileIterator() {
		return dataFiles.iterator();
	}

	/* (non-Javadoc)
	 * @see java.util.Vector#size()
	 */
	public int numDataFiles() {
		return dataFiles.size();
	}
	
}
