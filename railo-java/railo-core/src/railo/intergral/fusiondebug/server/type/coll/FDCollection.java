package railo.intergral.fusiondebug.server.type.coll;

import java.util.ArrayList;
import java.util.List;

import railo.intergral.fusiondebug.server.type.FDValueSupport;
import railo.intergral.fusiondebug.server.type.FDVariable;
import railo.intergral.fusiondebug.server.util.FDCaster;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;

import com.intergral.fusiondebug.server.FDLanguageException;
import com.intergral.fusiondebug.server.FDMutabilityException;
import com.intergral.fusiondebug.server.IFDStackFrame;

public class FDCollection extends FDValueSupport {

	private ArrayList children;
	private Collection coll;
	private String name;

	/**
	 * Constructor of the class
	 * @param frame 
	 * @param name 
	 * @param name
	 * @param coll
	 */
	public FDCollection(IFDStackFrame frame, String name, Collection coll) {
		this.coll=coll;
		Key[] keys = coll.keys();
		children=new ArrayList();
		FDCollectionNode node;
		for(int i=0;i<keys.length;i++){
			node = new FDCollectionNode(frame,coll,keys[i]);
			children.add(new FDVariable(frame,node.getName(),node));
		}
		this.name=name;
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
		return FDCaster.serialize(coll);
	}

	/**
	 * @see railo.intergral.fusiondebug.server.type.FDValueSupport#getName()
	 */
	public String getName() {
		return name;
	}
}
