package railo.runtime.search;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Element;

import railo.commons.collection.MapFactory;
import railo.commons.io.FileUtil;
import railo.commons.io.log.Log;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.commons.lock.KeyLock;
import railo.commons.lock.Lock;
import railo.commons.net.HTTPUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.DatabaseException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Query;
import railo.runtime.type.QueryColumn;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.util.KeyConstants;
import railo.runtime.type.util.ListUtil;

/**
 * represent a single Collection
 */
public abstract class SearchCollectionSupport2 implements SearchCollectionPlus {

    private static final int LOCK_TIMEOUT = 10*60*1000; // ten minutes
    private String name;
	private Resource path;
	private String language;
	private DateTime lastUpdate;
    private SearchEngineSupport searchEngine;
	//TODO change visibility to private
    protected Map<String,SearchIndex> indexes=MapFactory.<String,SearchIndex>getConcurrentMap();

    private DateTime created;

    private Log log;
	//private static LockManager manager=Lock Manager Impl.getInstance();
    private KeyLock<String> lock=new KeyLock<String>();

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
	public SearchCollectionSupport2(SearchEngineSupport searchEngine, String name,Resource path, String language, DateTime lastUpdate, DateTime created) {
		this.searchEngine=searchEngine;
	    this.name=name;
		this.path=path;
		this.language=SearchUtil.translateLanguage(language);
        this.lastUpdate=lastUpdate;
        this.created=created;
		this.log = searchEngine.getLogger();
	}

	@Override
	public final void create() throws SearchException {
		Lock l = lock();
		try {
	    _create();
	}
		finally {
			unlock(l);
		}
	}

	/**
	 * create a collection
	 * @throws SearchException
	 */
	protected abstract void _create() throws SearchException;

    @Override
    public final void optimize() throws SearchException  {
    	Lock l = lock();
         try {
         _optimize();
        changeLastUpdate();
    }
 		finally {
 			unlock(l);
 		}
    }

    /**
     * optimize a Collection
     * @throws SearchException
     */
    protected abstract void _optimize() throws SearchException ;

    @Override
    public final void map(Resource path) throws SearchException  {
    	Lock l = lock();
    	try {
        _map(path);
        changeLastUpdate();
    }
		finally {
			unlock(l);
		}
    }

    /**
     * map a Collection
     * @param path
     * @throws SearchException
     */ 
    protected abstract void _map(Resource path) throws SearchException ;

    @Override
    public final void repair() throws SearchException  {
    	Lock l = lock();
    	try {
        _repair();
        changeLastUpdate();
    }
		finally {
			unlock(l);
		}
    }

    /**
     * repair a Collection
     * @throws SearchException
     */
    protected abstract void _repair() throws SearchException ;
    
    @Override
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
        Lock l = lock();
    	try {
        SearchIndex si = new SearchIndex(title,key,type,query,extensions,language,urlpath,categoryTree,categories,
        		custom1,custom2,custom3,custom4);
        //String id=si.getId();
        IndexResult ir=IndexResultImpl.EMPTY;
        if(type==SearchIndex.TYPE_FILE){
        	Resource file=ResourceUtil.toResourceNotExisting(pc,key);
            if(!file.isFile())throw new SearchException("value of attribute key must specify a existing file, ["+key+"] is invalid");
             ir=indexFile(si,file);
             //ir=indexFile(id,title,file,language);
        }
        else if(type==SearchIndex.TYPE_PATH){
        	Resource dir=ResourceUtil.toResourceNotExisting(pc,key);
            if(!dir.isDirectory())throw new SearchException("value of attribute key must specify a existing directory, ["+key+"] is invalid");
            ir=indexPath(si,dir,recurse);
        }
        else if(type==SearchIndex.TYPE_URL) {
        	ir=indexURL(si,new URL(key),recurse,timeout);
        }
        else if(type==SearchIndex.TYPE_CUSTOM) {
        	Query qv;
        	if(StringUtil.isEmpty(query)){
            
        	// set columns
        		railo.runtime.type.Array columns=new ArrayImpl();
            	columns.append("key");
            	columns.append("body");
            	if(!StringUtil.isEmpty(title))columns.append("title");
            	if(!StringUtil.isEmpty(urlpath))columns.append("urlpath");
            	if(!StringUtil.isEmpty(custom1))columns.append("custom1");
            	if(!StringUtil.isEmpty(custom2))columns.append("custom2");
            	if(!StringUtil.isEmpty(custom3))columns.append("custom3");
            	if(!StringUtil.isEmpty(custom4))columns.append("custom4");
            	
            // populate query with a single row
                qv=new QueryImpl(columns,1,"query");
                // body
                qv.setAt(KeyConstants._key, 1, key);
                key="key";

                // body
                qv.setAt(KeyConstants._body, 1, body);
                body="body";

                // title
                if(!StringUtil.isEmpty(title)){
                	qv.setAt(KeyConstants._title, 1, title);
                	title="title";
                }

                // custom1
                if(!StringUtil.isEmpty(urlpath)){
                	qv.setAt("urlpath", 1, urlpath);
                	custom1="urlpath";
                }

                // custom1
                if(!StringUtil.isEmpty(custom1)){
                	qv.setAt(KeyConstants._custom1, 1, custom1);
                	custom1="custom1";
                }
                // custom2
                if(!StringUtil.isEmpty(custom2)){
                	qv.setAt(KeyConstants._custom2, 1, custom2);
                	custom2="custom2";
                }
                // custom3
                if(!StringUtil.isEmpty(custom3)){
                	qv.setAt(KeyConstants._custom3, 1, custom3);
                	custom3="custom3";
                }
                // custom4
                if(!StringUtil.isEmpty(custom4)){
                	qv.setAt(KeyConstants._custom4, 1, custom4);
                	custom4="custom4";
                }
            }
        	else qv = Caster.toQuery(pc.getVariable(query));
            
        	QueryColumn keyColumn=qv.getColumn(key);
            
            String[] strBodies=ListUtil.toStringArrayTrim(ListUtil.listToArrayRemoveEmpty(body,','));
            QueryColumn[] bodyColumns=new QueryColumn[strBodies.length];
            for(int i=0;i<bodyColumns.length;i++) {
                bodyColumns[i]=qv.getColumn(strBodies[i]);
            }
            
            ir= indexCustom(si,
                    getColumnEL(qv,title),
                    keyColumn,
                    bodyColumns,

                    getColumnEL(qv,urlpath),
                    getColumnEL(qv,custom1),
                    getColumnEL(qv,custom2),
                    getColumnEL(qv,custom3),
                    getColumnEL(qv,custom4));
        }
        createIndex(si);
        return ir;
    }
		finally {
			unlock(l);
		}
    }
 
    

	private QueryColumn getColumnEL(Query query, String column) {
        if(column==null || column.length()==0) return null;
        return query.getColumn(column,null);
    }

    @Override
    public final IndexResult indexFile(String id,String title, Resource res, String language) throws SearchException {
    	throw new SearchException("method indexFile(...) no longer supported use index(...) instead");
    }
    
    public final IndexResult indexFile(SearchIndex si, Resource file) throws SearchException {
    	IndexResult ir=_indexFile(si,file);
        changeLastUpdate();
        return ir;
    }

    protected abstract IndexResult _indexFile(SearchIndex si, Resource file)  throws SearchException;

    @Override
    public final IndexResult indexPath(String id, String title, Resource dir, String[] extensions, boolean recurse, String language) throws SearchException {
    	throw new SearchException("method indexPath(...) no longer supported use index(...) instead");
    }
    
    public final IndexResult indexPath(SearchIndex si, Resource dir, boolean recurse) throws SearchException {
    	IndexResult ir=_indexPath(si,dir,recurse);
        changeLastUpdate();
        return ir;
    }
	


    /**
     * updates a collection with a path
     * @param dir 
     * @param id
     * @param title
     * @param dir
     * @param recurse 
     * @param recurse
     * @param extensions
     * @param language
     * @throws SearchException
     */
    protected abstract IndexResult _indexPath(SearchIndex si, Resource dir, boolean recurse) throws SearchException;

    @Override
    public final IndexResult indexURL(String id,String title, URL url, String[] extensions, boolean recurse, String language) throws SearchException {
    	return indexURL(id, title, url, extensions, recurse, language, 10000);
    }
    
        // FUTURE replace this in interface with method above
    public final IndexResult indexURL(String id,String title, URL url, String[] extensions, boolean recurse, String language,long timeout) throws SearchException {
    	throw new SearchException("method indexURL(...) no longer supported use index(...) instead");
        
    } 
    
    public final IndexResult indexURL(SearchIndex si, URL url, boolean recurse,long timeout) throws SearchException {
    	IndexResult ir=_indexURL(si,url,recurse,timeout);
        changeLastUpdate();
        return ir;
    } 

    protected abstract IndexResult _indexURL(SearchIndex si, URL url, boolean recurse, long timeout) throws SearchException ;

    @Override
    public final IndexResult indexCustom(String id, QueryColumn title, QueryColumn keyColumn, QueryColumn[] bodyColumns, String language, 
    		QueryColumn custom1, QueryColumn custom2, QueryColumn custom3, QueryColumn custom4) throws SearchException {
    	throw new SearchException("method indexCustom(...) no longer supported use index(...) instead");
        
    }
    
    public final IndexResult indexCustom(SearchIndex si, QueryColumn colTitle, QueryColumn keyColumn, QueryColumn[] bodyColumns, QueryColumn ct1Column, QueryColumn ct2Column, QueryColumn ct3Column, QueryColumn ct4Column) throws SearchException {
    	IndexResult ir=_indexCustom(si, colTitle, keyColumn, bodyColumns, null,ct1Column, ct2Column, ct3Column, ct4Column);
        changeLastUpdate();
        return ir;
    }
    
    public final IndexResult indexCustom(SearchIndex si, QueryColumn colTitle, QueryColumn keyColumn, QueryColumn[] bodyColumns, 
    		QueryColumn urlpath, QueryColumn ct1Column, QueryColumn ct2Column, QueryColumn ct3Column, QueryColumn ct4Column) throws SearchException {
    	IndexResult ir=_indexCustom(si, colTitle, keyColumn, bodyColumns, urlpath, ct1Column, ct2Column, ct3Column, ct4Column);
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
    //protected abstract IndexResult _indexCustom(SearchIndex si, QueryColumn colTitle, QueryColumn keyColumn, QueryColumn[] bodyColumns, QueryColumn ct1Column, QueryColumn ct2Column, QueryColumn ct3Column, QueryColumn ct4Column) throws SearchException;
    protected abstract IndexResult _indexCustom(SearchIndex si, QueryColumn colTitle, QueryColumn keyColumn, QueryColumn[] bodyColumns, 
    		QueryColumn urlpath, QueryColumn ct1Column, QueryColumn ct2Column, QueryColumn ct3Column, QueryColumn ct4Column) throws SearchException;

    /**
     * @param index
     * @throws SearchException
     */
    private void createIndex(SearchIndex index) throws SearchException {
        Iterator<String> it = indexes.keySet().iterator();
        SearchIndex otherIndex=null;
        
        while(it.hasNext()) {
            Object key=it.next();
            if(key.equals(index.getId())) {
                otherIndex=indexes.get(key);
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
    public void addIndex(SearchIndex index) {
        indexes.put(index.getId(),index);
    }

	@Override
	public final String getLanguage() {
		return language;
	}
    
	@Override
	public final IndexResult purge() throws SearchException {
		Lock l = lock();
	try {
        indexes.clear();
        IndexResult ir=_purge();
        searchEngine.purgeCollection(this);
        changeLastUpdate();
        return ir;
	}
		finally {
			unlock(l);
		}
	}

	/**
	 * purge a collection
	 * @throws SearchException
	 */
	protected abstract IndexResult _purge() throws SearchException;

    @Override
    public final IndexResult delete() throws SearchException {
    	Lock l = lock();
    	try {
    	IndexResult ir=_delete();
	    searchEngine.removeCollection(this);
	    return ir;
    }
		finally {
			unlock(l);
		}
    }

    /**
     * delete the collection from a file
	 * @throws SearchException
     */
    protected abstract IndexResult _delete() throws SearchException;

    @Override
    public final IndexResult deleteIndex(PageContext pc,String key,short type,String queryName) throws SearchException {
        Iterator it = indexes.keySet().iterator();
        
        while(it.hasNext()) {
            Object id = it.next();
            if(id.equals(SearchIndex.toId(type,key,queryName))) {
                SearchIndex index = indexes.get(id);

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
    
	@Override
	public final Resource getPath() {
		return path;
	}

    @Override
    public DateTime getCreated() {
        return created;
    }
    
	@Override
	public final DateTime getLastUpdate() {
		return lastUpdate;
	}

	@Override
	public final String getName() {
		return name;
	} 
	
    @Override
    public final Log getLogger() {
        return log;
    }
    
    @Override
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
    
    @Override
    public Object created() {
        return created;
    }

    @Override
    public final int search(SearchData data, Query qry,String criteria, String language, short type,int startrow,int maxrow,String categoryTree, String[] categories) throws SearchException, PageException {
        int len=qry.getRecordcount();
        SearchResulItem[] records;
        
        AddionalAttrs aa = AddionalAttrs.getAddionlAttrs();
        boolean hasRowHandling=false;
        aa.setStartrow(startrow);
        if(maxrow!=-1)aa.setMaxrows(maxrow-len);
        
        Lock l = lock();
        try {
        	records = _search(data, criteria,language,type,categoryTree,categories);
        }
        finally {
        	unlock(l);
        	if(hasRowHandling=aa.hasRowHandling())
        		startrow = aa.getStartrow();
        	
        }
        
        
        
        // Startrow
        if(!hasRowHandling && startrow>1) {
            
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
        
        
        if(!ArrayUtil.isEmpty(records)) {
            
            int to=(!hasRowHandling && maxrow>-1 && len+records.length>maxrow)?maxrow-len:records.length;
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
            	si=indexes.get(record.getId());

                title=record.getTitle();
                custom1=record.getCustom1();
                custom2=record.getCustom2();
                custom3=record.getCustom3();
                custom4=record.getCustom4();
                url=record.getUrl();
                
                qry.setAt(KeyConstants._title,row,title);
                qry.setAt(KeyConstants._custom1,row,custom1);
                qry.setAt(KeyConstants._custom2,row,custom2);
                qry.setAt(KeyConstants._custom3,row,custom3);
                qry.setAt(KeyConstants._custom4,row,custom4);
                qry.setAt("categoryTree",row,record.getCategoryTree());
                qry.setAt(KeyConstants._category,row,record.getCategory());
                qry.setAt(KeyConstants._type,row,record.getMimeType());
                qry.setAt(KeyConstants._author,row,record.getAuthor());
                qry.setAt(KeyConstants._size,row,record.getSize());

                qry.setAt(KeyConstants._summary,row,record.getSummary());
                qry.setAt(KeyConstants._context,row,record.getContextSummary());
                qry.setAt(KeyConstants._score,row,new Float(record.getScore()));
                qry.setAt(KeyConstants._key,row,record.getKey());
                qry.setAt(KeyConstants._url,row,url);
                qry.setAt(KeyConstants._collection,row,getName());
                qry.setAt(KeyConstants._rank,row,new Double(row));
                String rootPath,file;
                String urlPath;
                if(si!=null) {
                	switch(si.getType()){
                	case SearchIndex.TYPE_PATH:
                		rootPath = si.getKey();
                		rootPath=rootPath.replace(FileUtil.FILE_ANTI_SEPERATOR,FileUtil.FILE_SEPERATOR);
                		file=record.getKey();
                		file=file.replace(FileUtil.FILE_ANTI_SEPERATOR,FileUtil.FILE_SEPERATOR);
                		qry.setAt(KeyConstants._url,row,toURL(si.getUrlpath(),StringUtil.replace(file, rootPath, "", true)));
                		
                		
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
                		qry.setAt(KeyConstants._url,row,toURL(urlPath,StringUtil.replace(file, rootPath, "", true)));
                		
                		
                	break;
                	default:
                		qry.setAt(KeyConstants._url,row,toURL(si.getUrlpath(),url));
                	break;
                	}
                	
                	
                    if(StringUtil.isEmpty(title))      qry.setAt(KeyConstants._title,row,si.getTitle());
                    if(StringUtil.isEmpty(custom1))    qry.setAt(KeyConstants._custom1,row,si.getCustom1());
                    if(StringUtil.isEmpty(custom2))    qry.setAt(KeyConstants._custom2,row,si.getCustom2());
                    if(StringUtil.isEmpty(custom3))    qry.setAt(KeyConstants._custom3,row,si.getCustom3());
                    if(StringUtil.isEmpty(custom4))    qry.setAt(KeyConstants._custom4,row,si.getCustom4());
                    
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
    	//Iterator<Entry<String, SearchIndex>> it = indexes.entrySet().iterator();
    	Iterator<SearchIndex> it = indexes.values().iterator();
		int len=indexes.size();
		SearchIndex[] rtn=new SearchIndex[len];
		int count=0;
		while(it.hasNext()) {
			rtn[count++]=it.next();
		}
		return rtn;
	}


    private Lock lock() throws SearchException {
		try {
			return lock.lock(getId(),LOCK_TIMEOUT);
			//manager.lock(LockManager.TYPE_EXCLUSIVE,getId(),LOCK_TIMEOUT,ThreadLocalPageContext.get().getId());
		} 
		catch (Exception e) {
			throw new SearchException(e);
		}
		
	}

	private void unlock(Lock l) {
		lock.unlock(l);
		//manager.unlock(ThreadLocalPageContext.get().getId());
	}


	private String getId() {
		return path.getRealResource(name).getAbsolutePath();
	}

	// FUTURE
	public Object getIndexesAsQuery() {
		Iterator<Entry<String, SearchIndex>> it = indexes.entrySet().iterator();
		
		final String v="VARCHAR";
        Query query=null;
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
        
        Entry<String, SearchIndex> entry;
        SearchIndex index;
        int row=0;
		while(it.hasNext()) {
			query.addRow();
			row++;
        	entry = it.next();
        	index=entry.getValue();
        	if(index==null)continue;
	        try {
		        
                query.setAt("categories",row,ListUtil.arrayToList(index.getCategories(),""));
                query.setAt("categoryTree",row,index.getCategoryTree());
                
                query.setAt(KeyConstants._custom1,row,index.getCustom1());
                query.setAt(KeyConstants._custom2,row,index.getCustom2());
                query.setAt(KeyConstants._custom3,row,index.getCustom3());
                query.setAt(KeyConstants._custom4,row,index.getCustom4());
                
                query.setAt(KeyConstants._extensions,row,ListUtil.arrayToList(index.getExtensions(),","));
                query.setAt(KeyConstants._key,row,index.getKey());
                query.setAt(KeyConstants._language,row,index.getLanguage());
                query.setAt(KeyConstants._query,row,index.getQuery());
                query.setAt(KeyConstants._title,row,index.getTitle());
                query.setAt("urlpath",row,index.getUrlpath());
                query.setAt(KeyConstants._type,row,SearchIndex.toStringTypeEL(index.getType()));
                
	        }
		    catch(PageException pe) {}
	    }
		return query;
	}

}