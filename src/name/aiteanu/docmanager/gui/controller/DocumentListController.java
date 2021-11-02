package name.aiteanu.docmanager.gui.controller;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;

import de.willuhn.datasource.GenericObject;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.datasource.rmi.DBService;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.formatter.DateFormatter;
import de.willuhn.jameica.gui.formatter.Formatter;
import de.willuhn.jameica.gui.formatter.TableFormatter;
import de.willuhn.jameica.gui.input.CheckboxInput;
import de.willuhn.jameica.gui.input.Input;
import de.willuhn.jameica.gui.input.SelectInput;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.gui.util.Font;
import de.willuhn.jameica.gui.util.SWTUtil;
import de.willuhn.jameica.hbci.gui.input.DateFromInput;
import de.willuhn.jameica.hbci.gui.input.DateToInput;
import de.willuhn.jameica.hbci.gui.input.InputCompat;
import de.willuhn.jameica.hbci.gui.input.RangeInput;
import de.willuhn.jameica.hbci.messaging.ObjectChangedMessage;
import de.willuhn.jameica.hbci.messaging.ObjectMessage;
import de.willuhn.jameica.hbci.server.Range;
import de.willuhn.jameica.messaging.Message;
import de.willuhn.jameica.messaging.MessageConsumer;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import name.aiteanu.docmanager.Settings;
import name.aiteanu.docmanager.gui.action.OpenDocument;
import name.aiteanu.docmanager.gui.menu.DocumentListMenu;
import name.aiteanu.docmanager.rmi.Account;
import name.aiteanu.docmanager.rmi.Document;


public class DocumentListController extends AbstractControl
{
	protected static Map<String, Object> cache = new HashMap<String, Object>();

	// list of all documents
	private TablePart documentsList;
	protected final static de.willuhn.jameica.system.Settings settings = new de.willuhn.jameica.system.Settings(DocumentListController.class);
	// Cache fuer die Filter-Einstellungen des Users fuer die Dauer der Sitzung.

	// Input fields for the document filter

	private Input from                    = null;
	private Input to                      = null;
	private RangeInput range              = null;
	private CheckboxInput unread          = null;	
	private SelectInput account; 

	private Listener listener             = null;

	private MessageConsumer mcChanged = null;

	/**
	 * ct.
	 * @param view this is our view (the welcome screen).
	 */
	public DocumentListController(AbstractView view)
	{
		super(view);

		this.listener = new Listener() {
			public void handleEvent(Event event) {
				// Wenn das event "null" ist, kann es nicht von SWT ausgeloest worden sein
				// sondern manuell von uns. In dem Fall machen wir ein forciertes Update
				// - ohne zu beruecksichtigen, ob in den Eingabe-Feldern wirklich was
				// geaendert wurde
				handleReload(event == null);
			}
		};

		this.mcChanged = new DocumentChangedMessageConsumer();

		Application.getMessagingFactory().registerMessageConsumer(this.mcChanged);
	}

	/**
	 * Returns the input field for the account name.
	 * @return input field.
	 * @throws RemoteException
	 */
	public SelectInput getAccount() throws RemoteException
	{
		if (account != null)
			return account;

		DBService service = Settings.getDBService();
		DBIterator<Account> accounts = service.createList(Account.class);

		account = new SelectInput(accounts, (Account)cache.get("documents.filter.account")); // getDocument().getAccount()
		//institute = new SelectInput(institutes, getDocument().getAccount().getInstitute());
		account.setAttribute("name");
		account.setName(Settings.i18n().tr("Account"));
		account.setEditable(false);
		account.setMandatory(false);
		account.setPleaseChoose(Settings.i18n().tr("<All accounts>"));
		return account;
	}


	public DBIterator<Document> getDocuments() throws RemoteException
	{
		Account acc = (Account) this.getAccount().getValue();
		Date start = (Date) this.getFrom().getValue();
		Date end = (Date) this.getTo().getValue();
		Boolean onlyUnread = (Boolean) this.getUnread().getValue();

		// Werte speichern
		cache.put("documents.filter.account", acc);
		settings.setAttribute("documents.filter.unread", onlyUnread);

		DBService service = Settings.getDBService();
		DBIterator<Document> documents = service.createList(Document.class);
		if(acc != null)
			documents.addFilter("accountid = ?", acc.getID());
		documents.addFilter("createdon >= ?", start);
		documents.addFilter("createdon <= ?", end);
		if(onlyUnread)
			documents.addFilter("readon is null");

		documents.setOrder("ORDER BY createdon DESC");
		return documents;
	}

	public TablePart getDocumentsTable() throws RemoteException
	{
		// do we have an already created table?
		if (documentsList != null)
			return documentsList;

		// 1) get the dataservice
		//DBService service = Settings.getDBService();

		// 2) now we can create the project list.
		//    We do not need to specify the implementing class for
		//    the interface "Project". Jameicas Classloader knows
		//    all classes an finds the right implementation automatically. ;)
		DBIterator<Document> documents = getDocuments(); //service.createList(Document.class);

		// 4) create the table
		documentsList = new TablePart(documents, new OpenDocument());

		// 5) now we have to add some columns.
		documentsList.addColumn(Settings.i18n().tr("Account"),"accountid", new Formatter() {
			public String format(Object o)
			{
				if (o == null)
					return null;
				try
				{
					Account account = (Account) o;        
					return account.getName();
				}
				catch (Exception e)
				{
					Logger.error("error while formatting account",e);
					return o.toString();
				}
			}
		}); // "name" is the field name from the sql table.
		documentsList.addColumn(Settings.i18n().tr("Remote folder"),"remotefolder"); // "name" is the field name from the sql table.
		documentsList.addColumn(Settings.i18n().tr("Name"),"title"); // "name" is the field name from the sql table.

		// 6) the following fields are a date fields. So we add a date formatter. 
		documentsList.addColumn(Settings.i18n().tr("Created on"),"createdon",new DateFormatter(Settings.DATEFORMAT));
		//documentsList.addColumn(Settings.i18n().tr("Geladen am"),"downloadedon",new DateFormatter(Settings.DATEFORMAT));
		documentsList.addColumn(Settings.i18n().tr("Read on"),"readon",new DateFormatter(Settings.DATEFORMAT));

		// 7) calculated project price (price per hour * hours)
		//accountsList.addColumn(Settings.i18n().tr("Efforts"),"summary", new CurrencyFormatter(Settings.CURRENCY,Settings.DECIMALFORMAT));
		//documentsList.addColumn(Settings.i18n().tr("Dateiname"),"filename"); // "name" is the field name from the sql table.
		documentsList.addColumn(Settings.i18n().tr("Comment"),"comment"); // "name" is the field name from the sql table.
		// 8) we are adding a context menu
		documentsList.setContextMenu(new DocumentListMenu());

		documentsList.setFormatter(new TableFormatter() {		
			@Override
			public void format(TableItem item) {
				try {
					Document doc = (Document)item.getData();
					if(doc.getFilename().toLowerCase().endsWith(".pdf")) {
						item.setImage(2, SWTUtil.getImage("application-pdf.png"));
					} else if(doc.getFilename().toLowerCase().endsWith(".csv")) {
						item.setImage(2, SWTUtil.getImage("application-csv.png"));
					}
					item.setFont(doc.getReadOn() == null ? Font.BOLD.getSWTFont() : Font.DEFAULT.getSWTFont());
				} catch (RemoteException e) {
				}
			}
		});

		documentsList.setMulti(true);
		documentsList.setRememberOrder(true);
		documentsList.setRememberColWidths(true);
		documentsList.setRememberState(true);

		return documentsList;
	}

	/**
	 * Liefert das Eingabe-Datum fuer das Start-Datum.
	 * @return Eingabe-Feld.
	 */
	public synchronized Input getFrom()
	{
		if (this.from != null)
			return this.from;

		this.from = new DateFromInput(null,"documents.filter.from");
		this.from.setName(Settings.i18n().tr("From"));
		this.from.setComment(null);
		this.from.addListener(this.listener);
		return this.from;
	}

	/**
	 * Liefert das Eingabe-Datum fuer das End-Datum.
	 * @return Eingabe-Feld.
	 */
	public synchronized Input getTo()
	{
		if (this.to != null)
			return this.to;

		this.to = new DateToInput(null,"documents.filter.to");
		this.to.setName(Settings.i18n().tr("to"));
		this.to.setComment(null);
		this.to.addListener(this.listener);
		return this.to;
	}

	/**
	 * Liefert eine Auswahl mit Zeit-Presets.
	 * @return eine Auswahl mit Zeit-Presets.
	 */
	public RangeInput getRange()
	{
		if (this.range != null)
			return this.range;

		this.range = new RangeInput(this.getFrom(),this.getTo(),Range.CATEGORY_AUSWERTUNG,"documents.filter.range");
		this.range.addListener(new Listener()
		{
			public void handleEvent(Event event)
			{
				if (range.getValue() != null && range.hasChanged())
					handleReload(true);
			}
		});

		return this.range;
	}

	/**
	 * Liefert eine Checkbox, um nur die ungelesenene Kontoauszuege anzuzeigen.
	 * @return Checkbox.
	 */
	public CheckboxInput getUnread()
	{
		if (this.unread != null)
			return this.unread;

		this.unread = new CheckboxInput(settings.getBoolean("documents.filter.unread",false));
		this.unread.setName(Settings.i18n().tr("Show only unread documents"));
		this.unread.addListener(this.listener);
		return this.unread;
	}

	/**
	 * Aktualisiert die Tabelle der angezeigten Daten.
	 * Die Aktualisierung geschieht um einige Millisekunden verzoegert,
	 * damit ggf. schnell aufeinander folgende Events gebuendelt werden.
	 * @param force true, wenn die Daten auch dann aktualisiert werden sollen,
	 * wenn an den Eingabe-Feldern nichts geaendert wurde.
	 */
	public synchronized void handleReload(boolean force)
	{
		try
		{
			final Object account   = getAccount().getValue();
			final Date dfrom     = (Date) getFrom().getValue();
			final Date dto       = (Date) getTo().getValue();
			final Boolean unread = (Boolean) getUnread().getValue();
			//    final Boolean inclusiveFilter = (Boolean) getInclusiveFilter().getValue();

			if (!force)
			{
				// Wenn es kein forcierter Reload ist, pruefen wir,
				// ob sich etwas geaendert hat oder Eingabe-Fehler
				// vorliegen
				if (!hasChanged())
					return;

				if (dfrom != null && dto != null && dfrom.after(dto))
				{
					GUI.getView().setErrorText(Settings.i18n().tr("End-Datum muss sich nach dem Start-Datum befinden"));
					return;
				}
			}

			GUI.startSync(new Runnable() //Sanduhr anzeigen
					{
				public void run()
				{
					try
					{
						TablePart table = getDocumentsTable();
						table.removeAll();

						DBIterator<Document> documents = getDocuments();
						while (documents.hasNext())
							table.addItem(documents.next());
						//            // Sortierung wiederherstellen
						table.restoreState();
						table.sort();
					}
					catch (Exception e)
					{
						Logger.error("error while reloading table",e);
						Application.getMessagingFactory().sendMessage(new StatusBarMessage(Settings.i18n().tr("Fehler beim Aktualisieren der Tabelle"), StatusBarMessage.TYPE_ERROR));
					}
				}
					});
		}
		catch (Exception e)
		{
			Logger.error("error while reloading data",e);
			Application.getMessagingFactory().sendMessage(new StatusBarMessage(Settings.i18n().tr("Fehler beim Aktualisieren der Tabelle"), StatusBarMessage.TYPE_ERROR));
		}
	}

	/**
	 * Prueft, ob seit der letzten Aktion Eingaben geaendert wurden.
	 * Ist das nicht der Fall, muss die Tabelle nicht neu geladen werden.
	 * @return true, wenn sich wirklich was geaendert hat.
	 */
	protected boolean hasChanged()
	{
		//return InputCompat.valueHasChanged(kontoAuswahl, from, to, unread, inclusiveFilter);
		return InputCompat.valueHasChanged(account, from, to, unread);
	}




	/**
	 * Hilfsklasse damit wir ueber geaenderte Dokumente informiert werden.
	 */
	public class DocumentChangedMessageConsumer implements MessageConsumer
	{

		@Override
		public Class[] getExpectedMessageTypes()
		{
			return new Class[]{
					ObjectChangedMessage.class
			};
		}

		@Override
		public void handleMessage(Message message) throws Exception {
			if (message == null)
				return;

			final GenericObject o = ((ObjectMessage) message).getObject();

			if (o == null || !(o instanceof Document))
				return;

			// Update auf dem GUI-Thread starten
			GUI.startSync(new Runnable() {
				public void run() {
					try {
						documentsList.updateItem(o, o);
					} catch (RemoteException e) {
						Logger.error("error while updating document list", e);
						Application.getMessagingFactory().sendMessage(new StatusBarMessage(Settings.i18n().tr("Error while updating document list: {0}", e.getMessage()),
								StatusBarMessage.TYPE_ERROR));
					}
				}
			});

		}

		@Override
		public boolean autoRegister()
		{
			return false;
		}
	}


}
