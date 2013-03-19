package railo.runtime.search;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

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
import railo.commons.io.log.LogAndSource;
import railo.commons.io.res.Resource;
import railo.commons.io.res.ResourceProvider;
import railo.commons.io.res.ResourcesImpl;
import railo.commons.lang.StringUtil;
import railo.runtime.Info;
import railo.runtime.config.Config;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.DatabaseException;
import railo.runtime.exp.PageException;
import railo.runtime.op.date.DateCaster;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Query;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;
import railo.runtime.type.util.KeyConstants;
import railo.runtime.type.util.ListUtil;

/**
 * 
 */
public abstract class SearchEngineSupport implements SearchEngine {
    
	private Resource searchFile;
	private Resource searchDir;
	private LogAndSource log;
    private Document doc;
    Struct collections=new StructImpl();
	
	@Override
	public void init(railo.runtime.config.Config config,Resource searchDir, LogAndSource log) throws SAXException, IOException, SearchException {
		this.searchDir=searchDir;
		this.searchFile=searchDir.getRealResource("search.xml");
		if(!searchFile.exists()) createSearchFile(searchFile);
		
		DOMParser parser = new DOMParser();
		InputStream is=null;
	    try {
			is = IOUtil.toBufferedInputStream(searchFile.getInputStream());
	        InputSource source = new InputSource(is);
	    	parser.parse(source);
	    }
	    finally {
	    	IOUtil.closeEL(is);
	    }
    	doc = parser.getDocument();
    	    	
    	this.log=log;
        
    	readCollections(config);
	} 
	
	@Override
	public final SearchCollection getCollectionByName(String name) throws SearchException {
		Object o=collections.get(name.toLowerCase(),null);
		if(o!=null)return (SearchCollection) o; 
		throw new SearchException("collection "+name+" is undefined");
	}
	
	@Override
	public final Query getCollectionsAsQuery() {
        final String v="VARCHAR";
        Query query=null;
        String[] cols = new String[]{"external","language","mapped","name","online","path","registered","lastmodified","categories","charset","created",
        							 "size","doccount"};
        String[] types = new String[]{"BOOLEAN",v,"BOOLEAN",v,"BOOLEAN",v,v,"DATE","BOOLEAN",v,"OBJECT","DOUBLE","DOUBLE"};
        try {
            query=new QueryImpl(cols,types, collections.size(),"query");
        } catch (DatabaseException e) {
            query=new QueryImpl(cols, collections.size(),"query");
        }
        
        //Collection.Key[] keys = collections.keys();
	    Iterator<Object> it = collections.valueIterator();
	    int i=-1;
        while(it.hasNext()) {
	        i++;
        	try {
		        SearchCollection coll = (SearchCollection) it.next();
                query.setAt(KeyConstants._external,i+1,Boolean.FALSE);
                query.setAt(KeyConstants._charset,i+1,"UTF-8");
                query.setAt(KeyConstants._created,i+1,coll.created());
                
                query.setAt("categories",i+1,Boolean.TRUE);
		        query.setAt(KeyConstants._language,i+1,coll.getLanguage());
		        query.setAt("mapped",i+1,Boolean.FALSE);
		        query.setAt(KeyConstants._name,i+1,coll.getName());
		        query.setAt(KeyConstants._online,i+1,Boolean.TRUE);
		        query.setAt(KeyConstants._path,i+1,coll.getPath().getAbsolutePath());
		        query.setAt("registered",i+1,"CF");
		        query.setAt(KeyConstants._lastmodified,i+1,coll.getLastUpdate());
		        query.setAt(KeyConstants._size,i+1,new Double(coll.getSize()));
		        query.setAt("doccount",i+1,new Double(coll.getDocumentCount())); 
	        }
		    catch(PageException pe) {}
	    }
		return query;
	}
	
	@Override
	public final SearchCollection createCollection(String name,Resource path, String language, boolean allowOverwrite) throws SearchException {
	    SearchCollection coll = _createCollection(name,path,language);
	    coll.create();
	    addCollection(coll,allowOverwrite);
	    return coll;
	}
	
	/**
	 * Creates a new Collection, will be invoked by createCollection
	 * @param name The Name of the Collection
	 * @param path the path to store
	 * @param language The language of the collection
	 * @return New SearchCollection
	 * @throws SearchException
	 */
	protected abstract SearchCollection _createCollection(String name,Resource path, String language) throws SearchException;
	
	/**
	 * adds a new Collection to the storage
	 * @param collection
	 * @param allowOverwrite if allowOverwrite is false and a collection already exist -> throw Exception
	 * @throws SearchException
	 */
	private final synchronized void addCollection(SearchCollection collection, boolean allowOverwrite) throws SearchException {
	    Object o = collections.get(collection.getName(),null);
	    if(!allowOverwrite && o!=null)
		    throw new SearchException("there is already a collection with name "+collection.getName());
		collections.setEL(collection.getName(),collection);
		// update
		if(o!=null) {
		    setAttributes(getCollectionElement(collection.getName()),collection);
		}
		// create
		else {
		    doc.getDocumentElement().appendChild(toElement(collection));
		}
		store();
	}

	/**
	 * removes a Collection from the storage
	 * @param collection Collection to remove
	 * @throws SearchException
	 */
	protected final synchronized void removeCollection(SearchCollection collection)
			throws SearchException {
	    removeCollection(collection.getName());
	    _removeCollection(collection);
	}
    
    /**
     * removes a Collection from the storage
     * @param collection Collection to remove
     * @throws SearchException
     */
    protected abstract void _removeCollection(SearchCollection collection) throws SearchException;
    
    /**
     * removes a Collection from the storage
     * @param name Name of the Collection to remove
     * @throws SearchException
     */
    private final synchronized void removeCollection(String name) throws SearchException {
        try {
            collections.remove(KeyImpl.init(name));
            doc.getDocumentElement().removeChild(getCollectionElement(name));
            store();
        } 
        catch (PageException e) {
            throw new SearchException("can't remove collection "+name+", collection doesn't exist");
        }
    }
    
    
    /**
     * purge a Collection 
     * @param collection Collection to purge
     * @throws SearchException
     */
    protected final synchronized void purgeCollection(SearchCollection collection)
            throws SearchException {
        
        purgeCollection(collection.getName());
    }
    
    /**
     * purge a Collection
     * @param name Name of the Collection to purge
     * @throws SearchException
     */
    private final synchronized void purgeCollection(String name) throws SearchException {
        
            //Map map=(Map)collections.get(name);
            //if(map!=null)map.clear();
            Element parent = getCollectionElement(name);
            NodeList list=parent.getChildNodes();
            int len=list.getLength();
            for(int i=len-1;i>=0;i--) {
                parent.removeChild(list.item(i));
            }
            //doc.getDocumentElement().removeChild(getCollectionElement(name));
            store();
        
    }

	@Override
	public Resource getDirectory() {
		return searchDir;
	}

	@Override
	public LogAndSource getLogger() {
		return log;
	}

    /**
     * return XML Element matching collection name
     * @param name
     * @return matching XML Element
     */
    protected final Element getCollectionElement(String name) {
        Element root = doc.getDocumentElement();
        NodeList children = root.getChildNodes();
        int len=children.getLength();
        for(int i=0;i<len;i++) {
            Node n=children.item(i);
            if(n instanceof Element && n.getNodeName().equals("collection")) {
                Element el = (Element)n;
                if(el.getAttribute("name").equalsIgnoreCase(name)) return el;
            }
        }
        return null;
    }

    @Override
    public Element getIndexElement(Element collElement, String id) {
        
        NodeList children = collElement.getChildNodes();
        int len=children.getLength();
        for(int i=0;i<len;i++) {
            Node n=children.item(i);
            if(n instanceof Element && n.getNodeName().equals("index")) {
                Element el = (Element)n;
                if(el.getAttribute("id").equals(id)) return el;
            }
        }
        return null;
    }

    /**
     * translate a collection object to a XML Element
     * @param coll Collection to translate
     * @return XML Element
     */
    private final Element toElement(SearchCollection coll) {
        Element el = doc.createElement("collection");
        setAttributes(el,coll);   
        return el;
    }

    /**
     * translate a collection object to a XML Element
     * @param index Index to translate
     * @return XML Element
     * @throws SearchException
     */
    protected final Element toElement(SearchIndex index) throws SearchException {
        Element el = doc.createElement("index");
        setAttributes(el,index);   
        return el;
    }
    
    /**
     * sets all attributes in XML Element from Search Collection
     * @param el
     * @param coll
     */
    private final void setAttributes(Element el,SearchCollection coll) {
        if(el==null) return;
        setAttribute(el,"language",coll.getLanguage());
        setAttribute(el,"name",coll.getName());
        
    	String value = coll.getLastUpdate().castToString(null);
        if(value!=null)setAttribute(el,"lastUpdate",value);
        value=coll.getCreated().castToString(null);
        if(value!=null)setAttribute(el,"created",value);
        
        setAttribute(el,"path",coll.getPath().getAbsolutePath());
    }
    
    /**
     * sets all attributes in XML Element from Search Index
     * @param el
     * @param index
     * @throws SearchException
     */
    protected final void setAttributes(Element el,SearchIndex index) throws SearchException {
        if(el==null) return;
        setAttribute(el,"categoryTree",index.getCategoryTree());
        setAttribute(el,"category",ListUtil.arrayToList(index.getCategories(),","));
        setAttribute(el,"custom1",index.getCustom1());
        setAttribute(el,"custom2",index.getCustom2());
        setAttribute(el,"custom3",index.getCustom3());
        setAttribute(el,"custom4",index.getCustom4());
        setAttribute(el,"id",index.getId());
        setAttribute(el,"key",index.getKey());
        setAttribute(el,"language",index.getLanguage());
        setAttribute(el,"title",index.getTitle());
        setAttribute(el,"extensions",ListUtil.arrayToList(index.getExtensions(),","));
        setAttribute(el,"type",SearchIndex.toStringType(index.getType()));
        setAttribute(el,"urlpath",index.getUrlpath());
        setAttribute(el,"query",index.getQuery());
    }

	/**
	 * helper method to set a attribute
     * @param el
     * @param name
     * @param value
     */
    private void setAttribute(Element el, String name, String value) {
        if(value!=null)el.setAttribute(name,value);
    }

    /**
	 * read in collections
     * @param config 
	 * @throws SearchException
     */
    private void readCollections(Config config) throws SearchException {
        Element root = doc.getDocumentElement();
        NodeList children = root.getChildNodes();
        int len=children.getLength();
        for(int i=0;i<len;i++) {
            Node n=children.item(i);
            if(n instanceof Element && n.getNodeName().equals("collection")) {
                readCollection(config,(Element)n);
            }
        }
    }

    /**
     * read in a single collection element
     * @param config 
     * @param el
     * @throws SearchException
     */
    private final void readCollection(Config config, Element el) throws SearchException {
        SearchCollectionPlus sc;
        //try {
            // Collection
            DateTime last = DateCaster.toDateAdvanced(el.getAttribute("lastUpdate"),ThreadLocalPageContext.getTimeZone(config),null);
            if(last==null)last=new DateTimeImpl();
            DateTime cre = DateCaster.toDateAdvanced(el.getAttribute("created"),ThreadLocalPageContext.getTimeZone(config),null);
            if(cre==null)cre=new DateTimeImpl();
            ResourceProvider frp = ResourcesImpl.getFileResourceProvider();
            sc =(SearchCollectionPlus) _readCollection(
                    el.getAttribute("name"),
                    frp.getResource(el.getAttribute("path")),
                    el.getAttribute("language"),
                    last,cre
            );
            collections.setEL(KeyImpl.init(sc.getName()),sc);
            
            // Indexes
            NodeList children = el.getChildNodes();
            int len=children.getLength();
            for(int i=0;i<len;i++) {
                Node n=children.item(i);
                if(n instanceof Element && n.getNodeName().equals("index")) {
                    readIndex(sc,(Element)n);
                }
            }
        /*} 
        catch (PageException e) {
            throw new SearchException(e);
        }*/
    }

    /**
     * read in a single Index
     * @param sc
     * @param el
     * @throws SearchException
     * @throws PageException
     */
    protected void readIndex(SearchCollectionPlus sc, Element el) throws SearchException {
            // Index
            SearchIndex si=new SearchIndex(
                    _attr(el,"id"),
                    _attr(el,"title"),
                    _attr(el,"key"),
                    SearchIndex.toType(_attr(el,"type")),
                    _attr(el,"query"),
                    ListUtil.listToStringArray(_attr(el,"extensions"),','),
                    _attr(el,"language"),
                    _attr(el,"urlpath"),
                    _attr(el,"categoryTree"),
                    ListUtil.listToStringArray(_attr(el,"category"),','),
                    _attr(el,"custom1"),
                    _attr(el,"custom2"),
                    _attr(el,"custom3"),
                    _attr(el,"custom4"));
           sc.addIndex(si);
    }

  private String _attr(Element el, String attr) {
        return StringUtil.emptyIfNull(el.getAttribute(attr));
    }

      /**
     * read in a existing collection
     * @param name
     * @param parh
     * @param language
     * @param count
     * @param lastUpdate
     * @param created 
     * @return SearchCollection
     * @throws SearchException
     */
    protected abstract SearchCollection _readCollection(String name, Resource parh, String language, DateTime lastUpdate, DateTime created) throws SearchException;

	/**
     * store loaded data to xml file
	 * @throws SearchException
     */
    protected final synchronized void store() throws SearchException {
        //Collection.Key[] keys=collections.keys();
        Iterator<Key> it = collections.keyIterator();
        Key k;
    	while(it.hasNext()) {
    		k=it.next();
            Element collEl = getCollectionElement(k.getString());
            SearchCollection sc = getCollectionByName(k.getString());
            setAttributes(collEl,sc);  
        }

        OutputFormat format = new OutputFormat(doc, null, true);
		format.setLineSeparator("\r\n");
		format.setLineWidth(72);
		OutputStream os=null;
		try {
		    XMLSerializer serializer = new XMLSerializer(os=IOUtil.toBufferedOutputStream(searchFile.getOutputStream()), format);
			serializer.serialize(doc.getDocumentElement());
		} catch (IOException e) {
		    throw new SearchException(e);
		}
		finally {
			IOUtil.closeEL(os);
		}
    }

    /**
	 * if no search xml exist create a empty one
	 * @param searchFile 
	 * @throws IOException
	 */
	private final static void createSearchFile(Resource searchFile) throws IOException {
	
		searchFile.createFile(true);
		InputStream in = new Info().getClass().getResourceAsStream("/resource/search/default.xml");
		IOUtil.copy(in,searchFile,true);
    	
	}
    
    @Override
    public abstract String getDisplayName();
}