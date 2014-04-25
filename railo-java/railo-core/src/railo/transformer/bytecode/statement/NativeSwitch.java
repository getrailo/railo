package railo.transformer.bytecode.statement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.objectweb.asm.Label;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.transformer.Factory;
import railo.transformer.Position;
import railo.transformer.TransformerException;
import railo.transformer.bytecode.Body;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.Statement;
import railo.transformer.bytecode.util.ExpressionUtil;

public final class NativeSwitch extends StatementBaseNoFinal implements FlowControlBreak,FlowControlContinue,HasBodies {

	public static final short LOCAL_REF=0;
	public static final short ARG_REF=1;
	public static final short PRIMITIVE=1;
	
	
	private int value;
	private Label end;
	private Statement defaultCase;
	List<Case> cases=new ArrayList<Case>();
	private Label[] labels=new Label[0];
	private int[] values=new int[0];
	private short type;
	
	public NativeSwitch(Factory f, int value, short type, Position start, Position end) {
		super(f,start, end);
		this.value=value;
		this.type=type;
	}

	@Override
	public void _writeOut(BytecodeContext bc) throws TransformerException {
		end = new Label();
		GeneratorAdapter adapter = bc.getAdapter();

		if(type==LOCAL_REF) adapter.loadLocal(value);
		else if(type==ARG_REF) adapter.loadArg(value);
		else adapter.push(value);
		
		Label beforeDefault = new Label();
		adapter.visitLookupSwitchInsn(beforeDefault, values, labels);
		
		Iterator<Case> it = cases.iterator();
		Case c;
		while(it.hasNext()) {
			c= it.next();
			adapter.visitLabel(c.label);
			ExpressionUtil.visitLine(bc, c.startPos);
			c.body.writeOut(bc);
			ExpressionUtil.visitLine(bc, c.endPos);
			if(c.doBreak){
				adapter.goTo(end);
			}
		}
		
		
		adapter.visitLabel(beforeDefault);
		if(defaultCase!=null)defaultCase.writeOut(bc);
		adapter.visitLabel(end);

	}
	
	public void addCase(int value, Statement body,Position start,Position end,boolean doBreak) {
		
		Case nc = new Case(value,body,start,end,doBreak);

		Label[] labelsTmp = new Label[cases.size()+1];
		int[] valuesTmp = new int[cases.size()+1];
		
		int count=0;
		boolean hasAdd=false;
		for(int i=0;i<labels.length;i++) {
			if(!hasAdd && nc.value<values[i]) {
				labelsTmp[count]=nc.label;
				valuesTmp[count]=nc.value;
				count++;
				hasAdd=true;
			}
			labelsTmp[count]=labels[i];
			valuesTmp[count]=values[i];
			count++;
		}
		if(!hasAdd) {
			labelsTmp[labels.length]=nc.label;
			valuesTmp[values.length]=nc.value;
		}
		labels=labelsTmp;
		values=valuesTmp;
		
		
		cases.add(nc);
	}
	
	public void addDefaultCase(Statement defaultStatement) {
		this.defaultCase=defaultStatement;
	}
	
	class Case {

		public boolean doBreak;
		private int value;
		private Statement body;
		private Label label=new Label();
		private Position startPos;
		private Position endPos;

		public Case(int value, Statement body,Position startline,Position endline, boolean doBreak) {
			this.value=value;
			this.body=body;
			this.startPos=startline;
			this.endPos=endline;
			this.doBreak=doBreak;
		}

	}

	/**
	 *
	 * @see railo.transformer.bytecode.statement.FlowControl#getBreakLabel()
	 */
	@Override
	public Label getBreakLabel() {
		return end;
	}

	/**
	 *
	 * @see railo.transformer.bytecode.statement.FlowControl#getContinueLabel()
	 */
	@Override
	public Label getContinueLabel() {
		return end;
	}

	/**
	 * @see railo.transformer.bytecode.statement.HasBodies#getBodies()
	 */
	@Override
	public Body[] getBodies() {
		if(cases==null) {
			if(defaultCase!=null) return new Body[]{(Body) defaultCase};
			return new Body[]{};
		}
		
		int len=cases.size(),count=0;
		if(defaultCase!=null)len++;
		Body[] bodies=new Body[len];
		Case c;
		Iterator<Case> it = cases.iterator();
		while(it.hasNext()) {
			c=it.next();
			bodies[count++]=(Body) c.body;
		}
		if(defaultCase!=null)bodies[count++]=(Body) defaultCase;
		
		return bodies;
	}

	@Override
	public String getLabel() {
		return null;
	}
}
