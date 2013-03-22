package railo.runtime.type.scope;

import java.io.UnsupportedEncodingException;

import railo.commons.net.URLItem;
import railo.runtime.PageContext;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.util.ApplicationContext;

/**
 * Implements URL Scope 
 */
public final class URLImpl extends ScopeSupport implements URL,ScriptProtected {

	private String encoding=null;
    private int scriptProtected=ScriptProtected.UNDEFINED;
    private static final URLItem[] empty=new URLItem[0];
	private static final Collection.Key REQUEST_TIMEOUT = KeyImpl.getInstance("RequestTimeout"); 
    private URLItem[] raw=empty;
    
	/**
	 * Standart Constructor
	 */
	public URLImpl() {
		super(true,"url",SCOPE_URL);
	}

    /**
     * @see railo.runtime.type.scope.URL#getEncoding()
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * @see railo.runtime.type.scope.URL#setEncoding(java.lang.String)
     */
    public void setEncoding(String encoding) throws UnsupportedEncodingException {
        encoding=encoding.trim().toUpperCase();
        if(encoding.equals(this.encoding)) return;
        this.encoding = encoding;
        if(isInitalized())fillDecoded(raw,encoding,isScriptProtected());
    }

	/**
	 * @see railo.runtime.type.scope.ScopeSupport#initialize(railo.runtime.PageContext)
	 */
	public void initialize(PageContext pc) {
        if(encoding==null)encoding=pc.getConfig().getWebCharset();
        if(scriptProtected==ScriptProtected.UNDEFINED) {
			scriptProtected=((pc.getApplicationContext().getScriptProtect()&ApplicationContext.SCRIPT_PROTECT_URL)>0)?
					ScriptProtected.YES:ScriptProtected.NO;
		}
        
        try {
			super.initialize(pc); 
            raw=setFromQueryString(pc. getHttpServletRequest().getQueryString());
            
            fillDecoded(raw,encoding,isScriptProtected());
            
            if(raw.length>0 && pc.getConfig().isAllowURLRequestTimeout()){
            	Object o=get(REQUEST_TIMEOUT,null);
            	if(o!=null) {
            		long timeout = Caster.toLongValue(o,-1);
            		if(timeout!=-1)pc.setRequestTimeout(timeout*1000);
            	}
            	Caster.toDoubleValue(o,-1);
            }
		}
        catch (Exception e) {}
	}
    /**
     * @see railo.runtime.type.scope.ScopeSupport#release()
     */
    public void release() {
    	encoding=null;
        raw=empty;
        scriptProtected=ScriptProtected.UNDEFINED;
        super.release();
    }

	/**
	 *
	 * @see railo.runtime.type.scope.URL#setScriptProtecting(boolean)
	 */
	public void setScriptProtecting(boolean scriptProtected) {
		
		int _scriptProtected = scriptProtected?ScriptProtected.YES:ScriptProtected.NO;
		//print.out(isInitalized()+"x"+(_scriptProtected+"!="+this.scriptProtected));
		if(isInitalized() && _scriptProtected!=this.scriptProtected) {
			fillDecodedEL(raw,encoding,scriptProtected);
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
		return scriptProtected==ScriptProtected.YES;
	}

	/**
	 * @return the raw
	 */
	public URLItem[] getRaw() {
		return raw;
	}
}