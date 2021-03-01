
package name.aiteanu.docmanager.gui.box;

import java.rmi.RemoteException;

import org.eclipse.swt.widgets.Composite;

import de.willuhn.jameica.gui.boxes.AbstractBox;
import de.willuhn.jameica.gui.boxes.Box;
import name.aiteanu.docmanager.gui.part.SynchronizeList;
import name.aiteanu.docmanager.Settings;

/**
 * Box zum Synchronisieren der Konten.
 */
public class SynchronizeBox extends AbstractBox implements Box
{
  private SynchronizeList list = null;

  @Override
  public String getName()
  {
    return "DocManager: " + Settings.i18n().tr("Synchronize documents");
  }

  @Override
  public void paint(Composite parent) throws RemoteException
  {
    this.list = new SynchronizeList();
    list.paint(parent);
  }

  @Override
  public int getDefaultIndex()
  {
    return 3;
  }

  @Override
  public boolean getDefaultEnabled()
  {
    return true;
  }

  @Override
  public int getHeight()
  {
    return 200;
  }

  @Override
  public boolean isActive()
  {
    return super.isActive() && !de.willuhn.jameica.hbci.Settings.isFirstStart();
  }

}
