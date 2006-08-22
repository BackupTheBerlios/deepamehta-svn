package de.deepamehta;

import java.io.*;



/**
 * Basis model of a topic/association detail.
 * <P>
 * A <CODE>Detail</CODE> can be serialized and send through a
 * <CODE>DataOutputStream</CODE>. The client builds a
 * {@link de.deepamehta.client.PresentationDetail} object upon
 * and displays the detail in a window.
 *
 * <H4>Hints for application programmers</H4>
 *
 * Use the <CODE>DIRECTIVE_SHOW_DETAIL</CODE> directive to instruct the client
 * to display the details of a certain topic.
 * <P>
 * <HR>
 * Last functional change: 26.1.2003 (2.0a17-pre7)<BR>
 * Last documentation update: 11.3.2001 (2.0a10-pre1)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class Detail implements DeepaMehtaConstants {



	// **************
	// *** Fields ***
	// **************



	/**
	 * Detail type. {@link #DETAIL_TOPIC} or {@link #DETAIL_ASSOCIATION}.
	 */
	protected int detailType;

	/**
	 * Detail content type. See the 5 {@link #DETAIL_CONTENT_NONE DETAIL_CONTENT_... constants}.
	 */
	protected int contentType;

	/**
	 * Detail content model.
	 */
	protected Object param1, param2;

	/**
	 * The title to be used for the detail window.
	 * <P>
	 * Initialized by constructors.
	 */
	protected String title;

	/**
	 * The command that evoked this detail.
	 * <P>
	 * Initialized by constructors.
	 */
	protected String command;



	// ********************
	// *** Constructors ***
	// ********************



	/**
	 * References checked: 22.11.2001 (2.0a13-post1)
	 *
	 * @see		de.deepamehta.topics.LiveTopic#getDetail
	 * @see		de.deepamehta.topics.ContainerTopic#getDetail
	 */
	public Detail(int detailType) {
		this.detailType = detailType;
		this.contentType = DETAIL_CONTENT_NONE;
	}

	/**
	 * Standard constructor.
	 * <P>
	 * References checked: 23.11.2001 (2.0a13-post1)
	 *
	 * @see		de.deepamehta.topics.LiveTopic#getDetail
	 * @see		de.deepamehta.topics.LiveTopic#openTextEditor
	 * @see		de.deepamehta.topics.LiveTopic#showTypeHelp
	 * @see		de.deepamehta.topics.LiveTopic#rename
	 * @see		de.deepamehta.topics.ContainerTopic#getDetail
	 * @see		de.deepamehta.assocs.LiveAssociation#openTextEditor
	 * @see		de.deepamehta.assocs.LiveAssociation#showTypeHelp
	 */
	public Detail(int detailType, int contentType, Object param1, Object param2,
    												String title, String command) {
		this.detailType = detailType;
		this.contentType = contentType;
		this.param1 = param1;
		this.param2 = param2;
		this.title = title;
		this.command = command;
	}

	/**
	 * Copy constructor.
	 * <P>
	 * References checked: 2.1.2002 (2.0a15-pre5)
	 *
	 * @see		de.deepamehta.service.CorporateDetail#CorporateDetail(Detail, ApplicationService)
	 * @see		de.deepamehta.client.PresentationDetail#PresentationDetail
	 */
	public Detail(Detail detail) {
		this.detailType = detail.detailType;
		this.contentType = detail.contentType;
		this.param1 = detail.param1;
		this.param2 = detail.param2;
		this.title = detail.title;
		this.command = detail.command;
	}

	/**
	 * Stream constructor.
	 * <P>
	 * References checked: 15.10.2001 (2.0a13-pre1)
	 *
	 * @see		de.deepamehta.client.PresentationDirectives#PresentationDirectives
	 */
	public Detail(DataInputStream in) throws IOException {
		this.detailType = in.readInt();
		this.contentType = in.readInt();
		switch (contentType) {
		case DETAIL_CONTENT_NONE:
			break;
		case DETAIL_CONTENT_TEXT:
		case DETAIL_CONTENT_IMAGE:
		case DETAIL_CONTENT_HTML:
			this.param1 = in.readUTF();						// text       / filename / HTML
			this.param2 = new Boolean(in.readBoolean());	// multiline? / not used / editable?
			this.title = in.readUTF();
			this.command = in.readUTF();
			break;
		case DETAIL_CONTENT_TABLE:
			// dimensions
			int colCount = in.readInt();
			int rowCount = in.readInt();
			String[] columnNames = new String[colCount];
			String[][] values = new String[rowCount][colCount];
			// column names
			for (int c = 0; c < colCount; c++) {
				columnNames[c] = in.readUTF();
			}
			// cell values
			for (int r = 0; r < rowCount; r++) {
				for (int c = 0; c < colCount; c++) {
					values[r][c] = in.readUTF();
				}
			}
			this.param1 = columnNames;
			this.param2 = values;
			this.title = in.readUTF();
			this.command = in.readUTF();
			break;
		default:
			throw new DeepaMehtaException("unexpected detail content type: " + contentType);
		}
	}



	// ***************
	// *** Methods ***
	// ***************



	public int getType() {
		return detailType;
	}

	/**
	 * References checked: 10.5.2002 (2.0a15-pre1)
	 *
	 * @see		de.deepamehta.client.GraphPanel#showNodeDetail
	 */
	public int getContentType() {
		return contentType;
	}

	public Object getParam1() {
		return param1;
	}

	public String getTitle() {
		return title;
	}

	public String getCommand() {
		return command;
	}



	// ---------------------
	// --- Serialization ---
	// ---------------------



	public void write(DataOutputStream out) throws IOException {
		out.writeInt(detailType);
		out.writeInt(contentType);
		switch (contentType) {
		case DETAIL_CONTENT_NONE:
			break;
		case DETAIL_CONTENT_TEXT:
		case DETAIL_CONTENT_IMAGE:
		case DETAIL_CONTENT_HTML:
			out.writeUTF((String) param1);
			out.writeBoolean(((Boolean) param2).booleanValue());
			out.writeUTF(title);
			out.writeUTF(command);
			break;
		case DETAIL_CONTENT_TABLE:
			String[] columnNames = (String[]) param1;
			String[][] values = (String[][]) param2;
			int colCount = columnNames.length;
			int rowCount = values.length;
			// dimensions
			out.writeInt(colCount);
			out.writeInt(rowCount);
			// column names
			for (int c = 0; c < colCount; c++) {
				out.writeUTF(columnNames[c]);
			}
			// cell values
			for (int r = 0; r < rowCount; r++) {
				for (int c = 0; c < colCount; c++) {
					out.writeUTF(values[r][c]);
				}
			}
			//
			out.writeUTF(title);
			out.writeUTF(command);
			break;
		default:
			throw new DeepaMehtaException("unexpected detail content type: " + contentType);
		}
	}
}
