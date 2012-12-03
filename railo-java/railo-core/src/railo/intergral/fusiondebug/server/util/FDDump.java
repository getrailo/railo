package railo.intergral.fusiondebug.server.util;

import java.util.Iterator;

import com.intergral.fusiondebug.server.IFDValue;
import com.intergral.fusiondebug.server.IFDVariable;

public class FDDump {

	//private static PrintStream out=System.out;

	public static void dump(IFDVariable var) {
		System.out.print(toString(var));
	}

	public static String toString(Object value) {
		StringBuffer sb=new StringBuffer();
		dump(sb,value, 0);
		return sb.toString();
	}
	
	public static String toString(IFDVariable var) {
		StringBuffer sb=new StringBuffer();
		dump(sb,var, 0);
		return sb.toString();
	}
	

	private static void dump(StringBuffer sb,Object value,int level) {
		if(value instanceof IFDValue) dump(sb, (IFDValue)value, level);
		else dump(sb, (IFDVariable)value, level);
	}
	
	private static void dump(StringBuffer sb,IFDValue value,int level) {
		for(int i=0;i<level;i++){
			sb.append(" - ");
		}
		
		sb.append(value.toString());
		sb.append("\n");
		if(value.hasChildren()){ 
			Iterator it = value.getChildren().iterator();
			while(it.hasNext()){
				Object o=it.next();
				dump(sb,(IFDVariable) o,level+1);
			}
		}
	}
	
	private static void dump(StringBuffer sb,IFDVariable var,int level) {
		for(int i=0;i<level;i++){
			sb.append(" - ");
		}
		sb.append(var.getName());
		sb.append(":");
		IFDValue value = var.getValue();
		
		sb.append(value.toString());
		sb.append("\n");
		//print.err(value.getClass().getName());
		if(value.hasChildren()){ 
			Iterator it = value.getChildren().iterator();
			while(it.hasNext()){
				Object o=it.next();
				//print.err(o.getClass().getName());
				dump(sb,(IFDVariable) o,level+1);
				//dump(sb,(IFDVariable) it.next(),level+1);
			}
		}
	}
	
	/*public static void main(String[] args) throws PageException {
		Array arr = new ArrayImpl();
		arr.setEL(1, "aaa");
		arr.setEL(2, Boolean.TRUE);
		arr.setEL(5, Constants.INTEGER_3);
		
		Array sub1 = new ArrayImpl();
		sub1.setEL(1, "ddd");
		arr.setEL(6, sub1);
		
		Struct sct=new StructImpl();
		sct.set("susi1", "eee");
		sct.set("susi2", "fff");
		arr.setEL(7, sct);
		
		Key aaa = KeyImpl.intern("aaa");
		Key bbb = KeyImpl.intern("bbb");
		Query qry=new QueryImpl(new Collection.Key[]{aaa,bbb},2,"quererli");
		qry.setAt(aaa, 1, "a1");
		qry.setAt(bbb, 1, "b1");
		qry.setAt(aaa, 2, "a2");
		qry.setAt(bbb, 2, sct);
		arr.setEL(8, qry);
		
		//arr.setEL(9, new StringBuffer());

		dump(new FDVariable(null,"susi",FDCaster.toFDValue(null,"susi",qry)));
		//dump(new FDVariable(null,"susi",FDCaster.toFDValue(null,"susi",arr)));
		
		//dump(FDCaster.toFDVariable("susi",arr));
	}*/
}
