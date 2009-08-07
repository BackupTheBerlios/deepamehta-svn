package de.deepamehta;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.BoundedRangeModel;



/**
 * Utility class for the DeepaMehta file service. There are methods for reading/writing a file from/to a stream.
 * <p>
 * 2 FileServer objects are created: one at client side and one at server side.
 * <p>
 * <hr>
 * Last change: 26.1.2009 (2.0ab9)<br>
 * J&ouml;rg Richter<br>
 * jri@deepamehta.de
 */
public class FileServer implements DeepaMehtaConstants {



	// **************
	// *** Fields ***
	// **************



	private static String baseDir;	// base directory of the DeepaMehta file repository. May be <code>null</code>.
	BoundedRangeModel model;		// the progress model that is updated while lengthy file operations



	// *******************
	// *** Constructor ***
	// *******************



	/**
	 * This constructor is used by the DeepaMehta server application.
	 * <p>
	 * References checked: 25.1.2009 (2.0b9)
	 *
	 * @see		de.deepamehta.service.FileserverConnection#FileserverConnection
	 */
	public FileServer() {
	}

	/**
	 * This constructor is used by the DeepaMehta client applet/application and by the monolithic application.
	 * <p>
	 * References checked: 25.1.2009 (2.0b9)
	 *
	 * @param	baseDir		base directory of the DeepaMehta file repository.
	 *						If <code>null</code> the file repository is created relative to the working directory.
	 *
	 * @see		de.deepamehta.client.PresentationService#PresentationService
	 */
	public FileServer(String baseDir, BoundedRangeModel model) {
		this.baseDir = baseDir;
		this.model = model;
	}



	// ***************
	// *** Methods ***
	// ***************



	/**
	 * Reads bytes from the stream and writes them to the file.
	 * <p>
	 * References checked: 25.1.2009 (2.0b9)
	 *
	 * @param	dstFile		the path of the file to write
	 *
	 * @see		de.deepamehta.service.FileserverConnection#performUpload
	 * @see		de.deepamehta.client.FileserverConnection#downloadFile
	 */
	public void readFile(File dstFile, DataInputStream in) throws IOException {
		long size = in.readLong();
		System.out.println(">>> Fileserver.readFile(): \"" + dstFile + "\" (" + size + " bytes)");
		// possibly create target directory
		createDirectory(dstFile);
		//
		OutputStream fileOut = new BufferedOutputStream(new FileOutputStream(dstFile), FILE_BUFFER_SIZE);
		//
		byte[] buffer = new byte[FILE_BUFFER_SIZE];
		long totalBytes = 0;
		int readBytes;
		long remainingBytes;
		//
		while (totalBytes < size) {
			// read from stream
			remainingBytes = size - totalBytes;
			readBytes = remainingBytes >= FILE_BUFFER_SIZE ? FILE_BUFFER_SIZE : (int) remainingBytes;
			readBytes = in.read(buffer, 0, readBytes);
			// write to file
			fileOut.write(buffer, 0, readBytes);
			totalBytes += readBytes;
			// update progress model
			if (model != null) {
				model.setValue(model.getValue() + 1);
			}
		}
		fileOut.close();
		//
		if (totalBytes == size) {
			System.out.println(">>> " + totalBytes + " bytes received");
		} else {
			System.out.println("*** FileServer.readFile(): " + totalBytes + " bytes received (corrupt)");
			throw new IOException("uploaded file \"" + dstFile + "\" is incomplete");
		}
	}

	/**
	 * Reads bytes from the file and writes them to the stream.
	 * <p>
	 * References checked: 25.1.2009 (2.0b9)
	 *
	 * @see		de.deepamehta.service.FileserverConnection#performDownload
	 * @see		de.deepamehta.client.FileserverConnection#uploadFile
	 */
	public void writeFile(File file, DataOutputStream out) throws IOException {
		long size = file.length();
		System.out.println(">>> Fileserver.writeFile(): \"" + file + "\" (" + size + " bytes)");
		InputStream fileIn = new BufferedInputStream(new FileInputStream(file),
			FILE_BUFFER_SIZE);
		byte[] buffer = new byte[FILE_BUFFER_SIZE];
		long totalBytes = 0;
		int readBytes;
		//
		out.writeLong(size);
		// read from file
		while ((readBytes = fileIn.read(buffer)) != -1) {
			// write to stream
			out.write(buffer, 0, readBytes);
			totalBytes += readBytes;
			// update progress model
			if (model != null) {
				model.setValue(model.getValue() + 1);
			}
		}
		//
		if (totalBytes == size) {
			System.out.println(">>> " + totalBytes + " bytes transmitted");
		} else {
			System.out.println("*** FileServer.writeFile(): " + totalBytes + " bytes transmitted (corrupt)");
			throw new IOException("file \"" + file + "\" has been transmitted incompletely");
		}
	}

	/**
	 * Copies the specified file into the client-side file repository.
	 * <p>
	 * References checked: 25.1.2009 (2.0b9)
	 *
	 * @see		de.deepamehta.client.PresentationService#performCopyRequest
	 */
	public void copyFile(File srcFile, int filetype) throws IOException {
		File dstFile = new File(repositoryPath(filetype) + srcFile.getName());
		long size = srcFile.length();
		//
		System.out.println(">>> FileServer.copyFile():\n    src=\"" + srcFile + "\" (" +
			size + " bytes)\n    dst=\"" + dstFile + "\"");
		// possibly create local document repository
		createDirectory(dstFile);
		//
		if (srcFile.equals(dstFile.getAbsoluteFile())) {
			System.out.println(">>> FileServer.copyFile(): " + dstFile + " is already in local " +
				"repository (type " + filetype + ") -- no copying required");
			return;
		}
		//
		InputStream fileIn = new BufferedInputStream(new FileInputStream(srcFile), FILE_BUFFER_SIZE);
		OutputStream fileOut = new BufferedOutputStream(new FileOutputStream(dstFile), FILE_BUFFER_SIZE);
		byte[] buffer = new byte[FILE_BUFFER_SIZE];
		long totalBytes = 0;
		int readBytes;
		//
		while ((readBytes = fileIn.read(buffer)) != -1) {
			fileOut.write(buffer, 0, readBytes);
			totalBytes += readBytes;
			// update progress model
			if (model != null) {
				model.setValue(model.getValue() + 1);
			}
		}
		fileIn.close();
		fileOut.close();
		//
		if (size == totalBytes) {
			System.out.println(">>> " + totalBytes + " bytes copied");
		} else {
			System.out.println("*** FileServer.copyFile(): " + totalBytes + " bytes " +
				"copied (corrupt)");
			throw new IOException("copy of \"" + srcFile + "\" is corrupt");
		}
	}

	// ---

	/**
	 * References checked: 20.1.2009 (2.0b9)
	 *
	 * @see		#copyFile
	 * @see		de.deepamehta.client.FileserverConnection#downloadFile
	 * @see		de.deepamehta.client.FileserverConnection#uploadFile
	 * @see		de.deepamehta.client.PresentationService#setLastModifiedLocally
	 * @see		de.deepamehta.client.PresentationService#downloadFile
	 * @see		de.deepamehta.client.PresentationService#performUploadRequest
	 * @see		de.deepamehta.client.PresentationService#QueuedRequest
	 * @see		de.deepamehta.service.FileserverConnection#performUpload
	 * @see		de.deepamehta.service.FileserverConnection#performDownload
	 * @see		de.deepamehta.topics.LiveTopic#upload
	 */
	public static String repositoryPath(int filetype) {
		String bd = baseDir != null ? baseDir : "";
		//
		switch (filetype) {
		case FILE_DOCUMENT:
			return bd + FILESERVER_DOCUMENTS_PATH;
		case FILE_ICON:
			return bd + FILESERVER_ICONS_PATH;
		case FILE_IMAGE:
			return bd + FILESERVER_IMAGES_PATH;
		case FILE_BACKGROUND:
			return bd + FILESERVER_BACKGROUNDS_PATH;
		default:
			throw new DeepaMehtaException("unexpected filetype: " + filetype);
		}
	}

	/**
	 * @see		de.deepamehta.topics.helper.TopicMapImporter#importFile
	 */
	public static int getFiletype(String filename) {
		// ### / vs \ workaround
		if (filename.startsWith(FILESERVER_DOCUMENTS_PATH.substring(0, FILESERVER_DOCUMENTS_PATH.length() - 1))) {
			return FILE_DOCUMENT;
		} else if (filename.startsWith(FILESERVER_ICONS_PATH.substring(0, FILESERVER_ICONS_PATH.length() - 1))) {
			return FILE_ICON;
		} else if (filename.startsWith(FILESERVER_IMAGES_PATH.substring(0, FILESERVER_IMAGES_PATH.length() - 1))) {
			return FILE_IMAGE;
		} else if (filename.startsWith(FILESERVER_BACKGROUNDS_PATH.substring(0, FILESERVER_BACKGROUNDS_PATH.length() - 1))) {
			return FILE_BACKGROUND;
		} else {
			throw new DeepaMehtaException("unexpected path: " + filename);
		}
	}

	// ---

	/**
	 * @see		#readFile
	 * @see		#copyFile
	 * @see		de.deepamehta.topics.TopicMapTopic#createTopicmapArchive
	 * @see		de.deepamehta.topics.CMImportExportTopic#exportCM
	 */
	public static void createDirectory(File file) {
		File dstDir = file.getParentFile();
		if (dstDir.mkdirs()) {
			System.out.println(">>> document repository has been created: " + dstDir);
		}
	}
}
