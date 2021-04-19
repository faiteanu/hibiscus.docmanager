package name.aiteanu.docmanager.gui.controller;

import java.io.File;
import java.rmi.RemoteException;
import java.util.Date;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.datasource.rmi.DBService;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.input.DateInput;
import de.willuhn.jameica.gui.input.FileInput;
import de.willuhn.jameica.gui.input.Input;
import de.willuhn.jameica.gui.input.SelectInput;
import de.willuhn.jameica.gui.input.TextAreaInput;
import de.willuhn.jameica.gui.input.TextInput;
import de.willuhn.jameica.hbci.messaging.ObjectChangedMessage;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import name.aiteanu.docmanager.Settings;
import name.aiteanu.docmanager.rmi.Account;
import name.aiteanu.docmanager.rmi.Document;


public class DocumentController extends AbstractControl
{

	// Input fields for the document attributes,
	private TextInput title;
	private TextAreaInput description;
	private DateInput createdOnDate;
	private DateInput downloadedOnDate;
	private DateInput readOnDate;
	
	private SelectInput account; 
	private TextInput remoteFolder;
	private FileInput localFilename;

	// this is the currently opened document
	private Document document;

	/**
	 * ct.
	 * @param view this is our view (the welcome screen).
	 */
	public DocumentController(AbstractView view)
	{
		super(view);
	}

	/**
	 * Small helper method to get the current document.
	 * @return
	 */
	private Document getDocument()
	{
		if (document != null)
			return document;
		document = (Document) getCurrentObject();
		return document;
	}

	/**
	 * Returns the input field for the account.
	 * @return input field.
	 * @throws RemoteException
	 */
	public SelectInput getAccount() throws RemoteException
	{
		if (account != null)
			return account;

	    DBService service = Settings.getDBService();
	    DBIterator<Account> accounts = service.createList(Account.class);
	    
	    account = new SelectInput(accounts, getDocument().getAccount());
	    //institute = new SelectInput(institutes, getDocument().getAccount().getInstitute());
	    account.setAttribute("name");
	    account.setName(Settings.i18n().tr("Account"));
	    account.setEditable(false);
		account.setMandatory(true);
		return account;
	}
	
	/**
	 * Returns the input field for the remote folder.
	 * @return input field.
	 * @throws RemoteException
	 */
	public Input getRemoteFolder() throws RemoteException
	{
		if (remoteFolder != null)
			return remoteFolder;
		// "255" is the maximum length for this input field.
		remoteFolder = new TextInput(getDocument().getRemoteFolder(),255);
		remoteFolder.setMandatory(true);
		remoteFolder.setName(Settings.i18n().tr("Remote folder"));
		return remoteFolder;
	}
	
	/**
	 * Returns the input field for the project name.
	 * @return input field.
	 * @throws RemoteException
	 */
	public Input getTitle() throws RemoteException
	{
		if (title != null)
			return title;
		// "255" is the maximum length for this input field.
		title = new TextInput(getDocument().getTitle(),255);
		title.setMandatory(true);
		title.setName(Settings.i18n().tr("Name"));
		return title;
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
    description = new TextAreaInput(getDocument().getComment());
    description.setName("");
    return description;
  }

  public FileInput getLocalFilenameAndFolder() throws RemoteException
  {
	  if (localFilename != null)
		  return localFilename;
	  // "255" is the maximum length for this input field.
	  localFilename = new FileInput(getDocument().getLocalFolder() + File.separator + getDocument().getFilename());
	  localFilename.setMandatory(true);
	  localFilename.setName(Settings.i18n().tr("Local path"));
	  return localFilename;
  }

  /**
   * Returns the input field for the start date.
   * @return input field.
   * @throws RemoteException
   */
  public Input getCreatedOnDate() throws RemoteException
  {
    if (createdOnDate != null)
      return createdOnDate;
    
    Date start = getDocument().getCreatedOn();
    if (start == null)
      start = new Date();
    createdOnDate = new DateInput(start);
    createdOnDate.setName(Settings.i18n().tr("Created on"));
    return createdOnDate;
  }


  /**
   * Returns the input field for the end date.
   * @return input field.
   * @throws RemoteException
   */
  public Input getDownloadedOnDate() throws RemoteException
  {
    if (downloadedOnDate != null)
      return downloadedOnDate;
    
    downloadedOnDate = new DateInput(getDocument().getDownloadedOn());
    downloadedOnDate.setName(Settings.i18n().tr("Downloaded on"));
    return downloadedOnDate;
  }
  
  public Input getReadOnDate() throws RemoteException
  {
    if (readOnDate != null)
      return readOnDate;
    
    readOnDate = new DateInput(getDocument().getReadOn());
    readOnDate.setName(Settings.i18n().tr("Read on"));
    return readOnDate;
  }
  

  /**
   * This method stores the project using the current values. 
   */
  public void handleStore()
  {
	  try
	  {

		  // get the current document.
		  Document document = getDocument();

		  // invoke all Setters of this document and assign the current values
		  document.setAccount((Account)getAccount().getValue());
		  document.setRemoteFolder((String)getRemoteFolder().getValue());
		  document.setTitle((String) getTitle().getValue());
		  document.setComment((String) getDescription().getValue());

		  // we can cast the return value of date input directly to "java.util.Date".
		  document.setCreatedOn((Date) getCreatedOnDate().getValue());
		  document.setDownloadedOn((Date) getDownloadedOnDate().getValue());
		  document.setReadOn((Date)getReadOnDate().getValue());

		  File fullPath = new File((String) getLocalFilenameAndFolder().getValue());
		  document.setLocalFolder(fullPath.getParent());
		  document.setFilename(fullPath.getName());
		  
		  // the DecimalInput fields returns a Double object
		  //      Double d = (Double) getPrice().getValue();
		  //      document.setPrice(d == null ? 0.0 : d.doubleValue());

		  // Now, let's store the project
		  // The store() method throws ApplicationExceptions if
		  // insertCheck() or updateCheck() failed.
		  try
		  {
			  document.store();
			  Application.getMessagingFactory().sendMessage(new StatusBarMessage(Settings.i18n().tr("Document stored successfully"),StatusBarMessage.TYPE_SUCCESS));
			  Application.getMessagingFactory().sendMessage(new ObjectChangedMessage(document));
		  }
		  catch (ApplicationException e)
		  {
			  Application.getMessagingFactory().sendMessage(new StatusBarMessage(e.getMessage(),StatusBarMessage.TYPE_ERROR));
		  }
	  }
	  catch (RemoteException e)
	  {
		  Logger.error("error while storing project",e);
		  Application.getMessagingFactory().sendMessage(new StatusBarMessage(Settings.i18n().tr("Error while storing Document: {0}",e.getMessage()),StatusBarMessage.TYPE_ERROR));
	  }
  }
}
