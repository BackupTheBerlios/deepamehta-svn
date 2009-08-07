package de.deepamehta.topics.personalweb;

import de.deepamehta.topics.helper.HTMLParser;
import de.deepamehta.topics.helper.SimpleHTMLParser;
import de.deepamehta.util.DeepaMehtaUtils;

import java.net.MalformedURLException;
import java.net.URL;



class ParseThread extends Thread {

	PersonalWeb pw;
	SynchronizedQueue parseQueue;
	
	ParseThread(PersonalWeb pw) {
		this.pw = pw;
		this.parseQueue = new SynchronizedQueue(pw, true);
	}

	public void run() {
		// go through the queue of items to parse
		ItemToParse item = null;
		while (!parseQueue.queueIsEmpty() || pw.fetchThread.fetchQueue.isBusy()) {
			item = (ItemToParse) parseQueue.get();	// may block
			// item is null if this thread has been notified just because the other thread has ended
			if (item == null) {
				break;
			}
			// --- parse item ---
			pw.parseCount++;
			//
			String html = parseHTML(item.url, item.html, item.local, item.webpageID, item.websiteID);
			// --- save it on the local filesystem ---
			if (!item.local) {
				pw.saveFile(item.url, html, false);		// save the converted content
			}
			//
			if (pw.aborted) {
				break;
			}
		}
		// finishing
		pw.writeLog("> ParseThread.run(): " + (pw.aborted ? "aborted" : "finished"));
		if (item == null) {
			// the other thread has already finished -- this thread must clean up
			pw.finish();
		} else {
			// wake up the other thread for cleaning up
			pw.fetchThread.fetchQueue.notifyQueue();
		}
	}



	// -----------------------
	// --- Private Methods ---
	// -----------------------



	/**
	 * Parses the html text and replaces http: links with file: links. The http: links
	 * are added to the list of URLs to retrieve. The url argument is the URL the
	 * html content is retrieved from.
	 *
	 * @see		#run
	 */
	private String parseHTML(URL url, String html, boolean local, String webpageID, String websiteID) {
		String attributes[] = {"HREF", "SRC", "BACKGROUND"};
		pw.addVisitedURL("--- followed links from \"" + url + "\" ---");
		pw.writeLog("> ---------------- parsing \"" + url + "\" ----------------");
		//
		boolean doPreparation = pw.prepare && !local;
		StringBuffer preparedHTML = null;
		if (doPreparation) {
			preparedHTML = new StringBuffer(html.length());
		}
		//
		int start = 0;
		HTMLParser commentParser = new HTMLParser(html);
		String comment = commentParser.textRange("<!--", "-->", true, true);
		// --- loop through all attributes in question ---
		while (true) {
			int pos, len;
			SimpleHTMLParser parser = getNextAttribute(attributes, html, start);
			if (parser == null) {
				break;
			}
			String kind = parser.getAttribute();
			String href = parser.getLink();
			String ref = parser.getAnchor();
			pos = parser.getPosition();
			len = parser.getLength();
			// ignore comments
			if (!comment.equals("")) {
				int commentPos = commentParser.getFoundPos();
				int commentEnd = commentPos + comment.length();
				if (pos > commentPos) {
					// attribute is inside or after comment -- write out comment and continue
					// Note: the range between comment and attribute may comprise more than one comment
					if (doPreparation) {
						preparedHTML.append(html.substring(start, commentEnd));
					}
					start = commentEnd;
					comment = commentParser.textRange("<!--", "-->", true, true);
					continue;
				}
			}
			//
			pw.linkCount++;
			pw.writeLog("> --- link detected: \"" + href + ref + "\" ---");
			try {
				// the decisive URL for fetching policy and for adding into fetch queue
				URL link = composeURL(pw.as.completeURL(url), href, local);	// may throw MalformedURLException
				pw.writeLog(">        normalized: \"" + link + "\"");
				// fetching policy
				boolean followLink = false;
				boolean transformLink = true;
				if (link.getProtocol().equals("http")) {
					followLink = doFollowLink(url, html, link);
				} else {
					transformLink = false;
				}
				if (followLink) {
					boolean localFileExists = pw.as.localFileExists(link);
					boolean parsingRequired = pw.isHTMLPage(link);
					ItemToFetch linkItem = null;
					if (parsingRequired) {
						linkItem = pw.revealLink(link, webpageID, websiteID);
					} else {
						linkItem = new ItemToFetch(link, null, null);
					}
					// --- queue this link for fetching ---
					if (!localFileExists || parsingRequired) {
						pw.putInFetchQueue(linkItem);
					}
				} else {
					// --- don't queue this link for fetching ---
					pw.addNotVisitedSite(link);
				}
				if (doPreparation) {
					if (transformLink) {
						// prepare for offline browsing: convert the http link into a file link
						String relative = pw.relativeURL(url, link, ref);
						// pw.writeLog("> relative(\"" + url + "\", \"" + link + "\") = \"" + relative + '"');
						pw.writeLog(">      converted to: \"" + relative + ref + '"');
						preparedHTML.append(html.substring(start, pos));
						preparedHTML.append(kind);
						preparedHTML.append(relative);
						preparedHTML.append(ref);
					} else {
						// don't transform link -- e.g. file: URLs, do neither fetch nor convert
						preparedHTML.append(html.substring(start, pos + len));
						pw.nonHTTPCount++;
						pw.writeLog(">            policy: *** not a http: link (URL object is valid)");
					}
				}
			} catch (MalformedURLException e) {
				// don't transform link -- e.g. news: URLs
				if (doPreparation) {
					preparedHTML.append(html.substring(start, pos + len));
				}
				pw.nonHTTPCount++;
				pw.writeLog(">            policy: *** not a http: link (URL object is NOT valid)");
			}
			start = pos + len;
			Thread.yield();		// for the case of a big file
		};	// while(true)
		// --- append tail ---
		if (doPreparation) {
			preparedHTML.append(html.substring(start));
			return preparedHTML.toString();
		} else {
			return html;
		}
	}

	private SimpleHTMLParser getNextAttribute(String[] attributes, String html, int start) {
		SimpleHTMLParser parser[] = new SimpleHTMLParser[attributes.length];
		int p = -1;
		int minPos = html.length();
		for (int a = 0; a < attributes.length; a++) {
			parser[a] = new SimpleHTMLParser(html, start, attributes[a]);
			int pos = parser[a].getPosition();
			if (pos >= 0 && pos < minPos) {
				minPos = pos;
				p = a;
			}
		}
		//
		return p != -1 ? parser[p] : null;
	}

	/**
	 * @param	link		may be empty
	 *
	 * @see		#parseHTML
	 */
	private URL composeURL(URL url, String link, boolean local) throws MalformedURLException {
		URL absoluteURL;	// result object
		int urlDepth = pw.charCount(url.getFile(), '/');
		int linkDepth = pw.startsCount(link, "../");
		// ### pw.writeLog(">           --> urlDepth=" + urlDepth + ", linkDepth=" + linkDepth);
		if (linkDepth > urlDepth) {
			pw.writeLog("*** composeURL(): can't compose URL -- null returned");
			return null;
		// E.g. on page http://www.hype.net/hype.html is a link "../indx.html"
		// Note: the ../ tries to leave the webspace, which is an HTML coding error and
		// is ignored by browsers resp. servers. As a workaraound the following check
		// involves "local", otherwise PersonalWeb would consider this link as
		// http://indx.html/
		} else if (linkDepth == urlDepth && local) {
			absoluteURL = new URL(url.getProtocol() + "://" + link.substring(3 * linkDepth));
		} else {
			absoluteURL = new URL(url, link);
		}
		return absoluteURL;
	}

	/**
	 * Applies the crawler's policy.
	 *
	 * @param	url		the origin URL
	 * @param	html	the origin HTML
	 * @param	link	the link URL
	 *
	 * @see		#parseHTML
	 */
	private boolean doFollowLink(URL url, String html, URL link) {
		String href = link.getFile();
		if (DeepaMehtaUtils.isImage(href)) {
			pw.imageCount++;
			if (pw.images) {
				pw.writeLog(">            policy: is an image - download required");
				return true;
			} else {
				pw.writeLog(">            policy: is an image - do not retrieve");							
			}
		} else if (href.endsWith(".css") || href.endsWith(".CSS")) {
			pw.writeLog(">            policy: is a stylesheet - download required");
			pw.cssCount++;
			return true;
		} else if (href.endsWith(".js") || href.endsWith(".JS")) {
			pw.writeLog(">            policy: is a javascript - download required");
			pw.scriptCount++;
			return true;
		} else if (pw.as.containsCGICall(link)) {
			pw.cgiCount++;
			if (pw.cgi) {
				pw.writeLog(">            policy: is a CGI call - download required");
				return true;
			} else {
				pw.writeLog(">            policy: is a CGI call - do not retrieve");
			}
		/* ### } else if (link.toString().startsWith(pw.rootPath)) {
			pw.writeLog(">            policy: belongs to document tree - download required");
			pw.documentCount++;
			return true;
		} else if (url.toString().startsWith(pw.rootPath)) {
			pw.externalCount++;
			if (pw.external) {
				pw.writeLog(">            policy: is an external link - download required");
				return true;
			} else {
				pw.writeLog(">            policy: is an external link - do not retrieve");
			} */
		} else {
			HTMLParser parser = new HTMLParser(html);
			if (parser.textRangeExists("<FRAMESET", "</FRAMESET>")) {
				pw.writeLog(">            policy: is a frame in a frameset - download required");
				pw.frameCount++;
				return true;
			} else {
				pw.writeLog(">            policy: too far from start page - don't follow this URL");
				pw.unfollowCount++;
			}
		}
		//
		return false;
	}
}
