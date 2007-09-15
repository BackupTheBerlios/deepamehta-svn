package de.deepamehta.topics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.CharacterCodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;

import org.apache.commons.httpclient.HttpException;

import de.deepamehta.BaseTopic;
import de.deepamehta.PresentableAssociation;
import de.deepamehta.PresentableTopic;
import de.deepamehta.PropertyDefinition;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateCommands;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.Session;

/**
 * 
 */
@SuppressWarnings("serial")
public class MailmanTopic extends LiveTopic {

	/* actions */
	private static final String ITEM_GET_LIST = "Get Messages";

	private static final String ICON_GET_LIST = "subscribe.gif";

	private static final String CMD_GET_LIST = "getList";

	/* types and properties */
	private static final String PROPERTY_MAILMAN_LIST = "Mailman List";

	private static final String TOPICTYPE_LISTMESSAGE = "tt-listmessage";

	private static final String PROPERTY_CONTENT = "content";

	private static final String PROPERTY_SUBJECT = "subject";

	private static final String PROPERTY_URL = "url";

	private static final String ASSOCTYPE_INLIST = "at-listmessageassoc";

	/**
	 * count of archives to process in get action
	 */
	private static final int ARCHIVE_COUNT = 1;

	public MailmanTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}

	public String getNameProperty() {
		return PROPERTY_MAILMAN_LIST;
	}

	public static void propertyLabel(PropertyDefinition propertyDef,
			ApplicationService as, Session session) {
		String propName = propertyDef.getPropertyName();
		if (propName.equals(PROPERTY_NAME)) {
			propertyDef.setPropertyLabel(PROPERTY_MAILMAN_LIST);
		}
	}

	public CorporateCommands contextCommands(String topicmapID,
			String viewmode, Session session, CorporateDirectives directives) {
		CorporateCommands commands = new CorporateCommands(as);
		// add standard commands
		int editorContext = as.editorContext(topicmapID);
		commands.addNavigationCommands(this, editorContext, session);
		//
		// custom commands
		commands.addSeparator();
		commands.addCommand(ITEM_GET_LIST, CMD_GET_LIST,
				FILESERVER_IMAGES_PATH, ICON_GET_LIST);
		//
		// add standard commands
		commands.addStandardCommands(this, editorContext, viewmode, session,
				directives);
		//
		return commands;
	}

	@SuppressWarnings("unchecked")
	public CorporateDirectives executeCommand(String command, Session session,
			String topicmapID, String viewmode) {
		if (command.equals(CMD_GET_LIST)) {
			CorporateDirectives directives = new CorporateDirectives();

			// download archives
			try {
				Hashtable data = getProperties();
				URL url = null;
				ArrayList<String> archiveUrls = null;
				url = new URL((String) data.get(PROPERTY_URL));
				archiveUrls = MailmanArchiveFetcher.getArchivePaths(url);

				if (archiveUrls.size() > 0) {
					String archive = null;
					URL fileUrl = null;
					File file = null;
					// for each archive (String archive : archivUrls)
					for (int i = 0; i < ARCHIVE_COUNT && i < archiveUrls.size(); i++) {
						archive = archiveUrls.get(12);
						fileUrl = new URL(url.toString() + archive);
						file = MailmanArchiveFetcher.getArchive(fileUrl);
						importArchive(file, directives);
					}
				}
			} catch (BadLocationException e) {
				e.printStackTrace();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (HttpException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// TODO add a message with import statistic

			return directives;
		} else {
			return super.executeCommand(command, session, topicmapID, viewmode);
		}

	}

	/**
	 * Imports a message topic.
	 * 
	 * @param date
	 * @param content
	 * @param inReplyToId
	 * @param mail
	 * @param messageId
	 * @param name
	 * @param referenceIds
	 * @param subject
	 */
	@SuppressWarnings("unchecked")
	private void importMessage(Date date, String content, String inReplyToId,
			String mail, String messageId, String name,
			ArrayList<String> referenceIds, String subject,
			CorporateDirectives directives) {

		// TODO use hash or seperate field for complete messageId
		messageId = messageId.substring(0, messageId.indexOf('@'));

		// create message topic
		PresentableTopic message = new PresentableTopic(messageId, 1,
				TOPICTYPE_LISTMESSAGE, 1, subject);

		Hashtable props = new Hashtable();
		props.put(PROPERTY_CONTENT, content);
		props.put(PROPERTY_SUBJECT, subject);
		message.setProperties(props);

		// create list association
		String assocID = as.getNewAssociationID();
		PresentableAssociation assoc = new PresentableAssociation(assocID, 1,
				ASSOCTYPE_INLIST, 1, "", messageId, 1, getID(), 1);

		directives.add(DIRECTIVE_SHOW_TOPIC, message, Boolean.TRUE);
		directives.add(DIRECTIVE_SHOW_ASSOCIATION, assoc, Boolean.TRUE);
		directives.add(DIRECTIVE_SELECT_TOPIC, messageId);

		// TODO create message references (reply, reference)

	}

	/**
	 * Imports an uncompressed mailman list archive file.
	 * 
	 * @param file
	 * @throws CharacterCodingException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 */
	private void importArchive(final File file, CorporateDirectives directives)
			throws CharacterCodingException, FileNotFoundException,
			IOException, ParseException {

		BufferedReader in = null;
		SimpleDateFormat formatter = new SimpleDateFormat(
				"EEE, d MMM yyyy HH:mm:ss Z", new Locale("en", "US"));

		// pattern
		Matcher matcher = null;
		Pattern pDate = Pattern.compile("^Date: (.*)$");
		Pattern pFrom = Pattern.compile("^From: (.*) at (.*) \\((.*)\\)$");
		Pattern pMessageId = Pattern.compile("Message-ID: <(.*)>");
		Pattern pInReplyToId = Pattern.compile("In-Reply-To: <(.*)>");
		Pattern pReferencesId = Pattern.compile("References: <(.*)>");
		Pattern pSubject = Pattern.compile("Subject: \\[[^\\]]*\\] (.*)");
		Pattern pTabReferencesId = Pattern.compile("\t<(.*)>");
		Pattern pTabSubject = Pattern.compile("\t(.*)");
		// messages readed until this ignore line or EOF
		Pattern pFromIgnore = Pattern.compile("^From .* at .*$");

		// message attributes
		Date date = null;
		String content = null;
		String inReplyToId = null;
		String line = null;
		String mail = null;
		String messageId = null;
		String name = null;
		ArrayList<String> referenceIds = null;
		String subject = null;

		try {
			// TODO read ISO-8859-1 like (=?ISO-8859-1?Q?J=F6rg_Richter?=)
			in = new BufferedReader(new FileReader(file));

			while ((line = in.readLine()) != null) {

				// read from line
				if (mail == null) {
					matcher = pFrom.matcher(line);
					if (matcher.matches()) {
						mail = matcher.group(1) + "@" + matcher.group(2);
						name = matcher.group(3);
						continue;
					}
				}

				// read date line
				if (mail != null && date == null) {
					matcher = pDate.matcher(line);
					if (matcher.matches()) {
						date = formatter.parse(matcher.group(1));
						continue;
					}
				}

				// read first subject line
				if (date != null && subject == null) {
					matcher = pSubject.matcher(line);
					if (matcher.matches()) {
						subject = matcher.group(1);
						continue;
					}
				}

				// read in reply to
				if (subject != null && messageId == null && inReplyToId == null) {
					matcher = pInReplyToId.matcher(line);
					if (matcher.matches()) {
						inReplyToId = matcher.group(1);
						continue;
					}
				}

				// read first reference
				if (subject != null && messageId == null
						&& referenceIds == null) {
					matcher = pReferencesId.matcher(line);
					if (matcher.matches()) {
						referenceIds = new ArrayList<String>(1);
						referenceIds.add(matcher.group(1));
						continue;
					}
				}

				// read messageId
				if (subject != null && messageId == null) {
					matcher = pMessageId.matcher(line);
					if (matcher.matches()) {
						messageId = matcher.group(1);
						continue;
					} else {
						if (referenceIds != null) {
							// read next referencesId
							matcher = pTabReferencesId.matcher(line);
							if (matcher.matches()) {
								referenceIds.add(matcher.group(1));
							}
						} else {
							// read additional subject lines before messageId
							matcher = pTabSubject.matcher(line);
							if (matcher.matches()) {
								subject += " " + matcher.group(1);
							}
						}
						continue;
					}
				}

				// read content
				if (messageId != null) {
					// actual (first) line at this point is empty => read next
					content = new String();
					while ((line = in.readLine()) != null) {
						matcher = pFromIgnore.matcher(line);
						if (matcher.matches()) { // message readed
							importMessage(date, content, inReplyToId, mail,
									messageId, name, referenceIds, subject,
									directives);
							mail = null;
							name = null;
							date = null;
							subject = null;
							inReplyToId = null;
							referenceIds = null;
							messageId = null;
							content = null;
							break;
						} else { // add content line
							content += line + "\n";
						}
					}
				}
			}
		} finally {
			if (in != null) {
				in.close();
			}
		}
		// import last message after loops
		if (content != null) {
			importMessage(date, content, inReplyToId, mail, messageId, name,
					referenceIds, subject, directives);
		}
	}

}
