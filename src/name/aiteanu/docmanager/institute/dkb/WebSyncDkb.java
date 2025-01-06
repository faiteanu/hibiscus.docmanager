package name.aiteanu.docmanager.institute.dkb;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.http.HttpHeaders;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.racic.selenium.helper.download.FileData;
import ch.racic.selenium.helper.download.SeleniumDownloadHelper;
import de.derrichter.finance.websync.connector.ChromeDriverWebClientInit;
import de.derrichter.finance.websync.connector.GeckoDriverWebClientInit;
import de.derrichter.finance.websync.institute.dkbvisa.InstitutOptions;
import de.derrichter.finance.websync.institute.dkbvisa.WebAuth;
import de.derrichter.finance.websync.institute.dkbvisa.WebUtils;
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
import name.aiteanu.docmanager.institute.dkb.api.ApiClient;
import name.aiteanu.docmanager.institute.dkb.api.DocumentInfo;
import name.aiteanu.docmanager.institute.dkb.api.GetDocumentsResponse;
import name.aiteanu.docmanager.rmi.Account;
import name.aiteanu.docmanager.rmi.Document;
import name.aiteanu.docmanager.synchronize.SynchronizeDocuments;

public class WebSyncDkb {

	private static WebDriver seleniumWebDriver = null;

	public static final String LOGIDENT = "[DocManager:DKB] ";
	public static final String MAILBOX_URL = "https://www.dkb.de/banking/postfach";

	public String getShortName() {
		return "DKB Doks";
	}

	public String getLongName() {
		return "Deutsche Kreditbank AG (Dokumente)";
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
							appHttpsProxyPort, false, "", "", headless, "", "");
				} else {
					SyncPropertiesHelper.setChromeDriverAndBrowserPaths();
					seleniumWebDriver = ChromeDriverWebClientInit.connConfig(false, true, true, WebLogger.class,
							WebProgressMonitor.class, appSysProxyUse, appProxyHost, appProxyPort, appHttpsProxyHost,
							appHttpsProxyPort, false, "", "", headless, "", "");
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
			Logger.info("INFO: es werden nun gleich alle aktive DKB-Konten zur Anmeldekennung '" + logUserString
					+ "' abgearbeitet ...");
			monitor.log("INFO: es werden nun gleich alle aktive DKB-Konten zur Anmeldekennung '" + logUserString
					+ "' abgearbeitet ...");

			loadDocuments(account, monitor);
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

	public void loadDocuments(Account account, ProgressMonitor monitor) throws Exception {
		Set<Cookie> cookies = seleniumWebDriver.manage().getCookies();

		ApiClient client = new ApiClient(cookies);
		GetDocumentsResponse documents = client.getDocuments();

		for (DocumentInfo document : documents.getData()) {
			String folder = getFolderName(document);
			if (!isDocumentAlreadyDownloaded(account, document, folder)) {
				try {
					byte[] content = client.getDocumentBinary(document.getId());
					saveDocument(account, document, content, folder, monitor);
				} catch (Exception error) {
					Logger.error("loadDocuments fehlerhaft:\n", error);
					monitor.log("Fehler: Dokument '" + document.getAttributes().getMetadata().getSubject() 
							+ "' konnte nicht geladen werden. " + error.getMessage());
				}
			} else {

			}
			monitor.addPercentComplete(1);
		}
	}

	private boolean isDocumentAlreadyDownloaded(Account account, DocumentInfo document, String folderName)
			throws Exception {
		// first, search document by ID
		DBIterator<Document> existingDocs = Settings.getDBService().createList(Document.class);
		existingDocs.addFilter("accountid = ?", account.getID());
		existingDocs.addFilter("remoteid = ?", document.getId());

		if (existingDocs.hasNext())
			return true;

		// If document was not found by ID, it might be a document from the old DKB
		// website, identified by folder and title
		existingDocs = Settings.getDBService().createList(Document.class);
		existingDocs.addFilter("accountid = ?", account.getID());
		existingDocs.addFilter("remotefolder = ?", folderName);
		existingDocs.addFilter("title = ?", document.getAttributes().getMetadata().getSubject());

		if (existingDocs.hasNext()) { // document was not found by RemoteID, but by folder and title
			// update document RemoteID
			Document doc = existingDocs.next();
			doc.setRemoteID(document.getId());
			doc.store();

			return true;
		}

		return false;
	}

	private String getFolderName(DocumentInfo document) {
		switch (document.getAttributes().getOwner()) {
		case "service-broker-documents":
			return "Wertpapierdokumente";
		case "service-documentexchange-api":
			return "Kontoauszüge";
		}
		return "";
	}

	private Document saveDocument(Account account, DocumentInfo document, byte[] content, String folderName,
			ProgressMonitor monitor) {
		try {
			Document doc = (Document) Settings.getDBService().createObject(Document.class, null);
			doc.setAccount(account);
			doc.setRemoteFolder(folderName);
			doc.setTitle(document.getAttributes().getMetadata().getSubject());
			doc.setCreatedOn(document.getAttributes().getCreationDate());

			try {
				String fileName = document.getAttributes().getMetadata().getSubject();
				if (document.getAttributes().getContentType().equals("application/pdf") && !fileName.endsWith(".pdf")) {
					fileName = fileName + ".pdf";
				}
				fileName = fileName.replaceAll("[\\\\/:*?\"<>|]", "_");
				File output = new File(account.getDocumentsPath() + File.separator + folderName, fileName);
				doc.setLocalFolder(output.getParent());
				doc.setFilename(fileName);
				doc.setRemoteID(document.getId());
				doc.setDownloadedOn(new Date());

				Logger.info(LOGIDENT + " Storing : " + output.getAbsolutePath());
				FileUtils.writeByteArrayToFile(output, content);
				output.setLastModified(doc.getCreatedOn().getTime());
				Logger.info(LOGIDENT + " Stored: " + output.getAbsolutePath());

			} catch (Exception ex) {
				monitor.log(LOGIDENT + " error while storing file : " + ex.getMessage());
				doc.setComment("Datei konnte nicht gespeichert werden. " + ex.getMessage());
			}

			doc.store();

			SynchronizeDocuments.notifyDocumentListeners(doc);
			return doc;
		} catch (Exception e) {
			monitor.log(LOGIDENT + " error while storing file : " + e.getMessage());
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
