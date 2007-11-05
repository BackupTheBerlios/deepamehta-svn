package de.deepamehta.kompetenzstern;

import de.deepamehta.DeepaMehtaConstants;

import java.awt.Color;
import java.awt.Point;



/**
 * <P>
 * <HR>
 * Last functional change: 10.6.2003 (2.0b1)<BR>
 * Last documentation update: 14.1.2003 (2.0a17-pre6)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public interface KS extends DeepaMehtaConstants {



	// *****************
	// *** Constants ***
	// *****************



	// ------------------
	// --- Workspaces ---
	// ------------------



	static final String WORKSPACE_KOMPETENZSTERN = "t-ks-workspace";
	static final String WORKSPACE_TEMPLATE_BUILDER = "t-ks-templateworkspace";



	// ---------------------
	// --- Configuration ---
	// ---------------------



	static final String TEMPLATE_STANDARD = "t-businesscheck";
	static final String SKALA_5 = "t-skala1";
	static final String SKALA_10 = "t-skala2";

	static final int FOOTER_HEIGHT = 60;
	static final int BACKGROUND_WIDTH = 520;
	static final int BACKGROUND_HEIGHT = 300;
	static final int X_CENTER = 320;
	static final int Y_CENTER = 150;
	static final int DIST_CENTER = 50;
	static final int DIST_CIRCLES = 12;
	static final int DIST_LAYER = 10;
	//
	static final Color COLOR_LAYER = new Color(224, 224, 224);
	static final Point OFFSET_TASK = new Point(30, 30);
	static final Point OFFSET_PROFILE = new Point(50, -50);



	// -------------------
	// --- Topic Types ---
	// -------------------



	static final String TOPICTYPE_KOMPETENZSTERN = "tt-kompetenzstern";
	static final String TOPICTYPE_KOMPETENZSTERN_TEMPLATE = "tt-kompetenzsterntemplate";
	static final String TOPICTYPE_BEWERTUNGS_EBENE = "tt-bewertungsebene";
	static final String TOPICTYPE_BEWERTUNGS_KRITERIUM = "tt-kriterium";
	static final String TOPICTYPE_BEWERTUNGS_KRITERIUM1 = "tt-kriterium1";	// subtype for 1/10 skala
	static final String TOPICTYPE_BEWERTUNGS_KRITERIUM2 = "tt-kriterium2";	// subtype for --/++ skala
	static final String TOPICTYPE_RELATED_DOCUMENT = "tt-relateddocument";
	static final String TOPICTYPE_EXPORTED_DOCUMENT = "tt-exporteddocument";

	static final String TOPICTYPE_BEWERTUNGS_GEGENSTAND = "tt-bewertungsgegenstand";
	static final String TOPICTYPE_BEWERTUNGS_SKALA = "tt-bewertungsskala";
	static final String TOPICTYPE_BEWERTUNG = "tt-bewertung";



	// -------------------------
	// --- Association Types ---
	// -------------------------



	static final String ASSOCTYPE_KOMPETENZSTERN = "at-kompetenzstern";



	// ----------------------------
	// --- Association Semantic ---
	// ----------------------------



	// accessed by KriteriumTopic, KompetenzsternExporter
    static final String SEMANTIC_STERN_COMPOSITION = ASSOCTYPE_COMPOSITION;
    // "Template" -> subtype of "Kompetenzstern"
    static final String SEMANTIC_STERN_TYPE = ASSOCTYPE_ASSOCIATION;
    static final String SEMANTIC_RELATED_DOCUMENT = ASSOCTYPE_ASSOCIATION;



	// ------------------
	// --- Properties ---
	// ------------------



	static final String PROPERTY_HELP = "Hilfe";
	static final String PROPERTY_WERT = "Wert";
	//
	static final String PROPERTY_FIRMA = "Firma";
	static final String PROPERTY_DATUM = "Datum";
	static final String PROPERTY_ERFASSER = "Erfasser";
	static final String PROPERTY_ZUSAMMENFASSUNG = "Zusammenfassung";
	// roles
	static final String PROPERTY_TEMPLATE_BUILDER = "Template Builder";
	// translated properties ### not real properties
	static final String PROPERTY_BESCHREIBUNG = "Beschreibung";
	static final String PROPERTY_DATEI = "Datei";



	// ----------------
	// --- Commands ---
	// ----------------



	static final String ITEM_PREFERENCES_KOMPETENZSTERN = "Einstellungen";
	static final String CMD_SET_TEMPLATE = "setTemplate";
	static final String CMD_SET_BEWERTUNGS_GEGENSTAND = "setBewertungsGegenstand";
	static final String CMD_SET_BEWERTUNGS_SKALA = "setBewertungsSkala";
	//
	static final String ITEM_SHOW_COMPETENCE_STARS = "Vergleiche mit";
	static final String ICON_SHOW_COMPETENCE_STARS = "compareKompetenzstern.gif";
	//
	static final String CMD_SHOW_COMPETENCE_STAR = "ShowCompetenceStar";
	static final String ICON_SHOW_COMPETENCE_STAR = "Kompetenzstern.gif";
	//
	static final String ITEM_SET_ORDER = "Reihenfolge festlegen";
	static final String ITEM_CHANGE_ORDER = "Reihenfolge ändern";
	static final String  CMD_SET_ORDER = "setOrder";
	static final String ICON_SET_ORDER = "setOrder.gif";
	//
    static final String ITEM_SHOW_DOCUMENTS = "Verknüpfte Dokumente anzeigen";
    static final String  CMD_SHOW_DOCUMENTS = "showDocuments";
    static final String ICON_SHOW_DOCUMENTS = "showDocuments.gif";
    //
    static final String ITEM_ASSIGN_DOCUMENT = "Dokument verknüpfen";
    static final String  CMD_ASSIGN_DOCUMENT = "assignDocument";
    static final String ICON_ASSIGN_DOCUMENT = "createDocument.gif";
	//
	static final String ITEM_ASSIGN_NEW_CRITERION = "Kriterium erzeugen";
	static final String  CMD_ASSIGN_NEW_CRITERION = "assignNewCriterion";
	static final String ICON_ASSIGN_NEW_CRITERION = "createKriterium.gif";
	//
	static final String ITEM_ASSIGN_NEW_BEWERTUNGSEBENE = "Bewertungsebene erzeugen";
	static final String  CMD_ASSIGN_NEW_BEWERTUNGSEBENE = "assignNewBewertungsebene";
	static final String ICON_ASSIGN_NEW_BEWERTUNGSEBENE = "createBewertungsebene.gif";
	//
	static final String ITEM_ASSIGN_COMPETENCE_STAR_TYPE = "Kompetenzstern-Typ festlegen";
	static final String  CMD_ASSIGN_COMPETENCE_STAR_TYPE = "setCompetenceStarType";
	// ### static final String ICON_ASSIGN_COMPETENCE_STAR_TYPE = "createBewertungsebene.gif";



	// ---------------
	// --- Actions ---
	// ---------------



	static final String ACTION_GO_HOME = "goHome";
	// Kompetenzstern
	static final String ACTION_EDIT_KS = "editKS";
	static final String ACTION_UPDATE_KS = "updateKS";
	static final String ACTION_CREATE_KS = "createKS";
	static final String ACTION_DELETE_KS = "deleteKS";
	static final String ACTION_SHOW_KS_INFO = "showKSInfo";
	static final String ACTION_SHOW_KS_FORM = "showKSForm";
	// Kriterium
	static final String ACTION_EDIT_CRITERIA = "editCriteria";
	static final String ACTION_UPDATE_CRITERIA = "updateCriteria";



	// -------------
	// --- Pages ---
	// -------------



    static final String PAGE_HOME = "Home";
    static final String PAGE_KS_FORM = "KSForm";
    static final String PAGE_CRITERIA_FORM = "CriteriaForm";
}
