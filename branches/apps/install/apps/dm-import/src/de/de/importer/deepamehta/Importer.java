package de.importer.deepamehta;

import de.deepamehta.DeepaMehtaConstants;



/**
 * Importer Topic 0.2<br>
 * Requires DeepaMehta 2.0b8. and Java 1.4
 * <p>
 * Last change: 03.06.2009<br>
 * This was build as a very basic CSV importer for DeepaMehta's Corporate Memory!
 *
 * Malte Rei&szlig;ig<br>
 * mre@deepamehta.de
 */
public interface Importer extends DeepaMehtaConstants {

	// Importer Topic Commands
	
	// --- mre's local installation / 
	// Note: to get your id's check your dms log while clicking on the resp. TopicTypes
    // static final String TOPICTYPE_IMPORT_WIZARD = "t-7106";
    static final String ASSOCTYPE_IMPORT_RESULT = "at-importresult";
    static final String TOPICTYPE_IMPORT_WIZARD = "tt-importwizard";

    static final String PROPERTY_IMPORT_STATUS = "Step";
    static final String PROPERTY_IMPORT_LOG = "Log";
    static final String PROPERTY_IMPORT_COLUMN_NAMES = "Property Names"; // update the patch
    static final String PROPERTY_IMPORT_SEPERATOR = "Seperated by";
    static final String PROPERTY_IMPORT_TEXT_DELIMITER = "Text Delimiter";
    static final String PROPERTY_IMPORT_TEMP_DATA = "Temp Data";
    static final String PROPERTY_IMPORT_FILE_ENCODING = "File Encoding";
    static final String PROPERTY_IMPORT_TARGETTYPE = "TypeId";
    static final String PROPERTY_IMPORT_FILE = "File to be used";


    // ----------------
	// --- Commands ---
	// ----------------


    // static final String ITEM_LOAD_DATA = "LoadData";

    static final String ITEM_LOAD_DATA = "Load Preview";
    static final String  CMD_LOAD_DATA = "loadDocument";

	static final String ITEM_IMPORT = "Import Data";
    static final String  CMD_IMPORT = "importData";

    static final String ITEM_ROLLBACK = "Rollback";
    static final String  CMD_ROLLBACK = "rollbackImport";

}
