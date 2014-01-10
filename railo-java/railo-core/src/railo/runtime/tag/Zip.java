package railo.runtime.tag;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import railo.commons.io.IOUtil;
import railo.commons.io.compress.ZipUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.filter.DirectoryResourceFilter;
import railo.commons.io.res.filter.FileResourceFilter;
import railo.commons.io.res.filter.OrResourceFilter;
import railo.commons.io.res.filter.ResourceFilter;
import railo.commons.io.res.util.FileWrapper;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.io.res.util.WildcardPatternFilter;
import railo.commons.lang.StringUtil;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.BodyTagImpl;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.dt.DateTimeImpl;

public final class Zip extends BodyTagImpl {

	private String action="zip";
	private String charset;
	private Resource destination;
	private String entryPath;
	private Resource file;
	private WildcardPatternFilter filter;
	private String name;
	private boolean overwrite;
	private String prefix;
	private boolean recurse=true;
	private boolean showDirectory;
	private boolean storePath=true;
	private String variable;
	private List<ZipParamAbstr> params;
	private Set<String> alreadyUsed;
	private Resource source;
	private static int id=0;
	
	
    @Override
    public void release()   {
        super.release();
        action="zip";
    	charset=null;
    	destination=null;
    	entryPath=null;
    	file=null;
    	filter=null;
    	name=null;
    	overwrite=false;
    	prefix=null;
    	recurse=true;
    	showDirectory=false;
    	source=null;
    	storePath=true;
    	variable=null;

    	if(params!=null)params.clear();
    	if(alreadyUsed!=null)alreadyUsed.clear();
    }
	
    
    
    /**
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action.trim().toLowerCase();
	}



	/**
	 * @param charset the charset to set
	 */
	public void setCharset(String charset) {
		this.charset = charset;
	}



	/**
	 * @param destination the destination to set
	 * @throws ExpressionException 
	 * @throws PageException 
	 */
	public void setDestination(String strDestination) throws PageException {
		this.destination = ResourceUtil.toResourceExistingParent(pageContext, strDestination);
		if(!destination.exists())destination.mkdirs();
		
		if(!destination.isDirectory())
			throw new ApplicationException("destination ["+strDestination+"] is not a existing directory");
		
		
	}



	/**
	 * @param entryPath the entryPath to set
	 */
	public void setEntrypath(String entryPath) {
		if(StringUtil.isEmpty(entryPath,true)) return;

		entryPath=entryPath.trim();
		entryPath=entryPath.replace('\\','/');
		
		if(StringUtil.startsWith(entryPath,'/'))entryPath=entryPath.substring(1);
		if(StringUtil.endsWith(entryPath,'/'))entryPath=entryPath.substring(0,entryPath.length()-1);
		this.entryPath = entryPath;
	}



	/**
	 * @param file the file to set
	 * @throws ExpressionException 
	 */
	public void setFile(String file) {
		this.file = ResourceUtil.toResourceNotExisting(pageContext, file);
	}

	/**
	 * @param filter the filter to set
	 */
	public void setFilter(String filter) {
		
		if ( !filter.isEmpty() )
			this.filter = new WildcardPatternFilter( filter );
	}



	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}



	/**
	 * @param overwrite the overwrite to set
	 */
	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}



	/**
	 * @param prefix the prefix to set
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}



	/**
	 * @param recurse the recurse to set
	 */
	public void setRecurse(boolean recurse) {
		this.recurse = recurse;
	}



	/**
	 * @param showDirectory the showDirectory to set
	 */
	public void setShowdirectory(boolean showDirectory) {
		this.showDirectory = showDirectory;
	}



	/**
	 * @param source the source to set
	 * @throws PageException 
	 */
	public void setSource(String strSource) throws PageException {
		source = ResourceUtil.toResourceExisting(pageContext, strSource);
		
	}



	/**
	 * @param storePath the storePath to set
	 */
	public void setStorepath(boolean storePath) {
		this.storePath = storePath;
	}



	/**
	 * @param variable the variable to set
	 */
	public void setVariable(String variable) {
		this.variable = variable;
	}



	@Override
	public int doStartTag() throws PageException	{
		return EVAL_BODY_INCLUDE;
	}

	private void actionDelete() throws ApplicationException, IOException {
		required("file",file,true);
		

		Resource existing = pageContext.getConfig().getTempDirectory().getRealResource(getTempName());
		IOUtil.copy(file, existing);
		
		ZipInputStream zis = null;  
		ZipOutputStream zos = null;
		try {
			zis = new ZipInputStream( IOUtil.toBufferedInputStream(existing.getInputStream()) );  
			zos = new ZipOutputStream(IOUtil.toBufferedOutputStream(file.getOutputStream()));
			
	        ZipEntry entry;
	        String path,name;
	        int index;
	        boolean accept;
	        
	        if(filter==null && recurse && entryPath==null)
	        	throw new ApplicationException("define at least one restriction, can't delete all the entries from a zip file");
	        
	        while ( ( entry = zis.getNextEntry()) != null ) {
	        	accept=false;
	        	path = entry.getName().replace('\\', '/');
            	index=path.lastIndexOf('/');
            	
	        	if(!recurse && index>0) accept=true;
	        	
	        	//dir=index==-1?"":path.substring(0,index);
            	name=path.substring(index+1);
            	
            	if(filter!=null && !filter.accept(name)) accept=true;
            	if(!entryPathMatch(path)) accept=true;
	        	
            	if(!accept) continue;
	        	
	        	add(zos, entry, zis, false);
	        	zis.closeEntry();
	        }
		}
		finally {
			IOUtil.closeEL(zis);
			IOUtil.closeEL(zos);
			existing.delete();
		}
		
	}



	private void actionList() throws PageException, IOException {
		required("file",file,true);
		required("name",name);
		
		
		
		
		railo.runtime.type.Query query=new QueryImpl(
                new String[]{"name","size","type","dateLastModified","directory","crc","compressedSize","comment"},
                0,"query");
		pageContext.setVariable(name, query);
        
		ZipFile zip = getZip(file);
		Enumeration entries = zip.entries();
		
        try {
        	String path,name,dir;
            ZipEntry ze;
            int row=0,index;
            while(entries.hasMoreElements()) {
            	ze = (ZipEntry)entries.nextElement();
            	if(!showDirectory && ze.isDirectory()) continue;
            	
                path = ze.getName().replace('\\', '/');
            	index=path.lastIndexOf('/');
            	if(!recurse && index>0) continue;
            	
            	dir=index==-1?"":path.substring(0,index);
            	name=path.substring(index+1);
            	            	
            	if(filter!=null && !filter.accept(null, name)) continue;
            	
            	
            	if(!entryPathMatch(dir)) continue;
            	//if(entryPath!=null && !(dir.equalsIgnoreCase(entryPath) || StringUtil.startsWithIgnoreCase(dir,entryPath+"/"))) ;///continue;
            	
            	row++;
            	query.addRow();
            	query.setAt("name", row, path);
            	query.setAt("size", row, Caster.toDouble(ze.getSize()));
            	query.setAt("type", row, ze.isDirectory()?"Directory":"File");
            	query.setAt("dateLastModified", row, new DateTimeImpl(pageContext,ze.getTime(),false));
            	query.setAt("crc", row, Caster.toDouble(ze.getCrc()));
            	query.setAt("compressedSize", row, Caster.toDouble(ze.getCompressedSize()));
            	query.setAt("comment", row, ze.getComment());
            	query.setAt("directory", row, dir);
            	//zis.closeEntry();
                
            }
        }
        finally {
        	IOUtil.closeEL(zip);
        }   
	}

	private boolean entryPathMatch(String dir) {
		if(entryPath==null) return true;
		
		return dir.equalsIgnoreCase(entryPath) || StringUtil.startsWithIgnoreCase(dir,entryPath+"/");
	}



	private void actionRead(boolean binary) throws ZipException, IOException, PageException {
		required("file",file,true);
		required("variable",variable);
		required("entrypath",variable);
		ZipFile zip = getZip(file);
		
		try {
			ZipEntry ze = zip.getEntry(entryPath);
			if(ze==null)ze = zip.getEntry(entryPath+"/");
			if(ze==null) throw new ApplicationException("zip file ["+file+"] has no entry with name ["+entryPath+"]");
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			InputStream is = zip.getInputStream(ze);
			IOUtil.copy(is, baos,true,false);
			zip.close();
			
			if(binary)
				pageContext.setVariable(variable, baos.toByteArray());
			else {
				if(charset==null)charset=pageContext.getConfig().getResourceCharset();
				pageContext.setVariable(variable, new String(baos.toByteArray(),charset));
			}
		}
		finally {
			IOUtil.closeEL(zip);
		}
		
	}



	private void actionUnzip() throws ApplicationException, IOException {
		required("file",file,true);
		required("destination",destination,false);

		ZipInputStream zis=null;
		String path;
		Resource target,parent;
		int index;
        try {
        	
	        zis = new ZipInputStream( IOUtil.toBufferedInputStream(file.getInputStream()) ) ;     
	        ZipEntry entry;
	        while ( ( entry = zis.getNextEntry()) != null ) {
	        	
        		path = entry.getName().replace('\\', '/');
            	index=path.lastIndexOf('/');
            	
            	// recurse
            	if(!recurse && index!=-1) {
            		zis.closeEntry();
            		continue;
            	}
            	// filter
            	if(filter!=null && !filter.accept(path.substring(index+1))) {
            		zis.closeEntry();
            		continue;
            	}
            	// entrypath
            	if(!entryPathMatch(path)) {
            		zis.closeEntry();
            		continue;
            	}
	        	target=destination.getRealResource(entry.getName());
	        	if(!storePath) target=destination.getRealResource(target.getName());
	            if(entry.isDirectory()) {
	                target.mkdirs();
	            }
	            else {
	            	if(storePath){
	            		parent=target.getParentResource();
	            		if(!parent.exists())parent.mkdirs();
	            	}
	                if(overwrite || !target.exists())IOUtil.copy(zis,target,false);
	            }
	            target.setLastModified(entry.getTime());
	            zis.closeEntry() ;
	        }
        }
        finally {
        	IOUtil.closeEL(zis);
        }
    }



	private void actionZip() throws PageException, IOException {
		required("file",file,false);
		Resource dir = file.getParentResource();
		
		if(!dir.exists()) {
			throw new ApplicationException("directory ["+dir.toString()+"] doesn't exist"); 
		}
		
		
		
		if((params==null || params.isEmpty()) && source!=null) {
			setParam(new ZipParamSource(source,entryPath,filter,prefix,recurse));
		}

		if((params==null || params.isEmpty())) {
			throw new ApplicationException("No source/content specified");
		}
		
		
		
		
		ZipOutputStream zos=null;
		Resource existing=null;
		try {
			
			// existing
			if(!overwrite && file.exists()) {
				existing = pageContext.getConfig().getTempDirectory().getRealResource(getTempName());
				IOUtil.copy(file, existing);
			}
			
				zos = new ZipOutputStream(IOUtil.toBufferedOutputStream(file.getOutputStream()));

			Object[] arr = params.toArray();
			for(int i=arr.length-1;i>=0;i--) {
				if(arr[i] instanceof ZipParamSource)
					actionZip(zos,(ZipParamSource)arr[i]);
				else if(arr[i] instanceof ZipParamContent)
					actionZip(zos,(ZipParamContent)arr[i]);
			}

			if(existing!=null) {
				ZipInputStream zis = new ZipInputStream( IOUtil.toBufferedInputStream(existing.getInputStream()) );  
				try {
			        ZipEntry entry;
			        while ( ( entry = zis.getNextEntry()) != null ) {
			        	add(zos, entry, zis, false);
			        	zis.closeEntry();
			        }
				}
				finally {
					zis.close();
				}
			}
		}
		finally {
			ZipUtil.close(zos);
			if(existing!=null)existing.delete();
			
		}
		
	}



	private String getTempName() {
		return "tmp-"+(id++)+".zip";
	}



	private void actionZip(ZipOutputStream zos, ZipParamContent zpc) throws PageException, IOException {
		Object content = zpc.getContent();
		if(Decision.isBinary(content)) {
			add(zos, new ByteArrayInputStream(Caster.toBinary(content)), zpc.getEntryPath(), System.currentTimeMillis(), true);
			
		}
		else {
			String charset=zpc.getCharset();
			if(StringUtil.isEmpty(charset))charset=pageContext.getConfig().getResourceCharset();
			add(zos, new ByteArrayInputStream(content.toString().getBytes(charset)), zpc.getEntryPath(), System.currentTimeMillis(), true);
		}
	}



	private void actionZip(ZipOutputStream zos, ZipParamSource zps) throws IOException {
		// prefix
		String p=zps.getPrefix();
		if(StringUtil.isEmpty(p))
			p=this.prefix;
		
		if(!StringUtil.isEmpty(p)){
			if(!StringUtil.endsWith(p, '/'))p+="/";
		}
		else 
			p="";
		
		
		
		if(zps.getSource().isFile()){
			
			String ep = zps.getEntryPath();
			if(ep==null)ep=zps.getSource().getName();
			if(!StringUtil.isEmpty(p)) ep=p+ep;
			
			add(zos,zps.getSource().getInputStream(),ep,zps.getSource().lastModified(),true);
		}	
		else {
			
			
			
			// filter
			ResourceFilter f = zps.getFilter();
			if(f==null)f=this.filter;
			if(zps.isRecurse()) {
				if(f!=null)f=new OrResourceFilter(new ResourceFilter[]{DirectoryResourceFilter.FILTER,f});
			}
			else {
				if(f==null)f=FileResourceFilter.FILTER;
			}
			
			addDir(zos,zps.getSource(),p,f);
		}
	}



	private void addDir(ZipOutputStream zos, Resource dir, String parent, ResourceFilter filter) throws IOException {
		Resource[] children = filter==null?dir.listResources():dir.listResources(filter);
		
		for(int i=0;i<children.length;i++) {
			
			
			if(children[i].isDirectory()) addDir(zos, children[i], parent+children[i].getName()+"/",filter);
			else {
				add(zos, children[i].getInputStream(), parent+children[i].getName(), children[i].lastModified(), true);
			}
		}
	}



	private void add(ZipOutputStream zos, InputStream is, String path, long lastMod, boolean closeInput) throws IOException {
		ZipEntry ze=new ZipEntry(path);
		ze.setTime(lastMod);
		add(zos, ze, is, closeInput);
	}
	
	private void add(ZipOutputStream zos, ZipEntry entry,InputStream is, boolean closeInput) throws IOException {
		if(alreadyUsed==null)alreadyUsed=new HashSet<String>();
		else if(alreadyUsed.contains(entry.getName())) return;
		zos.putNextEntry(entry);
        try {
            IOUtil.copy(is,zos,closeInput,false);
        } 
        finally {
            zos.closeEntry();
        }
		alreadyUsed.add(entry.getName());
	}



	@Override
	public void doInitBody()	{
		
	}

	@Override
	public int doAfterBody()	{
		return SKIP_BODY;
	}
    
    @Override
	public int doEndTag() throws PageException	{//print.out("doEndTag"+doCaching+"-"+body);
		try {
			if(action.equals("delete")) actionDelete();
			else if(action.equals("list")) actionList();
			else if(action.equals("read")) actionRead(false);
			else if(action.equals("readbinary")) actionRead(true);
			else if(action.equals("unzip")) actionUnzip();
			else if(action.equals("zip")) actionZip();
	        else 
				throw new ApplicationException("invalid value ["+action+"] for attribute action","values for attribute action are:info,move,rename,copy,delete,read,readbinary,write,append,upload");
		}
		catch(IOException ioe) {
			throw Caster.toPageException(ioe);
		}
		
		return EVAL_PAGE;
	}

	/**
     * sets if tag has a body or not
     * @param hasBody
     */
    public void hasBody(boolean hasBody) {
       ///this.hasBody=hasBody;
    }

    private ZipFile getZip(Resource file) throws ZipException, IOException {
		return new ZipFile(FileWrapper.toFile(file));
	}
    
    /**
	 * throw a error if the value is empty (null)
     * @param attributeName
     * @param atttributValue
	 * @throws ApplicationException
     */
    private void required(String attributeName, String attributValue) throws ApplicationException {
        if(StringUtil.isEmpty(attributValue))
            throw new ApplicationException(
                    "invalid attribute constellation for the tag zip", 
                    "attribute ["+attributeName+"] is required, if action is ["+action+"]");
    }

    /**
	 * throw a error if the value is empty (null)
     * @param attributeName
     * @param atttributValue
	 * @throws ApplicationException
     */
    private void required(String attributeName, Resource attributValue, boolean exists) throws ApplicationException {
        if(attributValue==null)
            throw new ApplicationException(
                    "invalid attribute constellation for the tag zip", 
                    "attribute ["+attributeName+"] is required, if action is ["+action+"]");
        
        if(exists && !attributValue.exists())
        	throw new ApplicationException(attributeName+" resource ["+attributValue+"] doesn't exist");
        else if(exists && !attributValue.canRead())
			throw new ApplicationException("no access to "+attributeName+" resource ["+attributValue+"]");
		
        
    }




	public void setParam(ZipParamAbstr param) {
		if(params==null) {
			params=new ArrayList<ZipParamAbstr>();
			alreadyUsed=new HashSet<String>();
		}
		params.add(param);
	}



	/**
	 * @return the source
	 */
	public Resource getSource() {
		return source;
	}
    
    
}