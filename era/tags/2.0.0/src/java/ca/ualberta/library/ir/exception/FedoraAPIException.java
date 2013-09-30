/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: FedoraAPIException.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.exception;

/**
 * The FedoraAPIException class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
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
