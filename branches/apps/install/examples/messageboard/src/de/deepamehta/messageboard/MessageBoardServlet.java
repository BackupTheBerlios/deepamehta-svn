package de.deepamehta.messageboard;

import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.messageboard.topics.MessageBoardTopic;
import de.deepamehta.messageboard.topics.MessageTopic;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.Session;
import de.deepamehta.service.web.DeepaMehtaServlet;
import de.deepamehta.service.web.RequestParameter;
import de.deepamehta.service.web.TopicTree;
import de.deepamehta.util.DeepaMehtaUtils;

import java.util.Vector;

import javax.servlet.ServletException;



/**
 * <p>
 * <hr>
 * Last change: 5.7.2008 (2.0b8)<br>
 * J&ouml;rg Richter<br>
 * jri@deepamehta.de
 */
public class MessageBoardServlet extends DeepaMehtaServlet implements MessageBoard {

	private String messageBoardID;
	private String messageBoardName;



	public void init() {
		super.init();
		//
		initMessageBoard(sc.getInitParameter("messageboard"));	// ### move to session init
	}



	protected String performAction(String action, RequestParameter params, Session session, CorporateDirectives directives)
																									throws ServletException {
		if (action == null) {
			// store in session
			setMode(MODE_SHOW_MESSAGE, session);
			//
			return PAGE_MESSAGE_BOARD;
		} else if (action.equals(ACTION_SHOW_MESSAGE)) {
			String messageID = params.getValue("id");
			// store in session
			setMode(MODE_SHOW_MESSAGE, session);
			if (checkMessage(messageID, session)) {
				setMessage(messageID, session);
			}
			//
			return PAGE_MESSAGE_BOARD;
		} else if (action.equals(ACTION_WRITE_TOPLEVEL_MESSAGE)) {
			// store in session
			setMode(MODE_WRITE_TOPLEVEL_MESSAGE, session);
			//
			return PAGE_MESSAGE_BOARD;
		} else if (action.equals(ACTION_WRITE_REPLY_MESSAGE)) {
			// store in session
			setMode(MODE_WRITE_REPLY_MESSAGE, session);
			//
			return PAGE_MESSAGE_BOARD;
		} else if (action.equals(ACTION_CREATE_MESSAGE)) {
			if (!getMode(session).equals(MODE_SHOW_MESSAGE)) {
				if (params.getParameter("button").equals("OK")) {
					// create message
					prepareMessage(params);
					String messageID = createTopic(TOPICTYPE_MESSAGE, params, session, directives);
					finalizeMessage(messageID, session);
					// store in session
					setMessage(messageID, session);
					setPageNr(0, session);
				} else {
					System.out.println(">>> no message created (cancelled by user)");
				}
				setMode(MODE_SHOW_MESSAGE, session);
			} else {
				System.out.println(">>> no message created (accidental action was caused by 'reload' button)");
			}
			//
			return PAGE_MESSAGE_BOARD;
		//
		// --- ACTION_COLLAPSE_NODE and ACTION_EXTEND_NODE ---
		//
		} else if (action.equals(ACTION_COLLAPSE_NODE)) {
			String messageID = params.getValue("id");
			Vector extendedNodes = getExtendedNodes(session);
			if (!extendedNodes.removeElement(messageID)) {
				System.out.println("*** ACTION_COLLAPSE_NODE: node " + messageID +
					" not found in list of extended nodes");
			}
			return PAGE_MESSAGE_BOARD;
		} else if (action.equals(ACTION_EXTEND_NODE)) {
			String messageID = params.getValue("id");
			Vector extendedNodes = getExtendedNodes(session);
			// ### error check
			if (extendedNodes.contains(messageID)) {
				System.out.println("*** ACTION_EXTEND_NODE: node " + messageID +
					" already in list of extended nodes");
			} else {
				extendedNodes.addElement(messageID);
			}
			return PAGE_MESSAGE_BOARD;
		//
		// --- ACTION_NEXT_PAGE and ACTION_PREV_PAGE ---
		//
		} else if (action.equals(ACTION_NEXT_PAGE)) {
			// ### pending: reloading might cause error
    		int pageNr = getPageNr(session);
    		// ### int pageSize = getPageSize(session);
    		setPageNr(pageNr + 1, session);
			return PAGE_MESSAGE_BOARD;
		} else if (action.equals(ACTION_PREV_PAGE)) {
			// ### pending: reloading might cause error
    		int pageNr = getPageNr(session);
    		// ### int pageSize = getPageSize(session);
    		setPageNr(pageNr - 1, session);
			return PAGE_MESSAGE_BOARD;
		} else {
			return super.performAction(action, params, session, directives);
		}
	}

	protected void preparePage(String page, RequestParameter params, Session session, CorporateDirectives directives) {
		if (page.equals(PAGE_MESSAGE_BOARD)) {
    		// --- update session attributes ---
    		setExtendedNodes(session);
    		setMessageBoardName(session);
    		// message count
    		Vector toplevelMessages = as.getRelatedTopics(messageBoardID, SEMANTIC_MESSAGE_HIERARCHY, TOPICTYPE_MESSAGE, 2);
    		session.setAttribute("messageCount", new Integer(toplevelMessages.size()));
    		// message tree
    		String[] sortProps = {PROPERTY_LAST_REPLY_DATE, PROPERTY_LAST_REPLY_TIME};
    		int pageNr = getPageNr(session);
    		int pageSize = getPageSize(session);
			TopicTree messages = getTopicTree(messageBoardID, TOPICTYPE_MESSAGE,
				SEMANTIC_MESSAGE_HIERARCHY, sortProps, true, pageNr, pageSize);		// descending=true
    		session.setAttribute("messages", messages);
    		// webpage
			MessageBoardTopic messageBoard = (MessageBoardTopic) as.getLiveTopic(messageBoardID, 1);
			BaseTopic webpage = messageBoard.getWebpage();
    		session.setAttribute("webpageName", webpage != null ? webpage.getName() : null);
    		session.setAttribute("webpageURL", webpage != null ? as.getTopicProperty(webpage, PROPERTY_URL) : null);
    		// ---
   			checkMessage(getMessage(session), session);
		}
	}



	// ***********************
	// *** Private Methods ***
	// ***********************



	/**
	 * Initializes instance variables "messageBoardID" and "messageBoardName"
	 *
	 * @see		#init
	 */
	private void initMessageBoard(String messageBoardID) {
		if (messageBoardID == null) {
			messageBoardID = DEFAULT_MESSAGE_BOARD;
		}
		//
		MessageBoardTopic messageBoard = (MessageBoardTopic) as.getLiveTopic(messageBoardID, 1);
		this.messageBoardID = messageBoardID;
		this.messageBoardName = messageBoard.getName();	// ### version=1
		//
		System.out.println(">>> MessageBoardServlet.initMessageBoard(): \"" + messageBoardName + "\" (" + messageBoardID + ")");
	}

	// ---

	private void prepareMessage(RequestParameter params) {
		// empty subject?
		if (params.getParameter(PROPERTY_NAME).equals("")) {
			params.setParameter(PROPERTY_NAME, "<no subject>");
		}
		// qutoe HTML and XML tags
		String message = DeepaMehtaUtils.encodeHTMLTags(params.getParameter(PROPERTY_DESCRIPTION), true);	// allowSomeTags=true
		params.setParameter(PROPERTY_DESCRIPTION, message);
	}

	private void finalizeMessage(String messageID, Session session) {
		String mode = getMode(session);
		if (mode.equals(MODE_WRITE_TOPLEVEL_MESSAGE)) {
			// associate message with message board ### see MessageBoardTopic.executeCommand()
			cm.createAssociation(as.getNewAssociationID(), 1, SEMANTIC_MESSAGE_HIERARCHY, 1,
				messageBoardID, 1, messageID, 1);
		} else if (mode.equals(MODE_WRITE_REPLY_MESSAGE)) {
			// associate message with toplevel message ### see MessageTopic.executeCommand()
			String toplevelMessageID = getToplevelMessage(session).getID();
			cm.createAssociation(as.getNewAssociationID(), 1, SEMANTIC_MESSAGE_HIERARCHY, 1,
				toplevelMessageID, 1, messageID, 1);
			// extend toplevel message
			Vector extendedNodes = getExtendedNodes(session);
			if (!extendedNodes.contains(toplevelMessageID)) {
				extendedNodes.addElement(toplevelMessageID);
			}
			// propagate last reply date/time to toplevel message ### pending for java client
			String date = as.getTopicProperty(messageID, 1, PROPERTY_LAST_REPLY_DATE);
			String time = as.getTopicProperty(messageID, 1, PROPERTY_LAST_REPLY_TIME);
			as.setTopicProperty(toplevelMessageID, 1, PROPERTY_LAST_REPLY_DATE, date);
			as.setTopicProperty(toplevelMessageID, 1, PROPERTY_LAST_REPLY_TIME, time);
		} else {
			throw new DeepaMehtaException("unexpected mode: \"" + mode + "\"");
		}
	}

	// --- Methods to store data in the session

	private void setMessageBoardName(Session session) {
		if (getMessageBoard(session) == null)  {
			session.setAttribute("messageboard", messageBoardName);
			System.out.println("> \"messageboard\" stored in session: " + messageBoardName);
		}
	}

	private void setExtendedNodes(Session session) {
		if (getExtendedNodes(session) == null)  {
			Vector extendedNodes = new Vector();
			extendedNodes.addElement(messageBoardID);
			session.setAttribute("extendedNodes", extendedNodes);
			System.out.println("> \"extendedNodes\" stored in session");
			//
			setPageSize(PAGE_SIZE, session);
			setPageNr(0, session);
		}
	}

	private void setMode(String mode, Session session) {
		session.setAttribute("mode", mode);
		System.out.println("> \"mode\" stored in session: " + mode);
	}

	private void setPageSize(int pageSize, Session session) {
		session.setAttribute("pageSize", new Integer(pageSize));
		System.out.println("> \"pageSize\" stored in session: " + pageSize);
	}

	private void setPageNr(int pageNr, Session session) {
		session.setAttribute("pageNr", new Integer(pageNr));
		System.out.println("> \"pageNr\" stored in session: " + pageNr);
	}

	/**
	 * Called for ACTION_SHOW_MESSAGE and ACTION_CREATE_MESSAGE
	 *
	 * @param	messageID	null means no selection
	 */
	private void setMessage(String messageID, Session session) {
		BaseTopic toplevelMessage = null;	// ### causes exception if a user is writing a reply in this moment
		if (messageID != null) {
			MessageTopic message = (MessageTopic) as.getLiveTopic(messageID, 1);	// ### version=1
			toplevelMessage = message.getToplevelMessage();
			session.setAttribute("message", new Message(messageID, as));
		}
		session.setAttribute("messageID", messageID);
		session.setAttribute("toplevelMessage", toplevelMessage);
		//
		System.out.println("> \"messageID\" stored in session: " + messageID);
		System.out.println("> \"toplevelMessage\" stored in session: " + toplevelMessage);
	}

	private boolean checkMessage(String messageID, Session session) {
		if (messageID == null) {
			return false;
		}
   		if (!cm.topicExists(messageID)) {
   			setMessage(null, session);
			System.out.println(">>> message " + messageID + " didn't exist anymore -- selection is removed");
   			return false;
   		}
   		return true;
	}

	// ---

	private String getMessageBoard(Session session) {
		return (String) session.getAttribute("messageboard");
	}

	private Vector getExtendedNodes(Session session) {
		return (Vector) session.getAttribute("extendedNodes");
	}

	private String getMessage(Session session) {
		return (String) session.getAttribute("messageID");
	}

	private BaseTopic getToplevelMessage(Session session) {
		return (BaseTopic) session.getAttribute("toplevelMessage");
	}

	private String getMode(Session session) {
		return (String) session.getAttribute("mode");
	}

	private int getPageSize(Session session) {
		return ((Integer) session.getAttribute("pageSize")).intValue();
	}

	private int getPageNr(Session session) {
		return ((Integer) session.getAttribute("pageNr")).intValue();
	}
}
