package name.aiteanu.docmanager.gui.action;

import java.rmi.RemoteException;

import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;
import name.aiteanu.docmanager.Settings;
import name.aiteanu.docmanager.gui.view.AccountDetail;
import name.aiteanu.docmanager.rmi.Account;

/**
 * Action for "show account details" or "create new account".
 */
public class OpenAccountDetail implements Action {

	@Override
	public void handleAction(Object context) throws ApplicationException {

		Account account = null;

		// check if the context is an account
		if (context != null && (context instanceof Account)) {
			account = (Account) context;
		} else {
			try {
				// create new project
				account = (Account) Settings.getDBService().createObject(Account.class, null);
			} catch (RemoteException e) {
				throw new ApplicationException(Settings.i18n().tr("error while creating new account"), e);
			}
		}

		// ok, lets start the dialog
		GUI.startView(AccountDetail.class, account);
	}
}
