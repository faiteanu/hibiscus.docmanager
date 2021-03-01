/**
 * 
 */
package name.aiteanu.docmanager.synchronize;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.system.BackgroundTask;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.ProgressMonitor;
import name.aiteanu.docmanager.Settings;
import name.aiteanu.docmanager.institute.deka.WebSyncDeka;
import name.aiteanu.docmanager.institute.dkb.WebSyncDkb;
import name.aiteanu.docmanager.rmi.Account;

/**
 * @author aiteanu
 *
 */
public class SynchronizeDocuments implements BackgroundTask {

	private boolean abort = false;
	private List<Account> accounts = new ArrayList<Account>();
	
	
	public SynchronizeDocuments(Collection<Account> accounts) {
		super();
		this.accounts.addAll(accounts);
	}

	@Override
	public void run(ProgressMonitor monitor) throws ApplicationException {
		boolean syncHasErrors = false;

		try {
			int currentIndex = 1;
			//DBIterator<Account> accounts = Settings.getDBService().createList(Account.class);
			//while (accounts.hasNext()) {
			for(Account account : accounts) {
				if (abort) {
					return;
				}
				//Account account = accounts.next();
				monitor.setPercentComplete(100 * currentIndex / accounts.size());
				monitor.setStatusText("Starte mit " + account.getName());

				Logger.info("Lade Dokumente: Starte mit " + account.getName());
				
				try { // jedes Institut wird einzeln im try-catch abgefragt
					switch (account.getInstitute()) {
					case "DKB":
						monitor.setStatusText("Institute " + account.getInstitute());
						WebSyncDkb sync = new WebSyncDkb();
						sync.synchronizeDocuments(account, monitor);
						account.setLastUpdateOn(new Date());
						account.store();
						break;
					case "Deka":
						monitor.setStatusText("Institute " + account.getInstitute());
						WebSyncDeka syncDeka = new WebSyncDeka();
						syncDeka.synchronizeDocuments(account, monitor);
						account.setLastUpdateOn(new Date());
						account.store();
						break;
					default:					
						monitor.setStatusText(account.getName() + " ist leider noch nicht implementiert.");
						Logger.info(account.getName() + " ist leider noch nicht implementiert.");

						break;
					}
				} catch (Exception e) {
					syncHasErrors = true;
					monitor.setStatus(ProgressMonitor.STATUS_ERROR);
					monitor.setStatusText(e.getMessage());
					//e.printStackTrace();
					//throw new ApplicationException("Fehler beim Laden der Dokumente." , e);
				}
				//Application.getMessagingFactory().sendMessage(new KursUpdatesMsg(wpid));
				
				++currentIndex;
			}
//		} catch (ApplicationException e) {
//			monitor.setStatus(ProgressMonitor.STATUS_ERROR);
//			monitor.setStatusText(e.getMessage());
//			e.printStackTrace();
//			throw e;
		} catch (Exception e) {
			monitor.setStatus(ProgressMonitor.STATUS_ERROR);
			monitor.setStatusText(e.getMessage());
			e.printStackTrace();
			throw new ApplicationException("Fehler beim Laden der Dokumente." , e);
		}
		monitor.setStatusText("Fertig");
		monitor.setStatus(ProgressMonitor.STATUS_DONE);
		monitor.setPercentComplete(101);

		// TODO überprüfen
		if(syncHasErrors) monitor.setStatus(ProgressMonitor.STATUS_ERROR);
	}

	@Override
	public void interrupt() {
		abort = true;
	}

	@Override
	public boolean isInterrupted() {
		return false;
	}

}
