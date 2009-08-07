package de.deepamehta.client;

import de.deepamehta.Detail;

import java.awt.Image;
import java.util.Hashtable;

import javax.swing.Icon;



/**
 * This interface specifies the controler for a {@link GraphPanel}.
 * <p>
 * <hr>
 * Last functional change: 18.4.2004 (2.0b3-pre2)<br>
 * Last documentation update: 16.11.2000 (2.0a7-pre3)<br>
 * J&ouml;rg Richter<br>
 * jri@freenet.de
 */
interface GraphPanelControler {



	// ---------------------------
	// --- ### Providing the model ---
	// ---------------------------



	boolean checkTopicType(String typeID);
	boolean checkAssociationType(String typeID);
	//
	// ### boolean topicTranslationAllowed(PresentationTopicMap topicmap);
	// ### boolean creatingEdgesEnabled(PresentationTopicMap topicmap);
	//
	Icon boldIcon();
	Icon italicIcon();
	Icon underlineIcon();
	//
	Image getImage(String imagefile);
	//
	String getBaseURL();



	// -----------------
	// --- Callbacks ---
	// -----------------



	void nodeSelected(PresentationTopicMap topicmap, GraphNode node);
	void edgeSelected(PresentationTopicMap topicmap, GraphEdge edge);
	void graphSelected(PresentationTopicMap topicmap);
	//
	void nodeDoubleClicked(PresentationTopicMap topicmap, GraphNode node);
	void edgeDoubleClicked(PresentationTopicMap topicmap, GraphEdge edge);
	//
	void showNodeMenu(PresentationTopicMap topicmap, GraphNode node, int x, int y);
	void showEdgeMenu(PresentationTopicMap topicmap, GraphEdge edge, int x, int y);
	void showGraphMenu(PresentationTopicMap topicmap, int x, int y);
	//
	void processNodeCommand(PresentationTopicMap topicmap, GraphNode node, String command);
	void processEdgeCommand(PresentationTopicMap topicmap, GraphEdge edge, String command);
	void processGraphCommand(PresentationTopicMap topicmap, String command);
	//
	void processNodeDetail(PresentationTopicMap topicmap, String nodeID, Detail detail);
	void processEdgeDetail(PresentationTopicMap topicmap, String edgeID, Detail detail);
	//
	void nodesMoved(PresentationTopicMap topicmap, Hashtable nodes);
	void graphMoved(PresentationTopicMap topicmap, int tx, int ty);
	//
	void repaint();
	void updateBounds(PresentationTopicMap topicmap);	// ###
	void detailWindowClosed(String key);
	//
	void beginTranslation();
	void beginCreatingEdge();
	//
	void beginLongTask();
	void endTask();
}
