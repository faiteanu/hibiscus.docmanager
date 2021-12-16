package name.aiteanu.docmanager.messaging;

import java.rmi.RemoteException;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.datasource.rmi.DBService;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.util.DelayedListener;
import de.willuhn.jameica.hbci.messaging.ObjectChangedMessage;
import de.willuhn.jameica.messaging.Message;
import de.willuhn.jameica.messaging.MessageConsumer;
import de.willuhn.jameica.messaging.SystemMessage;
import de.willuhn.logging.Level;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import name.aiteanu.docmanager.Settings;
import name.aiteanu.docmanager.rmi.Document;

public class NavigationMessageConsumer implements MessageConsumer {

	private DelayedListener listener = new DelayedListener(1000, new Worker());
	
	@Override
	public Class[] getExpectedMessageTypes() {
		return new Class[] { ObjectChangedMessage.class, SystemMessage.class };
	}

	@Override
	public void handleMessage(Message message) throws Exception {
		if (message instanceof SystemMessage) {
			SystemMessage msg = (SystemMessage) message;
			if(msg.getStatusCode() == SystemMessage.SYSTEM_STARTED) {
				//updateNavigation();
				listener.handleEvent(null);
			}
		} else if (message instanceof ObjectChangedMessage) {
			ObjectChangedMessage msg = (ObjectChangedMessage) message;
			if (msg.getObject() instanceof Document) {
				listener.handleEvent(null);
			}
		}
	}

	private void updateNavigation() throws ApplicationException {
		DBService service;
		try {
			service = Settings.getDBService();
			DBIterator<Document> documents = service.createList(Document.class);
			documents.addFilter("readon is null");
			int count = documents.size();
			GUI.getNavigation().setUnreadCount("docmanager.navi.documents", count);
		} catch (RemoteException e) {
			Logger.error("unable to access database", e);
			throw new ApplicationException("error while updating DocManager navigation", e);
		}
	}
	
	
	@Override
	public boolean autoRegister() {
		return false;
	}
	
	
  /**
   * Der eigentliche Worker.
   */
  private class Worker implements Listener
  {
    /**
     * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
     */
    public void handleEvent(Event event)
    {
    	DBService service;
		try {
			service = Settings.getDBService();
			DBIterator<Document> documents = service.createList(Document.class);
			documents.addFilter("readon is null");
			int count = documents.size();
			GUI.getNavigation().setUnreadCount("docmanager.navi.documents", count);
			
//			DBIterator<Account> accounts = service.createList(Account.class);
//			while(accounts.hasNext()) {
//				Account account = accounts.next();
//				NavigationItem nav = new NavigationItem();
//				
//			}
			
		} catch (RemoteException e) {
			Logger.write(Level.ERROR, "unable to update navigation", e);
		}
    }
    
  }

}
