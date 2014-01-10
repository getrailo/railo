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

import railo.commons.io.IOUtil;
import railo.commons.io.SystemUtil;
import railo.commons.io.res.Resource;
import railo.commons.lang.StringUtil;
import railo.runtime.exp.DatabaseException;
import railo.runtime.exp.PageException;
import railo.runtime.text.xml.XMLUtil;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Query;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

public final class RSSHandler extends DefaultHandler {
	
	public final static String DEFAULT_SAX_PARSER="org.apache.xerces.parsers.SAXParser";

	private static final Key RSSLINK = KeyImpl.intern("RSSLINK");
	private static final Key CONTENT = KeyImpl.intern("CONTENT");

	private static final Key LINK = KeyImpl.intern("LINK");
	private static final Key DESCRIPTION = KeyImpl.intern("DESCRIPTION");
	
	private static Collection.Key[] COLUMNS=new Collection.Key[]{
		KeyImpl.intern("AUTHOREMAIL"),
		KeyImpl.intern("AUTHORNAME"),
		KeyImpl.intern("AUTHORURI"),
		KeyImpl.intern("CATEGORYLABEL"),
		KeyImpl.intern("CATEGORYSCHEME"),
		KeyImpl.intern("CATEGORYTERM"),
		KeyImpl.intern("COMMENTS"),
		CONTENT,
		KeyImpl.intern("CONTENTMODE"),
		KeyImpl.intern("CONTENTSRC"),
		KeyImpl.intern("CONTENTTYPE"),
		KeyImpl.intern("CONTRIBUTOREMAIL"),
		KeyImpl.intern("CONTRIBUTORNAME"),
		KeyImpl.intern("CONTRIBUTORURI"),
		KeyImpl.intern("CREATEDDATE"),
		KeyImpl.intern("EXPIRATIONDATE"),
		KeyImpl.intern("ID"),
		KeyImpl.intern("IDPERMALINK"),
		KeyImpl.intern("LINKHREF"),
		KeyImpl.intern("LINKHREFLANG"),
		KeyImpl.intern("LINKLENGTH"),
		KeyImpl.intern("LINKREL"),
		KeyImpl.intern("LINKTITLE"),
		KeyImpl.intern("LINKTYPE"),
		KeyImpl.intern("PUBLISHEDDATE"),
		KeyImpl.intern("RIGHTS"),
		RSSLINK,
		KeyImpl.intern("SOURCE"),
		KeyImpl.intern("SOURCEURL"),
		KeyImpl.intern("SUMMARY"),
		KeyImpl.intern("SUMMARYMODE"),
		KeyImpl.intern("SUMMARYSRC"),
		KeyImpl.intern("SUMMARYTYPE"),
		KeyImpl.intern("TITLE"),
		KeyImpl.intern("TITLETYPE"),
		KeyImpl.intern("UPDATEDDATE"),
		KeyImpl.intern("URI"),
		KeyImpl.intern("XMLBASE")
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
	 * @throws DatabaseException 
	 */
	public RSSHandler(Resource res) throws IOException, SAXException, DatabaseException {
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
	 * @throws DatabaseException 
	 */
	public RSSHandler(InputStream stream) throws IOException, SAXException, DatabaseException {
		InputSource is=new InputSource(IOUtil.getReader(stream, SystemUtil.getCharset()));
		init(DEFAULT_SAX_PARSER,is);
	}
	
	private void init(String saxParser,InputSource is) throws SAXException, IOException, DatabaseException	{
		properties=new StructImpl();
		items=new QueryImpl(COLUMNS,0,"query");
		xmlReader=XMLUtil.createXMLReader(saxParser);
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

	@Override
	public void startElement(String uri, String name, String qName, Attributes atts) {
		inside = KeyImpl.getInstance(qName);
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
    
	@Override
	public void endElement(String uri, String name, String qName) {
		setContent(content.toString().trim());
		content=new StringBuffer();
		inside=null;
		lcInside="";
		
		if(qName.equals("image")) insideImage=false;
		if(qName.equals("item")) insideItem=false;
	}
	
	
    @Override
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