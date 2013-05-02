package railo.runtime;

public abstract class PagePlus extends Page {
	
	public Object udfDefaultValue(PageContext pc, int functionIndex, int argumentIndex) {
		return udfDefaultValue(pc, functionIndex, argumentIndex, null);
	}
	
	public Object udfDefaultValue(PageContext pc, int functionIndex, int argumentIndex, Object defaultValue) {
		return null;
	}
}
