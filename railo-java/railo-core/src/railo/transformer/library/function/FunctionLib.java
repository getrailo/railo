package railo.transformer.library.function;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import railo.commons.lang.Md5;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageRuntimeException;


/**
 * Eine FunctionLib repraesentiert eine FLD, 
 * sie stellt Methoden zur Verfuegung um auf alle Informationen 
 * die eine FLD bietet zuzugreifen.
 */
public final class FunctionLib {

	
	// all functions of the lib
	private HashMap<String,FunctionLibFunction> functions=new HashMap<String,FunctionLibFunction>();
	private String version="";
	private String shortName="";
	private URI uri;
	private String displayName="";
	private String description="";
	private String source;
	
	/**
	 * Geschuetzer Konstruktor ohne Argumente.
	 */
	protected FunctionLib() {}
	
	/**
	 * Gibt eine einzelne Funktion der FLD zurueck mit dem passenden Namen. 
	 * Gibt null zurueck falls die Funktion nicht existiert.
	 * @param name Name der Funktion.
	 * @return FunctionLibFunction 
	 */
	public FunctionLibFunction getFunction(String name)	{
		return functions.get(name.toLowerCase());
	}

	/**
	 * Gibt die Beschreibung der FLD zurueck.
	 * @return Beschreibung der FLD.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Gibt den Namen zur Ausgabe (Praesentation) der FLD zurueck.
	 * @return Ausgabename.
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Gibt den Kurzname der FLD zurueck.
	 * @return Kurzname.
	 */
	public String getShortName() {
		return shortName;
	}

	/**
	 * Gibt die eindeutige URI der FLD zurueck.
	 * @return URI.
	 */
	public URI getUri() {
		return uri;
	}

	/**
	 * Gibt die Version der FLD zurueck.
	 * @return String
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Fuegt der FunctionLib eine Funktion (FunctionLibFunction) zu.
	 * @param function
	 */
	public void setFunction(FunctionLibFunction function) {
		function.setFunctionLib(this);
		functions.put(function.getName(),function);
	}
	
	/**
	 * Setzt die Beschreibung der FLD.
	 * @param description Beschreibung der FLD.
	 */
	protected void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Setzt den Ausgabename der FLD.
	 * @param displayName Ausgabename
	 */
	protected void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * Setzt den Kurznamen der FLD.
	 * @param shortName Kurznamen der FLD.
	 */
	protected void setShortName(String shortName) {
		this.shortName = shortName;
	}

	/**
	 * Setzt den eindeutigen URI der FLD.
	 * @param uriString URI.
	 * @throws URISyntaxException
	 */
	protected void setUri(String uriString) throws URISyntaxException {
		setUri(new URI(uriString));
	}
	
	protected void setUri(URI uri)  {
		this.uri = uri;
	}

	/**
	 * Setzt die Version der FLD.
	 * @param version FLD der Version.
	 */
	protected void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return Returns the functions.
	 */
	public Map<String,FunctionLibFunction> getFunctions() {
		return functions;
	}

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getDisplayName()+":"+getShortName()+":"+super.toString();
    }
    
    public String getHash() {
    	StringBuffer sb=new StringBuffer();
    	Iterator<String> it = functions.keySet().iterator();
    	while(it.hasNext()) {
    		sb.append((functions.get(it.next())).getHash()+"\n");
    	}
    	try {
			return Md5.getDigestAsString(sb.toString());
		} catch (IOException e) {
			return "";
		}
    }

	/**
	 * duplicate this FunctionLib
	 * @param deepCopy
	 * @return
	 */
	public FunctionLib duplicate(boolean deepCopy) {
		FunctionLib fl = new FunctionLib();
		
		fl.description=this.description;
		fl.displayName=this.displayName;
		fl.functions=duplicate(this.functions,deepCopy);
		fl.shortName=this.shortName;
		fl.uri=this.uri;
		fl.version=this.version;
				
		return fl;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * duplcate a hashmap with FunctionLibFunction's
	 * @param funcs
	 * @param deepCopy
	 * @return cloned map
	 */
	private HashMap duplicate(HashMap funcs, boolean deepCopy) {
		if(deepCopy) throw new PageRuntimeException(new ExpressionException("deep copy not supported"));
		
		Iterator it = funcs.entrySet().iterator();
		Map.Entry entry;
		HashMap cm = new HashMap();
		while(it.hasNext()){
			entry=(Entry) it.next();
			cm.put(
					entry.getKey(), 
					deepCopy?
							entry.getValue(): // TODO add support for deepcopy ((FunctionLibFunction)entry.getValue()).duplicate(deepCopy):
							entry.getValue());
		}
		return cm;
	}
}