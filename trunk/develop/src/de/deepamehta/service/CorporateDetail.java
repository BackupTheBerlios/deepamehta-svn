package de.deepamehta.service;

import de.deepamehta.DeepaMehtaException;
import de.deepamehta.Detail;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.StringTokenizer;



/**
 * ### to be dropped
 * <p>
 * Server side extension of topic/association {@link de.deepamehta.Detail}.
 * <p>
 * <hr>
 * Last functional change: 3.2.2008 (2.0b8)<br>
 * Last documentation update: 14.10.2001 (2.0a13-pre1)<br>
 * J&ouml;rg Richter<br>
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
	 * <p>
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
	 * <p>
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
		return new CorporateDirectives();
	}
}
