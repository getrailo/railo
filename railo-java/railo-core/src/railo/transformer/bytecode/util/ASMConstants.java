package railo.transformer.bytecode.util;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.GeneratorAdapter;

public final class ASMConstants {

	public static void NULL(GeneratorAdapter ga) {
		ga.visitInsn(Opcodes.ACONST_NULL);
	}
}
