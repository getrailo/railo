package railo.runtime.functions.system;

import java.util.Iterator;

import railo.commons.io.res.util.ResourceUtil;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.converter.JSONConverter;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.UDF;
import railo.runtime.type.util.KeyConstants;

/**
 * returns the root of this actuell Page Context
 */
public final class CallStackGet implements Function {

	private static final long serialVersionUID = -5853145189662102420L;
	static final Collection.Key LINE_NUMBER = KeyImpl.init("LineNumber");

	public static Object call(PageContext pc) {
		Array arr=new ArrayImpl();
		_getTagContext(pc, arr, new Exception("Stack trace"),LINE_NUMBER);
		return arr;
	}

	public static Object call(PageContext pc, String type) throws PageException {

		Array arr = (Array)call(pc);

		if ( type.equalsIgnoreCase( "array" ) )
			return arr;

		if ( type.equalsIgnoreCase( "json" ) ) {

			try {

				return new JSONConverter(true,null).serialize( pc, arr, false );
			}
			catch (Throwable t) {

				throw Caster.toPageException( t );
			}
		}

		StringBuilder sb = new StringBuilder( 64 * arr.size() );
		Struct struct;
		String func;

		Iterator it = arr.valueIterator();

		if ( type.equalsIgnoreCase( "text" ) || type.equalsIgnoreCase( "string" ) ) {

			while (it.hasNext()) {

				struct = (Struct)it.next();

				sb.append( (String)struct.get( KeyConstants._template ) );

				func = (String)struct.get( KeyConstants._function );
				if ( !func.isEmpty() ) {
					sb.append( '.' ).append( func ).append( "()" );
				}

				sb.append( ':' ).append( ((Double)struct.get( LINE_NUMBER )).intValue() );

				if ( it.hasNext() )
					sb.append( "; " );
			}

			return sb.toString();
		}

		if ( type.equalsIgnoreCase( "html" ) ) {

			sb.append( "<ul class='-railo-array'>" );

			while (it.hasNext()) {

				struct = (Struct)it.next();

				sb.append( "<li>" );

				sb.append( (String)struct.get( KeyConstants._template ) );

				func = (String)struct.get( KeyConstants._function );
				if ( !func.isEmpty() ) {
					sb.append( '.' ).append( func ).append( "()" );
				}

				sb.append( ':' ).append( ((Double)struct.get( LINE_NUMBER )).intValue() );

				sb.append( "</li>" );
			}

			sb.append("</ul>");

			return sb.toString();
		}

		throw new FunctionException( pc, CallStackGet.class.getSimpleName(), 1, "type", "Argument type [" + type +"] is not valid.  Valid types are: [array], text, html, json." );
	}

	public static void _getTagContext(PageContext pc, Array tagContext, Throwable t,Collection.Key lineNumberName) {
		//Throwable root = t.getRootCause();
		Throwable cause = t.getCause();
		if(cause!=null)_getTagContext(pc, tagContext, cause,lineNumberName);
		StackTraceElement[] traces = t.getStackTrace();

		UDF[] udfs = ((PageContextImpl)pc).getUDFs();

		int line=0;
		String template;
		Struct item;
		StackTraceElement trace=null;
		String functionName,methodName;
		int index=udfs.length-1;
		for(int i=0;i<traces.length;i++) {
			trace=traces[i];
			template=trace.getFileName();
			if(trace.getLineNumber()<=0 || template==null || ResourceUtil.getExtension(template,"").equals("java")) continue;
			methodName=trace.getMethodName();
			if(methodName!=null && methodName.startsWith("udfCall") && index>-1) 
				functionName=udfs[index--].getFunctionName();

			else functionName="";

			item=new StructImpl();
			line=trace.getLineNumber();
			item.setEL(KeyConstants._function,functionName);
			try {
				template=ExpandPath.call(pc, template);
			}
			catch (PageException e) {}
			item.setEL(KeyConstants._template,template);
			item.setEL(lineNumberName,new Double(line));
			tagContext.appendEL(item);
		}
	}
}