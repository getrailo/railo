package railo.runtime.search;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Element;

import railo.commons.collections.HashTable;
import railo.commons.io.FileUtil;
import railo.commons.io.log.Log;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.commons.net.HTTPUtil;
import railo.runtime.PageContext;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.DatabaseException;
import railo.runtime.exp.PageException;
import railo.runtime.lock.LockManager;
import railo.runtime.lock.LockManagerImpl;
import railo.runtime.op.Caster;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.List;
import railo.runtime.type.Query;
import railo.runtime.type.QueryColumn;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;

/**
 * represent a single Collection
 */
public abstract class SearchCollectionSupport implements SearchCollection {

    private static final int LOCK_TIMEOUT = 10*60*1000; // ten minutes
    private String name;
	private Resource path;
	private String language;
	private DateTime lastUpdate;
    private SearchEngineSupport searchEngine;
	//TODO change visibility to private
    protected Map indexes=new HashTable();

    private DateTime created;

    private Log log;
	private static LockManager manager=LockManagerImpl.getInstance();

	/**
	 * constructor of the class
	 * @param searchEngine
	 * @param name name of the Collection
	 * @param path
	 * @param language
	 * @param count total count of documents in the collection
	 * @param lastUpdate
	 * @param created 
	 */
	public SearchCollectionSupport(SearchEngineSupport searchEngine, String name,Resource path, String language, DateTime lastUpdate, DateTime created) {
		this.searchEngine=searchEngine;
	    this.name=name;
		this.path=path;
		this.language=SearchUtil.translateLanguage(language);
        this.lastUpdate=lastUpdate;
        this.created=created;
		this.log = searchEngine.getLogger();
	}

	/**
     * @see railo.runtime.search.SearchCollection#create()
     */
	public final void create() throws SearchException {
		lock();
		try {
	    _create();
	}
		finally {
			unlock();
		}
	}

	/**
	 * create a collection
	 * @throws SearchException
	 */
	protected abstract void _create() throws SearchException;

    /**
     * @see railo.runtime.search.SearchCollection#optimize()
     */
    public final void optimize() throws SearchException  {
         lock();
         try {
         _optimize();
        changeLastUpdate();
    }
 		finally {
 			unlock();
 		}
    }

    /**
     * optimize a Collection
     * @throws SearchException
     */
    protected abstract void _optimize() throws SearchException ;

    /**
     * @see railo.runtime.search.SearchCollection#map(railo.commons.io.res.Resource)
     */
    public final void map(Resource path) throws SearchException  {
	    lock();
    	try {
        _map(path);
        changeLastUpdate();
    }
		finally {
			unlock();
		}
    }

    /**
     * map a Collection
     * @param path
     * @throws SearchException
     */ 
    protected abstract void _map(Resource path) throws SearchException ;

    /**
     * @see railo.runtime.search.SearchCollection#repair()
     */
    public final void repair() throws SearchException  {
        lock();
    	try {
        _repair();
        changeLastUpdate();
    }
		finally {
			unlock();
		}
    }

    /**
     * repair a Collection
     * @throws SearchException
     */
    protected abstract void _repair() throws SearchException ;
    
    /**
     * @return 
     * @see railo.runtime.search.SearchCollection#index(railo.runtime.PageContext, java.lang.String, short, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String[], java.lang.String, boolean, java.lang.String, java.lang.String[], java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public IndexResult index(PageContext pc, String key, short type, String urlpath, String title, String body, String language, 
            String[] extensions, String query, boolean recurse,String categoryTree, String[] categories,
            String custom1, String custom2, String custom3, String custom4) throws PageException, MalformedURLException, SearchException {
    	return index(pc, key, type, urlpath, title, body, language, extensions, query, recurse, categoryTree, categories, 10000, custom1, custom2, custom3, custom4);
    }
    		
    // FUTURE add this to interface
    public IndexResult index(PageContext pc, String key, short type, String urlpath, String title, String body, String language, 
            String[] extensions, String query, boolean recurse,String categoryTree, String[] categories, long timeout,
            String custom1, String custom2, String custom3, String custom4) throws PageException, MalformedURLException, SearchException {
        language=SearchUtil.translateLanguage(language);
        lock();
    	try {
        SearchIndex si = new SearchIndex(title,key,type,query,extensions,language,urlpath,categoryTree,categories,custom1,custom2,custom3,custom4);
        String id=si.getId();
        IndexResult ir=IndexResultImpl.EMPTY;
        if(type==SearchIndex.TYPE_FILE){
        	Resource file=ResourceUtil.toResourceNotExisting(pc,key);
            if(!file.isFile())throw new SearchException("value of attribute key must specify a existing file, ["+key+"] is invalid");
             ir=indexFile(id,title,file,language);
        }
        else if(type==SearchIndex.TYPE_PATH){
        	Resource dir=ResourceUtil.toResourceNotExisting(pc,key);
            if(!dir.isDirectory())throw new SearchException("value of attribute key must specify a existing directory, ["+key+"] is invalid");
            ir=indexPath(id,title,dir,extensions,recurse,language);
        }
        else if(type==SearchIndex.TYPE_URL) {
        	ir=indexURL(id,title,new URL(key),extensions,recurse,language,timeout);
        }
        else if(type==SearchIndex.TYPE_CUSTOM) {
        	Query qv;
        	if(StringUtil.isEmpty(query)){
            
        	// set columns
        		railo.runtime.type.Array columns=new ArrayImpl();
            	columns.append("key");
            	columns.append("body");
            	if(!StringUtil.isEmpty(title))columns.append("title");
            	if(!StringUtil.isEmpty(custom1))columns.append("custom1");
            	if(!StringUtil.isEmpty(custom2))columns.append("custom2");
            	if(!StringUtil.isEmpty(custom3))columns.append("custom3");
            	if(!StringUtil.isEmpty(custom4))columns.append("custom4");
            	
            // populate query with a single row
                qv=new QueryImpl(columns,1,"query");
                // body
                qv.setAt("key", 1, key);
                key="key";

                // body
                qv.setAt("body", 1, body);
                body="body";

                // title
                if(!StringUtil.isEmpty(title)){
                	qv.setAt("title", 1, title);
                	title="title";
                }

                // custom1
                if(!StringUtil.isEmpty(custom1)){
                	qv.setAt("custom1", 1, custom1);
                	custom1="custom1";
                }
                // custom2
                if(!StringUtil.isEmpty(custom2)){
                	qv.setAt("custom2", 1, custom2);
                	custom2="custom2";
                }
                // custom3
                if(!StringUtil.isEmpty(custom3)){
                	qv.setAt("custom3", 1, custom3);
                	custom3="custom3";
                }
                // custom4
                if(!StringUtil.isEmpty(custom4)){
                	qv.setAt("custom4", 1, custom4);
                	custom4="custom4";
                }
            }
        	else qv = Caster.toQuery(pc.getVariable(query));
            
        	QueryColumn keyColumn=qv.getColumn(key);
            
            String[] strBodies=List.toStringArrayTrim(List.listToArrayRemoveEmpty(body,','));
            QueryColumn[] bodyColumns=new QueryColumn[strBodies.length];
            for(int i=0;i<bodyColumns.length;i++) {
                bodyColumns[i]=qv.getColumn(strBodies[i]);
            }
            
            ir= indexCustom(id,
                    getColumnEL(qv,title),
                    keyColumn,
                    bodyColumns,
                    language,
                    getColumnEL(qv,custom1),
                    getColumnEL(qv,custom2),
                    getColumnEL(qv,custom3),
                    getColumnEL(qv,custom4));
        }
        createIndex(si);
        return ir;
    }
		finally {
			unlock();
		}
    }
 
    private QueryColumn getColumnEL(Query query, String column) {
        if(column==null || column.length()==0) return null;
        return query.getColumn(column,null);
    }

    /**
     * @see railo.runtime.search.SearchCollection#indexFile(java.lang.String, java.lang.String, railo.commons.io.res.Resource, java.lang.String)
     */
    public final IndexResult indexFile(String id,String title, Resource res, String language) throws SearchException {
    	IndexResult ir=_indexFile(id,title,res,language);
        changeLastUpdate();
        return ir;
    }

    /**
     * updates a collection with a file
     * @param id
     * @param title
     * @param file
     * @param language
     * @throws SearchException
     */
    protected abstract IndexResult _indexFile(String id, String title, Resource file, String language)  throws SearchException;

    /**
     * @see railo.runtime.search.SearchCollection#indexPath(java.lang.String, java.lang.String, railo.commons.io.res.Resource, java.lang.String[], boolean, java.lang.String)
     */
    public final IndexResult indexPath(String id, String title, Resource dir, String[] extensions, boolean recurse, String language) throws SearchException {
    	IndexResult ir=_indexPath(id,title,dir,extensions,recurse,language);
        changeLastUpdate();
        return ir;
    }


    /**
     * updates a collection with a path
     * @param id
     * @param title
     * @param dir
     * @param recurse
     * @param extensions
     * @param language
     * @throws SearchException
     */
    protected abstract IndexResult _indexPath(String id,String title, Resource dir, String[] extensions, boolean recurse, String language) throws SearchException;

    /**
     * @see railo.runtime.search.SearchCollection#indexURL(java.lang.String, java.lang.String, java.net.URL, java.lang.String[], boolean, java.lang.String)
     */
    public final IndexResult indexURL(String id,String title, URL url, String[] extensions, boolean recurse, String language) throws SearchException {
    	return indexURL(id, title, url, extensions, recurse, language, 10000);
    }
        // FUTURE replace this in interface with method above
    public final IndexResult indexURL(String id,String title, URL url, String[] extensions, boolean recurse, String language,long timeout) throws SearchException {
    	IndexResult ir=_indexURL(id,title,url,extensions,recurse,language,timeout);
        changeLastUpdate();
        return ir;
    } 

    /**
     * updates a collection with a url
     * @param id
     * @param title
     * @param recurse
     * @param extensions
     * @param url
     * @param language
     * @throws SearchException
     */
    protected abstract IndexResult _indexURL(String id,String title, URL url, String[] extensions, boolean recurse, String language,long timeout) throws SearchException ;
    
    protected abstract IndexResult _indexURL(String id,String title, URL url, String[] extensions, boolean recurse, String language) throws SearchException ;

    /**
     * @see railo.runtime.search.SearchCollection#indexCustom(java.lang.String, railo.runtime.type.QueryColumn, railo.runtime.type.QueryColumn, railo.runtime.type.QueryColumn[], java.lang.String, railo.runtime.type.QueryColumn, railo.runtime.type.QueryColumn, railo.runtime.type.QueryColumn, railo.runtime.type.QueryColumn)
     */
    public final IndexResult indexCustom(String id, QueryColumn title, QueryColumn keyColumn, QueryColumn[] bodyColumns, String language, 
    		QueryColumn custom1, QueryColumn custom2, QueryColumn custom3, QueryColumn custom4) throws SearchException {
    	IndexResult ir=_indexCustom(id,title,keyColumn,bodyColumns,language,custom1,custom2,custom3,custom4);
        changeLastUpdate();
        return ir;
    }

    /**
     * updates a collection with a custom
     * @param id
     * @param title Title for the Index
     * @param keyColumn Key Column
     * @param bodyColumns Body Column Array
     * @param language Language for index
     * @param custom1 
     * @param custom2 
     * @param custom3 
     * @param custom4 
     * @throws SearchException
     */
    protected abstract IndexResult _indexCustom(String id,QueryColumn title, QueryColumn keyColumn, QueryColumn[] bodyColumns, String language, QueryColumn custom1, QueryColumn custom2, QueryColumn custom3, QueryColumn custom4) throws SearchException;

    /**
     * @param index
     * @throws SearchException
     */
    private void createIndex(SearchIndex index) throws SearchException {
        Iterator it = indexes.keySet().iterator();
        SearchIndex otherIndex=null;
        
        while(it.hasNext()) {
            Object key=it.next();
            if(key.equals(index.getId())) {
                otherIndex=(SearchIndex) indexes.get(key);
                break;
            }
        }
        
        Element collElement=searchEngine.getCollectionElement(name);
        
        // Insert
        if(otherIndex==null) {
            addIndex(index);
            collElement.appendChild(searchEngine.toElement(index));
        }
        // Update
        else {
            addIndex(index);
            Element el=searchEngine.getIndexElement(collElement,index.getId());
            searchEngine.setAttributes(el,index);
        }
        changeLastUpdate();
    }

    /**
     * @param index
     */
    protected void addIndex(SearchIndex index) {
        indexes.put(index.getId(),index);
    }

	/**
     * @see railo.runtime.search.SearchCollection#getLanguage()
     */
	public final String getLanguage() {
		return language;
	}
    
	/**
     * @see railo.runtime.search.SearchCollection#purge()
     */
	public final IndexResult purge() throws SearchException {
		lock();
	try {
        indexes.clear();
        IndexResult ir=_purge();
        searchEngine.purgeCollection(this);
        changeLastUpdate();
        return ir;
	}
		finally {
			unlock();
		}
	}

	/**
	 * purge a collection
	 * @throws SearchException
	 */
	protected abstract IndexResult _purge() throws SearchException;

    /**
     * @see railo.runtime.search.SearchCollection#delete()
     */
    public final IndexResult delete() throws SearchException {
    	lock();
    	try {
    	IndexResult ir=_delete();
	    searchEngine.removeCollection(this);
	    return ir;
    }
		finally {
			unlock();
		}
    }

    /**
     * delete the collection from a file
	 * @throws SearchException
     */
    protected abstract IndexResult _delete() throws SearchException;

    /**
     * @see railo.runtime.search.SearchCollection#deleteIndex(railo.runtime.PageContext, java.lang.String, short, java.lang.String)
     */
    public final IndexResult deleteIndex(PageContext pc,String key,short type,String queryName) throws SearchException {
        Iterator it = indexes.keySet().iterator();
        
        while(it.hasNext()) {
            Object id = it.next();
            if(id.equals(SearchIndex.toId(type,key,queryName))) {
                SearchIndex index=(SearchIndex) indexes.get(id);

                IndexResult ir=_deleteIndex(index.getId());
                Element indexEl=searchEngine.getIndexElement(searchEngine.getCollectionElement(name),index.getId());
                if(indexEl!=null)indexEl.getParentNode().removeChild(indexEl);
                changeLastUpdate();
	            return ir; 
            }
        }
        return new IndexResultImpl(0,0,0);
    }

    /**
     * delete a Index from collection
	 * @param id id ofthe Index to delete
     * @throws SearchException
     */ 
    protected abstract IndexResult _deleteIndex(String id) throws SearchException;
    
	/**
     * @see railo.runtime.search.SearchCollection#getPath()
     */
	public final Resource getPath() {
		return path;
	}

    /**
     * @see railo.runtime.search.SearchCollection#getCreated()
     */
    public DateTime getCreated() {
        return created;
    }
    
	/**
     * @see railo.runtime.search.SearchCollection#getLastUpdate()
     */
	public final DateTime getLastUpdate() {
		return lastUpdate;
	}

	/**
     * @see railo.runtime.search.SearchCollection#getName()
     */
	public final String getName() {
		return name;
	} 
	
    /**
     * @see railo.runtime.search.SearchCollection#getLogger()
     */
    public final Log getLogger() {
        return log;
    }
    
    /**
     * @see railo.runtime.search.SearchCollection#getSearchEngine()
     */
    public final SearchEngine getSearchEngine() {
        return searchEngine;
    }

    /**
     * change the last update attribute and store it
     * @throws SearchException
     */
    private void changeLastUpdate() throws SearchException {
        lastUpdate=new DateTimeImpl();
        searchEngine.store();
    }
    
    /**
     * @see railo.runtime.search.SearchCollection#created()
     */
    public Object created() {
        return created;
    }

    /**
     * @see railo.runtime.search.SearchCollection#search(railo.runtime.search.SearchData, railo.runtime.type.Query, java.lang.String, java.lang.String, short, int, int, java.lang.String, java.lang.String[])
     */
    public final int search(SearchData data, Query qry,String criteria, String language, short type,int startrow,int maxrow,String categoryTree, String[] categories) throws SearchException, PageException {
        int len=qry.getRecordcount();
        SearchResulItem[] records;
        lock();
        try {
        	records = _search(data, criteria,language,type,categoryTree,categories);
        }
        finally {
        	unlock();
        }
        // Startrow
        if(startrow>1) {
            if(startrow>records.length) {
                return startrow-records.length;
            }
            
            int start=startrow-1;
            SearchResulItem[] tmpRecords=new SearchResulItem[records.length-start];
            for(int i=start;i<records.length;i++) {
                tmpRecords[i-start]=records[i];
            }
            records=tmpRecords;
            startrow=1;
        }
        
        
        
        if(records!=null && records.length>0) {
            
            int to=(maxrow>-1 && len+records.length>maxrow)?maxrow-len:records.length;
            qry.addRow(to);
            
            String title;
            String custom1;
            String custom2;
            String custom3;
            String custom4;
            String url;
            SearchResulItem record;
            SearchIndex si;
            for(int y=0;y<to;y++) {
                int row=len+y+1;
                record = records[y];
                si=(SearchIndex)indexes.get(record.getId());

                title=record.getTitle();
                custom1=record.getCustom1();
                custom2=record.getCustom2();
                custom3=record.getCustom3();
                custom4=record.getCustom4();
                url=record.getUrl();
                
                qry.setAt("title",row,title);
                qry.setAt("custom1",row,custom1);
                qry.setAt("custom2",row,custom2);
                qry.setAt("custom3",row,custom3);
                qry.setAt("custom4",row,custom4);
                qry.setAt("categoryTree",row,record.getCategoryTree());
                qry.setAt("category",row,record.getCategory());
                qry.setAt("type",row,record.getMimeType());
                qry.setAt("author",row,record.getAuthor());
                qry.setAt("size",row,record.getSize());
                
                qry.setAt("summary",row,record.getSummary());
                qry.setAt("score",row,new Float(record.getScore()));
                qry.setAt("key",row,record.getKey());
                qry.setAt("url",row,url);
                qry.setAt("collection",row,getName());
                qry.setAt("rank",row,new Double(row));
                String rootPath,file;
                String urlPath;
                if(si!=null) {
                	switch(si.getType()){
                	case SearchIndex.TYPE_PATH:
                		rootPath = si.getKey();
                		rootPath=rootPath.replace(FileUtil.FILE_ANTI_SEPERATOR,FileUtil.FILE_SEPERATOR);
                		file=record.getKey();
                		file=file.replace(FileUtil.FILE_ANTI_SEPERATOR,FileUtil.FILE_SEPERATOR);
                		qry.setAt("url",row,toURL(si.getUrlpath(),StringUtil.replace(file, rootPath, "", true)));
                		
                		
                	break;
                	case SearchIndex.TYPE_URL:
                		rootPath = si.getKey();
                		urlPath = si.getUrlpath();
                		try {
                			rootPath = getDirectory(si.getKey());
						} 
                		catch (MalformedURLException e) {}
                		if(StringUtil.isEmpty(urlPath))urlPath=rootPath;
                		file=record.getKey();
                		qry.setAt("url",row,toURL(urlPath,StringUtil.replace(file, rootPath, "", true)));
                		
                		
                	break;
                	default:
                		qry.setAt("url",row,toURL(si.getUrlpath(),url));
                	break;
                	}
                	
                	
                    if(StringUtil.isEmpty(title))      qry.setAt("title",row,si.getTitle());
                    if(StringUtil.isEmpty(custom1))    qry.setAt("custom1",row,si.getCustom1());
                    if(StringUtil.isEmpty(custom2))    qry.setAt("custom2",row,si.getCustom2());
                    if(StringUtil.isEmpty(custom3))    qry.setAt("custom3",row,si.getCustom3());
                    if(StringUtil.isEmpty(custom4))    qry.setAt("custom4",row,si.getCustom4());
                    
                }
            }
        }
        return startrow;
    }

    public static String getDirectory(String strUrl) throws MalformedURLException {
    	URL url = new URL(strUrl);
    	String path=url.getPath(); 
    	int slashIndex = path.lastIndexOf('/');
    	int dotIndex = path.lastIndexOf('.');
    	// no dot
    	if(dotIndex==-1){
    		if(path.endsWith("/"))return HTTPUtil.removeRef(url).toExternalForm();
    		return HTTPUtil.removeRef(new URL(
    				url.getProtocol(),
    				url.getHost(),
    				url.getPort(),path+"/")).toExternalForm();
    	}
    	if(slashIndex>dotIndex){
    		path=path.substring(0,dotIndex);
    		slashIndex = path.lastIndexOf('/');
    	}
    	
    	return HTTPUtil.removeRef(new URL(
				url.getProtocol(),
				url.getHost(),
				url.getPort(),path.substring(0,slashIndex+1))).toExternalForm();
	}

	private static String toURL(String url, String path) {
        if(StringUtil.isEmpty(url)) return path;
        if(StringUtil.isEmpty(path)) return url;
        
        url=url.replace('\\','/');
        path=path.replace('\\','/');
        if(StringUtil.startsWith(path, '/'))path=path.substring(1);
        if(StringUtil.endsWith(url, '/'))url=url.substring(0,url.length()-1);
        
        if(StringUtil.startsWithIgnoreCase(path, url))
        	return path;
        return url+"/"+path;
    }
    
	
	
	

    protected SearchIndex[] getIndexes() {
    	Iterator it = indexes.keySet().iterator();
		int len=indexes.size();
		SearchIndex[] rtn=new SearchIndex[len];
		int count=0;
		while(it.hasNext()) {
			rtn[count++]=(SearchIndex) indexes.get(it.next());
		}
		return rtn;
	}


	private void lock() throws SearchException {
		try {
			manager.lock(LockManager.TYPE_EXCLUSIVE,getId(),LOCK_TIMEOUT,ThreadLocalPageContext.get().getId());
		} 
		catch (Exception e) {
			throw new SearchException(e);
		}
		
	}

	private void unlock() {
		manager.unlock(ThreadLocalPageContext.get().getId());
		
	}


	private String getId() {
		return path.getRealResource(name).getAbsolutePath();
	}

	// FUTURE
	public Object getIndexesAsQuery() {
		Iterator it = indexes.entrySet().iterator();
		
		final String v="VARCHAR";
        QueryImpl query=null;
        String[] cols = new String[]{
        		"categories","categoryTree","custom1","custom2","custom3","custom4","extensions",
        		"key","language","query","title","urlpath","type"};
        String[] types = new String[]{
        		v,v,v,v,v,v,v,
        		v,v,v,v,v,v};
        try {
            query=new QueryImpl(cols,types, 0,"query");
        } catch (DatabaseException e) {
            query=new QueryImpl(cols, 0,"query");
        }
        
	    
        Map.Entry entry;
        SearchIndex index;
        int row=0;
		while(it.hasNext()) {
			query.addRow();
			row++;
        	entry=(Entry) it.next();
        	index=(SearchIndex) entry.getValue();
        	if(index==null)continue;
	        try {
		        
                query.setAt("categories",row,List.arrayToList(index.getCategories(),""));
                query.setAt("categoryTree",row,index.getCategoryTree());
                
                query.setAt("custom1",row,index.getCustom1());
                query.setAt("custom2",row,index.getCustom2());
                query.setAt("custom3",row,index.getCustom3());
                query.setAt("custom4",row,index.getCustom4());
                
                query.setAt("extensions",row,List.arrayToList(index.getExtensions(),","));
                query.setAt("key",row,index.getKey());
                query.setAt("language",row,index.getLanguage());
                query.setAt("query",row,index.getQuery());
                query.setAt("title",row,index.getTitle());
                query.setAt("urlpath",row,index.getUrlpath());
                query.setAt("type",row,SearchIndex.toStringTypeEL(index.getType()));
                
	        }
		    catch(PageException pe) {}
	    }
		return query;
	}

}