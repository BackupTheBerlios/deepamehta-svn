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

/**
 * Helper class to track the output of processes spawned by the Launch Pad.
 * @author vwegert
 */
public class ProcessWatcher extends Thread {

	private static Log logger = LogFactory.getLog(ProcessWatcher.class);
	private static int maxpid = 0;
	
	private int pid;
	private Process p;
	private BufferedReader stdout, stderr;
	
	/**
	 * default constructor
	 * @param p The process to watch.
	 */
	public ProcessWatcher(Process p) {
		logger.debug("ProcessWatcher created");
		this.p = p;
        this.stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
        this.stderr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        this.pid = getNewPID();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		String text;
		while (isRunning()) {
			try {
				while(this.stdout.ready()) {
					text = this.stdout.readLine();
					logger.info("Process " + this.pid + " STDOUT: " + text);
				}
				while(this.stderr.ready()) {
					text = this.stderr.readLine();
					logger.error("Process " + this.pid + " STDERR: " + text);
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
		logger.info("Process " + this.pid + " exited with return code " + this.p.exitValue());
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
			int dummy = this.p.exitValue();
		} catch (IllegalThreadStateException e) {
			return true;
		}
		
		return false;
	}
}
