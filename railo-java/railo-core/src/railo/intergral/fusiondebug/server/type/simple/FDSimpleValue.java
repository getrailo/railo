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
	
	@Override
	public String toString() {
		return str;
	}

	@Override
	public List getChildren() {
		return children;
	}

	@Override
	public boolean hasChildren() {
		return children!=null;
	}
}