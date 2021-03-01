package name.aiteanu.docmanager.server;

import java.rmi.RemoteException;
import java.util.Date;

import de.willuhn.datasource.db.AbstractDBObject;
import de.willuhn.datasource.rmi.ObjectNotFoundException;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import name.aiteanu.docmanager.Settings;
import name.aiteanu.docmanager.rmi.Account;
import name.aiteanu.docmanager.rmi.Document;

/**
 * Implementation of the task interface. Look into ProjectImpl for more code
 * comments.
 */
public class DocumentImpl extends AbstractDBObject implements Document {

	/**
	 * ct.
	 * 
	 * @throws RemoteException
	 */
	public DocumentImpl() throws RemoteException {
		super();
	}

	@Override
	protected String getTableName() {
		// this is the sql table name.
		return "DOCMANAGER_DOCUMENT";
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
			if (getTitle() == null || getTitle().length() == 0)
				throw new ApplicationException(Settings.i18n().tr("Please enter a title"));

//			if (getProject() == null)
//				throw new ApplicationException(Settings.i18n().tr("Please choose a project"));
//
//			if (getProject().isNewObject())
//				throw new ApplicationException(Settings.i18n().tr("Please store project first"));

		} catch (RemoteException e) {
			Logger.error("insert check of project failed", e);
			throw new ApplicationException(Settings.i18n().tr("unable to store project, please check the system log"));
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
		if ("accountid".equals(field))
			return Account.class;
		return null;
	}

	@Override
	public Account getAccount() throws RemoteException {
		// Yes, we can cast this directly to Account, because getForeignObject(String)
		// contains the mapping for this attribute.
		try {
			return (Account) getAttribute("accountid");
		} catch (ObjectNotFoundException e) {
			return null;
		}
	}

	@Override
	public void setAccount(Account account) throws RemoteException {
		// same here
		setAttribute("accountid", account);
	}

	@Override
	public String getRemoteFolder() throws RemoteException {
		return (String) getAttribute("remotefolder");
	}

	@Override
	public void setRemoteFolder(String remotefolder) throws RemoteException {
		setAttribute("remotefolder", remotefolder);
	}

	@Override
	public String getTitle() throws RemoteException {
		return (String) getAttribute("title");
	}

	@Override
	public void setTitle(String title) throws RemoteException {
		setAttribute("title", title);
	}

	@Override
	public String getLocalFolder() throws RemoteException {
		return (String) getAttribute("localfolder");
	}

	@Override
	public void setLocalFolder(String localfolder) throws RemoteException {
		setAttribute("localfolder", localfolder);
	}

	@Override
	public String getFilename() throws RemoteException {
		return (String) getAttribute("filename");
	}

	@Override
	public void setFilename(String filename) throws RemoteException {
		setAttribute("filename", filename);
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
	public Date getCreatedOn() throws RemoteException {
		return (Date) getAttribute("createdon");
	}

	@Override
	public void setCreatedOn(Date createdon) throws RemoteException {
		setAttribute("createdon", createdon);
	}

	@Override
	public Date getDownloadedOn() throws RemoteException {
		return (Date) getAttribute("downloadedon");
	}

	@Override
	public void setDownloadedOn(Date downloadedon) throws RemoteException {
		setAttribute("downloadedon", downloadedon);
	}

	@Override
	public Date getReadOn() throws RemoteException {
		return (Date) getAttribute("readon");
	}

	@Override
	public void setReadOn(Date readon) throws RemoteException {
		setAttribute("readon", readon);
	}
}
