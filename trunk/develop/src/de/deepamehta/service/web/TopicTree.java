package de.deepamehta.service.web;

import de.deepamehta.BaseTopic;

import java.util.Vector;



public class TopicTree {

	public BaseTopic topic;
	public Vector childTopics;	// element type is TopicTree
	public int totalChildCount;

	public TopicTree(BaseTopic topic) {
		this.topic = topic;
		this.childTopics = new Vector();
	}

	public int getChildCount() {
		return childTopics.size();
	}
}
