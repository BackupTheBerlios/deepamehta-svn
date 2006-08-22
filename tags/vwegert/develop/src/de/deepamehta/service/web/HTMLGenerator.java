package de.deepamehta.service.web;

import de.deepamehta.BaseTopic;
import de.deepamehta.BaseAssociation;
import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.DeepaMehtaUtils;
import de.deepamehta.OrderedItem;
import de.deepamehta.PropertyDefinition;
import de.deepamehta.Relation;
import de.deepamehta.topics.LiveTopic;
import de.deepamehta.topics.TypeTopic;
import de.deepamehta.topics.TopicTypeTopic;
import de.deepamehta.topics.AssociationTypeTopic;
import de.deepamehta.service.ApplicationService;
//
import java.text.Format;
import java.text.ChoiceFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.*;



/**
 * <P>
 * <HR>
 * Last sourcecode change: 24.4.2004 (2.0b6)<BR>
 * Last documentation update: 16.9.2002 (2.0a16-pre3)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class HTMLGenerator implements DeepaMehtaConstants {



	// *************
	// *** Field ***
	// *************



	ApplicationService as;
	Locale locale;
	Vector resources = new Vector();	// base type is ResourceBundle



	// *******************
	// *** Constructor ***
	// *******************



	/**
	 * @param	locale	if <CODE>null</CODE> the default locale is used.
	 */
	public HTMLGenerator(ApplicationService as, Locale locale) {
		this.as = as;
		this.locale = locale != null ? locale : Locale.getDefault();
		System.out.println(">>> HTML generator is localized to \"" + this.locale + "\"");
	}



	// ***************
	// *** Methods ***
	// ***************



	public void addResource(String bundleName) {
		resources.addElement(ResourceBundle.getBundle(bundleName, locale));
	}

	// --- message (3 forms) ---

	public String message(String key) {
		return ((ResourceBundle) resources.firstElement()).getString(key);	// ### only the first bundle is respected
	}

	public String message(String key, Object arg) {
		MessageFormat message = new MessageFormat("");
		message.setLocale(locale);	// Note: pattern must be set AFTER setting the locale
		message.applyPattern(message(key));									// ### only the first bundle is respected
		Object[] args = {arg};
		return message.format(args);
	}

	public String message(String key, String keyNo, String keyOne, String keyMany, Object arg) {
		MessageFormat message = new MessageFormat("");
		message.setLocale(locale);
		// Build choice format
		double[] ranges = {0, 1, 2};
		String[] msgs = {message(keyNo), message(keyOne), message(keyMany)};
		ChoiceFormat choice = new ChoiceFormat(ranges, msgs);
		// Note: pattern must be set AFTER setting the locale
		message.applyPattern(message(key));									// ### only the first bundle is respected
		// Note: formats must be set AFTER setting the pattern
		Format[] argFormats = {choice, NumberFormat.getInstance()};
		message.setFormats(argFormats);
		//
		Object[] args = {arg, arg};
		return message.format(args);
	}

	// --- list (13 forms) ---

	public String list(Vector topics) {
		return list(topics, null, null, null, null, null, null);
	}

	public String list(Vector topics, String[] propSel) {
		return list(topics, propSel, null, null, null);
	}

	public String list(Vector topics, Action[] actions) {
		return list(topics, null, actions);
	}

	public String list(Vector topics, String[] propSel, Action[] actions) {
		return list(topics, propSel, actions, null, null);
	}

	// ### "extraTopics" for KiezAtlas
	public String list(Vector topics, Hashtable extraTopics) {
		return list(topics, null, null, false, null, null, null, null, extraTopics);
	}

	// ### "extraTopics" for KiezAtlas
	public String list(Vector topics, String[] propSel, String infoAction, Hashtable extraTopics) {
		return list(topics, null, propSel, false, null, infoAction, null, null, extraTopics);
	}

	public String list(Vector topics, String[] propSel, Action[] actions, String infoAction) {
		return list(topics, propSel, actions, infoAction, null);
	}

	public String list(Vector topics, String[] propSel, Action[] actions, Hashtable colWidths) {
		return list(topics, null, propSel, false, actions, null, null, colWidths);
	}

	public String list(Vector topics, String[] propSel, Action[] actions, String infoAction, Hashtable colWidths) {
		return list(topics, null, propSel, false, actions, infoAction, null, colWidths);
	}

	public String list(Vector topics, String selectedID, String[] propSel, String infoAction, String infoActionParams) {
		return list(topics, selectedID, propSel, false, null, infoAction, infoActionParams, null);
	}

	public String list(Vector topics, String selectedID, String[] propSel, Action[] actions,
										String infoAction, String infoActionParams, Hashtable colWidths) {
		return list(topics, selectedID, propSel, false, actions, infoAction, infoActionParams, colWidths);
	}

	public String list(Vector topics, String selectedID, String[] propSel, boolean hideSel, Action[] actions,
									String infoAction, String infoActionParams, Hashtable colWidths) {
		return list(topics, selectedID, propSel, hideSel, actions, infoAction, infoActionParams, colWidths, null);
	}

	/**
	 * Renders a list of topics as a table, one row per topic, one column per property.
	 */
	public String list(Vector topics, String selectedID, String[] propSel, boolean hideSel, Action[] actions,
									String infoAction, String infoActionParams, Hashtable colWidths, Hashtable extraTopics) {
		if (topics.size() == 0) {
			return "";
		}
		//
		StringBuffer html = new StringBuffer();
		TypeTopic type = as.type((BaseTopic) topics.firstElement());
		//
		html.append("<TABLE>\r");
		html.append("<TR VALIGN=\"TOP\">");
		if (extraTopics != null) {
			html.append("<td></td>");
		}
		infoFieldsHeading(type, type, true, propSel, hideSel, actions, colWidths, html);	// deep=true
		actionNames(actions, html);
  		html.append("</TR>\r");
  		//
		for (int i = 0; i < topics.size(); i++) {
			String topicID = ((BaseTopic) topics.elementAt(i)).getID();
   			html.append("<TR VALIGN=\"TOP\"" + (topicID.equals(selectedID) ? " BGCOLOR=\"#F0F0F0\"" : "") + ">");
			if (extraTopics != null) {
				extraTopics((Vector) extraTopics.get(topicID), html);
			}
			infoFields(type, topicID, type, true, LAYOUT_ROWS, propSel, hideSel, infoAction, infoActionParams, html);	// deep=true
			actionButtons(topicID, actions, html);
   			html.append("</TR>\r");
		}
		html.append("</TABLE>\r");
		//
		return html.toString();
	}

	// ---

	public String listHeading(int count, String text, String sing, String plur) {
		switch (count) {
		case 0:
			return text + " no " + sing;
		case 1:
			return text + " one " + sing;
		default:
			return text + " " + count + " " + plur;
		}
	}

	// --- actionButton (2 forms) ---

	private void actionButton(String topicID, Action action, StringBuffer html) {
		html.append(actionButton(topicID, action.action, action.iconfile));
	}

	/**
	 * Renders an image button as one <CODE>TD</CODE> tag.
	 */
	public String actionButton(String topicID, String action, String iconfile) {
		return "<TD WIDTH=50><A HREF=\"controller?action=" + action + "&id=" + topicID +
			"\"><IMG SRC=\"images/" + iconfile + "\" BORDER=0></A></TD>";
	}

	// --- info (3 forms) ---

	public String info(String topicID) {
		return info(topicID, null);
	}

	public String info(String topicID, String[] propSel) {
		return info(topicID, propSel, false);
	}

	/**
	 * Renders a single topic as a 2-column table, one property per row
	 *
	 * @param	topicID		if <CODE>null</CODE> nothing is generated (empty string is returned)
	 * @param	propSel		if <CODE>null</CODE> all properties are displayed
	 */
	public String info(String topicID, String[] propSel, boolean hideSel) {
		if (topicID == null) {
			return "";
		}
		//
		StringBuffer html = new StringBuffer();
   		TypeTopic type = as.type(as.cm.getTopic(topicID, 1));	// throws DME ### version=1
		//
		html.append("<TABLE>\r");
		infoFields(type, topicID, type, true, LAYOUT_COLS, propSel, hideSel, null, null, html);		// deep=true
		html.append("</TABLE>\r");
		//
		return html.toString();
	}

	// --- form (8 forms) ---

	public String form(String typeID, String action) {
		return form(typeID, action, null, null);
	}

	public String form(String typeID, String action, String id) {
		return form(typeID, action, id, null);
	}

	public String form(String typeID, String action, String[] propSel) {
		return form(typeID, action, null, propSel);
	}

	public String form(String typeID, String action, String[] propSel, boolean hideSel) {
		return form(typeID, action, null, propSel, hideSel);
	}

	public String form(String typeID, String action, String[] propSel, boolean hideSel, Hashtable hints) {
		return form(typeID, action, null, propSel, hideSel, hints);
	}

	public String form(String typeID, String action, String id, String[] propSel) {
		return form(typeID, action, id, propSel, false);
	}

	public String form(String typeID, String action, String id, String[] propSel, boolean hideSel) {
		return form(typeID, action, id, propSel, hideSel, null);
	}

	/**
	 * Generates and returns a form for the specified type.
	 * The type can be a topic type or an association type.
	 *
	 * @param	id		may be <CODE>null</CODE>
	 */
	public String form(String typeID, String action, String id, String[] propSel, boolean hideSel, Hashtable hints) {
		StringBuffer html = new StringBuffer();
   		TypeTopic type = as.type(typeID, 1);	// throws DME
		//
		html.append("<FORM>\r");	// ### method="post" enctype="multipart/form-data"
		html.append("<TABLE>\r");
		//
		boolean containsFileField = formFields(type, id, true, "", type, propSel, hideSel, hints, html);	// deep=true, prefix=""
		// ### System.out.println(">>> HTMLGenerator.form(): containsFileField=" + containsFileField);
		if (containsFileField) {
			html.insert(5, " method=\"post\" enctype=\"multipart/form-data\"");
		}
		submitButton(action, html);
		//
		html.append("</TABLE>\r");
		html.append("</FORM>\r");
		//
		return html.toString();
	}

	// --- tree (5 forms) ---

	public String tree(TopicTree tree) {
		return tree(tree, null, null, null);
	}

	public String tree(TopicTree tree, Action[] actions, Vector extended, String highlighted) {
		return tree(tree, actions, null, extended, highlighted);
	}

	public String tree(TopicTree tree, Action[] actions, String infoAction, Vector extended, String highlighted) {
		return tree(tree, actions, infoAction, extended, highlighted, -1, -1);
	}

	public String tree(TopicTree tree, Action[] actions, String infoAction, Vector extended, String highlighted,
																					int pageNr, int pageSize) {
		return tree(tree, actions, infoAction, extended, null, highlighted, pageNr, pageSize);
	}

	/**
	 * @param	actions			additional links for every node, if <CODE>null</CODE> no links are rendered
	 * @param	infoAction		link for node itself, if <CODE>null</CODE> the node is not rendered as link
	 * @param	extended		IDs of nodes to extend, if <CODE>null</CODE> all nodes are extended
	 * @param	extendedParams	additional parameters for the extend/collapse actions, if <CODE>null</CODE> no parameters are added to the URL
	 * @param	highlighted		ID of node to highlight, if <CODE>null</CODE> no node is highlighted
	 */
	public String tree(TopicTree tree, Action[] actions, String infoAction, Vector extended, String extendedParams,
																	String highlighted, int pageNr, int pageSize) {
		StringBuffer html = new StringBuffer();
		//
		html.append("<TABLE>\r");
		treeNode(tree, 0, actions, infoAction, null, null, null, extended, extendedParams, highlighted, html);
		if (pageNr != -1 && tree.totalChildCount > pageSize) {
			pager(pageNr, pageSize, tree.totalChildCount, extendedParams, html);
		}
		html.append("</TABLE>\r");
		//
		return html.toString();
	}

	// --- treeForm (2 forms) ---

	public String treeForm(TopicTree tree, String action, String fieldname, String cardinality,
														Vector selected, Vector extended) {
		return treeForm(tree, action, fieldname, cardinality, selected, extended, null);
	}

	/**
	 * @param	cardinality		CARDINALITY_ONE or CARDINALITY_MANY
	 */
	public String treeForm(TopicTree tree, String action, String fieldname, String cardinality,
														Vector selected, Vector extended, String extendedParams) {
		StringBuffer html = new StringBuffer();
		//
		html.append("<FORM>\r");
		html.append("<TABLE>\r");
		//
		treeNode(tree, 0, null, null, fieldname, cardinality, selected, extended, extendedParams, null, html);
		submitButton(action, html);
		//
		html.append("</TABLE>\r");
		html.append("</FORM>\r");
		//
		return html.toString();
	}

	// ---

	public String topicChooser(String typeID) {
		StringBuffer html = new StringBuffer();
		Vector topics = as.cm.getTopics(typeID);	// ### can be very big
		//
		html.append("<SELECT NAME=\"id\">\r");
		Enumeration e = topics.elements();
		while (e.hasMoreElements()) {
			BaseTopic topic = (BaseTopic) e.nextElement();
			option(topic.getName(), topic.getID(), false, html);	// selected=false
		}
		html.append("</SELECT>\r");
		//
		return html.toString();
	}

	public String topicSelector(Vector topics, Vector selectedTopicIDs, String action, String imageAction) {
		StringBuffer html = new StringBuffer();
		//
		html.append("<table>");
		Enumeration e = topics.elements();
		while (e.hasMoreElements()) {
			BaseTopic topic = (BaseTopic) e.nextElement();	// ### was E0E0D0
			html.append("<tr" + (selectedTopicIDs.contains(topic.getID()) ? " BGCOLOR=\"#E0D8D0\"" : "") + "><td>" +
				"<a href=\"controller?action=" + imageAction + "&id=" + topic.getID() + "\">" +
				imageTag(topic) + "</a></td><td>" +
				"<a href=\"controller?action=" + action + "&id=" + topic.getID() + "\">" + topic.getName() +
				"</a></td></tr>");
		}
		html.append("</table>");
		//
		return html.toString();
	}

	// ---

	// ###
	public String formField(PropertyDefinition propDef, String propValue) {
		StringBuffer html = new StringBuffer();
		formField(propDef, propDef.getPropertyLabel(), propValue, "", null, html);	// hint=null
		//
		return html.toString();
	}

	public String submitButton(String action) {
		StringBuffer html = new StringBuffer();
		submitButton(action, html);
		//
		return html.toString();
	}

	// ---

	public String link(String text, String action) {
		return link(text, action, null);
	}

	public String link(String text, String action, String params) {
		StringBuffer html = new StringBuffer();
		link(action, params, null, text, true, html);	// topicID=null, bold=true
		//
		return html.toString();
	}

	// ---

	public String linkList(Vector topics, String action) {
		StringBuffer html = new StringBuffer();
		Enumeration e = topics.elements();
		while (e.hasMoreElements()) {
			BaseTopic topic = (BaseTopic) e.nextElement();
			link(action, null, topic.getID(), topic.getName(), true, html);		// params=null, bold=true
			html.append("<P>");
		}
		//
		return html.toString();
	}

	// ---

	public String staticLink(String text, String link) {
		return staticLink(text, link, null);
	}

	public String staticLink(String text, String link, String target) {
		StringBuffer html = new StringBuffer();
		staticLink(text, link, target, html);
		//
		return html.toString();
	}

	// ---

	public String imageTag(BaseTopic topic) {
		return imageTag(topic, false);
	}

	public String imageTag(BaseTopic topic, boolean withTooltip) {
		String iconfile = as.getLiveTopic(topic).getIconfile();
		return "<img src=\"" + as.getCorporateWebBaseURL() + FILESERVER_ICONS_PATH + iconfile + "\"" +
			(withTooltip ? " alt=\"" + topic.getName() + "\"" : "") + " border=\"0\">";
	}

	// ---

	public String displayObjects(Vector displayObjects) {
		StringBuffer html = new StringBuffer();
		//
		Enumeration e = displayObjects.elements();
		while (e.hasMoreElements()) {
			DisplayObject display = (DisplayObject) e.nextElement();
			switch (display.mode) {
			case DISPLAY_NONE:
				break;
			case DISPLAY_MULTIPLE_CHOICE:
				String title = (String) display.param1;
				Vector assocs = (Vector) display.param2;
				Vector names = (Vector) display.param3;
				html.append(title);
				for (int i = 0; i < assocs.size(); i++) {
					BaseAssociation assoc = (BaseAssociation) assocs.elementAt(i);
					String name = (String) names.elementAt(i);
					String params = "name=" + name + "&nextTask=" + assoc.getTopicID2();
					html.append((i > 0 ? " / " : " ") + link(name, ACTION_SELECT_CASE, params));
				}
				break;
			case DISPLAY_TYPEFORM:
				String typeID = (String) display.param1;
				String[] propSel = (String[]) display.param2;
				html.append(form(typeID, ACTION_PROCESS_TYPEFORM, propSel));
				break;
			case DISPLAY_FREEFORM:
				Vector ffDef = (Vector) display.param1;
				html.append("<FORM>");
				html.append("<TABLE>");
				Enumeration e2 = ffDef.elements();
				while (e2.hasMoreElements()) {
					Object ffElement = e2.nextElement();
					if (ffElement instanceof PropertyDefinition) {
						PropertyDefinition propDef = (PropertyDefinition) ffElement;
						html.append(formField(propDef, null));
					} else if (ffElement instanceof Vector) {
						Vector layoutElement = (Vector) ffElement;
						String kind = (String) layoutElement.elementAt(0);
						if (kind.equals(LAYOUT_ELEMENT_SEPARATOR)) {
							html.append("<TR><TD COLSPAN=2><HR></TD></TR>");
						} else if (kind.equals(LAYOUT_ELEMENT_SPACE)) {
							html.append("<TR><TD><BR><BR></TD></TR>");
						} else if (kind.equals(LAYOUT_ELEMENT_COMMENT)) {
							String comment = (String) layoutElement.elementAt(1);
							html.append("<TR><TD COLSPAN=2>" + comment + "</TD></TR>");
						}
					}
				}
				html.append(submitButton(ACTION_PROCESS_FREEFORM));
				html.append("</TABLE>");
				html.append("</FORM>");
				break;
			case DISPLAY_TOPIC_CHOOSER:
				title = (String) display.param1;
				typeID = (String) display.param2;
				// ### html.append("<H3>" + title + "</H3>");
				html.append("<FORM>");
				html.append(title + topicChooser(typeID));
				html.append("<INPUT TYPE=\"Submit\" VALUE=\"Weiter\">");
				html.append("<INPUT TYPE=\"Hidden\" NAME=\"action\" VALUE=\"" + ACTION_SELECT_TOPIC + "\">");
				html.append("</FORM>");
				break;
			case DISPLAY_TEXT:
				String text = (String) display.param1;
				html.append(text);
				break;
			case DISPLAY_HEADLINE:
				String headline = (String) display.param1;
				html.append("<H3>" + headline + "</H3>");
				break;
			case DISPLAY_LINK:
				text = (String) display.param1;
				String action = (String) display.param2;
				html.append(link(text, action));
				break;
			case DISPLAY_STATIC_LINK:
				text = (String) display.param1;
				String link = (String) display.param2;
				html.append(staticLink(text, link, "_new"));	// ### always displayed in new window
				break;
			default:
				throw new DeepaMehtaException("unexpected display mode: " + display.mode);
			}
			html.append("<P>");
		}
		//
		return html.toString();
	}



	// ***********************
	// *** Private Methods ***
	// ***********************



	/**
	 * @param	actions		may be <CODE>null</CODE>
	 * @param	infoAction	may be <CODE>null</CODE>
	 * @param	fieldname	may be <CODE>null</CODE>
	 * @param	cardinality	may be <CODE>null</CODE>
	 * @param	selected	may be <CODE>null</CODE>
	 * @param	extended	if <CODE>null</CODE> all nodes are extended
	 * @param	highlighted	may be <CODE>null</CODE>
	 */
	private void treeNode(TopicTree tree, int indent, Action[] actions, String infoAction,
										String fieldname, String cardinality, Vector selected,
										Vector extended, String extendedParams, String highlighted, StringBuffer html) {
		String topicID = tree.topic.getID();
		boolean isExtended = extended == null || extended.contains(topicID);
		boolean highlight = topicID.equals(highlighted);
		// --- render topic ---
		if (indent > 0) {	// ### don't render root node
			html.append("<TR" + (highlight ? " BGCOLOR=\"#F0F0F0\"" : "") + "><TD>");	// ### TD WIDTH=300
			html.append(DeepaMehtaUtils.nTimes("&nbsp;", 10 * (indent - 1)));
			// arrow
			if (extended != null) {
				String action = isExtended ? ACTION_COLLAPSE_NODE : ACTION_EXTEND_NODE;
				html.append(tree.getChildCount() > 0 ? "<A HREF=\"controller?action=" + action + "&id=" + topicID +
					(extendedParams != null ? "&" + extendedParams : "") + "\"><IMG SRC=\"images/" + (isExtended ? "arrow_down" : "arrow") + ".gif\" " +
					"BORDER=0 HSPACE=10></A>" : "<IMG SRC=\"images/arrow_empty.gif\" BORDER=0 HSPACE=10>");
			}
			// node name
			link(infoAction, topicID, tree.topic.getName(), indent == 1, html);
			html.append("</TD>");
			// buttons
			actionButtons(topicID, actions, html);
			// checkbox
			if (fieldname != null) {
				html.append("<TD>");
				if (cardinality.equals(CARDINALITY_MANY)) {
					boolean isSelected = selected.contains(topicID);
					checkbox(fieldname, topicID, isSelected, html);
				} else {
					// ###
				}
				html.append("</TD>");
			}
			//
			html.append("</TR>\r");
		}
		// --- render child topics ---
		if (isExtended) {
			Enumeration e = tree.childTopics.elements();
			while (e.hasMoreElements()) {
				treeNode((TopicTree) e.nextElement(), indent + 1, actions, infoAction, fieldname, cardinality,
					selected, extended, extendedParams, highlighted, html);		// recursive call
			}
		}
	}

	// ---

	/**
	 * Renders the form body for the specified type into the specified buffer.
	 * Every field is rendered as &lt;tr> tag.
	 * ### Note: the form fields of "strong" related types are embedded recursively.
	 *
	 * @param	type		the fields of this type are rendered
	 * @param	id			an topic ID or an association ID or <CODE>null</CODE>. If not <code>null</code> the fields
	 *						are filled with the respective topic or association.
	 * @param	prefix		
	 * @param	formType	the form type is passed through recursive calls
	 *
	 * @return	<code>true</code> if the form contain any input field of type "file", <code>false</code> otherwise.
	 *
	 * @see		form
	 * @see		formFields
	 */
	private boolean formFields(TypeTopic type, String id, boolean deep, String prefix, TypeTopic formType,
												String[] propSel, boolean hideSel, Hashtable hints, StringBuffer html) {
		boolean containsFileField = false;		// return variable
   		Hashtable props;
   		if (id == null) {
   			props = new Hashtable();
   		} else {
			if (formType instanceof TopicTypeTopic) {
	   			props = as.getTopicProperties(id, 1);
			} else if (formType instanceof AssociationTypeTopic) {
	   			props = as.getAssocProperties(id, 1);
	   		} else {
	   			throw new DeepaMehtaException("unexpected type class: " + formType.getClass());
	   		}
   		}
		// --- trigger hiddenProperties(type, relTopicTypeID) hook ---
		Vector hiddenProps = null;
		// Note: only topic types (in contrast to association types) have related types
		if (formType instanceof TopicTypeTopic) {
			hiddenProps = as.triggerHiddenProperties(formType, type.getID());		// may return null
		}
		// --- create form fields (table rows) ---
		Enumeration items = type.getDefinition().elements();
		while (items.hasMoreElements()) {
			OrderedItem item = (OrderedItem) items.nextElement();
			if (item instanceof PropertyDefinition) {
				PropertyDefinition propDef = (PropertyDefinition) item;
				String propName = propDef.getPropertyName();
				String propLabel = as.triggerPropertyLabel(propDef, formType, type.getID());
				String propValue = (String) props.get(propName);
				// Note: the hook returns _parameter names_ and the page delivers _field labels_
				if ((hiddenProps == null || !hiddenProps.contains(propName)) && !propIsHidden(propLabel, propSel, hideSel)) {
					String hint = hints != null ? (String) hints.get(propName) : null;
					boolean isFileField = formField(propDef, propLabel, propValue, prefix, hint, html);
					if (isFileField) {
						containsFileField = true;
					}
				}
			} else if (item instanceof Relation) {
				if (deep) {
					Relation rel = (Relation) item;
					if (rel.webForm.equals(WEB_FORM_TOPIC_SELECTOR)) {
						relationFormField(rel, id, prefix, propSel, hideSel, html);
					} else if (rel.webForm.equals(WEB_FORM) || rel.webForm.equals(WEB_FORM_DEEP)) {
						String relTopicTypeID = rel.relTopicTypeID;
						TypeTopic relTopicType = as.type(relTopicTypeID, 1);
						//
						String relTopicID = null;
						if (id != null) {
							Vector relTopics = as.getRelatedTopics(id, rel.assocTypeID, relTopicTypeID, 2,
								false, true);	// ordered=false, emptyAllowed=true ### relTopicPos=2 hardcoded
							if (relTopics.size() > 0) {	// ### only the first related topic is considered
								relTopicID = ((BaseTopic) relTopics.firstElement()).getID();
							}
						}
						// recursive call
						String newPrefix = prefix + PARAM_RELATION + PARAM_SEPARATOR + rel.id + LEVEL_SEPARATOR;
						boolean newDeep = rel.webForm.equals(WEB_FORM_DEEP);
						boolean isFileField = formFields(relTopicType, relTopicID, newDeep, newPrefix, formType,
							propSel, hideSel, hints, html);
						if (isFileField) {
							containsFileField = true;
						}
					} else {
						throw new DeepaMehtaException("unexpected web form mode: \"" + rel.webForm + "\"");
					}
				}
			} else {
				throw new DeepaMehtaException("unexpected object in type definition: " + item);
			}
		}
		//
		html.append("<INPUT TYPE=\"Hidden\" NAME=\"" + prefix + "id\" VALUE=\"" + id + "\">\r");	// ### id may be null
		//
		return containsFileField;
	}

	/**
	 * Renders the properties of a single topic.
	 *
	 * @see		#list
	 * @see		#info
	 * @see		#infoFields
	 */
	private void infoFields(TypeTopic type, String topicID, TypeTopic infoType, boolean deep, int layout,
													String[] propSel, boolean hideSel, String infoAction,
													String infoActionParams, StringBuffer html) {
   		Hashtable props = as.getTopicProperties(topicID, 1);
		// --- trigger hiddenProperties(type, relTopicTypeID) hook ---
		Vector hiddenProps = as.triggerHiddenProperties(infoType, type.getID());		// may return null
		// --- create info fields (LAYOUT_COLS: <tr>, LAYOUT_ROWS: <td>) ---
		Enumeration items = type.getDefinition().elements();
		while (items.hasMoreElements()) {
			OrderedItem item = (OrderedItem) items.nextElement();
			if (item instanceof PropertyDefinition) {
				PropertyDefinition propDef = (PropertyDefinition) item;
				String propName = propDef.getPropertyName();
				String propLabel = as.triggerPropertyLabel(propDef, infoType, type.getID());
				// Note: the hook returns _parameter names_ and the page delivers _field labels_
				if ((hiddenProps == null || !hiddenProps.contains(propName)) && !propIsHidden(propLabel, propSel, hideSel)) {
					if (infoField(propDef, propLabel, topicID, props, layout, infoAction, infoActionParams, html)) {
						infoAction = null;
					}
				}
			} else if (item instanceof Relation) {
				if (deep) {
					Relation rel = (Relation) item;
					if (rel.webInfo.equals(WEB_INFO_TOPIC_NAME)) {
						relationInfoField(rel, topicID, layout, propSel, hideSel, html);
					} else if (rel.webInfo.equals(WEB_INFO) || rel.webInfo.equals(WEB_INFO_DEEP)) {
						String relTopicTypeID = rel.relTopicTypeID;
						TypeTopic relTopicType = as.type(relTopicTypeID, 1);
						//
						String relTopicID = null;
						Vector selectedTopics = as.getRelatedTopics(topicID, rel.assocTypeID, relTopicTypeID, 2,
							false, true);	// ordered=false, emptyAllowed=true ### relTopicPos=2 hardcoded
						if (selectedTopics.size() > 0) {	// ### only the first related topic is considered
							relTopicID = ((BaseTopic) selectedTopics.firstElement()).getID();
						}
						// recursive call
						boolean newDeep = rel.webInfo.equals(WEB_INFO_DEEP);
						infoFields(relTopicType, relTopicID, infoType, newDeep, layout, propSel, hideSel, infoAction, infoActionParams, html);
					} else {
						throw new DeepaMehtaException("unexpected web info mode: \"" + rel.webInfo + "\"");
					}
				}
			} else {
				throw new DeepaMehtaException("unexpected object in type definition: " + item);
			}
		}		
	}

	/**
	 * Renders the properties of a single topic.
	 *
	 * @see		#list
	 * @see		#infoFieldsHeading
	 */
	private void infoFieldsHeading(TypeTopic type, TypeTopic infoType, boolean deep, String[] propSel, boolean hideSel,
														Action[] actions, Hashtable colWidths, StringBuffer html) {
		// --- trigger hiddenProperties(type, relTopicTypeID) hook ---
		Vector hiddenProps = as.triggerHiddenProperties(infoType, type.getID());		// may return null
		// --- create headings (<td>) ---
		Enumeration items = type.getDefinition().elements();
		while (items.hasMoreElements()) {
			OrderedItem item = (OrderedItem) items.nextElement();
			if (item instanceof PropertyDefinition) {
				PropertyDefinition propDef = (PropertyDefinition) item;
				String propName = propDef.getPropertyName();
				String propLabel = as.triggerPropertyLabel(propDef, infoType, type.getID());
				// Note: the hook returns _parameter names_ and the page delivers _field labels_
				if ((hiddenProps == null || !hiddenProps.contains(propName)) && !propIsHidden(propLabel, propSel, hideSel)) {
					infoFieldHeading(propDef, propLabel, colWidths, html);
				}
			} else if (item instanceof Relation) {
				if (deep) {
					Relation rel = (Relation) item;
					if (rel.webInfo.equals(WEB_INFO_TOPIC_NAME)) {
						relationInfoFieldHeading(rel, propSel, hideSel, colWidths, html);
					} else if (rel.webInfo.equals(WEB_INFO) || rel.webInfo.equals(WEB_INFO_DEEP)) {
						String relTopicTypeID = rel.relTopicTypeID;
						TypeTopic relTopicType = as.type(relTopicTypeID, 1);
						// recursive call
						boolean newDeep = rel.webInfo.equals(WEB_INFO_DEEP);
						infoFieldsHeading(relTopicType, infoType, newDeep, propSel, hideSel, actions, colWidths, html);
					} else {
						throw new DeepaMehtaException("unexpected web info mode: \"" + rel.webInfo + "\"");
					}
				}
			} else {
				throw new DeepaMehtaException("unexpected object in type definition: " + item);
			}
		}
	}

	// ---

	/**
	 * Renders the input field for the specified property.
	 *
	 * @param	propValue	may be <CODE>null</CODE>
	 * @param	hint		may be <CODE>null</CODE>
	 *
	 * @return	<code>true</code> if the input field is of type "file", <code>false</code> otherwise.
	 *
	 * @see		#formFields
	 */
	private boolean formField(PropertyDefinition propDef, String propLabel, String propValue, String prefix,
																					String hint, StringBuffer html) {
		boolean isFileField = false;		// return variable
		String propName = propDef.getPropertyName();
		String visual = propDef.getVisualization();
		// skip hidden properties
		if (visual.equals(VISUAL_HIDDEN)) {
			return isFileField;
		}
		// process hint
		int rows;
		if (hint != null) {
			String[] s = hint.split(":");
			if (s[0].equals("rows")) {
				rows = Integer.parseInt(s[1]);
			} else {
				System.out.println("*** HTMLGenerator.formField(): unexpected hint for property \"" + propName + "\": \"" + s[0] + "\"");
				rows = TEXTAREA_HEIGHT;
			}
		} else {
			rows = TEXTAREA_HEIGHT;
		}
		//
		html.append("<TR VALIGN=\"TOP\"><TD><SMALL>" + propLabel + "</SMALL></TD><TD>");
		//
		if (visual.equals(VISUAL_FIELD) || visual.equals(VISUAL_PASSWORD_FIELD)) {
			html.append("<INPUT TYPE=\"Text\" NAME=\"" + prefix + propName + "\" VALUE=\"" +
				(propValue != null ? propValue : "") + "\" SIZE=" + INPUTFIELD_WIDTH + ">");
		} else if (visual.equals(VISUAL_AREA) || visual.equals(VISUAL_TEXT_EDITOR)) {
			html.append("<TEXTAREA NAME=\"" + prefix + propName + "\" ROWS=" + rows +
				" COLS=" + TEXTAREA_WIDTH + ">" + (propValue != null ? propValue : "") + "</TEXTAREA>");
		} else if (visual.equals(VISUAL_CHOICE)) {
			choice(propDef.getOptions(), prefix + propName, propValue, html);
		} else if (visual.equals(VISUAL_RADIOBUTTONS)) {
			radioButtons(propDef.getOptions(), prefix + propName, propValue, html);
		} else if (visual.equals(VISUAL_SWITCH)) {
			checkbox(prefix + propName, SWITCH_ON.equals(propValue), html);
		} else if (visual.equals(VISUAL_DATE_CHOOSER)) {
			String year, month, day;
			if (propValue == null) {
				year = month = day = VALUE_NOT_SET;
			} else {
				StringTokenizer st = new StringTokenizer(propValue, DATE_SEPARATOR);
				year = st.nextToken();
				month = st.nextToken();
				day = st.nextToken();
			}
			yearChooser(propName, year, prefix, html);
			monthChooser(propName, month, prefix, html);
			dayChooser(propName, day, prefix, html);
		} else if (visual.equals(VISUAL_TIME_CHOOSER)) {
			String hour, minute;
			if (propValue == null) {
				hour = minute = VALUE_NOT_SET;
			} else {
				StringTokenizer st = new StringTokenizer(propValue, TIME_SEPARATOR);
				hour = st.nextToken();
				minute = st.nextToken();
			}
			hourChooser(propName, hour, prefix, html);
			minuteChooser(propName, minute, prefix, html);
		} else if (visual.equals(VISUAL_FILE_CHOOSER)) {
			html.append("<INPUT TYPE=\"file\" NAME=\"" + prefix + propName + "\" VALUE=\"" +
				(propValue != null ? propValue : "") + "\" SIZE=" + INPUTFIELD_WIDTH + ">");	// ### value, size
			isFileField = true;
		} else {
			throw new DeepaMehtaException("unexpected property visualization: " + visual);
		}
		//
		html.append("</TD></TR>\r");
		//
		return isFileField;
	}

	private boolean infoField(PropertyDefinition propDef, String propLabel, String topicID, Hashtable props, int layout,
											String infoAction, String infoActionParams, StringBuffer html) {
		boolean infolinkAdded = false;
		//
		String propName = propDef.getPropertyName();
		String visual = propDef.getVisualization();
		// skip hidden properties
		if (visual.equals(VISUAL_HIDDEN)) {
			return infolinkAdded;
		}
		//
		String propValue = (String) props.get(propName);
		if (visual.equals(VISUAL_SWITCH)) {
			propValue = SWITCH_ON.equals(propValue) ? SWITCH_ON : SWITCH_OFF;
		}
		//
		if (layout == LAYOUT_COLS) {
			html.append("<TR VALIGN=\"TOP\"><TD WIDTH=150><SMALL>" + propLabel + "</SMALL></TD>");
		}
		//
		html.append("<TD>");
		infolinkAdded = link(infoAction, infoActionParams, topicID, propValue, html);
		html.append("</TD>");
		//
		if (layout == LAYOUT_COLS) {
			html.append("</TR>\r");
		}
		//
		return infolinkAdded;
	}

	private void infoFieldHeading(PropertyDefinition propDef, String propLabel, Hashtable colWidths, StringBuffer html) {
		String propName = propDef.getPropertyName();
		String visual = propDef.getVisualization();
		// skip hidden properties
		if (visual.equals(VISUAL_HIDDEN)) {
			return;
		}
		//
		html.append("<TD" + width(propName, colWidths) + "><SMALL>" + propLabel + "</SMALL></TD>");
	}

	// ---

	/**
	 * Generates a selectable list of topics (<code>&lt;select></code>) and appends it to the specified buffer.
	 *
	 * @param	topicID		may be <CODE>null</CODE>
	 *
	 * @see		formFields
	 */
	private void relationFormField(Relation rel, String topicID, String prefix, String[] propSel, boolean hideSel,
																									StringBuffer html) {
		// ### compare to CorporateCommands.addRelationCommand()
		String relName = rel.name;
		String relTopicTypeID = rel.relTopicTypeID;
		String cardinality = rel.cardinality;
		String assocTypeID = rel.assocTypeID;
		//
		TypeTopic relTopicType = as.type(relTopicTypeID, 1);
		boolean many = cardinality.equals(CARDINALITY_MANY);
		String fieldLabel = !relName.equals("") ? relName : many ? relTopicType.getPluralNaming() : relTopicType.getName();
		//
		// ### Note: relation fields are identified "by label"
		if (propIsHidden(fieldLabel, propSel, hideSel)) {
			return;
		}
		//
		Vector topics = as.cm.getTopics(relTopicTypeID);	// ### can be very big
		Vector selectedTopics = as.getRelatedTopics(topicID, assocTypeID, relTopicTypeID, 2,
			false, true);	// ordered=false, emptyAllowed=true ### relTopicPos=2 hardcoded
		Vector selectedTopicIDs = DeepaMehtaUtils.topicIDs(selectedTopics);
		//
		html.append("<TR VALIGN=\"TOP\"><TD><SMALL>" + fieldLabel + "</SMALL></TD><TD>");
		html.append("<SELECT NAME=\"" + prefix + PARAM_RELATION + PARAM_SEPARATOR + rel.id + "\"" +
			(many ? " SIZE=" + topics.size() /* ### MULTIPLE_SELECT_HEIGHT */ + " MULTIPLE" : "") + ">\r");
		if (!many) {
			option(VALUE_NOT_SET, html);
		}
		Enumeration e = topics.elements();
		while (e.hasMoreElements()) {
			BaseTopic topic = (BaseTopic) e.nextElement();
			option(topic.getName(), topic.getID(), selectedTopicIDs, html);
		}
		html.append("</SELECT>\r");
		html.append("</TD></TR>\r");
	}

	/**
	 * @see		infoFields
	 */
	private void relationInfoField(Relation rel, String topicID, int layout, String[] propSel, boolean hideSel,
																			 StringBuffer html) {
		// ### compare to CorporateCommands.addRelationCommand()
		String relName = rel.name;
		String relTopicTypeID = rel.relTopicTypeID;
		String cardinality = rel.cardinality;
		String assocTypeID = rel.assocTypeID;
		//
		TypeTopic relTopicType = as.type(relTopicTypeID, 1);
		boolean many = cardinality.equals(CARDINALITY_MANY);
		String fieldLabel = !relName.equals("") ? relName : many ? relTopicType.getPluralNaming() : relTopicType.getName();
		//
		// ### Note: relation fields are identified "by label"
		if (propIsHidden(fieldLabel, propSel, hideSel)) {
			return;
		}
		//
		Vector selectedTopics = as.getRelatedTopics(topicID, assocTypeID, relTopicTypeID, 2,
			false, true);	// ordered=false, emptyAllowed=true ### relTopicPos=2 hardcoded
		// --- generate name and value ---
		if (layout == LAYOUT_COLS) {
			html.append("<TR VALIGN=\"TOP\"><TD><SMALL>" + fieldLabel + "</SMALL></TD>");
		}
		html.append("<TD>");
		Enumeration e = selectedTopics.elements();
		while (e.hasMoreElements()) {
			BaseTopic topic = (BaseTopic) e.nextElement();
			html.append(imageTag(topic) + " " + topic.getName() + "<BR>\r");
		}
		html.append("</TD>");
		//
		if (layout == LAYOUT_COLS) {
			html.append("</TR>\r");
		}
	}

	/**
	 * @see		infoFieldsHeading
	 */
	private void relationInfoFieldHeading(Relation rel, String[] propSel, boolean hideSel, Hashtable colWidths,
																							StringBuffer html) {
		// ### compare to relationInfoField() (above)
		String relName = rel.name;
		String relTopicTypeID = rel.relTopicTypeID;
		String cardinality = rel.cardinality;
		//
		TypeTopic relTopicType = as.type(relTopicTypeID, 1);
		String relTypeName = relTopicType.getName();
		boolean many = cardinality.equals(CARDINALITY_MANY);
		String fieldLabel = !relName.equals("") ? relName : many ? relTopicType.getPluralNaming() : relTypeName;
		//
		// ### Note: relation fields are identified "by label"
		if (propIsHidden(fieldLabel, propSel, hideSel)) {
			return;
		}
		// ### Note: here the name of the related type is used
		html.append("<TD" + width(relTypeName, colWidths) + "><SMALL>" + fieldLabel + "</SMALL></TD>");
	}

	// --- link (3 forms) ---

	private boolean link(String action, String topicID, String text, boolean bold, StringBuffer html) {
		return link(action, null, topicID, text, bold, html);
	}

	private boolean link(String action, String params, String topicID, String text, StringBuffer html) {
		return link(action, params, topicID, text, false, html);
	}

	private boolean link(String action, String params, String topicID, String text, boolean bold, StringBuffer html) {
		boolean linkAdded = false;
		//
		if (action != null) {
			html.append("<A HREF=\"controller?action=" + action + (topicID != null ? "&id=" + topicID : "") +
				(params != null ? "&" + params : "") + "\">");
			linkAdded = true;
		}
		if (text != null) {
			text = DeepaMehtaUtils.replace(text, '\r', "<br>");		// needed for "Multiline Input Field"
			html.append((bold ? "<b>" : "") + text + (bold ? "</b>" : ""));
		}
		if (action != null) {
			html.append("</A>");
		}
		//
		return linkAdded;
	}

	// --- imageLink (2 forms) ---

	private void imageLink(String action, String image1, String image2, boolean cond, StringBuffer html) {
		imageLink(action, null, image1, image2, cond, html);
	}

	private void imageLink(String action, String params, String image1, String image2, boolean cond, StringBuffer html) {
		if (cond) {
			html.append("<A HREF=\"controller?action=" + action + (params != null ? "&" + params : "") + "\">");
			html.append("<IMG SRC=\"images/" + image1 + "\" BORDER=0 HSPACE=10>");
			html.append("</A>");
		} else {
			html.append("<IMG SRC=\"images/" + image2 + "\" BORDER=0 HSPACE=10>");
		}
	}

	// ---

	private void staticLink(String text, String url, String target, StringBuffer html) {
		html.append("<A HREF=\"" + url + "\"" + (target != null ? " TARGET=\"" + target + "\"" : "") + ">" + text + "</A>");
	}

	// ---

	private void pager(int pageNr, int pageSize, int totalCount, String extendedParams, StringBuffer html) {
		pageNr++;
		int pageMax = (totalCount + pageSize - 1) / pageSize;
		//
		html.append("<TR><TD ALIGN=\"CENTER\"><BR>");
		imageLink(ACTION_PREV_PAGE, extendedParams, "arrow_left.gif", "arrow_left_disabled.gif", pageNr > 1, html);
		html.append("<SMALL>" + pageNr + "/" + pageMax + "</SMALL>");
		imageLink(ACTION_NEXT_PAGE, extendedParams, "arrow.gif", "arrow_disabled.gif", pageNr < pageMax, html);
		html.append("</TD></TR>");
	}

	private String width(String propName, Hashtable colWidths) {
		// column width
		String width = "";
		if (colWidths != null) {
			String w = (String) colWidths.get(propName);
			if (w != null) {
				width = " WIDTH=" + w;
			}
		}
		return width;
	}

	// ---

	/**
	 * @param	actions		may be <CODE>null</CODE>
	 *
	 * @see		#list
	 * @see		#treeNode
	 */
	private void actionButtons(String topicID, Action[] actions, StringBuffer html) {
		if (actions == null) {
			return;
		}
		//
		for (int i = 0; i < actions.length; i++) {
			actionButton(topicID, actions[i], html);
		}
	}

	private void actionNames(Action[] actions, StringBuffer html) {
		if (actions == null) {
			return;
		}
		//
		String space = DeepaMehtaUtils.nTimes("&nbsp;", 5);
		for (int i = 0; i < actions.length; i++) {
			String name = actions[i].name;
			html.append("<TD><SMALL>" + (name != null ? name + space : "") + "</SMALL></TD>");
		}
	}

	// ---

	private void submitButton(String action, StringBuffer html) {
		html.append("<TR VALIGN=\"BOTTOM\"><TD></TD><TD HEIGHT=50>\r");
		html.append("<INPUT TYPE=\"Submit\" VALUE=\"OK\">\r");
		html.append("<INPUT TYPE=\"Hidden\" NAME=\"action\" VALUE=\"" + action + "\">\r");
		html.append("</TD></TR>\r");
	}

	// ---

	// ### to be dropped
	private void extraTopics(Vector topics, StringBuffer html) {
		html.append("<td>");
		for (int i = 0; i < topics.size(); i++) {
			BaseTopic topic = (BaseTopic) topics.elementAt(i);
			html.append(imageTag(topic, true));		// withTooltip=true
		}
		html.append("</td>");
	}

	// ---

	private void choice(Vector options, String name, String propValue, StringBuffer html) {
		html.append("<SELECT NAME=\"" + name + "\">\r");
		Enumeration e = options.elements();
		while (e.hasMoreElements()) {
			BaseTopic topic = (BaseTopic) e.nextElement();
			option(topic.getName(), propValue, html);
		}
		html.append("</SELECT>\r");
	}

	private void radioButtons(Vector options, String name, String propValue, StringBuffer html) {
		Enumeration e = options.elements();
		while (e.hasMoreElements()) {
			BaseTopic topic = (BaseTopic) e.nextElement();
			html.append("<input type=\"radio\" name=\"" + name + "\" value=\"" + topic.getName() + "\"" +
				(topic.getName().equals(propValue) ? " checked" : "") + ">&nbsp;" + topic.getName() +
				"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\r");
		}
	}

	// --- checkbox (2 forms) ---

	private void checkbox(String name, boolean selected, StringBuffer html) {
		checkbox(name, null, selected, html);
	}

	private void checkbox(String name, String value, boolean selected, StringBuffer html) {
		html.append("<INPUT TYPE=\"Checkbox\" NAME=\"" + name + "\"" +
			(value != null ? " VALUE=\"" + value + "\"" : "") + (selected ? " CHECKED" : "") + ">");
	}

	// ---

	// ### compare to PresentationPropertyDefinition#createDateChooser
	private void yearChooser(String propName, String selectedYear, String prefix, StringBuffer html) {
		html.append("<SELECT NAME=\"" + prefix + propName + PARAM_SEPARATOR + "0\">\r");
		option(VALUE_NOT_SET, selectedYear, html);
		for (int i = YEAR_MAX; i >= YEAR_MIN; i--) {
			String year = Integer.toString(i);
			option(year, selectedYear, html);
		}
		html.append("</SELECT>\r");
	}

	// ### compare to PresentationPropertyDefinition#createDateChooser
	private void monthChooser(String propName, String selectedMonth, String prefix, StringBuffer html) {
		html.append("<SELECT NAME=\"" + prefix + propName + PARAM_SEPARATOR + "1\">\r");
		option(VALUE_NOT_SET, selectedMonth, html);
		for (int i = 0; i < 12; i++) {
			String monthVal = DeepaMehtaUtils.align(Integer.toString(i + 1));
			option(monthNames[i], monthVal, selectedMonth, html);
		}
		html.append("</SELECT>\r");
	}

	// ### compare to PresentationPropertyDefinition#createDateChooser
	private void dayChooser(String propName, String selectedDay, String prefix, StringBuffer html) {
		html.append("<SELECT NAME=\"" + prefix + propName + PARAM_SEPARATOR + "2\">\r");
		option(VALUE_NOT_SET, selectedDay, html);
		for (int i = 1; i <= 31; i++) {
			String day = Integer.toString(i);
			String dayVal = DeepaMehtaUtils.align(day);
			option(day, dayVal, selectedDay, html);
		}
		html.append("</SELECT>\r");
	}

	// ---

	// ### compare to PresentationPropertyDefinition#createTimeChooser
	private void hourChooser(String propName, String selectedHour, String prefix, StringBuffer html) {
		html.append("<SELECT NAME=\"" + prefix + propName + PARAM_SEPARATOR + "0\">\r");
		option(VALUE_NOT_SET, selectedHour, html);
		for (int i = 0; i <= 23; i++) {
			String hour = Integer.toString(i);
			String hourVal = DeepaMehtaUtils.align(hour);
			option(hour, hourVal, selectedHour, html);
		}
		html.append("</SELECT>\r");
	}

	// ### compare to PresentationPropertyDefinition#createTimeChooser
	private void minuteChooser(String propName, String selectedMinute, String prefix, StringBuffer html) {
		html.append("<SELECT NAME=\"" + prefix + propName + PARAM_SEPARATOR + "1\">\r");
		option(VALUE_NOT_SET, selectedMinute, html);
		for (int i = 0; i <= 55; i += 5) {
			String minute = DeepaMehtaUtils.align(Integer.toString(i));
			option(minute, selectedMinute, html);
		}
		html.append("</SELECT>\r");
	}

	// --- option (5 forms) ---

	private void option(String option, StringBuffer html) {
		option(option, null, html);
	}

	private void option(String option, String selectedValue, StringBuffer html) {
		option(option, null, selectedValue, html);
	}

	/**
	 * @param	value			may be <CODE>null</CODE>
	 * @param	selectedValue	may be <CODE>null</CODE>
	 */
	private void option(String option, String value, String selectedValue, StringBuffer html) {
		boolean selected = (value != null ? value : option).equals(selectedValue);
		option(option, value, selected, html);
	}

	private void option(String option, String value, Vector selectedValues, StringBuffer html) {
		boolean selected = selectedValues.contains(value != null ? value : option);
		option(option, value, selected, html);
	}

	private void option(String option, String value, boolean selected, StringBuffer html) {
		html.append("<OPTION" + (value != null ? " VALUE=\"" + value + "\"" : "") +
			(selected ? " SELECTED" : "") + ">" + option + "</OPTION>\r");
	}

	// ---

	private boolean propIsHidden(String prop, String[] propSel, boolean hideSel) {
		if (propSel == null) {
			return false;
		}
		//
		for (int i = 0; i < propSel.length; i++) {
			if (propSel[i].equals(prop)) {
				return hideSel;
			}
		}
		//
		return !hideSel;
	}
}
