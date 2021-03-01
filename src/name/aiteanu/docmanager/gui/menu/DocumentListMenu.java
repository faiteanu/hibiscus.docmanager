package name.aiteanu.docmanager.gui.menu;

import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.parts.CheckedContextMenuItem;
import de.willuhn.jameica.gui.parts.ContextMenu;
import de.willuhn.jameica.gui.parts.ContextMenuItem;
import de.willuhn.util.ApplicationException;
import name.aiteanu.docmanager.Settings;
import name.aiteanu.docmanager.gui.action.DeleteDocument;
import name.aiteanu.docmanager.gui.action.OpenDocument;
import name.aiteanu.docmanager.gui.action.OpenDocumentDetail;

/**
 * Prepared context menu for project tables. 
 */
public class DocumentListMenu extends ContextMenu
{
	/**
   * ct.
   */
  public DocumentListMenu()
	{
		// CheckedContextMenuItems will be disabled, if the user clicks into an empty space of the table
		addItem(new CheckedContextMenuItem(Settings.i18n().tr("Open document"), new OpenDocument()));
		addItem(new CheckedContextMenuItem(Settings.i18n().tr("Details..."), new OpenDocumentDetail()));
		
		// separator
		addItem(ContextMenuItem.SEPARATOR);

		//addItem(new CheckedContextMenuItem(Settings.i18n().tr("Add Task..."),new TaskDetail()));

		addItem(new ContextMenuItem(Settings.i18n().tr("New document..."),new Action()
		{
			public void handleAction(Object context) throws ApplicationException
			{
				// we force the context to be null to create a new document in any case
				new OpenDocumentDetail().handleAction(null);
			}
		}));

		addItem(ContextMenuItem.SEPARATOR);
		addItem(new CheckedContextMenuItem(Settings.i18n().tr("Delete..."), new DeleteDocument()));

	}
}
