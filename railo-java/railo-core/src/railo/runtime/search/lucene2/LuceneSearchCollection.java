package railo.runtime.search.lucene2;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
import railo.runtime.search.SuggestionItem;
import railo.runtime.search.lucene2.docs.CustomDocument;
import railo.runtime.search.lucene2.highlight.Highlight;
import railo.runtime.search.lucene2.net.WebCrawler;
import railo.runtime.search.lucene2.query.Literal;
import railo.runtime.search.lucene2.query.Op;
import railo.runtime.type.QueryColumn;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.util.ListUtil;

/**
 * 
 */
public final class LuceneSearchCollection extends SearchCollectionSupport {
    
	private static final long serialVersionUID = 3430238280421965781L;
	
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

    @Override
    protected void _create() throws SearchException {
        try {
			if(!collectionDir.exists())collectionDir.createDirectory(true);
		}
        catch (IOException e) {}
    }

    @Override
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

    @Override
    protected void _map(Resource path) throws SearchException {
        throw new SearchException("mapping of existing Collection for file ["+path+"] not supported");
    }

    @Override
    protected void _repair() throws SearchException {
        //throw new SearchException("repair of existing Collection not supported");
    }

    @Override
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

    

	@Override
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

	@Override
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
    protected IndexResult _deleteCustom(String id,QueryColumn keyColumn) throws SearchException {

        int countBefore=0;
        int countAfter=0; 
        
    	Map<String,Document> docs=new HashMap<String,Document>();
    	
    	Set<String> keys=toSet(keyColumn);
    	IndexWriter writer=null;
    	String key;
    	IndexReader reader=null;
    	Document doc;
    	
    	synchronized(token){
	    	try {
		        try {
	        		reader=_getReader(id,false);
	        		countBefore=reader.maxDoc();
	        		for(int i=0;i<countBefore;i++) {
	        			doc=reader.document(i);
	        			key=doc.getField("key").stringValue();
	        			if(!keys.contains(key))
	        				docs.put(key,doc);
	        		}
		        }
		        catch(Exception e) {}
		        finally {
		        	close(reader);
		        } 
		        countAfter=docs.size(); 
		        
        		
		        writer = _getWriter(id,true);
		        Iterator<Entry<String, Document>> it = docs.entrySet().iterator();
		        while(it.hasNext()) {
		        	writer.addDocument(it.next().getValue());
		        }
		        optimizeEL(writer);
	            
	        } catch (IOException e) {
				throw new SearchException(e);
			}
	        finally {
	        	close(writer);
	        }
	        indexSpellCheck(id);
    	}
        int removes=countBefore-countAfter;

        return new IndexResultImpl(removes,0,0);
    }

    private Set<String> toSet(QueryColumn column) {
    	Set<String> set=new HashSet<String>();
    	Iterator it = column.valueIterator();
    	while(it.hasNext()){
    		set.add(Caster.toString(it.next(),null));
    	}
		return set;
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
    protected IndexResult _indexCustom(String id, Object title, QueryColumn keyColumn, QueryColumn[] bodyColumns, String language,
    		Object urlpath,Object custom1,Object custom2,Object custom3,Object custom4) throws SearchException {
        _checkLanguage(language);
        String t;
        String url;
        String c1;
        String c2;
        String c3;
        String c4;
        
        int countExisting=0; 
        int countAdd=keyColumn.size();
        int countNew=0;
        
    	Map<String,Document> docs=new HashMap<String,Document>();
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
		        }  
	
        		countExisting=docs.size();
		        writer = _getWriter(id,true);
		        int len = keyColumn.size();
		        String key;
	        	for(int i=1;i<=len;i++) {
		            key=Caster.toString(keyColumn.get(i,null),null);
		            if(key==null) continue;
		            
		            StringBuilder body=new StringBuilder();
		            for(int y=0;y<bodyColumns.length;y++) {
		                Object tmp=bodyColumns[y].get(i,null);
		                if(tmp!=null){
	                        body.append(tmp.toString());
	                        body.append(' ');
		                }
		            }
	                //t=(title==null)?null:Caster.toString(title.get(i,null),null);
	                //url=(urlpath==null)?null:Caster.toString(urlpath.get(i,null),null);
	                
	                t=getRow(title,i);
	                url=getRow(urlpath,i);
	                c1=getRow(custom1,i);
	                c2=getRow(custom2,i);
	                c3=getRow(custom3,i);
	                c4=getRow(custom4,i);
	                
	                docs.put(key,CustomDocument.getDocument(t,key,body.toString(),url,c1,c2,c3,c4));
	                }
		        countNew=docs.size();
		        Iterator<Entry<String, Document>> it = docs.entrySet().iterator();
		        Entry<String, Document> entry;
		        Document doc;
		        while(it.hasNext()) {
		        	entry = it.next();
		        	doc = entry.getValue();
		        	writer.addDocument(doc);
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

	private String getRow(Object column, int row) {
		if(column instanceof QueryColumn){
			return Caster.toString(((QueryColumn)column).get(row,null),null);
		}
		if(column!=null) return Caster.toString(column,null);
		return null;
	}

	@Override
    protected IndexResult _purge() throws SearchException {
    	SearchIndex[] indexes=getIndexes();
    	int count=0;
    	for(int i=0;i<indexes.length;i++) {
    		count+=getDocumentCount(indexes[i].getId());
    	}
    	ResourceUtil.removeChildrenEL(collectionDir);
    	return new IndexResultImpl(count,0,0);
    }

    @Override
    protected IndexResult _delete() throws SearchException {
    	SearchIndex[] indexes=getIndexes();
    	int count=0;
    	for(int i=0;i<indexes.length;i++) {
    		count+=getDocumentCount(indexes[i].getId());
    	}
    	ResourceUtil.removeEL(collectionDir, true);
    	return new IndexResultImpl(count,0,0);
    }

	@Override
    protected IndexResult _deleteIndex(String id) throws SearchException {
    	int count=getDocumentCount(id);
    	ResourceUtil.removeEL(_getIndexDirectory(id,true), true);
    	return new IndexResultImpl(count,0,0);
    }

    @Override
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
			aa.setHasRowHandling(true);
			int startrow=aa.getStartrow();
			int maxrows=aa.getMaxrows();
			
			
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
            ArrayList<SearchResulItem> list=new ArrayList<SearchResulItem>();
            String ct,c;
            
            ArrayList<String> spellCheckIndex=spellcheck?new ArrayList<String>():null;
            
            int count=0;
            IndexReader reader = null;
            Searcher searcher = null;
            try {
	            outer:for(int i=0;i<files.length;i++) {
		        	if(removeCorrupt(files[i]))continue;
	            	String strFile=files[i].toString();
		            SearchIndex si = indexes.get(files[i].getName());
		            
		            if(si==null)continue;
		            ct=si.getCategoryTree();
		            c=ListUtil.arrayToList(si.getCategories(), ",");
		            
		            // check category tree
		            if(!matchCategoryTree(ct,categoryTree))continue;
		            if(!matchCategories(si.getCategories(),category))continue;
		            
		            Document doc;
		            String id=files[i].getName();
		            data.addRecordsSearched(_countDocs(strFile));
		            
            		reader = _getReader(id,false);
            		if(query==null && "*".equals(criteria)) {
		            	int len=reader.numDocs();
			            for(int y=0;y<len;y++) {
			            	if(startrow>++count)continue;
			            	if(maxrows>-1 && list.size()>=maxrows) break outer;
			            	doc = reader.document(y);
			        	    list.add(createSearchResulItem(highlighter,analyzer,doc,id,1,ct,c,aa.getContextPassages(),aa.getContextBytes()));
			            }
		            }
		            else {
			            if(spellcheck)spellCheckIndex.add(id);
		            	// search
			            searcher = new IndexSearcher(reader);
		                Hits hits = searcher.search(query);
			            int len=hits.length();
			            for (int y=0; y<len; y++) {
			            	if(startrow>++count)continue;
			            	if(maxrows>-1 && list.size()>=maxrows) break outer;
			            	//list.add(new SearchResulItemHits(hits,y,highlighter,analyzer,id,ct,c,aa.getContextPassages(),aa.getContextBytes()));
			            	doc = hits.doc(y);
			        	    list.add(createSearchResulItem(highlighter,analyzer,doc,id,hits.score(y),ct,c,aa.getContextPassages(),aa.getContextBytes()));
			            }  
			           
		            }
	             
	        	}
            }
        	finally {
        		close(reader);
        		close(searcher);
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
            
	        return list.toArray(new SearchResulItem[list.size()]);
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

	private static SearchResulItem createSearchResulItem(Object highlighter,Analyzer a,Document doc, String name, float score, String ct, String c,int maxNumFragments, int maxLength) {
		String contextSummary="";
		if(maxNumFragments>0)
			contextSummary=Highlight.createContextSummary(highlighter,a,doc.get("contents"),maxNumFragments,maxLength,doc.get("summary"));
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
        writer.addDocument(DocumentUtil.toDocument(file,url,SystemUtil.getCharset().name()));
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
    	if(!IndexReader.indexExists(file))throw new IOException("there is no index in ["+file+"]");
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
     * check given language against collection language
     * @param language
     * @throws SearchException
     */
    private void _checkLanguage(String language) throws SearchException {
    	
        if(language!=null && !language.trim().equalsIgnoreCase(getLanguage())) {
            throw new SearchException("collection Language and Index Language must be of same type, but collection language is of type ["+getLanguage()+"] and index language is of type ["+language+"]");
        }
    }

	@Override
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
	
	@Override
	public int getDocumentCount() {
		int count=0;
		SearchIndex[] _indexes = getIndexes();
		for(int i=0;i<_indexes.length;i++) {
			count+=getDocumentCount(_indexes[i].getId());
		}
		
		return count;
	}

	@Override
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
			SearchIndex index=indexes.get(it.next());
			
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

		@Override
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

		@Override
		public boolean accept(Resource parent, String name) {
			return name.endsWith("_spell");
		}

	}
}