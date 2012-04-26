package railo.runtime.com;

import java.util.Iterator;

import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.dump.SimpleDumpData;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.type.Collection;
import railo.runtime.type.Iteratorable;
import railo.runtime.type.Objects;
import railo.runtime.type.Struct;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.it.KeyAsStringIterator;

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

    /**
     * @see railo.runtime.type.ContextCollection#get(railo.runtime.PageContext, java.lang.String)
     */
    public Object get(PageContext pc, String propertyName) throws PageException {
        return COMUtil.toObject(this,Dispatch.call(dispatch,propertyName),propertyName);
    }

	public Object get(PageContext pc, Collection.Key key) throws PageException {
		return get(pc, key.getString());
	}

    /**
     * @see railo.runtime.type.Objects#get(railo.runtime.PageContext, java.lang.String, java.lang.Object)
     */
    public Object get(PageContext pc, String propertyName, Object defaultValue) {
        return COMUtil.toObject(this,Dispatch.call(dispatch,propertyName),propertyName,defaultValue);
    }

	public Object get(PageContext pc, Collection.Key key, Object defaultValue) {
		return get(pc, key.getString(), defaultValue);
	}

    /**
     * @see railo.runtime.type.Objects#set(railo.runtime.PageContext, java.lang.String, java.lang.Object)
     */
    public Object set(PageContext pc, String propertyName, Object value) {
        return setEL(pc,propertyName,value);
    }

	public Object set(PageContext pc, Collection.Key propertyName, Object value) throws PageException {
		return setEL(pc,propertyName.toString(),value);
	}

    /**
     * @see railo.runtime.type.Objects#setEL(railo.runtime.PageContext, java.lang.String, java.lang.Object)
     */
    public Object setEL(PageContext pc, String propertyName, Object value) {
		Dispatch.put(dispatch,propertyName,value);
		return value;
    }

	/**
	 *
	 * @see railo.runtime.type.Objects#setEL(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(PageContext pc, Collection.Key propertyName, Object value) {
		return setEL(pc, propertyName.toString(), value);
	}

    /**
     * @see railo.runtime.type.Objects#call(railo.runtime.PageContext, java.lang.String, java.lang.Object[])
     */
    public Object call(PageContext pc, String methodName, Object[] args) throws PageException {
        Object[] arr=new Object[args.length];
		for(int i=0;i<args.length;i++) {
			if(args[i] instanceof COMObject)arr[i]=((COMObject)args[i]).dispatch;
			else arr[i]=args[i];
		}	
		return COMUtil.toObject(this,Dispatch.callN(dispatch,methodName,arr),methodName);
    }

	public Object call(PageContext pc, Collection.Key methodName, Object[] arguments) throws PageException {
		return call(pc, methodName.getString(), arguments) ;
	}

    /**
     * @see railo.runtime.type.Objects#callWithNamedValues(railo.runtime.PageContext, java.lang.String, railo.runtime.type.Struct)
     */
    public Object callWithNamedValues(PageContext pc, String methodName, Struct args) throws PageException {
//      TODO gibt es hier eine bessere möglichkeit?
        Collection.Key[] keys = args.keys();
        Object[] values=new Object[keys.length];
        for(int i=0;i<keys.length;i++) {
            values[i]=args.get(keys[i],null);
        }   
        return call(pc,methodName,values);
    }

	/**
	 *
	 * @see railo.runtime.type.Objects#callWithNamedValues(railo.runtime.PageContext, railo.runtime.type.Collection.Key, railo.runtime.type.Struct)
	 */
	public Object callWithNamedValues(PageContext pc, Collection.Key methodName, Struct args) throws PageException {
		return callWithNamedValues(pc, methodName.getString(), args);
	}

    /* *
     * @see railo.runtime.reflection.wrapper.ObjectWrapper#getEmbededObject()
     * /
    public Object getEmbededObject() throws PageException {
        return dispatch;
    }*/

    /* *
     * @see railo.runtime.reflection.wrapper.ObjectWrapper#getEmbededObjectEL()
     * /
    public Object getEmbededObjectEL() {
        return dispatch;
    }*/

    /**
     * @see railo.runtime.type.Objects#isInitalized()
     */
    public boolean isInitalized() {
        return true;
    }

    /**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		DumpTable table = new DumpTable("com","#ff3300","#ff9966","#660000");
		table.appendRow(1,new SimpleDumpData("COM Object"),new SimpleDumpData(name));
		return table;
    }

    /**
     * @see railo.runtime.op.Castable#castToString()
     */
    public String castToString() throws ExpressionException {
        throw new ExpressionException("can't cast Com Object to a String");
    }

    /**
     * @see railo.runtime.op.Castable#castToString(java.lang.String)
     */
    public String castToString(String defaultValue) {
        return defaultValue;
    }

    /**
     * @see railo.runtime.op.Castable#castToBooleanValue()
     */
    public boolean castToBooleanValue() throws ExpressionException {
        throw new ExpressionException("can't cast Com Object to a boolean value");
    }
    
    /**
     * @see railo.runtime.op.Castable#castToBoolean(java.lang.Boolean)
     */
    public Boolean castToBoolean(Boolean defaultValue) {
        return defaultValue;
    }

    /**
     * @see railo.runtime.op.Castable#castToDoubleValue()
     */
    public double castToDoubleValue() throws ExpressionException {
        throw new ExpressionException("can't cast Com Object to a number");
    }
    
    /**
     * @see railo.runtime.op.Castable#castToDoubleValue(double)
     */
    public double castToDoubleValue(double defaultValue) {
        return defaultValue;
    }

    /**
     * @see railo.runtime.op.Castable#castToDateTime()
     */
    public DateTime castToDateTime() throws ExpressionException {
        throw new ExpressionException("can't cast Com Object to a Date");
    }
    
    /**
     * @see railo.runtime.op.Castable#castToDateTime(railo.runtime.type.dt.DateTime)
     */
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

	/**
	 * @see railo.runtime.op.Castable#compare(boolean)
	 */
	public int compareTo(boolean b) throws ExpressionException {
		throw new ExpressionException("can't compare Com Object with a boolean value");
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(railo.runtime.type.dt.DateTime)
	 */
	public int compareTo(DateTime dt) throws PageException {
		throw new ExpressionException("can't compare Com Object with a DateTime Object");
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(double)
	 */
	public int compareTo(double d) throws PageException {
		throw new ExpressionException("can't compare Com Object with a numeric value");
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(java.lang.String)
	 */
	public int compareTo(String str) throws PageException {
		throw new ExpressionException("can't compare Com Object with a String");
	}
    
	/**
	 * @see railo.runtime.type.Iteratorable#iterator()
	 */
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
    
    

    /**
     * @see railo.runtime.type.Iteratorable#valueIterator()
     */
    public Iterator valueIterator() {
        return new COMValueWrapperIterator(this);
    }
}