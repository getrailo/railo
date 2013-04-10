package railo.commons.lang;

public abstract class ExtendableClassLoader extends ClassLoader {
	
	public ExtendableClassLoader() {
		super();
	}

	public ExtendableClassLoader(ClassLoader parent) {
		super(parent);
	}

	/**
	 * allow to define a new Class with help of the bytecode passed to the method
	 * @param name
	 * @param barr
	 * @return
	 */
	public abstract Class<?> loadClass(String name, byte[] barr);
}
