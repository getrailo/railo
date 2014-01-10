package railo.runtime.search;

import java.io.IOException;

import railo.commons.lang.Md5;
import railo.commons.lang.StringUtil;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.util.ListUtil;




/**
 */
public final class SearchIndex {
    
    
    /**
     * Field <code>TYPE_FILE</code>
     */
    public static final short TYPE_FILE = 0;
    /**
     * Field <code>TYPE_PATH</code>
     */
    public static final short TYPE_PATH = 1;
    /**
     * Field <code>TYPE_CUSTOM</code>
     */
    public static final short TYPE_CUSTOM = 2;
    /**
     * Field <code>TYPE_URL</code>
     */
    public static final short TYPE_URL = 3;
    
    private String id;
    private String title;
    private String key;
    private short type;
    private String[] extensions;
    private String language;
    private String urlpath;
    private String custom1;
    private String custom2;
    private String query;
    private String custom3;
    private String custom4;
	private String categoryTree;
	private String[] categories;
    
    

    /**
     * @param title
     * @param id
     * @param key
     * @param type
     * @param query
     * @param extensions
     * @param language
     * @param urlpath
     * @param custom1
     * @param custom2
     * @param custom3 
     * @param custom4 
     */
    protected SearchIndex(String id, String title, String key, short type, String query, String[] extensions,
            String language, String urlpath,String categoryTree, String[] categories, String custom1, String custom2, String custom3, String custom4) {
        super();
        this.title = title;
        this.id = id;
        this.key = key;
        this.type = type;
        this.query = query;
        this.extensions = extensions;
        this.language = SearchUtil.translateLanguage(language);
        this.urlpath = urlpath;
        this.categoryTree = categoryTree;
        this.categories = ArrayUtil.trim(categories);
        this.custom1 = custom1;
        this.custom2 = custom2;
        this.custom3 = custom3;
        this.custom4 = custom4;
    }

    /**
     * @param title
     * @param key
     * @param type
     * @param query
     * @param extensions
     * @param language
     * @param urlpath
     * @param custom1
     * @param custom2
     * @param custom3 
     * @param custom4 
     */
    protected SearchIndex(String title, String key, short type, String query, String[] extensions,
            String language, String urlpath,String categoryTree, String[] categories, String custom1, String custom2, String custom3, String custom4) {
        super();
        
        this.title = title;
        this.key = key;
        this.type = type;
        this.query = query;
        this.extensions = extensions;
        this.language = SearchUtil.translateLanguage(language);
        this.urlpath = urlpath;
        this.categoryTree = categoryTree;
        this.categories = categories;
        this.custom1 = custom1;
        this.custom2 = custom2;
        this.custom3 = custom3;
        this.custom4 = custom4;
        this.id=toId(type,key,query);
    }

    /**
     * cast string type to short
     * @param type type to cast
     * @return casted type
     * @throws SearchException
     */
    public static short toType(String type) throws SearchException {
        type=type.toLowerCase().trim();
        if(type.equals("custom"))return SearchIndex.TYPE_CUSTOM; 
        else if(type.equals("query"))return SearchIndex.TYPE_CUSTOM; 
	    else if(type.equals("file"))return SearchIndex.TYPE_FILE; 
	    else if(type.equals("path"))return SearchIndex.TYPE_PATH; 
	    else if(type.equals("url"))return SearchIndex.TYPE_URL; 
	    else throw new SearchException("invalid value for attribute type ["+type+"]");
    }

    /**
     * cast short type to string
     * @param type type to cast
     * @return casted type
     * @throws SearchException
     */
    public static String toStringType(short type) throws SearchException {
        if(type==SearchIndex.TYPE_CUSTOM) return "custom";
        else if(type==SearchIndex.TYPE_FILE) return "file";
        else if(type==SearchIndex.TYPE_PATH) return "path";
        else if(type==SearchIndex.TYPE_URL) return "url";
        else throw new SearchException("invalid value for attribute type ["+type+"]");
        
    }

    /**
     * cast short type to string
     * @param type type to cast
     * @return casted type
     * @throws SearchException
     */
    public static String toStringTypeEL(short type) {
        if(type==SearchIndex.TYPE_CUSTOM) return "custom";
        else if(type==SearchIndex.TYPE_FILE) return "file";
        else if(type==SearchIndex.TYPE_PATH) return "path";
        else if(type==SearchIndex.TYPE_URL) return "url";
        else return "custom";
        
    }
    
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof SearchIndex)) return false;
        SearchIndex other=(SearchIndex) o;
        
        return (other.key.equals(key) && other.type==type);
    }
    
    
    /**
     * @return Returns the custom1.
     */
    public String getCustom1() {
        return custom1;
    }
    /**
     * @return Returns the custom2.
     */
    public String getCustom2() {
        return custom2;
    }

    /**
     * @return Returns the custom3.
     */
    public String getCustom3() {
        return custom3;
    }

    /**
     * @return Returns the custom4.
     */
    public String getCustom4() {
        return custom4;
    }
    
    /**
     * @return Returns the extensions.
     */
    public String[] getExtensions() {
        return extensions;
    }
    /**
     * @return Returns the key.
     */
    public String getKey() {
        return key;
    }
    /**
     * @return Returns the language.
     */
    public String getLanguage() {
        return language;
    }
    /**
     * @return Returns the title.
     */
    public String getTitle() {
        return title;
    }
    /**
     * @return Returns the type.
     */
    public short getType() {
        return type;
    }
    /**
     * @return Returns the id.
     */
    public String getId() {
        return id;
    }
    
    /**
     * @param id The id to set.
     * /
    public void setId(String id) {
        this.id = id;
    }*/
    
    /**
     * @return Returns the urlpath.
     */
    public String getUrlpath() {
        return urlpath;
    }

    /**
     * @return Returns the query.
     */
    public String getQuery() {
        return query;
    }

    @Override
    public String toString() {
        return "railo.runtime.search.SearchIndex(id:"+id+";title:"+title+";key:"+key+";type:"+toStringTypeEL(type)+
        ";language:"+language+";urlpath:"+urlpath+";query:"+query+";categoryTree:"+categoryTree+";categories:"+ListUtil.arrayToList(categories,",")+";custom1:"+custom1+";custom2:"+custom2+";custom3:"+custom3+";custom4:"+custom4+";)";
    }

    /**
     * @param type
     * @param key
     * @param queryName
     * @return id from given data
     */
    public static String toId(short type, String key, String queryName) {
    	if(type==SearchIndex.TYPE_CUSTOM) return "custom";
    	//if(type==SearchIndex.TYPE_FILE) return "file";//P504
    	//if(type==SearchIndex.TYPE_PATH) return "file";//P504
    	
        try {
			return SearchIndex.toStringTypeEL(type)+"-"+Md5.getDigestAsString(key+null);// null is for backward compatibility to older collections
		} catch (IOException e) {
			
			return SearchIndex.toStringTypeEL(type)+"-"+StringUtil.toVariableName(key+null);// null is for backward compatibility to older collections
		}
        //return SearchIndex.toStringTypeEL(type)+"-"+HexCoder.encode((key+queryName).getBytes());
    }

	/**
	 * @return the categories
	 */
	public String[] getCategories() {
		return categories;
	}

	/**
	 * @return the categoryTree
	 */
	public String getCategoryTree() {
		return categoryTree;
	}
}