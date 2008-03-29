package de.deepamehta.client;

import de.deepamehta.BaseTopic;
import de.deepamehta.Commands;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.Detail;
import de.deepamehta.Directive;
import de.deepamehta.Directives;
import de.deepamehta.PresentableAssociation;
import de.deepamehta.PresentableTopic;
import de.deepamehta.PresentableType;
import de.deepamehta.service.CorporateTopicMap;
import de.deepamehta.util.DeepaMehtaUtils;

import java.awt.Color;
import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;



/**
 * <p>
 * <hr>
 * Last functional change: 29.3.2008 (2.0b8)<br>
 * Last documentation update: 17.12.2001 (2.0a14-pre5)<br>
 * J&ouml;rg Richter<br>
 * jri@freenet.de
 */
public class PresentationDirectives extends Directives {



	// ********************
	// *** Constructors ***
	// ********************



	public PresentationDirectives(Directives dirs, PresentationService ps) {
		// >>> compare to CorporateDirectives.write()
		int dirCount = dirs.directives.size();
		Enumeration e = dirs.directives.elements();
		// ### System.out.println("> creating " + dirCount + " directives ...");
		Directive directive;
		int type = 0;
		Object param1, param2, param3, param4, param5;
		// loop through all directives
		try {
			while (e.hasMoreElements()) {
				directive = (Directive) e.nextElement();
				type = directive.type;
				param1 = directive.param1;
				param2 = directive.param2;
				param3 = directive.param3;
				param4 = directive.param4;
				param5 = directive.param5;
				switch (type) {
				case DIRECTIVE_SHOW_TOPIC:
					PresentationTopic topic = new PresentationTopic((PresentableTopic) param1, ps);
					// Note: param2 (evoke flag) is not sent to client
					directives.addElement(new Directive(type, topic, param3));
					break;
				case DIRECTIVE_SHOW_TOPICS:
					Vector topics = DeepaMehtaClientUtils.createPresentationTopics(((Vector) param1).elements(), ps);
					// Note: param2 (evoke flag) is not sent to client
					directives.addElement(new Directive(type, topics));
					break;
				case DIRECTIVE_SHOW_ASSOCIATION:
					PresentationAssociation assoc = new PresentationAssociation((PresentableAssociation) param1, ps);
					// Note: param2 (evoke flag) is not sent to client
					directives.addElement(new Directive(type, assoc, param3));
					break;
				case DIRECTIVE_SHOW_ASSOCIATIONS:
					Vector assocs = DeepaMehtaClientUtils.createPresentationAssociations(((Vector) param1).elements(), ps);
					// Note: param2 (evoke flag) is not sent to client
					directives.addElement(new Directive(type, assocs));
					break;
				case DIRECTIVE_HIDE_TOPIC:
				case DIRECTIVE_HIDE_ASSOCIATION:
				case DIRECTIVE_HIDE_TOPICS:
				case DIRECTIVE_HIDE_ASSOCIATIONS:
					// Note: param2 (die flag) is not sent to client
					directives.addElement(new Directive(type, param1, param3));
					break;
				case DIRECTIVE_SELECT_TOPIC:
				case DIRECTIVE_SELECT_ASSOCIATION:
					directives.addElement(new Directive(type, param1, param2, param3, param4, param5));
					break;
				case DIRECTIVE_SELECT_TOPICMAP:
					// Note: param3 (retype allowed?) not yet implemented ###
					directives.addElement(new Directive(type, param1, param2, param4));
					break;
				case DIRECTIVE_UPDATE_TOPIC_TYPE:
					PresentationType topicType = new PresentationType((PresentableType) param1, ps);
					directives.addElement(new Directive(type, topicType));
					break;
				case DIRECTIVE_UPDATE_ASSOC_TYPE:
					PresentationType assocType = new PresentationType((PresentableType) param1, ps);
					directives.addElement(new Directive(type, assocType));
					break;
				case DIRECTIVE_SHOW_TOPIC_PROPERTIES:
				case DIRECTIVE_SHOW_ASSOC_PROPERTIES:
					// Note: param3 (topic version) is not sent to client
					directives.addElement(new Directive(type, param1, param2, param4));
					break;
				case DIRECTIVE_FOCUS_TYPE:
				case DIRECTIVE_FOCUS_NAME:
				case DIRECTIVE_FOCUS_PROPERTY:
					// no parameters
					directives.addElement(new Directive(type));
					break;
				case DIRECTIVE_SET_TOPIC_TYPE:
				case DIRECTIVE_SET_TOPIC_NAME:
				case DIRECTIVE_SET_TOPIC_LABEL:
				case DIRECTIVE_SET_TOPIC_ICON:
				case DIRECTIVE_SET_ASSOC_TYPE:
				case DIRECTIVE_SET_ASSOC_NAME:
					// Note: param3 (topic / association version) is not sent to client
					// Note: param4 (properties) is not sent to client (SET_TOPIC_LABEL)
					directives.addElement(new Directive(type, param1, param2));
					break;
				case DIRECTIVE_SET_TOPIC_GEOMETRY:
				case DIRECTIVE_SET_TOPIC_LOCK:
					directives.addElement(new Directive(type, param1, param2, param3));
					break;
				case DIRECTIVE_SHOW_MENU:
					PresentationCommands commands = new PresentationCommands(
						(Commands) param2, ps);
					directives.addElement(new Directive(type, param1, commands, param3));
					break;
				case DIRECTIVE_SHOW_DETAIL:
					directives.addElement(new Directive(type, param1, param2));
					break;
				case DIRECTIVE_SHOW_WORKSPACE:
					PresentationTopicMap useMap;
					//
					String topicmapID = ((PresentableTopic) param1).getID();
					CorporateTopicMap workspace = (CorporateTopicMap) param2;
					int editorContext = ((Integer) param3).intValue();
					// ### Note: param2 (CorporateTopicMap) is split into param2 and param3
					useMap = new PresentationTopicMap(workspace.getTopicMap(), editorContext, topicmapID, VIEWMODE_USE, ps);
					// ### Note: param3 is shifted to param4
					directives.addElement(new Directive(type, param1, useMap, param3));
					break;
				case DIRECTIVE_SHOW_VIEW:
					topicmapID = ((PresentableTopic) param1).getID();
					CorporateTopicMap view = (CorporateTopicMap) param2;
					// ### Note: param2 (CorporateTopicMap) is split into param2 and param3
					useMap = new PresentationTopicMap(view.getTopicMap(), EDITOR_CONTEXT_VIEW, topicmapID, VIEWMODE_USE, ps);
					// ### Note: param3 and param4 are not sent to client
					directives.addElement(new Directive(type, param1, useMap));
					break;
				case DIRECTIVE_SELECT_EDITOR:
				case DIRECTIVE_CLOSE_EDITOR:
					directives.addElement(new Directive(type, param1));
					break;
				case DIRECTIVE_RENAME_EDITOR:
				case DIRECTIVE_SET_EDITOR_ICON:
					directives.addElement(new Directive(type, param1, param2));
					break;
				case DIRECTIVE_SET_EDITOR_BGIMAGE:
					directives.addElement(new Directive(type, param1, param2));
					break;
				case DIRECTIVE_SET_EDITOR_BGCOLOR:
					Color color = DeepaMehtaUtils.parseHexColor((String) param2, DEFAULT_VIEW_BGCOLOR);
					directives.addElement(new Directive(type, param1, color));
					break;
				case DIRECTIVE_SHOW_MESSAGE:
					directives.addElement(new Directive(type, param1, param2));
					break;
				case DIRECTIVE_PLAY_SOUND:
					directives.addElement(new Directive(type, param1));
					break;
				case DIRECTIVE_CHOOSE_FILE:
					// no parameters
					directives.addElement(new Directive(type));
					setChained(type);				// set chanined
					break;
				case DIRECTIVE_OPEN_FILE:
				case DIRECTIVE_COPY_FILE:
					directives.addElement(new Directive(type, param1, param2));
					break;
				case DIRECTIVE_CHOOSE_COLOR:
					directives.addElement(new Directive(type, param1));
					setChained(type);
					break;
				case DIRECTIVE_DOWNLOAD_FILE:
				case DIRECTIVE_UPLOAD_FILE:
				case DIRECTIVE_SET_LAST_MODIFIED:
					directives.addElement(new Directive(type, param1, param2, param3));
					break;
				case DIRECTIVE_QUEUE_DIRECTIVES:
					PresentationDirectives qd = new PresentationDirectives(
						(Directives) param1, ps);
					directives.addElement(new Directive(type, qd));
					break;
				case DIRECTIVE_QUEUE_MESSAGE:
				case DIRECTIVE_LAUNCH_APPLICATION:
				case DIRECTIVE_OPEN_URL:
					directives.addElement(new Directive(type, param1));
					break;
				default:
					throw new DeepaMehtaException("unexpected directive type: " + type);
				}
			} // end loop through all directives
		} catch (NullPointerException exp) {
			throw new DeepaMehtaException("error while creating directive of type " +
				type + " (" + exp + " -- probably caused by wrong usage " +
				"through application programmer)");
		} catch (ClassCastException exp) {
			throw new DeepaMehtaException("error while creating directive of type " +
				type + " (" + exp + " -- probably caused by wrong usage " +
				"through application programmer)");
		}
	}

	/**
	 * Stream constructor.
	 *
	 * @see		#PresentationDirectives
	 * @see		DeepaMehtaClient#createGUI
	 * @see		MessagingConnection#processMessage
	 *
	 * @see		PresentationTopicMap#viewmodeSwitched
	 * @see		PresentationTopicMap#changeTopicName
	 * @see		PresentationTopicMap#changeTopicType
	 * @see		PresentationTopicMap#changeAssocType
	 * @see		PresentationTopicMap#changeTopicData
	 * @see		PresentationTopicMap#createNewTopicType
	 * @see		PresentationTopicMap#createNewAssociationType
	 * @see		PresentationTopicMap#processTopicCommand
	 * @see		PresentationTopicMap#processAssociationCommand
	 */
	PresentationDirectives(DataInputStream in, PresentationService ps) throws DeepaMehtaException {
		int type = 0;
		Object param1, param2, param3, param4, param5;
		//
		try {
			int dirCount = in.readInt();
			//
			System.out.println("> receive " + dirCount + " directives ...");
			//
			StringBuffer log = new StringBuffer();	// diagnosis log
			// loop through all directives
			for (int i = 0; i < dirCount; i++) {
				type = in.readInt();
				// log
				log.append(' ');
				log.append(type);
				//
				switch (type) {
				case DIRECTIVE_SHOW_TOPIC:
					param1 = new PresentationTopic(in, ps);
					param2 = in.readUTF();	// topicmap ID, Note: is param3 at server side
					directives.addElement(new Directive(type, param1, param2));
					break;
				case DIRECTIVE_SHOW_TOPICS:
					param1 = DeepaMehtaClientUtils.readPresentationTopics(in, ps);
					directives.addElement(new Directive(type, param1));
					break;
				case DIRECTIVE_SHOW_ASSOCIATION:
					param1 = new PresentationAssociation(in);
					param2 = in.readUTF();	// topicmap ID, Note: is param3 at server side
					directives.addElement(new Directive(type, param1, param2));
					break;
				case DIRECTIVE_SHOW_ASSOCIATIONS:
					param1 = DeepaMehtaClientUtils.readPresentationAssociations(in);
					directives.addElement(new Directive(type, param1));
					break;
				case DIRECTIVE_HIDE_TOPIC:
				case DIRECTIVE_HIDE_ASSOCIATION:
					param1 = in.readUTF();
					param2 = in.readUTF();
					directives.addElement(new Directive(type, param1, param2));
					break;
				case DIRECTIVE_HIDE_TOPICS:
				case DIRECTIVE_HIDE_ASSOCIATIONS:
					param1 = DeepaMehtaUtils.readStrings(in);
					param2 = in.readUTF();
					directives.addElement(new Directive(type, param1, param2));
					break;
				case DIRECTIVE_SELECT_TOPIC:
				case DIRECTIVE_SELECT_ASSOCIATION:
					param1 = in.readUTF();
					param2 = DeepaMehtaUtils.readHashtable(in);
					param3 = DeepaMehtaUtils.readStrings(in);
					param4 = new Boolean(in.readBoolean());
					param5 = DeepaMehtaUtils.readHashtable(in);
					directives.addElement(new Directive(type, param1, param2, param3, param4, param5));
					break;
				case DIRECTIVE_SELECT_TOPICMAP:
					param1 = DeepaMehtaUtils.readHashtable(in);
					param2 = DeepaMehtaUtils.readStrings(in);
					// ### param3 = new Boolean(in.readBoolean());
					param4 = DeepaMehtaUtils.readHashtable(in);
					directives.addElement(new Directive(type, param1, param2, param4));
					break;
				case DIRECTIVE_UPDATE_TOPIC_TYPE:
					param1 = new PresentationType(in, ps);
					directives.addElement(new Directive(type, param1));
					break;
				case DIRECTIVE_UPDATE_ASSOC_TYPE:
					param1 = new PresentationType(in, ps);
					directives.addElement(new Directive(type, param1));
					break;
				case DIRECTIVE_SHOW_TOPIC_PROPERTIES:
				case DIRECTIVE_SHOW_ASSOC_PROPERTIES:
					param1 = in.readUTF();
					param2 = DeepaMehtaUtils.readHashtable(in);
					param3 = DeepaMehtaUtils.readHashtable(in);		// Note: is param4 at server side
					directives.addElement(new Directive(type, param1, param2, param3));
					break;
				case DIRECTIVE_FOCUS_TYPE:
				case DIRECTIVE_FOCUS_NAME:
					directives.addElement(new Directive(type));
					break;
				case DIRECTIVE_FOCUS_PROPERTY:
					// ### param1 = in.readUTF();
					directives.addElement(new Directive(type /* ###, param1 */));
					break;
				case DIRECTIVE_SET_TOPIC_TYPE:
				case DIRECTIVE_SET_TOPIC_NAME:
				case DIRECTIVE_SET_TOPIC_LABEL:
				case DIRECTIVE_SET_TOPIC_ICON:
				case DIRECTIVE_SET_ASSOC_TYPE:
				case DIRECTIVE_SET_ASSOC_NAME:
					param1 = in.readUTF();
					param2 = in.readUTF();
					directives.addElement(new Directive(type, param1, param2));
					break;
				case DIRECTIVE_SET_TOPIC_GEOMETRY:
					param1 = in.readUTF();
					int x = in.readInt();
					int y = in.readInt();
					param2 = new Point(x, y);
					param3 = in.readUTF();
					directives.addElement(new Directive(type, param1, param2, param3));
					break;
				case DIRECTIVE_SET_TOPIC_LOCK:
					param1 = in.readUTF();
					param2 = new Boolean(in.readBoolean());
					param3 = in.readUTF();
					directives.addElement(new Directive(type, param1, param2, param3));
					break;
				case DIRECTIVE_SHOW_MENU:
					param1 = in.readUTF();
					param2 = new PresentationCommands(in, ps);
					x = in.readInt();
					y = in.readInt();
					param3 = new Point(x, y);
					directives.addElement(new Directive(type, param1, param2, param3));
					break;
				case DIRECTIVE_SHOW_DETAIL:
					param1 = in.readUTF();
					param2 = new Detail(in);
					directives.addElement(new Directive(type, param1, param2));
					break;
				case DIRECTIVE_SHOW_WORKSPACE:
					// topicmap metadata
					param1 = new PresentableTopic(in);
					String topicmapID = ((PresentableTopic) param1).getID();
					// editor context
					// Note: the editor context is read before the topicmap
					param3 = new Integer(in.read());
					int editorContext = ((Integer) param3).intValue();
					// topicmap
					param2 = new PresentationTopicMap(in, editorContext, topicmapID, VIEWMODE_USE, ps);
					directives.addElement(new Directive(type, param1, param2, param3));
					break;
				case DIRECTIVE_SHOW_VIEW:
					// topicmap metadata
					param1 = new PresentableTopic(in);
					topicmapID = ((BaseTopic) param1).getID();
					// topicmap
					param2 = new PresentationTopicMap(in, EDITOR_CONTEXT_VIEW, topicmapID, VIEWMODE_USE, ps);
					directives.addElement(new Directive(type, param1, param2));
					break;
				case DIRECTIVE_SELECT_EDITOR:
				case DIRECTIVE_CLOSE_EDITOR:
					param1 = in.readUTF();
					directives.addElement(new Directive(type, param1));
					break;
				case DIRECTIVE_RENAME_EDITOR:
					param1 = in.readUTF();
					param2 = in.readUTF();
					directives.addElement(new Directive(type, param1, param2));
					break;
				case DIRECTIVE_SET_EDITOR_BGIMAGE:
					param1 = in.readUTF();
					param2 = in.readUTF();
					directives.addElement(new Directive(type, param1, param2));
					break;
				case DIRECTIVE_SET_EDITOR_BGCOLOR:
					param1 = in.readUTF();
					param2 = DeepaMehtaUtils.parseHexColor(in.readUTF(), DEFAULT_VIEW_BGCOLOR);
					directives.addElement(new Directive(type, param1, param2));
					break;
				case DIRECTIVE_SET_EDITOR_ICON:
					param1 = in.readUTF();
					param2 = in.readUTF();
					directives.addElement(new Directive(type, param1, param2));
					break;
				case DIRECTIVE_SHOW_MESSAGE:
					param1 = in.readUTF();
					param2 = new Integer(in.read());
					directives.addElement(new Directive(type, param1, param2));
					break;
				case DIRECTIVE_PLAY_SOUND:
					param1 = in.readUTF();
					directives.addElement(new Directive(type, param1));
					break;
				case DIRECTIVE_CHOOSE_FILE:
					directives.addElement(new Directive(type));
					setChained(type);				// set chanined
					break;
				case DIRECTIVE_CHOOSE_COLOR:
					param1 = in.readUTF();
					directives.addElement(new Directive(type, param1));
					setChained(type);
					break;
				case DIRECTIVE_COPY_FILE:
					param1 = in.readUTF();
					param2 = new Integer(in.read());
					directives.addElement(new Directive(type, param1, param2));
					break;
				case DIRECTIVE_DOWNLOAD_FILE:
				case DIRECTIVE_UPLOAD_FILE:
				case DIRECTIVE_SET_LAST_MODIFIED:
					param1 = in.readUTF();
					param2 = new Long(in.readLong());
					param3 = new Integer(in.read());
					directives.addElement(new Directive(type, param1, param2, param3));
					break;
				case DIRECTIVE_OPEN_FILE:
					param1 = in.readUTF();
					param2 = in.readUTF();
					directives.addElement(new Directive(type, param1, param2));
					break;
				case DIRECTIVE_QUEUE_DIRECTIVES:
					param1 = new PresentationDirectives(in, ps);
					directives.addElement(new Directive(type, param1));
					break;
				case DIRECTIVE_QUEUE_MESSAGE:
				case DIRECTIVE_LAUNCH_APPLICATION:
				case DIRECTIVE_OPEN_URL:
					param1 = in.readUTF();
					directives.addElement(new Directive(type, param1));
					break;
				default:
					System.out.println("*** PresentationDirectives(): unexpected " +
						"directive type (" + type + ")");
				}
			}	// end loop loop through all directives
			// diagnosis output
			System.out.println("> " + dirCount + " directives received, types:" + log);
			if (LOG_MEM_STAT) {
				DeepaMehtaUtils.memoryStatus();
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new DeepaMehtaException("I/O error while reading directive of type " +
				type + ": " + e);
		}
	}
}
