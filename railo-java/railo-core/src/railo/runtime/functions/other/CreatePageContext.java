/**
 * Implements the CFML Function getpagecontext
 */
package railo.runtime.functions.other;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.Cookie;

import railo.commons.io.DevNullOutputStream;
import railo.commons.lang.Pair;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.thread.ThreadUtil;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.util.CollectionUtil;

public final class CreatePageContext implements Function {


	
	public static Object call(PageContext pc, String serverName, String scriptName) throws PageException {
		return call(pc,serverName,scriptName,"",new StructImpl(),new StructImpl(),new StructImpl(),new StructImpl());
	}
	
	public static Object call(PageContext pc, String serverName, String scriptName,String queryString) throws PageException {
		return call(pc,serverName,scriptName,queryString,new StructImpl(),new StructImpl(),new StructImpl(),new StructImpl());
	}
	
	public static Object call(PageContext pc, String serverName, String scriptName,String queryString, Struct cookies) throws PageException {
		return call(pc,serverName,scriptName,queryString,cookies,new StructImpl(),new StructImpl(),new StructImpl());
	}
	
	public static Object call(PageContext pc, String serverName, String scriptName,String queryString, Struct cookies, Struct headers) throws PageException {
		return call(pc,serverName,scriptName,queryString,cookies,headers,new StructImpl(),new StructImpl());
	}
	
	public static Object call(PageContext pc, String serverName, String scriptName,String queryString, Struct cookies, Struct headers, Struct parameters) throws PageException {
		return call(pc,serverName,scriptName,queryString,cookies,headers,parameters,new StructImpl());
	}
	
	public static Object call(PageContext pc, String serverName, String scriptName,String queryString, Struct cookies, Struct headers, Struct parameters, Struct attributes) throws PageException {
		return ThreadUtil.createPageContext(
				pc.getConfig(), 
				DevNullOutputStream.DEV_NULL_OUTPUT_STREAM, 
				serverName, 
				scriptName, 
				queryString, 
				toCookies(cookies), 
				toPair(headers,true), 
				toPair(parameters,true), 
				castValuesToString(attributes));
	}

	private static Struct castValuesToString(Struct sct) throws PageException {
		Key[] keys = CollectionUtil.keys(sct);
		for(int i=0;i<keys.length;i++){
			sct.set(keys[i], Caster.toString(sct.get(keys[i])));
		}
		return sct;
	}

	private static Pair<String,Object>[] toPair(Struct sct, boolean doStringCast) throws PageException {
		Iterator<Entry<Key, Object>> it = sct.entryIterator();
		Entry<Key, Object> e;
		Object value;
		List<Pair<String,Object>> pairs=new ArrayList<Pair<String,Object>>();
		while(it.hasNext()){
			e = it.next();
			value= e.getValue();
			if(doStringCast)value=Caster.toString(value);
			pairs.add(new Pair<String,Object>(e.getKey().getString(),value));
		}
		return pairs.toArray(new Pair[pairs.size()]);
	}

	private static Cookie[] toCookies(Struct sct) throws PageException {
		Iterator<Entry<Key, Object>> it = sct.entryIterator();
		Entry<Key, Object> e;
		List<Cookie> cookies=new ArrayList<Cookie>();
		while(it.hasNext()){
			e = it.next();
			cookies.add(new Cookie(e.getKey().getString(), Caster.toString(e.getValue())));
		}
		return cookies.toArray(new Cookie[cookies.size()]);
	}
}