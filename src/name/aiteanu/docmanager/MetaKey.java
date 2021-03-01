package name.aiteanu.docmanager;

import java.io.File;
import java.rmi.RemoteException;

import de.willuhn.jameica.hbci.rmi.HibiscusDBObject;
import de.willuhn.jameica.system.Application;

/**
 * Listet bekannte Meta-Keys, die zu Fachobjekten gespeichert werden koennen.
 */
public enum MetaKey
{
 
  /**
   * Datum des letzten Abrufs der Dokumente im PDF-Format.
   */
  DOCUMENTS_INTERVAL_LAST("documents.interval.last","Datum des letzten Abrufs",null),

  /**
   * Ordner, in dem die Dokumente erstellt werden sollen.
   */
  DOCUMENTS_STORE_PATH("documents.store.path","Store documents in",Application.getPluginLoader().getPlugin(DocManager.class).getResources().getWorkPath() + File.separator + "doc"),


  ;

  private String name         = null;
  private String description  = null;
  private String defaultValue = null;
  
  /**
   * ct.
   * @param name
   * @param description
   * @param defaultValue
   */
  private MetaKey(String name, String description, String defaultValue)
  {
    this.name         = name;
    this.description  = description;
    this.defaultValue = defaultValue;
  }
  
  /**
   * Liefert den Namen des Meta-Keys.
   * @return der Name des Meta-Keys.
   */
  public String getName()
  {
    return this.name;
  }
  
  /**
   * Liefert einen optionalen Beschreibungstext zu dem Meta-Key.
   * @return optionaler Beschreibungstext zu dem Meta-Key.
   */
  public String getDescription()
  {
    return this.description;
  }
  
  /**
   * Liefert den Default-Wert.
   * @return der Default-Wert.
   */
  public String getDefault()
  {
    return this.defaultValue;
  }
  
  /**
   * Liefert den Wert des Meta-Keys fuer das Objekt.
   * @param o das Objekt.
   * @return der Wert des Meta-Keys oder der Default-Wert, wenn kein Wert existiert.
   * @throws RemoteException
   */
  public String get(HibiscusDBObject o) throws RemoteException
  {
    return this.get(o,null);
  }
  
  /**
   * Speichert den Wert des Meta-Keys fuer das Objekt.
   * @param o das Objekt.
   * @param value der Wert des Meta-Keys.
   * @throws RemoteException
   */
  public void set(HibiscusDBObject o, String value) throws RemoteException
  {
    this.set(o,null,value);
  }
  
  /**
   * Liefert den Wert des Meta-Keys fuer das Objekt.
   * @param o das Objekt.
   * @param suffix optionaler Suffix, um verschiedene Varianten des Meta-Key verwenden zu koennen.
   * @return der Wert des Meta-Keys oder der Default-Wert, wenn kein Wert existiert.
   * @throws RemoteException
   */
  public String get(HibiscusDBObject o, String suffix) throws RemoteException
  {
    String key = this.name;
    if (suffix != null)
      key = key + "." + suffix;
    return o.getMeta(key,this.defaultValue);
  }
  
  /**
   * Speichert den Wert des Meta-Keys fuer das Objekt.
   * @param o das Objekt.
   * @param suffix optionaler Suffix, um verschiedene Varianten des Meta-Key verwenden zu koennen.
   * @param value der Wert des Meta-Keys.
   * @throws RemoteException
   */
  public void set(HibiscusDBObject o, String suffix, String value) throws RemoteException
  {
    String key = this.name;
    if (suffix != null)
      key = key + "." + suffix;
    o.setMeta(key,value);
  }

}


