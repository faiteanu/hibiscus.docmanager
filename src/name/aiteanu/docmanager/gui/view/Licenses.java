package name.aiteanu.docmanager.gui.view;

import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.Part;
import name.aiteanu.docmanager.gui.controller.LicenseController;

public class Licenses extends AbstractView {

	public void bind() throws Exception {
		GUI.getView().setTitle("Lizenzinformationen von DocManager");

		LicenseController control = new LicenseController(this);
		Part libs = control.getLibList();
		libs.paint(getParent());

	}
}