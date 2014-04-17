package railo.runtime.type;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;

public class UDFCustomType implements CustomType {

	private UDF udf;

	public UDFCustomType(UDF udf) {
		this.udf=udf;
	}

	@Override
	public Object convert(PageContext pc, Object o) throws PageException {
		return udf.call(pc, new Object[]{o}, false);
	}

	@Override
	public Object convert(PageContext pc, Object o, Object defaultValue) {
		try {
			return udf.call(pc, new Object[]{o}, false);
		}
		catch (Throwable t) {
			return defaultValue;
		}
	}

}
