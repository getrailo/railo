package railo.runtime.com;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.dump.SimpleDumpData;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.Iteratorable;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Objects;
import railo.runtime.type.Struct;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.it.KeyAsStringIterator;
import railo.runtime.type.it.ObjectsEntryIterator;

import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

/**
 * 
 */
public final class COMObject implements Objects, Iteratorable {

	private String name;
    private Dispatch dispatch;
    private Variant parent;

    /**
	 * Public Constructor of the class
	 * @param dispatch
     * @throws ExpressionException 
	 */
	public COMObject(String dispatch) {
		//if(!SystemUtil.isWindows()) throw new ExpressionException("Com Objects are only supported in Windows Enviroments");
		this.name=dispatch;
		this.dispatch=new Dispatch(dispatch);
	}
	
	/**
	 * Private Constructor of the class for sub Objects
	 * @param parent
	 * @param dispatch
	 * @param name
	 */
	COMObject(Variant parent,Dispatch dispatch, String name) {
		this.parent=parent;
		this.name=name;
		this.dispatch=dispatch;
	}

    /*public Object get(PageContext pc, String propertyName) throws PageException {
        return COMUtil.toObject(this,Dispatch.call(dispatch,propertyName),propertyName);
    }*/

	public Object get(PageContext pc, Collection.Key key) throws PageException {
		return COMUtil.toObject(this,Dispatch.call(dispatch,key.getString()),key.getString());
	}

    /*public Object get(PageContext pc, String propertyName, Object defaultValue) {
        return COMUtil.toObject(this,Dispatch.call(dispatch,propertyName),propertyName,defaultValue);
    }*/

	@Override
    public Object get(PageContext pc, Collection.Key key, Object defaultValue) {
		return COMUtil.toObject(this,Dispatch.call(dispatch,key.getString()),key.getString(),defaultValue);
	}

    /*public Object set(PageContext pc, String propertyName, Object value) {
        return setEL(pc,propertyName,value);
    }*/

	public Object set(PageContext pc, Collection.Key propertyName, Object value) throws PageException {
		Dispatch.put(dispatch,propertyName.getString(),value);
		return value;
	}

    /*public Object setEL(PageContext pc, String propertyName, Object value) {
		Dispatch.put(dispatch,propertyName,value);
		return value;
    }*/

	@Override
	public Object setEL(PageContext pc, Collection.Key propertyName, Object value) {
		Dispatch.put(dispatch,propertyName.getString(),value);
		return value;
	}

    /*public Object call(PageContext pc, String methodName, Object[] args) throws PageException {
        Object[] arr=new Object[args.length];
		for(int i=0;i<args.length;i++) {
			if(args[i] instanceof COMObject)arr[i]=((COMObject)args[i]).dispatch;
			else arr[i]=args[i];
		}	
		return COMUtil.toObject(this,Dispatch.callN(dispatch,methodName,arr),methodName);
    }*/

	@Override
    public Object call(PageContext pc, Collection.Key key, Object[] args) throws PageException {
		String methodName=key.getString();
		Object[] arr=new Object[args.length];
		for(int i=0;i<args.length;i++) {
			if(args[i] instanceof COMObject)arr[i]=((COMObject)args[i]).dispatch;
			else arr[i]=args[i];
		}	
		return COMUtil.toObject(this,Dispatch.callN(dispatch,methodName,arr),methodName);
	}

    /*public Object callWithNamedValues(PageContext pc, String methodName, Struct args) throws PageException {
    	// TODO gibt es hier eine bessere moeglichkeit?
        Iterator<Object> it = args.valueIterator();
    	List<Object> values=new ArrayList<Object>();
        while(it.hasNext()) {
            values.add(it.next());
        }   
        return call(pc,KeyImpl.init(methodName),values.toArray(new Object[values.size()]));
    }*/

	@Override
	public Object callWithNamedValues(PageContext pc, Collection.Key key, Struct args) throws PageException {
		String methodName=key.getString();
		Iterator<Object> it = args.valueIterator();
    	List<Object> values=new ArrayList<Object>();
        while(it.hasNext()) {
            values.add(it.next());
        }   
        return call(pc,KeyImpl.init(methodName),values.toArray(new Object[values.size()]));
	}

    public boolean isInitalized() {
        return true;
    }

    @Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		DumpTable table = new DumpTable("com","#ff3300","#ff9966","#660000");
		table.appendRow(1,new SimpleDumpData("COM Object"),new SimpleDumpData(name));
		return table;
    }

    @Override
    public String castToString() throws ExpressionException {
        throw new ExpressionException("can't cast Com Object to a String");
    }

    @Override
    public String castToString(String defaultValue) {
        return defaultValue;
    }

    @Override
    public boolean castToBooleanValue() throws ExpressionException {
        throw new ExpressionException("can't cast Com Object to a boolean value");
    }
    
    @Override
    public Boolean castToBoolean(Boolean defaultValue) {
        return defaultValue;
    }

    @Override
    public double castToDoubleValue() throws ExpressionException {
        throw new ExpressionException("can't cast Com Object to a number");
    }
    
    @Override
    public double castToDoubleValue(double defaultValue) {
        return defaultValue;
    }

    @Override
    public DateTime castToDateTime() throws ExpressionException {
        throw new ExpressionException("can't cast Com Object to a Date");
    }
    
    @Override
    public DateTime castToDateTime(DateTime defaultValue) {
        return defaultValue;
    }
    
    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }
    /**
     * @return Returns the dispatch.
     */
    public Dispatch getDispatch() {
        return dispatch;
    }
    /**
     * @return Returns the parent.
     */
    public Variant getParent() {
        return parent;
    }

    /**
     * release the com Object
     */
    public void release() {
        dispatch.safeRelease();
    }

	@Override
	public int compareTo(boolean b) throws ExpressionException {
		throw new ExpressionException("can't compare Com Object with a boolean value");
	}

	@Override
	public int compareTo(DateTime dt) throws PageException {
		throw new ExpressionException("can't compare Com Object with a DateTime Object");
	}

	@Override
	public int compareTo(double d) throws PageException {
		throw new ExpressionException("can't compare Com Object with a numeric value");
	}

	@Override
	public int compareTo(String str) throws PageException {
		throw new ExpressionException("can't compare Com Object with a String");
	}
    
	public Iterator iterator() {
		return valueIterator();
	}
	
	@Override
    public Iterator<Collection.Key> keyIterator() {
        return new COMKeyWrapperIterator(this);
    }
    
    @Override
    public Iterator<String> keysAsStringIterator() {
        return new KeyAsStringIterator(keyIterator());
    }
    
    @Override
	public Iterator<Object> valueIterator() {
        return new COMValueWrapperIterator(this);
    }

	@Override
	public Iterator<Entry<Key, Object>> entryIterator() {
		return new ObjectsEntryIterator(keyIterator(), this);
	}
}