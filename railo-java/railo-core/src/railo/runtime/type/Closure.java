package railo.runtime.type;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import railo.runtime.ComponentImpl;
import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.PageException;
import railo.runtime.type.scope.ClosureScope;
import railo.runtime.type.scope.Variables;
import railo.runtime.type.util.ComponentUtil;
import railo.runtime.type.util.KeyConstants;
import railo.runtime.type.util.UDFUtil;

public class Closure extends UDFImpl {
	
	

	private static final long serialVersionUID = -7200106903813254844L; // do not change
	
	private Variables variables;


	public Closure(){
		super();
	}

	public Closure(UDFProperties properties) {
		super(properties);
		PageContext pc = ThreadLocalPageContext.get();
		if(pc.undefinedScope().getCheckArguments())
			this.variables=new ClosureScope(pc,pc.argumentsScope(),pc.localScope(),pc.variablesScope());
		else{
			this.variables=pc.variablesScope();
			variables.setBind(true);
		}
	}
	
	public Closure(UDFProperties properties, Variables variables) {
		super(properties);
		this.variables=variables;
		
	}

	@Override
	public UDF duplicate(ComponentImpl c) {
		Closure clo = new Closure(properties,variables);// TODO duplicate variables as well?
		clo.ownerComponent=c;
		clo.setAccess(getAccess());
		return clo;
	}

	@Override
	public Object callWithNamedValues(PageContext pc,Collection.Key calledName, Struct values, boolean doIncludePath) throws PageException {
		Variables parent=pc.variablesScope();
        try{
        	pc.setVariablesScope(variables);
        	return super.callWithNamedValues(pc, calledName,values, doIncludePath);
		}
		finally {
			pc.setVariablesScope(parent);
		}
	}
	
	@Override
	public Object callWithNamedValues(PageContext pc, Struct values, boolean doIncludePath) throws PageException {
		Variables parent=pc.variablesScope();
        try{
        	pc.setVariablesScope(variables);
        	return super.callWithNamedValues(pc, values, doIncludePath);
		}
		finally {
			pc.setVariablesScope(parent);
		}
	}

	@Override
	public Object call(PageContext pc,Collection.Key calledName, Object[] args, boolean doIncludePath) throws PageException {
		Variables parent=pc.variablesScope();
		try{
        	pc.setVariablesScope(variables);
			return super.call(pc, calledName, args, doIncludePath);
		}
		finally {
			pc.setVariablesScope(parent);
		}
	}

	@Override
	public Object call(PageContext pc, Object[] args, boolean doIncludePath) throws PageException {
		Variables parent=pc.variablesScope();
		try{
        	pc.setVariablesScope(variables);
			return super.call(pc, args, doIncludePath);
		}
		finally {
			pc.setVariablesScope(parent);
		}
	}

	@Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel,DumpProperties dp) {
		return UDFUtil.toDumpData(pageContext, maxlevel, dp,this,true);
	}

	@Override
	public Struct getMetaData(PageContext pc) throws PageException {
		Struct meta = ComponentUtil.getMetaData(pc, properties);
		meta.setEL(KeyConstants._closure, Boolean.TRUE);// MUST move this to class UDFProperties
		meta.setEL("ANONYMOUSCLOSURE", Boolean.TRUE);// MUST move this to class UDFProperties
		
		return meta;
	}

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		// access
		setAccess(in.readInt());
		
		// properties
		properties=(UDFPropertiesImpl) in.readObject();
	}


	public void writeExternal(ObjectOutput out) throws IOException {
		// access
		out.writeInt(getAccess());
		
		// properties
		out.writeObject(properties);
	}
}
