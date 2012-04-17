package railo.runtime.rest.path;


import railo.runtime.type.Struct;

public class LiteralPath extends Path {
	
	private String path;

	public LiteralPath(String path){
		this.path=path;
	}
	
	@Override
	public boolean match(Struct variables, String path) {
		return this.path.equals(path);
	}
	
	public String toString(){
		return "literal:"+path;
	}
}
