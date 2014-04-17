package railo.transformer.bytecode.util;

import java.io.BufferedReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.Tag;

import org.objectweb.asm.Type;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.lang.ClassException;
import railo.commons.lang.ClassUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.InterfacePage;
import railo.runtime.Page;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.PagePlus;
import railo.runtime.PageSource;
import railo.runtime.component.ImportDefintion;
import railo.runtime.component.ImportDefintionImpl;
import railo.runtime.component.Member;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigWeb;
import railo.runtime.exp.Abort;
import railo.runtime.exp.ExceptionHandler;
import railo.runtime.exp.PageException;
import railo.runtime.img.Image;
import railo.runtime.interpreter.VariableInterpreter;
import railo.runtime.op.Caster;
import railo.runtime.op.Operator;
import railo.runtime.poi.Excel;
import railo.runtime.poi.ExcelUtil;
import railo.runtime.security.SecurityManager;
import railo.runtime.type.Array;
import railo.runtime.type.Closure;
import railo.runtime.type.Collection;
import railo.runtime.type.FunctionValue;
import railo.runtime.type.Iteratorable;
import railo.runtime.type.Query;
import railo.runtime.type.Struct;
import railo.runtime.type.UDF;
import railo.runtime.type.UDFImpl;
import railo.runtime.type.UDFProperties;
import railo.runtime.type.UDFPropertiesImpl;
import railo.runtime.type.ref.Reference;
import railo.runtime.type.ref.VariableReference;
import railo.runtime.type.scope.Scope;
import railo.runtime.type.scope.Undefined;
import railo.runtime.type.scope.Variables;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.util.ListUtil;
import railo.runtime.util.NumberRange;
import railo.runtime.writer.BodyContentUtil;
import railo.transformer.bytecode.BytecodeException;

public final class Types {


	// TODO muss wohl alle Prim typen sein plus Object
    public static final int _BOOLEAN=1;
    public static final int _DOUBLE=2;
    private static final int _SHORT=7;
    
    public static final int _OBJECT=0;
    public static final int _STRING=3;
    

    private static final int _CHAR=	_DOUBLE;
    private static final int _FLOAT=	_DOUBLE;
    private static final int _LONG=	_DOUBLE;
    private static final int _INT=	_DOUBLE;
    private static final int _BYTE=	_DOUBLE;
    
    //public static final int SIZE_INT_TYPES=10;
    

    

    public static final Type ABORT=Type.getType(Abort.class);
    public static final Type ARRAY=Type.getType(railo.runtime.type.Array.class);

    public static final Type BYTE=Type.getType(Byte.class);
    public static final Type BYTE_VALUE=Type.getType(byte.class);
    public static final Type BYTE_ARRAY=Type.getType(Byte[].class);
    public static final Type BYTE_VALUE_ARRAY=Type.getType(byte[].class);

    public static final Type BOOLEAN = Type.getType(Boolean.class);
    public static final Type BOOLEAN_VALUE = Type.getType(boolean.class);

    public static final Type CHAR=Type.getType(char.class);
    public static final Type CHARACTER=Type.getType(Character.class);

    public static final Type DOUBLE = Type.getType(Double.class);
    public static final Type DOUBLE_VALUE = Type.getType(double.class);

    public static final Type FLOAT = Type.getType(Float.class);
    public static final Type FLOAT_VALUE = Type.getType(float.class);

    public static final Type IMAGE = Type.getType(Image.class);
    public static final Type INTEGER = Type.getType(Integer.class);
    public static final Type INT_VALUE = Type.getType(int.class);

    public static final Type LONG = Type.getType(Long.class);
    public static final Type LONG_VALUE = Type.getType(long.class);

    public static final Type SHORT = Type.getType(Short.class);
    public static final Type SHORT_VALUE = Type.getType(short.class);

    public static final Type COMPONENT=Type.getType(railo.runtime.Component.class);

    public final static Type PAGE=Type.getType(Page.class);
    public final static Type PAGE_PLUS=Type.getType(PagePlus.class);
    public final static Type PAGE_SOURCE=Type.getType(PageSource.class);
    public static final Type COMPONENT_PAGE=Type.getType(railo.runtime.ComponentPage.class);
	public static final Type INTERFACE_PAGE = Type.getType(InterfacePage.class);

    public static final Type COMPONENT_IMPL=Type.getType(railo.runtime.ComponentImpl.class);
    public static final Type INTERFACE_IMPL=Type.getType(railo.runtime.InterfaceImpl.class);
    
    public static final Type DATE_TIME=Type.getType(railo.runtime.type.dt.DateTime.class);
    
    public static final Type DATE=Type.getType(java.util.Date.class);

    public static final Type FILE=Type.getType(java.io.File.class);
    public static final Type EXCEL=Type.getType(Excel.class);
    public static final Type EXCEL_UTIL=Type.getType(ExcelUtil.class);
    
    public static final Type RESOURCE=Type.getType(Resource.class);
    
	public static final Type FUNCTION_VALUE = Type.getType(FunctionValue.class);

    public static final Type ITERATOR=Type.getType(Iterator.class);
    public static final Type ITERATORABLE=Type.getType(Iteratorable.class);

    public static final Type NODE=Type.getType(org.w3c.dom.Node.class);

    public static final Type OBJECT=Type.getType(Object.class);

    public static final Type OBJECT_ARRAY=Type.getType(Object[].class);

    public static final Type PAGE_CONTEXT=Type.getType(PageContext.class);
    public static final Type PAGE_CONTEXT_IMPL=Type.getType(PageContextImpl.class);


    public final static Type QUERY=Type.getType(railo.runtime.type.Query.class);
    public final static Type QUERY_COLUMN=Type.getType(railo.runtime.type.QueryColumn.class);
    
    public final static Type PAGE_EXCEPTION=Type.getType(PageException.class);

    public final static Type REFERENCE=Type.getType(Reference.class);

    public static final Type CASTER = Type.getType(Caster.class);

    public static final Type COLLECTION = Type.getType(Collection.class);
    
    public static final Type STRING = Type.getType(String.class);
    public static final Type STRING_ARRAY = Type.getType(String[].class);
    
    public static final Type STRUCT = Type.getType(railo.runtime.type.Struct.class);
    
    public static final Type OPERATOR = Type.getType(Operator.class);

    public static final Type CONFIG = Type.getType(Config.class);
    public static final Type CONFIG_WEB = Type.getType(ConfigWeb.class);

    public static final Type SCOPE = Type.getType(Scope.class);
    public static final Type VARIABLES = Type.getType(Variables.class);

    public static final Type TIMESPAN = Type.getType(railo.runtime.type.dt.TimeSpan.class);

	public static final Type THROWABLE = Type.getType(Throwable.class);
	public static final Type EXCEPTION = Type.getType(Exception.class);
	
	public static final Type VOID = Type.VOID_TYPE;
	
	public static final Type LIST_UTIL = Type.getType(ListUtil.class);
	public static final Type VARIABLE_INTERPRETER = Type.getType(VariableInterpreter.class);
	public static final Type VARIABLE_REFERENCE = Type.getType(VariableReference.class);
	public static final Type JSP_WRITER = Type.getType(JspWriter.class);
	public static final Type TAG = Type.getType(Tag.class);
	public static final Type NUMBER_RANGE = Type.getType(NumberRange.class);
	public static final Type SECURITY_MANAGER = Type.getType(SecurityManager.class);
	public static final Type READER = Type.getType(Reader.class);
	public static final Type BUFFERED_READER = Type.getType(BufferedReader.class);
	public static final Type ARRAY_UTIL = Type.getType(ArrayUtil.class);
	public static final Type EXCEPTION_HANDLER = Type.getType(ExceptionHandler.class);
	//public static final Type RETURN_ EXCEPTION = Type.getType(ReturnException.class);
	public static final Type TIMEZONE = Type.getType(java.util.TimeZone.class);
	public static final Type STRING_BUFFER = Type.getType(StringBuffer.class);
	public static final Type MEMBER = Type.getType(Member.class);
	public static final Type UDF = Type.getType(UDF.class);
	public static final Type UDF_PROPERTIES = Type.getType(UDFProperties.class);
	public static final Type UDF_PROPERTIES_IMPL = Type.getType(UDFPropertiesImpl.class);
	public static final Type UDF_IMPL = Type.getType(UDFImpl.class);
	public static final Type CLOSURE = Type.getType(Closure.class);
	public static final Type UDF_PROPERTIES_ARRAY = Type.getType(UDFProperties[].class);
	//public static final Type UDF_IMPL_ARRAY = Type.getType(UDFImpl[].class);
	public static final Type COLLECTION_KEY = Type.getType(Collection.Key.class);
	public static final Type COLLECTION_KEY_ARRAY = Type.getType(Collection.Key[].class);
	public static final Type UNDEFINED = Type.getType(Undefined.class);
	public static final Type MAP = Type.getType(Map.class);
	public static final Type MAP_ENTRY = Type.getType(Map.Entry.class);
	public static final Type CHAR_ARRAY = Type.getType(char[].class);
	public static final Type IOUTIL = Type.getType(IOUtil.class);
	public static final Type BODY_CONTENT = Type.getType(BodyContent.class);
	public static final Type BODY_CONTENT_UTIL = Type.getType(BodyContentUtil.class);
	public static final Type IMPORT_DEFINITIONS = Type.getType(ImportDefintion.class);
	public static final Type IMPORT_DEFINITIONS_IMPL = Type.getType(ImportDefintionImpl.class);
	public static final Type IMPORT_DEFINITIONS_ARRAY = Type.getType(ImportDefintion[].class);
	public static final Type CLASS = Type.getType(Class.class);
	public static final Type CLASS_ARRAY = Type.getType(Class[].class);
	public static final Type CLASS_LOADER = Type.getType(ClassLoader.class);
	public static final Type BIG_DECIMAL = Type.getType(BigDecimal.class);
	 

	/**
	 * translate sString classname to a real type
	 * @param type
	 * @return
	 * @throws railo.runtime.exp.TemplateExceptionption 
	 */
	public static Type toType(String type) throws BytecodeException {
		if(type==null) return OBJECT;
		type=type.trim();
		String lcType=StringUtil.toLowerCase(type);
		char first=lcType.charAt(0);
		        
        switch(first) {
        case 'a':
            if("any".equals(lcType))								return OBJECT;
            if("array".equals(lcType))								return ARRAY;
        break;
        case 'b':
            if("base64".equals(lcType))								return STRING;
            if("binary".equals(lcType))								return BYTE_VALUE_ARRAY;
            if("bool".equals(lcType) || "boolean".equals(type))		return BOOLEAN_VALUE;
            if("boolean".equals(lcType))							return BOOLEAN;
            if("byte".equals(type))									return BYTE_VALUE;
            if("byte".equals(lcType)) 								return BYTE;
        break;
        case 'c':
            if("char".equals(lcType)) 								return CHAR;
            if("character".equals(lcType))							return CHARACTER;
            if("collection".equals(lcType))							return BYTE_VALUE_ARRAY;
            if("component".equals(lcType))							return COMPONENT;
        break;
        case 'd':
            if("date".equals(lcType) || "datetime".equals(lcType))	return DATE_TIME;
            if("decimal".equals(lcType))							return STRING;
            if("double".equals(type)) 								return DOUBLE_VALUE;
            if("double".equals(lcType))								return DOUBLE;
        break;
        case 'e':
            if("excel".equals(lcType)) 								return EXCEL;
        break;
        case 'f':
            if("file".equals(lcType)) 								return FILE;
            if("float".equals(type))								return FLOAT_VALUE;
            if("float".equals(lcType))								return FLOAT;
            if("function".equals(lcType))								return UDF;
        break;
        case 'i':
            if("int".equals(lcType))								return INT_VALUE;
            else if("integer".equals(lcType))						return INTEGER;
            else if("image".equals(lcType))							return IMAGE;
        break;
        case 'j':
            if("java.lang.boolean".equals(lcType))					return BOOLEAN;
            if("java.lang.byte".equals(lcType))						return BYTE;
            if("java.lang.character".equals(lcType)) 				return CHARACTER;
            if("java.lang.short".equals(lcType))					return SHORT;
            if("java.lang.integer".equals(lcType))					return INTEGER;
            if("java.lang.long".equals(lcType))						return LONG;
            if("java.lang.float".equals(lcType))					return FLOAT;
            if("java.lang.double".equals(lcType))					return DOUBLE;
            if("java.io.file".equals(lcType))						return FILE;
            if("java.lang.string".equals(lcType))					return STRING;
            if("java.lang.string[]".equals(lcType))					return STRING_ARRAY;
            if("java.util.date".equals(lcType))						return DATE; 
            if("java.lang.object".equals(lcType))					return OBJECT; 
        break;
        case 'l':
            if("long".equals(type))									return LONG_VALUE;
            if("long".equals(lcType))								return LONG;
            if("long".equals(lcType))								return LONG;
        break;
        case 'n':
            if("node".equals(lcType))								return NODE;
            if("number".equals(lcType))								return DOUBLE_VALUE;
            if("numeric".equals(lcType))							return DOUBLE_VALUE;
        break;
        case 'o':
            if("object".equals(lcType))								return OBJECT;
        break;
        case 's':
            if("string".equals(lcType))								return STRING;
            if("struct".equals(lcType))								return STRUCT;
            if("short".equals(type))								return SHORT_VALUE;
            if("short".equals(lcType))								return SHORT;
        break;
        case 'u':
            if("udf".equals(lcType))								return UDF;
        break;
        case 'v':
            if("void".equals(lcType))								return VOID;
    	    if("variablestring".equals(lcType)) 					return STRING;
    	    if("variable_string".equals(lcType)) 					return STRING;
        break;
        case 'x':
            if("xml".equals(lcType)) 								return NODE;
        break;
        case '[':
            if("[Ljava.lang.String;".equals(lcType)) 				return STRING_ARRAY;
        break;
        
        
        
        default:
            if("query".equals(lcType))								return QUERY;
            if("querycolumn".equals(lcType))						return QUERY_COLUMN;
            if("timespan".equals(lcType))							return TIMESPAN;
        }
        
        // TODO Array als Lbyte und auch byte[]
        
		try {
			return Type.getType(ClassUtil.loadClass(type));
		} catch (ClassException e) {
			throw new BytecodeException(e,null);
		}
	}

	/**
	 * returns if given type is a "primitve" type or in other words a value type (no reference type, no object)
	 * @param type
	 * @return
	 */
	public static boolean isPrimitiveType(int type) {
		return type!=_OBJECT && type!=_STRING;
	}
	
	/**
	 * returns if given type is a "primitve" type or in other words a value type (no reference type, no object)
	 * @param type
	 * @return
	 */
	public static boolean isPrimitiveType(Type type) {
		String className=type.getClassName();
		if(className.indexOf('.')!=-1) return false;

		if("boolean".equals(className)) return true;
		if("short".equals(className)) return true;
		if("float".equals(className)) return true;
		if("long".equals(className)) return true;
		if("double".equals(className)) return true;
		if("char".equals(className)) return true;
		if("int".equals(className)) return true;
		if("byte".equals(className)) return true;
		
		return false;
	}
	public static int getType(Type type) {
		String className=type.getClassName();
		if(className.indexOf('.')!=-1) {
			if("java.lang.String".equalsIgnoreCase(className)) return _STRING;
			return _OBJECT;
		}

		if("boolean".equals(className)) return _BOOLEAN;
		if("short".equals(className)) return _SHORT;
		if("float".equals(className)) return _FLOAT;
		if("long".equals(className)) return _LONG;
		if("double".equals(className)) return _DOUBLE;
		if("char".equals(className)) return _CHAR;
		if("int".equals(className)) return _INT;
		if("byte".equals(className)) return _BYTE; 
		
		return _OBJECT;
	}

	public static Type toRefType(Type type) {
		String className=type.getClassName();
		if(className.indexOf('.')!=-1) return type;

		if("boolean".equals(className)) return BOOLEAN;
		if("short".equals(className)) return SHORT;
		if("float".equals(className)) return FLOAT;
		if("long".equals(className)) return LONG;
		if("double".equals(className)) return DOUBLE;
		if("char".equals(className)) return CHARACTER;
		if("int".equals(className)) return INT_VALUE;
		if("byte".equals(className)) return BYTE;
		return type;
	}

	public static Class toClass(Type type) throws ClassException {
		if(Types.STRING==type) return String.class;
		if(Types.BOOLEAN_VALUE==type) return boolean.class;
		if(Types.DOUBLE_VALUE==type) return double.class;
		if(Types.PAGE_CONTEXT==type) return PageContext.class;
		if(Types.OBJECT==type) return Object.class;
		if(Types.STRUCT==type) return Struct.class;
		if(Types.ARRAY==type) return Array.class;
		if(Types.COLLECTION_KEY==type) return Collection.Key.class;
		if(Types.COLLECTION_KEY_ARRAY==type) return Collection.Key[].class;
		if(Types.QUERY==type) return Query.class;
		if(Types.DATE_TIME==type) return railo.runtime.type.dt.DateTime.class;
		
		
		return ClassUtil.toClass(type.getClassName());
	}
	

}
