package railo.runtime.dump;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.apache.xerces.dom.AttributeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import railo.commons.date.TimeZoneUtil;
import railo.commons.io.res.Resource;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.converter.WDDXConverter;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.text.xml.XMLAttributes;
import railo.runtime.text.xml.XMLCaster;
import railo.runtime.type.Array;
import railo.runtime.type.Collection;
import railo.runtime.type.ObjectWrap;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.dt.DateTimeImpl;

public class DumpUtil {

	
	public static DumpData toDumpData(Object o,PageContext pageContext, int maxlevel, DumpProperties props) {
		if(maxlevel<=0) {
			return new SimpleDumpData("maximal dump level reached");
		}
		// null
		if(o == null) {
			DumpTable table=new DumpTable("#ff4400","#ff954f","#000000");
			table.appendRow(new DumpRow(0,new SimpleDumpData("Empty:null")));
			return table;
		}
		else if(o instanceof DumpData) {
			return ((DumpData)o);
		}
		// Printable
		else if(o instanceof Dumpable) {
			return ((Dumpable)o).toDumpData(pageContext,maxlevel,props);
		}
		// Map
		else if(o instanceof Map) {
			Map map=(Map) o;
			Iterator it=map.keySet().iterator();

			DumpTable table = new DumpTable("#ffb200","#ffcc00","#000000");
			table.setTitle("Map ("+Caster.toClassName(o)+")");
			
			while(it.hasNext()) {
				Object next=it.next();
				table.appendRow(1,toDumpData(next,pageContext,maxlevel,props),toDumpData(map.get(next),pageContext,maxlevel,props));
			}
			return table;
		}
		// Date
		else if(o instanceof Date) {
			return new DateTimeImpl((Date) o).toDumpData(pageContext,maxlevel,props);
		}

		// Calendar
		else if(o instanceof Calendar) {
			Calendar c=(Calendar)o;
			
			SimpleDateFormat df = new SimpleDateFormat("EE, dd MMM yyyy HH:mm:ss zz",Locale.ENGLISH);
			df.setTimeZone(c.getTimeZone());
			
			DumpTable table=new DumpTable("#ffb200","#ffcc00","#263300");
			table.setTitle("java.util.Calendar");
			table.appendRow(1, new SimpleDumpData("Timezone"), new SimpleDumpData(TimeZoneUtil.toString(c.getTimeZone())));
			table.appendRow(1, new SimpleDumpData("Time"), new SimpleDumpData(df.format(c.getTime())));
	        
			return table;
		}
		
		// List
		else if(o instanceof List) {
			List list=(List) o;
			ListIterator it=list.listIterator();
			
			DumpTable table = new DumpTable("#ffb200","#ffcc00","#000000");
			table.setTitle("Array (List)");
			
			while(it.hasNext()) {
				table.appendRow(1,new SimpleDumpData(it.nextIndex()+1),toDumpData(it.next(),pageContext,maxlevel,props));
			}
			return table;
		}
		// Number
		else if(o instanceof Number) {
			DumpTable table = new DumpTable("#ff4400","#ff954f","#000000");
			table.appendRow(1,new SimpleDumpData("number"),new SimpleDumpData(Caster.toString(((Number)o).doubleValue())));
			return table;
		}

		// String
		else if(o instanceof StringBuffer) {
			DumpTable dt=(DumpTable)toDumpData(o.toString(), pageContext, maxlevel, props);
			if(StringUtil.isEmpty(dt.getTitle()))
				dt.setTitle(Caster.toClassName(o));
			return dt;
		}
		// String
		else if(o instanceof String) {
			String str=(String) o;
			if(str.startsWith("<wddxPacket ")) {
				try {
					WDDXConverter converter =new WDDXConverter(pageContext.getTimeZone(),false);
					converter.setTimeZone(pageContext.getTimeZone());
					Object rst = converter.deserialize(str,false);
					DumpData data = toDumpData(rst, pageContext, maxlevel, props);
					
					DumpTable table = new DumpTable("#C2AF94","#F3EFEA","#000000");
					table.setTitle("WDDX");
					table.appendRow(1,new SimpleDumpData("encoded"),data);
					table.appendRow(1,new SimpleDumpData("raw"),new SimpleDumpData(str));
					return table;
				}
				catch(Throwable t) {}
			}
			DumpTable table = new DumpTable("#ff4400","#ff954f","#000000");
			table.appendRow(1,new SimpleDumpData("string"),new SimpleDumpData(str));
			return table;
		}
		// Character
		else if(o instanceof Character) {
			DumpTable table = new DumpTable("#ff4400","#ff954f","#000000");
			table.appendRow(1,new SimpleDumpData("character"),new SimpleDumpData(o.toString()));
			return table;
		}
		// Resultset
		else if(o instanceof ResultSet) {
			try {
				DumpData dd = new QueryImpl((ResultSet)o,"query").toDumpData(pageContext,maxlevel,props);
				if(dd instanceof DumpTable)
					((DumpTable)dd).setTitle(Caster.toClassName(o));
				return dd;
			} 
			catch (PageException e) {
				
			}
		}
		// Boolean
		else if(o instanceof Boolean) {
			DumpTable table = new DumpTable("#ff4400","#ff954f","#000000");
			table.appendRow(1,new SimpleDumpData("boolean"),new SimpleDumpData(((Boolean)o).booleanValue()));
			return table;
		}
		// File
		else if(o instanceof File) {
			DumpTable table = new DumpTable("#979EAA","#DEE9FB","#000000");
			table.appendRow(1,new SimpleDumpData("File"),new SimpleDumpData(o.toString()));
			return table;
		}
		// Resource
		else if(o instanceof Resource) {
			DumpTable table = new DumpTable("#979EAA","#DEE9FB","#000000");
			table.appendRow(1,new SimpleDumpData("Resource"),new SimpleDumpData(o.toString()));
			return table;
		}
		// Enumeration
		else if(o instanceof Enumeration) {
			Enumeration e=(Enumeration)o;
			
			DumpTable table = new DumpTable("#ffb200","#ffcc00","#000000");
			table.setTitle("Enumeration");
			
			while(e.hasMoreElements()) {
				table.appendRow(0,toDumpData(e.nextElement(),pageContext,maxlevel,props));
			}
			return table;
		}
		// byte[]
		else if(o instanceof byte[]) {
			byte[] bytes=(byte[]) o;
			
			DumpTable table = new DumpTable("#ffb200","#ffcc00","#000000");
			table.setTitle("Native Array");
			
			StringBuffer sb=new StringBuffer();
			for(int i=0;i<bytes.length;i++) {
				if(i!=0)sb.append("-");
				sb.append(bytes[i]);
				if(i==1000) {
					sb.append("  [truncated]  ");
					break;
				}
			}
			table.appendRow(0,new SimpleDumpData(sb.toString()));
			return table;
			 
			
		}
		// Object[]
		else if(Decision.isNativeArray(o)) {
			Array arr;
			try {
				arr = Caster.toArray(o);
				DumpTable htmlBox = new DumpTable("#ffb200","#ffcc00","#000000");
				htmlBox.setTitle("Native Array");
			
				int length=arr.size();
			
				for(int i=1;i<=length;i++) {
					Object ox=null;
					try {
						ox = arr.getE(i);
					} catch (Exception e) {}
					htmlBox.appendRow(1,new SimpleDumpData(i),toDumpData(ox,pageContext,maxlevel,props));
				}
				return htmlBox;
			} 
			catch (PageException e) {
				return new SimpleDumpData("");
			}
		}
		// Node
		else if(o instanceof Node) {
		    return XMLCaster.toDumpData((Node)o, pageContext,maxlevel,props);			
		}
		// ObjectWrap
		else if(o instanceof ObjectWrap) {
			maxlevel++;
		    return toDumpData(((ObjectWrap)o).getEmbededObject(null), pageContext,maxlevel,props);			
		}
		// NodeList
		if(o instanceof NodeList) {
			NodeList list=(NodeList)o;
			int len=list.getLength();
			DumpTable table = new DumpTable("#C2AF94","#F3EFEA","#000000");
			for(int i=0;i<len;i++) {
				table.appendRow(1,new SimpleDumpData(i),toDumpData(list.item(i),pageContext,maxlevel,props));
			}
			return table;
			
		}
		// AttributeMap
		else if(o instanceof AttributeMap) {
			return new XMLAttributes((AttributeMap)o,false).toDumpData(pageContext, maxlevel,props);			
		}
		// HttpSession
		else if(o instanceof HttpSession) {
		    HttpSession hs = (HttpSession)o;
		    Enumeration e = hs.getAttributeNames();
		    
		    DumpTable htmlBox = new DumpTable("#5965e4","#9999ff","#000000");
			htmlBox.setTitle("HttpSession");
		    while(e.hasMoreElements()) {
		        String key=e.nextElement().toString();
		        htmlBox.appendRow(1,new SimpleDumpData(key),toDumpData(hs.getAttribute(key), pageContext,maxlevel,props));
		    }
		    return htmlBox;
		}
		
		// Collection.Key
		else if(o instanceof Collection.Key) {
			Collection.Key key=(Collection.Key) o;
			DumpTable table = new DumpTable("#ff4400","#ff954f","#000000");
			table.appendRow(1,new SimpleDumpData("Collection.Key"),new SimpleDumpData(key.getString()));
			return table;
		}
		
		// reflect
		//else {
			DumpTable table = new DumpTable("#90776E","#B2A49B","#000000");
			
			Class clazz=o.getClass();
			if(o instanceof Class) clazz=(Class) o;
			String fullClassName=clazz.getName();
			int pos=fullClassName.lastIndexOf('.');
			String className=pos==-1?fullClassName:fullClassName.substring(pos+1);
			
			table.setTitle(className);
			table.appendRow(1,new SimpleDumpData("class"),new SimpleDumpData(fullClassName));
			
			// Fields
			Field[] fields=clazz.getFields();
			DumpTable fieldDump = new DumpTable("#90776E","#B2A49B","#000000");
			fieldDump.appendRow(7,new SimpleDumpData("name"),new SimpleDumpData("pattern"),new SimpleDumpData("value"));
			for(int i=0;i<fields.length;i++) {
				Field field = fields[i];
				DumpData value;
				try {//print.out(o+":"+maxlevel);
					value=new SimpleDumpData(Caster.toString(field.get(o), ""));
				} 
				catch (Exception e) {
					value=new SimpleDumpData("");
				}
				fieldDump.appendRow(0,new SimpleDumpData(field.getName()),new SimpleDumpData(field.toString()),value);
			}
			if(fields.length>0)table.appendRow(1,new SimpleDumpData("fields"),fieldDump);
			
			// Methods
			StringBuffer objMethods=new StringBuffer();
			Method[] methods=clazz.getMethods();
			DumpTable methDump = new DumpTable("#90776E","#B2A49B","#000000");
			methDump.appendRow(7,new SimpleDumpData("return"),new SimpleDumpData("interface"),new SimpleDumpData("exceptions"));
			for(int i=0;i<methods.length;i++) {
				Method method = methods[i];
				
				if(Object.class==method.getDeclaringClass()) {
					if(objMethods.length()>0)objMethods.append(", ");
					objMethods.append(method.getName());
					continue;
				}
				
				// exceptions
				StringBuffer sbExp=new StringBuffer();
				Class[] exceptions = method.getExceptionTypes();
				for(int p=0;p<exceptions.length;p++){
					if(p>0)sbExp.append("\n");
					sbExp.append(Caster.toClassName(exceptions[p]));
				}
				
				// parameters
				StringBuffer sbParams=new StringBuffer(method.getName());
				sbParams.append('(');
				Class[] parameters = method.getParameterTypes();
				for(int p=0;p<parameters.length;p++){
					if(p>0)sbParams.append(", ");
					sbParams.append(Caster.toClassName(parameters[p]));
				}
				sbParams.append(')');
				
				methDump.appendRow(0,
						new SimpleDumpData(Caster.toClassName(method.getReturnType())),

						new SimpleDumpData(sbParams.toString()),
						new SimpleDumpData(sbExp.toString())
				);
			}
			if(methods.length>0)table.appendRow(1,new SimpleDumpData("methods"),methDump);
			
			DumpTable inherited = new DumpTable("#90776E","#B2A49B","#000000");
			inherited.appendRow(7,new SimpleDumpData("Methods inherited from java.lang.Object"));
			inherited.appendRow(0,new SimpleDumpData(objMethods.toString()));
			table.appendRow(1,new SimpleDumpData(""),inherited);
			
			
			return table;
		//}
	}

	public static boolean keyValid(DumpProperties props,int level, String key) {
		if(props.getMaxlevel()-level>1) return true;
		
		// show
		Set set = props.getShow();
		if(set!=null && !set.contains(StringUtil.toLowerCase(key)))
			return false;
		
		// hide
		set = props.getHide();
		if(set!=null && set.contains(StringUtil.toLowerCase(key)))
			return false;
		
		return true;
	}
	
	public static boolean keyValid(DumpProperties props,int level, Collection.Key key) {
		if(props.getMaxlevel()-level>1) return true;
		
		// show
		Set set = props.getShow();
		if(set!=null && !set.contains(key.getLowerString()))
			return false;
		
		// hide
		set = props.getHide();
		if(set!=null && set.contains(key.getLowerString()))
			return false;
		
		return true;
	}
	
	
	

	public static DumpProperties toDumpProperties() {
		return DumpProperties.DEFAULT;
	}
}
