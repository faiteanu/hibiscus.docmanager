package name.aiteanu.docmanager.gui.menu;

import java.rmi.RemoteException;

import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.parts.CheckedContextMenuItem;
import de.willuhn.jameica.gui.parts.ContextMenu;
import de.willuhn.jameica.gui.parts.ContextMenuItem;
import de.willuhn.util.ApplicationException;
import name.aiteanu.docmanager.Settings;
import name.aiteanu.docmanager.gui.action.DeleteDocument;
import name.aiteanu.docmanager.gui.action.MarkDocumentAsRead;
import name.aiteanu.docmanager.gui.action.MarkDocumentAsUnread;
import name.aiteanu.docmanager.gui.action.OpenDocument;
import name.aiteanu.docmanager.gui.action.OpenDocumentDetail;
import name.aiteanu.docmanager.rmi.Document;

/**
 * Prepared context menu for document tables. 
 */
public class DocumentListMenu extends ContextMenu
{
	/**
	 * ct.
	 */
	public DocumentListMenu()
	{
		// CheckedContextMenuItems will be disabled, if the user clicks into an empty space of the table
		addItem(new CheckedContextMenuItem(Settings.i18n().tr("Open"), new OpenDocument(), "application-pdf.png"));
		addItem(new CheckedContextMenuItem(Settings.i18n().tr("Edit"), new OpenDocumentDetail(), "document-open.png"));
		addItem(new CheckedContextMenuItem(Settings.i18n().tr("Mark as read"), new MarkDocumentAsRead(), "emblem-default.png") { 
			@Override
			public boolean isEnabledFor(Object o) {
				if((o instanceof Document[]))
					return true;
				if(!(o instanceof Document))
					return false;
				
				try {
					return ((Document)o).getReadOn() == null;
				} catch (RemoteException e) {
					// ignore exception
				}
				return false;
			}});
	    addItem(new CheckedContextMenuItem(Settings.i18n().tr("Mark as unread"), new MarkDocumentAsUnread(), "edit-undo.png"){ 
			@Override
			public boolean isEnabledFor(Object o) {
				if((o instanceof Document[]))
					return true;
				if(!(o instanceof Document))
					return false;
				
				try {
					return ((Document)o).getReadOn() != null;
				} catch (RemoteException e) {
					// ignore exception
				}
				return false;
			}});
	    
		// separator
		addItem(ContextMenuItem.SEPARATOR);

		addItem(new ContextMenuItem(Settings.i18n().tr("New document..."),new Action()
		{
			public void handleAction(Object context) throws ApplicationException
			{
				// we force the context to be null to create a new document in any case
				new OpenDocumentDetail().handleAction(null);
			}
		}, "text-x-generic.png"));

		addItem(ContextMenuItem.SEPARATOR);
		addItem(new CheckedContextMenuItem(Settings.i18n().tr("Delete..."), new DeleteDocument(),"user-trash-full.png"));
		
//		addItem(ContextMenuItem.SEPARATOR);
//		addItem(new ContextMenuItem(Settings.i18n().tr("Alle als gelesen markieren"), new MarkDocumentAsRead(),"ok.png"));
	}
}
