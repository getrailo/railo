package railo.deployer.filter;

import railo.commons.io.res.Resource;

/**
 * Das Interface Filter dient der Deployer Klasse festzustellen, 
 * ob es sich bei einer Datei um eine CFML Datei handelt oder nicht. 
 * Vergleichbar mit einem Filter bei Webserver zum zuteilen von Dateien zu Modulen. 
 * Das Interface Filter ist als Interface implementiert um flexibler in dessen Handhabung zu sein.
 */
public interface Filter {
	
	/**
	 * Gibt zurueck ob die eingegebene Datei eine CFML Datei ist oder nicht.
	 * @param file File das geprueft werden soll.
	 * @return handelt es sich bei der CFML Datei um eine CFML
	 */
	public boolean isValid(Resource res);
}