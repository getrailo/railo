package railo.runtime.tag;

import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.TagImpl;
import railo.runtime.op.Caster;
import railo.runtime.search.SearchCollection;
import railo.runtime.search.SearchEngine;
import railo.runtime.search.SearchException;

/**
* Allows you to create and administer Collections.
**/
public final class Collection extends TagImpl {

	/** Specifies the action to perform. */
	private String action="list";

	/**  */
	private Resource path;

	/** Specifies a collection name or an alias if action = "map" */
	private String collection;

	/** Name of the output variable (action=list) */
	private String name;

	/** language of the collection (operators,stopwords) */
	private String language="english";
	
	//private boolean categories=false;


	@Override
	public void release()	{
		super.release();
		action="list";
		path=null;
		collection=null;
		name=null;
		language="english";
		//categories=false;
	}
	

	/**
	 * @param categories the categories to set
	 * @throws ApplicationException 
	 */
	public void setCategories(boolean categories) {
		// Railo always support categories
		//this.categories = categories;
	}
	
	/** set the value action
	*  Specifies the action to perform.
	* @param action value to set
	**/
	public void setAction(String action)	{
		if(action==null) return;
		this.action=action.toLowerCase().trim();
	}
	

	public void setEngine(String engine)	{
		// This setter only exists for compatibility reasions to other CFML engines, the attribute is completely ignored by Railo.
	}

	/** set the value path
	*  
	* @param path value to set
	 * @throws PageException 
	**/
	public void setPath(String strPath) throws PageException	{
		if(strPath==null) return;
	    this.path=ResourceUtil.toResourceNotExisting(pageContext,strPath.trim() );
		//this.path=new File(path.toLowerCase().trim());

		pageContext.getConfig().getSecurityManager().checkFileLocation(this.path);
		
	    if(!this.path.exists()) {
	    	Resource parent=this.path.getParentResource();
	        if(parent!=null && parent.exists())this.path.mkdirs();
	        else {
	            throw new ApplicationException("attribute path of the tag collection must be a existing directory");
	        }
	    }
		else if(!this.path.isDirectory())
		    throw new ApplicationException("attribute path of the tag collection must be a existing directory");
	}

	/** set the value collection
	*  Specifies a collection name or an alias if action = "map"
	* @param collection value to set
	**/
	public void setCollection(String collection)	{
		if(collection==null) return;
		this.collection=collection.toLowerCase().trim();
	}

	/** set the value name
	*  
	* @param name value to set
	**/
	public void setName(String name)	{
		if(name==null) return;
		this.name=name.toLowerCase().trim();
	}

	/** set the value language
	*  
	* @param language value to set
	**/
	public void setLanguage(String language)	{
		if(language==null) return;
		this.language=validateLanguage(language);
	}

	public static String validateLanguage(String language) {
		if(StringUtil.isEmpty(language,true)) 
			return "english"; 
		language=language.toLowerCase().trim();
		if("standard".equals(language))
			return "english";
		return language;
	}


	@Override
	public int doStartTag() throws PageException	{
	    //SerialNumber sn = pageContext.getConfig().getSerialNumber();
	    //if(sn.getVersion()==SerialNumber.VERSION_COMMUNITY)
	    //    throw new SecurityException("no access to this functionality with the "+sn.getStringVersion()+" version of railo");
	    
	    try {
			if(action.equals("create"))			doCreate();
			else if(action.equals("repair"))	doRepair();
			else if(action.equals("delete"))	doDelete();
			else if(action.equals("optimize"))	doOptimize();
			else if(action.equals("list"))		doList();
			else if(action.equals("map"))		doMap();
			else if(action.equals("categorylist"))doCategoryList();
			
			else throw new ApplicationException("Invalid value [" + action + "] for attribute action.", "allowed values are [create,repair,map,delete,optimize,list ]");
		} catch (SearchException e) {
			throw Caster.toPageException(e);
		}
		return SKIP_BODY;
	}

	/**
	 * @throws SearchException
	 * @throws PageException 
     * 
     */
    private void doMap() throws SearchException, PageException {
        required("collection",action,"collection",collection);
        required("collection",action,"path",path);
		getCollection().map(path);
    }

	/**
	 * Creates a query in the PageContext containing all available Collections of the current searchStorage
	 * @throws ApplicationException
	 * @throws PageException
	 * 
	 */
	private void doList() throws ApplicationException, PageException {
		required("collection",action,"name",name);
        //if(StringUtil.isEmpty(name))throw new ApplicationException("for action list attribute name is required");
        pageContext.setVariable(name,getSearchEngine().getCollectionsAsQuery());
	}
	

	private void doCategoryList() throws PageException, SearchException {
		// check attributes
		required("collection",action,"collection",collection);
		required("collection",action,"name",name);
		pageContext.setVariable(name,getCollection().getCategoryInfo());
	}

	/**
	 * Optimizes the Collection
	 * @throws SearchException
	 * @throws PageException 
	 * 
	 */
	private void doOptimize() throws SearchException, PageException {
	    required("collection",action,"collection",collection);
	    getCollection().optimize();
	}

	/**
	 * Deletes a Collection
	 * @throws SearchException
	 * @throws PageException 
	 * 
	 */
	private void doDelete() throws SearchException, PageException {
	    required("collection",action,"collection",collection);
	    getCollection().delete();
	}

	/**
	 * 
	 * @throws SearchException
	 * @throws PageException 
	 * 
	 */
	private void doRepair() throws SearchException, PageException {
	    required("collection",action,"collection",collection);
	    getCollection().repair();
	}

	/**
	 * Creates a new collection
	 * @throws SearchException
	 * @throws PageException 
	 * 
	 */
	private void doCreate() throws SearchException, PageException {
	    required("collection",action,"collection",collection);
	    required("collection",action,"path",path);
		getSearchEngine().createCollection(collection,path,language,SearchEngine.DENY_OVERWRITE);
	}

    /**
	 * Returns the Searchstorage defined in the Environment
	 * @return searchStorage
	 */
	private SearchEngine getSearchEngine() {
	    return pageContext.getConfig().getSearchEngine();
	}

    /**
     * the collection matching the collection name
     * @return collection
     * @throws SearchException
     */
    private SearchCollection getCollection() throws SearchException {
        return getSearchEngine().getCollectionByName(collection);
    }

    @Override
	public int doEndTag()	{
		return EVAL_PAGE;
	}
}