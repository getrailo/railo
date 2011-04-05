package railo.runtime.schedule;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.runtime.Info;
import railo.runtime.config.Config;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.op.Caster;
import railo.runtime.op.date.DateCaster;
import railo.runtime.type.dt.Date;
import railo.runtime.type.dt.DateImpl;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.Time;
import railo.runtime.type.dt.TimeImpl;


/**
 * 
 */
public final class StorageUtil {

    /**
	 * create xml file from a resource definition
	 * @param file 
     * @param resourcePath 
	 * @throws IOException
	 */
	public void loadFile(File file,String resourcePath) throws IOException {
		loadFile(ResourceUtil.toResource(file), resourcePath);
	}
    /**
	 * create xml file from a resource definition
	 * @param res 
     * @param resourcePath 
	 * @throws IOException
	 */
	public void loadFile(Resource res,String resourcePath) throws IOException {
		res.createFile(true);
		InputStream is = new Info().getClass().getResourceAsStream(resourcePath);
        IOUtil.copy(is,res,true);
	}

    /**
     * load a XML Document as DOM representation
     * @param file XML File to load
     * @return DOM Object
     * @throws SAXException
     * @throws IOException
     */
    public Document loadDocument(Resource file) throws SAXException, IOException {
        DOMParser parser = new DOMParser();
	    
		InputStream in = null;
		try {
			in = file.getInputStream();
			InputSource source = new InputSource(in);
			parser.parse(source);
		}
		finally {
			IOUtil.closeEL(in);
		}
    	
    	return parser.getDocument();
    }
    
    public Document loadDocument(String content) throws SAXException, IOException {
        DOMParser parser = new DOMParser();
	    
		InputSource source = new InputSource(content);
		parser.parse(source);
		
    	return parser.getDocument();
    }

    /**
     * return XML Element matching name
     * @param list source node list
     * @param key key to compare
     * @param value value to compare
     * @return matching XML Element
     */
    public Element getElement(NodeList list,String key, String value) {
        int len=list.getLength();
        for(int i=0;i<len;i++) {
            Node n=list.item(i);
            if(n instanceof Element) {
                Element el = (Element)n;
                if(el.getAttribute(key).equalsIgnoreCase(value)) return el;
            }
        }
        return null;
    }
    
	/**
     * store loaded data to xml file
	 * @param doc 
	 * @param file 
	 * @throws IOException
     */
    public synchronized void store(Document doc,File file) throws IOException {
    	store(doc, ResourceUtil.toResource(file));
    }
    
	/**
     * store loaded data to xml file
	 * @param doc 
	 * @param res 
	 * @throws IOException
     */
    public synchronized void store(Document doc,Resource res) throws IOException {
        OutputFormat format = new OutputFormat(doc, null, true);
		format.setLineSeparator("\r\n");
		format.setLineWidth(72);
		
		OutputStream os=null;
		try {
			XMLSerializer serializer = new XMLSerializer(os=res.getOutputStream(), format);
			serializer.serialize(doc.getDocumentElement());
		}
		finally {
			IOUtil.closeEL(os);
		}
    }

    
    /**
     * reads a XML Element Attribute ans cast it to a String
     * @param el XML Element to read Attribute from it
     * @param attributeName Name of the Attribute to read
     * @return Attribute Value
     */
    public String toString(Element el,String attributeName) {
        return el.getAttribute(attributeName);
    }
    
    /**
     * reads a XML Element Attribute ans cast it to a String
     * @param el XML Element to read Attribute from it
     * @param attributeName Name of the Attribute to read
     * @param defaultValue if attribute doesn't exist return default value
     * @return Attribute Value
     */
    public String toString(Element el,String attributeName, String defaultValue) {
        String value = el.getAttribute(attributeName);
        return (value==null)?defaultValue:value;
    }

    /**
     * reads a XML Element Attribute ans cast it to a File
     * @param el XML Element to read Attribute from it
     * @param attributeName Name of the Attribute to read
     * @return Attribute Value
     */
    /*public File toFile(Element el,String attributeName) {
        String attributeValue = el.getAttribute(attributeName);
        if(attributeValue==null || attributeValue.trim().length()==0) return null;
        return new File(attributeValue);
    }*/

    public Resource toResource(Config config,Element el,String attributeName) {
        String attributeValue = el.getAttribute(attributeName);
        if(attributeValue==null || attributeValue.trim().length()==0) return null;
        return config.getResource(attributeValue);
    }
    
    /**
     * reads a XML Element Attribute ans cast it to a boolean value
     * @param el XML Element to read Attribute from it
     * @param attributeName Name of the Attribute to read
     * @return Attribute Value
     */
    public boolean toBoolean(Element el,String attributeName) {
        return Caster.toBooleanValue(el.getAttribute(attributeName),false);
    }
    
    /**
     * reads a XML Element Attribute ans cast it to a boolean value
     * @param el XML Element to read Attribute from it
     * @param attributeName Name of the Attribute to read
     * @param defaultValue if attribute doesn't exist return default value
     * @return Attribute Value
     */
    public boolean toBoolean(Element el,String attributeName, boolean defaultValue) {
        String value = el.getAttribute(attributeName);
        if(value==null) return defaultValue;
        return Caster.toBooleanValue(value,false);
    }

    /**
     * reads a XML Element Attribute ans cast it to a int value
     * @param el XML Element to read Attribute from it
     * @param attributeName Name of the Attribute to read
     * @return Attribute Value
     */
    public int toInt(Element el,String attributeName) {
        return Caster.toIntValue(el.getAttribute(attributeName),Integer.MIN_VALUE);
    }
    
    public long toLong(Element el,String attributeName) {
        return Caster.toLongValue(el.getAttribute(attributeName),Long.MIN_VALUE);
    }
    
    /**
     * reads a XML Element Attribute ans cast it to a int value
     * @param el XML Element to read Attribute from it
     * @param attributeName Name of the Attribute to read
     * @param defaultValue if attribute doesn't exist return default value
     * @return Attribute Value
     */
    public int toInt(Element el,String attributeName, int defaultValue) {
        String value = el.getAttribute(attributeName);
        if(value==null) return defaultValue;
        int intValue=Caster.toIntValue(value,Integer.MIN_VALUE);
        if(intValue==Integer.MIN_VALUE) return defaultValue;
        return intValue;
    }

    /**
     * reads a XML Element Attribute ans cast it to a DateTime Object
     * @param config 
     * @param el XML Element to read Attribute from it
     * @param attributeName Name of the Attribute to read
     * @return Attribute Value
     */
    public DateTime toDateTime(Config config, Element el,String attributeName) {
        String str=el.getAttribute(attributeName);
        if(str==null) return null;
        return DateCaster.toDateAdvanced(str,ThreadLocalPageContext.getTimeZone(config),null);
    }
    
    /**
     * reads a XML Element Attribute ans cast it to a DateTime
     * @param el XML Element to read Attribute from it
     * @param attributeName Name of the Attribute to read
     * @param defaultValue if attribute doesn't exist return default value
     * @return Attribute Value
     */
    public DateTime toDateTime(Element el,String attributeName, DateTime defaultValue) {
        
        String value = el.getAttribute(attributeName);
        if(value==null) return defaultValue;
        DateTime dtValue=Caster.toDate(value,false,null,null);
        if(dtValue==null) return defaultValue;
        return dtValue;
    }

    /**
     * reads a XML Element Attribute ans cast it to a Date Object
     * @param el XML Element to read Attribute from it
     * @param attributeName Name of the Attribute to read
     * @return Attribute Value
     */
    public Date toDate(Config config,Element el,String attributeName) {
        DateTime dt = toDateTime(config,el,attributeName);
        if(dt==null) return null;
        return new DateImpl(dt);
    }
    
    /**
     * reads a XML Element Attribute ans cast it to a Date
     * @param el XML Element to read Attribute from it
     * @param attributeName Name of the Attribute to read
     * @param defaultValue if attribute doesn't exist return default value
     * @return Attribute Value
     */
    public Date toDate(Element el,String attributeName, Date defaultValue) {
        return new DateImpl(toDateTime(el,attributeName,defaultValue));
    }

    /**
     * reads a XML Element Attribute ans cast it to a Time Object
     * @param config 
     * @param el XML Element to read Attribute from it
     * @param attributeName Name of the Attribute to read
     * @return Attribute Value
     */
    public Time toTime(Config config, Element el,String attributeName) {
        DateTime dt = toDateTime(config,el,attributeName);
        if(dt==null) return null;
        return new TimeImpl(dt);
    }
    
    /**
     * reads a XML Element Attribute ans cast it to a Date
     * @param el XML Element to read Attribute from it
     * @param attributeName Name of the Attribute to read
     * @param defaultValue if attribute doesn't exist return default value
     * @return Attribute Value
     */
    public Time toTime(Element el,String attributeName, Time defaultValue) {
        return new TimeImpl(toDateTime(el,attributeName,defaultValue));
    }

    /**
     * reads 2 XML Element Attribute ans cast it to a Credential
     * @param el XML Element to read Attribute from it
     * @param attributeUser Name of the user Attribute to read
     * @param attributePassword Name of the password Attribute to read
     * @return Attribute Value
     */
    public Credentials toCredentials(Element el,String attributeUser, String attributePassword) {
        String user = el.getAttribute(attributeUser);
        String pass = el.getAttribute(attributePassword);
        if(user==null) return null;
        if(pass==null)pass="";
        return new UsernamePasswordCredentials(user,pass);
    }

    /**
     * reads 2 XML Element Attribute ans cast it to a Credential
     * @param el XML Element to read Attribute from it
     * @param attributeUser Name of the user Attribute to read
     * @param attributePassword Name of the password Attribute to read
     * @param defaultCredentials
     * @return Attribute Value
     */
    public Credentials toCredentials(Element el,String attributeUser, String attributePassword, Credentials defaultCredentials) {
        String user = el.getAttribute(attributeUser);
        String pass = el.getAttribute(attributePassword);
        if(user==null) return defaultCredentials;
        if(pass==null)pass="";
        return new UsernamePasswordCredentials(user,pass);
    }

    /**
     * sets a string value to a XML Element
     * @param el Element to set value on it
     * @param key key to set
     * @param value value to set
     */
    public void setString(Element el, String key, String value) {
        if(value!=null)el.setAttribute(key,value);
    }

    /**
     * sets a file value to a XML Element
     * @param el Element to set value on it
     * @param key key to set
     * @param value value to set
     */
    public void setFile(Element el, String key, File value) {
    	setFile(el, key, ResourceUtil.toResource(value));
    }

    /**
     * sets a file value to a XML Element
     * @param el Element to set value on it
     * @param key key to set
     * @param value value to set
     */
    public void setFile(Element el, String key, Resource value) {
        if(value!=null && value.toString().length()>0)el.setAttribute(key,value.getAbsolutePath());
    }

    /**
     * sets a boolean value to a XML Element
     * @param el Element to set value on it
     * @param key key to set
     * @param value value to set
     */
    public void setBoolean(Element el, String key, boolean value) {
        el.setAttribute(key,String.valueOf(value));
    }

    /**
     * sets a int value to a XML Element
     * @param el Element to set value on it
     * @param key key to set
     * @param value value to set
     */
    public void setInt(Element el, String key, int value) {
        el.setAttribute(key,String.valueOf(value));
    }

    /**
     * sets a datetime value to a XML Element
     * @param el Element to set value on it
     * @param key key to set
     * @param value value to set
     */
    public void setDateTime(Element el, String key, DateTime value) {
        if(value!=null){
            String str = value.castToString(null);
            if(str!=null)el.setAttribute(key,str);
        }
    }
    
    /**
     * sets a Credentials to a XML Element
     * @param el
     * @param username
     * @param password
     * @param credentials
     */
    public void setCredentials(Element el, String username, String password, Credentials credentials) {
        if(credentials==null) return;
        UsernamePasswordCredentials upc=(UsernamePasswordCredentials) credentials;
        if(upc.getUserName()!=null)el.setAttribute(username,upc.getUserName());
        if(upc.getPassword()!=null)el.setAttribute(password,upc.getPassword());
    }
}