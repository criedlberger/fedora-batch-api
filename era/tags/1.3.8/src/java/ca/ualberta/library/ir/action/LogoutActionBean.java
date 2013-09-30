package ca.ualberta.library.ir.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.LocalizableMessage;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

/**
 * The LogoutActionBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@UrlBinding("/public/logout")
public class LogoutActionBean extends BaseActionBean {

	@DefaultHandler
	@HandlesEvent("logout")
	public Resolution logout() throws Exception {
		context.logout();
		context.getMessages().add(new LocalizableMessage("logout.message"));
		return new RedirectResolution("/public/home");
	}
}
