package de.deepamehta.kompetenzstern.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.kompetenzstern.KS;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateCommands;
import de.deepamehta.service.CorporateDetail;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.DeepaMehtaServiceUtils;
import de.deepamehta.service.Session;
import de.deepamehta.topics.LiveTopic;
import de.deepamehta.topics.TypeTopic;

import java.awt.Point;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;



/**
 * Part of {@link KompetenzsternTopic Kompetenzstern} application.
 * <P>
 * <HR>
 * Last functional change: 3.2.2008 (2.0b8)<BR>
 * Last documentation update: 25.8.2001 (2.0a12-pre1)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class KriteriumTopic extends LiveTopic implements KS {



	// **************
	// *** Fields ***
	// **************



    private static final String ICON_CRITERION = "Kriterium.gif";

	private static final String ITEM_ASSIGN_NEW_CRITERION = "Unterkriterium erzeugen";
	private static final String  CMD_ASSIGN_NEW_CRITERION = "assignNewCriterion";
	private static final String ICON_ASSIGN_NEW_CRITERION = "createUnterkriterium.gif";
    // ---
    private static final String ITEM_NAVIGATE_BY_CRITERION = "Unterkriterien anzeigen";
    private static final String  CMD_NAVIGATE_BY_CRITERION = "RevealAllSubCriteria";
    private static final String ICON_NAVIGATE_BY_CRITERION = "showUnterkriterien.gif";
    // --- "Kriterium ausblenden"
    private static final String ITEM_HIDE_ALL_SUB_CRITERIA = "Kriterium ausblenden";
    private static final String  CMD_HIDE_ALL_SUB_CRITERIA = "HideAllSubCriteria";
    private static final String ICON_HIDE_ALL_SUB_CRITERIA = "hideUnterkriterien.gif";
    // ---
    private static final String ITEM_DELETE_ALL_SUB_CRITERIA = "Kriterium löchen";
    private static final String  CMD_DELETE_ALL_SUB_CRITERIA = "DeleteAllSubCriteria";
    private static final String ICON_DELETE_ALL_SUB_CRITERIA = ICON_DELETE_TOPIC;
	// ---
    private static final String ITEM_SUB_CRITERIA_SUFFIX = " (inkl. Unterkriterien)";



	// *******************
	// *** Constructor ***
	// *******************



	public KriteriumTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}



	// **********************
	// *** Defining Hooks ***
	// **********************



	// --------------------------
	// --- Providing Commands ---
	// --------------------------



	/**
	 * @see		de.deepamehta.service.ApplicationService#showTopicMenu
	 */
	public CorporateCommands contextCommands(String topicmapID, String viewmode,
												Session session, CorporateDirectives directives) {
        CorporateCommands commands = new CorporateCommands(as);
		//
		String viewTypeID = as.getLiveTopic(topicmapID, 1).getType();
		boolean insideStern = viewTypeID.equals(TOPICTYPE_KOMPETENZSTERN);
		boolean insideTemplate = viewTypeID.equals(TOPICTYPE_KOMPETENZSTERN_TEMPLATE);
		//
		if (!insideStern && !insideTemplate) {
			return super.contextCommands(topicmapID, viewmode, session, directives);
		}
		//
		boolean hasSubCriteria = !getSubCriteria().isEmpty();
		boolean hasRelatedDocuments = !getRelatedDocuments().isEmpty();
		boolean addHideDelete = isSubCriteria();
		//
		if (!insideTemplate) {
        	// --- Unterkriterien anzeigen ---
			if (hasSubCriteria) {
    	    	commands.addCommand(ITEM_NAVIGATE_BY_CRITERION, CMD_NAVIGATE_BY_CRITERION,
					FILESERVER_IMAGES_PATH, ICON_NAVIGATE_BY_CRITERION);
				// ### addNavigateBySubCriterion(commands);
			}
        	// --- Ausblenden (mit allen Unterkriterien) ---
			if (addHideDelete) {
	        	commands.addCommand(ITEM_HIDE_ALL_SUB_CRITERIA + (hasSubCriteria ?
					ITEM_SUB_CRITERIA_SUFFIX : ""), CMD_HIDE_ALL_SUB_CRITERIA,
					FILESERVER_IMAGES_PATH, ICON_HIDE_ALL_SUB_CRITERIA);
			}
		}
        // --- Unterkriterium erzeugen ---
        commands.addCommand(ITEM_ASSIGN_NEW_CRITERION, CMD_ASSIGN_NEW_CRITERION,
            FILESERVER_IMAGES_PATH, ICON_ASSIGN_NEW_CRITERION);
		// --- Dokuments ---
		if (!insideTemplate) {
	        commands.addSeparator();
			KompetenzsternTopic.addDocumentCommands(hasRelatedDocuments, commands);
		}
        // --- Löschen (mit allen Unterkriterien) ---
		// Note: deleting only enabled for subcriteria
		if (addHideDelete) {
			commands.addSeparator();
			commands.addCommand(ITEM_DELETE_ALL_SUB_CRITERIA + (hasSubCriteria ?
				ITEM_SUB_CRITERIA_SUFFIX : ""), CMD_DELETE_ALL_SUB_CRITERIA,
				FILESERVER_IMAGES_PATH, ICON_DELETE_ALL_SUB_CRITERIA);
		}
		//
        return commands;
	}



	// --------------------------
	// --- Executing Commands ---
	// --------------------------



	public CorporateDirectives executeCommand(String command,
                    Session session, String topicmapID, String viewmode) {
		// ### compare to BewertungsebeneTopic
		CorporateDirectives directives = new CorporateDirectives();
        //
		StringTokenizer st = new StringTokenizer(command, COMMAND_SEPARATOR);
        String cmd = st.nextToken();
		if (cmd.equals(CMD_NAVIGATE_BY_CRITERION)) {
			return navigateByTopictype(getSubkriteriumTypeID(), 2, SEMANTIC_STERN_COMPOSITION);
		} else if (cmd.equals(CMD_ASSIGN_NEW_CRITERION)) {
			// ### in template: TOPICTYPE_BEWERTUNGS_KRITERIUM
			createChildTopic(getSubkriteriumTypeID(), SEMANTIC_STERN_COMPOSITION, session, directives);
        } else if (cmd.equals(CMD_HIDE_ALL_SUB_CRITERIA)) {
            hideRecursively(topicmapID, viewmode, session, false, directives /* ###, false */);
        } else if (cmd.equals(CMD_DELETE_ALL_SUB_CRITERIA)) {
            hideRecursively(topicmapID, viewmode, session, true, directives /* ###, true */);
		} else if (KompetenzsternTopic.executeOrderCommand(this, command, directives, as)) {
			// do nothing
		} else if (KompetenzsternTopic.executeDocumentCommand(this, command, session, directives)) {
			// do nothing
		} else {
			return super.executeCommand(command, session, topicmapID, viewmode);
		}
		return directives;
	}



	// ---------------------------
	// --- Handling Properties ---
	// ---------------------------



	public CorporateDirectives propertiesChanged(Hashtable newData, Hashtable oldData,
											String topicmapID, String viewmode, Session session) {
		// ### compare to UserTopic.propertiesChanged() and
		// WorkspaceTopic.propertiesChanged() and
		// TopicMapTopic.propertiesChanged() and
		CorporateDirectives directives = super.propertiesChanged(newData, oldData, topicmapID, viewmode, session);
		//
		// --- "Wert" ---
		String value = (String) newData.get(PROPERTY_WERT);
		if (value != null) {
			try {
				if (!isSubCriteria()) {
					String ordNr = getProperty(PROPERTY_ORDINAL_NUMBER);
					// get Bewertung topic
					Hashtable props = new Hashtable();
					props.put(PROPERTY_ORDINAL_NUMBER, ordNr);
					BaseTopic topic = as.getTopic(TOPICTYPE_BEWERTUNG, props, topicmapID, directives);	// throws DME
					// determine new geometry
					KompetenzsternTopic stern = (KompetenzsternTopic) as.getLiveTopic(topicmapID, 1);
					int index = DeepaMehtaServiceUtils.findIndexByName(stern.werte, value);
					Point p = stern.circlePosition(Integer.parseInt(ordNr), stern.kriteriumCount, index);
					// set new geometry of Bewertung topic
					directives.add(DIRECTIVE_SET_TOPIC_GEOMETRY, topic.getID(), p, topicmapID);
				}
			} catch (DeepaMehtaException ex) {
				System.out.println("*** KriteriumTopic.propertiesChanged(): " + ex);
				ex.printStackTrace();	// ###
				directives.add(DIRECTIVE_SHOW_MESSAGE, "Der ge�nderte Wert kann nicht angezeigt werden (" +
					ex.getMessage() + ")", new Integer(NOTIFICATION_WARNING));
			}
		}
		//
		return directives;
	}

	public Vector disabledProperties(Session session) {
		Vector props = new Vector();
		if (getProperty(PROPERTY_LOCKED_GEOMETRY).equals(SWITCH_ON)) {
			props.addElement(PROPERTY_HELP);
		}
		return props;
	}

	public static Vector hiddenProperties(TypeTopic type) {
		Vector props = new Vector();
		props.addElement(PROPERTY_ICON);
		return props;
	}



	// -----------------------------
	// --- Handling Topic Detail ---
	// -----------------------------



	/**
	 * @see		de.deepamehta.service.ApplicationService#processTopicDetail
	 */
	public CorporateDirectives processDetailHook(CorporateDetail detail,
							Session session, String topicmapID, String viewmode) {
		String command = detail.getCommand();
		StringTokenizer st = new StringTokenizer(command, COMMAND_SEPARATOR);
		String cmd = st.nextToken();
		//
		if (cmd.equals(CMD_SET_ORDER)) {
			String assocID = st.nextToken();
			String order = (String) detail.getParam1();
			cm.setAssociationData(assocID, 1, PROPERTY_ORDINAL_NUMBER, order);
			return new CorporateDirectives();
		} else {
			return super.processDetailHook(detail, session, topicmapID, viewmode);
		}
	}



	// ***********************
	// *** Utility Methods ***
	// ***********************



    /**
     * @return	the subcriteria of this criterion as BaseTopics
     */
    public Vector getSubCriteria() {
        return as.getRelatedTopics(getID(), SEMANTIC_STERN_COMPOSITION, getSubkriteriumTypeID(), 2,
        	false, true);	// ordered=false, emptyAllowed=true
    }

	// ### see KompetenzsternTopic
	public String getSubkriteriumTypeID() {
		return getType();
		// ### return TOPICTYPE_BEWERTUNGS_KRITERIUM;
	}



	// ***********************
	// *** Private Methods ***
	// ***********************



    private Vector getRelatedDocuments() {
        return as.getRelatedTopics(getID(), SEMANTIC_RELATED_DOCUMENT, TOPICTYPE_RELATED_DOCUMENT, 2,
        	false, true);	// ordered=false, emptyAllowed=true
    }

	private boolean isSubCriteria() {
		// Note: subcriterias doesn't have an ordinal number, their rating doesn't affect the star rendering
		return getProperty(PROPERTY_ORDINAL_NUMBER).equals("");
	}

    /**
     * Hides sub-criteria (recursive).
	 *
     * @param	includeThis		if true, this topic will be hidden as well
     * @param	delete			if true, the topics will be deleted.
	 *
     * @see		#executeCommand
     */
	private void hideRecursively(String topicmapID, String viewmode, Session session, boolean delete,
                            	CorporateDirectives directives /* ###, boolean includeThis */) {
        Enumeration e = getSubCriteria().elements();
        while (e.hasMoreElements()) {
            BaseTopic topic = (BaseTopic) e.nextElement();
            try {
                KriteriumTopic krit = (KriteriumTopic) as.getLiveTopic(topic, session, directives);
                krit.hideRecursively(topicmapID, viewmode, session, delete, directives);
            } catch (DeepaMehtaException ex) {
                System.out.println(">>> KriteriumTopic.hideRecursively(): " + ex);
                // ### if the topic is not in as, it's not necessary to hide it
            }
        }
        // ### if (includeThis) {
        super.hide(topicmapID, viewmode, delete, directives);
        // ### }
    }
}
