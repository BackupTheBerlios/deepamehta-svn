package de.deepamehta.client;

import de.deepamehta.DeepaMehtaException;
import de.deepamehta.Directives;
import de.deepamehta.PresentableAssociation;
import de.deepamehta.PresentableTopic;
import de.deepamehta.PropertyDefinition;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.text.JTextComponent;



/**
 * <P>
 * <HR>
 * Last functional change: 29.12.2001 (2.0a14-pre5)<BR>
 * Last documentation update: 17.12.2001 (2.0a14-pre5)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class DeepaMehtaClientUtils {

	/**
	 * Part of reading a DIRECTIVE_SHOW_TOPICS.
	 * <P>
	 * References checked: 27.12.2001 (2.0a14-pre5)
	 *
	 * @return	Vector of {@link PresentationTopic}
	 *
	 * @see		PresentationDirectives#PresentationDirectives(DataInputStream, PresentationService)
	 */
	static Vector readPresentationTopics(DataInputStream in, PresentationService ps)
														throws IOException {
		Vector topics = new Vector();
		// read number of topics
		int topicCount = in.readInt();
		// read topics
		for (int i = 0; i < topicCount; i++) {
			topics.addElement(new PresentationTopic(in, ps));
		}
		return topics;
	}

	/**
	 * Part of reading a DIRECTIVE_SHOW_ASSOCIATIONS.
	 * <P>
	 * References checked: 27.12.2001 (2.0a14-pre5)
	 *
	 * @return	Vector of {@link PresentationAssociation}
	 *
	 * @see		PresentationDirectives#PresentationDirectives(DataInputStream, PresentationService)
	 */
	static Vector readPresentationAssociations(DataInputStream in) throws IOException {
		Vector associations = new Vector();
		// read number of associations
		int assocCount = in.readInt();
		// read associations
		for (int i = 0; i < assocCount; i++) {
			associations.addElement(new PresentationAssociation(in));
		}
		return associations;
	}



	// -------------------------------------
	// --- Creating Presentation Objects ---
	// -------------------------------------



	/**
	 * Transforms PresentableTopics into PresentationTopics.
	 * <P>
	 * ### Part of reading a DIRECTIVE_SHOW_TOPICS.
	 *
	 * @param	topics	element type is {@link de.deepamehta.PresentableTopic}
	 *
	 * @see		PresentationDirectives#PresentationDirectives(Directives, PresentationService)
	 * @see		PresentationTopicMap#init
	 */
	static Vector createPresentationTopics(Enumeration topics, PresentationService ps) {
		Vector t = new Vector();	// result vector
		//
		while (topics.hasMoreElements()) {
			PresentableTopic topic = (PresentableTopic) topics.nextElement();
			// ### should never happen
			if (!topic.getClass().getName().equals("de.deepamehta.PresentableTopic")) {
				throw new DeepaMehtaException("unexpected class: \"" + topic.getClass().getName() + "\"");
			}
			//
			t.addElement(new PresentationTopic(topic, ps));
		}
		//
		return t;
	}

	/**
	 * Transforms PresentableAssociations into PresentationAssociations.
	 * <P>
	 * Part of reading a DIRECTIVE_SHOW_ASSOCIATIONS as well as
	 * DIRECTIVE_SHOW_WORKSPACE and DIRECTIVE_SHOW_VIEW.
	 * <P>
	 * References checked: 1.4.2003 (2.0a18-pre8)
	 *
	 * @param	assocs	element type is {@link de.deepamehta.PresentableAssociation}
	 *
	 * @see		PresentationDirectives#PresentationDirectives(Directives, PresentationService)
	 * @see		PresentationTopicMap#init
	 */
	static Vector createPresentationAssociations(Enumeration assocs, PresentationService ps) {
		Vector t = new Vector();	// result vector
		//
		while (assocs.hasMoreElements()) {
			PresentableAssociation assoc = (PresentableAssociation) assocs.nextElement();
			// ### should never happen
			if (!assoc.getClass().getName().equals("de.deepamehta.PresentableAssociation")) {
				throw new DeepaMehtaException("unexpected class: \"" + assoc.getClass().getName() + "\"");
			}
			//
			t.addElement(new PresentationAssociation(assoc, ps));
		}
		//
		return t;
	}

	// ---

	static Vector createPresentationPropertyDefinitions(Enumeration propDefs) {
		Vector t = new Vector();	// result vector
		//
		PropertyDefinition propDef;
		while (propDefs.hasMoreElements()) {
			propDef = (PropertyDefinition) propDefs.nextElement();
			// ### should never happen
			if (!propDef.getClass().getName().equals(
				"de.deepamehta.PropertyDefinition")) {
				throw new DeepaMehtaException("unexpected class: \"" + propDef.getClass().getName() + "\"");
			}
			//
			t.addElement(new PresentationPropertyDefinition(propDef));
		}
		//
		return t;
	}



	// ---------------------------
	// --- Styled Text Editing ---
	// ---------------------------



	static Hashtable createActionTable(JTextComponent textComponent) {
    	Hashtable actions = new Hashtable();
    	Action[] actionsArray = textComponent.getActions();
		int count = actionsArray.length;
		// ### System.out.println(">>> action table (" + count + " actions):");
		Action a;
		String name;
    	for (int i = 0; i < count; i++) {
        	a = actionsArray[i];
			name = (String) a.getValue(Action.NAME);
        	actions.put(name, a);
			// ### System.out.println("  > \"" + name + "\"");
    	}
		return actions;
	}

	static Action getActionByName(String name, Hashtable actions) {
		Action action = (Action) actions.get(name);
		if (action == null) {
			throw new DeepaMehtaException("action with name \"" + name + "\" not found in " + actions);
		}
		return action;
	}
}
