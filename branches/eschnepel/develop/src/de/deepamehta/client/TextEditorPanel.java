package de.deepamehta.client;

import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.DeepaMehtaUtils;
//
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.*;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.*;



/**
 * A view component for displaying and editing text.
 * There are 3 tyes of text editor panels: EDITOR_TYPE_DEFAULT, EDITOR_TYPE_STYLED and EDITOR_TYPE_SINGLE_LINE.
 * The content can also set to be view-only.
 * <P>
 * <HR>
 * Last functional change: 13.9.2007 (2.0b8)<BR>
 * Last documentation update: 29.9.2001 (2.0a12-pre7)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
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
	 * <UL>
	 * <LI>{@link #EDITOR_TYPE_DEFAULT}
	 * <LI>{@link #EDITOR_TYPE_STYLED}
	 * <LI>{@link #EDITOR_TYPE_SINGLE_LINE}
	 * </UL>
	 */
	private int editorType;

	private JTextComponent textComponent;	// a JTextArea, JTextPane or JTextField respectively
	private PresentationDetail detail;

	boolean showToolbar;	// indicates weather a toolbar is created
	JPanel toolbar;

	/**
	 * Indicates weather this text editor is dirty (means: contains unsaved changes).
	 */
	private boolean isDirty;



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
	 *						<UL>
	 *						<LI>EDITOR_TYPE_DEFAULT
	 *						<LI>EDITOR_TYPE_STYLED
	 *						<LI>EDITOR_TYPE_SINGLE_LINE
	 *						</UL>
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
			add(new JScrollPane(textComponent));
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
			add(new JScrollPane(textComponent));
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
		return textComponent.getText();
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
	 * @see		PresentationDetail#PresentationDetail
	 */
	void setText(String text) {
		setText(text, null, this);	// ### baseURL=null
	}

	/**
	 * @param	baseURL		may be <code>null</code>
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
			// ### System.out.println(">>> TextEditorPanel.setText(): before setContentType(): " + ((JEditorPane) textComponent).getHyperlinkListeners().length + " hyperlink listeners registered");
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
				System.out.println("*** TextEditorPanel.setText(): invalid base URL: " + mue);
			}
			//
			// --- set text ---
			try {
				// ### required for what?
				// ### serious oddity: contents of less then 14 characters are not displayed!
				/* ### if (text.length() <= 59) {	// ### 59 <html><head></head><body></body></html> + whitespace
					text = "<html><body><p></p></body></html>";
				} */
				textComponent.setText(text);
				textComponent.setCaretPosition(0);
			} catch (Throwable e) {
				textComponent.setText("<html><body><font color=#FF0000>Page can't be displayed</font></body></html>");
				System.out.println("*** TextEditorPanel.setText(): error while HTML rendering: " + e);
			}
			// ### System.out.println(">>> TextEditorPanel.setText(): after setContentType():  " + ((JEditorPane) textComponent).getHyperlinkListeners().length + " hyperlink listeners registered");
		} else {
			textComponent.setText(text);
			textComponent.setCaretPosition(0);
		}
		//
		textComponent.getDocument().addDocumentListener(documentListener);
	}



	// ***********************
	// *** Private Methods ***
	// ***********************



	// --- addButton (2 forms) ---

	private void addButton(JPanel container, Action action, Hashtable actions, Icon icon) {
		String actionName = (String) action.getValue(Action.NAME);
		String actionCommand = (String) action.getValue(Action.ACTION_COMMAND_KEY);
		JButton button = new JButton(icon);
		button.setActionCommand(actionCommand);
		button.addActionListener(DeepaMehtaClientUtils.getActionByName(actionName, actions));
		container.add(button);
	}

	// ---

	/**
	 * @see		#changedUpdate
	 * @see		#insertUpdate
	 * @see		#removeUpdate
	 */
	private void setDirty(String message) {
		if (!isDirty) {
			System.out.println("> text editor content changed (by " + message + ")");
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
				System.out.println("*** importData(): c=" + c);
			}
			try {
				DataFlavor[] flavors = t.getTransferDataFlavors();
				boolean hasStringFlavor = t.isDataFlavorSupported(DataFlavor.stringFlavor);
				boolean hasImageFlavor = t.isDataFlavorSupported(DataFlavor.imageFlavor);
				boolean hasFilelistFlavor = t.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
				//
				System.out.println(">>> import data to text panel (" + flavors.length + " flavors)");
				// ### for (int i = 0; i < flavors.length; i++) {
				// ###	System.out.println(flavors[i]);
				// ###}
				System.out.println("  >   string flavor supported: " + hasStringFlavor);
				System.out.println("  >    image flavor supported: " + hasImageFlavor);
				System.out.println("  > filelist flavor supported: " + hasFilelistFlavor);
				//
				// We do not allow dropping on top of the selected text
		        if ((source == textComponent) && (textComponent.getCaretPosition() >= p0.getOffset()) &&
                                                (textComponent.getCaretPosition() <= p1.getOffset())) {
					shouldRemove = false;
					System.out.println(">>> dropping on top of the selected text is not allowed -- import canceled");
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
						System.out.println(">>> IMG tag inserted: \"" + html + "\"");
					} else {
						textComponent.getDocument().insertString(pos, data, null);
						System.out.println(">>> regular text inserted: \"" + data + "\"");
					}
				} else if (hasFilelistFlavor) {
	                java.util.List files = (java.util.List) t.getTransferData(DataFlavor.javaFileListFlavor);
					System.out.println("    " + files.size() + " files:");
	                for (int i = 0; i < files.size(); i++) {
						File file = (File) files.get(i);
						String filename = file.getName();
						System.out.println("    " + file);
						if (DeepaMehtaUtils.isHTML(filename)) {
							String html = DeepaMehtaUtils.readFile(file);
							textComponent.setText(html);	// ### replace instead insert
							textComponent.setCaretPosition(0);
							// ### ((JEditorPane) textComponent).setPage("file://" + file);	// ### replace instead insert
							// ### setDirty("dropping HTML file");
							System.out.println(">>> HTML inserted (read from file)");
							break;	// ### max one file is inserted
						} else if (DeepaMehtaUtils.isImage(filename)) {
							HTMLEditorKit kit = (HTMLEditorKit) ((JEditorPane) textComponent).getEditorKit();
							HTMLDocument doc = (HTMLDocument) textComponent.getDocument();
							int pos = textComponent.getCaretPosition();
							String imagefile = file.getPath().replace('\\', '/');		// ###
							String html = "<img src=\"" + imagefile + "\"></img>";
							kit.insertHTML(doc, pos, html, 0, 0, HTML.Tag.IMG);	// ### <img> not XML conform
							// ### doc.insertBeforeStart(doc.getParagraphElement(pos), html); 
							System.out.println(">>> IMG tag inserted: \"" + html + "\"");
						} else {
							System.out.println("### importData(): only implemented for HTML files -- import canceled");
						}
					}
				} else {
					System.out.println("*** importData(): no supported flavor " + c);
				}
				return true;
            } catch (UnsupportedFlavorException ufe) {
                System.out.println("*** while dropping to text panel: " + ufe);
			} catch (BadLocationException ble) {
                System.out.println("*** while dropping to text panel: " + ble);
            } catch (IOException ioe) {
                System.out.println("*** while dropping to text panel: " + ioe);
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
				System.out.println("*** createTransferable(): c=" + c);
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
				System.out.println(">>> createTransferable(): p0=" + p0 + ", p1=" + p1);
			} catch (BadLocationException e) {
				System.out.println("*** createTransferable(): " +
					"Can't create position - unable to remove text from source.");
			}
			shouldRemove = true;
			String data = source.getSelectedText();
			return new StringSelection(data);
		}

		// Remove the old text if the action is a MOVE.
		// However, we do not allow dropping on top of the selected text, so in that case do nothing.
		protected void exportDone(JComponent c, Transferable data, int action) {
			if (c != textComponent) {	// ###
				System.out.println("*** exportDone(): c=" + c);
			}
			System.out.println(">>> exportDone(): action=" + action + ", MOVE=" + MOVE + ", shouldRemove=" + shouldRemove);
			if (shouldRemove && (action == MOVE)) {
				if ((p0 != null) && (p1 != null) && (p0.getOffset() != p1.getOffset())) {
					try {
						textComponent.getDocument().remove(p0.getOffset(), p1.getOffset() - p0.getOffset());
					} catch (BadLocationException e) {
						System.out.println("*** exportDone(): Can't remove text from source.");
					}
				}
			}
			source = null;
		}
	}
}
