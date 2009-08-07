package de.deepamehta.client;

import de.deepamehta.Directives;
import de.deepamehta.PresentableTopic;

import java.awt.Image;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;



/**
 * <p>
 * <hr>
 * Last change: 7.9.2008 (2.0b8)<br>
 * J&ouml;rg Richter<br>
 * jri@deepamehta.de
 */
public final class PresentationTopic extends PresentableTopic implements GraphNode {



	// **************
	// *** Fields ***
	// **************



	private Vector associations = new Vector();		// element type: PresentationAssociation

	/**
	 * If the topic has individual appearance set (appMode == APPEARANCE_CUSTOM_ICON) this
	 * field holds the image to display the topic. If no individual appearance is set
	 * (appMode == APPEARANCE_DEFAULT) this field remains <code>null</code>.
	 * <p>
	 * Accessed by {@link #getImage}.<br>
	 * Initialzed by {@link #setImage}.
	 */
	private Image image;

	/**
	 * Key: property name (String)<br>
	 * Value: scrollbar value (Integer)
	 */
	private Hashtable scrollbarValues = new Hashtable();
	private Hashtable caretPositions = new Hashtable();



	// ********************
	// *** Constructors ***
	// ********************



	/**
	 * References checked: 27.12.2001 (2.0a14-pre5)
	 *
	 * @see		PresentationDirectives#PresentationDirectives(Directives, PresentationService)
	 * @see		DeepaMehtaClientUtils#createPresentationTopics
	 */
	public PresentationTopic(PresentableTopic topic, PresentationService ps) {
		super(topic);
		setImage(ps);
	}

	/**
	 * References checked: 27.12.2001 (2.0a14-pre5)
	 *
	 * Stream constructor.
	 *
	 * @see		PresentationDirectives#PresentationDirectives(DataInputStream, PresentationService)
	 * @see		DeepaMehtaClientUtils#readPresentationTopics
	 */
	public PresentationTopic(DataInputStream in, PresentationService ps) throws IOException {
		super(in);
		setImage(ps);
	}



	// ***************
	// *** Methods ***
	// ***************



	/**
	 * @see		PresentationTopicMap#changeTopicIcon
	 */
	void setIcon(String iconfile, PresentationService ps) {
		super.setIcon(iconfile);	// model
		setImage(ps);				// presentation
	}



	// ******************************************************************
	// *** Implementation of Interface de.deepamehta.client.GraphNode ***
	// ******************************************************************



	// Note: getGeometry()        is already implemented in PresentableTopic
	// Note: getAppearanceMode()  is already implemented in PresentableTopic
	// Note: getAppearanceParam() is already implemented in PresentableTopic
	// Note: getLabel()           is already implemented in PresentableTopic
	// Note: setGeometry()        is already implemented in PresentableTopic

	/**
	 * References checked: 17.2.2005 (2.0b5)
	 *
	 * @see		GraphPanel#paintNode
	 */
	public Image getImage() {
		return image;
	}

	public int getImageSize() {
		return IMAGE_SIZE;
	}

	// ### not part of interface
	public Hashtable getScrollbarValues() {
		return scrollbarValues;
	}

	// ### not part of interface
	public Hashtable getCaretPositions() {
		return caretPositions;
	}

	public void addEdge(GraphEdge edge) {
		associations.addElement(edge);
		// ### System.out.println(">>> PresentationTopic.addEdge(): assoc \"" + edge.getID() + "\" is registered at topic " + getID() + " (now " + associations.size() + " assocs registered)");
	}

	public void removeEdge(GraphEdge edge) {
		if (!associations.removeElement(edge)) {
			System.out.println("*** PresentationTopic.removeEdge(): \"" + edge + "\" not found in vector");
		}
		// ### System.out.println(">>> PresentationTopic.removeEdge(): assoc \"" + edge.getID() + "\" unregistered from topic " + getID() + " (still " + associations.size() + " assocs registered)");
	}

	public Enumeration getEdges() {
		return associations.elements();
	}

	public GraphNode relatedNode(GraphEdge edge) {
		if (this == edge.getNode1()) {
			return edge.getNode2();
		} else if (this == edge.getNode2()) {
			return edge.getNode1();
		} else {
			System.out.println("*** PresentationTopic.relatedNode(): " + this + " is not part of edge \"" + edge + "\"");
			return null;
		}
	}



	// **********************
	// *** Private Method ***
	// **********************



	/**
	 * @see		#PresentationTopic(PresentableTopic topic, PresentationService ps)
	 * @see		#PresentationTopic(DataInputStream in, PresentationService ps)
	 * @see		#setIcon(String iconfile, PresentationService ps)
	 */
	private void setImage(PresentationService ps) {
		if (appMode == APPEARANCE_DEFAULT) {
			image = null;
		} else if (appMode == APPEARANCE_CUSTOM_ICON) {
			image = ps.getImage(FILESERVER_ICONS_PATH + appParam);
		}
	}
}
