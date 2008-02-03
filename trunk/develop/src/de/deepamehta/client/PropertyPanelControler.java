package de.deepamehta.client;

import de.deepamehta.BaseAssociation;
import de.deepamehta.BaseTopic;

import java.util.Hashtable;
import java.util.Vector;

import javax.swing.ImageIcon;



/**
 * This interface specifies the controler for a {@link PropertyPanel}.
 * <p>
 * <hr>
 * Last functional change: 8.12.2002 (2.0a17-pre2)<br>
 * Last documentation update: 17.12.2001 (2.0a14-pre5)<br>
 * J&ouml;rg Richter<br>
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
