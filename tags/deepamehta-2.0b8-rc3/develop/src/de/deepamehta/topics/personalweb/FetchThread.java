package de.deepamehta.topics.personalweb;

import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.util.DeepaMehtaUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;



class FetchThread extends Thread implements DeepaMehtaConstants {

	PersonalWeb pw;
	SynchronizedQueue fetchQueue;

	FetchThread(PersonalWeb pw) {
		this.pw = pw;
		this.fetchQueue = new SynchronizedQueue(pw, false);
	}

	public void run() {
		// ### workaround
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			pw.writeLog("*** FetchThread.run(): " + e);
		}
		//
		ItemToFetch item = null;
		// Process the URLs in the fetch-queue as long as the queue isn't empty resp. the
		// parse thread is still working (and thus could cause further fetch requests)
		while (!fetchQueue.queueIsEmpty() || pw.parseThread.parseQueue.isBusy()) {
			item = (ItemToFetch) fetchQueue.get();		// may block
			// item is null if this thread has been notified just because the other thread has ended
			if (item == null) {
				break;
			}
			//
			pw.fetchCount++;
			//
			pw.addVisitedSite(item.url);
			try {
				boolean localFileExists = pw.as.localFileExists(item.url);
				boolean loadFromFile = localFileExists && pw.consider;
				boolean parsingRequired = pw.isHTMLPage(item.url);
				if (!localFileExists || parsingRequired) {
					// --- fetch item ---
					String content = getContent(item.url, loadFromFile);	// may throw IOException
					//
					if (parsingRequired) {
						pw.writeLog("<<< \"" + item.url + "\" is an HTML page --> parsing required");
						pw.saveFile(item.url, content, true);	// isOriginalSource=true
						//
						pw.putInParseQueue(new ItemToParse(item.url, content, loadFromFile, item.webpageID, item.websiteID));
						pw.setWebpageTopicName(item.webpageID, content);
					} else {
						pw.writeLog("<<< \"" + item.url + "\" is NOT an HTML page --> no parsing required, save now");
						pw.saveFile(item.url, content, false);	// isOriginalSource=false
					}
					// --- sleep ---
					if (pw.friendly) {
						pw.writeLog("<<< after download, sleep " + WEBCRAWLER_NICENESS + " seconds ...");
						Thread.sleep(1000 * WEBCRAWLER_NICENESS);
						pw.writeLog("<<< ... sleep is over, download continues");
					}
				} else {
					// ### should not happen anymore, see ParseThread.parseHTML()
					pw.writeLog("*** \"" + item.url + "\" downloaded already and requires no parsing --> do nothing");
					pw.noParsingNeeded++;
				}
			} catch (InterruptedException e) {
				pw.writeLog("*** FetchThread.run(): " + e);
				pw.addError(e + " (" + item.url + ")");
			} catch (UnknownHostException e) {
				// ### occurrs if there is no active internet connection
				pw.writeLog("*** FetchThread.run(): " + e);
				pw.addError(e + " (" + item.url + ")");
			} catch (IOException e) {
				pw.writeLog("*** FetchThread.run(): " + e);
				pw.addError(e.toString());
			}
			//
			if (pw.aborted) {
				break;
			}
		}
		// finishing
		pw.writeLog("<<< FetchThread.run(): " + (pw.aborted ? "aborted" : "finished"));
		if (item == null) {
			// the other thread has already finished -- this thread must clean up
			pw.finish();
		} else {
			// wake up the other thread for cleaning up
			pw.parseThread.parseQueue.notifyQueue();
		}
	}

	/**
	 * @see		#run
	 */
	private String getContent(URL url, boolean loadFromFile) throws IOException {
		try {
			String content;
			if (loadFromFile) {
				File file = new File(pw.as.localFile(url, true));
				pw.writeLog("<<< loading \"" + url + "\" from file (" + file.length() + " bytes)");
				content = DeepaMehtaUtils.readFile(file);
				pw.loadByteCount += content.length();
				pw.loadCount++;
			} else {
				pw.writeLog("<<< downloading \"" + url + "\"");
				content = pw.as.downloadFile(url);
				if (content.indexOf("404 Not Found") != -1) {
					pw.writeLog("*** FetchThread.fetch(): 404 Not Found");
				}
				pw.retrieveByteCount += content.length();
				pw.retrieveCount++;
				pw.writeLog("<<< download of \"" + url + "\" complete (" + content.length() + " bytes)");
			}
			return content;
		} catch (NullPointerException e) {
			pw.writeLog("*** FetchThread.getContent(): " + e);
			throw new IOException();
		}
	}
}
