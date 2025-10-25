package name.aiteanu.docmanager.institute.deka;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.function.Function;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import de.derrichter.finance.websync.utils.StringCharUtils;
import de.derrichter.finance.websync.utils.webdrivertools.SeleniumUtils;
import de.derrichter.hibiscus.mashup.crossover.callback.WebDialogs;
import name.aiteanu.docmanager.callback.WebLogger;
import name.aiteanu.docmanager.callback.WebProgressMonitor;

public class WebAuth {

	public static WebDriver loginWithSelenium(String responseLogin, String responsePasswort, WebDriver seleniumWebDriver, String optionalOptions, Class<?> externalLogger, Class<?> externalProgressMonitor, Class<?> externalDialogInterface) throws Exception {
		Method LogInfo = externalLogger.getMethod("info", new Class[] { String.class });
		Method LogWarn = externalLogger.getMethod("warn", new Class[] { String.class });
		Method LogError = externalLogger.getMethod("error", new Class[] { String.class });
		Method LogDebug = externalLogger.getMethod("debug", new Class[] { String.class });
		Method LogTrace = externalLogger.getMethod("trace", new Class[] { String.class });
		Method MonitorLog = externalProgressMonitor.getMethod("log", new Class[] { String.class });
		Method DialogWarn = externalDialogInterface.getMethod("warn", new Class[] { String.class, String.class });
		Method DialogAskTan = externalDialogInterface.getMethod("askTAN", new Class[] { String.class, String.class, String.class });
		Method DialogAskImageTan = externalDialogInterface.getMethod("askImageTAN", new Class[] { String.class, InputStream.class, String.class, String.class });
		String getLogMethod = "[WebLogin] ";
		boolean isSelfException = false;
		WebDriverWait wait = new WebDriverWait(seleniumWebDriver, Duration.ofSeconds(30));
		WebDriverWait wait10 = new WebDriverWait(seleniumWebDriver, Duration.ofSeconds(10));
		WebDriverWait wait1 = new WebDriverWait(seleniumWebDriver, Duration.ofSeconds(1));
		try {
			LogInfo.invoke(LogInfo, new Object[] { InstituteOptionsDeka.LOGIDENT + getLogMethod + InstituteOptionsDeka.SHORT_NAME + "-Login aufrufen ... (GET " + InstituteOptionsDeka.LOGIN_URL + ")" });
			try {
				seleniumWebDriver.get(InstituteOptionsDeka.LOGIN_URL);
				SeleniumUtils.waitForJSandJQueryToLoad(seleniumWebDriver);
				LogDebug.invoke(LogDebug, new Object[] { InstituteOptionsDeka.LOGIDENT + getLogMethod + "loginpage: current webdriver hash: " + seleniumWebDriver
						.getWindowHandle().hashCode() });
			} catch (Exception error) {
				isSelfException = true;
				throw new Exception("WebDriver-Fehler: " + ExceptionUtils.getStackTrace(error));
			} 
			String pageLoginResponse = seleniumWebDriver.getPageSource();
			try {
				WebUtils.checkSeleniumResponseHasError(pageLoginResponse, seleniumWebDriver, externalLogger, externalProgressMonitor, externalDialogInterface);
			} catch (Exception error) {
				isSelfException = true;
				throw new Exception("Fehler auf der Login-Seite: " + error.getMessage());
			} 

			SeleniumUtils.closeCookieLaw(seleniumWebDriver, InstituteOptionsDeka.LOGIDENT, InstituteOptionsDeka.LONG_NAME, InstituteOptionsDeka.LOGO_PATH, WebUtils.COOKIE_DETECT_PATH, WebUtils.COOKIE_ACCEPT_PATH, WebUtils.LOADER_CATCHSTRING, WebUtils.LOADER_EXCLUSIONS, WebUtils.LOADER_PATH, WebUtils.LOADER_TEXT, externalLogger);
			try {
				//wait.until((Function)ExpectedConditions.elementToBeClickable(By.id("login-toggle-desktop")));
				//seleniumWebDriver.findElement(By.id("login-toggle-desktop")).click();
				wait.until((Function)ExpectedConditions.presenceOfElementLocated(By.cssSelector("input.js-username")));
				//wait.until((Function)ExpectedConditions.elementToBeClickable(By.cssSelector(".meta-login__input--user")));
				//WebElement inputUsername = seleniumWebDriver.findElement(By.cssSelector("input.js-username"));
				WebElement inputUsername = seleniumWebDriver.findElement(By.cssSelector("input.js-username"));
				LogDebug.invoke(LogDebug, new Object[] { InstituteOptionsDeka.LOGIDENT + getLogMethod + "inputUsername: " + inputUsername });
				WebElement inputPasswd = seleniumWebDriver.findElement(By.cssSelector("input.js-password"));
				LogDebug.invoke(LogDebug, new Object[] { InstituteOptionsDeka.LOGIDENT + getLogMethod + "inputPassword: " + inputPasswd });
				inputUsername.click();
				inputUsername.sendKeys(new CharSequence[] { responseLogin });
				SeleniumUtils.waitForJSandJQueryToLoad(seleniumWebDriver);
				inputPasswd.click();
				inputPasswd.sendKeys(new CharSequence[] { responsePasswort });
				SeleniumUtils.waitForJSandJQueryToLoad(seleniumWebDriver);
			} catch (Exception error) {
				isSelfException = true;
				throw new Exception("Fehler beim Setzen des Login-Formulars oder der Felder (siehe Log - Bitte den Entwickler im Forum informieren)\nLog-Eintrag: " + 
						ExceptionUtils.getStackTrace(error));
			} 
			try {
				SeleniumUtils.clickElementHandleErrors(seleniumWebDriver, "submitContinue", "//button[contains(@class,'js-loginbutton')]", WebUtils.LOADER_CATCHSTRING, WebUtils.LOADER_EXCLUSIONS, WebUtils.LOADER_PATH, WebUtils.LOADER_TEXT, true, externalLogger);
			} catch (Exception error) {
				isSelfException = true;
				throw new Exception("WebDriver-Fehler: " + ExceptionUtils.getStackTrace(error));
			} 
			String pageAfterLoginResponse = seleniumWebDriver.getPageSource();
			try {
				WebUtils.checkLoginWasSuccessful(pageAfterLoginResponse, seleniumWebDriver, externalLogger, externalProgressMonitor, externalDialogInterface);
			} catch (Exception error) {
				isSelfException = true;
				throw new Exception(error.getMessage());
			} 

			/*
			boolean isChipTan = false;
			boolean isChipTanQR = false;
			boolean isAppTan = false;
			boolean isAppConfirm = false;
			boolean appAuthHasCancel = false;
			do {
				LogDebug.invoke(LogDebug, new Object[] { InstitutOptions.LOGIDENT + getLogMethod + "Prüfe auf Verfahren der Zwei-Faktor-Authentifizierung ..." });
				isChipTan = false;
				isChipTanQR = false;
				isAppTan = false;
				isAppConfirm = false;
				appAuthHasCancel = false;
				boolean haveFound = false;
				try {
					seleniumWebDriver.findElement(By.xpath("//form[contains(@action,'LoginWithBoundDevice')]"));
					LogDebug.invoke(LogDebug, new Object[] { InstitutOptions.LOGIDENT + getLogMethod + "Banking-App ist als Verfahren gesetzt ..." });
					boolean callAltTan = false;
					if (callAltTan == true) {
						LogDebug.invoke(LogDebug, new Object[] { InstitutOptions.LOGIDENT + getLogMethod + ">>> Entwicklertest <<< von alternativer TAN-Eingabe mit Tan-Generator oder TAN2Go-App, je nach genutzem Login-Benutzer ..." });
						try {
							SeleniumUtils.clickElementHandleErrors(seleniumWebDriver, "loginWithTan", "(//a[contains(@href,'javascript:loginWithTan')])[1]", WebUtils.LOADER_CATCHSTRING, WebUtils.LOADER_EXCLUSIONS, WebUtils.LOADER_PATH, WebUtils.LOADER_TEXT, true, externalLogger);
						} catch (Exception error) {
							isSelfException = true;
							throw new Exception("WebDriver-Fehler: " + ExceptionUtils.getStackTrace(error));
						} 
						try {
							wait1.until((Function)ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(.,'um den aktuellen Prozesszustand wiederherzustellen')]")));
							LogDebug.invoke(LogDebug, new Object[] { InstitutOptions.LOGIDENT + getLogMethod + "Prozesszustand verloren?, daher nochmal ..." });
							try {
								SeleniumUtils.clickElementHandleErrors(seleniumWebDriver, "resetProzess", "(//a[contains(@href,'LoginWithTan') or contains(@href,'chooseTanAlias')])[1]", WebUtils.LOADER_CATCHSTRING, WebUtils.LOADER_EXCLUSIONS, WebUtils.LOADER_PATH, WebUtils.LOADER_TEXT, true, externalLogger);
							} catch (Exception error) {
								isSelfException = true;
								throw new Exception("WebDriver-Fehler: " + ExceptionUtils.getStackTrace(error));
							} 
						} catch (TimeoutException|org.openqa.selenium.NoSuchElementException timeoutException) {}
					} else {
						haveFound = true;
						isAppConfirm = true;
					} 
				} catch (TimeoutException|org.openqa.selenium.NoSuchElementException noBankingApp) {
					LogDebug.invoke(LogDebug, new Object[] { InstitutOptions.LOGIDENT + getLogMethod + "Sicherheitsverfahren 'Banking-App' ohne TAN-Eingabe wird NICHT (direkt) angeboten, prauf manuelle TAN-Eingabe ..." });
				} 
				if (!haveFound)
					try {
						wait1.until((Function)ExpectedConditions.presenceOfElementLocated(By.xpath("//form[contains(@action,'InfoOpenLoginRequest')]")));
						LogDebug.invoke(LogDebug, new Object[] { InstitutOptions.LOGIDENT + getLogMethod + "von Vorankder Zwei-Faktor-Authentifizierung" });
						try {
							SeleniumUtils.clickElementHandleErrors(seleniumWebDriver, "submitContinue", "//button[@id='next']", WebUtils.LOADER_CATCHSTRING, WebUtils.LOADER_EXCLUSIONS, WebUtils.LOADER_PATH, WebUtils.LOADER_TEXT, true, externalLogger);
						} catch (Exception error) {
							isSelfException = true;
							throw new Exception("WebDriver-Fehler: " + ExceptionUtils.getStackTrace(error));
						} 
						pageAfterLoginResponse = seleniumWebDriver.getPageSource();
						try {
							WebUtils.checkSeleniumResponseHasError(pageAfterLoginResponse, seleniumWebDriver, externalLogger, externalProgressMonitor, externalDialogInterface);
						} catch (Exception error) {
							isSelfException = true;
							throw new Exception(error.getMessage());
						} 
					} catch (TimeoutException|org.openqa.selenium.NoSuchElementException noChipTanManual) {
						LogDebug.invoke(LogDebug, new Object[] { InstitutOptions.LOGIDENT + getLogMethod + "Vorankder Zwei-Faktor-Authentifizierung mit TAN wird nicht (mehr?) angezeigt" });
					}  
				if (!haveFound)
					try {
						seleniumWebDriver.findElement(By.xpath("//form[contains(@action,'chooseTanAlias')]"));
						MonitorLog.invoke(MonitorLog, new Object[] { "TAN2go verlangt die Wahl des Endgeres wird automatisch das erste Gerin der Liste gew..." });
						LogInfo.invoke(LogInfo, new Object[] { InstitutOptions.LOGIDENT + getLogMethod + "TAN2go verlangt die Wahl des Endgeres wird automatisch das erste Gerin der Liste gew..." });
						try {
							WebElement fistCheckBox = seleniumWebDriver.findElement(By.xpath("(//label[contains(@id,'slDeviceNames:')])[1]"));
							fistCheckBox.click();
							SeleniumUtils.waitForJSandJQueryToLoad(seleniumWebDriver);
						} catch (Exception error) {
							LogTrace.invoke(LogTrace, new Object[] { InstitutOptions.LOGIDENT + getLogMethod + "... bei der Seite mit TAN2go verlangt die Wahl des Endgerim XML-Format: \n" + seleniumWebDriver

									.getPageSource() });
							isSelfException = true;
							throw new Exception("Fehler beim Setzen des Login-Formulars oder der Felder (siehe Log - Bitte den Entwickler im Forum informieren)\nLog-Eintrag: " + 
									ExceptionUtils.getStackTrace(error));
						} 
						try {
							SeleniumUtils.clickElementHandleErrors(seleniumWebDriver, "submitContinue", "//button[@id='next']", WebUtils.LOADER_CATCHSTRING, WebUtils.LOADER_EXCLUSIONS, WebUtils.LOADER_PATH, WebUtils.LOADER_TEXT, true, externalLogger);
						} catch (Exception error) {
							isSelfException = true;
							throw new Exception("WebDriver-Fehler: " + ExceptionUtils.getStackTrace(error));
						} 
						pageAfterLoginResponse = seleniumWebDriver.getPageSource();
						try {
							WebUtils.checkSeleniumResponseHasError(pageAfterLoginResponse, seleniumWebDriver, externalLogger, externalProgressMonitor, externalDialogInterface);
						} catch (Exception error) {
							isSelfException = true;
							throw new Exception(error.getMessage());
						} 
					} catch (TimeoutException|org.openqa.selenium.NoSuchElementException noTAN2choose) {
						LogDebug.invoke(LogDebug, new Object[] { InstitutOptions.LOGIDENT + getLogMethod + "Sicherheitsverfahren 'TAN2go' verlangt wohl keine Wahl des Engeräts"});
					}  
				if (!haveFound)
					try {
						seleniumWebDriver.findElement(By.xpath("//label[contains(.,'TAN aus Ihrer DKB-TAN2go-App')]"));
						LogDebug.invoke(LogDebug, new Object[] { InstitutOptions.LOGIDENT + getLogMethod + "TAN2go ist als Verfahren gesetzt ..." });
						haveFound = true;
						isAppTan = true;
					} catch (TimeoutException|org.openqa.selenium.NoSuchElementException noTAN2goManual) {
						LogDebug.invoke(LogDebug, new Object[] { InstitutOptions.LOGIDENT + getLogMethod + "Sicherheitsverfahren 'TAN2go' mit manueller TAN-Eingabe wird NICHT (direkt) angeboten, prauf ChipTan-Verfahren ..." });
					}  
				if (!haveFound) {
					try {
						seleniumWebDriver.findElement(By.xpath("//p[contains(.,'Sie nutzen')]//b[contains(.,'chipTAN manuell')]"));
						LogDebug.invoke(LogDebug, new Object[] { InstitutOptions.LOGIDENT + getLogMethod + "chipTan manuell ist als Verfahren gesetzt ..." });
						haveFound = true;
						isChipTan = true;
					} catch (TimeoutException|org.openqa.selenium.NoSuchElementException timeoutException) {}
					try {
						seleniumWebDriver.findElement(By.xpath("//p[contains(.,'Sie nutzen')]//b[contains(.,'chipTAN QR')]"));
						LogDebug.invoke(LogDebug, new Object[] { InstitutOptions.LOGIDENT + getLogMethod + "chipTan QR ist als Verfahren gesetzt ..." });
						haveFound = true;
						isChipTanQR = true;
					} catch (TimeoutException|org.openqa.selenium.NoSuchElementException timeoutException) {}
					try {
						seleniumWebDriver.findElement(By.xpath("//p[contains(.,'Sie nutzen')]//b[contains(.,'chipTAN optisch')]"));
						LogWarn.invoke(LogWarn, new Object[] { InstitutOptions.LOGIDENT + getLogMethod + "chipTan optisch (Flickercode) ist als Verfahren gesetzt, wird aber NICHT unterst..." });
						LogInfo.invoke(LogInfo, new Object[] { InstitutOptions.LOGIDENT + getLogMethod + "Das 2FA-Sicherheitsverfahren wird daher nun automatisch auf 'chipTAN manuell' gewechselt ... " });
						MonitorLog.invoke(MonitorLog, new Object[] { "Info-Warnung: chipTan optisch (Flickercode) ist als Verfahren gesetzt, wird aber NICHT unterstützt."});
						MonitorLog.invoke(MonitorLog, new Object[] { "Das 2FA-Sicherheitsverfahren wird daher nun automatisch auf 'chipTAN manuell' gewechselt ..." });
						try {
							WebElement chipTanManualButton = seleniumWebDriver.findElement(By.xpath("//a[text()='chipTAN manuell']"));
							LogDebug.invoke(LogDebug, new Object[] { InstitutOptions.LOGIDENT + getLogMethod + "chipTanManualButton: " + chipTanManualButton });
							try {
								SeleniumUtils.clickElementHandleErrors(seleniumWebDriver, "chipTanManualButton", "//a[text()='chipTAN manuell']", WebUtils.LOADER_CATCHSTRING, WebUtils.LOADER_EXCLUSIONS, WebUtils.LOADER_PATH, WebUtils.LOADER_TEXT, true, externalLogger);
							} catch (Exception error) {
								isSelfException = true;
								throw new Exception("WebDriver-Fehler: " + ExceptionUtils.getStackTrace(error));
							} 
							pageAfterLoginResponse = seleniumWebDriver.getPageSource();
							try {
								WebUtils.checkSeleniumResponseHasError(pageAfterLoginResponse, seleniumWebDriver, externalLogger, externalProgressMonitor, externalDialogInterface);
							} catch (Exception error) {
								isSelfException = true;
								throw new Exception(error.getMessage());
							} 
							haveFound = true;
							isChipTan = true;
						} catch (TimeoutException|org.openqa.selenium.NoSuchElementException noChipTan) {
							LogDebug.invoke(LogDebug, new Object[] { InstitutOptions.LOGIDENT + getLogMethod + "Sicherheitsverfahren 'chipTAN manuell' wird NICHT zur Auswahl angeboten!" });
						} 
					} catch (TimeoutException|org.openqa.selenium.NoSuchElementException timeoutException) {}
				} 
				if (!haveFound)
					try {
						seleniumWebDriver.findElement(By.xpath("//form[contains(@action,'Vorschaltseite/single.xhtml')]"));
						LogDebug.invoke(LogDebug, new Object[] { InstitutOptions.LOGIDENT + getLogMethod + "iTAN ist als Verfahren gesetzt ... dies kann nicht mehr verwendet werden ..." });
						try {
							String str = seleniumWebDriver.findElement(By.xpath("(//form[@id='genericNoticeForm']//div[contains(@class,'Box')])[last()]")).getText().trim();
							DialogWarn.invoke(DialogWarn, new Object[] { "Fehlermeldung von [WebSync:DKB-Visa] :\n\n\n" + str, "institutlogo-dkbvisa.png" });
							isSelfException = true;
							throw new Exception("Fehlermeldung des Servers: " + str);
						} catch (Exception noiTANsecText) {
							isSelfException = true;
							throw new Exception("Hinweistext zu 'iTAN' konnte nicht gelesen werden. (Bitte den Entwickler im Forum informieren): " + 
									ExceptionUtils.getStackTrace(noiTANsecText));
						} 
					} catch (TimeoutException|org.openqa.selenium.NoSuchElementException noiTAN) {
						LogDebug.invoke(LogDebug, new Object[] { InstitutOptions.LOGIDENT + getLogMethod + "Sicherheitsverfahren 'iTAN' ist nicht eingestellt ..." });
					}  
				if (isAppConfirm != true)
					continue; 
				try {
					WebElement waiterCheck = null;
					MonitorLog.invoke(MonitorLog, new Object[] { "Warte auf Banking-App Freigabe ..." });
					int rounds = 0;
					Thread.sleep(3000L);
					boolean waitBestSign = true;
					do {
						try {
							waiterCheck = seleniumWebDriver.findElement(By.xpath("//form[contains(@action,'LoginWithBoundDevice')]"));
						} catch (TimeoutException|org.openqa.selenium.NoSuchElementException noBestSign) {
							waitBestSign = false;
						} 
						if (waitBestSign == true) {
							LogInfo.invoke(LogInfo, new Object[] { "[WebSync:DKB-Visa] Warte weiter auf Banking-App Freigabe (weitere 3 Sekunden) ..." });
							Thread.sleep(3000L);
							rounds++;
						} else {
							try {
								wait1.until((Function)ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(.,'um den aktuellen Prozesszustand wiederherzustellen')]")));
								LogDebug.invoke(LogDebug, new Object[] { InstitutOptions.LOGIDENT + getLogMethod + "Prozesszustand verloren?, daher nochmal ..." });
								try {
									SeleniumUtils.clickElementHandleErrors(seleniumWebDriver, "resetProzess", "(//a[contains(@href,'LoginWithTan')])[1]", WebUtils.LOADER_CATCHSTRING, WebUtils.LOADER_EXCLUSIONS, WebUtils.LOADER_PATH, WebUtils.LOADER_TEXT, true, externalLogger);
								} catch (Exception error) {
									isSelfException = true;
									throw new Exception("WebDriver-Fehler: " + 
											ExceptionUtils.getStackTrace(error));
								} 
								appAuthHasCancel = true;
							} catch (TimeoutException|org.openqa.selenium.NoSuchElementException timeoutException) {}
							try {
								wait1.until((Function)ExpectedConditions.presenceOfElementLocated(By.xpath("//form[contains(@action,'InfoOpenLoginRequest')]")));
								LogDebug.invoke(LogDebug, new Object[] { InstitutOptions.LOGIDENT + getLogMethod + "TAN-Sicherheitsverfahren wird angefordert?, es wurde wohl Baning-App abgelehnt, daher nochmal ..." });
								appAuthHasCancel = true;
							} catch (TimeoutException|org.openqa.selenium.NoSuchElementException timeoutException) {}
							try {
								seleniumWebDriver.findElement(By.xpath("//form[contains(@action,'Vorschaltseite/single.xhtml')]"));
								LogDebug.invoke(LogDebug, new Object[] { InstitutOptions.LOGIDENT + getLogMethod + "altes Sicherheitsverfahren iTAN angezeigt?, es wurde wohl Baning-App abgelehnt, daher nochmal ..." });
								appAuthHasCancel = true;
							} catch (TimeoutException|org.openqa.selenium.NoSuchElementException timeoutException) {}
							try {
								seleniumWebDriver.findElement(By.xpath("//form[contains(@action,'chooseTanAlias')]"));
								LogDebug.invoke(LogDebug, new Object[] { InstitutOptions.LOGIDENT + getLogMethod + "TAN2go verlangt die Wahl des Endgeres wurde wohl Baning-App abgelehnt, daher nochmal ..." });
								try {
									SeleniumUtils.clickElementHandleErrors(seleniumWebDriver, "jsBackButton", "//button[@tid='$pageflow-back']", WebUtils.LOADER_CATCHSTRING, WebUtils.LOADER_EXCLUSIONS, WebUtils.LOADER_PATH, WebUtils.LOADER_TEXT, true, externalLogger);
								} catch (Exception error) {
									isSelfException = true;
									throw new Exception("WebDriver-Fehler: " + 
											ExceptionUtils.getStackTrace(error));
								} 
								appAuthHasCancel = true;
							} catch (TimeoutException|org.openqa.selenium.NoSuchElementException timeoutException) {}
							try {
								seleniumWebDriver.findElement(By.xpath("//label[contains(.,'TAN aus Ihrer DKB-TAN2go-App')]"));
								LogDebug.invoke(LogDebug, new Object[] { InstitutOptions.LOGIDENT + getLogMethod + "Sicherheitsverfahren TAN2go angezeigt?, es wurde wohl Baning-App abgelehnt, daher nochmal ..." });
								appAuthHasCancel = true;
							} catch (TimeoutException|org.openqa.selenium.NoSuchElementException timeoutException) {}
							try {
								seleniumWebDriver.findElement(By.xpath("//p[contains(.,'Sie nutzen')]//b[contains(.,'chipTAN manuell')]"));
								LogDebug.invoke(LogDebug, new Object[] { InstitutOptions.LOGIDENT + getLogMethod + "Sicherheitsverfahren 'chipTAN manuell'? angezeigt?, es wurde wohl Banking-App abgelehnt, daher nochmal ..." });
								appAuthHasCancel = true;
							} catch (TimeoutException|org.openqa.selenium.NoSuchElementException timeoutException) {}
							try {
								seleniumWebDriver.findElement(By.xpath("//p[contains(.,'Sie nutzen')]//b[contains(.,'chipTAN optisch')]"));
								LogDebug.invoke(LogDebug, new Object[] { InstitutOptions.LOGIDENT + getLogMethod + "Sicherheitsverfahren 'chipTAN optisch (Flickercode)'? angezeigt?, es wurde wohl Banking-App abgelehnt, daher nochmal ..." });
								appAuthHasCancel = true;
							} catch (TimeoutException|org.openqa.selenium.NoSuchElementException timeoutException) {}
							try {
								seleniumWebDriver.findElement(By.xpath("//p[contains(.,'Sie nutzen')]//b[contains(.,'chipTAN QR')]"));
								LogDebug.invoke(LogDebug, new Object[] { InstitutOptions.LOGIDENT + getLogMethod + "Sicherheitsverfahren 'chipTAN QR (QR-Code)'? angezeigt?, es wurde wohl Banking-App abgelehnt, daher nochmal ..." });
								appAuthHasCancel = true;
							} catch (TimeoutException|org.openqa.selenium.NoSuchElementException timeoutException) {}
						} 
						if (rounds >= 19) {
							isSelfException = true;
							throw new Exception("Banking-App Authentifizierung auch nach 60 Sekunden nicht abgeschlossen. Warten auf Banking-App Freigabe abgebrochen");
						} 
					} while (waitBestSign == true);
				} catch (Exception error) {
					if (isSelfException == true)
						throw new Exception(error.getMessage()); 
					isSelfException = true;
					throw new Exception("Warten auf Banking-App Freigabe fehlerhaft: " + 
							ExceptionUtils.getStackTrace(error));
				} 
			} while (appAuthHasCancel == true);
			String tanArt = "unkown-tan";
			String secText = "Bitte geben Sie Ihre Tan ein";
			InputStream streamImage = null;
			if (isAppConfirm != true)
				if (isAppTan == true) {
					secText = seleniumWebDriver.findElement(By.xpath("//label[contains(@id,'tan-label')]")).getText().trim();
					LogTrace.invoke(LogTrace, new Object[] { InstitutOptions.LOGIDENT + getLogMethod + "secText: \n" + secText });
					tanArt = "TAN2go";
				} else if (isChipTan == true) {
					secText = seleniumWebDriver.findElement(By.xpath("(//form[@id='next']//fieldset//div[@class='hide-for-small-down'])[last()]")).getText().trim();
					tanArt = "chipTan";
					LogTrace.invoke(LogTrace, new Object[] { InstitutOptions.LOGIDENT + getLogMethod + "secText: \n" + secText });
				} else if (isChipTanQR == true) {
					String imageToScanPath = "//img[@alt='QR-Code']";
					wait10.until((Function)ExpectedConditions.presenceOfElementLocated(By.xpath(imageToScanPath)));
					String base64ImageData = seleniumWebDriver.findElement(By.xpath(imageToScanPath)).getAttribute("src").trim();
					LogTrace.invoke(LogTrace, new Object[] { InstitutOptions.LOGIDENT + getLogMethod + "base64ImageData: " + base64ImageData });
					streamImage = StringCharUtils.base64ImagetoInputStream(base64ImageData, externalLogger);
					LogTrace.invoke(LogTrace, new Object[] { InstitutOptions.LOGIDENT + getLogMethod + "streamImage (InputStream): " + streamImage });
					secText = seleniumWebDriver.findElement(By.xpath("(//form[@id='next']//fieldset//div[@class='hide-for-small-down'])[last()]")).getText().trim();
					LogTrace.invoke(LogTrace, new Object[] { InstitutOptions.LOGIDENT + getLogMethod + "secText: \n" + secText });
					tanArt = "chipTanQR";
				} else {
					try {
						seleniumWebDriver.findElement(By.xpath("//span[contains(.,'Gesamtbestand')]"));
						LogDebug.invoke(LogDebug, new Object[] { InstitutOptions.LOGIDENT + getLogMethod + "Summe in Euro wird angezeigt, dieser Login-Vorgang wohl ohne Zwei-Faktor-Authentifizierung ..." });
						isAppConfirm = true;
					} catch (TimeoutException|org.openqa.selenium.NoSuchElementException noBankingApp) {
						LogTrace.invoke(LogTrace, new Object[] { InstitutOptions.LOGIDENT + getLogMethod + "... bei der Seite mit dem vermeintlich angebotenen Sicherheitsverfahren handelt es sich um diese im XML-Format: \n" + seleniumWebDriver

								.getPageSource() });
						isSelfException = true;
						throw new Exception("Angebotes Sicherheitsverfahren nicht erkannt oder bekannt. Bitte informieren Sie im Forum den Entwickler");
					} 
				}  
			if (!isAppConfirm) {
				String userTanInput = null;
				try {
					if (isChipTanQR == true) {
						userTanInput = (String)DialogAskImageTan.invoke(DialogAskImageTan, new Object[] { tanArt, streamImage, secText, "institutlogo-dkbvisa.png" });
					} else {
						userTanInput = (String)DialogAskTan.invoke(DialogAskTan, new Object[] { tanArt, secText, "institutlogo-dkbvisa.png" });
					} 
				} catch (InvocationTargetException error) {
					isSelfException = true;
					if (!StringCharUtils.isNullOrEmptyOrNothing(error.getCause().toString()) && error
							.getCause().toString().contains("OperationCanceledException"))
						throw new Exception("Abbruch der " + tanArt + "-Eingabe durch Benutzer (OperationCanceledException)"); 
					LogError.invoke(LogError, new Object[] { InstitutOptions.LOGIDENT + getLogMethod + "TANAuthDialog fehlerhaft:\n" + 
							ExceptionUtils.getStackTrace(error) });
					throw new Exception("TANAuthDialog fehlerhaft: " + error.getMessage());
				} 
				WebElement tanInput = seleniumWebDriver.findElement(By.id("tanInputSelector"));
				LogDebug.invoke(LogDebug, new Object[] { InstitutOptions.LOGIDENT + getLogMethod + "tanInput: " + tanInput });
				tanInput.click();
				tanInput.sendKeys(new CharSequence[] { userTanInput });
				try {
					SeleniumUtils.clickElementHandleErrors(seleniumWebDriver, "submitContinue", "//button[@id='next']", WebUtils.LOADER_CATCHSTRING, WebUtils.LOADER_EXCLUSIONS, WebUtils.LOADER_PATH, WebUtils.LOADER_TEXT, true, externalLogger);
				} catch (Exception error) {
					isSelfException = true;
					throw new Exception("WebDriver-Fehler: " + ExceptionUtils.getStackTrace(error));
				} 
			} 
			pageAfterLoginResponse = seleniumWebDriver.getPageSource();
			try {
				WebUtils.checkSeleniumResponseHasError(pageAfterLoginResponse, seleniumWebDriver, externalLogger, externalProgressMonitor, externalDialogInterface);
			} catch (Exception error) {
				isSelfException = true;
				throw new Exception(error.getMessage());
			} 
			try {
				WebElement checkLoginField = seleniumWebDriver.findElement(By.xpath("//form[@id='login' or @name='login']"));
				isSelfException = true;
				throw new Exception("Die Loginseite wird trotz keinem bekannten Fehler noch immer angezeigt. Informieren Sie bitte den Entwickler im Forum");
			} catch (TimeoutException|org.openqa.selenium.NoSuchElementException timeoutException) {
				return seleniumWebDriver;
			} 
			*/
			try {
				seleniumWebDriver.findElement(By.xpath("//button[@class='depot-zugang__button']"));
				LogDebug.invoke(LogDebug, new Object[] { InstituteOptionsDeka.LOGIDENT + getLogMethod + "depot-zugang__button wird angezeigt, Login erfolgreich" });
			} catch (TimeoutException|org.openqa.selenium.NoSuchElementException noBankingApp) {
				LogTrace.invoke(LogTrace, new Object[] { InstituteOptionsDeka.LOGIDENT + getLogMethod + "... nach dem Login erscheint folgende Seite im XML-Format: \n" + seleniumWebDriver
						.getPageSource() });
				isSelfException = true;
				throw new Exception("Seite nach Login nicht erkannt. Bitte informieren Sie im Forum den Entwickler");
			} 
			return seleniumWebDriver;
		} catch (Exception error) {
			if (isSelfException == true)
				throw new Exception(error.getMessage()); 
			LogError.invoke(LogError, new Object[] { InstituteOptionsDeka.LOGIDENT + getLogMethod + "Fehlermeldung/Exception des Systems:\n" + 
					ExceptionUtils.getStackTrace(error) });
			throw new Exception("Fehlermeldung des Systems: " + error.getMessage());
		} 
	}

	public static void logoutWithSelenium(WebDriver seleniumWebDriver, String optionalOptions, Class<?> externalLogger, Class<?> externalProgressMonitor, Class<?> externalDialogInterface) throws Exception {
		Method LogInfo = externalLogger.getMethod("info", new Class[] { String.class });
		Method LogError = externalLogger.getMethod("error", new Class[] { String.class });
		Method LogDebug = externalLogger.getMethod("debug", new Class[] { String.class });
		Method LogTrace = externalLogger.getMethod("trace", new Class[] { String.class });
		String getLogMethod = "[WebLogout] ";
		boolean isSelfException = false;
		WebDriverWait wait = new WebDriverWait(seleniumWebDriver, Duration.ofSeconds(3));
		try {
			LogInfo.invoke(LogInfo, new Object[] { InstituteOptionsDeka.LOGIDENT + getLogMethod + "Abmelden aufrufen (" + InstituteOptionsDeka.LOGOUT_URL + ")" });
			try {
				seleniumWebDriver.get(InstituteOptionsDeka.LOGOUT_URL);
				SeleniumUtils.waitForJSandJQueryToLoad(seleniumWebDriver);
				Thread.sleep(1000L);
				LogDebug.invoke(LogDebug, new Object[] { InstituteOptionsDeka.LOGIDENT + getLogMethod + "loginpage: current webdriver hash: " + seleniumWebDriver
						.getWindowHandle().hashCode() });
			} catch (Exception error) {
				throw new Exception("WebDriver-Fehler: " + ExceptionUtils.getStackTrace(error));
			} 
			LogDebug.invoke(LogDebug, new Object[] { InstituteOptionsDeka.LOGIDENT + getLogMethod + "postLogoutPage: " + seleniumWebDriver });
			String postLogoutPage = seleniumWebDriver.getPageSource();
			try {
				WebUtils.checkSeleniumResponseHasError(postLogoutPage, seleniumWebDriver, externalLogger, externalProgressMonitor, externalDialogInterface);
			} catch (Exception error) {
				isSelfException = true;
				throw new Exception(error.getMessage());
			} 
			String successLogoutXpath = "//h1[contains(.,'Mein Depot')]";
			try {
				LogDebug.invoke(LogDebug, new Object[] { InstituteOptionsDeka.LOGIDENT + getLogMethod + "Gefundene Logout-Best" + wait
						.until((Function)ExpectedConditions.presenceOfElementLocated(By.xpath(successLogoutXpath))) });
				SeleniumUtils.waitForJSandJQueryToLoad(seleniumWebDriver);
			} catch (TimeoutException|org.openqa.selenium.NoSuchElementException notFounderr) {
				LogTrace.invoke(LogTrace, new Object[] { InstituteOptionsDeka.LOGIDENT + getLogMethod + "Seite nach Logout-Aufruf unbekannt; Code zur Analyse:\n" + postLogoutPage });
				isSelfException = true;
				throw new Exception("Seite nach Logout-Aufruf unbekannt; Bitte dem Entwickler im Forum melden!");
			} catch (Exception error) {
				throw new Exception("WebDriver-Fehler: " + ExceptionUtils.getStackTrace(error));
			} 
			LogInfo.invoke(LogInfo, new Object[] { InstituteOptionsDeka.LOGIDENT + getLogMethod + "Logout bei der '" + InstituteOptionsDeka.LONG_NAME + "' war erfolgreich" });
		} catch (Exception error) {
			if (isSelfException == true)
				throw new Exception(error.getMessage()); 
			LogError.invoke(LogError, new Object[] { InstituteOptionsDeka.LOGIDENT + getLogMethod + "WebLogout fehlerhaft! Stacktrace:\n" + 
					ExceptionUtils.getStackTrace(error) });
			throw new Exception("WebLogout fehlerhaft! Fehlermeldung:" + error.getMessage());
		} 
	}

}
