package railo.runtime.search.lucene2.html;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import org.ccil.cowan.tagsoup.Parser;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.exp.PageException;
import railo.runtime.type.util.ListUtil;


public final class HTMLParser extends Parser {

    private XMLReader xmlReader;
    private String title;
    private String charset;
    private StringBuffer current;
    private StringBuffer content;
    private boolean hasChanged;
    private String strContent;
    private Silent silent=new Silent(null,false);
    //private boolean silentBefore=false;
	private String description;
	private String keywords;
	private String author;
	private String custom1;
	private String custom2;
	private String custom3;
	private String custom4;
    
    
    public HTMLParser() {
        try {
            xmlReader=XMLReaderFactory.createXMLReader(Parser.class.getName());
        } 
        catch (SAXException e) {}
        xmlReader.setContentHandler(this);
        xmlReader.setErrorHandler(this);
        
    }
    /**
     * parse a concret url
     * @param file
     * @param charset
     * @throws IOException
     * @throws SAXException 
     * @throws SAXException
     */
    public synchronized void parse(File file, String charset) throws IOException, SAXException {
        parse(ResourceUtil.toResource(file), charset);
    	
    }

    public synchronized void parse(Resource res, String charset) throws IOException, SAXException {
        title="";
        this.charset=charset;
        current=new StringBuffer();
        content=new StringBuffer();
        hasChanged=false;
        
        Reader r=IOUtil.getReader(res,charset);
        InputSource is=new InputSource(r);
        is.setSystemId(res.toString());
        
        try {
            xmlReader.parse(is);
        } 
        finally {
        	IOUtil.closeEL(r);
        }
        strContent=content.toString();
    }
    
	public synchronized void parse(Reader reader) throws IOException, SAXException {
        title="";
        this.charset=null;
        current=new StringBuffer();
        content=new StringBuffer();
        hasChanged=false;
        
        InputSource is=new InputSource(reader);
        
        try {
            xmlReader.parse(is);
        } 
        finally {
        	IOUtil.closeEL(reader);
        }

        
        strContent=content.toString();
    }
    
    
    
    @Override
    public void startElement(String uri, String name, String qName, Attributes atts)throws SAXException {
        if(name.equalsIgnoreCase("script")) {
            silent=new Silent(silent,true);
        }
        else if(name.equalsIgnoreCase("body")) {
            silent=new Silent(silent,false);
        }
        else if(name.equalsIgnoreCase("meta")) {
            doMeta(atts);
        }
        
        
        if(hasChanged==false && charset==null && name.equalsIgnoreCase("meta")){
            if(atts.getValue("http-equiv")!=null) {
                String value=atts.getValue("content");
                String el;
                String n;
                String v;
                if(value!=null) {
                    try {
                        String[] arr=ListUtil.toStringArray(ListUtil.listToArrayRemoveEmpty(value,';'));
                        for(int i=0;i<arr.length;i++) {
                            el=arr[i];
                            n=ListUtil.first(el,"=",true).trim();
                            v=ListUtil.last(el,"=",true).trim();
                            if(n.equalsIgnoreCase("charset")) {
                                charset=v;
                                hasChanged=true;
                                //throw new SAXException("has found charset info");
                            }
                        }
                    } 
                    catch (PageException e) {}
                }
            }
        }
    }
    
    private void doMeta(Attributes atts) {
    	String name=atts.getValue("name");
    	if(name==null) name="";
    	else name=name.toLowerCase().trim();
    	
    	if("description".equals(name))		description=atts.getValue("content");
    	else if("keywords".equals(name))	keywords=atts.getValue("content");
    	else if("author".equals(name))		author=atts.getValue("content");
    	else if("custom1".equals(name))		custom1=atts.getValue("content");
    	else if("custom2".equals(name))		custom2=atts.getValue("content");
    	else if("custom3".equals(name))		custom3=atts.getValue("content");
    	else if("custom4".equals(name))		custom4=atts.getValue("content");
    	
	}
	// <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    public void endElement(String uri, String name, String qName) {
        if(name.equalsIgnoreCase("script")) {
            silent=silent.parent;
        }
        else if(name.equalsIgnoreCase("body")) {
            silent=silent.parent;
        }
        
        String c=current.toString().trim();
        //if(name.equals("title"))print.out(c);
        if(c.length()>0) {
            if(name.equalsIgnoreCase("title"))title=c;
            else {
                content.append(c);
                content.append('\n');
            }
            current=new StringBuffer();
        }
    }
    
    
    @Override
    public void characters (char ch[], int start, int length)   {
       if(!silent.value)
        	current.append(ch,start,length);
    }


    /**
     * @return Returns the content.
     */
    public String getContent() {
        return strContent;
    }

    /**
     * @return Returns the title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return Returns the charset.
     */
    public String getCharset() {
        return charset;
    }
    /**
     * @return Returns the summary
     */
    public String getSummary() {
    	return description;
        
    }

	/**
	 * @return the keywords
	 */
	public String getKeywords() {
		return keywords;
	}

	/**
	 * @return if keywords exists
	 */
	public boolean hasKeywords() {
		return !StringUtil.isEmpty(keywords,true);
	}

	/**
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * @return if author exists
	 */
	public boolean hasAuthor() {
		return !StringUtil.isEmpty(author,true);
	}
	
	public boolean hasCustom1() {
		return !StringUtil.isEmpty(custom1,true);
	}
	public boolean hasCustom2() {
		return !StringUtil.isEmpty(custom2,true);
	}
	public boolean hasCustom3() {
		return !StringUtil.isEmpty(custom3,true);
	}
	public boolean hasCustom4() {
		return !StringUtil.isEmpty(custom4,true);
	}

	/**
	 * @return the custom1
	 */
	public String getCustom1() {
		return custom1;
	}
	/**
	 * @return the custom2
	 */
	public String getCustom2() {
		return custom2;
	}
	/**
	 * @return the custom3
	 */
	public String getCustom3() {
		return custom3;
	}
	/**
	 * @return the custom4
	 */
	public String getCustom4() {
		return custom4;
	}
	
	
    
    /*public static void main(String[] args) throws Exception {
        HTMLParser parser = new HTMLParser();
        parser.parse(new File("C:\\projects\\jmuffin\\webroot\\cfmx\\jm\\test\\tags\\_tuv.htm"),null);
        
        //print.ln("title:"+parser.getTitle());
        //print.ln(parser.getContent());
        
        parser.parse(new File("C:\\projects\\jmuffin\\webroot\\cfmx\\jm\\test\\tags\\_tuv.htm"),"UTF-8");
        
        //print.ln("title:"+parser.getTitle());
        //print.ln(parser.getContent());
    }*/
    
    
    private class Silent {
        Silent parent;
        boolean value;
        /**
         * constructor of the class
         * @param parent
         * @param value
         */
        public Silent(Silent parent, boolean value) {
            this.parent = parent;
            this.value = value;
        }
        
    }
    
}








