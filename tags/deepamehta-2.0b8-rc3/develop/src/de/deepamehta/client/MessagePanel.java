package de.deepamehta.client;

import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.util.DeepaMehtaUtils;

import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;



/**
 * The message panel the client uses to notify the user about certain circumstances.
 * <P>
 * <HR>
 * Last functional change: 10.12.2006 (2.0b8)<BR>
 * Last documentation update: 10.11.2000 (2.0a7-pre2)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
class MessagePanel extends JPanel implements Runnable, DeepaMehtaConstants {



	// **************
	// *** Fields ***
	// **************




	/**
	 * The notification thread.
	 */
	private Thread notificationThread;

	/**
	 * The ring of notification icons.
	 */
	private Vector notificationRing = new Vector();

	/**
	 * The index of the currently presented notification icon in the ring of notification
	 * icons resp. <CODE>-1</CODE> if currently the empty icon is presented (the pseudo
	 * empty icon is virtually in the ring if the ring contains only one icon).
	 */
	private int notificationIndex;

	private Icon[] infoIcons = new Icon[NOTIFICATION_COUNT + 1];	// +1 counts the "empty" icon
	private AudioClip[] newMessageClip = new AudioClip[NOTIFICATION_COUNT + 1];
	private int mX, mY;
	private int tx, ty;												// global translation

	private ImageCanvas notificationPanel;
	private GraphPanel graphPanel;



	// *******************
	// *** Constructor ***
	// *******************



	/**
	 * @see		DeepaMehtaClient#showMessagePanel
	 */
	MessagePanel(final PresentationService ps) {
		graphPanel = ps.graphPanel;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBackground(DEFAULT_BGCOLOR_MESSAGE);
		//
		// --- load icons and sounds ---
		//
		if (ps.runsAsApplet) {
			System.out.print("> MessagePanel: loading icons and sounds ... ");
			// Note: +1 counts the "empty" icon
			for (int i = 0; i <= NOTIFICATION_COUNT; i++) {
				infoIcons[i] = new ImageIcon(ps.applet.getImage(ps.applet.getCodeBase(),
					FILESERVER_IMAGES_PATH + NOTIFICATION_ICONS[i]));
			}
			if (PLAY_SOUNDS) {
				for (int i = 1; i <= NOTIFICATION_COUNT; i++) {
					newMessageClip[i] = ps.applet.getAudioClip(ps.applet.getCodeBase(),
						FILESERVER_SOUNDS_PATH + NOTIFICATION_SOUNDS[i]);
				}
			}
		} else {
			// ### also the application must fetch the icons from the server
			System.out.print("> MessagePanel: loading icons ... ");
			// Note: +1 counts the "empty" icon
			for (int i = 0; i < NOTIFICATION_COUNT + 1; i++) {
				infoIcons[i] = new ImageIcon(FILESERVER_IMAGES_PATH + NOTIFICATION_ICONS[i]);
			}
		}
		System.out.println("done");
		// ---
		notificationPanel = new ImageCanvas();
		notificationPanel.setSize(20, 20);
		notificationPanel.setLocation(320, 11);
		notificationPanel.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				ps.notificationPanelClicked();
			}
		});
		graphPanel.setLayer(notificationPanel, JLayeredPane.PALETTE_LAYER.intValue() + 10);
		graphPanel.add(notificationPanel);
		//
		// --- registers for mouse pressed and dragged events ---
		//
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				thisPanelPressed(e);
			}
		});
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				thisPanelDragged(e);
			}
		});
	}



	// ******************************************************
	// *** Implementation of Interface java.lang.Runnable ***
	// ******************************************************



	public void run() {
		try {
			while (true) {
				nextNotificationIcon();
				Thread.sleep(NOTIFICATION_RATE);
			}
		} catch (InterruptedException e) {
			System.out.println("*** MessagePanel.run(): " + e);
		}
	}



	// ***************
	// *** Methods ***
	// ***************



	/**
	 * @see		#showNotificationIcon
	 * @see		DeepaMehtaClient#stateChanged	2x
	 */
	/* ### int getTabIndex() {
		Component c = getParent();
		if (c != null) {
			Component scrollPane = c.getParent();
			return tabbedPane.indexOfComponent(scrollPane);
		}
		return -1;
	} */



	// -------------------------
	// --- Overridden Method ---
	// -------------------------



	public void paint(Graphics g) {
		g.translate(tx, ty);
		super.paint(g);
	}



	// --------------------------
	// --- Additional Methods ---
	// --------------------------



	/**
	 * Method changed: 9.12.2001 (2.0a14-pre3)
	 *
	 * @see		DeepaMehtaClient#showMessage
	 */
	synchronized void addMessage(String text, int type) {
		String message = "<html><body><font face=\"Dialog\"><small>&nbsp;&nbsp;&nbsp;" + DeepaMehtaUtils.getTime(false) +
			"&nbsp;&nbsp;&nbsp;" + text + "</small></font></body></html>";
		Icon icon = infoIcons[type];
		JLabel label = new JLabel(message, icon, JLabel.LEFT);
		label.setForeground(Color.black);	// (default color is blue) ### use UIResource instead
		add(label);
		// play sound
		if (newMessageClip[type] != null) {
			newMessageClip[type].play();
		}
		// put icon in notification ring
		if (!notificationRing.contains(icon)) {
			notificationRing.addElement(icon);
		}
		// start notification
		if (notificationThread == null) {
			notificationThread = new Thread(this);
			notificationThread.start();
		}
	}

	/**
	 * @see		DeepaMehtaClient#stateChanged
	 */
	synchronized void stopNotification() {
		if (notificationThread != null) {
			notificationThread.stop();	// ### deprecated
			notificationThread = null;
			notificationRing.setSize(0);
			// set the "empty" icon
			showNotificationIcon(getEmptyIcon());
		}
	}

	/**
	 * @see		#stopNotification					(package)
	 * @see		#nextNotificationIcon				(private)
	 * @see		DeepaMehtaClient#showMessagePanel
	 */
	ImageIcon getEmptyIcon() {
		return (ImageIcon) infoIcons[0];
	}



	// ***********************
	// *** Private Methods ***
	// ***********************



	private synchronized void nextNotificationIcon() {
		int iconsCount = notificationRing.size();
		notificationIndex++;
		if (notificationIndex >= iconsCount) {
			// if there is only one icon in the notification ring the "empty" icon
			// is "virtually" contained in the notification ring
			notificationIndex = iconsCount > 1 ? 0 : -1;
		}
		Icon icon;
		if (notificationIndex == -1) {
			// the "empty" icon
			icon = getEmptyIcon();
		} else {
			icon = (Icon) notificationRing.elementAt(notificationIndex);
		}
		showNotificationIcon(icon);
	}

	/**
	 * @see		#stopNotification
	 * @see		#nextNotificationIcon
	 */
	private void showNotificationIcon(Icon icon) {
		notificationPanel.setImage(((ImageIcon) icon).getImage());
		notificationPanel.repaint();
		// ### graphPanel.repaint();
	}

	// ---

	private void thisPanelPressed(MouseEvent e) {
		this.mX = e.getX();
		this.mY = e.getY();
	}

	private void thisPanelDragged(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		int dx = x - mX;
		int dy = y - mY;
		mX = x;
		mY = y;
		tx += dx;
		ty += dy;
		tx = Math.min(tx, 0);
		ty = Math.min(ty, 0);
		repaint();
	}
}
