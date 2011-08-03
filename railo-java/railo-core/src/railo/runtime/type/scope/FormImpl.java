package railo.runtime.type.scope;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletInputStream;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import railo.commons.collections.HashTable;
import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.lang.ByteNameValuePair;
import railo.commons.lang.StringUtil;
import railo.commons.net.URLItem;
import railo.runtime.PageContext;
import railo.runtime.config.ConfigImpl;
import railo.runtime.exp.PageException;
import railo.runtime.net.http.ServletInputStreamDummy;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.List;
import railo.runtime.util.ApplicationContext;


/**
 * Form Scope
 */
public final class FormImpl extends ScopeSupport implements Form,ScriptProtected {
	

	private byte EQL=61;
	private byte NL=10;
	private byte AMP=38;
	
	
	private HashTable fileItems=new HashTable();
	private Exception initException=null;

    private String encoding=null;
    private int scriptProtected=ScriptProtected.UNDEFINED;
    private static final URLItem[] empty=new URLItem[0];
	//private static final ResourceFilter FILTER = new ExtensionResourceFilter(".upload",false);
    private URLItem[] raw=empty;
    private static int count=1;

    private static final int HEADER_TEXT_PLAIN=0;
    private static final int HEADER_MULTIPART_FORM_DATA=1;
    private static final int HEADER_APP_URL_ENC=2;
	private int headerType=-1;
    
	/**
	 * standart class Constructor
	 */
	public FormImpl() {
		super(true,"form",SCOPE_FORM);
	}

    /**
     * @see railo.runtime.type.scope.Form#getEncoding()
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * @see railo.runtime.type.scope.Form#setEncoding(java.lang.String)
     */
    public void setEncoding(String encoding) throws UnsupportedEncodingException {
        encoding=encoding.trim().toUpperCase();
        if(encoding.equals(this.encoding)) return;
        this.encoding = encoding;
        if(!isInitalized()) return;
        fillDecoded(raw,encoding,isScriptProtected());
        setFieldNames();
    }

	/**
	 * @see railo.runtime.type.scope.ScopeSupport#initialize(railo.runtime.PageContext)
	 */
	public void initialize(PageContext pc) {
		if(encoding==null)encoding=pc.getConfig().getWebCharset();
		
		if(scriptProtected==ScriptProtected.UNDEFINED) {
			scriptProtected=((pc.getApplicationContext().getScriptProtect()&ApplicationContext.SCRIPT_PROTECT_FORM)>0)?
					ScriptProtected.YES:ScriptProtected.NO;
		}
		
        super.initialize(pc);
		
        String contentType=pc. getHttpServletRequest().getContentType();
        
		if(contentType==null) return;
		contentType=StringUtil.toLowerCase(contentType);
		if(contentType.startsWith("multipart/form-data")) {
			headerType=HEADER_MULTIPART_FORM_DATA;
			initializeMultiPart(pc,isScriptProtected());
		}
		else if(contentType.startsWith("text/plain")) {
			headerType=HEADER_TEXT_PLAIN;
			initializeUrlEncodedOrTextPlain(pc,'\n',isScriptProtected());
		}
		else {
			headerType=HEADER_APP_URL_ENC;
			initializeUrlEncodedOrTextPlain(pc,'&',isScriptProtected());
		}
		setFieldNames();
	}

    void setFieldNames() {
    	if(size()>0) {
    		setEL(KeyImpl.FIELD_NAMES,List.arrayToList(keysAsString(), ","));
        }
    }


    private void initializeMultiPart(PageContext pc, boolean scriptProteced) {
    	// get temp directory
    	Resource tempDir = ((ConfigImpl)pc.getConfig()).getTempDirectory();
    	Resource tempFile;
    	
    	// Create a new file upload handler
    	String encoding=getEncoding();
    	ServletFileUpload upload = new ServletFileUpload();
    	upload.setHeaderEncoding(encoding);
    	
    	
    	// Parse the request
    	try {
    		FileItemIterator iter = upload.getItemIterator(pc.getHttpServletRequest());
        	//byte[] value;
        	InputStream is;
        	ArrayList list=new ArrayList();
			while (iter.hasNext()) {
			    FileItemStream item = iter.next();
			    
			    
			    is=IOUtil.toBufferedInputStream(item.openStream());
			    if (item.getContentType()==null || StringUtil.isEmpty(item.getName())) {
			    	list.add(new URLItem(item.getFieldName(),new String(IOUtil.toBytes(is),encoding),false));	     
			    } 
			    else {
			    	tempFile=tempDir.getRealResource(getFileName());
			    	fileItems.put(item.getFieldName().toLowerCase(),
			    			new Item(tempFile,item.getContentType(),item.getName(),item.getFieldName()));
					String value=tempFile.toString();
			    	IOUtil.copy(is, tempFile,true);
				    list.add(new URLItem(item.getFieldName(),value,false));	     
			    }       
			}
			
			raw=(URLItem[]) list.toArray(new URLItem[list.size()]);
			fillDecoded(raw,encoding,scriptProteced);
		} 
    	catch (Exception e) {
			
        	//throw new PageRuntimeException(Caster.toPageException(e));
        	fillDecodedEL(new URLItem[0],encoding,scriptProteced);
			initException=e;
		}
	}
    
    private static String getFileName() { 
		return "tmp-"+(count++)+".upload";
	}

	/*private void initializeMultiPart(PageContext pc, boolean scriptProteced) {

    	File tempDir=FileWrapper.toFile(pc.getConfig().getTempDirectory());
    	
    	// Create a factory for disk-based file items
    	DiskFileItemFactory factory = new DiskFileItemFactory(-1,tempDir);

    	// Create a new file upload handler
    	ServletFileUpload upload = new ServletFileUpload(factory);
    	
    	upload.setHeaderEncoding(getEncoding());
    	
		//FileUpload fileUpload=new FileUpload(new DiskFileItemFactory(0,tempDir));
		java.util.List list;
		try {
			list = upload.parseRequest(pc.getHttpServletRequest());
            raw=new ByteNameValuePair[list.size()];
                        
			for(int i=0;i<raw.length;i++) {
				DiskFileItem val=(DiskFileItem) list.get(i);
				if(val.isFormField()) {
                    raw[i]=new ByteNameValuePair(getBytes(val.getFieldName()),val.get(),false);
                }
				else {
					print.out("-------------------------------");
					print.out("fieldname:"+val.getFieldName());
					print.out("name:"+val.getName());
					print.out("formfield:"+val.isFormField());
					print.out("memory:"+val.isInMemory());
					print.out("exist:"+val.getStoreLocation().getCanonicalFile().exists());
					
					fileItems.put(val.getFieldName().toLowerCase(),val);
					
                    raw[i]=new ByteNameValuePair(getBytes(val.getFieldName()),val.getStoreLocation().getCanonicalFile().toString().getBytes(),false);
                    //raw.put(val.getFieldName(),val.getStoreLocation().getCanonicalFile().toString());
				}
			}
            fillDecoded(raw,encoding,scriptProteced);
		} 
        catch (Exception e) {
        	
        	//throw new PageRuntimeException(Caster.toPageException(e));
        	fillDecodedEL(new ByteNameValuePair[0],encoding,scriptProteced);
			initException=e;
		}
	}*/
    
	private void initializeUrlEncodedOrTextPlain(PageContext pc, char delimeter, boolean scriptProteced) {
		BufferedReader reader=null;
		try {
			reader = pc.getHttpServletRequest().getReader();
			raw=setFrom___(IOUtil.toString(reader,false),delimeter);
			fillDecoded(raw,encoding,scriptProteced);
		} 
        catch (Exception e) {
        	
        	fillDecodedEL(new URLItem[0],encoding,scriptProteced);
			initException=e;
        }
        finally {
        	IOUtil.closeEL(reader);
        }
	}

	/**
	 * @see railo.runtime.type.Scope#release()
	 */
	public void release() {
		super.release();
        encoding=null;
        scriptProtected=ScriptProtected.UNDEFINED;
        raw=empty;
		
		if(!fileItems.isEmpty()) {
			Iterator it = fileItems.entrySet().iterator();
			Item item;
			while(it.hasNext()) {
				item=(Item) ((Map.Entry) it.next()).getValue();
				item.getResource().delete();
			}
			fileItems.clear();
		}
		initException=null;
		
	}

	public FormImpl.Item[] getFileItems() {
		if(fileItems==null || fileItems.isEmpty()) return new FormImpl.Item[0];
		
		Iterator it = fileItems.entrySet().iterator();
		Map.Entry entry;
		FormImpl.Item[] rtn=new FormImpl.Item[fileItems.size()];
		int index=0;
		while(it.hasNext()){
			entry=(Entry) it.next();
			rtn[index++]=(Item) entry.getValue();
		}
		return rtn;
	}
	
	
	/**
     * @see railo.runtime.type.scope.Form#getFileUpload(java.lang.String)
     */
	public DiskFileItem getFileUpload(String key) {
		return null;
	}
	public Item getUploadResource(String key) {
		key=key.trim();
		String lcKey = StringUtil.toLowerCase(key);
		
		// x
		Item item = (Item) fileItems.get(lcKey);
		if(item!=null)return item;
		
		// form.x
		if(lcKey.startsWith("form.")) {
			lcKey=lcKey.substring(5).trim();
			item = (Item) fileItems.get(lcKey);
			if(item!=null)return item;
		}
		
		// form . x
		try {
			Array array = List.listToArray(lcKey, '.');
			if(array.size()>1 && array.getE(1).toString().trim().equals("form")) {
				array.removeE(1);
				lcKey=List.arrayToList(array, ".").trim();
				item = (Item) fileItems.get(lcKey);
				if(item!=null)return item;
			}
		} 
		catch (PageException e) {}
		
		// /file.tmp
		Iterator it = fileItems.entrySet().iterator();
		//print.out("------------------");
		while(it.hasNext()) {
			item=(Item) ((Map.Entry)it.next()).getValue();
			//print.out(item.getResource().getAbsolutePath()+" - "+key);
			//try {
				//if(item.getStoreLocation().getCanonicalFile().toString().equalsIgnoreCase(key))return item;
				if(item.getResource().getAbsolutePath().equalsIgnoreCase(key))return item;
			//} 
			//catch (IOException e) {}
		}
		
		return null;
	}

	/**
     * @see railo.runtime.type.scope.Form#getInitException()
     */
	public PageException getInitException() {
		if(initException!=null)
			return Caster.toPageException(initException);
		return null;
	}

	/**
	 *
	 * @see railo.runtime.type.scope.URL#setScriptProtecting(boolean)
	 */
	public void setScriptProtecting(boolean scriptProtected) {
		int _scriptProtected = scriptProtected?ScriptProtected.YES:ScriptProtected.NO;
		if(isInitalized() && _scriptProtected!=this.scriptProtected) {
			fillDecodedEL(raw,encoding,scriptProtected);
			setFieldNames();
		}
		this.scriptProtected=_scriptProtected;
		/*if(isScriptProtected()) return;
		if(scriptProtected) {
			if(isInitalized()) {
				fillDecodedEL(raw,encoding,scriptProtected);
			}
			this.scriptProtected=ScriptProtected.YES;
		}
		else this.scriptProtected=ScriptProtected.NO;*/
	}

	/**
	 *
	 * @see railo.runtime.type.scope.URL#isScriptProtected()
	 */
	public boolean isScriptProtected() {
		return scriptProtected==ScriptProtected.YES ;
	}

	/**
	 * @return the raw
	 */
	public URLItem[] getRaw() {
		return raw;
	}

	public void addRaw(URLItem[] raw) {
		URLItem[] nr=new URLItem[this.raw.length+raw.length];
		for(int i=0;i<this.raw.length;i++) {
			nr[i]=this.raw[i];
		}
		for(int i=0;i<raw.length;i++) {
			nr[this.raw.length+i]=raw[i];
		}
		this.raw=nr;
		
		if(!isInitalized()) return;
        fillDecodedEL(this.raw,encoding,isScriptProtected());
        setFieldNames();
	}

	public class Item {
		Resource resource;
		String contentType;
		String name;
		private String fieldName;
		
		public Item(Resource resource, String contentType,String name, String fieldName) {
			this.fieldName = fieldName;
			this.name = name;
			this.resource = resource;
			this.contentType = contentType;
		}
		/**
		 * @return the resource
		 */
		public Resource getResource() {
			return resource;
		}
		/**
		 * @return the contentType
		 */
		public String getContentType() {
			return contentType;
		}
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		/**
		 * @return the fieldName
		 */
		public String getFieldName() {
			return fieldName;
		}
	}

	/**
	 * @return return content as a http header input stream
	 */
	public ServletInputStream getInputStream() {
		if(headerType==HEADER_APP_URL_ENC) {
			return new ServletInputStreamDummy(toBarr(raw,AMP));
		}
		else if(headerType==HEADER_TEXT_PLAIN) {
			return new ServletInputStreamDummy(toBarr(raw,NL));
		}
		/*else if(headerType==HEADER_MULTIPART_FORM_DATA) {
			return new FormImplInputStream(this);
			// TODO
		}*/
		return new ServletInputStreamDummy(new byte[]{});
	}

	private byte[] toBarr(URLItem[] items, byte del) {
		
		ByteNameValuePair[] raw=new ByteNameValuePair[items.length];
		for(int i=0;i<raw.length;i++) {
			try {
				raw[i]=new ByteNameValuePair(items[i].getName().getBytes("iso-8859-1"),items[i].getValue().getBytes("iso-8859-1"),items[i].isUrlEncoded());
			} catch (UnsupportedEncodingException e) {}
		}
		
		int size=0;
		for(int i=0;i<raw.length;i++) {
			size+=raw[i].getName().length;
			size+=raw[i].getValue().length;
			size+=2;
		}
		size--;
		byte[] barr = new byte[size],bname,bvalue;
		int count=0;
		
		for(int i=0;i<raw.length;i++) {
			bname=raw[i].getName();
			bvalue=raw[i].getValue();
			// name
			for(int y=0;y<bname.length;y++) {
				barr[count++]=bname[y];
			}
			barr[count++]=EQL;
			// value
			for(int y=0;y<bvalue.length;y++) {
				barr[count++]=bvalue[y];
			}
			if(i+1<raw.length)barr[count++]=del;
		}
		return barr;
	}

}