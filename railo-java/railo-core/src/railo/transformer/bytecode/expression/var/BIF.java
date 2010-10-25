package railo.transformer.bytecode.expression.var;

import railo.transformer.library.function.FunctionLibFunction;



public final class BIF extends FunctionMember {
		private static String ANY="any";
	
		private String name;
		private int argType;
		private String className;
		private String returnType=ANY;
		private FunctionLibFunction flf;



		public BIF(String name, FunctionLibFunction flf) {
			this.name=name;
			this.flf=flf;
		}

		public void setArgType(int argType) {
			this.argType=argType;
		}

		public void setClassName(String className) {
			this.className=className;
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
		 * @return the className
		 */
		public String getClassName() {
			return className;
		}

		/**
		 * @return the name
		 */
		public String getName() {
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