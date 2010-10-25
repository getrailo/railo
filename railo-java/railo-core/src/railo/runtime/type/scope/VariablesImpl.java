package railo.runtime.type.scope;

import railo.runtime.type.Struct;
import railo.runtime.type.UDF;

public class VariablesImpl extends ScopeSupport implements Variables {

	public VariablesImpl() {
		super("variables",SCOPE_VARIABLES,Struct.TYPE_REGULAR);
	}

	/**
	 * @see railo.runtime.type.scope.Variables#registerUDF(railo.runtime.type.Collection.Key, railo.runtime.type.UDF)
	 */
	public void registerUDF(Key key, UDF udf) {
		
		
		setEL(key, udf);
	}

	public void registerUDF(String key, UDF udf) {
		setEL(key, udf);
	}
}
