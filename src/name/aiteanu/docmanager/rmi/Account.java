package name.aiteanu.docmanager.rmi;

import java.rmi.RemoteException;
import java.util.Date;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.hbci.rmi.HibiscusDBObject;

/**
 * Interface of the business object for accounts. According to the SQL table, we
 * define some getter an setter here.
 * 
 * <pre>
 * CREATE TABLE DOCMANAGER_ACCOUNT (
 *   id IDENTITY(1),
 *   institute varchar(255) NOT NULL,
 *   username varchar(255) NOT NULL,
 *   name varchar(255) NULL,
 *   DocumentsPath varchar(255) NULL,
 *   comment varchar(1000) NULL,
 *   lastupdate timestamp,
 *   UNIQUE (id),
 *   PRIMARY KEY (id)
 * );
 * </pre>
 * 
 * <br>
 * Getters and setters for the primary key (id) are not needed. Every one of the
 * following methods has to throw a RemoteException. <br>
 */
public interface Account extends HibiscusDBObject {
	/**
	 * Returns the project for this account.
	 * 
	 * @return the project.
	 * @throws RemoteException
	 */
	public String getInstitute() throws RemoteException;

	/**
	 * Stores the Project for this account.
	 * 
	 * @param project
	 * @throws RemoteException
	 */
	public void setInstitute(String institute) throws RemoteException;

	/**
	 * Returns the name of this account.
	 * 
	 * @return name of the account.
	 * @throws RemoteException
	 */
	public String getName() throws RemoteException;

	/**
	 * Stores the name of the account.
	 * 
	 * @param name name of the account.
	 * @throws RemoteException
	 */
	public void setName(String name) throws RemoteException;

	/**
	 * Returns the name of this account.
	 * 
	 * @return name of the account.
	 * @throws RemoteException
	 */
	public String getUserName() throws RemoteException;

	/**
	 * Stores the name of the account.
	 * 
	 * @param name name of the account.
	 * @throws RemoteException
	 */
	public void setUserName(String name) throws RemoteException;

	/**
	 * Returns the comment for the account.
	 * 
	 * @return comment.
	 * @throws RemoteException
	 */
	public String getComment() throws RemoteException;

	/**
	 * Stores the account comment.
	 * 
	 * @param comment account comment.
	 * @throws RemoteException
	 */
	public void setComment(String comment) throws RemoteException;
	
	public String getDocumentsPath() throws RemoteException;
	
	public void setDocumentsPath(String path) throws RemoteException;

	public Date getLastUpdateOn() throws RemoteException;

	public void setLastUpdateOn(Date lastUpdateOn) throws RemoteException;

	/**
	 * Get the documents belonging to this account.
	 */
	DBIterator<Document> getDocuments() throws RemoteException;

	/**
	 * Get the number of documents belonging to this account.
	 * @return
	 * @throws RemoteException
	 */
	int getDocumentCount() throws RemoteException;

}
