package railo.runtime.spooler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import railo.commons.lang.StringUtil;
import railo.commons.net.HTTPUtil;
import railo.commons.net.http.HTTPResponse;
import railo.commons.net.http.httpclient4.HTTPEngine4Impl;
import railo.runtime.PageContext;
import railo.runtime.config.Config;
import railo.runtime.config.RemoteClient;
import railo.runtime.converter.ConverterException;
import railo.runtime.converter.JSONConverter;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.PageException;
import railo.runtime.interpreter.JSONExpressionInterpreter;
import railo.runtime.op.Caster;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.util.KeyConstants;

public abstract class SpoolerTaskHTTPCall extends SpoolerTaskSupport {
	
	private static final long serialVersionUID = -1994776413696459993L;
	
	private RemoteClient client;
    
    
	public SpoolerTaskHTTPCall(ExecutionPlan[] plans,RemoteClient client) {
		super(plans);
		this.client=client;
	}

	/**
	 * @return 
	 * @see railo.runtime.spooler.SpoolerTask#execute()
	 */
	public final Object execute(Config config) throws PageException {
		return execute(client, config, getMethodName(), getArguments());
	}
	
	public static final Object execute(RemoteClient client, Config config, String methodName, Struct args) throws PageException {
		//return rpc.callWithNamedValues(config, getMethodName(), getArguments());
		PageContext pc = ThreadLocalPageContext.get();
		
		
		// remove wsdl if necessary
		String url=client.getUrl();
		if(StringUtil.endsWithIgnoreCase(url,"?wsdl"))
			url=url.substring(0,url.length()-5);
		
		// Params
		Map<String, String> params=new HashMap<String, String>();
		params.put("method",methodName);
		params.put("returnFormat","json");
		try {
			params.put("argumentCollection",new JSONConverter(true).serialize(pc, args, false));
		
		
			HTTPResponse res = HTTPEngine4Impl.post(
				HTTPUtil.toURL(url,true), 
				client.getServerUsername(), 
				client.getServerPassword(), -1L, -1, config.getWebCharset(), "Railo Remote Invocation", client.getProxyData(), null,params);
		
			return new JSONExpressionInterpreter().interpret(pc, res.getContentAsString());
			
		}
		catch (IOException ioe) {
			throw Caster.toPageException(ioe); 
		}
		catch (ConverterException ce) {
			throw Caster.toPageException(ce); 
		}
		
	}
	
	
	/**
	 * @see railo.runtime.spooler.SpoolerTask#subject()
	 */
	public String subject() {
		return client.getLabel();
	}

	/**
	 * @see railo.runtime.spooler.SpoolerTask#detail()
	 */
	public Struct detail() {
		Struct sct=new StructImpl();
		sct.setEL(KeyConstants._label, client.getLabel());
		sct.setEL(KeyConstants._url, client.getUrl());
		
		return sct;
	}
	


	protected abstract String getMethodName();
	protected abstract Struct getArguments();
}
