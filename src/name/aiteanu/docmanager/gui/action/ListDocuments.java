package name.aiteanu.docmanager.gui.action;

import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;
import name.aiteanu.docmanager.gui.view.DocumentList;

/**
 * Action to open the documents list.
 */
public class ListDocuments implements Action {

	@Override
	public void handleAction(Object context) throws ApplicationException {
		GUI.startView(DocumentList.class, null);
	}

}
