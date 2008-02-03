package de.deepamehta;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.awt.Point;



/**
 * Basis model for a detail window. Details windows are displayed within a topicmap and
 * are bound to either a topic, an association, or a topicmap.
 * <p>
 * A <code>Detail</code> can be serialized and send through a <code>DataOutputStream</code>.
 * The client builds a {@link de.deepamehta.client.PresentationDetail} object upon and displays the detail window.
 *
 * <H4>Hints for application programmers</H4>
 *
 * Use the <code>DIRECTIVE_SHOW_DETAIL</code> directive to instruct the client
 * to display the details of a certain topic.
 * <p>
 * <hr>
 * Last functional change: 3.2.2008 (2.0b8)<br>
 * Last documentation update: 3.2.2008 (2.0b8)<br>
 * J&ouml;rg Richter<br>
 * jri@freenet.de
 */
public class Detail implements DeepaMehtaConstants {



	// **************
	// *** Fields ***
	// **************



	/**
	 * Detail type.
	 * {@link #DETAIL_TOPIC}, {@link #DETAIL_ASSOCIATION}, or {@link #DETAIL_TOPICMAP}.
	 */
	protected int detailType;

	/**
	 * The anchor point for the guiding polygon.
	 * Only used for detail type {@link #DETAIL_TOPICMAP}.
	 */
	protected Point guideAnchor;

	/**
	 * Detail content type.
	 * See the 5 {@link #DETAIL_CONTENT_NONE DETAIL_CONTENT_... constants}.
	 */
	protected int contentType;

	/**
	 * Detail content model.
	 * Usage depends on detail content type.
	 */
	protected Object param1, param2;

	/**
	 * The title to be used for the detail window.
	 * <p>
	 * Initialized by constructors.
	 */
	protected String title;

	/**
	 * The command that evoked this detail.
	 * <p>
	 * Initialized by constructors.
	 */
	protected String command;



	// ********************
	// *** Constructors ***
	// ********************



	/**
	 * References checked: 2.2.2008 (2.0b8)
	 *
	 * @see		de.deepamehta.topics.ContainerTopic#getDetail
	 */
	public Detail(int detailType) {
		this.detailType = detailType;
		this.contentType = DETAIL_CONTENT_NONE;
	}

	/**
	 * Standard constructor.
	 * <p>
	 * References checked: 2.2.2008 (2.0b8)
	 *
	 * @see		de.deepamehta.topics.LiveTopic#openTextEditor
	 * @see		de.deepamehta.topics.LiveTopic#createTopicHelp
	 * @see		de.deepamehta.topics.ContainerTopic#getDetail
	 * @see		de.deepamehta.topics.ImageTopic#getDetail
	 * @see		de.deepamehta.assocs.LiveAssociation#openTextEditor
	 * @see		de.deepamehta.assocs.LiveAssociation#createAssociationHelp
	 * @see		de.deepamehta.kompetenzstern.topics.KompetenzsternTopic#openTextEditor
	 */
	public Detail(int detailType, int contentType, Object param1, Object param2, String title, String command) {
		this(detailType, contentType, param1, param2, title, command, null);
	}

	public Detail(int detailType, int contentType, Object param1, Object param2, String title, String command,
																							Point guideAnchor) {
		this.detailType = detailType;
		this.contentType = contentType;
		this.param1 = param1;
		this.param2 = param2;
		this.title = title;
		this.command = command;
		this.guideAnchor = guideAnchor;
	}

	/**
	 * Copy constructor.
	 * <p>
	 * References checked: 2.1.2002 (2.0a15-pre5)
	 *
	 * @see		de.deepamehta.service.CorporateDetail#CorporateDetail(Detail, de.deepamehta.service.ApplicationService)
	 * @see		de.deepamehta.client.PresentationDetail#PresentationDetail
	 */
	public Detail(Detail detail) {
		this.detailType = detail.detailType;
		this.contentType = detail.contentType;
		this.param1 = detail.param1;
		this.param2 = detail.param2;
		this.title = detail.title;
		this.command = detail.command;
		this.guideAnchor = detail.guideAnchor;
	}

	/**
	 * Stream constructor.
	 * <p>
	 * References checked: 15.10.2001 (2.0a13-pre1)
	 *
	 * @see		de.deepamehta.client.PresentationDirectives#PresentationDirectives
	 */
	public Detail(DataInputStream in) throws IOException {
		this.detailType = in.readInt();
		if (detailType == DETAIL_TOPICMAP) {
			int x = in.readInt();
			int y = in.readInt();
			this.guideAnchor = new Point(x, y);
		}
		//
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

	public Point getGuideAnchor() {
		return guideAnchor;
	}

	// ---

	public void setGuideAnchor(Point guideAnchor) {
		this.guideAnchor = guideAnchor;
	}



	// ---------------------
	// --- Serialization ---
	// ---------------------



	public void write(DataOutputStream out) throws IOException {
		out.writeInt(detailType);
		if (detailType == DETAIL_TOPICMAP) {
			out.writeInt(guideAnchor.x);
			out.writeInt(guideAnchor.y);
		}
		//
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
