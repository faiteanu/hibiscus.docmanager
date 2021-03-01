package name.aiteanu.docmanager.gui.controller;

import de.willuhn.io.FileFinder;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Part;
import de.willuhn.jameica.gui.parts.FormTextPart;
import de.willuhn.jameica.plugin.AbstractPlugin;
import de.willuhn.jameica.plugin.Manifest;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.util.InfoReader;
import de.willuhn.logging.Logger;
import de.willuhn.util.I18N;
import name.aiteanu.docmanager.DocManager;

import java.io.File;
import java.io.FileInputStream;

public class LicenseController extends AbstractControl {
  private Part libList = null;
  
  private I18N i18n = null;
  
  public LicenseController(AbstractView view) {
    super(view);
    this.i18n = Application.getPluginLoader().getPlugin(DocManager.class).getResources().getI18N();
  }
  
  public Part getLibList() {
    if (this.libList != null)
      return this.libList; 
    AbstractPlugin plugin = (AbstractPlugin)Application.getPluginLoader().getPlugin(DocManager.class);
    StringBuffer buffer = new StringBuffer();
    buffer.append("<form>");
    Manifest manifest = null;
    try {
      manifest = Application.getPluginLoader().getManifest(DocManager.class);
    } catch (Exception e) {
      Logger.error("unable to read info.xml from plugin hibiscus.docmanager", e);
    } 
    buffer.append("<p><span color=\"header\" font=\"header\">" + this.i18n.tr("Hibiscus DocManager") + "</span></p>");
    if (manifest != null) {
      buffer.append("<p>");
      buffer.append(manifest.getDescription());
      buffer.append("<br/>" + manifest.getHomepage());
      buffer.append("<br/>" + manifest.getLicense());
      buffer.append("</p>");
    } 
    String path = plugin.getManifest().getPluginDir();
    FileFinder finder = new FileFinder(new File(path + "/lib"));
    finder.matches(".*?info\\.xml$");
    File[] infos = finder.findRecursive();
    for (int i = 0; i < infos.length; i++) {
      if (!infos[i].isFile() || !infos[i].canRead()) {
        Logger.warn("unable to read " + infos[i] + ", skipping");
      } else {
        try {
          InfoReader ir = new InfoReader(new FileInputStream(infos[i]));
          buffer.append("<p>");
          buffer.append("<b>" + ir.getName() + "</b>");
          buffer.append("<br/>" + ir.getDescription());
          buffer.append("<br/>" + ir.getUrl());
          buffer.append("<br/>" + ir.getLicense());
          buffer.append("</p>");
        } catch (Exception e) {
          Logger.error("unable to parse " + infos[0], e);
        } 
      } 
    } 
    buffer.append("</form>");
    this.libList = (Part)new FormTextPart(buffer.toString());
    return this.libList;
  }
}
