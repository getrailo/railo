package railo.transformer.bytecode.reflection;

public abstract class ASMField {

	private String name;
	
	public String getName(){
		return name;
	}

	public abstract Object get(Object obj);
	
	public abstract void set(Object obj, Object value);
}
