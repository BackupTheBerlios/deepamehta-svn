package de.deepamehta;

//
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
 * Utility class for sending a file through a stream.
 * <P>
 * <HR>
 * Last functional change: 12.3.2003 (2.0a18-pre6)<BR>
 * Last documentation update: 17.4.2001 (2.0a10-pre5)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class FileServer implements DeepaMehtaConstants {



	// **************
	// *** Fields ***
	// **************



	BoundedRangeModel model;



	// *******************
	// *** Constructor ***
	// *******************



	/**
	 * @see		de.deepamehta.service.FileserverConnection#FileserverConnection
	 */
	public FileServer() {
	}

	/**
	 * @see		de.deepamehta.client.FileserverConnection#FileserverConnection
	 */
	public FileServer(BoundedRangeModel model) {
		this.model = model;
	}



	// ***************
	// *** Methods ***
	// ***************



	/**
	 * @see		de.deepamehta.service.FileserverConnection#performUpload
	 * @see		de.deepamehta.client.FileserverConnection#performDownloadRequest
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
	 * @see		de.deepamehta.service.FileserverConnection#performDownload
	 * @see		de.deepamehta.client.FileserverConnection#performUploadRequest
	 */
	public void writeFile(File file, DataOutputStream out) throws IOException {
		long size = file.length();
		System.out.println(">>> Fileserver.writeFile(): \"" + file + "\" (" + size +
			" bytes)");
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
	 * Copies the specified file into the clients document repository resp.
	 * icon repository.
	 *
	 * @see		de.deepamehta.client.FileserverConnection#performCopyRequest
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
	 * @see		#copyFile
	 * @see		de.deepamehta.client.DeepaMehtaClient#uploadFile
	 * @see		de.deepamehta.client.FileserverConnection#setLastModifiedLocally
	 * @see		de.deepamehta.client.FileserverConnection#performUploadRequest
	 * @see		de.deepamehta.service.FileserverConnection#performUpload
	 */
	public static String repositoryPath(int filetype) {
		switch (filetype) {
		case FILE_DOCUMENT:
			return FILESERVER_DOCUMENTS_PATH;
		case FILE_ICON:
			return FILESERVER_ICONS_PATH;
		case FILE_IMAGE:
			return FILESERVER_IMAGES_PATH;
		case FILE_BACKGROUND:
			return FILESERVER_BACKGROUNDS_PATH;
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
