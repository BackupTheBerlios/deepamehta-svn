package de.deepamehta;

import de.deepamehta.client.PresentationTopicMap;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Hashtable;



/**
 * Extends the basis model of a topic by the data needed to present a topic graphically.
 * <P>
 * A <CODE>PresentableTopic</CODE> can be serialized and send through an output stream.
 * A <CODE>PresentableTopic</CODE> is created at server side and send to the client
 * who builds a {@link de.deepamehta.client.PresentationTopic} upon it.
 * <P>
 * The "presentable" data comprises
 * <UL>
 * <LI>Geometry
 *		<UL>
 *		<LI><CODE>GEOM_MODE_ABSOLUTE</CODE> The geometry is known in absolute coordinates
 *		<LI><CODE>GEOM_MODE_NEAR</CODE> The geometry is not known yet -- the client is supposed
 *			to place the topic in the near of another known topic
 *		<LI><CODE>GEOM_MODE_FREE</CODE> The geometry is not known yet -- the client is supposed
 *			to place the topic at any free position
 *		</UL>
 * <LI>Appearance (icon)
 *		<UL>
 *		<LI><CODE>APPEARANCE_DEFAULT</CODE> There is no individual icon to present this topic --
 *			the appearance is determined by the respective topic type
 *		<LI><CODE>APPEARANCE_CUSTOM_ICON</CODE> There is an individual icon to present this topic --
 *			this feature is called "individual appearnace"
 *		</UL>
 * <LI>Additional Label (presented above the icon)
 * </UL>
 * <P>
 * <HR>
 * Last change: 21.9.2008 (2.0b8)<BR>
 * J&ouml;rg Richter<BR>
 * jri@deepamehta.de
 */
public class PresentableTopic extends BaseTopic implements DeepaMehtaConstants {



	// **************
	// *** Fields ***
	// **************



	// ----------------
	// --- Geometry ---
	// ----------------



	/**
	 * One of the 4 {@link #GEOM_MODE_ABSOLUTE geometry modes}
	 */
	private int geomMode;

	private Point geometry;			// used for GEOM_MODE_ABSOLUTE and GEOM_MODE_RELATIVE, ### accessed by PresentationTopic
	private String nearTopicID;		// used for GEOM_MODE_NEAR and GEOM_MODE_RELATIVE

	private boolean isLocked;		// if set, the topic can't be moved



	// ------------------
	// --- Appearance ---
	// ------------------



	/**
	 * Individual appearance mode: {@link #APPEARANCE_DEFAULT} or {@link #APPEARANCE_CUSTOM_ICON}.
	 * <P>
	 * Initialized by constructors of {@link #PresentableTopic this class} and (indirect) subclass
	 * {@link de.deepamehta.client.PresentationType#PresentationType} and by {@link #setIcon}.
	 */
	protected int appMode;

	/**
	 * Individual appearance parameter: in case of {@link #APPEARANCE_CUSTOM_ICON} this
	 * field contains the filename of the individual icon, in case of
	 * {@link #APPEARANCE_DEFAULT} this field remains uninitialized (and is not
	 * serialized).
	 * <P>
	 * Initialized by constructors of {@link #PresentableTopic this class} and (indirect)
	 * subclass
	 * {@link de.deepamehta.client.PresentationType#PresentationType} and
	 * by {@link #setIcon}.
	 */
	protected String appParam;



	// -------------
	// --- Label ---
	// -------------



	private String topicLabel;  	// the label shown above this topic



	// -----------------
	// --- Transient ---
	// -----------------



	private Hashtable properties;
	private boolean evoke;
	private String originalID;	// if set: indicates the topic originates from a datasource



	// ********************
	// *** Constructors ***
	// ********************



	public PresentableTopic(String id, int version, String type, int typeVersion, String name) {
		super(id, version, type, typeVersion, name);
		this.geomMode = GEOM_MODE_FREE;
		this.appMode = APPEARANCE_DEFAULT;
		this.topicLabel = "";
	}

	/**
	 * see		de.deepamehta.service.ApplicationService#createPresentableTopic(BaseTopic topic, String appTopicID)
	 */
	public PresentableTopic(BaseTopic topic) {
		super(topic);
		this.geomMode = GEOM_MODE_FREE;
		this.appMode = APPEARANCE_DEFAULT;
		this.topicLabel = "";
	}

	public PresentableTopic(BaseTopic topic, Hashtable properties) {
		this(topic);
		this.properties = properties;
	}

	public PresentableTopic(BaseTopic topic, Point geometry) {
		super(topic);
		this.geomMode = GEOM_MODE_ABSOLUTE;
		this.geometry = geometry;
		this.appMode = APPEARANCE_DEFAULT;
		this.topicLabel = "";
	}

	/**
	 * @see		de.deepamehta.topics.TopicContainerTopic#createPresentableTopic(BaseTopic topic, String nearTopicID)
	 */
	public PresentableTopic(BaseTopic topic, String nearTopicID) {
		super(topic);
		this.geomMode = GEOM_MODE_NEAR;
		this.nearTopicID = nearTopicID;
		this.appMode = APPEARANCE_DEFAULT;
		this.topicLabel = "";
	}

	/**
	 * Copy constructor.
	 */
	public PresentableTopic(PresentableTopic topic) {
		super(topic);
		//
		this.geomMode = topic.geomMode;
		this.geometry = topic.geometry;
		this.nearTopicID = topic.nearTopicID;
		this.isLocked = topic.isLocked;
		//
		this.appMode = topic.appMode;
		this.appParam = topic.appParam;
		//
		this.topicLabel = topic.topicLabel;
	}

	/**
	 * @see		de.deepamehta.service.ApplicationService#createTopic
	 * @see		de.deepamehta.service.ApplicationService#createNewContainer
	 * @see		de.deepamehta.service.ApplicationService#getAllTopics
	 * @see		de.deepamehta.service.RelationalCorporateMemory#createPresentableTopic
	 * @see		de.deepamehta.topics.ElementContainerTopic#createNewContainer
	 * @see		de.deepamehta.topics.ElementContainerTopic#createTopicFromElement
	 */
	public PresentableTopic(String id, int version, String type, int typeVersion, String name,
																		Point geometry) {
		this(id, version, type, typeVersion, name, geometry, "");		// topicLabel=""
	}

	public PresentableTopic(String id, int version, String type, int typeVersion, String name,
																		String nearTopicID) {
		this(id, version, type, typeVersion, name, nearTopicID, "");	// topicLabel=""
	}

	public PresentableTopic(String id, int version, String type, int typeVersion, String name,
																		String nearTopicID, Point offset) {
		super(id, version, type, typeVersion, name);
		this.geomMode = GEOM_MODE_RELATIVE;
		this.nearTopicID = nearTopicID;
		this.geometry = offset;
		this.appMode = APPEARANCE_DEFAULT;
		this.topicLabel = "";
	}

	/**
	 * @see		de.deepamehta.service.ApplicationService#createNewContainer
	 */
	public PresentableTopic(String id, int version, String type, int typeVersion, String name,
																		Point geometry, String topicLabel) {
		super(id, version, type, typeVersion, name);
		this.geomMode = GEOM_MODE_ABSOLUTE;
		this.geometry = geometry;
		this.appMode = APPEARANCE_DEFAULT;
		this.topicLabel = topicLabel;
	}

	/**
	 * @see		de.deepamehta.service.ApplicationService#getRelatedTopics
	 * @see		de.deepamehta.topics.ContainerTopic#createPresentableTopic
	 */
	public PresentableTopic(String id, int version, String type, int typeVersion, String name,
																		String nearTopicID, String topicLabel) {
		super(id, version, type, typeVersion, name);
		this.geomMode = GEOM_MODE_NEAR;
		this.nearTopicID = nearTopicID;
		this.appMode = APPEARANCE_DEFAULT;
		this.topicLabel = topicLabel;
	}

	/**
	 * Stream constructor.
	 */
	public PresentableTopic(DataInputStream in) throws IOException {
		super(in);
		// --- read geometry ---
		geomMode = in.read();
		switch (geomMode) {
		case GEOM_MODE_FREE:
			// no parameters
			break;
		case GEOM_MODE_ABSOLUTE:
		case GEOM_MODE_RELATIVE:
			int x = in.readInt();
			int y = in.readInt();
			geometry = new Point(x, y);
			if (geomMode == GEOM_MODE_ABSOLUTE) {
				break;
			}
		case GEOM_MODE_NEAR:
			nearTopicID = in.readUTF();
			break;
		default:
			throw new DeepaMehtaException("unexpected geometry mode: " + geomMode);
		}
		isLocked = in.readBoolean();
		// --- read appearance ---
		appMode = in.read();
		switch (appMode) {
		case APPEARANCE_DEFAULT:
			// no parameter
			break;
		case APPEARANCE_CUSTOM_ICON:
			appParam = in.readUTF();
			break;
		default:
			throw new DeepaMehtaException("unexpected appearance mode: " + appMode);
		}
		// --- read topic label ---
		topicLabel = in.readUTF();
	}



	// ***************
	// *** Methods ***
	// ***************



	public int getGeometryMode() {
		return geomMode;
	}

	public Point getGeometry() {
		return geometry;
	}

	public void setGeometry(Point p) {
		this.geomMode = GEOM_MODE_ABSOLUTE;
		this.geometry = p;
	}

	public boolean isLocked() {
		return isLocked;
	}

	/**
	 * @see		de.deepamehta.service.ApplicationService#initTopicLock
	 * @see		RelationalCorporateMemory#queryPresentableTopics(String query)
	 * @see		PresentationTopicMap#setTopicLock
	 */
	public void setLocked(boolean isLocked) {
		// ### error check
		if (this.isLocked == isLocked) {
			throw new DeepaMehtaException("lock of " + this + " is already set to \"" + isLocked + "\"");
		}
		//
		this.isLocked = isLocked;
	}

	// ---

	public String getNearTopicID() {
		return nearTopicID;
	}

	public void setNearTopicID(String topicID) {
		this.geomMode = GEOM_MODE_NEAR;
		this.nearTopicID = topicID;
	}

	// ---

	public String getLabel() {
		return topicLabel;
	}

	public void setLabel(String label) {
		topicLabel = label;
	}

	// ---

	/**
	 * @see		de.deepamehta.service.CorporateDirectives#createLiveTopic
	 */
	public Hashtable getProperties() {
		return properties;
	}

	/**
	 * @see		de.deepamehta.service.ApplicationService#createNewContainer
	 * @see		de.deepamehta.topics.ElementContainerTopic#createTopicFromElement
	 */
	public void setProperties(Hashtable properties) {
		this.properties = properties;
	}

	// ---

	public boolean getEvoke() {
		return evoke;
	}

	public void setEvoke(boolean evoke) {
		this.evoke = evoke;
	}

	// ---

	/**
	 * References checked: 7.8.2008 (2.0b8)
	 *
	 * @see		de.deepamehta.topics.ElementContainerTopic#createTopicFromElement
	 */
	public void setOriginalID(String originalID) {
		this.originalID = originalID;
	}

	public String getOriginalID() {
		return originalID;
	}

	/**
	 * References checked: 7.8.2008 (2.0b8)
	 *
	 * @see		de.deepamehta.service.CorporateDirectives#showTopic
	 * @see		de.deepamehta.service.ApplicationService#buildResultView
	 */
	public boolean fromDatasource() {
		return getOriginalID() != null;
	}

	// ---

	/**
	 * @see		de.deepamehta.client.DeepaMehtaClient#editorIcon
	 */
	public int getAppearanceMode() {
		return appMode;
	}

	/**
	 * @see		de.deepamehta.client.DeepaMehtaClient#editorIcon
	 * @see		de.deepamehta.client.PresentationType#setIcon
	 */
	public String getAppearanceParam() {
		return appParam;
	}

	// ---

	/**
	 * Sets appearance for this <CODE>PresentableTopic</CODE>.
	 *
	 * @param	iconfile	if empty appearance is set to APPEARANCE_DEFAULT otherwise
	 *						appearance is set to APPEARANCE_CUSTOM_ICON
	 *
	 * @see		de.deepamehta.service.ApplicationService#createPresentableTopic
	 * @see		de.deepamehta.service.ApplicationService#initTopicAppearance
	 */
	public void setIcon(String iconfile) {
		if (iconfile.equals("")) {
			this.appMode = APPEARANCE_DEFAULT;
		} else {
			this.appMode = APPEARANCE_CUSTOM_ICON;
			this.appParam = iconfile;
		}
	}



	// ---------------------
	// --- Serialization ---
	// ---------------------



	/**
	 * Serializes this <CODE>PresentableTopic</CODE> and writes it to the specified
	 * output stream.
	 */
	public void write(DataOutputStream out) throws IOException {
		super.write(out);
		// --- geometry ---
		out.write(geomMode);
		switch (geomMode) {
		case GEOM_MODE_FREE:
			// no parameter
			break;
		case GEOM_MODE_ABSOLUTE:
		case GEOM_MODE_RELATIVE:
			out.writeInt(geometry.x);
			out.writeInt(geometry.y);
			if (geomMode == GEOM_MODE_ABSOLUTE) {
				break;
			}
		case GEOM_MODE_NEAR:
			out.writeUTF(nearTopicID);
			break;
		default:
			throw new DeepaMehtaException("unexpected geometry mode: " + geomMode);
		}
		out.writeBoolean(isLocked);
		// --- appearance ---
		out.write(appMode);
		switch (appMode) {
		case APPEARANCE_DEFAULT:
			// no parameter
			break;
		case APPEARANCE_CUSTOM_ICON:
			if (appParam == null) {
				throw new DeepaMehtaException("topic " + this + " has " +
					"APPEARANCE_CUSTOM_ICON but the appearance parameter is not set");
			}
			out.writeUTF(appParam);
			break;
		default:
			throw new DeepaMehtaException("unexpected appearance mode: " + appMode);
		}
		// --- label ---
		out.writeUTF(topicLabel);
	}	
}
