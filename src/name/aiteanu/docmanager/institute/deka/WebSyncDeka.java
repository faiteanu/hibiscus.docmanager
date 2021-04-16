package name.aiteanu.docmanager.institute.deka;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import ch.racic.selenium.helper.download.FileData;
import ch.racic.selenium.helper.download.SeleniumDownloadHelper;
import de.derrichter.finance.websync.connector.ChromeDriverWebClientInit;
import de.derrichter.finance.websync.connector.GeckoDriverWebClientInit;
import de.derrichter.finance.websync.utils.webdrivertools.SeleniumUtils;
import de.derrichter.hibiscus.mashup.crossover.callback.WebDialogs;
import de.derrichter.hibiscus.mashup.crossover.utils.SyncPropertiesHelper;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.hbci.messaging.ImportMessage;
import de.willuhn.jameica.plugin.PluginResources;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.ProgressMonitor;
import name.aiteanu.docmanager.DocManager;
import name.aiteanu.docmanager.Settings;
import name.aiteanu.docmanager.callback.Auth;
import name.aiteanu.docmanager.callback.WebLogger;
import name.aiteanu.docmanager.callback.WebProgressMonitor;
import name.aiteanu.docmanager.data.WebFolder;
import name.aiteanu.docmanager.rmi.Account;
import name.aiteanu.docmanager.rmi.Document;

public class WebSyncDeka {

	private static WebDriver seleniumWebDriver = null;
//	private Queue<WebFolder> folderQueue;

	

	public String getShortName() {
		return "Deka Doks";
	}

	public String getLongName() {
		return "DekaBank (Dokumente)";
	}

	public void synchronizeDocuments(Account account, ProgressMonitor monitor) throws ApplicationException {
		boolean isSelfException = false;
		boolean syncHasErrors = false;

//	    try {
//	      if (konto.getMeta("hasAlreadySynced", "FALSE").equals("TRUE")) {
//	        Logger.info("Dieses Konto wird einmal ausgelassen da es in dieser Sitzung bereits synchronisiert wurde");
//	        monitor.log("Dieses Konto wird einmal ausgelassen da es in dieser Sitzung bereits synchronisiert wurde");
//	        konto.setMeta("hasAlreadySynced", "FALSE");
//	        return;
//	      } 
//	    } catch (RemoteException error) {
//	      Logger.error("hasAlreadySynced konnte nicht gesetzt werden", error);
//	      throw new ApplicationException("hasAlreadySynced konnte nicht gesetzt werden");
//	    } 
		boolean successfulLogin = false;
		try {
//	      HBCIDBServiceImpl db;
//	      monitor.setPercentComplete(3);
			boolean appSysProxyUse = Application.getConfig().getUseSystemProxy();
			String appProxyHost = Application.getConfig().getProxyHost();
			int appProxyPort = Application.getConfig().getProxyPort();
			String appHttpsProxyHost = Application.getConfig().getHttpsProxyHost();
			int appHttpsProxyPort = Application.getConfig().getHttpsProxyPort();
			try {
				boolean headless = true; // TODO before checkin
				String osname = System.getProperty("os.name");
				String osarch = System.getProperty("os.arch");
				if (osname.contains("Linux") && osarch.contains("386")) {
					SyncPropertiesHelper.setGeckoDriverPaths(); // changed
					seleniumWebDriver = GeckoDriverWebClientInit.connConfig(false, true, false, WebLogger.class,
							WebProgressMonitor.class, appSysProxyUse, appProxyHost, appProxyPort, appHttpsProxyHost,
							appHttpsProxyPort, false, "", "", headless);
				} else {
					SyncPropertiesHelper.setChromeDriverPaths();
					seleniumWebDriver = ChromeDriverWebClientInit.connConfig(false, true, true, WebLogger.class,
							WebProgressMonitor.class, appSysProxyUse, appProxyHost, appProxyPort, appHttpsProxyHost,
							appHttpsProxyPort, false, "", "", headless);
				}
			} catch (Exception webClientError) {
				isSelfException = true;
				throw new Exception("SeleniumWebDriverInit fehlerhaft: " + webClientError.getMessage());
			}
			seleniumWebDriver = Auth.seleniumLogin(account.getUserName(), monitor, seleniumWebDriver, getShortName(),
					InstitutOptions.LOGO_PATH, InstitutOptions.LOGIN_URL, WebAuth.class, "",
					InstitutOptions.MIN_PASS_LENGTH, InstitutOptions.MAX_PASS_LENGTH);
			Logger.info(getShortName() + "-Login war erfolgreich");
			monitor.log(getShortName() + "-Login war erfolgreich");
			successfulLogin = true;
			monitor.setPercentComplete(30);
			String logUserString = account.getUserName().substring(0, 4) + "*******";
			Logger.info("INFO: es werden nun gleich alle aktiven Deka-Konten zur Anmeldekennung '" + logUserString
					+ "' abgearbeitet ...");
			monitor.log("INFO: es werden nun gleich alle aktiven Deka-Konten zur Anmeldekennung '" + logUserString
					+ "' abgearbeitet ...");

//	      folderQueue = new LinkedList<WebFolder>();

//	      enqueueFolder(new WebFolder("", MAILBOX_URL));

//	      ArrayList<WebFolder> folders = null;
//	      try {
//	    	  folders = WebSyncDkb.getMailboxFolders(seleniumWebDriver, WebLogger.class, WebProgressMonitor.class, WebDialogs.class);
//	    	  for(WebFolder f : folders) {
//	    		  Logger.info(f.getName() + " " + f.getRelativeUrl());
//	    	  }	    	   	   
//	      } catch (Exception error) {
//	    	  isSelfException = true;
//	    	  throw new Exception("Order im Postfach konnten nicht ermittelt werden.");
//	      } 
//	      
//	      HashSet<String> skippedFolders = new HashSet<String>();
////	      skippedFolders.add("Archiv");
//	      skippedFolders.add("Tresor");
////	      
//	      skippedFolders.add("Kontoauszüge");
//	      skippedFolders.add("Kreditkartenabrechnungen");
//	      skippedFolders.add("Mitteilungen");
//	      skippedFolders.add("Steuerbescheinigungen");
//	      skippedFolders.add("Vertragsinformationen");
//	      skippedFolders.add("Wertpapierdokumente");
//	      
//	      ArrayList<WebFolder> foldersToLoad = new ArrayList<WebFolder>();
//	      for(WebFolder f : folders) {
//    		  if(!skippedFolders.contains(f.getName())) {
//    			  foldersToLoad.add(f);
//    		  }
//    	  }	

//	      while(!folderQueue.isEmpty()) {
//	    	  WebFolder folder = folderQueue.poll();
//		      try {
//		    	  parseSubfolders(account, folder, "", seleniumWebDriver, WebLogger.class, WebProgressMonitor.class, WebDialogs.class);
////		  		  String localPath = account.getDocumentsPath(); // res.getWorkPath() + "/doc/DKB";
			downloadDocuments(account, seleniumWebDriver, WebLogger.class, WebProgressMonitor.class, WebDialogs.class);
//		    	   
//		      } catch (Exception error) {
//		    	  isSelfException = true;
//		    	  throw new Exception("Dokumente im Postfach konnten nicht geladen werden. " + folder.getName(), error);
//		      } 
//	      }
		} catch (Exception error) {
			try {
				Logger.debug("Es ist ein Fehler beim Abruf aufgetreten, daher nun ...");
				Auth.resetPassword(monitor, getShortName(), account.getUserName());
				Logger.debug("Passwort-Reset erfolgreich");
			} catch (Exception authError) {
				Logger.error("Passwort-Reset fehlerhaft:", authError);
			}
			syncHasErrors = true;
			if (isSelfException == true)
				throw new ApplicationException(error.getMessage());
			Logger.error(getShortName() + "-Collector verursachte einen Fehler! Stacktrace:", error);
			throw new ApplicationException(error.getMessage());
		} finally {
//	      monitor.setPercentComplete(95);
			if (successfulLogin == true) {
				monitor.log("Web-Logout der aktuellen Online-Sitzung ...");
				try {
					WebAuth.logoutWithSelenium(seleniumWebDriver, null, WebLogger.class, WebProgressMonitor.class,
							WebDialogs.class);
					monitor.log(getShortName() + "-Logout war erfolgreich");
				} catch (Exception error) {
					Logger.error("Logout fehlerhaft; Bitte dem Entwickler im Forum melden!" + error.getMessage());
					monitor.log("Warnung: " + getShortName()
							+ "-Logout war fehlerhaft; Bitte dem Entwickler im Forum melden!");
				}
			} else if (successfulLogin == true) {

			}
			try {
				SeleniumUtils.quitBrowserDriver(seleniumWebDriver, WebLogger.class);
			} catch (Exception error) {
				Logger.error("SeleniumUtils.quitBrowserDriver fehlerhaft:\n", error);
			}
//	      monitor.setPercentComplete(100);
		}
	}

//	protected void enqueueFolder(WebFolder webFolder) {
//		HashSet<String> skippedFolders = new HashSet<String>();
//		// skippedFolders.add("Archiv");
////		skippedFolders.add("Tresor");
////		//	      
////		skippedFolders.add("Kontoauszüge");
////		skippedFolders.add("Kreditkartenabrechnungen");
////		skippedFolders.add("Mitteilungen");
////		skippedFolders.add("Steuerbescheinigungen");
////		skippedFolders.add("Vertragsinformationen");
////		skippedFolders.add("Wertpapierdokumente");
//
//		if (!skippedFolders.contains(webFolder.getName())) {
//			folderQueue.add(webFolder);
//		}
//	}
//
//	public ArrayList<WebFolder> getMailboxFolders(WebDriver seleniumWebDriver, Class<?> externalLogger,
//			Class<?> externalProgressMonitor, Class<?> externalDialogInterface) throws Exception {
//		Method LogInfo = externalLogger.getMethod("info", new Class[] { String.class });
//		Method LogWarn = externalLogger.getMethod("warn", new Class[] { String.class });
//		Method LogDebug = externalLogger.getMethod("debug", new Class[] { String.class });
//		Method LogTrace = externalLogger.getMethod("trace", new Class[] { String.class });
//		Method MonitorLog = externalProgressMonitor.getMethod("log", new Class[] { String.class });
//		final String getLogMethod = "[getMailboxFolders] ";
//		boolean isSelfException = false;
//		WebDriverWait wait1 = new WebDriverWait(seleniumWebDriver, 1L);
//		LogInfo.invoke(LogInfo,
//				new Object[] { InstitutOptions.LOGIDENT + getLogMethod + "Ordner ermitteln ... (GET " + InstitutOptions.MAILBOX_URL + ")" });
//		try {
//			seleniumWebDriver.get(InstitutOptions.MAILBOX_URL);
//			String LOADER_PATH = "//div[@class='ajax_loading' and @stlye='']"; // WebUtils.LOADER_PATH
//			String LOADER_TEXT = "DummyLoaderText"; // WebUtils.LOADER_TEXT
//			SeleniumUtils.waitForPageLoading(seleniumWebDriver, LOADER_PATH, LOADER_TEXT, true, externalLogger);
//			LogDebug.invoke(LogDebug, new Object[] { InstitutOptions.LOGIDENT + getLogMethod + "accountspage: current webdriver hash: "
//					+ seleniumWebDriver.getWindowHandle().hashCode() });
//		} catch (Exception error) {
//			isSelfException = true;
//			throw new Exception("WebDriver-Fehler: " + ExceptionUtils.getStackTrace(error));
//		}
//		String pageLoginAccounts = seleniumWebDriver.getPageSource();
//		try {
//			WebUtils.checkSeleniumResponseHasError(pageLoginAccounts, seleniumWebDriver, externalLogger,
//					externalProgressMonitor, externalDialogInterface);
//		} catch (Exception error) {
//			isSelfException = true;
//			throw new Exception("Fehler auf der Konto" + error.getMessage());
//		}
//
//		ArrayList<WebFolder> folders = new ArrayList<WebFolder>();
//		try {
//			List<WebElement> elements = seleniumWebDriver
//					.findElements(By.xpath("//a[@tid='gotoFolder' and not(contains(@class, 'icons'))]"));
//			LogTrace.invoke(LogTrace, new Object[] { InstitutOptions.LOGIDENT + getLogMethod + " Folders: " + elements.size() });
//			for (WebElement el : elements) {
//				String name = el.getText();
//				if (name.contains("(")) {
//					name = name.substring(0, name.indexOf("(") - 1);
//				}
//				folders.add(new WebFolder(name, el.getAttribute("href")));
//			}
//		} catch (Exception error) {
//			isSelfException = true;
//			throw new Exception("Auslesen der Ordner fehlgeschlagen: " + error.getMessage());
//		}
//		return folders;
//	}
//
//	public boolean parseSubfolders(Account account, WebFolder folder, String localPath, WebDriver seleniumWebDriver,
//			Class<?> externalLogger, Class<?> externalProgressMonitor, Class<?> externalDialogInterface)
//			throws Exception {
//		Method LogInfo = externalLogger.getMethod("info", new Class[] { String.class });
//		Method LogWarn = externalLogger.getMethod("warn", new Class[] { String.class });
//		Method LogDebug = externalLogger.getMethod("debug", new Class[] { String.class });
//		Method LogTrace = externalLogger.getMethod("trace", new Class[] { String.class });
//		Method MonitorLog = externalProgressMonitor.getMethod("log", new Class[] { String.class });
//		String getLogMethod = "[parseSubfolders] ";
//		boolean isSelfException = false;
//		WebDriverWait wait1 = new WebDriverWait(seleniumWebDriver, 1L);
//
////		SeleniumDownloadHelper downloader = new SeleniumDownloadHelper(seleniumWebDriver);
//
////	    LogInfo.invoke(LogInfo, new Object[] { LOGIDENT + getLogMethod + "Gewählte Ordner durchgehen ..." });
////	    for(WebFolder folder : folders) {
//		try {
//			LogInfo.invoke(LogInfo, new Object[] { InstitutOptions.LOGIDENT + getLogMethod + " Ordner: " + folder.getRelativeUrl() });
//			seleniumWebDriver.get(folder.getRelativeUrl());
//			String LOADER_PATH = "//div[@class='ajax_loading' and @stlye='']"; // WebUtils.LOADER_PATH
//			String LOADER_TEXT = "DummyLoaderText"; // WebUtils.LOADER_TEXT
//			SeleniumUtils.waitForPageLoading(seleniumWebDriver, LOADER_PATH, LOADER_TEXT, true, externalLogger);
//		} catch (Exception error) {
//			isSelfException = true;
//			throw new Exception("WebDriver-Fehler: " + ExceptionUtils.getStackTrace(error));
//		}
//		String pageSource = seleniumWebDriver.getPageSource();
//		try {
//			WebUtils.checkSeleniumResponseHasError(pageSource, seleniumWebDriver, externalLogger,
//					externalProgressMonitor, externalDialogInterface);
//		} catch (Exception error) {
//			isSelfException = true;
//			throw new Exception("Fehler in dem Ordner" + error.getMessage());
//		}
//
//		// Find documents in folder
//		try {
//			boolean hasNextPage = false;
//
////		    	DateFormat df = new SimpleDateFormat("dd.MM.yyyy");		    	
//
//			do {
//				// List<WebElement> elements =
//				// seleniumWebDriver.findElements(By.xpath("//a[contains(@class,'iconSpeichern')]"));
//				List<WebElement> elements = seleniumWebDriver.findElements(By.cssSelector(".abaxx-table tr"));
//				LogTrace.invoke(LogTrace, new Object[] { InstitutOptions.LOGIDENT + getLogMethod + " Folders: " + elements.size() });
//
//				for (WebElement el : elements) {
//					// WebElement title = findElement(el, By.cssSelector(".abaxx-aspect-subject"));
//					// // in Unterordnern
//					// if(title == null) {
//					// title = findElement(el, By.cssSelector(".subject")); // direkt im Postfach
//					// heißt das Element anders
//					// }
//					WebElement linkFolder = findElement(el, By.cssSelector(".evt-gotoFolder"));
//
//					if (linkFolder != null) {
//						String titleStr = linkFolder.getText();
//						if (titleStr.contains("(")) { // es kann sein, dass die Anzahl der ungelesenen Dokumente
//														// angezeigt wird, zB "Wertpapierdokumente (3)"
//							titleStr = titleStr.substring(0, titleStr.indexOf("(") - 1);
//						}
//						String url = linkFolder.getAttribute("href");
//						String folderName = ("".equals(folder.getName()) ? "" : folder.getName() + "/") + titleStr;
//						enqueueFolder(new WebFolder(folderName, url));
//					} else {
//						WebElement linkFile = findElement(el, By.cssSelector(".evt-getMailboxAttachment"));
//						if (linkFile != null) {
//							downloadDocument(el, account, folder, localPath, seleniumWebDriver, externalLogger,
//									externalProgressMonitor, externalDialogInterface);
//						}
//					}
//
//				}
//
//				WebElement elementNext = findElement(seleniumWebDriver,
//						By.xpath("//span[@class='pager-navigator-next']//a"));// seleniumWebDriver.findElement(By.xpath("//span[@class='pager-navigator-next']//a"));
//				if (elementNext != null) {
//					hasNextPage = true;
//					elementNext.click();
//					SeleniumUtils.waitForJSandJQueryToLoad(seleniumWebDriver);
//				} else {
//					hasNextPage = false;
//				}
//			} while (hasNextPage);
//		} catch (Exception error) {
//			isSelfException = true;
//			throw new Exception("Auslesen der Ordner fehlgeschlagen: " + error.getMessage());
//		}
//
////	    }
//		return true;
//
//	}

	public boolean downloadDocuments(Account account, WebDriver seleniumWebDriver, Class<?> externalLogger,
			Class<?> externalProgressMonitor, Class<?> externalDialogInterface) throws Exception {
		Method LogInfo = externalLogger.getMethod("info", new Class[] { String.class });
		Method LogWarn = externalLogger.getMethod("warn", new Class[] { String.class });
		Method LogDebug = externalLogger.getMethod("debug", new Class[] { String.class });
		Method LogTrace = externalLogger.getMethod("trace", new Class[] { String.class });
		Method MonitorLog = externalProgressMonitor.getMethod("log", new Class[] { String.class });
		String getLogMethod = "[downloadDocuments] ";
		boolean isSelfException = false;
		WebDriverWait wait1 = new WebDriverWait(seleniumWebDriver, 1L);

		SeleniumDownloadHelper downloader = new SeleniumDownloadHelper(seleniumWebDriver);

		LogInfo.invoke(LogInfo, new Object[] { InstitutOptions.LOGIDENT + getLogMethod + "Gew�hlte Ordner durchgehen ..." });
//	    for(WebFolder folder : folders) {
		try {
			seleniumWebDriver.get(InstitutOptions.MAILBOX_URL);
			String LOADER_PATH = "//div[@class='ajax_loading' and @stlye='']"; // WebUtils.LOADER_PATH
			String LOADER_TEXT = "DummyLoaderText"; // WebUtils.LOADER_TEXT
			SeleniumUtils.waitForPageLoading(seleniumWebDriver, LOADER_PATH, LOADER_TEXT, true, externalLogger);
			LogInfo.invoke(LogInfo, new Object[] { InstitutOptions.LOGIDENT + getLogMethod + " Ordner: " + InstitutOptions.MAILBOX_URL });
		} catch (Exception error) {
			isSelfException = true;
			throw new Exception("WebDriver-Fehler: " + ExceptionUtils.getStackTrace(error));
		}
		String pageLoginAccounts = seleniumWebDriver.getPageSource();
		try {
			WebUtils.checkSeleniumResponseHasError(pageLoginAccounts, seleniumWebDriver, externalLogger,
					externalProgressMonitor, externalDialogInterface);
		} catch (Exception error) {
			isSelfException = true;
			throw new Exception("Fehler auf der Konto" + error.getMessage());
		}

		// Find documents in folder
		try {
//		    	boolean hasNextPage = false;

			DateFormat df = new SimpleDateFormat("dd.MM.yyyy");

//		    	do{
			WebElement mailboxFrame = findElement(seleniumWebDriver, By.xpath("//iframe[@id='depot']"));
			seleniumWebDriver.switchTo().frame(mailboxFrame);
			List<WebElement> elements = seleniumWebDriver
					.findElements(By.xpath("//form[@name='MailboxDocs']//tbody/tr"));
			LogTrace.invoke(LogTrace, new Object[] { InstitutOptions.LOGIDENT + getLogMethod + " Folders: " + elements.size() });

			for (WebElement el : elements) {
				try {
					WebElement created = findElement(el, By.cssSelector(":nth-child(2)"));
					// WebElement title = findElement(el, By.cssSelector(":nth-child(5)"));
					WebElement link = findElement(el, By.cssSelector(":nth-child(5) a"));
					Date createdOn = (created != null ? df.parse(created.getText()) : new Date());
					String titleStr = link.getText();
					String url = link.getAttribute("href");

					DBIterator<Document> existingDocs = Settings.getDBService().createList(Document.class);
					existingDocs.addFilter("accountid = ?", account.getID());
					existingDocs.addFilter("createdon = ?", createdOn);
					existingDocs.addFilter("title = ?", titleStr);
					if (!existingDocs.hasNext()) { // only add document which did not exist in the DB
						try {
							// create new document
							Document doc =  (Document) Settings.getDBService().createObject(Document.class, null);
							doc.setAccount(account);
							doc.setRemoteFolder("Postfach");
							doc.setTitle(titleStr);
							doc.setCreatedOn(createdOn);

							try { // download file and set metadata
								FileData fd = downloader.getFileFromUrlRaw(new URL(url));
								String fileName = fd.getGuessedFilename().replaceAll("[\\\\/:*?\"<>|]", "_");
								if(fileName.contains("documentId")) {
									fileName = fileName.substring(fileName.indexOf("documentId=") + 11) + ".pdf";
								}
								File output = new File(account.getDocumentsPath(), fileName);
								doc.setLocalFolder(output.getParent());
								doc.setFilename(fileName);
								doc.setDownloadedOn(new Date());

								LogInfo.invoke(LogInfo, new Object[] { InstitutOptions.LOGIDENT + getLogMethod + " Storing : " + output.getAbsolutePath() });
								FileUtils.writeByteArrayToFile(output, fd.getData());
								output.setLastModified(doc.getCreatedOn().getTime());

							} catch (Exception ex) {
								MonitorLog.invoke(MonitorLog, new Object[] { InstitutOptions.LOGIDENT + getLogMethod + " error while downloading file : " + ex.getMessage() });
								doc.setComment("Datei konnte nicht von der Deka-Webseite geladen werden. " + ex.getMessage());
							}

							doc.store();

							try
							{
								Application.getMessagingFactory().sendMessage(new ImportMessage(doc));
							}
							catch (Exception ex)
							{
								Logger.error("error while sending import message",ex);
							}
																	
						} catch (RemoteException e) {
							MonitorLog.invoke(MonitorLog, new Object[] {
									InstitutOptions.LOGIDENT + getLogMethod + " error while downloading file : " + e.getMessage() });
							// throw new ApplicationException(Settings.i18n().tr("error while downloading
							// file"),e);
						} catch (Exception e) {
							MonitorLog.invoke(MonitorLog, new Object[] {
									InstitutOptions.LOGIDENT + getLogMethod + " error while downloading file : " + e.getMessage() });
							// throw new ApplicationException(Settings.i18n().tr("error while downloading
							// file"),e);
						}
					} else {
						LogInfo.invoke(LogInfo, new Object[] {
								InstitutOptions.LOGIDENT + getLogMethod + " Document already downloaded : " + created.getText() + " " +  titleStr});
					}
				} catch (NoSuchElementException nse) {
				}
			}

//			    	WebElement elementNext = findElement(seleniumWebDriver, By.xpath("//span[@class='pager-navigator-next']//a"));//seleniumWebDriver.findElement(By.xpath("//span[@class='pager-navigator-next']//a"));
//			    	if(elementNext != null) {
//			    		hasNextPage = true;
//			    		elementNext.click();
//			    		SeleniumUtils.waitForJSandJQueryToLoad(seleniumWebDriver);
//			    	} else {
//			    		hasNextPage = false;
//			    	}
//		    	} while(hasNextPage);
		} catch (Exception error) {
			isSelfException = true;
			throw new Exception("Auslesen der Ordner fehlgeschlagen: " + error.getMessage());
		} finally {
			seleniumWebDriver.switchTo().defaultContent();
		}

//	    }
		return true;

	}

//	public boolean downloadDocument(WebElement elementFile, Account account, WebFolder folder, String localPath,
//			WebDriver seleniumWebDriver, Class<?> externalLogger, Class<?> externalProgressMonitor,
//			Class<?> externalDialogInterface) throws Exception {
//		Method LogInfo = externalLogger.getMethod("info", new Class[] { String.class });
//		Method LogWarn = externalLogger.getMethod("warn", new Class[] { String.class });
//		Method LogDebug = externalLogger.getMethod("debug", new Class[] { String.class });
//		Method LogTrace = externalLogger.getMethod("trace", new Class[] { String.class });
//		Method MonitorLog = externalProgressMonitor.getMethod("log", new Class[] { String.class });
//		String getLogMethod = "[downloadDocuments] ";
//		boolean isSelfException = false;
//		WebDriverWait wait1 = new WebDriverWait(seleniumWebDriver, 1L);
//
//		SeleniumDownloadHelper downloader = new SeleniumDownloadHelper(seleniumWebDriver);
//
////	    LogInfo.invoke(LogInfo, new Object[] { LOGIDENT + getLogMethod + "Gewählte Ordner durchgehen ..." });
//
//		// Find documents in folder
////	    try {
////	    	boolean hasNextPage = false;
//
//		DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
//
//		// do{
//		// List<WebElement> elements =
//		// seleniumWebDriver.findElements(By.xpath("//a[contains(@class,'iconSpeichern')]"));
//		// List<WebElement> elements =
//		// seleniumWebDriver.findElements(By.xpath("//tbody/tr[contains(@class,'mbo-folderview-row')]"));
//		// LogTrace.invoke(LogTrace, new Object[] { LOGIDENT + getLogMethod + " Folders:
//		// " + elements.size() });
//
//		// try {
//		WebElement created = findElement(elementFile, By.cssSelector(".abaxx-aspect-created"));
//		WebElement title = findElement(elementFile, By.cssSelector(".evt-getMailboxAttachment")); // abaxx-aspect-subject
//		WebElement link = findElement(elementFile, By.cssSelector(".iconSpeichern0"));
//		Date createdOn = (created != null ? df.parse(created.getText()) : new Date());
//		String titleStr = title.getText();
//		String url = link.getAttribute("href");
//
//		DBIterator<Document> existingDocs = Settings.getDBService().createList(Document.class);
//		existingDocs.addFilter("accountid = ?", account.getID());
//		existingDocs.addFilter("remotefolder = ?", folder.getName());
//		existingDocs.addFilter("title = ?", titleStr);
//		if (!existingDocs.hasNext()) { // only add document which did not exist in the DB
//			try {
//				FileData fd = downloader.getFileFromUrlRaw(new URL(url));
//				String fileName = fd.getGuessedFilename().replaceAll("[\\\\/:*?\"<>|]", "_");
//				File output = new File(account.getDocumentsPath() + File.separator + folder.getName(), fileName);
//
//				// create new document
//				Document doc = (Document) Settings.getDBService().createObject(Document.class, null);
//				doc.setAccount(account);
//				doc.setRemoteFolder(folder.getName());
//				doc.setTitle(titleStr);
//				doc.setLocalFolder(output.getParent());
//				doc.setFilename(fileName);
//				doc.setCreatedOn(createdOn);
//				doc.setDownloadedOn(new Date());
//				doc.store();
//
//				LogInfo.invoke(LogInfo,
//						new Object[] { InstitutOptions.LOGIDENT + getLogMethod + " Storing : " + output.getAbsolutePath() });
//				FileUtils.writeByteArrayToFile(output, fd.getData());
//				output.setLastModified(doc.getCreatedOn().getTime());
//			} catch (RemoteException e) {
//				MonitorLog.invoke(MonitorLog,
//						new Object[] { InstitutOptions.LOGIDENT + getLogMethod + " error while downloading file : " + e.getMessage() });
//				// throw new ApplicationException(Settings.i18n().tr("error while downloading
//				// file"),e);
//			} catch (Exception e) {
//				MonitorLog.invoke(MonitorLog,
//						new Object[] { InstitutOptions.LOGIDENT + getLogMethod + " error while downloading file : " + e.getMessage() });
//				// throw new ApplicationException(Settings.i18n().tr("error while downloading
//				// file"),e);
//			}
//		}
////	    		}catch (NoSuchElementException nse) {
////	    		}
////	    	}
////
////	    	WebElement elementNext = findElement(seleniumWebDriver, By.xpath("//span[@class='pager-navigator-next']//a"));//seleniumWebDriver.findElement(By.xpath("//span[@class='pager-navigator-next']//a"));
////	    	if(elementNext != null) {
////	    		hasNextPage = true;
////	    		elementNext.click();
////	    		SeleniumUtils.waitForJSandJQueryToLoad(seleniumWebDriver);
////	    	} else {
////	    		hasNextPage = false;
////	    	}
////	    } while(hasNextPage);
////	} catch (Exception error) {
////		isSelfException = true;
////		throw new Exception("Auslesen der Ordner fehlgeschlagen: " + error.getMessage());
////	} 
//
//		// }
//		return true;
//
//	}

	public static WebElement findElement(WebDriver webDriver, By condition) {
		try {
			return webDriver.findElement(condition);
		} catch (NoSuchElementException e) {
			return null;
		}
	}

	public static WebElement findElement(WebElement element, By condition) {
		try {
			return element.findElement(condition);
		} catch (NoSuchElementException e) {
			return null;
		}
	}
}
