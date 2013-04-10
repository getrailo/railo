package railo.runtime.functions;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.reflection.Reflector;

public class BIFProxy extends BIF {

	private Class clazz;

	public BIFProxy(Class clazz) {
		this.clazz=clazz;
	}

	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		Object[] _args=new Object[args.length+1];
		_args[0]=pc;
		for(int i=0;i<args.length;i++){
			_args[i+1]=args[i];
		}
		return Reflector.callStaticMethod(clazz,"call",_args);
	}

}
