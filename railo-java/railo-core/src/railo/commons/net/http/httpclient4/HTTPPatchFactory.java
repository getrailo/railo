package railo.commons.net.http.httpclient4;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import railo.commons.lang.ClassUtil;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;

public class HTTPPatchFactory {
	
	public static HttpEntityEnclosingRequestBase getHTTPPatch(String url) throws PageException	{
		// try to load the class, perhaps class does not exists with older jars
		Class clazz = ClassUtil.loadClass(
				HttpEntityEnclosingRequestBase.class.getClassLoader(),
				"org.apache.http.client.methods.HttpPatch",null);
		if(clazz==null) throw new ApplicationException("cannot load class [org.apache.http.client.methods.HttpPatch], you have to update your apache-commons-http*** jars");
		try {
			return (HttpEntityEnclosingRequestBase) ClassUtil.loadInstance(clazz,new Object[]{url});
		}
		catch (Throwable t) {
			throw Caster.toPageException(t);
		}
		
		
		//FUTURE if we have the new jar for sure return new HttpPatch(url);
	}
}
