package railo;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import railo.commons.io.IOUtil;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.QueryImpl;

/**
 *  
 */
public class aprint {

	

	public static void date(String value) {
		long millis=System.currentTimeMillis();
    	o(
    			new Date(millis)
    			+"-"
    			+(millis-(millis/1000*1000))
    			+" "+value);
	}

	public static void ds() {
		Thread.dumpStack();
	}
	
	public static void ds(Object label) {
		err(label);
		Thread.dumpStack();
	}
	
	public static void dumpStack() {
		Thread.dumpStack();
	}
	
	public static void dumpStack(String label) {
		err(label);
		Thread.dumpStack();
	}
    
    public static void err(boolean o) { 
		System.err.println(o);
	}
    public static void err(double d) {
		System.err.println(d);
	}
    public static void err(long d) {
		System.err.println(d);
	}
    public static void err(float d) {
		System.err.println(d);
	}
    public static void err(int d) {
		System.err.println(d);
	}
    public static void err(short d) {
		System.err.println(d);
	}

    public static void out(Object o1,Object o2,Object o3) {
		System.out.print(o1);
		System.out.print(o2);
		System.out.println(o3);
	}
	
	public static void out(Object o1,Object o2) {
		System.out.print(o1);
		System.out.println(o2);
	}

	public static void out(Object o,long l) {
		System.out.print(o);
		System.out.println(l);
	}
	
	public static void out(Object o,double d) {
		System.out.print(o);
		System.out.println(d);
	}
    
    
    public static void out(byte[] arr, int offset, int len) {
        System.out.print("byte[]{");
        for(int i=offset;i<len+offset;i++) {
            if(i>0)System.out.print(',');
            System.out.print(arr[i]);
        }
        System.out.println("}");
    }
    
    

    

	
	public static void out(double o) {
		System.out.println(o);
	}
	
	public static void out(float o) {
		System.out.println(o);
	}
	public static void out(long o) {
		System.out.println(o);
	}
	public static void out(int o) {
		System.out.println(o);
	}
	public static void out(char o) {
		System.out.println(o);
	}
	public static void out(boolean o) {
		System.out.println(o);
	}
	public static void out() {
		System.out.println();
	}

	public static void printST(Throwable t) {
		if(t instanceof InvocationTargetException){
			t=((InvocationTargetException)t).getTargetException();
		}
		err(t.getClass().getName());
		t.printStackTrace();
		
	}
	
	public static void printST(Throwable t,PrintStream ps) {
		if(t instanceof InvocationTargetException){
			t=((InvocationTargetException)t).getTargetException();
		}
		err(t.getClass().getName());
		t.printStackTrace(ps);
		
	}
	
	

	public static void out(Object o) {
		_(System.out, o);
	}
	public static void err(Object o) {
		_(System.err, o);
	}
	

	public static void o(Object o) {
		_(System.out, o);
	}
	public static void e(Object o) {
		_(System.err, o);
	}
	public static void oe(Object o, boolean valid) {
		_(valid?System.out:System.err, o);
	}
	
	public static void dateO(String value) {
		_date(System.out, value);
	}
	
	public static void dateE(String value) {
		_date(System.err, value);
	}

	private static void _date(PrintStream ps,String value) {
		long millis = System.currentTimeMillis();
		_(ps,
		new Date(millis)
		+"-"
		+(millis-(millis/1000*1000))
		+" "+value);
	}
	
	
	
	private static void _(PrintStream ps,Object o) {
    	if(o instanceof Enumeration) _(ps,(Enumeration)o);
    	if(o instanceof Object[]) _(ps,(Object[])o);
    	else if(o instanceof boolean[]) _(ps,(boolean[])o);
    	else if(o instanceof byte[]) _(ps,(byte[])o);
    	else if(o instanceof int[]) _(ps,(int[])o);
    	else if(o instanceof float[]) _(ps,(float[])o);
    	else if(o instanceof long[]) _(ps,(long[])o);
    	else if(o instanceof double[]) _(ps,(double[])o);
    	else if(o instanceof char[]) _(ps,(char[])o);
    	else if(o instanceof short[]) _(ps,(short[])o);
    	else if(o instanceof Set) _(ps,(Set)o);
    	else if(o instanceof List) _(ps,(List)o);
    	else if(o instanceof Map) _(ps,(Map)o);
    	else if(o instanceof Iterator) _(ps,(Iterator)o);
    	else if(o instanceof NamedNodeMap) _(ps,(NamedNodeMap)o);
    	else if(o instanceof ResultSet) _(ps,(ResultSet)o);
    	else if(o instanceof Node) _(ps,(Node)o);
    	else if(o instanceof Throwable) _(ps,(Throwable)o);
    	else if(o instanceof Cookie) {
    		Cookie c=(Cookie) o;
    		ps.println("Cookie(name:"+c.getName()+";domain:"+c.getDomain()+";maxage:"+c.getMaxAge()+";path:"+c.getPath()+";value:"+c.getValue()+";version:"+c.getVersion()+";secure:"+c.getSecure()+")");
    	}
    	else if(o instanceof InputSource) {
    		InputSource is=(InputSource) o;
    		Reader r = is.getCharacterStream();
    		try {
				ps.println(IOUtil.toString(is.getCharacterStream()));
			} catch (IOException e) {}
			finally {
				IOUtil.closeEL(r);
			}
    	}
    	
    	else ps.println(o);
    }
	
	
	
	
	private static void _(PrintStream ps,Object[] arr) {
    	if(arr==null){
    		ps.println("null");
    		return;
    	}
        ps.print(arr.getClass().getComponentType().getName()+"[]{");
        for(int i=0;i<arr.length;i++) {
            if(i>0) {
                ps.print("\t,");
            }
            _(ps,arr[i]);
        }
        ps.println("}");
    }
    
    private static void _(PrintStream ps,int[] arr) {
        ps.print("int[]{");
        for(int i=0;i<arr.length;i++) {
            if(i>0)ps.print(',');
            ps.print(arr[i]);
        }
        ps.println("}");
    }
    
    private static void _(PrintStream ps,byte[] arr) {
        ps.print("byte[]{");
        for(int i=0;i<arr.length;i++) {
            if(i>0)ps.print(',');
            ps.print(arr[i]);
        }
        ps.println("}");
    }
    
    private static void _(PrintStream ps,boolean[] arr) {
        ps.print("boolean[]{");
        for(int i=0;i<arr.length;i++) {
            if(i>0)ps.print(',');
            ps.print(arr[i]);
        }
        ps.println("}");
    }
    
    private static void _(PrintStream ps,char[] arr) {
        ps.print("char[]{");
        for(int i=0;i<arr.length;i++) {
            if(i>0)ps.print(',');
            ps.print(arr[i]);
        }
        ps.println("}");
    }
    
    private static void _(PrintStream ps,short[] arr) {
        ps.print("short[]{");
        for(int i=0;i<arr.length;i++) {
            if(i>0)ps.print(',');
            ps.print(arr[i]);
        }
        ps.println("}");
    }
    
    private static void _(PrintStream ps,float[] arr) {
        ps.print("float[]{");
        for(int i=0;i<arr.length;i++) {
            if(i>0)ps.print(',');
            ps.print(arr[i]);
        }
        ps.println("}");
    }
    
    private static void _(PrintStream ps,long[] arr) {
        ps.print("long[]{");
        for(int i=0;i<arr.length;i++) {
            if(i>0)ps.print(',');
            ps.print(arr[i]);
        }
        ps.println("}");
    }
    
    private static void _(PrintStream ps,double[] arr) {
        ps.print("double[]{");
        for(int i=0;i<arr.length;i++) {
            if(i>0)ps.print(',');
            ps.print(arr[i]);
        }
        ps.println("}");
    }
    

	private static void _(PrintStream ps,Node n) {
		ps.print(Caster.toString(n,null));
	}
	
	
	private static void _(PrintStream ps,Throwable t) {
    	t.printStackTrace(ps);
    }
    

    private static void _(PrintStream ps,Enumeration en) {
        
    	_(ps,en.getClass().getName()+" [");
        while(en.hasMoreElements()) {
        	ps.print(en.nextElement());
            ps.println(",");
        }
        _(ps,"]");
    }
    
    private static void _(PrintStream ps,List list) {
        ListIterator it = list.listIterator();
        _(ps,list.getClass().getName()+" {");
        while(it.hasNext()) {
            int index = it.nextIndex();
            it.next();
            ps.print(index);
            ps.print(":");
            ps.print(list.get(index));
            ps.println(";");
        }
        _(ps,"}");
    }
    
    private static void _(PrintStream ps,Iterator it) {
        
        _(ps,it.getClass().getName()+" {");
        while(it.hasNext()) {
            ps.print(it.next());
            ps.println(";");
        }
        _(ps,"}");
    }
    
    
    private static void _(PrintStream ps,Set set) {
    	Iterator it = set.iterator();
    	ps.println(set.getClass().getName()+" {");
        while(it.hasNext()) {
        	_(ps,it.next());
            ps.println(",");
        }
        _(ps,"}");
    }
    
    private static void _(PrintStream ps,ResultSet res) {
    	try {
			_(ps, new QueryImpl(res,"query").toString());
		} catch (PageException e) {
			_(ps, res.toString());
		}
    }

    private static void _(PrintStream ps,Map map) {
    	if(map==null) {
    		ps.println("null");
    		return;
    	}
        Iterator it = map.keySet().iterator();
        
        if(map.size()<2) {
        	ps.print(map.getClass().getName()+" {");
            while(it.hasNext()) {
                Object key = it.next();

                ps.print(key);
                ps.print(":");
                ps.print(map.get(key));
            }
            ps.println("}");
        } 
        else {
	        ps.println(map.getClass().getName()+" {");
	        while(it.hasNext()) {
	            Object key = it.next();
	            ps.print("	");
	            ps.print(key);
	            ps.print(":");
	            ps.print(map.get(key));
	            ps.println(";");
	        }
	        ps.println("}");
        }
    }

    private static void _(PrintStream ps,NamedNodeMap map) {
    	if(map==null) {
    		ps.println("null");
    		return;
    	}
        int len = map.getLength();
        ps.print(map.getClass().getName()+" {");
        Attr attr;
        for(int i=0;i<len;i++) {
        	attr=(Attr)map.item(i);

        	ps.print(attr.getName());
        	ps.print(":");
        	ps.print(attr.getValue());
            ps.println(";");
        }
        ps.println("}");
    }
    
    

}