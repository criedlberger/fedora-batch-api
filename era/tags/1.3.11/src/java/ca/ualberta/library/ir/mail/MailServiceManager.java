/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: era
 * $Id: MailServiceManager.java 5485 2012-08-13 17:40:46Z pcharoen $
 */

package ca.ualberta.library.ir.mail;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import ca.ualberta.library.ir.domain.Register;
import ca.ualberta.library.ir.domain.User;
import ca.ualberta.library.ir.exception.MailServiceException;
import ca.ualberta.library.ir.model.solr.Item;

/**
 * The MailServiceManager class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 5485 $ $Date: 2012-08-13 11:40:46 -0600 (Mon, 13 Aug 2012) $
 */
public interface MailServiceManager {

	public void sendProquestUploadMail(String[] to, String language, String subject, List<String> files)
		throws MailServiceException;

	public void sendPasswordMail(User user, String password, String subject, String url) throws MailServiceException;

	public void sendUsernameMail(User user, String subject, String url) throws MailServiceException;

	public void sendRegisterMail(User user, Register register, String subject, String url) throws MailServiceException;

	public void sendActivationMail(User user, String subject, String url) throws MailServiceException;

	public void sendDepositMail(User user, String subject, Map<String, Object> model, String url)
		throws MailServiceException;

	public void sendSubmissionMail(List<User> users, User user, String subject, Map<String, Object> model, String url)
		throws MailServiceException;

	public void sendRejectMail(User user, String subject, Map<String, Object> model, String url, String comments)
		throws MailServiceException;

	public void sendArchiveMail(User user, String subject, Map<String, Object> model, String url, String comments)
		throws MailServiceException;

	public void sendSubscriptionMail(User user, String subject, Collection<Item> items) throws MailServiceException;

	public void sendEmbargoedMail(List<User> users, String subject, Map<String, Object> model, String url)
		throws MailServiceException;
}
