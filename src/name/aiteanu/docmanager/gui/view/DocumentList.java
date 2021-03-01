package name.aiteanu.docmanager.gui.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.TabFolder;

import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.input.MultiInput;
import de.willuhn.jameica.gui.parts.ButtonArea;
import de.willuhn.jameica.gui.util.ColumnLayout;
import de.willuhn.jameica.gui.util.Container;
import de.willuhn.jameica.gui.util.SimpleContainer;
import de.willuhn.jameica.gui.util.TabGroup;
import de.willuhn.util.ApplicationException;
import name.aiteanu.docmanager.Settings;
import name.aiteanu.docmanager.gui.action.SyncDocuments;
import name.aiteanu.docmanager.gui.controller.DocumentListController;

/**
 * View to show the list of existing documents.
 */
public class DocumentList extends AbstractView {

	@Override
	public void bind() throws Exception {

		GUI.getView().setTitle(Settings.i18n().tr("Documents"));

		DocumentListController controller = new DocumentListController(this);

		final TabFolder folder = new TabFolder(this.getParent(), SWT.NONE);
		folder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		TabGroup tab = new TabGroup(folder, Settings.i18n().tr("Filter view"));

		ColumnLayout cols = new ColumnLayout(tab.getComposite(), 2);

		{
			Container left = new SimpleContainer(cols.getComposite());
			left.addInput(controller.getAccount());
			//left.addInput(this.getKontoAuswahl());
			//left.addInput(this.getInclusiveFilter());
			left.addInput(controller.getUnread());
		}

		{
			Container right = new SimpleContainer(cols.getComposite());
			right.addInput(controller.getRange());
			MultiInput range = new MultiInput(controller.getFrom(), controller.getTo());
			right.addInput(range);
		}
		

		ButtonArea buttons = new ButtonArea();

		// the last parameter "true" makes the button the default one
		buttons.addButton(Settings.i18n().tr("Load documents"), new SyncDocuments(), null, true,	"mail-send-receive.png");
		buttons.addButton(Settings.i18n().tr("Refresh"), new Action()
	    {
	      @Override
	      public void handleAction(Object context) throws ApplicationException
	      {
	        controller.handleReload(true);
	      }
	    
	    },null,true,"view-refresh.png");
		buttons.paint(getParent());

		controller.getDocumentsTable().paint(this.getParent());
	}
}
