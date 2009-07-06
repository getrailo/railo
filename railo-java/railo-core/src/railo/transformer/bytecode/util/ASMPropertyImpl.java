package railo.transformer.bytecode.util;

import org.objectweb.asm.Type;

import railo.runtime.exp.PageException;

public final class ASMPropertyImpl implements ASMProperty {


	private Type type;
	private String name;
	
	public ASMPropertyImpl(Class type,String name){
		this.type=ASMUtil.toType(type, true);
		this.name=name;
	}
	public ASMPropertyImpl(String type,String name) throws PageException{
		this.type=ASMUtil.toType(type, true);
		this.name=name;
	}
	public ASMPropertyImpl(Type type,String name){
		this.type=type;
		this.name=name;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the type
	 */
	public Type getASMType() {
		return type;
	}
	
	/**
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "name:"+name+";type:"+type.getClassName();
	}

}