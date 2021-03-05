package name.aiteanu.docmanager.gui.dialog;

import org.eclipse.swt.widgets.Composite;

import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.dialogs.AbstractDialog;
import de.willuhn.jameica.gui.input.LabelInput;
import de.willuhn.jameica.gui.parts.ButtonArea;
import de.willuhn.jameica.gui.parts.FormTextPart;
import de.willuhn.jameica.gui.util.LabelGroup;
import de.willuhn.jameica.plugin.AbstractPlugin;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;
import name.aiteanu.docmanager.DocManager;
import name.aiteanu.docmanager.Settings;

/**
 * Our "About..." dialog.
 */
public class About extends AbstractDialog
{

	/**
	 * ct.
	 * @param position
	 */
	public About(int position)
	{
		super(position);
		this.setTitle(Settings.i18n().tr("About DocManager..."));
	}

	@Override
	protected void paint(Composite parent) throws Exception
	{

		FormTextPart text = new FormTextPart();
		text.setText("<form>" +
				"<p><b>DocManager plugin</b></p>" +
				"<br/>Licence: CC BY-NC-SA 4.0 - https://creativecommons.org/licenses/by-nc-sa/4.0/" +
				"<br/><p>Copyright by Fabian Aiteanu</p>" +
				"<p>https://github.com/faiteanu/hibiscus.docmanager</p>" +
				"</form>");

		text.paint(parent);

		LabelGroup group = new LabelGroup(parent, " Information ");

		AbstractPlugin p = Application.getPluginLoader().getPlugin(DocManager.class);

		group.addLabelPair(Settings.i18n().tr("Version"), 			new LabelInput(""+p.getManifest().getVersion()));
		group.addLabelPair(Settings.i18n().tr("Working directory"), new LabelInput(""+p.getResources().getWorkPath()));

		ButtonArea buttons = new ButtonArea();
		buttons.addButton(Settings.i18n().tr("Close"),new Action() {
			public void handleAction(Object context) throws ApplicationException
			{
				close();
			}
		},null,true);
		buttons.paint(parent);
		getShell().pack();

	}

	@Override
	protected Object getData() throws Exception
	{
		return null;
	}

}
