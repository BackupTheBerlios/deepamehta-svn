/*
 * Created on 10.01.2006
 *
 * This file is part of the DeepaMehta framework.
 */
package de.deepamehta.environment.instance;

import javax.swing.ImageIcon;

import de.deepamehta.DeepaMehtaMessages;

/**
 * This class represents a type-safe enumeration that is used to describe
 * the type of an instance configuration - either client, server or monolithic.
 * @author vwegert
 */
public final class InstanceType {

    public static final InstanceType MONOLITHIC = new InstanceType(1);
    public static final InstanceType SERVER     = new InstanceType(2);
    public static final InstanceType CLIENT     = new InstanceType(3);
    
    private static ImageIcon clientIcon     = new ImageIcon(ClassLoader.getSystemResource("de/deepamehta/environment/instance/client.png"));
    private static ImageIcon serverIcon     = new ImageIcon(ClassLoader.getSystemResource("de/deepamehta/environment/instance/server.png"));
    private static ImageIcon monolithicIcon = new ImageIcon(ClassLoader.getSystemResource("de/deepamehta/environment/instance/monolithic.png"));
    
    private int type;
    
    private InstanceType(int type) {
        this.type = type;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
    	switch(this.type) {
    	case 1: return DeepaMehtaMessages.getString("Instance.TypeMonolithic"); //$NON-NLS-1$
    	case 2: return DeepaMehtaMessages.getString("Instance.TypeServer"); //$NON-NLS-1$
    	case 3: return DeepaMehtaMessages.getString("Instance.TypeClient"); //$NON-NLS-1$
    	default: return ""; // should never be reached
    	}
    }
    
    public ImageIcon getIcon() {
    	switch(this.type) {
    	case 1: return monolithicIcon;
    	case 2: return serverIcon;
    	case 3: return clientIcon;
    	default: return null; // should never be reached.
    	}
    }
    
    /**
     * MISSDOC No documentation for method isMonolithic of type InstanceType
     * @return
     */
    public boolean isMonolithic() {
        return this.equals(MONOLITHIC);
    }
    
    /**
     * MISSDOC No documentation for method isServer of type InstanceType
     * @return
     */
    public boolean isServer() {
        return this.equals(SERVER);
    }
    
    /**
     * MISSDOC No documentation for method isClient of type InstanceType
     * @return
     */
    public boolean isClient() {
        return this.equals(CLIENT);
    }
    
}
