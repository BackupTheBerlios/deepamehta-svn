/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.deepamehta.client;

import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaConstants;
import org.lobobrowser.ua.NavigationEvent;
import org.lobobrowser.ua.NavigationListener;
import org.lobobrowser.ua.NavigationVetoException;

/**
 *
 * @author Malte Reißig / mre@deepamehta.de
 */
public class BrowserNavigationListener implements DeepaMehtaConstants, NavigationListener {

    private PropertyPanelControler controler;
    private PropertyPanel panel;

    public BrowserNavigationListener (PropertyPanelControler controler, PropertyPanel panel) {
        this.controler = controler;
        this.panel = panel;
    }

    public void beforeNavigate(NavigationEvent arg0) throws NavigationVetoException {
        if (arg0.isFromClick()) {
            String command = CMD_FOLLOW_HYPERLINK + COMMAND_SEPARATOR + arg0.getURL();
            System.out.println("Deepa Browser was clicked:" + arg0.getMethod().toString() +", " + arg0.getParamInfo() + ", " + arg0.isFromClick());
            controler.executeTopicCommand(panel.getTopicmap().getEditor().getTopicMap(), panel.getTopic(), command);
        }
        // throw new UnsupportedOperationException("beforeNavigate:" + arg0.getMethod().toString());
    }

    public void beforeLocalNavigate(NavigationEvent arg0) throws NavigationVetoException {
        // System.out.println("beforeLocalNavigate:" + arg0.getMethod().toString());
        // throw new UnsupportedOperationException("beforeLocalNavigate." + arg0.getMethod().toString());
    }

    public void beforeWindowOpen(NavigationEvent arg0) throws NavigationVetoException {
        String filePath = arg0.getURL().getPath();
        System.out.println("    maybe a download is going to start: it's a " + filePath.substring(filePath.length()-4));
        // throw new UnsupportedOperationException("beforeWindowOpen." + arg0.getMethod().toString());
    }

}
