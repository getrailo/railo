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
	
    @Override
    public SearchCollection _createCollection(String name, Resource path, String language) throws SearchException {
    	//SearchUtil.getAnalyzer(language);
        return new LuceneSearchCollection(this,name,path,language,new DateTimeImpl(),new DateTimeImpl());
    }

    @Override
    public void _removeCollection(SearchCollection collection) throws SearchException {
        //throw new SearchException("Lucene Search Engine not implemeted");
    }

    @Override
    public SearchCollection _readCollection(String name, Resource path, String language, DateTime lastUpdate, DateTime created) throws SearchException {
        //throw new SearchException("Lucene Search Engine not implemeted");
        return new LuceneSearchCollection(this,name,path,language,lastUpdate,created);
    }

    @Override
    public String getDisplayName() {
        return "Lucene Search Engine";
    }


}