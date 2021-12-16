
package name.aiteanu.docmanager.gui.action;

import java.rmi.RemoteException;

import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.hbci.HBCI;
import de.willuhn.jameica.hbci.messaging.ObjectChangedMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;
import name.aiteanu.docmanager.rmi.Document;

/**
 * Action um Dokumente als ungelesen zu markieren.
 */
public class MarkDocumentAsUnread implements Action
{
	private final static I18N i18n = Application.getPluginLoader().getPlugin(HBCI.class).getResources().getI18N();

	@Override
	public void handleAction(Object context) throws ApplicationException
	{
		if (context == null || (!(context instanceof Document) && !(context instanceof Document[])))
			throw new ApplicationException(i18n.tr("No document selected"));

		if(context instanceof Document) {
			context = new Document[] { (Document)context };
		}

		for(Document document : (Document[])context) {		
			try {
				document.setReadOn(null);
				document.store();
				Application.getMessagingFactory().sendMessage(new ObjectChangedMessage(document));
			} catch (RemoteException e) {
				Logger.error("Error marking document as unread.", e);
			}
		}
	}

}


