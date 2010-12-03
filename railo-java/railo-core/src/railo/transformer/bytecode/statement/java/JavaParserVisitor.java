package railo.transformer.bytecode.statement.java;

import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.BooleanLiteralExpr;
import japa.parser.ast.expr.CastExpr;
import japa.parser.ast.expr.CharLiteralExpr;
import japa.parser.ast.expr.DoubleLiteralExpr;
import japa.parser.ast.expr.IntegerLiteralExpr;
import japa.parser.ast.expr.IntegerLiteralMinValueExpr;
import japa.parser.ast.expr.LongLiteralExpr;
import japa.parser.ast.expr.LongLiteralMinValueExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.NullLiteralExpr;
import japa.parser.ast.expr.ObjectCreationExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import japa.parser.ast.expr.UnaryExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.IfStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.PrimitiveType;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import org.objectweb.asm.Label;

import railo.print;
import railo.commons.lang.ClassException;
import railo.commons.lang.ClassUtil;
import railo.commons.lang.StringUtil;
import railo.transformer.bytecode.ScriptBody;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.expression.var.NullExpression;
import railo.transformer.bytecode.literal.LitBoolean;
import railo.transformer.bytecode.literal.LitDouble;
import railo.transformer.bytecode.literal.LitFloat;
import railo.transformer.bytecode.literal.LitInteger;
import railo.transformer.bytecode.literal.LitLong;
import railo.transformer.bytecode.literal.LitString;
import railo.transformer.bytecode.statement.ExpressionStatement;

public class JavaParserVisitor extends VoidVisitorAdapter {

    private ScriptBody body;
	private Label start;
	private Label end;

	public JavaParserVisitor(ScriptBody body, Label start, Label end) {
		this.body=body;
		this.start=start;
		this.end=end;
	}


	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.VoidVisitorAdapter#visit(japa.parser.ast.stmt.BlockStmt, java.lang.Object)
	 */
	@Override
	public void visit(BlockStmt n, Object arg) {
		//print.o("visit(BlockStmt n, Object arg)");
		//print.o(n+":"+arg);
		
		super.visit(n, arg);
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.VoidVisitorAdapter#visit(japa.parser.ast.stmt.IfStmt, java.lang.Object)
	 */	public void visit(IfStmt n, Object arg) {
		super.visit(n, arg);
	}
	 


	public void visit(MethodDeclaration n, Object arg) {
		super.visit(n, arg);
    }

	private void statement(Statement statement) {
		System.out.println(statement);
		//System.err.println(statement.getData());
		//System.err.println(statement.getData());
		
		
	}
	
	
	
	
	
	
	
	
	

	public void visit(ExpressionStmt n, Object arg) {
		super.visit(n, arg);
		DataBag db=toDataBag(arg);
		Expression e=(Expression) db.rtn.pop();
		
		
		body.addStatement(new ExpressionStatement(e));
		db.rtn.clear();
	}
	
	/**
	 * @see japa.parser.ast.visitor.VoidVisitorAdapter#visit(japa.parser.ast.body.VariableDeclaratorId, java.lang.Object)
	 */
	public void visit(VariableDeclaratorId n, Object arg) {
		DataBag db=toDataBag(arg);
		append(db,n.getName());
	}
	

	/**
	 * @see japa.parser.ast.visitor.VoidVisitorAdapter#visit(japa.parser.ast.expr.NameExpr, java.lang.Object)
	 */
	public void visit(NameExpr n, Object arg) {
		DataBag db=toDataBag(arg);
		append(db,n.getName());
	}
	
	/**
	 * @see japa.parser.ast.visitor.VoidVisitorAdapter#visit(japa.parser.ast.type.ClassOrInterfaceType, java.lang.Object)
	 */
	public void visit(ClassOrInterfaceType n, Object arg) {
		DataBag db=toDataBag(arg);
		append(db,loadClass(n.toString()));
		
	}
	

	/**
	 * @see japa.parser.ast.visitor.VoidVisitorAdapter#visit(japa.parser.ast.type.PrimitiveType, java.lang.Object)
	 */
	public void visit(PrimitiveType n, Object arg) {
		DataBag db=toDataBag(arg);
		append(db,loadPrimitiveClass(n.getType().toString()));
	}
	
	
	// OPERATORS
	/**
	 * @see japa.parser.ast.visitor.VoidVisitorAdapter#visit(japa.parser.ast.expr.BinaryExpr, java.lang.Object)
	 */
	public void visit(BinaryExpr n, Object arg) {
		super.visit(n, arg);
		
		DataBag db=(DataBag)arg;
		Object right=db.rtn.pop();
		Object left=db.rtn.pop();
		
		append(db, new Operation(n.getBeginLine(), left, right, n.getOperator().name(), db));
	}
	
	/**
	 * @see japa.parser.ast.visitor.VoidVisitorAdapter#visit(japa.parser.ast.expr.UnaryExpr, java.lang.Object)
	 */
	public void visit(UnaryExpr n, Object arg) {
		super.visit(n, arg);
		DataBag db=(DataBag)arg;
		Object operant=db.rtn.pop();
		
		append(db, new UnaryOp(n.getBeginLine(), operant, n.getOperator().name(), db));
	}
	
	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.VoidVisitorAdapter#visit(japa.parser.ast.expr.CastExpr, java.lang.Object)
	 */
	public void visit(CastExpr n, Object arg) {
		super.visit(n, arg);
		DataBag db=(DataBag)arg;
		Object value=db.rtn.pop();
		Class type = (Class) db.rtn.pop();
		
		append(db, new CastOp(n.getBeginLine(), value, type, db));
	}
	
	
	
	// VARIABLES
	public void visit(AssignExpr n, Object arg) {
		super.visit(n, arg);
		DataBag db=(DataBag)arg;
		
		Object value=db.rtn.pop();
		String name = (String) db.rtn.pop();
		append(db, new Assign(n.getBeginLine(), name,value, n.getOperator().toString() ,db));// TODO line
	}

	public void visit(VariableDeclarationExpr n, Object arg) {
		super.visit(n, arg);
		DataBag db=(DataBag)arg;
		if(db.rtn.size()>=3){
			Object value=db.rtn.pop();
			String name = (String) db.rtn.pop();
			Class type = (Class) db.rtn.pop();
			print.e("type:"+n.getType());
			print.e("value:"+value);
			append(db, new VariableDecl(n.getBeginLine(), type, name, value,db));
		}
		else {
			String name = (String) db.rtn.pop();
			Class type = (Class) db.rtn.pop();
			append(db, new VariableDecl(n.getBeginLine(), type, name, null,db));
		}
	}
	
	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.VoidVisitorAdapter#visit(japa.parser.ast.expr.ObjectCreationExpr, java.lang.Object)
	 */
	public void visit(ObjectCreationExpr n, Object arg) {
		System.err.println("ObjectCreationExpr:"+n);
		
		
		super.visit(n, arg);
	}
	
	
	// LITERAL
	
	/**
	 * @see japa.parser.ast.visitor.VoidVisitorAdapter#visit(japa.parser.ast.expr.StringLiteralExpr, java.lang.Object)
	 */
	public void visit(StringLiteralExpr n, Object arg) {
		DataBag db=toDataBag(arg);
		append(db, LitString.toExprString(n.getValue()));
	}

	/**
	 * @see japa.parser.ast.visitor.VoidVisitorAdapter#visit(japa.parser.ast.expr.BooleanLiteralExpr, java.lang.Object)
	 */
	public void visit(BooleanLiteralExpr n, Object arg) {
		DataBag db=toDataBag(arg);
		append(db, LitBoolean.toExprBoolean(n.getValue()));
	}

	/**
	 * @see japa.parser.ast.visitor.VoidVisitorAdapter#visit(japa.parser.ast.expr.CharLiteralExpr, java.lang.Object)
	 */
	public void visit(CharLiteralExpr n, Object arg) {
		String str=n.getValue();
		
		//BytecodeContext bc=toBytecodeContext(arg);
		//bc.getAdapter().push((int)str.charAt(0));
		
		//super.visit(n, arg);
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.VoidVisitorAdapter#visit(japa.parser.ast.expr.DoubleLiteralExpr, java.lang.Object)
	 */
	public void visit(DoubleLiteralExpr n, Object arg) {
		String str=n.getValue().trim();
		DataBag db=toDataBag(arg);

		// float
		if(StringUtil.endsWithIgnoreCase(str, "f")) {
			str=str.substring(0,str.length()-1);
			append(db, LitFloat.toExprFloat(Float.valueOf(str),n.getBeginLine()));
			return;
		}
		
		// double
		if(StringUtil.endsWithIgnoreCase(str, "d")) {
			str=str.substring(0,str.length()-1);	
		}
		append(db, LitDouble.toExprDouble(Double.valueOf(str),n.getBeginLine()));
		
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.VoidVisitorAdapter#visit(japa.parser.ast.expr.IntegerLiteralExpr, java.lang.Object)
	 */
	public void visit(IntegerLiteralExpr n, Object arg) {
		String str=n.getValue();
		DataBag db=toDataBag(arg);
		append(db, LitInteger.toExpr(Integer.valueOf(str),n.getBeginLine()));// TODO 0X 0
		
		//super.visit(n, arg);
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.VoidVisitorAdapter#visit(japa.parser.ast.expr.IntegerLiteralMinValueExpr, java.lang.Object)
	 */
	public void visit(IntegerLiteralMinValueExpr n, Object arg) {
		super.visit(n, arg);
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.VoidVisitorAdapter#visit(japa.parser.ast.expr.LongLiteralExpr, java.lang.Object)
	 */
	public void visit(LongLiteralExpr n, Object arg) {
		String str=n.getValue();
		str=str.substring(0,str.length()-1);
		

		DataBag db=toDataBag(arg);
		append(db, LitLong.toExpr(Long.parseLong(str),n.getBeginLine()));// TODO 0X 0
		
	}

	/* (non-Javadoc)
	 * @see japa.parser.ast.visitor.VoidVisitorAdapter#visit(japa.parser.ast.expr.LongLiteralMinValueExpr, java.lang.Object)
	 */
	public void visit(LongLiteralMinValueExpr n, Object arg) {
		super.visit(n, arg);
	}

	/**
	 * @see japa.parser.ast.visitor.VoidVisitorAdapter#visit(japa.parser.ast.expr.NullLiteralExpr, java.lang.Object)
	 */
	@Override
	public void visit(NullLiteralExpr n, Object arg) {
		DataBag db=toDataBag(arg);
		append(db,NullExpression.NULL_EXPRESSION);
	}

	private void append(DataBag db, Object o) {
		db.rtn.add(o);
	}

	private DataBag setLine(DataBag db) {
		// TODO Auto-generated method stub
		return db;
	}

	private DataBag toDataBag(Object arg) {
		return toDataBag(arg,true);
	}
	
	private DataBag toDataBag(Object arg, boolean setLine) {
		if(setLine) return setLine((DataBag)arg);
		return (DataBag)arg;
	}

	private static Class loadClass(String type) {
		try {
			try {
				return ClassUtil.loadClass(type);
			} catch (ClassException e) {
				return ClassUtil.loadClass("java.lang."+type);
			}
		} catch (ClassException e) {
			throw new RuntimeException(e);
		}
	}
	private static Class loadPrimitiveClass(String type)  {
		if("byte".equalsIgnoreCase(type)) return byte.class;
		if("boolean".equalsIgnoreCase(type)) return boolean.class;
		if("char".equalsIgnoreCase(type)) return char.class;
		if("short".equalsIgnoreCase(type)) return short.class;
		if("int".equalsIgnoreCase(type)) return int.class;
		if("long".equalsIgnoreCase(type)) return long.class;
		if("float".equalsIgnoreCase(type)) return float.class;
		if("double".equalsIgnoreCase(type)) return double.class;
		
		throw new RuntimeException("invalid primitive type ["+type+"]");
		
	}
	/*private BytecodeContext toBytecodeContext(Object arg) {
		return ((DataBag)arg).bc;
	}
	private GeneratorAdapter toAdapter(Object arg) {
		return toBytecodeContext(arg).getAdapter();
	}*/
	

	
	/*private Expression toExpression(Object o) {
		if(o instanceof Expression) return (Expression) o;
		else if(o instanceof String) return new LitString((String) o, -1);
		throw new RuntimeException("cannot cast objet from type ["+Caster.toClassName(o)+"] to a Expression");
	}

	private String toString(Object o) {
		if(o instanceof String) 
			return (String) o;
		else if(o instanceof Expression){
			String res = ASMUtil.toString((Expression) o, null);
			return res;
		}
		throw new RuntimeException("cannot cast objet from type ["+Caster.toClassName(o)+"] to a String");
	}*/
}