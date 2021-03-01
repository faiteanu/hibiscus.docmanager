package name.aiteanu.docmanager.gui.action;

import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.dialogs.AbstractDialog;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import name.aiteanu.docmanager.Settings;
import name.aiteanu.docmanager.gui.dialog.About;

public class ShowAbout implements Action {

	@Override
	public void handleAction(Object context) throws ApplicationException {
		try {
			new About(AbstractDialog.POSITION_CENTER).open();
		} catch (ApplicationException ae) {
			throw ae;
		} catch (Exception e) {
			Logger.error("error while opening about dialog", e);
			throw new ApplicationException(Settings.i18n().tr("Error while opening the About dialog"));
		}
	}

}
