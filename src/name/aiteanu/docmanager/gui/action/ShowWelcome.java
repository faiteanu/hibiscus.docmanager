
package name.aiteanu.docmanager.gui.action;

import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;
import name.aiteanu.docmanager.gui.view.Welcome;

/**
 * Action for the welcome screen.
 */
public class ShowWelcome implements Action {

	@Override
	public void handleAction(Object context) throws ApplicationException {
		GUI.startView(Welcome.class, null);
	}

}