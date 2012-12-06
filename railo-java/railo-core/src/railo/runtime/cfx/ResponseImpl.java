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
	
	@Override
	public Query addQuery(String name, String[] column) {
		railo.runtime.type.Query query=new railo.runtime.type.QueryImpl(column,0,name);
		
		try {
			pc.setVariable(name,query);
		} 
		catch (PageException e) {
		}
		return new QueryWrap(query);
	}

	@Override
	public void setVariable(String key, String value) {
		try {
			pc.setVariable(key,value);
		} 
		catch (PageException e) {
		}
	}

	@Override
	public void write(String str) {
		try {
			pc.write(str);
		} catch (IOException e) {
			
		}
	}

	@Override
	public void writeDebug(String str) {
		if(debug)write(str);
	}

}