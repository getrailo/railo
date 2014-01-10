package railo.transformer.bytecode.statement;

public interface FlowControl {

	public static final int BREAK=1;
	public static final int CONTINUE=2;
	public static final int RETRY=4;
	
	public String getLabel();
}
