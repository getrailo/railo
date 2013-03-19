package railo.transformer.bytecode.util;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import railo.commons.lang.StringUtil;
import railo.runtime.type.util.ListUtil;

public class ClassRenamer extends ClassVisitor implements Opcodes {

	private final String newName;
	private String oldName;
	private boolean doNothing;

	ClassRenamer(ClassVisitor cv, String newName) {
		super(ASM4, cv);
		newName=ListUtil.trim(newName, "\\/");
		this.newName = newName;
	}

	public void visit(int version, int access, String name, String signature,String superName, String[] interfaces) {
		oldName=name;
		doNothing=oldName.equals(newName);
		
		cv.visit(version, ACC_PUBLIC, newName, signature, superName, interfaces);
	}

	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		MethodVisitor mv = cv.visitMethod(access, name, fixDesc(desc),fixSignature(signature), exceptions);
		if (mv != null && (access & ACC_ABSTRACT) == 0) {
			mv = new MethodRenamer(mv);
		}
		return mv;
	}

	class MethodRenamer extends MethodVisitor {

		public MethodRenamer(final MethodVisitor mv) {
			super(ASM4, mv);
		}

		public void visitTypeInsn(int i, String s) {
			if (!doNothing && oldName.equals(s)) {
				s = newName;
			}
			mv.visitTypeInsn(i, s);
		}

		public void visitFieldInsn(int opcode, String owner, String name,
				String desc) {
			if (!doNothing && oldName.equals(owner)) {
				mv.visitFieldInsn(opcode, newName, name, fixDesc(desc));
			} else {
				mv.visitFieldInsn(opcode, owner, name, fixDesc(desc));
			}
		}

		public void visitMethodInsn(int opcode, String owner, String name,
				String desc) {
			if (!doNothing && oldName.equals(owner)) {
				mv.visitMethodInsn(opcode, newName, name, fixDesc(desc));
			} else {
				mv.visitMethodInsn(opcode, owner, name, fixDesc(desc));
			}
		}
	}

	private String fixDesc(String desc) {
		//print.e("fixDesc:"+desc);
		return _fix(desc);
	}

	private String fixSignature(String signature) {
		//print.e("fixSignature:"+signature);
		return _fix(signature);
	}

	private String _fix(String str) {
		if (!doNothing && !StringUtil.isEmpty(str)) {
			if (str.indexOf(oldName) != -1) {
				str = StringUtil.replace(str, oldName, newName,false);
			}
		}
		return str;
	}
	
	public static byte[] rename(byte[] src, String newName){
		ClassReader cr = new ClassReader(src);
		ClassWriter cw = ASMUtil.getClassWriter();
		ClassVisitor ca = new ClassRenamer(cw,newName);
		cr.accept(ca, ClassReader.SKIP_DEBUG);
		return cw.toByteArray();
	}

	/*public static void main(String[] args) throws Throwable {
		String path = "/Users/mic/Projects/Railo/webroot/WEB-INF/railo/cfclasses/CF_Users_mic_Projects_Railo_webroot_jm4653/jira/test/test_cfm$cf.class";
		ResourceProvider frp = ResourcesImpl.getFileResourceProvider();
		Resource res = frp.getResource(path);
		print.e(getClassName(res));
	}*/
}