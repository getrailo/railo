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
import railo.runtime.type.Collection;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.scope.FormImpl.Item;
import railo.runtime.type.util.StructSupport;

/**
 * 
 */
public final class UrlFormImpl extends StructSupport implements URLForm,FormUpload {

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
		form.addRaw(url.getRaw());
		
		/*String[] keys = url.keys();
		for(int i=0;i<keys.length;i++) {
			form.setEL(keys[i], url.get(keys[i],null));
		}*/
	}
	/**
	 *
	 * @see railo.runtime.type.scope.Scope#release()
	 */
	public void release() {
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

	/**
	 *
	 * @see railo.runtime.type.scope.URL#setEncoding(java.lang.String)
	 */
	public void setEncoding(String encoding)throws UnsupportedEncodingException {
		form.setEncoding(encoding);
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
	public Iterator keyIterator() {
		return form.keyIterator();
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#keysAsString()
	 */
	public String[] keysAsString() {
		return form.keysAsString();
	}

	/**
	 * @see railo.runtime.type.Collection#keys()
	 */
	public Collection.Key[] keys() {
		return form.keys();
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#remove(railo.runtime.type.Collection.Key)
	 */
	public Object remove(Collection.Key key) throws PageException {
		return form.remove(key);
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#removeEL(railo.runtime.type.Collection.Key)
	 */
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
		return form.duplicate(deepCopy);
	}
	


	/**
	 * @see railo.runtime.type.scope.URL#setScriptProtecting(boolean)
	 */
	public void setScriptProtecting(boolean b) {
		form.setScriptProtecting(b);
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
	public java.util.Collection values() {
		return form.values();
	}

	public Item getUploadResource(String key) {
		return form.getUploadResource(key);
	}

	public Item[] getFileItems() {
		return form.getFileItems();
	}

	public ServletInputStream getInputStream() {
		return form.getInputStream();
	}
}
