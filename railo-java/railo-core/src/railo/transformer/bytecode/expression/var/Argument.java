package railo.transformer.bytecode.expression.var;

import org.objectweb.asm.Type;

import railo.transformer.TransformerException;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.cast.CastOther;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.util.ExpressionUtil;
import railo.transformer.expression.Expression;


public class Argument extends ExpressionBase {
		
		private Expression raw;
		private String type;

		public Argument(Expression value, String type) {
			super(value.getFactory(),value.getStart(),value.getEnd());
			this.raw=value;//Cast.toExpression(value,type);
			this.type=type;
		}

		/**
		 * @return the value
		 */
		public Expression getValue() {
			return CastOther.toExpression(raw,type);
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
		public Type _writeOut(BytecodeContext bc, int mode) throws TransformerException {
			return getValue().writeOut(bc, mode);
		}
		
		public Type writeOutValue(BytecodeContext bc, int mode) throws TransformerException {
			ExpressionUtil.visitLine(bc, getStart());
			Type t = getValue().writeOut(bc, mode);
			ExpressionUtil.visitLine(bc, getEnd());
			return t;
		}

		/**
		 * @return the type
		 */
		public String getStringType() {
			return type;
		}
	}