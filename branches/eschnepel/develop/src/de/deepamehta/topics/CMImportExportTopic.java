package de.deepamehta.topics;

import de.deepamehta.Association;
import de.deepamehta.BaseTopic;
import de.deepamehta.BaseAssociation;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.FileServer;
import de.deepamehta.service.Session;
import de.deepamehta.service.CorporateCommands;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.topics.helper.TopicMapExporter;
import de.deepamehta.topics.helper.ArchiveFileCollector;
//
import com.ibm.xml.parsers.NonValidatingDOMParser;
//
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
//
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
//
import java.util.zip.*;
import java.util.*;
import java.io.*;



/**
 * This active topic class provides Corporate Memory Import/Export functionality. 
 * <P>
 * The <I>context-commands</I> (about to appear in the topic context menu) provided by a
 * <CODE>CMImportExportTopic</CODE> are <I>exporting</I> resp. <I>importing</I>
 * of whole corporate memory to resp. from an archive file. 
 * <P>
 * The exporting of corporate memory on the server side is provided in the method
 * {@link #executeCommand executeCommand()}. 
 * The archive file is created in method {@link #exportCM exportCM()} and downloaded 
 * by the client. After that the user is informed by message.
 * <P>
 * The importing of corporate memory on the server side is divided into three steps:
 * <P><OL>
 * <LI>sending of directive for choosing archive file name 
 * ({@link #executeCommand executeCommand()})</LI>
 * <LI>sending of directives for copying of archive file to client's document repository 
 *     and upload to the server 
 * ({@link #executeChainedCommand executeChainedCommand()})</LI>
 * <LI>importing from archive file stored in server's document repository 
 *     ({@link #importFromFile importFromFile()})</LI>
 * </OL>
 * <HR>
 * Last functional change: 11.9.2007 (2.0b8)<BR>
 * Last documentation update: 13.4.2002 (2.0a14-post1)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class CMImportExportTopic extends LiveTopic {



	// *******************
	// *** Constructor ***
	// *******************



	/**
	 * Empty contructor, only calls ancestor contructor.
	 */
	public CMImportExportTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}



	// **********************
	// *** Defining Hooks ***
	// **********************



	// --------------------------
	// --- Providing Commands ---
	// --------------------------



	/**
	 * Adds context menu items for importing and exporting of corporate memory.
	 *
	 * @see		de.deepamehta.service.ApplicationService#getTopicCommands
	 */
	public CorporateCommands contextCommands(String topicmapID, String viewmode,
								Session session, CorporateDirectives directives) {
		CorporateCommands commands = new CorporateCommands(as);
		// --- export/import ---
		commands.addCommand(as.string(ITEM_EXPORT_CM), CMD_EXPORT_CM);
		commands.addCommand(as.string(ITEM_IMPORT_CM), CMD_IMPORT_CM);
		// --- generic topic commands ---
		commands.add(super.contextCommands(topicmapID, viewmode, session, directives));
		//
		return commands;
	}



	// --------------------------
	// --- Executing Commands ---
	// --------------------------



	/**
	 * @see		de.deepamehta.service.ApplicationService#performTopicAction
	 */
	public CorporateDirectives executeCommand(String command, Session session,
													String topicmapID, String viewmode) {
		CorporateDirectives directives = new CorporateDirectives();
		if (command.equals(CMD_EXPORT_CM)) {
			try {
				// export corporate memory
				File file = createArchiveFile();
				// let client download the export file
				String filename = file.getName();
				Long lastModified = new Long(file.lastModified());
				directives.add(DIRECTIVE_DOWNLOAD_FILE, filename, lastModified,
					new Integer(FILE_DOCUMENT));
				// notify user
				directives.add(DIRECTIVE_SHOW_MESSAGE, "Corporate memory has been exported " +
					"to file \"" + filename + "\" (in your local document repository)",
					new Integer(NOTIFICATION_DEFAULT));
			} catch	(IOException e) {
				throw new DeepaMehtaException("I/O error while exporting corporate memory: " +
					e.getMessage());
			}
		} else if (command.equals(CMD_IMPORT_CM)) {
			System.out.println(">>> Importing CM...");
			directives.add(DIRECTIVE_CHOOSE_FILE);
		} else {
			directives = super.executeCommand(command, session, topicmapID, viewmode);
		}
		return directives;
	}
	
	/**
	 * ### Processes chained importing command. The method sends directive 
	 * {@link de.deepamehta.DeepaMehtaConstants#DIRECTIVE_COPY_FILE 
	 * DIRECTIVE_COPY_FILE} and if the server runs on different machine also 
	 * {@link de.deepamehta.DeepaMehtaConstants#DIRECTIVE_UPLOAD_FILE 
	 * DIRECTIVE_UPLOAD_FILE} is sent. The continuing of processing after 
	 * file transfers is ensured by sending 
	 * {@link de.deepamehta.DeepaMehtaConstants#DIRECTIVE_QUEUE_MESSAGE 
	 * DIRECTIVE_QUEUE_MESSAGE}.
	 *
	 * @see		de.deepamehta.service.ApplicationService#performTopicAction
	 */
	public CorporateDirectives executeChainedCommand(String command, String result, String topicmapID,
																	String viewmode, Session session) {
		StringTokenizer st = new StringTokenizer(command, COMMAND_SEPARATOR);		// ###
		String cmd = st.nextToken();
		//
		if (cmd.equals(CMD_IMPORT_CM)) {
			// ### no way to pass the real geometry
			return importCM(result, session);
		} else {
			return super.executeChainedCommand(command, result, topicmapID, viewmode, session);
		}
	}



	// *********************
	// *** Static Method ***
	// *********************



	/**
	 * Imports corporate memory from archive file stored in server's document repository.
	 * <P>
	 * This method consists of these parts: 
	 * <UL>
	 * <LI>declassifying of archive entry</LI>
	 * <LI>parsing XML 
	 * <LI>importing of topics</LI>
	 * <LI>importing of associations</LI>
	 * </UL><P>
	 * Importing of single topic is done in following steps:
	 * <OL>
	 * <LI>getting topic attributes from XML file</LI>
	 * <LI>founding topic with the same name and type in corporate memory</LI>
	 *   <UL>
	 *   <LI>if found, existing topic id is retrieved</LI>
	 *   <LI>if not found, the new topic id is created according to type of the topic</LI>
	 *   </UL>
	 * <LI>Both ids, old stored in archive and existing or new from corporate memory are 
	 *     stored in <CODE>topicImportMap</CODE></LI>
	 * </OL>
	 *
	 * @param   file  	input archive file
	 * @param   as  	
	 * @param   session  
	 *
	 * @throws	DeepaMehtaException  when import process was interupted
	 *
	 * @see		de.deepamehta.service.ApplicationService#importCM
	 */
	/* ### public static CorporateDirectives importFromFile(File file, ApplicationService as,
												Session session) throws DeepaMehtaException {
	 	System.out.println(">>> CMImportExportTopic.importFromFile(): " + file);
	 	CorporateDirectives directives = new CorporateDirectives();
			ZipFile zfile = new ZipFile(file);
			Enumeration entries = zfile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) entries.nextElement();
				String filename = entry.getName();
				InputStream in = zfile.getInputStream(entry);
				if (filename.indexOf("/") != -1) {
					// ### importFile(in, filename);
				} else if (filename.endsWith(".xml")) {
					doc = getParsedDocument(in);
					importDocument(doc);
				}
				in.close();
			}
	 		if (LOG_IMPORT_EXPORT) {
	 		    System.out.println("> CMImportExport.importFromFile(): Importing CM " +
					"from the file: " + file.getName());
	 		}
			directives.add(DIRECTIVE_SHOW_MESSAGE, "The Corporate Memory " +
				"was successfully updated from the archive file \"" + file.getName() + 
				"\"", new Integer(NOTIFICATION_DEFAULT));
	 	return directives;
	} */



	// **********************
	// *** Private Method ***
	// **********************
	


	// --------------
	// --- Import ---
	// --------------



	private CorporateDirectives importCM(String path, Session session) {
		// ### compare to TopicMapTopic.doImport()
		// ### compare to LiveTopic.copyAndUpload()
		CorporateDirectives directives = new CorporateDirectives();
		// --- copy and upload ---
		String filename = copyAndUpload(path, FILE_DOCUMENT, session, directives);
		// --- begin import ---
		if (filename != null) {
			directives.add(DIRECTIVE_QUEUE_MESSAGE, "importCM:" + filename);
		}
		//
		return directives;
	}



	// --------------
	// --- Export ---
	// --------------



	/**
	 * Exports corporate memory to a ZIP archive file. The archive file name is  
	 * <CODE>corporate-memory.zip</CODE>.
	 * <P> 
	 * For constructing of XML code for topics and association are used static methods
	 * {@link TopicMapTopic#makeTopicsXML(Enumeration e, StringBuffer out, 
	 * ApplicationService as) TopicMapTopic.makeTopicsXML()}
	 * and 
	 * {@link TopicMapTopic#makeAssociationsXML TopicMapTopic.makeAssociationsXML()}
	 * The XML file is written to ZIP stream throw OutputStreamWriter with UTF-8 encoding
	 * for correct saving of regional characters. 
	 *
	 * @return  reference to the <CODE>File</CODE> object stored in the file system 
	 * 			of the server
	 * @throws  IOException  if the archive file cannot be written
	 *
	 * @see		#executeCommand
	 */
	private File createArchiveFile() throws IOException {
		// ### compare to TopicMapExporter.createTopicmapArchive()
		// create file reference for topicmap archive file
		String filename = "corporate-memory.zip";	// ###
		File archiveFile = new File(FILESERVER_DOCUMENTS_PATH + filename);
		// possibly create corporate document repository
		FileServer.createDirectory(archiveFile);
		// create corporate memory archive file
		ZipOutputStream zipout = new ZipOutputStream(new FileOutputStream(archiveFile));
		zipout.putNextEntry(new ZipEntry("corporate-memory.xml"));
		// create handler and file set
		ContentHandler handler = new XMLSerializer(zipout, new OutputFormat("xml", "utf-8", true));
		ArchiveFileCollector collector = new ArchiveFileCollector();
		try {
			handler.startDocument();
			exportCM(handler, collector);
			handler.endDocument();
			collector.export(zipout);
		} catch (SAXException e) {
			System.out.println("*** CMImportExportTopic.createArchiveFile(): " + e);
		}
		//
		zipout.close();
		return archiveFile;		
	}	

	private void exportCM(ContentHandler handler, ArchiveFileCollector collector) throws SAXException, IOException {
		// ### compare to TopicMapTopic.exportTopicmap()
		Enumeration topics = cm.getTopics().elements();
		Enumeration assocs = cm.getAssociations().elements();
		TopicMapExporter.startElement(handler, "topicmap", null);	// attribs=null
		// export topics and assocs, visible=false
		System.out.println(">>> exporting topics ...");
		TopicMapExporter.exportTopics(topics, handler, false, as, collector);
		System.out.println(">>> exporting associations ...");
		TopicMapExporter.exportAssociations(assocs, handler, false, as, collector);
		// export type definitions
		System.out.println(">>> exporting topic types ...");
		TopicMapExporter.exportTypes(cm.getTopicTypes().elements(), handler, collector, as);
		System.out.println(">>> exporting association types ...");
		TopicMapExporter.exportTypes(cm.getAssociationTypes().elements(), handler, collector, as);
		// finish the export of this viewmode
		TopicMapExporter.endElement(handler, "topicmap");
		//
		System.out.println(">>> export complete");
	}
}
