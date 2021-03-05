package name.aiteanu.docmanager.gui.action;

import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;
import name.aiteanu.docmanager.gui.view.AccountList;

/**
 * Action to open the account list.
 */
public class ListAccounts implements Action {

	@Override
	public void handleAction(Object context) throws ApplicationException {
		GUI.startView(AccountList.class, null);
	}

}
