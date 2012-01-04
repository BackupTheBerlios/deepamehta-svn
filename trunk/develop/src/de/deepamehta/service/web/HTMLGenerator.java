package de.deepamehta.service.web;

import de.deepamehta.BaseAssociation;
import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.Directive;
import de.deepamehta.OrderedItem;
import de.deepamehta.PropertyDefinition;
import de.deepamehta.Relation;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.TopicBean;
import de.deepamehta.service.TopicBeanField;
import de.deepamehta.topics.AssociationTypeTopic;
import de.deepamehta.topics.TopicTypeTopic;
import de.deepamehta.topics.TypeTopic;
import de.deepamehta.util.DeepaMehtaUtils;

import java.text.ChoiceFormat;
import java.text.Format;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;



/**
 * <p>
 * <hr>
 * Last change: 03.01.2012 (2.0b8e)<br>
 * J&ouml;rg Richter / Malte Rei&szlig;ig<br>
 * jri@deepamehta.de / mre@deepamehta.de
 */
public class HTMLGenerator implements DeepaMehtaConstants {



	// *************
	// *** Field ***
	// *************



	ApplicationService as;
	Locale locale;
	Vector resources = new Vector();	// base type is ResourceBundle

	String[] notificationClass = {null, "notification-info", "notification-warning", "notification-error"};



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

	// ---

	/**
	 * Renders a list of notifications.
	 *
	 * @param	notifications	a vector of {@link de.deepamehta.service.web.Notification} objects
	 *
	 * @return	a nested <code>DIV</code> element, or empty string if the vector contains no elements
	 */
	public String notification(Vector notifications) {
		StringBuffer html = new StringBuffer(notifications.size() > 0 ? "<div class=\"notification\">\r": "");
		//
		Enumeration e = notifications.elements();
		while (e.hasMoreElements()) {
			Notification notification = (Notification) e.nextElement();
			html.append("<div class=\"" + notificationClass[notification.type] + "\">" + notification.message + "</div>\r");
		}
		html.append(notifications.size() > 0 ? "</div>\r" : "");
		//
		return html.toString();
	}

	// --- list (15 forms) ---

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

	public String list(Vector topics, String selectedID, String[] propSel, boolean hideSel, String infoAction,
																								boolean doZebraStriping) {
		return list(topics, selectedID, propSel, hideSel, null, infoAction, null, null, null, doZebraStriping);
	}

	public String list(Vector topics, String selectedID, String[] propSel, Action[] actions, String infoAction,
																			String infoActionParams, Hashtable colWidths) {
		return list(topics, selectedID, propSel, false, actions, infoAction, infoActionParams, colWidths);
	}

	public String list(Vector topics, String selectedID, String[] propSel, boolean hideSel, Action[] actions, String infoAction,
																			String infoActionParams, Hashtable colWidths) {
		return list(topics, selectedID, propSel, hideSel, actions, infoAction, infoActionParams, colWidths, null);
	}

	public String list(Vector topics, String selectedID, String[] propSel, boolean hideSel, Action[] actions, String infoAction,
									String infoActionParams, Hashtable colWidths, Hashtable extraTopics) {
		return list(topics, selectedID, propSel, hideSel, actions, infoAction, infoActionParams, colWidths, extraTopics, false);
	}

	/**
	 * Renders a list of topics as a table, one row per topic, one column per property.
	 *
	 * @param	topics	list of topics (BaseTopics) or topic IDs (Strings)
	 */
	public String list(Vector topics, String selectedID, String[] propSel, boolean hideSel, Action[] actions, String infoAction,
									String infoActionParams, Hashtable colWidths, Hashtable extraTopics, boolean doZebraStriping) {
		if (topics.size() == 0) {
			return "";
		}
		//
		StringBuffer html = new StringBuffer();
		Object o = topics.firstElement();
		TypeTopic type = as.type(o instanceof BaseTopic ? (BaseTopic) o : as.cm.getTopic((String) o, 1));
		//
		html.append("<table>\r");
		html.append("<tr valign=\"top\">");
		if (extraTopics != null) {
			html.append("<td></td>");
		}
		infoFieldsHeading(type, type, true, propSel, hideSel, actions, colWidths, html);	// deep=true
		actionNames(actions, html);
  		html.append("</tr>\r");
  		//
		for (int i = 0; i < topics.size(); i++) {
			o = topics.elementAt(i);
			String topicID = o instanceof BaseTopic ? ((BaseTopic) o).getID() : (String) o;
			String classAttr = topicID.equals(selectedID) ? " class=\"list-highlight\"" : doZebraStriping ?
				i % 2 == 0 ? " class=\"list-evenrow\"" : " class=\"list-oddrow\"" : "";
   			html.append("<tr valign=\"top\"" + classAttr + ">");
			if (extraTopics != null) {
				extraTopics((Vector) extraTopics.get(topicID), html);
			}
			infoFields(type, topicID, type, true, LAYOUT_ROWS, propSel, hideSel, infoAction, infoActionParams, html);	// deep=true
			actionButtons(topicID, actions, html);
   			html.append("</tr>\r");
		}
		html.append("</table>\r");
		//
		return html.toString();
	}
	
	/**
	 * Renders a list of topics as a table, one row per topic, one column per property. 
	 * * sortable if TopicBeanField.TYPE_SINGLE
	 * * sortable per default
	 * sortBy Action uses the given TopicBeanField Identifier as sortField, see listBeanHeadings()
	 * renders editAction into Name of Topic (1 Column) (can be passed within the action)
	 * propSel and hideSel is the same like in former list methods,
	 * 
	 * @param topicBeans
	 * @param selectedID
	 * @param propSel
	 * @param hideSel
	 * @param action
	 * @param deleteAction - has to be not null! an empty String hides this 
     * function and a given string is the action rendered on a submit button
     * additionally this submit button calls a JavaScript function, namely
     * confirmDelete() which wether sends the delete request or cancels it.
     * @param doZebraStriping
	 * @return HTML List of all TopicBeans
	 */
	public String listTopicBeans(Vector topicBeans, String selectedID, String[] propSel, String[] propContaining, boolean hideSel,
									String nameAction, String sortAction, String deleteAction, boolean doZebraStriping, String highlightColumnHeader) {
		if (topicBeans.size() == 0) {
			return "";
		}
		hideBeanFields(topicBeans, propSel, propContaining, hideSel);
		StringBuffer html = new StringBuffer();
		html.append("<table>\r");
		html.append("<tr valign=\"top\">");
		TopicBean firstBean = (TopicBean) topicBeans.get(0);
		// ### Note: The firstBean entered into listBeanHeadings provides the name and number of the rendered columnHeaders
		listBeanHeadings(html, firstBean, highlightColumnHeader, sortAction, deleteAction);
		html.append("</tr>");
		//
		for (int i = 0; i < topicBeans.size(); i++) {
			TopicBean topic = (TopicBean) topicBeans.elementAt(i);
			String classAttr = topic.id.equals(selectedID) ? " class=\"list-highlight\"" : doZebraStriping ?
				i % 2 == 0 ? " class=\"list-evenrow\"" : " class=\"list-oddrow\"" : "";
   			html.append("<tr valign=\"top\"" + classAttr + ">");
			listBeanFields(html, topic, nameAction, deleteAction);
			html.append("</tr>\r");
		}
		html.append("</table>");
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
	 * Renders an image button as one <CODE>td</CODE> tag.
	 */
	public String actionButton(String topicID, String action, String iconfile) {
		return "<td width=50><a href=\"controller?action=" + action + "&id=" + topicID +
			"\"><img src=\"images/" + iconfile + "\" border=0></a></td>";
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
		html.append("<table>\r");
		infoFields(type, topicID, type, true, LAYOUT_COLS, propSel, hideSel, null, null, html);		// deep=true
		html.append("</table>\r");
		//
		return html.toString();
	}

	// --- info (2 forms) ---
	
	public String info(TopicBean topicBean) {
		return info(topicBean, DeepaMehtaConstants.LAYOUT_STYLE_2COLUMN);
	}
	
	public String info(TopicBean topicBean, int layoutStyle) {
		StringBuffer html = new StringBuffer("");
		if (layoutStyle == DeepaMehtaConstants.LAYOUT_STYLE_2COLUMN){
			html.append("<table border=\"0\" class=\"info-container\">\r");
			Enumeration e = topicBean.fields.elements();
			while (e.hasMoreElements()) {
				TopicBeanField field = (TopicBeanField) e.nextElement();
				infoField2ColumnLayout(field, html);
			}
			html.append("</table>\r");
			return html.toString();
		} else if (layoutStyle == DeepaMehtaConstants.LAYOUT_STYLE_FLOW) {
			html.append("<div class=\"info-container\">\r");
			Enumeration e = topicBean.fields.elements();
			while (e.hasMoreElements()) {
				TopicBeanField field = (TopicBeanField) e.nextElement();
				infoFieldFlowLayout(field, html);
			}
			html.append("</div>\r");
			return html.toString();
		} else {
			throw new DeepaMehtaException("unxepected Bean Layout: " + layoutStyle);
		}
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
		html.append("<form>\r");	// ### method="post" enctype="multipart/form-data"
		html.append("<table>\r");
		//
		boolean containsFileField = formFields(type, id, true, "", type, propSel, hideSel, hints, html);	// deep=true, prefix=""
		// ### System.out.println(">>> HTMLGenerator.form(): containsFileField=" + containsFileField);
		if (containsFileField) {
			html.insert(5, " method=\"post\" enctype=\"multipart/form-data\"");
		}
		submitButton(action, html);
		//
		html.append("</table>\r");
		html.append("</form>\r");
		//
		return html.toString();
	}

	/**
	 * Generates and returns a form for the specified type.
	 * The type can be a topic type or an association type.
	 *
	 * @param	id		may be <CODE>null</CODE>
	 */
	public String extendedForm(String typeID, String action, String[] propSel, boolean hideSel, String ext) {
		StringBuffer html = new StringBuffer();
    		TypeTopic type = as.type(typeID, 1);	// throws DME
		//
		html.append("<form>\r");	// ### method="post" enctype="multipart/form-data"
    		// insert html styled extension into the form...
    		html.append(ext);
		html.append("<table>\r");
		//
		boolean containsFileField = formFields(type, null, true, "", type, propSel, hideSel, null, html);	// deep=true, prefix=""
		// ### System.out.println(">>> HTMLGenerator.form(): containsFileField=" + containsFileField);
		if (containsFileField) {
			html.insert(5, " method=\"post\" enctype=\"multipart/form-data\"");
		}
		submitButton(action, html);
		//
		html.append("</table>\r");
		html.append("</form>\r");
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
		html.append("<table>\r");
		// render nodes
		treeNode(tree, 0, actions, infoAction, null, null, null, extended, extendedParams, highlighted, html);
		// render pager
		if (pageNr != -1 && tree.totalChildCount > pageSize) {
			pager(pageNr, pageSize, tree.totalChildCount, extendedParams, html);
		}
		html.append("</table>\r");
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
		html.append("<form>\r");
		html.append("<table>\r");
		//
		treeNode(tree, 0, null, null, fieldname, cardinality, selected, extended, extendedParams, null, html);
		submitButton(action, html);
		//
		html.append("</table>\r");
		html.append("</form>\r");
		//
		return html.toString();
	}

	// ---

	public String topicChooser(String typeID) {
		StringBuffer html = new StringBuffer();
		Vector topics = as.cm.getTopics(typeID);	// ### can be very big
		//
		html.append("<select name=\"id\">\r");
		Enumeration e = topics.elements();
		while (e.hasMoreElements()) {
			BaseTopic topic = (BaseTopic) e.nextElement();
			option(topic.getName(), topic.getID(), false, html);	// selected=false
		}
		html.append("</select>\r");
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
			html.append("<tr" + (selectedTopicIDs.contains(topic.getID()) ? " bgcolor=\"#E0D8D0\"" : "") + "><td>" +
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
		link(action, params, null, text, false, true, html);	// topicID=null, quoteHTML=false, bold=true
		//
		return html.toString();
	}

	// ---

	public String linkList(Vector topics, String action) {
		StringBuffer html = new StringBuffer();
		Enumeration e = topics.elements();
		while (e.hasMoreElements()) {
			BaseTopic topic = (BaseTopic) e.nextElement();
			link(action, null, topic.getID(), topic.getName(), false, true, html);	// params=null, quoteHTML=false, bold=true
			html.append("<p>");
		}
		//
		return html.toString();
	}

     /**
	 * Copy of the linkList function which renders an extra button with row styles in a table
     * the former method call should be ### delegated to this implementations instead of doubled code
	 *
     * The extraButton triggers a javaScript function with the specific name confirmDelete()
     * make sure that this method is available and
     *
     * @param topics
     * @param action
     * @param extraButton
     * @return
     */
    public String linkList(Vector topics, String action, String buttonAction) {
		StringBuffer html = new StringBuffer();
		Enumeration e = topics.elements();
		html.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\">");
		int i = 0;
		while (e.hasMoreElements()) {
			BaseTopic topic = (BaseTopic) e.nextElement();
			String classAttr = i % 2 == 0 ? " class=\"list-evenrow\"" : " class=\"list-oddrow\"";
   			html.append("<tr valign=\"top\"" + classAttr + ">");
				html.append("<td>");
			link(action, null, topic.getID(), topic.getName(), false, false, html);	// params=null, quoteHTML=false, bold=true
				html.append("</td><td>");
            html.append("<form name=\"delete"+topic.getID()+"\" method=\"GET\" action=\"controller\" " +
				"onsubmit=\"return confirmDelete()\">");
			html.append("<input type=\"hidden\" name=\"action\" value=\"" + buttonAction + "\">\n");
			html.append("<input type=\"hidden\" name=\"id\" value=\"" + topic.getID() + "\">\n");
			html.append("<input type=\"submit\" value=\"Eintrag l&ouml;schen\" ></form>");
			html.append("</tr>");
			i++;
		}
		html.append("</table>");
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
		// ### alt=\"" + topic.getName() + "\" removed for kiezatlas, list servlet excel updates
		String iconfile = as.getLiveTopic(topic).getIconfile();
		return "<img src=\"" + as.getCorporateWebBaseURL() + FILESERVER_ICONS_PATH + iconfile + "\"" +
			" style=\"vertical-align: middle; border-style: none\"" +
			(withTooltip ? " title=\"" + topic.getName() + "\"" : "") + ">";
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
				html.append("<form>");
				html.append("<table>");
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
							html.append("<tr><td colspan=2><hr></td></tr>");
						} else if (kind.equals(LAYOUT_ELEMENT_SPACE)) {
							html.append("<tr><td><br><br></td></tr>");
						} else if (kind.equals(LAYOUT_ELEMENT_COMMENT)) {
							String comment = (String) layoutElement.elementAt(1);
							html.append("<tr><td colspan=2>" + comment + "</td></tr>");
						}
					}
				}
				html.append(submitButton(ACTION_PROCESS_FREEFORM));
				html.append("</table>");
				html.append("</form>");
				break;
			case DISPLAY_TOPIC_CHOOSER:
				title = (String) display.param1;
				typeID = (String) display.param2;
				// ### html.append("<H3>" + title + "</H3>");
				html.append("<form>");
				html.append(title + topicChooser(typeID));
				html.append("<input type=\"submit\" value=\"Weiter\">");
				html.append("<input type=\"hidden\" name=\"action\" value=\"" + ACTION_SELECT_TOPIC + "\">");
				html.append("</form>");
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
			html.append("<p>");
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
			html.append("<tr" + (highlight ? " class=\"tree-highlight\"" : "") + "><td>");
			html.append(DeepaMehtaUtils.nTimes("&nbsp;", 6 * (indent - 1)));
			// arrow
			if (extended != null) {
				String action = isExtended ? ACTION_COLLAPSE_NODE : ACTION_EXTEND_NODE;
				html.append(tree.getChildCount() > 0 ? "<a href=\"controller?action=" + action + "&id=" + topicID +
					(extendedParams != null ? "&" + extendedParams : "") + "\"><img src=\"images/" +
					(isExtended ? "arrow_down" : "arrow") + ".gif\" border=0 hspace=10></a>" :
					"<img src=\"images/arrow_empty.gif\" border=0 hspace=10>");
			}
			// node name
			link(infoAction, topicID, tree.topic.getName(), true, indent == 1, html);	// quoteHTML=true
			html.append("</td>");
			// buttons
			actionButtons(topicID, actions, html);
			// checkbox
			if (fieldname != null) {
				html.append("<td>");
				if (cardinality.equals(CARDINALITY_MANY)) {
					boolean isSelected = selected.contains(topicID);
					checkbox(fieldname, topicID, isSelected, html);
				} else {
					// ###
				}
				html.append("</td>");
			}
			//
			html.append("</tr>\r");
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
		// ### compare to infoFields()
		// ### compare to ApplicationService.addFieldsToTopicBean()
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
    		Hashtable describedPropertyNames = getPropertyDescriptions(formType);
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
			        String propDescription = "";
			        if (describedPropertyNames.get(propName) != null) {
			        	propDescription = describedPropertyNames.get(propName).toString();
			        }
			        String propLabel = as.triggerPropertyLabel(propDef, formType, type.getID());
				String propValue = (String) props.get(propName);
				// Note: the hook returns _parameter names_ and the page delivers _field labels_
				if ((hiddenProps == null || !hiddenProps.contains(propName)) && !propIsHidden(propLabel, propSel, hideSel)) {
					String hint = hints != null ? (String) hints.get(propName) : null;
					if (formField(propDef, propLabel, propValue, prefix, hint, html, propDescription)) {
						containsFileField = true;
					}
				}
			} else if (item instanceof Relation) {
				if (deep) {
					Relation rel = (Relation) item;
					// query selected topics
					Vector relTopics = id != null ? as.getRelatedTopics(id, rel.assocTypeID, rel.relTopicTypeID, 2,
						false, true) : new Vector();	// ordered=false, emptyAllowed=true ### relTopicPos=2 hardcoded
					//
					if (rel.webForm.equals(WEB_FORM_TOPIC_SELECTOR)) {
						relationFormField(rel, relTopics, prefix, propSel, hideSel, html);
					} else if (rel.webForm.equals(WEB_FORM) || rel.webForm.equals(WEB_FORM_DEEP)) {
						if (relationFormFields(rel, relTopics, prefix, formType, propSel, hideSel, hints, html)) {
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
		html.append("<input type=\"hidden\" name=\"" + prefix + "id\" value=\"" + id + "\">\r");	// ### id may be null
		//
		return containsFileField;
	}

	/**
	 * fetches a simple "Topic" instance related to a "Property" TopicType via a new "Helptext"-AssociationType..
	 **/
	private Hashtable getPropertyDescriptions(TypeTopic formType) {
		Hashtable descriptions = new Hashtable();
		//
    		Vector relatedProps = as.getRelatedTopics(formType.getID(), ASSOCTYPE_COMPOSITION, TOPICTYPE_PROPERTY, 2);
		// 
		Enumeration properties = relatedProps.elements();
		while (properties.hasMoreElements()) {
			BaseTopic property = (BaseTopic) properties.nextElement();
			BaseTopic note = as.getRelatedTopic(property.getID(), ASSOCTYPE_HELPTEXT, TOPICTYPE_TOPIC, 2, true);
			if (note != null) {
				String descr = as.getTopicProperty(note, PROPERTY_DESCRIPTION);
        			descr = descr.substring(descr.indexOf("<p>")+3, descr.indexOf("</p>")); // use everything within the first <p>
				descriptions.put(property.getName(), descr);
			}
		}
		return descriptions;
	}
	
	// ---
	
	/**
	 * Renders a TopicBean Field according to "2-column" layout style ({@link LAYOUT_STYLE_2COLUMN}).
	 * If some fields of TopcBeanField.TYPE_SINGLE has the Visualization Mode "FileChoose", there is a link rendered 
	 * pointing into the documents/resp. images/ repository.
	 */
	private void infoField2ColumnLayout(TopicBeanField field, StringBuffer html) {
		html.append("<tr valign=\"top\">" +
			"<td width=150><span class=\"info-label\">" + field.label + "</span></td>" +
			"<td>");
		switch (field.type) {
		case TopicBeanField.TYPE_SINGLE:
			if (field.value != null) {
				String text = DeepaMehtaUtils.transformToHTML(field.value);
				html.append("<span class=\"info-content\">"); 
				if (field.visualMode.equals(VISUAL_FILE_CHOOSER)) {
					String repositoryPath = as.getCorporateWebBaseURL();
					repositoryPath += isPngGifJpg(field.value) ? FILESERVER_IMAGES_PATH : FILESERVER_DOCUMENTS_PATH;
					html.append("<a href=\"" + repositoryPath + text + "\" target=\"_blank\">" + text + "</a>");
				} else {
					html.append(text);
				}
				html.append("<br></span>");
			}
			break;
		case TopicBeanField.TYPE_MULTI:
			Enumeration e = field.values.elements();
			html.append("<span class=\"info-content\">");
			while (e.hasMoreElements()) {
				BaseTopic topic = (BaseTopic) e.nextElement();
				String text = DeepaMehtaUtils.transformToHTML(topic.getName());
				html.append(imageTag(topic) + " " + text + "<br>");
			}
			html.append("</span>");
			break;
		default:
			throw new DeepaMehtaException("unexpected topic bean field type: " + field.type);
		}
		html.append("</td></tr>\r");
	}

	/**
	 * Renders a TopicBean Field according to "flow" layout style ({@link LAYOUT_STYLE_FLOW}).
	 */
	private void infoFieldFlowLayout(TopicBeanField field, StringBuffer html) {
		html.append("<p class=\"info\">");
		html.append("<span class=\"info-label\">" + field.label + ": </span>");
		switch (field.type) {
		case TopicBeanField.TYPE_SINGLE:
			if (field.value != null) {
				String text = DeepaMehtaUtils.transformToHTML(field.value);
				html.append("<span class=\"info-content\">"); 
				if (field.visualMode.equals(VISUAL_FILE_CHOOSER)) {
					String repositoryPath = as.getCorporateWebBaseURL();
					repositoryPath += isPngGifJpg(field.value) ? FILESERVER_IMAGES_PATH : FILESERVER_DOCUMENTS_PATH;
					html.append("<a href=\"" + repositoryPath + text + "\" target=\"_blank\">" + text + "</a>");
				} else {
					html.append(text);
				}
				html.append("<br></span>");
			}
			break;
		case TopicBeanField.TYPE_MULTI:
			Enumeration e = field.values.elements();
			html.append("<span class=\"info-content\">");
			while (e.hasMoreElements()) {
				BaseTopic topic = (BaseTopic) e.nextElement();
				String text = DeepaMehtaUtils.transformToHTML(topic.getName());
				html.append("<br>" + imageTag(topic) + " " + text);
			}
			html.append("</span>");
			break;
		default:
			throw new DeepaMehtaException("unexpected topic bean field type: " + field.type);
		}
		html.append("</p>\r");
	}
	
	/**
	 * Checks CaseInsensitive if filename contains png, gif, or jpg
	 * Used for either linking in the documents/ or images/ repos
	 * 
	 * #infoFieldFlowLayout for TopicBeanFields
	 * #infoField2ColumnLayout for TopicBeanFields
	 * 
	 * @param filename
	 * @return 
	 */
	private boolean isPngGifJpg(String filename) {
		filename = filename.toLowerCase();
		if ( filename.indexOf("png") != -1 || filename.indexOf("gif") != -1 || filename.indexOf("jpg") != -1) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Renders the fields of a single topic.
	 *
	 * @param	type		the type of the topic. Acts as "template": all fields of this type definition are rendered.
	 * @param	topicID		the ID of the topic. If null, fields are rendered with empty values.
	 *
	 * @see		#list
	 * @see		#info
	 * @see		#infoFields
	 */
	private void infoFields(TypeTopic type, String topicID, TypeTopic infoType, boolean deep, int layout,
							String[] propSel, boolean hideSel, String infoAction, String infoActionParams, StringBuffer html) {
		// ### compare to formFields()
		// ### compare to ApplicationService.addFieldsToTopicBean()
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
				String propValue = topicID != null ? as.getTopicProperty(topicID, 1, propName) : "";
				// Note: the hook returns _parameter names_ and the page delivers _field labels_
				if ((hiddenProps == null || !hiddenProps.contains(propName)) && !propIsHidden(propLabel, propSel, hideSel)) {
					if (infoField(propDef, propLabel, propValue, topicID, layout, infoAction, infoActionParams, html)) {
						infoAction = null;
					}
				}
			} else if (item instanceof Relation) {
				if (deep) {
					Relation rel = (Relation) item;
					// query selected topics
					Vector selectedTopics = topicID != null ? as.getRelatedTopics(topicID, rel.assocTypeID, rel.relTopicTypeID, 2,
						false, true) : new Vector();	// ordered=false, emptyAllowed=true ### relTopicPos=2 hardcoded
					//
					if (rel.webInfo.equals(WEB_INFO_TOPIC_NAME)) {
						relationInfoField(rel, selectedTopics, layout, propSel, hideSel, html);
					} else if (rel.webInfo.equals(WEB_INFO) || rel.webInfo.equals(WEB_INFO_DEEP)) {
						relationInfoFields(rel, selectedTopics, infoType, layout, propSel, hideSel,
							infoAction, infoActionParams, html);
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
	 * A formField backwards compatibility method without for rendering of formFields without a propDescription/help text
	 */
	private boolean formField(PropertyDefinition propDef, String propLabel, String propValue, String prefix,
        	                                String hint, StringBuffer html) {
		//
		return formField(propDef, propLabel, propValue, prefix, hint, html, "");
	}

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
						String hint, StringBuffer html, String propDescription) {
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
				System.out.println("*** HTMLGenerator.formField(): unexpected hint for property \"" +
					propName + "\": \"" + s[0] + "\"");
				rows = TEXTAREA_HEIGHT;
			}
		} else {
			rows = TEXTAREA_HEIGHT;
		}
		//
		if (propDescription != "") {
			html.append("<tr valign=\"top\"><td colspan=\"2\" class=\"form-helptext\">Hinweis: " + propDescription + "</td></tr>");
		}
		html.append("<tr valign=\"top\"><td><small>" + propLabel + "</small></td><td>");
		//
		if (visual.equals(VISUAL_FIELD) || visual.equals(VISUAL_PASSWORD_FIELD) || visual.equals(VISUAL_COLOR_CHOOSER)) {
			html.append("<input type=\"text\" name=\"" + prefix + propName + "\" value=\"" +
				(propValue != null ? DeepaMehtaUtils.encodeHTMLEntities(propValue) : "") + "\" size=" + INPUTFIELD_WIDTH + ">");
		} else if (visual.equals(VISUAL_AREA) || visual.equals(VISUAL_TEXT_EDITOR)) {
			html.append("<textarea name=\"" + prefix + propName + "\" rows=" + rows +
				" cols=" + TEXTAREA_WIDTH + ">" + (propValue != null ? propValue : "") + "</textarea>");
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
			html.append("<input type=\"file\" name=\"" + prefix + propName + "\" value=\"" +
				(propValue != null ? propValue : "") + "\" size=" + INPUTFIELD_WIDTH + ">");	// ### value, size
			isFileField = true;
		} else {
			throw new DeepaMehtaException("unexpected property visualization: " + visual);
		}
		//
		html.append("</td></tr>\r");
		//
		return isFileField;
	}

	private boolean infoField(PropertyDefinition propDef, String propLabel, String propValue, String topicID, int layout,
											String infoAction, String infoActionParams, StringBuffer html) {
		boolean infolinkAdded = false;
		//
		String visual = propDef.getVisualization();
		// skip hidden properties
		if (visual.equals(VISUAL_HIDDEN)) {
			return infolinkAdded;
		}
		//
		if (visual.equals(VISUAL_SWITCH)) {
			propValue = SWITCH_ON.equals(propValue) ? SWITCH_ON : SWITCH_OFF;
		} else if (visual.equals(VISUAL_PASSWORD_FIELD)) {
			propValue = "&bull;&bull;&bull;";
		}
		//
		if (layout == LAYOUT_COLS) {
			html.append("<tr valign=\"top\"><td width=150><small>" + propLabel + "</small></td>");
		}
		//
		html.append("<td>");
		boolean quoteHTML = !visual.equals(VISUAL_TEXT_EDITOR);
		infolinkAdded = link(infoAction, infoActionParams, topicID, propValue, quoteHTML, html);
		html.append("</td>");
		//
		if (layout == LAYOUT_COLS) {
			html.append("</tr>\r");
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
		html.append("<td" + width(propName, colWidths) + "><small>" + propLabel + "</small></td>");
	}

	// --- remove all fields by full name in all given beans
	
	private void hideBeanFields(Vector beans, String[] propSel, String[] propContaining, boolean hideSel) {
		for (int i = 0; i < beans.size(); i++) {
			TopicBean topic = (TopicBean) beans.get(i);
			for (int j = 0; j < propSel.length; j++) {
				topic.removeField(propSel[j]);
			}
			for (int k = 0; k < propContaining.length; k++) {
				topic.removeFieldsContaining(propContaining[k]);
			}
		}
	}
	
	/**
	 * Render columns in a TableData HTML Tag. Bootstrap columns by given bean and it's fields. 
	 * One Column can be highlighted if the given column name matches the TopicBeanField name attribute
	 * The given sortAction is passed to your controller and has it's value stored in the sortField parameter
	 * 
	 * @param html
	 * @param topic
	 * @param highlightColumnHeader
	 * @param sortAction
	 * @param deleteAction has to be not null! controls if there is an additonal
     * column renderd for a deletButton in a row of each topicbean
	 */
	private void listBeanHeadings(StringBuffer html, TopicBean topic, String highlightColumnHeader, String sortAction, String deleteAction) {
		Vector allFields = topic.fields;
		for (int j = 0; j < allFields.size(); j++) {
			TopicBeanField propField = (TopicBeanField) allFields.get(j); 
			// Single Type
			if (propField.type == TopicBeanField.TYPE_SINGLE)  {
				// sortable, make active column visible
				if (highlightColumnHeader != null && highlightColumnHeader.equals(propField.name)){
					html.append("<td class=\"list-highlightcolumnheader\">");
				} else {
					html.append("<td class=\"small\">");
				}
				html.append("<a href=\"controller?action="+sortAction+"&sortField="+propField.name+"\">"+ propField.label + "</a>");
			// Multi  Type
			} else {
				html.append("<td class=\"small\">");
				html.append(propField.label);
			}
			html.append("</td>");
		}
		if (!deleteAction.equals("")) {
			html.append("<td>Zusatzfunktion</td>");
		}
	}
	
	/**
     * list all fields of a given bean and can provide two actions for an entry
     *
     *
     * @param html
     * @param topic
     * @param action
     * the action string is rendered as an action onto the topic name
     * as a url-form-encoded link to the controler servlet
     * @param deleteAction - this string controls wether there is an action
     * rendered onto the topic name as a url-form-encoded link to the controler
     * servlet
     */
	private void listBeanFields(StringBuffer html, TopicBean topic, String action, String deleteAction) {
		for (int i = 0; i < topic.fields.size(); i++) {
			TopicBeanField field = (TopicBeanField) topic.fields.get(i);
			html.append("<td>");
				if (field.type == TopicBeanField.TYPE_MULTI ) {
					Vector values = topic.getValues(field.name);
					for (int j = 0; j < values.size(); j ++) {
						BaseTopic value = (BaseTopic) values.get(j);
						html.append(imageTag(value) + " " +value.getName() + "<br/>");
					}
				} else if(field.type == TopicBeanField.TYPE_SINGLE) {
					if (field.name.equals("Name") && (!action.equals(""))) {
						html.append("<a href=\"controller?action="+action+"&id="+topic.id +"\">" + field.value + "</a>");
					} else {
						html.append(field.value);
					}
				}
			html.append("</td>");
		}
		if (!deleteAction.equals("")) {
			html.append("<td>");
			html.append("<form name=\"delete"+topic.id+"\" method=\"GET\" action=\"controller\" " +
				"onsubmit=\"return confirmDelete()\">");
			html.append("<input type=\"hidden\" name=\"action\" value=\"" + deleteAction + "\">\n");
			html.append("<input type=\"hidden\" name=\"id\" value=\"" + topic.id + "\">\n");
			html.append("<input type=\"submit\" value=\"Eintrag l&ouml;schen\" ></form>");
			html.append("</td>");
			// html.append("<a href=\"alert(\"Do you really want to delete this entry!\")controller?action="+deleteAction+"&id="+topic.id + "\">Eintrag löschen</a>") ;
		}
		
	}
	
	// ---

	/**
	 * Generates a selectable list of topics (<code>&lt;select></code>) and appends it to the specified buffer.
	 *
	 * @param	topicID		may be <CODE>null</CODE>
	 *
	 * @see		formFields
	 */
	private void relationFormField(Relation rel, Vector selectedTopics, String prefix, String[] propSel, boolean hideSel,
																									StringBuffer html) {
		// ### compare to relationInfoField()
		// ### compare to ApplicationService.addRelationFieldToTopicBean
		// ### compare to CorporateCommands.addRelationCommand()
		//
		TypeTopic relTopicType = as.type(rel.relTopicTypeID, 1);
		boolean many = rel.cardinality.equals(CARDINALITY_MANY);
		String fieldLabel = !rel.name.equals("") ? rel.name : many ? relTopicType.getPluralNaming() : relTopicType.getName();
		//
		// ### Note: relation fields are identified "by label"
		if (propIsHidden(fieldLabel, propSel, hideSel)) {
			return;
		}
		// --- generate topic selector ---
		Vector topics = as.cm.getTopics(rel.relTopicTypeID);	// ### can be very big
		//
		html.append("<tr valign=\"top\"><td><small>" + fieldLabel + "</small></td><td>");
		html.append("<select name=\"" + prefix + PARAM_RELATION + PARAM_SEPARATOR + rel.id + "\"" +
			(many ? " size=" + topics.size() /* ### MULTIPLE_SELECT_HEIGHT */ + " multiple" : "") + ">\r");
		if (!many) {
			option(VALUE_NOT_SET, html);
		}
		Enumeration e = topics.elements();
		while (e.hasMoreElements()) {
			BaseTopic topic = (BaseTopic) e.nextElement();
			option(topic.getName(), topic.getID(), DeepaMehtaUtils.topicIDs(selectedTopics), html);
		}
		html.append("</select>\r");
		html.append("</td></tr>\r");
	}

	/**
	 * @return	<code>true</code> if one of the input fields is of type "file", <code>false</code> otherwise.
	 */
	private boolean relationFormFields(Relation rel, Vector selectedTopics, String prefix, TypeTopic formType,
														String[] propSel, boolean hideSel, Hashtable hints, StringBuffer html) {
		// ### compare to relationInfoFields()
		// ### compare to ApplicationService.addRelationFieldsToTopicBean
		//
		String relTopicID = null;
		if (selectedTopics.size() > 0) {
			// ### only the first related topic is considered
			relTopicID = ((BaseTopic) selectedTopics.firstElement()).getID();
		}
		TypeTopic relTopicType = as.type(rel.relTopicTypeID, 1);
		prefix = prefix + PARAM_RELATION + PARAM_SEPARATOR + rel.id + LEVEL_SEPARATOR;
		boolean deep = rel.webForm.equals(WEB_FORM_DEEP);
		// recursive call
		return formFields(relTopicType, relTopicID, deep, prefix, formType, propSel, hideSel, hints, html);
	}

	// ---

	/**
	 * @see		infoFields
	 */
	private void relationInfoField(Relation rel, Vector selectedTopics, int layout, String[] propSel, boolean hideSel,
																									StringBuffer html) {
		// ### compare to relationFormField()
		// ### compare to ApplicationService.addRelationFieldToTopicBean
		// ### compare to CorporateCommands.addRelationCommand()
		//
		TypeTopic relTopicType = as.type(rel.relTopicTypeID, 1);
		boolean many = rel.cardinality.equals(CARDINALITY_MANY);
		String fieldLabel = !rel.name.equals("") ? rel.name : many ? relTopicType.getPluralNaming() : relTopicType.getName();
		//
		// ### Note: relation fields are identified "by label"
		if (propIsHidden(fieldLabel, propSel, hideSel)) {
			return;
		}
		// --- generate topic names ---
		if (layout == LAYOUT_COLS) {
			html.append("<tr valign=\"top\"><td><small>" + fieldLabel + "</small></td>");
		}
		html.append("<td>");
		Enumeration e = selectedTopics.elements();
		while (e.hasMoreElements()) {
			BaseTopic topic = (BaseTopic) e.nextElement();
			html.append(imageTag(topic) + " " + topic.getName() + "<br>\r");
		}
		html.append("</td>");
		//
		if (layout == LAYOUT_COLS) {
			html.append("</tr>\r");
		}
	}

	/**
	 * @see		infoFields
	 */
	private void relationInfoFields(Relation rel, Vector selectedTopics, TypeTopic infoType, int layout,
							String[] propSel, boolean hideSel, String infoAction, String infoActionParams, StringBuffer html) {
		// ### compare to relationFormFields()
		// ### compare to ApplicationService.addRelationFieldsToTopicBean
		//
		String relTopicID = null;
		if (selectedTopics.size() > 0) {
			// ### only the first related topic is considered
			relTopicID = ((BaseTopic) selectedTopics.firstElement()).getID();
		}
		TypeTopic relTopicType = as.type(rel.relTopicTypeID, 1);
		boolean deep = rel.webInfo.equals(WEB_INFO_DEEP);
		// recursive call
		infoFields(relTopicType, relTopicID, infoType, deep, layout, propSel, hideSel, infoAction, infoActionParams, html);
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
		html.append("<td" + width(relTypeName, colWidths) + "><small>" + fieldLabel + "</small></td>");
	}

	// --- link (3 forms) ---

	private boolean link(String action, String topicID, String text, boolean quoteHTML, boolean bold, StringBuffer html) {
		return link(action, null, topicID, text, quoteHTML, bold, html);
	}

	private boolean link(String action, String params, String topicID, String text, boolean quoteHTML, StringBuffer html) {
		return link(action, params, topicID, text, quoteHTML, false, html);
	}

	private boolean link(String action, String params, String topicID, String text, boolean quoteHTML, boolean bold,
																										StringBuffer html) {
		boolean linkAdded = false;
		//
		if (action != null) {
			html.append("<a href=\"controller?action=" + action + (topicID != null ? "&id=" + topicID : "") +
				(params != null ? "&" + params : "") + "\">");
			linkAdded = true;
		}
		if (text != null) {
			if (quoteHTML) {
				text = DeepaMehtaUtils.encodeHTMLTags(text);
				text = DeepaMehtaUtils.replaceLF(text);		// needed for "Multiline Input Field"
			}
			html.append((bold ? "<b>" : "") + text + (bold ? "</b>" : ""));
		}
		if (action != null) {
			html.append("</a>");
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
			html.append("<a href=\"controller?action=" + action + (params != null ? "&" + params : "") + "\">");
			html.append("<img src=\"images/" + image1 + "\" border=0 hspace=10>");
			html.append("</a>");
		} else {
			html.append("<img src=\"images/" + image2 + "\" border=0 hspace=10>");
		}
	}

	// ---

	private void staticLink(String text, String url, String target, StringBuffer html) {
		html.append("<a href=\"" + url + "\"" + (target != null ? " target=\"" + target + "\"" : "") + ">" + text + "</a>");
	}

	// ---

	private void pager(int pageNr, int pageSize, int totalCount, String extendedParams, StringBuffer html) {
		pageNr++;
		int pageMax = (totalCount + pageSize - 1) / pageSize;
		//
		html.append("<tr><td align=\"center\"><br>");
		imageLink(ACTION_PREV_PAGE, extendedParams, "arrow_left.gif", "arrow_left_disabled.gif", pageNr > 1, html);
		html.append("<span class=\"tree-pager\">Page " + pageNr + "/" + pageMax + "</span>");
		imageLink(ACTION_NEXT_PAGE, extendedParams, "arrow.gif", "arrow_disabled.gif", pageNr < pageMax, html);
		html.append("</td></tr>");
	}

	private String width(String propName, Hashtable colWidths) {
		// column width
		String width = "";
		if (colWidths != null) {
			String w = (String) colWidths.get(propName);
			if (w != null) {
				width = " width=" + w;
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
			html.append("<td><small>" + (name != null ? name + space : "") + "</small></td>");
		}
	}

	// ---

	private void submitButton(String action, StringBuffer html) {
		html.append("<tr valign=\"bottom\"><td></td><td height=50>\r");
		html.append("<input type=\"submit\" value=\"OK\">\r");
		html.append("<input type=\"hidden\" name=\"action\" value=\"" + action + "\">\r");
		html.append("</td></tr>\r");
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
		html.append("<select name=\"" + name + "\">\r");
		Enumeration e = options.elements();
		while (e.hasMoreElements()) {
			BaseTopic topic = (BaseTopic) e.nextElement();
			option(topic.getName(), propValue, html);
		}
		html.append("</select>\r");
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
		html.append("<input type=\"checkbox\" name=\"" + name + "\"" +
			(value != null ? " value=\"" + value + "\"" : "") + (selected ? " checked" : "") + ">");
	}

	// ---

	// ### compare to PresentationPropertyDefinition#createDateChooser
	private void yearChooser(String propName, String selectedYear, String prefix, StringBuffer html) {
		html.append("<select name=\"" + prefix + propName + PARAM_SEPARATOR + "0\">\r");
		option(VALUE_NOT_SET, selectedYear, html);
		for (int i = YEAR_MAX; i >= YEAR_MIN; i--) {
			String year = Integer.toString(i);
			option(year, selectedYear, html);
		}
		html.append("</select>\r");
	}

	// ### compare to PresentationPropertyDefinition#createDateChooser
	private void monthChooser(String propName, String selectedMonth, String prefix, StringBuffer html) {
		html.append("<select name=\"" + prefix + propName + PARAM_SEPARATOR + "1\">\r");
		option(VALUE_NOT_SET, selectedMonth, html);
		for (int i = 0; i < 12; i++) {
			String monthVal = DeepaMehtaUtils.align(Integer.toString(i + 1));
			option(monthNames[i], monthVal, selectedMonth, html);
		}
		html.append("</select>\r");
	}

	// ### compare to PresentationPropertyDefinition#createDateChooser
	private void dayChooser(String propName, String selectedDay, String prefix, StringBuffer html) {
		html.append("<select name=\"" + prefix + propName + PARAM_SEPARATOR + "2\">\r");
		option(VALUE_NOT_SET, selectedDay, html);
		for (int i = 1; i <= 31; i++) {
			String day = Integer.toString(i);
			String dayVal = DeepaMehtaUtils.align(day);
			option(day, dayVal, selectedDay, html);
		}
		html.append("</select>\r");
	}

	// ---

	// ### compare to PresentationPropertyDefinition#createTimeChooser
	private void hourChooser(String propName, String selectedHour, String prefix, StringBuffer html) {
		html.append("<select name=\"" + prefix + propName + PARAM_SEPARATOR + "0\">\r");
		option(VALUE_NOT_SET, selectedHour, html);
		for (int i = 0; i <= 23; i++) {
			String hour = Integer.toString(i);
			String hourVal = DeepaMehtaUtils.align(hour);
			option(hour, hourVal, selectedHour, html);
		}
		html.append("</select>\r");
	}

	// ### compare to PresentationPropertyDefinition#createTimeChooser
	private void minuteChooser(String propName, String selectedMinute, String prefix, StringBuffer html) {
		html.append("<select name=\"" + prefix + propName + PARAM_SEPARATOR + "1\">\r");
		option(VALUE_NOT_SET, selectedMinute, html);
		for (int i = 0; i <= 55; i += 5) {
			String minute = DeepaMehtaUtils.align(Integer.toString(i));
			option(minute, selectedMinute, html);
		}
		html.append("</select>\r");
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

	/**
	 * @param	selectedValues	may be <CODE>null</CODE>
	 */
	private void option(String option, String value, Vector selectedValues, StringBuffer html) {
		boolean selected = selectedValues != null ? selectedValues.contains(value != null ? value : option) : false;
		option(option, value, selected, html);
	}

	private void option(String option, String value, boolean selected, StringBuffer html) {
		html.append("<option" + (value != null ? " value=\"" + value + "\"" : "") +
			(selected ? " selected" : "") + ">" + option + "</option>\r");
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
