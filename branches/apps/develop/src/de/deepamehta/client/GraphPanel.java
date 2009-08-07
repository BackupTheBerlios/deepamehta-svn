package de.deepamehta.client;

import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.PresentableTopic;
import de.deepamehta.util.DeepaMehtaUtils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;



/**
 * An interactive <i>view</i> of a graph that consists of nodes and edges.
 * The <i>controler</i> is passed to the <code>GraphPanel</code> {@link #GraphPanel constructor}.
 * <p>
 * This is a singleton. The graphical DeepaMehta client (namely the {@link PresentationService}) instantiates only one
 * <code>GraphPanel</code> and switches the <i>model</i> on-the-fly by means of {@link #setModel}.
 * <p>
 * A controler class must implement the {@link GraphPanelControler} interface.
 * A node class must implement the {@link GraphNode} interface.
 * An edge class must implement the {@link GraphEdge} interface.
 * <p>
 * <hr>
 * Last functional change: 3.2.2008 (2.0b8)<br>
 * Last documentation update: 3.2.2008 (2.0b8)<br>
 * J&ouml;rg Richter<br>
 * jri@freenet.de
 */
class GraphPanel extends JDesktopPane implements ActionListener, DeepaMehtaConstants {



	// **************
	// *** Fields ***
	// **************



	private static final int CLICKED_ON_ICON = 1;
	private static final int CLICKED_ON_NAME = 2;

	// --- Model ---

	PresentationTopicMap topicmap;	// ### private

	/**
	 * The graph's nodes.
	 * <p>
	 * Key: node id (<code>String</code>)<br>
	 * Value: node ({@link GraphNode})
	 */
	private Hashtable nodes;

	/**
	 * The graph's edges.
	 * <p>
	 * Key: edge id (<code>String</code>)<br>
	 * Value: edge ({@link GraphEdge})
	 */
	private Hashtable edges;

	// types
	private Vector allNodeTypesV;
			Hashtable allNodeTypes;	// ### accessed by TopicMapEditorControler.checkTopicType()
	private Vector allEdgeTypesV;
			Hashtable allEdgeTypes;	// ### accessed by TopicMapEditorControler.checkAssociationType()

	// --- Controler ---

	/**
	 * The controler of this <code>GraphPanel</code>.
	 */
	private GraphPanelControler controler;

	// ---

	private Selection selection;
	private Point translation;				// graph translation
	private Rectangle bounds;				// the bounding rectangle of this graph

	/**
	 * Displayed node/edge details.
	 * <p>
 	 * ### The hashkey is composed by topicID/assocID AND detail command (one topic may have many
	 * details open at the same time, but no two of the same processing command)
 	 * <p>
	 * Key: "topicID:command" (<code>String</code>)<br>
	 * Value: detail ({@link PresentationDetail})
	 */
	private Hashtable details;

	private boolean dragInProgress;			// true if dragged before released
	private boolean translateInProgress;	// \
	private boolean moveInProgress;			// | max one is set
	private boolean edgeInProgress;			// |
	private boolean clusterInProgress;		// /
	//
	private Hashtable cluster;				// cluster of associated nodes
	private GraphNode targetNode;			// used while edgeInProgress
	private int ex, ey;						// used while edgeInProgress
	private int mX, mY;						// last mouse position
	private int topicMenuItemCount;
	private boolean creatingEdgesEnabled;
	//
	private Vector currentBunch;
	private int currentBunchIndex;
	//
	private int width, height;
	private Font font;						// used for topic name/label and association name
	private FontMetrics metrics;
	//
	private Image corporateImage;
	private Image customerImage;			// may be null
	//
	private Image topImage;
	private Image bottomImage;
	private Image leftImage;
	private Image rightImage;
	//
	JComponent graphPanel;



	// *******************
	// *** Constructor ***
	// *******************



	/**
	 * @see		PresentationService#createMainGUI
	 */
	GraphPanel(final GraphPanelControler controler) {
		this.controler = controler;
		//
		setLayout(null);

		graphPanel = new JComponent() {
			boolean firstPaint = true;

			/**
			 * Paints the graph.
			 */
			public void paint(Graphics g) {
				// Note: topicmap is null if no model is set
				if (topicmap == null) {
					System.out.println("*** GraphPanel can't be painted (no model set)");
					return;
				}
				// Note: the rendering hints must be set every time paint is called
				try {
					if (firstPaint) {
						metrics = g.getFontMetrics(font);
						firstPaint = false;
					}
					((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
					// ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					//	RenderingHints.VALUE_TEXT_ANTIALIAS_ON);	// ### does it help?
				} catch (Throwable e) {
					if (firstPaint) {
						System.out.println(">>> Drawing is not antialiased (JDK 1.2 is required): " + e + ", font=" + font);
					}
				}
				// --- background image ---
				Image bgImage = topicmap.getBackgroundImage();
				if (bgImage != null) {
					g.drawImage(bgImage, translation.x, translation.y, this);
				}
				//
				paintDetails(g);
				paintBorderImages(g);
				//
				g.translate(translation.x, translation.y);
				//
				paintEdges(g);
				paintNodes(g);
				//
				paintCorporateImages(g);
			}

			// ---

			private void paintNodes(Graphics g) {
				Enumeration e = nodes.elements();
				GraphNode node = null;
				while (e.hasMoreElements()) {
					try {
						node = (GraphNode) e.nextElement();
						paintNode(node, g);
					} catch (DeepaMehtaException e2) {
						System.out.println("*** node " + node + " not painted (" + e2.getMessage() + ")");
					}
				}
			}

			private void paintEdges(Graphics g) {
				Enumeration e = edges.elements();
				while (e.hasMoreElements()) {
					GraphEdge edge = (GraphEdge) e.nextElement();
					paintEdge(edge, g);
				}
				if (edgeInProgress) {
					Point p = selection.topic.getGeometry();
					g.setColor(EDGE_COLOR);
					DeepaMehtaUtils.paintLine(g, p.x, p.y, ex - translation.x, ey - translation.y, true);
				}
			}

			private void paintDetails(Graphics g) {
				Enumeration e = details.keys();
				while (e.hasMoreElements()) {
					String key = (String) e.nextElement();
					PresentationDetail pd = getDetail(key);
					String objectID = DeepaMehtaUtils.explode(key)[0];
					// ### copy in PresentationService.showDetail()
					int x, y;
					switch (pd.getType()) {
					case DETAIL_TOPIC:
						Point p = getNode(objectID).getGeometry();
						x = p.x;
						y = p.y;
						break;
					case DETAIL_ASSOCIATION:
						GraphEdge edge = getEdge(objectID);	// ### throws DME
						Point p1 = edge.getNode1().getGeometry();
						Point p2 = edge.getNode2().getGeometry();
						x = (p1.x + p2.x) / 2;
						y = (p1.y + p2.y) / 2;
						break;
					case DETAIL_TOPICMAP:
						x = pd.getGuideAnchor().x;
						y = pd.getGuideAnchor().y;
						break;
					default:
						throw new DeepaMehtaException("unexpected detail type: " + pd.getType());
					}
					//
					pd.paintGuide(g, x + translation.x, y + translation.y);
				}
			}

			private void paintBorderImages(Graphics g) {
				if (showTopImage()) {
					g.drawImage(topImage, (width - BORDER_IMAGE_WIDTH) / 2, 0, this);
				}
				if (showBottomImage()) {
					g.drawImage(bottomImage, (width - BORDER_IMAGE_WIDTH) / 2, height -
						BORDER_IMAGE_HEIGHT, this);
				}
				if (showLeftImage()) {
					g.drawImage(leftImage, 0, (height - BORDER_IMAGE_WIDTH) / 2, this);
				}
				if (showRightImage()) {
					g.drawImage(rightImage, width - BORDER_IMAGE_HEIGHT, (height -
						BORDER_IMAGE_WIDTH) / 2, this);
				}
			}

			private void paintCorporateImages(Graphics g) {
				g.drawImage(corporateImage, width - 104 - translation.x, height - 50 - translation.y, this);
				if (customerImage != null) {
					g.drawImage(customerImage, 2 - translation.x, height - 50 - translation.y, this);
				}
			}
		};	// end of anonymous inner class

		setLayer(graphPanel, DEFAULT_LAYER.intValue());
		setToolTipText("");		// non-null required to switch tooltips on.
								// Note: the string doesn't matter because we override getToolTipText()
		add(graphPanel);
		//
		// --- registers for mouse pressed, dragged and released events ---
		//
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				thisPanelPressed(e);
			}
			public void mouseReleased(MouseEvent e) {
				thisPanelReleased(e);
			}
		});
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				thisPanelDragged(e);
			}
		});
		//
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				Dimension s = getSize();
				//
				graphPanel.setSize(s);
				// update border images
				width = s.width;
				height = s.height;
				repaint();
			}
		});
	}



	// *****************************************************************
	// *** Implementation of interface java.awt.event.ActionListener ***
	// *****************************************************************



	/**
	 * Handles the actions fired by popup menus.
	 */
	public void actionPerformed(ActionEvent e) {
		controler.beginLongTask();
		//
		String command = e.getActionCommand();
		JMenuItem item = (JMenuItem) e.getSource();
		JPopupMenu menu = (JPopupMenu) item.getParent();
		String menuID = menu.getLabel();	// the menuID is stored in the menu's label
		//
		if (menuID.equals(MENU_TOPIC)) {
			controler.processNodeCommand(topicmap, selection.topic, command);
		} else if (menuID.equals(MENU_ASSOC)) {
			controler.processEdgeCommand(topicmap, selection.assoc, command);
		} else if (menuID.equals(MENU_VIEW)) {
			// Note: the parameters of view commands are extended by the click coordinates
			controler.processGraphCommand(topicmap, command + COMMAND_SEPARATOR +
				(mX - translation.x) + COMMAND_SEPARATOR + (mY - translation.y));
		} else {
			throw new DeepaMehtaException("unexpected popup menu: " + menu + " (menuID: \"" + menuID +
				"\") -- command \"" + command + "\" not processed");
		}
		//
		controler.endTask();
	}



	// ***************
	// *** Methods ***
	// ***************



	// Overrides JComponent
	public String getToolTipText(MouseEvent evt) {
		int x = evt.getX();
		int y = evt.getY();
		Vector foundNodes = findAllNodes(x, y);
		int count = foundNodes.size();
		if (count == 0) {
			return null;
		}
		// ### System.out.println("> x=" + x + " y=" + y + " ==> " + foundNodes.size() + " topics");
		boolean showTip = false;
		StringBuffer tipText = new StringBuffer("<html>");
		Enumeration e = foundNodes.elements();
		while (e.hasMoreElements()) {
			GraphNode node = ((FoundNode) e.nextElement()).node;
			if (count == 1 && type(node).getHiddenTopicNames() || count > 1) {
				tipText.append((showTip ? "<br>" : "") + (count > 1 ? "- " : "") + node.getName());
				showTip = true;
			}
		}
		return showTip ? tipText.toString() : null;
	}

	// ---

	void setTypes(Vector nodeTypesV, Hashtable nodeTypes, Vector edgeTypesV, Hashtable edgeTypes) {
		this.allNodeTypes = nodeTypes;
		this.allNodeTypesV = nodeTypesV;
		this.allEdgeTypes = edgeTypes;
		this.allEdgeTypesV = edgeTypesV;
	}

	void setImages(Image corpImg, Image custImg,
				   Image topImg, Image leftImg, Image buttomImg, Image rightImg /* ###,
				   Image boldImg, Image italicImg, Image underlineImg */) {
		this.corporateImage = corpImg;
		this.customerImage = custImg;
		//
		this.topImage = topImg;
		this.leftImage = leftImg;
		this.bottomImage = buttomImg;
		this.rightImage = rightImg;
	}

	/**
	 * @see		PresentationService#selectTopicmap
	 */
	void setModel(PresentationTopicMap topicmap, Selection selection, Rectangle bounds, Hashtable details,
											boolean creatingEdgesEnabled, Font font) {
		System.out.println(">>> GraphPanel.setModel(): font=" + font);
		removeDetailWindows();
		//
		this.topicmap = topicmap;
		this.nodes = topicmap.getTopics();
		this.edges = topicmap.getAssociations();
		this.translation = topicmap.getTranslation();
		this.selection = selection;
		this.bounds = bounds;
		this.details = details;
		this.creatingEdgesEnabled = creatingEdgesEnabled;
		this.font = font;
		//
		setBackgroundColor(topicmap.getBackgroundColor());
		addDetailWindows();
	}

	// ---

	// ### copy in TopicmapEditorModel
	int getSelected() {
		return selection.mode;
	}

	// ---

	/* ### int getBoundsWidth() {
		return xMax - xMin;
	}

	int getBoundsHeight() {
		return yMax - yMin;
	} */

	// ---

	Point getTranslation() {
		return translation;
	}

	/**
	 * @see		#setModel
	 * @see		PresentationService#changeBackgroundColor
	 */
	void setBackgroundColor(Color color) {
		setBackground(color);
	}



	// ----------------------
	// --- Handling Menus ---
	// ----------------------



	/**
	 * @see		TopicmapEditorModel#showMenu
	 */
	void showMenu(String menuID, PresentationCommands commands, int x, int y) {
		if (!commands.isEmpty()) {
			commands.popupMenu(this, menuID).show(this, x, y);
		}
	}



	// -----------------------------
	// --- Handling Node Details ---
	// -----------------------------



	// ### copy in TopicmapEditorModel
	private PresentationDetail getDetail(String key) {
		return (PresentationDetail) details.get(key);
	}

	// ---

	private void removeDetailWindows() {
		// Note: details is null if no model is set
		if (details == null) {
			return;
		}
		//
		Enumeration e = details.elements();
		while (e.hasMoreElements()) {
			PresentationDetail detail = (PresentationDetail) e.nextElement();
			remove(detail.getWindow());
		}
	}

	private void addDetailWindows() {
		Enumeration e = details.elements();
		while (e.hasMoreElements()) {
			JInternalFrame detailWindow = ((PresentationDetail) e.nextElement()).getWindow();
			addDetailWindow(detailWindow);
		}
	}

	/**
	 * @see		#addDetailWindows
	 * @see		PresentationService#showDetail
	 */
	void addDetailWindow(JInternalFrame detailWindow) {
		setLayer(detailWindow, JLayeredPane.PALETTE_LAYER.intValue());
		add(detailWindow);
	}



	// --------------------------
	// --- Painting (private) ---
	// --------------------------



	private void paintNode(GraphNode node, Graphics g) throws DeepaMehtaException {
		Point p = node.getGeometry();
		// error check
		if (p == null) {
			throw new DeepaMehtaException("topic has no geometry");
		}
		//
		// --- paint icon ---
		Image icon = getIcon(node);
		int iconWidth = icon.getWidth(this);
		int iconHeight = icon.getHeight(this);
		// ### System.out.println("  > size=" + iconWidth + "x" + iconHeight + " pixel");	// ###
		int iw2 = iconWidth / 2;
		int ih2 = iconHeight / 2;
		g.drawImage(icon, p.x - iw2, p.y - ih2, this);
		// --- paint selection ---
		if (node == selection.topic) {
			g.setColor(Color.red);
			g.drawRect(p.x - iw2 - 2, p.y - ih2 - 2, iconWidth + 3, iconHeight + 3);
			g.drawRect(p.x - iw2 - 3, p.y - ih2 - 3, iconWidth + 5, iconHeight + 5);
		}
		// --- paint name ---
		if (getAppearanceType(node) == null || !type(node).getHiddenTopicNames()) {
			g.setFont(font);
			g.setColor(TEXT_COLOR);
			g.drawString(node.getName(), p.x - iw2, p.y - ih2 + iconHeight + font.getSize() + 2);
		}
		// --- paint label ---
		if (node.getLabel() != null) {
			g.drawString(node.getLabel(), p.x - iw2, p.y - ih2 - 4);
		}
	}

	private void paintEdge(GraphEdge edge, Graphics g) {
		PresentationType type = type(edge);		// for getting edge color
		// error check 1
		if (type == null) {
			System.out.println("*** GraphPanel.paintEdge(): " + edge +
				": type doesn't exist -- edge not painted");
			return;
		}
		// get involved nodes
		GraphNode node1 = edge.getNode1();
		GraphNode node2 = edge.getNode2();
		// error check 2
		if (node1 == null) {
			System.out.println("*** GraphPanel.paintEdge(): " + edge +
				": node 1 not set -- edge not painted");
			// ### Note: error has been reported before
			return;
		}
		if (node2 == null) {
			System.out.println("*** GraphPanel.paintEdge(): " + edge +
				": node 2 not set -- edge not painted");
			// ### Note: error has been reported before
			return;
		}
		// get node geometry
		Point p1 = node1.getGeometry();
		Point p2 = node2.getGeometry();
		// error check 3
		if (p1 == null) {
			System.out.println("*** GraphPanel.paintEdge(): " + node1 +
				" has no gemoetry -- edge not painted");
			return;
		}
		if (p2 == null) {
			System.out.println("*** GraphPanel.paintEdge(): " + node2 +
				" has no gemoetry -- edge not painted");
			return;
		}
		// mark edge
		if (edge == selection.assoc) {
			g.setColor(Color.red);
			g.drawLine(p1.x - 2, p1.y - 2, p2.x - 2, p2.y - 2);
			g.drawLine(p1.x + 3, p1.y - 2, p2.x + 3, p2.y - 2);
			g.drawLine(p1.x - 2, p1.y + 3, p2.x - 2, p2.y + 3);
			g.drawLine(p1.x + 2, p1.y + 3, p2.x + 2, p2.y + 3);
		}
		// paint edge
		Color color = type.getColor();
		boolean directed = !edge.getType().equals("at-generic") &&			
						   !edge.getType().equals("at-kompetenzstern");		// ### application specific
		g.setColor(color);
		DeepaMehtaUtils.paintLine(g, p1.x, p1.y, p2.x, p2.y, directed);
		// --- paint name ---
		g.setFont(font);
		g.setColor(EDGE_COLOR);
		g.drawString(edge.getName(), (p1.x + p2.x) / 2, (p1.y + p2.y) / 2 - 5);
	}



	// ---------------------------------------
	// --- Handling mouse events (private) ---
	// ---------------------------------------



	private void thisPanelPressed(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		mX = x;
		mY = y;
		// click precedence (raising): CLICKED_ON_NAME node -> edge -> CLICKED_ON_ICON node
		FoundNode foundNode = findNode(x, y, !isSpecial(e));
		GraphNode node = foundNode != null ? foundNode.node : null;
		GraphEdge edge = foundNode == null || foundNode.clickedMode == CLICKED_ON_NAME ? findEdge(x, y) : null;
		// Note: an edge is only be searched if no node was found resp. the node was CLICKED_ON_NAME
		if (node != null && edge != null) {
			// Note: an edge dominates a CLICKED_ON_NAME node
			edgeClicked(edge, e);
		} else if (node != null) {
			nodeClicked(node, e);
		} else if (edge != null) {
			edgeClicked(edge, e);
		} else {
			graphClicked(e);
		}
	}

	private void thisPanelDragged(MouseEvent e) {
		if (moveInProgress || clusterInProgress || translateInProgress) {
			if (!dragInProgress) {
				controler.beginTranslation();
				dragInProgress = true;
			}
			int x = e.getX();
			int y = e.getY();
			int dx = x - mX;
			int dy = y - mY;
			mX = x;
			mY = y;
			if (moveInProgress) {
				translateNode(selection.topic, dx, dy);
				controler.updateBounds(topicmap);
			} else if (clusterInProgress) {
				translateCluster(dx, dy);
				controler.updateBounds(topicmap);
			} else {
				translateGraph(dx, dy);
			}
			repaint();
		} else if (edgeInProgress) {
			if (!dragInProgress) {
				controler.beginCreatingEdge();
				dragInProgress = true;
			}
			ex = e.getX();
			ey = e.getY();
			FoundNode foundNode = findNode(ex, ey, false);
			targetNode = foundNode != null ? foundNode.node : null;
			if (targetNode != null) {
				Point p = targetNode.getGeometry();
				ex = p.x + translation.x;
				ey = p.y + translation.y;
			}
			repaint();
		}
	}

	private void thisPanelReleased(MouseEvent e) {
		if (moveInProgress) {
			moveInProgress = false;
			if (dragInProgress) {
				// update geometry
				Hashtable nodes = new Hashtable();
				nodes.put(selection.topic.getID(), selection.topic);
				controler.nodesMoved(topicmap, nodes);
				controler.endTask();
				dragInProgress = false;
			} else {
				// ### nodeSelected(node);
			}
		} else if (clusterInProgress) {
			clusterInProgress = false;
			if (dragInProgress) {
				controler.nodesMoved(topicmap, cluster);
				controler.endTask();
				dragInProgress = false;
			} else {
				// ### edgeSelected(edge);
			}
		} else if (translateInProgress) {
			translateInProgress = false;
			if (dragInProgress) {
				controler.graphMoved(topicmap, translation.x, translation.y);
				controler.endTask();
				dragInProgress = false;
			} else {
				// ### graphSelected();
			}
		} else if (edgeInProgress) {
			edgeInProgress = false;
			//
			controler.endTask();
			dragInProgress = false;
			//
			if (targetNode != null && targetNode != selection.topic) {
				controler.processGraphCommand(topicmap, CMD_CREATE_ASSOC + COMMAND_SEPARATOR +
					selection.topic.getID() + COMMAND_SEPARATOR + targetNode.getID());
			} else {
				repaint();
			}
		}
	}

	// ---

	/**
	 * @see		#thisPanelPressed
	 */
	private void nodeClicked(GraphNode node, MouseEvent e) {
		// Note: if the nodes type has set "Disabled" the node doesn't react upon clicking
		try {
			if (type(node).isDisabled()) {	// type() throws DME
				return;
			}
		} catch (DeepaMehtaException e2) {
			// ignore
		}
		// --- perform node selection ---
		nodeSelected(node);		// ###
		// --- perform special (doubleclick, right-click, alt-click) ---
		int x = e.getX();
		int y = e.getY();
		if (e.getClickCount() == 2) {		// double clicked
			controler.beginLongTask();
			controler.nodeDoubleClicked(topicmap, selection.topic);
			controler.endTask();
		// ### Note: alt is checked BEFORE right-click because on Mac OS X platform
		// ### isPopupTrigger() supersedes isAltDown()
		} else if (e.isAltDown()) {			// alt modifier is pressed -- start creating an edge
			if (creatingEdgesEnabled) {
				edgeInProgress = true;
				targetNode = null;
				ex = x;
				ey = y;
			}
		} else if (isPopupTrigger(e)) {		// right-click -- show node context menu
			controler.beginLongTask();
			controler.showNodeMenu(topicmap, node, x, y);
			controler.endTask();
		} else {							// default -- start moving a node
			// ### if (controler.topicTranslationAllowed(topicmap)) {
			if (!((PresentableTopic) node).isLocked()) {
				moveInProgress = true;
			}
		}
	}

	/**
	 * @see		#thisPanelPressed
	 */
	private void edgeClicked(GraphEdge edge, MouseEvent e) {
		// Note: if the edges type has set "Disabled" the edge doesn't react upon clicking
		if (type(edge).isDisabled()) {
			return;
		}
		// --- perform edge selection ---
		edgeSelected(edge);
		// --- perform special (doubleclick, right-click) ---
		if (e.getClickCount() == 2) {		// double clicked
			controler.beginLongTask();
			controler.edgeDoubleClicked(topicmap, selection.assoc);
			controler.endTask();
		} else if (isPopupTrigger(e)) {		// right-click -- show edge context menu
			controler.beginLongTask();
			controler.showEdgeMenu(topicmap, edge, e.getX(), e.getY());
			controler.endTask();
		} else {	// default -- start moving a cluster
			cluster = createNodeCluster(edge.getNode1());	// ### create later
			if (!clusterIsLocked()) {
				clusterInProgress = true;
			}
		}
	}

	/**
	 * @see		#thisPanelPressed
	 */
	private void graphClicked(MouseEvent e) {
		// --- perform graph selection ---
		graphSelected();	// ###
		// --- perform special (right-click) ---
		if (isPopupTrigger(e)) {	// right-click -- show graph context menu
			controler.beginLongTask();
			controler.showGraphMenu(topicmap, e.getX(), e.getY());
			controler.endTask();
		} else {	// default -- start moving the graph
			translateInProgress = true;
		}
	}

	// ---

	/**
	 * Informs the controler about a node selection.
	 * <p>
	 * ### This is only performed if the specified node is not already the selected node.
	 *
	 * @see		#nodeClicked
	 */
	private void nodeSelected(GraphNode node) {
		if (node != selection.topic) {
			controler.nodeSelected(topicmap, node);
		}
	}

	/**
	 * Informs the controler about an edge selection.
	 * <p>
	 * ### This is only performed if the specified edge is not already the selected edge.
	 *
	 * @see		#edgeClicked
	 */
	private void edgeSelected(GraphEdge edge) {
		if (edge != selection.assoc) {
			controler.edgeSelected(topicmap, edge);
		}
	}

	/**
	 * Informs the controler about graph selection.
	 * <p>
	 * ### This is only performed if the graph is not already selected.
	 *
	 * @see		#graphClicked
	 */
	private void graphSelected() {
		if (selection.mode != SELECTED_TOPICMAP) {
			controler.graphSelected(topicmap);
		}
	}

	// ---

	private boolean isSpecial(MouseEvent e) {
		return e.getClickCount() == 2 || e.isAltDown() || isPopupTrigger(e);
	}

	/**
	 * Checks weather the specified mouse event represents a popup trigger.
	 * <p>
	 * ### This is a workaround because <code>InputEvent.isPopupTrigger()</code>
	 * doesn't work on Windows platform.
	 *
	 * @see		#nodeClicked
	 * @see		#edgeClicked
	 * @see		#graphClicked
	 */
	private boolean isPopupTrigger(MouseEvent e) {
		if (e.isPopupTrigger()) {
			return true;
		} else if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0) {
			return true;
		}
		return false;
	}



	// ----------------------------------------
	// --- Creating node clusters (private) ---
	// ----------------------------------------



	/**
	 * Returns the cluster of visible connected nodes that contains the specified node.
	 *
	 * @see		#edgeClicked
	 */
	private Hashtable createNodeCluster(GraphNode node) {
		Hashtable cluster = new Hashtable();
		// ### error check
		if (node == null) {
			return cluster;		// already reported
		}
		// start recursion
		nodeCluster(node, cluster);
		System.out.println(">>> GraphPanel.createNodeCluster(): " + cluster.size() + " nodes");
		//
		return cluster;
	}
	
	/**
	 * Called recursively.
	 */
	private void nodeCluster(GraphNode node, Hashtable cluster) {
		// ### error check
		if (node == null) {
			return;		// already reported
		}
		//
		String id = node.getID();
		if (cluster.get(id) == null) {
			cluster.put(id, node);
			Enumeration e = node.getEdges();
			GraphEdge edge;
			while (e.hasMoreElements()) {
				edge = (GraphEdge) e.nextElement();
				nodeCluster(node.relatedNode(edge), cluster);
			}
		}
	}

	// ---

	private boolean clusterIsLocked() {
		Enumeration e = cluster.elements();
		while (e.hasMoreElements()) {
			PresentableTopic topic = (PresentableTopic) e.nextElement();
			if (topic.isLocked()) {
				return true;
			}
		}
		return false;
	}



	// ------------------------------
	// --- Translations (private) ---
	// ------------------------------



	/**
	 * @see		#thisPanelDragged
	 */
	private void translateGraph(int x, int y) {
		translation.x += x;
		translation.y += y;
		//
		Enumeration e = details.elements();
		while (e.hasMoreElements()) {
			PresentationDetail detail = (PresentationDetail) e.nextElement();
			translateDetail(detail, x, y);
		}
	}

	/**
	 * @see		#thisPanelDragged
	 */
	private void translateCluster(int x, int y) {
		Enumeration e = cluster.elements();
		while (e.hasMoreElements()) {
			GraphNode node = (GraphNode) e.nextElement();
			translateNode(node, x, y);
		}
	}

	/**
	 * @see		#thisPanelDragged
	 */
	private void translateNode(GraphNode node, int x, int y) {
		node.getGeometry().translate(x, y);
		PresentationDetail detail = getDetail(node.getID());
		if (detail != null) {
			translateDetail(detail, x, y);
		}
	}



	/**
	 * @see		#translateGraph
	 * @see		#translateNode
	 */
	private void translateDetail(PresentationDetail detail, int x, int y) {
		JInternalFrame detailWindow = detail.getWindow();
		Point l = detailWindow.getLocation();
		detailWindow.setLocation(l.x + x, l.y + y);
	}



	// -------------------------------------
	// --- Finding nodes/edges (private) ---
	// -------------------------------------



	/**
	 * References checked: 9.8.2001 (2.0a11)
	 *
	 * @return	the node at a given position or <code>null</code> if there is no node.
	 *
	 * @see		#thisPanelPressed
	 * @see		#thisPanelDragged
	 */
	private FoundNode findNode(int x, int y, boolean selectNextInBunch) {
		Vector bunch = findAllNodes(x, y);
		//
		if (bunch.equals(currentBunch)) {
			if (selectNextInBunch && currentBunch.size() > 0) {
				currentBunchIndex = (currentBunchIndex + 1) % currentBunch.size();
				// ### System.out.println(">>> same bunch (" + currentBunch.size() + " nodes) ==> new index is " + currentBunchIndex);
			}
		} else {
			currentBunch = bunch;
			currentBunchIndex = 0;
			// ### System.out.println(">>> new bunch (" + bunch.size() + " nodes) ==> begin at 0");
		}
		//
		return currentBunch.size() > 0 ? (FoundNode) currentBunch.elementAt(currentBunchIndex) : null;
	}

	/**
	 * @see		#getToolTipText
	 * @see		#findNode
	 */
	private Vector findAllNodes(int x, int y) {
		x -= translation.x;
		y -= translation.y;
		//
		Vector foundNodes = new Vector();
		Enumeration e = nodes.elements();
		while (e.hasMoreElements()) {
			GraphNode node = (GraphNode) e.nextElement();
			Point p = node.getGeometry();
			Image icon = getIcon(node);
			int iconWidth = icon.getWidth(this);
			int iconHeight = icon.getHeight(this);
			int iw2 = iconWidth / 2;
			int ih2 = iconHeight / 2;
			// check if inside icon
			if (Math.abs(x - p.x) <= iw2 && Math.abs(y - p.y) <= ih2) {
				foundNodes.addElement(new FoundNode(node, CLICKED_ON_ICON));
			}
			// check if inside name
			if (!type(node).getHiddenTopicNames() &&
				x >= p.x - iw2 && x <= p.x - iw2 + metrics.stringWidth(node.getName()) &&
				y >= p.y - ih2 + iconHeight + 2 && y <= p.y - ih2 + iconHeight + font.getSize() + 2) {
				foundNodes.addElement(new FoundNode(node, CLICKED_ON_NAME));
				// Note: there is no break here -- the search is continued because
				// the found node could be dominated by a CLICKED_ON_ICON node
			}
		}
		return foundNodes;
	}

	/**
	 * @see		#thisPanelPressed
	 *
	 * @return	The edge at the specified position or <code>null</code> if there is no edge.
	 */
	private GraphEdge findEdge(int x, int y) {
		x -= translation.x;
		y -= translation.y;
		Enumeration e = edges.elements();
		GraphEdge edge = null;
		GraphNode node1, node2;
		Point p1, p2;
		// --- phase 1: collect edge candidates based on bounding rectangle ---
		Vector candidates = new Vector();
		while (e.hasMoreElements()) {
			edge = (GraphEdge) e.nextElement();
			node1 = edge.getNode1();
			node2 = edge.getNode2();
			if (node1 == null || node2 == null) {
				// Note: this is an error condition and has been already reported
				continue;
			}
			p1 = node1.getGeometry();
			p2 = node2.getGeometry();
			if (edgeHit(p1, p2, new Point(x,y))) {
				candidates.addElement(edge);
			}
		}
		int candCount = candidates.size();
		if (candCount == 0) {
			return null;
		}
		if (candCount == 1) {
			// ### this is a workaround to enable selection of horizontal resp. vertical
			// edges
			return (GraphEdge) candidates.firstElement();
		}
		// --- phase 2: determine the nearest edge ---
		// System.out.print(candCount + " candidates found:");
		e = candidates.elements();
		float dist;
		float minDist = 100;
		GraphEdge nearestEdge = null;
		while (e.hasMoreElements()) {
			edge = (GraphEdge) e.nextElement();
			p1 = edge.getNode1().getGeometry();
			p2 = edge.getNode2().getGeometry();
			dist = Math.abs(Math.abs(((x - p1.x) / (float) (y - p1.y)) /
									 ((p2.x - p1.x) / (float) (p2.y - p1.y))) - 1);
			// System.out.print("   " + dist);
			if (dist < minDist) {
				minDist = dist;
				nearestEdge = edge;
			}
		}
		// System.out.println();
		return minDist < 0.3 ? nearestEdge : null;
	}

	/**
	 * @param	p1		one endpoint of the edge
	 * @param	p2		other endpoint of the edge
	 * @param	hit		the point clicked
	 *
	 * @see		#findEdge
	 */
	private boolean edgeHit(Point p1, Point p2, Point hit) {
		// original implementation
		/*
		int x = hit.x;
		int y = hit.y;
		xMin = Math.min(p1.x, p2.x) - 2;
		xMax = Math.max(p1.x, p2.x) + 2;
		yMin = Math.min(p1.y, p2.y) - 2;
		yMax = Math.max(p1.y, p2.y) + 2;
		return (x >= xMin && x <= xMax && y >= yMin && y <= yMax);
		*/
		// a, b, and c should be considered as geometric vectors with components x, y
		Point a = new Point(hit.x - p1.x, hit.y - p1.y);  // vector from p1 to hit
		Point b = new Point(p2.x  - p1.x, p2.y  - p1.y);  // vector from p1 to p2
		float ab_scalarproduct = (float) a.x * b.x + a.y * b.y;  // scalar product of a and b
		float b_length_square = (float) b.x * b.x + b.y * b.y;  // 
		if (b_length_square == 0) {
			return false;
		}		
		float x = ab_scalarproduct / b_length_square;
		Point c = new Point((int) (x * b.x) - a.x, (int) (x * b.y) - a.y);
		int c_length_square = c.x * c.x + c.y * c.y;		
		return 0 < x && x < 1 && c_length_square <= 25;
	}



	// ---------------------------------------
	// --- Showing border images (private) ---
	// ---------------------------------------



	// Note: xMin, xMax, yMin, yMax are not valid if there are no nodes contained in this graph,
	// in this case isEmpty() returns true

	private boolean showTopImage() {
		return !isEmpty() && bounds.y + translation.y < 0;
	}

	private boolean showBottomImage() {
		return !isEmpty() && bounds.y + bounds.height + translation.y > height;
	}

	private boolean showLeftImage() {
		return !isEmpty() && bounds.x + translation.x < 0;
	}

	private boolean showRightImage() {
		return !isEmpty() && bounds.x + bounds.width + translation.x > width;
	}



	// ----------------------
	// --- Misc (private) ---
	// ----------------------



	/**
	 * Returns the icon that is actually used to paint the specified node.
	 *
	 * @return	the icon, or <code>null</code> if it can't be determinded due to an error.
	 *
	 * @see		paintNode
	 * @see		findAllNodes
	 */
	private Image getIcon(GraphNode node) {
		Image icon = node.getImage();	// individual icon
		if (icon == null) {
			PresentationType type = getAppearanceType(node);
			// Note: if type is null icon remains null
			if (type != null) {
				// error check
				if (!type.hasImage()) {
					throw new DeepaMehtaException("type has no icon");
				}
				icon = type.getImage();
			}
		}
		return icon;
	}

	/**
	 * Returns the type that is determining the appearance of the specified node.
	 * <p>
	 * Note: topics and type topics have different appearance logic.
	 * - type topics have always an image
	 * - the image of type topics may be dynamically created
	 *
	 * @return	the type, or <code>null</code> if the type can't be retrieved due to an error.
	 *
	 * @see		paintNode
	 * @see		getIcon
	 */
	private PresentationType getAppearanceType(GraphNode node) {
		PresentationType type;
		//
		String id = node.getID();
		String typeID = node.getType();
		try {
			if (typeID.equals(TOPICTYPE_TOPICTYPE)) {
				type = nodeType(id);
			} else if (typeID.equals(TOPICTYPE_ASSOCTYPE)) {
				type = edgeType(id);
			} else {
				type = type(node);		// throws DME ### not nice
			}
		} catch (DeepaMehtaException e) {
			// ignore -- Note: type is null
			type = null;
		}
		return type;
	}

	// --- type (2 forms) ---

	/**
	 * @see		#paintNode
	 * @see		#nodeClicked
	 * @see		#findNode
	 */
	private PresentationType type(GraphNode node) throws DeepaMehtaException {
		return nodeType(node.getType());	// nodeType() throws DME
	}

	/**
	 * @see		#paintEdge
	 * @see		#edgeClicked
	 */
	private PresentationType type(GraphEdge edge) throws DeepaMehtaException {
		return edgeType(edge.getType());	// edgeType() throws DME
	}

	// ---

	/**
	 * @see		#paintNode
	 * @see		#type(GraphNode node)
	 */
	private PresentationType nodeType(String typeID) throws DeepaMehtaException {
		controler.checkTopicType(typeID);			// throws DME
		return (PresentationType) allNodeTypes.get(typeID);
	}

	/**
	 * @see		#paintNode
	 * @see		#type(GraphEdge edge)
	 */
	private PresentationType edgeType(String typeID) throws DeepaMehtaException {
		controler.checkAssociationType(typeID);		// throws DME
		return (PresentationType) allEdgeTypes.get(typeID);
	}

	// ---

	private GraphNode getNode(String nodeID) {
		return (GraphNode) nodes.get(nodeID);
	}

	private GraphEdge getEdge(String edgeID) {
		return (GraphEdge) edges.get(edgeID);
	}

	// ---

	private boolean isEmpty() {
		return nodes.size() == 0;
	}



	// ***************************
	// *** Private Inner Class ***
	// ***************************



	private class FoundNode {
	
		GraphNode node;
		int clickedMode;
		
		FoundNode(GraphNode node, int clickedMode) {
			this.node = node;
			this.clickedMode = clickedMode;
		}

		public boolean equals(Object o) {
			FoundNode fn = (FoundNode) o;
			return node == fn.node && clickedMode == fn.clickedMode;
		}
	}
}
