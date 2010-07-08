package railo.transformer.bytecode.expression.var;


public abstract class FunctionMember implements Member{
	private Argument[] arguments=new Argument[0];
	private boolean _hasNamedArgs;

	public void addArgument(Argument argument) {
		if(argument instanceof NamedArgument)_hasNamedArgs=true;
		Argument[] tmp=new Argument[arguments.length+1];
		for(int i=0;i<arguments.length;i++){
			tmp[i]=arguments[i];
		}
		tmp[arguments.length]=argument;
		arguments=tmp;
	}

	/**
	 * @return the arguments
	 */
	public Argument[] getArguments() {
		return arguments;
	}
	public void setArguments(Argument[] arguments) {
		this.arguments= arguments;
	}
	

	public boolean hasNamedArgs() {
		return _hasNamedArgs;
	}

}
