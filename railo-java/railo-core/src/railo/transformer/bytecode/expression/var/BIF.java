package railo.transformer.bytecode.expression.var;

import railo.transformer.Factory;
import railo.transformer.expression.ExprString;
import railo.transformer.library.function.FunctionLibFunction;



public final class BIF extends FunctionMember {
	
		private static String ANY="any";
	
		private ExprString name;
		private int argType;
		private Class clazz;
		private String returnType=ANY;
		private FunctionLibFunction flf;

		private final Factory factory;



		public BIF(ExprString name, FunctionLibFunction flf) {
			this.name=name;
			this.flf=flf;
			this.factory=name.getFactory();
		}
		
		public BIF(Factory factory,String name, FunctionLibFunction flf) {
			this.name=factory.createLitString(name);
			this.flf=flf;
			this.factory=factory;
		}

		public Factory getFactory() {
			return factory;
		}
		
		public void setArgType(int argType) {
			this.argType=argType;
		}

		public void setClass(Class clazz) {
			this.clazz=clazz;
		}

		public void setReturnType(String returnType) {
			this.returnType=returnType;
		}

		/**
		 * @return the argType
		 */
		public int getArgType() {
			return argType;
		}

		/**
		 * @return the class
		 */
		public Class getClazz() {
			return clazz;
		}

		/**
		 * @return the name
		 */
		public ExprString getName() {
			return name;
		}

		/**
		 * @return the returnType
		 */
		public String getReturnType() {
			return returnType;
		}

		/**
		 * @return the flf
		 */
		public FunctionLibFunction getFlf() {
			return flf;
		}

		/**
		 * @param flf the flf to set
		 */
		public void setFlf(FunctionLibFunction flf) {
			this.flf = flf;
		}
	}