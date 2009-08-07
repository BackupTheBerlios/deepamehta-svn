package de.deepamehta.client;

import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.util.DeepaMehtaUtils;

import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.Icon;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;



/**
 * Editor model for an opened topicmap.
 * <p>
 * <hr>
 * Last change: 3.2.2008 (2.0b8)<br>
 * J&ouml;rg Richter<br>
 * jri@deepamehta.de
 */
class TopicmapEditorModel implements DeepaMehtaConstants {



	// **************
	// *** Fields ***
	// **************



	private PresentationTopicMap topicMap;

	/**
	 * The topicmap being edited by this topicmap editor (type <code>tt-topicmap</code>).
	 */
	private BaseTopic topicmapTopic;

	/**
	 * The editor context this editor is deployed for.
	 * <p>
	 * See the 4 {@link #EDITOR_CONTEXT_PERSONAL EDITOR_CONTEXT_XXX constants}.
	 */
	private int editorContext;

	/**
	 * Displayed topic/assoc details.
	 * <p>
 	 * ### The hashkey is composed by topicID/assocID AND detail command (one topic may have many
	 * details open at the same time, but no two of the same processing command)
 	 * <p>
	 * Key: "topicID:command" (<code>String</code>)<br>
	 * Value: detail ({@link PresentationDetail})
	 */
	private Hashtable details = new Hashtable();
	private Selection selection = new Selection();
	private Rectangle bounds = new Rectangle();				// the bounding rectangle of this graph
	private Image bgImage;

	// ---

	private GraphPanelControler controler;	// ###
			ComboBoxItem item;				// ###



	// *******************
	// *** Constructor ***
	// *******************



	/**
	 * @param	editorContext	The editor context this editor is deployed for.<br>
	 *							See the 4 {@link #EDITOR_CONTEXT_PERSONAL EDITOR_CONTEXT_XXX constants}.
	 * @param	topicmapTopic	The topic representing the view being edited by this editor (type <code>tt-topicmap</code>).
	 * @param	topicMap
	 *
	 * @see		PresentationService#createTopicMapEditor
	 */
	TopicmapEditorModel(GraphPanelControler controler, int editorContext, BaseTopic topicmapTopic,
																	 PresentationTopicMap topicMap) {
		this.controler = controler;
		this.editorContext = editorContext;
		this.topicmapTopic = topicmapTopic;
		this.topicMap = topicMap;
		//
		initAssociations(topicMap);
		//
		setBounds();
	}



	// ***************
	// *** Methods ***
	// ***************



	/**
	 * @see		PresentationService#saveGeometry
	 * @see		PresentationService#closeEditor
	 */
	String getTopicMapID() {
		return topicmapTopic.getID();
	}

	/**
	 * Returns the topic representing the view being edited by this editor (type
	 * <code>tt-topicmap</code>).
	 *
	 * @see		TopicMapEditorControler#getTopicmapTopic
	 */
	BaseTopic getTopicmap() {
		return topicmapTopic;
	}

	PresentationTopicMap getTopicMap() {
		return topicMap;
	}

	int getContext() {
		return editorContext;
	}

	GraphPanelControler getControler() {
		return controler;
	}

	// ---

	PresentationTopic getTopic(String topicID) {
		return (PresentationTopic) topicMap.getTopic(topicID);
	}

	PresentationAssociation getAssociation(String assocID) {
		return (PresentationAssociation) topicMap.getAssociation(assocID);
	}

	// ### copy in GraphPanel
	int getSelected() {
		return selection.mode;
	}

	// ### copy in GraphPanel
	PresentationDetail getDetail(String key) {
		return (PresentationDetail) details.get(key);
	}

	Hashtable getDetails() {
		return details;
	}

	Rectangle getBounds() {
		return bounds;
	}

	Selection getSelection() {
		return selection;
	}

	// ---

	/**
	 * @see		PresentationService#changeEditorIcon
	 */
	void setIcon(Icon icon) {
		item.icon = icon;
	}

	void setName(String name) {
		item.text = name;
	}

	void setItem(ComboBoxItem item) {
		this.item = item;
	}

	/**
	 * @see		PresentationService#changeEditorBackgroundImage
	 */
	void setBackgroundImage(Image image) {
		topicMap.setBackgroundImage(image);
	}

	/**
	 * @see		PresentationService#changeBackgroundColor
	 */
	void setBackgroundColor(Color color) {
		topicMap.setBackgroundColor(color);
	}

	// ---

	/**
	 * @see		PresentationService#showDetail
	 */
	void putDetail(String key, PresentationDetail detail) {
		if (details.put(key, detail) != null) {
			System.out.println("*** TopicmapEditorModel.putDetail(): key \"" + key + "\" overridden");
		}
	}

	/**
	 * References checked: 26.4.2003 (2.0a18-pre10)
	 *
	 * @see		#hideDetails
	 * @see		PresentationService#detailWindowClosed
	 */
	void removeNodeDetail(String key) {
		if (details.remove(key) == null) {
			System.out.println("*** TopicmapEditorModel.removeNodeDetail(): detail \"" + key + "\" not found in hashtable");
		}
	}

	// ---

	/**
	 * Initializes the geometry of the specified topic and adds it to the
	 * specified viewmode of this editor.
	 * <p>
	 * Presumption: the type is already present.
	 * <p>
	 * Part of processing {@link #DIRECTIVE_SHOW_TOPIC} and
	 * {@link #DIRECTIVE_SHOW_TOPICS} (called repeatedly).
	 * <p>
	 * References checked: 30.3.2003 (2.0a18-pre8)
	 *
	 * @return	<code>true</code> if the geometry has been initialized,
	 *			<code>false</code> if the topic already have a geometry.
	 *
	 * @see		PresentationService#showTopic
	 */
	boolean showTopic(PresentationTopic topic) throws DeepaMehtaException {
		boolean inited = topicMap.initGeometry(topic);
		if (!topicMap.topicExists(topic.getID())) {
			topicMap.addTopic(topic);	// throws DME
			// Note: topicExists() and addTopic() are defined in BaseTopicMap
		}
		return inited;
	}

	/**
	 * Adds the specified association to the specified viewmode of this editor.
	 * <p>
	 * Part of processing {@link #DIRECTIVE_SHOW_ASSOCIATION} and
	 * {@link #DIRECTIVE_SHOW_ASSOCIATIONS} (called repeatedly).
	 * <p>
	 * References checked: 1.4.2003 (2.0a18-pre8)
	 *
	 * @param	assoc	The association to add (the associated topics are not yet set)
	 *
	 * @see		PresentationService#showAssociation
	 */
	void showAssociation(PresentationAssociation assoc) throws DeepaMehtaException {
		// ### only if not already exists
		if (!topicMap.associationExists(assoc.getID())) {
			topicMap.addAssociation(assoc);		// throws DME
			assoc.registerTopics(topicMap);		// ### must collect and register later? ### only if assoc was not already in topicmap
			// Note: associationExists() and addAssociation() are defined in BaseTopicMap
		}
	}

	// ---

	/**
	 * References checked: 26.4.2003 (2.0a18-pre10)
	 *
	 * @see		PresentationService#hideTopic
	 */
	void hideTopic(String topicID) {
		topicMap.deleteTopic(topicID);		// Note: deleteTopic() is from BaseTopicMap, does NOT throw DME
		hideDetails(topicID);
		removeNodeSelection(topicID);
	}

	/**
	 * References checked: 13.5.2002 (2.0a15-pre1)
	 *
	 * @throws	DeepaMehtaException		if the specified association did not exist
	 *									in the specified viewmode
	 *
	 * @see		PresentationService#hideAssociation
	 */
	void hideAssociation(String assocID) throws DeepaMehtaException {
		topicMap.deleteAssociation(assocID);	// Note: deleteAssociation() is overridden in PresentationTopicMap, throws DME
		hideDetails(assocID);
		removeEdgeSelection(assocID);
	}

	// ---

	/**
	 * @see		#hideTopic
	 * @see		#hideAssociation
	 */
	private void hideDetails(String topicID) {
		Enumeration e = details.keys();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			String id = DeepaMehtaUtils.explode(key)[0];
			if (id.equals(topicID)) {
				// close detail window
				getDetail(key).getWindow().dispose();
				System.out.println(">>> detail \"" + key + "\" closed (aborted)");
				// remove detail from model
				removeNodeDetail(key);
				// ### actually not part of presentation logic
				// getDetail(key).closeWindow();
				// Note: closeWindow() acts as user had clicked the close-button
			}
		}
	}

	// ---

	/**
	 * Updates the model to reflect the specified topic is selected now.
	 * <p>
	 * References checked: 8.5.2002 (2.0a15-pre1)
	 *
	 * @see		TopicmapEditorModel#selectTopic
	 */
	void selectTopic(String topicID) {
		PresentationTopic topic = getTopic(topicID);
		// error check
		if (topic == null) {
			System.out.println("*** TopicmapEditorModel.selectTopic(): topic \"" + topicID + "\" " +
			   "not found -- topic not selected");
			return;
		}
		//
		selection.mode = SELECTED_TOPIC;
		selection.topic = topic;
		selection.assoc = null;
	}

	/**
	 * Updates the model to reflect the specified assoc is selected now.
	 * <p>
	 * References checked: 8.5.2002 (2.0a15-pre1)
	 *
	 * @see		TopicmapEditorModel#selectAssociation
	 */
	void selectAssociation(String assocID) {
		PresentationAssociation assoc = getAssociation(assocID);
		// error check
		if (assoc == null) {
			System.out.println("*** TopicmapEditorModel.selectEdge(): assoc \"" + assocID + "\" " +
			   "not found -- assoc not selected");
			return;
		}
		//
		selection.mode = SELECTED_ASSOCIATION;
		selection.assoc = assoc;
		selection.topic = null;
	}

	/**
	 * Updates the model to reflect this graph is selected now.
	 * <p>
	 * References checked: 10.6.2002 (2.0a15-pre7)
	 *
	 * @see		TopicmapEditorModel#selectTopicMap
	 */
	void selectTopicmap() {
		selection.mode = SELECTED_TOPICMAP;
		selection.topic = null;
		selection.assoc = null;
	}

	private void selectNone() {
		selection.mode = SELECTED_NONE;
		selection.topic = null;
		selection.assoc = null;
	}

	// ---

	/**
	 * If the selected topic is the specified topic the model is updated to reflect nothing is
	 * selected now.
	 * <p>
	 * References checked: 10.6.2002 (2.0a15-pre7)
	 *
	 * @see		TopicmapEditorModel#removeSelection(String viewmode)
	 */
	void removeNodeSelection(String topicID) {
		if (isSelectedNode(topicID)) {
			selectNone();
		}
	}

	/**
	 * If the selected topic is the specified topic the model is updated to reflect nothing is
	 * selected now.
	 * <p>
	 * References checked: 10.6.2002 (2.0a15-pre7)
	 *
	 * @see		TopicmapEditorModel#removeSelection(String viewmode)
	 */
	void removeEdgeSelection(String assocID) {
		if (isSelectedEdge(assocID)) {
			selectNone();
		}
	}

	// ---

	private boolean isSelectedNode(String topicID) {
		return selection.mode == SELECTED_TOPIC && selection.topic.getID().equals(topicID);
	}

	private boolean isSelectedEdge(String assocID) {
		return selection.mode == SELECTED_ASSOCIATION && selection.assoc.getID().equals(assocID);
	}

	// ---

	/**
	 * References checked: 7.4.2007 (2.0b8)
	 *
	 * @see		TopicmapEditorModel#TopicmapEditorModel
	 * @see		PresentationService#updateBounds
	 * @see		PresentationService#processDirectives	4x
	 */
	void setBounds() {
		DeepaMehtaUtils.initBounds(topicMap, bounds);
	}

	void addDetailListeners(JInternalFrame detailWindow) {
		// internal frame listener
		detailWindow.addInternalFrameListener(new InternalFrameAdapter() {
			public void internalFrameClosed(InternalFrameEvent event) {
				JInternalFrame detailWindow = (JInternalFrame) event.getSource();
				String key = detailWindow.getName();
				System.out.print(">>> detail \"" + key + "\" closed ");
				updateDetail(key);
				controler.detailWindowClosed(key);
			}
		});
		// component listener
		detailWindow.addComponentListener(new ComponentAdapter() {
			public void componentMoved(ComponentEvent evt) {
				controler.repaint();	// update guides
			}
			public void componentResized(ComponentEvent evt) {
				controler.repaint();	// update guides
			}
		});
	}



	// ***********************
	// *** Private Methods ***
	// ***********************



	/**
	 * @see		#addDetailListeners
	 */
	private void updateDetail(String key) {
		PresentationDetail detail = getDetail(key);
		if (detail.isDirty()) {
			System.out.println("(dirty)");
			detail.updateModel();
			controler.beginLongTask();
			String id = DeepaMehtaUtils.explode(key)[0];
			int detailType = detail.getType();
			if (detailType == DETAIL_TOPIC) {
				controler.processNodeDetail(topicMap, id, detail);
			} else if (detailType == DETAIL_ASSOCIATION) {
				controler.processEdgeDetail(topicMap, id, detail);
			} else if (detailType == DETAIL_TOPICMAP) {
				// ### ?
			} else {
				// ### error
			}
			controler.endTask();
		} else {
			System.out.println("(not dirty)");
		}
	}

	// ---

	private void initAssociations(PresentationTopicMap topicmap) {
		Enumeration assocs = topicmap.getAssociations().elements();
		while (assocs.hasMoreElements()) {
			PresentationAssociation assoc = (PresentationAssociation) assocs.nextElement();
			assoc.registerTopics(topicmap);		// ### only if assoc was not already in topicmap
		}
	}
}
