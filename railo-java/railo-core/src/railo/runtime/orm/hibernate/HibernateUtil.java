package railo.runtime.orm.hibernate;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.Type;

import railo.commons.lang.StringUtil;
import railo.runtime.ComponentPro;
import railo.runtime.component.Property;
import railo.runtime.db.DatasourceConnection;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.orm.ORMException;
import railo.runtime.type.Array;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.List;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;


public class HibernateUtil {

	public static final short FIELDTYPE_ID=0;
	public static final short FIELDTYPE_COLUMN=1;
	public static final short FIELDTYPE_TIMESTAMP=2;
	public static final short FIELDTYPE_RELATION=4;
	public static final short FIELDTYPE_VERSION=8;
	public static final short FIELDTYPE_COLLECTION=16;
	
	private static final String KEYWORDS="absolute,access,accessible,action,add,after,alias,all,allocate,allow,alter,analyze,and,any,application,are,array,as,asc,asensitive,assertion,associate,asutime,asymmetric,at,atomic,audit,authorization,aux,auxiliary,avg,backup,before,begin,between,bigint,binary,bit,bit_length,blob,boolean,both,breadth,break,browse,bufferpool,bulk,by,cache,call,called,capture,cardinality,cascade,cascaded,case,cast,catalog,ccsid,change,char,char_length,character,character_length,check,checkpoint,clob,close,cluster,clustered,coalesce,collate,collation,collection,collid,column,comment,commit,compress,compute,concat,condition,connect,connection,constraint,constraints,constructor,contains,containstable,continue,convert,corresponding,count,count_big,create,cross,cube,current,current_date,current_default_transform_group,current_lc_ctype,current_path,current_role,current_server,current_time,current_timestamp,current_timezone,current_transform_group_for_type,current_user,cursor,cycle,data,database,databases,date,day,day_hour,day_microsecond,day_minute,day_second,days,db2general,db2genrl,db2sql,dbcc,dbinfo,deallocate,dec,decimal,declare,default,defaults,deferrable,deferred,delayed,delete,deny,depth,deref,desc,describe,descriptor,deterministic,diagnostics,disallow,disconnect,disk,distinct,distinctrow,distributed,div,do,domain,double,drop,dsnhattr,dssize,dual,dummy,dump,dynamic,each,editproc,else,elseif,enclosed,encoding,end,end-exec,end-exec1,endexec,equals,erase,errlvl,escape,escaped,except,exception,excluding,exclusive,exec,execute,exists,exit,explain,external,extract,false,fenced,fetch,fieldproc,file,fillfactor,filter,final,first,float,float4,float8,for,force,foreign,found,free,freetext,freetexttable,from,full,fulltext,function,general,generated,get,get_current_connection,global,go,goto,grant,graphic,group,grouping,handler,having,high_priority,hold,holdlock,hour,hour_microsecond,hour_minute,hour_second,hours,identified,identity,identity_insert,identitycol,if,ignore,immediate,in,including,increment,index,indicator,infile,inherit,initial,initially,inner,inout,input,insensitive,insert,int,int1,int2,int3,int4,int8,integer,integrity,intersect,interval,into,is,isobid,isolation,iterate,jar,java,join,key,keys,kill,language,large,last,lateral,leading,leave,left,level,like,limit,linear,lineno,lines,linktype,load,local,locale,localtime,localtimestamp,locator,locators,lock,lockmax,locksize,long,longblob,longint,longtext,loop,low_priority,lower,ltrim,map,master_ssl_verify_server_cert,match,max,maxextents,maxvalue,mediumblob,mediumint,mediumtext,method,microsecond,microseconds,middleint,min,minus,minute,minute_microsecond,minute_second,minutes,minvalue,mlslabel,mod,mode,modifies,modify,module,month,months,names,national,natural,nchar,nclob,new,new_table,next,no,no_write_to_binlog,noaudit,nocache,nocheck,nocompress,nocycle,nodename,nodenumber,nomaxvalue,nominvalue,nonclustered,none,noorder,not,nowait,null,nullif,nulls,number,numeric,numparts,nvarchar,obid,object,octet_length,of,off,offline,offsets,old,old_table,on,online,only,open,opendatasource,openquery,openrowset,openxml,optimization,optimize,option,optionally,or,order,ordinality,out,outer,outfile,output,over,overlaps,overriding,package,pad,parameter,part,partial,partition,path,pctfree,percent,piecesize,plan,position,precision,prepare,preserve,primary,print,prior,priqty,privileges,proc,procedure,program,psid,public,purge,queryno,raiserror,range,raw,read,read_write,reads,readtext,real,reconfigure,recovery,recursive,ref,references,referencing,regexp,relative,release,rename,repeat,replace,replication,require,resignal,resource,restart,restore,restrict,result,result_set_locator,return,returns,revoke,right,rlike,role,rollback,rollup,routine,row,rowcount,rowguidcol,rowid,rownum,rows,rrn,rtrim,rule,run,runtimestatistics,save,savepoint,schema,schemas,scope,scratchpad,scroll,search,second,second_microsecond,seconds,secqty,section,security,select,sensitive,separator,session,session_user,set,sets,setuser,share,show,shutdown,signal,similar,simple,size,smallint,some,source,space,spatial,specific,specifictype,sql,sql_big_result,sql_calc_found_rows,sql_small_result,sqlcode,sqlerror,sqlexception,sqlid,sqlstate,sqlwarning,ssl,standard,start,starting,state,static,statistics,stay,stogroup,stores,straight_join,style,subpages,substr,substring,successful,sum,symmetric,synonym,sysdate,sysfun,sysibm,sysproc,system,system_user,table,tablespace,temporary,terminated,textsize,then,time,timestamp,timezone_hour,timezone_minute,tinyblob,tinyint,tinytext,to,top,trailing,tran,transaction,translate,translation,treat,trigger,trim,true,truncate,tsequal,type,uid,under,undo,union,unique,unknown,unlock,unnest,unsigned,until,update,updatetext,upper,usage,use,user,using,utc_date,utc_time,utc_timestamp,validate,validproc,value,values,varbinary,varchar,varchar2,varcharacter,variable,variant,varying,vcat,view,volumes,waitfor,when,whenever,where,while,window,with,within,without,wlm,work,write,writetext,xor,year,year_month,zerofill,zone";
	private static final Set<String> keywords=new HashSet<String>();
	private static final Key FIELDTYPE = KeyImpl.intern("fieldtype");
	static {
		Array arr = List.listToArray(KEYWORDS, ',');
		Iterator<String> it = arr.valueIterator();
		while(it.hasNext()){
			keywords.add(it.next());
		}
	}
	
	
	public static boolean isKeyword(String word){
		if(word==null) return false;
		return keywords.contains(word.trim().toLowerCase());
	}
	
	
	public static Type getPropertyType(ClassMetadata metaData, String name) throws HibernateException {
		try{
			return  metaData.getPropertyType(name);
		}
		catch(HibernateException he){
			if(name.equalsIgnoreCase(metaData.getIdentifierPropertyName())) 
				return metaData.getIdentifierType();
			
			String[] names = metaData.getPropertyNames();
			for(int i=0;i<names.length;i++){
				if(names[i].equalsIgnoreCase(name))
					return metaData.getPropertyType(names[i]);
			}
			throw he;
		}
	}
	public static Type getPropertyType(ClassMetadata metaData, String name, Type defaultValue) {
		try{
			return  metaData.getPropertyType(name);
		}
		catch(HibernateException he){
			if(name.equalsIgnoreCase(metaData.getIdentifierPropertyName())) 
				return metaData.getIdentifierType();
			
			String[] names = metaData.getPropertyNames();
			for(int i=0;i<names.length;i++){
				if(names[i].equalsIgnoreCase(name))
					return metaData.getPropertyType(names[i]);
			}
			return defaultValue;
		}
	}
	
	public static String validateColumnName(ClassMetadata metaData, String name) throws ORMException {
		String res = validateColumnName(metaData, name,null);
		if(res!=null) return res;
		throw new ORMException("invalid name, there is no property with name ["+name+"] in the entity ["+metaData.getEntityName()+"]",
				"valid properties names are ["+railo.runtime.type.List.arrayToList(metaData.getPropertyNames(), ", ")+"]");
		
	}
	

	public static String validateColumnName(ClassMetadata metaData, String name, String defaultValue) {
		if(name.equalsIgnoreCase(metaData.getIdentifierPropertyName())) 
			return metaData.getIdentifierPropertyName();
		
		String[] names = metaData.getPropertyNames();
		for(int i=0;i<names.length;i++){
			if(names[i].equalsIgnoreCase(name))
				return names[i];
		}
		return defaultValue;
	}
	
	// 
	
	public static Property[] createPropertiesFromTable(DatasourceConnection dc, String tableName) throws ORMException, PageException {
		Struct properties = new StructImpl();
		try {
			DatabaseMetaData md = dc.getConnection().getMetaData();
			String dbName=dc.getDatasource().getDatabase();
			String name;
			
			
			// get all columns
			ResultSet res = md.getColumns(dbName, null, tableName, null);
			Property p;
			while(res.next()) {
				name=res.getString("COLUMN_NAME");
				p=new Property();
				p.setName(name);
				p.setType(res.getString("TYPE_NAME"));
				properties.setEL(name, p);
			}
			
			// ids
			res = md.getPrimaryKeys(null, null, tableName);
			while(res.next()) {
				name=res.getString("COLUMN_NAME");
				p=(Property) properties.get(name,null);
				if(p!=null) p.getMeta().setEL("fieldtype", "id");
			}
			
			// MUST foreign-key relation
		
		}
		catch(Throwable t){
			return new Property[0];
		}
		
		Iterator it = properties.valueIterator();
		Property[] rtn=new Property[properties.size()];
		for(int i=0;i<rtn.length;i++){
			rtn[i]=(Property) it.next();
		}
		
    	return rtn;
	}


	public static Property[] getProperties(ComponentPro component,int fieldType, Property[] defaultValue) {
		Property[] props = component.getProperties(true);
		java.util.List<Property> rtn=new ArrayList<Property>();
		
		if(props!=null) {
			for(int i=0;i<props.length;i++){
				if(fieldType==getFieldType(props[i],FIELDTYPE_COLUMN))
					rtn.add(props[i]);
			}
		}
		return rtn.toArray(new Property[rtn.size()]);
	}


	private static int getFieldType(Property property, int defaultValue) {
		return getFieldType(Caster.toString(property.getMeta().get(FIELDTYPE, null),null),defaultValue);
			
	}
	
	private static int getFieldType(String fieldType, int defaultValue) {
		if(StringUtil.isEmpty(fieldType,true)) return defaultValue;
		fieldType=fieldType.trim().toLowerCase();
		

		if("id".equals(fieldType)) return FIELDTYPE_ID;
		if("column".equals(fieldType)) return FIELDTYPE_COLUMN;
		if("timestamp".equals(fieldType)) return FIELDTYPE_TIMESTAMP;
		if("relation".equals(fieldType)) return FIELDTYPE_RELATION;
		if("version".equals(fieldType)) return FIELDTYPE_VERSION;
		if("collection".equals(fieldType)) return FIELDTYPE_COLLECTION;
		return defaultValue;
	}
	
}
