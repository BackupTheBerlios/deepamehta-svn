/**
 * 
 */
package de.deepamehta.installer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import com.izforge.izpack.event.SimpleInstallerListener;
import com.izforge.izpack.installer.AutomatedInstallData;
import com.izforge.izpack.util.AbstractUIProgressHandler;
import com.izforge.izpack.util.FileExecutor;
import com.izforge.izpack.util.OsVersion;

/**
 * MISSDOC No documentation for type DeepaMehtaInstallerListener
 * @author vwegert
 *
 */
public class DeepaMehtaInstallerListener extends SimpleInstallerListener {

	/* (non-Javadoc)
	 * @see com.izforge.izpack.event.InstallerListener#afterPacks(com.izforge.izpack.installer.AutomatedInstallData, com.izforge.izpack.util.AbstractUIProgressHandler)
	 */
	public void afterPacks(AutomatedInstallData idata, AbstractUIProgressHandler handler) throws Exception {
		// determine the target platform
		if (OsVersion.IS_LINUX) {
			createLinuxScript(idata, handler);	
		} else if (OsVersion.IS_WINDOWS) {
			createWindowsScript(idata, handler);
		} else {
			handler.emitError("DeepaMehta Installer", 
					"Sorry, we don't know how to create a startup script for platform " + 
					OsVersion.getOsDetails() + " yet. If you can tell us, please do!");
		}
	}

	/**
	 * MISSDOC No documentation for method createLinucScript of type DeepaMehtaInstallerListener
	 * @param idata
	 * @param handler 
	 * @throws FileNotFoundException
	 */
	private void createLinuxScript(AutomatedInstallData idata, AbstractUIProgressHandler handler) throws FileNotFoundException {
		// try to locate the JRE
		File jre = new File(idata.getVariable("JAVA_HOME") + "/bin/java");
		if (!jre.exists()) {
			handler.emitError("DeepaMehta Installer", "Unable to locate JRE.");
		} else {
			// write the startup script
			String scriptName = idata.getInstallPath() + System.getProperty("file.separator") + "DeepaMehta.sh";
			File scriptFile = new File(scriptName);
			PrintWriter scriptWriter = new PrintWriter(new FileOutputStream(scriptFile));
			scriptWriter.println("#!/bin/sh");
			scriptWriter.println(jre.getAbsolutePath() + 
					" -Djava.endorsed.dirs=" + idata.getInstallPath() + "/libs/endorsed" +
					" -Dde.deepamehta.home=" + idata.getInstallPath() +
					" -jar " + idata.getInstallPath() + "/bin/DeepaMehtaLaunchPad.jar" + 
			" ${1} ${2} ${3} ${4} ${5}");
			scriptWriter.close();
			// make the script executable
		    String[] params = {"chmod", "a+x", scriptFile.getAbsolutePath()};
		    String[] output = new String[2];
		    FileExecutor fe = new FileExecutor();
		    fe.executeCommand(params, output);
		}
	}

	/**
	 * MISSDOC No documentation for method createWindowsScript of type DeepaMehtaInstallerListener
	 * @param idata
	 * @param handler
	 * @throws FileNotFoundException
	 */
	private void createWindowsScript(AutomatedInstallData idata, AbstractUIProgressHandler handler) throws FileNotFoundException {
		// try to locate the JRE
		File jre = new File(idata.getVariable("JAVA_HOME") + "\\bin\\java.exe");
		if (!jre.exists()) {
			handler.emitError("DeepaMehta Installer", "Unable to locate JRE.");
		} else {
			// write the startup script
			String scriptName = idata.getInstallPath() + System.getProperty("file.separator") + "DeepaMehta.cmd";
			File scriptFile = new File(scriptName);
			PrintWriter scriptWriter = new PrintWriter(new FileOutputStream(scriptFile));
			scriptWriter.println("@ECHO OFF");
			scriptWriter.println(jre.getAbsolutePath() + 
					" -Djava.endorsed.dirs=" + idata.getInstallPath() + "\\libs\\endorsed" +
					" -Dde.deepamehta.home=" + idata.getInstallPath() +
					" -jar " + idata.getInstallPath() + "\\bin\\DeepaMehtaLaunchPad.jar");
			scriptWriter.close();
		}
	}

}
