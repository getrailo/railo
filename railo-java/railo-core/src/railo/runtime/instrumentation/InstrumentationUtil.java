package railo.runtime.instrumentation;

import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import railo.commons.lang.ClassUtil;
import railo.runtime.type.List;
import railo.transformer.bytecode.util.ASMUtil;

public class InstrumentationUtil {
	
	/**
	 * clear all method/constructor bodies of the given class
	 * @param clazz class to clear
	 * @param redefineClass if true change the class instance, if false just create the byte array
	 * @return
	 */
	public static byte[] clearAllBodies(Class<?> clazz,boolean redefineClass){
		byte[] barr = clean(clazz);
		if(redefineClass)redefineClass(clazz, barr);
		return barr;
	}

	private static byte[] clean(Class<?> clazz) {  
        try {
			return clean(ClassUtil.toBytes(clazz));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
    }  
	
	private static byte[] clean(byte[] org) {  
		ClassWriter cw = ASMUtil.getClassWriter();
        ChangeAdapter ca = new ChangeAdapter(cw);  
        ClassReader cr = new ClassReader(org); 
        cr.accept(ca, false);  
        return cw.toByteArray();
    }  
	
	/**
	 * redefine the class with the given byte array
	 * @param clazz
	 * @param barr
	 * @return
	 */
	public static boolean redefineClass(Class clazz, byte[] barr){
		Instrumentation inst = InstrumentationFactory.getInstance();
	    if(inst!=null && inst.isRedefineClassesSupported()) {
	    	try {
	        	inst.redefineClasses(new ClassDefinition(clazz,barr));
				return true;
			} 
	    	catch (Throwable t) {t.printStackTrace();}
	    }
	    return false;
	}

	public static class ChangeAdapter extends ClassAdapter {  
		public ChangeAdapter(ClassVisitor cv) {  
		    super(cv);
		}  

			/**
			 * @see org.objectweb.asm.ClassAdapter#visitMethod(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
			 */
			public MethodVisitor visitMethod(int access, String name, String desc,String signature, String[] exceptions) {
				MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);  
				if(name.startsWith("<")) return mv;
				
				mv.visitCode();
				String ret = List.last(desc, ')').toLowerCase();
				// void
				if(ret.equals("v")) {
					mv.visitInsn(Opcodes.RETURN);
				}
				// boolean, short, int, char, 
				else if(ret.equals("z") || ret.equals("s") || ret.equals("i") || ret.equals("c") || ret.equals("b")) {
					mv.visitInsn(Opcodes.ICONST_0);
					mv.visitInsn(Opcodes.IRETURN);
				}
				else if(ret.equals("f")) {
					mv.visitInsn(Opcodes.FCONST_0);
					mv.visitInsn(Opcodes.FRETURN);
				}
				else if(ret.equals("j")) {
					mv.visitInsn(Opcodes.LCONST_0);
					mv.visitInsn(Opcodes.LRETURN);
				}
				else if(ret.equals("d")) {
					mv.visitInsn(Opcodes.DCONST_0);
					mv.visitInsn(Opcodes.DRETURN);
				}
				else {
					mv.visitInsn(Opcodes.ACONST_NULL);
			        mv.visitInsn(Opcodes.ARETURN);
				}
		        mv.visitEnd();
		        return mv;
			}
		} 
}