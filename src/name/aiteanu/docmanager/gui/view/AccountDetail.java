package name.aiteanu.docmanager.gui.view;

import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.parts.ButtonArea;
import de.willuhn.jameica.gui.util.ColumnLayout;
import de.willuhn.jameica.gui.util.Container;
import de.willuhn.jameica.gui.util.SimpleContainer;
import de.willuhn.util.ApplicationException;
import name.aiteanu.docmanager.Settings;
import name.aiteanu.docmanager.gui.action.DeleteAccount;
import name.aiteanu.docmanager.gui.controller.AccountController;


/**
 * this is the dialog for the account details. 
 */
public class AccountDetail extends AbstractView
{

	@Override
	public void bind() throws Exception
	{
		// draw the title
		GUI.getView().setTitle(Settings.i18n().tr("Account details"));

		// Instantiate controller
		final AccountController controller = new AccountController(this);

		Container c = new SimpleContainer(getParent());

		// layout with 2 columns
		ColumnLayout columns = new ColumnLayout(c.getComposite(),2);

		// left side
		Container left = new SimpleContainer(columns.getComposite());
		left.addHeadline(Settings.i18n().tr("Details"));
		left.addInput(controller.getInstitute());
		left.addInput(controller.getUserName());
		left.addInput(controller.getName());
		left.addInput(controller.getPath());

		// right side
		Container right = new SimpleContainer(columns.getComposite(),true);
		right.addHeadline(Settings.i18n().tr("Description"));
		right.addInput(controller.getDescription());

		// add some buttons
		ButtonArea buttons = new ButtonArea();
		buttons.addButton(Settings.i18n().tr("Delete"),  	new DeleteAccount(), controller.getCurrentObject(), false, "user-trash-full.png");
		buttons.addButton(Settings.i18n().tr("Store"),   	new Action()
		{
			public void handleAction(Object context) throws ApplicationException
			{
				controller.handleStore();
			}
		}, null, true, "document-save.png"); // "true" defines this button as the default button

		// Don't forget to paint the button area
		buttons.paint(getParent());

		// show task tasks in this project
		//new Headline(getParent(),Settings.i18n().tr("Tasks within this project"));
		//control.getTaskList().paint(getParent());
	}

	@Override
	public void unbind() throws ApplicationException
	{
		// this method will be invoked when leaving the dialog.
		// You are able to interrupt the unbind by throwing an
		// ApplicationException.
	}

}
