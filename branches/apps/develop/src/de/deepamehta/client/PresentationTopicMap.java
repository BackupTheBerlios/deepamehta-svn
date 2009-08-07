package de.deepamehta.client;

import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.Directives;
import de.deepamehta.PresentableTopic;
import de.deepamehta.PresentableTopicMap;
import de.deepamehta.Topic;
import de.deepamehta.util.DeepaMehtaUtils;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Logger;



/**
 * Topicmap model as instantiated by the graphical DeepaMehta frontend.
 * <p>
 * A <code>PresentationTopicMap</code> is cpomosed by
 * {@link PresentationTopic}'s, {@link PresentationAssociation}'s and {@link PresentationType}'s.
 * <p>
 * <hr>
 * Last change: 17.9.2008 (2.0b8)<br>
 * J&ouml;rg Richter<br>
 * jri@deepamehta.de
 */
public class PresentationTopicMap extends PresentableTopicMap implements DeepaMehtaConstants {



	// **************
	// *** Fields ***
	// **************



	private static Logger logger = Logger.getLogger("de.deepamehta");

	/**
	 * ### The client who deploys this <code>PresentationTopicMap</code>
	 */
	PresentationService ps;		// accessed by TopicmapEditorModel.normalViewActivated()

	/**
	 * The ID of the topic (type <code>tt-topicmap</code>) representing this topicmap.
	 * <p>
	 * <b>Initialized by</b>
	 * <ul>
	 *     <li><code>PresentationTopicMap()</code></li>
	 * </ul>
	 * <b>Accessed by</b>
	 * <ul>
	 *     <li><code>getID()</code></li>
	 *     <li><code>hideTopic()</code></li>
	 *     <li><code>createNewTopic()</code></li>
	 *     <li><code>createNewAssociation()</code></li>
	 *     <li><code>createNewTopicType()</code></li>
	 *     <li><code>createNewAssociationType()</code></li>
	 *     <li><code>showRelatedTopics()</code></li>
	 *     <li><code>processTopicCommand()</code></li>
	 * </ul>
	 */
	private String topicmapID;

	/**
	 * The viewmode this <code>PresentationTopicMap</code> is deployed for
	 * ({@link #VIEWMODE_USE} resp. {@link #VIEWMODE_BUILD}).
	 * <p>
	 * Initialized by {@link #PresentationTopicMap constructor}.<br>
	 * Accessed by {@link #getViewmode}.
	 */
	private String viewMode;

	/**
	 * Initialized by {@link #PresentationTopicMap constructor}.
	 */
	private int editorContext;

	/**
	 * Initialized by {@link #setEditor}.
	 */
	private TopicmapEditorModel editor;

	// 3 presentation objects, initialized by init()

	private Image bgImage;
	private Color bgColor;
	private Point translation;

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
	 * Part of reading DIRECTIVE_SHOW_WORKSPACE and DIRECTIVE_SHOW_VIEW.
	 * <p>
	 * References checked: 7.4.2007 (2.0b8)
	 *
	 * @see		PresentationDirectives#PresentationDirectives(Directives, PresentationService)
	 */
	PresentationTopicMap(PresentableTopicMap topicmap, int editorContext,
							String topicmapID, String viewMode, PresentationService ps) {
		
		super(topicmap);
		//
		this.ps = ps;
		this.topicmapID = topicmapID;
		this.viewMode = viewMode;
		this.editorContext = editorContext;
		//
		init(ps);
	}

	/**
	 * Stream constructor.
	 * <p>
	 * Part of reading DIRECTIVE_SHOW_WORKSPACE and DIRECTIVE_SHOW_VIEW.
	 * <p>
	 * References checked: 7.4.2007 (2.0b8)
	 *
	 * @see		PresentationDirectives#PresentationDirectives(DataInputStream, PresentationService)
	 */
	PresentationTopicMap(DataInputStream in, int editorContext, String topicmapID,
							String viewMode, PresentationService ps) throws IOException {
		super(in);
		//
		this.ps = ps;
		this.topicmapID = topicmapID;
		this.viewMode = viewMode;
		this.editorContext = editorContext;
		//
		init(ps);
	}



	// ***************
	// *** Methods ***
	// ***************



	// --- Overrides BaseTopicMap ---

	public String toString() {
		return getEditor().getTopicmap().toString();
	}

	/**
	 * @see		TopicmapEditorModel#hideAssociation
	 *
	 * @throws	DeepaMehtaException		if the specified association did not exist
	 *									in this topicmap
	 */
	public void deleteAssociation(String assocID) throws DeepaMehtaException {
		// Note: getAssociation() throws DeepaMehtaException
		((PresentationAssociation) getAssociation(assocID)).unregisterTopics();
		super.deleteAssociation(assocID);
	}

	// ### Note: addAssociation() is not overridden, the topics are registered in PresentationService.initAssociations()

	// ---

	String getID() {
		return topicmapID;
	}

	String getViewmode() {
		return viewMode;
	}

	TopicmapEditorModel getEditor() {
		return editor;
	}

	/**
	 * @see		PresentationService#creatingEdgesEnabled
	 */
	int getEditorContext() {
		return editorContext;
	}

	Image getBackgroundImage() {
		return bgImage;
	}

	Color getBackgroundColor() {
		return bgColor;
	}

	Point getTranslation() {
		return translation;
	}

	public Hashtable getScrollbarValues() {
		return scrollbarValues;
	}

	public Hashtable getCaretPositions() {
		return caretPositions;
	}

	// ---

	void setBackgroundImage(Image image) {
		bgImage = image;
		// ### bgImagefile (defined in superclass) is not updated, not a problem
	}

	void setBackgroundColor(Color color) {
		bgColor = color;
		// ### bgColorcode (defined in superclass) is not updated, not a problem
	}

	void setEditor(TopicmapEditorModel editor) {
		this.editor = editor;
	}

	// ---

	/**
	 * Initialites the geometry of the specified topic.
	 * <p>
	 * References checked: 29.1.2002 (2.0a14-pre7)
	 *
	 * @return	<code>true</code> if the geometry has been initialized,
	 *			<code>false</code> if the topic already have a geometry.
	 *
	 * @see		TopicmapEditorModel#showTopic
	 */
	boolean initGeometry(PresentationTopic topic) {
		// if the topics exists already in this topicmap its geometry is not changed
		if (topicExists(topic.getID())) {
			return false;
		}
		//
		int geomMode = topic.getGeometryMode();
		switch (geomMode) {
		case GEOM_MODE_ABSOLUTE:
			// no client side initialization required
			return false;
		case GEOM_MODE_RELATIVE:
			Point nearPoint = ((PresentationTopic) getTopic(topic.getNearTopicID())).getGeometry();
			Point d = topic.getGeometry();
			Point g = new Point(nearPoint.x + d.x, nearPoint.y + d.y);
			topic.setGeometry(g);
			return true;
		case GEOM_MODE_NEAR:
			nearPoint = ((PresentationTopic) getTopic(topic.getNearTopicID())).getGeometry();
			topic.setGeometry(nearPoint(nearPoint));
			return true;
		case GEOM_MODE_FREE:
			topic.setGeometry(freePoint());
			return true;
		default:
			throw new DeepaMehtaException("unexpected geometry mode: " + geomMode);
		}
	}

	// ---

	/**
	 * @see		PresentationService#changeTopicName(Topic topic, String name)
	 */
	void changeTopicLabel(String topicID, String label) {
		((PresentableTopic) getTopic(topicID)).setLabel(label);
	}

	/**
	 * @see		TopicmapEditorModel#changeTopicIcon
	 */
	void changeTopicIcon(String topicID, String iconfile) {
		((PresentationTopic) getTopic(topicID)).setIcon(iconfile, ps);
	}

	/**
	 * References checked: 29.1.2002 (2.0a14-pre7)
	 *
	 * @see		TopicmapEditorModel#setTopicGeometry
	 */
	void setTopicGeometry(String topicID, Point p) {
		((PresentationTopic) getTopic(topicID)).setGeometry(p);
	}

	/**
	 * References checked: 22.4.2004 (2.0b3-pre2)
	 *
	 * @see		TopicmapEditorModel#setTopicGeometry
	 */
	void setTopicLock(String topicID, boolean isLocked) {
		((PresentationTopic) getTopic(topicID)).setLocked(isLocked);
	}



	// ***********************
	// *** Private Methods ***
	// ***********************



	private void init(PresentationService ps) {
		// transforms presentable topics/assocs into presentation topics/assocss
		this.topics = DeepaMehtaUtils.fromTopicVector(
			DeepaMehtaClientUtils.createPresentationTopics(this.topics.elements(), ps));
		this.associations = DeepaMehtaUtils.fromAssociationVector(
			DeepaMehtaClientUtils.createPresentationAssociations(this.associations.elements(), ps));
		// background image
		if (!bgImagefile.equals("")) {
			this.bgImage = ps.getImage(FILESERVER_BACKGROUNDS_PATH + bgImagefile);
		}
		// background color
		String defaultColor = DEFAULT_VIEW_BGCOLOR;
		this.bgColor = DeepaMehtaUtils.parseHexColor(bgColorcode, defaultColor);
		// translation
		this.translation = DeepaMehtaUtils.parsePoint(translationCoord);	// translationCoord is defined in superclass
	}

	// ---

	/**
	 * ### to be dropped
	 *
	 * @see		#createNewAssociationType
	 */
	private String createAssociationTypeID(String typeName) {
		String typeID = "at-" + typeName.toLowerCase();
		if (typeID.length() > MAX_ID_LENGTH) {
			typeID = typeID.substring(0, MAX_ID_LENGTH);
		}
		return typeID;
	}



	// --------------------------
	// --- Geometry Utilities ---
	// --------------------------



	private Point randomPoint() {
		return new Point(300 + (int) (300 * Math.random()), (int) (100 * Math.random()));
	}

	private Point freePoint() {
		int distMax = 2 * FREE_MAX;	// find a free position inside this square
		int x = 0, y = 0;
		boolean free = false;
		while (!free) {
			// make distMax/4 tries to find a near position that is free
			for (int i = 0; i < distMax / 4; i++) {
				x = (int) (distMax * Math.random()) + FREE_MIN;
				y = (int) (distMax * Math.random()) + FREE_MIN;
				if (positionIsFree(x, y)) {
					free = true;
					break;
				}
			}
			// increase the radius while no free position is found
			if (!free) {
				logger.fine("no free position found in " + distMax / 4 +
					" tries -- increase square to " + (distMax + FREE_MAX));
				distMax += FREE_MAX;				
			}
		}
		return new Point(x, y);
	}

	/**
	 * Finds a free position that is near to the specified position.
	 *
	 * @see		initGeometry	package private
	 */
	private Point nearPoint(Point p) {
		int distMax = 2 * NEAR_MAX;	// the radius inside a position is regarded as near
		int dx = 0, dy = 0;
		boolean free = false;
		// increase the radius while no free position is found
		while (!free) {
			// make distMax/4 tries to find a near position that is free
			for (int i = 0; i < distMax / 4; i++) {
				dx = (int) (distMax * Math.random()) - distMax / 2;
				dy = (int) (distMax * Math.random()) - distMax / 2;
				if (positionIsFree(p.x + dx, p.y + dy)) {
					free = true;
					break;
				}
			}
			if (!free) {
				logger.fine("no free position found in " + distMax / 4 +
					" tries -- increase radius to " + (distMax + NEAR_MAX));
				distMax += NEAR_MAX;				
			}
		}
		return new Point(p.x + dx, p.y + dy);
	}

	/**
	 * Checks weather the specified position is free.
	 *
	 * @see		nearPoint	(above)
	 */
	private boolean positionIsFree(int x, int y) {
		Enumeration e = getTopics().elements();  // getTopics() is from BaseTopicMap
		while (e.hasMoreElements()) {
			PresentationTopic topic = (PresentationTopic) e.nextElement();
			if (isTooNear(topic.getGeometry(), x, y)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks weather the 2 specified positions are to near and thus would cause a
	 * visiual collision.
	 *
	 * @see		positionIsFree	(above)
	 */
	private boolean isTooNear(Point p, int x, int y) {
		return Math.abs(p.x - x) < NEAR_MIN && Math.abs(p.y - y) < NEAR_MIN;
	}
}
