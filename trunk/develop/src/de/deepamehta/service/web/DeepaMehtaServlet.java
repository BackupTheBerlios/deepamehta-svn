package de.deepamehta.service.web;

import de.deepamehta.BaseAssociation;
import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.PropertyDefinition;
import de.deepamehta.Relation;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.ApplicationServiceHost;
import de.deepamehta.service.ApplicationServiceInstance;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.CorporateMemory;
import de.deepamehta.service.Session;
import de.deepamehta.topics.TypeTopic;
import de.deepamehta.util.CaseInsensitveHashtable;
import de.deepamehta.util.DeepaMehtaUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;



/**
 * <p>
 * <hr>
 * Last change: 4.7.2008 (2.0b8)<br>
 * J&ouml;rg Richter<br>
 * jri@deepamehta.de
 */
public class DeepaMehtaServlet extends HttpServlet implements ApplicationServiceHost, DeepaMehtaConstants {

	protected ServletContext sc;
	private String generatorMethod;			// HTML_GENERATOR_JSP or HTML_GENERATOR_XSLT
	//
	private DocumentBuilder docBuilder;		// only initialized for HTML_GENERATOR_XSLT
	private TransformerFactory tFactory;	// only initialized for HTML_GENERATOR_XSLT
	private String stylesheetName;			// only initialized for HTML_GENERATOR_XSLT

	protected ApplicationService as;
	protected CorporateMemory cm;

	private final String commInfo = "direct method calls from " + getClass();



	public void init() {
		sc = getServletContext();
		generatorMethod = sc.getInitParameter("generator");
		// set default
		if (generatorMethod == null) {
			generatorMethod = HTML_GENERATOR_JSP;
		}
		//
		if (generatorMethod.equals(HTML_GENERATOR_JSP)) {
			// ignore
		} else if (generatorMethod.equals(HTML_GENERATOR_XSLT)) {
			try {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				docBuilder = factory.newDocumentBuilder();					// throws PCE
				tFactory = TransformerFactory.newInstance();
				stylesheetName = sc.getInitParameter("stylesheet");
				if (stylesheetName == null) {
					throw new DeepaMehtaException("parameter \"stylesheet\" is missing in web.xml");
				}
			} catch (ParserConfigurationException e) {
				System.out.println("*** DeepaMehtaServlet.init(): " + e);
			}
		} else {
			throw new DeepaMehtaException("unexpected HTML generator method: \"" + generatorMethod +
				"\" -- expected values are \"jsp\" (default) and \"xslt\"");
		}
		// --- create application service ---
		String home = sc.getInitParameter("home");
		String service = sc.getInitParameter("service");
		// Note: the current working directory is the directory from where tomcat was started
		ApplicationServiceInstance instance = ApplicationServiceInstance.lookup(
			service, home != null ? home + "/install/config/dm.properties" : "../config/dm.properties");
		as = ApplicationService.create(this, instance);		// throws DME ### servlet is not properly inited
		cm = as.cm;
		// --- create external connection ---
		try {
			new ExternalConnection("localhost", instance.port, as);		// ### host and port should be context parameter
		} catch (IOException e) {
			System.out.println(">>> type synchronization NOT available (" + e + ")");
		}
		//
		System.out.println(">>> HTML generator method: \"" + generatorMethod + "\"");
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		performRequest(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		performRequest(request, response);	// ###
	}

	public void destroy() {
		System.out.println("--- DeepaMehtaServlet destroyed (" + getClass() + ") ---");
		as.shutdown();
	}



	// ********************************************************************************
	// *** Implementation of interface de.deepamehta.service.ApplicationServiceHost ***
	// ********************************************************************************



	public String getCommInfo() {
		return commInfo;
	}

	public void sendDirectives(Session session, CorporateDirectives directives,
									ApplicationService as, String topicmapID, String viewmode) {
		// ### do nothing
	}

	public void broadcastChangeNotification(String topicID) {
		// ### do nothing
	}



	// *************
	// *** Hooks ***
	// *************



	protected String performAction(String action, RequestParameter params, Session session, CorporateDirectives directives)
																										throws ServletException {
		throw new DeepaMehtaException("unexpected action: \"" + action + "\"");
	}

	protected void preparePage(String page, RequestParameter params, Session session, CorporateDirectives directives)
																										throws ServletException {
	}

	protected void addResources(HTMLGenerator html) {
		// ### must add main resources
	}



	// ***********************
	// *** Utility Methods ***
	// ***********************



	// --- createTopic (2 forms) ---

	/**
	 * Utility method to create a topic directly from form data.
	 */
	protected final String createTopic(String typeID, RequestParameter params, Session session, CorporateDirectives directives) {
		return createTopic(typeID, params, session, directives, null, null);
	}

	/**
	 * Utility method to create a topic directly from form data.
	 *
	 * @param	topicmapID	may be null. Might be required by specific application logic, as triggered e.g.
	 *						by the propertiesChanged() hook.
	 * @param	topicID		the ID for the newly created topic. If <code>null</code>, a new topic ID is created.
	 *
	 * @return	the ID of the created topic
	 */
	protected final String createTopic(String typeID, RequestParameter params, Session session, CorporateDirectives directives,
																					String topicmapID, String topicID) {
		if (topicID == null) {
			topicID = as.getNewTopicID();
		}
		processForm(typeID, topicID, params.getTable(), true, session, directives, topicmapID, VIEWMODE_USE);		// doCreate=true
		//
		return topicID;
	}

	// --- updateTopic (2 forms) ---

	protected final String updateTopic(String typeID, RequestParameter params, Session session, CorporateDirectives directives) {
		return updateTopic(typeID, params, session, directives, null, null);
	}

	protected final String updateTopic(String typeID, RequestParameter params, Session session, CorporateDirectives directives,
																String topicmapID, String viewmode) {
		String topicID = params.getValue("id");												// ### hardcoded
		processForm(typeID, topicID, params.getTable(), false, session, directives, topicmapID, viewmode);			// doCreate=false
		//
		return topicID;
	}

	// ---

	protected final void deleteTopic(String topicID) {
		CorporateDirectives directives = as.deleteTopic(topicID, 1);	// ### version=1
		directives.updateCorporateMemory(as, null, null, null);
	}

	protected final void deleteAssociation(String typeID, String topicID1, String topicID2) {
		BaseAssociation assoc = cm.getAssociation(typeID, topicID1, topicID2);
		CorporateDirectives directives = as.deleteAssociation(assoc);		// ### session=null
		directives.updateCorporateMemory(as, null, null, null);
	}

	// ---

	protected final void createAssignments(String topicID, String assocTypeID, String[] values) {
		// ### System.out.println("  > relation --> \"" + rel.relTopicTypeID + "\" (" + rel.name + "): " + values.length + " values");
		for (int i = 0; i < values.length; i++) {
			String value = values[i];
			// ### System.out.println("  > \"" + value + "\"");
			if (!value.equals(VALUE_NOT_SET)) {
				cm.createAssociation(as.getNewAssociationID(), 1, assocTypeID, 1, topicID, 1, value, 1);
			}
		}
	}

	protected final void removeAssignments(String topicID, String relTopicTypeID, String assocTypeID) {
		Enumeration e = as.getRelatedTopics(topicID, assocTypeID, relTopicTypeID, 2, false,
			true).elements();	// ordered=false, emptyAllowed=true ### relTopicPos=2 hardcoded
		while (e.hasMoreElements()) {
			BaseTopic topic = (BaseTopic) e.nextElement();
			BaseAssociation assoc = cm.getAssociation(assocTypeID, topicID, topic.getID());	// ### fixed order
			as.deleteAssociation(assoc);	// ### session=null
		}
	}

	// ---

	protected final Vector substract(Vector v1, Vector v2) {
		Enumeration e = v2.elements();
		while (e.hasMoreElements()) {
			v1.removeElement((BaseTopic) e.nextElement());
		}
		return v1;
	}

	// ---

	protected final BaseTopic getUser(Session session) {
		return (BaseTopic) session.getAttribute("user");
	}

	protected final String getUserID(Session session) {
		return getUser(session).getID();
	}

	// --- getTopicTree (5 forms) ---

	protected final TopicTree getTopicTree(String topicID) {
		BaseTopic topic = cm.getTopic(topicID, 1);	// ### version=1
		return getTopicTree(topicID, topic.getType(), ASSOCTYPE_COMPOSITION);
	}

	protected final TopicTree getTopicTree(String topicID, String childTypeID, String assocTypeID) {
		return getTopicTree(topicID, childTypeID, assocTypeID, true);	// sortAssociations=true
	}

	// sort by association order

	protected final TopicTree getTopicTree(String topicID, String childTypeID, String assocTypeID,
																		boolean sortAssociations) {
		return getTopicTree(topicID, childTypeID, assocTypeID, null, sortAssociations, false, -1, -1);
	}

	// sort by topic properties

	protected final TopicTree getTopicTree(String topicID, String childTypeID, String assocTypeID,
																		String[] sortTopicProps, boolean descending) {
		return getTopicTree(topicID, childTypeID, assocTypeID, sortTopicProps, false, descending, -1, -1);
	}

	protected final TopicTree getTopicTree(String topicID, String childTypeID, String assocTypeID,
																		String[] sortTopicProps, boolean descending,
																		int pageNr, int pageSize) {
		return getTopicTree(topicID, childTypeID, assocTypeID, sortTopicProps, false, descending, pageNr, pageSize);
	}



	// ***********************
	// *** Private Methods ***
	// ***********************



	private TopicTree getTopicTree(String topicID, String childTypeID, String assocTypeID,
													String[] sortTopicProps, boolean sortAssociations, boolean descending,
													int pageNr, int pageSize) {
		// ### System.out.println(">>> getTopicTree(): sortTopicProps=" + sortTopicProps + " sortAssociations=" + sortAssociations);
		BaseTopic topic = cm.getTopic(topicID, 1);	// ### version=1
		TopicTree tree = new TopicTree(topic);
		addChilds(0, tree, childTypeID, assocTypeID, sortTopicProps, sortAssociations, descending, pageNr, pageSize);
		// ### System.out.println(">>> getTopicTree(): finished");
		return tree;
	}

	// ---

	/**
	 * @see		#getTopicTree(String topicID, String childTypeID, String assocTypeID, String[] sortTopicProps, boolean sortAssociations)
	 * @see		#addChilds
	 */
	private void addChilds(int level, TopicTree tree, String childTypeID, String assocTypeID,
													String[] sortTopicProps, boolean sortAssociations, boolean descending,
													int pageNr, int pageSize) {
		// --- get child topics ---
		Vector childs;
		if (sortTopicProps != null) {
			childs = cm.getRelatedTopics(tree.topic.getID(), assocTypeID, childTypeID, 2, sortTopicProps, descending);
		} else {
			childs = cm.getRelatedTopics(tree.topic.getID(), assocTypeID, childTypeID, 2, sortAssociations);
		}
		// --- paging ---
		tree.totalChildCount = childs.size();
		Enumeration e = childs.elements();
		int addCount;
		if (level == 0 && pageNr != -1) {
			// skipping
			int skipCount = pageNr * pageSize;
			for (int i = 0; i < skipCount; i++) {
				e.nextElement();
			}
			//
			addCount = Math.min(tree.totalChildCount - skipCount, pageSize);
		} else {
			addCount = tree.totalChildCount;
		}
		// --- add child trees ---
		Vector v = tree.childTopics;
		for (int i = 0; i < addCount; i++) {
			BaseTopic topic = (BaseTopic) e.nextElement();
			TopicTree t = new TopicTree(topic);
			v.addElement(t);
			addChilds(level + 1, t, childTypeID, assocTypeID, sortTopicProps, sortAssociations, descending,
				pageNr, pageSize);	// recursive call
		}
	}

	// --- processForm (2 forms) ---

	/**
	 * Processes a topic form. Called recusively to process embedded forms.
	 * <p>
	 * Used for both: "create topic" (<code>doCreate=true</code>) and
	 * "update topic" (<code>doCreate=false</code>) actions.
	 *
	 * @param	typeID		the topic type of the form resp. embedded form
	 * @param	topicID		the ID of the topic to be created resp. updated
	 * @param	params		all request parameters as hashtable
	 *
	 * @see		#createTopic
	 * @see		#updateTopic
	 * @see		#updateStrongRelations
	 * @see		de.deepamehta.messageboard.MessageBoardServlet#performAction
	 */
	private void processForm(String typeID, String topicID, Hashtable params, boolean doCreate, Session session,
																								CorporateDirectives directives) {
		processForm(typeID, topicID, params, doCreate, session, directives, null, null);
	}

	private void processForm(String typeID, String topicID, Hashtable params, boolean doCreate, Session session,
															CorporateDirectives directives, String topicmapID, String viewmode) {
		System.out.println(">>> processForm(): doCreate=\"" + doCreate +
			"\" typeID=\"" + typeID + "\" topicID=\"" + topicID + "\"");
		//
		if (doCreate) {
			// ### Note: the topic name is not yet set ("") but only through topic naming behavoir as triggered by
			// setTopicProperties(). As a consequence the topic name is not known inside the propertyChangeAllowed() hook
			// and can't be used for reporting -- bad ### not true anymore, 4.7.2008
			// Note: the topic name is temprarily set to the value of the "Name" field.
			String[] val = (String[]) params.get(PROPERTY_NAME);
			String name = val != null ? val[0] : "";
			//
			as.createLiveTopic(topicID, typeID, name /* "" */, topicmapID, viewmode, session, directives);
		}
		// properties
		Hashtable props = getProperties(params, typeID);
		directives.add(as.setTopicProperties(topicID, 1, props, topicmapID, session));
		// weak relations
		updateWeakRelations(typeID, topicID, getWeakRelationParameters(params));
		// strong relations
		updateStrongRelations(typeID, topicID, getStrongRelationParameters(params), doCreate, session, directives);
	}

	// ---

	/**
	 * Processes an association form.
	 * <p>
	 * Hint to application developers: your derived servlet will call this method to perform "create association"
	 * (<code>doCreate=true</code>) and "update association" (<code>doCreate=false</code>) actions.
	 *
	 * @param	assocID		the ID of the association to be created resp. updated
	 * @param	params		all request parameters as hashtable
	 * @param	doCreate	if <code>true</code> the association is created, otherwise updated
	 * @param	topicID1	only used for <code>doCreate=true</code>
	 * @param	topicID2	only used for <code>doCreate=true</code>
	 */
	private void processForm(String typeID, String assocID, Hashtable params, boolean doCreate,
																		String topicID1, String topicID2, Session session) {
		System.out.println(">>> processForm(): doCreate=\"" + doCreate + "\" typeID=\"" + typeID +
			"\" assocID=\"" + assocID + "\"");
		CorporateDirectives directives = new CorporateDirectives();
		//
		if (doCreate) {
			as.createLiveAssociation(assocID, typeID, topicID1, topicID2, session, directives);
		}
		//
		Hashtable props = getProperties(params, typeID);
		// properties
		directives.add(as.setAssocProperties(assocID, 1, props, null, null, session));
		directives.updateCorporateMemory(as, session, null, null);
	}

	// ---

	/**
	 * @see		#doGet
	 * @see		#doPost
	 */
	private void performRequest(HttpServletRequest request, HttpServletResponse response)
															throws IOException, ServletException {
		RequestParameter params = new RequestParameter(request);
		Session session = getSession(request);
		CorporateDirectives directives = new CorporateDirectives();
		String action = params.getValue("action");
		String method = request.getMethod();
		System.out.println(">>> DeepaMehtaServlet: " + method + (!method.equals("GET") ? " (content type=\"" +
			request.getContentType() + "\")" : "") + " action=\"" + action + "\"");
		//
		// --- trigger performAction() hook ---
		String page = performAction(action, params, session, directives);
		//
		// --- trigger preparePage() hook ---
		preparePage(page, params, session, directives);
		//
		// process directives
		directives.updateCorporateMemory(as, session, null, null);
		// Note: topicmapID=null, viewmode=null ### should be OK because in web interface there is no "default topicmap"
		//
		if (generatorMethod.equals(HTML_GENERATOR_JSP)) {
			redirectToPage(page, request, response);
		} else if (generatorMethod.equals(HTML_GENERATOR_XSLT)) {
			buildPage(page, session, request, response);
		} else {
			// ### never happens
			throw new DeepaMehtaException("unexpected HTML generator method: " + generatorMethod);
		}
	}

	// ---

	private void redirectToPage(String page, HttpServletRequest request, HttpServletResponse response)
																throws IOException, ServletException {
		sc.getRequestDispatcher("/pages/" + page + ".jsp").forward(request, response);
		// ### should the extension be part of the "page" parameter? Consider static .html files
	}

	private void buildPage(String page, Session session, HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		try {
			Document doc = createDocument(page, session, request);
			//
			writer = response.getWriter();												// throws IO
			boolean debug = request.getParameter("debug") != null;
			Transformer transformer;
			if (!debug) {
				File stylesheet = new File("stylesheets/" + stylesheetName);
				transformer = tFactory.newTransformer(new StreamSource(stylesheet));	// throws TCE
			} else {
				transformer = tFactory.newTransformer();								// throws TCE
			}
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(new DOMSource(doc), new StreamResult(writer));		// throws TE
			//
		} catch (TransformerConfigurationException e) {
			System.out.println("*** DeepaMehtaServlet.buildPage(): " + e);
			writer.println("<html><body><h2>Error on page \"" + page + "\"</h2>" +
				"<code>" + e + "</code></body></html>");
		} catch (TransformerException e) {
			System.out.println("*** DeepaMehtaServlet.buildPage(): " + e + " (" + e.getMessageAndLocation() + ")");
		} catch (IOException e) {
			System.out.println("*** DeepaMehtaServlet.buildPage(): " + e);
		}
	}

	// ---

	/**
	 * Creates a DOM document that holds the data for the specified page.
	 * This "page data" consists of
	 * <UL>
	 * <LI>Session Attributes
	 * <LI>Request Attributes
	 * <LI>Type Information
	 * </UL>
	 */
	private Document createDocument(String page, Session session, HttpServletRequest request) {
		Document doc = docBuilder.newDocument();
		Element root = (Element) doc.createElement("page");
		Hashtable types = new Hashtable();		// collects the types to be exported
		doc.appendChild(root);
		root.setAttribute("name", page);
		root.setAttribute("baseURL", as.getCorporateWebBaseURL());	// ### used for accessing images
		// add session attributes
		Enumeration e = session.getAttributeNames();
		while (e.hasMoreElements()) {
			String name = (String) e.nextElement();
			Object value = session.getAttribute(name);
			addAttribute(doc, name, value, types);
		}
		// add request attributes
		e = request.getAttributeNames();
		while (e.hasMoreElements()) {
			String name = (String) e.nextElement();
			Object value = request.getAttribute(name);
			addAttribute(doc, name, value, types);
		}
		// add types
		e = types.keys();
		while (e.hasMoreElements()) {
			String typeID = (String) e.nextElement();
			as.type(typeID, 1).exportTypeDefinition(doc);
		}
		//
		return doc;
	}

	/**
	 * @see		#createDocument
	 */
	private void addAttribute(Document doc, String name, Object value, Hashtable types) {
		Element root = doc.getDocumentElement();
		if (value instanceof String) {
			Element param = doc.createElement("param"); 
			root.appendChild(param);
			param.setAttribute("name", name);
			// ### quick hack for forms
			if (name.equals("typeID")) {
				collectType((String) value, types);
			}
			//
			param.appendChild(doc.createTextNode((String) value));
		} else if (value instanceof BaseTopic) {
			Element param = doc.createElement("param"); 
			root.appendChild(param);
			param.setAttribute("name", name);
			//
			addObject(doc, param, value, types);
		} else if (value instanceof Vector) {
			Element param = doc.createElement("param"); 
			root.appendChild(param);
			param.setAttribute("name", name);
			//
			Enumeration e = ((Vector) value).elements();
			while (e.hasMoreElements()) {
				Object element = e.nextElement();
				addObject(doc, param, element, types);
			}
		} else if (value instanceof HTMLGenerator) {
			// ignore
		} else if (value instanceof WebSession) {
			// ignore
		} else {
			throw new DeepaMehtaException("unexpected session attribute: " + value);
		}
	}

	/**
	 * @param	types	collects the types to be exported
	 */
	private void addObject(Document doc, Element parent, Object o, Hashtable types) {
		if (o instanceof BaseTopic) {
			BaseTopic topic = (BaseTopic) o;
			String typeID = topic.getType();
			String iconfile = as.getLiveTopic(topic).getIconfile();
			// topic
			Element topicElement = doc.createElement("topic");
			parent.appendChild(topicElement);
			topicElement.setAttribute("ID", topic.getID());
			topicElement.setAttribute("types", typeID);
			topicElement.setAttribute("icon", iconfile);
			// name
			Element topnameElement = doc.createElement("topname");
			Element basenameElement = doc.createElement("basename");
			topicElement.appendChild(topnameElement);
			topnameElement.appendChild(basenameElement);
			basenameElement.appendChild(doc.createTextNode(topic.getName()));
			// properties
			Hashtable props = as.getTopicProperties(topic);
			Enumeration e = props.keys();
			while (e.hasMoreElements()) {
				String name = (String) e.nextElement();
				String value = (String) props.get(name);
				Element propElement = doc.createElement("property");
				topicElement.appendChild(propElement);
				propElement.setAttribute("name", name);
				if (value.startsWith("<html>")) {	// ### should ask typedef for "Text Editor"
					try {
						value = DeepaMehtaUtils.html2xml(value);
						Document htmlDoc = docBuilder.parse(new InputSource(new StringReader(value)));
						Element root = htmlDoc.getDocumentElement();
						Node html = doc.importNode(root, true);	// ### deep=true
						propElement.appendChild(html);
					} catch (IOException iox) {
						System.out.println("*** error while parsing property \"" + name + "\": " + iox);
					} catch (SAXException se) {
						System.out.println("*** error while parsing property \"" + name + "\": " + se);
					}
				} else {
					propElement.appendChild(doc.createTextNode(value));
				}
			}
			//
			collectType(typeID, types);
		} else {
			throw new DeepaMehtaException("unexpected session attribute: " + o + " (" + o.getClass() + ")");
		}
	}

	/**
	 * Collects the specified type and all of its supertypes.
	 *
	 * @param	types	the collecting parameter
	 */
	private void collectType(String typeID, Hashtable types) {
		TypeTopic type = as.type(typeID, 1);
		while (type != null) {
			types.put(type.getID(), type.getID());
			type = type.getSupertype();
		}
	}

	// ---

	/**
	 * @see		#processForm
	 */
	private void updateWeakRelations(String typeID, String topicID, Hashtable params) {
		// ### System.out.println(">>> update relations of topic \"" + topicID + "\"");
		TypeTopic type = as.type(typeID, 1);	// ### version=1
		Enumeration e = params.keys();
		while (e.hasMoreElements()) {
			String relID = (String) e.nextElement();
			Relation rel = type.getRelation(relID);
			// remove existing assignments
			removeAssignments(topicID, rel);
			// create new assignments
			String[] values = (String[]) params.get(relID);
			createAssignments(topicID, rel, values);
		}
	}

	/**
	 * Called recursively (indirect).<br>
	 * ### Note: the <code>doCreate</code> parameter may toggle at each call.
	 *
	 * @see		#processForm
	 */
	private void updateStrongRelations(String typeID, String topicID, Hashtable params, boolean doCreate, Session session,
																								CorporateDirectives directives) {
		TypeTopic type = as.type(typeID, 1);	// ### version=1
		Enumeration e = params.keys();
		while (e.hasMoreElements()) {
			String relID = (String) e.nextElement();
			Relation rel = type.getRelation(relID);
			Hashtable strongParams = (Hashtable) params.get(relID);
			String relTopicID = null;
			boolean newDoCreate = doCreate;
			if (!newDoCreate) {
				relTopicID = ((String[]) strongParams.get("id"))[0];
				// ### error check
				if (relTopicID.equals("null")) {	// ###
					Vector relTopics = as.getRelatedTopics(topicID, rel.assocTypeID, rel.relTopicTypeID, 2,
						false, true);	// ordered=false, emptyAllowed=true ### relTopicPos=2 hardcoded
					if (relTopics.size() == 0) {
						// toggle from "update" to "create" mode
						newDoCreate = true;
						// ### Note: once toggled to "create" mode we will never switch back to "update"
						System.out.println(">>> updateStrongRelations(): \"" + typeID + "\" (" +
							topicID + ") is missing a related \"" + rel.relTopicTypeID + "\" topic -- switch " +
							"to \"create\" mode");
					} else {
						System.out.println(">>> updateStrongRelations(): related \"" + rel.relTopicTypeID +
							"\" topic of \"" + typeID + "\" (" + topicID + ") meanwhile created through " +
							"RELOAD -- operation is aborted");
						break;
					}
				}
			}
			if (newDoCreate) {
				relTopicID = as.getNewTopicID();
				cm.createAssociation(as.getNewAssociationID(), 1, rel.assocTypeID, 1, topicID, 1, relTopicID, 1);
			}
			// recursive call (indirect)	// ### topicmapID, viewmode?
			processForm(rel.relTopicTypeID, relTopicID, strongParams, newDoCreate, session, directives);
		}
	}

	// ---

	/**
	 * ### inline?
	 */
	private void removeAssignments(String topicID, Relation rel) {
		removeAssignments(topicID, rel.relTopicTypeID, rel.assocTypeID);
	}

	/**
	 * ### inline?
	 */
	private void createAssignments(String topicID, Relation rel, String[] values) {
		createAssignments(topicID, rel.assocTypeID, values);
	}

	// ---

	private Session getSession(HttpServletRequest request) {
		HttpSession session = request.getSession(false);	// create=false;
		if (session == null) {
			// --- create HTTP session ---
			session = request.getSession();
			System.out.println("=== DeepaMehtaServlet: HTTP session created ===");
			//
			session.setAttribute("session", new WebSession(session));
			// --- create HTML generator ---
			if (generatorMethod.equals(HTML_GENERATOR_JSP)) {
				String language = sc.getInitParameter("language");
				String country = sc.getInitParameter("country");
				Locale locale = language != null && country != null ? new Locale(language, country) : null;
				HTMLGenerator html = new HTMLGenerator(as, locale);
				// trigger addResources() hook
				addResources(html);
				//
				session.setAttribute("html", html);
			}
		}
		return (Session) session.getAttribute("session");
	}

	// ---

	/**
	 * ### Converts the specified request parameters (originating from a topic/association form)
	 * into topic/association properties. Basically two conversions are performed:
	 * <ol>
	 *	<li>Composed properties are merged into one property. Composed properties are such that results from more than
	 *		one form field, e.g. date and time selectors</li>
	 *	<li>Properties of related topics are filtered out.</li>
	 * </ol>
	 *
	 * @see		#processForm
	 */
	private final Hashtable getProperties(Hashtable params, String typeID) {
		Hashtable props = new CaseInsensitveHashtable();		// return object
		Hashtable compProps = new CaseInsensitveHashtable();	// intermediate table to compile the single values of composed properties
		//
		Enumeration e = params.keys();
		while (e.hasMoreElements()) {
			String propName = (String) e.nextElement();
			String propValue = ((String[]) params.get(propName))[0];
			// ### skip "action", "button", "id", "typeID" properties
			// ### as well as properties with empty value
			if (propValue.equals("") || propName.equals("action") || propName.equals("button") ||
				propName.equals("id") || propName.equals("typeID")) {
				// Note: the internally used parameters "action" and "button" must be removed here
				// to not confuse getWeakRelationParameters()
				if (propValue.equals("") || propName.equals("action") || propName.equals("button")) {
					params.remove(propName);
				}
				//
				continue;
			}
			int pos = propName.indexOf(PARAM_SEPARATOR);
			if (pos != -1) {
				String propertyName = propName.substring(0, pos);
				if (propertyName.equals(PARAM_RELATION)) {
					// value is part of relation
					continue;
				}
				// value is part of composed date/time value
				int partNumber = Integer.parseInt(propName.substring(pos + 1));
				String[] compProp = (String[]) compProps.get(propertyName);
				if (compProp == null) {
					compProp = new String[3];
					compProps.put(propertyName, compProp);
				}
				compProp[partNumber] = propValue;
			} else {
				// value is simple property value
				PropertyDefinition propDef = as.type(typeID, 1).getPropertyDefinition(propName);
        			if (propDef == null) { // ### FIXME: can happen if property label contain a "
		        		System.out.println("ERROR *** cannot getProperty() by propName=\"" + propName + "\" skipping saving this prop");
				} else {
					// ### System.out.println(">>> DeepaMehtaServlet.getProperties(): typeID=\"" + typeID + "\" propName=\"" + propName + "\" propDef=" + propDef);
				        if (propDef.getVisualization().equals(VISUAL_TEXT_EDITOR)) {
				        	propValue = DeepaMehtaUtils.replace(propValue, '\r', "<br>");		// ### was <br/>
			        		if (!propValue.startsWith("<html>")) {	// ### must ignore case. <HTML> used in help texts?
							propValue = "<html><body><p>" + propValue + "</p></body></html>";	// ### <html>
            					}
	          			}
        	  			props.put(propName, propValue);
        			}
			}
			// remove processed parameter
			if (params.remove(propName) == null) {
				throw new DeepaMehtaException("parameter \"" + propName + "\" not found");
			}
		}
		// assemble composed properties from compiled single values
		e = compProps.keys();
		while (e.hasMoreElements()) {
			String propName = (String) e.nextElement();
			String[] compProp = (String[]) compProps.get(propName);
			String propValue;
			if (compProp[2] != null) {
				// date property
				propValue = compProp[0] + DATE_SEPARATOR + compProp[1] + DATE_SEPARATOR + compProp[2];
			} else {
				// time property
				propValue = compProp[0] + TIME_SEPARATOR + compProp[1];
			}
			props.put(propName, propValue);
		}		
		//
		return props;
	}

	/**
	 * @param	params		### all request parameters as hashtable
	 *
	 * @return	the parameter values for weak relations (weak means: non-strong) as hashtable,<br>
	 *			key: relation ID (string)<br>
	 *			value: IDs of selected topics (array of strings)
	 *
	 * @see		#processForm
	 */
	private Hashtable getWeakRelationParameters(Hashtable params) {
		Hashtable relParams = new Hashtable();
		//
		Enumeration e = params.keys();
		while (e.hasMoreElements()) {
			String propName = (String) e.nextElement();
			// ### skip "id" parameter
			if (propName.equals("id")) {
				continue;
			}
			// only level 0 is processed here.
			// deeper levels are processed via recursion.
			if (propName.indexOf(LEVEL_SEPARATOR) >= 0) {
				continue;
			}
			int pos = propName.indexOf(PARAM_SEPARATOR);
			// error check
			if (pos == -1) {
				throw new DeepaMehtaException("no PARAM_SEPARATOR \"" + PARAM_SEPARATOR + "\" found in \"" + propName + "\"");
			}
			//
			String relID = propName.substring(pos + 1);
			String[] values = (String[]) params.get(propName);
			relParams.put(relID, values);
			// remove processed parameter
			if (params.remove(propName) == null) {
				throw new DeepaMehtaException("parameter \"" + propName + "\" not found");
			}
		}
		//
		return relParams;
	}

	/**
	 * @param	params		### all request parameters as hashtable
	 *
	 * @return	the parameters of related topics (via strong relation) as hashtable,<br>
	 *			key: relation ID (string)<br>
	 *			value: parameter of related topic as hashtable,<br>
	 *			&nbsp;&nbsp;&nbsp;&nbsp;key: property name (string)<br>
	 *			&nbsp;&nbsp;&nbsp;&nbsp;value: property value (string)
	 *
	 * @see		#processForm
	 */
	private Hashtable getStrongRelationParameters(Hashtable params) {
		Hashtable relParams = new Hashtable();
		//
		Enumeration e = params.keys();
		while (e.hasMoreElements()) {
			String paramName = (String) e.nextElement();
			// ### skip "id" parameter
			if (paramName.equals("id")) {
				continue;
			}
			//
			int pos = paramName.indexOf(PARAM_SEPARATOR);
			// error check 1
			if (pos == -1) {
				throw new DeepaMehtaException("no PARAM_SEPARATOR \"" + PARAM_SEPARATOR + "\" found in \"" + paramName + "\"");
			}
			// error check 2
			if (!paramName.substring(0, pos).equals(PARAM_RELATION)) {
				throw new DeepaMehtaException("no PARAM_RELATION \"" + PARAM_RELATION + "\" found at beginning of \"" + paramName + "\"");
			}
			//
			int pos2 = paramName.indexOf(LEVEL_SEPARATOR, pos + 1);
			// error check 3
			if (pos2 == -1) {
				throw new DeepaMehtaException("no LEVEL_SEPARATOR \"" + LEVEL_SEPARATOR + "\" found in \"" + paramName + "\"");
			}
			//
			String relID = paramName.substring(pos + 1, pos2);
			Hashtable nextLevelParams = (Hashtable) relParams.get(relID);
			if (nextLevelParams == null) {
				nextLevelParams = new Hashtable();
				relParams.put(relID, nextLevelParams);
			}
			String nextLevelParamName = paramName.substring(pos2 + 1);	// strip relation ID
			String[] values = (String[]) params.get(paramName);
			nextLevelParams.put(nextLevelParamName, values);
		}
		//
		return relParams;
	}
}
