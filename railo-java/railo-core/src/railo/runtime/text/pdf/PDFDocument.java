package railo.runtime.text.pdf;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.pdfbox.exceptions.CryptographyException;
import org.pdfbox.exceptions.InvalidPasswordException;
import org.pdfbox.pdmodel.PDDocument;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.type.file.FileResource;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.util.StructSupport;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;

public class PDFDocument extends StructSupport implements Struct {

	private byte[] barr;
	private String password;
	private Resource resource;
	private Set<Integer> pages;

	public PDFDocument(byte[] barr, String password) {
		this.barr=barr;
		this.password=password;
	}

	public PDFDocument(Resource resource, String password) {
		this.resource=resource;
		this.password=password;
	}

	public PDFDocument(byte[] barr, Resource resource, String password) {
		this.resource=resource;
		this.barr=barr;
		this.password=password;
	}
	

	/**
	 * @see railo.runtime.type.Collection#clear()
	 */
	public void clear() {
		getInfo().clear();
	}


	/**
	 * @see railo.runtime.type.Collection#containsKey(railo.runtime.type.Collection.Key)
	 */
	public boolean containsKey(Key key) {
		return getInfo().containsKey(key);
	}

	/**
	 * @see railo.runtime.type.Collection#duplicate(boolean)
	 */
	public Collection duplicate(boolean deepCopy) {
		PDFDocument duplicate=new PDFDocument(barr,resource,password);
		return duplicate;
	}
	

	/**
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key)
	 */
	public Object get(Key key) throws PageException {
		return getInfo().get(key);
	}

	/**
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(Key key, Object defaultValue) {
		return getInfo().get(key, defaultValue);
	}

	/**
	 * @see railo.runtime.type.Collection#keys()
	 */
	public Key[] keys() {
		return getInfo().keys();
	}

	/**
	 * @see railo.runtime.type.Collection#remove(railo.runtime.type.Collection.Key)
	 */
	public Object remove(Key key) throws PageException {
		return getInfo().remove(key);
	}

	/**
	 * @see railo.runtime.type.Collection#removeEL(railo.runtime.type.Collection.Key)
	 */
	public Object removeEL(Key key) {
		return getInfo().removeEL(key);
	}

	/**
	 * @see railo.runtime.type.Collection#set(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(Key key, Object value) throws PageException {
		return getInfo().set(key, value);
	}

	/**
	 * @see railo.runtime.type.Collection#setEL(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(Key key, Object value) {
		return getInfo().setEL(key, value);
	}

	/**
	 * @see railo.runtime.type.Collection#size()
	 */
	public int size() {
		return getInfo().size();
	}

	/**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int, railo.runtime.dump.DumpProperties)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel,DumpProperties properties) {
		
		DumpData dd = getInfo().toDumpData(pageContext, maxlevel,properties);
		if(dd instanceof DumpTable)((DumpTable)dd).setTitle("Struct (PDFDocument)");
		return dd;
	}

	/**
	 * @see railo.runtime.type.Iteratorable#keyIterator()
	 */
	public Iterator<Collection.Key> keyIterator() {
		return getInfo().keyIterator();
	}
    
    @Override
	public Iterator<String> keysAsStringIterator() {
    	return getInfo().keysAsStringIterator();
    }
	
	@Override
	public Iterator<Entry<Key, Object>> entryIterator() {
		return getInfo().entryIterator();
	}
	
	@Override
	public Iterator<Object> valueIterator() {
		return getInfo().valueIterator();
	}

	/**
	 * @see railo.runtime.op.Castable#castToBooleanValue()
	 */
	public boolean castToBooleanValue() throws PageException {
		return getInfo().castToBooleanValue();
	}
    
    /**
     * @see railo.runtime.op.Castable#castToBoolean(java.lang.Boolean)
     */
    public Boolean castToBoolean(Boolean defaultValue) {
        return getInfo().castToBoolean(defaultValue);
    }

	/**
	 * @see railo.runtime.op.Castable#castToDateTime()
	 */
	public DateTime castToDateTime() throws PageException {
		return getInfo().castToDateTime();
	}
    
    /**
     * @see railo.runtime.op.Castable#castToDateTime(railo.runtime.type.dt.DateTime)
     */
    public DateTime castToDateTime(DateTime defaultValue) {
        return getInfo().castToDateTime(defaultValue);
    }

	/**
	 * @see railo.runtime.op.Castable#castToDoubleValue()
	 */
	public double castToDoubleValue() throws PageException {
		return getInfo().castToDoubleValue();
	}
    
    /**
     * @see railo.runtime.op.Castable#castToDoubleValue(double)
     */
    public double castToDoubleValue(double defaultValue) {
        return getInfo().castToDoubleValue(defaultValue);
    }

	/**
	 * @see railo.runtime.op.Castable#castToString()
	 */
	public String castToString() throws PageException {
		return getInfo().castToString();
	}
	/**
	 * @see railo.runtime.type.util.StructSupport#castToString(java.lang.String)
	 */
	public String castToString(String defaultValue) {
		return getInfo().castToString(defaultValue);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(java.lang.String)
	 */
	public int compareTo(String str) throws PageException {
		return getInfo().compareTo(str);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(boolean)
	 */
	public int compareTo(boolean b) throws PageException {
		return getInfo().compareTo(b);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(double)
	 */
	public int compareTo(double d) throws PageException {
		return getInfo().compareTo(d);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(railo.runtime.type.dt.DateTime)
	 */
	public int compareTo(DateTime dt) throws PageException {
		return getInfo().compareTo(dt);
	}
///////////////////////////////////////////////
	
	public PdfReader getPdfReader() throws ApplicationException {
		try {
			if(barr!=null) {
				if(password!=null)return new PdfReader(barr,password.getBytes());
				return new PdfReader(barr);
			}
			if(password!=null)return new PdfReader(IOUtil.toBytes(resource),password.getBytes());
			return new PdfReader(IOUtil.toBytes(resource));
		}
		catch(IOException ioe) {
			throw new ApplicationException("can not load file ["+resource+"]",ioe.getMessage());
		}
	}
	
	private String getFilePath() {
		if(resource==null) return "";
		return resource.getAbsolutePath();
	}

	public Struct getInfo()  {

		PdfReader pr=null;
		try {
			pr=getPdfReader();
			//PdfDictionary catalog = pr.getCatalog();
			int permissions = pr.getPermissions();
			boolean encrypted=pr.isEncrypted();
			
			Struct info=new StructImpl();
			info.setEL("FilePath", getFilePath());
			
			// access
			info.setEL("ChangingDocument", allowed(encrypted,permissions,PdfWriter.ALLOW_MODIFY_CONTENTS));
			info.setEL("Commenting", allowed(encrypted,permissions,PdfWriter.ALLOW_MODIFY_ANNOTATIONS));
			info.setEL("ContentExtraction", allowed(encrypted,permissions,PdfWriter.ALLOW_SCREENREADERS));
			info.setEL("CopyContent", allowed(encrypted,permissions,PdfWriter.ALLOW_COPY));
			info.setEL("DocumentAssembly", allowed(encrypted,permissions,PdfWriter.ALLOW_ASSEMBLY+PdfWriter.ALLOW_MODIFY_CONTENTS));
			info.setEL("FillingForm", allowed(encrypted,permissions,PdfWriter.ALLOW_FILL_IN+PdfWriter.ALLOW_MODIFY_ANNOTATIONS));
			info.setEL("Printing", allowed(encrypted,permissions,PdfWriter.ALLOW_PRINTING));
			info.setEL("Secure", "");
			info.setEL("Signing", allowed(encrypted,permissions,PdfWriter.ALLOW_MODIFY_ANNOTATIONS+PdfWriter.ALLOW_MODIFY_CONTENTS+PdfWriter.ALLOW_FILL_IN));
			
			info.setEL("Encryption", encrypted?"Password Security":"No Security");// MUST
			info.setEL("TotalPages", Caster.toDouble(pr.getNumberOfPages()));
			info.setEL("Version", "1."+pr.getPdfVersion());
			info.setEL("permissions", ""+permissions);
			info.setEL("permiss", ""+PdfWriter.ALLOW_FILL_IN);
			
			info.setEL("Application", "");
			info.setEL("Author", "");
			info.setEL("CenterWindowOnScreen", "");
			info.setEL("Created", "");
			info.setEL("FitToWindow", "");
			info.setEL("HideMenubar", "");
			info.setEL("HideToolbar", "");
			info.setEL("HideWindowUI", "");
			info.setEL("Keywords", "");
			info.setEL("Language", "");
			info.setEL("Modified", "");
			info.setEL("PageLayout", "");
			info.setEL("Producer", "");
			info.setEL("Properties", "");
			info.setEL("ShowDocumentsOption", "");
			info.setEL("ShowWindowsOption", "");
			info.setEL("Subject", "");
			info.setEL("Title", "");
			info.setEL("Trapped", "");
	
			// info
			HashMap imap = pr.getInfo();
			Iterator it = imap.entrySet().iterator();
			Map.Entry entry;
			while(it.hasNext()) {
				entry=(Entry) it.next();
				info.setEL(Caster.toString(entry.getKey(),null), entry.getValue());
			}
			return info;
		}
		catch(PageException pe) {
			throw new PageRuntimeException(pe);
		}
		finally {
			if(pr!=null)pr.close();
		}
	}
	

	

	private static Object allowed(boolean encrypted, int permissions, int permission) {
		return (!encrypted || (permissions&permission)>0)?"Allowed":"Not Allowed";
	}



	public void setPages(String strPages) throws PageException {
		if(StringUtil.isEmpty(strPages))return;
		if(pages==null)
			pages=new HashSet<Integer>();
		PDFUtil.parsePageDefinition(pages,strPages);
	}

	public Set<Integer> getPages() {
		//if(pages==null)pages=new HashSet();
		return pages;
	}

	public Resource getResource() {
		return resource;
	}
	public byte[] getRaw() throws IOException {
		if(barr!=null)return barr;
		return IOUtil.toBytes(resource);
	}

	/**
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value) {
		return getInfo().containsValue(value);
	}

	/**
	 * @see java.util.Map#values()
	 */
	public java.util.Collection values() {
		return getInfo().values();
	}
	
	public PDDocument toPDDocument() throws CryptographyException, InvalidPasswordException, IOException {
		PDDocument doc;
		if(barr!=null) 
			doc= PDDocument.load(new ByteArrayInputStream(barr,0,barr.length));
		else if(resource instanceof FileResource)
			doc= PDDocument.load((File)resource);
		else 
			doc= PDDocument.load(new ByteArrayInputStream(IOUtil.toBytes(resource),0,barr.length));
		
		if(password!=null)doc.decrypt(password);
		
		
		return doc;
		
	}

}
