package de.deepamehta.topics.helper;

import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.FileServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;



/**
 * ### to be dropped, functionality will move to TopicMapExporter
 * <p>
 * Helper class for XML export of topicmaps. This class collects all Icons and Documents
 * which belong to the exported topicmap. During export, one instance of this class
 * is created and passed to all relevant export routines which call methods
 * {@link #putIcon} and {@link #putDocument}.
 */
public class ArchiveFileCollector implements DeepaMehtaConstants {



	// **************
	// *** Fields ***
	// **************



	/**
	 * These containers collect the documents and icons to be exported
	 */
	Hashtable documents;
	Hashtable icons;



	// *******************
	// *** Constructor ***
	// *******************



	/**
	 * @see     de.deepamehta.topics.helper.TopicMapExporter#createTopicmapArchive
	 */
	public ArchiveFileCollector() {
		documents = new Hashtable();
		icons = new Hashtable();
	}



	// **********************
	// *** Public Methods ***
	// **********************



	/**
	 * Saves the reference to one iconfile which is to be exported.
	 *
	 * @param   iconName  the name of the icon file to be exported
	 *
	 * @see     TopicMapExporter#exportTopic
	 * @see     de.deepamehta.topics.TopicTypeTopic#exportTypeDefinition
	 * @see     de.deepamehta.topics.TypeTopic#exportPropertyDefinitions
	 */
	public void putIcon(String iconName) {
		if (iconName != null && ! iconName.equals("")) {
			// ### System.out.println(">>> ArchiveFileCollector.putIcon(\"" + iconName + "\")");
			icons.put(iconName, new File(FILESERVER_ICONS_PATH + iconName));
		}
	}

	/**
	 * Saves the reference to one document which is to be exported.
	 *
	 * @param   docName  the name of the document file to be exported
	 *
	 * @see     TopicMapExporter#exportTopic
	 */
	public void putDocument(String docName) {
		if (docName != null && ! docName.equals("")) {
			// ### System.out.println(">>> ArchiveFileCollector.putDocument(\"" + docName + "\")");
			documents.put(docName, new File(FileServer.repositoryPath(FILE_DOCUMENT) + docName));
		}
	}

	/**
	 * Exports the collected icons and documents to the ZIP archive.
	 *
	 * @param   zout    the ZipOutputStream into the ZIP archive
	 *
	 * @see     TopicMapExporter#createTopicmapArchive
	 */
	public void export(ZipOutputStream zout) {
		try {
			exportFiles(documents, zout);
			exportFiles(icons, zout);
		} catch(IOException e) {
			System.out.println("### ArchiveFileCollector.export: " + e);
		}
	}



	// **********************
	// *** Private Method ***
	// **********************



	/**
	 * Exports either documents or icons.
	 *
	 * @param   hash    one of the two Hashtables containing either documents or icons
	 * @param   zout    the ZipOutputStream into the ZIP archive
	 *
	 * @see     #export
	 */
	private void exportFiles(Hashtable hash, ZipOutputStream zout) throws IOException {
		byte[] buffer = new byte[4096];
		int bytes_read;
		Enumeration files = hash.elements();
		while (files.hasMoreElements()) {
			File file = (File) files.nextElement();
			try {
				FileInputStream in = new FileInputStream(file);
				ZipEntry entry = new ZipEntry(file.getPath());
				zout.putNextEntry(entry);
				// ### System.out.println(">>> ArchiveFileCollector.exportFile(\"" + file.getName() + "\")");
				// ### use file utils?
				while ((bytes_read = in.read(buffer)) != -1) {
					zout.write(buffer, 0, bytes_read);
				}
				in.close();
			} catch(FileNotFoundException e) {
				System.out.println("### ArchiveFileCollector.exportFiles: File "
					+ file.getName() + " not found!");
			}
		}
	}
}
