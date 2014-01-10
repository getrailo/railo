package railo.runtime.net.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import railo.commons.io.IOUtil;
import railo.commons.lang.ExceptionUtil;
import railo.commons.lang.StringUtil;
import railo.commons.lang.mimetype.MimeType;
import railo.commons.net.HTTPUtil;
import railo.commons.net.http.HTTPEngine;
import railo.commons.net.http.HTTPResponse;
import railo.commons.net.http.Header;
import railo.runtime.ComponentPage;
import railo.runtime.Info;
import railo.runtime.PageContext;
import railo.runtime.converter.ConverterException;
import railo.runtime.converter.JSONConverter;
import railo.runtime.converter.ScriptConverter;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.dump.SimpleDumpData;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.net.proxy.ProxyData;
import railo.runtime.net.rpc.RPCException;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.Iteratorable;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Objects;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.UDF;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.it.KeyAsStringIterator;
import railo.runtime.type.it.KeyIterator;
import railo.runtime.type.it.ObjectsEntryIterator;
import railo.runtime.type.it.ObjectsIterator;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.util.KeyConstants;
import railo.runtime.type.util.ListUtil;
import railo.runtime.type.util.UDFUtil;

/**
 * Client to implement http based webservice
 */
public class HTTPClient implements Objects, Iteratorable {

	private static final long serialVersionUID = -7920478535030737537L;

	private static final String USER_AGENT = "Railo "+Info.getFullVersionInfo();

	
	private URL metaURL;
	private String username;
	private String password;
	private ProxyData proxyData;
	private URL url;
	private Struct meta;

	private int argumentsCollectionFormat=-1;

	public HTTPClient(String httpUrl, String username, String password, ProxyData proxyData) throws PageException {
		try {
			url=HTTPUtil.toURL(httpUrl,true);
			
			if(!StringUtil.isEmpty(this.url.getQuery())) throw new ApplicationException("invalid url, query string is not allowed as part of the call");
			metaURL=HTTPUtil.toURL(url.toExternalForm()+"?cfml",true);
		}
		catch (MalformedURLException e) {
			throw Caster.toPageException(e);
		}
		
		this.username=username;
		this.password=password;
		this.proxyData=proxyData;
		
	}

	@Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		try {
			Array args;
			Struct sct = getMetaData(pageContext),val,a;
			DumpTable cfc = new DumpTable("udf","#66ccff","#ccffff","#000000"),udf,arg;
			cfc.setTitle("Web Service (HTTP)");
			if(dp.getMetainfo())cfc.setComment(url.toExternalForm());
			Iterator<Entry<Key, Object>> it = sct.entryIterator();
			Entry<Key, Object> e;
			// Loop UDFs
			while(it.hasNext()){
				e = it.next();
				val=Caster.toStruct(e.getValue());
				
				// udf name
				udf = new DumpTable("udf","#66ccff","#ccffff","#000000");
				arg = new DumpTable("udf","#66ccff","#ccffff","#000000");
				
				cfc.appendRow(1, new SimpleDumpData(e.getKey().getString()),udf);
				
				// args
				args = Caster.toArray(val.get(KeyConstants._arguments));
				udf.appendRow(1,new SimpleDumpData("arguments"),arg);
				arg.appendRow(7,new SimpleDumpData("name"),new SimpleDumpData("required"),new SimpleDumpData("type"));
				Iterator<Object> ait = args.valueIterator();
				while(ait.hasNext()){
					a=Caster.toStruct(ait.next());
					arg.appendRow(0,
							new SimpleDumpData(Caster.toString(a.get(KeyConstants._name))),
							new SimpleDumpData(Caster.toString(a.get(KeyConstants._required))),
							new SimpleDumpData(Caster.toString(a.get(KeyConstants._type))));
					
				}

				// return type
				udf.appendRow(1,new SimpleDumpData("return type"),new SimpleDumpData(Caster.toString(val.get(KeyConstants._returntype))));
				
				
				/*
				cfc.appendRow(new DumpRow(0,new DumpData[]{
						new SimpleDumpData(arg.getDisplayName()),
						new SimpleDumpData(e.getKey().getString()),
						new SimpleDumpData(arg.isRequired()),
						new SimpleDumpData(arg.getTypeAsString()),
						def,
						new SimpleDumpData(arg.getHint())}));*/
				
			}
			return cfc;
	        
		}
		catch (Throwable t) {
			throw new PageRuntimeException(Caster.toPageException(t));
		}
	}
	
	private Struct getMetaData(PageContext pc) {
		if(meta==null) {
			pc=ThreadLocalPageContext.get(pc);
			InputStream is=null;
			
			try{
				HTTPResponse rsp = HTTPEngine.get(metaURL, username, password, -1, 0, "UTF-8", USER_AGENT, proxyData, null);
				MimeType mt = getMimeType(rsp,null);
				int format = MimeType.toFormat(mt, -1);
				if(format==-1) throw new ApplicationException("cannot convert response with mime type ["+mt+"] to a CFML Object");
				is = rsp.getContentAsStream();
				Struct data = Caster.toStruct(ReqRspUtil.toObject(pc,IOUtil.toBytes(is,false),format,mt.getCharset(),null));
				Object oUDF=data.get(KeyConstants._functions,null);
				Object oAACF=data.get(ComponentPage.ACCEPT_ARG_COLL_FORMATS,null);
				
				if(oUDF!=null && oAACF!=null) {
					meta=Caster.toStruct(oUDF);
					String[] strFormats = ListUtil.listToStringArray(Caster.toString(oAACF),',');
					argumentsCollectionFormat=UDFUtil.toReturnFormat(strFormats,UDF.RETURN_FORMAT_JSON);
				}
				else {
					meta=data;
				}
				
				
			}
			catch(Throwable t) {
				throw new PageRuntimeException(Caster.toPageException(t));
			}
			finally {
				IOUtil.closeEL(is);
			}
		}
		return meta;
	}

	@Override
	public Iterator<Key> keyIterator() {
		try {
			return getMetaData(null).keyIterator();
		}
		catch (Exception e) {
			return new KeyIterator(new Collection.Key[0]);
		}
	}

	@Override
	public Object call(PageContext pc, Key methodName, Object[] arguments) throws PageException {
		checkFunctionExistence(pc,methodName,false);
		
		if(arguments.length==0) return _callWithNamedValues(pc, methodName, new StructImpl());
		Struct m = checkFunctionExistence(pc,methodName,true);
		
		
		Array args = Caster.toArray(m.get(KeyConstants._arguments,null),null);
		if(args==null) args=new ArrayImpl();
		Struct sct=new StructImpl(),el;
		String name;
		for(int i=0;i<arguments.length;i++){
			if(args.size()>i) {
				el=Caster.toStruct(args.get(i+1, null),null);
				if(el!=null) {
					name=Caster.toString(el.get(KeyConstants._name,null),null);
					if(!StringUtil.isEmpty(name)) {
						sct.set(name, arguments[i]);
						continue;
					}
				}
			}
			sct.set("arg"+(i+1), arguments[i]);
		}
		
		
		return _callWithNamedValues(pc, methodName, sct);
	}

	@Override

	public Object callWithNamedValues(PageContext pc, Key methodName, Struct args) throws PageException {
		checkFunctionExistence(pc,methodName,false);
		return _callWithNamedValues(pc, methodName, args);
		
	}
	
	private Object _callWithNamedValues(PageContext pc, Key methodName, Struct args) throws PageException {
		
		// prepare request
		Map<String,String> formfields=new HashMap<String, String>();
		formfields.put("method", methodName.getString());
		formfields.put("returnformat", "cfml");
		
		
		String str;
		try {
			if(UDF.RETURN_FORMAT_JSON==argumentsCollectionFormat)	{
				str = new JSONConverter(true).serialize(pc,args,false);
				formfields.put("argumentCollectionFormat", "json");
			}
			else if(UDF.RETURN_FORMAT_SERIALIZE==argumentsCollectionFormat)	{
				str = new ScriptConverter().serialize(args);
				formfields.put("argumentCollectionFormat", "cfml");
			}
			else {
				str = new ScriptConverter().serialize(args); // Json interpreter in Railo also accepts cfscript
			}
		}
		catch (ConverterException e) {
			throw Caster.toPageException(e);
		}
		
		
		// add aparams to request
		formfields.put("argumentCollection", str);
		/*
		Iterator<Entry<Key, Object>> it = args.entryIterator();
		Entry<Key, Object> e;
		while(it.hasNext()){
			e = it.next();
			formfields.put(e.getKey().getString(), Caster.toString(e.getValue()));
		}*/
		
		Map<String,String> headers=new HashMap<String, String>();
		headers.put("accept", "application/cfml,application/json"); // application/java disabled for the moment, it is not working when we have different railo versions
		
		InputStream is=null;
		try {
			// call remote cfc
			HTTPResponse rsp = HTTPEngine.post(url, username, password, -1, 0, "UTF-8", USER_AGENT, proxyData,headers, formfields);
			
			// read result
			Header[] rspHeaders = rsp.getAllHeaders();
			MimeType mt = getMimeType(rspHeaders,null);
			int format = MimeType.toFormat(mt, -1);
			if(format==-1) {
				if(rsp.getStatusCode()!=200) {
					boolean hasMsg=false;
					String msg=rsp.getStatusText();
					for(int i=0;i<rspHeaders.length;i++){
						if(rspHeaders[i].getName().equalsIgnoreCase("exception-message")){
							msg=rspHeaders[i].getValue();
							hasMsg=true;
						}
					}
					is = rsp.getContentAsStream();
					ApplicationException ae = new ApplicationException("remote component throws the following error:"+msg);
					if(!hasMsg)ae.setAdditional(KeyImpl.init("respone-body"),IOUtil.toString(is, mt.getCharset()));
					
					throw ae;
				}
				throw new ApplicationException("cannot convert response with mime type ["+mt+"] to a CFML Object");
			}
			is = rsp.getContentAsStream();
			return ReqRspUtil.toObject(pc,IOUtil.toBytes(is,false),format,mt.getCharset(),null);
		}
		catch (IOException ioe) {
			throw Caster.toPageException(ioe);
		}
		finally {
			IOUtil.closeEL(is);
		}
	}

	private Struct checkFunctionExistence(PageContext pc,Key methodName, boolean getDataFromRemoteIfNecessary) throws ApplicationException {
		if(getDataFromRemoteIfNecessary) getMetaData(pc);
		if(meta==null) return null;
		Struct m = Caster.toStruct(meta.get(methodName,null),null);
		if(m==null) throw new ApplicationException(
				"the remote component has no function with name ["+methodName+"]",
				ExceptionUtil.createSoundexDetail(methodName.getString(),meta.keysAsStringIterator(),"functions"));
		return m;
	}


	private MimeType getMimeType(HTTPResponse rsp, MimeType defaultValue) {
		return getMimeType(rsp.getAllHeaders(), defaultValue);
	}
	private MimeType getMimeType(Header[] headers, MimeType defaultValue) {
		String returnFormat=null,contentType=null;
		for(int i=0;i<headers.length;i++){
			if(headers[i].getName().equalsIgnoreCase("Return-Format"))returnFormat=headers[i].getValue();
			else if(headers[i].getName().equalsIgnoreCase("Content-Type"))contentType=headers[i].getValue();
		}
		MimeType rf=null,ct=null;
		
		// return format
		if(!StringUtil.isEmpty(returnFormat)) {
			int format=UDFUtil.toReturnFormat(returnFormat,-1);
			rf=MimeType.toMimetype(format, null);
		}
		// ContentType
		if(!StringUtil.isEmpty(contentType)) {
			ct= MimeType.getInstance(contentType);
		}
		if(rf!=null && ct!=null) {
			if(rf.same(ct)) return ct; // because this has perhaps a charset definition
			return rf;
		}
		if(rf!=null) return rf;
		if(ct!=null) return ct;
		
		
		return defaultValue;
	}

	@Override
	public Object get(PageContext pc, Collection.Key key) throws PageException {
        return call(pc,KeyImpl.init("get"+key.getString()), ArrayUtil.OBJECT_EMPTY);
	}

	@Override
	public Object get(PageContext pc, Collection.Key key, Object defaultValue) {
		try {
            return call(pc,KeyImpl.init("get"+StringUtil.ucFirst(key.getString())), ArrayUtil.OBJECT_EMPTY);
        } catch (PageException e) {
            return defaultValue;
        }
	}
	
	@Override
	public Object set(PageContext pc, Collection.Key propertyName, Object value) throws PageException {
        return call(pc,KeyImpl.init("set"+propertyName.getString()), new Object[]{value});
	}

	@Override
	public Object setEL(PageContext pc, Collection.Key propertyName, Object value) {
		try {
            return call(pc,KeyImpl.init("set"+propertyName.getString()), new Object[]{value});
        } catch (PageException e) {
            return null;
        }
	}
	
	@Override
	public Iterator<String> keysAsStringIterator() {
		return new KeyAsStringIterator(keyIterator());
	}

	@Override
	public Iterator<Object> valueIterator() {
		return new ObjectsIterator(keyIterator(),this);
	}
	
	@Override
	public Iterator<Entry<Key, Object>> entryIterator() {
		return new ObjectsEntryIterator(keyIterator(), this);
	}

	@Override
    public String castToString() throws ExpressionException {
        throw new RPCException("can't cast Webservice to a string");
    }

	@Override
	public String castToString(String defaultValue) {
		return defaultValue;
	}

    @Override
    public boolean castToBooleanValue() throws ExpressionException {
        throw new RPCException("can't cast Webservice to a boolean");
    }
    
    @Override
    public Boolean castToBoolean(Boolean defaultValue) {
        return defaultValue;
    }

    @Override
    public double castToDoubleValue() throws ExpressionException {
        throw new RPCException("can't cast Webservice to a number");
    }
    
    @Override
    public double castToDoubleValue(double defaultValue) {
        return defaultValue;
    }

    @Override
    public DateTime castToDateTime() throws RPCException {
        throw new RPCException("can't cast Webservice to a Date Object");
    }
    
    @Override
    public DateTime castToDateTime(DateTime defaultValue) {
        return defaultValue;
    }

	@Override
	public int compareTo(boolean b) throws ExpressionException {
		throw new ExpressionException("can't compare Webservice Object with a boolean value");
	}

	@Override
	public int compareTo(DateTime dt) throws PageException {
		throw new ExpressionException("can't compare Webservice Object with a DateTime Object");
	}

	@Override
	public int compareTo(double d) throws PageException {
		throw new ExpressionException("can't compare Webservice Object with a numeric value");
	}

	@Override
	public int compareTo(String str) throws PageException {
		throw new ExpressionException("can't compare Webservice Object with a String");
	}
}
