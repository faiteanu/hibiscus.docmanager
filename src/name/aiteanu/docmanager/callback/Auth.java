package name.aiteanu.docmanager.callback;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import de.derrichter.finance.websync.utils.StringCharUtils;
import de.derrichter.hibiscus.mashup.crossover.callback.WebDialogs;
import de.derrichter.hibiscus.mashup.crossover.callback.WebLogger;
import de.derrichter.hibiscus.mashup.crossover.callback.WebProgressMonitor;
import de.willuhn.jameica.hbci.Settings;
import de.willuhn.jameica.hbci.rmi.Konto;
import de.willuhn.jameica.security.Wallet;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.logging.Logger;
import de.willuhn.util.ProgressMonitor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.openqa.selenium.WebDriver;

public class Auth {
  static Map<String, String> PassCache = new HashMap<>();
  
  static Map<String, String[]> QuestCache = (Map)new HashMap<>();
  
  static Map<String, String[]> AnswerCache = (Map)new HashMap<>();
  
  private static String getPasswort(String login_Benutzer, ProgressMonitor monitor, String institutAlias, String logo, int minPassLength, int maxPassLength) throws Exception {
    boolean isSelfException = false;
    try {
//      String login_Benutzer = konto.getKundennummer();
      Wallet hibiscus_Wallet = Settings.getWallet();
      boolean hibiscus_cachePins = Settings.getCachePin();
      boolean hibiscus_storePins = Settings.getStorePin();
      String wallet_Alias = "docmanager." + institutAlias.toLowerCase() + "." + StringCharUtils.createMD5Hash(login_Benutzer, WebLogger.class);
      String login_Passwort = null;
      monitor.setPercentComplete(8);
      login_Passwort = "";
      if (hibiscus_cachePins == true) {
        login_Passwort = PassCache.get(wallet_Alias);
      } else {
        PassCache.put(wallet_Alias, "");
      } 
      if (hibiscus_storePins == true) {
        if (hibiscus_Wallet.get(wallet_Alias) != null)
          PassCache.put(wallet_Alias, hibiscus_Wallet.get(wallet_Alias).toString()); 
        login_Passwort = PassCache.get(wallet_Alias);
      } else if (hibiscus_Wallet.get(wallet_Alias) != null) {
        hibiscus_Wallet.set(wallet_Alias, null);
      } 
      try {
        if (StringCharUtils.isNullOrEmptyOrNothing(login_Passwort)) {
          String logUserString = login_Benutzer.substring(0, 3) + "*******";
          Logger.info("Passwort für Anmeldekennung " + logUserString + " wird abgefragt ...");
          //login_Passwort = WebDialogs.askPIN(minPassLength, maxPassLength, logo);
          login_Passwort = name.aiteanu.docmanager.callback.WebDialogs.askPassword(minPassLength, maxPassLength, 
        		  "Bitte geben Sie Ihr Passwort / Ihre PIN zum Konto ein.<br>Benutzername: " + login_Benutzer, "Passwort-Eingabe " + login_Benutzer,logo);
        } 
      } catch (OperationCanceledException err) {
        isSelfException = true;
        throw new OperationCanceledException(institutAlias + "-Login fehlgeschlagen! Passwort-Eingabe vom Benuzter abgebrochen (" + err
            .getClass().getSimpleName() + ")");
      } catch (Exception error) {
        isSelfException = true;
        throw new Exception(institutAlias + "-Login fehlgeschlagen! " + ExceptionUtils.getStackTrace(error));
      } 
      return login_Passwort;
    } catch (OperationCanceledException error) {
      throw new OperationCanceledException(error.getMessage());
    } catch (Exception error) {
      if (isSelfException == true)
        throw new Exception(error.getMessage()); 
      Logger.error("Exception bei Auth.getPasswort! Stacktrace:", error);
      throw new Exception("Exception bei Auth.getPasswort! Fehlernachricht: " + error.getMessage());
    } 
  }
  
  private static String setPasswort(String login_Benutzer, ProgressMonitor monitor, String institutAlias, String login_Passwort) throws Exception {
    boolean isSelfException = false;
    try {
//      String login_Benutzer = konto.getKundennummer();
      Wallet hibiscus_Wallet = Settings.getWallet();
      boolean hibiscus_cachePins = Settings.getCachePin();
      boolean hibiscus_storePins = Settings.getStorePin();
      Logger.debug("Einstellung von Hibiscus zum Passwort Zwischenspeichern ist: " + hibiscus_cachePins);
      Logger.debug("Einstellung von Hibiscus zu Passwort in Wallet speichern ist: " + hibiscus_storePins);
      String wallet_Alias = "docmanager." + institutAlias.toLowerCase() + "." + StringCharUtils.createMD5Hash(login_Benutzer, WebLogger.class);
      if (hibiscus_cachePins == true)
        PassCache.put(wallet_Alias, login_Passwort); 
      if (hibiscus_storePins == true)
        hibiscus_Wallet.set(wallet_Alias, login_Passwort); 
    } catch (Exception error) {
      if (isSelfException == true)
        throw new Exception(error.getMessage()); 
      Logger.error("Exception bei Auth.setPasswort! Stacktrace:", error);
      throw new Exception("Exception bei Auth.setPasswort! Fehlernachricht: " + error.getMessage());
    } 
    return login_Passwort;
  }
  
//  public static HtmlPage htmlunitLogin(Konto konto, ProgressMonitor monitor, WebClient mashupWebClient, String institutAlias, String logo, String loginURL, Class<?> WebAuth, String optionalOptions, int minPassLength, int maxPassLength) throws Exception {
//    boolean isSelfException = false;
//    String login_Benutzer = konto.getKundennummer();
//    HtmlPage postLoginPage = null;
//    monitor.log("Web-Login mit HTMLUnit-Engine mit Anmeldekennung " + login_Benutzer.substring(0, 4) + "******* auf " + loginURL + " ...");
//    try {
//      String login_Passwort = null;
//      do {
//        login_Passwort = getPasswort(konto, monitor, institutAlias, logo, minPassLength, maxPassLength);
//        Class[] methodParameters = new Class[7];
//        methodParameters[0] = String.class;
//        methodParameters[1] = String.class;
//        methodParameters[2] = WebClient.class;
//        methodParameters[3] = String.class;
//        methodParameters[4] = Class.class;
//        methodParameters[5] = Class.class;
//        methodParameters[6] = Class.class;
//        Method WebLogin = WebAuth.getMethod("loginWithHTMLUnit", methodParameters);
//        try {
//          postLoginPage = (HtmlPage)WebLogin.invoke(WebLogin, new Object[] { login_Benutzer, login_Passwort, mashupWebClient, optionalOptions, WebLogger.class, WebProgressMonitor.class, WebDialogs.class });
//        } catch (InvocationTargetException terror) {
//          isSelfException = true;
//          throw new Exception("Web-Login mit HTMLUnit fehlgeschlagen! " + terror.getTargetException().getLocalizedMessage());
//        } catch (Exception error) {
//          isSelfException = true;
//          throw new Exception("Web-Login [Invoke] mit HTMLUnit fehlgeschlagen! " + ExceptionUtils.getStackTrace(error));
//        } 
//      } while (postLoginPage == null);
//      setPasswort(konto, monitor, institutAlias, login_Passwort);
//      return postLoginPage;
//    } catch (OperationCanceledException error) {
//      throw new OperationCanceledException(error.getMessage());
//    } catch (Exception error) {
//      if (isSelfException == true)
//        throw new Exception(error.getMessage()); 
//      Logger.error("Exception bei Auth.Login! Stacktrace:", error);
//      throw new Exception("Exception bei Auth.Login! Fehlernachricht: " + error.getMessage());
//    } 
//  }
  
  public static WebDriver seleniumLogin(String login_Benutzer, ProgressMonitor monitor, WebDriver seleniumWebDriver, String institutAlias, String logo, String loginURL, Class<?> webAuth, String optionalOptions, int minPassLength, int maxPassLength) throws Exception {
    boolean isSelfException = false;
    try {
//      String login_Benutzer = konto.getKundennummer();
      monitor.log("Web-Login mit Selenium-WebDriver-Engine mit Anmeldekennung " + login_Benutzer.substring(0, 4) + "******* auf " + loginURL + " ...");
      String login_Passwort = getPasswort(login_Benutzer, monitor, institutAlias, logo, minPassLength, maxPassLength);
      do {
        Class[] methodParameters = new Class[7];
        methodParameters[0] = String.class;
        methodParameters[1] = String.class;
        methodParameters[2] = WebDriver.class;
        methodParameters[3] = String.class;
        methodParameters[4] = Class.class;
        methodParameters[5] = Class.class;
        methodParameters[6] = Class.class;
        Method WebLogin = webAuth.getMethod("loginWithSelenium", methodParameters);
        try {
          seleniumWebDriver = (WebDriver)WebLogin.invoke(WebLogin, new Object[] { login_Benutzer, login_Passwort, seleniumWebDriver, optionalOptions, WebLogger.class, WebProgressMonitor.class, WebDialogs.class });
        } catch (InvocationTargetException terror) {
          isSelfException = true;
          throw new Exception("Web-Login mit Selenium-WebDriver fehlgeschlagen! " + terror.getTargetException().getLocalizedMessage());
        } catch (Exception error) {
          isSelfException = true;
          throw new Exception("Web-Login [Invoke] mit Selenium-WebDriver fehlgeschlagen! " + ExceptionUtils.getStackTrace(error));
        } 
      } while (seleniumWebDriver == null);
      setPasswort(login_Benutzer, monitor, institutAlias, login_Passwort);
      return seleniumWebDriver;
    } catch (OperationCanceledException error) {
      throw new OperationCanceledException(error.getMessage());
    } catch (Exception error) {
      if (isSelfException == true)
        throw new Exception(error.getMessage()); 
      Logger.error("Exception bei Auth.Login! Stacktrace:", error);
      throw new Exception("Exception bei Auth.Login! Fehlernachricht: " + error.getMessage());
    } 
  }
  
//  public static HtmlPage htmlunitLoginQuestion(Konto konto, ProgressMonitor monitor, WebClient htmlunitWebClient, String InstitutAlias, String logo, Class<?> webAuth, HtmlPage postLoginPage) throws Exception {
//    boolean isSelfException = false;
//    try {
//      String loginAntwort;
//      Class[] methodParameters = new Class[4];
//      methodParameters[0] = HtmlPage.class;
//      methodParameters[1] = Class.class;
//      methodParameters[2] = Class.class;
//      methodParameters[3] = Class.class;
//      Method webLoginQuestionGet = webAuth.getMethod("webLoginQuestionGet", methodParameters);
//      String loginQuestion = null;
//      try {
//        loginQuestion = (String)webLoginQuestionGet.invoke(webLoginQuestionGet, new Object[] { postLoginPage, WebLogger.class, WebProgressMonitor.class, WebDialogs.class });
//      } catch (InvocationTargetException terror) {
//        isSelfException = true;
//        throw new Exception("Web-Login (Pre-Phase #2) fehlgeschlagen! " + terror.getTargetException().getLocalizedMessage());
//      } catch (Exception error) {
//        isSelfException = true;
//        throw new Exception("Web-Login (Pre-Phase #2) [Invoke] fehlgeschlagen! " + ExceptionUtils.getStackTrace(error));
//      } 
//      String login_Benutzer = konto.getKundennummer();
//      Wallet hibiscus_Wallet = Settings.getWallet();
//      boolean hibiscus_cachePins = Settings.getCachePin();
//      boolean hibiscus_storePins = Settings.getStorePin();
//      String wallet_Alias = "mashup." + InstitutAlias.toLowerCase() + "." + StringCharUtils.createMD5Hash(konto.getBLZ() + "." + login_Benutzer, WebLogger.class);
//      if (hibiscus_storePins == true) {
//        if (QuestCache.get(login_Benutzer) == null) {
//          Logger.debug("Der Zwischenspeicher des Benutzers fdie Sicherheitsfrage(n) und Antwort(en) ist noch kein Array, wird nun erzeugt ...");
//          QuestCache.put(login_Benutzer, new String[5]);
//          AnswerCache.put(login_Benutzer, new String[5]);
//        } 
//        String[] arrayOfString1 = new String[5];
//        String[] arrayOfString2 = new String[5];
//        for (int j = 0; j < 5; j++) {
//          if (!StringCharUtils.isNullOrEmptyOrNothing((String)hibiscus_Wallet.get(wallet_Alias + ".SecQuestion." + j))) {
//            arrayOfString1[j] = (String)hibiscus_Wallet.get(wallet_Alias + ".SecQuestion." + j);
//            arrayOfString2[j] = (String)hibiscus_Wallet.get(wallet_Alias + ".SecAnswer." + j);
//          } 
//        } 
//        QuestCache.put(login_Benutzer, arrayOfString1);
//        AnswerCache.put(login_Benutzer, arrayOfString2);
//      } else {
//        for (int j = 0; j < 5; j++) {
//          if (!StringCharUtils.isNullOrEmptyOrNothing((String)hibiscus_Wallet.get(wallet_Alias + ".SecQuestion." + j)))
//            hibiscus_Wallet.set(wallet_Alias + ".SecQuestion." + j, null); 
//          if (!StringCharUtils.isNullOrEmptyOrNothing((String)hibiscus_Wallet.get(wallet_Alias + ".SecAnswer." + j)))
//            hibiscus_Wallet.set(wallet_Alias + ".SecAnswer." + j, null); 
//        } 
//      } 
//      boolean newQuestion = true;
//      int answernr = 0;
//      int ansernewsavenr = 0;
//      try {
//        Logger.debug("Der Zwischenspeicher 'QuestCache' des Benutzers ist vom Typ: " + ((String[])QuestCache.get(login_Benutzer)).getClass());
//      } catch (NullPointerException error) {
//        Logger.debug("Der Zwischenspeicher 'QuestCache' des Benutzers ist leer, also Null");
//      } 
//      if (QuestCache.get(login_Benutzer) == null) {
//        Logger.debug("Der Zwischenspeicher des Benutzers der Sicherheitsfrage und Antwort ist noch kein Array, wird nun erzeugt ...");
//        QuestCache.put(login_Benutzer, new String[5]);
//        AnswerCache.put(login_Benutzer, new String[5]);
//      } 
//      String[] lQuest = QuestCache.get(login_Benutzer);
//      Logger.trace("String-Array lQuest erzeugt aus QuestCache des Loginbenuzter enh" + Arrays.toString((Object[])lQuest));
//      String[] lAnsw = AnswerCache.get(login_Benutzer);
//      Logger.trace("String-Array lAnsw erzeugt aus AnswerCache des Loginbenuzter enh" + Arrays.toString((Object[])lAnsw));
//      for (int i = 0; i < 5; i++) {
//        if (!StringCharUtils.isNullOrEmptyOrNothing(lQuest[i]))
//          if (lQuest[i].equals(loginQuestion)) {
//            Logger.trace("Frage aus dem Cache mit der Nummer [" + i + "] sollte der gestellten Frage entsprechen: " + loginQuestion);
//            newQuestion = false;
//            answernr = i;
//          } else {
//            ansernewsavenr = i + 1;
//          }  
//      } 
//      Logger.debug("Ist dies eine neue Frage (true = Ja; false = Nein): " + newQuestion);
//      if (newQuestion == true)
//        answernr = ansernewsavenr; 
//      if (newQuestion == true) {
//        loginAntwort = "";
//      } else {
//        loginAntwort = lAnsw[answernr];
//        Logger.trace("LoginAntwort aus dem Cache zu der Frage mit Nummer [" + answernr + "]: " + loginAntwort);
//      } 
//      boolean enterAnswer = false;
//      do {
//        try {
//          if (StringCharUtils.isNullOrEmptyOrNothing(loginAntwort) || newQuestion == true)
//            loginAntwort = Application.getCallback().askPassword("Login-Phase #2\n\n\nSicherheitsfrage beantworten\nvon Konto " + konto
//                .getBezeichnung() + "\n\n" + loginQuestion); 
//          if (false == loginAntwort.isEmpty())
//            enterAnswer = true; 
//        } catch (Exception err) {
//          isSelfException = true;
//          throw new Exception("Eingabe der Antwort auf die Sicherheitsfrage vom Benuzter abgebrochen ...");
//        } 
//      } while (!enterAnswer);
//      Class[] methodParameters1 = new Class[6];
//      methodParameters1[0] = String.class;
//      methodParameters1[1] = WebClient.class;
//      methodParameters1[2] = HtmlPage.class;
//      methodParameters1[3] = Class.class;
//      methodParameters1[4] = Class.class;
//      methodParameters1[5] = Class.class;
//      Method webLoginQuestionSetWithHtmlUnit = webAuth.getMethod("webLoginQuestionSetWithHtmlUnit", methodParameters1);
//      HtmlPage postLoginQuestionPage = null;
//      try {
//        postLoginQuestionPage = (HtmlPage)webLoginQuestionSetWithHtmlUnit.invoke(webLoginQuestionSetWithHtmlUnit, new Object[] { loginAntwort, htmlunitWebClient, postLoginPage, WebLogger.class, WebProgressMonitor.class, WebDialogs.class });
//      } catch (InvocationTargetException terror) {
//        isSelfException = true;
//        throw new Exception("Web-Login (Phase #2) fehlgeschlagen! " + terror.getTargetException().getLocalizedMessage());
//      } catch (Exception error) {
//        isSelfException = true;
//        throw new Exception("Web-Login (Phase #2) [Invoke] fehlgeschlagen! " + ExceptionUtils.getStackTrace(error));
//      } 
//      if (hibiscus_cachePins == true) {
//        lQuest[answernr] = loginQuestion;
//        lAnsw[answernr] = loginAntwort;
//      } 
//      if (hibiscus_storePins == true) {
//        hibiscus_Wallet.set(wallet_Alias + ".SecQuestion." + answernr, loginQuestion);
//        hibiscus_Wallet.set(wallet_Alias + ".SecAnswer." + answernr, loginAntwort);
//      } 
//      return postLoginQuestionPage;
//    } catch (Exception error) {
//      if (isSelfException == true)
//        throw new Exception(error.getMessage()); 
//      Logger.error("Fehlermeldung/Exception des Systems:", error);
//      throw new Exception("Fehlermeldung des Systems: " + error.getMessage());
//    } 
//  }
//  
//  public static WebDriver seleniumLoginQuestion(Konto konto, ProgressMonitor monitor, WebDriver webDriverClient, String institutAlias, String logo, Class<?> webAuth) throws Exception {
//    boolean isSelfException = false;
//    try {
//      String loginAntwort;
//      Class[] methodParameters = new Class[4];
//      methodParameters[0] = WebDriver.class;
//      methodParameters[1] = Class.class;
//      methodParameters[2] = Class.class;
//      methodParameters[3] = Class.class;
//      Method webLoginQuestionGetWithSelenium = webAuth.getMethod("loginQuestionGetWithSelenium", methodParameters);
//      String loginQuestion = null;
//      try {
//        loginQuestion = (String)webLoginQuestionGetWithSelenium.invoke(webLoginQuestionGetWithSelenium, new Object[] { webDriverClient, WebLogger.class, WebProgressMonitor.class, WebDialogs.class });
//      } catch (InvocationTargetException terror) {
//        isSelfException = true;
//        throw new Exception("Web-Login (Pre-Phase #2) fehlgeschlagen! " + terror.getTargetException().getLocalizedMessage());
//      } catch (Exception error) {
//        isSelfException = true;
//        throw new Exception("Web-Login (Pre-Phase #2) [Invoke] fehlgeschlagen! " + ExceptionUtils.getStackTrace(error));
//      } 
//      String login_Benutzer = konto.getKundennummer();
//      Wallet hibiscus_Wallet = Settings.getWallet();
//      boolean hibiscus_cachePins = Settings.getCachePin();
//      boolean hibiscus_storePins = Settings.getStorePin();
//      String wallet_Alias = "mashup." + institutAlias.toLowerCase() + "." + StringCharUtils.createMD5Hash(konto.getBLZ() + "." + login_Benutzer, WebLogger.class);
//      if (hibiscus_storePins == true) {
//        if (QuestCache.get(login_Benutzer) == null) {
//          Logger.debug("Der Zwischenspeicher des Benutzers fdie Sicherheitsfrage(n) und Antwort(en) ist noch kein Array, wird nun erzeugt ...");
//          QuestCache.put(login_Benutzer, new String[5]);
//          AnswerCache.put(login_Benutzer, new String[5]);
//        } 
//        String[] arrayOfString1 = new String[5];
//        String[] arrayOfString2 = new String[5];
//        for (int j = 0; j < 5; j++) {
//          if (!StringCharUtils.isNullOrEmptyOrNothing((String)hibiscus_Wallet.get(wallet_Alias + ".SecQuestion." + j))) {
//            arrayOfString1[j] = (String)hibiscus_Wallet.get(wallet_Alias + ".SecQuestion." + j);
//            arrayOfString2[j] = (String)hibiscus_Wallet.get(wallet_Alias + ".SecAnswer." + j);
//          } 
//        } 
//        QuestCache.put(login_Benutzer, arrayOfString1);
//        AnswerCache.put(login_Benutzer, arrayOfString2);
//      } else {
//        for (int j = 0; j < 5; j++) {
//          if (!StringCharUtils.isNullOrEmptyOrNothing((String)hibiscus_Wallet.get(wallet_Alias + ".SecQuestion." + j)))
//            hibiscus_Wallet.set(wallet_Alias + ".SecQuestion." + j, null); 
//          if (!StringCharUtils.isNullOrEmptyOrNothing((String)hibiscus_Wallet.get(wallet_Alias + ".SecAnswer." + j)))
//            hibiscus_Wallet.set(wallet_Alias + ".SecAnswer." + j, null); 
//        } 
//      } 
//      boolean newQuestion = true;
//      int answernr = 0;
//      int ansernewsavenr = 0;
//      try {
//        Logger.debug("Der Zwischenspeicher 'QuestCache' des Benutzers ist vom Typ: " + ((String[])QuestCache.get(login_Benutzer)).getClass());
//      } catch (NullPointerException error) {
//        Logger.debug("Der Zwischenspeicher 'QuestCache' des Benutzers ist leer, also Null");
//      } 
//      if (QuestCache.get(login_Benutzer) == null) {
//        Logger.debug("Der Zwischenspeicher des Benutzers der Sicherheitsfrage und Antwort ist noch kein Array, wird nun erzeugt ...");
//        QuestCache.put(login_Benutzer, new String[5]);
//        AnswerCache.put(login_Benutzer, new String[5]);
//      } 
//      String[] lQuest = QuestCache.get(login_Benutzer);
//      Logger.trace("String-Array lQuest erzeugt aus QuestCache des Loginbenuzter enh" + Arrays.toString((Object[])lQuest));
//      String[] lAnsw = AnswerCache.get(login_Benutzer);
//      Logger.trace("String-Array lAnsw erzeugt aus AnswerCache des Loginbenuzter enh" + Arrays.toString((Object[])lAnsw));
//      for (int i = 0; i < 5; i++) {
//        if (!StringCharUtils.isNullOrEmptyOrNothing(lQuest[i]))
//          if (lQuest[i].equals(loginQuestion)) {
//            Logger.trace("Frage aus dem Cache mit der Nummer [" + i + "] sollte der gestellten Frage entsprechen: " + loginQuestion);
//            newQuestion = false;
//            answernr = i;
//          } else {
//            ansernewsavenr = i + 1;
//          }  
//      } 
//      Logger.debug("Ist dies eine neue Frage (true = Ja; false = Nein): " + newQuestion);
//      if (newQuestion == true)
//        answernr = ansernewsavenr; 
//      if (newQuestion == true) {
//        loginAntwort = "";
//      } else {
//        loginAntwort = lAnsw[answernr];
//        Logger.trace("LoginAntwort aus dem Cache zu der Frage mit Nummer [" + answernr + "]: " + loginAntwort);
//      } 
//      boolean enterAnswer = false;
//      do {
//        try {
//          if (StringCharUtils.isNullOrEmptyOrNothing(loginAntwort) || newQuestion == true) {
//            String subAccountText = "";
//            if (!StringCharUtils.isNullOrEmptyOrNothing(konto.getUnterkonto()))
//              subAccountText = "\nUnterkonto: \t" + konto.getUnterkonto(); 
//            String kontoText = "\n" + konto.getBezeichnung() + "\nKontonummer: \t" + konto.getKontonummer() + subAccountText + "\nzur Anmeldekennung: " + konto.getKundennummer() + System.getProperty("line.separator");
//            loginAntwort = WebDialogs.askAnswer("Login-Phase #2\n\n\nSicherheitsfrage beantworten von Konto" + kontoText + "\n\nFrage: " + loginQuestion, logo);
//          } 
//          if (false == loginAntwort.isEmpty())
//            enterAnswer = true; 
//        } catch (Exception err) {
//          isSelfException = true;
//          throw new Exception("Eingabe der Antwort auf die Sicherheitsfrage vom Benuzter abgebrochen ...");
//        } 
//      } while (!enterAnswer);
//      Class[] methodParameters1 = new Class[5];
//      methodParameters1[0] = String.class;
//      methodParameters1[1] = WebDriver.class;
//      methodParameters1[2] = Class.class;
//      methodParameters1[3] = Class.class;
//      methodParameters1[4] = Class.class;
//      Method webLoginQuestionSetWithSelenium = webAuth.getMethod("loginQuestionSetWithSelenium", methodParameters1);
//      try {
//        webDriverClient = (WebDriver)webLoginQuestionSetWithSelenium.invoke(webLoginQuestionSetWithSelenium, new Object[] { loginAntwort, webDriverClient, WebLogger.class, WebProgressMonitor.class, WebDialogs.class });
//      } catch (InvocationTargetException terror) {
//        isSelfException = true;
//        throw new Exception("Web-Login (Phase #2) fehlgeschlagen! " + terror.getTargetException().getLocalizedMessage());
//      } catch (Exception error) {
//        isSelfException = true;
//        throw new Exception("Web-Login (Phase #2) [Invoke] fehlgeschlagen! " + ExceptionUtils.getStackTrace(error));
//      } 
//      if (hibiscus_cachePins == true) {
//        lQuest[answernr] = loginQuestion;
//        lAnsw[answernr] = loginAntwort;
//      } 
//      if (hibiscus_storePins == true) {
//        hibiscus_Wallet.set(wallet_Alias + ".SecQuestion." + answernr, loginQuestion);
//        hibiscus_Wallet.set(wallet_Alias + ".SecAnswer." + answernr, loginAntwort);
//      } 
//      return webDriverClient;
//    } catch (Exception error) {
//      if (isSelfException == true)
//        throw new Exception(error.getMessage()); 
//      Logger.error("Fehlermeldung/Exception des Systems:", error);
//      throw new Exception("Fehlermeldung des Systems: " + error.getMessage());
//    } 
//  }
  
  public static void resetPassword(ProgressMonitor monitor, String institutAlias, String login_Benutzer) throws Exception {
    boolean isSelfException = false;
    try {
      Logger.info("Passwort-Reset zur Anmeldekennung " + login_Benutzer.substring(0, 4) + "******* und Institut: " + institutAlias + " []");
      Wallet hibiscus_Wallet = Settings.getWallet();
      boolean hibiscus_cachePins = Settings.getCachePin();
      boolean hibiscus_storePins = Settings.getStorePin();
      String wallet_Alias = "mashup." + institutAlias.toLowerCase() + "." + StringCharUtils.createMD5Hash(login_Benutzer, WebLogger.class);
      if (hibiscus_cachePins == true) {
        PassCache.put(wallet_Alias, null);
        QuestCache.put(login_Benutzer, null);
        AnswerCache.put(login_Benutzer, null);
      } 
      if (hibiscus_storePins == true) {
        hibiscus_Wallet.set(wallet_Alias, null);
        for (int i = 0; i < 5; i++) {
          hibiscus_Wallet.set(wallet_Alias + ".SecQuestion." + i, null);
          hibiscus_Wallet.set(wallet_Alias + ".SecAnswer." + i, null);
        } 
      } 
    } catch (Exception error) {
      if (isSelfException == true)
        throw new Exception(error.getMessage()); 
      Logger.error("Fehlermeldung/Exception des Systems:", error);
      throw new Exception("Fehlermeldung des Systems: " + error.getMessage());
    } 
  }
}
