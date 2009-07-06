package railo.intergral.fusiondebug.server.type.simple;

import java.util.List;

import railo.intergral.fusiondebug.server.type.FDValueNotMutability;

public class FDSimpleValue extends FDValueNotMutability {
	
	private String str;
	private List children;

	public FDSimpleValue(List children,String str){
		this.children=children;
		this.str=str;
		
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return str;
	}

	/**
	 * @see com.intergral.fusiondebug.server.IFDValue#getChildren()
	 */
	public List getChildren() {
		return children;
	}

	/**
	 * @see com.intergral.fusiondebug.server.IFDValue#hasChildren()
	 */
	public boolean hasChildren() {
		return children!=null;
	}
}