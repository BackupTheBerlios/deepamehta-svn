package de.deepamehta.topics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import javax.swing.text.BadLocationException;
import javax.swing.text.EditorKit;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpHost;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.protocol.Protocol;



public class MailmanArchiveFetcher {

	

	/**
	 * Returns a file with the uncommpressed archive from a HTTP request to the
	 * URL.
	 * 
	 * @param url
	 * @return file
	 * @throws HttpException
	 * @throws IOException
	 * @throws BadLocationException
	 * @throws URISyntaxException
	 */
	public static File getArchive(final URL url) throws HttpException,
			IOException, BadLocationException, URISyntaxException {

		File file = null;
		FileChannel out = null;
		HttpClient client = new HttpClient();
		GetMethod get = new GetMethod(getHttpMethod(url, client));

		// do request
		try {
			int status = client.executeMethod(get);
			// TODO better status code handling
			if (status == 200) {
				InputStream gz = get.getResponseBodyAsStream();
				GZIPInputStream stream = new GZIPInputStream(gz);
				ReadableByteChannel in = Channels.newChannel(stream);
				file = File.createTempFile("archive", ".txt");
				out = new FileOutputStream(file).getChannel();
				copy(in, out);
			} else {
				throw new HttpException("wrong HTTP code (" + status + ")");
			}

		} finally {
			// close get and file output
			get.releaseConnection();
			if (out != null) {
				out.close();
			}
		}
		return file;
	}

	/**
	 * Returns all <i>Mailman</i> archive paths from a HTTP request to the URL.
	 * 
	 * @param url
	 * @return archiveUrls
	 * @throws HttpException
	 * @throws IOException
	 * @throws BadLocationException
	 * @throws URISyntaxException
	 */
	public static ArrayList<String> getArchivePaths(final URL url)
			throws HttpException, IOException, BadLocationException,
			URISyntaxException {

		ArrayList<String> archiveUrls = new ArrayList<String>();
		HttpClient client = new HttpClient();
		GetMethod get = new GetMethod(getHttpMethod(url, client));

		// do request
		try {
			int status = client.executeMethod(get);
			// TODO better status code handling
			if (status == 200) {
				archiveUrls = getArchiveAnchors(get.getResponseBodyAsStream());
			} else {
				throw new HttpException("wrong HTTP code (" + status + ")");
			}

		} finally {
			get.releaseConnection();
		}
		return archiveUrls;
	}

	/**
	 * Gets default port or explicit declared port from URL
	 * 
	 * @param url
	 * @return port
	 */
	public static int getPort(final URL url) {
		int port = 0;
		if (url.getPort() == -1) {
			port = url.getDefaultPort();
		} else {
			port = url.getPort();
		}
		return port;
	}

	/**
	 * This method copies data from the src channel and writes it to the dest
	 * channel until EOF on src. This implementation makes use of compact() on
	 * the temp buffer to pack down the data if the buffer wasn't fully drained.
	 * This may result in data copying, but minimizes system calls. It also
	 * requires a cleanup loop to make sure all the data gets sent.
	 */
	private static void copy(ReadableByteChannel src, WritableByteChannel dest)
			throws IOException {
		ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
		while (src.read(buffer) != -1) {
			// prepare the buffer to be drained
			buffer.flip();
			// write to the channel, may block
			dest.write(buffer);
			// If partial transfer, shift remainder down
			// If buffer is empty, same as doing clear()
			buffer.compact();
		}
		// EOF will leave buffer in fill state
		buffer.flip();
		// make sure the buffer is fully drained.
		while (buffer.hasRemaining()) {
			dest.write(buffer);
		}
	}

	/**
	 * Parses the html stream an returns a string for all URLs like
	 * <code>*.txt.gz</code>.
	 * 
	 * @param htmlStream
	 * @return urls
	 * @throws IOException
	 * @throws BadLocationException
	 * @throws URISyntaxException
	 */
	private static ArrayList<String> getArchiveAnchors(InputStream htmlStream)
			throws IOException, BadLocationException, URISyntaxException {

		ArrayList<String> urls = new ArrayList<String>();
		Pattern p = Pattern.compile("^.*\\.txt\\.gz$");

		// parse the HTML
		EditorKit kit = new HTMLEditorKit();
		HTMLDocument doc = (HTMLDocument) kit.createDefaultDocument();
		doc.putProperty("IgnoreCharsetDirective", true);
		kit.read(htmlStream, doc, 0);

		// get all link elements
		HTMLDocument.Iterator anchors = doc.getIterator(HTML.Tag.A);
		while (anchors.isValid()) {
			SimpleAttributeSet s = (SimpleAttributeSet) anchors.getAttributes();
			String link = (String) s.getAttribute(HTML.Attribute.HREF);
			// add archive links to url list
			if (link != null && p.matcher(link).matches()) {
				urls.add(link);
			}
			anchors.next();
		}

		return urls;
	}

	/**
	 * Returns the HTTP <code>GetMethod</code> object depending on protocol.
	 * 
	 * @param url
	 * @param client
	 * @return path
	 */
	private static String getHttpMethod(final URL url, HttpClient client) {
		String path = null;
		// TODO interact wtith user if host verifivation fails (like browser?)!
		// use of factory which ignores host verification!
		if (url.getProtocol().equals("https")) {
			int port = getPort(url);
			EasySSLProtocolSocketFactory ssl = new EasySSLProtocolSocketFactory();
			Protocol protocol = new Protocol(url.getProtocol(), ssl, port);
			HttpHost server = new HttpHost(url.getHost(), port, protocol);
			client.getHostConfiguration().setHost(server);
			path = url.getPath();
		} else {
			path = url.toString();
		}
		return path;
	}
}
