/*
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Educational Community License (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.opensource.org/licenses/ecl1.txt">
 * http://www.opensource.org/licenses/ecl1.txt.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2007 by
 * The Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 */

package ca.ualberta.library.fedora.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.fcrepo.client.FedoraClient;
import org.fcrepo.server.utilities.StreamUtility;

/**
 * A client to a Fedora server's upload facility, accessed via a basic-authenticated multipart POST to the server.
 *
 * See server.management.UploadServlet for protocol details.
 *
 * @author cwilper@cs.cornell.edu
 */
public class Uploader {

	/** Logger for this class. */
	private static final Log log = LogFactory.getLog(Uploader.class);

	private final FedoraClient fc;

	/**
	 * Construct an uploader to a certain repository as a certain user.
	 */
	public Uploader(String protocol, String host, int port, String user, String pass) throws IOException {
		new UsernamePasswordCredentials(user, pass);
		String baseURL = protocol + "://" + host + ":" + port + "/fedora";
		fc = new FedoraClient(baseURL, user, pass);
	}

	/**
	 * Send the data from the stream to the server.
	 *
	 * This is less efficient than <i>upload(File)</i>, but if you already have a stream, it's convenient.
	 *
	 * This method takes care of temporarily making a File out of the stream, making the request, and removing the
	 * temporary file. Having a File source for the upload is necessary because the content-length must be sent along
	 * with the request as per the HTTP Multipart POST protocol spec.
	 */
	public String upload(InputStream in) throws IOException {
		File tempFile = File.createTempFile("fedora-upload-", null);
		FileOutputStream out = new FileOutputStream(tempFile);
		try {
			StreamUtility.pipeStream(in, out, 8192);
			return upload(tempFile);
		} finally {
			in.close();
			out.close();
			if (!tempFile.delete()) {
				System.err.println("WARNING: Could not remove temporary file: " + tempFile.getName());
				tempFile.deleteOnExit();
			}
		}
	}

	/**
	 * Send a file to the server, getting back the identifier.
	 */
	public String upload(File file) throws IOException {
		// paint initial status to the progress bar
		String msg = "Uploading " + file.length() + " bytes to " + fc.getUploadURL();
		log.debug(msg);
		try {
			return fc.uploadFile(file);
		} catch (IOException e) {
			return "";
		}
	}
}