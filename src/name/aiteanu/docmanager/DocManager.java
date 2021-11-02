package name.aiteanu.docmanager;

import java.rmi.RemoteException;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.datasource.rmi.DBService;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.plugin.AbstractPlugin;
import de.willuhn.jameica.plugin.Version;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import name.aiteanu.docmanager.rmi.Document;
import name.aiteanu.docmanager.server.DocDBServiceImpl;

/**
 * You need to have at least one class wich inherits from <code>AbstractPlugin</code>.
 * If so, Jameica will detect your plugin automatically at startup.
 */
public class DocManager extends AbstractPlugin
{

	/**
	 * This method is invoked on every startup.
	 * You can make here some stuff to init your plugin.
	 * If you get some errors here and you dont want to activate the plugin,
	 * simply throw an ApplicationException.
	 * You dont need to implement this function.
	 * @see de.willuhn.jameica.plugin.AbstractPlugin#init()
	 */
	@Override
	public void init() throws ApplicationException
	{
		super.init();
		
		checkDatabase();
		
//		DBService service;
//		try {
//			service = Settings.getDBService();
//			DBIterator<Document> documents = service.createList(Document.class);
//			documents.addFilter("readon is null");
//			int count = documents.size();
//			GUI.getNavigation().setUnreadCount("docmanager.navi.documents", count);
//		} catch (RemoteException e) {
//			Logger.error("unable to access database", e);
//			throw new ApplicationException("error while initializing plugin", e);
//		}
	}

	/**
	 * This method is called only the first time, the plugin is loaded (before executing init()).
	 * if your installation procedure was not successfull, throw an ApplicationException.
	 * You dont need to implement this function.
	 * @see de.willuhn.jameica.plugin.AbstractPlugin#install()
	 */
	@Override
	public void install() throws ApplicationException
	{
		
		// If we are running in client/server mode and this instance
		// is the client, we do not need to create a database.
		// Instead of this we will get our objects via RMI from
		// the server
		if (Application.inClientMode())
			return;
		Logger.info("creating database tables");
		try {
			DocDBServiceImpl service = new DocDBServiceImpl();
			service.start();
			service.install();
			service.stop(false);
			// That's all. Database installed and tables created ;)
		}
		catch (Exception e)
		{
			Logger.error("unable to create database",e);
			throw new ApplicationException("error while installing plugin",e);
		}	
	
	}

	@Override
	public void uninstall(boolean deleteUserData) throws ApplicationException {
		super.uninstall(deleteUserData);
	}
	
	/**
	 * This method will be executed on every version change.
	 * You dont need to implement this function.
	 * @see de.willuhn.jameica.plugin.AbstractPlugin#update(de.willuhn.jameica.plugin.Version)
	 */
	@Override
	public void update(Version oldVersion) throws ApplicationException
	{
		super.update(oldVersion);
	}

	/**
	 * Here you can do some cleanup stuff.
	 * The method will be called on every clean shutdown of jameica.
	 * You dont need to implement this function.
	 * @see de.willuhn.jameica.plugin.AbstractPlugin#shutDown()
	 */
	@Override
	public void shutDown()
	{
		super.shutDown();
	}
	
	private void checkDatabase() throws ApplicationException {
		// If we are running in client/server mode and this instance
		// is the client, we do not need to create a database.
		// Instead of this we will get our objects via RMI from
		// the server
		if (Application.inClientMode())
			return;
		Logger.info("creating database tables");
		try {
			DocDBServiceImpl service = new DocDBServiceImpl();
			service.start();
			service.checkConsistency();
			//service.install();
			service.stop(false);
			// That's all. Database installed and tables created ;)
		}
		catch (Exception e)
		{
			Logger.error("unable to create database",e);
			throw new ApplicationException("error while installing plugin",e);
		}	
	}
}
