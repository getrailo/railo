package railo.runtime.net.rpc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

import railo.print;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.op.Caster;
import railo.runtime.reflection.Reflector;
import railo.runtime.reflection.pairs.MethodInstance;

public class PojoIterator implements Iterator<Object> {
	
	private static final Object[] EMPTY_ARG = new Object[]{}; 
	
	private Pojo pojo;
	private Method[] getters;
	private Class<? extends Pojo> clazz;
	private int index=-1;

	public PojoIterator(Pojo pojo) {
		this.pojo=pojo;
		this.clazz=pojo.getClass();
		getters = Reflector.getGetters(pojo.getClass());
		print.e(getters);
	}

	@Override
	public boolean hasNext() {
		return (index+1)<getters.length;
	}

	@Override
	public Object next() {
		Method g = getters[++index];
		try {
			return g.invoke(pojo, EMPTY_ARG);
		}
		catch (Throwable t) {
			throw new PageRuntimeException(Caster.toPageException(t));
		}
	}

	@Override
	public void remove() {
		throw new RuntimeException("method remove is not supported!");
	}

}
