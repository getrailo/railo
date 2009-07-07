package railo.runtime.cfx;

import java.io.IOException;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;

import com.allaire.cfx.Query;
import com.allaire.cfx.Response;



/**
 * 
 */
public final class ResponseImpl implements Response {
	
	private PageContext pc;
	private boolean debug;
	
	
	/**
	 * @param pc
	 * @param debug
	 */
	public ResponseImpl(PageContext pc,boolean debug) {
		this.pc=pc;
		this.debug=debug;
	}
	
	/**
	 * @see com.allaire.cfx.Response#addQuery(java.lang.String, java.lang.String[])
	 */
	public Query addQuery(String name, String[] column) {
		railo.runtime.type.Query query=new railo.runtime.type.QueryImpl(column,0,name);
		
		try {
			pc.setVariable(name,query);
		} 
		catch (PageException e) {
		}
		return new QueryWrap(query);
	}

	/**
	 * @see com.allaire.cfx.Response#setVariable(java.lang.String, java.lang.String)
	 */
	public void setVariable(String key, String value) {
		try {
			pc.setVariable(key,value);
		} 
		catch (PageException e) {
		}
	}

	/**
	 * @see com.allaire.cfx.Response#write(java.lang.String)
	 */
	public void write(String str) {
		try {
			pc.write(str);
		} catch (IOException e) {
			
		}
	}

	/**
	 * @see com.allaire.cfx.Response#writeDebug(java.lang.String)
	 */
	public void writeDebug(String str) {
		if(debug)write(str);
	}

}