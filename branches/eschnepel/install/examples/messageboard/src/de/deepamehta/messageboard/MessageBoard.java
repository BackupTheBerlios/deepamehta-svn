package de.deepamehta.messageboard;

import de.deepamehta.DeepaMehtaConstants;



/**
 * <P>
 * <HR>
 * Last functional change: 20.9.2002 (2.0a16-pre4)<BR>
 * Last documentation update: 20.9.2002 (2.0a16-pre4)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public interface MessageBoard extends DeepaMehtaConstants {



	// *****************
	// *** Constants ***
	// *****************



	// -----------------------------
	// --- Messageboard Settings ---
	// -----------------------------



	static final String DEFAULT_MESSAGE_BOARD = "t-deepamehtaforum";
	static final int PAGE_SIZE = 7;



	// ---------------
	// --- Strings ---
	// ---------------



	static final String STRING_CREATE_TOPIC = "Create new Topic";
	static final String STRING_REPLY_TO_TOPIC = "Reply to ";



	// ---------------
	// --- Actions ---
	// ---------------



	static final String ACTION_SHOW_MESSAGE = "showMessage";
	static final String ACTION_CREATE_MESSAGE = "createMessage";
	static final String ACTION_WRITE_TOPLEVEL_MESSAGE = "writeToplevelMessage";
	static final String ACTION_WRITE_REPLY_MESSAGE = "writeReplyMessage";



	// -------------
	// --- Modes ---
	// -------------



	static final String MODE_SHOW_MESSAGE = "showMessage";
	static final String MODE_WRITE_TOPLEVEL_MESSAGE = "writeToplevelMessage";
	static final String MODE_WRITE_REPLY_MESSAGE = "writeReplyMessage";



	// -------------
	// --- Pages ---
	// -------------



    static final String PAGE_MESSAGE_BOARD = "MessageBoard";
}
