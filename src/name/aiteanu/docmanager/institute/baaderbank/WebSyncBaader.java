package name.aiteanu.docmanager.institute.baaderbank;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Function;

import ch.racic.selenium.helper.download.FileData;
import ch.racic.selenium.helper.download.SeleniumDownloadHelper;
import de.derrichter.finance.websync.connector.ChromeDriverWebClientInit;
import de.derrichter.finance.websync.connector.GeckoDriverWebClientInit;
import de.derrichter.finance.websync.utils.webdrivertools.SeleniumUtils;
import de.derrichter.hibiscus.mashup.crossover.callback.WebDialogs;
import de.derrichter.hibiscus.mashup.crossover.utils.SyncPropertiesHelper;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.ProgressMonitor;
import name.aiteanu.docmanager.Settings;
import name.aiteanu.docmanager.callback.Auth;
import name.aiteanu.docmanager.callback.WebLogger;
import name.aiteanu.docmanager.callback.WebProgressMonitor;
import name.aiteanu.docmanager.rmi.Account;
import name.aiteanu.docmanager.rmi.Document;
import name.aiteanu.docmanager.synchronize.SynchronizeDocuments;

public class WebSyncBaader {

	private static WebDriver seleniumWebDriver = null;


	public String getShortName() {
		return "Baader Bank Doks";
	}

	public String getLongName() {
		return "Baader Bank (Dokumente)";
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
					InstituteOptionsBaader.LOGO_PATH, InstituteOptionsBaader.LOGIN_URL, WebAuth.class, "",
					InstituteOptionsBaader.MIN_PASS_LENGTH, InstituteOptionsBaader.MAX_PASS_LENGTH);
			Logger.info(getShortName() + "-Login war erfolgreich");
			monitor.log(getShortName() + "-Login war erfolgreich");
			successfulLogin = true;
			monitor.setPercentComplete(30);
			String logUserString = account.getUserName().substring(0, 4) + "*******";
			Logger.info("INFO: es werden nun gleich alle aktiven Baader Bank-Konten zur Anmeldekennung '" + logUserString
					+ "' abgearbeitet ...");
			monitor.log("INFO: es werden nun gleich alle aktiven Baader-Konten zur Anmeldekennung '" + logUserString
					+ "' abgearbeitet ...");


			downloadDocuments(account, seleniumWebDriver, WebLogger.class, WebProgressMonitor.class, WebDialogs.class);

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

	public boolean downloadDocuments(Account account, WebDriver seleniumWebDriver, Class<?> externalLogger,
			Class<?> externalProgressMonitor, Class<?> externalDialogInterface) throws Exception {
		Method LogInfo = externalLogger.getMethod("info", new Class[] { String.class });
		Method LogWarn = externalLogger.getMethod("warn", new Class[] { String.class });
		Method LogDebug = externalLogger.getMethod("debug", new Class[] { String.class });
		Method LogTrace = externalLogger.getMethod("trace", new Class[] { String.class });
		Method MonitorLog = externalProgressMonitor.getMethod("log", new Class[] { String.class });
		String getLogMethod = "[downloadDocuments] ";
		boolean isSelfException = false;
		WebDriverWait wait1 = new WebDriverWait(seleniumWebDriver, Duration.ofSeconds(1));

		SeleniumDownloadHelper downloader = new SeleniumDownloadHelper(seleniumWebDriver);

		LogInfo.invoke(LogInfo, new Object[] { InstituteOptionsBaader.LOGIDENT + getLogMethod + "Gewählte Ordner durchgehen ..." });
		try {
			WebElement downloads = findElement(seleniumWebDriver, By.xpath("//ul[contains(@class,'navbar-nav')]//a[contains(@href,'downloadobs')]"));
			downloads.click();
			LogInfo.invoke(LogInfo, new Object[] { InstituteOptionsBaader.LOGIDENT + getLogMethod + " Ordner: " + InstituteOptionsBaader.MAILBOX_URL });
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
			throw new Exception("Fehler auf der Download-Seite: " + error.getMessage());
		}

		// Find documents in folder
		try {
			DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
			DBIterator<Document> existingDocs = Settings.getDBService().createList(Document.class);
			existingDocs.addFilter("accountid = ?", account.getID());
			HashSet<String> existingIds = new HashSet<>();
			while (existingDocs.hasNext()) {
				Document doc = existingDocs.next();
				existingIds.add(doc.getRemoteID());
			}

			List<WebElement> folders = seleniumWebDriver.findElements(By.cssSelector("div.accordion"));
			LogTrace.invoke(LogTrace, new Object[] { InstituteOptionsBaader.LOGIDENT + getLogMethod + " Folders: " + folders.size() });

			for (WebElement folder : folders) {
				try {
					WebElement folderName = findElement(folder, By.cssSelector(".fl"));
					//folderName.click();
					//wait1.until((Function)ExpectedConditions.presenceOfElementLocated(By.cssSelector("aside input.js-username")))
					List<WebElement> rows = folder.findElements(By.cssSelector("div.accordionContent tbody tr "));
					
					for(WebElement el : rows) {
						WebElement link = findElement(el, By.cssSelector(":nth-child(5) a"));
						String url = link.getAttribute("href");
						String remoteId = getQueryParam(url, "bfId");
						if (!existingIds.contains(remoteId)) { // only add document which did not exist in the DB
							WebElement created = findElement(el, By.cssSelector(":nth-child(1)"));
							WebElement title = findElement(el, By.cssSelector(":nth-child(4)"));
							String titleStr = title.getAttribute("innerHTML");
		
	//						DBIterator<Document> existingDocs = Settings.getDBService().createList(Document.class);
	//						existingDocs.addFilter("accountid = ?", account.getID());
	//						existingDocs.addFilter("createdon = ?", createdOn);
	//						existingDocs.addFilter("title = ?", titleStr);
							try {
								Date createdOn = (created != null ? df.parse(created.getAttribute("innerHTML")) : new Date());
								// create new document
								Document doc =  (Document) Settings.getDBService().createObject(Document.class, null);
								doc.setAccount(account);
								doc.setRemoteFolder(folderName.getText());
								doc.setRemoteID(remoteId);
								doc.setTitle(titleStr);
								doc.setCreatedOn(createdOn);
	
								try { // download file and set metadata
									FileData fd = downloader.getFileFromUrlRaw(new URL(url));
									String fileName = fd.getGuessedFilename().replaceAll("[\\\\/:*?\"<>|]", "_");
									File output = new File(account.getDocumentsPath() + File.separator + folderName.getText() + File.separator + fileName);
									doc.setLocalFolder(output.getParent());
									doc.setFilename(fileName);
									doc.setDownloadedOn(new Date());
	
									LogInfo.invoke(LogInfo, new Object[] { InstituteOptionsBaader.LOGIDENT + getLogMethod + " Storing : " + output.getAbsolutePath() });
									FileUtils.writeByteArrayToFile(output, fd.getData());
									output.setLastModified(doc.getCreatedOn().getTime());
	
								} catch (Exception ex) {
									MonitorLog.invoke(MonitorLog, new Object[] { InstituteOptionsBaader.LOGIDENT + getLogMethod + " error while downloading file : " + ex.getMessage() });
									doc.setComment("Datei konnte nicht von der Baader-Webseite geladen werden. " + ex.getMessage());
								}
	
								doc.store();
	
								SynchronizeDocuments.notifyDocumentListeners(doc);
																		
							} catch (RemoteException e) {
								MonitorLog.invoke(MonitorLog, new Object[] {
										InstituteOptionsBaader.LOGIDENT + getLogMethod + " error while downloading file : " + e.getMessage() });
								// throw new ApplicationException(Settings.i18n().tr("error while downloading
								// file"),e);
							} catch (Exception e) {
								MonitorLog.invoke(MonitorLog, new Object[] {
										InstituteOptionsBaader.LOGIDENT + getLogMethod + " error while downloading file : " + e.getMessage() });
								// throw new ApplicationException(Settings.i18n().tr("error while downloading
								// file"),e);
							}
						} else {
//							LogInfo.invoke(LogInfo, new Object[] {
//									InstituteOptionsBaader.LOGIDENT + getLogMethod + " Document already downloaded : " + created.getText() + " " +  titleStr});
							LogInfo.invoke(LogInfo, new Object[] {
									InstituteOptionsBaader.LOGIDENT + getLogMethod + " Document already downloaded : " + remoteId});
						}
					}
				} catch (NoSuchElementException nse) {
				}
			}


		} catch (Exception error) {
			isSelfException = true;
			throw new Exception("Auslesen der Ordner fehlgeschlagen: " + error.getMessage());
		} 

		return true;

	}

	private String getQueryParam(String url, String paramName) {
		int queryStart = url.indexOf("?");
		if (queryStart > 0) {
			String query = url.substring(queryStart + 1);
			String[] params = query.split("&");
			for (String param : params) {
				if (param.startsWith(paramName)) {
					return param.split("=")[1];
				}
			}
		}
		return null;
	}

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
