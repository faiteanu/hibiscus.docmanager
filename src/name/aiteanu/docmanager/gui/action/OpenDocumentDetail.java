package name.aiteanu.docmanager.gui.action;

import java.rmi.RemoteException;

import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;
import name.aiteanu.docmanager.Settings;
import name.aiteanu.docmanager.gui.view.DocumentDetail;
import name.aiteanu.docmanager.rmi.Document;

/**
 * Action for "open document details" or "create new Document".
 */
public class OpenDocumentDetail implements Action {

	@Override
	public void handleAction(Object context) throws ApplicationException {

		Document document = null;

		// check if the context is a document
		if (context != null && (context instanceof Document)) {
			document = (Document) context;
		} else if (context != null && (context instanceof Document[])) { // if multiple documents are selected, take the first one
			document = ((Document[]) context)[0];
		} else {
			try {
				// create new project
				document = (Document) Settings.getDBService().createObject(Document.class, null);
			} catch (RemoteException e) {
				throw new ApplicationException(Settings.i18n().tr("error while creating new document"), e);
			}
		}

		// ok, lets start the dialog
		GUI.startView(DocumentDetail.class, document);
	}
}