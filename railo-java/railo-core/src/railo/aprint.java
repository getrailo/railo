package railo;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;

import org.xml.sax.InputSource;

import railo.commons.io.IOUtil;

/**
 *  
 */
public class aprint {

	public static void dumpStack() {
		Thread.dumpStack();
	}
	
	public static void dumpStack(String label) {
		err(label);
		Thread.dumpStack();
	}
    public static void err(Object o) {
		System.err.println(o);
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
    
    public static void out(Object[] arr) {
    	if(arr==null){
    		System.out.println("null");
    		return;
    	}
        System.out.print(arr.getClass().getComponentType().getName()+"[]{");
        for(int i=0;i<arr.length;i++) {
            if(i>0) {
                System.out.print("\t,");
            }
            out(arr[i]);
        }
        System.out.println("}");
    }
    
    public static void out(int[] arr) {
        System.out.print("int[]{");
        for(int i=0;i<arr.length;i++) {
            if(i>0)System.out.print(',');
            System.out.print(arr[i]);
        }
        System.out.println("}");
    }
    
    public static void out(byte[] arr) {
        System.out.print("byte[]{");
        for(int i=0;i<arr.length;i++) {
            if(i>0)System.out.print(',');
            System.out.print(arr[i]);
        }
        System.out.println("}");
    }
    
    public static void out(boolean[] arr) {
        System.out.print("boolean[]{");
        for(int i=0;i<arr.length;i++) {
            if(i>0)System.out.print(',');
            System.out.print(arr[i]);
        }
        System.out.println("}");
    }
    
    public static void out(char[] arr) {
        System.out.print("char[]{");
        for(int i=0;i<arr.length;i++) {
            if(i>0)System.out.print(',');
            System.out.print(arr[i]);
        }
        System.out.println("}");
    }
    
    public static void out(float[] arr) {
        System.out.print("float[]{");
        for(int i=0;i<arr.length;i++) {
            if(i>0)System.out.print(',');
            System.out.print(arr[i]);
        }
        System.out.println("}");
    }
    
    public static void out(long[] arr) {
        System.out.print("long[]{");
        for(int i=0;i<arr.length;i++) {
            if(i>0)System.out.print(',');
            System.out.print(arr[i]);
        }
        System.out.println("}");
    }
    
    public static void out(double[] arr) {
        System.out.print("double[]{");
        for(int i=0;i<arr.length;i++) {
            if(i>0)System.out.print(',');
            System.out.print(arr[i]);
        }
        System.out.println("}");
    }
    
    public static void out(byte[] arr, int offset, int len) {
        System.out.print("byte[]{");
        for(int i=offset;i<len+offset;i++) {
            if(i>0)System.out.print(',');
            System.out.print(arr[i]);
        }
        System.out.println("}");
    }
    
    public static void out(Object o) {
    	if(o instanceof Enumeration) out((Enumeration)o);
    	if(o instanceof Object[]) out((Object[])o);
    	else if(o instanceof boolean[]) out((boolean[])o);
    	else if(o instanceof byte[]) out((byte[])o);
    	else if(o instanceof int[]) out((int[])o);
    	else if(o instanceof float[]) out((float[])o);
    	else if(o instanceof long[]) out((long[])o);
    	else if(o instanceof double[]) out((double[])o);
    	else if(o instanceof Set) out((Set)o);
    	else if(o instanceof List) out((List)o);
    	else if(o instanceof Map) out((Map)o);
    	else if(o instanceof Throwable) out((Throwable)o);
    	else if(o instanceof Cookie) {
    		Cookie c=(Cookie) o;
    		System.out.println("Cookie(name:"+c.getName()+";domain:"+c.getDomain()+";maxage:"+c.getMaxAge()+";path:"+c.getPath()+";value:"+c.getValue()+";version:"+c.getVersion()+";secure:"+c.getSecure()+")");
    	}
    	else if(o instanceof InputSource) {
    		InputSource is=(InputSource) o;
    		Reader r = is.getCharacterStream();
    		try {
				System.out.println(IOUtil.toString(is.getCharacterStream()));
			} catch (IOException e) {}
			finally {
				IOUtil.closeEL(r);
			}
    	}
    	
    	else System.out.println(o);
    }

    private static void out(Throwable t) {
    	t.printStackTrace(System.out);
    }
    

    private static void out(Enumeration en) {
        
    	out(en.getClass().getName()+" [");
        while(en.hasMoreElements()) {
        	System.out.print(en.nextElement());
            System.out.println(",");
        }
        out("]");
    }
    
    private static void out(List list) {
        ListIterator it = list.listIterator();
        out(list.getClass().getName()+" {");
        while(it.hasNext()) {
            int index = it.nextIndex();
            it.next();
            System.out.print(index);
            System.out.print(":");
            System.out.print(list.get(index));
            System.out.println(";");
        }
        out("}");
    }
    
    
    private static void out(Set set) {
    	Iterator it = set.iterator();
    	out(set.getClass().getName()+" {");
        while(it.hasNext()) {
            out(it.next());
            System.out.println(",");
        }
        out("}");
    }

    private static void out(Map map) {
    	if(map==null) {
    		out("null");
    		return;
    	}
        Iterator it = map.keySet().iterator();
        out("java.util.Map {");
        while(it.hasNext()) {
            Object key = it.next();

            System.out.print(key);
            System.out.print(":");
            System.out.print(map.get(key));
            System.out.println(";");
        }
        out("}");
    }
    
	public static void out(Object o,long l) {
		System.out.print(o);
		System.out.println(l);
	}
	
	public static void out(Object o,double d) {
		System.out.print(o);
		System.out.println(d);
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
}