package name.aiteanu.docmanager.rmi;

import java.rmi.RemoteException;
import java.util.Date;

import de.willuhn.datasource.rmi.DBObject;


/**
 * Interface of the business object for documents.
 * According to the SQL table, we define some getter and setter here.
 * <pre>
 * CREATE TABLE DOCMANAGER_DOCUMENT (
 * --id NUMERIC default UNIQUEKEY('DOCMANAGER_DOCUMENT'),
 * ID IDENTITY(1),
 * AccountID int(4) NOT NULL,
 * RemoteFolder varchar(255) NULL,
 * RemoteID varchar(255) NULL,
 * Title varchar(255) NOT NULL,
 * LocalFolder varchar(255) NULL,
 * Filename varchar(255) NULL,
 * Comment varchar(1000) NULL,
 * CreatedOn timestamp,
 * DownloadedOn timestamp,
 * ReadOn timestamp,
 * UNIQUE (id),
 * PRIMARY KEY (id)
 * 
 * );
 * </pre>
 * <br>Getters and setters for the primary key (id) are not needed.
 * Every one of the following methods has to throw a RemoteException.
 * <br>
 */
public interface Document extends DBObject
{
	public Account getAccount() throws RemoteException;
	public void setAccount(Account account) throws RemoteException;
	public String getRemoteFolder() throws RemoteException;
	public void setRemoteFolder(String name) throws RemoteException;
	public String getRemoteID() throws RemoteException;
	public void setRemoteID(String name) throws RemoteException;
	public String getTitle() throws RemoteException;
	public void setTitle(String name) throws RemoteException;
	public String getLocalFolder() throws RemoteException;
	public void setLocalFolder(String name) throws RemoteException;
	public String getFilename() throws RemoteException;
	public void setFilename(String name) throws RemoteException;
	public String getComment() throws RemoteException;
	public void setComment(String comment) throws RemoteException;
	public Date getCreatedOn() throws RemoteException;
	public void setCreatedOn(Date createdOn) throws RemoteException;
	public Date getDownloadedOn() throws RemoteException;
	public void setDownloadedOn(Date downloadedOn) throws RemoteException;
	public Date getReadOn() throws RemoteException;
	public void setReadOn(Date readOn) throws RemoteException;
}
