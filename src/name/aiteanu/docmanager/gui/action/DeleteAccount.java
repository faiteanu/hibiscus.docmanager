package name.aiteanu.docmanager.gui.action;

import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import name.aiteanu.docmanager.Settings;
import name.aiteanu.docmanager.rmi.Account;

/**
 * Action for "delete document".
 */
public class DeleteAccount implements Action {

	/**
	 * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
	 */
	public void handleAction(Object context) throws ApplicationException {

		// check if the context is a document
		if (context == null || !(context instanceof Account))
			throw new ApplicationException(Settings.i18n().tr("Please choose an account"));

		Account account = (Account) context;
		if(true)
			throw new ApplicationException("Deletion not implemented yet");
		
		try {

			// before deleting the document, we show up a confirm dialog ;)
			String question = Settings.i18n()
					.tr("Do you really want to delete this account? " + "The file on disk will be deleted as well.");
			if (!Application.getCallback().askUser(question))
				return;

//			File file = null;
//			try {
//				file = new File(document.getLocalFolder() + File.separator + document.getFilename());
//			} catch (RemoteException e) {
//				Logger.error("error while deleting document", e);
//			}
//			if (file == null || !file.exists()) {
//				Application.getMessagingFactory().sendMessage(new StatusBarMessage(
//						Settings.i18n().tr("Datei existiert nicht im Dateisystem"), StatusBarMessage.TYPE_INFO));
//			} else {
//				file.delete();
//			}
			
			//account.delete();

			// Send Status update message
			Application.getMessagingFactory().sendMessage(new StatusBarMessage(
					Settings.i18n().tr("Account deleted successfully"), StatusBarMessage.TYPE_SUCCESS));
		} catch (ApplicationException ae) {
			throw ae;
		} catch (Exception e) {
			Logger.error("error while deleting Account", e);
			throw new ApplicationException(Settings.i18n().tr("Error while deleting Account"));
		}
	}

}
