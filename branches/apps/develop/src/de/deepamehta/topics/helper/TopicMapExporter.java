package de.deepamehta.topics.helper;

import de.deepamehta.BaseAssociation;
import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.FileServer;
import de.deepamehta.PresentableTopic;
import de.deepamehta.Type;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.topics.TopicMapTopic;
import de.deepamehta.util.DeepaMehtaUtils;

import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.Logger;
import org.apache.fop.apps.Driver;
import org.apache.fop.messaging.MessageHandler;
import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;

import java.awt.Point;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;



/**
 * Export utilities for the following purposes:
 * <ul>
 * <li>A topicmap can be exported to XML, SVG or PDF.
 * <li>
 * </ul>
 * <hr>
 * Last change: 26.1.2009 (2.0b9)<br>
 * J&ouml;rg Richter<br>
 * jri@deepamehta.de
 */
public class TopicMapExporter implements DeepaMehtaConstants {

	/**
	 * XML parser to turn the HTML-formatted content of styled properties into SAX events.
	 */
	private static SAXParser parser = new SAXParser();

	/**
	 * The topicmap to be exported.
	 *
	 * ### to remove this instance variable and pass it as
	 *     parameter to the public methods to make them static?
	 */
	private TopicMapTopic topicmap;



	// *******************
	// *** Constructor ***
	// *******************



	/**
	 * Constructor.
	 * <p>
	 * References checked: 9.11.2004 (2.0b3)
	 *
	 * @param   topicmap	the topicmap to be exported.
	 *
	 * @see     de.deepamehta.topics.TopicMapTopic#export
	 */
	public TopicMapExporter(TopicMapTopic topicmap) {
		this.topicmap = topicmap;
	}



	// **********************
	// *** Static Methods ***
	// **********************



	/**
	 * Exports the topicmap to a ZIP archive file, containing XML files for use mode,
	 * build mode, and all necessary documents and icon files.
	 * <p>
	 * References checked: 12.4.2002 (2.0a14-post1)
	 *
	 * @param   fileBasename the name of the exported file, without suffix
	 *
	 * @return  the exported zip archive file
	 *
	 * @see     de.deepamehta.topics.TopicMapTopic#export
	 */
	public File createTopicmapArchive(String fileBasename) throws IOException {
		// ### compare to CMImportExportTopic.exportCM()
		File archiveFile = new File(FileServer.repositoryPath(FILE_DOCUMENT) + fileBasename + ".zip");
		// possibly create corporate document repository
		FileServer.createDirectory(archiveFile);
		// create topicmap archive file
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(archiveFile));
		// create file set
		ArchiveFileCollector collector = new ArchiveFileCollector();
		//
		exportViewMode(out, fileBasename, collector);
		collector.export(out);
		out.close();
		//
		return archiveFile;
	}

	/**
	 * Exports the topicmap to SVG by using a custom stylesheet.
	 *
	 * @param   fileBasename the name of the exported file, without suffix
	 *
	 * @return  the exported File
	 *
	 * @see     de.deepamehta.topics.TopicMapTopic#export
	 */
	public File createSVGFile(String fileBasename) throws IOException, DeepaMehtaException {
		// --- trigger getSVGStylesheetName() hook ---
		String stylesheetName = topicmap.getSVGStylesheetName();
		//
		File outFile = new File(FileServer.repositoryPath(FILE_DOCUMENT) + fileBasename + ".svg");
		FileServer.createDirectory(outFile);
		OutputStream out = new FileOutputStream(outFile);
		transformTopicmap(topicmap, stylesheetName, new StreamResult(out));	// ### outFile
		out.close();
		//
		return outFile;
	}

	/**
	 * Exports the topicmap to PDF by using custom stylesheets.
	 *
	 * @param   foStylesheetName    the filename of the XML->FO  transformation stylesheet
	 * @param   svgStylesheetName   the filename of the XML->SVG transformation stylesheet
	 * @param   fileBasename        the name of the exported file, without suffix
	 *
	 * @return  the exported File
	 *
	 * @see de.deepamehta.topics.TopicMapTopic#export
	 */
	public File createPDFFile(String fileBasename) throws IOException, DeepaMehtaException {
		// --- trigger getFOStylesheetName() and getSVGStylesheetName() hooks --- ### right place?
		String foStylesheetName = topicmap.getFOStylesheetName();
		String svgStylesheetName = topicmap.getSVGStylesheetName();
		//
		File outFile = // ### new File(FileServer.repositoryPath(FILE_DOCUMENT) + fileBasename + ".fo");	// ### debugging: write fo file
			new File(FileServer.repositoryPath(FILE_DOCUMENT) + fileBasename + ".pdf");
		FileServer.createDirectory(outFile);
		OutputStream out = new FileOutputStream(outFile);
		// --- SAX transformation ---
		Driver driver = new Driver();
		Logger logger = new ConsoleLogger(ConsoleLogger.LEVEL_ERROR);
		MessageHandler.setScreenLogger(logger);
		driver.setLogger(logger);
		driver.setRenderer(Driver.RENDER_PDF);
		driver.setOutputStream(out);
		Result result = // ### new StreamResult(out);	// ### debugging: write the fo to file
			new SAXResult(new SVGInsertContentHandlerProxy(topicmap, svgStylesheetName, driver.getContentHandler()));
		transformTopicmap(topicmap, foStylesheetName, result);
		out.close();
		return outFile;
	}



	// **********************
	// *** Static Methods ***
	// **********************



	/**
	 * Exports one viewmode of the topicmap to the OutputStream.
	 *
	 * @param   out			the OutputStream into the ZIP archive
	 * @param   filename	the filename of the generated XML file
	 * @param   collector	collects document and icon files for export, may be <code>null</code>.
	 *
	 * @see		#createTopicmapArchive
	 */
	private void exportViewMode(ZipOutputStream out, String filename, ArchiveFileCollector collector) {
		try {
			out.putNextEntry(new ZipEntry(filename + ".xml"));
			//
			SAXTransformerFactory saxTFactory = (SAXTransformerFactory) TransformerFactory.newInstance();
			TransformerHandler handler = saxTFactory.newTransformerHandler();
			handler.setResult(new StreamResult(out));
			//
			handler.startDocument();
			// --- trigger exportTopicmap() hook ---
			topicmap.exportTopicmap(handler, collector);
			//
			handler.endDocument();
		} catch (Throwable e) {
			System.out.println("*** TopicMapExporter.exportViewMode(): " + e);
			e.printStackTrace();
		}
	}

	/**
	 * Transforms the specified topicmap by using the specified XSL stylesheet.
	 * ### The resulting SAX events are handled by the specified handler.
	 * <p>
	 * References checked: 18.10.2003 (2.0b2)
	 *
	 * @param   topicmap        topicmap to transform
	 * @param   stylesheetName	filename of the XSL stylesheet
	 * @param   handler	        SAX event handler
	 *
	 * @see     #createSVGFile
	 * @see     #createPDFFile
	 * @see     SVGInsertContentHandlerProxy#processingInstruction
	 */
	static void transformTopicmap(TopicMapTopic topicmap, String stylesheetName, Result result) {
		try {
			String stylesheet = DeepaMehtaUtils.readFile(new File(stylesheetName));
			//
			SAXTransformerFactory saxTFactory = (SAXTransformerFactory) TransformerFactory.newInstance();
			TransformerHandler handler = saxTFactory.newTransformerHandler(new StreamSource(new StringReader(stylesheet)));
			handler.setResult(result);
			//
			Transformer transformer = handler.getTransformer();
			transformer.setParameter("iconDirectory", "file:" + System.getProperty("user.dir") +
				System.getProperty("file.separator") + FILESERVER_ICONS_PATH);
			//
			handler.startDocument();
			// --- trigger exportTopicmap() hook ---
			topicmap.exportTopicmap(handler, null);
			//
			handler.endDocument();
		} catch (Throwable e) {
			System.out.println("*** TopicMapExporter.transformTopicmap(): " + e);
			e.printStackTrace();
		}
	}

	// ---

	/**
	 * ### Creates XML representation for the specified topics.
	 * <p>
	 * References checked: 21.1.2003 (2.0a17-pre7)
	 *
	 * @param   topics		the topics to be exported, enumeration of BaseTopics
	 * @param   handler		SAX event handler
	 * @param   visible		<code>true</code> means that the association will be visible in the exported map
	 * @param   as			the ApplicationService that provides the BaseAssociations
	 * @param   collector	collects document and icon files for export ###, may be <code>null</code>.
	 *
	 * @see     de.deepamehta.topics.TopicMapTopic#exportTopicmap
	 * @see     de.deepamehta.topics.CMImportExportTopic#exportCM
	 */
	public static void exportTopics(Enumeration topics, ContentHandler handler, boolean visible,
									ApplicationService as, ArchiveFileCollector collector) throws SAXException {
		while (topics.hasMoreElements()) {
			BaseTopic topic = (BaseTopic) topics.nextElement();
			exportTopic(topic, handler, visible, as, collector);
		}
	}

	/**
	 * ### Creates XML representation for the specified associations.
	 * <p>
	 * References checked: 13.4.2002 (2.0a14-post1)
	 *
	 * @param   assocs		the BaseAssociations to be exported
	 * @param   handler		SAX event handler
	 * @param   visible		<code>true</code> means that the association will be visible in the exported map
	 * @param   as			the ApplicationService that provides the BaseAssociations
	 *
	 * @see     de.deepamehta.topics.TopicMapTopic#exportTopicmap
	 * @see     de.deepamehta.topics.CMImportExportTopic#exportCM
	 */
	public static void exportAssociations(Enumeration assocs, ContentHandler handler, boolean visible,
									ApplicationService as, ArchiveFileCollector collector) throws SAXException {
		while (assocs.hasMoreElements()) {
			BaseAssociation assoc = (BaseAssociation) assocs.nextElement();
			exportAsssociation(assoc, handler, visible, as, collector);
		}
	}

	// ---

	/**
	 * Exports one topic to a SAX handler.
	 *
	 * @param   topic   	the topic to be exported
	 * @param   handler		SAX event handler
	 * @param   visible 	<code>true</code> means that geometry will be saved
	 * @param   as			the ApplicationService that provides the BaseAssociations
	 * @param   collector   collects document and icon files for export, may be <code>null</code>
	 *
	 * @see		#exportTopics
	 */
	private static void exportTopic(BaseTopic topic, ContentHandler handler, boolean visible,
					ApplicationService as, ArchiveFileCollector collector) throws SAXException {
		// --- attributes for topic element ---
		Hashtable attribs = new Hashtable();
		attribs.put("ID", topic.getID());
		attribs.put("types", topic.getType());
		attribs.put("visible", visible ? "yes" : "no");
		// geometry and icon
		if (visible) {
			Point p = ((PresentableTopic) topic).getGeometry();
			attribs.put("x", new Integer(p.x));
			attribs.put("y", new Integer(p.y));
			String iconfile = as.getLiveTopic(topic).getIconfile();	// ### as.getTopicProperty(topic, PROPERTY_ICON);
			attribs.put("icon", iconfile);
			// ### store icon file
			if (collector != null && !iconfile.equals("")) {
				collector.putIcon(iconfile);
			}
		}
		// --- generate topic element ---
		startElement(handler, "topic", attribs);
		startElement(handler, "topname", null);
		startElement(handler, "basename", null);
		characters(handler, topic.getName());
		endElement(handler, "basename");
		endElement(handler, "topname");
		//
		exportProperties(as.getTopicProperties(topic), topic.getID(), handler, collector != null);
		//
		endElement(handler, "topic");
		// ### store document file
		if (collector != null && as.isInstanceOf(topic, TOPICTYPE_DOCUMENT)) {
			collector.putDocument(as.getTopicProperty(topic, PROPERTY_FILE));
		}
	}

	/**
	 * Creates XML representation for one association.
	 *
	 * @param   assoc		the association to be exported to XML
	 * @param   handler		SAX event handler
	 * @param   visible		<code>true</code> means that the association will be visible in the exported map
	 * @param   as			the ApplicationService that provides the BaseAssociations
	 *
	 * @see		#exportAssociations
	 */
	public static void exportAsssociation(BaseAssociation assoc, ContentHandler handler, boolean visible,
									ApplicationService as, ArchiveFileCollector collector) throws SAXException {
		Hashtable attribs = new Hashtable();
		attribs.put("ID", assoc.getID());
		attribs.put("type", assoc.getType());
		attribs.put("visible", visible ? "yes" : "no");
		if (visible) {
			attribs.put("color", as.type(assoc).getAssocTypeColor());
		}
		startElement(handler, "assoc", attribs);

		attribs = new Hashtable();
		attribs.put("anchrole", "tt-topic1");
		startElement(handler, "assocrl", attribs);
		characters(handler, assoc.getTopicID1());
		endElement(handler, "assocrl");
		attribs.put("anchrole", "tt-topic2");
		startElement(handler, "assocrl", attribs);
		characters(handler, assoc.getTopicID2());
		endElement(handler, "assocrl");
		exportProperties(as.getAssocProperties(assoc), assoc.getID(), handler, collector != null);
		endElement(handler, "assoc");
	}

	// ---

	/**
	 * Exports the type definitons of the specified types.
	 * <p>
	 * References checked: 17.1.2003 (2.0a17-pre6)
	 *
	 * @param   types		the types to be exported (enumeration of <code>Type</code>)
	 * @param   handler     SAX event handler
	 * @param   collector   collects document and icon files for export
	 *
	 * @see     de.deepamehta.topics.CMImportExportTopic#exportCM
	 * @see     de.deepamehta.topics.TopicMapTopic#exportTopicmap
	 */
	public static void exportTypes(Enumeration types, ContentHandler handler, ArchiveFileCollector collector,
																ApplicationService as) throws SAXException {
		while (types.hasMoreElements()) {
			String typeID = ((Type) types.nextElement()).getID();
			as.type(typeID, 1).exportTypeDefinition(handler, collector);	// ### version 1
		}
	}

	/**
	 * ### Creates XML representation for properties of topic. This method is also used
	 * for storing of topicmap properties.
	 * <p>
	 * References checked: 1.5.2004 (2.0b3-pre2)
	 *
	 * @param   properties  input properties <code>Hashtable</code>
	 * @param   id			id of the origin topic/association, for debugging only
	 * @param   handler		SAX event handler
	 * @param   exportCData specifies if styled properties should be exported as CDATA
	 *
	 * @see		#exportTopic
	 * @see		#exportAssociation
	 * @see		de.deepamehta.topics.TopicMapTopic#exportTopicmap
	 */
	public static void exportProperties(Hashtable properties, String id, ContentHandler handler,
																	boolean exportCData) throws SAXException {
		Enumeration e = properties.keys();
		while (e.hasMoreElements()) {
			String propName = (String) e.nextElement();
			String propValue = (String) properties.get(propName);
			Hashtable attribs = new Hashtable();
			attribs.put("name", propName);
			startElement(handler, "property", attribs);
			if ((propValue.startsWith("<html>") || propValue.startsWith("<paragraph>")) && !exportCData) {
				try {
					parser.reset();
					parser.setFeature("http://apache.org/xml/features/continue-after-fatal-error", true);
					parser.setContentHandler(new EmbeddedContentHandlerProxy(handler));
					parser.parse(new InputSource(new StringReader(propValue)));
					// ### Dosn't compile! We need a DefaultHandler, a ContentHandler is not sufficient
					// ### SAXParser parser = parserFactory.newSAXParser();
					// ### parser.parse(new InputSource(new StringReader(propValue)), new EmbeddedContentHandlerProxy(handler));
				} catch (Exception ex) {
					System.out.println("*** TopicMapExporter.exportProperties(): while parsing \"" + propName +
						"\" of \"" + id + "\": " + ex);
				}
			} else {
				characters(handler, propValue);
			}
			endElement(handler, "property");
		}
	}

	// --- SAX utility methods and classes ---

	/**
	 * Utility method to write an opening XML tag.
	 *
	 * @param   handler     SAX event handler
	 * @param   tagName     the name of the tag
	 * @param   attributes  the attributes of this tag as a map of key-value pairs,
	 * 						may be <code>null</code>.
	 */
	public static void startElement(ContentHandler handler, String tagName,
									Hashtable attributes) throws SAXException {
		AttributesImpl attrs = new AttributesImpl();
		if (attributes != null) {
			Enumeration attribKeys = attributes.keys();
			while(attribKeys.hasMoreElements()) {
				String key = attribKeys.nextElement().toString();
				String value = attributes.get(key).toString();
				attrs.addAttribute("", "", key, "CDATA", value);
			}
		}
		handler.startElement("", "", tagName, attrs);
	}

	/**
	 * Utility method to write a closing XML tag.
	 *
	 * @param   handler SAX event handler
	 * @param   tagName the name of the tag
	 */
	public static void endElement(ContentHandler handler, String tagName) throws SAXException {
		handler.endElement("", "", tagName);
	}

	/**
	 * Utility method to write a text node.
	 *
	 * @param   handler SAX event handler
	 * @param   string  the output string
	 */
	public static void characters(ContentHandler handler, String string) throws SAXException {
		char[] chars = string.toCharArray();
		if (handler instanceof LexicalHandler) {
			((LexicalHandler) handler).startCDATA();
			handler.characters(chars, 0, chars.length);
			((LexicalHandler) handler).endCDATA();
		} else {
			handler.characters(chars, 0, chars.length);
		}
	}
}
