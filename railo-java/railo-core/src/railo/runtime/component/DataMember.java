package railo.runtime.component;

import railo.runtime.op.Duplicator;


public final class DataMember extends MemberSupport {
	private Object value;

	public DataMember(int access, Object value) { 
		super(access);
		this.value=value;
	}

	@Override
	public Object getValue() {
		return value;
	}

	public Object duplicate(boolean deepCopy) {
		return new DataMember(getAccess(),Duplicator.duplicate(value, deepCopy));
	}
}
