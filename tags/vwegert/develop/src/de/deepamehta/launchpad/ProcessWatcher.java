/*
 * Created on 27.12.2005
 *
 * This file is part of the DeepaMehta framework.
 */
package de.deepamehta.launchpad;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.deepamehta.environment.instance.InstanceConfiguration;

/**
 * Helper class to track the output of processes spawned by the Launch Pad.
 * @author vwegert
 */
public class ProcessWatcher extends Thread {

	private static Log logger = LogFactory.getLog(ProcessWatcher.class);
	private static int maxpid = 0;
	
	private int pid;
	private Process process;
	private InstanceConfiguration config;
	private BufferedReader stdout, stderr;
	private ProcessOutputWindow outputWindow = null; 
	
	/**
	 * default constructor
	 * @param c 
	 * @param p The process to watch.
	 */
	public ProcessWatcher(InstanceConfiguration c, Process p) {
		logger.debug("ProcessWatcher created");
		this.process = p;
		this.config = c;
        this.stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
        this.stderr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        this.pid = getNewPID();
        if (this.config.getLogWindow()) {
        	outputWindow = new ProcessOutputWindow(this);
        }
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		String text;
		if (this.outputWindow != null)
			outputWindow.show();
		while (isRunning()) {
			try {
				while(this.stdout.ready()) {
					text = this.stdout.readLine();
					if (this.outputWindow != null)
						outputWindow.addOutputLine(text);
				}
				while(this.stderr.ready()) {
					text = this.stderr.readLine();
					if (this.outputWindow != null)
						outputWindow.addErrorLine(text);
				}
			} catch (IOException e) {
				logger.error("I/O error while reading process output.", e);
			}
			try {
				sleep(500);
			} catch (InterruptedException e1) {
				// nothing to do here
			}
		}
		logger.info("Process " + this.pid + " exited with return code " + this.process.exitValue());
		if (this.outputWindow != null) {
			outputWindow.hide();
			outputWindow.dispose();
			outputWindow = null;
		}
	}

	/**
	 * @return Returns a new process ID.
	 */
	private synchronized int getNewPID() {
        maxpid++;	
        return maxpid;
	}
	
	/**
	 * @return <code>true</code> if the process is still running.
	 */
	private boolean isRunning() {
		try {
			int dummy = this.process.exitValue();
		} catch (IllegalThreadStateException e) {
			return true;
		}
		
		return false;
	}

	/**
	 * @return Returns the config.
	 */
	public InstanceConfiguration getConfig() {
		return config;
	}

	/**
	 * @return Returns the process.
	 */
	public Process getProcess() {
		return process;
	}

	/**
	 * @return Returns the pid.
	 */
	public int getPid() {
		return pid;
	}
}
