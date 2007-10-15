package de.deepamehta.service;

import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.OrderedItem;
import de.deepamehta.PropertyDefinition;
import de.deepamehta.Relation;
import de.deepamehta.topics.TypeTopic;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;



/**
 * <p>
 * <hr>
 * Last sourcecode change: 14.10.2007 (2.0b8)<br>
 * Last documentation update: 14.10.2007 (2.0b8)<br>
 * J&ouml;rg Richter<br>
 * jri@freenet.de
 */
public class TopicBean implements DeepaMehtaConstants {

	public Vector fields = new Vector();
	private ApplicationService as;

	public TopicBean(String topicID, ApplicationService as) {
		this.as = as;
		addFields(topicID, true);
	}

	// ---

	private void addFields(String topicID, boolean deep) {
   		Hashtable props = as.getTopicProperties(topicID, 1);
		Enumeration items = as.getTopicType(topicID, 1).getDefinition().elements();
		while (items.hasMoreElements()) {
			OrderedItem item = (OrderedItem) items.nextElement();
			if (item instanceof PropertyDefinition) {
				PropertyDefinition propDef = (PropertyDefinition) item;
				String propName = propDef.getPropertyName();
				String propValue = (String) props.get(propName);
				fields.addElement(new Field(propName, propValue));
			} else if (item instanceof Relation) {
				if (deep) {
					Relation rel = (Relation) item;
					if (rel.webInfo.equals(WEB_INFO_TOPIC_NAME)) {
						addRelationField(rel, topicID);
					} else if (rel.webInfo.equals(WEB_INFO) || rel.webInfo.equals(WEB_INFO_DEEP)) {
						String relTopicID = null;
						Vector selectedTopics = as.getRelatedTopics(topicID, rel.assocTypeID, rel.relTopicTypeID, 2,
							false, true);	// ordered=false, emptyAllowed=true ### relTopicPos=2 hardcoded
						if (selectedTopics.size() > 0) {	// ### only the first related topic is considered
							relTopicID = ((BaseTopic) selectedTopics.firstElement()).getID();
						}
						// recursive call
						boolean newDeep = rel.webInfo.equals(WEB_INFO_DEEP);
						addFields(relTopicID, newDeep);
					} else {
						throw new DeepaMehtaException("unexpected web info mode: \"" + rel.webInfo + "\"");
					}
				}
			} else {
				throw new DeepaMehtaException("unexpected object in type definition: " + item);
			}
		}
	}

	private void addRelationField(Relation rel, String topicID) {
		// ### compare to HTMLGenerator.relationInfoField()
		String relName = rel.name;
		String relTopicTypeID = rel.relTopicTypeID;
		String cardinality = rel.cardinality;
		String assocTypeID = rel.assocTypeID;
		//
		TypeTopic relTopicType = as.type(relTopicTypeID, 1);
		boolean many = cardinality.equals(CARDINALITY_MANY);
		String fieldLabel = !relName.equals("") ? relName : many ? relTopicType.getPluralNaming() : relTopicType.getName();
		//
		Vector selectedTopics = as.getRelatedTopics(topicID, assocTypeID, relTopicTypeID, 2,
			false, true);	// ordered=false, emptyAllowed=true ### relTopicPos=2 hardcoded
		fields.addElement(new Field(fieldLabel, selectedTopics));
	}

	// ---

	public class Field {

		public static final int TYPE_SINGLE = 0;
		public static final int TYPE_MULTI = 1;

		public int type;
		public String name;
		public String value;
		public Vector values;

		Field(String name, String value) {
			type = TYPE_SINGLE;
			this.name = name;
			this.value = value;
		}

		Field(String name, Vector values) {
			type = TYPE_MULTI;
			this.name = name;
			this.values = values;
		}
	}
}
