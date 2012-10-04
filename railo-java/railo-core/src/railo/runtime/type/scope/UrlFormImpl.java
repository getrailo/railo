/**
 * 
 */
package railo.runtime.type.scope;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import javax.servlet.ServletInputStream;

import org.apache.commons.fileupload.disk.DiskFileItem;

import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.exp.PageException;
import railo.runtime.listener.ApplicationContext;
import railo.runtime.op.Duplicator;
import railo.runtime.type.Collection;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.util.StructSupport;

/**
 * 
 */
public final class UrlFormImpl extends StructSupport implements URLForm {

	private static final long serialVersionUID = -5709431392572723178L;

	private FormImpl form;
	private URLImpl url;
	private boolean isInit;

	public UrlFormImpl(FormImpl form, URLImpl url) {
		this.form=form;
		this.url=url;
	}
	
	/**
	 * @see railo.runtime.type.scope.Scope#initialize(railo.runtime.PageContext)
	 */
	public void initialize(PageContext pc) {
		if(isInit) return;
		isInit=true;
		form.initialize(pc);
		url.initialize(pc);
		//print.ln(">>>"+List.arrayToList(url.keys(),","));
		form.addRaw(pc.getApplicationContext(),url.getRaw());
		
		/*String[] keys = url.keys();
		for(int i=0;i<keys.length;i++) {
			form.setEL(keys[i], url.get(keys[i],null));
		}*/
	}
	
	@Override
	public void release() {
		isInit=false;
		form.release();
		url.release();
	}
	
	@Override
	public void release(PageContext pc) {
		isInit=false;
		form.release();
		url.release();
	}


	/* *
	 * @see railo.runtime.type.scope.URLForm#getForm()
	 * /
	public Form getForm() {
		return form;
	}*/

	/* *
	 *
	 * @see railo.runtime.type.scope.URLForm#getURL()
	 * /
	public URL getURL() {
		return url;
	}*/

	/**
	 *
	 * @see railo.runtime.type.scope.URL#getEncoding()
	 */
	public String getEncoding() {
		return form.getEncoding();
	}

	@Override
	public void setEncoding(ApplicationContext ac, String encoding)throws UnsupportedEncodingException {
		form.setEncoding(ac,encoding);
	}

	/**
	 *
	 * @see railo.runtime.type.scope.Scope#getType()
	 */
	public int getType() {
		return form.getType();
	}

	/**
	 *
	 * @see railo.runtime.type.scope.Scope#getTypeAsString()
	 */
	public String getTypeAsString() {
		return form.getTypeAsString();
	}

	/**
	 *
	 * @see railo.runtime.type.scope.Scope#isInitalized()
	 */
	public boolean isInitalized() {
		return isInit;
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#clear()
	 */
	public void clear() {
		form.clear();
		url.clear();
	}

	/**
	 * @see railo.runtime.type.Collection#containsKey(railo.runtime.type.Collection.Key)
	 */
	public boolean containsKey(Collection.Key key) {
		return form.containsKey(key);
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key)
	 */
	public Object get(Collection.Key key) throws PageException {
		return form.get(key);
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(Collection.Key key, Object defaultValue) {
		return form.get(key, defaultValue);
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#keyIterator()
	 */
	public Iterator<Collection.Key> keyIterator() {
		return form.keyIterator();
	}
    
    @Override
	public Iterator<String> keysAsStringIterator() {
    	return form.keysAsStringIterator();
    }
	
	@Override
	public Iterator<Entry<Key, Object>> entryIterator() {
		return form.entryIterator();
	}
	
	@Override
	public Iterator<Object> valueIterator() {
		return form.valueIterator();
	}

	@Override
	public Collection.Key[] keys() {
		return form.keys();
	}

	@Override
	public Object remove(Collection.Key key) throws PageException {
		return form.remove(key);
	}

	@Override
	public Object removeEL(Collection.Key key) {
		return form.removeEL(key);
	}

	/**
	 * @see railo.runtime.type.Collection#set(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(Collection.Key key, Object value) throws PageException {
		return form.set(key, value);
	}

	/**
	 * @see railo.runtime.type.Collection#setEL(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(Collection.Key key, Object value) {
		return form.setEL(key, value);
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#size()
	 */
	public int size() {
		return form.size();
	}

	/**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		return form.toDumpData(pageContext, maxlevel,dp);
	}

	/**
	 * @see railo.runtime.op.Castable#castToBooleanValue()
	 */
	public boolean castToBooleanValue() throws PageException {
		return form.castToBooleanValue();
	}
    
    /**
     * @see railo.runtime.op.Castable#castToBoolean(java.lang.Boolean)
     */
    public Boolean castToBoolean(Boolean defaultValue) {
        return form.castToBoolean(defaultValue);
    }

	/**
	 *
	 * @see railo.runtime.op.Castable#castToDateTime()
	 */
	public DateTime castToDateTime() throws PageException {
		return form.castToDateTime();
	}
    
    /**
     * @see railo.runtime.op.Castable#castToDateTime(railo.runtime.type.dt.DateTime)
     */
    public DateTime castToDateTime(DateTime defaultValue) {
        return form.castToDateTime(defaultValue);
    }

	/**
	 *
	 * @see railo.runtime.op.Castable#castToDoubleValue()
	 */
	public double castToDoubleValue() throws PageException {
		return form.castToDoubleValue();
	}
    
    /**
     * @see railo.runtime.op.Castable#castToDoubleValue(double)
     */
    public double castToDoubleValue(double defaultValue) {
        return form.castToDoubleValue(defaultValue);
    }

	/**
	 *
	 * @see railo.runtime.op.Castable#castToString()
	 */
	public String castToString() throws PageException {
		return form.castToString();
	}
	
	/**
	 * @see railo.runtime.type.util.StructSupport#castToString(java.lang.String)
	 */
	public String castToString(String defaultValue) {
		return form.castToString(defaultValue);
	}


	/**
	 * @throws PageException 
	 * @see railo.runtime.op.Castable#compare(boolean)
	 */
	public int compareTo(boolean b) throws PageException {
		return form.compareTo(b);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(railo.runtime.type.dt.DateTime)
	 */
	public int compareTo(DateTime dt) throws PageException {
		return form.compareTo(dt);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(double)
	 */
	public int compareTo(double d) throws PageException {
		return form.compareTo(d);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(java.lang.String)
	 */
	public int compareTo(String str) throws PageException {
		return form.compareTo(str);
	}

	/**
	 *
	 * @see railo.runtime.type.scope.Form#getFileUpload(java.lang.String)
	 */
	public DiskFileItem getFileUpload(String key) {
		return form.getFileUpload(key);
	}

	/**
	 *
	 * @see railo.runtime.type.scope.Form#getInitException()
	 */
	public PageException getInitException() {
		return form.getInitException();
	}

	/**
	 * @see railo.runtime.type.Collection#duplicate(boolean)
	 */
	public Collection duplicate(boolean deepCopy) {
		return (Collection) Duplicator.duplicate(form,deepCopy);
	}
	
	@Override
	public void setScriptProtecting(ApplicationContext ac,boolean b) {
		form.setScriptProtecting(ac,b);
	}

	/**
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value) {
		return form.containsValue(value);
	}

	/**
	 * @see java.util.Map#values()
	 */
	public java.util.Collection<Object> values() {
		return form.values();
	}

	public FormItem getUploadResource(String key) {
		return form.getUploadResource(key);
	}

	public FormItem[] getFileItems() {
		return form.getFileItems();
	}

	public ServletInputStream getInputStream() {
		return form.getInputStream();
	}
}
