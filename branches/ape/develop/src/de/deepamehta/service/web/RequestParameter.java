package de.deepamehta.service.web;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;



/**
 * Wrapper class for HttpServletRequest that can also parse multi-part content.
 * <P>
 * <HR>
 * Last functional change: 12.6.2006 (2.0b7)<BR>
 * Last documentation update: 19.6.2004 (2.0b3)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class RequestParameter {

	private Hashtable params = new Hashtable();
	private Vector uploads = new Vector();
	private String pathInfo;

	/**
	 * @see		DeepaMehtaServlet#performRequest
	 */
	RequestParameter(HttpServletRequest request) throws ServletException {
		this.pathInfo = request.getPathInfo();
		//
		if (!DiskFileUpload.isMultipartContent(request)) {
			Enumeration e = request.getParameterNames();
			while (e.hasMoreElements()) {
				String name = (String) e.nextElement();
				String[] values = request.getParameterValues(name);
				params.put(name, values);
			}
		} else {
			try {
				Hashtable paramTable = new Hashtable();
				DiskFileUpload fu = new DiskFileUpload();
				fu.setHeaderEncoding("ISO-8859-1");		// ###
				List items = fu.parseRequest(request);
				Iterator i = items.iterator();
				while (i.hasNext()) {
					FileItem item = (FileItem) i.next();
					String name = item.getFieldName();
					if (item.isFormField()) {
						String value = item.getString("ISO-8859-1");	// ###
						addParameter(name, value, paramTable);
					} else {
						String value = getFilename(item.getName());	// ### explorer sends path
						if (!value.equals("")) {
							addParameter(name, value, paramTable);
							uploads.addElement(item);
						} /* ### else {
							System.out.println("> RequestParameter(): file item is <empty>");
						} */
					}
				}
				// convert vectors to arrays
				Enumeration e = paramTable.keys();
				while (e.hasMoreElements()) {
					String name = (String) e.nextElement();
					Vector values = (Vector) paramTable.get(name);
					String[] a = new String[values.size()];
					params.put(name, values.toArray(a));
				}
			} catch (UnsupportedEncodingException e) {
				throw new ServletException(e);
			} catch (FileUploadException e) {
				throw new ServletException(e);
			} 
		}
	}

	// ###
	String getFilename(String path) {
		int pos = path.lastIndexOf('\\');
		return pos != -1 ? path.substring(pos + 1) : path;
	}

	// ---

	/**
	 * @deprecated	As of DeepaMehta 2.0b7, replaced by {@link #getParameter}
	 */
	public String getValue(String name) {
		return getParameter(name);
	}

	public String getParameter(String name) {
		String[] values = getValues(name);
		return values != null ? values[0] : null;
	}

	public void setParameter(String name, String value) {
		getParameterValues(name)[0] = value;
	}

	/**
	 * @deprecated	As of DeepaMehta 2.0b7, replaced by {@link #getParameterValues}
	 */
	public String[] getValues(String name) {
		return getParameterValues(name);
	}

	public String[] getParameterValues(String name) {
		return (String[]) params.get(name);
	}

	// ---

	public Hashtable getTable() {
		return params;
	}

	public Vector getUploads() {
		return uploads;
	}

	public String getPathInfo() {
		return pathInfo;
	}



	// ***********************
	// *** Private Methods ***
	// ***********************



	private void addParameter(String name, String value, Hashtable paramTable) {
		Vector values = (Vector) paramTable.get(name);
		if (values == null) {
			values = new Vector();
			paramTable.put(name, values);
		}
		values.addElement(value);
	}
}
