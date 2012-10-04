package railo.runtime.orm.hibernate;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.Type;

import railo.commons.lang.StringUtil;
import railo.commons.lang.types.RefBoolean;
import railo.runtime.Component;
import railo.runtime.ComponentScope;
import railo.runtime.PageContext;
import railo.runtime.component.Property;
import railo.runtime.db.SQLCaster;
import railo.runtime.db.SQLItemImpl;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.orm.ORMEngine;
import railo.runtime.orm.ORMException;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Query;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.cfc.ComponentAccess;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.util.ComponentUtil;
import railo.runtime.type.util.QueryUtil;

public class HibernateCaster {
	
	private static final int NULL = -178696;
	private static final Key ENTITY_NAME = KeyImpl.intern("entityname");

	public static Object toCFML(Object src) {
		if(src==null) return null;
		if(src instanceof Collection) return src;
		
		if(src instanceof List){
			return toCFML((List) src);
		}
		/*if(src instanceof Map){
			return toCFML(pc,(Map) src);
		}*/
		return src;
	}
	
	public static Array toCFML(List src)  {
        int size=src.size();
        
        Array trg = new ArrayImpl();
        for(int i=0;i<size;i++) {
            trg.setEL(i+1,toCFML(src.get(i)));
        }
        return trg;
	}
	
	/*public static Object toCFML(PageContext pc,Map src) throws PageException {
		
		Object type =src.remove("$type$");
		if(type instanceof String){
			
			Component cfc = toComponent(pc, (String)type);
			return toCFML(pc,src, cfc);
		}
		
		
		Iterator<Map.Entry<String, Object>> it = src.entrySet().iterator();
        Struct trg=new StructImpl();
        Map.Entry<String, Object> entry;
        while(it.hasNext()){
			entry=it.next();
            trg.setEL(entry.getKey(),toCFML(pc,entry.getValue()));
        }
        return trg;
	}*/
	

	
	public static String getEntityName(Component cfc) {
		
		String name=null;
		try {
			ComponentAccess cfca = ComponentUtil.toComponentAccess(cfc);
			name=Caster.toString(cfca.getMetaStructItem(ENTITY_NAME),null);
		} 
		catch (Throwable t) {
			try {
				Struct md = cfc.getMetaData(ThreadLocalPageContext.get());
				name = Caster.toString(md.get(ENTITY_NAME),null);
				
			}catch (PageException e) {}
		}
		
		if(!StringUtil.isEmpty(name)) {
			return name;
		}
		return getName(cfc);
		
		
	}

	private static String getName(Component cfc) {
		String name=null;
		// MUSTMUST cfc.getName() should return the real case, this should not be needed
		name = cfc.getPageSource().getDisplayPath();
	    name=railo.runtime.type.List.last(name, "\\/",true);
	    int index=name.lastIndexOf('.');
	    name= name.substring(0,index);
		return name;
	}

	public static int cascade(HibernateORMEngine engine,String cascade) throws ORMException {
		int c=cascade(cascade,-1);
		if(c!=-1) return c;
		throw new HibernateException(engine,"invalid cascade defintion ["+cascade+"], valid values are [all,all-delete-orphan,delete,delete-orphan,refresh,save-update]");
	}
	
	public static int cascade(String cascade, int defaultValue) {
		cascade=cascade.trim().toLowerCase();
		if("all".equals(cascade)) return HibernateConstants.CASCADE_ALL;
		
		if("save-update".equals(cascade)) return HibernateConstants.CASCADE_SAVE_UPDATE;
		if("save_update".equals(cascade)) return HibernateConstants.CASCADE_SAVE_UPDATE;
		if("saveupdate".equals(cascade)) return HibernateConstants.CASCADE_SAVE_UPDATE;
		
		if("delete".equals(cascade)) return HibernateConstants.CASCADE_DELETE;
		
		if("delete-orphan".equals(cascade)) return HibernateConstants.CASCADE_DELETE_ORPHAN;
		if("delete_orphan".equals(cascade)) return HibernateConstants.CASCADE_DELETE_ORPHAN;
		if("deleteorphan".equals(cascade)) return HibernateConstants.CASCADE_DELETE_ORPHAN;

		if("all-delete-orphan".equals(cascade)) return HibernateConstants.CASCADE_ALL_DELETE_ORPHAN;
		if("all_delete_orphan".equals(cascade)) return HibernateConstants.CASCADE_ALL_DELETE_ORPHAN;
		if("alldeleteorphan".equals(cascade)) return HibernateConstants.CASCADE_ALL_DELETE_ORPHAN;
		
		if("refresh".equals(cascade)) return HibernateConstants.REFRESH;
		
		return defaultValue;
	}

	public static int collectionType(HibernateORMEngine engine,String strCollectionType) throws ORMException {
		int ct=collectionType(strCollectionType, -1);
		if(ct!=-1) return ct;
		throw new ORMException(engine,"invalid collectionType defintion ["+strCollectionType+"], valid values are [array,struct]");
	}
	public static int collectionType(String strCollectionType, int defaultValue) {
		strCollectionType=strCollectionType.trim().toLowerCase();
		if("struct".equals(strCollectionType)) return HibernateConstants.COLLECTION_TYPE_STRUCT;
		if("array".equals(strCollectionType)) return HibernateConstants.COLLECTION_TYPE_ARRAY;
		
		return defaultValue;
	}

	public static String toHibernateType(ColumnInfo info, String type, String defaultValue)	{
		
		// no type defined
		if(StringUtil.isEmpty(type,true)) {
			return HibernateCaster.toHibernateType(info,defaultValue);
		}
		
		// type defined
		String tmp=HibernateCaster.toHibernateType(type,null);
		if(tmp!=null) return tmp;
		
		if(info!=null){
			tmp=HibernateCaster.toHibernateType(info,defaultValue);
			//ORMUtil.printError("type ["+type+"] is not a valid Hibernate type. Use instead type ["+tmp+"]", engine);
			return tmp;
		}
		//throw new ORMException("type ["+type+"] is not a valid Hibernate type.");
		return defaultValue;
		
		
	}
	
	public static int toSQLType(String type,int defaultValue) 	{
		type=type.trim().toLowerCase();
		type=toHibernateType(type,type);
		if("long".equals(type)) return Types.BIGINT;
		if("binary".equals(type)) return Types.BINARY;
		if("boolean".equals(type)) return Types.BIT;
		if("blob".equals(type)) return Types.BLOB;
		if("boolean".equals(type)) return Types.BOOLEAN;
		if("character".equals(type)) return Types.CHAR;
		if("clob".equals(type)) return Types.CLOB;
		if("date".equals(type)) return Types.DATE;
		if("big_decimal".equals(type)) return Types.DECIMAL;
		if("big_integer".equals(type)) return Types.NUMERIC;
		if("double".equals(type)) return Types.DOUBLE;
		if("float".equals(type)) return Types.FLOAT;
		if("integer".equals(type)) return Types.INTEGER;
		if("binary".equals(type)) return Types.VARBINARY;
		if("string".equals(type)) return Types.VARCHAR;
		if("short".equals(type)) return Types.SMALLINT;
		if("time".equals(type)) return Types.TIME;
		if("timestamp".equals(type)) return Types.TIMESTAMP;
		if("byte".equals(type)) return Types.TINYINT;
		
		return defaultValue;
	}
	
	public static String toHibernateType(ColumnInfo info, String defaultValue)	{
		if(info==null)return defaultValue;
		
		String rtn = toHibernateType(info.getType(),info.getSize(), null);
		if(rtn!=null) return rtn;
		return toHibernateType(info.getTypeName(),defaultValue);
	}
	
	public static String toHibernateType(int type, int size, String defaultValue) {
		// MUST do better
		switch(type){
		case Types.ARRAY: return "";
		case Types.BIGINT: return "long";
		case Types.BINARY: return "binary";
		case Types.BIT: return "boolean";
		case Types.BLOB: return "blob";
		case Types.BOOLEAN: return "boolean";
		case Types.CHAR:
			return "string";
			//if(size>1) return "string";
			//return "character";
		case Types.CLOB: return "clob";
		//case Types.DATALINK: return "";
		case Types.DATE: return "date";
		case Types.DECIMAL: return "big_decimal";
		//case Types.DISTINCT: return "";
		case Types.DOUBLE: return "double";
		case Types.FLOAT: return "float";
		case Types.INTEGER: return "integer";
		//case Types.JAVA_OBJECT: return "";
		case Types.LONGVARBINARY: return "binary";
		case Types.LONGVARCHAR: return "string";
		//case Types.NULL: return "";
		case Types.NUMERIC: return "big_decimal";
		//case Types.OTHER: return "";
		//case Types.REAL: return "";
		//case Types.REF: return "";
		case Types.SMALLINT: return "short";
		//case Types.STRUCT: return "";
		case Types.TIME: return "time";
		case Types.TIMESTAMP: return "timestamp";
		case Types.TINYINT: return "byte";
		case Types.VARBINARY: return "binary";
		case Types.NVARCHAR: return "string";
		case Types.VARCHAR: return "string";
		}
		return defaultValue;
	}
	
	public static String toHibernateType(HibernateORMEngine engine,String type) throws ORMException	{
		String res=toHibernateType(type, null);
		if(res==null) throw new ORMException(engine,"the type ["+type+"] is not supported");
		return res;
	}
	
	
	// calendar_date: A type mapping for a Calendar object that represents a date
	//calendar: A type mapping for a Calendar object that represents a datetime.
	public static String toHibernateType(String type, String defaultValue)	{
		type=type.trim().toLowerCase();
		type=StringUtil.replace(type, "java.lang.", "", true);
		type=StringUtil.replace(type, "java.util.", "", true);
		type=StringUtil.replace(type, "java.sql.", "", true);
		
		// return same value
		if("long".equals(type)) return type;
		if("binary".equals(type)) return type;
		if("boolean".equals(type)) return type;
		if("blob".equals(type)) return "binary";
		if("boolean".equals(type)) return type;
		if("character".equals(type)) return type;
		if("clob".equals(type)) return "text";
		if("date".equals(type)) return type;
		if("big_decimal".equals(type)) return type;
		if("double".equals(type)) return type;
		if("float".equals(type)) return type;
		if("integer".equals(type)) return type;
		if("binary".equals(type)) return type;
		if("string".equals(type)) return type;
		if("big_integer".equals(type)) return type;
		if("short".equals(type)) return type;
		if("time".equals(type)) return type;
		if("timestamp".equals(type)) return type;
		if("byte".equals(type)) return type;
		if("binary".equals(type)) return type;
		if("string".equals(type)) return type;
		if("text".equals(type)) return type;
		if("calendar".equals(type)) return type;
		if("calendar_date".equals(type)) return type;
		if("locale".equals(type)) return type;
		if("timezone".equals(type)) return type;
		if("currency".equals(type)) return type;
		
		if("imm_date".equals(type)) return type;
		if("imm_time".equals(type)) return type;
		if("imm_timestamp".equals(type)) return type;
		if("imm_calendar".equals(type)) return type;
		if("imm_calendar_date".equals(type)) return type;
		if("imm_serializable".equals(type)) return type;
		if("imm_binary".equals(type)) return type;
		
		// return different value
		if("bigint".equals(type)) 						return "long";
		if("bit".equals(type)) 						return "boolean";
		
		if("int".equals(type)) 						return "integer";
		if("char".equals(type)) 					return "character";
		
		if("bool".equals(type)) 					return "boolean";
		if("yes-no".equals(type)) 					return "yes_no";
		if("yesno".equals(type)) 					return "yes_no";
		if("yes_no".equals(type)) 					return "yes_no";
		if("true-false".equals(type)) 				return "true_false";
		if("truefalse".equals(type)) 				return "true_false";
		if("true_false".equals(type)) 				return "true_false";
		if("varchar".equals(type)) 					return "string";
		if("big-decimal".equals(type)) 				return "big_decimal";
		if("bigdecimal".equals(type)) 				return "big_decimal";
		if("java.math.bigdecimal".equals(type)) 	return "big_decimal";
		if("big-integer".equals(type)) 				return "big_integer";
		if("biginteger".equals(type)) 				return "big_integer";
		if("bigint".equals(type)) 				return "big_integer";
		if("java.math.biginteger".equals(type)) 	return "big_integer";
		if("byte[]".equals(type)) 					return "binary";
		if("serializable".equals(type)) 			return "serializable";
		
		if("datetime".equals(type)) 				return "timestamp";
		if("numeric".equals(type)) 					return "double";
		if("number".equals(type)) 					return "double";
		if("numeric".equals(type)) 					return "double";
		if("char".equals(type)) 					return "character";
		if("nchar".equals(type)) 					return "character";
		if("decimal".equals(type)) 					return "double";
		if("eurodate".equals(type)) 				return "timestamp";
		if("usdate".equals(type)) 				return "timestamp";
		if("int".equals(type)) 						return "integer";
		if("varchar".equals(type)) 						return "string";
		if("nvarchar".equals(type)) 						return "string";
		
		return defaultValue;
		
		// FUTURE
		/*
		
		add support for 
		- any, object,other
		
		add support for custom types https://issues.jboss.org/browse/RAILO-1341
		- array
	    - base64
	    - guid
        - memory
	    - node, xml
	    - query
	    - struct
        - uuid
        - variablename, variable_name
	    - variablestring, variable_string
	    
		*/
		
    }

	/**
	 * translate CFMl specific types to Hibernate/SQL specific types
	 * @param engine
	 * @param ci
	 * @param value
	 * @return
	 * @throws PageException
	 */
	public static Object toSQL(HibernateORMEngine engine,ColumnInfo ci, Object value, RefBoolean isArray) throws PageException {
		return toSQL(engine, ci.getType(), value,isArray);
	}
	
	/**
	 * translate CFMl specific types to Hibernate/SQL specific types
	 * @param engine
	 * @param type
	 * @param value
	 * @return
	 * @throws PageException
	 */
	public static Object toSQL(HibernateORMEngine engine,Type type, Object value, RefBoolean isArray) throws PageException {
		int t = toSQLType(type.getName(), Types.OTHER);
		if(t==Types.OTHER) return value;
		return toSQL(engine, t, value,isArray);
	}

	/**
	 * translate CFMl specific type to SQL specific types
	 * @param engine
	 * @param sqlType
	 * @param value
	 * @return
	 * @throws PageException
	 */
	private static Object toSQL(HibernateORMEngine engine,int sqlType, Object value, RefBoolean isArray) throws PageException {
		if(isArray!=null)isArray.setValue(false);
		SQLItemImpl item = new SQLItemImpl(value,sqlType);
		try{
			return SQLCaster.toSqlType(item);
		}
		catch(PageException pe){
			// pherhaps it is a array of this type 
			if(isArray!=null && Decision.isArray(value)) {
				Object[] src = Caster.toNativeArray(value);
				ArrayList<Object> trg = new ArrayList<Object>();
				for(int i=0;i<src.length;i++){
					try{
						trg.add(SQLCaster.toSqlType(new SQLItemImpl(src[i],sqlType)));
					}
					catch(PageException inner){
						throw pe;
					}
				}
				isArray.setValue(true);
				return ArrayUtil.toArray(trg);
				
			}
			throw pe;
		}
		
	}


	public static railo.runtime.type.Query toQuery(PageContext pc,HibernateORMSession session, Object obj, String name) throws PageException {
		Query qry=null;
		// a single entity
		if(!Decision.isArray(obj)){
			qry= toQuery(pc,session,HibernateCaster.toComponent(obj),name,null,1,1);
		}
		
		// a array of entities
		else {
			Array arr=Caster.toArray(obj);
			int len=arr.size();
			if(len>0) {
				Iterator it = arr.valueIterator();
				int row=1;
				while(it.hasNext()){
					qry=toQuery(pc,session,HibernateCaster.toComponent(it.next()),name,qry,len,row++);
				}
			}
			else 
				qry=new QueryImpl(new Collection.Key[0],0,"orm");
		}
		
		if(qry==null) {
			if(!StringUtil.isEmpty(name))
				throw new ORMException(session.getEngine(),"there is no entity inheritance that match the name ["+name+"]");
			throw new ORMException(session.getEngine(),"cannot create query");
		}
		return qry;
	}
	
	private static Query toQuery(PageContext pc,HibernateORMSession session,Component cfc, String entityName,Query qry, int rowcount, int row) throws PageException {
		// inheritance mapping
		if(!StringUtil.isEmpty(entityName)){
			//String cfcName = toComponentName(HibernateCaster.toComponent(pc, entityName));
			return inheritance(pc,session,cfc,qry, entityName);
		}
		return populateQuery(pc,session,cfc,qry);
	}




	private static Query populateQuery(PageContext pc,HibernateORMSession session,Component cfc,Query qry) throws PageException {
		Property[] properties = ComponentUtil.getProperties(cfc,true,true);
		ComponentScope scope = cfc.getComponentScope();
		HibernateORMEngine engine=(HibernateORMEngine) session.getEngine();
		
		// init
		if(qry==null){
			ClassMetadata md = ((HibernateORMEngine)session.getEngine()).getSessionFactory(pc).getClassMetadata(getEntityName(cfc));
			//Struct columnsInfo= engine.getTableInfo(session.getDatasourceConnection(),toEntityName(engine, cfc),session.getEngine());
			Array names=new ArrayImpl();
			Array types=new ArrayImpl();
			String name;
			//ColumnInfo ci;
			int t;
			for(int i=0;i<properties.length;i++){
				name=HibernateUtil.validateColumnName(md, properties[i].getName(),null);
				//if(columnsInfo!=null)ci=(ColumnInfo) columnsInfo.get(name,null);
				//else ci=null;
				names.append(name);
				if(name!=null){
					
					t=HibernateCaster.toSQLType(HibernateUtil.getPropertyType(md, name).getName(), NULL);
					if(t==NULL)
						types.append("object");
					else
						types.append(SQLCaster.toStringType(t));
				}
				else 
					types.append("object");
			}
			
			qry=new QueryImpl(names,types,0,getEntityName(cfc));
			
		}
		// check
		else if(engine.getMode() == ORMEngine.MODE_STRICT){
			if(!qry.getName().equals(getEntityName(cfc)))
				throw new ORMException(session.getEngine(),"can only merge entities of the same kind to a query");
		}
		
		// populate
		Key[] names=QueryUtil.getColumnNames(qry);
		
		
		int row=qry.addRow();
		for(int i=0;i<names.length;i++){
			qry.setAtEL(names[i], row, scope.get(names[i],null));
		}
		return qry;
	}




	private static Query inheritance(PageContext pc,HibernateORMSession session,Component cfc,Query qry, String entityName) throws PageException {
		Property[] properties = cfc.getProperties(true);
		ComponentScope scope = cfc.getComponentScope();
		String name;
		Object value;
		Array arr;
		for(int i=0;i<properties.length;i++){
			name=properties[i].getName();
			value=scope.get(name,null);
			if(value instanceof Component){
				qry=inheritance(pc,session,qry,cfc,(Component) value,entityName);
			}
			else if(Decision.isArray(value)){
				arr = Caster.toArray(value);
				Iterator it = arr.valueIterator();
				while(it.hasNext()){
					value=it.next();
					if(value instanceof Component){
						qry=inheritance(pc,session,qry,cfc,(Component) value,entityName);
					}
				}
			}
		}
		return qry;
	}




	private static Query inheritance(PageContext pc,HibernateORMSession session,Query qry,Component parent,Component child,String entityName) throws PageException {
		if(getEntityName(child).equalsIgnoreCase(entityName))
			return populateQuery(pc,session,child,qry);
		return inheritance(pc,session,child, qry, entityName);// MUST geh ACF auch so tief?
	}


	/**
	 * return the full name (package and name) of a component
	 * @param cfc
	 * @return
	 */
	public static String toComponentName(Component cfc) {
		return cfc.getPageSource().getComponentName();
	}
	
	public static Component toComponent(Object obj) throws PageException {
		return Caster.toComponent(obj);
	}


	/*public static Component toComponent(PageContext pc, Object obj) throws PageException {
		if(obj instanceof String)
			return toComponent(pc, (String)obj);
		 return Caster.toComponent(obj);
	}*/
	/*public static Component toComponent(PageContext pc, String name) throws PageException {
		// MUST muss �ber cfcs kommen oder neues init machen
		return CreateObject.doComponent(pc, name);
	}*/
}
