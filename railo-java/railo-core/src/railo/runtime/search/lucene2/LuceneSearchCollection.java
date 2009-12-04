package railo.runtime.search.lucene2;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.spell.Dictionary;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.FSDirectory;

import railo.commons.collections.HashTable;
import railo.commons.io.SystemUtil;
import railo.commons.io.log.LogAndSource;
import railo.commons.io.res.Resource;
import railo.commons.io.res.ResourcesImpl;
import railo.commons.io.res.filter.DirectoryResourceFilter;
import railo.commons.io.res.filter.ResourceFilter;
import railo.commons.io.res.filter.ResourceNameFilter;
import railo.commons.io.res.util.FileWrapper;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.SerializableObject;
import railo.commons.lang.StringUtil;
import railo.runtime.op.Caster;
import railo.runtime.search.AddionalAttrs;
import railo.runtime.search.IndexResult;
import railo.runtime.search.IndexResultImpl;
import railo.runtime.search.SearchCollectionSupport;
import railo.runtime.search.SearchData;
import railo.runtime.search.SearchEngineSupport;
import railo.runtime.search.SearchException;
import railo.runtime.search.SearchIndex;
import railo.runtime.search.SearchResulItem;
import railo.runtime.search.SearchResulItemImpl;
import railo.runtime.search.lucene2.docs.CustomDocument;
import railo.runtime.search.lucene2.highlight.Highlight;
import railo.runtime.search.lucene2.net.WebCrawler;
import railo.runtime.search.lucene2.query.Literal;
import railo.runtime.search.lucene2.query.Op;
import railo.runtime.search.lucene2.suggest.SuggestionItem;
import railo.runtime.type.List;
import railo.runtime.type.QueryColumn;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.dt.DateTime;

/**
 * 
 */
public final class LuceneSearchCollection extends SearchCollectionSupport {
    

	private Resource collectionDir;
	private boolean spellcheck;
	private LogAndSource log;
    private static final SerializableObject token=new SerializableObject();
	
	
    /**
     * @param searchEngine
     * @param name
     * @param path
     * @param language
     * @param lastUpdate
     * @param created 
     */
    public LuceneSearchCollection(SearchEngineSupport searchEngine, String name, Resource path, String language, //int count, 
    		DateTime lastUpdate, DateTime created,boolean spellcheck) {
        super(searchEngine, name, path, language, lastUpdate,created);
        this.spellcheck=spellcheck;
        collectionDir=getPath().getRealResource(StringUtil.toIdentityVariableName(getName()));
        
        log=searchEngine.getLogger();
        
    }
    
    public LuceneSearchCollection(SearchEngineSupport searchEngine, String name, Resource path, String language, //int count, 
    		DateTime lastUpdate, DateTime created) {
    	this(searchEngine, name, path, language, lastUpdate, created, true);
    }

    /**
     * @see railo.runtime.search.SearchCollection#_create()
     */
    protected void _create() throws SearchException {
        try {
			if(!collectionDir.exists())collectionDir.createDirectory(true);
		}
        catch (IOException e) {}
    }

    /**
     * @see railo.runtime.search.SearchCollection#_optimize()
     */
    protected void _optimize() throws SearchException {
        IndexWriter[] writers=_getWriters(false);
        for(int i=0;i<writers.length;i++) {
            try {
                optimizeEL(writers[i]);
            } 
            finally {
            	close(writers[i]);
            }
        }
    }

    /**
     *
     * @see railo.runtime.search.SearchCollectionSupport#_map(railo.commons.io.res.Resource)
     */
    protected void _map(Resource path) throws SearchException {
        throw new SearchException("mapping of existing Collection for file ["+path+"] not supported");
    }

    /**
     * @see railo.runtime.search.SearchCollection#_repair()
     */
    protected void _repair() throws SearchException {
        //throw new SearchException("repair of existing Collection not supported");
    }

    /**
     * @see railo.runtime.search.SearchCollectionSupport#_indexFile(java.lang.String, java.lang.String, railo.commons.io.res.Resource, java.lang.String)
     */
    protected IndexResult _indexFile(String id, String title, Resource res,String language) throws SearchException {
    	info(res.getAbsolutePath());
        _checkLanguage(language);
        int before=getDocumentCount(id);
        IndexWriter writer=null;
        synchronized(token){
	        try {
	            writer = _getWriter(id,true);
	            _index(writer,res,res.getName());
	            writer.optimize();
	        } 
	        catch (Exception e) {
	            throw new SearchException(e);
	        }
	        finally {
	        	close(writer);
	        }
	        indexSpellCheck(id);
        }
        if(getDocumentCount(id)==before) return new IndexResultImpl(0,0,1);
        return new IndexResultImpl(0,1,0);
    }

    

	/**
     * @see railo.runtime.search.SearchCollectionSupport#_indexPath(java.lang.String, java.lang.String, railo.commons.io.res.Resource, java.lang.String[], boolean, java.lang.String)
     */
    protected IndexResult _indexPath(String id, String title, Resource dir,String[] extensions, boolean recurse, String language) throws SearchException {
    	info(dir.getAbsolutePath());
    	_checkLanguage(language);
    	int doccount=0;
        IndexWriter writer=null;
        synchronized(token){
	        try {
	            writer = _getWriter(id,true);
				doccount=_list(0,writer,dir,new LuceneExtensionFileFilter(extensions,recurse),"");
	        	//optimizeEL(writer);
				writer.optimize();
	        } 
	        catch (IOException e) {
				throw new SearchException(e);
			}
	        finally {
	        	close(writer);
	        }
	        indexSpellCheck(id);
        }
        
        
        
        return new IndexResultImpl(0,0,doccount);
    } 

    private void optimizeEL(IndexWriter writer) {
    	if(writer==null)return;
    	try {
			writer.optimize();
		} 
    	catch (Throwable t) {
			//print.printST(t);
		}
	}

	private void indexSpellCheck(String id) throws SearchException  {
    	if(!spellcheck) return;
    	
    	IndexReader reader=null;
    	FSDirectory spellDir=null;
    	
    	Resource dir = _createSpellDirectory(id);
		try {
    		File spellFile = FileWrapper.toFile(dir);
    		spellDir = FSDirectory.getDirectory(spellFile);
	    	reader = _getReader(id,false);
	    	Dictionary dictionary = new LuceneDictionary(reader,"contents");
			
	    	SpellChecker spellChecker = new SpellChecker(spellDir);
	    	spellChecker.indexDictionary(dictionary);
			
    	}
    	catch(IOException ioe) {
    		throw new SearchException(ioe);
    	}
    	finally {
    		flushEL(reader);
			closeEL(reader);
    	}
	}

	private void close(IndexWriter writer) throws SearchException {
    	if(writer!=null){
    		//print.out("w-close");
    		try {
				writer.close();
			} catch (IOException e) {
				throw new SearchException(e);
			}
    	}
	}

    private static void close(IndexReader reader) throws SearchException {
    	if(reader!=null){
    		try {
				reader.close();
			} catch (IOException e) {
				throw new SearchException(e);
			}
    	}
	}

    private static void close(Searcher searcher) throws SearchException {
    	if(searcher!=null){
    		try {
    			searcher.close();
			} catch (IOException e) {
				throw new SearchException(e);
			}
    	}
	}

    private static void flushEL(IndexReader reader) {
    	//print.out("r-closeEL");
    	if(reader!=null){
    		try {
				reader.flush();
			} catch (Throwable t) {
				//throw new SearchException(t);
			}
    	}
	}
    private static void closeEL(IndexReader reader) {
    	//print.out("r-closeEL");
    	if(reader!=null){
    		try {
				reader.close();
			} catch (Throwable t) {
				//throw new SearchException(t);
			}
    	}
	}

	/**
     * @see railo.runtime.search.SearchCollection#_indexURL(java.lang.String, java.lang.String, java.net.URL, java.lang.String[], boolean, java.lang.String)
     */
    protected IndexResult _indexURL(String id, String title, URL url,String[] extensions, boolean recurse, String language)throws SearchException {
    	//timeout=ThreadLocalPageContext.getConfig().getRequestTimeout().getMillis();
    	return _indexURL(id, title, url, extensions, recurse, language,50000L);
    }
    public IndexResult _indexURL(String id, String title, URL url,String[] extensions, boolean recurse, String language, long timeout)throws SearchException {
        _checkLanguage(language);
        info(url.toExternalForm());
        int before=getDocumentCount(id);
        IndexWriter writer=null;
        synchronized(token){
	        try {
	            writer = _getWriter(id,true);
	            new WebCrawler(log).parse(writer, url, extensions, recurse,timeout);
	            
	            writer.optimize();
	        } 
	        catch (Exception e) {
	            throw new SearchException(e);
	        }
	        finally {
	        	close(writer);
	        }
	        indexSpellCheck(id);
        }
        if(getDocumentCount(id)==before) return new IndexResultImpl(0,0,1);
        return new IndexResultImpl(0,1,0);
        //throw new SearchException("url indexing not supported");
        
    }


    /**
     * @param id
     * @param title
     * @param keyColumn
     * @param bodyColumns
     * @param language
     * @param custom1
     * @param custom2
     * @param custom3
     * @param custom4
     * @return 
     * @throws SearchException
     */
    protected IndexResult _indexCustom(String id, QueryColumn title, QueryColumn keyColumn, QueryColumn[] bodyColumns, String language,
            QueryColumn custom1,QueryColumn custom2,QueryColumn custom3,QueryColumn custom4) throws SearchException {
        _checkLanguage(language);
        String t;
        String c1;
        String c2;
        String c3;
        String c4;
        
        int countExisting=0; 
        int countAdd=keyColumn.size();
        int countNew=0;
        
    	Map docs=new HashTable();
    	IndexWriter writer=null;
    	synchronized(token){
	    	try {
	        	// read existing reader
	        	IndexReader reader=null;
	        	try {
	        		reader=_getReader(id,false);
	        		int len=reader.maxDoc();
	        		Document doc;
	        		for(int i=0;i<len;i++) {
	        			doc=reader.document(i);
	        			docs.put(doc.getField("key").stringValue(),doc);
	        		}
		        }
		        catch(Exception e) {}
		        finally {
		        	close(reader);
		        	//if(reader!=null)reader.close();
		        }   
	
		        countExisting=docs.size();
		        
	        	writer = _getWriter(id,true);
		        int len = keyColumn.size();
		        for(int i=1;i<=len;i++) {
		            Object key=keyColumn.get(i,null);
		            if(key==null) continue;
		            
		            StringBuffer body=new StringBuffer();
		            for(int y=0;y<bodyColumns.length;y++) {
		                Object tmp=bodyColumns[y].get(i,null);
		                if(tmp!=null){
	                        body.append(tmp.toString());
	                        body.append(' ');
		                }
		            }
	                t=(title==null)?null:Caster.toString(title.get(i,null),null);
	                c1=(custom1==null)?null:Caster.toString(custom1.get(i,null),null);
	                c2=(custom2==null)?null:Caster.toString(custom2.get(i,null),null);
	                c3=(custom3==null)?null:Caster.toString(custom3.get(i,null),null);
	                c4=(custom4==null)?null:Caster.toString(custom4.get(i,null),null);
	                
	                docs.put(key.toString(),CustomDocument.getDocument(t,key.toString(),body.toString(),c1,c2,c3,c4));
	                }
		        countNew=docs.size();
		        Iterator it = docs.entrySet().iterator();
		        Map.Entry entry;
		        while(it.hasNext()) {
		        	entry = (Map.Entry) it.next();
		        	writer.addDocument((Document) entry.getValue());
		        }
		        optimizeEL(writer);
	            //writer.optimize();
	            
	        }
	        catch(IOException ioe) {
	            throw new SearchException(ioe);
	        }
	        finally {
	        	close(writer);
	        }
	        indexSpellCheck(id);
    	}
        int inserts=countNew-countExisting;

        return new IndexResultImpl(0,inserts,countAdd-inserts);
    }

	/**
     * @see railo.runtime.search.SearchCollection#_purge()
     */
    protected IndexResult _purge() throws SearchException {
    	SearchIndex[] indexes=getIndexes();
    	int count=0;
    	for(int i=0;i<indexes.length;i++) {
    		count+=getDocumentCount(indexes[i].getId());
    	}
    	ResourceUtil.removeChildrenEL(collectionDir);
    	return new IndexResultImpl(count,0,0);
    }

    /**
     * @see railo.runtime.search.SearchCollection#_delete()
     */
    protected IndexResult _delete() throws SearchException {
    	SearchIndex[] indexes=getIndexes();
    	int count=0;
    	for(int i=0;i<indexes.length;i++) {
    		count+=getDocumentCount(indexes[i].getId());
    	}
    	ResourceUtil.removeEL(collectionDir, true);
    	return new IndexResultImpl(count,0,0);
    }

	/**
     * @see railo.runtime.search.SearchCollectionSupport#_deleteIndex(java.lang.String)
     */
    protected IndexResult _deleteIndex(String id) throws SearchException {
    	int count=getDocumentCount(id);
    	ResourceUtil.removeEL(_getIndexDirectory(id,true), true);
    	return new IndexResultImpl(count,0,0);
    }

    /**
     * @see railo.runtime.search.SearchCollection#_search(railo.runtime.search.SearchData, java.lang.String, java.lang.String, short, java.lang.String, java.lang.String[])
     */
    public SearchResulItem[] _search(SearchData data, String criteria, String language,short type, 
    		String categoryTree, String[] category) throws SearchException {
        try {
        	
            if(type!=SEARCH_TYPE_SIMPLE) throw new SearchException("search type explicit not supported");
            Analyzer analyzer = SearchUtil.getAnalyzer(language);
            Query query=null;
            Op op=null;
            Object highlighter=null;
            railo.runtime.search.lucene2.query.QueryParser queryParser=new railo.runtime.search.lucene2.query.QueryParser();
			AddionalAttrs aa = AddionalAttrs.getAddionlAttrs();
			if(!criteria.equals("*")) {
				// FUTURE take this data from calling parameters
				op=queryParser.parseOp(criteria);
				if(op==null) criteria="*";
				else criteria=op.toString();
				try {
					
					query = new QueryParser("contents",analyzer ).parse(criteria);
					highlighter = Highlight.createHighlighter(query,aa.getContextHighlightBegin(),aa.getContextHighlightEnd());
					
		            
				}
	            catch (ParseException e) {
					throw new SearchException(e);
				}
			}
			
			Resource[] files = _getIndexDirectories();
			
            if(files==null) return new SearchResulItem[0];
            ArrayList list=new ArrayList();
            String ct,c;
            
            ArrayList spellCheckIndex=spellcheck?new ArrayList():null;
            
            for(int i=0;i<files.length;i++) {
	        	if(removeCorrupt(files[i]))continue;
            	String strFile=files[i].toString();
	            SearchIndex si = (SearchIndex)indexes.get(files[i].getName());
	            
	            if(si==null)continue;
	            ct=si.getCategoryTree();
	            c=List.arrayToList(si.getCategories(), ",");
	            
	            // check category tree
	            if(!matchCategoryTree(ct,categoryTree))continue;
	            if(!matchCategories(si.getCategories(),category))continue;
	            
	            Document doc;
	            String id=files[i].getName();
	            data.addRecordsSearched(_countDocs(strFile));
	            IndexReader reader = null;
	            Searcher searcher = null;
            	try {
            		reader = _getReader(id,false);
            		if(query==null && "*".equals(criteria)) {
		            	// get all records
		            	int len=reader.numDocs();
			            for(int y=0;y<len;y++) {
			        	    doc = reader.document(y);
			        	    list.add(createSearchResulItemImpl(highlighter,analyzer,doc,id,1,ct,c,aa.getContextPassages(),aa.getContextBytes()));
			            }
		            }
		            else {

			            if(spellcheck)spellCheckIndex.add(id);
		            	// search
			            searcher = new IndexSearcher(reader);
		                Hits hits = searcher.search(query);
			            int len=hits.length();
			            for (int y=0; y<len; y++) {
			        	    doc = hits.doc(y);
			        	    list.add(createSearchResulItemImpl(highlighter,analyzer,doc,id,hits.score(y),ct,c,aa.getContextPassages(),aa.getContextBytes()));
			            }  
		            }
	            }
            	finally {
            		close(reader);
            		close(searcher);
            	}    
	        }
            
            // spellcheck
            //SearchData data=ThreadLocalSearchData.get();
            if(spellcheck && data!=null) {
            	if(data.getSuggestionMax()>=list.size()) {
            		
	            	Map suggestions = data.getSuggestion();
	            	Iterator it = spellCheckIndex.iterator();
	            	String id;
	            	Literal[] literals = queryParser.getLiteralSearchedTerms();
	            	String[] strLiterals = queryParser.getStringSearchedTerms();
	            	boolean setSuggestionQuery=false;
	            	while(it.hasNext()) {
		            	id=(String) it.next();
		            	// add to set to remove duplicate values
		            	SuggestionItem si;
		            	SpellChecker sc = getSpellChecker(id);
		            	for(int i=0;i<strLiterals.length;i++) {
		            		String[] arr = sc.suggestSimilar(strLiterals[i], 1000);
		            		if(arr.length>0){
		            			literals[i].set("<suggestion>"+arr[0]+"</suggestion>"); 
		            			setSuggestionQuery=true;
		            			
			            		si=(SuggestionItem) suggestions.get(strLiterals[i]);
				            	if(si==null)suggestions.put(strLiterals[i],new SuggestionItem(arr));
				            	else si.add(arr);
		            		}
		            	}
			        }
	            	if(setSuggestionQuery)data.setSuggestionQuery(op.toString());
            	}
            }
            
	        return (SearchResulItem[])list.toArray(new SearchResulItem[list.size()]);
        } 
        catch (IOException e) 		{ throw new SearchException(e); }
        
    }
    
    private SpellChecker getSpellChecker(String id) throws IOException {
    	FSDirectory siDir = FSDirectory.getDirectory(FileWrapper.toFile(_getSpellDirectory(id)));
        SpellChecker spellChecker = new SpellChecker(siDir);
        return spellChecker;
    }

	private boolean removeCorrupt(Resource dir) {
    	if(ResourceUtil.isEmptyFile(dir)) {
    		ResourceUtil.removeEL(dir, true);
    		return true;
    	}
    	return false;
	}

	private static SearchResulItem createSearchResulItemImpl(Object highlighter,Analyzer a,Document doc, String name, float score, String ct, String c,int maxNumFragments, int maxLength) {
		String contextSummary=Highlight.createContextSummary(highlighter,a,doc.get("raw"),maxNumFragments,maxLength,doc.get("summary"));
		String summary = doc.get("summary");
		
		
		return new SearchResulItemImpl(
                name,
                doc.get("title"),
                score,
                doc.get("key"),
                doc.get("url"),
                summary,contextSummary,
                ct,c,
                doc.get("custom1"),
                doc.get("custom2"),
                doc.get("custom3"),
                doc.get("custom4"),
                doc.get("mime-type"),
                doc.get("author"),
                doc.get("size"));

	}

	private boolean matchCategories(String[] categoryIndex, String[] categorySearch) {
    	if(categorySearch==null ||categorySearch.length==0) return true;
    	String search;
    	for(int s=0;s<categorySearch.length;s++) {
    		search=categorySearch[s];
    		for(int i=0;i<categoryIndex.length;i++) {
    			if(search.equals(categoryIndex[i]))return true;
    		}
    	}
		return false;
	}

	private boolean matchCategoryTree(String categoryTreeIndex, String categoryTreeSearch) {
    	//if(StringUtil.isEmpty(categoryTreeIndex) || categoryTreeIndex.equals("/")) return true;
    	//if(StringUtil.isEmpty(categoryTreeSearch) || categoryTreeSearch.equals("/")) return true;
    	return categoryTreeIndex.startsWith(categoryTreeSearch);
	}

   /**
     * list a directory and call every file 
     * @param writer
     * @param res
     * @param filter
     * @param url
     * @throws IOException
     * @throws InterruptedException
     */
    private int _list(int doccount,IndexWriter writer, Resource res,ResourceFilter filter,String url) {
        
        if (res.isReadable()) {
        	if (res.exists() && res.isDirectory()) {
            	Resource[] files = (filter==null)?res.listResources():res.listResources(filter);
                if (files != null) {
                    for (int i = 0; i < files.length; i++) {
                    	if(removeCorrupt(files[i])){
                    		continue;
                    	}
                        doccount=_list(doccount,writer, files[i],filter,url+"/"+files[i].getName());
                    }
                }
            } 
            else {
                try {
                	info(res.getAbsolutePath());
                    _index(writer,res,url);
                    doccount++;
                } catch (Exception e) {}
            }
        }
        return doccount;
    }
    
    /**
     * index a single file
     * @param writer
     * @param file
     * @param url
     * @throws IOException
     * @throws InterruptedException
     */
    private void _index(IndexWriter writer, Resource file,String url) throws IOException {
        if(!file.exists()) return;
        writer.addDocument(DocumentUtil.toDocument(file,url,SystemUtil.getCharset()));
    }
    

    
    

    /**
     * @param id
     * @return returns the Index Directory
     */
    private Resource _getIndexDirectory(String id, boolean createIfNotExists) {
    	Resource indexDir=collectionDir.getRealResource(id);
        if(createIfNotExists && !indexDir.exists())indexDir.mkdirs();
        return indexDir;
    }

    /**
     * get writer to id
     * @param id
     * @return returns the Writer 
     * @throws IOException
     * @throws SearchException
     * @throws IOException 
     */
    private IndexWriter _getWriter(String id,boolean create) throws SearchException, IOException {
    	// FUTURE support for none file -> Directory Object
    	Resource dir = _getIndexDirectory(id,true);
    	return new IndexWriter(FileWrapper.toFile(dir), SearchUtil.getAnalyzer(getLanguage()), create);
    	//return new ResourceIndexWriter(dir, SearchUtil.getAnalyzer(getLanguage()), create);
    	/*try {
    		return new ResourceIndexWriter(dir, SearchUtil.getAnalyzer(getLanguage()), true);
    	} catch (IOException e) {
    		ResourceUtil.removeChildrenEL(dir);
			dir.getResourceProvider().unlock(dir);
			return new ResourceIndexWriter(dir, SearchUtil.getAnalyzer(getLanguage()),true);
		}*/
    }

    private IndexReader _getReader(String id,boolean absolute) throws IOException {
    	return _getReader(_getFile(id, absolute));
    }  

    private IndexReader _getReader(File file) throws IOException {
    	return IndexReader.open(file);
    }  
    
    private File _getFile(String id,boolean absolute) throws IOException {
    	Resource res = absolute?ResourcesImpl.getFileResourceProvider().getResource(id):_getIndexDirectory(id,true);
    	res.getResourceProvider().read(res);
    	return FileWrapper.toFile(res);
    }  
    
    /**
     * @return returns all existing IndexWriter
     */
    private Resource[] _getIndexDirectories() {
    	Resource[] files = collectionDir.listResources(new DirectoryResourceFilter());
        
        return files;
    }

    /**
     * @return returns all existing IndexWriter
     * @throws SearchException
     */
    private IndexWriter[] _getWriters(boolean create) throws SearchException {
    	Resource[] files = _getIndexDirectories();
        if(files==null) return new IndexWriter[0];
        
        IndexWriter[] writers=new IndexWriter[files.length];
        for(int i=0;i<files.length;i++) {
            try {
                writers[i]=_getWriter(files[i].getName(),create);
            } catch (IOException e) {}
        }
        return writers;
    }
    

    private int _countDocs(String col)	{
    	// FUTURE add support for none file resources
        int totalDocs;
        IndexReader reader=null;
        try	{
        	reader=_getReader(col,true);
        	totalDocs = reader.numDocs();
        }
        catch(Exception e)	{
            return 0;
        }
        finally {
        	closeEL(reader);
        }
        return totalDocs;
    }

    /**
     * @deprecated see SearchUtil.getAnalyzer(String language);
     * @param language
     * @return returns language matching Analyzer
     * @throws SearchException
     */
    public static Analyzer _getAnalyzer(String language) throws SearchException {
        return SearchUtil.getAnalyzer(language);
    }

    /** 
     * check given language against colllection language
     * @param language
     * @throws SearchException
     */
    private void _checkLanguage(String language) throws SearchException {
    	
        if(language!=null && !language.trim().equalsIgnoreCase(getLanguage())) {
            throw new SearchException("collection Language and Index Language must be of same type, but collection language is of type ["+getLanguage()+"] and index language is of type ["+language+"]");
        }
    }

	/**
	 * @see railo.runtime.search.SearchCollection#getDocumentCount()
	 */
	public int getDocumentCount(String id) {
		try {
			if(!_getIndexDirectory(id,false).exists()) return 0;
			IndexReader r=null;
			int num=0;
			try {
				r = _getReader(id,false);
				num=r.numDocs();
			}
			finally {
				close(r);
			}
			return num;
		}
		catch (Exception e) {}
		return 0;
	}
	
	/**
	 * @see railo.runtime.search.SearchCollection#getDocumentCount()
	 */
	public int getDocumentCount() {
		int count=0;
		SearchIndex[] _indexes = getIndexes();
		for(int i=0;i<_indexes.length;i++) {
			count+=getDocumentCount(_indexes[i].getId());
		}
		
		return count;
	}

	/**
	 * @see railo.runtime.search.SearchCollection#getSize()
	 */
	public long getSize() {
		return ResourceUtil.getRealSize(collectionDir)/1024;
	}

	public Object getCategoryInfo() {
		Struct categories=new StructImpl();
		Struct categorytrees=new StructImpl();
		Struct info=new StructImpl();
		info.setEL("categories", categories);
		info.setEL("categorytrees", categorytrees);
		
		Iterator it = indexes.keySet().iterator();
		String[] cats;
		String catTree;
		Double tmp;
		
		while(it.hasNext()) {
			SearchIndex index=(SearchIndex) indexes.get(it.next());
			
			// category tree
			catTree = index.getCategoryTree();
			tmp=(Double) categorytrees.get(catTree,null);
			if(tmp==null) categorytrees.setEL(catTree,Caster.toDouble(1));
			else categorytrees.setEL(catTree,Caster.toDouble(tmp.doubleValue()+1));
			
			// categories
			cats = index.getCategories();
			for(int i=0;i<cats.length;i++) {
				tmp=(Double) categories.get(cats[i],null);
				if(tmp==null) categories.setEL(cats[i],Caster.toDouble(1));
				else categories.setEL(cats[i],Caster.toDouble(tmp.doubleValue()+1));
			}
		}
		return info;
	}

	class ResourceIndexWriter extends IndexWriter {

		private Resource dir;

		public ResourceIndexWriter(Resource dir, Analyzer analyzer, boolean create) throws IOException {
			
			super(FileWrapper.toFile(dir), analyzer, create);
			this.dir=dir;
			dir.getResourceProvider().lock(dir);
			
		}

		/**
		 *
		 * @see org.apache.lucene.index.IndexWriter#close()
		 */
		public synchronized void close() throws IOException {
			super.close();
			dir.getResourceProvider().unlock(dir);
		}
		
	}
    
	private Resource _createSpellDirectory(String id) {
    	Resource indexDir=collectionDir.getRealResource(id+"_"+(_getMax(true)+1)+"_spell");
    	//print.out("create:"+indexDir);
        indexDir.mkdirs();
        return indexDir;
    }
    
    private Resource _getSpellDirectory(String id) {
    	Resource indexDir=collectionDir.getRealResource(id+"_"+_getMax(false)+"_spell");
    	//print.out("get:"+indexDir);
        return indexDir;
    }

    private long _getMax(boolean delete) {
    	Resource[] children = collectionDir.listResources(new SpellDirFilter());
    	long max=0, nbr;
    	String name;
    	for(int i=0;i<children.length;i++) {
    		name=children[i].getName();
    		name=name.substring(0,name.length()-6);
    		nbr=Caster.toLongValue(name.substring(name.lastIndexOf('_')+1),0);
    		if(delete){
    			try {
					children[i].remove(true);
					continue;
				} 
    			catch (Throwable t) {}
    		}
    		if(nbr>max)max=nbr;
    	}
    	return max;
    }
    
    private void info(String doc) {
		if(log==null) return;
		log.info("Collection:"+getName(), "indexing "+doc);
	}
	
	public class SpellDirFilter implements ResourceNameFilter {

		/**
		 * filter all names with the following pattern [<name>_<count>_spell]
		 * 
		 * @see railo.commons.io.res.filter.ResourceNameFilter#accept(railo.commons.io.res.Resource, java.lang.String)
		 */
		public boolean accept(Resource parent, String name) {
			return name.endsWith("_spell");
		}

	}
}