package railo.transformer.bytecode.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.commons.digest.MD5;
import railo.commons.lang.StringUtil;
import railo.commons.lang.SystemOut;
import railo.runtime.component.Property;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.PageException;
import railo.runtime.net.rpc.AxisCaster;
import railo.runtime.op.Caster;
import railo.transformer.bytecode.Body;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Literal;
import railo.transformer.bytecode.Page;
import railo.transformer.bytecode.ScriptBody;
import railo.transformer.bytecode.Statement;
import railo.transformer.bytecode.cast.CastBoolean;
import railo.transformer.bytecode.cast.CastDouble;
import railo.transformer.bytecode.cast.CastString;
import railo.transformer.bytecode.expression.ExprDouble;
import railo.transformer.bytecode.expression.ExprString;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.expression.var.Variable;
import railo.transformer.bytecode.expression.var.VariableString;
import railo.transformer.bytecode.literal.LitBoolean;
import railo.transformer.bytecode.literal.LitDouble;
import railo.transformer.bytecode.literal.LitString;
import railo.transformer.bytecode.statement.FlowControl;
import railo.transformer.bytecode.statement.PrintOut;
import railo.transformer.bytecode.statement.TryCatchFinally;
import railo.transformer.bytecode.statement.tag.Attribute;
import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.bytecode.statement.tag.TagComponent;
import railo.transformer.bytecode.statement.tag.TagTry;
import railo.transformer.cfml.evaluator.EvaluatorException;

public final class ASMUtil {

	private static final int VERSION_2=1;
	private static final int VERSION_3=2;

	public static final short TYPE_ALL=0;
	public static final short TYPE_BOOLEAN=1;
	public static final short TYPE_NUMERIC=2;
	public static final short TYPE_STRING=4;
	
	
	
	
	private static int version=0;
	
	private final static Method CONSTRUCTOR_OBJECT = Method.getMethod("void <init> ()");
	private static final String VERSION_MESSAGE = "you use a old version of the ASM Jar, please update your jar files";
	private static long id=0;
		
	/**
	 * Gibt zur�ck ob das direkt �bergeordnete Tag mit dem �bergebenen Full-Name (Namespace und Name) existiert.
	 * @param el Startelement, von wo aus gesucht werden soll.
	 * @param fullName Name des gesuchten Tags.
	 * @return Existiert ein solches Tag oder nicht.
	 */
	public static boolean hasAncestorTag(Tag tag, String fullName) {
	    return getAncestorTag(tag, fullName)!=null;
	}
	

	/**
	 * Gibt das �bergeordnete CFXD Tag Element zur�ck, falls dies nicht existiert wird null zur�ckgegeben.
	 * @param el Element von dem das parent Element zur�ckgegeben werden soll.
	 * @return �bergeordnete CFXD Tag Element
	 */
	public static Tag getParentTag(Tag tag)	{
		Statement p=tag.getParent();
		if(p==null)return null;
		p=p.getParent();
		if(p instanceof Tag) return (Tag) p;
		return null;
	}

	public static boolean isParentTag(Tag tag,String fullName)	{
		Tag p = getParentTag(tag);
		if(p==null) return false;
		return p.getFullname().equalsIgnoreCase(fullName);
		
	}
	public static boolean isParentTag(Tag tag,Class clazz)	{
		Tag p = getParentTag(tag);
		if(p==null) return false;
		return p.getClass()==clazz;
		
	}
	
	/**
	 * has ancestor LoopStatement 
	 * @param stat
	 * @return
	 */
	public static boolean hasAncestorLoopStatement(Statement stat) {
		return getAncestorFlowControlStatement(stat)!=null;
	}
	
	/**
	 * get ancestor LoopStatement 
	 * @param stat
	 * @param ingoreScript 
	 * @return
	 */
	public static FlowControl getAncestorFlowControlStatement(Statement stat) {
		Statement parent = stat;
		while(true)	{
			parent=parent.getParent();
			if(parent==null)return null;
			if(parent instanceof FlowControl)	{
				if(parent instanceof ScriptBody){
					FlowControl scriptBodyParent = getAncestorFlowControlStatement(parent);
					if(scriptBodyParent!=null) return scriptBodyParent;
					return (FlowControl)parent;
				}
				
				
				return (FlowControl) parent;
			}
		}
	}
	
	public static boolean hasAncestorTryStatement(Statement stat) {
		return getAncestorTryStatement(stat)!=null;
	}
	
	public static Statement getAncestorTryStatement(Statement stat) {
		Statement parent = stat;
		while(true)	{
			parent=parent.getParent();
			if(parent==null)return null;
			
			if(parent instanceof TagTry)	{
				return parent;
			}
			else if(parent instanceof TryCatchFinally)	{
				return parent;
			}
		}
	}
	


	
	/**
	 * Gibt ein �bergeordnetes Tag mit dem �bergebenen Full-Name (Namespace und Name) zur�ck, 
	 * falls ein solches existiert, andernfalls wird null zur�ckgegeben.
	 * @param el Startelement, von wo aus gesucht werden soll.
	 * @param fullName Name des gesuchten Tags.
	 * @return  �bergeornetes Element oder null.
	 */
	public static Tag getAncestorTag(Tag tag, String fullName) {
		Statement parent=tag;
		while(true)	{
			parent=parent.getParent();
			if(parent==null)return null;
			if(parent instanceof Tag)	{
				tag=(Tag) parent;
				if(tag.getFullname().equalsIgnoreCase(fullName))
					return tag;
			}
		}
	}

    /**
     * extract the content of a attribut
     * @param cfxdTag
     * @param attrName
     * @return attribute value
     * @throws EvaluatorException
     */
	public static Boolean getAttributeBoolean(Tag tag,String attrName) throws EvaluatorException {
		Boolean b= getAttributeLiteral(tag, attrName).getBoolean(null);
		if(b==null)throw new EvaluatorException("attribute ["+attrName+"] must be a constant boolean value");
		return b;
    }
    
    /**
     * extract the content of a attribut
     * @param cfxdTag
     * @param attrName
     * @return attribute value
     * @throws EvaluatorException
     */
	public static Boolean getAttributeBoolean(Tag tag,String attrName, Boolean defaultValue) {
		Literal lit=getAttributeLiteral(tag, attrName,null);
		if(lit==null) return defaultValue;
		return lit.getBoolean(defaultValue); 
    }


    /**
     * extract the content of a attribut
     * @param cfxdTag
     * @param attrName
     * @return attribute value
     * @throws EvaluatorException
     */
	public static String getAttributeString(Tag tag,String attrName) throws EvaluatorException {
		return getAttributeLiteral(tag, attrName).getString();
    }
    
    /**
     * extract the content of a attribut
     * @param cfxdTag
     * @param attrName
     * @return attribute value
     * @throws EvaluatorException
     */
	public static String getAttributeString(Tag tag,String attrName, String defaultValue) {
		Literal lit=getAttributeLiteral(tag, attrName,null);
		if(lit==null) return defaultValue;
		return lit.getString(); 
    }
	
	/**
     * extract the content of a attribut
     * @param cfxdTag
     * @param attrName
     * @return attribute value
     * @throws EvaluatorException
     */
	public static Literal getAttributeLiteral(Tag tag,String attrName) throws EvaluatorException {
		Attribute attr = tag.getAttribute(attrName);
		if(attr!=null && attr.getValue() instanceof Literal) return ((Literal)attr.getValue());
        throw new EvaluatorException("attribute ["+attrName+"] must be a constant value");
    }
	
	
    
    /**
     * extract the content of a attribut
     * @param cfxdTag
     * @param attrName
     * @return attribute value
     * @throws EvaluatorException
     */
	public static Literal getAttributeLiteral(Tag tag,String attrName, Literal defaultValue) {
		Attribute attr = tag.getAttribute(attrName);
		if(attr!=null && attr.getValue() instanceof Literal) return ((Literal)attr.getValue());
        return defaultValue; 
    }
	
	
	

	/**
	 * Pr�ft ob das das angegebene Tag in der gleichen Ebene nach dem angegebenen Tag vorkommt.
	 * @param tag Ausgangspunkt, nach diesem tag darf das angegebene nicht vorkommen.
	 * @param nameToFind Tag Name der nicht vorkommen darf
	 * @return kommt das Tag vor.
	 */
	public static boolean hasSisterTagAfter(Tag tag, String nameToFind) {
		Body body=(Body) tag.getParent();
		List stats = body.getStatements();
		Iterator it = stats.iterator();
		Statement other;
		
		boolean isAfter=false;
		while(it.hasNext()) {
			other=(Statement) it.next();
			
			if(other instanceof Tag) {
				if(isAfter) {
					if(((Tag) other).getTagLibTag().getName().equals(nameToFind))
					return true;
				}
				else if(other == tag) isAfter=true;
				
			}
			
		}
		return false;
	}
	
	
	
	/**
	 * Pr�ft ob das angegebene Tag innerhalb seiner Ebene einmalig ist oder nicht.
	 * @param tag Ausgangspunkt, nach diesem tag darf das angegebene nicht vorkommen.
	 * @return kommt das Tag vor.
	 */
	public static boolean hasSisterTagWithSameName(Tag tag) {
		
		Body body=(Body) tag.getParent();
		List stats = body.getStatements();
		Iterator it = stats.iterator();
		Statement other;
		String name=tag.getTagLibTag().getName();
		
		while(it.hasNext()) {
			other=(Statement) it.next();
			
			if(other != tag && other instanceof Tag && ((Tag) other).getTagLibTag().getName().equals(name))
					return true;
			
		}
		return false;
	}

	/**
	 * remove this tag from his parent body
	 * @param tag
	 */
	public static void remove(Tag tag) {
		Body body=(Body) tag.getParent();
		body.getStatements().remove(tag);
	}

	/**
	 * replace src with trg
	 * @param src
	 * @param trg
	 */
	public static void replace(Tag src, Tag trg, boolean moveBody) {
		trg.setParent(src.getParent());
		
		Body p=(Body) src.getParent();
		List stats = p.getStatements();
		Iterator it = stats.iterator();
		Statement stat;
		int count=0;
		
		while(it.hasNext()) {
			stat=(Statement) it.next();
			if(stat==src) {
				if(moveBody && src.getBody()!=null)src.getBody().setParent(trg);
				stats.set(count, trg);
				break;
			}
			count++;
		}
	}
	
	public static Page getAncestorPage(Statement stat) throws BytecodeException {
		Statement parent=stat;
		while(true)	{
			parent=parent.getParent();
			if(parent==null) {
				throw new BytecodeException("missing parent Statement of Statment",stat.getLine());
				//return null;
			}
			if(parent instanceof Page)	return (Page) parent;
		}
	}
	
	public static Tag getAncestorComponent(Statement stat) throws BytecodeException {
		//print.ln("getAncestorPage:"+stat);
		Statement parent=stat;
		while(true)	{
			parent=parent.getParent();
			//print.ln(" - "+parent);
			if(parent==null) {
				throw new BytecodeException("missing parent Statement of Statment",stat.getLine());
				//return null;
			}
			if(parent instanceof TagComponent)
			//if(parent instanceof Tag && "component".equals(((Tag)parent).getTagLibTag().getName()))	
				return (Tag) parent;
		}
	}
	
	public static Statement getRoot(Statement stat) {
		while(true)	{
			if(isRoot(stat))	{
				return stat;
			}
			stat=stat.getParent();
		}
	}



    public static boolean isRoot(Statement statement) { 
    	//return statement instanceof Page || (statement instanceof Tag && "component".equals(((Tag)statement).getTagLibTag().getName()));
    	return statement instanceof Page || statement instanceof TagComponent;
    }
	
	public static void invokeMethod(GeneratorAdapter adapter, Type type, Method method) {
		if(type.getClass().isInterface())
			adapter.invokeInterface(type, method);
		else
			adapter.invokeVirtual(type, method);
	}

    public static byte[] createPojo(String className, ASMProperty[] properties,Class parent,Class[] interfaces, String srcName) throws PageException {
    	className=className.replace('.', '/');
    	className=className.replace('\\', '/');
    	className=railo.runtime.type.List.trim(className, "/");
    	String[] inter=null;
    	if(interfaces!=null){
    		inter=new String[interfaces.length];
    		for(int i=0;i<inter.length;i++){
    			inter[i]=interfaces[i].getName().replace('.', '/');
    		}
    	}
    // CREATE CLASS	
		//ClassWriter cw = new ClassWriter(true);
    	ClassWriter cw = ASMUtil.getClassWriter();
        cw.visit(Opcodes.V1_2, Opcodes.ACC_PUBLIC, className, null, parent.getName().replace('.', '/'), inter);
        String md5;
        try{
    		md5=createMD5(properties);
    	}
    	catch(Throwable t){
    		md5="";
    		t.printStackTrace();
    	}
        
        FieldVisitor fv = cw.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC, "_md5_", "Ljava/lang/String;", null, md5);
        fv.visitEnd();
        
        
    // Constructor
        GeneratorAdapter adapter = new GeneratorAdapter(Opcodes.ACC_PUBLIC,CONSTRUCTOR_OBJECT,null,null,cw);
        adapter.loadThis();
        adapter.invokeConstructor(toType(parent,true), CONSTRUCTOR_OBJECT);
        adapter.returnValue();
        adapter.endMethod();
    
        // properties
        for(int i=0;i<properties.length;i++){
        	createProperty(cw,className,properties[i]);
        }
        
        // complexType src
        if(!StringUtil.isEmpty(srcName)) {
	        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "_srcName", "()Ljava/lang/String;", null, null);
	        mv.visitCode();
	        Label l0 = new Label();
	        mv.visitLabel(l0);
	        mv.visitLineNumber(4, l0);
	        mv.visitLdcInsn(srcName);
	        mv.visitInsn(Opcodes.ARETURN);
	        mv.visitMaxs(1, 0);
	        mv.visitEnd();
        }
        
        cw.visitEnd();
        return cw.toByteArray();
    }
    
    private static void createProperty(ClassWriter cw,String classType, ASMProperty property) throws PageException {
		String name = property.getName();
		Type type = property.getASMType();
		Class clazz = property.getClazz();
		
		cw.visitField(Opcodes.ACC_PRIVATE, name, type.toString(), null, null).visitEnd();
		
		int load=loadFor(type);
		int sizeOf=sizeOf(type);
		
    	// get<PropertyName>():<type>
    		Type[] types=new Type[0];
    		Method method = new Method((clazz==boolean.class?"get":"get")+StringUtil.ucFirst(name),type,types);
            GeneratorAdapter adapter = new GeneratorAdapter(Opcodes.ACC_PUBLIC , method, null, null, cw);
            
            Label start = new Label();
            adapter.visitLabel(start);
            
            adapter.visitVarInsn(Opcodes.ALOAD, 0);
			adapter.visitFieldInsn(Opcodes.GETFIELD, classType, name, type.toString());
			adapter.returnValue();
			Label end = new Label();
			adapter.visitLabel(end);
			adapter.visitLocalVariable("this", "L"+classType+";", null, start, end, 0);
			adapter.visitMaxs(sizeOf, 1);
			
			adapter.visitEnd();
			
			
			
			
			
			
		
		// set<PropertyName>(object):void
			types=new Type[]{type};
			method = new Method("set"+StringUtil.ucFirst(name),Types.VOID,types);
            adapter = new GeneratorAdapter(Opcodes.ACC_PUBLIC , method, null, null, cw);
            
            start = new Label();
            adapter.visitLabel(start);
            adapter.visitVarInsn(Opcodes.ALOAD, 0);
            adapter.visitVarInsn(load, 1);
            adapter.visitFieldInsn(Opcodes.PUTFIELD, classType, name, type.toString());
			
			adapter.visitInsn(Opcodes.RETURN);
			end = new Label();
			adapter.visitLabel(end);
			adapter.visitLocalVariable("this", "L"+classType+";", null, start, end, 0);
			adapter.visitLocalVariable(name, type.toString(), null, start, end, 1);
			adapter.visitMaxs(sizeOf+1, sizeOf+1);
			adapter.visitEnd();
        
			
			
			
			
			
	}

    public static int loadFor(Type type) {
    	if(type.equals(Types.BOOLEAN_VALUE) || type.equals(Types.INT_VALUE) || type.equals(Types.CHAR) || type.equals(Types.SHORT_VALUE))
    		return Opcodes.ILOAD;
    	if(type.equals(Types.FLOAT_VALUE))
    		return Opcodes.FLOAD;
    	if(type.equals(Types.LONG_VALUE))
    		return Opcodes.LLOAD;
    	if(type.equals(Types.DOUBLE_VALUE))
    		return Opcodes.DLOAD;
    	return Opcodes.ALOAD;
	}

    public static int sizeOf(Type type) {
    	if(type.equals(Types.LONG_VALUE) || type.equals(Types.DOUBLE_VALUE))
    		return 2;
    	return 1;
	}


	/**
     * translate a string cfml type definition to a Type Object
     * @param cfType
     * @param axistype
     * @return
     * @throws PageException
     */
    public static Type toType(String cfType, boolean axistype) throws PageException {
		return toType(Caster.cfTypeToClass(cfType), axistype);
	}

    /**
     * translate a string cfml type definition to a Type Object
     * @param cfType
     * @param axistype
     * @return
     * @throws PageException
     */
    public static Type toType(Class type, boolean axistype) {
		if(axistype)type=AxisCaster.toAxisTypeClass(type);
		return Type.getType(type);	
	}
    

	public static String createMD5(ASMProperty[] props) {
		
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<props.length;i++){
			sb.append("name:"+props[i].getName()+";");
			if(props[i] instanceof Property){
				sb.append("type:"+((Property)props[i]).getType()+";");
			}
			else {
				try {
					sb.append("type:"+props[i].getASMType()+";");
					
				} 
				catch (PageException e) {}
			}
		}
		try {
			return MD5.getDigestAsString(sb.toString());
		} catch (IOException e) {
			return "";
		}
	}



	public static void removeLiterlChildren(Tag tag, boolean recursive) {
		Body body=tag.getBody();
		if(body!=null) {
        	List list = body.getStatements();
        	Statement[] stats = (Statement[]) list.toArray(new Statement[list.size()]);
        	PrintOut po;
        	Tag t;
        	for(int i=0;i<stats.length;i++) {
            	if(stats[i] instanceof PrintOut) {
            		po=(PrintOut) stats[i];
            		if(po.getExpr() instanceof Literal) {
            			body.getStatements().remove(po);
            		}
            	}
            	else if(recursive && stats[i] instanceof Tag) {
            		t=(Tag) stats[i];
            		if(t.getTagLibTag().isAllowRemovingLiteral()) {
            			removeLiterlChildren(t, recursive);
            		}
            	}
            }
        }
	}


	public synchronized static String getId() {
		if(id<0)id=0;
		return StringUtil.addZeros(++id,6);
	}


	public static boolean isEmpty(Body body) {
		return body==null || body.isEmpty();
	}


	/**
	 * @param adapter
	 * @param expr
	 * @param mode
	 */
	public static void pop(GeneratorAdapter adapter, Expression expr,int mode) {
		if(mode==Expression.MODE_VALUE && (expr instanceof ExprDouble))adapter.pop2();
		else adapter.pop();
	}
	public static void pop(GeneratorAdapter adapter, Type type) {
		if(type.equals(Types.DOUBLE_VALUE))adapter.pop2();
		else if(type.equals(Types.VOID));
		else adapter.pop();
	}


	public static ClassWriter getClassWriter() {
		if(version==VERSION_2)
			return new ClassWriter(true);
		
		try{
			ClassWriter cw = new ClassWriter(true);
			version=VERSION_2;
			return cw;
		}
		catch(NoSuchMethodError err){
			if(version==0){
				version=VERSION_3;
			}
			
			PrintWriter ew = ThreadLocalPageContext.getConfig().getErrWriter();
			SystemOut.printDate(ew, VERSION_MESSAGE);
			
			try {
				return  ClassWriter.class.getConstructor(new Class[]{int.class}).newInstance(new Object[]{new Integer(1)});
				
			} 
			catch (Exception e) {
				throw new RuntimeException(Caster.toPageException(e));
				
			}
		}
	}

	/*
	 * For 3.1
	 * 
	 * public static ClassWriter getClassWriter() {
		if(version==VERSION_3)
			return new ClassWriter(ClassWriter.COMPUTE_MAXS);
		
		try{
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			version=VERSION_3;
			return cw;
		}
		catch(NoSuchMethodError err){
			if(version==0){
				version=VERSION_2;
				throw new RuntimeException(new ApplicationException(VERSION_MESSAGE+
						", after reload this version will work as well, but please update to newer version"));
			}
			
			PrintWriter ew = ThreadLocalPageContext.getConfig().getErrWriter();
			SystemOut.printDate(ew, VERSION_MESSAGE);
			//err.printStackTrace(ew);
			
			try {
				return (ClassWriter) ClassWriter.class.getConstructor(new Class[]{boolean.class}).newInstance(new Object[]{Boolean.TRUE});
				
			} 
			catch (Exception e) {
				throw new RuntimeException(Caster.toPageException(e));
				
			}
		}
	}*/


	public static String createOverfowMethod() {
		return "_call"+ASMUtil.getId();
	}
	
	// FUTURE add to loader, same method is also in FD Extension railo.intergral.fusiondebug.server.util.FDUtil
	public static boolean isOverfowMethod(String name) {
		return name.startsWith("_call") && name.length()>=11;
	}


	public static boolean isDotKey(ExprString expr) {
		return expr instanceof LitString && !((LitString)expr).fromBracket();
	}

	public static String toString(Expression exp,String defaultValue) {
		try {
			return toString(exp);
		} catch (BytecodeException e) {
			return defaultValue;
		}
	}
	public static String toString(Expression exp) throws BytecodeException {
		if(exp instanceof Variable) {
			return toString(VariableString.toExprString(exp));
		}
		else if(exp instanceof VariableString) {
			return ((VariableString)exp).castToString();
		}
		else if(exp instanceof Literal) {
			return ((Literal)exp).toString();
		}
		return null;
	}


	public static Boolean toBoolean(Attribute attr, int line) throws BytecodeException {
		if(attr==null)
			throw new BytecodeException("attribute does not exist",line);
		
		if(attr.getValue() instanceof Literal){
			Boolean b=((Literal)attr.getValue()).getBoolean(null);
			if(b!=null) return b; 
		}
		throw new BytecodeException("attribute ["+attr.getName()+"] must be a constant boolean value",line);
		
		
	}
	public static Boolean toBoolean(Attribute attr, int line, Boolean defaultValue) {
		if(attr==null)
			return defaultValue;
		
		if(attr.getValue() instanceof Literal){
			Boolean b=((Literal)attr.getValue()).getBoolean(null);
			if(b!=null) return b; 
		}
		return defaultValue;	
	}


	public static boolean isCFC(Statement s) {
		Statement p;
		while((p=s.getParent())!=null){
			s=p;
		}
		
		return true;
	}


	
	
	public static boolean isLiteralAttribute(Tag tag, String attrName, short type,boolean required,boolean throwWhenNot) throws EvaluatorException {
		Attribute attr = tag.getAttribute(attrName);
		String strType="/constant";
		if(attr!=null) {
			switch(type){
			case TYPE_ALL:
				if(attr.getValue() instanceof Literal) return true;
			break;
			case TYPE_BOOLEAN:
				if(CastBoolean.toExprBoolean(attr.getValue()) instanceof LitBoolean) return true;
				strType=" boolean";
			break;
			case TYPE_NUMERIC:
				if(CastDouble.toExprDouble(attr.getValue()) instanceof LitDouble) return true;
				strType=" numeric";
			break;
			case TYPE_STRING:
				if(CastString.toExprString(attr.getValue()) instanceof LitString) return true;
				strType=" string";
			break;
			}
			if(!throwWhenNot) return false;
			throw new EvaluatorException("Attribute ["+attrName+"] of the Tag ["+tag.getFullname()+"] must be a literal"+strType+" value");
		}
		if(required){
			if(!throwWhenNot) return false;
			throw new EvaluatorException("Attribute ["+attrName+"] of the Tag ["+tag.getFullname()+"] is required");
		}
		return true;
	}
	
}
