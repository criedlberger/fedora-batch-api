/**
 * University of Alberta Libraries
 * Information Technology and Services
 * Project: fedora
 * $Id: FedoraAPIException.java 3587 2009-01-21 17:31:58Z pcharoen $
 */
package ca.ualberta.library.fedora.exception;

/**
 * The FedoraAPIException class.
 *
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 3587 $ $Date: 2009-01-21 10:31:58 -0700 (Wed, 21 Jan 2009) $
 */
public class FedoraAPIException extends Exception {

	private static final long serialVersionUID = -3900747178212576353L;

	/**
	 * The FedoraAPIException class constructor.
	 */
	public FedoraAPIException() {
	}

	/**
	 * The FedoraAPIException class constructor.
	 * @param message
	 */
	public FedoraAPIException(String message) {
		super(message);
	}

	/**
	 * The FedoraAPIException class constructor.
	 * @param cause
	 */
	public FedoraAPIException(Throwable cause) {
		super(cause);
	}

	/**
	 * The FedoraAPIException class constructor.
	 * @param message
	 * @param cause
	 */
	public FedoraAPIException(String message, Throwable cause) {
		super(message, cause);
	}

}
