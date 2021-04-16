
package name.aiteanu.docmanager.gui.action;

import java.io.File;
import java.rmi.RemoteException;
import java.util.Date;

import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.internal.action.Program;
import de.willuhn.jameica.hbci.HBCI;
import de.willuhn.jameica.hbci.messaging.ObjectChangedMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;
import name.aiteanu.docmanager.rmi.Document;

/**
 * Action zum Oeffnen eines Dokuments.
 */
public class OpenDocument implements Action
{
	private final static I18N i18n = Application.getPluginLoader().getPlugin(HBCI.class).getResources().getI18N();

	@Override
	public void handleAction(Object context) throws ApplicationException
	{
		if (context == null || (!(context instanceof Document) && !(context instanceof Document[])))
			throw new ApplicationException(i18n.tr("Bitte wählen Sie das zu öffnende Dokument"));

		if(context instanceof Document) {
			context = new Document[] { (Document)context };
		}

		for(Document document : (Document[])context) {
			File file = null;
			try {
				file = new File(document.getLocalFolder() + File.separator + document.getFilename());
			} catch (RemoteException e) {

			}
			if (file == null || !file.exists() || !file.canRead())
				throw new ApplicationException(i18n.tr("Datei existiert nicht oder ist nicht lesbar: " + file.getPath()));

			new Program().handleAction(file);

			// Als gelesen markieren, sobald er geoeffnet wurde
			//KontoauszugPdfUtil.markRead(true,k);
			try {
				if(document.getReadOn() == null) {
					document.setReadOn(new Date());
					document.store();
					Application.getMessagingFactory().sendMessage(new ObjectChangedMessage(document));
				}
			} catch (RemoteException e) {
				Logger.error("Error marking document as read.", e);
			}
		}
	}

}


