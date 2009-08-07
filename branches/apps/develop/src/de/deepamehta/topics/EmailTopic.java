package de.deepamehta.topics;

import de.deepamehta.BaseAssociation;
import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.FileServer;
import de.deepamehta.PresentableAssociation;
import de.deepamehta.PresentableTopic;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateCommands;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.Session;

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;



/**
 * An email.
 * <p>
 * <hr>
 * Last change: 26.1.2009 (2.0b9)<br>
 * J&ouml;rg Richter<br>
 * jri@deepamehta.de
 */
public class EmailTopic extends LiveTopic {

	private static Logger logger = Logger.getLogger("de.deepamehta");

	private static final String TEXT_NO_SUBJECT = "<No Subject>";

	// email states
	private static final String EMAIL_STATE_DRAFT = "Draft";
	private static final String EMAIL_STATE_RECEIVED = "Received";
	private static final String EMAIL_STATE_SENT = "Sent";

	// commands
	private static final String ITEM_ATTACH_DOCUMENT = "Attach a document";
	private static final String CMD_ATTACH_DOCUMENT = "attachDocument";
	private static final String ICON_ATTACH_DOCUMENT = "document.gif";

	private static final String ITEM_SEND = "Send";
	private static final String CMD_SEND = "send";
	private static final String ICON_SEND = "sendEmail.gif";

	private static final String ITEM_REPLY = "Reply";
	private static final String CMD_REPLY = "reply";

	private static final String ITEM_FORWARD = "Forward";
	private static final String CMD_FORWARD = "forward";



	// *******************
	// *** Constructor ***
	// *******************



	public EmailTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}



	// **********************
	// *** Defining Hooks ***
	// **********************



	// ------------------
	// --- Life Cycle ---
	// ------------------



	public CorporateDirectives evoke(Session session, String topicmapID, String viewmode) {
		CorporateDirectives directives = super.evoke(session, topicmapID, viewmode);
		setProperty(PROPERTY_STATUS, EMAIL_STATE_DRAFT);
		String author = as.getEmailAddress(session.getUserID());	// may return null
		if (author != null) {
			setProperty(PROPERTY_FROM, author);
		}
		as.createLiveAssociation(as.getNewAssociationID(), ASSOCTYPE_SENDER, getID(), session.getUserID(), session, directives);
		return directives;
	}



	// --------------------------
	// --- Providing Commands ---
	// --------------------------



	public CorporateCommands contextCommands(String topicmapID, String viewmode,
								Session session, CorporateDirectives directives) {
		CorporateCommands commands = new CorporateCommands(as);
		//
		int editorContext = as.editorContext(topicmapID);
		commands.addNavigationCommands(this, editorContext, session);
		// --- send/reply/forward ---
		String s = getProperty(PROPERTY_STATUS);
		commands.addSeparator();
		if (s.equals(EMAIL_STATE_DRAFT)) {
			commands.addCommand(ITEM_ATTACH_DOCUMENT, CMD_ATTACH_DOCUMENT, FILESERVER_ICONS_PATH, ICON_ATTACH_DOCUMENT);
			commands.addCommand(ITEM_SEND, CMD_SEND, FILESERVER_IMAGES_PATH, ICON_SEND);
		} else if (s.equals(EMAIL_STATE_RECEIVED)) {
			commands.addCommand(ITEM_REPLY, CMD_REPLY);
			commands.addCommand(ITEM_FORWARD, CMD_FORWARD);
		} else if (s.equals(EMAIL_STATE_SENT)) {
			commands.addCommand(ITEM_FORWARD, CMD_FORWARD);
		} else {
			throw new DeepaMehtaException("unexpected email status: \"" + s + "\"");
		}
		//
		commands.addStandardCommands(this, editorContext, viewmode, session, directives);
		//
		return commands;
	}



	// --------------------------
	// --- Executing Commands ---
	// --------------------------



	public CorporateDirectives executeCommand(String command, Session session, String topicmapID, String viewmode) {
		CorporateDirectives directives = new CorporateDirectives();
		if (command.equals(CMD_SEND)) {
			sendMail(directives);
		} else if (command.equals(CMD_ATTACH_DOCUMENT)) {
			createChildTopic(TOPICTYPE_DOCUMENT, SEMANTIC_EMAIL_ATTACHMENT, session, directives);
		} else if (command.equals(CMD_REPLY)) {
			createDraftForReply(session.getUserID(), 1, directives);
		} else if (command.equals(CMD_FORWARD)) {
			createDraftForForward(session.getUserID(), 1, directives);
		} else {
			return super.executeCommand(command, session, topicmapID, viewmode);
		}
		return directives;
	}



	// ---------------------------
	// --- Handling Properties ---
	// ---------------------------



	public String getNameProperty() {
		return PROPERTY_SUBJECT;
	}



	// -----------------------------
	// --- Handling Associations ---
	// -----------------------------



	public String associationAllowed(String assocTypeID, String relTopicID, int relTopicPos, CorporateDirectives directives) {
		String relTypeID = as.getLiveTopic(relTopicID, 1).getType();
		if ((relTypeID.equals(TOPICTYPE_PERSON) || relTypeID.equals(TOPICTYPE_INSTITUTION) ||
												   relTypeID.equals(TOPICTYPE_RECIPIENT_LIST)) && relTopicPos == 2) {
			return SEMANTIC_EMAIL_RECIPIENT;
		} else if (relTypeID.equals(TOPICTYPE_DOCUMENT) && relTopicPos == 2) {
			return SEMANTIC_EMAIL_ATTACHMENT;
		}
		return super.associationAllowed(assocTypeID, relTopicID, relTopicPos, directives);
	}



	// **********************
	// *** Custom Methods ***
	// **********************



	private void sendMail(CorporateDirectives directives) throws DeepaMehtaException {
		try {
			Hashtable props = getProperties();
			if (!props.get(PROPERTY_STATUS).equals(EMAIL_STATE_DRAFT)) {
				return;
			}
			// error check
			Vector recipients = collectRecipients();
			if (recipients.size() == 0) {
				// ### TODO: error notification (or disable "Send" command in advance?)
				return;
			}
			// create message
			MimeMessage msg = new MimeMessage(getMailSession(as.getSMTPServer()));
			// set sender
			String author = (String) props.get(PROPERTY_FROM);
			logger.info(this + ", author=\"" + author + "\"");
			msg.setFrom(new InternetAddress(author));
			// set recipients
			setRecipients(msg, recipients);
			// set subject
			String subject = (String) props.get(PROPERTY_SUBJECT);
			if (subject == null || subject.equals("")) {
				subject = TEXT_NO_SUBJECT;
			}
			msg.setSubject(subject);
			// set date
			Date d = new Date();
			msg.setSentDate(d);
			// set text and attachments
			String msgText = (String) props.get(PROPERTY_TEXT);
			addAttachments(msgText, msg);
			//
			Transport.send(msg);
			//
			setProperty(PROPERTY_STATUS, EMAIL_STATE_SENT);
			setProperty(PROPERTY_DATE, d.toString());
		} catch (Throwable e) {
			throw new DeepaMehtaException(e.toString(), e);
		}
	}

	public static void sendMail(String smtpServer, String from, String to, String subject, String body) {
		try {
			MimeMessage msg = new MimeMessage(getMailSession(smtpServer));
			msg.setFrom(new InternetAddress(from));
			msg.setRecipients(Message.RecipientType.TO, to);
			if (subject == null || subject.equals("")) {
				subject = TEXT_NO_SUBJECT;
			}
			msg.setSubject(subject);
			msg.setText(body, "UTF-8");
			msg.setSentDate(new Date());
			//
			Transport.send(msg);
		} catch (Throwable e) {
			throw new DeepaMehtaException(e.toString(), e);
		}
	}

	// ---

	private static javax.mail.Session getMailSession(String smtpServer) {
		Properties props = new Properties();
		props.put("mail.smtp.host", smtpServer);
		javax.mail.Session session = javax.mail.Session.getDefaultInstance(props);	// ### authenticator=null
		session.setDebug(true);		// ###
		return session;
	}

	// ---

	/**
	 * Returns the email addresses of all recipients.
	 *
	 * @return	the email addresses as vector of {@link Recipient}s.
	 */
	public Vector collectRecipients() {
		Vector recipients = new Vector();
		//
		Enumeration e = getRecipientTopics().elements();
		while (e.hasMoreElements()) {
			BaseTopic recipientTopic = (BaseTopic) e.nextElement();
			String recipientType = getRecipientType(recipientTopic.getID());
			if (recipientTopic.getType().equals(TOPICTYPE_RECIPIENT_LIST)) {
				RecipientListTopic recipientList = (RecipientListTopic) as.getLiveTopic(recipientTopic);
				Vector recipientTopics = recipientList.getSelectedRecipients();
				//
				addRecipientsToVector(recipientTopics, recipientType, recipients);
			} else {
				addRecipientToVector(recipientTopic, recipientType, recipients);
			}
		}
		//
		return recipients;
	}

	/**
	 * Returns the recipient topics related to this email.
	 * Recipient topics are "Person", "Institution", or "Recipient List" topics.
	 *
	 * @return	the recipient topics as vector of {@link de.deepamehta.BaseTopic}s.
	 */
	public Vector getRecipientTopics() {
		return as.getRelatedTopics(getID(), SEMANTIC_EMAIL_RECIPIENT, 2);
	}

	private String getRecipientType(String recipientID) {
		BaseAssociation assoc = cm.getAssociation(SEMANTIC_EMAIL_RECIPIENT, getID(), recipientID);
		return cm.getAssociationData(assoc.getID(), 1, PROPERTY_RECIPIENT_TYPE);
	}

	private void addRecipientsToVector(Vector recipientTopics, String recipientType, Vector recipients) {
		Enumeration e = recipientTopics.elements();
		while (e.hasMoreElements()) {
			BaseTopic recipientTopic = (BaseTopic) e.nextElement();
			addRecipientToVector(recipientTopic, recipientType, recipients);
		}
	}

	private void addRecipientToVector(BaseTopic recipientTopic, String recipientType, Vector recipients) {
		String emailAddress = as.getEmailAddress(recipientTopic.getID());
		if (emailAddress != null && emailAddress.length() > 0) { 
			Recipient recipient = new Recipient(recipientType, emailAddress);
			if (!recipients.contains(recipient)) {
				recipients.addElement(recipient);
			}
		}
	}

	// ---

	/**
	 * @param	recipients	vector of {@link Recipient}s.
	 */
	private void setRecipients(MimeMessage msg, Vector recipients) throws MessagingException {
		Enumeration e = recipients.elements();
		while (e.hasMoreElements()) {
			Recipient recipient = (Recipient) e.nextElement();
			msg.addRecipient(recipient.type, new InternetAddress(recipient.emailAddress));
		}
	}

	private void addAttachments(String msgText, MimeMessage msg) throws MessagingException {
		Enumeration docs = as.getRelatedTopics(getID(), SEMANTIC_EMAIL_ATTACHMENT, 2).elements();
		Multipart mp = null;
		while (docs.hasMoreElements()) {
			BaseTopic doc = (BaseTopic)docs.nextElement();
			if (doc.getType().equals(TOPICTYPE_DOCUMENT)) {
				String sFileName = getProperty(doc, PROPERTY_FILE);
				String sFile = FileServer.repositoryPath(FILE_DOCUMENT) + sFileName;
				MimeBodyPart mbp = new MimeBodyPart();
				FileDataSource ds = new FileDataSource(sFile);
				DataHandler dh = new DataHandler(ds);
				mbp.setDataHandler(dh);
				String sFileDoc = doc.getName();
				if ((sFileDoc != null) && !sFileDoc.equals("")){
					mbp.setFileName(sFileDoc);
				} else {
					mbp.setFileName(sFileName);
				}
				if (mp == null) {
					mp = new MimeMultipart();
					MimeBodyPart mbpText = new MimeBodyPart();
					mbpText.setText(msgText, "UTF-8");
					mp.addBodyPart(mbpText);
				}
				mp.addBodyPart(mbp);
			}
		}
		// multi part or not
		if (mp != null) {
			msg.setContent(mp);
		} else {
			msg.setText(msgText, "UTF-8");
		}
	}

	public void createDraftForReply(String userID, int userVersion, CorporateDirectives directives) {
		Hashtable data = getProperties();
		if (!data.get(PROPERTY_STATUS).equals(EMAIL_STATE_RECEIVED)) {
			return;
		}
		String rcpts = (String)data.get("AuthorAddress");
		String author = as.getEmailAddress(userID);		// ### null
		String subject = "RE:" + (String)data.get(PROPERTY_SUBJECT);
		String date = "";
		String msgText = formatPassedMsgText(data, "     ");
		String topicID = as.cm.getNewTopicID();
		as.cm.createTopic(topicID, 1, TOPICTYPE_EMAIL, 1, subject);
		Hashtable elementData = new Hashtable();
		elementData.put(PROPERTY_FROM, author);
		elementData.put(PROPERTY_TO, rcpts);
		elementData.put(PROPERTY_SUBJECT, subject);
		elementData.put("UID", "0");
		elementData.put(PROPERTY_STATUS, EMAIL_STATE_DRAFT);
		elementData.put(PROPERTY_DATE, date);
		elementData.put(PROPERTY_TEXT, msgText);
		as.cm.setTopicData(topicID, 1, elementData);
		String assocID = as.cm.getNewAssociationID();
		as.cm.createAssociation(assocID, 1, ASSOCTYPE_ASSOCIATION, 1, topicID, 1, userID, 1);
		PresentableTopic presTopic = new PresentableTopic(topicID, 1, TOPICTYPE_EMAIL, 1, subject, getID(), "");
		directives.add(DIRECTIVE_SHOW_TOPIC, presTopic, Boolean.TRUE, "");
	}
	
	public void createDraftForForward(String userID, int userVersion, CorporateDirectives directives) {
		Hashtable data = getProperties();
		if (!data.get(PROPERTY_STATUS).equals(EMAIL_STATE_RECEIVED) &&
			!data.get(PROPERTY_STATUS).equals(EMAIL_STATE_SENT)) {
			return;
		}
		String rcpts = "";
		String author = as.getEmailAddress(userID);		// ### null
		String subject = "FW:" + (String)data.get(PROPERTY_SUBJECT);
		String date = "";
		String msgText = formatPassedMsgText(data, "");
		String topicID = as.cm.getNewTopicID();
		as.cm.createTopic(topicID, 1, TOPICTYPE_EMAIL, 1, subject);
		Hashtable elementData = new Hashtable();
		elementData.put(PROPERTY_FROM, author);
		elementData.put(PROPERTY_TO, rcpts);
		elementData.put(PROPERTY_SUBJECT, subject);
		elementData.put("UID", "0");
		elementData.put(PROPERTY_STATUS, EMAIL_STATE_DRAFT);
		elementData.put(PROPERTY_DATE, date);
		elementData.put(PROPERTY_TEXT, msgText);
		as.cm.setTopicData(topicID, 1, elementData);
		String assocID = as.cm.getNewAssociationID();
		as.cm.createAssociation(assocID, 1, ASSOCTYPE_ASSOCIATION, 1, topicID, 1, userID, userVersion);
		Vector vAssocs = copyAttachsAssocs(topicID);
		PresentableTopic presTopic = new PresentableTopic(topicID, 1, TOPICTYPE_EMAIL, 1, subject, getID(), "");
		directives.add(DIRECTIVE_SHOW_TOPIC, presTopic, Boolean.TRUE, "");
		showAssocs(vAssocs, directives);		
	}
	
	public Vector copyAttachsAssocs(String topicID) {
		BaseTopic attdoc = null;
		Vector vAssocs = new Vector();
		Enumeration attachs = as.getRelatedTopics(getID(), SEMANTIC_EMAIL_ATTACHMENT, 2).elements();
		while (attachs.hasMoreElements()) {
			attdoc = (BaseTopic) attachs.nextElement();
			if (attdoc.getType().equals(TOPICTYPE_DOCUMENT)) {
				String assocID = as.cm.getNewAssociationID();
				as.cm.createAssociation(assocID, 1, SEMANTIC_EMAIL_ATTACHMENT, 1, topicID, 1, attdoc.getID(), 1);
				PresentableAssociation presAssoc = new PresentableAssociation(assocID, 1,
					SEMANTIC_EMAIL_ATTACHMENT, 1, "", topicID, 1, attdoc.getID(), 1);
				vAssocs.add(presAssoc);
			}
		}
		return vAssocs;
	}

	public void showAssocs(Vector vAssocs, CorporateDirectives directives) {
		Enumeration assocs = vAssocs.elements();
		while (assocs.hasMoreElements()) {
			PresentableAssociation presAssoc = (PresentableAssociation) assocs.nextElement();
			directives.add(DIRECTIVE_SHOW_ASSOCIATION, presAssoc, Boolean.TRUE, "");
		}		
	}
	
	public String formatPassedMsgText(Hashtable data, String sIndent) {
		String sTextOut = "\n" + sIndent +"-----Original Message:-----\n";
		String sHeader = formatMsgHeaderAbstract(data, sIndent);
		sTextOut += sHeader;
		sTextOut += "\n";
		String sMsgOrig = (String)data.get(PROPERTY_TEXT);
		sTextOut += indentText(sMsgOrig, sIndent);
		return sTextOut;
	}
	
	public String formatMsgHeaderAbstract(Hashtable data, String sIndent) {
		String sTextOut = new String();
		sTextOut += sIndent + "From:\t" + (String) data.get(PROPERTY_FROM) + "\n";
		sTextOut += sIndent + "Sent:\t" + (String) data.get(PROPERTY_DATE) + "\n";
		sTextOut += sIndent + "To:\t" + (String) data.get(PROPERTY_TO) + "\n";
		sTextOut += sIndent + "Subject:\t" + (String) data.get(PROPERTY_SUBJECT) + "\n";
		return sTextOut;
	}
	
	public String indentText(String sTextIn, String sIndent) {
		String sTextOut = "";
		boolean bIndent = true;
		StringTokenizer st = new StringTokenizer(sTextIn, "\n\r", true);
		while (st.hasMoreTokens()) {
			String s = st.nextToken();
			if (bIndent) {
				bIndent = false;
				sTextOut += sIndent;
			}
			if (s.charAt(0) == '\n') {
				bIndent = true;
			}
			sTextOut += s;
		}
		return sTextOut;
	}



	// *********************
	// *** Inner Classes ***
	// *********************



	private class Recipient {

		Message.RecipientType type;
		String emailAddress;

		Recipient(String recipientType, String emailAddress) {
			if (recipientType.equals(RECIPIENT_TYPE_TO) || recipientType.equals("")) {
				this.type = Message.RecipientType.TO;
			} else if (recipientType.equals(RECIPIENT_TYPE_CC)) {
				this.type = Message.RecipientType.CC;
			} else if (recipientType.equals(RECIPIENT_TYPE_BCC)) {
				this.type = Message.RecipientType.BCC;
			} else {
				throw new DeepaMehtaException("unexpected recipient type: \"" + recipientType + "\"");
			}
			//
			this.emailAddress = emailAddress;
		}

		public boolean equals(Object o) {
			Recipient r = (Recipient) o;
			return r.type == type && r.emailAddress.equals(emailAddress);
		}
	}
}
