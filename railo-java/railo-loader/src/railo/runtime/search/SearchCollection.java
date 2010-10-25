package railo.runtime.search;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

import railo.commons.io.log.Log;
import railo.commons.io.res.Resource;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.type.Query;
import railo.runtime.type.QueryColumn;
import railo.runtime.type.dt.DateTime;

/**
 * a Search Collection
 */
public interface SearchCollection extends Serializable {

    /**
     * Field <code>SEARCH_TYPE_SIMPLE</code>
     */
    public static final short SEARCH_TYPE_SIMPLE = 0;

    /**
     * Field <code>SEARCH_TYPE_EXPLICIT</code>
     */
    public static final short SEARCH_TYPE_EXPLICIT = 1;

    /**
     * create a collection
     * @throws SearchException
     */
    public abstract void create() throws SearchException;

    /**
     * optimize a Collection
     * @throws SearchException
     */
    public abstract void optimize() throws SearchException;

    /**
     * map a Collection
     * @param path
     * @throws SearchException
     */
    public abstract void map(Resource path) throws SearchException;

    /**
     * repair a Collection
     * @throws SearchException
     */
    public abstract void repair() throws SearchException;

    /**
     * updates a index of a collection
     * @param pc
     * @param key
     * @param type
     * @param urlpath
     * @param title
     * @param body
     * @param language
     * @param extensions
     * @param query
     * @param recurse
     * @param categoryTree
     * @param categories
     * @param custom1
     * @param custom2
     * @param custom3
     * @param custom4
     * @throws PageException
     * @throws MalformedURLException
     * @throws SearchException
     */
    public abstract IndexResult index(PageContext pc, String key, short type,
            String urlpath, String title, String body, String language,
            String[] extensions, String query, boolean recurse, 
            String categoryTree, String[] categories,
            String custom1, String custom2, String custom3, String custom4)
            throws PageException, MalformedURLException, SearchException;

    /**
     * updates a collection with a file
     * @param id
     * @param title
     * @param file
     * @param language
     * @throws SearchException
     */
    public abstract IndexResult indexFile(String id, String title, Resource file,String language) throws SearchException;

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
    public abstract IndexResult indexPath(String id, String title, Resource dir,
            String[] extensions, boolean recurse, String language)
            throws SearchException;

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
    public abstract IndexResult indexURL(String id, String title, URL url,
            String[] extensions, boolean recurse, String language)
            throws SearchException;

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
    public abstract IndexResult indexCustom(String id, QueryColumn title,
            QueryColumn keyColumn, QueryColumn[] bodyColumns, String language,// FUTURE,QueryColumn urlpath, 
            QueryColumn custom1, QueryColumn custom2, QueryColumn custom3,QueryColumn custom4) throws SearchException;

    /**
     * @return Returns the language.
     */
    public abstract String getLanguage();

    /**
     * purge a collection
     * @throws SearchException
     */
    public abstract IndexResult purge() throws SearchException;

    /**
     * delete the collection
     * @throws SearchException
     */
    public abstract IndexResult delete() throws SearchException;

    /**
     * delete a Index from collection
     * @param pc
     * @param key
     * @param type
     * @param queryName
     * @throws SearchException
     */
    public abstract IndexResult deleteIndex(PageContext pc, String key, short type, String queryName) throws SearchException;

    /**
     * @return Returns the path.
     */
    public abstract Resource getPath();

    /**
     * @return returns when collection is created
     */
    public abstract DateTime getCreated();

    /**
     * @return Returns the lastUpdate.
     */
    public abstract DateTime getLastUpdate();

    /**
     * @return Returns the name.
     */
    public abstract String getName();

    /** 
     * @return Returns the logFile.
     */
    public abstract Log getLogger();

    /**
     * @return Returns the searchEngine.
     */
    public abstract SearchEngine getSearchEngine();

    /**
     * return time when collection was created
     * @return create time
     */
    public abstract Object created();

    /**
     * search the collection
     * @param data
     * @param qry Query to append resuts
     * @param criteria
     * @param language
     * @param type SEARCH_TYPE_EXPLICIT or SEARCH_TYPE_SIMPLE
     * @param startrow
     * @param maxrow
     * @param categoryTree
     * @param category
     * @return new startrow
     * @throws SearchException
     * @throws PageException
     */
    public abstract int search(SearchData data, Query qry,
            String criteria, String language, short type, int startrow,
            int maxrow, String categoryTree, String[] category) throws SearchException, PageException;
    
    /**
     * search the collection
     * @param data
     * @param criteria
     * @param language
     * @param type SEARCH_TYPE_EXPLICIT or SEARCH_TYPE_SIMPLE
     * @param categoryTree
     * @param category
     * @return Result as SearchRecord Array
     * @throws SearchException
     */
    public abstract SearchResulItem[] _search(SearchData data,
            String criteria, String language, short type, String categoryTree, String[] category)
            throws SearchException;

	/**
	 * @return the size of the collection in KB
	 */
	public long getSize();

	/**
	 * @return the counts of the documents in the collection
	 */
	public int getDocumentCount();
	
	public int getDocumentCount(String id);

	public abstract Object getCategoryInfo();


}