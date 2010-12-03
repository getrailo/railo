package railo.runtime.cache.eh.remote.rest.sax;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import railo.loader.util.Util;

public class CacheFactory extends DefaultHandler {
	
	public final static String DEFAULT_SAX_PARSER="org.apache.xerces.parsers.SAXParser";
		
	private XMLReader xmlReader;
	
	protected String inside;
	protected StringBuffer content=new StringBuffer();

	private boolean insideCacheConfiguration;

	private CacheConfiguration cc=new CacheConfiguration();
	private CacheStatistics cs=new CacheStatistics();

	private boolean insideStatistics;

	//private boolean insideError;
	//private boolean insideMessage;



	/**
	 * Privater Konstruktor, der als Eingabe die TLD als File Objekt erh�lt.
	 * @param saxParser String Klassenpfad zum Sax Parser.
	 * @param file File Objekt auf die TLD.
	 * @throws IOException 
	 * @throws SAXException 
	 */
	public CacheFactory(InputStream in) throws IOException, SAXException {
		super();
		init(in);
	}
	
	/**
	 * Generelle Initialisierungsmetode der Konstruktoren.
	 * @param saxParser String Klassenpfad zum Sax Parser.
	 * @param  is InputStream auf die TLD.
	 * @throws SAXException 
	 * @throws IOException 
	 */
	protected void init(InputStream in) throws IOException, SAXException 	{
		Reader r=null;
		try {
			InputSource is=new InputSource(in);
			
			xmlReader=XMLReaderFactory.createXMLReader(DEFAULT_SAX_PARSER);
			xmlReader.setContentHandler(this);
			xmlReader.setErrorHandler(this);
			xmlReader.parse(is);
			
		}
		finally {
			Util.closeEL(r);
		}
    }

	/**
	 * Geerbte Methode von org.xml.sax.ContentHandler, 
	 * wird bei durchparsen des XML, beim Auftreten eines Start-Tag aufgerufen.
	 *  
	 * @see org.xml.sax.ContentHandler#startElement(String, String, String, Attributes)
	 */
	public final void startElement(String uri, String name, String qName, Attributes atts) {
		inside=qName;

		if(qName.equalsIgnoreCase("cacheConfiguration")) insideCacheConfiguration=true;
		else if(qName.equalsIgnoreCase("statistics")) insideStatistics=true;
		
		//doStartElement(uri, name, qName, atts);
	}
	
	
	
	
	/**
	 * Geerbte Methode von org.xml.sax.ContentHandler, 
	 * wird bei durchparsen des XML, beim auftreten eines End-Tag aufgerufen.
	 *  
	 * @see org.xml.sax.ContentHandler#endElement(String, String, String)
	 */
	public final void endElement(String uri, String name, String qName) throws SAXException {
		_setContent(content.toString().trim());
		content=new StringBuffer();
		inside="";

		if(qName.equalsIgnoreCase("cacheConfiguration")) insideCacheConfiguration=false;
		else if(qName.equalsIgnoreCase("statistics")) insideStatistics=false;
		//doEndElement(uri, name, qName);
	}
	
	
	
    private void _setContent(String value) throws SAXException {
    	
    	/*if(insideError && insideMessage)	{
    		throw new SAXException(value);
    	}*/
    	setContent(value);
    	
    }

    protected void setContent(String value) throws SAXException 	{
		if(insideCacheConfiguration)	{
			if("clearOnFlush".equalsIgnoreCase(inside))
				cc.setClearOnFlush(toBooleanValue(value,true));
			else if("diskExpiryThreadIntervalSeconds".equalsIgnoreCase(inside))
				cc.setDiskExpiryThreadIntervalSeconds(toInt(value,0));
			else if("diskPersistent".equalsIgnoreCase(inside))
				cc.setDiskPersistent(toBooleanValue(value,false));
			else if("diskSpoolBufferSizeMB".equalsIgnoreCase(inside))
				cc.setDiskSpoolBufferSize(toInt(value,0)*1024L*1024L);
			else if("eternal".equalsIgnoreCase(inside))
				cc.setEternal(toBooleanValue(value,false));
			else if("maxElementsInMemory".equalsIgnoreCase(inside))
				cc.setMaxElementsInMemory(toInt(value,0));
			else if("maxElementsOnDisk".equalsIgnoreCase(inside))
				cc.setMaxElementsOnDisk(toInt(value,0));
			else if("name".equalsIgnoreCase(inside))
				cc.setName(value);
			else if("overflowToDisk".equalsIgnoreCase(inside))
				cc.setOverflowToDisk(toBooleanValue(value,true));
			else if("timeToIdleSeconds".equalsIgnoreCase(inside))
				cc.setTimeToIdleSeconds(toInt(value,0));
			else if("timeToLiveSeconds".equalsIgnoreCase(inside))
				cc.setTimeToLiveSeconds(toInt(value,0));
    	}
		else if(insideStatistics){
			if("averageGetTime".equalsIgnoreCase(inside))
				cs.setAverageGetTime(toDoubleValue(value,0));
			else if("cacheHits".equalsIgnoreCase(inside))
				cs.setCacheHits(toInt(value,0));
			else if("diskStoreSize".equalsIgnoreCase(inside))
				cs.setDiskStoreSize(toInt(value,0));
			else if("evictionCount".equalsIgnoreCase(inside))
				cs.setEvictionCount(toInt(value,0));
			else if("inMemoryHits".equalsIgnoreCase(inside))
				cs.setInMemoryHits(toInt(value,0));
			else if("memoryStoreSize".equalsIgnoreCase(inside))
				cs.setMemoryStoreSize(toInt(value,0));
			else if("misses".equalsIgnoreCase(inside))
				cs.setMisses(toInt(value,0));
			else if("onDiskHits".equalsIgnoreCase(inside))
				cs.setOnDiskHits(toInt(value,0));
			else if("size".equalsIgnoreCase(inside))
				cs.setSize(toInt(value,0));
			else if("statisticsAccuracy".equalsIgnoreCase(inside))
				cs.setStatisticsAccuracy(value);
		}
		else{
			//System.err.println(inside+":"+value);
		}
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
	
	

	/**
	 * @return the cc
	 */
	public CacheConfiguration getCacheConfiguration() {
		return cc;
	}
	
	private boolean toBooleanValue(String str, boolean defaultValue) {
		str=str.trim().toLowerCase();
		if("true".equalsIgnoreCase(str)) return true;
		else if("false".equalsIgnoreCase(str)) return false;
		return defaultValue;
	}


	private double toDoubleValue(String str, int defaultValue) {
		try{
			return Double.parseDouble(str);
		}
		catch(Throwable t){
			return defaultValue;
		}
	}
	private int toInt(String str, int defaultValue) {
		try{
			return Integer.parseInt(str);
		}
		catch(Throwable t){
			return defaultValue;
		}
	}

	public CacheMeta getMeta() {
		return new CacheMeta(cc,cs);
	}
}