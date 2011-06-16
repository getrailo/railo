package railo.intergral.fusiondebug.server.type.coll;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import railo.intergral.fusiondebug.server.type.FDValueSupport;
import railo.intergral.fusiondebug.server.type.FDVariable;
import railo.intergral.fusiondebug.server.util.FDCaster;
import railo.runtime.Component;
import railo.runtime.ComponentPro;
import railo.runtime.type.Array;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.Struct;
import railo.runtime.type.comparator.TextComparator;

import com.intergral.fusiondebug.server.FDLanguageException;
import com.intergral.fusiondebug.server.FDMutabilityException;
import com.intergral.fusiondebug.server.IFDStackFrame;

public class FDCollection extends FDValueSupport {

	private static final int INTERVALL = 10;
	private ArrayList children;
	private Collection coll;
	private String name;
	private Key[] keys;

	/**
	 * Constructor of the class
	 * @param frame 
	 * @param name 
	 * @param name
	 * @param coll
	 */
	

	public FDCollection(IFDStackFrame frame, String name, Collection coll) {
		this(frame,name,coll,keys(coll));
	}

	public FDCollection(IFDStackFrame frame, String name, Collection coll, Key[] keys) {
		
		this.name=name;
		this.coll=coll;
		this.keys=keys;
		//Key[] keys = coll.keys();
		children=new ArrayList();
		
		int intervall=INTERVALL;
		while(intervall*intervall<keys.length)
			intervall*=intervall;
		
		if(keys.length>intervall){
			FDCollection node;
			
			
			int len=keys.length;
			
			int max;
			for(int i=0;i<len;i+=intervall)	{
				max=(i+(intervall))<len?(intervall):len-i;
				Key[] skeys=new Key[max];
				for(int y=0;y<max;y++)	{
					skeys[y]=keys[i+y];
				}				
				node = new FDCollection(frame,"Rows",coll,skeys);
				children.add(new FDVariable(frame,node.getName(),node));
			}
		}
		else {
			FDCollectionNode node;
			for(int i=0;i<keys.length;i++){
				node = new FDCollectionNode(frame,coll,keys[i]);
				children.add(new FDVariable(frame,node.getName(),node));
			}
		}
	}
	
	private static Key[] keys(Collection coll) {
		Key[] keys=coll.keys();
		if(coll instanceof Array) return keys;
		TextComparator comp=new TextComparator(true,true);
		Arrays.sort(keys,comp);
		return keys;
	}

	/**
	 * @see com.intergral.fusiondebug.server.IFDVariable#getChildren()
	 */
	public List getChildren() {
		return children;
	}

 
	public IFDStackFrame getStackFrame() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.intergral.fusiondebug.server.IFDValue#isMutable()
	 */
	public boolean isMutable() {
		return false;
	}

	public void set(String arg0) throws FDMutabilityException,FDLanguageException {
		throw new FDMutabilityException();
	}

	/**
	 * @see com.intergral.fusiondebug.server.IFDValue#hasChildren()
	 */
	public boolean hasChildren() {
		return true;
	}
	

	/* *
	 * @see java.lang.Object#toString()
	 * /
	public String toString() {
		return "<CFML "+StringUtil.ucFirst(Caster.toTypeName(coll))+">";
	}*/
	

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if(coll instanceof Array)
			return "["+fromto()+"]";
		if(coll instanceof Component){
			ComponentPro c=(ComponentPro) coll;
			return "Component "+c.getName()+"("+c.getPageSource().getDisplayPath()+")";
		}
		if(coll instanceof Struct)
			return "{"+fromto()+"}";
		return FDCaster.serialize(coll);
	}
	

	private String fromto() {
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<keys.length;i++){
			if(i!=0)sb.append(",");
			sb.append(keys[i].toString());
		}
		return keys[0]+" ... "+keys[keys.length-1];
	}


	/**
	 * @see railo.intergral.fusiondebug.server.type.FDValueSupport#getName()
	 */
	public String getName() {
		return name;
	}
}
