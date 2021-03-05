
package name.aiteanu.docmanager.gui.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.util.LabelGroup;
import de.willuhn.util.ApplicationException;
import name.aiteanu.docmanager.Settings;
import name.aiteanu.docmanager.gui.part.SynchronizeList;


/**
 * Welcome screen of this plugin.
 */
public class Welcome extends AbstractView
{

	/**
	 * this method will be invoked when starting the view.
	 * @see de.willuhn.jameica.gui.AbstractView#bind()
	 */
	public void bind() throws Exception
	{
		GUI.getView().setTitle(Settings.i18n().tr("DocManager"));

		LabelGroup group = new LabelGroup(this.getParent(),Settings.i18n().tr("welcome"));

		group.addText(Settings.i18n().tr("Choose the accounts to be synchronized"),false);

		Composite comp = new Composite(getParent(), SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		comp.setLayoutData(gridData);

		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		comp.setLayout(layout);

		SynchronizeList list = new SynchronizeList();
		list.paint(comp);

		//	    ColumnLayout columns = new ColumnLayout(getParent(),1);

		//		ButtonArea buttons = new ButtonArea();
		//		
		//		// the last parameter "true" makes the button the default one
		//		buttons.addButton(Settings.i18n().tr("Load documents"), new SyncDocuments(),null,true);
		//		
		//		buttons.paint(getParent());

	}

	/**
	 * this method will be executed when exiting the view.
	 * You don't need to dispose your widgets, the GUI controller will
	 * do this in a recursive way for you.
	 * @see de.willuhn.jameica.gui.AbstractView#unbind()
	 */
	@Override
	public void unbind() throws ApplicationException
	{
		// We've nothing to do here ;)
	}

}
