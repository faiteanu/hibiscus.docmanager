/**
 * 
 */
package name.aiteanu.docmanager.callback;

import de.willuhn.logging.Logger;

/**
 * @author aitea
 *
 */
public class WebLogger {
	  public static void info(String message) {
	    Logger.info(message);
	  }
	  
	  public static void warn(String message) {
	    Logger.warn(message);
	  }
	  
	  public static void error(String message) {
	    Logger.error(message);
	  }
	  
	  public static void debug(String message) {
	    Logger.debug(message);
	  }
	  
	  public static void trace(String message) {
	    Logger.trace(message);
	  }
	}
