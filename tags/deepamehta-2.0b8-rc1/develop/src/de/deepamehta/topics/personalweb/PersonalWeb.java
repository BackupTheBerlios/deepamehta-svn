// PersonalWeb
// by Joerg Richter
// jri@prz.tu-berlin.de
// 
// 1.0b1	 7.10.96	first release
// 1.0b2	22.10.96	also handles CGI calls
//						batch mode
//						considering local files improved
//						statistics is logged
//						user interface polished
//						more efficient memory usage
// 1.0b3     2.12.96    also handles links inside <FRAME> and <SCRIPT>
//						converts all links into relative URLs
//						preserves settings
//						more detailed status information
//						show visited and rejected sites
//						show legend
// 1.0b4	 17.4.97	HTML conversion fixed
//						settings are preserved also in interactive mode
//						more log informatition (visited URLs, visited sites, rejected sites)
//						CGI call detection improved
// 1.0b5      9.1.98    doesn't rely on the "pat" package for regular expressions anymore
//						all links are converted (to obtain real transparency)
// 1.0b6   25.7.2001	adapted for DeepaMehta
//							- GUI dropped
//							- creates graph of Webpage topics
//						now also handles framesets, stylesheets and javascripts
//						HTML conversion improved (more tolerant against HTML-coding errors)
//						supports more media types: ra, pdf, shtml, css, js, exe, tar...
//						statistics extended, e.g. error log
//						naming of logfiles polished
//						bug fixed: "site-changing" links were not handled correctly when re-parsing a local HTML file
//						bug fixed: pages containing news: or ftp: links gets corrupted while HTML conversion
// 1.0b7    7.8.2002    original HTML sources are backed up
//						improved parsing: comments and thus JavaScripts are ignored
//						new setting: "friendly", if set the fetch thread sleeps between downloads
// 1.0b8    5.2.2005    improved parsing: better recognition of HTML attributes
//						default settings have changed: only the start page is retrieved
//						more file types are recognized (jpeg, png, mp3)


package de.deepamehta.topics.personalweb;

import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.PresentableAssociation;
import de.deepamehta.PresentableTopic;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.DeepaMehtaServiceUtils;
import de.deepamehta.service.Session;
import de.deepamehta.util.DeepaMehtaUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;



public class PersonalWeb implements DeepaMehtaConstants {

			String rootPath;			// URL of start page (this is truncated to a directory path)
	private String localPath;			// Path of Personal Webspace, e.g. "/home/httpd/html/pw/"
	private URL rootURL;
	private PrintStream log;			// logfile
	// DeepaMehta specific
			ApplicationService as;
			Session session;
			String topicmapID;
			String viewmode;
	// crawler threads
			FetchThread	fetchThread;	// thread for fetching content files
			ParseThread	parseThread;	// thread for parsing HTML pages
			boolean aborted = false;
	// crawler settings ### constants for now
	public boolean images = true;		// "include images"
	public boolean external = false;	// "include external documents"
	public boolean cgi = false;			// "include cgi calls"
	public boolean prepare = true;		// "prepare for offline browsing"
	public boolean consider = false;	// "consider local files"
	public boolean logfile = true;		// "create logfile"
	public boolean friendly = false;	// sleep between downloads
	// statistics
			int parseCount = 0;			// number of parses
			int fetchCount = 0;			// number of followed URLs (loads and retrieves)
			int loadCount = 0;			//     number of loads of local files
			int retrieveCount = 0;		//     number of retrieved from network
			int loadByteCount = 0;		//     number of loaded bytes
			int retrieveByteCount = 0;	//	   number of retrieved bytes
			int errorCount = 0;			//     number of errors
			int noParsingNeeded = 0;	//     number of URL's downloaded already and don't needs to get parsed
			int linkCount = 0;			// number of detected links
			int documentCount = 0;		//     number of tree-documents links
			int externalCount = 0;		//     number of external documents links
			int imageCount = 0;			//     number of image links
			int cgiCount = 0;			//     number of cgi call links
			int cssCount = 0;			//     number of stylesheets
			int frameCount = 0;			//     number of frames
			int scriptCount = 0;		//     number of javascripts
			int unfollowCount = 0;		//     number of unfollowed links
			int nonHTTPCount = 0;		// number of non-HTTP links
	private StringBuffer visitedURLs = new StringBuffer();		// ### content of gui.contentArea
	private StringBuffer visitedSites = new StringBuffer();		// ### content of gui.visitedArea
	private StringBuffer notVisitedSites = new StringBuffer();	// ### content of gui.notVisitedArea
	private StringBuffer errors = new StringBuffer();



	// *******************
	// *** Constructor ***
	// *******************



	/**
	 * @see		de.deepamehta.topics.WebpageTopic#download
	 */
	public PersonalWeb(String startURL, String webpageID, String websiteID,
									ApplicationService as, Session session,
									String topicmapID, String viewmode) {
		try {
			// --- initialize member variables ---
			this.rootURL = new URL(startURL);
			this.rootPath = startURL.substring(0, startURL.lastIndexOf('/') + 1);
			this.localPath = FILESERVER_WEBPAGES_PATH;	// ###
			createLogfile(rootURL, session.getUserName());
			this.as = as;
			this.session = session;
			this.topicmapID = topicmapID;
			this.viewmode = viewmode;
			// --- reporting ---
			writeLog("--- PersonalWeb 1.0b8 ---");
			writeLog("Start page: \"" + startURL + "\" (" + webpageID + ", website=" +
				websiteID + ")");
			writeLog("Local path: \"" + localPath + '"');
			writeLog("Include external documents  : " + external);
			writeLog("Include images              : " + images);
			writeLog("Include CGI calls           : " + cgi);
			writeLog("Prepare for offline browsing: " + prepare);
			writeLog("Consider local files        : " + consider);
			writeLog("Create logfile              : " + logfile);
			writeLog("Be friendly                 : " + friendly + (friendly ?
				" (waiting " + WEBCRAWLER_NICENESS + " secs between downloads)" : "") + '\n');
			// --- create crawler threads ---
			fetchThread = new FetchThread(this);
			parseThread = new ParseThread(this);
			fetchThread.setPriority(fetchThread.getPriority() - 1);
			parseThread.setPriority(parseThread.getPriority() - 2);
			writeLog("fetch-thread priority: " + fetchThread.getPriority());
			writeLog("parse-thread priority: " + parseThread.getPriority());
			// --- put start URL into fetch-queue ---
			putInFetchQueue(new ItemToFetch(rootURL, webpageID, websiteID));
			// --- start crawler threads ---
			fetchThread.start();
			parseThread.start();
		} catch (MalformedURLException e) {
			writeLog("*** PersonalWeb(): " + e);
		}
	}



	// ***************
	// *** Methods ***
	// ***************



	public void stop() {
		aborted = true;
	}

	void writeLog(String text) {
		if (LOG_PW) {
			System.out.println(text);
		}
		if (logfile) {
			log.println(text);
		}
	}

	// ---

	/**
	 * @see		FetchThread#run
	 * @see		ParseThread#run
	 */
	void finish() {
		writeLog("- PersonalWeb.finish()");
		writeLog("\nVisited URLs:\n");
		writeLog(visitedURLs.toString());
		writeLog("\nVisited sites:\n");
		writeLog(visitedSites.toString());
		writeLog("\nNot visited sites:\n");
		writeLog(notVisitedSites.toString());
		writeLog("\nNumber of followed URLs: " + fetchCount);
		writeLog("    " + retrieveCount + " received from network (" + retrieveByteCount / 1024 + "K)");
		writeLog("    " + loadCount + " loaded from local filesystem (" + loadByteCount / 1024 + "K)");
		writeLog("    " + noParsingNeeded + " exists locally and requires no parsing");
		writeLog("    " + errorCount + " errors");
		writeLog("Number of parsed files: " + parseCount);
		writeLog("Number of detected HTTP links: " + linkCount); 
		writeLog("    " + documentCount + " documents in tree");
		writeLog("    " + externalCount + " external documents");
		writeLog("    " + imageCount + " images");
		writeLog("    " + frameCount + " frames");
		writeLog("    " + cgiCount + " CGI calls");
		writeLog("    " + scriptCount + " javascripts");
		writeLog("    " + cssCount + " stylesheets");
		writeLog("    " + unfollowCount + " URLs unfollowed");
		writeLog("Number of detected non-HTTP links: " + nonHTTPCount);
		writeLog("\n\nErrors:\n");
		writeLog(errors.toString());
		closeLogfile();
	}

	// ---

	/**
	 * Puts the specified URL into the fetch-queue
	 *
	 * @see		#PersonalWeb
	 * @see		ParseThread#parseHTML
	 */
	void putInFetchQueue(ItemToFetch item) {
		// Note: contains() can be used here because ItemToFetch overrides equals()
		if (!fetchThread.fetchQueue.contains(item)) {
			addVisitedURL(item.url.toString());
			fetchThread.fetchQueue.put(item);
		}
	}

	/**
	 * Puts the specified item into the parse-queue
	 *
	 * @see		FetchThread#run
	 */
	void putInParseQueue(ItemToParse item) {
		// Note: the parse-queue is not checked before the item is put in. Not necessary
		// because parsing is always caused by fetching and putInFetchQueue() checks
		// the fetch-queue
		parseThread.parseQueue.put(item);
	}

	// ---

	/**
	 * @param	isOriginalSource		### bad
	 *
	 * @see		FetchThread#run
	 * @see		ParseThread#run
	 */
	void saveFile(URL url, String content, boolean isOriginalSource) {
		try {
			File file;
			//
			String localFile = as.localFile(url, true);
			if (isOriginalSource) {
				int pos = localFile.lastIndexOf('.');
				String extension = localFile.substring(pos);
				localFile = localFile.substring(0, pos) + ".orig" + extension;
				file = new File(localFile);
				if (file.exists()) {
					writeLog("--- original source \"" + localFile + "\" exists -- not overwritten");
					return;
				} else {
					writeLog("--- writing original source \"" + localFile + "\"");
				}
			} else {
				writeLog("--- writing file \"" + localFile + "\"");
				file = new File(localFile);
			}
			//
			new File(file.getParent()).mkdirs();
			DataOutputStream out = new DataOutputStream(new FileOutputStream(file));
			out.writeBytes(content);
			out.close();
		} catch (IOException e) {
			writeLog("*** PersonalWeb.saveFile(): " + e);
		}
	}

	/**
	 * @see		FetchThread#run
	 * @see		ParseThread#parseHTML
	 */
	boolean isHTMLPage(URL url) {
		String file = as.localFile(url, false);
		String extension = file.substring(file.lastIndexOf('.')).toLowerCase();
		return extension.equals(".html") || extension.equals(".htm") ||
			extension.equals(".phtml") || extension.equals(".shtml");
	}

	// ---

	/**
	 * @see		#putInFetchQueue
	 */
	void addVisitedURL(String text) {
		visitedURLs.append(text + '\n');
	}

	/**
	 * @see		FetchThread#run
	 */
	void addVisitedSite(URL url) {
		String site = url.getHost();	
		if (visitedSites.toString().indexOf(site) == -1) {
			visitedSites.append(site + '\n');
		}
	}

	/**
	 * @see		ParseThread#parseHTML
	 */
	void addNotVisitedSite(URL url) {
		String site = url.getHost();	
		if (notVisitedSites.toString().indexOf(site) == -1 &&
			visitedSites.toString().indexOf(site) == -1) {
			notVisitedSites.append(site + '\n');
		}
	}

	/**
	 * @see		FetchThread#run
	 */
	void addError(String err) {
		errors.append(err + "\n");
		errorCount++;
	}

	// ---

	// returns the relative link which links the baseURL to the linkURL.
	// The anchor of the linkURL is passed separately.
	String relativeURL(URL baseURL, URL linkURL, String anchor) {
		String base = as.localFile(baseURL, false);
		String link = as.localFile(linkURL, false);
		// PersonalWeb.writeLog("base=\"" + base + "\", link=\"" + link + '"');
		if (!anchor.equals("") && base.equals(link)) {
			// the link is an anchor within the document
			return "";
		}
		int pos1 = 0;
		int pos2;
		while ((pos2 = base.indexOf('/', pos1)) != -1) {
			if (!base.regionMatches(pos1, link, pos1, pos2 - pos1 + 1)) {
				break;
			}
			pos1 = pos2 + 1;
		}
		int depth = charCount(base.substring(pos1), '/');
		StringBuffer relative = new StringBuffer();
		for (int level = 0; level < depth; level++) {
			relative.append("../");
		}
		relative.append(link.substring(pos1));
		return relative.toString();
	}

	/**
	 * @see		#relativeURL
	 */
	int charCount(String str, char c) {
		int count = 0;
		for (int pos = 0; pos < str.length(); pos++) {
			if (str.charAt(pos) == c) {
				count++;
			}
		}
		return count;
	}

	int startsCount(String str, String start) {
		int i = 0;
		int l = start.length();
		int count = 0;
		// ### use regionMatches()?
		while (i + l <= str.length() && str.substring(i, i + l).equals(start)) {
			count++;
			i += l;
		}
		return count;
	}

	String encodeHTML(String html) {
		StringBuffer encoded = new StringBuffer(html.length() + 16);
		for (int c = 0; c < html.length(); c++) {
			char ch = html.charAt(c);
			if (ch == ' ') {
				encoded.append("%20");
			} else if (ch == '?') {
				encoded.append("%3F");
			} else if (ch == 'Ä') {
				// ### why ist there a test for Ä but none for ÖÜäöüß
				// should test for any char > 127 and convert to HEX
				encoded.append("%C4");
			} else {
				encoded.append(ch);
			}
		}
		return encoded.toString();
	}
	
	String decodeHTML(String html) {
		StringBuffer decoded = new StringBuffer(html.length() + 16);
		for (int c = 0; c < html.length(); c++) {
			char ch = html.charAt(c);
			if (ch == '%') {
				decoded.append((char) (Integer.parseInt(html.substring(c + 1, c + 3), 16)));
				c += 2;
			} else {
				decoded.append(ch);
			}
		}
		return decoded.toString();
	}	

	// ---

	private void createLogfile(URL rootURL, String username) {
		if (logfile) {
			try {
				Date now = new Date();
				File logfile = new File(localPath + "Logfiles/" + rootURL.getHost() + "-" +
					DeepaMehtaUtils.getDate(".") + "-" + DeepaMehtaUtils.getTime(true, ".") + "-" +
					username + ".log");
				new File(logfile.getParent()).mkdirs();
				log = new PrintStream(new FileOutputStream(logfile));
				System.out.println(">>> logfile created (" + logfile + ")");
			} catch(IOException e) {
				System.out.println("*** createLogfile(): " + e);
			}
		}
	}

	private void closeLogfile() {
		if (logfile) {
			log.close();
		}
	}



	// -----------------------------------
	// --- DeepaMehta specific methods ---
	// -----------------------------------



	/**
	 * @see		ParseThread#parseHTML
	 */
	ItemToFetch revealLink(URL linkURL, String sourceWebpageID, String sourceWebsiteID) {
		// ### compare to WebpageTopic.propertiesChanged()
		CorporateDirectives directives = new CorporateDirectives();
		// --- create linked webpage ---
		PresentableTopic webpage = as.createWebpageTopic(linkURL, sourceWebpageID);
		String webpageID = webpage.getID();
		// reveal linked webpage
		PresentableAssociation assoc = as.createPresentableAssociation(ASSOCTYPE_ASSOCIATION,
			sourceWebpageID, 1, webpageID, 1, true);
		directives.add(DIRECTIVE_SHOW_TOPIC, webpage, new Boolean(webpage.getEvoke()), topicmapID);
		directives.add(DIRECTIVE_SHOW_ASSOCIATION, assoc, Boolean.TRUE, topicmapID);
		// --- create website of linked webpage ---
		String host = linkURL.getHost();
		PresentableTopic website = as.createPresentableTopic(TOPICTYPE_WEBSITE, host, webpageID);
		String websiteID = website.getID();
		// reveal website of linked webpage
		assoc = as.createPresentableAssociation(ASSOCTYPE_ASSOCIATION, webpageID, 1, websiteID, 1, true);
		directives.add(DIRECTIVE_SHOW_TOPIC, website, new Boolean(website.getEvoke()), topicmapID);
		directives.add(DIRECTIVE_SHOW_ASSOCIATION, assoc, Boolean.TRUE, topicmapID);
		// --- create domain for website of linked webpage ---
		String domainName = DeepaMehtaServiceUtils.domain(host, as);
		PresentableTopic domain = as.createPresentableTopic(TOPICTYPE_INTERNET_DOMAIN, domainName, websiteID);
		String domainID = domain.getID();
		// reveal domain for website of linked webpage
		assoc = as.createPresentableAssociation(ASSOCTYPE_ASSOCIATION, websiteID, 1, domainID, 1, true);
		directives.add(DIRECTIVE_SHOW_TOPIC, domain, new Boolean(domain.getEvoke()), topicmapID);
		directives.add(DIRECTIVE_SHOW_ASSOCIATION, assoc, Boolean.TRUE, topicmapID);
		// --- create association between source and destination website ---
		if (!sourceWebsiteID.equals(websiteID)) {
			PresentableTopic sourceWebsite = as.createPresentableTopic(sourceWebsiteID, websiteID);
			assoc = as.createPresentableAssociation(ASSOCTYPE_ASSOCIATION, sourceWebsiteID, 1, websiteID, 1, true);
			directives.add(DIRECTIVE_SHOW_TOPIC, sourceWebsite, Boolean.FALSE, topicmapID);
			directives.add(DIRECTIVE_SHOW_ASSOCIATION, assoc, Boolean.TRUE, topicmapID);
		}
		// --- send directives ---
		as.getHostObject().sendDirectives(session, directives, as, topicmapID, viewmode);
		//
		return new ItemToFetch(linkURL, webpageID, websiteID);
	}

	/**
	 * @see		FetchThread#run
	 */
	void setWebpageTopicName(String webpageID, String html) {
		as.setWebpageTopicName(webpageID, html, topicmapID, viewmode, session);
	}

	/**
	 * @see		#finish
	 */
	private void sendNotification(String text) {
		CorporateDirectives directives = new CorporateDirectives();
		directives.add(DIRECTIVE_SHOW_MESSAGE, text, new Integer(NOTIFICATION_DEFAULT));
		as.getHostObject().sendDirectives(session, directives, as, null, null);
	}
}
