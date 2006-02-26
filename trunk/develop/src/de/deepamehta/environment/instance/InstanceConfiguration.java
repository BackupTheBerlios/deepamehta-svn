/*
 * Created on 19.12.2005
 *
 * This file is part of the DeepaMehta framework.
 */
package de.deepamehta.environment.instance;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.deepamehta.environment.Environment;
import de.deepamehta.environment.EnvironmentException;

/**
 * This class represents a single entry of the configuration file - that is 
 * a single instance known to the environment. It corresponds to the 
 * &lt;instance&gt; element of the configuration file
 * @author vwegert
 *
 */
public class InstanceConfiguration {
	
	private static Log logger = LogFactory.getLog(InstanceConfiguration.class);
	
	private String id, description;
	private InstanceType instanceType = null;

	private String clientHost;
	private int clientPort;
	
	private String serverInterface;
	private int serverPort;
	
	private CorporateMemoryConfiguration cmConfig = null;

	/**
	 * @return Returns the ID.
	 */
	
	public String getId() {
		return this.id;
	}
	
	/**
	 * @return Returns the description.
	 */	
	public String getDescription() {
		return this.description;
	}

	/**
	 * @return Returns the instance type.
	 */
	public InstanceType getInstanceType() {
		return this.instanceType;
	}
	
	/**
	 * @return Returns the client host.
	 */
	public String getClientHost() {
		return this.clientHost;
	}
	
	/**
	 * @return Returns the client port.
	 */
	public int getClientPort() {
		return this.clientPort;
	}
	
	/**
	 * @return Returns the server interface.
	 */
	public String getServerInterface() {
		return this.serverInterface;
	}
	
	/**
	 * @return Returns the server port.
	 */
	public int getServerPort() {
		return this.serverPort;
	}
	
    /**
     * @return Returns the Corporate Memory Configuration.
     * @throws EnvironmentException
     */
    public CorporateMemoryConfiguration getCMConfig() throws EnvironmentException {
        // Note: This method checks whether a CM config is specified to prevent
        // client instances from trying to access a non-existing CM.
        if (this.cmConfig != null) {
            return this.cmConfig;
        } else {
            throw new EnvironmentException("CM Configuration not initialized");
        }
    }
    
    /**
	 * @param id The id to set.
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * @param type The instance type to set.
	 */
	public void setInstanceType(InstanceType type) {
		this.instanceType = type;
	}
	
	/**
	 * Convenience method to set the instance type to monolithic.
	 */
	public void setInstanceTypeMonolithic() {
	    this.instanceType = InstanceType.MONOLITHIC;
	}
	
	/**
	 * Convenience method to set the instance type to server.
	 */
	public void setInstanceTypeServer() {
	    this.instanceType = InstanceType.SERVER;
	}
	
	/**
	 * Convenience method to set the instance type to client.
	 */
	public void setInstanceTypeClient() {
	    this.instanceType = InstanceType.CLIENT;
	}
	
	/**
	 * @param clientHost The client host to set.
	 */
	public void setClientHost(String clientHost) {
		this.clientHost = clientHost;
	}
	
	/**
	 * @param clientPort The client port to set.
	 */
	public void setClientPort(int clientPort) {
		this.clientPort = clientPort;
	}
	
	/**
	 * @param serverInterface The server interface to set.
	 */
	public void setServerInterface(String serverInterface) {
		this.serverInterface = serverInterface;
	}
	
	/**
	 * @param serverPort The server port to set.
	 */
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	/**
     * @param cmConfig The corporate memory configuration to set.
     */
    public void setCMConfig(CorporateMemoryConfiguration cmConfig) {
        this.cmConfig = cmConfig;
    }

	/**
	 * Determines the executable JAR archive that has to be run to start
	 * the instance.
	 * @return The full path to the executable JAR.
	 */
	public String getExecutableArchive() {
	    String base = Environment.getEnvironment().getHomeDirectory();
	    String sep  = Environment.getEnvironment().getFileSeparator();
	    base = base + sep;		    
	    if (this.instanceType.isMonolithic())
		    return base + "bin" + sep + "DeepaMehta.jar"; 
		if (this.instanceType.isServer())     
		    return base + "bin" + sep + "DeepaMehtaService.jar"; 
		if (this.instanceType.isClient())     
		    return base + "bin" + sep + "DeepaMehtaClient.jar"; 
	    return null; // should never be reached
	}

    /**
     * @return Returns a string used to display the connection data (either 
     * server interface and port or client host and port).
     */
    public String getConnectionDisplayText() {
        if (this.instanceType.isServer()) {
            return getServerInterface() + ":" + getServerPort();
        } else if (this.instanceType.isClient()) {
            return getClientHost() + ":" + getClientPort();
        } else {
			return null;
		}
    }
    
    /**
     * @return Returns a string used to display the database connection 
     * information. 
     */
    public String getDatabaseDisplayText() {
        return "TODO"; // TODO no database display text yet
        // TODO the name should be changed - might not be a database after all 
    }
 
    /**
     * Converts the contents of the configuration into DOM objects. This method
     * is used by the instance manager during serialization to write
     * changes to the configuration file.  
     * @param doc The document that will contain the nodes.
     * @return an Element representing the &lt;inctance&gt; element of the
     * configuration file.
     */
    public Node toNode(Document doc) {
        
        Node me = doc.createElement("instance");
        ((Element) me).setAttribute("id", getId());
        ((Element) me).setAttribute("description", getDescription());
        
        if (getInstanceType().isMonolithic()) {
            Node monolithic = doc.createElement("monolithic");
            try {
                monolithic.appendChild(getCMConfig().toNode(doc));
            } catch (Exception e) {
                logger.error("Unable to serialize CM config of monolithic instance " + getId(), e);
            }
            me.appendChild(monolithic);
        }
        
        if (getInstanceType().isServer()) {
            Node server = doc.createElement("server");
            ((Element) server).setAttribute("interface", getServerInterface());
            ((Element) server).setAttribute("port", Integer.toString(getServerPort()));
            try {
                server.appendChild(getCMConfig().toNode(doc));
            } catch (Exception e) {
                logger.error("Unable to serialize CM config of server instance " + getId(), e);
            }
            me.appendChild(server);
        }
        
        if (getInstanceType().isClient()) {
            Node client = doc.createElement("client");
            ((Element) client).setAttribute("host", getClientHost());
            ((Element) client).setAttribute("port", Integer.toString(getClientPort()));
            me.appendChild(client);
        }
        
        return me;
    }

    /**
     * @return Returns the working directory of the instance. 
     */
    public String getWorkingDirectory() {
    	if (getInstanceType().isClient())
    	{
    		// FIXME will run into trouble with icons some day
    		return Environment.getEnvironment().getWorkingDirectory();
    	} else {
    		String base = Environment.getEnvironment().getHomeDirectory();
    		String sep = Environment.getEnvironment().getFileSeparator();
    		return base + sep + "data" + sep + getId() + sep;
    	}
    }
    

}
