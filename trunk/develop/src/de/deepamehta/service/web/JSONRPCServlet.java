package de.deepamehta.service.web;

import de.deepamehta.BaseAssociation;
import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.ApplicationServiceHost;
import de.deepamehta.service.ApplicationServiceInstance;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.CorporateMemory;
import de.deepamehta.service.Session;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
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
import javax.xml.transform.TransformerFactory;



/**
 * <p>
 * <hr>
 * Last change: 4.7.2008 (2.0b8)<br>
 * J&ouml;rg Richter<br>
 * jri@deepamehta.de
 */
public class JSONRPCServlet extends HttpServlet implements ApplicationServiceHost, DeepaMehtaConstants {

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
				System.out.println("*** JSONRPCServlet.init(): " + e);
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
		System.out.println("--- JSONRPCServlet destroyed (" + getClass() + ") ---");
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



	protected String performAction(String topicId, String params, Session session, CorporateDirectives directives)
																										throws ServletException, DeepaMehtaException {
		 // delegate to implementing subclass ?
        throw new DeepaMehtaException(" unexpected method requested !");
	}

    protected String performPostRequest(String remoteMethod, String params, Session session, CorporateDirectives directives)
																										throws ServletException, DeepaMehtaException {
		 // delegate to implementing subclass ?
        throw new DeepaMehtaException(" unexpected method requested !");
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


	/**
     * If the request is a HTTP POST Request, it is considered as a JSON-RPC-Call
     * usage is simply a string "method: remoteMethodName, params: one:two:three"
     * If the request is a HTTP GET Request, it is considered as HTML-Request where
     * the topicId is appended to the URL e.g. /rpc/t-1234
     *
     * Consideration is not to make every RPC Call a POST Request -
     * This is referred as bad practice
     *
	 * @see		#doGet
	 * @see		#doPost
	 */
	private void performRequest(HttpServletRequest request, HttpServletResponse response)
															throws IOException, ServletException {
		RequestParameter params = new RequestParameter(request);
		Session session = getSession(request);
		CorporateDirectives directives = new CorporateDirectives();
		String method = request.getMethod();
        // if GET Print the resource if POST print send content/type
        System.out.println(">>> JSONRPCServlet: " + method + (!method.equals("GET") ? " (content type=\"" +
        request.getContentType() + "\")" : " on resource =\"" + request.getPathInfo() + "\""));
		//
        String remoteMethodCall = "";
        String remoteParams = "";
        if (method.equals("POST")) {
            // analyze body
            BufferedReader in = new BufferedReader(
                                new InputStreamReader(
                                request.getInputStream()));
            String requestBody;
            while ((requestBody = in.readLine()) != null) {
                String[] body = requestBody.split(",");
                // System.out.println("params (" + body.length+")");
                remoteMethodCall= body[0].substring(9);
                remoteParams = body[1].substring(9, body[1].length()-1);
                System.out.println(">>> POST BODY: \n" +
                        "       method: " + remoteMethodCall + ",\n" +
                        "       params: " + remoteParams);
            }
            in.close();
            // --- trigger performPostrequest() hook
            String result = performPostRequest(remoteMethodCall, remoteParams, session, directives);
            System.out.println(">>> POST RESULT: \n");
            System.out.println("    "+result + "\n");
            response.setContentType("applicaton/json");
            response.setStatus(response.SC_OK);
            // write back
            PrintWriter writer = response.getWriter();
            writer.print(result);
            writer.checkError();
            writer.close();
            //response.setHeader("Mime-Type", "");
        } else if (method.equals("GET")) {
            // topicId in der URL ?, trim first backslash
            String topicPath = request.getPathInfo().substring(1);
            // --- trigger performAction() hook ---
            String result = performAction(topicPath, remoteParams, session, directives);
            // --- trigger preparePage() hook ---
            preparePage(topicPath, params, session, directives);
            redirectToPage(result, request, response);
        }
		//
		// process directives
		directives.updateCorporateMemory(as, session, null, null);
		// Note: topicmapID=null, viewmode=null ### should be OK because in web interface there is no "default topicmap"
	}

	private void redirectToPage(String page, HttpServletRequest request, HttpServletResponse response)
																throws IOException, ServletException {
		sc.getRequestDispatcher("/pages/" + page + ".jsp").forward(request, response);
		// ### should the extension be part of the "page" parameter? Consider static .html files
	}

	// ---

	private Session getSession(HttpServletRequest request) {
		HttpSession session = request.getSession(false);	// create=false;
		if (session == null) {
			// --- create HTTP session ---
			session = request.getSession();
			System.out.println("=== JSONRPCServlet: HTTP session created ===");
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

}
