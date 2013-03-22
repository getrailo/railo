package railo.transformer.bytecode.expression.var;

import org.objectweb.asm.Type;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.cast.Cast;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.util.ExpressionUtil;


public class Argument extends ExpressionBase {
		
		private Expression value;
		private String type;

		public Argument(Expression value, String type) {
			super(value.getLine());
			this.value=Cast.toExpression(value,type);
			this.type=type;
		}

		/**
		 * @return the value
		 */
		public Expression getValue() {
			return value;
		}
		public void setValue(Expression value) {
			this.value= value;
		}

		/**
		 *
		 * @see railo.transformer.bytecode.expression.ExpressionBase#_writeOut(org.objectweb.asm.commons.GeneratorAdapter, int)
		 */
		public Type _writeOut(BytecodeContext bc, int mode) throws BytecodeException {
			//print.dumpStack();
			return value.writeOut(bc, mode);
		}
		
		public Type writeOutValue(BytecodeContext bc, int mode) throws BytecodeException {
			//print.dumpStack();
			ExpressionUtil.visitLine(bc, getLine());
			return value.writeOut(bc, mode);
		}

		/**
		 * @return the type
		 */
		public String getStringType() {
			return type;
		}
		public void setStringType(String type) {
			this.type= type;
		}
	}