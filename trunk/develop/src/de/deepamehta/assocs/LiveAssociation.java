package de.deepamehta.assocs;

import de.deepamehta.BaseAssociation;
import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.Detail;
import de.deepamehta.PropertyDefinition;
import de.deepamehta.service.Session;
import de.deepamehta.service.CorporateCommands;
import de.deepamehta.service.CorporateDetail;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.CorporateMemory;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.topics.TypeTopic;
//
import java.util.*;



/**
 * A server-side proxy association for a client-side presented association that "lives"
 * in {@link de.deepamehta.service.ApplicationService}.
 * <P>
 * A <CODE>LiveAssociation</CODE> has access to the {@link de.deepamehta.service.ApplicationService} and
 * to the {@link CorporateMemory}.
 * <P>
 * <HR>
 * Last sourcecode change: 11.9.2007 (2.0b8)<BR>
 * Last documentation update: 11.12.2000 (2.0a8-pre3)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class LiveAssociation extends BaseAssociation implements DeepaMehtaConstants {



	// **************
	// *** Fields ***
	// **************



	protected ApplicationService as;
	protected CorporateMemory cm;



	// ********************
	// *** Constructors ***
	// ********************



	/**
	 * @see		de.deepamehta.service.ApplicationService#createLiveAssociation
	 */
	public LiveAssociation(BaseAssociation assoc, ApplicationService as) {
		super(assoc);
		this.as = as;
		this.cm = as.cm;
	}



	// **********************
	// *** Defining Hooks ***
	// **********************



	// ------------------
	// --- Life Cycle ---
	// ------------------



	/**
	 * Subclasses can override this method to perform some work when an association is
	 * bring into live the very first time.
	 * <P>
	 * The default implementation creates the association in corporate memory.
	 *
	 * @see		de.deepamehta.service.ApplicationService#createLiveAssociation
	 */
	public void evoke() {
		// create association in corporate memory
		cm.createAssociation(getID(), getVersion(), getType(), getTypeVersion(),
			getTopicID1(), getTopicVersion1(), getTopicID2(), getTopicVersion2());
		// set default values
		Enumeration e = as.type(this).getTypeDefinition().elements();
		while (e.hasMoreElements()) {
			PropertyDefinition propDef = (PropertyDefinition) e.nextElement();
			String defaultValue = propDef.getDefaultValue();
			if (!defaultValue.equals("")) {
				String propName = propDef.getPropertyName();
				// existing values are not overridden
				if (cm.getAssociationData(getID(), getVersion(), propName).equals("")) {
					cm.setAssociationData(getID(), getVersion(), propName, defaultValue);
				}
			}			
		}
	}

	/**
	 * Triggered when an association dies.
	 * <P>
	 * The default implementation deletes the association from 3 spots ...
	 * <UL>
	 * <LI>corporate memory
	 * <LI>live corporate memory
	 * <LI>all views
	 * </UL>
	 * ... and returns empty directives.
	 *
	 * @see		de.deepamehta.service.ApplicationService#deleteAssociation
	 */
	public CorporateDirectives die() {
		// --- delete association from all views ---
		as.deleteViewAssociation(getID());
		// --- delete association and its properties from corporate memory ---
		cm.deleteAssociation(id);
		cm.deleteAssociationData(getID(), getVersion());
		// --- delete association from live corporate memory ---
		// Note: deleteAssociation() is derived from BaseTopicMap
		as.deleteAssociation(getID() + ":" + getVersion());
		//
		return new CorporateDirectives();
	}



	// ------------------------------------------
	// --- Reacting upon dedicated situations ---
	// ------------------------------------------



	public CorporateDirectives nameChanged(String name) {
		CorporateDirectives directives = new CorporateDirectives();
		directives.add(DIRECTIVE_SET_ASSOC_NAME, getID(), name, new Integer(getVersion()));
		return directives;
	}

	/**
	 * Subclasses can override this method to react upon retyping this association.
	 * <P>
	 * The default implementation performs the retyping, it should be called from subclassss.
	 *
	 * @see		de.deepamehta.service.ApplicationService#changeAssociationType
	 */
	public CorporateDirectives typeChanged(String typeID) {
		CorporateDirectives directives = new CorporateDirectives();
		directives.add(DIRECTIVE_SET_ASSOC_TYPE, getID(), typeID, new Integer(getVersion()));
		directives.add(DIRECTIVE_SELECT_ASSOCIATION, getID());
		return directives;
	}



	// --------------------------
	// --- Providing Commands ---
	// --------------------------



	/**
	 * Returns the set of standard association commands:
	 * <OL>
	 * <LI>"Retype"
	 * <LI>"Hide"
	 * <LI>"Delete"
	 * </OL>
	 * Documentation updated: 11.10.2001 (2.0a12)
	 *
	 * @param	session		the triggering client session
	 *
	 * @return	the provided context commands
	 *
	 * @see		de.deepamehta.service.ApplicationService#getAssociationCommands
	 */
	public CorporateCommands contextCommands(String topicmapID, String viewmode,
								Session session, CorporateDirectives directives) {
		CorporateCommands commands = new CorporateCommands(as);
		//
		// Note: the "Retype" and "Hide" commands are not available for "at-navigation"
		// associations ### further association type property needed
		if (!getType().equals(ASSOCTYPE_NAVIGATION)) {
			commands.addHideAssociationCommand(session);
			commands.addRetypeAssociationCommand(this, session, directives);
			commands.addDeleteAssociationCommand(this, session);
		} else {
			commands.addDeleteAssociationCommand(as.string(ITEM_REMOVE_ASSOC), FILESERVER_IMAGES_PATH, ICON_HIDE_ASSOC);
		}
		//
		return commands;
	}



	// --------------------------
	// --- Executing Commands ---
	// --------------------------



	/**
	 * Subclasses can override this method to supply an action behavoir.
	 * An action is user triggered.
	 * <P>
	 * ### The default implementation returns empty <CODE>CorporateDirectives</CODE>.
	 *
	 * @see		de.deepamehta.service.ApplicationService#performAssociationAction
	 */
	public CorporateDirectives executeCommand(String command, Session session,
													String topicmapID, String viewmode) {
		// >>> compare to LiveTopic.executeCommand()
		CorporateDirectives directives = new CorporateDirectives();
		StringTokenizer st = new StringTokenizer(command, COMMAND_SEPARATOR);
		String cmd = st.nextToken();
		if (cmd.equals(CMD_DEFAULT)) {
			// --- trigger getDetail() hook ---
			directives.add(DIRECTIVE_SHOW_DETAIL, getID(), getDetail());
		} else if (cmd.equals(CMD_GET_ASSOC_COMMANDS)) {
			String assocID = st.nextToken();
			int version = Integer.parseInt(st.nextToken());
			int x = Integer.parseInt(st.nextToken());
			int y = Integer.parseInt(st.nextToken());
			directives.add(as.showAssociationMenu(assocID, version, topicmapID, viewmode, x, y, session));
		} else if (cmd.equals(CMD_SELECT_ASSOC)) {
			directives.add(DIRECTIVE_SELECT_ASSOCIATION, getID());
		} else if (cmd.equals(CMD_CHANGE_ASSOC_TYPE)) {
			String typeID = st.nextToken();
			directives.add(as.changeAssociationType(getID(), getVersion(), typeID, 1, session)); // ### typeVersion=1
		} else if (cmd.equals(CMD_CHANGE_ASSOC_TYPE_BY_NAME)) {
			String typeName = st.nextToken();
			as.changeAssociationType(getID(), getVersion(), typeName, topicmapID, viewmode, session, directives);
		} else if (cmd.equals(CMD_NEW_ASSOC_TYPE)) {
			directives.add(DIRECTIVE_FOCUS_TYPE);
		} else if (cmd.equals(CMD_HIDE_ASSOC)) {
			hide(topicmapID, viewmode, false, directives);
		} else if (cmd.equals(CMD_DELETE_ASSOC)) {
			delete(topicmapID, viewmode, directives);
		} else if (cmd.equals(CMD_SET_PROPERTY)) {
			String prop = st.nextToken();
			String value = st.nextToken();
			directives.add(setAssociationData(prop, value, topicmapID, viewmode));
		} else if (cmd.equals(CMD_EDIT_ASSOC_PROPERTY) || cmd.equals(CMD_VIEW_ASSOC_PROPERTY)) {
			String prop = st.nextToken();
			boolean styled = new Boolean(st.nextToken()).booleanValue();
			boolean multiline = new Boolean(st.nextToken()).booleanValue();
			boolean editable = cmd.equals(CMD_EDIT_ASSOC_PROPERTY);
			openTextEditor(prop, styled, multiline, editable, directives, command);
		} else if (cmd.equals(CMD_SHOW_HELP)) {
			String typeID = st.nextToken();
			showTypeHelp(typeID, session, directives);
		} else if (cmd.equals(CMD_SUBMIT_FORM)) {
			// Note: there is no standard behavoir for "submitForm" command -- do nothing
			// just avoid "no command handler implemented" exception
		} else {
			throw new DeepaMehtaException("command not implemented");
		}
		return directives;
	}

	/**
	 * Subclasses can override this method to process result of chanined actions.<BR>
	 * [### explain]
	 * <P>
	 * ### The default implementation returns null.
	 *
	 * @see		de.deepamehta.service.ApplicationService#performChainedAssociationCommand
	 */
	public CorporateDirectives executeChainedCommand(String command, String result,
											String topicmapID, String viewmode, Session session) {
		// return empty directives
		return new CorporateDirectives();
	}



	// ---------------------------
	// --- Handling Properties ---
	// ---------------------------



	/**
	 * [###]
	 * <P>
	 * To reject the new properties (means: preventing them from being
	 * stored in corporate memory) the application must return <CODE>false</CODE>.
	 */
	public boolean propertiesChangeAllowed(Hashtable oldData, Hashtable newData,
														CorporateDirectives directives) {
		return true;
	}

	/**
	 * The "properties changed" hook.
	 * <P>
	 * By this hook an application programmer can supply an "properties changed" behavoir. This hook
	 * is triggered by the framework if one (### zero) or more properties of this association has
	 * been changed. The framework passes the old and the new (means: changed) properties.
	 * <P>
	 * The default implementation returns empty directives.
	 *
	 * @see		de.deepamehta.service.ApplicationService#setTopicProperties
	 * @see		TopicMapTopic#propertiesChanged
	 */
	public CorporateDirectives propertiesChanged(Hashtable newProps, Hashtable oldProps,
											String topicmapID, String viewmode, Session session) {
		System.out.println(">>> association properties of " + this + " changed, " + getClass());
		return new CorporateDirectives();
	}

	/**
	 * ### could be defined interactively
	 *
	 * @see		de.deepamehta.service.ApplicationService#setAssocProperties
	 */
	public String getNameProperty() {
		return PROPERTY_NAME;
	}

	/**
	 * ### only consulted if latter returns null
	 *
	 * @see		de.deepamehta.service.ApplicationService#setAssocProperties
	 */
	public String getAssociationName(Hashtable props, Hashtable oldProps) {
		return null;
	}

	/**
	 * Applications can use this hook to disable certain properties of this association.
	 * Disabled properties are visible but not editable by the user.
	 * <P>
	 * This hook can be utilized to implement an access control mechanism.
	 * The default implementation realizes the following rule: an association is only editable
	 * if the current user
	 * 1) is the owner of the association, or<br>
	 * 2) is a DeepaMehta administrator, or<br>
	 * 3) has the "Editor" role within a workspace the resp. association type is assigned to.
	 * <P>
	 * This hook is triggered every time this association is selected.
	 *
	 * @see			de.deepamehta.service.ApplicationService#disabledProperties
	 *
	 * @return		A vector of property names (<CODE>String</CODE>s)
	 */
	public Vector disabledProperties(Session session) {
		// ### almost identical to LiveTopic.disabledProperties()
		Vector disabledProps = new Vector();
		//
		TypeTopic type = as.type(this);
		String userID = session.getUserID();
		if (!as.isAssocOwner(getID(), session) && !as.isAdministrator(userID) && !as.hasEditorRole(userID, getType())) {
			// disable all properties
			Enumeration e = type.getTypeDefinition().elements();
			while (e.hasMoreElements()) {
				PropertyDefinition propDef = (PropertyDefinition) e.nextElement();
				disabledProps.addElement(propDef.getPropertyName());
			}
		}
		//
		return disabledProps;
	}



	// ------------------------------
	// --- Handling Detail Window ---
	// ------------------------------



	// ------------------------------
	// --- Handling Detail Window ---
	// ------------------------------



	/**
	 * Subclasses can override this method to provide topic details.
	 * <P>
	 * ### The default implementation returns the properties in a 1-row table format.
	 *
	 * @see		#executeCommand
	 */
	public Detail getDetail() {
		return createAssociationHelp(getType());
	}

	/**
	 * @see		de.deepamehta.service.ApplicationService#processTopicDetail
	 */
	public CorporateDirectives processDetailHook(CorporateDetail detail,
							Session session, String topicmapID, String viewmode) {
		return detail.process(session, getID(), 1, topicmapID, viewmode);
	}



	// **********************
	// *** Helper Methods ***
	// **********************



	// --- getAssociationData (3 forms) ---

	public Hashtable getAssociationData() {
		return as.getAssocProperties(getID(), getVersion());
	}

	public String getAssociationData(String propName) {
		return as.getAssocProperty(getID(), getVersion(), propName);
	}

	public String getAssociationData(BaseAssociation assoc, String propName) {
		return as.getAssocProperty(assoc.getID(), assoc.getVersion(), propName);
	}

	// ---

	public CorporateDirectives setAssociationData(String field, String value,
													String topicmapID, String viewmode) {
		CorporateDirectives directives = new CorporateDirectives();
		Hashtable props = new Hashtable();
		props.put(field, value);
		directives.add(DIRECTIVE_SHOW_ASSOC_PROPERTIES, getID(), props, new Integer(1));
		return directives;
	}



	// ***********************
	// *** Private Methods ***
	// ***********************



	/**
	 * @see		#executeCommand
	 * @see		#delete
	 */
	private void hide(String topicmapID, String viewmode, boolean delete, CorporateDirectives directives) {
		directives.add(DIRECTIVE_HIDE_ASSOCIATION, getID(), new Boolean(delete), topicmapID);
	}

	/**
	 * @see		#executeCommand
	 */
	private void delete(String topicmapID, String viewmode,
													CorporateDirectives directives) {
		// --- deleting implies hiding ---
		hide(topicmapID, viewmode, true, directives);
		// --- trigger die() hook ---
		// tell this proxy association it is about to die. The die event is propagated
		// through the superclasses -- finally LiveAssociation.die() will delete the
		// association from corporate memory as well as from live corporate memory.
		// ### directives.add(die());
	}

	// ---

	/**
	 * Extend the specified directives to let the user edit/view the specified
	 * property of this live association.
	 * <P>
	 * Called to handle {@link #CMD_EDIT_ASSOC_PROPERTY} and
	 * {@link #CMD_VIEW_ASSOC_PROPERTY} commands.
	 *
	 * @see		#executeCommand
	 */
	private void openTextEditor(String propName, boolean styled, boolean multiline,
					boolean editable, CorporateDirectives directives, String command) {
		// ### compare to LiveTopic.openTextEditor()
		// ### compare to KompetenzsternTopic.openTextEditor()
		Detail detail;
		String title = propName;
		if (styled) {
			detail = new Detail(DETAIL_ASSOCIATION, DETAIL_CONTENT_HTML,
            	getAssociationData(propName), new Boolean(editable), title, command);
		} else {
			detail = new Detail(DETAIL_ASSOCIATION, DETAIL_CONTENT_TEXT,
            	getAssociationData(propName), new Boolean(multiline), title, command);
		}
		directives.add(DIRECTIVE_SHOW_DETAIL, getID(), detail);
	}

	// ---

	private void showTypeHelp(String typeID, Session session, CorporateDirectives directives) {
		Detail detail = createAssociationHelp(typeID);
		directives.add(DIRECTIVE_SHOW_DETAIL, getID(), detail);
	}

	private Detail createAssociationHelp(String typeID) {
		TypeTopic type = as.type(typeID, 1);
		String html = type.getProperty(PROPERTY_DESCRIPTION);
		String title = as.string(ITEM_SHOW_HELP, type.getName());
		Detail detail = new Detail(DETAIL_ASSOCIATION, DETAIL_CONTENT_HTML, html, Boolean.FALSE, title, "??");	// ### command="??"
		return detail;
	}
}
