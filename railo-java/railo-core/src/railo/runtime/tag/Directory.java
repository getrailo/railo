package railo.runtime.tag;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import railo.commons.io.ModeUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.ResourceMetaData;
import railo.commons.io.res.filter.AndResourceFilter;
import railo.commons.io.res.filter.DirectoryResourceFilter;
import railo.commons.io.res.filter.FileResourceFilter;
import railo.commons.io.res.filter.NotResourceFilter;
import railo.commons.io.res.filter.OrResourceFilter;
import railo.commons.io.res.filter.ResourceFilter;
import railo.commons.io.res.filter.ResourceNameFilter;
import railo.commons.io.res.type.file.FileResource;
import railo.commons.io.res.type.s3.S3;
import railo.commons.io.res.type.s3.S3Constants;
import railo.commons.io.res.type.s3.S3Exception;
import railo.commons.io.res.type.s3.S3Resource;
import railo.commons.io.res.util.ModeObjectWrap;
import railo.commons.io.res.util.ResourceAndResourceNameFilter;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.io.res.util.UDFFilter;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.TagImpl;
import railo.runtime.functions.s3.StoreSetACL;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.security.SecurityManager;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.Query;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.UDF;
import railo.runtime.type.util.KeyConstants;

/**
* Handles interactions with directories.
*
*
*
**/
public final class Directory extends TagImpl  {

	public static final int TYPE_ALL = 0;
	public static final int TYPE_FILE = 1;
	public static final int TYPE_DIR = 2;
	
	public static final ResourceFilter DIRECTORY_FILTER = new DirectoryResourceFilter();
	public static final ResourceFilter FILE_FILTER = new FileResourceFilter();
	
	private static final Key MODE = KeyConstants._mode;
	private static final Key META = KeyConstants._meta;
	private static final Key DATE_LAST_MODIFIED = KeyConstants._dateLastModified;
	private static final Key ATTRIBUTES = KeyConstants._attributes;
	private static final Key DIRECTORY = KeyConstants._directory;
		
	public static final int LIST_INFO_QUERY_ALL = 1;
	public static final int LIST_INFO_QUERY_NAME = 2;
	public static final int LIST_INFO_ARRAY_NAME = 4;
	public static final int LIST_INFO_ARRAY_PATH = 8;
	
	public static final int NAMECONFLICT_ERROR     = 1;
	public static final int NAMECONFLICT_SKIP      = 2;
	public static final int NAMECONFLICT_OVERWRITE = 3;
//	public static final int NAMECONFLICT_CLOSURE   = 5;	// FUTURE
	public static final int NAMECONFLICT_UNDEFINED = NAMECONFLICT_OVERWRITE;	// default
	
	/** Optional for action = "list". Ignored by all other actions. File extension filter applied to
	** 		returned names. For example: *m. Only one mask filter can be applied at a time. */
	private ResourceFilter filter;
	private ResourceAndResourceNameFilter nameFilter;

	/** The name of the directory to perform the action against. */
	private Resource directory;

	/** Defines the action to be taken with directory(ies) specified in directory. */
	private String action="list";

	/** Optional for action = "list". Ignored by all other actions. The query columns by which to sort 
	** 		the directory listing. Any combination of columns from query output can be specified in comma-separated list.
	** 		You can specify ASC (ascending) or DESC (descending) as qualifiers for column names. ASC is the default */
	private String sort;

	/** Used with action = "Create" to define the permissions for a directory on UNIX and Linux 
	** 		platforms. Ignored on Windows. Options correspond to the octal values of the UNIX chmod command. From 
	** 		left to right, permissions are assigned for owner, group, and other. */
	private int mode=-1;

	/** Required for action = "rename". Ignored by all other actions. The new name of the directory 
	** 		specified in the directory attribute. */
	private String strNewdirectory;

	/** Required for action = "list". Ignored by all other actions. Name of output query for directory 
	** 		listing. */
	private String name=null;

    
    private boolean recurse=false;

	private String serverPassword;

	private int type=TYPE_ALL; 
	//private boolean listOnlyNames;
	private int listInfo=LIST_INFO_QUERY_ALL;
	//private int acl=S3Constants.ACL_UNKNOW;
	private Object acl=null;
	private int storage=S3Constants.STORAGE_UNKNOW;
	private String destination; 

	private int nameconflict = NAMECONFLICT_UNDEFINED;
	
	private boolean createPath=true;


	@Override
	public void release()	{
		super.release();
		acl=null;
		storage=S3Constants.STORAGE_UNKNOW;


		type=TYPE_ALL;
		filter=null;
		nameFilter=null;
		destination=null;
		directory=null;
		action="list";
		sort=null;
		mode=-1;
		strNewdirectory=null;
		name=null;
        recurse=false;
        serverPassword=null;
        listInfo=LIST_INFO_QUERY_ALL;

        nameconflict = NAMECONFLICT_UNDEFINED;
        createPath=true;
	}


	public void setCreatepath(boolean createPath) throws PageException	{
		this.createPath=createPath;
	}


	/**
	 *  sets a filter
	 * @param filter
	 * @throws PageException
	 **/
	public void setFilter(Object filter) throws PageException	{
		this.filter=nameFilter=UDFFilter.createResourceAndResourceNameFilter(filter);
	}

	public void setFilter(UDF filter) throws PageException	{
		this.filter=nameFilter=UDFFilter.createResourceAndResourceNameFilter(filter);
	}

	public void setFilter(String pattern) {

		this.filter = nameFilter = UDFFilter.createResourceAndResourceNameFilter( pattern );
	}

	/** set the value acl
	*  used only for s3 resources, for all others ignored
	* @param acl value to set
	 * @throws ApplicationException 
	 * @Deprecated only exists for backward compatibility to old ra files.
	**/
	public void setAcl(String acl) throws ApplicationException	{
		this.acl=acl;
		/*acl=acl.trim().toLowerCase();
				
		if("private".equals(acl)) 					this.acl=S3Constants.ACL_PRIVATE;
		else if("public-read".equals(acl)) 			this.acl=S3Constants.ACL_PRIVATE;
		else if("public-read-write".equals(acl))	this.acl=S3Constants.ACL_PUBLIC_READ_WRITE;
		else if("authenticated-read".equals(acl))	this.acl=S3Constants.ACL_AUTH_READ;
		
		else throw new ApplicationException("invalid value for attribute acl ["+acl+"]",
				"valid values are [private,public-read,public-read-write,authenticated-read]");*/
	}
	
	public void setAcl(Object acl) 	{
		this.acl=acl;
	}
	
	public void setStoreacl(Object acl) 	{
		this.acl=acl;
	}
	
	/** set the value storage
	*  used only for s3 resources, for all others ignored
	* @param storage value to set
	 * @throws PageException 
	**/
	public void setStorage(String storage) throws PageException	{
		try {
			this.storage=S3.toIntStorage(storage);
		} catch (S3Exception e) {
			throw Caster.toPageException(e);
		}
	}
	public void setStorelocation(String storage) throws PageException	{
		setStorage(storage);
	}
	
	
	
	public void setServerpassword(String serverPassword)	{
	    this.serverPassword=serverPassword;
	}

	
	public void setListinfo(String strListinfo)	{
		strListinfo=strListinfo.trim().toLowerCase();
		this.listInfo="name".equals(strListinfo)?LIST_INFO_QUERY_NAME:LIST_INFO_QUERY_ALL;
	}
	
	

	/** set the value directory
	*  The name of the directory to perform the action against.
	* @param directory value to set
	**/
	public void setDirectory(String directory)	{
		
        this.directory=ResourceUtil.toResourceNotExisting(pageContext ,directory);
        //print.ln(this.directory);
	}

	/** set the value action
	*  Defines the action to be taken with directory(ies) specified in directory.
	* @param action value to set
	**/
	public void setAction(String action)	{
		this.action=action.toLowerCase();
	}

	/** set the value sort
	*  Optional for action = "list". Ignored by all other actions. The query columns by which to sort 
	* 		the directory listing. Any combination of columns from query output can be specified in comma-separated list.
	* 		You can specify ASC (ascending) or DESC (descending) as qualifiers for column names. ASC is the default
	* @param sort value to set
	**/
	public void setSort(String sort)	{
	    if(sort.trim().length()>0)
		this.sort=sort;
	}

	/** set the value mode
	*  Used with action = "Create" to define the permissions for a directory on UNIX and Linux 
	* 		platforms. Ignored on Windows. Options correspond to the octal values of the UNIX chmod command. From 
	* 		left to right, permissions are assigned for owner, group, and other.
	* @param mode value to set
	 * @throws PageException 
	**/
	public void setMode(String mode) throws PageException	{
		try {
			this.mode=ModeUtil.toOctalMode(mode);
		} 
		catch (IOException e) {
			throw Caster.toPageException(e);
		}
	}

	/** set the value newdirectory
	*  Required for action = "rename". Ignored by all other actions. The new name of the directory 
	* 		specified in the directory attribute.
	* @param newdirectory value to set
	**/
	public void setNewdirectory(String newdirectory)	{
		//this.newdirectory=ResourceUtil.toResourceNotExisting(pageContext ,newdirectory);
		this.strNewdirectory=newdirectory;
	}
	public void setDestination(String destination)	{
		this.destination=destination;
	}

	/** set the value name
	*  Required for action = "list". Ignored by all other actions. Name of output query for directory 
	* 		listing.
	* @param name value to set
	**/
	public void setName(String name)	{
		this.name=name;
	}
    
    /**
     * @param recurse The recurse to set.
     */
    public void setRecurse(boolean recurse) {
        this.recurse = recurse;
    }

    /** set the value nameconflict
	*  Action to take if destination directory is the same as that of a file in the directory.
	* @param nameconflict value to set
	 * @throws ApplicationException 
	**/
	public void setNameconflict(String nameconflict) throws ApplicationException	{
		this.nameconflict = toNameconflict( nameconflict );
	}
	
	public static int toNameconflict( String nameconflict ) throws ApplicationException	{
		
		if ( StringUtil.isEmpty( nameconflict, true ) )
			return NAMECONFLICT_UNDEFINED;
		
		nameconflict = nameconflict.trim();
		
		if ( "merge".equalsIgnoreCase( nameconflict ) || "overwrite".equalsIgnoreCase( nameconflict ) )
			return NAMECONFLICT_OVERWRITE;
				
		if ( "error".equalsIgnoreCase( nameconflict ) )
			return NAMECONFLICT_ERROR;

		if ( "skip".equalsIgnoreCase( nameconflict ) )
			return NAMECONFLICT_SKIP;
						
		throw new ApplicationException("invalid value for attribute/argument nameconflict ["+nameconflict+"]",
			"valid values are [error,merge,overwrite]");
	}
	
	@Override
	public int doStartTag() throws PageException	{

	    //securityManager = pageContext.getConfig().getSecurityManager();
		if(action.equals("list")) {
			Object res=actionList(pageContext,directory,serverPassword,type,filter,nameFilter,listInfo,recurse,sort);
			if(!StringUtil.isEmpty(name) && res!=null)pageContext.setVariable(name,res);
		}
		else if(action.equals("create")) actionCreate(pageContext,directory,serverPassword,createPath,mode,acl,storage, nameconflict);
		else if(action.equals("delete")) actionDelete(pageContext,directory,recurse,serverPassword);
		else if(action.equals("forcedelete")) actionDelete(pageContext,directory,true,serverPassword);
		else if(action.equals("rename")) actionRename(pageContext,directory,strNewdirectory,serverPassword,createPath,acl,storage);
		else if(action.equals("copy")) {
			if(StringUtil.isEmpty(destination,true) && !StringUtil.isEmpty(strNewdirectory,true)) {
				destination=strNewdirectory.trim();
			}
			actionCopy(pageContext,directory,destination,serverPassword,createPath,acl,storage,filter,recurse, nameconflict);
		}
		else throw new ApplicationException("invalid action ["+action+"] for the tag directory");
			
		return SKIP_BODY;
	}


	@Override
	public int doEndTag()	{
		return EVAL_PAGE;
	}

	/**
	 * list all files and directories inside a directory
	 * @throws PageException
	 */
	public static Object actionList(PageContext pageContext,Resource directory, String serverPassword, int type,ResourceFilter filter,ResourceAndResourceNameFilter nameFilter, 
			int listInfo,boolean recurse,String sort) throws PageException {
		// check directory
		SecurityManager securityManager = pageContext.getConfig().getSecurityManager();
		securityManager.checkFileLocation(pageContext.getConfig(),directory,serverPassword);
	    
	    if(type!=TYPE_ALL) {
	    	ResourceFilter typeFilter = (type==TYPE_DIR)?DIRECTORY_FILTER:FILE_FILTER;
	    	if(filter==null) filter=typeFilter; 
	    	else filter=new AndResourceFilter(new ResourceFilter[]{typeFilter,filter});
	    }
	    
	    
		// create query Object
	    String[] names = new String[]{"name","size","type","dateLastModified","attributes","mode","directory"};
	    String[] types=new String[]{"VARCHAR","DOUBLE","VARCHAR","DATE","VARCHAR","VARCHAR","VARCHAR"};
	    
	    boolean hasMeta=directory instanceof ResourceMetaData;
	    if(hasMeta){
	    	names = new String[]{"name","size","type","dateLastModified","attributes","mode","directory","meta"};
		    types=new String[]{"VARCHAR","DOUBLE","VARCHAR","DATE","VARCHAR","VARCHAR","VARCHAR","OBJECT"};
	    }
	    Array array=null;
	    Query query=null;
	    Object rtn;
	    if(listInfo==LIST_INFO_QUERY_ALL || listInfo==LIST_INFO_QUERY_NAME){
	    	boolean listOnlyNames=listInfo==LIST_INFO_QUERY_NAME;
	    	rtn=query=new QueryImpl(
				listOnlyNames?new String[]{"name"}:names,
				listOnlyNames?new String[]{"VARCHAR"}:types,
				0,"query");
	    }
	    else 
	    	rtn=array=new ArrayImpl();
	    
	    if(!directory.exists()){
	    	if(directory instanceof FileResource) return rtn;
			throw new ApplicationException("directory ["+directory.toString()+"] doesn't exist");
		}
		if(!directory.isDirectory()){
			if(directory instanceof FileResource) return rtn;
			throw new ApplicationException("file ["+directory.toString()+"] exists, but isn't a directory");
		}
		if(!directory.isReadable()){
			if(directory instanceof FileResource) return rtn;
			throw new ApplicationException("no access to read directory ["+directory.toString()+"]");
		}
		
		long startNS=System.nanoTime();
		
		try {
			// Query All
			if(listInfo==LIST_INFO_QUERY_ALL)
				_fillQueryAll(query,directory,filter,0,hasMeta,recurse);
			
			// Query Name
			else if(listInfo==LIST_INFO_QUERY_NAME) {
        	   if(recurse || type!=TYPE_ALL)_fillQueryNamesRec("",query, directory, filter, 0,recurse);
        	   else _fillQueryNames(query, directory, nameFilter, 0);
			}
			
			//Array Name/Path
			else if(listInfo==LIST_INFO_ARRAY_NAME || listInfo==LIST_INFO_ARRAY_PATH) {
				boolean onlyName=listInfo==LIST_INFO_ARRAY_NAME;
        	   if(!onlyName || recurse || type!=TYPE_ALL)_fillArrayPathOrName(array, directory, nameFilter, 0, recurse, onlyName);//QueryNamesRec("",query, directory, filter, 0,recurse);
        	   else _fillArrayName(array, directory, nameFilter, 0);
			}	
			 
        	   
        } catch (IOException e) {
            throw Caster.toPageException(e);
        }
		
		// sort
		if(sort!=null && query!=null) {
			String[] arr=sort.toLowerCase().split(",");
			for(int i=arr.length-1;i>=0;i--) {
				try {
					String[] col=arr[i].trim().split("\\s+");
					if(col.length==1)query.sort(col[0].trim());
					else if(col.length==2) {
						String order=col[1].toLowerCase().trim();
						if(order.equals("asc"))
							query.sort(col[0],railo.runtime.type.Query.ORDER_ASC);
						else if(order.equals("desc"))
							query.sort(col[0],railo.runtime.type.Query.ORDER_DESC);
						else 
							throw new ApplicationException("invalid order type ["+col[1]+"]");
					}
				}
				catch(Throwable t) {}
			}		
		}
		if(query!=null)query.setExecutionTime(System.nanoTime()-startNS);
		return rtn; 
	}
	
	


    private static int _fillQueryAll(Query query, Resource directory, ResourceFilter filter, int count, boolean hasMeta, boolean recurse) throws PageException, IOException {
    	//long start=System.currentTimeMillis();
    	Resource[] list=directory.listResources();
    	
		if(list==null || list.length==0) return count;
        String dir=directory.getCanonicalPath();
        //      fill data to query
        //query.addRow(list.length);
        boolean isDir;
        for(int i=0;i<list.length;i++) {
            if(filter==null || filter.accept(list[i])) {
                query.addRow(1);
                count++;
                query.setAt(KeyConstants._name,count,list[i].getName());
                isDir=list[i].isDirectory();
                query.setAt(KeyConstants._size,count,new Double(isDir?0:list[i].length()));
                query.setAt(KeyConstants._type,count,isDir?"Dir":"File");
                if(directory.getResourceProvider().isModeSupported()){
                        	
                	query.setAt(MODE,count,new ModeObjectWrap(list[i]));
                }
                query.setAt(DATE_LAST_MODIFIED,count,new Date(list[i].lastModified()));
                query.setAt(ATTRIBUTES,count,getFileAttribute(list[i],true));
                
                if(hasMeta){
                	query.setAt(META,count,((ResourceMetaData)list[i]).getMetaData());
                }
                
        		query.setAt(DIRECTORY,count,dir);
            }   
            if(recurse && list[i].isDirectory())
                count=_fillQueryAll(query,list[i],filter,count,hasMeta,recurse);
        }
        return count;
    }
 // this method only exists for performance reasion
    private static int _fillQueryNames(Query query, Resource directory, ResourceNameFilter filter, int count) throws PageException {
    	String[] list=directory.list();
    	if(list==null || list.length==0) return count;
        for(int i=0;i<list.length;i++) {
            if(filter==null || filter.accept(directory,list[i])) {
                query.addRow(1);
                count++;
                query.setAt(KeyConstants._name,count,list[i]);  
            }     
        }
        return count;
    }
    
    private static int _fillQueryNamesRec(String parent, Query query, Resource directory, ResourceFilter filter, int count, boolean recurse) throws PageException {
    	Resource[] list=directory.listResources();
    	if(list==null || list.length==0) return count;
        for(int i=0;i<list.length;i++) {
            if(filter==null || filter.accept(list[i])) {
                query.addRow(1);
                count++;
                query.setAt(KeyConstants._name,count,parent.concat(list[i].getName()));
                
            } 
            if(recurse && list[i].isDirectory())
                count=_fillQueryNamesRec(parent+list[i].getName()+"/",query,list[i],filter,count,recurse);  
        }
        return count;
    }
    
    private static int _fillArrayPathOrName(Array arr, Resource directory, ResourceFilter filter, int count, boolean recurse,boolean onlyName) throws PageException {
    	Resource[] list=directory.listResources();
    	if(list==null || list.length==0) return count;
        for(int i=0;i<list.length;i++) {
            if(filter==null || filter.accept(list[i])) {
                arr.appendEL(onlyName?list[i].getName():list[i].getAbsolutePath());
            	count++;
                
            } 
            if(recurse && list[i].isDirectory())
                count=_fillArrayPathOrName(arr,list[i],filter,count,recurse,onlyName);  
        }
        return count;
    }
    
    // this method only exists for performance reasion
    private static int _fillArrayName(Array arr, Resource directory, ResourceNameFilter filter, int count) {
    	String[] list=directory.list();
    	if(list==null || list.length==0) return count;
        for(int i=0;i<list.length;i++) {
            if(filter==null || filter.accept(directory,list[i])) {
            	arr.appendEL(list[i]);  
            }     
        }
        return count;
    }



    /**
	 * create a directory
	 * @throws PageException 
	 */
    public static void actionCreate(PageContext pc,Resource directory,String serverPassword, boolean createPath, int mode, Object acl, int storage, int nameConflict) throws PageException {

    	SecurityManager securityManager = pc.getConfig().getSecurityManager();
	    securityManager.checkFileLocation(pc.getConfig(),directory,serverPassword);
	    
		if(directory.exists()) {
			if(directory.isDirectory()) {
				if ( nameConflict == NAMECONFLICT_SKIP )
					return;

				throw new ApplicationException("directory ["+directory.toString()+"] already exist");
			}
			else if(directory.isFile())
				throw new ApplicationException("can't create directory ["+directory.toString()+"], it exist a file with same name");
		}
		//if(!directory.mkdirs())	throw new ApplicationException("can't create directory ["+directory.toString()+"]");
		try {
			directory.createDirectory(createPath);
		} catch (IOException ioe) {
			throw Caster.toPageException(ioe);
		}
		
		// set S3 stuff
		setS3Attrs(directory,acl,storage);
	    
		// Set Mode
		if(mode!=-1) {
			try {
            	directory.setMode(mode);
                //FileUtil.setMode(directory,mode);
            } catch (IOException e) {
                throw Caster.toPageException(e);
            }
		}
	}
	
	private static void setS3Attrs(Resource res,Object acl,int storage) throws PageException {
		String scheme = res.getResourceProvider().getScheme();
		
		if("s3".equalsIgnoreCase(scheme)){
			S3Resource s3r=(S3Resource) res;
			if(acl!=null){
				try {
					// old way
					if(Decision.isString(acl)) {
						if(Decision.isInteger(acl)) s3r.setACL(Caster.toIntValue(acl));
						else s3r.setACL(S3.toIntACL(Caster.toString(acl)));
					}
					// new way
					else {
						StoreSetACL.invoke(s3r, acl);
					}
				} catch (IOException e) {
					throw Caster.toPageException(e);
				}
			}
			
			if(storage!=S3Constants.STORAGE_UNKNOW) s3r.setStorage(storage);
		}
	}



	/**
	 * delete directory
	 * @param dir
	 * @param forceDelete
	 * @throws PageException 
	 */
	public static void actionDelete(PageContext pc,Resource dir, boolean forceDelete,String serverPassword) throws PageException {
		SecurityManager securityManager = pc.getConfig().getSecurityManager();
	    securityManager.checkFileLocation(pc.getConfig(),dir,serverPassword);
	    
		// directory doesn't exist
		if(!dir.exists()) {
			if(dir.isDirectory())
				throw new ApplicationException("directory ["+dir.toString()+"] doesn't exist");
			else if(dir.isFile())
				throw new ApplicationException("file ["+dir.toString()+"] doesn't exist and isn't a directory");
		}
		
		// check if file
		if(dir.isFile())
			throw new ApplicationException("can't delete ["+dir.toString()+"], it isn't a directory it is a file");
		
		// delete directory
		try {
			dir.remove(forceDelete);
		} catch (IOException e) {
			throw Caster.toPageException(e);
		}
	}

	/**
	 * rename a directory to a new Name
	 * @throws PageException 
	 */
	public static  void actionRename(PageContext pc,Resource directory,String strNewdirectory,String serverPassword, boolean createPath, Object acl,int storage) throws PageException {
		// check directory
		SecurityManager securityManager = pc.getConfig().getSecurityManager();
	    securityManager.checkFileLocation(pc.getConfig(),directory,serverPassword);
		
	    
		if(!directory.exists())
			throw new ApplicationException("the directory ["+directory.toString()+"] doesn't exist");
		if(!directory.isDirectory())
			throw new ApplicationException("the file ["+directory.toString()+"] exists, but it isn't a directory");
		if(!directory.canRead())
			throw new ApplicationException("no access to read directory ["+directory.toString()+"]");
		
		if(strNewdirectory==null)
			throw new ApplicationException("the attribute [newDirectory] is not defined");
		
		// real to source 
		Resource newdirectory=toDestination(pc,strNewdirectory,directory);
		
	    securityManager.checkFileLocation(pc.getConfig(),newdirectory,serverPassword);
		if(newdirectory.exists())
			throw new ApplicationException("new directory ["+newdirectory.toString()+"] already exists");
		if(createPath) {
			newdirectory.getParentResource().mkdirs();
			
		}
		try {
			directory.moveTo(newdirectory);
		}
		catch(Throwable t) {
			throw Caster.toPageException(t);
		}
		
		// set S3 stuff
		setS3Attrs(directory,acl,storage);
	    
	}
	
	
	public static  void actionCopy(PageContext pc,Resource directory,String strDestination,String serverPassword,boolean createPath, Object acl,int storage, ResourceFilter filter, boolean recurse, int nameconflict) throws PageException {
		// check directory
		SecurityManager securityManager = pc.getConfig().getSecurityManager();
	    securityManager.checkFileLocation(pc.getConfig(),directory,serverPassword);
		
	    
		if(!directory.exists())
			throw new ApplicationException("directory ["+directory.toString()+"] doesn't exist");
		if(!directory.isDirectory())
			throw new ApplicationException("file ["+directory.toString()+"] exists, but isn't a directory");
		if(!directory.canRead())
			throw new ApplicationException("no access to read directory ["+directory.toString()+"]");
		
		if(StringUtil.isEmpty(strDestination))
			throw new ApplicationException("attribute destination is not defined");
		
		// real to source 
		Resource newdirectory=toDestination(pc,strDestination,directory);
		
		if ( nameconflict == NAMECONFLICT_ERROR && newdirectory.exists() )
			throw new ApplicationException("new directory ["+newdirectory.toString()+"] already exist");
		
	    securityManager.checkFileLocation(pc.getConfig(),newdirectory,serverPassword);

		try {
			// has already a filter
			if(filter!=null) {
				if(recurse) filter=new OrResourceFilter(new ResourceFilter[]{
						filter,DirectoryResourceFilter.FILTER
				});
			}
			else {
				if(!recurse)filter=new NotResourceFilter(DirectoryResourceFilter.FILTER);
			}
			if(!createPath) {
				Resource p = newdirectory.getParentResource();
				if(p!=null && !p.exists())
					throw new ApplicationException("parent directory for ["+newdirectory+"] doesn't exist");
			}
			ResourceUtil.copyRecursive(directory, newdirectory,filter);
		}
		catch(Throwable t) {
			throw new ApplicationException(t.getMessage());
		}
		
		// set S3 stuff
		setS3Attrs(directory,acl,storage);
	    
	}

	
	private static Resource toDestination(PageContext pageContext,String path, Resource source) {
		if(source!=null && path.indexOf(File.separatorChar)==-1 && path.indexOf('/')==-1 && path.indexOf('\\')==-1) {
			Resource p = source.getParentResource();
			if(p!=null)return p.getRealResource(path);
		}
		return ResourceUtil.toResourceNotExisting(pageContext ,path);
	}


	private static String getFileAttribute(Resource file, boolean exists){
		return  exists && !file.isWriteable() ? "R".concat(file.isHidden() ? "H" : "") : file.isHidden() ? "H" : "";
	}


	/**
	 * @param strType the type to set
	 */
	public void setType(String strType) throws ApplicationException {
		strType=strType.trim().toLowerCase();
		
		if("all".equals(strType)) type=TYPE_ALL;
		else if("dir".equals(strType)) type=TYPE_DIR;
		else if("directory".equals(strType)) type=TYPE_DIR;
		else if("file".equals(strType)) type=TYPE_FILE;
		else throw new ApplicationException("invalid type ["+strType+"] for the tag directory");
			
	}

}