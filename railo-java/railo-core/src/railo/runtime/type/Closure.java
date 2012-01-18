package railo.runtime.type;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import railo.runtime.ComponentImpl;
import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.exp.PageException;
import railo.runtime.functions.other.CreateUniqueId;
import railo.runtime.type.util.ComponentUtil;

public class Closure extends UDFImpl {
	
	
	public Closure(){
		super();
	}
	
	public Closure(UDFProperties properties) {
		super(properties);
	}

	/* (non-Javadoc)
	 * @see railo.runtime.type.UDFImpl#duplicate(railo.runtime.ComponentImpl)
	 */
	@Override
	public UDF duplicate(ComponentImpl c) {
		// TODO Auto-generated method stub
		return super.duplicate(c);
	}

	/* (non-Javadoc)
	 * @see railo.runtime.type.UDFImpl#callWithNamedValues(railo.runtime.PageContext, railo.runtime.type.Struct, boolean)
	 */
	@Override
	public Object callWithNamedValues(PageContext pc, Struct values, boolean doIncludePath) throws PageException {
		// TODO Auto-generated method stub
		return super.callWithNamedValues(pc, values, doIncludePath);
	}

	/* (non-Javadoc)
	 * @see railo.runtime.type.UDFImpl#call(railo.runtime.PageContext, java.lang.Object[], boolean)
	 */
	@Override
	public Object call(PageContext pc, Object[] args, boolean doIncludePath) throws PageException {
		// TODO Auto-generated method stub
		return super.call(pc, args, doIncludePath);
	}

	/* (non-Javadoc)
	 * @see railo.runtime.type.UDFImpl#toDumpData(railo.runtime.PageContext, int, railo.runtime.dump.DumpProperties)
	 */
	@Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel,DumpProperties dp) {
		return toDumpData(pageContext, maxlevel, dp,this,true);
	}

	/* (non-Javadoc)
	 * @see railo.runtime.type.UDFImpl#getMetaData(railo.runtime.PageContext)
	 */
	@Override
	public Struct getMetaData(PageContext pc) throws PageException {
		Struct meta = ComponentUtil.getMetaData(pc, properties);
		meta.setEL(KeyImpl.CLOSURE, Boolean.TRUE);// MUST move this to class UDFProperties
		meta.setEL("ANONYMOUSCLOSURE", Boolean.TRUE);// MUST move this to class UDFProperties
		
		return meta;
	}

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		// access
		setAccess(in.readInt());
		
		// properties
		properties=(UDFPropertiesImpl) in.readObject();
	}


	public void writeExternal(ObjectOutput out) throws IOException {
		// access
		out.writeInt(getAccess());
		
		// properties
		out.writeObject(properties);
		
		
	}
}
