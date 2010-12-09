package railo.transformer.bytecode.statement.java;

import org.objectweb.asm.Type;

import railo.print;
import railo.commons.lang.ClassException;
import railo.commons.lang.ClassUtil;
import railo.runtime.reflection.Reflector;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.literal.LitDouble;
import railo.transformer.bytecode.literal.LitFloat;
import railo.transformer.bytecode.literal.LitInteger;
import railo.transformer.bytecode.util.ASMUtil;

public class Assign extends ExpressionBase {

	private String name;
	private Object value;
	private DataBag db;
	private String operator;

	public Assign(int line,String name,Object value, String operator, DataBag db) {
		super(line);
		this.name=name;
		this.value=value;
		this.operator=operator;
		this.db = db;
	}

	public Type _writeOut(BytecodeContext bc, int mode) throws BytecodeException {
		
		Integer local=db.locals.get(name);
		if(local==null){
			throw new BytecodeException("there is no variable declaration for ["+name+"]", getLine());
		}
		Type t = bc.getAdapter().getLocalType(local.intValue());
		if("assign".equals(operator))writeOut(db,bc,t,mode,value,getLine(),false);
		else{
			new Operation(getLine(), name, value, operator, db).writeOut(bc, mode);
		}
		dup(bc,t);
		bc.getAdapter().storeLocal(local.intValue(),t);
		
		return t;
	}

	public static void dup(BytecodeContext bc, Type t) {
		String cn=t.getClassName();
		if(cn.equals("long") || cn.equals("double")) bc.getAdapter().dup2();
		else bc.getAdapter().dup();
	}

	public static Type writeOut(DataBag db,BytecodeContext bc, Type to, int mode,Object value, int line, boolean castExplicit) throws BytecodeException {
		Type from;
		if(value instanceof Expression)
			from=((Expression)value).writeOut(bc, mode);
		else {
			Integer var=db.locals.get(value);
			if(var==null)
				throw new BytecodeException("there is no variable with name ["+value+"] in the enviroment", line);
			from=bc.getAdapter().getLocalType(var.intValue());
			bc.getAdapter().loadLocal(var.intValue(),from);
			
		}
		print.e("val:"+value+">"+(value instanceof Expression));
		//Double d=new Double(0);
		//int i=(double)d;
	
		print.e(from+"->"+to+"="+(to!=null && !from.equals(to)));
		if(to!=null && !from.equals(to)){
			boolean isRefFrom = ASMUtil.isRefType(from);
			boolean isRefTo = ASMUtil.isRefType(to);
			
			print.o("castExplicit:"+castExplicit);
			if(castExplicit) {
				Class fc=null,tc=null;
			
				if(!isRefFrom && !isRefTo){
					fc = ASMUtil.getValueTypeClass(from,null);
					tc = ASMUtil.getValueTypeClass(to,null);
				}
				else {
					try {
						fc = ClassUtil.loadClass(from.getClassName());
						tc = ClassUtil.loadClass(to.getClassName());
					}
					catch (ClassException e) {
						throw new BytecodeException(e, line);
					}
				}
				print.o(fc.getName()+"><"+tc.getName());
				if(((tc==boolean.class && fc!=boolean.class))||(fc==boolean.class && tc!=boolean.class))
					throw new BytecodeException("cannot cast from ["+fc.getName()+"] to ["+tc.getName()+"]", line);
				else
					bc.getAdapter().cast(from, to);
			}
			else {
				
				// unbox
				if(isRefFrom && !isRefTo){
					bc.getAdapter().unbox(to);
					from=ASMUtil.toValueType(from);
					isRefFrom=false;
				}
				// box
				else if(!isRefFrom && isRefTo){
					bc.getAdapter().box(from);
					from=ASMUtil.toRefType(from);
					isRefFrom=true;
				}
				
				
				
				
				// value types
				if(!isRefFrom && !isRefTo){
					Class fc = ASMUtil.getValueTypeClass(from,null);
					Class tc = ASMUtil.getValueTypeClass(to,null);
					if(Reflector.canConvert(fc, tc))
						bc.getAdapter().cast(from, to);
					else {
						boolean doThrow=true;
						if(value instanceof LitDouble){
							double d=((LitDouble)value).getDoubleValue();
							if(canConvert(d, tc)){
								bc.getAdapter().cast(from, to);
								doThrow=false;
							}
						}
						if(value instanceof LitFloat){
							float f=((LitFloat)value).getFloatValue();
							if(canConvert(f, tc)){
								bc.getAdapter().cast(from, to);
								doThrow=false;
							}
						}
						if(value instanceof LitInteger){
							int i=((LitInteger)value).geIntValue();
							if(canConvert(i, tc)){
								bc.getAdapter().cast(from, to);
								doThrow=false;
							}
						}
						
						if(doThrow)throw new BytecodeException("cannot convert from ["+fc.getName()+"] to ["+tc.getName()+"]", line);
					}
				}
				
				// ref types
				else {
					try {
						Class fc = ClassUtil.loadClass(from.getClassName());
						Class tc = ClassUtil.loadClass(to.getClassName());
						if(Reflector.isInstaneOf(fc, tc))
							bc.getAdapter().cast(from, to);
						else 
							throw new BytecodeException("cannot convert from ["+fc.getName()+"] to ["+tc.getName()+"]", line);
					} 
					catch (ClassException e) {
						throw new BytecodeException(e, line);
					}
				}
			}
		}
		return from;
	}
	
	

	private static boolean canConvert(double d, Class trg) {
		if(trg==double.class) return true;
		if(trg==float.class) return d==(float)d;
		if(trg==long.class) return d==(long)d;
		if(trg==int.class) return d==(int)d;
		if(trg==char.class) return d==(char)d;
		if(trg==short.class) return d==(short)d;
		if(trg==byte.class) return d==(byte)d;
		
		return false;
	}
	
	private static boolean canConvert(float f, Class trg) {
		if(trg==double.class) return true;
		if(trg==float.class) return f==(float)f;
		if(trg==long.class) return f==(long)f;
		if(trg==int.class) return f==(int)f;
		if(trg==char.class) return f==(char)f;
		if(trg==short.class) return f==(short)f;
		if(trg==byte.class) return f==(byte)f;
		
		return false;
	}
	
	private static boolean canConvert(int i, Class trg) {
		if(trg==double.class) return true;
		if(trg==float.class) return i==(float)i;
		if(trg==long.class) return i==(long)i;
		if(trg==int.class) return i==(int)i;
		if(trg==char.class) return i==(char)i;
		if(trg==short.class) return i==(short)i;
		if(trg==byte.class) return i==(byte)i;
		
		return false;
	}
}
