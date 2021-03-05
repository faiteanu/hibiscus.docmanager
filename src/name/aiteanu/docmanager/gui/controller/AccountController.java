package name.aiteanu.docmanager.gui.controller;

import java.io.File;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.datasource.rmi.DBService;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Part;
import de.willuhn.jameica.gui.formatter.DateFormatter;
import de.willuhn.jameica.gui.input.DirectoryInput;
import de.willuhn.jameica.gui.input.Input;
import de.willuhn.jameica.gui.input.SelectInput;
import de.willuhn.jameica.gui.input.TextAreaInput;
import de.willuhn.jameica.gui.input.TextInput;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.hbci.HBCI;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import name.aiteanu.docmanager.DocManager;
import name.aiteanu.docmanager.MetaKey;
import name.aiteanu.docmanager.Settings;
import name.aiteanu.docmanager.gui.action.OpenAccountDetail;
import name.aiteanu.docmanager.rmi.Account;


public class AccountController extends AbstractControl
{

	// list of all projects
	private TablePart accountsList;

	// Input fields for the project attributes,
	private TextInput name;
	private TextAreaInput description;
	
	private SelectInput institute; 
	private TextInput userName;

	// this is the currently opened account
	private Account account;

	private DirectoryInput path;

	/**
	 * ct.
	 * @param view this is our view (the welcome screen).
	 */
	public AccountController(AbstractView view)
	{
		super(view);
	}

	/**
	 * Small helper method to get the current project.
	 * @return
	 */
	private Account getAccount()
	{
		if (account != null)
			return account;
		account = (Account) getCurrentObject();
		return account;
	}

	/**
	 * Returns the input field for the project name.
	 * @return input field.
	 * @throws RemoteException
	 */
	public SelectInput getInstitute() throws RemoteException
	{
		if (institute != null)
			return institute;
		
	    List<String> institutes = new LinkedList<String>();
	    institutes.add("DKB");
	    institutes.add("Deka");
	    //groups.add(""); // <Keine Kategorie>
	    //groups.addAll(KontoUtil.getGroups());

	    institute = new SelectInput(institutes, getAccount().getInstitute());
	    institute.setName(Settings.i18n().tr("Institute"));
	    institute.setEditable(false);
		institute.setMandatory(true);
		return institute;
	}
	
	/**
	 * Returns the input field for the project name.
	 * @return input field.
	 * @throws RemoteException
	 */
	public Input getUserName() throws RemoteException
	{
		if (userName != null)
			return userName;
		// "255" is the maximum length for this input field.
		userName = new TextInput(getAccount().getUserName(),255);
		userName.setMandatory(true);
		userName.setName(Settings.i18n().tr("User"));
		return userName;
	}
	
	/**
	 * Returns the input field for the project name.
	 * @return input field.
	 * @throws RemoteException
	 */
	public Input getName() throws RemoteException
	{
		if (name != null)
			return name;
		// "255" is the maximum length for this input field.
		name = new TextInput(getAccount().getName(),255);
		name.setMandatory(true);
		name.setName(Settings.i18n().tr("Label"));
		return name;
	}
	
  //  /**
//   * Returns the input field for the project name.
//   * @return input field.
//   * @throws RemoteException
//   */
//  public Input getName() throws RemoteException
//  {
//    if (name != null)
//      return name;
//    // "255" is the maximum length for this input field.
//    name = new TextInput(getProject().getName(),255);
//    name.setMandatory(true);
//    name.setName(Settings.i18n().tr("Name"));
//    return name;
//  }

  /**
   * Returns the input field for the project description.
   * @return input field.
   * @throws RemoteException
   */
  public Input getDescription() throws RemoteException
  {
    if (description != null)
      return description;
    description = new TextAreaInput(getAccount().getComment());
    description.setName("");
    return description;
  }

  /**
   * Liefert ein Eingabefeld fuer den Ordner, in dem die Dokumente gespeichert werden sollen.
   * @return Eingabefeld fuer den Ordner, in dem die Dokumente gespeichert werden sollen.
   */
  public DirectoryInput getPath() throws RemoteException
  {
    if (this.path != null)
      return this.path;
    
    if(getAccount().getDocumentsPath() == null)
    	getAccount().setDocumentsPath(Application.getPluginLoader().getPlugin(DocManager.class).getResources().getWorkPath() + File.separator + "doc");
    this.path = new DirectoryInput(getAccount().getDocumentsPath());
    this.path.setName(Settings.i18n().tr("Store documents in"));
    return this.path;
  }
  
  
  /**
   * Returns the input field for the project price.
   * @return input field.
   * @throws RemoteException
   */
//  public Input getPrice() throws RemoteException
//  {
//    if (price != null)
//      return price;
//    price = new DecimalInput(getProject().getPrice(), Settings.DECIMALFORMAT);
//    price.setComment(Settings.i18n().tr("{0} per Hour",Settings.CURRENCY));
//    price.setName(Settings.i18n().tr("Price"));
//    
//    // if we change the price, we have to refresh the summary
//    price.addListener(new Listener() {
//      public void handleEvent(Event event)
//      {
//        try
//        {
//          Double d = (Double) getPrice().getValue();
//          if (d == null)
//            return;
//          
//          double p = d.doubleValue();
//          if (Double.isNaN(p))
//            return;
//          
//          double effort = getProject().getEfforts();
//          double sum = effort * p;
//          
//          getEffortSummary().setValue(Settings.DECIMALFORMAT.format(sum));
//        }
//        catch (Exception e)
//        {
//          Logger.error("error while calculating sum",e);
//          Application.getMessagingFactory().sendMessage(new StatusBarMessage(Settings.i18n().tr("Error while calculating summary: {0}",e.getMessage()),StatusBarMessage.TYPE_ERROR));
//        }
//      }
//    });
//    return price;
//  }

  /**
   * Returns the input field for the start date.
   * @return input field.
   * @throws RemoteException
   */
//  public Input getStartDate() throws RemoteException
//  {
//    if (startDate != null)
//      return startDate;
//    
//    Date start = getProject().getStartDate();
//    if (start == null)
//      start = new Date();
//    startDate = new DateInput(start,Settings.DATEFORMAT);
//    startDate.setName(Settings.i18n().tr("Start date"));
//    return startDate;
//  }


  /**
   * Returns the input field for the end date.
   * @return input field.
   * @throws RemoteException
   */
//  public Input getEndDate() throws RemoteException
//  {
//    if (endDate != null)
//      return endDate;
//    
//    endDate = new DateInput(getProject().getEndDate(),Settings.DATEFORMAT);
//    endDate.setName(Settings.i18n().tr("End date"));
//    return endDate;
//  }

	/**
	 * Returns a text label that contains the summary of all efforts in this project.
   * @return label.
   * @throws RemoteException
   */
//  public Input getEffortSummary() throws RemoteException
//	{
//		if (effortSummary != null)
//			return effortSummary;
//
//    double effort = getProject().getEfforts();
//    double sum = effort * getProject().getPrice();
//
//    effortSummary = new LabelInput(Settings.DECIMALFORMAT.format(sum));
//    effortSummary.setName(Settings.i18n().tr("Efforts"));
//    effortSummary.setComment(Settings.i18n().tr("{0} [{1} Hours]",Settings.CURRENCY,Settings.DECIMALFORMAT.format(effort)));
//		return effortSummary;
//	}

  /**
   * Creates a table containing all projects.
   * @return a table with projects.
   * @throws RemoteException
   */
  public Part getAccountsTable() throws RemoteException
  {
    // do we have an already created table?
    if (accountsList != null)
      return accountsList;
   
    // 1) get the dataservice
    DBService service = Settings.getDBService();
    
    // 2) now we can create the project list.
    //    We do not need to specify the implementing class for
    //    the interface "Project". Jameicas Classloader knows
    //    all classes an finds the right implementation automatically. ;)
    DBIterator<Account> accounts = service.createList(Account.class);
    
    // 4) create the table
    accountsList = new TablePart(accounts, new OpenAccountDetail());

    // 5) now we have to add some columns.
    accountsList.addColumn(Settings.i18n().tr("Institute"),"institute"); // "name" is the field name from the sql table.
    accountsList.addColumn(Settings.i18n().tr("User"),"username"); // "name" is the field name from the sql table.
    accountsList.addColumn(Settings.i18n().tr("Label"),"name"); // "name" is the field name from the sql table.

    // 6) the following fields are a date fields. So we add a date formatter. 
    accountsList.addColumn(Settings.i18n().tr("Documents last updated on"),"lastupdate",new DateFormatter(Settings.DATEFORMAT));
    //accountsList.addColumn(Settings.i18n().tr("End date"),"enddate",    new DateFormatter(Settings.DATEFORMAT));

    // 7) calculated project price (price per hour * hours)
    //accountsList.addColumn(Settings.i18n().tr("Efforts"),"summary", new CurrencyFormatter(Settings.CURRENCY,Settings.DECIMALFORMAT));
    accountsList.addColumn(Settings.i18n().tr("Documents"),"documentcount"); // "name" is the field name from the sql table.
		// 8) we are adding a context menu
	//	accountsList.setContextMenu(new ProjectListMenu());
    return accountsList;
  }

  /**
   * This method stores the account using the current values. 
   */
  public void handleStore()
  {
    try
    {

      // get the current project.
      Account account = getAccount();

      // invoke all Setters of this project and assign the current values
      account.setInstitute((String) getInstitute().getValue());
      account.setUserName((String) getUserName().getValue());
      account.setName((String) getName().getValue());
      account.setComment((String) getDescription().getValue());
      account.setDocumentsPath((String)getPath().getValue());
			// we can cast the return value of date input directly to "java.util.Date".
      //account.setEndDate((Date) getEndDate().getValue());
      //account.setStartDate((Date) getStartDate().getValue());

			// the DecimalInput fields returns a Double object
      //Double d = (Double) getPrice().getValue();
      //account.setPrice(d == null ? 0.0 : d.doubleValue());

      // Now, let's store the project
      // The store() method throws ApplicationExceptions if
      // insertCheck() or updateCheck() failed.
      try
      {
        account.store();
        Application.getMessagingFactory().sendMessage(new StatusBarMessage(Settings.i18n().tr("Account stored successfully"),StatusBarMessage.TYPE_SUCCESS));
      }
      catch (ApplicationException e)
      {
        Application.getMessagingFactory().sendMessage(new StatusBarMessage(e.getMessage(),StatusBarMessage.TYPE_ERROR));
      }
    }
    catch (RemoteException e)
    {
      Logger.error("error while storing project",e);
      Application.getMessagingFactory().sendMessage(new StatusBarMessage(Settings.i18n().tr("Error while storing Account: {0}",e.getMessage()),StatusBarMessage.TYPE_ERROR));
    }
  }
}
