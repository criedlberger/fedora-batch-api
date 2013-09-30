package ca.ualberta.library.fedora.utils;

//	import java.io.File;
	import java.io.IOException;
	import java.io.InputStream;
//	import java.io.FileInputStream;
//	import java.io.FileOutputStream;
	import java.io.OutputStream;
//	import java.io.ByteArrayInputStream;
//	import java.io.ByteArrayOutputStream;
//	import java.net.MalformedURLException;
//	import java.rmi.RemoteException;
//	import java.util.StringTokenizer;
//	import java.util.HashMap;

	import org.apache.commons.logging.Log;
	import org.apache.commons.logging.LogFactory;


	/**
	 * 
	 *
	 * @author Chris Riedlberger
	 */
	public class FileHandler {
		private static final Log log = LogFactory.getLog(FileHandler.class);

		/**
		 * Copies the contents of an InputStream to an OutputStream, then closes
		 * both.
		 *
		 * @param in The source stream.
		 * @param out The target stram.
		 * @param bufSize Number of bytes to attempt to copy at a time.
		 * @throws IOException If any sort of read/write error occurs on either
		 *         stream.
		 */
		public static void pipeStream(InputStream in, OutputStream out, int bufSize)
				throws IOException {
			try {
				byte[] buf = new byte[bufSize];
				int len;
				while ( ( len = in.read( buf ) ) > 0 ) {
					out.write( buf, 0, len );
				}
			} finally {
				try {
					in.close();
					out.close();
				} catch (IOException e) {
					log.error("WARNING: Could not close stream.", e);
					System.err.println("WARNING: Could not close stream.");
				}
			}
		}

}
