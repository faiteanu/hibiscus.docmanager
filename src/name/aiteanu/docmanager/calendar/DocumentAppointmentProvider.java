package name.aiteanu.docmanager.calendar;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.graphics.RGB;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.gui.calendar.AbstractAppointment;
import de.willuhn.jameica.gui.calendar.Appointment;
import de.willuhn.jameica.gui.calendar.AppointmentProvider;
import de.willuhn.jameica.gui.util.Color;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import name.aiteanu.docmanager.Settings;
import name.aiteanu.docmanager.gui.action.OpenDocument;
import name.aiteanu.docmanager.rmi.Document;

/**
 * Implementierung eines Termin-Providers fuer Dokumente.
 */
public class DocumentAppointmentProvider implements AppointmentProvider {
	@Override
	public String getName() {
		return "DocManager: Dokumente";
	}

	@Override
	public List<Appointment> getAppointments(Date from, Date to) {
		List<Appointment> result = new LinkedList<Appointment>();

		DBIterator<Document> documents;
		try {
			documents = Settings.getDBService().createList(Document.class);
			documents.addFilter("createdon >= ?", from);
			documents.addFilter("createdon <= ?", to);
	
		    while (documents.hasNext()) {
		    	Document doc = documents.next();
		    	result.add(new MyAppointment(doc));
		    }
		} catch (RemoteException e) {
			Logger.error("unable to load documents", e);
		}
	    return result;
	}
	


	/**
	 * Hilfsklasse zum Anzeigen und Oeffnen des Appointments.
	 */
	private class MyAppointment extends AbstractAppointment {
		Document document;
		
		public MyAppointment(Document doc) {
			this.document = doc;
		}
		
		@Override
		public Date getDate() {
			try {
				return document.getCreatedOn();
			} catch (RemoteException e) {
				return null;
			}
		}

		@Override
		public String getName() {
			try {
				return document.getTitle();
			} catch (RemoteException e) {
				return "<unbekannt>";
			}
		}

		@Override
		public void execute() throws ApplicationException {
			new OpenDocument().handleAction(document);
		}
		
		@Override
		public String getDescription() {
			try {
				return Settings.i18n().tr("{0} - {1}\n{2}", document.getAccount().getName(), document.getRemoteFolder(), document.getTitle());
			} catch (RemoteException e) {
				return null;
			}
		}
		
		@Override
		public RGB getColor() {
			try {
				if (document.getReadOn() == null) {
					return Color.LINK.getSWTColor().getRGB();
				}
			} catch (RemoteException e) {
				// ignore error
			}
			return Color.BLACK.getSWTColor().getRGB();
		}
	}

}
