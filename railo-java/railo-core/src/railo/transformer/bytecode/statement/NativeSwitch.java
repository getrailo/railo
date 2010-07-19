package railo.transformer.bytecode.statement;

import java.util.ArrayList;
import java.util.Iterator;

import org.objectweb.asm.Label;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.transformer.bytecode.Body;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Statement;
import railo.transformer.bytecode.util.ExpressionUtil;

public final class NativeSwitch extends StatementBase implements FlowControl,HasBodies {

	public static final short LOCAL_REF=0;
	public static final short ARG_REF=1;
	public static final short PRIMITIVE=1;
	
	
	private int value;
	private Label end;
	private Statement defaultCase;
	ArrayList cases=new ArrayList();
	private Label[] labels=new Label[0];
	private int[] values=new int[0];
	private short type;
	
	public NativeSwitch(int value, short type, int startline,int endline) {
		super(startline, endline);
		this.value=value;
		this.type=type;
	}

	public void _writeOut(BytecodeContext bc) throws BytecodeException {
		end = new Label();
		GeneratorAdapter adapter = bc.getAdapter();

		if(type==LOCAL_REF) adapter.loadLocal(value);
		else if(type==ARG_REF) adapter.loadArg(value);
		else adapter.push(value);
		
		Label beforeDefault = new Label();
		adapter.visitLookupSwitchInsn(beforeDefault, values, labels);
		
		Iterator it = cases.iterator();
		Case c;
		while(it.hasNext()) {
			c=((Case) it.next());
			adapter.visitLabel(c.label);
			ExpressionUtil.visitLine(bc, c.startline);
			c.body.writeOut(bc);
			ExpressionUtil.visitLine(bc, c.endline);
			if(c.doBreak){
				adapter.goTo(end);
			}
		}
		
		
		adapter.visitLabel(beforeDefault);
		if(defaultCase!=null)defaultCase.writeOut(bc);
		adapter.visitLabel(end);

	}
	
	public void addCase(int value, Statement body,int startline,int endline,boolean doBreak) {
		
		Case nc = new Case(value,body,startline,endline,doBreak);

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
		private int startline;
		private int endline;

		public Case(int value, Statement body,int startline,int endline, boolean doBreak) {
			this.value=value;
			this.body=body;
			this.startline=startline;
			this.endline=endline;
			this.doBreak=doBreak;
		}

	}

	/**
	 *
	 * @see railo.transformer.bytecode.statement.FlowControl#getBreakLabel()
	 */
	public Label getBreakLabel() {
		return end;
	}

	/**
	 *
	 * @see railo.transformer.bytecode.statement.FlowControl#getContinueLabel()
	 */
	public Label getContinueLabel() {
		return end;
	}

	/**
	 * @see railo.transformer.bytecode.statement.HasBodies#getBodies()
	 */
	public Body[] getBodies() {
		if(cases==null) {
			if(defaultCase!=null) return new Body[]{(Body) defaultCase};
			return new Body[]{};
		}
		
		int len=cases.size(),count=0;
		if(defaultCase!=null)len++;
		Body[] bodies=new Body[len];
		Case c;
		Iterator it = cases.iterator();
		while(it.hasNext()) {
			c=(Case) it.next();
			bodies[count++]=(Body) c.body;
		}
		if(defaultCase!=null)bodies[count++]=(Body) defaultCase;
		
		return bodies;
	}
}
