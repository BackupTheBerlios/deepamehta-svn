package de.deepamehta.client;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;


/**
 *
 * @author Boris Gomolka (boris@neuron-interworks.de)
 * Created on 5. Mai 2005, 19:14 <br>
 * Last functional change: 21.05.2005 <br>
 * Last documentation update: 7.05.2005 <br>
 */
public class MenuScrollHelper implements ActionListener, MouseListener, MouseWheelListener {

    public static final int UP = -1;
    public static final int DOWN = 1;
    public static final int NEUTRAL = 0;

    public static final int DEFAULT_HEIGHT = 26;

    public static final String UID = "up";
    public static final String DID = "down";

    public static final String ULABEL = "Scroll up";
    public static final String DLABEL = "Scroll down";

    public static final int SCROLL_DELAY = 150;

    private int offset = 0;
    private int direction = 0;
    private int max = 0;

    private JPopupMenu jMenu;
    private List elements;
    private ScrollThread t;

    private JMenuItem up;
    private JMenuItem down;


    /** Creates a new instance of MenuScrollHelper */
    public MenuScrollHelper(JPopupMenu jMenu, List elements) {
        this.jMenu = jMenu;
        this.elements = elements;
        init();
    }

    static void plug(JPopupMenu jMenu, List elements){
        new MenuScrollHelper(jMenu, elements); 
    }

    /** Setup some extra decoration on JPopupmenu and make it work */
    public void init(){

        // TODO: get this infos from somewhere else
        Toolkit tk = Toolkit.getDefaultToolkit();

        if (elements != null) {
            int itemheight = ((JComponent) elements.get(0) ).getHeight();
            if (itemheight == 0)
                itemheight = DEFAULT_HEIGHT;
            this.max =  (int) tk.getScreenSize().getHeight()/itemheight;
        }

        // fill menu initial
        int i=0;
        Iterator elementiterator = this.elements.iterator();
        while (elementiterator.hasNext()){
            if (i >= offset && i < offset+max){
                JComponent jtmp = (JComponent) elementiterator.next();
                jMenu.add( jtmp );
            }else{
                elementiterator.next();
            }
            i++;
        }
        jMenu.pack();

        // add scroll triggers if appropriate
        if (elements.size() > max){
            /** construct scrolltrigger items*/
            JMenuItem up = new JMenuItem(MenuScrollHelper.ULABEL, null);
            JMenuItem down = new JMenuItem(MenuScrollHelper.DLABEL, null);

            up.addActionListener(this);
            up.addMouseListener(this);
            up.setEnabled(false);
            up.setName(UID);

            down.addActionListener(this);
            down.addMouseListener(this);
            down.setEnabled(false);
            down.setName(DID);

            this.jMenu.addMouseWheelListener(this);

            this.jMenu.add(up,0); // add on top of the menu items
            this.jMenu.add(down); // add as the last item
        }
    }

    public void actionPerformed(ActionEvent ae) {
    }

    public void mouseEntered(java.awt.event.MouseEvent me)  {
        // set scroll direction
        if (me.getComponent().getName().equals(MenuScrollHelper.UID)){
            direction = UP;
        } else {
            direction = DOWN;
        }

        // start scroll thread if appropriate
        try {
                this.t = new ScrollThread(this);
                t.start();

        } catch(Exception e){
            e.printStackTrace();
        }

    }

    public void mouseExited(java.awt.event.MouseEvent me)  {
        direction = NEUTRAL;
    }

    public void mousePressed(java.awt.event.MouseEvent me)  {
        // nothing to do
    }

    public void mouseReleased(java.awt.event.MouseEvent me)  {
        // nothing to do
    }

    public void mouseClicked(java.awt.event.MouseEvent me)  {
        // nothing to do
    }

    /** contains the payload: get the current offset and fills visible menueslots with right items from the
     * virtual menulist */
    public void scroll() {
        if (direction == DOWN && offset < elements.size() - max) { offset++; } else if (direction == UP && offset > 0) {
            offset--;
        } else {
            direction = NEUTRAL;
            return;
        }

        // remove current menuitems 
        for (int comcount = jMenu.getComponentCount() - 2; comcount >= 1 ; comcount--){
            jMenu.remove(comcount);
        }

        // fill in the new items to the menuslots
        int i=0;
        Iterator elementiterator = this.elements.iterator();
        JComponent jtmp;
        while (elementiterator.hasNext()){
            if (i >= offset && i < offset+max){
                jtmp = (JComponent) elementiterator.next();
                jMenu.add( jtmp , (i - offset) + 1);
            }else{
                elementiterator.next();
            }
            i++;
        }
        // display the itemcount somewhere ?
        //           up.setName(ULABEL + "("+ offset +")");
        //           down.setName(DLABEL + "("+ (i - offset) +")");
        jMenu.pack();
    }


    public void mouseWheelMoved(MouseWheelEvent e) {
        int notches = e.getWheelRotation();

        // only run if scrollthread is currently inactive
        try {
                if (notches < NEUTRAL) {
                    direction = UP;
                } else {
                    direction = DOWN;
                }
                scroll();
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }


    /** help thread which performes the scrolling */
    private class ScrollThread extends Thread {

        MenuScrollHelper msh;
        public ScrollThread(MenuScrollHelper msh){
            this.msh = msh;
        }

        public void run() {
            while (direction != NEUTRAL){
                try {
                    msh.scroll();
                    Thread.sleep(MenuScrollHelper.SCROLL_DELAY);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
