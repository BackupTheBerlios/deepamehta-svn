package de.deepamehta;

import java.io.DataInputStream;
import java.io.IOException;
import java.sql.ResultSet;



/**
 * This class exists only for the sake of othogonality.
 * <P>
 * An association needs no extension of its basis model for being presented
 * (see {@link PresentableTopic}).
 * <P>
 * <HR>
 * Last functional change: 21.2.2003 (2.0a18-pre3)<BR>
 * Last documentation update: 29.11.2000 (2.0a7)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class PresentableAssociation extends BaseAssociation {



	// ********************
	// *** Constructors ***
	// ********************



	/**
	 * Standard constructor.
	 *
	 * @see		de.deepamehta.service.ApplicationService#createPresentableAssociation
	 * @see		de.deepamehta.service.RelationalCorporateMemory#createPresentableAssociations(ResultSet)
	 */
	public PresentableAssociation(String id, int version, String type, int typeVersion, String name,
				String topicID1, int topicVersion1, String topicID2, int topicVersion2) {
		super(id, version, type, typeVersion, name, topicID1, topicVersion1, topicID2, topicVersion2);
	}

	/**
	 * Copy constructor.
	 */
	public PresentableAssociation(BaseAssociation assoc) {
		super(assoc);
	}

	/**
	 * Stream constructor.
	 *
	 * @see		de.deepamehta.service.InteractionConnection#performAddAssociation
	 */
	public PresentableAssociation(DataInputStream in) throws IOException {
		super(in);
	}
}
