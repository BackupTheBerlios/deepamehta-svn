/*
 * Created on 18.12.2005
 *
 * This file is part of the DeepaMehta framework.
 */
package de.deepamehta.launchpad;

import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.deepamehta.DeepaMehtaMessages;
import de.deepamehta.environment.Environment;
import de.deepamehta.environment.EnvironmentException;
import de.deepamehta.environment.EnvironmentFactory;
import de.deepamehta.environment.instance.InstanceConfiguration;
import de.deepamehta.environment.instance.UnknownInstanceException;
import de.deepamehta.launchpad.setup.ActionList;
import de.deepamehta.launchpad.setup.wizard.SetupWizard;

// FIXME add CLI to launch pad
// FIXME create method to update instance

/**
 * This is the main class of the Launch Pad.
 * @author vwegert
 */
public class LaunchPad {

	private static Log logger = LogFactory.getLog(LaunchPad.class);
	private Environment env;
	private MainWindow mw;
	
	/**
	 * Static method to start the application.  
	 * @param args The command line arguments to parse.
	 */
	public static void main(String[] args) {
	    LaunchPad lp = new LaunchPad();
	    lp.start(args);
	}
	
	/**
	 * Starts the launch pad.
	 * @param args The command line arguments to parse.
	 * @return 0 if the launch pad was started successfully.
	 */
	public Integer start(String[] args) {
        try {
			this.env = EnvironmentFactory.getLaunchPadEnvironment(args);
		} catch (EnvironmentException e) {
			logger.error("An error occurred during initialization of the environment.", e);
			return new Integer(2);
		}
        try {
        	this.runInteractive();
        } catch (HeadlessException he) {
        	logger.error("No headless interface yet, sorry.", he);
            return new Integer(1);        	
        }
        return null;

    }
	
	/**
	 * Stops the runnung launch pad.
	 * @param exitCode The exit code to return.
	 * @return The exit code.
	 */
	public int stop(int exitCode) {
	    logger.debug("Launch Pad shutting down.");
	    if (this.mw.isVisible()) {
	        this.mw.hide();
	        this.mw.dispose();
	    }
        return exitCode;
    }
	
	/**
	 * Starts the interactive (GUI) part of the launch pad.
	 * @throws HeadlessException
	 */
	private void runInteractive() throws HeadlessException {
		this.mw = new MainWindow(this);
		if (this.env.getInstanceTableModel().getRowCount() == 0) {
		    if (JOptionPane.showConfirmDialog(this.mw, 
		            DeepaMehtaMessages.getString("LaunchPad.CreateFirstInstanceQuestion"), //$NON-NLS-1$
		            DeepaMehtaMessages.getString("LaunchPad.CreateFirstInstanceTitle"),  //$NON-NLS-1$ 
		            JOptionPane.YES_NO_OPTION, 
		            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
		        createInstance();
		}
	}

	/**
	 * Launch the instance specified.
	 * @param id The instance ID.
	 */
	public void launchInstance(String id) {
		InstanceConfiguration config;
		logger.info("Starting instance " + id);
		try {
			config = env.getInstance(id);
        } catch (UnknownInstanceException e) {
            logger.error("Trying to start unknown instance - WTF?", e);
            return;
        }

        /* The command line looks like this:
         *   java -Djava.endorsed.dirs=<path> -jar <jarfile> <args> <id>
         * 
         * <jarfile> is determined according to the instance type.
         * <args> contains --logger-config <config_file> (if specified)
         * 
         * The instance is started with the working directory data/<id>/.
         */

        // gather arguments
        ArrayList cmd = new ArrayList();
        cmd.add(this.env.getJavaRuntime());
        cmd.add("-Djava.endorsed.dirs=" + this.env.getEndorsedPath());
        cmd.add("-Dde.deepamehta.home=" + this.env.getHomeDirectory());
        cmd.add("-jar");
        cmd.add(config.getExecutableArchive());
        if (config.getLogConfig() != null) {
        	cmd.add("-l");
        	cmd.add(config.getLogConfig());
        }        
        cmd.add(config.getId());
        
        // assemble command line
        String cmdline = "";
        for (Iterator iter = cmd.iterator(); iter.hasNext();) {
        	String element = (String) iter.next();
        	cmdline = cmdline + " " + element;
        }
        logger.debug("Trying to start new process using " + cmdline);
        
        // spawn process
        Process process;
        try {
        	process = Runtime.getRuntime().exec(cmdline, null, new File(config.getWorkingDirectory())); 
        	new ProcessWatcher(config, process).start();
        } catch (IOException e) {
        	logger.error("I/O error while trying to spawn task", e);
        }

	}
	
	/**
	 * Start the setup wizard to create a new instance.
	 */
	public void createInstance() {
	    SetupWizard wiz;
	    try {
            wiz = new SetupWizard(this.mw);
    	    wiz.run();
        } catch (HeadlessException e) {
            logger.error("Unable to open setup wizard.", e);
        }
	
	}
	
	/**
	 * Shows the property window to edit the instance specified.
	 * @param id The instance ID.
	 */
	public void editInstance(String id) {
		try {
			InstanceConfiguration config = this.env.getInstance(id);
			InstancePropertiesDialog props = new InstancePropertiesDialog(this.mw);
			props.show(config);
		} catch (UnknownInstanceException e) {
          logger.error("Trying to open properties dialog for non-existing instance - WTF?", e);
		}
	}
	
	/**
	 * MISSDOC No documentation for method installApplication of type LaunchPad
	 * @param id
	 */
	public void installApplication(String id) {

		InstanceConfiguration config;
		try {
			config = env.getInstance(id);
        } catch (UnknownInstanceException e) {
            logger.error("Trying to install application into unknown instance - WTF?", e);
            return;
        }
		
		ApplicationSelectionDialog sel = new ApplicationSelectionDialog(mw, DeepaMehtaMessages.getString("LaunchPad.ApplicationSelectionTitle"), true); //$NON-NLS-1$
		sel.show();
		String app = sel.getSelectedApplication();
		if (app != null) {
			logger.debug("Installing application " + app + " into instance " + id + "...");
			ActionList actions = new ActionList();
			actions.prepareApplicationInstallation(config, env.getApplication(app));
			if (actions.execute()) {
				// show status message
				JOptionPane.showMessageDialog(mw, DeepaMehtaMessages.getString("LaunchPad.ApplicationInstallationSuccessMessage", app, id), //$NON-NLS-1$
						DeepaMehtaMessages.getString("LaunchPad.Title"), JOptionPane.INFORMATION_MESSAGE); //$NON-NLS-1$
			} else {
				// show error message
				JOptionPane.showMessageDialog(mw, actions.getMessages(), 
						DeepaMehtaMessages.getString("LaunchPad.Title"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
			}
		}
		
	}
	
	/**
	 * Removes an instance.
	 * @param id The instance ID.
	 */
	public void deleteInstance(String id) {
		// TODO No code to delete an instance yet.
		
	}

}
