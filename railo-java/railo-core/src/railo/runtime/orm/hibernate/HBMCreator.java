package railo.runtime.orm.hibernate;


import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import railo.commons.lang.StringUtil;
import railo.runtime.Component;
import railo.runtime.ComponentPro;
import railo.runtime.PageContext;
import railo.runtime.component.Property;
import railo.runtime.db.DatasourceConnection;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.orm.ORMConfiguration;
import railo.runtime.orm.ORMEngine;
import railo.runtime.orm.ORMException;
import railo.runtime.orm.ORMUtil;
import railo.runtime.text.xml.XMLUtil;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.List;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.dt.DateTimeImpl;
import railo.runtime.type.util.ComponentUtil;

public class HBMCreator {
	
	
	public static void createXMLMapping(PageContext pc,DatasourceConnection dc, Component cfc,ORMConfiguration ormConf,Element hibernateMapping,HibernateORMEngine engine) throws PageException {
		String str;
		Boolean b;
		Integer i;
		
		
		// MUST Support for embeded objects 
		
		ComponentPro cfci = ComponentUtil.toComponentPro(cfc);
		Struct meta = cfci.getMetaData(pc);
		Property[] props = cfci.getProperties();
		
		// create class element and attach
		Document doc = XMLUtil.getDocument(hibernateMapping);
		
		StringBuilder comment=new StringBuilder();
		comment.append("\nsource:").append(cfci.getPageSource().getDisplayPath());
		comment.append("\ncompilation-time:").append(new DateTimeImpl(ComponentUtil.getCompileTime(pc,cfci.getPageSource()),false)).append("\n");
		
		hibernateMapping.appendChild(doc.createComment(comment.toString()));
		String extend = cfc.getExtends();
		
		Element clazz;
		boolean isClass=StringUtil.isEmpty(extend);
		boolean doTable=true;
		if(isClass)  {
			clazz = doc.createElement("class");
			hibernateMapping.appendChild(clazz);
		}
		// extended CFC
		else{
			String ext = List.last(extend,'.').trim();
			String discriminatorValue = toString(meta,"discriminatorValue");
			if(!StringUtil.isEmpty(discriminatorValue,true)) {
				doTable=false;
				clazz = doc.createElement("subclass");
				hibernateMapping.appendChild(clazz);
		        //addClassAttributes(classNode);
		        clazz.setAttribute("extends", ext);
		        
		        clazz.setAttribute("discriminator-value", discriminatorValue);
			}
			else {
				clazz = doc.createElement("joined-subclass");
				hibernateMapping.appendChild(clazz);
				clazz.setAttribute("extends",ext);
				Element key = doc.createElement("key");
			    clazz.appendChild(key);
		        key.setAttribute("column", formatColumn(toString(meta,"joincolumn",true)));
			}

		}
		
		//lazy
		//clazz.setAttribute("lazy", "true");
		//createXMLMappingTuplizer(clazz,pc);

		addGeneralClassAttributes(pc,ormConf,engine,cfc,meta,clazz);
		String tableName=getTableName(pc,meta,cfc);
		if(doTable)addGeneralTableAttributes(pc,ormConf,engine,cfc,meta,clazz);
		
        
        
        Struct columnsInfo=null;
        if(ormConf.useDBForMapping()){
        	columnsInfo = engine.getTableInfo(dc,getTableName(pc, meta, cfci),engine);
        }
        
        if(isClass)setCacheStrategy(engine,doc, meta, clazz);
        
		// id
        if(isClass) addId(doc,clazz,pc,meta,props,columnsInfo,tableName,engine);
	      
        // discriminator
        addDiscriminator(doc,clazz,pc,meta);
        
		// version
        if(isClass)addVersion(clazz,pc, props,columnsInfo,tableName,engine);
		
		// property
		addProperty(clazz,pc, props,columnsInfo,tableName,engine);
		
		// relations
		addRelation(clazz,pc, props,columnsInfo,tableName,engine,dc,ormConf);
		
		// collection
		addCollection(clazz,pc, props,columnsInfo,tableName,engine,dc,ormConf);
		
	}
	
	
	



	private static void addId(Document doc, Element clazz, PageContext pc, Struct meta, Property[] props, Struct columnsInfo, String tableName, HibernateORMEngine engine) throws PageException {
		ArrayList<Property> ids=new ArrayList<Property>();
        for(int y=0;y<props.length;y++){
			String fieldType = Caster.toString(props[y].getMeta().get("fieldType",null),null);
			if("id".equalsIgnoreCase(fieldType) || List.listFindNoCaseIgnoreEmpty(fieldType,"id",',')!=-1)
				ids.add(props[y]);
		}
        
        // no id field defined
        if(ids.size()==0) {
        	for(int y=0;y<props.length;y++){
    			String fieldType = Caster.toString(props[y].getMeta().get("fieldType",null),null);
    			//print.o(fieldType+":"+props[y].getName());
    			if(StringUtil.isEmpty(fieldType,true) && props[y].getName().equalsIgnoreCase("id")){
    				ids.add(props[y]);
    				props[y].getMeta().set("fieldType", "id");
    			}
    		}
        } 
        
        Property[] _ids = ids.toArray(new Property[ids.size()]);
        
        if(_ids.length==1) 
        	createXMLMappingId(clazz,pc, _ids[0],columnsInfo,tableName,engine);
        else if(_ids.length>1) 
        	createXMLMappingCompositeId(clazz,pc, _ids,columnsInfo,tableName,engine);
        else 
        	throw new ORMException(engine,"missing id property for entity ["+tableName+"]");
	}






	private static void addVersion(Element clazz, PageContext pc,Property[] props, Struct columnsInfo, String tableName,HibernateORMEngine engine) throws PageException {
    	for(int y=0;y<props.length;y++){
			String fieldType = Caster.toString(props[y].getMeta().get("fieldType",null),null);
			if("version".equalsIgnoreCase(fieldType))
				createXMLMappingVersion(engine,clazz,pc, props[y]);
			else if("timestamp".equalsIgnoreCase(fieldType))
				createXMLMappingTimestamp(engine,clazz,pc, props[y]);
		}
	}



	private static void addCollection(Element clazz, PageContext pc,Property[] props, Struct columnsInfo, String tableName,HibernateORMEngine engine, DatasourceConnection dc,ORMConfiguration ormConf) throws PageException {
    	
    	for(int y=0;y<props.length;y++){
			String fieldType = Caster.toString(props[y].getMeta().get("fieldType","column"),"column");
			if("collection".equalsIgnoreCase(fieldType))
				createXMLMappingCollection(clazz,pc, props[y],ormConf,engine);
		}
		
	}



	private static void addRelation(Element clazz, PageContext pc,Property[] props, Struct columnsInfo, String tableName,HibernateORMEngine engine, DatasourceConnection dc, ORMConfiguration ormConf) throws PageException {
    	for(int y=0;y<props.length;y++){
			String fieldType = Caster.toString(props[y].getMeta().get("fieldType","column"),"column");
			if("one-to-one".equalsIgnoreCase(fieldType))
				createXMLMappingOneToOne(clazz,pc, props[y],engine);
			if("many-to-one".equalsIgnoreCase(fieldType))
				createXMLMappingManyToOne(clazz,pc, props[y],engine);
			if("one-to-many".equalsIgnoreCase(fieldType))
				createXMLMappingOneToMany(engine,dc,ormConf,clazz,pc, props[y],engine);
			if("many-to-many".equalsIgnoreCase(fieldType))
				createXMLMappingManyToMany(clazz,pc, props[y],ormConf,engine);
		}
	}



	private static void addProperty(Element clazz, PageContext pc, Property[] props, Struct columnsInfo, String tableName, HibernateORMEngine engine) throws ORMException {
    	for(int y=0;y<props.length;y++){
			String fieldType = Caster.toString(props[y].getMeta().get("fieldType","column"),"column");
			if("column".equalsIgnoreCase(fieldType))
				createXMLMappingProperty(clazz,pc, props[y],columnsInfo,tableName,engine);
		}
	}



	private static void addDiscriminator(Document doc,Element clazz, PageContext pc,Struct meta) throws ORMException {
		
    	 String str = toString(meta,"discriminatorColumn");
    	 if(!StringUtil.isEmpty(str,true)){
    		 Element disc = doc.createElement("discriminator");
    		 clazz.appendChild(disc);
    		 disc.setAttribute("column",str);
    	 }
    	 

    	
    	
	}



	private static void addGeneralClassAttributes(PageContext pc, ORMConfiguration ormConf, HibernateORMEngine engine, Component cfc, Struct meta, Element clazz) throws PageException {
    	
    	// name
		clazz.setAttribute("node", HibernateCaster.toComponentName(cfc));
		
    	// entity-name
    	String str=toString(meta,"entityname");
		if(StringUtil.isEmpty(str,true)) str=HibernateCaster.getEntityName(pc,cfc);
		clazz.setAttribute("entity-name",str);
		

        // batch-size
        Integer i = toInteger(meta,"batchsize");
        if(i!=null && i.intValue()>0)clazz.setAttribute("batch-size",Caster.toString(i));
		
		// dynamic-insert
        Boolean b = toBoolean(meta,"dynamicinsert");
        if(b!=null && b.booleanValue())clazz.setAttribute("dynamic-insert","true");
        
        // dynamic-update
        b=toBoolean(meta,"dynamicupdate");
        if(b!=null && b.booleanValue())clazz.setAttribute("dynamic-update","true");
        
		// lazy
        b=toBoolean(meta,"lazy");
        if(b!=null)clazz.setAttribute("lazy",Caster.toString(b.booleanValue()));
        
        // select-before-update
        b=toBoolean(meta,"selectbeforeupdate");
        if(b!=null && b.booleanValue())clazz.setAttribute("select-before-update","true");

        // optimistic-lock
        str=toString(meta,"optimisticLock");
        if(!StringUtil.isEmpty(str,true)) {
        	str=str.trim().toLowerCase();
        	if("all".equals(str) || "dirty".equals(str) || "none".equals(str) || "version".equals(str))
        		clazz.setAttribute("optimistic-lock",str);
        	else
        		throw new ORMException(engine,"invalid value ["+str+"] for attribute [optimistic-lock] of tag [component], valid values are [all,dirty,none,version]");
        }
        
        // read-only
        b=toBoolean(meta,"readOnly");
        if(b!=null && b.booleanValue()) clazz.setAttribute("mutable", "false");
        
        // rowid
        str=toString(meta,"rowid");
        if(!StringUtil.isEmpty(str,true)) clazz.setAttribute("rowid",str);
        
        // where
        str=toString(meta,"where");
        if(!StringUtil.isEmpty(str,true)) clazz.setAttribute("where", str);

       
	}
	private static void addGeneralTableAttributes(PageContext pc, ORMConfiguration ormConf, HibernateORMEngine engine, Component cfc, Struct meta, Element clazz) throws PageException {
		 // table
        clazz.setAttribute("table",getTableName(pc,meta,cfc));
        
        // catalog
        String str = toString(meta,"catalog");
        if(str==null)// empty string is allowed as input
        	str=ormConf.getCatalog();
        if(!StringUtil.isEmpty(str,true)) clazz.setAttribute("catalog", str);
        
        // schema
        str=toString(meta,"schema");
        if(str==null)// empty string is allowed as input
        	str=ormConf.getSchema();
        if(!StringUtil.isEmpty(str,true)) clazz.setAttribute( "schema", str);
        
	}
	private static String getTableName(PageContext pc, Struct meta, Component cfc) throws ORMException {
		String tableName=toString(meta,"table");
        if(StringUtil.isEmpty(tableName,true)) tableName=HibernateCaster.getEntityName(pc, cfc);
        //if(tableName==null) print.ds("null:"+cfc.getAbsName());
		return tableName;
	}






	private static void createXMLMappingCompositeId(Element clazz, PageContext pc,Property[] props,Struct columnsInfo,String tableName,HibernateORMEngine engine) throws PageException {
		Struct meta;
		
		Document doc = XMLUtil.getDocument(clazz);
		Element cid = doc.createElement("composite-id");
		clazz.appendChild(cid);
		
		Property prop;
		// ids
		for(int y=0;y<props.length;y++){
			prop=props[y];
			meta = prop.getMeta();
			Element key = doc.createElement("key-property");
			cid.appendChild(key);
			
			// name
			key.setAttribute("name",prop.getName());
			
			// column
			Element column = doc.createElement("column");
			key.appendChild(column);
			
			String str = toString(meta,"column");
	    	if(StringUtil.isEmpty(str,true)) str=prop.getName();
	    	column.setAttribute("name",formatColumn(str));
	    	ColumnInfo info=getColumnInfo(columnsInfo,tableName,str,engine);
	    	if(info!=null){
	    		column.setAttribute("sql-type",info.getTypeName());
	    		column.setAttribute("length",Caster.toString(info.getSize()));
	    	}
			
	    	 // type
			str=getType(info,prop,meta,"long");
			key.setAttribute("type", str);
            
		}
		
		// many-to-one
		String fieldType;
		for(int y=0;y<props.length;y++){
			prop=props[y];
			meta = prop.getMeta();
			fieldType = toString(meta,"fieldType");
			if(List.listFindNoCaseIgnoreEmpty(fieldType,"many-to-one",',')==-1)continue;
			
			Element key = doc.createElement("key-many-to-one");
			cid.appendChild(key);
			
			// name
			key.setAttribute("name",prop.getName());
			
			// entity-name
			setEntityName(pc, engine, meta, key);
			
			// fkcolum
			String str=toString(meta,"fkcolumn");
			setColumn(doc, key, str);
			
			// lazy
			str=toString(meta,"lazy");
			key.setAttribute("lazy",str);
		}
		
		
	}
	
	
	private static void createXMLMappingId(Element clazz, PageContext pc,Property prop,Struct columnsInfo,String tableName,ORMEngine engine) throws PageException {
		Struct meta = prop.getMeta();
		String str;
		Integer i;
		
		Document doc = XMLUtil.getDocument(clazz);
		Element id = doc.createElement("id");
		clazz.appendChild(id);
		
		
				
        // access
    	str=toString(meta,"access");
		if(!StringUtil.isEmpty(str,true))id.setAttribute("access", str);
    	
		// name
		id.setAttribute("name",prop.getName());
		
		// column
		Element column = doc.createElement("column");
		id.appendChild(column);

		str=toString(meta,"column");
    	if(StringUtil.isEmpty(str,true)) str=prop.getName();
    	column.setAttribute("name",formatColumn(str));
    	ColumnInfo info=getColumnInfo(columnsInfo,tableName,str,engine);
    	
		
        // type
		String type = getType(info,prop,meta,"string");
		id.setAttribute("type", type);
		
		
		if(info!=null && !"string".equalsIgnoreCase(type)){
    		//column.setAttribute("sql-type",info.getTypeName());
    		//column.setAttribute("length",Caster.toString(info.getSize()));
    	}
    	
		
		// unsaved-value
		str=toString(meta,"unsavedValue");
		if(!StringUtil.isEmpty(str,true))id.setAttribute("unsaved-value", str);

		createXMLMappingGenerator(engine,id,pc,prop);		
	}
	

	
	
	
	
	
	private static String getType(ColumnInfo info, Property prop,Struct meta,String defaultValue) throws ORMException {
		// ormType
		String type = toString(meta,"ormType"); 
		type=HibernateCaster.toHibernateType(info,type,null);
		
		// dataType
		if(StringUtil.isEmpty(type,true)){
			type=toString(meta,"dataType");
			type=HibernateCaster.toHibernateType(info,type,null);
		}
		
		// type
		if(StringUtil.isEmpty(type,true)){
			type=prop.getType();
			type=HibernateCaster.toHibernateType(info,type,null);
		}
		
		// type from db info
		if(StringUtil.isEmpty(type,true)){
			if(info!=null){
				type=info.getTypeName();
				type=HibernateCaster.toHibernateType(info,type,defaultValue);
			}
			else type=defaultValue;
		}
		return type;
	}









	private static ColumnInfo getColumnInfo(Struct columnsInfo,String tableName,String columnName,ORMEngine engine) throws ORMException {
		if(columnsInfo!=null) {
	    	ColumnInfo info = (ColumnInfo) columnsInfo.get(columnName,null);
			if(info==null) {
				String msg="table ["+tableName+"] has no column with name ["+columnName+"]";
				if(columnsInfo!=null)
					msg+=", column names are ["+List.arrayToList(columnsInfo.keysAsString(), ", ")+"]";
				ORMUtil.printError(msg, engine);
				
				//throw new ORMException(msg);
			}
			return info;
    	}
		return null;
	}







	private static void createXMLMappingGenerator(ORMEngine engine,Element id, PageContext pc,Property prop) throws PageException {
		Struct meta = prop.getMeta();
		
		// generator
		String className=toString(meta,"generator");
		if(StringUtil.isEmpty(className,true)) return;
		

		Document doc = XMLUtil.getDocument(id);
		Element generator = doc.createElement("generator");
		id.appendChild(generator);
		
		generator.setAttribute("class", className);
		
		// params
		Object obj=meta.get("params",null);
		if(obj!=null){
			Struct sct=null;
			if(obj instanceof String) obj=convertToSimpleMap((String)obj);
			if(Decision.isStruct(obj)) sct=Caster.toStruct(obj);
			else throw new ORMException(engine,"invalid value for attribute [params] of tag [property]");
			className=className.trim().toLowerCase();
			
			// special classes
			if("foreign".equals(className) && !sct.containsKey("property")){
				sct.setEL("property", toString(meta, "property",true));
			}
			else if("select".equals(className) && !sct.containsKey("key")){
				sct.setEL("key", toString(meta, "selectKey",true));
			}
			else if("sequence".equals(className) && !sct.containsKey("sequence")){
				sct.setEL("sequence", toString(meta, "sequence",true));
			}
			
			Key[] keys = sct.keys();
			Element param;
			for(int y=0;y<keys.length;y++){
				
				param = doc.createElement("param");
				generator.appendChild(param);
				
				param.setAttribute( "name", keys[y].getLowerString());
				param.appendChild(doc.createTextNode(Caster.toString(sct.get(keys[y]))));
				
			}
		}
	}
	
	
	
	

	

	private static void createXMLMappingProperty(Element clazz, PageContext pc,Property prop,Struct columnsInfo,String tableName,ORMEngine engine) throws ORMException {
		Struct meta = prop.getMeta();
		
        
		
		// get table name
		String columnName=toString(meta,"column");
    	if(StringUtil.isEmpty(columnName,true)) columnName=prop.getName();
    	
    	ColumnInfo info=getColumnInfo(columnsInfo,tableName,columnName,engine);
		
		Document doc = XMLUtil.getDocument(clazz);
		Element property = doc.createElement("property");
		clazz.appendChild(property);
		
		//name
		property.setAttribute("name",prop.getName());
		
		// type
		String str = getType(info, prop, meta, "string");
		property.setAttribute("type",str);
		
		
		
		// formula or column
		str=toString(meta,"formula");
        Boolean b;
		if(!StringUtil.isEmpty(str,true))	{
        	property.setAttribute("formula","("+str+")");
        }
        else {
        	//property.setAttribute("column",columnName);
        	
        	Element column = doc.createElement("column");
        	property.appendChild(column);
        	column.setAttribute("name", columnName);

            // check
            str=toString(meta,"check");
            if(!StringUtil.isEmpty(str,true)) column.setAttribute("check",str);
            
	        // default
	        str=toString(meta,"dbDefault");
	        if(!StringUtil.isEmpty(str,true)) column.setAttribute("default",str);
            
            // index
            str=toString(meta,"index");
            if(!StringUtil.isEmpty(str,true)) column.setAttribute("index",str);
        	
        	// length
            Integer i = toInteger(meta,"length");
            if(i!=null && i>0) column.setAttribute("length",Caster.toString(i.intValue()));
            
            // not-null
            b=toBoolean(meta,"notnull");
            if(b!=null && b.booleanValue())column.setAttribute("not-null","true");
            
            // precision
            i=toInteger(meta,"precision");
            if(i!=null && i>-1) column.setAttribute("precision",Caster.toString(i.intValue()));
            
            // scale
            i=toInteger(meta,"scale");
            if(i!=null && i>-1) column.setAttribute("scale",Caster.toString(i.intValue()));
            
            // sql-type
            str=toString(meta,"sqltype");
            if(!StringUtil.isEmpty(str,true)) column.setAttribute("sql-type",str);
            
        // unique
            b=toBoolean(meta,"unique");
	        if(b!=null && b.booleanValue())column.setAttribute("unique","true");
	        
	        // unique-key
	        str=toString(meta,"uniqueKey");
	        if(StringUtil.isEmpty(str))str=Caster.toString(meta.get("uniqueKeyName",null),null);
	        if(!StringUtil.isEmpty(str,true)) column.setAttribute("unique-key",str);
	        
	        
        }
        
        // generated
        str=toString(meta,"generated");
        if(!StringUtil.isEmpty(str,true)){
        	str=str.trim().toLowerCase();
        	
        	if("always".equals(str) || "insert".equals(str) || "never".equals(str))
        		property.setAttribute("generated",str);
        	else
        		throw new ORMException(engine,"invalid value ["+str+"] for attribute [generated] of column ["+columnName+"], valid values are [always,insert,never]");
        }
        
        
        // update
        b=toBoolean(meta,"update");
        if(b!=null && !b.booleanValue())property.setAttribute("update","false");
        
        // insert
        b=toBoolean(meta,"insert");
        if(b!=null && !b.booleanValue())property.setAttribute("insert","false");
        
        // lazy
        b=toBoolean(meta,"lazy");
        if(b!=null && b.booleanValue())property.setAttribute("lazy","true");
        
        // optimistic-lock
        b=toBoolean(meta,"optimisticlock");
        if(b!=null && !b.booleanValue())property.setAttribute("optimistic-lock","false");
        
	}
	
	
	/*
	MUST dies kommt aber nicht hier sondern in verarbeitung in component
	<cfproperty 
    persistent="true|false" 
   >
	 * */
	private static void createXMLMappingOneToOne(Element clazz, PageContext pc,Property prop,HibernateORMEngine engine) throws PageException {
		Struct meta = prop.getMeta();
		
		Boolean b;
		
		Document doc = XMLUtil.getDocument(clazz);
		Element x2o;
		
		// column
		String fkcolumn=toString(meta,"fkcolumn");
		String linkTable=toString(meta,"linkTable");
		
		
		if(!StringUtil.isEmpty(linkTable,true) || !StringUtil.isEmpty(fkcolumn,true)) {
			clazz=getJoin(clazz);
			
			x2o= doc.createElement("many-to-one");
			//x2o.setAttribute("column", fkcolumn);
			x2o.setAttribute("unique", "true");
			
			if(!StringUtil.isEmpty(linkTable,true)){
				setColumn(doc, x2o, linkTable);
			}
			else {
				setColumn(doc, x2o, fkcolumn);
			}

			
			
			
			// update
	        b=toBoolean(meta,"update");
	        if(b!=null)x2o.setAttribute("update",Caster.toString(b.booleanValue()));
	        
	        // insert
	        b=toBoolean(meta,"insert");
	        if(b!=null)x2o.setAttribute("insert",Caster.toString(b.booleanValue()));
	        
	        // not-null
	        b=toBoolean(meta,"notNull");
	        if(b!=null)x2o.setAttribute("not-null",Caster.toString(b.booleanValue()));
	        
	        
	        // optimistic-lock
	        b=toBoolean(meta,"optimisticLock");
	        if(b!=null)x2o.setAttribute("optimistic-lock",Caster.toString(b.booleanValue()));
	        
	        // not-found
			b=toBoolean(meta, "missingRowIgnored");
	        if(b!=null && b.booleanValue()) x2o.setAttribute("not-found", "ignore");

			/* / index
			str=toString(meta,"index");
			if(!StringUtil.isEmpty(str,true)) x2o.setAttribute("index", str); 
			*/
			
			
		}
		else {
			x2o= doc.createElement("one-to-one");
		}
		clazz.appendChild(x2o);
		
		// access
		String str=toString(meta,"access");
		if(!StringUtil.isEmpty(str,true)) x2o.setAttribute("access", str);
			
		// constrained
		b=toBoolean(meta, "constrained");
        if(b!=null && b.booleanValue()) x2o.setAttribute("constrained", "true");
		
		// formula
		str=toString(meta,"formula");
		if(!StringUtil.isEmpty(str,true)) x2o.setAttribute("formula", str);
		
		// embed-xml
		str=toString(meta,"embedXml");
		if(!StringUtil.isEmpty(str,true)) x2o.setAttribute("embed-xml", str);
		
		// property-ref
		str=toString(meta,"mappedBy");
		if(!StringUtil.isEmpty(str,true)) x2o.setAttribute("property-ref", str);
		
		// foreign-key
		str=toString(meta,"foreignKeyName");
		if(StringUtil.isEmpty(str,true)) str=toString(meta,"foreignKey");
		if(!StringUtil.isEmpty(str,true)) x2o.setAttribute("foreign-key", str);
        
		setEntityName(pc,engine,meta,x2o);
		
		createXMLMappingXToX(engine,x2o, pc,prop,meta);
	}
	
	private static Element getJoin(Element clazz) {
		if(clazz.getNodeName().equals("subclass")){
			NodeList joins = clazz.getElementsByTagName("join");
			if(joins!=null && joins.getLength()>0)
				return (Element)joins.item(0);
			
		}
		return clazz;
	}
	
	private static void setEntityName(PageContext pc,HibernateORMEngine engine, Struct meta, Element el) throws PageException {
		// entity
		String str=toString(meta,"entityName");
		if(!StringUtil.isEmpty(str,true)) {
			el.setAttribute("entity-name", str);
		}
		else {
			// cfc
			str = toString(meta,"cfc");
			if(!StringUtil.isEmpty(str,true)){
				Component cfc=engine.getEntityByCFCName(str, false);
				str=HibernateCaster.getEntityName(pc,cfc);
				el.setAttribute("entity-name", str);
			}
			
		}
	}









	private static void createXMLMappingCollection(Element clazz, PageContext pc,Property prop,ORMConfiguration ormConf, HibernateORMEngine engine) throws PageException {
		Struct meta = prop.getMeta();
		Document doc = XMLUtil.getDocument(clazz);
		Element el=null;
		
		
		 
	        
	        

	      
		
		Element join = getJoin(clazz);
        
		// collection type
		String str=toString(meta, "collectiontype");
		if(StringUtil.isEmpty(str,true))str="array";
		else str=str.trim().toLowerCase();
		
		// bag
		if("array".equals(str) || "bag".equals(str)){
			el = doc.createElement("bag");
		}
		// map
		else if("struct".equals(str) || "map".equals(str)){
			el = doc.createElement("map");
			
			
			// map-key
			str=toString(meta,"structKeyColumn",true);
			if(!StringUtil.isEmpty(str,true)) {
				Element mapKey=doc.createElement("map-key");
				el.appendChild(mapKey);
				mapKey.setAttribute("column", str);
				
				// type
				str=toString(meta,"structKeyType");
				if(!StringUtil.isEmpty(str,true))mapKey.setAttribute("type", str);
			}
		}
		else throw new ORMException(engine,"invalid value ["+str+"] for attribute [collectiontype], valid values are [array,struct]");
		
		if(join==clazz) clazz.appendChild(el);
		else clazz.insertBefore(el, join);
		
		
		// name 
		el.setAttribute("name", prop.getName());
		
		// table
		str=toString(meta, "table",true);
		el.setAttribute("table",str);
		
		// catalog
		str=toString(meta, "catalog");
		if(!StringUtil.isEmpty(str,true))el.setAttribute("catalog",str);
		
		// schema
		str=toString(meta, "schema");
		if(!StringUtil.isEmpty(str,true))el.setAttribute("schema",str);
		
		// mutable
		Boolean b=toBoolean(meta, "readonly");
        if(b!=null && b.booleanValue()) el.setAttribute("mutable", "false");
		
		// order-by
		str=toString(meta, "orderby");
		if(!StringUtil.isEmpty(str,true))el.setAttribute("order-by",str);
        
		// element-column
		str=toString(meta,"elementcolumn");
		if(!StringUtil.isEmpty(str,true)){
			Element element = doc.createElement("element");
			el.appendChild(element);
			
			// column
			element.setAttribute("column", str);
			
			// type
			str=toString(meta,"elementtype");
			if(!StringUtil.isEmpty(str,true)) element.setAttribute("type", str);
		}
		
        // batch-size
        Integer i=toInteger(meta, "batchsize");
        if(i!=0){
        	if(i.intValue()>1) el.setAttribute("batch-size", Caster.toString(i.intValue()));
		
		// column
		str=toString(meta,"fkcolumn");
		if(StringUtil.isEmpty(str,true)) str=toString(meta,"column");
		if(!StringUtil.isEmpty(str,true)){
			Element key = doc.createElement("key");
			el.appendChild(key);
			
			// column
			key.setAttribute("column", formatColumn(str));
			
			// property-ref
			str=toString(meta,"mappedBy");
			if(!StringUtil.isEmpty(str,true)) key.setAttribute("property-ref", str);
		}
		
		// cache
		setCacheStrategy(engine,doc, meta, el);
		
		// optimistic-lock
		b=toBoolean(meta, "optimisticlock");
        if(b!=null && !b.booleanValue()) el.setAttribute("optimistic-lock", "false");
        }
       
	}
	
	
	private static void createXMLMappingManyToMany(Element clazz, PageContext pc,Property prop,ORMConfiguration ormConf, HibernateORMEngine engine) throws PageException {
		Element el = createXMLMappingXToMany(engine,clazz, pc, prop);
		Struct meta = prop.getMeta();
		Document doc = XMLUtil.getDocument(clazz);
		Element m2m = doc.createElement("many-to-many");
		el.appendChild(m2m);
		
		// link
		setLink(el,meta,ormConf);
		
		setEntityName(pc, engine, meta, m2m);

        // order-by
		String str = toString(meta,"orderby");
		if(!StringUtil.isEmpty(str,true))m2m.setAttribute("order-by", str);
		
		// column
		str=toString(meta,"inversejoincolumn");
		setColumn(doc, m2m, str);
		
		// not-found
		Boolean b=toBoolean(meta, "missingrowignored");
        if(b!=null && b.booleanValue()) m2m.setAttribute("not-found", "ignore");
        
        // property-ref
		str=toString(meta,"mappedby");
		if(!StringUtil.isEmpty(str,true)) m2m.setAttribute("property-ref", str);
		
		// foreign-key
		str=toString(meta,"foreignKeyName");
		if(StringUtil.isEmpty(str,true)) str=toString(meta,"foreignKey");
		if(!StringUtil.isEmpty(str,true)) m2m.setAttribute("foreign-key", str);
	}
	
	private static boolean setLink(Element el, Struct meta, ORMConfiguration ormConf) throws ORMException {
		String str=toString(meta, "linktable");
		if(!StringUtil.isEmpty(str,true)){
			el.setAttribute("table", str);
		
			// schema
			str=toString(meta, "linkschema");
			if(StringUtil.isEmpty(str,true)) str=ormConf.getSchema();
			if(!StringUtil.isEmpty(str,true)) el.setAttribute("schema", str);
			
			// catalog
			str=toString(meta, "linkcatalog");
			if(StringUtil.isEmpty(str,true)) str=ormConf.getCatalog();
			if(!StringUtil.isEmpty(str,true)) el.setAttribute("catalog", str);
			return true;
		}
		return false;
	}









	private static void createXMLMappingOneToMany(HibernateORMEngine engine,DatasourceConnection dc,ORMConfiguration ormConf,Element clazz, PageContext pc,Property prop, HibernateORMEngine engine2) throws PageException {
		Element el = createXMLMappingXToMany(engine,clazz, pc, prop);
		Struct meta = prop.getMeta();
		Document doc = XMLUtil.getDocument(clazz);
		Element x2m;
		
		
		
        // order-by
		String str = toString(meta,"orderby");
		if(!StringUtil.isEmpty(str,true))el.setAttribute("order-by", str);
		
		// link
		if(setLink(el,meta,ormConf)){
			x2m = doc.createElement("many-to-many");
			x2m.setAttribute("unique","true");
			
			str=toString(meta,"inversejoincolumn");
			setColumn(doc, x2m, str);
		}
		else {
			x2m = doc.createElement("one-to-many");
		}
		el.appendChild(x2m);
		

		// entity-name
		setEntityName(pc,engine,meta,x2m);
		
	}

	

	
	
	
	
	
	private static Element createXMLMappingXToMany(ORMEngine engine,Element clazz, PageContext pc,Property prop) throws PageException {
		Struct meta = prop.getMeta();
		Document doc = XMLUtil.getDocument(clazz);
		Element el=null;
		
		Element join = getJoin(clazz);
		
		
	// collection type
		String str=toString(meta, "collectiontype");
		if(StringUtil.isEmpty(str,true))str="array";
		else str=str.trim().toLowerCase();
		
		// bag
		if("array".equals(str) || "bag".equals(str)){
			el = doc.createElement("bag");
			
		}
		// map
		else if("struct".equals(str) || "map".equals(str)){
			el = doc.createElement("map");
			
			// map-key
			Element mapKey = doc.createElement("map-key");
			el.appendChild(mapKey);
			
			// column
			str=toString(meta,"structKeyColumn",true);
			mapKey.setAttribute("column", str);
			
			// type
			str=toString(meta,"structKeyType");
			if(!StringUtil.isEmpty(str,true))mapKey.setAttribute("type", str);
		}
		else throw new ORMException(engine,"invalid value ["+str+"] for attribute [collectiontype], valid values are [array,struct]");
		
		if(join==clazz) clazz.appendChild(el);
		else clazz.insertBefore(el, join);
        

		
		// batch-size
        Integer i=toInteger(meta, "batchsize");
        if(i!=null){
        	if(i.intValue()>1) el.setAttribute("batch-size", Caster.toString(i.intValue()));
        }
 
		// cacheUse
        setCacheStrategy(engine,doc, meta, el);
        
        // column
		str=toString(meta,"fkcolumn");
		if(!StringUtil.isEmpty(str,true)){
			Element key = doc.createElement("key");
			el.appendChild(key);
			
			// column
			setColumn(doc,key,str);
			
			// property-ref
			str=toString(meta,"mappedBy");
			if(!StringUtil.isEmpty(str,true)) key.setAttribute("property-ref", str);
		}
		
        // inverse
		Boolean b = toBoolean(meta, "inverse");
        if(b!=null && b.booleanValue()) el.setAttribute("inverse", "true");
        
		
		
		// mutable 
		b = toBoolean(meta, "readonly");
        if(b!=null && b.booleanValue()) el.setAttribute("mutable", "false");
		
		// optimistic-lock
		b=toBoolean(meta, "optimisticlock");
        if(b!=null && !b.booleanValue()) el.setAttribute("optimistic-lock", "false");
        
		// where
		str=toString(meta,"where");
		if(!StringUtil.isEmpty(str,true)) el.setAttribute("where", str);
        
        
        
        
		createXMLMappingXToX(engine,el, pc,prop,meta);
		
		return el;
	}
	
	private static void setCacheStrategy(ORMEngine engine,Document doc,Struct meta, Element el) throws ORMException {
		String strategy = toString(meta,"cacheuse");
		
		if(!StringUtil.isEmpty(strategy,true)){
			strategy=strategy.trim().toLowerCase();
			if("read-only".equals(strategy) || "nonstrict-read-write".equals(strategy) || "read-write".equals(strategy) || "transactional".equals(strategy)){
				Element cache = doc.createElement("cache");
				el.appendChild(cache);
				cache.setAttribute("usage", strategy);
				String name = toString(meta,"cacheName");
				if(!StringUtil.isEmpty(name,true)){
					cache.setAttribute("region", name);
				}
			}	
			else
				throw new ORMException(engine,"invalid value ["+strategy+"] for attribute [cacheuse], valid values are [read-only,nonstrict-read-write,read-write,transactional]");
		}
		
	}

	





	private static void setColumn(Document doc, Element el, String columnValue) throws PageException {
		if(StringUtil.isEmpty(columnValue,true)) return;
		
		String[] arr = List.toStringArray(List.listToArray(columnValue, ','));
		if(arr.length==1){
			el.setAttribute("column", formatColumn(arr[0]));
		}
		else {
			Element column;
			for(int i=0;i<arr.length;i++){
				column=doc.createElement("column");
				el.appendChild(column);
				column.setAttribute("name", formatColumn(arr[0]));
			}
		}
	}









	private static void createXMLMappingManyToOne(Element clazz, PageContext pc,Property prop, HibernateORMEngine engine) throws PageException {
		Struct meta = prop.getMeta();
		Boolean b;
		
		Document doc = XMLUtil.getDocument(clazz);
		clazz=getJoin(clazz);
		
		Element m2o = doc.createElement("many-to-one");
		clazz.appendChild(m2o);
		
		// columns
		String linktable = toString(meta,"linktable");
		String _columns;
		if(!StringUtil.isEmpty(linktable,true)) _columns=toString(meta,"inversejoincolumn");
		else _columns=toString(meta,"fkcolumn");
		setColumn(doc, m2o, _columns);
		
		// cfc
		setEntityName(pc,engine,meta,m2o);
		
		// column
		String str=toString(meta,"column",true);
		m2o.setAttribute("column", str);
		
		// insert
		b=toBoolean(meta, "insert");
        if(b!=null && !b.booleanValue()) m2o.setAttribute("insert", "false");
		
        // update
		b=toBoolean(meta, "update");
        if(b!=null && !b.booleanValue()) m2o.setAttribute("update", "false");
		
        // property-ref
		str=toString(meta,"mappedBy");
		if(!StringUtil.isEmpty(str,true)) m2o.setAttribute("property-ref", str);

        // update
		b=toBoolean(meta, "unique");
        if(b!=null && b.booleanValue()) m2o.setAttribute("unique", "true");

        // not-null
		b=toBoolean(meta, "notnull");
        if(b!=null && b.booleanValue()) m2o.setAttribute("not-null", "true");
		
        // optimistic-lock
		b=toBoolean(meta, "optimisticLock");
        if(b!=null && !b.booleanValue()) m2o.setAttribute("optimistic-lock", "false");
        
        // not-found
		b=toBoolean(meta, "missingRowIgnored");
        if(b!=null && b.booleanValue()) m2o.setAttribute("not-found", "ignore");
        
        // index
		str=toString(meta,"index");
		if(!StringUtil.isEmpty(str,true)) m2o.setAttribute("index", str);
        
        // unique-key
		str=toString(meta,"uniqueKeyName");
		if(StringUtil.isEmpty(str,true))str=toString(meta,"uniqueKey");
		if(!StringUtil.isEmpty(str,true)) m2o.setAttribute("unique-key", str);
		
		// foreign-key
		str=toString(meta,"foreignKeyName");
		if(StringUtil.isEmpty(str,true)) str=toString(meta,"foreignKey");
		if(!StringUtil.isEmpty(str,true)) m2o.setAttribute("foreign-key", str);

		// access
		str=toString(meta,"access");
		if(!StringUtil.isEmpty(str,true)) m2o.setAttribute("access", str);
		
        createXMLMappingXToX(engine,m2o, pc,prop,meta);
        
	}
	
	
	
	
	
	
	
	
	private static String formatColumn(String name) {
        name=name.trim();
		return name;
    }
	
	/*

 
	<cfproperty 
cfc="Referenced_CFC_Name" 
linktable="Link table name" 
linkcatalog="Catalog for the link table" 
linkschema="Schema for the link table" 
fkcolumn="Foreign Key column name" 
inversejoincolumn="Column name or comma-separated list of primary key columns" 


>

	*/
	private static void createXMLMappingXToX(ORMEngine engine,Element x2x, PageContext pc, Property prop, Struct meta) throws ORMException {
		x2x.setAttribute("name",prop.getName());
		
		// cascade
		String str=toString(meta,"cascade");
		if(!StringUtil.isEmpty(str,true)) x2x.setAttribute("cascade", str);
		
		// fetch
		str=toString(meta,"fetch");
		if(!StringUtil.isEmpty(str,true)) {
			str=str.trim().toLowerCase();
			if("join".equals(str) || "select".equals(str))
				x2x.setAttribute("fetch", str);
			else
				throw new ORMException(engine,"invalid value ["+str+"] for attribute [fetch], valid values are [join,select]");
		}
		
		// lazy
		Boolean b=toBoolean(meta, "lazy");
        if(b!=null) x2x.setAttribute("lazy", b.booleanValue()?"proxy":"false");
	}



	

	private static void createXMLMappingTimestamp(ORMEngine engine,Element clazz, PageContext pc,Property prop) throws PageException {
		Struct meta = prop.getMeta();
		String str;
		Integer i;
		Boolean b;
		

		Document doc = XMLUtil.getDocument(clazz);
		Element timestamp = doc.createElement("timestamp");
		clazz.appendChild(timestamp);
		
		timestamp.setAttribute("name",prop.getName());
		
		 // access
		str=toString(meta,"access");
		if(!StringUtil.isEmpty(str,true))timestamp.setAttribute("access", str);
		
		// column
		str=toString(meta,"column");
		if(StringUtil.isEmpty(str,true)) str=prop.getName();
		timestamp.setAttribute("column",formatColumn(str));

		// generated
		b=toBoolean(meta, "generated");
        if(b!=null) timestamp.setAttribute("generated", b.booleanValue()?"always":"never");
        
        // source
        str=toString(meta,"source");
		if(!StringUtil.isEmpty(str,true)) {
			str=str.trim().toLowerCase();
			if("db".equals(str) || "vm".equals(str))
				timestamp.setAttribute("source", str);
			else 
				throw new ORMException(engine,"invalid value ["+str+"] for attribute [source], valid values are [db,vm]");
		}
		
		// unsavedValue
        str=toString(meta,"unsavedValue");
		if(!StringUtil.isEmpty(str,true)) {
			str=str.trim().toLowerCase();
			if("null".equals(str) || "undefined".equals(str))
				timestamp.setAttribute("unsaved-value", str);
			else 
				throw new ORMException(engine,"invalid value ["+str+"] for attribute [unsavedValue], valid values are [null, undefined]");
		}
	}

	
	private static void createXMLMappingVersion(ORMEngine engine,Element clazz, PageContext pc,Property prop) throws PageException {
		Struct meta = prop.getMeta();
		
		Document doc = XMLUtil.getDocument(clazz);
		Element version = doc.createElement("version");
		clazz.appendChild(version);
		
		
		version.setAttribute("name",prop.getName());
		
		// column
		String str = toString(meta,"column");
		if(StringUtil.isEmpty(str,true)) str=prop.getName();
		version.setAttribute("column",str);
		
		 // access
    	str=toString(meta,"access");
		if(!StringUtil.isEmpty(str,true))version.setAttribute("access", str);
		
		// generated
		Boolean b = toBoolean(meta, "generated");
        if(b!=null) version.setAttribute( "generated", b.booleanValue()?"always":"never");
        
        // insert
        b=toBoolean(meta, "insert");
        if(b!=null && !b.booleanValue()) version.setAttribute("insert", "false");
        
        // type
		str=toString(meta,"dataType");
		if(StringUtil.isEmpty(str,true))str=toString(meta,"ormType");
		if(!StringUtil.isEmpty(str,true)) {
			str=str.trim().toLowerCase();
			if("int".equals(str) || "integer".equals(str))
				version.setAttribute("type", "integer");
			else 
				throw new ORMException(engine,"invalid value ["+str+"] for attribute [dataType], valid values are [int,integer]");
		}
		
		// unsavedValue
        str=toString(meta,"unsavedValue");
		if(!StringUtil.isEmpty(str,true)) {
			str=str.trim().toLowerCase();
			if("null".equals(str) || "negative".equals(str) || "undefined".equals(str))
				version.setAttribute("unsaved-value", str);
			else 
				throw new ORMException(engine,"invalid value ["+str+"] for attribute [unsavedValue], valid values are [null, negative, undefined]");
		}
	}   
	
	
	private static Struct convertToSimpleMap(String paramsStr) {
		paramsStr=paramsStr.trim();
        if(!StringUtil.startsWith(paramsStr, '{') || !StringUtil.endsWith(paramsStr, '}'))
        	return null;
        	
		paramsStr = paramsStr.substring(1, paramsStr.length() - 1);
		String items[] = List.listToStringArray(paramsStr, ','); 
		
		Struct params=new StructImpl();
		String arr$[] = items;
		int index;
        for(int i = 0; i < arr$.length; i++)	{
            String pair = arr$[i];
            index = pair.indexOf('=');
            if(index == -1) return null;
            
            params.setEL(
            		deleteQuotes(pair.substring(0, index).trim()).trim(), 
            		deleteQuotes(pair.substring(index + 1).trim()));
        }

        return params;
    }
	
	private static String deleteQuotes(String str)	{
        if(StringUtil.isEmpty(str,true))return "";
        char first=str.charAt(0);
        if((first=='\'' || first=='"') && StringUtil.endsWith(str, first))
        	return str.substring(1, str.length() - 1);
        return str;
    }
	
	
	private static String toString(Struct sct, String key) throws ORMException {
		return toString(sct, key, false);
	}

	private static String toString(Struct sct, String key, boolean throwErrorWhenNotExist) throws ORMException {
		Object value = sct.get(key,null);
		if(value==null) {
			if(throwErrorWhenNotExist)
				throw new ORMException("attribute ["+key+"] is required");
			return null;
		}
		
		String str=Caster.toString(value,null);
		if(str==null) throw new ORMException("invalid type ["+Caster.toTypeName(value)+"] for attribute ["+key+"], value must be a string");
		return str;
	}
	
	private static Boolean toBoolean(Struct sct, String key) throws ORMException {
		Object value = sct.get(key,null);
		if(value==null) return null;
		
		Boolean b=Caster.toBoolean(value,null);
		if(b==null) throw new ORMException("invalid type ["+Caster.toTypeName(value)+"] for attribute ["+key+"], value must be a boolean");
		return b;
	}

	private static Integer toInteger(Struct sct, String key) throws ORMException {
		Object value = sct.get(key,null);
		if(value==null) return null;
		
		Integer i=Caster.toInteger(value,null);
		if(i==null) throw new ORMException("invalid type ["+Caster.toTypeName(value)+"] for attribute ["+key+"], value must be a numeric value");
		return i;
	}
}
