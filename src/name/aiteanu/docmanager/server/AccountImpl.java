package name.aiteanu.docmanager.server;

import java.rmi.RemoteException;
import java.util.Date;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.datasource.rmi.DBService;
import de.willuhn.jameica.hbci.server.AbstractHibiscusDBObject;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import name.aiteanu.docmanager.Settings;
import name.aiteanu.docmanager.rmi.Account;
import name.aiteanu.docmanager.rmi.Document;

/**
 * Implementation of the Account interface. Look into AccountImpl for more code
 * comments.
 */
public class AccountImpl extends AbstractHibiscusDBObject implements Account {

	/**
	 * ct.
	 * 
	 * @throws RemoteException
	 */
	public AccountImpl() throws RemoteException {
		super();				
	}

	@Override
	protected String getTableName() {
		// this is the sql table name.
		return "DOCMANAGER_ACCOUNT";
	}

	@Override
	public String getPrimaryAttribute() throws RemoteException {
		// our primary attribute is the name.
		return "ID";
	}

	@Override
	protected void deleteCheck() throws ApplicationException {
	}

	@Override
	protected void insertCheck() throws ApplicationException {
		try {
			if (getName() == null || getName().length() == 0)
				throw new ApplicationException(Settings.i18n().tr("Please enter an account name"));
			
			if (getUserName() == null || getUserName().length() == 0)
				throw new ApplicationException(Settings.i18n().tr("Please enter a user name"));
			
			if (getDocumentsPath() == null || getDocumentsPath().length() == 0)
				throw new ApplicationException(Settings.i18n().tr("Please enter a path to store documents"));

//			if (getProject() == null)
//				throw new ApplicationException(Settings.i18n().tr("Please choose a project"));
//
//			if (getProject().isNewObject())
//				throw new ApplicationException(Settings.i18n().tr("Please store project first"));

		} catch (RemoteException e) {
			Logger.error("insert check of account failed", e);
			throw new ApplicationException(Settings.i18n().tr("unable to store account, please check the system log"));
		}
	}

	@Override
	protected void updateCheck() throws ApplicationException {
		// same as insertCheck
		insertCheck();
	}

	@Override
	protected Class<?> getForeignObject(String field) throws RemoteException {
		// the system is able to resolve foreign keys and loads
		// the according objects automatically. You only have to
		// define which class handles which foreign key.
		// if ("project_id".equals(field))
		// return Project.class;
		return null;
	}


	@Override
	public String getName() throws RemoteException {
		return (String) getAttribute("name");
	}

	@Override
	public void setName(String name) throws RemoteException {
		setAttribute("name", name);
	}

	@Override
	public String getComment() throws RemoteException {
		return (String) getAttribute("comment");
	}

	@Override
	public void setComment(String comment) throws RemoteException {
		setAttribute("comment", comment);
	}

	@Override
	public String getInstitute() throws RemoteException {
		return (String) getAttribute("institute");
	}

	@Override
	public void setInstitute(String institute) throws RemoteException {
		setAttribute("institute", institute);
	}

	@Override
	public String getUserName() throws RemoteException {
		return (String) getAttribute("username");
	}

	@Override
	public void setUserName(String username) throws RemoteException {
		setAttribute("username", username);
	}

	@Override
	public Date getLastUpdateOn() throws RemoteException {
		return (Date) getAttribute("lastupdate");
	}

	@Override
	public void setLastUpdateOn(Date lastUpdate) throws RemoteException {
		setAttribute("lastupdate", lastUpdate);
	}
	
	@Override
	public String getDocumentsPath() throws RemoteException {
		return (String) getAttribute("documentspath");
	}

	@Override
	public void setDocumentsPath(String path) throws RemoteException {
		setAttribute("documentspath", path);
	}

	@Override
	public DBIterator<Document> getDocuments() throws RemoteException {
		try {
			// 1) Get the Database Service.
			DBService service = this.getService();

			// you can get the Database Service also via:
			// DBService service = this.getService();

			// 3) We create the task list using getList(Class)
			DBIterator<Document> documents = service.createList(Document.class);

			// 4) we add a filter to only query for tasks with our project id
			documents.addFilter("accountid = " + this.getID());

			return documents;
		} catch (Exception e) {
			throw new RemoteException("unable to load document list", e);
		}
	}

	@Override
	public int getDocumentCount() throws RemoteException {
		DBIterator<Document> documents = getDocuments();
		return documents.size();
	}

	/**
	 * We overwrite the delete method to delete all assigned documents too.
	 * 
	 * @see de.willuhn.datasource.rmi.Changeable#delete()
	 */
	@Override
	public void delete() throws RemoteException, ApplicationException {
		try {
			// we start a new transaction
			// to delete all or nothing
			this.transactionBegin();

			DBIterator<Document> documents = getDocuments();
			while (documents.hasNext()) {
				Document doc = documents.next();
				doc.delete();
			}
			super.delete(); // we delete the account itself

			// everything seems to be ok, lets commit the transaction
			this.transactionCommit();

		} catch (RemoteException re) {
			this.transactionRollback();
			throw re;
		} catch (ApplicationException ae) {
			this.transactionRollback();
			throw ae;
		} catch (Throwable t) {
			this.transactionRollback();
			throw new ApplicationException(Settings.i18n().tr("error while deleting account"), t);
		}
	}

	@Override
	public Object getAttribute(String fieldName) throws RemoteException {
		// You are able to create virtual object attributes by overwriting
		// this method. Just catch the fieldName and invent your own attributes ;)
		if ("documentcount".equals(fieldName)) {
			return getDocumentCount();
		}

		return super.getAttribute(fieldName);
	}


}
