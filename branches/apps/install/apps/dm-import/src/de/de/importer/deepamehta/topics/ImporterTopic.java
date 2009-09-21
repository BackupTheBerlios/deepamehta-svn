package de.importer.deepamehta.topics;


import de.deepamehta.AmbiguousSemanticException;
import de.deepamehta.BaseAssociation;
import de.importer.deepamehta.Importer;
import java.io.IOException;
import java.util.StringTokenizer;

import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.PresentableAssociation;
import de.deepamehta.PresentableTopic;
import de.deepamehta.PropertyDefinition;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateCommands;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.Session;
import de.deepamehta.topics.DocumentTopic;
import de.deepamehta.topics.TypeTopic;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 * <br>
 * Requires DeepaMehta 2.0b8
 *
 * The Import Wizard Custom Implementation
 * 
 * <p>
 * Last change: 03.06.2009<br>
 * 
 * Malte Rei&szlig;ig<br>
 * * mre@deepamehta.de
 */

public class ImporterTopic extends DocumentTopic implements Importer {



	static final String VERSION = " 0.9a";
	static {
		System.out.println(">>> Import Wizard" + VERSION);
	}

	// *******************
	// *** Constructor ***
	// *******************



	public ImporterTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}

    public CorporateDirectives evoke(Session session, String topicmapID, String viewmode) {
        setProperty(PROPERTY_DESCRIPTION, "<body>There are two ways to select the document which contains the data to be imported. " +
                "The first way is more easy and sufficient for most users.<p> " +
                "1) Select the \"Choose\" button and grab the file from your file system. </p>"+
                "<p>The other way described is just for merging input of <b>multiple files</b> which have the same <b>data structure</b>.</p>" +
                "<p>2) Create a <i>Document</i> topic (by right clicking on the background canvas) and use the \"Choose\" button to select a CSV file from your file system. " +
                "Then draw a connection <b>from</b> the <i>Import Wizard<i> icon to the <i>Document</i> icon. Rightclick the connector line and select > Retype >> <i>Assignment</i> and you're finished. <br>" +
                "<p><b>When done right click the \"Import Wizard\" topic again and select \"Load Preview\". </p>"+
                "<br> Note: When using the <i>advanced</i> mode the name of this topic will serve as the Name of the new <i>Topic Type</i>." +
                "(Dropping data into here is not yet implemented.) " +
                "</body>");
        return super.evoke(session, topicmapID, viewmode);
    }



	// --------------------------
	// --- Providing Commands ---
	// --------------------------


    public CorporateCommands contextCommands(String topicmapID, String viewmode, Session session, CorporateDirectives directives) {
        CorporateCommands commands = new CorporateCommands(as);
		//
		int editorContext = as.editorContext(topicmapID);
		commands.addNavigationCommands(this, editorContext, session);	// EDITOR_CONTEXT_VIEW only
		// additional states
		int currentState = new Integer(getProperty(PROPERTY_IMPORT_STATUS));
        int importLock = 1;
        int rollbackLock = 1;
        if (currentState != 1) {
            importLock = 0;
        }
        if (currentState == 3) {
            // disabled rollback
            rollbackLock = 1;
        }
        commands.addSeparator();
        commands.addCommand(ITEM_LOAD_DATA, CMD_LOAD_DATA, FILESERVER_ICONS_PATH, "getuptodate.gif", 0);
            // additional commands: ""
        commands.addCommand(ITEM_IMPORT, CMD_IMPORT, FILESERVER_ICONS_PATH, "message.gif", importLock);
        commands.addSeparator();
        commands.addCommand(ITEM_ROLLBACK, CMD_ROLLBACK, FILESERVER_ICONS_PATH, "vortrag.gif", rollbackLock);
            //
		commands.addStandardCommands(this, editorContext, viewmode, session, directives);
		//
		return commands;
	}


	
	// --------------------------
	// --- Executing Commands ---
	// --------------------------



	public CorporateDirectives executeCommand(String command, Session session, String topicmapID, String viewmode) {
		CorporateDirectives directives = new CorporateDirectives();
		//
		StringTokenizer st = new StringTokenizer(command, COMMAND_SEPARATOR);
        StringBuffer log = new StringBuffer();
		String cmd = st.nextToken();
		// Delegating custom commands
		if (cmd.equals(CMD_LOAD_DATA)) {
            // get string data now
            String data = getImportData(directives); // ensures linefeeds
            // get hashtable as elements
            Vector entries = getTopicProperties(data, directives);
            // preview and internal property defintions
            Hashtable props = new Hashtable();
            String propDef = generatePreview(entries, props);
            // Stop after here and if generatePreview handle the modes !!
            String typeId = "";
            // Handling advanced import mode
            if (as.getTopicProperty(this, PROPERTY_IMPORT_COLUMN_NAMES).equals("Advanced")) {
                BaseTopic targetType;
                try {
                    targetType = as.getRelatedTopic(getID(), ASSOCTYPE_ASSOCIATION, TOPICTYPE_TOPICTYPE, 2, true);
                } catch(AmbiguousSemanticException aex) {
                    System.out.println(aex.getMessage());
                    targetType = aex.getDefaultTopic();
                }
                if (targetType != null) {
                    // got ONE target topictype assigned and the import wizard is in advanced mode
                    System.out.println("INFO: There is already a TopicType assigned to my Import Wizard.");
                    System.out.println("INFO: Going for manual mapping configuration and not suggest a type("+targetType.getID()+").");
                    setProperty(PROPERTY_IMPORT_TARGETTYPE, targetType.getID());
                    typeId = targetType.getID();
                } else {
                    // got NO target topictype assigned and the import wizard is in advanced mode
                    typeId = createImportType(entries, directives, session);
                    setProperty(PROPERTY_IMPORT_TARGETTYPE, typeId);
                    System.out.println("INFO: Just created a TopicType for the Import Wizard ("+typeId+")");
                }
            } else if (as.getTopicProperty(this, PROPERTY_IMPORT_COLUMN_NAMES).equals("Autosuggest")) {
                    System.out.println(">> IMPORT WIZARD should take the first two columns and put them into a \"TOPIC\" (Name, Descr)");
            } else if (as.getTopicProperty(this, PROPERTY_IMPORT_COLUMN_NAMES).equals("First row contains property names")) {
                    System.out.println(">> IMPORT WIZARD should probably create a kind of a generic \"TOPIC\" (Name, Descr, plus column filed names)");
            }
            System.out.println("INFO: identified " + entries.size() + " topics with props: " + propDef);
            directives.add(DIRECTIVE_SHOW_TOPIC_PROPERTIES, getID(), props, new Integer(1));
            setProperty(PROPERTY_IMPORT_TEMP_DATA, data);
            setProperty(PROPERTY_IMPORT_LOG, propDef);
            setProperty(PROPERTY_IMPORT_TARGETTYPE, typeId);
            setProperty(PROPERTY_IMPORT_STATUS, "2");
        } else if (cmd.equals(CMD_IMPORT)) {
            String data = getProperty(PROPERTY_IMPORT_TEMP_DATA);
            String typeId = getProperty(PROPERTY_IMPORT_TARGETTYPE);
            StringBuffer report = new StringBuffer();
            // split up each row into elements
            Vector entries = getTopicProperties(data, directives);
            Vector topics = createTopics(typeId, entries, directives, session, report);
            if (topics.isEmpty()) {
                Hashtable props = new Hashtable(); // empty
                props.put(PROPERTY_DESCRIPTION, "<body><h2>Import Error</h2></body>");
                directives.add(DIRECTIVE_SHOW_TOPIC_PROPERTIES, getID(), props, new Integer(1));
            }
            Hashtable justReport = new Hashtable(); // empty
            justReport.put(PROPERTY_DESCRIPTION, generateReport(topics, typeId));
            directives.add(DIRECTIVE_SHOW_TOPIC_PROPERTIES, getID(), justReport, new Integer(1));
            setProperty(PROPERTY_IMPORT_STATUS, "3");
            setProperty(PROPERTY_IMPORT_LOG, report.toString());
        } else if (cmd.equals(CMD_ROLLBACK)){
            String logFile = getProperty(PROPERTY_IMPORT_LOG);
            String[] topics = logFile.split("\n");
            // ### clean up disabled
            // cleanUp(topics, directives);
            setProperty(PROPERTY_IMPORT_STATUS, "1");
        //} else if (cmd.equals(CMD_ASSIGN_FILE)) {
          //  copyAndUpload("", FILE_DOCUMENT, PROPERTY_FILE, session, directives);
        } else if (cmd.equals(CMD_FOLLOW_HYPERLINK)) {
			String topicId = st.nextToken();
            String urlPrefix = "http://";
			if (!topicId.startsWith(urlPrefix)) {
				System.out.println("*** ImporterTopic.executeCommand(): URL \"" + topicId + "\" not recognized by " +
					"CMD_FOLLOW_HYPERLINK");
				return directives;
			}
			String action = topicId.substring(urlPrefix.length());
			//if (topicId.startsWith(urlPrefix)) {
            System.out.println("**** hyperlink action is: " + action);
			//	BaseTopic topic = as.getLiveTopic(topicId.substring(topicId.lastIndexOf("/")+1), 1);
            //    PresentableTopic pt = as.createPresentableTopic(topic);
            //    directives.add(DIRECTIVE_SHOW_TOPIC, pt);
			//}
        } else {
			return super.executeCommand(command, session, topicmapID, viewmode);
		}
		return directives;
		//return directives;
	}

    // ----------------------
	// --- Topic Hooks ---
	// ----------------------

    @Override
    public boolean propertyChangeAllowed(String propName, String propValue, Session session, CorporateDirectives directives) {
        System.out.println("    Property Change Allowed Hook triggered : " + propName);
        if(propName.equals("Name")) {
            Vector existingTopic = cm.getTopicsByName(propValue);
            for (int a = 0; existingTopic.size() > a; a++) {
            }
        }
        return true;
    }

    public Vector disabledProperties(Session session) {
		Vector props = super.disabledProperties(session);
		// Note: if this property is already added by the superclass it is added
		// here again and thus are contained twice in the vector, but this is no problem
		props.addElement(PROPERTY_DESCRIPTION);
		return props;
	}
    
    public static void propertyLabel(PropertyDefinition propDef, ApplicationService as, Session session) {
		
        String propName = propDef.getPropertyName();
        if (propName.equals(PROPERTY_DESCRIPTION)) {
            propDef.setPropertyLabel("Import Preview and Reporting");
        }
	}

    public static Vector hiddenProperties(TypeTopic type) {
        // hide twitter timestamp within the property panel
        // this is provided as a label of a twitter topic
        Vector hiddenProps = new Vector();
        hiddenProps.add(PROPERTY_IMPORT_FILE_ENCODING);
        hiddenProps.add(PROPERTY_IMPORT_TEMP_DATA);
        hiddenProps.add(PROPERTY_IMPORT_TARGETTYPE);
        hiddenProps.add(PROPERTY_IMPORT_TEXT_DELIMITER);
        hiddenProps.add(PROPERTY_IMPORT_STATUS);
        hiddenProps.add(PROPERTY_IMPORT_LOG);
        // hiddenProps.add(PROPERTY_FILE);
        return hiddenProps;
    }
	
	// **********************
	// *** Custom Methods ***
	// **********************
	
	public boolean readFileData(CorporateDirectives directives) {
        return true;
	}

    public String getImportData(CorporateDirectives directives) {
        StringBuffer importData = new StringBuffer("");
        //
        File data;
        String fileName = getProperty(PROPERTY_FILE);
        // fileName is either set by Icon Property (which we misused here)
        // or if that is not set through a related Document topic
        if (!fileName.equals("")) {
            // Get a system property
            String dir = System.getProperty("user.home");
            String os = System.getProperty("os.name");
            if (os.startsWith("Window")) {
                dir = dir + "\\";
            } else {
                dir = dir + "/";
            }
            data = new File(dir+FILE_REPOSITORY_PATH + FILESERVER_DOCUMENTS_PATH + fileName);
        } else {
            BaseTopic importDocument = as.getRelatedTopic(getID(), ASSOCTYPE_ASSOCIATION, TOPICTYPE_DOCUMENT, 2, false);
            fileName = as.getTopicProperty(importDocument, PROPERTY_FILE);
        }
        //for (int i = 0; i < files.size(); i++) {
            //BaseTopic importDocument = (BaseTopic) files.get(i);

            // ###
            // Get a system property
            String dir = System.getProperty("user.home");
            String os = System.getProperty("os.name");
            if (os.startsWith("Window")) {
                dir = dir + "\\";
            } else {
                dir = dir + "/";
            }
            data = new File(dir+FILE_REPOSITORY_PATH + FILESERVER_DOCUMENTS_PATH + fileName);
            try {
                //#### Encoded Reader
                System.out.println("INFO: Encoding Reader is not yet implemented, dm reads now Unicode(UTF8)");
                FileReader reader = new FileReader(data);
                BufferedReader yReader = new BufferedReader(reader);
                System.out.println("Encoding of the File is " + reader.getEncoding() + " of file " + data.getName());
                while (yReader.ready() && yReader.ready()) {
                    String line = yReader.readLine();
                    if (line.indexOf("\n") == -1 ) {
                        importData.append(line+ "\n");
                    } else {
                        importData.append(line);
                    }
                }
            } catch (EOFException ex2) {
                Logger.getLogger(ImporterTopic.class.getName()).log(Level.SEVERE, null, ex2);
                // return importData.toString();
                //if (files.size() > 1) {
                //    System.out.println("INFO: Merging two documents");
                //    importData.append("--- BEGINNING OF NEW DOCUMENT ---");
                //}
            } catch (IOException ex) {
                Logger.getLogger(ImporterTopic.class.getName()).log(Level.SEVERE, null, ex);
            }
        //}
        return importData.toString();
    }

    private void cleanUp(String[] topics, CorporateDirectives directives) {
        Vector newPropsToDelete = as.getRelatedTopics(topics[0].toString(), ASSOCTYPE_COMPOSITION, 2);
        BaseTopic topic = as.getLiveTopic(topics[0], 1);
        BaseTopic typeContainter = as.getRelatedTopic(topic.getID(), ASSOCTYPE_AGGREGATION, 1);
        try {
            directives.add(as.deleteTopic(topics[0], 1));
        } catch(DeepaMehtaException ex) {
            System.out.println("Error: Deleting TopicType: " + topics[0] + ex.getMessage());
        }
        System.out.println("INFO: deleted topictype : " + topic.getName() + ", "+topic.getID()+", " + newPropsToDelete.size() + " props and 1 container of type: "+typeContainter.getName());
        //PresentableTopic pt2 = as.createPresentableTopic(topic);
        directives.add(DIRECTIVE_HIDE_TOPIC, topic.getID());
        String[] ts = topics[1].split(",");
        for (int t=0; t < ts.length; t++) {
            String id = ts[t].replaceAll(",", "");
            //topic = as.getLiveTopic(id, 1);
            //PresentableTopic pt = as.createPresentableTopic(topic);
            directives.add(DIRECTIVE_HIDE_TOPIC, id);
            try {
                directives.add(as.deleteTopic(id, version));
            } catch(DeepaMehtaException ex) {
                System.out.println("ERROR: could not delete Topic: " + ex.getMessage());
            }
            System.out.println("INFO: deleted topic : " + id);
        }
    }

    // used for advanced import  - where the user can create an own custom type based on his column names
    private String createImportType(Vector entries, CorporateDirectives directives, Session session) {
        String typeId = as.getNewTopicID();
        BaseTopic tt = as.createLiveTopic(typeId, TOPICTYPE_TOPICTYPE, getName(), session);
        as.setTopicProperty(tt, PROPERTY_NAME, getName());
        PresentableTopic pttt = new PresentableTopic(tt, getID());
        directives.add(DIRECTIVE_SHOW_TOPIC, pttt);
        Hashtable fields = (Hashtable) entries.firstElement();
        Object[] props = fields.keySet().toArray();
        propertyLoop:
        for (int i=0; i<props.length; i++) {
            String assocId = as.getNewAssociationID();
            String propName = (String) props[i];
            if (propName.equals(PROPERTY_NAME)
                    || propName.equals(PROPERTY_DESCRIPTION)
                    || propName.equals(PROPERTY_ICON)
                    || propName.equals("Titel")
                    || propName.equals("Title")) {
                // skip creating properties
                System.out.println("Found " + propName + " fitting into DeepaMehta's Generic TopicType Topic, skip modelling " +  propName);
            } else {
                // properties
                BaseTopic propTopic = as.createLiveTopic(as.getNewTopicID(), TOPICTYPE_PROPERTY, propName, null);
                // To configure visualization Modes, as.setTopicProperty(topic, propDef, propDef);
                BaseAssociation assoc = as.createLiveAssociation(assocId, ASSOCTYPE_COMPOSITION, pttt.getID(), propTopic.getID(), session, directives);
                // make them presentable
                PresentableTopic pt = new PresentableTopic(propTopic, getID());
                PresentableAssociation pa = new PresentableAssociation(assoc);
                // show them
                directives.add(DIRECTIVE_SHOW_TOPIC, pt);
                directives.add(DIRECTIVE_SHOW_ASSOCIATION, pa);
            }
        }
        // store the link between importer topic and just created topictype
        BaseAssociation assoc2 = as.createLiveAssociation(as.getNewAssociationID(), ASSOCTYPE_ASSOCIATION, getID(), tt.getID(), session, directives);
        PresentableAssociation pa2 = new PresentableAssociation(assoc2);
        directives.add(DIRECTIVE_SHOW_ASSOCIATION, pa2);
        return tt.getID();
    }

    private PresentableTopic createNewTopicMap(Vector topics, Session session, CorporateDirectives directives) {
        BaseTopic map = as.createLiveTopic(as.getNewTopicID(), TOPICTYPE_TOPICMAP, "Import Result", session);
        BaseAssociation assoc = as.createLiveAssociation(as.getNewAssociationID(), ASSOCTYPE_ASSOCIATION, getID(), map.getID(), session, directives);
        PresentableAssociation pa = new PresentableAssociation(assoc);
        PresentableTopic mapTopic = new PresentableTopic(map, getID());
        directives.add(DIRECTIVE_SHOW_TOPIC, mapTopic);
        directives.add(DIRECTIVE_SHOW_ASSOCIATION, pa);
        return mapTopic;
    }

    private Vector createTopics(String typeId, Vector entries, CorporateDirectives directives, Session session, StringBuffer logFile) {
        Vector topics = new Vector();
        logFile.append(typeId+"\n");
        // BaseTopic typetopic = as.getLiveTopic(typeId, 1);
        for (int i=0; i < entries.size(); i++) {
            String topicId = as.getNewTopicID();
            String topicName = "";
            Hashtable topicProps = (Hashtable) entries.get(i);
            Object[] keys = topicProps.keySet().toArray();
            String hasName = (String) topicProps.get(PROPERTY_NAME);
            String hasTitle = (String) topicProps.get("Title");
            String hasTitel = (String) topicProps.get("Titel");
            if (hasTitel != null) {
                topicName = hasTitel;
            } else if (hasTitle != null ) {
                topicName = hasTitle;
            } else if (hasName != null) {
                topicName = hasName;
            }
            // System.out.println("name is taken from first column: " + topicName);
            BaseTopic topic = as.createLiveTopic(topicId, typeId, topicName, session);
            // and stored twice
            as.setTopicProperty(topic, PROPERTY_NAME, topicName);
            for (int ti = 0; ti < keys.length; ti++) {
                // from second prop on
                String propName = (String) keys[ti];
                String propValue = (String) topicProps.get(propName);
                //System.out.println("INFO: storing : " + propName + " to "+ propValue);
                as.setTopicProperty(topic, propName, propValue);
            }
            logFile.append(topicId+",");
            PresentableTopic pt = as.createPresentableTopic(topic);
            // stores the topics originating import wizard instance
            BaseAssociation resultAssoc = as.createLiveAssociation(as.getNewAssociationID(),
                    ASSOCTYPE_IMPORT_RESULT, topicId, getID(), session, directives);
            topics.add(pt);
        }
        return topics;
    }

    private String generatePreview(Vector topics, Hashtable props) {
        StringBuffer resultTypeDescr = new StringBuffer();
        String quote = "\"";
        Hashtable fields = (Hashtable) topics.firstElement();
        Set cols = fields.keySet();
        //html.append("<p/>");topics
        Object[] keys = cols.toArray();
        int rows = (topics.size() > 5) ? 5 : topics.size();
        // keys are propNames
        int columns = keys.length;
        for (int c = 0; c < keys.length; c++) {
            // name each property field
            String propName = (String) keys[c];
            propName = propName.replaceAll(quote, "");
            //html.append("<b>" + name + "</b><br/>");
            resultTypeDescr.append(""+propName+",");
        }
        StringBuffer html = new StringBuffer();
        html.append("<body><h2>Import Preview for TopicType \"<i>"+getName()+"</i>\"</h2>");
        html.append("<i>Note: Fields with \"Titel\", \"Title\" and \"Name\" are additionally stored as topic names.</i><br/>");
        html.append("<table border=\"1\" >");
        for (int r = 0; r < rows; r++) {
            // get fields + values for each entry
            Hashtable entryFields = (Hashtable) topics.get(r);
            html.append("<tr>");
            for (int c = 0; c < columns; c++) {
                // each property
                String pname = (String) keys[c];
                String pval = (String) entryFields.get(pname);
                //System.out.println("a property is named: " + pname + " and it's value is: " + pval );
                // String name = entryFields[c];
                html.append("<td>");
                if (r == 0) {
                    html.append("<b>" + pname + "</b>");
//                    if (c == 0) {
//                        //rename field headers to name and descr
//                        html.append("<b>Name</b>");
//                    } else if ( c == 1) {
//                        html.append("<b>Description</b>");
//                    } else {
//                    }
                } else {
                    html.append("<i>" + pval + "</i>");
                }
                html.append("</td>");
            }
            html.append("</tr>");
        }
        html.append("</table>");
        html.append("<p>At the moment, DeepaMehta could import for you " + (topics.size()) + " of type <b>"+getName()+"</b>");
        html.append("<br/></body>");
        // save report into dm-hashtable
        props.put(PROPERTY_DESCRIPTION,  html.toString());
        // return internal typeDescr ?
        return resultTypeDescr.toString();
    }

    private String generateReport(Vector entries, String typeId) {
        StringBuffer html = new StringBuffer();
        html.append("<body><h2>Import Report</h2><br/>");
        html.append("<b>" + entries.size() +"</b> topics were successfully imported.<br/>");
        html.append("<p>You can reveal those contents item by item or all oif them by navigating along <b>\"What's related\"</b> by \"Associaton Type\" and then select <b>\"Import Results\"</b>.</p>");
        html.append("<p>The TopicType <b>\""+as.getLiveTopic(typeId, 1).getName()+"\"</b> was created with the following attributes of your data. <br>");
        html.append("<ul>");
        Vector properties = as.getRelatedTopics(typeId, ASSOCTYPE_COMPOSITION, 2);
        for (int p=0; p < properties.size(); p++) {
            BaseTopic topic = (BaseTopic) properties.get(p);
            html.append("<li><a href=\"http://revealTopic|"+topic.getID()+"\">"+topic.getName()+"</a></li>");
        }
        html.append("</ul>");
        html.append("</p></body>");
        return html.toString();
    }

    private Vector getTopicProperties(String data, CorporateDirectives directives) {
        
        String seperator = getProperty(PROPERTY_IMPORT_SEPERATOR);
        String textdelimiter = getProperty(PROPERTY_IMPORT_TEXT_DELIMITER);
        if (seperator.equals("Tabulator")) {
            System.out.println(">>>> seperator is tabulator \t");
            seperator = "\t";
        } else if (seperator.equals("Comma")) {
            seperator = ",";
            System.out.println(">>>> seperator is comma");
        } else if (seperator.equals("Colon")) {
            seperator = ":";
            System.out.println(">>>> seperator is colon");
        }
        // char lf = 0x0a; char cr = 0x0d;
        String semicolon = ";";
        String quote = "\"";
        String delim = quote;
        if (textdelimiter.equals("Semicolon")) {
            System.out.println(">>>> delimiter is semicolon");
            delim  = semicolon;
        }
        if (data.indexOf("--- BEGINNING OF NEW DOCUMENT ---") != -1) {
            System.out.println("new document is beginning");
        }// ## no linefeed in content allowed
        String[] entries = data.split("\n");
        Vector topics = new Vector();
        System.out.println(">>>> reading " + entries.length + "lines, first line is: "+entries[0]);
        Hashtable propOrder = new Hashtable();
        // first line serves the field/headers
        String [] fields = entries[0].split(seperator);
        for (int i = 0; i < fields.length; i++) {
            // remove quotation marks
            fields[i] = fields[i].replaceAll(delim, "");
            propOrder.put(i, fields[i]);
            // System.out.println("propDef: i : " +i +" is : " + fields[i]);
        }
        for (int i = 1; i <entries.length; i++) {
            String[] values = entries[i].split(seperator);
            Hashtable topicProps = new Hashtable(); // check values for text delimiters
            // System.out.println("    delimiter is " + delim);
            for (int p = 0; p< values.length; p++) {
                String propId = (String) propOrder.get(p);
                String propVal = values[p];
                if (propId == null) {
                    System.out.println("ERROR: caused by comma in a Value, skip saving value " + propVal);
                } else {
                    propVal = propVal.replaceAll(delim, "");
                    // System.out.println("propId is" + propId + " propVal: " + propVal);
                    topicProps.put(propId, propVal);
                }
                // remove all delimiters
                
//                int firstPos = value.indexOf(delim);
//                if (firstPos != -1) {
//                    int lastPos = value.length();
//                        if (value.indexOf(delim, firstPos+1) != -1) {
//                            // System.out.println("INFO: value has a delimiter in: " + value.substring(firstPos, lastPos));
//                        }
//
//                }
            }
            // save topic
            topics.add(topicProps);
        }
        return topics;

    }
}