package de.deepamehta.client;

import de.deepamehta.DeepaMehtaConstants;



class Selection implements DeepaMehtaConstants {

	// selection mode, see "Selection Modes" in DeepaMehtaConstants
	// SELECTED_NONE, SELECTED_TOPIC, SELECTED_ASSOCIATION, SELECTED_TOPICMAP
	int mode;

	PresentationTopic topic;		//	\ max one
	PresentationAssociation assoc;	//	/ is set

	Selection() {
		// Note: there are 2 situations there is no selection (SELECTED_NONE)
		// 1) a view is opened
		// 2) the selected node/edge is removed
		mode = SELECTED_NONE;
	}
}
