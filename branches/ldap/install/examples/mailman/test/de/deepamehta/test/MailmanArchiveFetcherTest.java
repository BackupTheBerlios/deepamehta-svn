package de.deepamehta.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;

import javax.swing.text.BadLocationException;

import org.apache.commons.httpclient.HttpException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.deepamehta.topics.MailmanArchiveFetcher;


public class MailmanArchiveFetcherTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testInformationsAboutUrl() {
		try {

			// use default port
			URL url = new URL("https://localhost/path");
			assertEquals(443, url.getDefaultPort());
			assertEquals("localhost", url.getHost());
			assertEquals("/path", url.getPath());
			assertEquals(-1, url.getPort());
			assertEquals("https", url.getProtocol());
			assertEquals(443, MailmanArchiveFetcher.getPort(url));

			// use explicit port declaration
			url = new URL("https://localhost:10443/path");
			assertEquals(443, url.getDefaultPort());
			assertEquals("localhost", url.getHost());
			assertEquals("/path", url.getPath());
			assertEquals(10443, url.getPort());
			assertEquals("https", url.getProtocol());
			assertEquals(10443, MailmanArchiveFetcher.getPort(url));

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testFetchDeepamehtaDevelMailmanArchives() {
		try {
			URL url = null;

			// get over https (the ending slash is required for https!)
			url = new URL(
					"https://lists.berlios.de/pipermail/deepamehta-devel/");
			ArrayList<String> archiveUrls = MailmanArchiveFetcher
					.getArchivePaths(url);

			// for (String archive : archivUrls) {
			String archive = archiveUrls.get(12);
			URL fileUrl = new URL(url.toString() + archive);
			File file = MailmanArchiveFetcher.getArchive(fileUrl);
			// }

			// and over http
			// url = new
			// URL("http://lists.berlios.de/pipermail/deepamehta-devel");
			// body = MailmanArchivFetcher.getHttpBody(url);

		} catch (BadLocationException e) {
			e.printStackTrace();
		} catch (ConnectException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
}
