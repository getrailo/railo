package railo.transformer.library.function;

import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.filter.ExtensionResourceFilter;
import railo.commons.io.res.util.ResourceUtil;
import railo.runtime.op.Caster;
import railo.runtime.text.xml.XMLUtil;
import railo.runtime.type.util.ArrayUtil;
import railo.transformer.library.tag.TagLibFactory;



/**
 *
 * Die FunctionLibFactory ist der Produzent fuer eine oder mehrere FunctionLib, 
 * d.H. ueber statische Methoden (get, getDir) koennen FunctionLibs geladen werden. 
 * Die FunctionLibFactory erbt sich vom DefaultHandler. 
 */
public final class FunctionLibFactory extends DefaultHandler {
	private XMLReader xmlReader;
	//private File file;
	private boolean insideFunction=false,insideAttribute=false,insideReturn=false;
	private String inside;
	private StringBuffer content=new StringBuffer();
	/**
	 * Definiert den Default SAX Parser
	 */
	public final static String DEFAULT_SAX_PARSER="org.apache.xerces.parsers.SAXParser";
	
	
	private static Map<String,FunctionLib> hashLib=new HashMap<String,FunctionLib>();
	private static FunctionLib systemFLD;
	private FunctionLib lib=new FunctionLib();
	private FunctionLibFunction function;

	private FunctionLibFunctionArg arg;
	
	private final static String FLD_1_0=	"/resource/fld/web-cfmfunctionlibrary_1_0";
	

	
	/**
	 * Privater Konstruktor, der als Eingabe die FLD als InputStream erhaelt.
	 * @param saxParser String Klassenpfad zum Sax Parser.
	 * @param is InputStream auf die TLD.
	 * @throws FunctionLibException
	 
	private FunctionLibFactory(String saxParser,InputSource is) throws FunctionLibException {
		super();
		init(saxParser,is);
	}*/
	
	/**
	 * Privater Konstruktor, der als Eingabe die FLD als File Objekt erhaelt.
	 * @param saxParser String Klassenpfad zum Sax Parser.
	 * @param file File Objekt auf die TLD.
	 * @throws FunctionLibException
	 */
	private FunctionLibFactory(String saxParser,Resource file) throws FunctionLibException {
		super();
		Reader r=null;
		try {
			init(saxParser,new InputSource(r=IOUtil.getReader(file.getInputStream(), (Charset)null)));
		} catch (IOException e) {
			throw new FunctionLibException("File not found: "+e.getMessage());
		}
		finally {
			IOUtil.closeEL(r);
		}
	}
	
	/**
	 * Privater Konstruktor nur mit Sax Parser Definition, liest Default FLD vom System ein.
	 * @param saxParser String Klassenpfad zum Sax Parser.
	 * @throws FunctionLibException
	 */
	private FunctionLibFactory(String saxParser) throws FunctionLibException {
		super();
		InputSource is=new InputSource(this.getClass().getResourceAsStream(FLD_1_0) );
		init(saxParser,is);		
	}
	
	/**
	 * Generelle Initialisierungsmetode der Konstruktoren.
	 * @param saxParser String Klassenpfad zum Sax Parser.
	 * @param is InputStream auf die TLD.
	 * @throws FunctionLibException
	 */
	private void init(String saxParser,InputSource is) throws FunctionLibException	{
		
		
		
		try {

			xmlReader=XMLUtil.createXMLReader(saxParser);
			xmlReader.setContentHandler(this);
			xmlReader.setErrorHandler(this);
			xmlReader.setEntityResolver(new FunctionLibEntityResolver());
			xmlReader.parse(is);
		} catch (IOException e) {
			
			throw new FunctionLibException("IO Exception: "+e.getMessage());
		} catch (SAXException e) {
			throw new FunctionLibException("SaxException: "+e.getMessage());
		}
		
    }
	    
	/**
	 * Geerbte Methode von org.xml.sax.ContentHandler, 
	 * wird bei durchparsen des XML, beim Auftreten eines Start-Tag aufgerufen.
	 *  
	 * @see org.xml.sax.ContentHandler#startElement(String, String, String, Attributes)
	 */
    public void startElement (String uri, String name,
			      String qName, Attributes atts)	{
    // Start Function
    	inside=qName;
    	if(qName.equals("function")) startFunction();
    	else if(qName.equals("argument")) startArg();
    	else if(qName.equals("return")) startReturn();
    }
    
	/**
	 * Geerbte Methode von org.xml.sax.ContentHandler, 
	 * wird bei durchparsen des XML, beim auftreten eines End-Tag aufgerufen.
	 *  
	 * @see org.xml.sax.ContentHandler#endElement(String, String, String)
	 */
    public void endElement (String uri, String name, String qName)	{
		setContent(content.toString().trim());
		content=new StringBuffer();
    	inside="";
    	if(qName.equals("function")) endFunction();
    	else if(qName.equals("argument")) endArg();
    	else if(qName.equals("return")) endReturn();
		
    }
    
	/**
	 * Wird jedesmal wenn das Tag function beginnt aufgerufen, 
	 * um intern in einen anderen Zustand zu gelangen.
	 */
    private void startFunction()	{
    	function=new FunctionLibFunction();
    	insideFunction=true;
    }
    
	/**
	 * Wird jedesmal wenn das Tag function endet aufgerufen, 
	 * um intern in einen anderen Zustand zu gelangen.
	 */
    private void endFunction()	{
    	lib.setFunction(function);
    	insideFunction=false;
    }
    
	/**
	 * Wird jedesmal wenn das Tag argument beginnt aufgerufen, 
	 * um intern in einen anderen Zustand zu gelangen.
	 */
    private void startArg()	{
    	insideAttribute=true;
    	arg=new FunctionLibFunctionArg();
    }
    
    /**
	 * Wird jedesmal wenn das Tag argument endet aufgerufen, 
	 * um intern in einen anderen Zustand zu gelangen.
	 */
    private void endArg()	{
    	function.setArg(arg);
    	insideAttribute=false;
    }
    
    /**
	 * Wird jedesmal wenn das Tag return beginnt aufgerufen, 
	 * um intern in einen anderen Zustand zu gelangen.
	 */
    private void startReturn()	{
    	insideReturn=true;
    }
    
    /**
	 * Wird jedesmal wenn das Tag return endet aufgerufen, 
	 * um intern in einen anderen Zustand zu gelangen.
	 */
    private void endReturn()	{
    	insideReturn=false;
    }
    
	/**
	 * Geerbte Methode von org.xml.sax.ContentHandler, 
	 * wird bei durchparsen des XML, zum einlesen des Content eines Body Element aufgerufen.
	 * 
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	public void characters (char ch[], int start, int length)	{
		content.append(new String(ch,start,length));
	}
	
	private void setContent(String value)	{
		if(insideFunction)	{			
			// Attributes Value
			if(insideAttribute)	{
				if(inside.equals("type")) arg.setType(value);
				else if(inside.equals("name")) arg.setName(value);
				else if(inside.equals("default")) arg.setDefaultValue(value);
				else if(inside.equals("default-value")) arg.setDefaultValue(value); // deprecated
				else if(inside.equals("status")) arg.setStatus(TagLibFactory.toStatus(value));
				else if(inside.equals("description")) arg.setDescription(value);
				else if(inside.equals("alias")) arg.setAlias(value);
				
				else if(inside.equals("required"))	{
					arg.setRequired(value);
					if(arg.isRequired())
						function.setArgMin(function.getArgMin()+1);
				}
			}
			// Return Values
			else if(insideReturn)	{
				if(inside.equals("type"))
					function.setReturn(value);
			}
			// Function Value
			else	{
				if(inside.equals("name"))
					function.setName(value);
				
				else if(inside.equals("class"))
					function.setCls(value);
				
				else if(inside.equals("tte-class"))
					function.setTteClass(value);
    			

				else if(inside.equals("description"))
					function.setDescription(value);

				else if(inside.equals("member-name"))
					function.setMemberName(value);
				else if(inside.equals("member-chaining"))
					function.setMemberChaining(Caster.toBooleanValue(value,false));
				
				else if(inside.equals("status"))
					function.setStatus(TagLibFactory.toStatus(value));
				
				else if(inside.equals("argument-type"))
					function.setArgType(value.equalsIgnoreCase("dynamic")?FunctionLibFunction.ARG_DYNAMIC:FunctionLibFunction.ARG_FIX);
				
				else if(inside.equals("argument-min"))
					function.setArgMin(Integer.parseInt(value));
				
				else if(inside.equals("argument-max"))
					function.setArgMax(Integer.parseInt(value));
			}
			
		}
		else {
			//function lib values
			if(inside.equals("flib-version")) lib.setVersion(value);
			else if(inside.equals("short-name")) lib.setShortName(value);
			else if(inside.equals("uri")) { 
				try {
					lib.setUri(value);
				} catch (URISyntaxException e) {} 
			} 
			else if(inside.equals("display-name")) lib.setDisplayName(value);
			else if(inside.equals("description")) lib.setDescription(value);		
		}		
    }

	/**
	 * Gibt die interne FunctionLib zurueck.
	 * @return Interne Repraesentation der zu erstellenden FunctionLib.
	 */
	private FunctionLib getLib() {
		return lib;
	}
	
	/**
	 * Laedt mehrere FunctionLib's die innerhalb eines Verzeichnisses liegen.
	 * @param dir Verzeichnis im dem die FunctionLib's liegen.
	 * @return FunctionLib's als Array
	 * @throws FunctionLibException
	 */
	public static FunctionLib[] loadFromDirectory(Resource dir) throws FunctionLibException	{
		return loadFromDirectory(dir,DEFAULT_SAX_PARSER);
	}
	
	/**
	 * Laedt mehrere FunctionLib's die innerhalb eines Verzeichnisses liegen.
	 * @param dir Verzeichnis im dem die FunctionLib's liegen.
	 * @param saxParser Definition des Sax Parser mit dem die FunctionLib's eingelesen werden sollen.
	 * @return FunctionLib's als Array
	 * @throws FunctionLibException
	 */
	public static FunctionLib[] loadFromDirectory(Resource dir,String saxParser) throws FunctionLibException	{
		if(!dir.isDirectory())return new FunctionLib[0];
		ArrayList<FunctionLib> arr=new ArrayList<FunctionLib>();
		
		Resource[] files=dir.listResources(new ExtensionResourceFilter("fld"));
		for(int i=0;i<files.length;i++)	{
			if(files[i].isFile())
				arr.add(FunctionLibFactory.loadFromFile(files[i],saxParser));				
		}
	
		return arr.toArray(new FunctionLib[arr.size()]);
	}
	/**
	 * Laedt eine einzelne FunctionLib.
	 * @param file FLD die geladen werden soll.
	 * @return FunctonLib
	 * @throws FunctionLibException
	 */
	public static FunctionLib loadFromFile(Resource file) throws FunctionLibException	{
		return loadFromFile(file,DEFAULT_SAX_PARSER);
	}
	
	/**
	 * Laedt eine einzelne FunctionLib.
	 * @param res FLD die geladen werden soll.
	 * @param saxParser Definition des Sax Parser mit dem die FunctionLib eingelsesen werden soll.
	 * @return FunctionLib
	 * @throws FunctionLibException
	 */
	public static FunctionLib loadFromFile(Resource res,String saxParser) throws FunctionLibException	{
		// Read in XML
		FunctionLib lib=FunctionLibFactory.hashLib.get(ResourceUtil.getCanonicalPathEL(res));//getHashLib(file.getAbsolutePath());
		if(lib==null)	{
			lib=new FunctionLibFactory(saxParser,res).getLib();
			FunctionLibFactory.hashLib.put(ResourceUtil.getCanonicalPathEL(res),lib);
		}
		lib.setSource(res.toString());
		
		return lib;
	}
	
	/**
	 * Laedt die Systeminterne FLD.
	 * @return FunctionLib
	 * @throws FunctionLibException
	 */
	public static FunctionLib loadFromSystem() throws FunctionLibException	{
		return loadFromSystem(DEFAULT_SAX_PARSER);
	}
	
	/**
	 * Laedt die Systeminterne FLD.
	 * @param saxParser Definition des Sax Parser mit dem die FunctionLib eingelsesen werden soll.
	 * @return FunctionLib
	 * @throws FunctionLibException
	 */
	public static FunctionLib loadFromSystem(String saxParser) throws FunctionLibException	{
		if(systemFLD==null)
			systemFLD=new FunctionLibFactory(saxParser).getLib();
		
		return systemFLD;
	}
	
	/**
	 * return one FunctionLib contain content of all given Function Libs
	 * @param flds
	 * @return combined function lib
	 */
	public static FunctionLib combineFLDs(FunctionLib[] flds){
		FunctionLib fl = new FunctionLib();
		if(ArrayUtil.isEmpty(flds)) return fl ;

		setAttributes(flds[0],fl);
		
		// add functions
		for(int i=0;i<flds.length;i++){
			copyFunctions(flds[i],fl);
		}
		return fl;
	}
	
	public static FunctionLib combineFLDs(Set flds){
		FunctionLib newFL = new FunctionLib(),tmp;
		if(flds.size()==0) return newFL ;

		Iterator it = flds.iterator();
		int count=0;
		while(it.hasNext()){
			tmp=(FunctionLib) it.next();
			if(count++==0) setAttributes(tmp,newFL);
			copyFunctions(tmp,newFL);
		}
		return newFL;
	}

	/**
	 * copy function from one FunctionLib to a other
	 * @param extFL
	 * @param newFL
	 */
	private static void copyFunctions(FunctionLib extFL, FunctionLib newFL) {
		Iterator<Entry<String, FunctionLibFunction>> it = extFL.getFunctions().entrySet().iterator();
		FunctionLibFunction flf;
		while(it.hasNext()){
			flf= it.next().getValue(); // TODO function must be duplicated because it gets a new FunctionLib assigned
			newFL.setFunction(flf);
		}
	}

	/**
	 * copy attributes from old fld to the new
	 * @param extFL
	 * @param newFL
	 */
	private static void setAttributes(FunctionLib extFL, FunctionLib newFL) {
		newFL.setDescription(extFL.getDescription());
		newFL.setDisplayName(extFL.getDisplayName());
		newFL.setShortName(extFL.getShortName());
		newFL.setUri(extFL.getUri());
		newFL.setVersion(extFL.getVersion());
	}
	
}