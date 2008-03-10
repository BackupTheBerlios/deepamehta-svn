package de.deepamehta.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateCommands;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.Session;

import java.util.StringTokenizer;



/**
 * A chat board is a topic map that holds chats.
 * <p>
 * <hr>
 * Last functional change: 10.3.2008 (2.0b8)<br>
 * Last documentation update: 11.4.2003 (2.0a18-pre9)<br>
 * J&ouml;rg Richter<br>
 * jri@freenet.de
 */
public class ChatBoardTopic extends TopicMapTopic {



	// **************
	// *** Fields ***
	// **************



	// commands

	static final String ITEM_SHOW_CHATS = "Show Chats";
	static final String CMD_SHOW_CHATS = "showChats";
	static final String ICON_SHOW_CHATS = "eye-grey.gif";

	static final String ITEM_CREATE_CHAT = "Create Chat";
	static final String CMD_CREATE_CHAT = "createChat";
	static final String ICON_CREATE_CHAT = "create.gif";



	// *******************
	// *** Constructor ***
	// *******************



	public ChatBoardTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}



	// **********************
	// *** Defining Hooks ***
	// **********************



	public CorporateCommands viewCommands(String topicmapID, String viewmode,
								Session session, CorporateDirectives directives) {
		CorporateCommands commands = new CorporateCommands(as);
		//
		BaseTopic messageBoard = as.originTopicmap(getID());
		if (messageBoard != null) {
			commands.addCommand(ITEM_SHOW_CHATS, CMD_SHOW_CHATS, FILESERVER_IMAGES_PATH, ICON_SHOW_CHATS);
			commands.addCommand(ITEM_CREATE_CHAT, CMD_CREATE_CHAT, FILESERVER_IMAGES_PATH, ICON_CREATE_CHAT);
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

	public CorporateDirectives executeCommand(String command, Session session,
						String topicmapID, String viewmode) throws DeepaMehtaException {
		CorporateDirectives directives = new CorporateDirectives();
		//
		StringTokenizer st = new StringTokenizer(command, COMMAND_SEPARATOR);
		String cmd = st.nextToken();
		//
		if (cmd.equals(CMD_SHOW_CHATS)) {
			int x = Integer.parseInt(st.nextToken());
			int y = Integer.parseInt(st.nextToken());
			String chatBoardID = as.originTopicmap(getID()).getID();
			// --- search chats associated with the chat board ---
			// Note: searchByTopicType() is defined in TopicMapTopic
			searchByTopicType(TOPICTYPE_CHAT, chatBoardID, x, y, topicmapID, viewmode, session, directives);
		} else if (cmd.equals(CMD_CREATE_CHAT)) {
			int x = Integer.parseInt(st.nextToken());
			int y = Integer.parseInt(st.nextToken());
			String chatBoardID = as.originTopicmap(getID()).getID();
			// --- create chat ---
			// Note: createTopic() is defined in TopicMapTopic
			String chatID = createTopic(TOPICTYPE_CHAT, x, y, topicmapID, session, directives);
			// --- associate chat with chat board --- ### see MessageBoardServlet.performAction()
			cm.createAssociation(as.getNewAssociationID(), 1, SEMANTIC_CHAT, 1, chatBoardID, 1, chatID, 1);
		} else {
			return super.executeCommand(command, session, topicmapID, viewmode);
		}
		return directives;
	}
}
