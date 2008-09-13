package de.deepamehta.client;

import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.util.DeepaMehtaUtils;

import java.awt.BorderLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.logging.Logger;
import java.util.logging.Level;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.TransferHandler;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;



/**
 * A view component for displaying and editing text.
 * There are 3 tyes of text editor panels: EDITOR_TYPE_DEFAULT, EDITOR_TYPE_STYLED and EDITOR_TYPE_SINGLE_LINE.
 * The content can also set to be view-only.
 * <p>
 * <hr>
 * Last change: 7.9.2008 (2.0b8)<br>
 * J&ouml;rg Richter<br>
 * jri@deepamehta.de
 */
class TextEditorPanel extends JPanel implements ActionListener, DocumentListener, DeepaMehtaConstants {



	// *****************
	// *** Constants ***
	// *****************



	// ### private static final String CMD_SET_HEADLINE1 = "setHeadline1";
	// ### private static final String CMD_SET_HEADLINE2 = "setHeadline2";



	// **************
	// *** Fields ***
	// **************



	/**
	 * The text editor type, one of this constants
	 * <ul>
	 * <li>{@link #EDITOR_TYPE_DEFAULT}
	 * <li>{@link #EDITOR_TYPE_STYLED}
	 * <li>{@link #EDITOR_TYPE_SINGLE_LINE}
	 * </ul>
	 */
	private int editorType;

	private JTextComponent textComponent;	// a JTextArea, JTextPane, or JTextField respectively
	private JScrollPane scrollPane;			// is null in case of EDITOR_TYPE_SINGLE_LINE
	private PresentationDetail detail;

	boolean showToolbar;	// indicates weather a toolbar is created
	JPanel toolbar;

	/**
	 * Indicates weather this text editor is dirty (means: contains unsaved changes).
	 */
	private boolean isDirty;

	private static Logger logger = Logger.getLogger("de.deepamehta");



	// ********************
	// *** Constructors ***
	// ********************



	/**
	 * References checked: 10.9.2007 (2.0b8)
	 *
	 * @see		PresentationDetail#PresentationDetail
	 * @see		PresentationPropertyDefinition#createTextEditor
	 */
	TextEditorPanel(int editorType, HyperlinkListener listener, GraphPanelControler controler, boolean showToolbar) {
		this(editorType, listener, controler, showToolbar, null);
	}

	/**
	 * References checked: 10.9.2007 (2.0b8)
	 *
	 * @param	editorType	editor type, one of this constants
	 *						<ul>
	 *						<li>EDITOR_TYPE_DEFAULT
	 *						<li>EDITOR_TYPE_STYLED
	 *						<li>EDITOR_TYPE_SINGLE_LINE
	 *						</ul>
	 * @param	controler	for editors of type <code>EDITOR_TYPE_STYLED</code>: just needed to get the icons for the
	 *						toolbar buttons ### bad approach
	 *
	 * @see		PresentationDetail#PresentationDetail
	 */
	TextEditorPanel(int editorType, HyperlinkListener listener, GraphPanelControler controler, boolean showToolbar,
																							PresentationDetail detail) {
		this.editorType = editorType;
		this.showToolbar = showToolbar;
		this.detail = detail;
		setLayout(new BorderLayout());
		// --- build this text editor panel ---
		switch (editorType) {
		case EDITOR_TYPE_DEFAULT:
			textComponent = new JTextArea();
			scrollPane = new JScrollPane(textComponent);
			add(scrollPane);
			break;
		case EDITOR_TYPE_STYLED:
			textComponent = new JTextPane();
			textComponent.setTransferHandler(new TextTransferHandler());	// ### requires Java 1.4
			((JEditorPane) textComponent).addHyperlinkListener(listener);
			// --- add toolbar ---
			if (showToolbar) {
				toolbar = new JPanel();
				Hashtable actions = DeepaMehtaClientUtils.createActionTable(textComponent);
				toolbar.setBackground(COLOR_PROPERTY_PANEL);
				addButton(toolbar, new StyledEditorKit.BoldAction(), actions, controler.boldIcon());
				addButton(toolbar, new StyledEditorKit.ItalicAction(), actions, controler.italicIcon());
				addButton(toolbar, new StyledEditorKit.UnderlineAction(), actions, controler.underlineIcon());
				// ### addButton(toolbar, "H1", CMD_SET_HEADLINE1);
				// ### addButton(toolbar, "H2", CMD_SET_HEADLINE2);
				add(toolbar, BorderLayout.SOUTH);
			}
			scrollPane = new JScrollPane(textComponent);
			add(scrollPane);
			break;
		case EDITOR_TYPE_SINGLE_LINE:
			textComponent = new JTextField();
			((JTextField) textComponent).addActionListener(this);
			add(textComponent);
			break;
		default:
			throw new DeepaMehtaException("unexpected text editor type: " + editorType);
		}
		// --- enable automatic drag and drop support ---
		try {
			textComponent.setDragEnabled(true);
		} catch (NoSuchMethodError e) {
			// requires JDK 1.4 ###
		}
	}



	// *****************************************************************
	// *** Implementation of interface java.awt.event.ActionListener ***
	// *****************************************************************



	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		if (e.getSource() == textComponent) {
			if (detail != null) {
				detail.closeWindow();
			}
		} else {
		/* ### if (actionCommand.equals(CMD_SET_HEADLINE1)) {
		} else if (actionCommand.equals(CMD_SET_HEADLINE2)) {
		} else { */
			throw new DeepaMehtaException("unexpected event source: " + e.getSource() +
				" (action command: \"" + actionCommand + "\")");
		}
		
	}



	// **********************************************************************
	// *** Implementation of interface javax.swing.event.DocumentListener ***
	// **********************************************************************



	public void changedUpdate(DocumentEvent e) {
		setDirty("style change");
	}

	public void insertUpdate(DocumentEvent e) {
		setDirty("typing");
	}

	public void removeUpdate(DocumentEvent e) {	
		setDirty("deleting");
	}



	// ***************
	// *** Methods ***
	// ***************



	String getText() {
		return getTextComponent().getText();
	}

	JTextComponent getTextComponent() {
		return textComponent;
	}

	JScrollPane getScrollPane() {
		return scrollPane;
	}

	/**
	 * @see		PresentationDetail#isDirty
	 */
	boolean isDirty() {
		return isDirty;
	}

	// --- overrides Component ---

	/**
	 * @see		PropertyPanel.PropertyField#setEnabled
	 * @see		PresentationDetail#PresentationDetail
	 */
	public void setEnabled(boolean enabled) {
		textComponent.setEnabled(enabled);
		textComponent.setEditable(enabled);		// ### required for hyperlinks to work
		//
		if (showToolbar) {
			toolbar.setVisible(enabled);
		}
	}

	public void requestFocus() {
		textComponent.requestFocus();
	}

	// --- setText (2 forms) ---

	/**
	 * References checked: 12.9.2008 (2.0b8)
	 *
	 * @see		PresentationDetail#PresentationDetail
	 */
	void setText(String text) {
		setText(text, null, this);	// ### baseURL=null
	}

	/**
	 * Note: if called by PropertyPanel the PropertyPanel is the document listener, and if called
	 * by PresentationDetail this TextEditorPanel itself is the document listener.
	 *
	 * References checked: 12.9.2008 (2.0b8)
	 *
	 * @param	baseURL				may be <code>null</code>
	 * @param	documentListener	the document listener notified about text changes.
	 *
	 * @see		PropertyPanel.PropertyField#setText
	 */
	void setText(String text, String baseURL, DocumentListener documentListener) {
		//
		textComponent.getDocument().removeDocumentListener(documentListener);
		//
		if (editorType == EDITOR_TYPE_STYLED) {
			//
			// --- set content type ---
			((JEditorPane) textComponent).setContentType("text/html");
			//
			// --- set base URL ---
			try {
				// Note: baseURL is null if no baseURL is set for the corresponding property
				// Note: baseURL is empty if corporate baseURL is used but not set
				if (baseURL != null && !baseURL.equals("")) {
					((HTMLDocument) textComponent.getDocument()).setBase(new URL(baseURL));
				}
			} catch (MalformedURLException mue) {
				logger.warning("invalid base URL: " + mue);
			}
			//
			// --- set text ---
			try {
				text = fixMissingParagraphTag(text);	// ###
				textComponent.setText(text);
			} catch (Throwable e) {
				textComponent.setText("<html><body><font color=#FF0000>Page can't be displayed</font></body></html>");
				logger.warning("error while HTML rendering: " + e);
			}
		} else {
			textComponent.setText(text);
		}
		//
		textComponent.getDocument().addDocumentListener(documentListener);
	}



	// ***********************
	// *** Private Methods ***
	// ***********************



	private void addButton(JPanel container, Action action, Hashtable actions, Icon icon) {
		String actionName = (String) action.getValue(Action.NAME);
		String actionCommand = (String) action.getValue(Action.ACTION_COMMAND_KEY);
		JButton button = new JButton(icon);
		button.setActionCommand(actionCommand);
		button.addActionListener(DeepaMehtaClientUtils.getActionByName(actionName, actions));
		container.add(button);
	}

	private String fixMissingParagraphTag(String text) {
		// check for <body> tag
		int bs = text.indexOf("<body>");
		if (bs == -1) {
			if (text.length() > 0) {
				logger.warning("no <body> tag found in\n" + text);
			}
			return text;
		}
		// skip whitespace
		int ws = bs + 6;
		char c = text.charAt(ws);
		while (Character.isWhitespace(c)) {
			c = text.charAt(++ws);
		}
		// check for <p> tag
		int p = ws;
		if (text.substring(p, p + 3).equals("<p>")) {
			// no need to fix
			logger.fine("no need to fix\n" + text);
			return text;
		}
		// check for </body> tag
		int be = text.indexOf("</body>", p);
		// fix missing <p> tag
		StringBuffer fixedText = new StringBuffer(text);
		fixedText.insert(be, "</p>");
		fixedText.insert(p, "<p>");
		logger.fine(text + "\n                fixed to\n" + fixedText);
		return fixedText.toString();
	}

	/**
	 * @see		#changedUpdate
	 * @see		#insertUpdate
	 * @see		#removeUpdate
	 */
	private void setDirty(String message) {
		if (!isDirty) {
			logger.info("text editor content changed (by " + message + ")");
			isDirty = true;
		}
	}



	// *******************
	// *** Inner Class ***
	// *******************



	private class TextTransferHandler extends TransferHandler {

    	// We do not allow dropping on top of the selected text,
	    private JTextComponent source;
		private boolean shouldRemove;

		// Start and end position in the source text.
		// We need this information when performing a MOVE in order to remove the dragged text from the source.
		Position p0 = null, p1 = null;

		// --- import methods ---

		public boolean canImport(JComponent c, DataFlavor[] flavors) {
			return true;
		}

		public boolean importData(JComponent c, Transferable t) {
			if (c != textComponent) {	// ### never happens, we dont share transfer handlers
				logger.warning("c=" + c);
			}
			try {
				DataFlavor[] flavors = t.getTransferDataFlavors();
				boolean hasStringFlavor = t.isDataFlavorSupported(DataFlavor.stringFlavor);
				boolean hasImageFlavor = t.isDataFlavorSupported(DataFlavor.imageFlavor);
				boolean hasFilelistFlavor = t.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
				//
				logger.info("importing data to text panel (" + flavors.length + " flavors)" +
					"    string flavor supported: " + hasStringFlavor +
					"    image flavor supported: " + hasImageFlavor +
					"    filelist flavor supported: " + hasFilelistFlavor);
				// ### for (int i = 0; i < flavors.length; i++) {
				// ###	System.out.println(flavors[i]);
				// ###}
				//
				// We do not allow dropping on top of the selected text
		        if ((source == textComponent) && (textComponent.getCaretPosition() >= p0.getOffset()) &&
                                                (textComponent.getCaretPosition() <= p1.getOffset())) {
					shouldRemove = false;
					logger.info("dropping on top of the selected text is not allowed -- import canceled");
					return true;
				}
				//
				if (hasStringFlavor) {
					String data = (String) t.getTransferData(DataFlavor.stringFlavor);
					int pos = textComponent.getCaretPosition();
					if (DeepaMehtaUtils.isImage(data)) {
						HTMLEditorKit kit = (HTMLEditorKit) ((JEditorPane) textComponent).getEditorKit();
						HTMLDocument doc = (HTMLDocument) textComponent.getDocument();
						String html = "<img src=\"" + data + "\"></img>";
						kit.insertHTML(doc, pos, html, 0, 0, HTML.Tag.IMG);	// ### <img> not XML conform
						// ### doc.insertBeforeStart(doc.getParagraphElement(pos), html); 
						logger.info("inserting <img> tag \"" + html + "\"");
					} else {
						textComponent.getDocument().insertString(pos, data, null);
						logger.info("inserting regular text \"" + data + "\"");
					}
				} else if (hasFilelistFlavor) {
	                java.util.List files = (java.util.List) t.getTransferData(DataFlavor.javaFileListFlavor);
					logger.info("    " + files.size() + " files:");
	                for (int i = 0; i < files.size(); i++) {
						File file = (File) files.get(i);
						String filename = file.getName();
						logger.info("    " + file);
						if (DeepaMehtaUtils.isHTML(filename)) {
							String html = DeepaMehtaUtils.readFile(file);
							textComponent.setText(html);	// ### replace instead insert
							textComponent.setCaretPosition(0);
							// ### ((JEditorPane) textComponent).setPage("file://" + file);	// ### replace instead insert
							// ### setDirty("dropping HTML file");
							logger.info("inserting HTML (read from file)");
							break;	// ### max one file is inserted
						} else if (DeepaMehtaUtils.isImage(filename)) {
							HTMLEditorKit kit = (HTMLEditorKit) ((JEditorPane) textComponent).getEditorKit();
							HTMLDocument doc = (HTMLDocument) textComponent.getDocument();
							int pos = textComponent.getCaretPosition();
							String imagefile = file.getPath().replace('\\', '/');		// ###
							String html = "<img src=\"" + imagefile + "\"></img>";
							kit.insertHTML(doc, pos, html, 0, 0, HTML.Tag.IMG);	// ### <img> not XML conform
							// ### doc.insertBeforeStart(doc.getParagraphElement(pos), html); 
							logger.info("inserting <img> tag \"" + html + "\"");
						} else {
							logger.warning("only implemented for HTML files -- import canceled");
						}
					}
				} else {
					logger.warning("no supported flavor " + c);
				}
				return true;
            } catch (UnsupportedFlavorException ufe) {
                logger.warning("while dropping to text panel: " + ufe);
			} catch (BadLocationException ble) {
                logger.warning("while dropping to text panel: " + ble);
            } catch (IOException ioe) {
                logger.warning("while dropping to text panel: " + ioe);
			}
			//
			return super.importData(c, t);
		}

		// --- export methods ---

		public int getSourceActions(JComponent c) {
			return COPY_OR_MOVE;
		}

		// Create a Transferable implementation that contains the selected text.
		protected Transferable createTransferable(JComponent c) {
			if (c != textComponent) {	// ###
				logger.warning("c=" + c);
			}
			source = (JTextComponent) c;
			int start = source.getSelectionStart();
			int end = source.getSelectionEnd();
			Document doc = source.getDocument();
			if (start == end) {
				return null;
			}
			try {
				p0 = doc.createPosition(start);
				p1 = doc.createPosition(end);
				logger.info("p0=" + p0 + ", p1=" + p1);
			} catch (BadLocationException e) {
				logger.warning("can't create position -- unable to remove text from source");
			}
			shouldRemove = true;
			String data = source.getSelectedText();
			return new StringSelection(data);
		}

		// Remove the old text if the action is a MOVE.
		// However, we do not allow dropping on top of the selected text, so in that case do nothing.
		protected void exportDone(JComponent c, Transferable data, int action) {
			if (c != textComponent) {	// ###
				logger.warning("c=" + c);
			}
			logger.info("action=" + action + ", MOVE=" + MOVE + ", shouldRemove=" + shouldRemove);
			if (shouldRemove && (action == MOVE)) {
				if ((p0 != null) && (p1 != null) && (p0.getOffset() != p1.getOffset())) {
					try {
						textComponent.getDocument().remove(p0.getOffset(), p1.getOffset() - p0.getOffset());
					} catch (BadLocationException e) {
						logger.warning("can't remove text from source");
					}
				}
			}
			source = null;
		}
	}
}
