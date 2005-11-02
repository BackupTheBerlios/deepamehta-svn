package de.deepamehta.client;

import de.deepamehta.BaseAssociation;
import de.deepamehta.BaseTopic;
//
import javax.swing.ImageIcon;
import java.awt.Frame;
import java.util.*;



/**
 * This interface specifies the controler for a {@link PropertyPanel}.
 * <P>
 * <HR>
 * Last functional change: 8.12.2002 (2.0a17-pre2)<BR>
 * Last documentation update: 17.12.2001 (2.0a14-pre5)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
interface PropertyPanelControler {



	// ---------------------------
	// --- Providing the model ---
	// ---------------------------



	// ### types are twice in the model -- bad. Note: collection framework could be used,
	// ### but client is supposed to be run in JDK 1.1 compliant environments -- not true anymore

	Hashtable getTopicTypes();
	Hashtable getAssociationTypes();
	Vector getTopicTypesV();
	Vector getAssociationTypesV();

	// ---

	ImageIcon getIcon(String iconfile);
	String string(int item);	// returns language dependant string



	// -----------------
	// --- Callbacks ---
	// -----------------



	void changeTopicData(PresentationTopicMap topicmap, BaseTopic topic, Hashtable newData);
	void changeAssocData(PresentationTopicMap topicmap, BaseAssociation assoc, Hashtable newData);

	void executeTopicCommand(PresentationTopicMap topicmap, BaseTopic topic, String command);
	void executeAssocCommand(PresentationTopicMap topicmap, BaseAssociation assoc, String command);

	void beginLongTask();
	void endTask();
}
