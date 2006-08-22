package de.deepamehta.service;

import de.deepamehta.DeepaMehtaException;
import de.deepamehta.Detail;
//
import java.io.*;
import java.util.*;



/**
 * ### to be dropped
 * <P>
 * Server side extension of topic/association {@link de.deepamehta.Detail}.
 * <P>
 * <HR>
 * Last functional change: 12.4.2003 (2.0a18-pre9)<BR>
 * Last documentation update: 14.10.2001 (2.0a13-pre1)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class CorporateDetail extends Detail {



	// **************
	// *** Fields ***
	// **************



	private ApplicationService as;



	// ********************
	// *** Constructors ***
	// ********************



	/**
	 * Copy constructor.
	 * <P>
	 * References checked: 2.1.2002 (2.0a14-pre5)
	 *
	 * @see		EmbeddedService#processTopicDetail
	 */
	public CorporateDetail(Detail detail, ApplicationService as) {
		super(detail);
		this.as = as;
	}

	/**
	 * Stream constructor.
	 * <P>
	 * References checked: 20.10.2001 (2.0a13-pre1)
	 *
	 * @see		InteractionConnection#performProcessTopicDetail
	 */
	CorporateDetail(DataInputStream in, ApplicationService as) throws IOException {
		super(in);
		this.as = as;
	}



	// ***************
	// *** Methods ***
	// ***************



	/**
	 * References checked: 22.11.2001 (2.0a13-post1)
	 *
	 * @see		de.deepamehta.topics.LiveTopic#processDetailHook
	 */
	public CorporateDirectives process(Session session, String topicID, int version,
													String topicmapID, String viewmode) {
		CorporateDirectives directives = new CorporateDirectives();
		//
		StringTokenizer st = new StringTokenizer(command, ":");
		String cmd = st.nextToken();
		//
		/* ### if (cmd.equals(CMD_CHANGE_TOPIC_NAME)) {
			String name = (String) param1;
			directives.add(as.changeTopicName(topicID, version, name, topicmapID, viewmode));
			return directives;
		} else */ if (cmd.equals(CMD_EDIT_TOPIC_PROPERTY)) {
			String prop = st.nextToken();
			String value = (String) param1;
			Hashtable props = new Hashtable();
			props.put(prop, value);
			directives.add(DIRECTIVE_SHOW_TOPIC_PROPERTIES, topicID, props, new Integer(version));
			return directives;
		} else if (cmd.equals(CMD_EDIT_ASSOC_PROPERTY)) {
			String prop = st.nextToken();
			String value = (String) param1;
			Hashtable props = new Hashtable();
			props.put(prop, value);
			directives.add(DIRECTIVE_SHOW_ASSOC_PROPERTIES, topicID, props, new Integer(version));
			return directives;
		} else {
			throw new DeepaMehtaException("unexpected command: \"" + command + "\"");
		}
	}
}
