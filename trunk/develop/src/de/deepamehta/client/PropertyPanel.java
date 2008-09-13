package de.deepamehta.client;

import de.deepamehta.Association;
import de.deepamehta.BaseAssociation;
import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.PropertyDefinition;
import de.deepamehta.Topic;
import de.deepamehta.Type;
import de.deepamehta.util.DeepaMehtaUtils;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.logging.Level;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.JTextComponent;



/**
 * The <i>view</i> that is shown at the right side of the graphical DeepaMehta client.
 * The property panel shows the properties of the selected topic/association/topicmap and allows the user to edit them.
 * The <i>controller</i> is passed to the <code>PropertyPanel</code> {@link #PropertyPanel constructor}.
 * <p>
 * This is a singleton, there is only one instance created.
 * <hr>
 * Last change: 8.9.2008 (2.0b8)<br>
 * J&ouml;rg Richter<br>
 * jri@deepamehta.de
 */
class PropertyPanel extends JPanel implements ActionListener, ItemListener, DocumentListener,
											  HyperlinkListener, DeepaMehtaConstants {



	// *****************
	// *** Constants ***
	// *****************



	// preferences
	// private static final String REVEAL_ALL_BUTTON_LABEL = "Reveal all";		// ### not yet functional
	// private static final String REVEAL_ALL_BUTTON_COMMAND = "revealAll";		// ### not yet functional

	// Accessed by
	//		- PresentationPropertyDefinition.createTextField()
	static final String CMD_TOPIC_DATA_CHANGED_MENU = "topicDataChangedMenu";
	static final String CMD_TOPIC_DATA_CHANGED_BUTTONS = "topicDataChangedButtons";
	static final String CMD_TOPIC_DATA_CHANGED_CHECKBOX = "topicDataChangedCheckbox";

	// selection modes
	private static final String SELECTED_NONE  = "none";
	private static final String SELECTED_TOPIC = "topic";
	private static final String SELECTED_ASSOCIATION = "assoc";
	private static final String SELECTED_TOPICMAP = "topicmap";



	// **************
	// *** Fields ***
	// **************



	private PropertyPanelControler controler;

	private static Logger logger = Logger.getLogger("de.deepamehta");

	/**
	 * Key: &lt;topicmap ID>:&lt;viewmode><br>
	 * Value: {@link PropertyPanel.Selection}
	 */
	private Hashtable selections = new Hashtable();

	// --- Selection Model ---

	// Note: both variables are updated together

	/**
	 * The key of the current selection (format is &lt;topicmap ID>:&lt;viewmode>) or
	 * <code>null</code> if there is currently no selection.
	 * <p>
	 * Updated in
	 * {@link #setSelection}
	 * {@link #restoreSelection}
	 * {@link #clearSelection}
	 */
	private String currentSelection;

	/**
	 * Current selection mode. See the 4 {@link #SELECTED_NONE SELECTED_XXX constants}
	 * <p>
	 * Note: there are 2 situations the property is empty (SELECTED_NONE)
	 * <ol>
	 * <li>a view is opened
	 * <li>the selected topic/association is removed
	 * </ol>
	 * <p>
	 * Updated in
	 * {@link #setSelection}
	 * {@link #restoreSelection}
	 * {@link #clearSelection}
	 */
	private String selected = SELECTED_NONE;	// ### compare to GraphPanel

	// ---

	/**
	 * This flag indicates wheather the property values in the property panel are dirty
	 * (means: changed, but not yet saved).
	 */
	private boolean propertiesAreDirty;

	// ---
	
	/**
	 * Key: Topic Type ID (<code>String</code>)<br>
	 * Value: PresentationType
	 */
	private Hashtable topicTypes;
	private Vector topicTypesV;

	/**
	 * Key: Association Type ID (<code>String</code>)<br>
	 * Value: PresentationType
	 */
	private Hashtable assocTypes;
	private Vector assocTypesV;

	// ---

	/**
	 * A hashtable containing the property forms for every type.
	 * A property form consists of single property fields (ordered).
	 * Note: also hidden property fields are contained in the vector ### required?
	 * <p>
	 * Key: type ID (<code>String</code>)<br>
	 * Value: vector of <code>PropertyField</code> objects
	 * <p>
	 * Building: {@link #buildPropertyFields}<br>
	 * Accessing: {@link #getPropertyFields}
	 */
	private Hashtable propertyForms = new Hashtable();

	/**
	 * Key: type ID (<code>String</code>)<br>
	 * Value: <code>JPanel</code>
	 */
	private Hashtable propertyPanels = new Hashtable();

	// --- GUI Components ---

	private JComboBox topicTypeChoice;
	private JComboBox assocTypeChoice;

	/**
	 * The <code>JPanel</code> deployed for the topic characterstics resp. association
	 * characterstics.
	 * This panel uses the {@link #characteristicsCardLayout} object as its layout manager.
	 */
	private JPanel characteristicsCardPanel = new JPanel();
	private CardLayout characteristicsCardLayout = new CardLayout();

	/**
	 * The <code>JPanel</code> deployed for the property panel.
	 * This panel uses the {@link #dataCardLayout} object as its layout manager.
	 */
	private JPanel dataCardPanel = new JPanel();
	private CardLayout dataCardLayout = new CardLayout();



	// *******************
	// *** Constructor ***
	// *******************



	/**
	 * References checked: 6.9.2008 (2.0b8)
	 *
	 * @see		PresentationService#createMainGUI
	 */
	PropertyPanel(PropertyPanelControler controler) {
		try {
			this.controler = controler;
			//
			this.topicTypeChoice = new JComboBox();
			this.assocTypeChoice = new JComboBox();
			//
			dataCardPanel.setLayout(dataCardLayout);
			dataCardPanel.add(new JPanel(), SELECTED_NONE);
			topicTypeChoice.setEditable(true);
			topicTypeChoice.setMaximumRowCount(32);
			assocTypeChoice.setEditable(true);
			assocTypeChoice.setMaximumRowCount(32);
			// --- "Selected Topic" panel ---
			JPanel panel = new JPanel();
			panel.setBackground(COLOR_PROPERTY_PANEL);
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			panel.add(new JLabel(controler.string(LABEL_TOPIC_TYPE)));
			panel.add(topicTypeChoice);
			// --- "Selected Association" panel ---
			JPanel panel2 = new JPanel();
			panel2.setBackground(COLOR_PROPERTY_PANEL);
			panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));
			panel2.add(new JLabel(controler.string(LABEL_ASSOC_TYPE)));
			panel2.add(assocTypeChoice);
			//
			characteristicsCardPanel.setLayout(characteristicsCardLayout);
			characteristicsCardPanel.add(new JPanel(), SELECTED_NONE);
			characteristicsCardPanel.add(panel, SELECTED_TOPIC);
			characteristicsCardPanel.add(panel2, SELECTED_ASSOCIATION);
			// Note: for SELECTED_TOPICMAP the SELECTED_TOPIC panel is used also
			//
			JPanel activePanel = new JPanel();
			activePanel.setLayout(new BoxLayout(activePanel, BoxLayout.Y_AXIS));
			activePanel.add(characteristicsCardPanel);
			// build
			setLayout(new BorderLayout());
			add(activePanel, BorderLayout.NORTH);
			add(dataCardPanel);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error constructing property panel ...", e);
		}
	}



	// *****************************************************************
	// *** Implementation of interface java.awt.event.ActionListener ***
	// *****************************************************************



	/**
	 * Called when one of this events occurred:
	 * <ul>
	 * <li>The value of a "Options Menu" / "Option Button" / "Switch" property has been changed
	 * <li>Return has been pressed in an (single line) input field of the property panel
	 * <li>A property bound action button has been pressed
	 * <li>The "Reveal all" button has been pressed
	 * </ul>
	 */
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		StringTokenizer st = new StringTokenizer(command, COMMAND_SEPARATOR);
		String cmd = st.nextToken();
		//
		if (cmd.equals(CMD_TOPIC_DATA_CHANGED_MENU)) {
			setPropertiesDirty("menu");
			storeProperties();
		} else if (cmd.equals(CMD_TOPIC_DATA_CHANGED_BUTTONS)) {
			setPropertiesDirty("radiobuttons");
			storeProperties();
		} else if (cmd.equals(CMD_TOPIC_DATA_CHANGED_CHECKBOX)) {
			setPropertiesDirty("checkbox");
			storeProperties();
		} else {
			storeProperties();
			executeCommand(command);
		}
	}



	// ***************************************************************
	// *** Implementation of interface java.awt.event.ItemListener ***
	// ***************************************************************



	/**
	 * ### should rely on action command<br>
	 */
	public void itemStateChanged(ItemEvent e) {
		Object src = e.getSource();
		boolean state = e.getStateChange() == ItemEvent.SELECTED;
		PresentationTopicMap topicmap = getTopicmap();
		// --- topic type selection ---
		if (src == topicTypeChoice) {
			if (state) {
				controler.beginLongTask();
				String typename = (String) topicTypeChoice.getSelectedItem();
				String command = CMD_CHANGE_TOPIC_TYPE_BY_NAME + COMMAND_SEPARATOR + typename;
				controler.executeTopicCommand(topicmap, getTopic(), command);
				controler.endTask();
			}
		// --- association type selection ---
		} else if (src == assocTypeChoice) {
			if (state) {
				controler.beginLongTask();
				String typename = (String) assocTypeChoice.getSelectedItem();
				String command = CMD_CHANGE_ASSOC_TYPE_BY_NAME + COMMAND_SEPARATOR + typename;
				controler.executeAssocCommand(topicmap, getAssociation(), command);
				controler.endTask();
			}
		} else {
			throw new DeepaMehtaException("unexpected item event: " + e);
		}
	}



	// **********************************************************************
	// *** Implementation of interface javax.swing.event.DocumentListener ***
	// **********************************************************************



	public void changedUpdate(DocumentEvent e) {
		setPropertiesDirty("style change");
	}

	public void insertUpdate(DocumentEvent e) {
		setPropertiesDirty("typing");
	}

	public void removeUpdate(DocumentEvent e) {	
		setPropertiesDirty("deleting");
	}



	// ***********************************************************************
	// *** Implementation of interface javax.swing.event.HyperlinkListener ***
	// ***********************************************************************



	public void hyperlinkUpdate(HyperlinkEvent e) {
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			String command = CMD_FOLLOW_HYPERLINK + COMMAND_SEPARATOR + e.getURL();
			executeCommand(command);
		}
	}



	// ***************
	// *** Methods ***
	// ***************



	public Dimension getMinimumSize() {
		return new Dimension(0, 0);
	}

	/**
	 * @see		PresentationService#createGUI
	 */
	void init() {
		this.topicTypes = controler.getTopicTypes();
		this.topicTypesV = controler.getTopicTypesV();
		this.assocTypes = controler.getAssociationTypes();
		this.assocTypesV = controler.getAssociationTypesV();
	}

	// ---

	/**
	 * @see		#checkPropertyFields
	 * @see		PresentationService#updateTopicType
	 * @see		PresentationService#updateAssociationType
	 */
	void buildPropertyForm(Type type) {
		buildPropertyFields(type);
		buildPropertyPanel(type.getID());
	}

	// ---

	/**
	 * Called from PresentationService when processing {@link #DIRECTIVE_SELECT_TOPIC}.
	 *
	 * @see		PresentationService#selectTopic
	 */
	void topicSelected(BaseTopic topic, PresentationTopicMap topicmap, String viewmode,
							Hashtable props, Hashtable baseURLs, Vector disabledProperties, boolean retypeIsAllowed) {
		setSelection(topic, topicmap, viewmode, props, baseURLs, disabledProperties, retypeIsAllowed);
		updateTopic(topic);
	}

	/**
	 * Called from PresentationService when processing {@link #DIRECTIVE_SELECT_ASSOCIATION}.
	 *
	 * @see		PresentationService#selectAssociation
	 */
	void assocSelected(BaseAssociation assoc, PresentationTopicMap topicmap, String viewmode,
							Hashtable props, Hashtable baseURLs, Vector disabledProperties, boolean retypeIsAllowed) {
		setSelection(assoc, topicmap, viewmode, props, baseURLs, disabledProperties, retypeIsAllowed);
		updateAssoc(assoc);
	}

	/**
	 * Called from PresentationService when processing {@link #DIRECTIVE_SELECT_TOPICMAP}.
	 *
	 * @see		PresentationService#selectTopicMap
	 */
	void topicmapSelected(PresentationTopicMap topicmap, String viewmode,
									Hashtable props, Hashtable baseURLs, Vector disabledProperties) {
		setSelection(topicmap, viewmode, props, baseURLs, disabledProperties);
		updateTopic(topicmap.getEditor().getTopicmap());
	}

	// ---

	/**
	 * References checked: 28.5.2002 (2.0a15-pre4)
	 *
	 * @see		PresentationService#selectTopicmap(String topicmapID)
	 */
	void update(String topicmapID, String viewmode) {
		// update model
		if (!restoreSelection(topicmapID, viewmode)) {
			updateEmpty();	// specified view has no selection
			return;
		}
		// update view
		if (selected == SELECTED_TOPIC) {
			updateTopic(getTopic());
		} else if (selected == SELECTED_ASSOCIATION) {
			updateAssoc(getAssociation());
		} else if (selected == SELECTED_TOPICMAP) {
			updateTopic(getTopicmap().getEditor().getTopicmap());
		} else {
			throw new DeepaMehtaException("unexpected selection mode: " + selected);
		}
	}

	// ---

	/**
	 * Removes the activition if the currently activated topic matches the
	 * specified topic.
	 *
	 * @see		#removeTopicSelection(Vector topicIDs, String topicmapID, String viewmode)
	 * @see		PresentationService#hideTopic
	 */
	void removeTopicSelection(String topicID) {
		if (isSelectedTopic(topicID)) {
			removeSelection();
		}
	}

	/**
	 * Removes the activition if the currently activated association matches the
	 * specified association.
	 *
	 * @see		#removeAssociationSelection(Vector assocIDs, String topicmapID, String viewmode)
	 * @see		PresentationService#hideAssociation
	 */
	void removeAssociationSelection(String assocID) {
		if (isSelectedAssociation(assocID)) {
			removeSelection();
		}
	}

	// ---

	/**
	 * Returns the selected topic / association / topicmap resp. <code>null</code> if there is no selection.
	 */
	private Object getSelectedObject() {
		if (selected.equals(SELECTED_TOPIC)) {
			return getTopic();
		} else if (selected.equals(SELECTED_ASSOCIATION)) {
			return getAssociation();
		} else if (selected.equals(SELECTED_TOPICMAP)) {
			return getTopicmap();
		} else if (selected.equals(SELECTED_NONE)) {
			return null;
		} else {
			throw new DeepaMehtaException("unexpected selection mode: \"" + selected + "\"");
		}
	}

	/**
	 * Removes the selection.
	 *
	 * @see		#removeTopicSelection
	 * @see		#removeAssociationSelection
	 */
	private void removeSelection() {
		selections.remove(currentSelection);
		//
		clearSelection();	// update model
		updateEmpty();		// update view
	}

	// --- Getting info about current selection (8 methods) ---

	// Presumption: there is a selection

	// ### should return PresentationTopic
	BaseTopic getTopic() {
		return getSelection().topic;
	}

	// ### should return PresentationAssociation
	BaseAssociation getAssociation() {
		return getSelection().assoc;
	}

	PresentationTopicMap getTopicmap() {
		return getSelection().topicmap;
	}

	Hashtable getProperties() {
		return getSelection().props;
	}

	Hashtable getBaseURLs() {
		return getSelection().baseURLs;
	}

	Vector getDisabledProperties() {
		return getSelection().disabledProps;
	}

	boolean getRetypeAllowed() {
		return getSelection().retypeIsAllowed;
	}

	private String getSelected() {
		return getSelection().selected;
	}

	// ---

	/**
	 * Convenience method to access current Selection.
	 * <p>
	 * Presumption: there is a selection.
	 */
	private Selection getSelection() {
		return getSelection(currentSelection);
	}

	/**
	 * Gets the selection with the specified key (format is &lt;topicmap ID>:&lt;viewmode>).
	 */
	private Selection getSelection(String selection) {
		return (Selection) selections.get(selection);
	}

	// --- setSelection (3 forms) ---

	/**
	 * Updates the model to reflect the specified topic is selected now.
	 */
	private void setSelection(BaseTopic topic, PresentationTopicMap topicmap,
					String viewmode, Hashtable props, Hashtable baseURLs, Vector disabledProperties, boolean retypeIsAllowed) {
		Selection selection = new Selection(topicmap, topic, props, baseURLs, disabledProperties, retypeIsAllowed);
		currentSelection = topicmap.getID() + ":" + viewmode;
		selections.put(currentSelection, selection);
		selected = SELECTED_TOPIC;
	}

	/**
	 * Updates the model to reflect the specified association is selected now.
	 */
	private void setSelection(BaseAssociation assoc, PresentationTopicMap topicmap,
					String viewmode, Hashtable props, Hashtable baseURLs, Vector disabledProperties, boolean retypeIsAllowed) {
		Selection selection = new Selection(topicmap, assoc, props, baseURLs, disabledProperties, retypeIsAllowed);
		currentSelection = topicmap.getID() + ":" + viewmode;
		selections.put(currentSelection, selection);
		selected = SELECTED_ASSOCIATION;
	}

	/**
	 * Updates the model to reflect the specified topicmap is selected now.
	 */
	private void setSelection(PresentationTopicMap topicmap, String viewmode,
					Hashtable props, Hashtable baseURLs, Vector disabledProperties) {
		Selection selection = new Selection(topicmap, props, baseURLs, disabledProperties);
		currentSelection = topicmap.getID() + ":" + viewmode;
		selections.put(currentSelection, selection);
		selected = SELECTED_TOPICMAP;
	}

	// ---

	/**
	 * Updates selection model.
	 */
	private void setSelectionProperties(Hashtable props, boolean all) {
		Selection selection = getSelection();
		if (all) {
			selection.props = props;
		} else {
			Hashtable selProps = selection.props;
			Enumeration e = props.keys();
			while (e.hasMoreElements()) {
				String propName = (String) e.nextElement();
				String propValue = (String) props.get(propName);
				selProps.put(propName, propValue);
			}
		}
	}

	// ---

	/**
	 * @see		TopicmapEditorModel#focusName
	 */
	void focusTopicNameField() {
		// ### topicNameField.requestFocus();
	}

	/**
	 * @see		TopicmapEditorModel#focusType
	 */
	void focusTopicTypeChoice() {
		topicTypeChoice.getEditor().selectAll();	// ### requestFocus();
	}

	/**
	 * @see		TopicmapEditorModel#focusType
	 */
	void focusAssociationTypeChoice() {
		assocTypeChoice.getEditor().selectAll();	// ### requestFocus();
	}

	/**
	 * ### param not yet used, instead the first property is fosused
	 *
	 * @see		TopicmapEditorModel#focusType
	 */
	void focusProperty(/* ### String prop */) {
		Vector propertyFields = getPropertyFields();
		Enumeration e = propertyFields.elements();
		while (e.hasMoreElements()) {
			PropertyField propField = (PropertyField) e.nextElement();
			if (!propField.isHidden()) {
				propField.focus();
				return;
			}
		}
		logger.warning("there is no property to focus");
	}

	// ---

	/**
	 * Stores changed properties (actually the {@link #controler} is informed).
	 * <p>
	 * The properties are stored only, if marked dirty (see {@link #propertiesAreDirty}).
	 * After storing the resp. flag is cleared.
	 *
	 * References checked: 6.9.2008 (2.0b8)
	 *
	 * @see		#actionPerformed	4x
	 * @see		PresentationService#storeProperties
	 */
	void storeProperties() {
		if (propertiesAreDirty) {
			Hashtable props = getPanelProperties();
			if (selected == SELECTED_TOPIC) {
				controler.changeTopicData(getTopicmap(), getTopic(), props);
			} else if (selected == SELECTED_ASSOCIATION) {
				controler.changeAssocData(getTopicmap(), getAssociation(), props);
			} else if (selected == SELECTED_TOPICMAP) {
				controler.changeTopicData(getTopicmap(), getTopicmap().getEditor().getTopicmap(), props);
			} else {
				throw new DeepaMehtaException("properties are marked dirty but nothing is selected");
			}
			setSelectionProperties(props, true);
			propertiesAreDirty = false;
		}
		//
		saveScrollbarValues();
	}

	/**
	 * @see		#actionPerformed
	 * @see		#hyperlinkUpdate
	 */
	private void executeCommand(String command) {
		if (selected == SELECTED_TOPIC) {
			controler.executeTopicCommand(getTopicmap(), getTopic(), command);
		} else if (selected == SELECTED_ASSOCIATION) {
			controler.executeAssocCommand(getTopicmap(), getAssociation(), command);
		} else if (selected == SELECTED_TOPICMAP) {
			controler.executeTopicCommand(getTopicmap(), getTopicmap().getEditor().getTopicmap(), command);
		} else {
			throw new DeepaMehtaException("unexpected selection mode: \"" + selected + "\"");
		}
	}

	// ---

	/**
	 * @see		PresentationService#showTopicProperties
	 */
	void showTopicProperties(String topicID, Hashtable properties, Hashtable baseURLs) {
		if (selected.equals(SELECTED_TOPIC)) {
			PresentationTopicMap topicmap = getTopicmap();
			if (topicmap.topicExists(topicID)) {
				showPropertiesConditionally(properties, baseURLs, topicmap.getTopic(topicID));
			}
		} else if (selected.equals(SELECTED_TOPICMAP)) {
			// Note: the topic could be the topicmap itself
			PresentationTopicMap topicmap = getTopicmap();
			if (topicmap.getID().equals(topicID)) {
				showPropertiesConditionally(properties, baseURLs, topicmap);
			}
		}
	}

	/**
	 * @see		PresentationService#showAssociationProperties
	 */
	void showAssociationProperties(String assocID, Hashtable properties, Hashtable baseURLs) {
		if (selected.equals(SELECTED_ASSOCIATION)) {
			PresentationTopicMap topicmap = getTopicmap();	// ### getEditor(topicmapID).getTopicmap(viewmode);
			if (topicmap.associationExists(assocID)) {
				showPropertiesConditionally(properties, baseURLs, topicmap.getAssociation(assocID));
			}
		}
	}

	// --- showPropertiesConditionally (3 forms) ---

	/**
	 * @see		#showTopicProperties
	 */
	private void showPropertiesConditionally(Hashtable properties, Hashtable baseURLs, Topic topic) {
		if (isSelectedTopic(topic.getID())) {
			showProperties(properties, baseURLs, topic.getType(), false);
			setSelectionProperties(properties, false);
		} else {
			logger.info("topic " + topic + " isn't currently selected -- no need to show properties now");
		}
	}

	/**
	 * @see		#showAssociationProperties
	 */
	private void showPropertiesConditionally(Hashtable properties, Hashtable baseURLs, Association assoc) {
		if (isSelectedAssociation(assoc.getID())) {
			showProperties(properties, baseURLs, assoc.getType(), false);
			setSelectionProperties(properties, false);
		} else {
			logger.info("association " + assoc + " isn't currently selected -- no need to show properties now");
		}
	}

	/**
	 * @see		#showTopicProperties
	 */
	private void showPropertiesConditionally(Hashtable properties, Hashtable baseURLs, PresentationTopicMap topicmap) {
		if (isSelectedTopicmap(topicmap.getID())) {
			showProperties(properties, baseURLs, topicmap.getEditor().getTopicmap().getType(), false);
			setSelectionProperties(properties, false);
		} else {
			logger.info("topicmap " + topicmap + " isn't currently selected -- no need to show properties now");
		}
	}

	// ---

	/**
	 * References checked: 1.6.2002 (2.0a15-pre5)
	 *
	 * @see		PresentationService#addTopicType
	 */
	void addTopicTypeToChoice(Type type) {
		topicTypeChoice.removeItemListener(this);
		topicTypeChoice.addItem(type.getName());
		topicTypeChoice.addItemListener(this);
		//
		// topicTypeChoice.addItem(type.menuItem());	// ### doesn't work
	}

	/**
	 * References checked: 1.6.2002 (2.0a15-pre5)
	 *
	 * @see		PresentationService#addAssociationType
	 */
	void addAssociationTypeToChoice(Type type) {
		assocTypeChoice.removeItemListener(this);
		assocTypeChoice.addItem(type.getName());
		assocTypeChoice.addItemListener(this);
		//
		// assocTypeChoice.addItem(type.menuItem());	// ### doesn't work
	}



	// ***********************
	// *** Private Methods ***
	// ***********************



	// --- ### change method names

	/**
	 * @see		#topicSelected
	 * @see		#topicmapSelected
	 * @see		#update
	 */
	private void updateTopic(BaseTopic topic) {
		selectCharacteristicsPanel(SELECTED_TOPIC);
		showProperties();
		//
		updateTopicTypeChoice(topic.getType(), getRetypeAllowed());
	}

	/**
	 * @see		#assocSelected
	 * @see		#update
	 */
	private void updateAssoc(BaseAssociation assoc) {
		selectCharacteristicsPanel(SELECTED_ASSOCIATION);
		showProperties();
		//
		updateAssocTypeChoice(assoc.getType(), getRetypeAllowed());
	}

	// --- showProperties (2 forms) ---

	/**
	 * Depending on the selected topic/association shows the corresponding properties
	 * form and fills in the property values.
	 *
	 * @see		#updateTopic
	 * @see		#updateAssoc
	 */
	private void showProperties() {
		if (selected == SELECTED_TOPIC) {
			String typeID = getTopic().getType();
			checkPropertyFields(typeID);
			selectDataPanel(typeID);
			showProperties(getProperties(), getBaseURLs(), typeID, true);	// all=true
			disableProperties();
		} else if (selected == SELECTED_ASSOCIATION) {
			String typeID = getAssociation().getType();
			checkPropertyFields(typeID);
			selectDataPanel(typeID);
			showProperties(getProperties(), getBaseURLs(), typeID, true);	// all=true
			disableProperties();
		} else if (selected == SELECTED_TOPICMAP) {
			String typeID = getTopicmap().getEditor().getTopicmap().getType();
			checkPropertyFields(typeID);
			selectDataPanel(typeID);
			showProperties(getProperties(), getBaseURLs(), typeID, true);	// all=true
			disableProperties();
		} else {
			throw new DeepaMehtaException("unexpected selection mode: \"" + selected + "\"");
		}
	}

	/**
	 * Shows the specified properties.
	 * <p>
	 * The property fields to fill are determined by the specified typeID.
	 * <p>
	 * Presumption: the topic the properties are bound to is currently selected
	 * (otherwise there is no need to call this method because there will be no visual result ###).
	 *
	 * @param	all		if not set, only the <i>passed</i> properties are set, not
	 *					<i>every</i> property according to the type definition, thus
	 *					the application programmers are not required to pass a full set
	 *					of properties when using the DIRECTIVE_SHOW_PROPERTIES ###
	 *
	 * @see		#showProperties					true
	 * @see		#showPropertiesConditionally	false
	 */
	private void showProperties(Hashtable properties, Hashtable baseURLs, String typeID, boolean all) {
		// error check 1
		if (properties == null) {
			logger.warning(typeID + " no topic data passed (null) -- properties not shown");
			return;
		}
		// --- get property fields ---
		Vector propertyFields = getPropertyFields(typeID);
		// error check 2 ### can't happen anymore
		if (propertyFields == null) {
			logger.warning("no property fields for \"" + typeID + "\" -- properties not shown");
			return;
		}
		// --- set passed property values into property fields ---
		// loop through all properties of type definition resp. loop through all passed properties
		Enumeration e = all ? propertyFields.elements() : properties.keys();
		while (e.hasMoreElements()) {
			String propName;
			PropertyField propField;
			//
			if (all) {
				propField = (PropertyField) e.nextElement();
				propName = propField.getName();
			} else {
				propName = (String) e.nextElement();
				propField = getPropertyField(propertyFields, propName);
			}
			String propValue = (String) properties.get(propName);
			// Note: propValue may be null if property was never set -- must transform to "" because
			// textEditor.setText(null) fails if the underlying document is an HTMLDocument
			if (propValue == null) {
				propValue = "";
			}
			try {
				String baseURL = (String) baseURLs.get(propName);
				propField.setText(propValue, baseURL, this, this);	// throws DME
			} catch (DeepaMehtaException dme) {
				logger.warning("\"" + propName + "\" value \"" + propValue + "\" can't be shown (" + dme.getMessage() + ")");
			}
		}
	}

	// ---

	/**
	 * @see		#showProperties
	 */
	private void disableProperties() {
		// --- get property fields ---
		Vector propertyFields = getPropertyFields();
		// error check
		if (propertyFields == null) {
			logger.warning("no property fields for \"" + getTopic().getType() + "\" -- properties not disbaled");
			return;
		}
		// ---
		// go through all fields of the type definition		
		Enumeration e = propertyFields.elements();
		while (e.hasMoreElements()) {
			PropertyField propField = (PropertyField) e.nextElement();
			String propName = propField.getName();
			boolean enabled = !getDisabledProperties().contains(propName);
			propField.setEnabled(enabled);
		}
	}

	// ---

	/**
	 * ### should be private
	 * <p>
	 * References checked: 1.11.2001 (2.0a13-pre3)
	 *
	 * @see		#removeSelection
	 * @see		#update
	 * @see		PresentationService#upperAreaChanged
	 * @see		PresentationService#lowerAreaChanged
	 */
	void updateEmpty() {
		selectCharacteristicsPanel(SELECTED_NONE);
		selectDataPanel(SELECTED_NONE);
	}

	// ---

	/**
	 * @see		#actionPerformed	3x
	 * @see		#changedUpdate
	 * @see		#insertUpdate
	 * @see		#removeUpdate
	 */
	private void setPropertiesDirty(String message) {
		if (!propertiesAreDirty) {
			propertiesAreDirty = true;
			logger.info("properties of " + getSelectedObject() + " changed (by " + message + ")");
		}
	}

	// ---

	/**
	 * Creates a vector of {@link PropertyField} objects for the specified
	 * type and stores it in the {@link #propertyForms} hashtable.
	 *
	 * @see		#buildPropertyForm
	 */
	private void buildPropertyFields(Type type) {
		Vector propertyFields = new Vector();
		// get all property definitions of the topic type
		Enumeration e = type.getTypeDefinition().elements();
		while (e.hasMoreElements()) {
			PresentationPropertyDefinition propertyDef = (PresentationPropertyDefinition) e.nextElement();
			PropertyField propField = new PropertyField(propertyDef, this, this);
			propertyFields.addElement(propField);
		}
		//
		propertyForms.put(type.getID(), propertyFields);
	}

	/**
	 * Creates the property panel for the specified type and adds it to the {@link #dataCardPanel}.
	 *
	 * @param	type	 a topic type or association type topic
	 *
	 * @see		#buildPropertyForm
	 */
	private void buildPropertyPanel(String typeID) {
		// --- create property panel ---
		JPanel propertyPanel = getPropertyPanel(typeID);
		if (propertyPanel == null) {
			propertyPanel = new JPanel();
			propertyPanel.setBackground(COLOR_PROPERTY_PANEL);
			propertyPanel.setLayout(new BoxLayout(propertyPanel, BoxLayout.Y_AXIS));
			propertyPanels.put(typeID, propertyPanel);
			//
			dataCardPanel.add(propertyPanel, typeID);
		} else {
			propertyPanel.removeAll();
		}
		// --- add property fields ---
		Vector propertyFields = getPropertyFields(typeID);
		Enumeration e = propertyFields.elements();
		while (e.hasMoreElements()) {
			PropertyField propField = (PropertyField) e.nextElement();
			// add a field to the panel if it is not a hidden field
			if (!propField.isHidden()) {
				propertyPanel.add(new JLabel(propField.getLabel()));	// ### JLabel.LEFT alignment shows no result
				propertyPanel.add(propField.view);
			}
		}
		// ### invalidate();		// ### does it help?
	}

	// ---

	/**
	 * Returns the property values currently entered in the property panel.
	 * <p>
	 * Values of hidden properties are not returned.
	 * <p>
	 * References checked: 9.9.2008 (2.0b8)
	 *
	 * @return	the property values as a hashtable.<br>
	 *			Key: property name (<code>String</code>)<br>
	 *			Value: property value (<code>String</code>)
	 *
	 * @see		#storeProperties
	 */
	private Hashtable getPanelProperties() {
		Hashtable properties = new Hashtable();	// the result object
		//
		Enumeration e = getPropertyFields().elements();
		while (e.hasMoreElements()) {
			PropertyField propField = (PropertyField) e.nextElement();
			if (!propField.isHidden()) {
				properties.put(propField.getName(), propField.getText());
			}
		}
		//
		return properties;
	}

	// ---

	/**
	 * Selects the specified characteristics panel from the card layout.
	 *
	 * @param	cardID	{@link #SELECTED_NONE}, {@link #SELECTED_TOPIC} or {@link #SELECTED_ASSOCIATION}
	 *
	 * @see		#topicSelected
	 * @see		#assocSelected
	 * @see		#removeActivition
	 */
	private void selectCharacteristicsPanel(String cardID) {
		characteristicsCardLayout.show(characteristicsCardPanel, cardID);
	}

	/**
	 * @see		#removeActivition
	 * @see		#showProperties
	 */
	private void selectDataPanel(String cardID) {
		dataCardLayout.show(dataCardPanel, cardID);
	}

	// ---

	/**
	 * Selects the item from the topic type combo box that corresponds to the specified topic type ID.
	 *
	 * @see		#topicTypeChanged
	 * @see		#updateTopic
	 */
	private void updateTopicTypeChoice(String typeID, boolean retypeIsAllowed) {
		String typeName = topicTypeName(typeID);
		// error check ### ever happens?
		if (typeName == null) {
			logger.warning("type \"" + typeID + "\" is unknown -- topic type choice can't be updated");
			return;
		}
		//
		topicTypeChoice.setEnabled(retypeIsAllowed);
		//
		topicTypeChoice.removeItemListener(this);
		topicTypeChoice.setSelectedItem(typeName);
		topicTypeChoice.addItemListener(this);
	}

	/**
	 * Selects the item from the association type combo box that corresponds to the specified association type ID.
	 *
	 * @see		#assocTypeChanged
	 * @see		#updateAssoc
	 */
	private void updateAssocTypeChoice(String typeID, boolean retypeIsAllowed) {
		String typeName = assocTypeName(typeID);
		// error check ### ever happens?
		if (typeName == null) {
			logger.warning("type \"" + typeID + "\" is unknown -- association type choice can't be updated");
			return;
		}
		//
		assocTypeChoice.setEnabled(retypeIsAllowed);
		//
		assocTypeChoice.removeItemListener(this);
		assocTypeChoice.setSelectedItem(typeName);
		assocTypeChoice.addItemListener(this);
	}

	// ---

	/**
	 * @param	propName	### used for reporting only
	 *
	 * @see		#setText
	 */
	private void setSelectedCustomItem(JComboBox cbox, String text, ActionListener actionListener, String propName) {
		for (int i = 0; i < cbox.getItemCount(); i++) {
			if (((ComboBoxItem) cbox.getItemAt(i)).text.equals(text)) {
				cbox.removeActionListener(actionListener);
				cbox.setSelectedIndex(i);
				cbox.addActionListener(actionListener);
				return;
			}
		}
		if (text.equals("")) {
			logger.warning("property \"" + propName + "\" has no value -- displayed value might not be accurate");
		} else {
			logger.warning("property \"" + propName + "\" has unexpected value \"" + text +
				"\" -- displayed value might not be accurate");
		}
	}

	/**
	 * @see		PropertyField#setDate
	 * @see		PropertyField#setTime
	 */
	private void setSelectedItem(JComboBox cbox, String text, ActionListener actionListener) {
		cbox.removeActionListener(actionListener);
		cbox.setSelectedItem(text);
		cbox.addActionListener(actionListener);
	}

	// ---

	private JPanel getPropertyPanel(String typeID) {
		return (JPanel) propertyPanels.get(typeID);
	}

	// --- getPropertyField (2 forms) ---

	private PropertyField getPropertyField(String propName) {
		return getPropertyField(getPropertyFields(), propName);
	}

	private PropertyField getPropertyField(Vector propertyFields, String propName) {
		Enumeration e = propertyFields.elements();
		while (e.hasMoreElements()) {
			PropertyField field = (PropertyField) e.nextElement();
			if (field.getName().equals(propName)) {
				return field;
			}
		}
		//
		throw new DeepaMehtaException("property \"" + propName + "\" not found in " + propertyFields);
	}

	// --- getPropertyFields (4 forms) ---

	/**
	 * Returns the property fields for the current selection.
	 *
	 * @see		#focusProperty
	 * @see		#getProperties
	 */
	private Vector getPropertyFields() {
		if (selected.equals(SELECTED_TOPIC)) {
			return getPropertyFields(getTopic());
		} else if (selected.equals(SELECTED_ASSOCIATION)) {
			return getPropertyFields(getAssociation());
		} else if (selected.equals(SELECTED_TOPICMAP)) {
			return getPropertyFields(getTopicmap().getEditor().getTopicmap());
		} else if (selected.equals(SELECTED_NONE)) {
			throw new DeepaMehtaException("getPropertyFields(): nothing is selected");
		} else {
			throw new DeepaMehtaException("unexpected selection mode: \"" + selected + "\"");
		}
	}

	/**
	 * References checked: 3.8.2001 (2.0a11)
	 *
	 * @see		#disableProperties
	 */
	private Vector getPropertyFields(Topic topic) {
		return getPropertyFields(topic.getType());
	}

	private Vector getPropertyFields(Association assoc) {
		return getPropertyFields(assoc.getType());
	}

	/**
	 * Returns the property fields of the specified type.
	 * <p>
	 * References checked: 3.8.2001 (2.0a11)
	 *
	 * @see		#showProperties
	 */
	private Vector getPropertyFields(String typeID) {
		return (Vector) propertyForms.get(typeID);
	}

	// ---

	/**
	 * Checks weather the property form for the specified type does exist, if not it is build.
	 * <p>
	 * Presumption: the type is known by the presentation service.
	 *
	 * @see		#showProperties
	 */
	private void checkPropertyFields(String typeID) {
		if (getPropertyFields(typeID) == null) {
			logger.info("building property panel for type \"" + typeID + "\"");
			//
			if (selected == SELECTED_TOPIC || selected == SELECTED_TOPICMAP) {
				buildPropertyForm(topicType(typeID));
			} else if (selected == SELECTED_ASSOCIATION) {
				buildPropertyForm(assocType(typeID));
			} else {
				throw new DeepaMehtaException("unexpected selection mode: \"" + selected + "\"");
			}
		}
	}

	// ---

	/**
	 * @see		#update
	 */
	private boolean restoreSelection(String topicmapID, String viewmode) {
		String selection = topicmapID + ":" + viewmode;
		if (getSelection(selection) == null) {
			return false;
		}
		currentSelection = selection;
		selected = getSelected();
		return true;
	}

	/**
	 * Updates model to reflect there is no selection now.
	 *
	 * @see		#removeSelection
	 */
	private void clearSelection() {
		currentSelection = null;
		selected = SELECTED_NONE;
	}

	// ---

	private boolean isSelectedTopic(String topicID) {
		return selected == SELECTED_TOPIC && getTopic().getID().equals(topicID);
	}

	private boolean isSelectedAssociation(String assocID) {
		return selected == SELECTED_ASSOCIATION && getAssociation().getID().equals(assocID);
	}

	private boolean isSelectedTopicmap(String topicmapID) {
		return selected == SELECTED_TOPICMAP && getTopicmap().getID().equals(topicmapID);
	}

	// ---

	private void saveScrollbarValues() {
		Hashtable scrollbarValues = getScrollbarValues();
		Hashtable caretPositions = getCaretPositions();
		if (scrollbarValues != null) {	// Note: is null if there is no selection
			String logText = null;
			//
			Enumeration e = getPropertyFields().elements();
			while (e.hasMoreElements()) {
				PropertyField propField = (PropertyField) e.nextElement();
				JScrollBar scrollBar = propField.getScrollBar();
				JTextComponent textComponent = propField.getTextComponent();
				if (scrollBar != null) {
					String propName = propField.getName();
					int scrollbarValue = scrollBar.getValue();
					int caretPosition = textComponent.getCaretPosition();
					// save scrollbar value
					scrollbarValues.put(propName, new Integer(scrollbarValue));
					caretPositions.put(propName, new Integer(caretPosition));
					// logging
					if (logText == null) {
						logText = "saving scrollbar values of " + getSelectedObject();
					}
					logText += "\n    \"" + propName + "\": scrollbarValue=" + scrollbarValue + " caretPosition=" + caretPosition;
				}
			}
			// logging
			if (logText != null) {
				logger.fine(logText);
			}
		}
	}

	/**
	 * Returns the scrollbar values of the selected topic / association / topicmap resp. <code>null</code>
	 * if there is no selection.
	 */
	private Hashtable getScrollbarValues() {
		if (selected == SELECTED_TOPIC) {
			return ((PresentationTopic) getTopic()).getScrollbarValues();
		} else if (selected == SELECTED_ASSOCIATION) {
			return ((PresentationAssociation) getAssociation()).getScrollbarValues();
		} else if (selected == SELECTED_TOPICMAP) {
			return ((PresentationTopicMap) getTopicmap()).getScrollbarValues();
		} else if (selected == SELECTED_NONE) {
			return null;
		} else {
			throw new DeepaMehtaException("unexpected selection mode: " + selected);
		}
	}

	private Hashtable getCaretPositions() {
		if (selected == SELECTED_TOPIC) {
			return ((PresentationTopic) getTopic()).getCaretPositions();
		} else if (selected == SELECTED_ASSOCIATION) {
			return ((PresentationAssociation) getAssociation()).getCaretPositions();
		} else if (selected == SELECTED_TOPICMAP) {
			return ((PresentationTopicMap) getTopicmap()).getCaretPositions();
		} else if (selected == SELECTED_NONE) {
			return null;
		} else {
			throw new DeepaMehtaException("unexpected selection mode: " + selected);
		}
	}

	// ---

	/**
	 * Returns the topic type name that corresponds to a specified topic type id,
	 * e.g. id "tt-property" -> name "Property". If no topic type exists with the
	 * specified id, null is returned ### should throw instead.
	 *
	 * @return	the topic type name or null
	 *
	 * @see		#updateTopicTypeChoice
	 */
	private String topicTypeName(String typeID) {
		// ### there is a slightly modified copy in TopicMap
		Type type = (Type) topicTypes.get(typeID);
		return type != null ? type.getName() : null;
	}

	/**
	 * @see		#updateAssocTypeChoice
	 */
	private String assocTypeName(String typeID) {
		Type type = (Type) assocTypes.get(typeID);
		return type != null ? type.getName() : null;
	}

	// ---

	/**
	 * @see		#checkPropertyFields
	 */
	private Type topicType(String typeID) {
		Type type = (Type) topicTypes.get(typeID);
		if (type == null) {
			// ### Note: this is not an error
			return null;
		}
		return type;
	}

	/**
	 * @see		#checkPropertyFields
	 */
	private Type assocType(String typeID) {
		Type type = (Type) assocTypes.get(typeID);
		if (type == null) {
			// ### Note: this is not an error
			return null;
		}
		return type;
	}



	// *****************************
	// *** Private Inner Classes ***
	// *****************************



	/**
	 * A single property field as deployed by a property form.
	 * <p>
	 * <code>PropertyField</code> objects are created by {@link #createPropertyFields}
	 * and stored in the {@link #propertyForms} hashtable.
	 */
	private class PropertyField {



		// **************
		// *** Fields ***
		// **************



		PresentationPropertyDefinition propertyDef;

		/**
		 * The <i>View</i> of the <code>PropertyField</code>.
		 * <p>
		 * <table>
		 * <tr><th>Initialized by<th>Situation
		 * <tr><td>{@link PropertyPanel$PropertyField#PropertyField}<td>The constructor
		 *				calls {@link PresentationPropertyDefinition#createGUIComponent}
		 *				and sets the view to element [0] of the result
		 * <tr><th>Accessed by<th>Situation
		 * <tr><td>{@link #createPropertyForm}<td>The view component is added to a
		 *				property form
		 * </table>
		 */
		JComponent view;
		private Object model;



		// *******************
		// *** Constructor ***
		// *******************



		/**
		 * Standard constructor.
		 * <p>
		 * Creates the GUI component for this <code>PropertyField</code> and stores
		 * it in the {@link #view} and {@link #model} fields.
		 * <p>
		 * References checked: 10.9.2007 (2.0b8)
		 *
		 * @see		PropertyPanel#buildPropertyFields
		 */
		PropertyField(PresentationPropertyDefinition propertyDef, ActionListener actionListener,
																  HyperlinkListener hyperlinkListener) {
			this.propertyDef = propertyDef;
			if (isHidden()) {
				actionListener = null;		// ### required?
				hyperlinkListener = null;	// ### required?
			}
			Object[] component = propertyDef.createGUIComponent(actionListener, hyperlinkListener, controler);
			this.view = (JComponent) component[0];
			this.model = component[1];
		}



		// ***************
		// *** Methods ***
		// ***************



		public String toString() {
			return getName();
		}

		/**
		 * @see		PropertyPanel#getProperties
		 */
		String getText() {
			String visualization = propertyDef.getVisualization();
			if (visualization.equals(VISUAL_FIELD) ||
				visualization.equals(VISUAL_FILE_CHOOSER) ||
				visualization.equals(VISUAL_COLOR_CHOOSER) ||
				visualization.equals(VISUAL_PASSWORD_FIELD) ||
				visualization.equals(VISUAL_AREA) || visualization.equals("")) {
				return ((JTextComponent) model).getText();
			} else if (visualization.equals(VISUAL_TEXT_EDITOR)) {
				return ((TextEditorPanel) model).getText();
			} else if (visualization.equals(VISUAL_CHOICE)) {
				ComboBoxItem item = (ComboBoxItem) ((JComboBox) view).getSelectedItem();
				if (item == null) {
					// ### error check
					logger.warning("can't get selected item (ComboBoxItem) of menu " + propertyDef + " -- empty string returned");
					return "";
				}
				return item.text;
			} else if (visualization.equals(VISUAL_RADIOBUTTONS)) {
				JRadioButton button = getButton();
				// ### error check
				if (button == null) {
					logger.warning("can't get selected button of " + propertyDef + " -- empty string returned");
					return "";
				}
				//
				return button.getText();
			} else if (visualization.equals(VISUAL_SWITCH)) {
				JCheckBox cbox = (JCheckBox) view;
				return cbox.isSelected() ? SWITCH_ON : SWITCH_OFF;
			} else if (visualization.equals(VISUAL_DATE_CHOOSER)) {
				JComboBox[] menus = (JComboBox[]) model;
				String year = (String) menus[0].getSelectedItem();
				String month = (String) menus[1].getSelectedItem();
				if (!month.equals(VALUE_NOT_SET)) {
					month = DeepaMehtaUtils.align(Integer.toString(menus[1].getSelectedIndex()));
				}
				String day = (String) menus[2].getSelectedItem();
				if (!day.equals(VALUE_NOT_SET)) {
					day = DeepaMehtaUtils.align(day);
				}
				return year + DATE_SEPARATOR + month + DATE_SEPARATOR + day;
			} else if (visualization.equals(VISUAL_TIME_CHOOSER)) {
				JComboBox[] menus = (JComboBox[]) model;
				String hour = (String) menus[0].getSelectedItem();
				if (!hour.equals(VALUE_NOT_SET)) {
					hour = DeepaMehtaUtils.align(hour);
				}
				String minute = (String) menus[1].getSelectedItem();
				return hour + TIME_SEPARATOR + minute;
			} else {
				throw new DeepaMehtaException("unexpected visualization mode: \"" +
					visualization + "\"");
			}
		}

		/**
		 * @see		PropertyPanel#showProperties
		 */
		void setText(String text, String baseURL, ActionListener actionListener, DocumentListener documentListener) throws DeepaMehtaException {
			String visualization = propertyDef.getVisualization();
			if (visualization.equals(VISUAL_FIELD) || visualization.equals(VISUAL_FILE_CHOOSER) ||
					visualization.equals(VISUAL_COLOR_CHOOSER) || visualization.equals(VISUAL_PASSWORD_FIELD) ||
					visualization.equals(VISUAL_AREA) || visualization.equals("")) {
				JTextComponent comp = (JTextComponent) model;
				comp.getDocument().removeDocumentListener(documentListener);
				comp.setText(text);
				comp.getDocument().addDocumentListener(documentListener);
				// restore scrollbar value
				restoreScrollbarValue();
			} else if (visualization.equals(VISUAL_TEXT_EDITOR)) {
				TextEditorPanel textEditor = (TextEditorPanel) model;
				textEditor.setText(text, baseURL, documentListener);
				// addRevealAllButton(actionListener);	// ### doesn't work
				// restore scrollbar value
				restoreScrollbarValue();
			} else if (visualization.equals(VISUAL_CHOICE)) {
				JComboBox cbox = (JComboBox) view;
				setSelectedCustomItem(cbox, text, actionListener, getName());
			} else if (visualization.equals(VISUAL_RADIOBUTTONS)) {
				// get the radio button corresponding to "text"
				JRadioButton button = getButton(text);
				// error check
				if (button == null) {
					if (text.equals("")) {
						logger.warning("property \"" + getName() + "\" has no value -- " +
							"displayed value might not be accurate");
					} else {
						logger.warning("property \"" + getName() + "\" has unexpected value \"" + text + "\" -- " +
							"displayed value might not be accurate");
					}
					removeAllSelections();
					return;
				}
				// select the radiobutton
				button.setSelected(true);
			} else if (visualization.equals(VISUAL_SWITCH)) {
				JCheckBox cbox = (JCheckBox) view;
				cbox.setSelected(SWITCH_ON.equals(text));
			} else if (visualization.equals(VISUAL_DATE_CHOOSER)) {
				setDate(text, actionListener);
			} else if (visualization.equals(VISUAL_TIME_CHOOSER)) {
				setTime(text, actionListener);
			} else if (visualization.equals(VISUAL_HIDDEN)) {
				// do nothing
			} else {
				throw new DeepaMehtaException("unexpected visualization mode: \"" + visualization + "\"");
			}
		}

		private void setDate(String date, ActionListener actionListener) throws DeepaMehtaException {
			try {
				String year, month, day;
				if (date.equals("")) {
					year = month = day = VALUE_NOT_SET;
				} else {
					StringTokenizer st = new StringTokenizer(date, DATE_SEPARATOR);
					year = st.nextToken();
					month = st.nextToken();
					if (!month.equals(VALUE_NOT_SET)) {
						month = monthNames[Integer.parseInt(month) - 1];
					}
					day = DeepaMehtaUtils.unalign(st.nextToken());
				}
				JComboBox[] menus = (JComboBox[]) model;
				setSelectedItem(menus[0], year, actionListener);
				setSelectedItem(menus[1], month, actionListener);
				setSelectedItem(menus[2], day, actionListener);
			} catch (NoSuchElementException e) {
				throw new DeepaMehtaException("unexpected date format");
			}
		}

		private void setTime(String time, ActionListener actionListener) {
			String hour, minute;
			if (time.equals("")) {
				hour = minute = VALUE_NOT_SET;
			} else {
				StringTokenizer st = new StringTokenizer(time, TIME_SEPARATOR);
				hour = DeepaMehtaUtils.unalign(st.nextToken());
				minute = st.nextToken();
			}
			JComboBox[] menus = (JComboBox[]) model;
			setSelectedItem(menus[0], hour, actionListener);
			setSelectedItem(menus[1], minute, actionListener);
		}

		void setEnabled(boolean enabled) {
			String visualization = propertyDef.getVisualization();
			if (visualization.equals(VISUAL_FIELD) ||
				visualization.equals(VISUAL_FILE_CHOOSER) ||
				visualization.equals(VISUAL_COLOR_CHOOSER) ||
				visualization.equals(VISUAL_PASSWORD_FIELD) ||
				visualization.equals(VISUAL_AREA) || visualization.equals("")) {
				((JTextComponent) model).setEnabled(enabled);
				// action button ###
				if (propertyDef.hasActionButton()) {
					JButton button = (JButton) view.getComponent(1);
					button.setEnabled(enabled);
				}
			} else if (visualization.equals(VISUAL_TEXT_EDITOR)) {
				((TextEditorPanel) model).setEnabled(enabled);
			} else if (visualization.equals(VISUAL_CHOICE)) {
				((JComboBox) view).setEnabled(enabled);
			} else if (visualization.equals(VISUAL_RADIOBUTTONS)) {
				Enumeration e = ((ButtonGroup) model).getElements();
				while (e.hasMoreElements()) {
					JRadioButton button = (JRadioButton) e.nextElement();
					button.setEnabled(enabled);
				}
			} else if (visualization.equals(VISUAL_SWITCH)) {
				((JCheckBox) view).setEnabled(enabled);
			} else if (visualization.equals(VISUAL_DATE_CHOOSER)) {
				((JComboBox[]) model)[0].setEnabled(enabled);
				((JComboBox[]) model)[1].setEnabled(enabled);
				((JComboBox[]) model)[2].setEnabled(enabled);
			} else if (visualization.equals(VISUAL_TIME_CHOOSER)) {
				((JComboBox[]) model)[0].setEnabled(enabled);
				((JComboBox[]) model)[1].setEnabled(enabled);
			} else {
				// ### hidden
			}
		}

		void focus() {
			String visualization = propertyDef.getVisualization();
			if (visualization.equals(VISUAL_FIELD) ||
				visualization.equals(VISUAL_FILE_CHOOSER) ||
				visualization.equals(VISUAL_COLOR_CHOOSER) ||
				visualization.equals(VISUAL_PASSWORD_FIELD) ||
				visualization.equals(VISUAL_AREA) || visualization.equals("")) {
				((JTextComponent) model).requestFocus();
				// ### does it work? must use document?
			} else if (visualization.equals(VISUAL_TEXT_EDITOR)) {
				((TextEditorPanel) model).requestFocus();
				// ### does it work? must use document?
			} else if (visualization.equals(VISUAL_DATE_CHOOSER)) {
				// ### pending
			} else if (visualization.equals(VISUAL_TIME_CHOOSER)) {
				// ### pending
			} else {
				// ###
			}
		}

		/* ### doesn't work
		void addRevealAllButton(ActionListener actionListener) {
			if (getName().equals(PROPERTY_RESULT)) {
				// create "Reveal all" btton
				JButton button = new JButton(REVEAL_ALL_BUTTON_LABEL);
				button.setActionCommand(REVEAL_ALL_BUTTON_COMMAND);
				button.addActionListener(actionListener);
				// add to text component
				JTextPane comp = (JTextPane) getTextComponent();
				boolean editable = comp.isEditable();
				// comp.setCaretPosition(0);	// ### required?
				comp.setEditable(true);			// in order to insert a component, the text must be editable
				comp.insertComponent(button);	// ### doesn't work
				comp.setEditable(editable);
			}
		} */

		boolean isHidden() {
			return propertyDef.getHidden();
		}

		/**
		 * References checked: 4.8.2001 (2.0a11)
		 *
		 * @see		PropertyPanel#getProperties
		 */
		String getName() {
			return propertyDef.getPropertyName();
		}

		/**
		 * References checked: 4.8.2001 (2.0a11)
		 *
		 * @see		PropertyPanel#buildPropertyPanel
		 */
		String getLabel() {
			return propertyDef.getPropertyLabel();
		}

		// --- getButton (2 forms) ---

		/**
		 * Returns the radio button with the specified label or <code>null</code> if there is no such radio button.
		 * Only used for <code>VISUAL_RADIOBUTTONS</code> properties.
		 *
		 * @see		#setText
		 */
		JRadioButton getButton(String label) {
			Enumeration e = ((ButtonGroup) model).getElements();
			while (e.hasMoreElements()) {
				JRadioButton button = (JRadioButton) e.nextElement();
				if (button.getText().equals(label)) {
					return button;
				}
			}
			return null;
		}

		/**
		 * Returns the selected radio button or <code>null</code> if no radio button is selected.
		 * Only used for <code>VISUAL_RADIOBUTTONS</code> properties.
		 *
		 * @see		#getText
		 */
		JRadioButton getButton() {
			Enumeration e = ((ButtonGroup) model).getElements();
			while (e.hasMoreElements()) {
				JRadioButton button = (JRadioButton) e.nextElement();
				if (button.isSelected()) {
					return button;
				}
			}
			return null;
		}

		// ---

		/**
		 * Returns the vertical scrollbar of this field, resp. <code>null</code> if this field has no scrollbar.
		 */
		JScrollBar getScrollBar() {
			String visualization = propertyDef.getVisualization();
			if (visualization.equals(VISUAL_AREA) || visualization.equals(VISUAL_TEXT_EDITOR)) {
				JScrollPane scrollPane = visualization.equals(VISUAL_AREA) ? (JScrollPane) view :
					((TextEditorPanel) model).getScrollPane();
				if (scrollPane != null) {	// Note: text editors of type EDITOR_TYPE_SINGLE_LINE have no scrollpane
					return scrollPane.getVerticalScrollBar();
				}
			}
			return null;
		}

		JTextComponent getTextComponent() {
			String visualization = propertyDef.getVisualization();
			if (visualization.equals(VISUAL_AREA) || visualization.equals(VISUAL_TEXT_EDITOR)) {
				JTextComponent textComponent = visualization.equals(VISUAL_AREA) ? (JTextComponent) model :
					((TextEditorPanel) model).getTextComponent();
				return textComponent;
			}
			return null;
		}

		void restoreScrollbarValue() {
			JScrollBar scrollbar = getScrollBar();
			if (scrollbar != null) {
				Hashtable scrollbarValues = getScrollbarValues();
				Hashtable caretPositions = getCaretPositions();
				// error check
				if (scrollbarValues == null) {
					logger.warning("no scrollbar values for field \"" + this + "\" available");
					return;
				}
				//
				JTextComponent textComponent = getTextComponent();
				Integer scrollbarValue = (Integer) scrollbarValues.get(getName());
				Integer caretPosition = (Integer) caretPositions.get(getName());
				if (scrollbarValue != null) {
					logger.fine("restoring scrollbar value of " + getSelectedObject() +
						"\n    \"" + getName() + "\": scrollbarValue=" + scrollbarValue + " caretPosition=" + caretPosition);
					scrollbar.setValue(scrollbarValue.intValue());
					textComponent.setCaretPosition(caretPosition.intValue());
				} else {
					textComponent.setCaretPosition(0);
				}
			}
		}

		// ---

		// ### doesn't work
		void removeAllSelections() {
			ButtonGroup group = (ButtonGroup) model;
			Enumeration e = group.getElements();
			while (e.hasMoreElements()) {
				JRadioButton button = (JRadioButton) e.nextElement();
				// button.setSelected(false);
				group.setSelected(button.getModel(), false);
			}
		}
	} // end of inner class PropertyField

	// ---

	/**
	 * Model of selected topic/association.
	 * <p>
	 * <code>Selection</code> objects are stored in the {PropertyPanel#selections} hashtable.
	 */
	private class Selection {

		PresentationTopicMap topicmap;
		BaseTopic topic;		//	\ max one
		BaseAssociation assoc;	//	/ is set
		Hashtable props;
		Hashtable baseURLs;
		Vector disabledProps;
		boolean retypeIsAllowed;
		String selected;		// SELECTED_TOPIC, SELECTED_ASSOCIATION, SELECTED_TOPICMAP

		/**
		 * @see		PropertyPanel#setSelection(BaseTopic topic, PresentationTopicMap topicmap, String viewmode, Hashtable props, Vector disabledProperties)
		 */
		Selection(PresentationTopicMap topicmap, BaseTopic topic, Hashtable props, Hashtable baseURLs,
																			Vector disabledProps, boolean retypeIsAllowed) {
			this.topicmap = topicmap;
			this.topic = topic;
			this.props = props;
			this.baseURLs = baseURLs;
			this.disabledProps = disabledProps;
			this.retypeIsAllowed = retypeIsAllowed;
			this.selected = SELECTED_TOPIC;
		}

		/**
		 * @see		PropertyPanel#setSelection(BaseAssociation assoc, PresentationTopicMap topicmap, String viewmode, Hashtable props)
		 */
		Selection(PresentationTopicMap topicmap, BaseAssociation assoc, Hashtable props, Hashtable baseURLs,
																			Vector disabledProps, boolean retypeIsAllowed) {
			this.topicmap = topicmap;
			this.assoc = assoc;
			this.props = props;
			this.baseURLs = baseURLs;
			this.disabledProps = disabledProps;
			this.retypeIsAllowed = retypeIsAllowed;
			this.selected = SELECTED_ASSOCIATION;
		}

		Selection(PresentationTopicMap topicmap, Hashtable props, Hashtable baseURLs, Vector disabledProps) {
			this.topicmap = topicmap;
			this.props = props;
			this.baseURLs = baseURLs;
			this.disabledProps = disabledProps;
			this.retypeIsAllowed = false;	// ###
			this.selected = SELECTED_TOPICMAP;
		}
	}
}
