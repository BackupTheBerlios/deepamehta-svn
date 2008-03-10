package de.deepamehta.messageboard.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.messageboard.MessageBoard;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateCommands;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.Session;
import de.deepamehta.topics.TopicMapTopic;

import java.util.StringTokenizer;



/**
 * A Message Board.
 * <p>
 * <hr>
 * Last functional change: 10.3.2008 (2.0b8)<br>
 * Last documentation update: 17.9.2002 (2.0a16-pre3)<br>
 * J&ouml;rg Richter<br>
 * jri@freenet.de
 */
public class MessageBoardTopic extends TopicMapTopic implements MessageBoard {



	// **************
	// *** Fields ***
	// **************



	// commands

	static final String ITEM_SHOW_MESSAGES = "Show Messages";
	static final String CMD_SHOW_MESSAGES = "showMessages";
	static final String ICON_SHOW_MESSAGES = "eye-grey.gif";

	static final String ITEM_CREATE_MESSAGE = "Create Message";
	static final String CMD_CREATE_MESSAGE = "createMessage";
	static final String ICON_CREATE_MESSAGE = "create.gif";



	// *******************
	// *** Constructor ***
	// *******************



	public MessageBoardTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}



	// **********************
	// *** Defining Hooks ***
	// **********************



	// --------------------------
	// --- Providing Commands ---
	// --------------------------



	public CorporateCommands viewCommands(String topicmapID, String viewmode,
								Session session, CorporateDirectives directives) {
		CorporateCommands commands = new CorporateCommands(as);
		//
		BaseTopic messageBoard = as.originTopicmap(getID());
		if (messageBoard != null) {
			commands.addCommand(ITEM_SHOW_MESSAGES, CMD_SHOW_MESSAGES, FILESERVER_IMAGES_PATH, ICON_SHOW_MESSAGES);
			commands.addCommand(ITEM_CREATE_MESSAGE, CMD_CREATE_MESSAGE, FILESERVER_IMAGES_PATH, ICON_CREATE_MESSAGE);
			commands.addSeparator();
			commands.addCloseCommand(session);
		} else {
			// this message board was created manually and was never published
			// -- it can't be used until published
			commands.addPublishCommand(getID(), session, directives);
		}
		//
		return commands;
	}



	// --------------------------
	// --- Executing Commands ---
	// --------------------------



	public CorporateDirectives executeCommand(String command, Session session,
						String topicmapID, String viewmode) throws DeepaMehtaException {
		CorporateDirectives directives = new CorporateDirectives();
		//
		StringTokenizer st = new StringTokenizer(command, COMMAND_SEPARATOR);
		String cmd = st.nextToken();
		//
		if (cmd.equals(CMD_SHOW_MESSAGES)) {
			int x = Integer.parseInt(st.nextToken());
			int y = Integer.parseInt(st.nextToken());
			String messageBoardID = as.originTopicmap(getID()).getID();
			// --- search messages associated with the message board ---
			// Note: searchByTopicType() is defined in TopicMapTopic
			searchByTopicType(TOPICTYPE_MESSAGE, messageBoardID, x, y, topicmapID, viewmode,
				session, directives);
		} else if (cmd.equals(CMD_CREATE_MESSAGE)) {
			int x = Integer.parseInt(st.nextToken());
			int y = Integer.parseInt(st.nextToken());
			String messageBoardID = as.originTopicmap(getID()).getID();
			// --- create message ---
			// Note: createTopic() is defined in TopicMapTopic
			String messageID = createTopic(TOPICTYPE_MESSAGE, x, y, topicmapID, session, directives);
			// --- associate message with message board --- ### see MessageBoardServlet.performAction()
			cm.createAssociation(as.getNewAssociationID(), 1, SEMANTIC_MESSAGE_HIERARCHY, 1,
				messageBoardID, 1, messageID, 1);
		} else {
			return super.executeCommand(command, session, topicmapID, viewmode);
		}
		return directives;
	}



	// **********************
	// *** Public Methods ***
	// **********************



	/**
	 * @return	the webpage associated with this message board. <code>null</code> if no webpage is associated.
	 */
	public BaseTopic getWebpage() {
		BaseTopic webpage = as.getRelatedTopic(getID(), SEMANTIC_WEBPAGE,
			TOPICTYPE_WEBPAGE, 2, true);		// emptyAllowed=true ### throws ASE
		return webpage;
	}
}
