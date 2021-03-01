/**
 * 
 */
package name.aiteanu.docmanager.callback;

//import de.derrichter.hibiscus.mashup.crossover.websync.WebSynchronizeBackend;
//import de.willuhn.jameica.hbci.synchronize.SynchronizeSession;
import de.willuhn.jameica.services.BeanService;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ProgressMonitor;

/**
 * 
 * @author aitea
 *
 */
public class WebProgressMonitor {
  public static void log(String message) {
//    BeanService service = (BeanService)Application.getBootLoader().getBootable(BeanService.class);
//    Logger.trace("MonitorLog-Invoke eingegangen - BeanService: " + service.toString());
//    SynchronizeSession session = ((WebSynchronizeBackend)service.get(WebSynchronizeBackend.class)).getCurrentSession();
//    Logger.trace("MonitorLog-Invoke eingegangen - SynchronizeSession: ");
//    if (session != null) {
//      ProgressMonitor monitor = session.getProgressMonitor();
//      monitor.log(message);
//    } else {
//      Logger.warn("SynchronizeSession ist null; daher kann keine MonitorLog ausgegeben werden");
//    } 
  }
  
  public static void setPercentComplete(int prozent) {
//    BeanService service = (BeanService)Application.getBootLoader().getBootable(BeanService.class);
//    SynchronizeSession session = ((WebSynchronizeBackend)service.get(WebSynchronizeBackend.class)).getCurrentSession();
//    if (session != null) {
//      ProgressMonitor monitor = session.getProgressMonitor();
//      Logger.trace("MonitorLog-Fortschritt wird per Invoke auf '" + String.valueOf(prozent) + "' Prozent gesetzt");
//      monitor.setPercentComplete(prozent);
//    } else {
//      Logger.warn("SynchronizeSession ist null; daher kann kein Monitor-Fortschritt ausgegeben werden");
//    } 
  }
}
