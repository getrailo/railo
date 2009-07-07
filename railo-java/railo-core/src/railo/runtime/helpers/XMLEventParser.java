package railo.runtime.helpers;

import java.io.InputStream;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.runtime.PageContext;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.UDF;



/**
 * Sax Parse with callback to CFC Methods
 */
public final class XMLEventParser extends DefaultHandler {
	
	private UDF startDocument;
	private UDF startElement;
	private UDF body;
	private UDF endElement;
	private UDF endDocument;
	private UDF error;
	
	private StringBuffer sbBody;
	private PageContext pc;
	private Struct att;
	/**
	 * Field <code>DEFAULT_SAX_PARSER</code>
	 */
	public final static String DEFAULT_SAX_PARSER="org.apache.xerces.parsers.SAXParser";
	
	/**
	 * constructor of the class
	 * @param pc
	 * @param startDocument
	 * @param startElement
	 * @param body
	 * @param endElement
	 * @param endDocument
	 * @param error
	 */
	public XMLEventParser(
			PageContext pc,
			UDF startDocument,
			UDF startElement,
			UDF body,
			UDF endElement,
			UDF endDocument,
			UDF error) {
		
		this.pc=pc;
		this.startDocument=startDocument;
		this.startElement=startElement;
		this.body=body;
		this.endElement=endElement;
		this.endDocument=endDocument;
		this.error=error;

	}

	/**
	 * start execution of the parser
	 * @param xmlFile
	 * @throws PageException
	 */
	public void start(Resource xmlFile) throws PageException {
		start(xmlFile,DEFAULT_SAX_PARSER);
	}
	
	/**
	 * start execution of the parser
	 * @param xmlFile
	 * @param saxParserCass
	 * @throws PageException
	 */
	public void start(Resource xmlFile,String saxParserCass) throws PageException {
		InputStream is=null;
		try {
			XMLReader xmlReader = XMLReaderFactory.createXMLReader(saxParserCass);
			xmlReader.setContentHandler(this);
			xmlReader.setErrorHandler(this);
			xmlReader.parse(new InputSource(is=IOUtil.toBufferedInputStream(xmlFile.getInputStream())));
		} catch (Exception e) {
			throw Caster.toPageException(e); 
		}	
		finally {
			IOUtil.closeEL(is);
		}
		
	}

	/**
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	public void characters(char[] ch, int start, int length) throws SAXException {
		sbBody.append(ch,start,length);
	}

	/**
	 * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
	 */
	public void error(SAXParseException e) throws SAXException {
		error(Caster.toPageException(e));
	}
	/**
	 * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
	 */
	public void fatalError(SAXParseException e) throws SAXException {
		error(Caster.toPageException(e));
	}
	/**
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		sbBody=new StringBuffer();
		att = toStruct(attributes);
		call(startElement,new Object[]{uri,localName,qName,att});
	}

	/**
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void endElement(String uri, String localName, String qName) throws SAXException {
		call(body,new Object[]{sbBody.toString()});
		call(endElement,new Object[]{uri,localName,qName,att});
	}
	
	/**
	 * @see org.xml.sax.helpers.DefaultHandler#startDocument()
	 */
	public void startDocument() throws SAXException {
		call(startDocument,new Object[]{});
	}
	
	/**
	 * @see org.xml.sax.helpers.DefaultHandler#endDocument()
	 */
	public void endDocument() throws SAXException {
		call(endDocument,new Object[]{});
	}
	
	/**
	 * call a user defined function
	 * @param udf
	 * @param arguments
	 */
	private void call(UDF udf, Object[] arguments) {
		try {
			udf.call(pc,arguments,false);
		} catch (PageException pe) {
			error(pe);
		}
	}
	
	/**
	 * call back error function if a error occour
	 * @param pe
	 */
	private void error(PageException pe) {
		try {
			// TLPC
			pc=ThreadLocalPageContext.get(pc);
			error.call(pc,new Object[]{pe.getCatchBlock(pc)},false);
		} 
		catch (PageException e) {}
	}
	
	/**
	 * cast a Attributes object to a Struct
	 * @param att
	 * @return Attributes as Struct
	 */
	private Struct toStruct(Attributes att) {
		int len=att.getLength();
		Struct sct=new StructImpl();
		for(int i=0;i<len;i++) {
			sct.setEL(att.getQName(i),att.getValue(i));
		}
		return sct;
	}
}