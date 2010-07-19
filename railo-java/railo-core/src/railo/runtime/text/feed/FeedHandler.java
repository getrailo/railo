package railo.runtime.text.feed;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

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
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.CastableArray;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

public final class FeedHandler extends DefaultHandler {

	public final static String DEFAULT_SAX_PARSER="org.apache.xerces.parsers.SAXParser";

	private static final Key VALUE = KeyImpl.getInstance("value");
	
	private XMLReader xmlReader;

	//private StringBuffer content=new StringBuffer();


	private FeedStruct data;
	private String path=""; 
	private Collection.Key inside;
	private Stack parents=new Stack();
	private FeedDeclaration decl;

	private Map root=new HashMap();

	private boolean hasDC;
	
	
	/**
	 * Constructor of the class
	 * @param res
	 * @throws IOException
	 * @throws SAXException 
	 */
	public FeedHandler(Resource res) throws IOException, SAXException {
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
	public FeedHandler(InputSource is) throws IOException, SAXException {
		init(DEFAULT_SAX_PARSER,is);
		
	}

	/**
	 * Constructor of the class
	 * @param stream
	 * @throws IOException
	 * @throws SAXException 
	 */
	public FeedHandler(InputStream stream) throws IOException, SAXException {
		InputSource is=new InputSource(IOUtil.getReader(stream, SystemUtil.getCharset()));
		init(DEFAULT_SAX_PARSER,is);
	}
	
	private void init(String saxParser,InputSource is) throws SAXException, IOException	{
		//print.out("is:"+is);
		hasDC=false;
		data=new FeedStruct();
		xmlReader=XMLReaderFactory.createXMLReader(saxParser);
		xmlReader.setContentHandler(this);
		xmlReader.setErrorHandler(this);
		xmlReader.setDTDHandler(new DummyDTDHandler());
		xmlReader.parse(is);
    }
	
	/**
	 * @return the hasDC
	 */
	public boolean hasDC() {
		return hasDC;
	}
	public void setDocumentLocator(Locator locator) { 
		if (locator instanceof Locator2) {
			Locator2 locator2 = (Locator2) locator;
			root.put("encoding", locator2.getEncoding());
		} 
	}

	/**
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String name, String qName, Attributes atts) {
		if(qName.startsWith("dc:")){
			name="dc_"+name;
			hasDC=true;
		}
		inside = KeyImpl.init(name);
		if(StringUtil.isEmpty(path))path=name;
		else {
			path+="."+name;
		}
		if(decl==null){
			String decName=name;
			String version = atts.getValue("version");
			if("feed".equals(decName)) {
				if(!StringUtil.isEmpty(version))decName="atom_"+version;
				else decName="atom_1.0";
			}
			else {
				if(!StringUtil.isEmpty(version))decName+="_"+version;
			}
			decl=FeedDeclaration.getInstance(decName);
			root.put("version",decName);
			
			
		}
		
		
		FeedStruct sct=new FeedStruct(path,inside);
		// attributes
		Map attrs = getAttributes(atts, path);
		if(attrs!=null){
			Map.Entry entry;
			Iterator it = attrs.entrySet().iterator();
			sct.setHasAttribute(true);
			while(it.hasNext()){
				entry=(Entry) it.next();
				sct.setEL((String)entry.getKey(), entry.getValue());
			}
		}
		
		Object obj = data.get(inside, null);
		if(obj instanceof Array)	{
			((Array)obj).appendEL(sct);
		}
		else if(obj instanceof FeedStruct){
			Array arr = new ArrayImpl();
			arr.appendEL(obj);
			arr.appendEL(sct);
			data.setEL(inside, arr);
		}
		else if(obj instanceof String){
			// wenn wert schon existiert wird castableArray in serContent erstellt
		}
		else {
			El el=(El) decl.getDeclaration().get(path);
			if(el!=null && (el.getQuantity()==El.QUANTITY_0_N || el.getQuantity()==El.QUANTITY_1_N)){
				Array arr = new ArrayImpl();
				arr.appendEL(sct);
				data.setEL(inside, arr);
			}
			else data.setEL(inside, sct);
			
		}
		parents.add(data);
		data=sct;
		
		//<enclosure url="http://www.scripting.com/mp3s/weatherReportDicksPicsVol7.mp3" length="6182912" type="audio/mpeg"/>
	}
	

	public void endElement(String uri, String name, String qName) {
		
		//setContent(content.toString().trim());
		setContent(data.getString().trim());
		
		data=(FeedStruct) parents.pop();
		path=data.getPath();
		inside=data.getInside();	
	}
	
	public void characters (char ch[], int start, int length)	{
		data.append(new String(ch,start,length));
		//content.append(new String(ch,start,length));
	}
	
	private void setContent(String value)	{
		//print.out(path+":"+inside);
		if(StringUtil.isEmpty(inside)) return;
			
			if(data.hasAttribute()) {
				if(!StringUtil.isEmpty(value))setEl(data,VALUE,value);
			}
			else {
				FeedStruct parent=(FeedStruct) parents.peek();
				setEl(parent,inside,value);
			}
				
    }

	private void setEl(Struct sct, Collection.Key key, String value) {
		Object existing = sct.get(key,null);
		
		if(existing instanceof CastableArray){
			((CastableArray)existing).appendEL(value);
		}
		else if(existing instanceof String){
			CastableArray ca=new CastableArray(existing);
			ca.appendEL(existing);
			ca.appendEL(value);
			sct.setEL(key,ca);
		}
		else 
			sct.setEL(key,Caster.toString(value));
		
		
		/*if(existing instanceof Struct)sct.setEL(key,value);
		else if(existing instanceof Array)((Array)existing).appendEL(value);
		else if(existing!=null){
			CastableArray ca=new CastableArray(existing);
			ca.appendEL(existing);
			ca.appendEL(value);
			sct.setEL(key,ca);
		}
		else*/
	}
	
	private Map getAttributes(Attributes attrs, String path) {
		El el = (El) decl.getDeclaration().get(path);
		
		int len=attrs.getLength();
		if((el==null || el.getAttrs()==null) && len==0) return null;
		
		Map map=new HashMap();
		if(el!=null) {
			Attr[] defaults = el.getAttrs();
			if(defaults!=null) {
				for(int i=0;i<defaults.length;i++) {
					if(defaults[i].hasDefaultValue())
						map.put(defaults[i].getName(), defaults[i].getDefaultValue());
				}
			}
		}
		for(int i=0;i<len;i++) {
			map.put(attrs.getQName(i), attrs.getValue(i));
		}
		return map;
	}
	

	/**
	 * @return the properties
	 */
	public Struct getData() {
		return data;
	}
	
	
	

	/**
	 * @see org.xml.sax.helpers.DefaultHandler#endDocument()
	 */
	public void endDocument() throws SAXException {
		super.endDocument();
		Struct def=new StructImpl();
		Key[] entryLevel = decl.getEntryLevel();
		for(int i=0;i<entryLevel.length;i++) {
			data=(FeedStruct) data.get(entryLevel[i],def);
		}
		data.putAll(root);
	}
}