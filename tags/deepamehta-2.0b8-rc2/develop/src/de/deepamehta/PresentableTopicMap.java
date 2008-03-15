package de.deepamehta;

import de.deepamehta.util.DeepaMehtaUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;



/**
 * Extends a basis topicmap ... ###
 * <P>
 * <HR>
 * Last functional change: 24.12.2001 (2.0a14-pre5)<BR>
 * Last documentation update: 24.12.2001 (2.0a14-pre5)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class PresentableTopicMap extends BaseTopicMap implements DeepaMehtaConstants {



	// **************
	// *** Fields ***
	// **************



	protected String bgImagefile;
	protected String bgColorcode;		// #<rr><gg><bb>
	protected String translationCoord;	// <x>:<y>



	// ********************
	// *** Constructors ***
	// ********************



	/**
	 * Standard constructor.
	 * <P>
	 * References checked: 26.12.2001 (2.0a14-pre5)
	 *
	 * @see		de.deepamehta.service.ApplicationService.createUserView()
	 */
	public PresentableTopicMap(Vector topics, Vector associations, String bgImagefile,
											String bgColorcode, String translationCoord) {
		super(topics, associations);
		//
		this.bgImagefile = bgImagefile;
		this.bgColorcode = bgColorcode;
		this.translationCoord = translationCoord;
	}

	/**
	 * Copy constructor.
	 * <P>
	 * References checked: 27.12.2001 (2.0a14-pre5)
	 *
	 * @see		de.deepamehta.client.PresentationTopicMap#PresentationTopicMap(PresentableTopicMap, int editorContext, String topicmapID, String viewMode, PresentationService)
	 */
	public PresentableTopicMap(PresentableTopicMap topicmap) {
		super(topicmap);
		//
		this.bgImagefile = topicmap.bgImagefile;
		this.bgColorcode = topicmap.bgColorcode;
		this.translationCoord = topicmap.translationCoord;
	}

	/**
	 * Stream constructor.
	 * <P>
	 * References checked: 27.12.2001 (2.0a14-pre5)
	 *
	 * @see		de.deepamehta.client.PresentationTopicMap#PresentationTopicMap(DataInputStream, int editorContext, String topicmapID, String viewMode, PresentationService)
	 */
	public PresentableTopicMap(DataInputStream in) throws IOException {
		this.topics = DeepaMehtaUtils.fromTopicVector(
			DeepaMehtaUtils.readTopics(in));
		this.associations = DeepaMehtaUtils.fromAssociationVector(
			DeepaMehtaUtils.readAssociations(in));
		//
		this.bgImagefile = in.readUTF();
		this.bgColorcode = in.readUTF();
		this.translationCoord = in.readUTF();
	}



	// ---------------------
	// --- Serialization ---
	// ---------------------



	/**
	 * @see		InteractionConnection#createCorporateTopicMap
	 * @see		de.deepamehta.Directives#write
	 */
	public void write(DataOutputStream out) throws IOException {
		super.write(out);
		//
		out.writeUTF(bgImagefile);
		out.writeUTF(bgColorcode);
		out.writeUTF(translationCoord);
	}
}
