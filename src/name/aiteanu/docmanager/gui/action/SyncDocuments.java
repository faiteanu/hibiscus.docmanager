
package name.aiteanu.docmanager.gui.action;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.willuhn.datasource.pseudo.PseudoIterator;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;
import name.aiteanu.docmanager.Settings;
import name.aiteanu.docmanager.rmi.Account;
import name.aiteanu.docmanager.synchronize.SynchronizeDocuments;

/**
 * @author aiteanu
 *
 */
public class SyncDocuments implements Action {

	@Override
	public void handleAction(Object context) throws ApplicationException {
		
		List<Account> list = new ArrayList<Account>();
		if(context != null && context instanceof Account[]) {
			list = Arrays.asList((Account[])context);
		} else if(context != null && context instanceof Account) {
			list.add((Account)context);
		} else { // context == null
			try {
				DBIterator<Account> accounts = Settings.getDBService().createList(Account.class);
				list.addAll(PseudoIterator.asList(accounts));
			} catch (RemoteException e) {
				
			}			
		}
		
		SynchronizeDocuments sync = new SynchronizeDocuments(list);
		Application.getController().start(sync);
	}

}
