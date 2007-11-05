package de.deepamehta;

import de.deepamehta.client.PresentationService;

import org.apache.fop.viewer.Command;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;



/**
 * A set of commands that is bound to either a
 * <UL>
 * <LI>topic
 * <LI>association
 * <LI>topicmap
 * </UL>
 * The set of commands is ordered and may be hierarchical.
 * The client displays a command set as a popup menu possibly with submenus.
 * <P>
 * Related commands may be put into a named group of commands what causes the client to
 * present them as a submenu (with the group name as menu title). Additionally commands
 * and groups may be separated what causes the client to put a separator line into th
 * topic context menu.
 * <P>
 * A <CODE>Commands</CODE> object is serialized by the server by using the
 * {@link #write} method and send to the client who builds a
 * {@link de.deepamehta.client.PresentationCommands} object upon it.
 *
 * <H4>Hints for application programmers</H4>
 *
 * A live topic provides its commands by the
 * {@link de.deepamehta.topics.LiveTopic#contextCommands contextCommands()} hook.
 * Within this hook the <CODE>Commands</CODE> object should be get with
 * <CODE>Commands commands = super.contextCommands(session)</CODE>, then the
 * commands (resp. groups, separators) are added by {@link #addCommand},
 * {@link #addCommandGroup} resp. {@link #addSeparator}. Finally the the
 * <CODE>Commands</CODE> object is returned.
 * <P>
 * The command handling is done in the
 * {@link de.deepamehta.topics.LiveTopic#executeCommand executeCommand()} and 
 * {@link de.deepamehta.topics.LiveTopic#executeCommand executeChainedCommand()}
 * hooks.
 * <P>
 * As an example see the kernel topic
 * {@link de.deepamehta.topics.TopicMapTopic TopicMapTopic}.
 * <P>
 * Internally a <CODE>Commands</CODE> object is a collection of single
 * {@link Command Command} objects (protected inner class). The appliation programmer will
 * not use the class <CODE>Command</CODE> directly.
 * <P>
 * <HR>
 * Last functional change: 26.12.2001 (2.0a14-pre5)<BR>
 * Last documentation update: 9.10.2001 (2.0a12)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class Commands implements DeepaMehtaConstants {



	// **************
	// *** Fields ***
	// **************



	/**
	 * The command type.
	 * <P>
	 * See the three {@link #TYPE_COMMAND command type constants}.
	 */
	public int type;

	// --- not used for TYPE_COMMAND_SEPARATOR ---

	public String label;
	public String iconpath;
	public String iconfile;

	// --- only used for TYPE_COMMAND ---

	public String command;
	public int state;

	// --- only used for TYPE_COMMAND_GROUP ---

	/**
	 * Element type is {@link Commands.Command}.
	 * <P>
	 * Only used for {@link #TYPE_COMMAND_GROUP}.
	 */
	public Vector groupCommands;



	// ********************
	// *** Constructors ***
	// ********************


	
	/**
	 * Default constructor.
	 *
	 * Constructs a top-level commands group.
	 *
	 * @see		de.deepamehta.topics.TopicMapTopic#contextCommands
	 */
	public Commands() {
		this("", "", "");
	}

	/**
	 * Copy constructor.
	 *
	 * @see		de.deepamehta.client.PresentationCommands#PresentationCommands(Commands, PresentationService)
	 */
	public Commands(Commands commands) {
		this.type = commands.type;
		this.label = commands.label;
		this.iconpath = commands.iconpath;
		this.iconfile = commands.iconfile;
		this.command = commands.command;
		this.state = commands.state;
		this.groupCommands = commands.groupCommands;
	}

	Commands(int type) {
		if (type != TYPE_COMMAND_SEPARATOR) {
			throw new DeepaMehtaException("Commands(1) constructor can only be used " +
				"for TYPE_COMMAND_SEPARATOR");
		}
		this.type = type;
	}

	Commands(String command, String label, String iconpath, String iconfile,
																	int state) {
		this.type = TYPE_COMMAND;
		this.command = command;
		this.label = label;
		this.iconpath = iconpath;
		this.iconfile = iconfile;
		this.state = state;
	}

	/**
	 * @see		#addCommandGroup
	 */
	Commands(String label, String iconpath, String iconfile) {
		this.type = TYPE_COMMAND_GROUP;
		this.label = label;
		this.iconpath = iconpath;
		this.iconfile = iconfile;
		this.groupCommands = new Vector();
	}

	/**
	 * Stream constructor.
	 *
	 * @see		de.deepamehta.client.PresentationCommands#PresentationCommands(DataInputStream)
	 */
	protected Commands(DataInputStream in) throws IOException {
		this.type =  in.readInt();
		switch (type) {
		case TYPE_COMMAND:
			this.command = in.readUTF();
			this.label = in.readUTF();
			this.iconpath = in.readUTF();
			this.iconfile = in.readUTF();
			this.state = in.readInt();
			break;
		case TYPE_COMMAND_GROUP:
			this.label = in.readUTF();
			this.iconpath = in.readUTF();
			this.iconfile = in.readUTF();
			this.groupCommands = readCommands(in);
			break;
		case TYPE_COMMAND_SEPARATOR:
			// do nothing
			break;
		}
	}




	// ***************
	// *** Methods ***
	// ***************



	// --- addCommand (4 forms) ---

	public void addCommand(String label, String command) {
		addCommand(label, command, "", "");
	}

	public void addCommand(String label, String command, String iconpath, String iconfile) {
		addCommand(label, command, iconpath, iconfile, COMMAND_STATE_DEFAULT);
	}

	// ---

	public void addCommand(String label, String command, int commandState) {
		addCommand(label, command, "", "", commandState);
	}

	public void addCommand(String label, String command, String iconpath, String iconfile, int commandState) {
		// error check
		if (type != TYPE_COMMAND_GROUP) {
			throw new DeepaMehtaException("addCommand() can only be used for " +
				"commands of type TYPE_COMMAND_GROUP");
		}
		//
		groupCommands.addElement(new Commands(command, label, iconpath, iconfile, commandState));
	}

	// --- addCommandGroup (2 forms) ---

	/**
	 * @return	An ID for the group.
	 */
	public Commands addCommandGroup(String label) {
		return addCommandGroup(label, "", "");
	}

	public Commands addCommandGroup(String label, String iconpath, String iconfile) {
		// error check
		if (type != TYPE_COMMAND_GROUP) {
			throw new DeepaMehtaException("addCommandGroup() can only be used for " +
				"commands of type TYPE_COMMAND_GROUP");
		}
		//
		Commands commandGroup = new Commands(label, iconpath, iconfile);
		groupCommands.addElement(commandGroup);
		return commandGroup;
	}

	// ---

	/**
	 * Adds a separator to this command set.
	 */
	public void addSeparator() {
		// error check
		if (type != TYPE_COMMAND_GROUP) {
			throw new DeepaMehtaException("addSeparator() can only be used for " +
				"commands of type TYPE_COMMAND_GROUP");
		}
		//
		groupCommands.addElement(new Commands(TYPE_COMMAND_SEPARATOR));
	}

	// ---

	public void add(Commands commands) {
		// error check 1
		if (type != TYPE_COMMAND_GROUP) {
			throw new DeepaMehtaException("add() can only be used for " +
				"commands of type TYPE_COMMAND_GROUP");
		}
		// error check 2
		if (commands.type != TYPE_COMMAND_GROUP) {
			throw new DeepaMehtaException("add() can only be used with " +
				"commands of type TYPE_COMMAND_GROUP");
		}
		//
		groupCommands.addAll(commands.groupCommands);
	}

	// ---

	public boolean isEmpty() {
		return groupCommands.size() == 0;	// ### could contain only separators
	}

	/**
	 * @see		de.deepamehta.service.InteractionConnection#performGetTopicActions
	 */
	public void write(DataOutputStream out) throws IOException {
		try {
			out.writeInt(type);
			switch (type) {
			case TYPE_COMMAND:
				out.writeUTF(command);
				out.writeUTF(label);
				out.writeUTF(iconpath);
				out.writeUTF(iconfile);
				out.writeInt(state);
				break;
			case TYPE_COMMAND_GROUP:
				out.writeUTF(label);
				out.writeUTF(iconpath);
				out.writeUTF(iconfile);
				writeCommands(groupCommands, out);
				break;
			case TYPE_COMMAND_SEPARATOR:
				// do nothing
				break;
			}
		} catch (NullPointerException e) {
			System.out.println("*** Command.write(): some field not set in " + this +
				" -- connection will lose sync!");
		}
	}



	// ***********************
	// *** Private Methods ***
	// ***********************



	/**
	 * @see		#Commands(DataInputStream)
	 * @see		Command#Command(DataInputStream)
	 */
	private Vector readCommands(DataInputStream in) throws IOException {	
		Vector actions = new Vector();
		// read number of topics
		int actionCount = in.readInt();
		// read topics
		for (int i = 0; i < actionCount; i++) {
			actions.addElement(new Commands(in));
		}
		return actions;
	}

	/**
	 * @see		#write
	 * @see		Command#write
	 */
	private void writeCommands(Vector commands, DataOutputStream out) throws IOException {
		out.writeInt(commands.size());
		Enumeration e = commands.elements();
		Commands command;
		while (e.hasMoreElements()) {
			command = (Commands) e.nextElement();
			command.write(out);
		}
	}
}
