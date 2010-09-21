package railo.runtime.tag;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import railo.commons.io.IOUtil;
import railo.commons.io.ModeUtil;
import railo.commons.io.SystemUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.type.s3.S3Constants;
import railo.commons.io.res.util.ModeObjectWrap;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.TagImpl;
import railo.runtime.functions.list.ListFirst;
import railo.runtime.functions.list.ListLast;
import railo.runtime.img.ImageUtil;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.reflection.Reflector;
import railo.runtime.type.Array;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.List;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.dt.DateImpl;
import railo.runtime.type.dt.DateTimeImpl;
import railo.runtime.type.scope.FormImpl;

/**
* Handles all interactions with files. The attributes you use with cffile depend on the value of the action attribute. 
*  For example, if the action = "write", use the attributes associated with writing a text file.
*
*
*
**/
public final class FileTag extends TagImpl {

	public static final int NAMECONFLICT_UNDEFINED=0;
	public static final int NAMECONFLICT_ERROR=1;
	public static final int NAMECONFLICT_SKIP=2;
	public static final int NAMECONFLICT_OVERWRITE=3;
	public static final int NAMECONFLICT_MAKEUNIQUE=4;
	private static final Key SET_ACL = KeyImpl.getInstance("setACL");
    
    //private static final String DEFAULT_ENCODING=Charset.getDefault();

	/** Type of file manipulation that the tag performs. */
	private String action;

	/** Absolute pathname of directory or file on web server. */
	private String strDestination;

	/** Content of the file to be created. */
	private Object output;

	/** Absolute pathname of file on web server. */
	private Resource file;

	/** Applies only to Solaris and HP-UX. Permissions. Octal values of UNIX chmod command. Assigned to owner, group, and other, respectively. */
	private int mode=-1;

	/** Name of variable to contain contents of text file. */
	private String variable;

	/** Name of form field used to select the file. */
	private String filefield;

	/** Character set name for the file contents. */
	private String charset=null;

	/** Yes: appends newline character to text written to file */
	private boolean addnewline=true;
	private boolean fixnewline=true;
	/** One attribute (Windows) or a comma-delimited list of attributes (other platforms) to set on the file. 
	** If omitted, the file's attributes are maintained. */
	private String attributes;

	/** Absolute pathname of file on web server. 
	** On Windows, use backward slashes; on UNIX, use forward slashes. */
	private Resource source;

	/** Action to take if filename is the same as that of a file in the directory. */
	private int nameconflict=NAMECONFLICT_UNDEFINED;

	/** Limits the MIME types to accept. Comma-delimited list. For example, to permit JPG and Microsoft Word file uploads:
	** accept = "image/jpg, application/msword"
	** The browser uses file extension to determine file type. */
	private String accept;
    
    private String result=null;
	
	private railo.runtime.security.SecurityManager securityManager;

	private String serverPassword=null;
	private int acl=S3Constants.ACL_PUBLIC_READ;

	/**
	* @see javax.servlet.jsp.tagext.Tag#release()
	*/
	public void release()	{
		super.release();
		acl=S3Constants.ACL_PUBLIC_READ;
		action=null;
		strDestination=null;
		output=null;
		file=null;
		mode=-1;
		variable=null;
		filefield=null;
		charset=null;
		addnewline=true;
		fixnewline=true;
		attributes=null;
		source=null;
		nameconflict=NAMECONFLICT_UNDEFINED;
		accept=null;
		securityManager=null;
        result=null;
        serverPassword=null;
	}

	/** set the value action
	*  Type of file manipulation that the tag performs.
	* @param action value to set
	**/	
	public void setAction(String action)	{
		this.action=action.toLowerCase();
	}

	/** set the value destination
	*  Absolute pathname of directory or file on web server.
	* @param destination value to set
	**/
	public void setDestination(String destination)	{
		this.strDestination=destination;//ResourceUtil.toResourceNotExisting(pageContext ,destination);
	}

	/** set the value output
	*  Content of the file to be created.
	* @param output value to set
	**/
	public void setOutput(Object output)	{
		if(output==null)this.output="";
		else this.output=output;
	}

	/** set the value file
	*  Absolute pathname of file on web server.
	* @param file value to set
	**/
	public void setFile(String file)	{
		this.file=ResourceUtil.toResourceNotExisting(pageContext ,file);
        
	}

	/** set the value mode
	*  Applies only to Solaris and HP-UX. Permissions. Octal values of UNIX chmod command. Assigned to owner, group, and other, respectively.
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

	/** set the value variable
	*  Name of variable to contain contents of text file.
	* @param variable value to set
	**/
	public void setVariable(String variable)	{
		this.variable=variable;
	}

	/** set the value filefield
	*  Name of form field used to select the file.
	* @param filefield value to set
	**/
	public void setFilefield(String filefield)	{
		this.filefield=filefield;
	}

	/** set the value charset
	*  Character set name for the file contents.
	* @param charset value to set
	**/
	public void setCharset(String charset)	{
		this.charset=charset.trim();
	}
	
	/** set the value acl
	*  used only for s3 resources, for all others ignored
	* @param charset value to set
	 * @throws ApplicationException 
	**/
	public void setAcl(String acl) throws ApplicationException	{
		acl=acl.trim().toLowerCase();
				
		if("private".equals(acl)) 					this.acl=S3Constants.ACL_PRIVATE;
		else if("public-read".equals(acl)) 			this.acl=S3Constants.ACL_PRIVATE;
		else if("public-read-write".equals(acl))	this.acl=S3Constants.ACL_PUBLIC_READ_WRITE;
		else if("authenticated-read".equals(acl))	this.acl=S3Constants.ACL_AUTH_READ;
		
		else throw new ApplicationException("invalid value for attribute acl ["+acl+"]",
				"valid values are [private,public-read,public-read-write,authenticated-read]");
	}
	
	
	public void setServerpassword(String serverPassword)	{
	    this.serverPassword=serverPassword;
	}

	/** set the value addnewline
	*  Yes: appends newline character to text written to file
	* @param addnewline value to set
	**/
	public void setAddnewline(boolean addnewline)	{
		this.addnewline=addnewline;
	}

	/** set the value attributes
	*  One attribute (Windows) or a comma-delimited list of attributes (other platforms) to set on the file. 
	* If omitted, the file's attributes are maintained.
	* @param attributes value to set
	**/
	public void setAttributes(String attributes)	{
		this.attributes=attributes;
	}

	/** set the value source
	*  Absolute pathname of file on web server. 
	* On Windows, use backward slashes; on UNIX, use forward slashes.
	* @param source value to set
	**/
	public void setSource(String source)	{
		this.source=ResourceUtil.toResourceNotExisting(pageContext ,source);
	}

	/** set the value nameconflict
	*  Action to take if filename is the same as that of a file in the directory.
	* @param nameconflict value to set
	 * @throws ApplicationException 
	**/
	public void setNameconflict(String nameconflict) throws ApplicationException	{
		nameconflict=nameconflict.toLowerCase().trim();
		if("error".equals(nameconflict)) 			this.nameconflict=NAMECONFLICT_ERROR;
		else if("skip".equals(nameconflict)) 		this.nameconflict=NAMECONFLICT_SKIP;
		else if("overwrite".equals(nameconflict)) 	this.nameconflict=NAMECONFLICT_OVERWRITE;
		else if("makeunique".equals(nameconflict)) 	this.nameconflict=NAMECONFLICT_MAKEUNIQUE;
		else throw new ApplicationException("invalid value for attribute nameconflict ["+nameconflict+"]",
				"valid values are [error,skip,overwrite,makeunique]");
	}

	/** set the value accept
	*  Limits the MIME types to accept. Comma-delimited list. For example, to permit JPG and Microsoft Word file uploads:
	* accept = "image/jpg, application/msword"
	* The browser uses file extension to determine file type.
	* @param accept value to set
	**/
	public void setAccept(String accept)	{
		this.accept=accept;
	}
    
    /**
     * @param result The result to set.
     */
    public void setResult(String result) {
        this.result = result;
    }


	/**
	* @see javax.servlet.jsp.tagext.Tag#doStartTag()
	*/
	public int doStartTag() throws PageException	{
		
		if(StringUtil.isEmpty(charset)) charset=pageContext.getConfig().getResourceCharset();
		
	    securityManager = pageContext.getConfig().getSecurityManager();
		if(action.equals("move")) actionMove();
		else if(action.equals("rename")) actionMove();
		else if(action.equals("copy")) actionCopy();
		else if(action.equals("delete")) actionDelete();
		else if(action.equals("read")) actionRead();
		else if(action.equals("readbinary")) actionReadBinary();
		else if(action.equals("write")) actionWrite();
		else if(action.equals("append")) actionAppend();
		else if(action.equals("upload")) actionUpload();
        else if(action.equals("info")) actionInfo();
        else if(action.equals("touch")) actionTouch();
        else 
			throw new ApplicationException("invalid value ["+action+"] for attribute action","values for attribute action are:info,move,rename,copy,delete,read,readbinary,write,append,upload");
				
		return SKIP_BODY;
	}

	/**
	* @see javax.servlet.jsp.tagext.Tag#doEndTag()
	*/
	public int doEndTag()	{
		return EVAL_PAGE;
	}

	/**
	 * move source file to destination path or file
	 * @throws PageException
	 */
	private void actionMove() throws PageException {
		if(nameconflict==NAMECONFLICT_UNDEFINED) nameconflict=NAMECONFLICT_OVERWRITE;
		
		if(source==null)
			throw new ApplicationException("attribute source is not defined for tag file");
		if(StringUtil.isEmpty(strDestination))
			throw new ApplicationException("attribute destination is not defined for tag file");
		
		Resource destination=toDestination(strDestination,source);
		setACL(destination);
		
		securityManager.checkFileLocation(pageContext.getConfig(),source,serverPassword);
		securityManager.checkFileLocation(pageContext.getConfig(),destination,serverPassword);
		
		// source
		if(!source.exists())
			throw new ApplicationException("source file ["+source.toString()+"] doesn't exist");
		else if(!source.isFile())
			throw new ApplicationException("source file ["+source.toString()+"] is not a file");
		else if(!source.isReadable() || !source.isWriteable())
			throw new ApplicationException("no access to source file ["+source.toString()+"]");
		
		// destination
		if(destination.isDirectory()) destination=destination.getRealResource(source.getName());
		if(destination.exists() && nameconflict!=NAMECONFLICT_OVERWRITE) {
			// SKIP
			if(nameconflict==NAMECONFLICT_SKIP) return;
			// MAKEUNIQUE
			else if(nameconflict==NAMECONFLICT_MAKEUNIQUE) destination=makeUnique(destination);
			// ERROR
			else throw new ApplicationException("destiniation file ["+destination.toString()+"] already exist");
		}
        
		try {
			source.moveTo(destination);
				
		}
		catch(Throwable t) {
			throw new ApplicationException(t.getMessage());
		}
        setMode(destination);
        setAttributes(destination);
	}

	private Resource toDestination(String path, Resource source) {
		if(source!=null && path.indexOf(File.separatorChar)==-1 && path.indexOf('/')==-1 && path.indexOf('\\')==-1) {
			Resource p = source.getParentResource();
			if(p!=null)return p.getRealResource(path);
		}
		return ResourceUtil.toResourceNotExisting(pageContext ,path);
	}

	/**
	 * copy source file to destination file or path
	 * @throws PageException
	 */
	private void actionCopy() throws PageException {
		if(nameconflict==NAMECONFLICT_UNDEFINED) nameconflict=NAMECONFLICT_OVERWRITE;
		
		if(source==null)
			throw new ApplicationException("attribute source is not defined for tag file");
		if(StringUtil.isEmpty(strDestination))
			throw new ApplicationException("attribute destination is not defined for tag file");

		Resource destination=toDestination(strDestination,source);
		
		
		securityManager.checkFileLocation(pageContext.getConfig(),source,serverPassword);
		securityManager.checkFileLocation(pageContext.getConfig(),destination,serverPassword);
		
		// source
		if(!source.exists())
			throw new ApplicationException("source file ["+source.toString()+"] doesn't exist");
		else if(!source.isFile())
			throw new ApplicationException("source file ["+source.toString()+"] is not a file");
		else if(!source.canRead())
			throw new ApplicationException("no access to source file ["+source.toString()+"]");
		
		// destination
		if(destination.isDirectory()) destination=destination.getRealResource(source.getName());
		if(destination.exists() && nameconflict!=NAMECONFLICT_OVERWRITE) {
			// SKIP
			if(nameconflict==NAMECONFLICT_SKIP) return;
			// MAKEUNIQUE
			else if(nameconflict==NAMECONFLICT_MAKEUNIQUE) destination=makeUnique(destination);
			// ERROR
			else throw new ApplicationException("destiniation file ["+destination.toString()+"] already exist");
		}
        
		setACL(destination);
		
		
        try {
            IOUtil.copy(source,destination);			
		}
		catch(IOException e) {
			
            ApplicationException ae = new ApplicationException("can't copy file ["+source+"] to ["+destination+"]",e.getMessage());
            ae.setStackTrace(e.getStackTrace());
            throw ae;
		}
        setMode(destination);
        setAttributes(destination);
	}

	private void setACL(Resource res) {
		String scheme = res.getResourceProvider().getScheme();
		
		if("s3".equalsIgnoreCase(scheme)){
			try {
				Reflector.callMethod(res, SET_ACL, new Object[]{Caster.toInteger(acl)});
			} 
			catch (PageException e) {}
		}
		// set acl for s3 resource
		/*if(res instanceof S3Resource) {
			((S3Resource)res).setACL(acl);
		}*/
	}

	private Resource makeUnique(Resource res) {

		String ext=getFileExtension(res);
		String name=getFileName(res);
		ext=(ext==null)?"":"."+ext;
		int count=0;
		while(res.exists()) {
			res=res.getParentResource().getRealResource(name+(++count)+ext);
		}
		
		return res;
	}

	/**
	 * copy source file to destination file or path
	 * @throws PageException 
	 */
	private void actionDelete() throws PageException {
		checkFile(false,false,false);
		setACL(file);
		try {
			if(!file.delete()) throw new ApplicationException("can't delete file ["+file+"]");
		}
		catch(Throwable t) {
			throw new ApplicationException(t.getMessage());
		}
	}

	/**
	 * read source file
	 * @throws PageException
	 */
	private void actionRead() throws PageException {
		if(variable==null)
			throw new ApplicationException("attribute variable is not defined for tag file");
		checkFile(false,true,false);
		//print.ln(charset);
		//TextFile tf=new TextFile(file.getAbsolutePath());
			
		try {
		    pageContext.setVariable(variable,IOUtil.toString(file,charset));
		}
        catch (IOException e) {
        	
			throw new ApplicationException("can't read file ["+file+"]",e.getMessage());
		}

	}

	/**
	 * read source file
	 * @throws PageException
	 */
	private void actionReadBinary() throws PageException {
		if(variable==null)
			throw new ApplicationException("attribute variable is not defined for tag file");
		checkFile(false,true,false);
		
		//TextFile tf=new TextFile(file.getAbsolutePath());
		
		try {
            pageContext.setVariable(variable,IOUtil.toBytes(file));
		}catch (IOException e) {
			throw new ApplicationException("can't read binary file ["+source.toString()+"]",e.getMessage());
		}
	}
	
    /**
     * write to the source file
     * @throws PageException
     */
    private void actionWrite() throws PageException {
        if(output==null)
            throw new ApplicationException("attribute output is not defined for tag file");
        checkFile(true,false,true);
        setACL(file);
        try {
        	if(output instanceof InputStream)	{
        		IOUtil.copy(
        				(InputStream)output,
        				file,
        				false);
        	}
        	else if(Decision.isCastableToBinary(output,false))	{
        		IOUtil.copy(
        				new ByteArrayInputStream(Caster.toBinary(output)), 
        				file,
        				true);
        	}
        	else {
        		String content=Caster.toString(output);
        		if(fixnewline)content=doFixNewLine(content);
        		if(addnewline) content+=SystemUtil.getLineSeparator();
        		
                if(content.length()==0)ResourceUtil.touch(file);
                else IOUtil.write(file,content,charset,false);
        		
        	}    
        } 
        catch (UnsupportedEncodingException e) {
            throw new ApplicationException("Unsupported Charset Definition ["+charset+"]",e.getMessage());
        }
        catch (IOException e) {
            
            throw new ApplicationException("can't write file "+file.getAbsolutePath(),e.getMessage());
        }
        
        setMode(file);
        setAttributes(file);
    }
    
    /**
     * write to the source file
     * @throws PageException
     */
    private void actionTouch() throws PageException {
        checkFile(true,true,true);
        setACL(file);

        try {
            ResourceUtil.touch(file);
        } 
        catch (IOException e) {
            
            throw new ApplicationException("can't touch file "+file.getAbsolutePath(),e.getMessage());
        }
        
        setMode(file);
        setAttributes(file);
    }
    
    

	/**
	 * append data to source file
	 * @throws PageException
	 */
	private void actionAppend() throws PageException {
		if(output==null)
			throw new ApplicationException("attribute output is not defined for tag file");
		checkFile(true,false,true);
		setACL(file);
		
        try {

            if(!file.exists()) file.createNewFile();
            String content=Caster.toString(output);
            if(fixnewline)content=doFixNewLine(content);
    		if(addnewline) content+="\n";
            IOUtil.write(file,content,charset,true);
        	
        } 
		catch (UnsupportedEncodingException e) {
            throw new ApplicationException("Unsupported Charset Definition ["+charset+"]",e.getMessage());
        }
        catch (IOException e) {
            throw new ApplicationException("can't write file",e.getMessage());
        }
        setMode(file);
        setAttributes(file);
	}

    private String doFixNewLine(String content) {
		// TODO replace new line with system new line
		return content;
	}

	/**
	 * list all files and directories inside a directory
	 * @throws PageException
	 */
	private void actionInfo() throws PageException {
		
		if(variable==null)
			throw new ApplicationException("attribute variable is not defined for tag file");
		checkFile(false,false,false);
		
		Struct sct =new StructImpl();
		pageContext.setVariable(variable,sct);
		
		// fill data to query
		sct.setEL("name",file.getName());
		sct.setEL("size",Long.valueOf(file.length()));
		sct.setEL("type",file.isDirectory()?"Dir":"File");
		sct.setEL("dateLastModified",new DateTimeImpl(pageContext,file.lastModified(),false));
		sct.setEL("attributes",getFileAttribute(file));
		if(SystemUtil.isUnix())sct.setEL("mode",new ModeObjectWrap(file));
        
		//InputStream is=null;
		try { 		
			//is=file.getInputStream();
            BufferedImage bi = ImageUtil.toBufferedImage(file, null);
            if(bi!=null) {
	            Struct img =new StructImpl();
	            img.setEL("width",new Double(bi.getWidth()));
	            img.setEL("height",new Double(bi.getHeight()));
	            sct.setEL("img",img);
            }
        } 
		catch (IOException e) {
            //throw Caster.toPageException(e);
        }
		//finally {
			//IOUtil.closeEL(is);
		//}
	}

	private static String getFileAttribute(Resource file){
		return  file.exists() && !file.canWrite() ? "R".concat(file.isHidden() ? "H" : "") : file.isHidden() ? "H" : "";
	}
	
	/**
	 * read source file
	 * @throws PageException
	 */
	private synchronized void actionUpload() throws PageException {
		if(nameconflict==NAMECONFLICT_UNDEFINED) nameconflict=NAMECONFLICT_ERROR;

		boolean fileWasRenamed=false;
		boolean fileWasAppended=false;
		boolean fileExisted=false;
		boolean fileWasOverwritten=false;
		
		PageException pe = pageContext.formScope().getInitException();
		if(pe!=null) throw pe;

		//DiskFileItem fileItem=getFileItem();
		FormImpl.Item item=getFormItem();
		String contentType=item.getContentType();
		
		// check file type
		checkContentType(contentType);
		
		// set cffile struct
		Struct cffile=new StructImpl();
        if(StringUtil.isEmpty(result)) {
            pageContext.undefinedScope().set("file",cffile);
		    pageContext.undefinedScope().set("cffile",cffile);
        }
        else {
            pageContext.setVariable(result,cffile);
        }
        	long length = item.getResource().length();
			cffile.set("timecreated",new DateTimeImpl(pageContext.getConfig()));
			cffile.set("timelastmodified",new DateTimeImpl(pageContext.getConfig()));
			cffile.set("datelastaccessed",new DateImpl(pageContext));
			cffile.set("oldfilesize",Long.valueOf(length));
			cffile.set("filesize",Long.valueOf(length));
			cffile.set("contenttype",ListFirst.call(pageContext,contentType,"/"));
			cffile.set("contentsubtype",ListLast.call(pageContext,contentType,"/"));
		
		// client file
		String strClientFile=item.getName();
		while(strClientFile.indexOf('\\')!=-1)
			strClientFile=strClientFile.replace('\\','/');
		Resource clientFile=pageContext.getConfig().getResource(strClientFile);
		String clientFileName=clientFile.getName();
			
			//String dir=clientFile.getParent();
			//dir=correctDirectory(dir);
		
			cffile.set("clientdirectory",getParent(clientFile));
			cffile.set("clientfile",clientFile.getName());
			cffile.set("clientfileext",getFileExtension(clientFile));
			cffile.set("clientfilename",getFileName(clientFile));
		
	    // check desination
	    if(StringUtil.isEmpty(strDestination))
	    	throw new ApplicationException("attribute destination is not defined in tag file");

	    
	    Resource destination=toDestination(strDestination,null);
	    setACL(destination);
		
	    
		securityManager.checkFileLocation(pageContext.getConfig(),destination,serverPassword);
		
	   // destination.getCanonicalPath()
	    if(destination.isDirectory()) 
	    	destination=destination.getRealResource(clientFileName);
	    else if(!clientFileName.equalsIgnoreCase(destination.getName()))
	    	fileWasRenamed=true;
	    
	    // check parent desination -> directory of the desinatrion
	    Resource parentDestination=destination.getParentResource();
	    
	    if(!parentDestination.exists())
	    	throw new ApplicationException("attribute destination has a invalid value ["+destination+"], directory ["+parentDestination+"] doesn't exist");
	    else if(!parentDestination.canWrite())
	    	throw new ApplicationException("can't write to desination directory ["+parentDestination+"], no access to write");
	    
	    // set server variables
		cffile.set("serverdirectory",getParent(destination));
		cffile.set("serverfile",destination.getName());
		cffile.set("serverfileext",getFileExtension(destination));
		cffile.set("serverfilename",getFileName(destination));
		cffile.set("attemptedserverfile",destination.getName());
	    
		
	    // check nameconflict
	    if(destination.exists()) {
	    	fileExisted=true;
	    	if(nameconflict==NAMECONFLICT_ERROR) {
	    		throw new ApplicationException("desination file ["+destination+"] already exist");
	    	}
	    	else if(nameconflict==NAMECONFLICT_SKIP) {
				cffile.set("fileexisted",Caster.toBoolean(fileExisted));
				cffile.set("filewasappended",Boolean.FALSE);
				cffile.set("filewasoverwritten",Boolean.FALSE);
				cffile.set("filewasrenamed",Boolean.FALSE);
				cffile.set("filewassaved",Boolean.FALSE);
	    		return ;
	    	}
	    	else if(nameconflict==NAMECONFLICT_MAKEUNIQUE) {
	    		destination=makeUnique(destination);
	    		fileWasRenamed=true;
	    		
				//if(fileWasRenamed) {
				cffile.set("serverdirectory",getParent(destination));
				cffile.set("serverfile",destination.getName());
				cffile.set("serverfileext",getFileExtension(destination));
				cffile.set("serverfilename",getFileName(destination));
				cffile.set("attemptedserverfile",destination.getName());	
				//}
	    	}
	    	else if(nameconflict==NAMECONFLICT_OVERWRITE) {
	    		//fileWasAppended=true;	
	    		fileWasOverwritten=true;
	    		if(!destination.delete())
	    			if(destination.exists()) // hier hatte ich concurrent problem das damit ausgeraeumt ist
	    				throw new ApplicationException("can't delete desination file ["+destination+"]");
	    	}
	    	// for "overwrite" no action is neded
	    	
	    }
	    
			try {
				destination.createNewFile();
				IOUtil.copy(item.getResource(),destination);
			}
			catch(Throwable t) {
				throw new ApplicationException(t.getMessage());
			}
			
			// Set cffile/file struct
			
			cffile.set("fileexisted",Caster.toBoolean(fileExisted));
			cffile.set("filewasappended",Caster.toBoolean(fileWasAppended));
			cffile.set("filewasoverwritten",Caster.toBoolean(fileWasOverwritten));
			cffile.set("filewasrenamed",Caster.toBoolean(fileWasRenamed));
			cffile.set("filewassaved",Boolean.TRUE);
			

	        setMode(destination);
	        setAttributes(destination);
			
	}

	/**
	 * check if the content ii ok
	 * @param contentType 
	 * @throws PageException
	 */
	private void checkContentType(String contentType) throws PageException {
		String type=ListFirst.call(pageContext,contentType,"/").trim().toLowerCase();
		String subType=ListLast.call(pageContext,contentType,"/").trim().toLowerCase();
		
		if(accept==null || accept.trim().length()==0) return;
		
		Array whishedTypes=List.listToArrayRemoveEmpty(accept,',');
		int len=whishedTypes.size();
		for(int i=1;i<=len;i++) {
			String whishedType=Caster.toString(whishedTypes.getE(i)).trim();
			String wType=ListFirst.call(pageContext,whishedType,"/").trim().toLowerCase();
			String wSubType=ListLast.call(pageContext,whishedType,"/").trim().toLowerCase();
			if((wType.equals("*") || wType.equals(type)) && (wSubType.equals("*") || wSubType.equals(subType)))return;
			
		}
		throw new ApplicationException("The MIME type of the uploaded file ["+contentType+"] was not accepted by the server.","only this ["+accept+"] mime type are accepted");
	}

	/**
	 * rreturn fileItem matching to filefiled definition or throw a exception
	 * @return FileItem
	 * @throws ApplicationException
	 */
	private FormImpl.Item getFormItem() throws ApplicationException {
		// check filefield
		if(filefield==null)
			throw new ApplicationException("attribute fileField is not defined in tag file, but is required when action upload");
	    
		FormImpl.Item fileItem = ((FormImpl)pageContext.formScope()).getUploadResource(filefield);
		if(fileItem==null) {
			if(pageContext.formScope().get(filefield,null)==null)
				throw new ApplicationException("form field ["+filefield+"] is not a file field");
			throw new ApplicationException("form field ["+filefield+"] doesn't exist or has no content");
		}
		
		return fileItem;
	}
	
	/**
	 * get file extension of a file object
	 * @param file file object
	 * @return extnesion
	 */
	private static String getFileExtension(Resource file) {
		String name=file.getName();
		String[] arr;
		try {
			arr = List.toStringArray(List.listToArrayRemoveEmpty(name, '.'));
		} catch (PageException e) {
			arr=null;
		}
		if(arr.length<2) return null;
		
		return arr[arr.length-1];
	}
	
	/**
	 * get file name of a file object without extension
	 * @param file file object
	 * @return name of the file 
	 */
	private static String getFileName(Resource file) {
		String name=file.getName();
		int pos=name.lastIndexOf(".");
		
		if(pos==-1)return name;
		return name.substring(0,pos);
	}
	
	/*private String correctDirectory(Resource resource) {
		if(StringUtil.isEmpty(resource,true)) return "";
		resource=resource.trim();
		if((StringUtil.endsWith(resource, '/') || StringUtil.endsWith(resource, '\\')) && resource.length()>1) {
			return resource.substring(0,resource.length()-1);
		}
		return resource;
	}*/
	
	private String getParent(Resource res) {
		Resource parent = res.getParentResource();
		//print.out("res:"+res);
		//print.out("parent:"+parent);
		if(parent==null) return "";
		return ResourceUtil.getCanonicalPathEL(parent);
	}
	

	private void checkFile(boolean create, boolean canRead, boolean canWrite) throws PageException {
		if(file==null)
			throw new ApplicationException("attribute file is not defined for tag file");

		securityManager.checkFileLocation(pageContext.getConfig(),file,serverPassword);
		if(!file.exists()) {
			if(create) {
				Resource parent=file.getParentResource();
				if(parent!=null && !parent.exists())
					throw new ApplicationException("parent directory for ["+file+"] doesn't exists");
				try {
					file.createFile(false);
				} catch (IOException e) {
					throw new ApplicationException("invalid file ["+file+"]",e.getMessage());
				}
			}
			else if(!file.isFile()) 
				throw new ApplicationException("source file ["+file.toString()+"] is not a file");
			else 
				throw new ApplicationException("source file ["+file.toString()+"] doesn't exist");
		}
		else if(!file.isFile())
			throw new ApplicationException("source file ["+file.toString()+"] is not a file");
        else if(canRead &&!file.canRead())
            throw new ApplicationException("no read access to source file ["+file.toString()+"]");
        else if(canWrite && !file.canWrite())
            throw new ApplicationException("no write access to source file ["+file.toString()+"]");
	
	}

	/**
	 * set attributes on file
     * @param file
	 * @throws PageException
     */
    private void setAttributes(Resource file) throws PageException {
        if(attributes==null) return;
        try {
        	ResourceUtil.setAttribute(file, attributes);
            //file.setAttribute(attributes);
        	//FileUtil.setAttribute(file,attributes);
        } 
        catch (IOException e) {
            throw new ApplicationException("can't change attributes of file "+file,e.getMessage());
        }
    }

    /**
	 * change mode of given file
     * @param file
	 * @throws ApplicationException
     */
    private void setMode(Resource file) throws ApplicationException {
        if(mode==-1) return;
        try {
        	file.setMode(mode);
            //FileUtil.setMode(file,mode);
        } catch (IOException e) {
            throw new ApplicationException("can't change mode of file "+file,e.getMessage());
        }
    }

	/**
	 * @param fixnewline the fixnewline to set
	 */
	public void setFixnewline(boolean fixnewline) {
		this.fixnewline = fixnewline;
	}
}