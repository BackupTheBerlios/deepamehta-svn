package de.deepamehta.topics;

import de.deepamehta.Topic;
import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.PresentableTopic;
import de.deepamehta.PresentableAssociation;
import de.deepamehta.PropertyDefinition;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.Session;
import de.deepamehta.service.CorporateCommands;
import de.deepamehta.service.CorporateDirectives;
//
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;
import javax.activation.*; 
import java.util.*;
import java.io.*;



/**
 * An email.
 * <P>
 * <HR>
 * Last sourcecode change: 3.1.2005 (2.0b4)<BR>
 * Last documentation update: 21.11.2001 (2.0a13-post1)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class EmailTopic extends LiveTopic {

	// email states
	private static final String EMAIL_STATE_DRAFT = "Draft";
	private static final String EMAIL_STATE_RECEIVED = "Received";
	private static final String EMAIL_STATE_SENT = "Sent";

	// commands
	private static final String ITEM_SEND = "Send";
	private static final String ITEM_REPLY = "Reply";
	private static final String ITEM_FORWARD = "Forward";
	
	// "Email" -> "Recipient" (User)
	public static final String ASSOCTYPE_EMAILADDRESSEE = "at-emailaddressee";	// ###
	public static final String ASSOCTYPE_ATTACHEMENT = "at-attachement";

	public class LocateRcptsResult  {
		public ArrayList	aRcpts;
		public boolean		bUsedAttached;
	}
	
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
		CorporateDirectives cd = super.evoke(session, topicmapID, viewmode);
		setTopicData(PROPERTY_STATUS, EMAIL_STATE_DRAFT);
		String author = as.getEmailAddress(session.getUserID());	// may return null
		if (author != null) {
			setTopicData(PROPERTY_FROM, author);
		}
		String assocID = as.cm.getNewAssociationID();
		cm.createAssociation(assocID, 1, ASSOCTYPE_ASSOCIATION, 1, getID(), 1, session.getUserID(), 1);
		return cd;
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
			commands.addCommand(ITEM_SEND, ITEM_SEND);
		} else if (s.equals(EMAIL_STATE_RECEIVED)) {
			commands.addCommand(ITEM_REPLY, ITEM_REPLY);
			commands.addCommand(ITEM_FORWARD, ITEM_FORWARD);
		} else if (s.equals(EMAIL_STATE_SENT)) {
			commands.addCommand(ITEM_FORWARD, ITEM_FORWARD);
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



	public CorporateDirectives executeCommand(String actionCommand, Session session,
													String topicmapID, String viewmode) {
		CorporateDirectives directives = new CorporateDirectives();
		if (actionCommand.equals(ITEM_SEND)) {
			sendMail(session.getUserID(), 1, directives);
		} else if (actionCommand.equals(ITEM_REPLY)) {
			createDraftForReply(session.getUserID(), 1, directives);
		} else if (actionCommand.equals(ITEM_FORWARD)) {
			createDraftForForward(session.getUserID(), 1, directives);
		} else {
			return super.executeCommand(actionCommand, session, topicmapID, viewmode);
		}
		return directives;
	}



	// ---------------------------
	// --- Handling Properties ---
	// ---------------------------



	public String getNameProperty() {
		return PROPERTY_SUBJECT;
	}



	// ***************
	// *** Methods ***
	// ***************



	private void sendMail(String userID, int userVersion, CorporateDirectives directives) throws DeepaMehtaException {
		Hashtable data = getProperties();
		if (!data.get(PROPERTY_STATUS).equals(EMAIL_STATE_DRAFT)) {
			return;
		}
		LocateRcptsResult locRes = locateRcpts();
		ArrayList aRcpts = locRes.aRcpts;
		if (aRcpts.size() == 0) {
			return;
		}
		// ### String from = as.getEmailAddress(userID);
		String author = (String) data.get(PROPERTY_FROM);
		System.out.println(">>> EmailTopic.sendMail(): " + this + ", author=\"" + author + "\"");
		Properties mprops = new Properties();
		mprops.put("mail.smtp.host", as.getSMTPServer());	// throws DME
		javax.mail.Session session = javax.mail.Session.getDefaultInstance(mprops, null);
		session.setDebug(true);
		try {
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(author));
			InternetAddress[] address = new InternetAddress[aRcpts.size()];
			for (int i = 0; i < aRcpts.size(); i++) {
				address[i] = new InternetAddress((String)aRcpts.get(i));
			}
			msg.setRecipients(Message.RecipientType.TO, address);
			msg.setSubject((String)data.get(PROPERTY_SUBJECT));
			Date d = new Date();
			msg.setSentDate(d);			
			String msgText = (String) data.get(PROPERTY_TEXT);
			addAttachs(msgText, msg);
			//
			Transport.send(msg);
			//
			setTopicData(PROPERTY_STATUS, EMAIL_STATE_SENT);
			setTopicData(PROPERTY_DATE, d.toString());
			Hashtable props = null;
			if (locRes.bUsedAttached) {
				String sRcpts = new String((String)aRcpts.get(0));
				for (int i = 1; i < aRcpts.size(); i++) {
					sRcpts += ", ";
					sRcpts += (String)aRcpts.get(i);
				}
				if (props == null) {
					props = new Hashtable();
				}
				props.put(PROPERTY_TO, sRcpts);
			}		
			/* ### if (!author.equals(from)) {
				if (props == null) {
					props = new Hashtable();
				}
				props.put(PROPERTY_FROM, from);
			}
			if (props != null) {
				directives.add(DIRECTIVE_SHOW_TOPIC_PROPERTIES, getID(), props, new Integer(1));
			} */
		} catch (MessagingException me) {
			throw new DeepaMehtaException(me.toString());
			/* ### mex.printStackTrace();
			Exception ex = null;
			if ((ex = mex.getNextException()) != null) {
				System.out.println("*** EmailTopic.sendMail(): exception " + ex.getMessage());
				ex.printStackTrace();
			} */
		}
	}

	public LocateRcptsResult locateRcpts() {
		LocateRcptsResult res = new LocateRcptsResult();
		ArrayList aOut = new ArrayList();
		res.aRcpts = aOut;
		res.bUsedAttached = false;
		HashSet setCheck = new HashSet();
		String sRcpts = getProperty(PROPERTY_TO);
		if (sRcpts != null) {
			StringTokenizer st = new StringTokenizer(sRcpts, ",;");
			while (st.hasMoreTokens()) {
				String val = st.nextToken();
				String key = new String(val);
				key = key.toLowerCase().trim();
				if (!setCheck.contains(key)) {
					setCheck.add(key);
					aOut.add(val);
				}
			}
		}
		Enumeration eRcpts = as.getRelatedTopics(getID(), ASSOCTYPE_EMAILADDRESSEE, 2).elements();
		while (eRcpts.hasMoreElements()) {
			BaseTopic user = (BaseTopic) eRcpts.nextElement();
			String val = as.getEmailAddress(user.getID());
			if ((val != null) && (val.length() > 0)) {
				String key = new String(val);
				key = key.toLowerCase().trim();
				if (!setCheck.contains(key)) {
					setCheck.add(key);
					aOut.add(val);
					res.bUsedAttached = true;
				}
			}
		}
		return res;
	}
	
	public void addAttachs(String msgText, MimeMessage msg) throws MessagingException {
		Enumeration docs = as.getRelatedTopics(getID(), ASSOCTYPE_ATTACHEMENT, 1).elements();
		Multipart mp = null;
		MimetypesFileTypeMap mapFileTypes = new MimetypesFileTypeMap();
		while (docs.hasMoreElements()) {
			BaseTopic doc = (BaseTopic)docs.nextElement();
			if (doc.getType().equals(TOPICTYPE_DOCUMENT)) {
				String sFileName = as.getTopicProperty(doc.getID(), doc.getVersion(), "File");
				String sFile = FILESERVER_DOCUMENTS_PATH + sFileName;
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
					mbpText.setContent(msgText, "text/plain");
					mp.addBodyPart(mbpText);
				}
				mp.addBodyPart(mbp);
			}
		}
		if (mp != null) {
			msg.setContent(mp);
		} else {
			msg.setContent(msgText, "text/plain");
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
		Enumeration attachs = as.getRelatedTopics(getID(), ASSOCTYPE_ATTACHEMENT, 1).elements();
		while (attachs.hasMoreElements()) {
			attdoc = (BaseTopic)attachs.nextElement();
			if (attdoc.getType().equals(TOPICTYPE_DOCUMENT)) {
				String assocID = as.cm.getNewAssociationID();
				as.cm.createAssociation( assocID, 1, ASSOCTYPE_ATTACHEMENT, 1, attdoc.getID(), 1, topicID, 1);
				PresentableAssociation presAssoc = new PresentableAssociation(
					assocID, 1, ASSOCTYPE_ATTACHEMENT, 1, "", attdoc.getID(), 1, topicID, 1);
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
}
