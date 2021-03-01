package name.aiteanu.docmanager.gui.action;

import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;
import name.aiteanu.docmanager.gui.view.Licenses;

public class ShowLicense implements Action {

	@Override
	public void handleAction(Object context) throws ApplicationException {
		GUI.startView(Licenses.class, null);
	}

}
