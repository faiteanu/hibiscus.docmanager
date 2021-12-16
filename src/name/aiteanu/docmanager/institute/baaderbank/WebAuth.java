package name.aiteanu.docmanager.institute.baaderbank;

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
		//WebDriverWait wait10 = new WebDriverWait(seleniumWebDriver, Duration.ofSeconds(10));
		//WebDriverWait wait1 = new WebDriverWait(seleniumWebDriver, Duration.ofSeconds(1));
		try {
			LogInfo.invoke(LogInfo, new Object[] { InstituteOptionsBaader.LOGIDENT + getLogMethod + InstituteOptionsBaader.SHORT_NAME + "-Login aufrufen ... (GET " + InstituteOptionsBaader.LOGIN_URL + ")" });
			try {
				seleniumWebDriver.get(InstituteOptionsBaader.LOGIN_URL);
				SeleniumUtils.waitForJSandJQueryToLoad(seleniumWebDriver);
				LogDebug.invoke(LogDebug, new Object[] { InstituteOptionsBaader.LOGIDENT + getLogMethod + "loginpage: current webdriver hash: " + seleniumWebDriver
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
//			String cookieDetectPath = "//div[@class='mfp-content' and not(contains(@style,'display: none;'))]";
//			String cookieAcceptPath = "//button[contains(@class,'js-accept-selected-cookies')]";
//			SeleniumUtils.closeCookieLaw(seleniumWebDriver, InstituteOptionsBaader.LOGIDENT, InstituteOptionsBaader.LONG_NAME, InstituteOptionsBaader.LOGO_PATH, cookieDetectPath, cookieAcceptPath, WebUtils.LOADER_CATCHSTRING, WebUtils.LOADER_EXCLUSIONS, WebUtils.LOADER_PATH, WebUtils.LOADER_TEXT, externalLogger);
			try {
				//wait.until((Function)ExpectedConditions.presenceOfElementLocated(By.cssSelector("aside input.js-username")));
				wait.until((Function)ExpectedConditions.elementToBeClickable(By.cssSelector("input[name='authusername']")));
				WebElement inputUsername = seleniumWebDriver.findElement(By.cssSelector("input[name='authusername']"));
				LogDebug.invoke(LogDebug, new Object[] { InstituteOptionsBaader.LOGIDENT + getLogMethod + "inputUsername: " + inputUsername });
				WebElement inputPasswd = seleniumWebDriver.findElement(By.cssSelector("input[name='authpassword']"));
				LogDebug.invoke(LogDebug, new Object[] { InstituteOptionsBaader.LOGIDENT + getLogMethod + "inputPassword: " + inputPasswd });
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
				SeleniumUtils.clickElementHandleErrors(seleniumWebDriver, "submitContinue", "//button[@type='submit']", WebUtils.LOADER_CATCHSTRING, WebUtils.LOADER_EXCLUSIONS, WebUtils.LOADER_PATH, WebUtils.LOADER_TEXT, true, externalLogger);
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
			
			// Überprüfe, ob TAN-Eingabe nötig ist
			try {
				seleniumWebDriver.findElement(By.xpath("//h1[contains(., 'TAN-Eingabe erforderlich)]"));
				LogDebug.invoke(LogDebug, new Object[] { InstituteOptionsBaader.LOGIDENT + getLogMethod + "vor der Zwei-Faktor-Authentifizierung" });
				
				String tanArt = "tan";
				String secText = "Bitte geben Sie Ihre TAN ein";
				String userTanInput = null;
				try {
					userTanInput = (String)DialogAskTan.invoke(DialogAskTan, new Object[] { tanArt, secText, "institutlogo-baader.png" });
				} catch (InvocationTargetException error) {
					isSelfException = true;
					if (!StringCharUtils.isNullOrEmptyOrNothing(error.getCause().toString()) && error
							.getCause().toString().contains("OperationCanceledException"))
						throw new Exception("Abbruch der " + tanArt + "-Eingabe durch Benutzer (OperationCanceledException)"); 
					LogError.invoke(LogError, new Object[] { InstituteOptionsBaader.LOGIDENT + getLogMethod + "TANAuthDialog fehlerhaft:\n" + 
							ExceptionUtils.getStackTrace(error) });
					throw new Exception("TANAuthDialog fehlerhaft: " + error.getMessage());
				} 
				WebElement tanInput = seleniumWebDriver.findElement(By.cssSelector("input[name='tan']"));
				LogDebug.invoke(LogDebug, new Object[] { InstituteOptionsBaader.LOGIDENT + getLogMethod + "tanInput: " + tanInput });
				tanInput.click();
				tanInput.sendKeys(new CharSequence[] { userTanInput });
				try {
					SeleniumUtils.clickElementHandleErrors(seleniumWebDriver, "submitContinue", "//input[@name='OK']", WebUtils.LOADER_CATCHSTRING, WebUtils.LOADER_EXCLUSIONS, WebUtils.LOADER_PATH, WebUtils.LOADER_TEXT, true, externalLogger);
				} catch (Exception error) {
					isSelfException = true;
					throw new Exception("WebDriver-Fehler: " + ExceptionUtils.getStackTrace(error));
				} 
			} catch (TimeoutException|org.openqa.selenium.NoSuchElementException noChipTanManual) {
				LogDebug.invoke(LogDebug, new Object[] { InstituteOptionsBaader.LOGIDENT + getLogMethod + "Zwei-Faktor-Authentifizierung mit TAN wird nicht angezeigt" });
			}  
			
			return seleniumWebDriver;
		} catch (Exception error) {
			if (isSelfException == true)
				throw new Exception(error.getMessage()); 
			LogError.invoke(LogError, new Object[] { InstituteOptionsBaader.LOGIDENT + getLogMethod + "Fehlermeldung/Exception des Systems:\n" + 
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
			LogInfo.invoke(LogInfo, new Object[] { InstituteOptionsBaader.LOGIDENT + getLogMethod + "Abmelden aufrufen (" + InstituteOptionsBaader.LOGOUT_URL + ")" });
			try {
				seleniumWebDriver.get(InstituteOptionsBaader.LOGOUT_URL);
				SeleniumUtils.waitForJSandJQueryToLoad(seleniumWebDriver);
				Thread.sleep(1000L);
				LogDebug.invoke(LogDebug, new Object[] { InstituteOptionsBaader.LOGIDENT + getLogMethod + "loginpage: current webdriver hash: " + seleniumWebDriver
						.getWindowHandle().hashCode() });
			} catch (Exception error) {
				throw new Exception("WebDriver-Fehler: " + ExceptionUtils.getStackTrace(error));
			} 
			LogDebug.invoke(LogDebug, new Object[] { InstituteOptionsBaader.LOGIDENT + getLogMethod + "postLogoutPage: " + seleniumWebDriver });
			String postLogoutPage = seleniumWebDriver.getPageSource();
			try {
				WebUtils.checkSeleniumResponseHasError(postLogoutPage, seleniumWebDriver, externalLogger, externalProgressMonitor, externalDialogInterface);
			} catch (Exception error) {
				isSelfException = true;
				throw new Exception(error.getMessage());
			} 
			String successLogoutXpath = "//p[contains(.,'von unserem Portal abgemeldet')]";
			try {
				LogDebug.invoke(LogDebug, new Object[] { InstituteOptionsBaader.LOGIDENT + getLogMethod + "Gefundene Logout-Bestätigung: " + wait
						.until((Function)ExpectedConditions.presenceOfElementLocated(By.xpath(successLogoutXpath))) });
				SeleniumUtils.waitForJSandJQueryToLoad(seleniumWebDriver);
			} catch (TimeoutException|org.openqa.selenium.NoSuchElementException notFounderr) {
				LogTrace.invoke(LogTrace, new Object[] { InstituteOptionsBaader.LOGIDENT + getLogMethod + "Seite nach Logout-Aufruf unbekannt; Code zur Analyse:\n" + postLogoutPage });
				isSelfException = true;
				throw new Exception("Seite nach Logout-Aufruf unbekannt; Bitte dem Entwickler im Forum melden!");
			} catch (Exception error) {
				throw new Exception("WebDriver-Fehler: " + ExceptionUtils.getStackTrace(error));
			} 
			LogInfo.invoke(LogInfo, new Object[] { InstituteOptionsBaader.LOGIDENT + getLogMethod + "Logout bei der '" + InstituteOptionsBaader.LONG_NAME + "' war erfolgreich" });
		} catch (Exception error) {
			if (isSelfException == true)
				throw new Exception(error.getMessage()); 
			LogError.invoke(LogError, new Object[] { InstituteOptionsBaader.LOGIDENT + getLogMethod + "WebLogout fehlerhaft! Stacktrace:\n" + 
					ExceptionUtils.getStackTrace(error) });
			throw new Exception("WebLogout fehlerhaft! Fehlermeldung:" + error.getMessage());
		} 
	}

}
