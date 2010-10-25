package railo.runtime.type.scope;

import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.type.Collection;
import railo.runtime.type.StructImpl;
import railo.runtime.type.dt.DateTimeImpl;

/**
 * client scope that not store it's data
 */
public final class ClientMemory extends ClientSupport {

	
	/**
	 * Constructor of the class
	 * @param pc
	 * @param name
	 */
	private ClientMemory(PageContext pc) {
		super(
				new StructImpl(),
				new DateTimeImpl(pc.getConfig()),
				null,
				-1,1);
	}

	/**
	 * Constructor of the class, clone existing
	 * @param other
	 */
	private ClientMemory(ClientMemory other,boolean deepCopy) {
		super(other,deepCopy);
	}
	
	
	/**
	 * load a new instance of the class
	 * @param pc
	 * @return
	 */
	public static Client getInstance(PageContext pc) {
		return new ClientMemory(pc);
	}
	
	/**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		DumpTable table = super.toDumpTable(pageContext, maxlevel,dp);
		table.setTitle("Scope Client (Memory)");
		return table;
	}
	
	/**
	 *
	 * @see railo.runtime.type.Collection#duplicate(boolean)
	 */
	public Collection duplicate(boolean deepCopy) {
    	return new ClientMemory(this,deepCopy);
	}
	
	

}
