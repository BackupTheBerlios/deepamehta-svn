package de.deepamehta.client;

import de.deepamehta.DeepaMehtaException;
import de.deepamehta.Detail;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.beans.PropertyVetoException;

import javax.swing.JInternalFrame;
import javax.swing.JTable;



/**
 * Extends the detail window model by a view.
 * <p>
 * The view consists of an internal window and "guides" which visually couple the window
 * with the origin topic/association/topicmap.
 * <p>
 * <hr>
 * Last functional change: 10.9.2007 (2.0b8)<br>
 * Last documentation update: 3.2.2008 (2.0b8)<br>
 * J&ouml;rg Richter<br>
 * jri@freenet.de
 */
class PresentationDetail extends Detail {



	/**
	 * Guides geometry.
	 */
	private static final int[][][] gi = {
		{{1, 0, 0}, {0, 0, 1}},
		{{1, 0}, {0, 0}},
		{{1, 1, 0}, {1, 0, 0}},
		{{0, 0}, {0, 1}},
		{{}, {}},
		{{1, 1}, {1, 0}},
		{{0, 0, 1}, {0, 1, 1}},
		{{0, 1}, {1, 1}},
		{{0, 1, 1}, {1, 1, 0}},
	};



	// **************
	// *** Fields ***
	// **************



	/**
	 * The window used to show the detail.
	 * <p>
	 * Note: remains uninitialized for {@link #DETAIL_CONTENT_NONE}.
	 */
	private JInternalFrame detailWindow;
	
	/**
	 * The contents of the detail window.
	 * <p>
	 * Note: remains uninitialized for {@link #DETAIL_CONTENT_NONE}.
	 */
	private Component content;

	/**
	 * Note: remains uninitialized for {@link #DETAIL_CONTENT_NONE}.
	 */
	private Color guideColor;

	private boolean firstPaint = true;

	// helper arrays for painting the guides
	int[] xv = new int[2]; int[] yv = new int[2];
	int[] xg = new int[4]; int[] yg = new int[4];



	// *******************
	// *** Constructor ***
	// *******************



	/**
	 * References checked: 3.2.2008 (2.0b8)
	 *
	 * @param	x	anchor coordinate for the guiding polygon
	 * @param	y	anchor coordinate for the guiding polygon
	 *
	 * @see		PresentationService#showDetail
	 */
	PresentationDetail(Detail detail, int x, int y, GraphPanelControler controler) {
		super(detail);
		//
		switch (contentType) {
		case DETAIL_CONTENT_NONE:
			// do nothing
			break;
		case DETAIL_CONTENT_TEXT:
			// create text editor component (single line)
			String text = (String) param1;
			boolean multiline = ((Boolean) param2).booleanValue();
			int editorType = multiline ? EDITOR_TYPE_DEFAULT : EDITOR_TYPE_SINGLE_LINE;
			TextEditorPanel textEditor = new TextEditorPanel(editorType, null, controler, false, multiline ? null : this);	// ### hyperlinkListener=null
			textEditor.setText(text);
			// show detail window
			int width = multiline ? TEXT_EDITOR_WIDTH : INPUT_LINE_WIDTH;
			int height = multiline ? TEXT_EDITOR_HEIGHT : INPUT_LINE_HEIGHT;
			createWindow(x, y, width, height, textEditor);
			//
			break;
		case DETAIL_CONTENT_HTML:
			// create text editor component (styled)
			String html = (String) param1;
			boolean editable = ((Boolean) param2).booleanValue();
			textEditor = new TextEditorPanel(EDITOR_TYPE_STYLED, null, controler, editable);	// ### hyperlinkListener=null
			textEditor.setText(html);
			textEditor.setEnabled(editable);
			// show detail window
			createWindow(x, y, TEXT_EDITOR_WIDTH, TEXT_EDITOR_HEIGHT, textEditor);
			//
			break;
		case DETAIL_CONTENT_IMAGE:
			// create image component
			String imagefile = (String) param1;
			ImageCanvas content = new ImageCanvas(controler.getImage(imagefile));
			// show detail window
			createWindow(x, y, 160, 220, content);		// ### width, height
			//
			break;
		case DETAIL_CONTENT_TABLE:
			// create table component
			String[] columnNames = (String[]) param1;
			String[][] values = (String[][]) param2;
			JTable table = new JTable(values, columnNames);
			// show detail window
			createWindow(x, y, 200, 81, table);
			//
			break;
		default:
			throw new DeepaMehtaException("unexpected detail content type: " + contentType);
		}
	}



	// ***************
	// *** Methods ***
	// ***************



	JInternalFrame getWindow() {
		return detailWindow;
	}

	/**
	 * References checked: 29.11.2001 (2.0a13-post3)
	 *
	 * @see		TextEditorPanel#actionPerformed
	 * @see		GraphPanel#hideNodeDetails
	 */
	void closeWindow() {
		try {
			detailWindow.setClosed(true);
		} catch (PropertyVetoException e) {
			throw new DeepaMehtaException("error while closing a detail window (" + e + ")");
		}
	}

	/**
	 * @see		GraphPanel#updateNodeDetail
	 */
	boolean isDirty() {
		switch (contentType) {
		case DETAIL_CONTENT_NONE:
		case DETAIL_CONTENT_IMAGE:
			return false;
		case DETAIL_CONTENT_TEXT:
		case DETAIL_CONTENT_HTML:
			return ((TextEditorPanel) content).isDirty();
		case DETAIL_CONTENT_TABLE:
			// ### pending
			return false;
		default:
			throw new DeepaMehtaException("unexpected detail content type: " + contentType);
		}
	}

	// ---

	/**
	 * @param	x	anchor coordinate
	 * @param	y	anchor coordinate
	 *
	 * @see		GraphPanel
	 */
	void paintGuide(Graphics g, int x, int y) {
		Rectangle fb = detailWindow.getBounds();
		xv[0] = fb.x; xv[1] = fb.x + fb.width;
		yv[0] = fb.y; yv[1] = fb.y + fb.height;
		// determine quadrant
		int q = y > yv[1] ? 6 : y > yv[0] ? 3 : 0;
		   q += x > xv[1] ? 2 : x > xv[0] ? 1 : 0;
		// build guide
		int c = gi[q][0].length;
		xg[0] = x; yg[0] = y;
		for (int cc = 0; cc < c; cc++) {
			xg[cc + 1] = xv[gi[q][0][cc]];
			yg[cc + 1] = yv[gi[q][1][cc]];
		}
		// paint guide
		g.setColor(guideColor);
		g.fillPolygon(new Polygon(xg, yg, c + 1));	// ### creating bad
	}

	/**
	 * @see		GraphPanel#updateNodeDetail
	 */
	void updateModel() {
		switch (contentType) {
		case DETAIL_CONTENT_NONE:
		case DETAIL_CONTENT_IMAGE:
			// ignore
			break;
		case DETAIL_CONTENT_TEXT:
		case DETAIL_CONTENT_HTML:
			param1 = ((TextEditorPanel) content).getText();
			break;
		case DETAIL_CONTENT_TABLE:
			// ### pending
			break;
		default:
			throw new DeepaMehtaException("unexpected detail content type: " + contentType);
		}
	}



	// ***********************
	// *** Private Methods ***
	// ***********************



	/**
	 * @see		#PresentationDetail
	 */
	private void createWindow(int x, int y, final int width, final int height, final Component content) {
		// ### this.width = width;
		// ### this.height = height;
		// --- initialize detail window ---
		detailWindow = new JInternalFrame(title, true, true, true) {
			public Dimension getPreferredSize() {	// ###
				System.out.println(">>> detail window \"" + getTitle() + "\" is asked " +
					"for size --> " + width + "x" + height);
				return new Dimension(width, height);
			}
			public void paint(Graphics g) {
				super.paint(g);
				// optionally focus the content
				if (firstPaint) {
					switch (contentType) {
					case DETAIL_CONTENT_NONE:
					case DETAIL_CONTENT_IMAGE:
						// ignore
						break;
					case DETAIL_CONTENT_TEXT:
					case DETAIL_CONTENT_HTML:
						((TextEditorPanel) content).requestFocus();
						break;
					case DETAIL_CONTENT_TABLE:
						// ### pending
						break;
					default:
						throw new DeepaMehtaException("unexpected detail content type: " + contentType);
					}
					firstPaint = false;
				}
			}
		};
		// Note: a TextEditorPanel may already contain a ScrollPane
		detailWindow.getContentPane().add(/* ### new JScrollPane( */ content);
		detailWindow.setLocation(x + 24, y + 24);
		detailWindow.setSize(width, height);
		detailWindow.setVisible(true);
		// --- initialize window content ---
		this.content = content;
		// --- initialize guide color ---
		guideColor = new Color(240, 240, 240);	// ###
	}
}
