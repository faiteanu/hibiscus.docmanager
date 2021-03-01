package name.aiteanu.docmanager.search;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import de.willuhn.datasource.rmi.DBIterator;
import name.aiteanu.docmanager.Settings;
import de.willuhn.jameica.search.Result;
import de.willuhn.jameica.search.SearchProvider;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import name.aiteanu.docmanager.gui.action.OpenAccountDetail;
import name.aiteanu.docmanager.rmi.Account;

/**
 * Extension to the jameica search service.
 * If you implement the "SearchProvider" interface, jameica automatically
 * detects the provider. You are now able to search for projects in jameica.
 */
public class AccountSearchProvider implements SearchProvider
{
  @Override
  public String getName()
  {
    return Settings.i18n().tr("Accounts");
  }

  @Override
  public List search(String search) throws RemoteException, ApplicationException
  {
    // We have to return a list of "Result" objects
    List<Result> result = new ArrayList<Result>();
    if (search == null || search.length() < 3)
      return result;
    
    String s = "%" + search.toLowerCase() + "%";
    DBIterator<Account> accounts = Settings.getDBService().createList(Account.class);
    accounts.addFilter("lower(institute) like ? or lower(username) like ? or lower(comment) like ?",new Object[]{s,s,s});
    while (accounts.hasNext())
    {
      result.add(new MyResult(accounts.next()));
    }
    return result;
  }
  
  /**
   * Our implementation of the search result items.
   */
  public class MyResult implements Result
  {
    private Account account = null;
    
    /**
     * ct.
     * @param project
     */
    private MyResult(Account account)
    {
      this.account = account;
    }
    
    /**
     * @see de.willuhn.jameica.search.Result#execute()
     */
    public void execute() throws RemoteException, ApplicationException
    {
      new OpenAccountDetail().handleAction(this.account);
    }

    /**
     * @see de.willuhn.jameica.search.Result#getName()
     */
    public String getName()
    {
      try
      {
        return this.account.getName();
      }
      catch (Exception e)
      {
        Logger.error("unable to determine project name",e);
        return "";
      }
    }
    
  }

}
