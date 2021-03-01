
package name.aiteanu.docmanager.search;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.search.Result;
import de.willuhn.jameica.search.SearchProvider;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import name.aiteanu.docmanager.Settings;
import name.aiteanu.docmanager.gui.action.OpenDocumentDetail;
import name.aiteanu.docmanager.rmi.Document;

/**
 * Extension to the jameica search service.
 * If you implement the "SearchProvider" interface, jameica automatically
 * detects the provider. You are now able to search for tasks in jameica.
 */
public class DocumentSearchProvider implements SearchProvider
{
  /**
   * @see de.willuhn.jameica.search.SearchProvider#getName()
   */
  public String getName()
  {
    return Settings.i18n().tr("Documents");
  }

  /**
   * @see de.willuhn.jameica.search.SearchProvider#search(java.lang.String)
   */
  public List search(String search) throws RemoteException, ApplicationException
  {
    // We have to return a list of "Result" objects
    List<Result> result = new ArrayList<Result>();
    if (search == null || search.length() < 3)
      return result;
    
    String s = "%" + search.toLowerCase() + "%";
    DBIterator<Document> documents = Settings.getDBService().createList(Document.class);
    documents.addFilter("lower(remotefolder) like ? or lower(title) like ? or lower(comment) like ?",new Object[]{s,s,s});
    while (documents.hasNext())
    {
      result.add(new MyResult(documents.next()));
    }
    return result;
  }
  
  /**
   * Our implementation of the search result items.
   */
  public class MyResult implements Result
  {
    private Document document = null;
    
    /**
     * ct.
     * @param task
     */
    private MyResult(Document task)
    {
      this.document = task;
    }
    
    /**
     * @see de.willuhn.jameica.search.Result#execute()
     */
    public void execute() throws RemoteException, ApplicationException
    {
      new OpenDocumentDetail().handleAction(this.document);
    }

    /**
     * @see de.willuhn.jameica.search.Result#getName()
     */
    public String getName()
    {
      try
      {
        return this.document.getAccount().getName() + " " + this.document.getTitle();
      }
      catch (Exception e)
      {
        Logger.error("unable to determine task name",e);
        return "";
      }
    }
    
  }

}

