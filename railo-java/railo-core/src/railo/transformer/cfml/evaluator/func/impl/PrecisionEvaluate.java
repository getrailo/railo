/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
package railo.transformer.cfml.evaluator.func.impl;

import railo.runtime.exp.TemplateException;
import railo.transformer.bytecode.cast.CastString;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.expression.var.Argument;
import railo.transformer.bytecode.expression.var.BIF;
import railo.transformer.bytecode.op.OpBigDecimal;
import railo.transformer.bytecode.op.OpDouble;
import railo.transformer.cfml.evaluator.FunctionEvaluator;
import railo.transformer.library.function.FunctionLibFunction;


public class PrecisionEvaluate implements FunctionEvaluator {

	@Override
	public void evaluate(BIF bif, FunctionLibFunction flf) throws TemplateException {

		Argument[] args = bif.getArguments();

		for (Argument arg : args) {
			Expression value = arg.getValue();
			if (value instanceof OpDouble) {
				arg.setValue(CastString.toExprString(toOpBigDecimal(((OpDouble)value))), "any");
			}
		}
	}

	private OpBigDecimal toOpBigDecimal(OpDouble op) {
		Expression left = op.getLeft();
		Expression right = op.getRight();
		if(left instanceof OpDouble) left=toOpBigDecimal((OpDouble) left);
		if(right instanceof OpDouble) right=toOpBigDecimal((OpDouble) right);
		return new OpBigDecimal(left, right, op.getOperation());
	}
}
