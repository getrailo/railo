package railo.runtime.text.feed;

import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.Locator2;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import railo.commons.io.IOUtil;
import railo.commons.io.SystemUtil;
import railo.commons.io.res.Resource;
import railo.commons.lang.StringUtil;
import railo.runtime.exp.PageException;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Query;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

public final class RSSHandler extends DefaultHandler {
	
	public final static String DEFAULT_SAX_PARSER="org.apache.xerces.parsers.SAXParser";

	private static final Key RSSLINK = KeyImpl.getInstance("RSSLINK");
	private static final Key CONTENT = KeyImpl.getInstance("CONTENT");

	private static final Key LINK = KeyImpl.getInstance("LINK");
	private static final Key DESCRIPTION = KeyImpl.getInstance("DESCRIPTION");
	
	private static Collection.Key[] COLUMNS=new Collection.Key[]{
		KeyImpl.getInstance("AUTHOREMAIL"),
		KeyImpl.getInstance("AUTHORNAME"),
		KeyImpl.getInstance("AUTHORURI"),
		KeyImpl.getInstance("CATEGORYLABEL"),
		KeyImpl.getInstance("CATEGORYSCHEME"),
		KeyImpl.getInstance("CATEGORYTERM"),
		KeyImpl.getInstance("COMMENTS"),
		CONTENT,
		KeyImpl.getInstance("CONTENTMODE"),
		KeyImpl.getInstance("CONTENTSRC"),
		KeyImpl.getInstance("CONTENTTYPE"),
		KeyImpl.getInstance("CONTRIBUTOREMAIL"),
		KeyImpl.getInstance("CONTRIBUTORNAME"),
		KeyImpl.getInstance("CONTRIBUTORURI"),
		KeyImpl.getInstance("CREATEDDATE"),
		KeyImpl.getInstance("EXPIRATIONDATE"),
		KeyImpl.getInstance("ID"),
		KeyImpl.getInstance("IDPERMALINK"),
		KeyImpl.getInstance("LINKHREF"),
		KeyImpl.getInstance("LINKHREFLANG"),
		KeyImpl.getInstance("LINKLENGTH"),
		KeyImpl.getInstance("LINKREL"),
		KeyImpl.getInstance("LINKTITLE"),
		KeyImpl.getInstance("LINKTYPE"),
		KeyImpl.getInstance("PUBLISHEDDATE"),
		KeyImpl.getInstance("RIGHTS"),
		RSSLINK,
		KeyImpl.getInstance("SOURCE"),
		KeyImpl.getInstance("SOURCEURL"),
		KeyImpl.getInstance("SUMMARY"),
		KeyImpl.getInstance("SUMMARYMODE"),
		KeyImpl.getInstance("SUMMARYSRC"),
		KeyImpl.getInstance("SUMMARYTYPE"),
		KeyImpl.getInstance("TITLE"),
		KeyImpl.getInstance("TITLETYPE"),
		KeyImpl.getInstance("UPDATEDDATE"),
		KeyImpl.getInstance("URI"),
		KeyImpl.getInstance("XMLBASE")
	};
	
	
	private XMLReader xmlReader;

	private String lcInside;
	private StringBuffer content=new StringBuffer();

	private boolean insideImage;
	private boolean insideItem;

	private Struct image;
	private Struct properties;
	private Query items;

	private Collection.Key inside;
	
	/**
	 * Constructor of the class
	 * @param res
	 * @throws IOException
	 * @throws SAXException 
	 */
	public RSSHandler(Resource res) throws IOException, SAXException {
		InputStream is=null;
		try {
			InputSource source=new InputSource(is=res.getInputStream());
			source.setSystemId(res.getPath());
			init(DEFAULT_SAX_PARSER,source);
		} 
		finally {
			IOUtil.closeEL(is);
		}
	}

	/**
	 * Constructor of the class
	 * @param stream
	 * @throws IOException
	 * @throws SAXException 
	 */
	public RSSHandler(InputStream stream) throws IOException, SAXException {
		InputSource is=new InputSource(IOUtil.getReader(stream, SystemUtil.getCharset()));
		init(DEFAULT_SAX_PARSER,is);
	}
	
	private void init(String saxParser,InputSource is) throws SAXException, IOException	{
		properties=new StructImpl();
		items=new QueryImpl(COLUMNS,0,"query");
		xmlReader=XMLReaderFactory.createXMLReader(saxParser);
		xmlReader.setContentHandler(this);
		xmlReader.setErrorHandler(this);
		
		//xmlReader.setEntityResolver(new TagLibEntityResolver());
		xmlReader.parse(is);
		
		//properties.setEL("encoding",is.getEncoding());
		
    }
	
	public void setDocumentLocator(Locator locator) { 
		  if (locator instanceof Locator2) {
		    Locator2 locator2 = (Locator2) locator;
		    properties.setEL("encoding", locator2.getEncoding());
		  } 
		}

	/**
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String name, String qName, Attributes atts) {
		inside = KeyImpl.init(qName);
		lcInside=qName.toLowerCase();
		if(lcInside.equals("image")) 		insideImage=true;
		else if(qName.equals("item")) 	{
			items.addRow();
			insideItem=true;
		}
		else if(lcInside.equals("rss")) 	{
			String version = atts.getValue("version");
			if(!StringUtil.isEmpty(version))
				properties.setEL("version", "rss_"+version);
		}
		
		/* / cloud
		else if(!insideItem && lcInside.equals("cloud")) 	{
			
			
			
			String url = atts.getValue("url");
			if(!StringUtil.isEmpty(url))items.setAtEL("LINKHREF", items.getRowCount(), url);
			String length = atts.getValue("length");
			if(!StringUtil.isEmpty(length))items.setAtEL("LINKLENGTH", items.getRowCount(), length);
			String type = atts.getValue("type");
			if(!StringUtil.isEmpty(type))items.setAtEL("LINKTYPE", items.getRowCount(), type);
		}*/
		
		
		// enclosure
		else if(insideItem && lcInside.equals("enclosure")) 	{
			String url = atts.getValue("url");
			if(!StringUtil.isEmpty(url))items.setAtEL("LINKHREF", items.getRowCount(), url);
			String length = atts.getValue("length");
			if(!StringUtil.isEmpty(length))items.setAtEL("LINKLENGTH", items.getRowCount(), length);
			String type = atts.getValue("type");
			if(!StringUtil.isEmpty(type))items.setAtEL("LINKTYPE", items.getRowCount(), type);
		}
		
		else if(atts.getLength()>0) {
			int len=atts.getLength();
			Struct sct=new StructImpl();
			for(int i=0;i<len;i++) {
				sct.setEL(atts.getQName(i), atts.getValue(i));
			}
			properties.setEL(inside, sct);
		}
		
		//<enclosure url="http://www.scripting.com/mp3s/weatherReportDicksPicsVol7.mp3" length="6182912" type="audio/mpeg"/>
	}
    
	/**
	 * Geerbte Methode von org.xml.sax.ContentHandler, 
	 * wird bei durchparsen des XML, beim auftreten eines End-Tag aufgerufen.
	 *  
	 * @see org.xml.sax.ContentHandler#endElement(String, String, String)
	 */
	public void endElement(String uri, String name, String qName) {
		setContent(content.toString().trim());
		content=new StringBuffer();
		inside=null;
		lcInside="";
		
		if(qName.equals("image")) insideImage=false;
		if(qName.equals("item")) insideItem=false;
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
		if(StringUtil.isEmpty(lcInside)) return;
		
		if(insideImage)	{
			if(image==null){
				image=new StructImpl();
				properties.setEL("image",image);
			}
			image.setEL(inside,value);
		}
		else if(insideItem)	{
			try {
				items.setAt(toItemColumn(inside), items.getRowCount(), value);
			} catch (PageException e) {
				//print.err(inside);
			}
			
		}
		else {
			if(!(StringUtil.isEmpty(value,true) && properties.containsKey(inside)))
				properties.setEL(inside,value);
		}	
    }

	private Collection.Key toItemColumn(Collection.Key key) {
		if(key.equalsIgnoreCase(LINK))			return RSSLINK;
		else if(key.equalsIgnoreCase(DESCRIPTION))return CONTENT;
		return key;
	}

	/**
	 * @return the properties
	 */
	public Struct getProperties() {
		return properties;
	}

	/**
	 * @return the items
	 */
	public Query getItems() {
		return items;
	}
	
	
	/*public static void main(String[] args) throws IOException, SAXException {
		ResourceProvider frp = ResourcesImpl.getFileResourceProvider();
		Resource res = frp.getResource("/Users/mic/Projects/Railo/webroot/jm/feed/092.xml");
		RSSHandler rss=new RSSHandler(res);
		print.out(rss.getProperties());
		print.out(rss.getItems());
		
	}*/
}