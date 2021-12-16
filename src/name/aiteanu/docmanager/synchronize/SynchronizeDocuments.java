/**
 * 
 */
package name.aiteanu.docmanager.synchronize;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import de.willuhn.jameica.hbci.messaging.ImportMessage;
import de.willuhn.jameica.messaging.TextMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.BackgroundTask;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.ProgressMonitor;
import name.aiteanu.docmanager.institute.baaderbank.InstituteOptionsBaader;
import name.aiteanu.docmanager.institute.baaderbank.WebSyncBaader;
import name.aiteanu.docmanager.institute.deka.InstituteOptionsDeka;
import name.aiteanu.docmanager.institute.deka.WebSyncDeka;
import name.aiteanu.docmanager.institute.dkb.WebSyncDkb;
import name.aiteanu.docmanager.rmi.Account;
import name.aiteanu.docmanager.rmi.Document;

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
					case InstituteOptionsBaader.SHORT_NAME:
						monitor.setStatusText("Institute " + account.getInstitute());
						WebSyncBaader syncBaader = new WebSyncBaader();
						syncBaader.synchronizeDocuments(account, monitor);
						account.setLastUpdateOn(new Date());
						account.store();
						break;
					case "DKB":
						monitor.setStatusText("Institute " + account.getInstitute());
						WebSyncDkb syncDkb = new WebSyncDkb();
						syncDkb.synchronizeDocuments(account, monitor);
						account.setLastUpdateOn(new Date());
						account.store();
						break;
					case InstituteOptionsDeka.SHORT_NAME:
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

	public static void notifyDocumentListeners(Document doc) {
		try
		{
			Application.getMessagingFactory().sendMessage(new ImportMessage(doc));
			Application.getMessagingFactory().getMessagingQueue("hibiscus.ibankstatement")
					.sendMessage(new TextMessage(doc.getLocalFolder() + File.separator + doc.getFilename()));
		}
		catch (Exception ex)
		{
			Logger.error("error while sending import message",ex);
		}
	}
}
