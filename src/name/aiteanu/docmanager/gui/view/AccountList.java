package name.aiteanu.docmanager.gui.view;

import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.parts.ButtonArea;
import name.aiteanu.docmanager.Settings;
import name.aiteanu.docmanager.gui.action.OpenAccountDetail;
import name.aiteanu.docmanager.gui.action.SyncDocuments;
import name.aiteanu.docmanager.gui.controller.AccountController;

/**
 * View to show the list of existing accounts.
 */
public class AccountList extends AbstractView {

	@Override
	public void bind() throws Exception {

		GUI.getView().setTitle(Settings.i18n().tr("Available Accounts"));

		AccountController control = new AccountController(this);

		control.getAccountsTable().paint(this.getParent());

		ButtonArea buttons = new ButtonArea();

		// the last parameter "true" makes the button the default one
		buttons.addButton(Settings.i18n().tr("New account..."), new OpenAccountDetail(), null, false, "list-add.png");
		// buttons.addButton(Settings.i18n().tr("Load documents"), new SyncDocuments(), null, true, "mail-send-receive.png");

		buttons.paint(getParent());

	}
}
