package railo.transformer.bytecode.expression.var;

import org.objectweb.asm.Type;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.cast.Cast;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.util.ExpressionUtil;


public class Argument extends ExpressionBase {
		
		private Expression raw;
		private String type;

		public Argument(Expression value, String type) {
			super(value.getLine());
			this.raw=value;//Cast.toExpression(value,type);
			this.type=type;
		}

		/**
		 * @return the value
		 */
		public Expression getValue() {
			return Cast.toExpression(raw,type);
		}
		
		/**
		 * return the uncasted value
		 * @return
		 */
		public Expression getRawValue() {
			return raw;
		}
		
		public void setValue(Expression value,String type) {
			this.raw = value;
			this.type=type;
			
		}

		/**
		 *
		 * @see railo.transformer.bytecode.expression.ExpressionBase#_writeOut(org.objectweb.asm.commons.GeneratorAdapter, int)
		 */
		public Type _writeOut(BytecodeContext bc, int mode) throws BytecodeException {
			return getValue().writeOut(bc, mode);
		}
		
		public Type writeOutValue(BytecodeContext bc, int mode) throws BytecodeException {
			ExpressionUtil.visitLine(bc, getLine());
			return getValue().writeOut(bc, mode);
		}

		/**
		 * @return the type
		 */
		public String getStringType() {
			return type;
		}
	}