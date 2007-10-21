package de.deepamehta.service;

import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.DeepaMehtaException;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;



/**
 * A data container that holds all the content about one topic. The content comprises of the topics properties as well as
 * the properties of related topics as defined by Relations. For creating topic beans the Application Service provides
 * a {@link ApplicationService#createTopicBean factory method}.
 * <p>
 * The contents are modelled as a list of fields. A field has a name and a value component. In case of a property the
 * field's name is the property-name and the value component is the property-value. In case of related topics that are
 * included by name (the Relation's "Web Info" property is set to "Related Topic Name") the field's name is the type-name
 * and the value component is a vector of BaseTopics.
 * <p>
 * The HTML Generator provides a {HTMLGenerator#info(TopicBean) rendering method} to layout a Topic Bean as a 2-column table.
 * <p>
 * This class provides a number of methods for manipulating the fields of a Topic Bean before rendering.
 * <p>
 * <hr>
 * Last sourcecode change: 20.10.2007 (2.0b8)<br>
 * Last documentation update: 14.10.2007 (2.0b8)<br>
 * J&ouml;rg Richter<br>
 * jri@freenet.de
 */
public class TopicBean implements DeepaMehtaConstants {

	public static final String FIELD_SEPARATOR = " / ";

	public String id, name, typeID, typeName, icon;
	public Vector fields = new Vector();	// element type is TopicBeanField

	// ---

	/**
	 * Returns the value of the specified field. If there is no such field, <code>null</code> is returned.
	 * If there are more than one fields, only the value of the first field is returned.
	 * <p>
	 * Use this method only for {@link TopicBeanField#TYPE_SINGLE TYPE_SINGLE} fields.
	 * If used for a {@link TopicBeanField#TYPE_MULTI TYPE_MULTI} field an exception is thrown.
	 *
	 * @return	the field value.
	 *
	 * @throws	DeepaMehtaException	if the specified field is of type <code>TYPE_MULTI</code>. Note: fields that list
	 *				related topics by name (the Relation's "Web Info" property is set to "Related Topic Name") are modelled
	 *				as <code>TYPE_MULTI</code> fields, also if the Relation's "Cardinality" property is set to "one".
	 *				To get the values of <code>TYPE_MULTI</code> fields, use {@link #getValues}.
	 */
	public String getValue(String fieldName) {
		TopicBeanField field = getField(fieldName);
		if (field == null) {
			return null;
		}
		// error check
		if (field.type == TopicBeanField.TYPE_MULTI) {
			throw new DeepaMehtaException("field \"" + fieldName + "\" is a multi-value field. Use getValues() instead.");
		}
		//
		return field.value;
	}

	/**
	 * Returns the values of the specified field. If there is no such field, <code>null</code> is returned.
	 * If there are more than one fields, only the values of the first field is returned.
	 * <p>
	 * Use this method only for {@link TopicBeanField#TYPE_MULTI TYPE_MULTI} fields.
	 * If used for a {@link TopicBeanField#TYPE_SINGLE TYPE_SINGLE} field an exception is thrown.
	 *
	 * @return	the field values as vector of BaseTopics.
	 *
	 * @throws	DeepaMehtaException	if the specified field is of type <code>TYPE_SINGLE</code>.
	 *				To get the value of <code>TYPE_SINGLE</code> fields, use {@link #getValue}.
	 */
	public Vector getValues(String fieldName) {
		TopicBeanField field = getField(fieldName);
		if (field == null) {
			return null;
		}
		// error check
		if (field.type == TopicBeanField.TYPE_SINGLE) {
			throw new DeepaMehtaException("field \"" + fieldName + "\" is a single-value field. Use getValue() instead.");
		}
		//
		return field.values;
	}

	/**
	 * Gets a field by its name. By setting its <code>name</code> and <code>value</code> (or <code>values</code>) member
	 * variables the field can be re-labeled or content-changed before rendering.
	 *
	 * @return	the field with the specified name. If there is no such field, <code>null</code> is returned.
	 *			If there are more than one fields, the first is returned.
	 */
	public TopicBeanField getField(String fieldName) {
		Enumeration e = fields.elements();
		while (e.hasMoreElements()) {
			TopicBeanField field = (TopicBeanField) e.nextElement();
			if (field.name.equals(fieldName)) {
				return field;
			}
		}
		return null;
	}

	/**
	 * Removes the field with the specified name. If there is no such field, nothing is done.
	 * If there are more than one fields, only the first is removed.
	 */
	public void removeField(String fieldName) {
		TopicBeanField field = getField(fieldName);
		if (field != null) {
			fields.removeElement(field);
		}
	}

	/**
	 * Removes all fields whose names contains the specified substring. If there is no such field, nothing is done.
	 */
	public void removeFieldsContaining(String partialFieldName) {
		Iterator i = fields.iterator();
		while (i.hasNext()) {
			TopicBeanField field = (TopicBeanField) i.next();
			if (field.name.indexOf(partialFieldName) != -1) {
				i.remove();
			}
		}
	}
}
