package railo.runtime.search.lucene2;

import railo.commons.io.res.Resource;
import railo.runtime.search.SearchCollection;
import railo.runtime.search.SearchEngineSupport;
import railo.runtime.search.SearchException;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;

/**
 * 
 */
public final class LuceneSearchEngine extends SearchEngineSupport {

	public LuceneSearchEngine() {

	}
	
    /**
     * @see railo.runtime.search.coreDuplicate.SearchEngineSupport#_createCollection(java.lang.String, railo.commons.io.res.Resource, java.lang.String)
     */
    public SearchCollection _createCollection(String name, Resource path, String language) throws SearchException {
    	//SearchUtil.getAnalyzer(language);
        return new LuceneSearchCollection(this,name,path,language,new DateTimeImpl(),new DateTimeImpl());
    }

    /**
     * @see railo.runtime.search.SearchEngine#_removeCollection(railo.runtime.search.SearchCollection)
     */
    public void _removeCollection(SearchCollection collection) throws SearchException {
        //throw new SearchException("Lucene Search Engine not implemeted");
    }

    /**
     * @see railo.runtime.search.coreDuplicate.SearchEngineSupport#_readCollection(java.lang.String, railo.commons.io.res.Resource, java.lang.String, railo.runtime.type.dt.DateTime, railo.runtime.type.dt.DateTime)
     */
    public SearchCollection _readCollection(String name, Resource path, String language, DateTime lastUpdate, DateTime created) throws SearchException {
        //throw new SearchException("Lucene Search Engine not implemeted");
        return new LuceneSearchCollection(this,name,path,language,lastUpdate,created);
    }

    /**
     * @see railo.runtime.search.SearchEngine#getDisplayName()
     */
    public String getDisplayName() {
        return "Lucene Search Engine";
    }


}