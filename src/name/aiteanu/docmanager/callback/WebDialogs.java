package name.aiteanu.docmanager.callback;

import de.derrichter.hibiscus.mashup.gui.dialogs.security.AuthDialog;
import de.willuhn.jameica.gui.util.SWTUtil;
import de.willuhn.logging.Logger;

public class WebDialogs {
	public static String askPassword(int minL, int maxL, String message, String title, String logoPath) throws Exception {
		String art = "Passwort";
		AuthDialog.setAliasText(art);
		AuthDialog dialog = new AuthDialog();
		dialog.setText(message);
		dialog.setTitle(title);
		dialog.setMinMaxLenght(minL, maxL);
		Logger.trace("logoPath: " + logoPath);
		dialog.setLogoImage(SWTUtil.getImage(logoPath));
		String password = (String) dialog.open();
		return password;
	}
}
