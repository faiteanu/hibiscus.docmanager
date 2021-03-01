package name.aiteanu.docmanager.gui.view;

import name.aiteanu.docmanager.Settings;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.parts.ButtonArea;
import de.willuhn.jameica.gui.util.ColumnLayout;
import de.willuhn.jameica.gui.util.Container;
import de.willuhn.jameica.gui.util.SimpleContainer;
import de.willuhn.util.ApplicationException;
import name.aiteanu.docmanager.gui.action.DeleteDocument;
import name.aiteanu.docmanager.gui.action.OpenDocument;
import name.aiteanu.docmanager.gui.controller.DocumentController;


/**
 * this is the dialog for the project details. 
 */
public class DocumentDetail extends AbstractView
{

	@Override
	public void bind() throws Exception
	{
		// draw the title
		GUI.getView().setTitle(Settings.i18n().tr("Document details"));

		// Instantiate controller
		final DocumentController controller = new DocumentController(this);

		Container c = new SimpleContainer(getParent());

		// layout with 2 columns
		ColumnLayout columns = new ColumnLayout(c.getComposite(),2);

		// left side
		Container left = new SimpleContainer(columns.getComposite());
		left.addHeadline(Settings.i18n().tr("Details"));
		left.addInput(controller.getAccount());
		left.addInput(controller.getRemoteFolder());
		left.addInput(controller.getTitle());
		//left.addInput(control.getPrice());
		left.addInput(controller.getCreatedOnDate());
		left.addInput(controller.getDownloadedOnDate());
		left.addInput(controller.getReadOnDate());

		// right side
		Container right = new SimpleContainer(columns.getComposite(),true);
		right.addHeadline(Settings.i18n().tr("Description"));
		right.addInput(controller.getDescription());

		//c.addHeadline(Settings.i18n().tr("Summary"));
		//c.addInput(control.getEffortSummary());

		SimpleContainer bottom = new SimpleContainer(getParent(),false);
		bottom.addInput(controller.getLocalFilenameAndFolder());
		
		// add some buttons
		ButtonArea buttons = new ButtonArea();
		buttons.addButton(Settings.i18n().tr("Open document"), new OpenDocument(), controller.getCurrentObject(), false, "application-pdf.png");
		buttons.addButton(Settings.i18n().tr("Delete"),  	new DeleteDocument(), controller.getCurrentObject(), false, "user-trash-full.png");
		buttons.addButton(Settings.i18n().tr("Store"),   	new Action()
		{
			public void handleAction(Object context) throws ApplicationException
			{
				controller.handleStore();
			}
		},null,true,"document-save.png"); // "true" defines this button as the default button

		// Don't forget to paint the button area
		buttons.paint(getParent());

		// show task tasks in this project
		//new Headline(getParent(),Settings.i18n().tr("Tasks within this project"));
		//control.getTaskList().paint(getParent());
	}

	/**
	 * @see de.willuhn.jameica.gui.AbstractView#unbind()
	 */
	public void unbind() throws ApplicationException
	{
		// this method will be invoked when leaving the dialog.
		// You are able to interrupt the unbind by throwing an
		// ApplicationException.
	}

}
