package de.deepamehta.environment.application;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.deepamehta.environment.ClassSpecification;
import de.deepamehta.environment.Environment;
import de.deepamehta.environment.EnvironmentFactory;

public class ApplicationSpecification {

	private static Log logger = LogFactory.getLog(ApplicationSpecification.class);
	
	private Environment env;
	
	private String id, description, sourcePath;
	private Vector implementations; // contains ClassSpecification instances pointing to the JARs to load
	private Vector dataFiles, contentFiles;
	
	public ApplicationSpecification() {
		env = EnvironmentFactory.getEnvironment();
		implementations = new Vector();
		dataFiles = new Vector();
		contentFiles = new Vector();
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
	public boolean addImplementation(ClassSpecification spec) {
		return implementations.add(spec);
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
				String source = element.getClassSource();
				if (!source.equals("core")) {
					if (!source.startsWith(env.getFileSeparator())) {
						// TODO How about Windoze?
						source = getSourcePath() + env.getFileSeparator() + source;
					}
					env.loadExternalJAR(new URL("file://" + source)); // TODO URL assembly?
				}
			} catch (MalformedURLException e) {
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

	/* (non-Javadoc)
	 * @see java.util.Vector#add(java.lang.Object)
	 */
	public boolean addContentFile(String filename) {
		return contentFiles.add(filename);
	}

	/* (non-Javadoc)
	 * @see java.util.Vector#get(int)
	 */
	public String getContentFile(int index) {
		return (String) contentFiles.get(index);
	}

	/* (non-Javadoc)
	 * @see java.util.AbstractList#iterator()
	 */
	public Iterator getContentFileIterator() {
		return contentFiles.iterator();
	}

	/* (non-Javadoc)
	 * @see java.util.Vector#size()
	 */
	public int numContentFiles() {
		return contentFiles.size();
	}

	/**
	 * @return Returns the sourcePath.
	 */
	public String getSourcePath() {
		return sourcePath;
	}

	/**
	 * @param sourcePath The sourcePath to set.
	 */
	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}
	
}
