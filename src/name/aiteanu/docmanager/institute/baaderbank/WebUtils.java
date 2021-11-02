package name.aiteanu.docmanager.institute.baaderbank;

import java.lang.reflect.Method;

import org.openqa.selenium.WebDriver;

import de.derrichter.finance.websync.utils.StringCharUtils;

public class WebUtils {
	static String LOADER_CATCHSTRING = "DummyCatchString";

	static String LOADER_EXCLUSIONS = "DummyExclutionCatchString";

	static String LOADER_PATH = "//div[@class='ajax_loading' and @stlye='']";

	static String LOADER_TEXT = "DummyLoaderText";

	public static void checkSeleniumResponseHasError(String responsePageAsString, WebDriver seleniumWebDriver,
			Class<?> externalLogger, Class<?> externalProgressMonitor, Class<?> externalDialogInterface) throws Exception {
		Method DialogError = externalDialogInterface.getMethod("error", new Class[] { String.class, String.class });
	    String logIdent = InstituteOptionsBaader.LOGIDENT;
	    String longName = InstituteOptionsBaader.LONG_NAME;
	    String errorMessage = null;
//	    if (responsePageAsString.contains("PIN ist falsch")) {
//	      errorMessage = "Benutzer-ID oder PIN ist falsch. Bitte �berpr�fen Sie die Konfiguration und Ihre Eingabe.";
//	      DialogError.invoke(DialogError, new Object[] { "Fehlermeldung der Deka:\n\n\n" + errorMessage, InstitutOptions.LOGO_PATH });
//	      errorMessage = StringCharUtils.shrinkString(errorMessage);
//	      throw new Exception(errorMessage);
//	    } 
//	    if (seleniumWebDriver.getCurrentUrl().contains("RequestPin.xhtml")) {
//	      errorMessage = "Sie haben Ihr/e PIN/Passwort vergessen und ksich nicht mehr im Banking anmelden? Geben Sie hier Ihren Anmeldenamen an und beantworten die Verifizierungsfragen.\n\nUnter der Website\nhttps://www.dkb.de/Welcome/content/RequestPin.xhtml\nkSie ein neues Passwort anfordern.\n\nDurch das Absenden des Auftrags wird Ihr Zugang zum Banking sofort gesperrt und ein Start-Passwort zur einmaligen Anmeldung generiert.";
//	      DialogError.invoke(DialogError, new Object[] { "Hinweis-Frage der Deutsche Kreditbank AG (VISA):\n\n\nPasswort vergessen\n\n" + errorMessage, "institutlogo-dkbvisa.png" });
//	      errorMessage = StringCharUtils.shrinkString(errorMessage);
//	      throw new Exception(errorMessage);
//	    } 
//	    if (responsePageAsString.contains("Online-Sperre aufheben") || responsePageAsString.contains("PIN-Sperre aufheben")) {
//	      errorMessage = "Ihr Zugang zum Online-Banking ist gesperrt. \n\nFdie Freischaltung melden Sie sich im Online-Banking an und folgen dort den Anweisungen.";
//	      DialogError.invoke(DialogError, new Object[] { "Fehlermeldung der Deutsche Kreditbank AG (VISA):\n\n\n" + errorMessage, "institutlogo-dkbvisa.png" });
//	      errorMessage = StringCharUtils.shrinkString(errorMessage);
//	      throw new Exception(errorMessage);
//	    } 
	    if (responsePageAsString.contains("Die Webseite ist derzeit nicht erreichbar")) {
	      errorMessage = "Geplante Wartungsarbeiten\n\nManchmal muss man auch in der digitalen Welt manuell Hand anlegen.\n\nDie Webseite steht aufgrund von Wartungsarbeiten aktuell nicht zur Verf�gung.";
	      DialogError.invoke(DialogError, new Object[] { "Fehlermeldung der " + InstituteOptionsBaader.SHORT_NAME + ":\n\n\n" + errorMessage, InstituteOptionsBaader.LOGO_PATH });
	      errorMessage = StringCharUtils.shrinkString(errorMessage);
	      throw new Exception(errorMessage);
	    } 
//	    try {
//	      setSerchStrings();
//	      setSerchExclusionStrings();
//	      SeleniumUtils.checkWebDriverResponse(responsePageAsString, seleniumWebDriver, logIdent, longName, "institutlogo-dkbvisa.png", xpathSearchString, xpathSearchStringExclusion, externalLogger, externalProgressMonitor, externalDialogInterface);
//	    } catch (Exception error) {
//	      throw new Exception(error.getMessage());
//	    } 

	}
	
	public static void checkLoginWasSuccessful(String responsePageAsString, WebDriver seleniumWebDriver,
			Class<?> externalLogger, Class<?> externalProgressMonitor, Class<?> externalDialogInterface) throws Exception {
		Method DialogError = externalDialogInterface.getMethod("error", new Class[] { String.class, String.class });
	    String logIdent = InstituteOptionsBaader.LOGIDENT;
	    String longName = InstituteOptionsBaader.LONG_NAME;
	    String errorMessage = null;
	    if (responsePageAsString.contains("Dialog abgebrochen")) {
	      errorMessage = "Benutzer-ID oder PIN ist falsch. Bitte �berpr�fen Sie die Konfiguration und Ihre Eingabe.";
	      DialogError.invoke(DialogError, new Object[] { "Fehlermeldung der " + InstituteOptionsBaader.SHORT_NAME + ":\n\n\n" + errorMessage, InstituteOptionsBaader.LOGO_PATH });
	      errorMessage = StringCharUtils.shrinkString(errorMessage);
	      throw new Exception(errorMessage);
	    } 

	}

}
