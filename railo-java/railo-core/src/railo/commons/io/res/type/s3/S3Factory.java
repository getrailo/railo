package railo.commons.io.res.type.s3;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import railo.loader.util.Util;
import railo.runtime.text.xml.XMLUtil;

/**
 * Die Klasse TagLibFactory liest die XML Repr�sentation einer TLD ein 
 * und l�dt diese in eine Objektstruktur. 
 * Sie tut dieses mithilfe eines Sax Parser.
 * Die Klasse kann sowohl einzelne Files oder gar ganze Verzeichnisse von TLD laden.
 */
public abstract class S3Factory extends DefaultHandler {
	
	public final static String DEFAULT_SAX_PARSER="org.apache.xerces.parsers.SAXParser";
		
	private XMLReader xmlReader;
	
	protected String inside;
	protected StringBuffer content=new StringBuffer();

	private boolean insideError;
	private boolean insideMessage;



	/**
	 * Privater Konstruktor, der als Eingabe die TLD als File Objekt erh�lt.
	 * @param saxParser String Klassenpfad zum Sax Parser.
	 * @param file File Objekt auf die TLD.
	 * @throws IOException 
	 * @throws SAXException 
	 */
	public S3Factory() {
		
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
			xmlReader=XMLUtil.createXMLReader(DEFAULT_SAX_PARSER);
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

		if(qName.equalsIgnoreCase("Error")) insideError=true;
		if(qName.equalsIgnoreCase("Message")) insideMessage=true;
		doStartElement(uri, name, qName, atts);
	}
	protected abstract void doStartElement(String uri, String name, String qName, Attributes atts);
    
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

		if(qName.equalsIgnoreCase("Error")) insideError=false;
		if(qName.equalsIgnoreCase("Message")) insideMessage=false;
		doEndElement(uri, name, qName);
	}
	
	public abstract void doEndElement(String uri, String name, String qName) throws SAXException;
	
	
    private void _setContent(String value) throws SAXException {
    	
    	if(insideError && insideMessage)	{
    		throw new SAXException(value);
    	}
    	setContent(value);
    	/*
    	<?xml version="1.0" encoding="UTF-8"?>
		<Error>
			<Code>SignatureDoesNotMatch</Code>
			<Message>The request signature we calculated does not match the signature you provided. 
				Check your key and signing method.</Message>
			<RequestId>53DE01E3379AEF9F</RequestId>
			<SignatureProvided>CsJJe9qgVVxoOPyAZ48XhFd8VJs=</SignatureProvided>
			<StringToSignBytes>47 45 54 0a 0a 0a 57 65 64 2c 20 30 35 20 4d 61 72 20 32 30 30 38 20 31 31 3a 31 39 3a 34 33 20 47 4d 54 0a 2f</StringToSignBytes>
			<AWSAccessKeyId>03SG52G1QX3EVP5FEMG2</AWSAccessKeyId>
			<HostId>TFyQxYQuisdThJrENWZW7Q1yp5mbVabV8jGx6B0m9pB6dSG/AJhpCTEWnQpW/otb</HostId>
			<StringToSign>GET

    	*/
    }

	protected abstract void setContent(String value) throws SAXException;

	/** 
     * Geerbte Methode von org.xml.sax.ContentHandler, 
	 * wird bei durchparsen des XML, zum einlesen des Content eines Body Element aufgerufen.
	 * 
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	public void characters (char ch[], int start, int length)	{
		content.append(new String(ch,start,length));
	}
}