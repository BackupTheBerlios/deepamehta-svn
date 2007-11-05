package de.deepamehta.topics.helper;

import de.deepamehta.BaseAssociation;
import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.FileServer;
import de.deepamehta.PresentableTopic;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.Session;
import de.deepamehta.topics.TopicTypeTopic;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.awt.Point;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;



/**
 * Utility class for importing topicmaps ### also used for CM import
 * ### this class is to be dropped
 * ### functionality will move to application service
 * <P>
 * The XML parsing is performed by means of the Xerces parser (Apache Group) ### JAX should be used
 * <P>
 * <HR>
 * Last functional change: 17.2.2005 (2.0b5)<BR>
 * Last documentation update: 14.2.2003 (2.0a18-pre2)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class TopicMapImporter implements DeepaMehtaConstants {



	// **************
	// *** Fields ***
	// **************



	private ApplicationService as;

	/**
	 * The Mappings from the topics in the Map to be imported
	 * to the IDs of the topics that are already existing in Corporate Memory.
	 * <P>
	 * Key: original topic ID (String)<BR>
	 * Value: mapped topic ID (IDMapping)
	 */
	private Hashtable idMapping = new Hashtable();

	// --- statistics ---
	private int importedTopics;
	private int skippedTopics;
	private int importedAssocs;
	private int skippedAssocs;
	//
	private int importedTopicTypes;
	private int skippedTopicTypes;
	private int importedAssocTypes;
	private int skippedAssocTypes;
	//
	private int importedDocs;
	private int skippedDocs;
	private int importedIcons;
	private int skippedIcons;



	// *******************
	// *** Constructor ***
	// *******************



	/**
	 * References checked: 18.4.2002 (2.0a14-post1)
	 *
	 * @see     de.deepamehta.service.ApplicationService#importTopicmap
	 * @see     de.deepamehta.service.ApplicationService#importCM
	 */
	public TopicMapImporter(ApplicationService as) {
		this.as = as;
	}



	// **********************
	// *** Public methods ***
	// **********************



	/**
	 * Imports a topicmap from an exported ZIP archive, including the referenced icon and document files.
	 * The imported topicmap appears in the users personal workspace.
	 * <P>
	 * Note: this method is also used for CM import (no topicmap is created).
	 * <P>
	 * References checked: 17.4.2002 (2.0a14-post1)
	 *
	 * @param   file    the XML file to be imported
	 * @param   session identifies the client that initiated the import, resp. null for CM import
	 * @param   x       coordinate of the visible view topic, not used for CM import
	 * @param   y       coordinate of the visible view topic, not used for CM import
	 *
	 * @return  the directives to notify the user about the import result
	 *
	 * @see     de.deepamehta.service.ApplicationService#importTopicmap
	 * @see     de.deepamehta.service.ApplicationService#importCM
	 */
	public CorporateDirectives doImport(File file, Session session, int x, int y) {
		boolean cmImport = session == null;
		String viewID = null;
		Document doc = null;
		if (!cmImport) {
			viewID = as.getNewTopicID();
		}
		try {
			ZipFile zfile = new ZipFile(file);
			Enumeration e = zfile.entries();
			while (e.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) e.nextElement();
				String filename = entry.getName();
				InputStream in = zfile.getInputStream(entry);
				//
				filename = filename.replace('\\', '/');		// ###
				if (filename.indexOf("/") != -1 /* ### || filename.indexOf("\\") != -1 */) {	// ###
					importFile(in, filename);		// document or icon
				} else if (filename.endsWith(".xml")) {
					doc = getParsedDocument(in);
					importTopicMap(doc, viewID);	// topicmap
				} else {
					throw new DeepaMehtaException("unexpected zip entry: " + filename);
				}
				in.close();
			}
		} catch (IOException e) {
			System.out.println("*** TopicMapImporter.doImport(): " + e);
		}
		if (!cmImport) {
			// ### Note: the informations for the symbol of the created
			// project are taken from the last imported XML document
			return createNewView(viewID, doc, session, x, y);
		} else {
			CorporateDirectives directives = new CorporateDirectives();
			directives.add(DIRECTIVE_SHOW_MESSAGE, "The Corporate Memory was successfully updated " +
				"from the archive file \"" + file.getName() + "\"", new Integer(NOTIFICATION_DEFAULT));
			return directives;
		}
	}



	// ***********************
	// *** Private Methods ***
	// ***********************



	/**
	 * Imports a file from the ZIP archive to the server document or icon repository.
	 * ### If a file with the given name already exists, it will be replaced.
	 *
	 * @param   in          the data from the ZIP archive
	 * @param   filename    the name of the file to be created, including the path to the
	 *                      directory, e.g. <CODE>icons/</CODE> or <CODE>documents/</CODE>
	 *
	 * @see     #doImport
	 */
	private void importFile(InputStream in, String filename) {
		try {
			File file = new File(filename);
			int filetype = FileServer.getFiletype(filename);
			// --- skip existing files ---
			if (file.exists()) {
				if (filetype == FILE_DOCUMENT) skippedDocs++;
				if (filetype == FILE_ICON) skippedIcons++;
				return;
				/* ### if (!file.delete()) {
					System.out.println("*** TopicMapImporter.importFile(): existing file " +
						file.getAbsolutePath() + " cannot be overwritten");
					return;
				} */
			}
			// --- create file ---
			if (filetype == FILE_DOCUMENT) importedDocs++;
			if (filetype == FILE_ICON) importedIcons++;
			// ### use file utils?
			// ### create document repository?
			OutputStream out = new FileOutputStream(file);
			byte[] buffer = new byte[FILE_BUFFER_SIZE];
			int bytes_read;
			while((bytes_read = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytes_read);
			}
			out.close();
		} catch(IOException e) {
			System.out.println("*** TopicMapImporter.importFile(): " + e);
		}
	}

	/**
	 * Imports a single topicmap from the given XML document.
	 * The import is performed 2 steps:
	 * <OL>
	 *      <LI>mapping of type IDs, topic IDs, association IDs</LI>
	 *      <LI>import of types, topics, associations</LI>
	 * </OL>
	 * Note: this method is also used for CM import (...).
	 *
	 * @param   doc     the parsed XML document as DOM
	 * @param   viewID  the ID of the map that is newly created to display the imported topics
	 *					and associations, resp. null for CM import
	 *
	 * @see     #doImport
	 */
	private void importTopicMap(Document doc, String viewID) {
		boolean cmImport = viewID == null;
		Element root = doc.getDocumentElement();
		if (!cmImport) {
			// Note: the view itself is matter of mapping.
			// Important for views which contain themselfs, e.g. a Kompetenzstern Template
			String origMapID = root.getAttribute("ID");
			idMapping.put(origMapID, new IDMapping(viewID, false));		// unified=false
			System.out.println(">>> import topicmap \"" + origMapID + "\" -> \"" + viewID + "\"");
		}
		//
		NodeList topictypes = root.getElementsByTagName("topictype");
		NodeList assoctypes = root.getElementsByTagName("assoctype");
		NodeList topics = root.getElementsByTagName("topic");
		NodeList assocs = root.getElementsByTagName("assoc");
		// --- map type IDs ---
		mapTypeIDs(topictypes, TOPICTYPE_TOPICTYPE);
		mapTypeIDs(assoctypes, TOPICTYPE_ASSOCTYPE);
		// --- import types ---
		if (!cmImport) {
			for (int i = 0; i < topictypes.getLength(); i++) {
				importTopicTypeTopic((Element) topictypes.item(i));
			}
			for (int i = 0; i < assoctypes.getLength(); i++) {
				importAssocTypeTopic((Element) assoctypes.item(i));
			}
		}
		// --- map IDs ---
		mapTopicIDs(topics);	// Note: the types must be imported already
		mapAssocIDs(assocs);
		// --- import topics and assocs ---
		for (int i = 0; i < topics.getLength(); i++) {
			importTopic((Element) topics.item(i), viewID);
		}
		for (int i = 0; i < assocs.getLength(); i++) {
			importAssoc((Element) assocs.item(i), viewID);
		}
	}

	// ---

	/**
	 * Imports a single topic to Corporate Memory.
	 * Also creates ViewTopic for the specified view and viewmode, if
	 * the "visible" attribute of the topic element is set.
	 *
	 * @param   topic       the DOM element representing the topic
	 * @param   viewID      the ID of the newly created view, may be null
	 *
	 * @see     #importTopicMap
	 */
	private void importTopic(Element topic, String viewID) {
		String elementName = topic.getNodeName();
		// error check ### ever happens?
		if (!elementName.equals("topic")) {
			System.out.println("*** TopicMapImporter.importTopic(): element name=\"" + elementName + "\"");
		}
		//
		String origID = topic.getAttribute("ID");
		IDMapping mapping = getTopicMapping(origID);
		String topicID = mapping.mappedID();
		String topictype = getTopicMapping(topic.getAttribute("types")).mappedID();	// ### reporting
		String topicName = getTopicName(topic);										// ### reporting
		// do only import topic if not unified
		if (!mapping.unified()) {
			as.cm.createTopic(topicID, 1, topictype, 1, topicName);
			importedTopics++;
			System.out.println("> topic imported: " + topictype + " \"" + topicName + "\" (" + origID + " -> " + topicID + ")");
		} else {
			skippedTopics++;
			System.out.println("> topic skipped: " + topictype + " \"" + topicName + "\" (" + origID + " -> " + topicID + ")");
		}
		// set properties
		as.cm.setTopicData(topicID, 1, getProperties(topic));	// ### doubles?
		// put topic into topicmap
		if (viewID != null && topic.getAttribute("visible").equals("yes")) {
			int x = Integer.parseInt(topic.getAttribute("x"));
			int y = Integer.parseInt(topic.getAttribute("y"));
			as.cm.createViewTopic(viewID, 1, VIEWMODE_USE, topicID, 1, x, y, false);
		}
	}

	/**
	 * Imports a single association to Corporate Memory.
	 * Also creates ViewAssociation for the specified view and viewmode, if
	 * the "visible" attribute of the assoc element is set.
	 *
	 * @param   topic       the DOM element representing the association
	 * @param   viewID      the ID of the newly created view
	 *
	 * @see     #importTopicMap
	 */
	private void importAssoc(Element assoc, String viewID) {
		String elementName = assoc.getNodeName();
		if (elementName.equals("assoc")) {
			String origID = assoc.getAttribute("ID");
			IDMapping assocMapper = getAssocMapping(origID);
			String assocID = assocMapper.mappedID();
			String assoctype = getTopicMapping(assoc.getAttribute("type")).mappedID();
			if(!assocMapper.unified()) {
				// --- retrieve data from DOM element
				NodeList assocrls = assoc.getElementsByTagName("assocrl");
				Element element = (Element) assocrls.item(0);
				String roletype1 = element.getAttribute("anchrole");	// ###
				String topicID1 = getTopicMapping(getTextOfChildNode(element)).mappedID();
				element = (Element) assocrls.item(1);
				String roletype2 = element.getAttribute("anchrole");	// ###
				String topicID2 = getTopicMapping(getTextOfChildNode(element)).mappedID();
				// --- create association in CM ---
				as.cm.createAssociation(assocID, 1, assoctype, 1, topicID1, 1, topicID2, 1);
				//
				importedAssocs++;
				System.out.println("> association imported: " + assoctype + " (" + origID + " -> " + assocID + ")");
			} else {
				skippedAssocs++;
				System.out.println("> association skipped: " + assoctype + " (" + origID + " -> " + assocID + ")");
			}
			// set properties
			as.cm.setAssociationData(assocID, 1, getProperties(assoc));
			// put topic into topicmap
			if (viewID != null && assoc.getAttribute("visible").equals("yes")) {
				as.cm.createViewAssociation(viewID, 1, VIEWMODE_USE, assocID, 1, false);
			}
		} else {
			// ### ever happens?
			System.out.println("*** TopicMapImporter.importAssoc(): element name=\"" + elementName + "\"");
		}
	}

	// ---

	/**
	 * Imports a topic type topic, including its associations to its topic container, properties, and options,
	 * plus the association to its supertype. If the type already exists in CM, this method won't do anything.
	 * Note: The type definition (props, options, containers) are not unified.
	 *
	 * @param   type    the DOM element that represents the topic type
	 *
	 * @see     #importTopicMap
	 */
	private void importTopicTypeTopic(Element type) {
		// ### compare to TopicTypeTopic.exportTypeDefinition()
		String origID = type.getAttribute("ID");
		IDMapping mapping = getTopicMapping(origID);
		String topicID = mapping.mappedID();
		String typeName = getTextOfChildNode(type, "name");
		if (!mapping.unified()) {
			// --- create TopicType topic
			as.cm.createTopic(topicID, 1, TOPICTYPE_TOPICTYPE, 1, typeName);
			// --- set TopicData of TopicType topic
			Hashtable props = new Hashtable();
			props.put(PROPERTY_NAME, typeName);
			props.put(PROPERTY_DESCRIPTION, getTextOfChildNode(type, "description"));
			props.put(PROPERTY_ICON, type.getAttribute("icon"));
			props.put(PROPERTY_CREATION_ICON, type.getAttribute("creationicon"));
			props.put(PROPERTY_COLOR, type.getAttribute("color"));
			props.put(PROPERTY_PLURAL_NAME, getTextOfChildNode(type, "pluralname"));
			props.put(PROPERTY_TYPE_DESCRIPTION_QUERY, getTextOfChildNode(type, "descriptionquery"));
			props.put(PROPERTY_DISABLED, type.getAttribute("disabled"));
			props.put(PROPERTY_HIDDEN_TOPIC_NAMES, type.getAttribute("hiddennames"));
			props.put(PROPERTY_UNIQUE_TOPIC_NAMES, type.getAttribute("unified"));
			props.put(PROPERTY_IMPLEMENTATION, type.getAttribute("implementation"));
			as.cm.setTopicData(topicID, 1, props);
			// --- create associations to supertype and containertype
			String supertypeRef = type.getAttribute("supertypeID");
			if (!supertypeRef.equals("")) {
				String supertypeID = getTopicMapping(supertypeRef).mappedID();
				String assocID = as.getNewAssociationID();
				as.cm.createAssociation(assocID, 1, SEMANTIC_TYPE_DERIVATION, 1, supertypeID, 1, topicID, 1);
			}
			String containerTypeRef = type.getAttribute("containertypeID");
			if (!containerTypeRef.equals("")) {
				String containertypeID = getTopicMapping(containerTypeRef).mappedID();
				String assocID = as.getNewAssociationID();
				as.cm.createAssociation(assocID, 1, SEMANTIC_CONTAINER_TYPE, 1, containertypeID, 1, topicID, 1);
			}
			// ### PENDING: relations
			// --- import properties and options
			importPropertiesOfTypeTopic(type, topicID);
			//
			importedTopicTypes++;
			System.out.println("> topic type imported: \"" + typeName + "\" (" + origID + " -> " + topicID + ")");
		} else {
			skippedTopicTypes++;
			System.out.println("> topic type skipped: \"" + typeName + "\" (" + origID + " -> " + topicID + ")");
		}
	}

	/**
	 * Imports an association type topic, including its semantic
	 * relations to its properties, options, plus the association to its supertype.
	 * If the type already exists in CM, this method won't do anything.
	 * Note: The type definition (props, options, containers) are not unified.
	 *
	 * @param   type    the DOM element that represents the association type
	 *
	 * @see     #importTopicMap
	 */
	private void importAssocTypeTopic(Element type) {
		String origID = type.getAttribute("ID");
		IDMapping mapping = getTopicMapping(origID);
		String topicID = mapping.mappedID();
		String typeName = getTextOfChildNode(type, "name");
		if (!mapping.unified()) {
			// --- create AssocType topic
			as.cm.createTopic(topicID, 1, TOPICTYPE_ASSOCTYPE, 1, typeName);
			// --- set TopicData of AssocType topic
			Hashtable props = new Hashtable();
			props.put("Name", typeName);
			props.put("Color", type.getAttribute("color"));
			props.put("Disabled", type.getAttribute("disabled"));
			props.put("Custom Implementation", type.getAttribute("implementation"));
			props.put("Plural Name", getTextOfChildNode(type, "pluralname"));
			props.put("Description", getTextOfChildNode(type, "description"));
			props.put("Description Query", getTextOfChildNode(type, "descriptionquery"));
			as.cm.setTopicData(topicID, 1, props);
			// --- create associations to supertype
			String supertypeRef = type.getAttribute("supertypeID");
			if (!supertypeRef.equals("")) {
				String supertypeID = getTopicMapping(supertypeRef).mappedID();
				String assocID = as.getNewAssociationID();
				as.cm.createAssociation(assocID, 1, SEMANTIC_TYPE_DERIVATION, 1, supertypeID, 1, topicID, 1);
			}
			// --- import properties and options
			importPropertiesOfTypeTopic(type, topicID);
			//
			importedAssocTypes++;
			System.out.println("> association type imported: \"" + typeName + "\" (" + origID + " -> " + topicID + ")");
		} else {
			skippedAssocTypes++;
			System.out.println("> association type skipped: \"" + typeName + "\" (" + origID + " -> " + topicID + ")");
		}
	}

	/**
	 * Imports the properties of a TypeTopic (topic type topic or assoc type topic).
	 *
	 * @param   type    the DOM element representing the type topic
	 * @param   typeID  the mapped ID of the type topic
	 *
	 * @see     #importTopicTypeTopic
	 * @see     #importAssocTypeTopic
	 */
	private void importPropertiesOfTypeTopic(Element type, String typeID) {
		// --- find "propertydef" elements
		NodeList properties = type.getElementsByTagName("propertydef");
		for (int i = 0; i < properties.getLength(); i++) {
			Element property = (Element) properties.item(i);
			// --- create Property topic
			String propname = property.getAttribute("name");
			String propertyID = as.getNewTopicID();
			as.cm.createTopic(propertyID, 1, "tt-property", 1, propname);
			// --- set TopicData of this Property topic
			Hashtable props = new Hashtable();
			props.put("Visualization", property.getAttribute("visualization"));
			props.put("Edit Icon", property.getAttribute("editicon"));
			as.cm.setTopicData(propertyID, 1, props);
			// --- create assocs from TopicType topic to this Property topic
			String assocID = as.getNewAssociationID();
			as.cm.createAssociation(assocID, 1, SEMANTIC_PROPERTY_DEFINITION, 1, typeID, 1, propertyID, 1);
			// --- create and associate options of this Property topic
			importOptionsOfProperty(property, propertyID);
		}
	}

	/**
	 * Imports the options of a PropertyTopic.
	 *
	 * @param   property    the DOM element representing the property
	 * @param   propertyID  the ID of the property
	 *
	 * @see     #importPropertiesOfTypeTopic
	 */
	private void importOptionsOfProperty(Element property, String propertyID) {
		// --- find "option" elements
		NodeList options = property.getElementsByTagName("option");
		for (int i = 0; i < options.getLength(); i++) {
			Element option = (Element) options.item(i);
			// --- create Constant topic
			String optionName = getTextOfChildNode(option);
			String optionID = as.getNewTopicID();
			as.cm.createTopic(optionID, 1, TOPICTYPE_PROPERTY_VALUE, 1, optionName);
			// --- create assocs from Property topic to this Constant topic
			String assocID = as.getNewAssociationID();
			as.cm.createAssociation(assocID, 1, SEMANTIC_OPTION_DEFINITION, 1, propertyID, 1, optionID, 1);
		}
	}

	// ---

	/**
	 * Gets entry from zip archive and parses it by XML parser.
	 *
	 * @param   in      the XML data as InputStream
	 *
	 * @return  the parsed document as DOM tree
	 *
	 * @see     #doImport
	 */
	private Document getParsedDocument(InputStream in) {
		DOMParser parser = new DOMParser();
		try  {
			parser.parse(new InputSource(in));
			return parser.getDocument();
		} catch (Exception e) {
			System.out.println("### TopicMapImporter.getParsedDocument: ");
			e.printStackTrace();
			return null;
		}
	}

	// ---

	/**
	 * Retrieves the name of a topic.
	 *
	 * @param   topic   the DOM element representing the topic
	 *
	 * @return  the name of the given topic
	 *
	 * @see     #importTopic
	 * @see     #mapTopicIDs
	 */
	private String getTopicName(Element topic) {
		Element topname = (Element) topic.getElementsByTagName("topname").item(0);
		return getTextOfChildNode(topname, "basename");
	}

	/**
	 * Assumes that the given node has a text node as its only child,
	 * and retrieves the text value of this child node.
	 *
	 * @param   node    a DOM node with a single text child node
	 *
	 * @return  the text value of the child node, or "" if no child was found
	 */
	private static String getTextOfChildNode(Node node) {
		Node child = node.getFirstChild();
		return child != null ? child.getNodeValue() : "";
	}

	/**
	 * Starting from "current" element, looks for a child element
	 * with the specified element name, and retrieves the value of its text childnode.
	 *
	 * @param   current     a DOM element with a child element
	 * @param   elementName the name of the child element
	 *
	 * @return  the text value of the child element, or "" if no child was found
	 */
	private static String getTextOfChildNode(Element current, String elementName) {
		Node nameItem = current.getElementsByTagName(elementName).item(0);
		return getTextOfChildNode(nameItem);
	}

	/**
	 * Looks for &lt;property&gt; child elements of the current elements
	 * and creates a Hashtable from them.
	 *
	 * @param   el  an XML tag, typically a &lt;topic&gt; or &lt;assoc&gt;
	 *
	 * @return  the name-value pairs as Hashtable with the names as keys
	 *
	 * @see     #importTopic
	 * @see     #importAssoc
	 * @see     #createNewView
	 */
	private static Hashtable getProperties(Element el) {
		Hashtable props = new Hashtable();
		//
		// ### NodeList propElems = el.getElementsByTagName("property");	### doesn't work for <topicmap>
		NodeList nodes = el.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = (Node) nodes.item(i);
			// ### System.out.println(">>> child of \"" + el.getAttribute("ID") + "\": \"" + node.getNodeName() + "\"");
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element elem = (Element) node;
				if (elem.getTagName().equals("property")) {
					String propName = elem.getAttribute("name");
					String propVal = getTextOfChildNode(node);
					props.put(propName, propVal == null ? "" : propVal);
					// ### System.out.println(">>> child of \"" + el.getAttribute("ID") + "\" is NOT <property> but <" + node.getTagName() + ">");
				}
			}
		}
		//
		return props;
	}

	// ---

	/**
	 * Retrieves the mapping of the topic identified by the given ID.
	 * If no mapping was found, a mapping to a new topic ID will be created, stored and returned.
	 *
	 * @param   topicID  the ID of an imported topic
	 *
	 * @return  an IDMapping containing the topic id of the topic in CM/LCM,
	 *          or a generated topic id if no mapping was found,
	 *          plus the information whether the topic was unified.
	 */
	private IDMapping getTopicMapping(String topicID) {
		IDMapping mapper = (IDMapping) idMapping.get(topicID);
		if (mapper == null) {
			// Note: the IDMapping is created "lazy" to avoid creation of new topic IDs
			// in case of ...
			mapper = new IDMapping(as.getNewTopicID(), false);			// unified=false
			idMapping.put(topicID, mapper);
		}
		return mapper;
	}

	/**
	 * Retrieves the mapping of the association identified by the given ID.
	 * If no mapping was found, a mapping to a new assoc ID will be created, stored and returned.
	 *
	 * @param   assocID  the ID of an imported association
	 *
	 * @return  an IDMapping containing the association id of the association in CM/LCM,
	 *          or a generated association id if no mapping was found,
	 *          plus the information whether the association was unified.
	 */
	private IDMapping getAssocMapping(String assocID) {
		IDMapping mapper = (IDMapping) idMapping.get(assocID);
		if (mapper == null) {
			mapper = new IDMapping(as.getNewAssociationID(), false);	// unified=false
			idMapping.put(assocID, mapper);
		}
		return mapper;
	}

	// ---

	/**
	 * Creates a new view for the imported topicmap in the personal workspace.
	 *
	 * @param   viewID  the ID of the new view
	 * @param   doc     a DOM objects which contains informations about the imported
	 *                  view, e.g. name, type, and properties
	 * @param   session identifies the client that initiated the import
	 * @param   x       coordinate of the visible view topic
	 * @param   y       coordinate of the visible view topic
	 *
	 * @return  the directives to display the visible view topic in the client
	 *
	 * @see     #doImport
	 */
	private CorporateDirectives createNewView(String viewID, Document doc, Session session, int x, int y) {
		Element docElement = (Element) doc.getDocumentElement();
		String topicmapName = docElement.getAttribute("name");
		String topicmapTypeID = docElement.getAttribute("type");
		as.cm.createTopic(viewID, 1, topicmapTypeID, 1, topicmapName);
		Hashtable props = getProperties(docElement);
		System.out.println(">>> TopicMapImporter.createNewView(): viewID=\"" + viewID + "\"");
		System.out.println(">>> " + props);
		as.cm.setTopicData(viewID, 1, props);
		// ### dont access cm, instead set evoke=TRUE
		CorporateDirectives directives = new CorporateDirectives();
		PresentableTopic topicmap = new PresentableTopic(viewID, 1, topicmapTypeID, 1, topicmapName, new Point(x, y));
		directives.add(DIRECTIVE_SHOW_TOPIC, topicmap, Boolean.FALSE, session.getPersonalWorkspace().getID());
		//
		return directives;
	}



	// ------------------
	// --- ID Mapping ---
	// ------------------



	/**
	 * Maps types to be imported with existing types.
	 *
	 * @param   types     the types to be imported, a list of &lt;topictype&gt; or &lt;assoctype&gt; nodes
	 * @param   typeID    <CODE>tt-topictype</CODE> or <CODE>tt-assoctype</CODE>
	 *
	 * @see     #importTopicMap
	 */
	private void mapTypeIDs(NodeList types, String typeID) {
		for (int i = 0; i < types.getLength(); i++) {
			Element type = (Element) types.item(i);
			String origTypeID = type.getAttribute("ID");
			BaseTopic mappedType;
			//
			// ### QUICK HACK: ASSOCTYPE_ASSOCIATION is treated special because there are
			// ### 2 association types named "Association".
			if (origTypeID.equals(ASSOCTYPE_ASSOCIATION)) {
				mappedType = as.cm.getTopic(origTypeID, 1);	// version=1
			// ### end of hack
			} else {
				// try unification by name
				String typeName = getTextOfChildNode(type, "name");
				mappedType = as.cm.getTopic(typeID, typeName, 1);	// version=1
			}
			// if unified, ID mapping is required
			if (mappedType != null) {
				idMapping.put(origTypeID, new IDMapping(mappedType.getID(), true));		// unified=true
			}
		}
	}

	// ---

	/**
	 * Maps topics to be imported with existing topics.
	 *
	 * @param   topics    a list of &lt;topic&gt; nodes
	 *
	 * @see     #importTopicMap
	 */
	private void mapTopicIDs(NodeList topics) {
		for (int i = 0; i < topics.getLength(); i++) {
			Element topic = (Element) topics.item(i);
			String topicTypeID = getTopicMapping(topic.getAttribute("types")).mappedID();
			if (uniqueTopicNames(topicTypeID)) {
				String topicname = getTopicName(topic);
				// try unification by name
				BaseTopic mappedTopic = as.cm.getTopic(topicTypeID, topicname, 1);	// version=1
				// if unified, ID mapping is required
				if (mappedTopic != null) {
					String origTopicID = topic.getAttribute("ID");
					idMapping.put(origTopicID, new IDMapping(mappedTopic.getID(), true));	// unified=true
				}
			}
		}
	}

	/**
	 * Maps associations to be imported with existing associations.
	 *
	 * @param   assocs    a list of &lt;assoc&gt; nodes
	 *
	 * @see     #importTopicMap
	 */
	private void mapAssocIDs(NodeList assocs) {
		for (int i = 0; i < assocs.getLength(); i++) {
			Element assoc = (Element) assocs.item(i);
			NodeList assocrls = assoc.getElementsByTagName("assocrl");
			IDMapping mapping1 = getTopicMapping(getTextOfChildNode(assocrls.item(0)));
			IDMapping mapping2 = getTopicMapping(getTextOfChildNode(assocrls.item(1)));
			if (mapping1.unified() && mapping2.unified()) {
				String assocTypeID = getTopicMapping(assoc.getAttribute("type")).mappedID();
				// try unification
				BaseAssociation mappedAssoc = as.cm.getAssociation(assocTypeID, mapping1.mappedID(), mapping2.mappedID());
				// if unified, ID mapping is required
				if (mappedAssoc != null) {
					String origAssocID = assoc.getAttribute("ID");
					idMapping.put(origAssocID, new IDMapping(mappedAssoc.getID(), true));	// unified=true
				}
			}
		}
	}

	// ---

	/**
	 * Returns the "Unique Topic Names" setting of the specified topic type.
	 *
	 * @see     #mapTopicIDs
	 */
	private boolean uniqueTopicNames(String typeID) {
		return ((TopicTypeTopic) as.type(typeID, 1)).getUniqueTopicNames();
	}

	public void report() {
		System.out.println("> " + importedTopics + " topics imported, " + skippedTopics + " skipped");
		System.out.println("> " + importedAssocs + " associations imported, " + skippedAssocs + " skipped");
		System.out.println("> " + importedTopicTypes + " topic types imported, " + skippedTopicTypes + " skipped");
		System.out.println("> " + importedAssocTypes + " association types imported, " + skippedAssocTypes + " skipped");
		System.out.println("> " + importedDocs + " documents imported, " + skippedDocs + " skipped");
		System.out.println("> " + importedIcons + " icons imported, " + skippedIcons + " skipped");
	}



	// ***************************
	// *** Private inner class ***
	// ***************************



	/**
	 * Data container that holds one ID mapping.
	 * IDMapping objects are stored in the {#idMapping} table.
	 */
	private class IDMapping {



		// **************
		// *** Fields ***
		// **************



		/**
		 * The ID of an object in Corporate Memory (either an existing one
		 * or a new one which will be created during the import process).
		 * <P>
		 * The ID of the imported object is stored in the mapping as the key
		 * for this object.
		 */
		private String mappedID;

		/**
		 * <CODE>true</CODE> if the imported topic could be unified.
		 */
		private boolean unified;



		// *******************
		// *** Constructor ***
		// *******************



		private IDMapping(String mappedID, boolean unified) {
			this.mappedID = mappedID;
			this.unified = unified;
		}



		// ***************
		// *** Methods ***
		// ***************



		public String toString() {
			return "(mapped ID: " + mappedID + ", unified: " + unified + ")";
		}

		private boolean unified() {
			return unified;
		}

		private String mappedID() {
			return mappedID;
		}
	}
}
