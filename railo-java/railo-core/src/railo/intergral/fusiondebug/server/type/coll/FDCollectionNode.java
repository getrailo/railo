package railo.intergral.fusiondebug.server.type.coll;

import railo.intergral.fusiondebug.server.type.FDNodeValueSupport;
import railo.intergral.fusiondebug.server.util.FDCaster;
import railo.runtime.type.Array;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;

import com.intergral.fusiondebug.server.FDMutabilityException;
import com.intergral.fusiondebug.server.IFDStackFrame;

public class FDCollectionNode extends FDNodeValueSupport {

	private Collection coll;
	private Key key;

	/**
	 * Constructor of the class
	 * @param coll
	 * @param key
	 */
	public FDCollectionNode(IFDStackFrame frame,Collection coll, Key key) {
		super(frame);
		this.coll=coll;
		this.key=key;
	}

	@Override
	public String getName() {
		if(coll instanceof Array) return "["+key.getString()+"]";
		return key.getString();
	}

	@Override
	protected Object getRawValue() {
		return coll.get(key,null);
	}

	public boolean isMutable() {
		return true;
	}
	
	public void set(String value) throws FDMutabilityException {
		coll.setEL(key, FDCaster.unserialize(value));
	}
}
