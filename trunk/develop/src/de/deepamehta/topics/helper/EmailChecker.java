package de.deepamehta.topics.helper;

import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.FileServer;
import de.deepamehta.service.ApplicationService;

import com.sun.mail.pop3.POP3Folder;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import javax.mail.Address;
import javax.mail.FetchProfile;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.UIDFolder;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;



public class EmailChecker implements DeepaMehtaConstants, Runnable {

	public static final String ASSOCTYPE_ATTACHEMENT = "at-attachement";
	
	public class MsgFound {

		public boolean	bFound;
		public String	text;
		
		MsgFound() { 
			bFound = false;
			text = new String();
		}
	}
	
	static final long clSleep = 5 * 60 * 1000;	// every 5 minutes
	private String	userID;
	private int		userVersion;
	ApplicationService	as;

	/**
	 * @see		de.deepamehta.service.ApplicationService#startSession
	 */
	public EmailChecker(String userID, int userVersion, ApplicationService as) {
		this.userID = userID;
		this.userVersion = userVersion;
		this.as = as;
		//
		new Thread(this).start();
	}
	
	public void run() {
		while (true) {
			updateEmails();
			try {
				Thread.sleep(clSleep);
			} catch (InterruptedException e) {
			}
		}
	}
	
	public synchronized void updateEmails() {
		try {
			// ### System.out.println(">>> checking emails for user \"" + userID + "\"");
			int newMailCount = 0;
			String mbox = "INBOX";
			UIDFolder ufolder = null;
			POP3Folder pfolder = null;
			//
			Properties props = System.getProperties();
			Session session = Session.getInstance(props, null);
			// session.setDebug(debug);
			Store store = null;
			String url = as.getMailboxURL(userID);
			if (url == null) {
				// ### System.out.println("*** EmailChecker.updateEmails(): for user \"" + userID + "\" no mailbox is set");
				return;
			}
			URLName urln = new URLName(url);
			store = session.getStore(urln);
			store.connect();
			Folder folder = store.getDefaultFolder();
			if (folder == null) {
				System.out.println("No default folder");
				return;
			}
			folder = folder.getFolder(mbox);
			if (folder == null) {
				System.out.println("Invalid folder");
				return;
			}
			//
			if ((folder instanceof UIDFolder)) {
			    ufolder = (UIDFolder)folder;
			} else if (folder instanceof POP3Folder) {
				pfolder = (POP3Folder)folder;
			} else {
				System.out.println("This Provider or this folder does not support UIDs");
				return;
			}
			try {
				folder.open(Folder.READ_WRITE);
			} catch (MessagingException ex) {
				folder.open(Folder.READ_ONLY);
			}
			int totalMessages = folder.getMessageCount();
			//
			if (totalMessages == 0) {
				System.out.println("Empty folder");
				folder.close(false);
				store.close();
				return;
			}
			// Attributes & Flags for all messages ..
			Message[] msgs = folder.getMessages();
			// Use a suitable FetchProfile
			FetchProfile fp = new FetchProfile();
			fp.add(FetchProfile.Item.ENVELOPE);
			fp.add(FetchProfile.Item.FLAGS);
			fp.add("X-Mailer");
			folder.fetch(msgs, fp);
			//
			Hashtable mailsMap = getMailsMap(userID, userVersion);
			for (int i = 0; i < msgs.length; i++) {
				String sUID = "";
				if (ufolder != null) {
					sUID += ufolder.getUID(msgs[i]);
				} else {
					sUID += pfolder.getUID(msgs[i]);
				}
				if (mailsMap.get(sUID) == null) {
					addEmail(msgs[i], sUID);
					newMailCount++;
				}
			}
			//
			System.out.println(">>> " + newMailCount + " new emails for user \"" + userID + "\"");
			folder.close(false);
			store.close();
		} catch (NoClassDefFoundError e) {
			System.out.println("*** EmailChecker.updateEmails(): " + e + " -- mailbox for \"" + userID + "\" not updated");
			String className = e.getMessage();
			if (className.startsWith("javax/mail/")) {
				System.out.println(">>> Make shure Sun's Javamail package (mail.jar) is installed correctly");
			}
		} catch (Exception e) {
			System.out.println("*** EmailChecker.updateEmails(): " + e + " -- mailbox for \"" + userID + "\" not updated");
		}
	}
	
	void addEmail(Message msg, String sUID) {
		String name = "";	// topicname
		String author = "";
		String authorAddress = "";
		String rcpts = "";
		String subject = "";
		String date = "Unknown";
		MsgFound msgFound = new MsgFound();
		Vector attachs = new Vector();
		try {
			name = msg.getSubject();
			if (name.length() > 32) {	// ###
				name = name.substring( 0, 32);
			}
			Address[] a = msg.getFrom();
			if (a != null) {
				for (int i = 0; i < a.length; i++) {
					if (i > 0) {
						author += ", ";
					}
					if (a[i] instanceof InternetAddress) {
						InternetAddress ia = (InternetAddress)a[i];
						author += ia.getPersonal() + " <" + ia.getAddress() + ">";
						if (authorAddress.length()==0) {
							authorAddress = ia.getAddress();
						}
					} else {
						author += a[i].toString();
					}
				}
			}
			//
			a = msg.getRecipients(Message.RecipientType.TO);
			if (a != null) {
				for (int i = 0; i < a.length; i++) {
					if (i > 0) {
						rcpts += ",";
					}
					rcpts += a[i].toString();
				}
			}
			subject = msg.getSubject();
			Date d = msg.getSentDate();
			if (d != null) {
				date = d.toString();
			}
			traverseParts(msg, msgFound, attachs);
		} catch (Exception ex) {
			System.out.println("*** EmailChecker.addEmail(): " + ex);
			ex.printStackTrace();
		}
		// create "Email" topic
		System.out.println("> new mail from \"" + author + "\": \"" + subject + "\", " + date);
		String topicID = as.cm.getNewTopicID();
		as.cm.createTopic(topicID, 1, TOPICTYPE_EMAIL, 1, name);
		Hashtable props = new Hashtable();
		props.put(PROPERTY_FROM, author);
		props.put("AuthorAddress", authorAddress);
		props.put(PROPERTY_TO, rcpts);
		props.put(PROPERTY_SUBJECT, subject);
		props.put("UID", sUID);
		props.put(PROPERTY_STATUS, "Received");
		props.put(PROPERTY_DATE, date);
		props.put(PROPERTY_TEXT, msgFound.bFound ? msgFound.text : "");
		as.cm.setTopicData(topicID, 1, props);
		String assocID = as.cm.getNewAssociationID();
		as.cm.createAssociation(assocID, 1, ASSOCTYPE_ASSOCIATION, 1, topicID, 1, userID, 1);
		//
		addAttachs(topicID, attachs);
	}

	public boolean traverseParts(Part p, MsgFound msgFound, Vector attachs) {
		try {
			if (p.isMimeType("text/plain")) {
				if (!msgFound.bFound) {
					msgFound.text = (String)p.getContent();
					msgFound.bFound = true;
				} else {
					attachs.add(p);
				}
			} else if (p.isMimeType("multipart/*")) {
				Multipart mp = (Multipart)p.getContent();
				int count = mp.getCount();
				for (int i = 0; i < count; i++) {
					traverseParts( mp.getBodyPart(i), msgFound, attachs);
				}
			} else if (p.isMimeType("message/rfc822")) {
				//todo
			} else {
				Object o = p.getContent();
				if (o instanceof String) {
					//todo
				} else if (o instanceof InputStream) {
					attachs.add(p);
				}
			}
		} catch (MessagingException ex) {
			System.out.println("EmailChecker.traverseParts: E-mail exception " + ex.getMessage());
			ex.printStackTrace();
		} catch (Exception ex) {
			System.out.println("EmailChecker.traverseParts: Exception " + ex.getMessage());
			ex.printStackTrace();
		}
		return true;
	}
	
	public void addAttachs(String mailID, Vector attachs) {
		Enumeration e = attachs.elements();
		try {
			while (e.hasMoreElements()) {
				Part p = (Part)e.nextElement();
				String fileName = p.getFileName();
				if (fileName == null) { continue;
				}
				if (p.isMimeType("text/plain")) {
					addAttach(mailID, p.getContentType(), fileName, new StringReader((String)p.getContent()));
				} else {
					Object o = p.getContent();
					if (o instanceof InputStream) {
						addAttach( mailID, p.getContentType(), fileName, (InputStream)o);
					}
				}
			}
		} catch (MessagingException ex) {
			System.out.println("EmailChecker.addAttachs: E-mail exception " + ex.getMessage());
			ex.printStackTrace();
		} catch (IOException ex) {
			return;
		}
	}
	
	public boolean addAttach(String mailID, String mimeType, String fileName, Object inData) {
		String path = FileServer.repositoryPath(FILE_DOCUMENT);
		File dstDir = new File(path);
		// ### create target directory
		if (dstDir.mkdirs()) {
			System.out.println(">>> EmailChecker.saveAttach(): target directory created: " + dstDir);
		}
		File dstFile;
		try {
			dstFile = File.createTempFile("att", fileName, dstDir);	// ### requires JDK 1.2
			if (inData instanceof InputStream) {
				copyFile((InputStream) inData, dstFile);
			}else if (inData instanceof Reader) {
				copyFile((Reader) inData, dstFile);
			}
		} catch (IOException e) {
			return false;
		}
		//
		Hashtable props = new Hashtable();
		String topicID = as.cm.getNewTopicID();
		as.cm.createTopic(topicID, 1, TOPICTYPE_DOCUMENT, 1, fileName);
		props.put("File", dstFile.getName());
		props.put("MimeType", mimeType);
		as.cm.setTopicData(topicID, 1, props);
		String assocID = as.cm.getNewAssociationID();
		as.cm.createAssociation(assocID, 1, ASSOCTYPE_ATTACHEMENT, 1, topicID, 1, mailID, 1);
		return true;
	}

	public void copyFile(InputStream inStream, File dstFile) throws IOException {
		InputStream fileIn = new BufferedInputStream(inStream, FILE_BUFFER_SIZE);
		OutputStream fileOut = new BufferedOutputStream(
							   new FileOutputStream(dstFile), FILE_BUFFER_SIZE);
		byte[] buffer = new byte[FILE_BUFFER_SIZE];
		long totalBytes = 0;
		int readBytes;
		while ((readBytes = fileIn.read(buffer)) != -1) {
			fileOut.write(buffer, 0, readBytes);
			totalBytes += readBytes;
		}
		fileIn.close();
		fileOut.close();
		System.out.println("> " + totalBytes + " bytes copied");
	}
	
	public void copyFile(Reader reader, File dstFile) throws IOException {
		Reader fileIn = new BufferedReader(reader, FILE_BUFFER_SIZE/2);
		Writer fileOut = new BufferedWriter(new FileWriter(dstFile), FILE_BUFFER_SIZE/2);
		char[] buffer = new char[FILE_BUFFER_SIZE/2];
		long totalChars = 0;
		int readChars;
		while ((readChars = fileIn.read(buffer)) != -1) {
			fileOut.write(buffer, 0, readChars);
			totalChars += readChars;
		}
		fileIn.close();
		fileOut.close();
		System.out.println("> " + totalChars + " chars copied");
	}
	
	public Hashtable getMailsMap(String userID, int userVersion) {
		Hashtable mapMails = new Hashtable();
		Enumeration mails = as.getRelatedTopics(userID, ASSOCTYPE_ASSOCIATION, 1).elements();
		while (mails.hasMoreElements()) {
			BaseTopic mail = (BaseTopic) mails.nextElement();
			if (mail.getType().equals(TOPICTYPE_EMAIL)) {
				String uid = as.getTopicProperty(mail.getID(), mail.getVersion(), "UID");
				mapMails.put(uid, mail);
			}
		}
		return mapMails;
	}
}

