package railo.runtime.orm.hibernate;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.DOMException;
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
import railo.runtime.orm.ORMException;
import railo.runtime.orm.ORMUtil;
import railo.runtime.text.xml.XMLUtil;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.dt.DateTimeImpl;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.util.ComponentUtil;
import railo.runtime.type.util.KeyConstants;
import railo.runtime.type.util.ListUtil;

public class HBMCreator {
	
	
	private static final Collection.Key PROPERTY = KeyConstants._property;
	private static final Collection.Key LINK_TABLE = KeyImpl.intern("linktable");
	private static final Collection.Key CFC = KeyConstants._cfc;
	private static final Collection.Key GENERATOR = KeyImpl.intern("generator");
	private static final Collection.Key PARAMS = KeyImpl.intern("params");
	private static final Collection.Key SEQUENCE = KeyImpl.intern("sequence");
	private static final Collection.Key UNIQUE_KEY_NAME = KeyImpl.intern("uniqueKeyName");
	private static final Collection.Key GENERATED = KeyImpl.intern("generated");
   
	public static void createXMLMapping(PageContext pc,DatasourceConnection dc, Component cfc,Element hibernateMapping,SessionFactoryData data) throws PageException {
		
		// MUST Support for embeded objects 
		Struct meta = cfc.getMetaData(pc);
		
		String extend = cfc.getExtends();
		boolean isClass=StringUtil.isEmpty(extend);

		// MZ: Fetches all inherited persistent properties
		//Property[] _props=getAllPersistentProperties(pc,cfc,dc,meta,isClass);

		Property[] _props=getProperties(pc,cfc,dc,meta,isClass, true,data);

		

		Map<String, PropertyCollection> joins=new HashMap<String, PropertyCollection>();
		PropertyCollection propColl = splitJoins(cfc,joins, _props,data);
		
		
		
		// create class element and attach
		Document doc = XMLUtil.getDocument(hibernateMapping);
		
		StringBuilder comment=new StringBuilder();
		comment.append("\nsource:").append(cfc.getPageSource().getDisplayPath());
		comment.append("\ncompilation-time:").append(new DateTimeImpl(ComponentUtil.getCompileTime(pc,cfc.getPageSource()),false)).append("\n");
		
		hibernateMapping.appendChild(doc.createComment(comment.toString()));
		
		//print.e(cfc.getAbsName()+";"+isClass+" -> "+cfci.getBaseAbsName()+":"+cfci.isBasePeristent());
		if(!isClass && !cfc.isBasePeristent()) {
			isClass=true;
		} 
		
		
		Element join=null;
		boolean doTable=true;
		
		Element clazz;
		if(isClass)  {
			clazz = doc.createElement("class");
			hibernateMapping.appendChild(clazz);
		}
		// extended CFC
		else{
			// MZ: Fetches one level deep
			_props=getProperties(pc,cfc,dc,meta,isClass, false,data);
			// MZ: Reinitiate the property collection
			propColl = splitJoins(cfc,joins, _props,data);

			String ext = ListUtil.last(extend,'.').trim();
			try {
				Component base = data.getEntityByCFCName(ext, false);
				ext = HibernateCaster.getEntityName(base);
			}
			catch(Throwable t){}
			
			
			String discriminatorValue = toString(cfc,null,meta,"discriminatorValue",data);
			if(!StringUtil.isEmpty(discriminatorValue,true)) {
				doTable=false;
				clazz = doc.createElement("subclass");
				hibernateMapping.appendChild(clazz);
		        //addClassAttributes(classNode);
		        clazz.setAttribute("extends", ext);
		        clazz.setAttribute("discriminator-value", discriminatorValue);
		        
		        String joincolumn = toString(cfc,null,meta,"joincolumn",false,data);
		        if(!StringUtil.isEmpty(joincolumn)){
		        	join = doc.createElement("join");
			        clazz.appendChild(join);
			        doTable=true;
			        Element key = doc.createElement("key");
			        join.appendChild(key);
			        key.setAttribute("column", formatColumn(joincolumn,data));
		        }
		        
			}
			else {
				// MZ: Match on joinColumn for a joined subclass, otherwise use a union subclass
				String joinColumn = toString(cfc,null,meta,"joincolumn",false,data);
				if (!StringUtil.isEmpty(joinColumn,true)) {
					clazz = doc.createElement("joined-subclass");
					hibernateMapping.appendChild(	clazz);
					clazz.setAttribute("extends",ext);
					Element key = doc.createElement("key");
					clazz.appendChild(key);
					key.setAttribute("column", formatColumn(joinColumn,data));
				}
				else {
					// MZ: When no joinColumn exists, default to an explicit table per class
					clazz = doc.createElement("union-subclass");
					clazz.setAttribute("extends",ext);
					doTable = true;
					hibernateMapping.appendChild(	clazz);
				}

			}
		}

		//createXMLMappingTuplizer(clazz,pc);

		addGeneralClassAttributes(pc,cfc,meta,clazz,data);
		String tableName=getTableName(pc,meta,cfc,data);
		
		if(join!=null) clazz=join;
		if(doTable)addGeneralTableAttributes(pc,cfc,meta,clazz,data);
		
		
        
        
        Struct columnsInfo=null;
        if(data.getORMConfiguration().useDBForMapping()){
        	columnsInfo = data.getTableInfo(dc,getTableName(pc, meta, cfc,data));
        }

        if(isClass)setCacheStrategy(cfc,null,doc, meta, clazz,data);
        
		// id
        if(isClass) addId(cfc,doc,clazz,meta,propColl,columnsInfo,tableName,data);
	      
        // discriminator
        if(isClass) addDiscriminator(cfc,doc,clazz,pc,meta,data);
        
		// version
        if(isClass)addVersion(cfc,clazz,pc, propColl,columnsInfo,tableName,data);
		
		// property
		addProperty(cfc,clazz,pc, propColl,columnsInfo,tableName,data);
		
		// relations
		addRelation(cfc,clazz,pc, propColl,columnsInfo,tableName,dc,data);

		// collection
		addCollection(cfc,clazz,pc, propColl,columnsInfo,tableName,data);
		
		// join
		addJoin(cfc,clazz,pc, joins,columnsInfo,tableName,dc,data);

		
	}
	
	private static Property[] getProperties(PageContext pc, Component cfci, DatasourceConnection dc, Struct meta, boolean isClass, boolean recursivePersistentMappedSuperclass,SessionFactoryData data) throws ORMException, PageException {
		Property[] _props;
		if (recursivePersistentMappedSuperclass && (cfci instanceof ComponentPro)) {
			_props = ((ComponentPro)cfci).getProperties(true, true, true, true);
		}
		else {
			_props = cfci.getProperties(true);
		}

		if(isClass && _props.length==0 && data.getORMConfiguration().useDBForMapping()){
			if(meta==null)meta = cfci.getMetaData(pc);
        	_props=HibernateUtil.createPropertiesFromTable(dc,getTableName(pc, meta, cfci,data));
        }
		return _props;
	}

	private static void addId(Component cfc,Document doc, Element clazz, Struct meta, PropertyCollection propColl, Struct columnsInfo, String tableName, SessionFactoryData data) throws PageException {
		Property[] _ids = getIds(cfc,propColl,data);
		
        //Property[] _ids = ids.toArray(new Property[ids.size()]);
        
        if(_ids.length==1) 
        	createXMLMappingId(cfc,clazz, _ids[0],columnsInfo,tableName,data);
        else if(_ids.length>1) 
        	createXMLMappingCompositeId(cfc,clazz, _ids,columnsInfo,tableName,data);
        else 
        	throw new HibernateORMException(data,cfc,"missing id property for entity ["+HibernateCaster.getEntityName(cfc)+"]",null);
	}
	

	private static PropertyCollection splitJoins(Component cfc,Map<String, PropertyCollection> joins,Property[] props,SessionFactoryData data) throws PageException {
		Struct sct=new StructImpl();
		ArrayList<Property> others = new ArrayList<Property>();
		java.util.List<Property> list;
		String table;
		Property prop;
		String fieldType;
		boolean isJoin;
		for(int i=0;i<props.length;i++){
			prop=props[i];
			table=getTable(cfc,prop,data);
			// joins
			if(!StringUtil.isEmpty(table,true)){
				isJoin=true;
				// wrong field type
				try {
					fieldType = toString(cfc, prop, sct, KeyConstants._fieldtype,false,data);
					
					if("collection".equalsIgnoreCase(fieldType)) isJoin=false;
					else if("primary".equals(fieldType)) isJoin=false;
					else if("version".equals(fieldType)) isJoin=false;
					else if("timestamp".equals(fieldType)) isJoin=false;
				} 
				catch (ORMException e) {}
				
				// missing column
				String columns=null;
				try {
					if(ORMUtil.isRelated(props[i])){
			        	columns=toString(cfc,props[i], prop.getDynamicAttributes(), "fkcolumn",data);
			        }
			        else {
			        	columns=toString(cfc,props[i], prop.getDynamicAttributes(), "joincolumn",data);
			        }
				}
				catch(ORMException e){}
				if(StringUtil.isEmpty(columns)) isJoin=false;
				
				if(isJoin){
					table=table.trim();
					list = (java.util.List<Property>) sct.get(table,null);
					if(list==null){
						list=new ArrayList<Property>();
						sct.setEL(KeyImpl.init(table), list);
					}
					list.add(prop);
					continue;
				}
			}
			others.add(prop);
		}
		
		// fill to joins
		Iterator<Entry<Key, Object>> it = sct.entryIterator();
		Entry<Key, Object> e;
		while(it.hasNext()){
			e = it.next();
			list=(java.util.List<Property>) e.getValue();
			joins.put(e.getKey().getString(), new PropertyCollection(e.getKey().getString(),list));
		}
		
		
		
		return new PropertyCollection(null,others);
	}
	
	
	
	

	private static Property[] getIds(Component cfc,PropertyCollection pc,SessionFactoryData data) throws PageException {
		return getIds(cfc,pc.getProperties(), pc.getTableName(),false,data);
	}
	
	private static Property[] getIds(Component cfc,Property[] props,String tableName, boolean ignoreTableName,SessionFactoryData data) throws PageException {
		ArrayList<Property> ids=new ArrayList<Property>();
        for(int y=0;y<props.length;y++){
        	if(!ignoreTableName && !hasTable(cfc,props[y], tableName,data)) continue;
        	
        	
        	String fieldType = Caster.toString(props[y].getDynamicAttributes().get(KeyConstants._fieldtype,null),null);
			if("id".equalsIgnoreCase(fieldType) || ListUtil.listFindNoCaseIgnoreEmpty(fieldType,"id",',')!=-1)
				ids.add(props[y]);
		}
        
        // no id field defined
        if(ids.size()==0) {
        	String fieldType;
        	for(int y=0;y<props.length;y++){
        		if(!ignoreTableName && !hasTable(cfc,props[y], tableName,data)) continue;
            	fieldType = Caster.toString(props[y].getDynamicAttributes().get(KeyConstants._fieldtype,null),null);
    			if(StringUtil.isEmpty(fieldType,true) && props[y].getName().equalsIgnoreCase("id")){
    				ids.add(props[y]);
    				props[y].getDynamicAttributes().setEL(KeyConstants._fieldtype, "id");
    			}
    		}
        } 
        
        // still no id field defined
        if(ids.size()==0 && props.length>0) {
        	String owner = props[0].getOwnerName();
			if(!StringUtil.isEmpty(owner)) owner=ListUtil.last(owner, '.').trim();
        	
        	String fieldType;
        	if(!StringUtil.isEmpty(owner)){
        		String id=owner+"id";
        		for(int y=0;y<props.length;y++){
        			if(!ignoreTableName && !hasTable(cfc,props[y], tableName,data)) continue;
                	fieldType = Caster.toString(props[y].getDynamicAttributes().get(KeyConstants._fieldtype,null),null);
	    			if(StringUtil.isEmpty(fieldType,true) && props[y].getName().equalsIgnoreCase(id)){
	    				ids.add(props[y]);
	    				props[y].getDynamicAttributes().setEL(KeyConstants._fieldtype, "id");
	    			}
	    		}
        	}
        } 
        return ids.toArray(new Property[ids.size()]);
	}






	private static void addVersion(Component cfc,Element clazz, PageContext pc,PropertyCollection propColl, Struct columnsInfo, String tableName,SessionFactoryData data) throws PageException {
    	Property[] props = propColl.getProperties();
		for(int y=0;y<props.length;y++){
			String fieldType = Caster.toString(props[y].getDynamicAttributes().get(KeyConstants._fieldtype,null),null);
			if("version".equalsIgnoreCase(fieldType))
				createXMLMappingVersion(clazz,pc, cfc,props[y],data);
			else if("timestamp".equalsIgnoreCase(fieldType))
				createXMLMappingTimestamp(clazz,pc,cfc, props[y],data);
		}
	}



	private static void addCollection(Component cfc,Element clazz, PageContext pc,PropertyCollection propColl, Struct columnsInfo, String tableName,SessionFactoryData data) throws PageException {
		Property[] props = propColl.getProperties();
		for(int y=0;y<props.length;y++){
			String fieldType = Caster.toString(props[y].getDynamicAttributes().get(KeyConstants._fieldtype,"column"),"column");
			if("collection".equalsIgnoreCase(fieldType))
				createXMLMappingCollection(clazz,pc, cfc,props[y],data);
		}
	}
	
	
	private static void addJoin(Component cfc,Element clazz, PageContext pc,Map<String, PropertyCollection> joins, Struct columnsInfo, String tableName,DatasourceConnection dc, SessionFactoryData data) throws PageException {
        
		Iterator<Entry<String, PropertyCollection>> it = joins.entrySet().iterator();
		Entry<String, PropertyCollection> entry;
		while(it.hasNext()){
			entry = it.next();
			addJoin(cfc,pc,columnsInfo,clazz,entry.getValue(),dc,data);
		}
		
		
		
    }
	
	private static void addJoin(Component cfc,PageContext pc,Struct columnsInfo, Element clazz, PropertyCollection coll, DatasourceConnection dc, SessionFactoryData data) throws PageException {
		String table = coll.getTableName();
		Property[] properties = coll.getProperties();
		if(properties.length==0) return;
		
		Document doc = XMLUtil.getDocument(clazz);
		
		Element join = doc.createElement("join");
        clazz.appendChild(join);
		
        join.setAttribute("table", escape(HibernateUtil.convertTableName(data,coll.getTableName())));
        //addTableInfo(joinNode, table, schema, catalog);
        
        Property first = properties[0];
        String schema = null, catalog=null, mappedBy=null, columns=null;
        if(ORMUtil.isRelated(first)){
        	catalog=toString(cfc,first, first.getDynamicAttributes(), "linkcatalog",data);
        	schema=toString(cfc,first, first.getDynamicAttributes(), "linkschema",data);
        	columns=toString(cfc,first, first.getDynamicAttributes(), "fkcolumn",data);
        	
        }
        else {
        	catalog=toString(cfc,first, first.getDynamicAttributes(), "catalog",data);
        	schema=toString(cfc,first, first.getDynamicAttributes(), "schema",data);
        	mappedBy=toString(cfc,first, first.getDynamicAttributes(), "mappedby",data);
        	columns=toString(cfc,first, first.getDynamicAttributes(), "joincolumn",data);
        }

        if(!StringUtil.isEmpty(catalog)) join.setAttribute("catalog", catalog);
        if(!StringUtil.isEmpty(schema)) join.setAttribute("schema", schema);
        
        Element key = doc.createElement("key");
        join.appendChild(key);
        if(!StringUtil.isEmpty(mappedBy)) key.setAttribute("property-ref", mappedBy);
        setColumn(doc, key, columns,data);
        
        addProperty(cfc,join,pc, coll,columnsInfo,table,data);
		int count=addRelation(cfc,join,pc, coll,columnsInfo,table,dc,data);
        
		if(count>0) join.setAttribute("inverse", "true");
			
		
	}







	



	private static int addRelation(Component cfc,Element clazz, PageContext pc,PropertyCollection propColl, Struct columnsInfo, String tableName, DatasourceConnection dc, SessionFactoryData data) throws PageException {
    	Property[] props = propColl.getProperties();
		int count=0;
    	for(int y=0;y<props.length;y++){
			String fieldType = Caster.toString(props[y].getDynamicAttributes().get(KeyConstants._fieldtype,"column"),"column");
			if("one-to-one".equalsIgnoreCase(fieldType)){
				createXMLMappingOneToOne(clazz,pc, cfc,props[y],data);
				count++;
			}
			else if("many-to-one".equalsIgnoreCase(fieldType)){
				createXMLMappingManyToOne(clazz,pc, cfc,props[y],propColl,data);
				count++;
			}
			else if("one-to-many".equalsIgnoreCase(fieldType)){
				createXMLMappingOneToMany(cfc,propColl,clazz,pc, props[y],data);
				count++;
			}
			else if("many-to-many".equalsIgnoreCase(fieldType)){
				createXMLMappingManyToMany(cfc,propColl,clazz,pc, props[y],dc,data);
				count++;
			}
		}
    	return count;
	}



	private static void addProperty(Component cfc,Element clazz, PageContext pc, PropertyCollection propColl, Struct columnsInfo, String tableName, SessionFactoryData data) throws PageException {
		Property[] props = propColl.getProperties();
		for(int y=0;y<props.length;y++){
			String fieldType = Caster.toString(props[y].getDynamicAttributes().get(KeyConstants._fieldtype,"column"),"column");
			if("column".equalsIgnoreCase(fieldType))
				createXMLMappingProperty(clazz,pc,cfc, props[y],columnsInfo,tableName,data);
		}
	}



	private static void addDiscriminator(Component cfc,Document doc,Element clazz, PageContext pc,Struct meta,SessionFactoryData data) throws DOMException, PageException {
		
    	 String str = toString(cfc,null,meta,"discriminatorColumn",data);
    	 if(!StringUtil.isEmpty(str,true)){
    		 Element disc = doc.createElement("discriminator");
    		 clazz.appendChild(disc);
    		 disc.setAttribute("column",formatColumn(str,data));
    	 }
    	 

    	
    	
	}



	private static void addGeneralClassAttributes(PageContext pc, Component cfc, Struct meta, Element clazz, SessionFactoryData data) throws PageException {
    	
    	// name
		clazz.setAttribute("node", HibernateCaster.toComponentName(cfc));
		
    	// entity-name
    	String str=toString(cfc,null,meta,"entityname",data);
		if(StringUtil.isEmpty(str,true)) str=HibernateCaster.getEntityName(cfc);
		clazz.setAttribute("entity-name",str);
		

        // batch-size
        Integer i = toInteger(cfc,meta,"batchsize",data);
        if(i!=null && i.intValue()>0)clazz.setAttribute("batch-size",Caster.toString(i));
		
		// dynamic-insert
        Boolean b = toBoolean(cfc,meta,"dynamicinsert",data);
        if(b!=null && b.booleanValue())clazz.setAttribute("dynamic-insert","true");
        
        // dynamic-update
        b=toBoolean(cfc,meta,"dynamicupdate",data);
        if(b!=null && b.booleanValue())clazz.setAttribute("dynamic-update","true");
        
		// lazy (dtd defintion:<!ATTLIST class lazy (true|false) #IMPLIED>)
        b=toBoolean(cfc,meta,"lazy",data);
        if(b==null) b=Boolean.TRUE;
        clazz.setAttribute("lazy",Caster.toString(b.booleanValue()));
		
        // select-before-update
        b=toBoolean(cfc,meta,"selectbeforeupdate",data);
        if(b!=null && b.booleanValue())clazz.setAttribute("select-before-update","true");

        // optimistic-lock
        str=toString(cfc,null,meta,"optimisticLock",data);
        if(!StringUtil.isEmpty(str,true)) {
        	str=str.trim().toLowerCase();
        	if("all".equals(str) || "dirty".equals(str) || "none".equals(str) || "version".equals(str))
        		clazz.setAttribute("optimistic-lock",str);
        	else
        		throw new HibernateORMException(data,cfc,"invalid value ["+str+"] for attribute [optimisticlock] of tag [component], valid values are [all,dirty,none,version]",null);
        }
        
        // read-only
        b=toBoolean(cfc,meta,"readOnly",data);
        if(b!=null && b.booleanValue()) clazz.setAttribute("mutable", "false");
        
        // rowid
        str=toString(cfc,null,meta,"rowid",data);
        if(!StringUtil.isEmpty(str,true)) clazz.setAttribute("rowid",str);
        
        // where
        str=toString(cfc,null,meta,"where",data);
        if(!StringUtil.isEmpty(str,true)) clazz.setAttribute("where", str);

       
	}
	private static void addGeneralTableAttributes(PageContext pc, Component cfc, Struct meta, Element clazz,SessionFactoryData data) throws PageException {
		 // table
        clazz.setAttribute("table",escape(getTableName(pc,meta,cfc,data)));
        
        // catalog
        String str = toString(cfc,null,meta,"catalog",data);
        if(str==null)// empty string is allowed as input
        	str=data.getORMConfiguration().getCatalog();
        if(!StringUtil.isEmpty(str,true)) clazz.setAttribute("catalog", str);
        
        // schema
        str=toString(cfc,null,meta,"schema",data);
        if(str==null)// empty string is allowed as input
        	str=data.getORMConfiguration().getSchema();
        if(!StringUtil.isEmpty(str,true)) clazz.setAttribute( "schema", str);
        
	}
	private static String escape(String str) {
		if(HibernateUtil.isKeyword(str)) return "`"+str+"`";
		return str;
	}






	private static String getTableName(PageContext pc, Struct meta, Component cfc,SessionFactoryData data) throws PageException {
		String tableName=toString(cfc,null,meta,"table",data);
		if(StringUtil.isEmpty(tableName,true)) 
			tableName=HibernateCaster.getEntityName(cfc);
		return HibernateUtil.convertTableName(data,tableName);
	}
	
	private static String getTable(Component cfc,Property prop,SessionFactoryData data) throws PageException {
		try {
			return HibernateUtil.convertTableName(data,toString(cfc,prop, prop.getDynamicAttributes(), "table",data));
		} catch (ORMException e) {
			return null;
		}
	}
	
	private static boolean hasTable(Component cfc,Property prop,String tableName,SessionFactoryData data) throws PageException {
		String t = getTable(cfc,prop,data);
		boolean left=StringUtil.isEmpty(t,true);
		boolean right=StringUtil.isEmpty(tableName,true);
		if(left && right) return true;
		if(left || right) return false;
		return tableName.trim().equalsIgnoreCase(t.trim());
	}






	private static void createXMLMappingCompositeId(Component cfc,Element clazz, Property[] props,Struct columnsInfo,String tableName, SessionFactoryData data) throws PageException {
		Struct meta;
		
		Document doc = XMLUtil.getDocument(clazz);
		Element cid = doc.createElement("composite-id");
		clazz.appendChild(cid);
		
		//cid.setAttribute("mapped","true");
		
		
		Property prop;
		// ids
		for(int y=0;y<props.length;y++){
			prop=props[y];
			meta = prop.getDynamicAttributes();
			Element key = doc.createElement("key-property");
			cid.appendChild(key);
			
			// name
			key.setAttribute("name",prop.getName());
			
			// column
			Element column = doc.createElement("column");
			key.appendChild(column);
			
			String str = toString(cfc,prop,meta,"column",data);
	    	if(StringUtil.isEmpty(str,true)) str=prop.getName();
	    	column.setAttribute("name",formatColumn(str,data));
	    	ColumnInfo info=getColumnInfo(columnsInfo,tableName,str,null);
	    	
            str = toString(cfc,prop,meta,"sqltype",data);
	    	if(!StringUtil.isEmpty(str,true)) column.setAttribute("sql-type",str);
	    	str = toString(cfc,prop,meta,"length",data);
	    	if(!StringUtil.isEmpty(str,true)) column.setAttribute("length",str);
            
	    	/*if(info!=null){
	    		column.setAttribute("sql-type",info.getTypeName());
	    		column.setAttribute("length",Caster.toString(info.getSize()));
	    	}*/
			
	    	 // type
			//str=getType(info,prop,meta,"long"); //MUSTMUST
			//key.setAttribute("type", str);
			
			String generator=toString(cfc,prop,meta,"generator",data);
			String type = getType(info,cfc,prop,meta,getDefaultTypeForGenerator(generator,"string"),data);
			if(!StringUtil.isEmpty(type))key.setAttribute("type", type);
			
			
			
            
		}
		
		// many-to-one
		String fieldType;
		for(int y=0;y<props.length;y++){
			prop=props[y];
			meta = prop.getDynamicAttributes();
			fieldType = toString(cfc,prop,meta,"fieldType",data);
			if(ListUtil.listFindNoCaseIgnoreEmpty(fieldType,"many-to-one",',')==-1)continue;
			
			Element key = doc.createElement("key-many-to-one");
			cid.appendChild(key);
			
			// name
			key.setAttribute("name",prop.getName());
			
			// entity-name
			setForeignEntityName(cfc,prop, meta, key,false,data);
			
			// fkcolum
			String str=toString(cfc,prop,meta,"fkcolumn",data);
			setColumn(doc, key, str,data);
			
			// lazy
			setLazy(cfc,prop,meta,key,data);
		}
	}
	
	
	private static void createXMLMappingId(Component cfc,Element clazz, Property prop,Struct columnsInfo,String tableName,SessionFactoryData data) throws PageException {
		Struct meta = prop.getDynamicAttributes();
		String str;
		
		Document doc = XMLUtil.getDocument(clazz);
		Element id = doc.createElement("id");
		clazz.appendChild(id);
			
        // access
    	str=toString(cfc,prop,meta,"access",data);
		if(!StringUtil.isEmpty(str,true))id.setAttribute("access", str);
    	
		// name
		id.setAttribute("name",prop.getName());
		
		// column
		Element column = doc.createElement("column");
		id.appendChild(column);

		str=toString(cfc,prop,meta,"column",data);
    	if(StringUtil.isEmpty(str,true)) str=prop.getName();
    	column.setAttribute("name",formatColumn(str,data));
    	ColumnInfo info=getColumnInfo(columnsInfo,tableName,str,null);
    	StringBuilder foreignCFC=new StringBuilder();
		String generator=createXMLMappingGenerator(id,cfc,prop,foreignCFC,data);

		str = toString(cfc,prop,meta,"length",data);
    	if(!StringUtil.isEmpty(str,true)) column.setAttribute("length",str);
        
		// type    
		String type = getType(info,cfc,prop,meta,getDefaultTypeForGenerator(generator,foreignCFC,data),data);
		//print.o(prop.getName()+":"+type+"::"+getDefaultTypeForGenerator(generator,foreignCFC));
		if(!StringUtil.isEmpty(type))id.setAttribute("type", type);
		
		// unsaved-value
		str=toString(cfc,prop,meta,"unsavedValue",data);
		if(str!=null)id.setAttribute("unsaved-value", str);
		
	}
	
	private static String getDefaultTypeForGenerator(String generator,StringBuilder foreignCFC, SessionFactoryData data) {
		String value = getDefaultTypeForGenerator(generator, null);
		if(value!=null) return value;
		
		if("foreign".equalsIgnoreCase(generator)) {
			if(!StringUtil.isEmpty(foreignCFC)) {
				try {
					Component cfc = data.getEntityByCFCName(foreignCFC.toString(), false);
					if(cfc!=null){
						Property[] ids = getIds(cfc,cfc.getProperties(true),null,true,data);
						if(!ArrayUtil.isEmpty(ids)){
							Property id = ids[0];
							id.getDynamicAttributes();
							Struct meta = id.getDynamicAttributes();
							if(meta!=null){
								String type=Caster.toString(meta.get(KeyConstants._type,null));
								
								if(!StringUtil.isEmpty(type) && (!type.equalsIgnoreCase("any") && !type.equalsIgnoreCase("object"))){
									return type;
								}
								
									String g=Caster.toString(meta.get(GENERATOR,null));
									if(!StringUtil.isEmpty(g)){
										return getDefaultTypeForGenerator(g,foreignCFC,data);
									}
								
							}
						}
					}
				}
				catch(Throwable t){}
			}
			return "string";
		}
		
		return "string";
	}
	
	private static String getDefaultTypeForGenerator(String generator,String defaultValue) {
		if("increment".equalsIgnoreCase(generator)) return "integer";
		if("identity".equalsIgnoreCase(generator)) return "integer";
		if("native".equalsIgnoreCase(generator)) return "integer";
		if("seqhilo".equalsIgnoreCase(generator)) return "string";
		if("uuid".equalsIgnoreCase(generator)) return "string";
		if("guid".equalsIgnoreCase(generator)) return "string";
		if("select".equalsIgnoreCase(generator)) return "string";
		return defaultValue;
	}
	
	private static String getType(ColumnInfo info, Component cfc,Property prop,Struct meta,String defaultValue, SessionFactoryData data) throws ORMException {
		// ormType
		String type = toString(cfc,prop,meta,"ormType",data);
		//type=HibernateCaster.toHibernateType(info,type,null);
		
		// dataType
		if(StringUtil.isEmpty(type,true)){
			type=toString(cfc,prop,meta,"dataType",data);
			//type=HibernateCaster.toHibernateType(info,type,null);
		}
		
		// type
		if(StringUtil.isEmpty(type,true)){
			type=prop.getType();
			//type=HibernateCaster.toHibernateType(info,type,null);
		}
		
		// type from db info
		if(StringUtil.isEmpty(type,true)){
			if(info!=null){
				type=info.getTypeName();
				//type=HibernateCaster.toHibernateType(info,type,defaultValue);
			}
			else return defaultValue;
		}
		
		return HibernateCaster.toHibernateType(info,type,defaultValue);
	}









	private static ColumnInfo getColumnInfo(Struct columnsInfo,String tableName,String columnName,ColumnInfo defaultValue) {
		if(columnsInfo!=null) {
	    	ColumnInfo info = (ColumnInfo) columnsInfo.get(KeyImpl.getInstance(columnName),null);
			if(info==null) return defaultValue;
			return info;
    	}
		return defaultValue;
	}
	
	/*private static ColumnInfo getColumnInfo(Struct columnsInfo,String tableName,String columnName,ORMEngine engine) throws ORMException {
		if(columnsInfo!=null) {
	    	ColumnInfo info = (ColumnInfo) columnsInfo.get(columnName,null);
			if(info==null) {
				String msg="table ["+tableName+"] has no column with name ["+columnName+"]";
				if(columnsInfo!=null)
					msg+=", column names are ["+List.arrayToList(columnsInfo.keys(), ", ")+"]";
				ORMUtil.printError(msg, engine);
				
				//throw new ORMException(msg);
			}
			return info;
    	}
		return null;
	}*/

	private static String createXMLMappingGenerator(Element id,Component cfc,Property prop,StringBuilder foreignCFC, SessionFactoryData data) throws PageException {
		Struct meta = prop.getDynamicAttributes();
		
		// generator
		String className=toString(cfc,prop,meta,"generator",data);
		if(StringUtil.isEmpty(className,true)) return null;
		

		Document doc = XMLUtil.getDocument(id);
		Element generator = doc.createElement("generator");
		id.appendChild(generator);
		
		generator.setAttribute("class", className);
		
		//print.e("generator:"+className);
		
		// params
		Object obj=meta.get(PARAMS,null);
		//if(obj!=null){
			Struct sct=null;
			if(obj==null) obj=new StructImpl();
			else if(obj instanceof String) obj=ORMUtil.convertToSimpleMap((String)obj);
			
			if(Decision.isStruct(obj)) sct=Caster.toStruct(obj);
			else throw new HibernateORMException(data,cfc,"invalid value for attribute [params] of tag [property]",null);
			className=className.trim().toLowerCase();
			
			// special classes
			if("foreign".equals(className)){
				if(!sct.containsKey(PROPERTY)) sct.setEL(PROPERTY, toString(cfc,prop,meta, PROPERTY,true,data));
				
				if(sct.containsKey(PROPERTY)){
					String p = Caster.toString(sct.get(PROPERTY),null);
					if(!StringUtil.isEmpty(p))foreignCFC.append(p);
				}
				
				
			}
			else if("select".equals(className)){
				//print.e("select:"+toString(meta, "selectKey",true));
				if(!sct.containsKey(KeyConstants._key)) sct.setEL(KeyConstants._key, toString(cfc,prop,meta, "selectKey",true,data));
			}
			else if("sequence".equals(className)){
				if(!sct.containsKey(SEQUENCE)) sct.setEL(SEQUENCE, toString(cfc,prop,meta, "sequence",true,data));
			}
			
			//Key[] keys = sct.keys();
			Iterator<Entry<Key, Object>> it = sct.entryIterator();
			Entry<Key, Object> e;
			Element param;
			while(it.hasNext()){
				e = it.next();
				param = doc.createElement("param");
				generator.appendChild(param);
				
				param.setAttribute( "name", e.getKey().getLowerString());
				param.appendChild(doc.createTextNode(Caster.toString(e.getValue())));
				
			}
		//}
		return className;
	}
	
	
	
	

	

	private static void createXMLMappingProperty(Element clazz, PageContext pc,Component cfc,Property prop,Struct columnsInfo,String tableName,SessionFactoryData data) throws PageException {
		Struct meta = prop.getDynamicAttributes();
		
        
		
		// get table name
		String columnName=toString(cfc,prop,meta,"column",data);
    	if(StringUtil.isEmpty(columnName,true)) columnName=prop.getName();
    	
    	ColumnInfo info=getColumnInfo(columnsInfo,tableName,columnName,null);
		
		Document doc = XMLUtil.getDocument(clazz);
		final Element property = doc.createElement("property");
		clazz.appendChild(property);
		
		//name
		property.setAttribute("name",prop.getName());
		
		// type
		String str = getType(info, cfc,prop, meta, "string",data);
		property.setAttribute("type",str);
		
		
		
		// formula or column
		str=toString(cfc,prop,meta,"formula",data);
        Boolean b;
		if(!StringUtil.isEmpty(str,true))	{
        	property.setAttribute("formula","("+str+")");
        }
        else {
        	//property.setAttribute("column",columnName);
        	
        	Element column = doc.createElement("column");
        	property.appendChild(column);
        	column.setAttribute("name", escape(HibernateUtil.convertColumnName(data,columnName)));

            // check
            str=toString(cfc,prop,meta,"check",data);
            if(!StringUtil.isEmpty(str,true)) column.setAttribute("check",str);
            
	        // default
	        str=toString(cfc,prop,meta,"dbDefault",data);
	        if(!StringUtil.isEmpty(str,true)) column.setAttribute("default",str);
            
            // index
            str=toString(cfc,prop,meta,"index",data);
            if(!StringUtil.isEmpty(str,true)) column.setAttribute("index",str);
        	
        	// length
            Integer i = toInteger(cfc,meta,"length",data);
            if(i!=null && i>0) column.setAttribute("length",Caster.toString(i.intValue()));
            
            // not-null
            b=toBoolean(cfc,meta,"notnull",data);
            if(b!=null && b.booleanValue())column.setAttribute("not-null","true");
            
            // precision
            i=toInteger(cfc,meta,"precision",data);
            if(i!=null && i>-1) column.setAttribute("precision",Caster.toString(i.intValue()));
            
            // scale
            i=toInteger(cfc,meta,"scale",data);
            if(i!=null && i>-1) column.setAttribute("scale",Caster.toString(i.intValue()));
            
            // sql-type
            str=toString(cfc,prop,meta,"sqltype",data);
            if(!StringUtil.isEmpty(str,true)) column.setAttribute("sql-type",str);
            
        // unique
            b=toBoolean(cfc,meta,"unique",data);
	        if(b!=null && b.booleanValue())column.setAttribute("unique","true");
	        
	        // unique-key
	        str=toString(cfc,prop,meta,"uniqueKey",data);
	        if(StringUtil.isEmpty(str))str=Caster.toString(meta.get(UNIQUE_KEY_NAME,null),null);
	        if(!StringUtil.isEmpty(str,true)) column.setAttribute("unique-key",str);
	        
	        
        }
        
        // generated
        str=toString(cfc,prop,meta,"generated",data);
        if(!StringUtil.isEmpty(str,true)){
        	str=str.trim().toLowerCase();
        	
        	if("always".equals(str) || "insert".equals(str) || "never".equals(str))
        		property.setAttribute("generated",str);
        	else
        		throw invalidValue(cfc,prop,"generated",str,"always,insert,never",data);
				//throw new ORMException("invalid value ["+str+"] for attribute [generated] of column ["+columnName+"], valid values are [always,insert,never]");
        }
        
        
        // update
        b=toBoolean(cfc,meta,"update",data);
        if(b!=null && !b.booleanValue())property.setAttribute("update","false");
        
        // insert
        b=toBoolean(cfc,meta,"insert",data);
        if(b!=null && !b.booleanValue())property.setAttribute("insert","false");
        
        // lazy (dtd defintion:<!ATTLIST property lazy (true|false) "false">)
        b=toBoolean(cfc,meta,"lazy",data);
        if(b!=null && b.booleanValue())property.setAttribute("lazy","true");
        
        // optimistic-lock
        b=toBoolean(cfc,meta,"optimisticlock",data);
        if(b!=null && !b.booleanValue())property.setAttribute("optimistic-lock","false");
        
	}
	
	
	/*
	MUST dies kommt aber nicht hier sondern in verarbeitung in component
	<cfproperty 
    persistent="true|false" 
   >
	 * */
	private static void createXMLMappingOneToOne(Element clazz, PageContext pc,Component cfc,Property prop,SessionFactoryData data) throws PageException {
		Struct meta = prop.getDynamicAttributes();
		
		Boolean b;
		
		Document doc = XMLUtil.getDocument(clazz);
		Element x2o;
		
		// column
		String fkcolumn=toString(cfc,prop,meta,"fkcolumn",data);
		String linkTable=toString(cfc,prop,meta,"linkTable",data);
		
		
		if(!StringUtil.isEmpty(linkTable,true) || !StringUtil.isEmpty(fkcolumn,true)) {
			clazz=getJoin(clazz);
			
			x2o= doc.createElement("many-to-one");
			//x2o.setAttribute("column", fkcolumn);
			x2o.setAttribute("unique", "true");
			
			if(!StringUtil.isEmpty(linkTable,true)){
				setColumn(doc, x2o, linkTable,data);
			}
			else {
				setColumn(doc, x2o, fkcolumn,data);
			}

			
			
			
			// update
	        b=toBoolean(cfc,meta,"update",data);
	        if(b!=null)x2o.setAttribute("update",Caster.toString(b.booleanValue()));
	        
	        // insert
	        b=toBoolean(cfc,meta,"insert",data);
	        if(b!=null)x2o.setAttribute("insert",Caster.toString(b.booleanValue()));
	        
	        // not-null
	        b=toBoolean(cfc,meta,"notNull",data);
	        if(b!=null)x2o.setAttribute("not-null",Caster.toString(b.booleanValue()));
	        
	        
	        // optimistic-lock
	        b=toBoolean(cfc,meta,"optimisticLock",data);
	        if(b!=null)x2o.setAttribute("optimistic-lock",Caster.toString(b.booleanValue()));
	        
	        // not-found
			b=toBoolean(cfc,meta, "missingRowIgnored",data);
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
		String str=toString(cfc,prop,meta,"access",data);
		if(!StringUtil.isEmpty(str,true)) x2o.setAttribute("access", str);
			
		// constrained
		b=toBoolean(cfc,meta, "constrained",data);
        if(b!=null && b.booleanValue()) x2o.setAttribute("constrained", "true");
		
		// formula
		str=toString(cfc,prop,meta,"formula",data);
		if(!StringUtil.isEmpty(str,true)) x2o.setAttribute("formula", str);
		
		// embed-xml
		str=toString(cfc,prop,meta,"embedXml",data);
		if(!StringUtil.isEmpty(str,true)) x2o.setAttribute("embed-xml", str);
		
		// property-ref
		str=toString(cfc,prop,meta,"mappedBy",data);
		if(!StringUtil.isEmpty(str,true)) x2o.setAttribute("property-ref", str);
		
		// foreign-key
		str=toString(cfc,prop,meta,"foreignKeyName",data);
		if(StringUtil.isEmpty(str,true)) str=toString(cfc,prop,meta,"foreignKey",data);
		if(!StringUtil.isEmpty(str,true)) x2o.setAttribute("foreign-key", str);
        
		setForeignEntityName(cfc,prop,meta,x2o,true,data);
		
		createXMLMappingXToX(x2o, pc,cfc,prop,meta,data);
	}
	
	
	
	
	
	
	private static Component loadForeignCFC(PageContext pc,Component cfc,Property prop, Struct meta, SessionFactoryData data) throws PageException {
		// entity
		String str=toString(cfc,prop,meta,"entityName",data);
		Component fcfc=null;
		
		if(!StringUtil.isEmpty(str,true)) {
			fcfc = data.getEntityByEntityName(str, false);
			if(fcfc!=null) return fcfc;
		}
			
		str = toString(cfc,prop,meta,"cfc",false,data);
		if(!StringUtil.isEmpty(str,true)){
			return data.getEntityByCFCName(str, false);
		}
		return null;
	}









	private static void createXMLMappingCollection(Element clazz, PageContext pc,Component cfc,Property prop,SessionFactoryData data) throws PageException {
		Struct meta = prop.getDynamicAttributes();
		Document doc = XMLUtil.getDocument(clazz);
		Element el=null;
		        
		// collection type
		String str=prop.getType();
		if(StringUtil.isEmpty(str,true) || "any".equalsIgnoreCase(str) || "object".equalsIgnoreCase(str))str="array";
		else str=str.trim().toLowerCase();
		
		
		
		// bag
		if("array".equals(str) || "bag".equals(str)){
			el = doc.createElement("bag");
		}
		// map
		else if("struct".equals(str) || "map".equals(str)){
			el = doc.createElement("map");
			
			
			// map-key
			str=toString(cfc,prop,meta,"structKeyColumn",true,data);
			if(!StringUtil.isEmpty(str,true)) {
				Element mapKey=doc.createElement("map-key");
				el.appendChild(mapKey);
				mapKey.setAttribute("column", str);
				
				// type
				str=toString(cfc,prop,meta,"structKeyType",data);
				if(!StringUtil.isEmpty(str,true))mapKey.setAttribute("type", str);
				else mapKey.setAttribute("type", "string");
			}
		}
		else throw invalidValue(cfc,prop,"collectiontype",str,"array,struct",data);
		//throw new ORMException("invalid value ["+str+"] for attribute [collectiontype], valid values are [array,struct]");
		
		setBeforeJoin(clazz,el);
		
		// name 
		el.setAttribute("name", prop.getName());
		
		// table
		str=toString(cfc,prop,meta, "table",true,data);
		el.setAttribute("table",escape(HibernateUtil.convertTableName(data,str)));
		
		// catalog
		str=toString(cfc,prop,meta, "catalog",data);
		if(!StringUtil.isEmpty(str,true))el.setAttribute("catalog",str);
		
		// schema
		str=toString(cfc,prop,meta, "schema",data);
		if(!StringUtil.isEmpty(str,true))el.setAttribute("schema",str);
		
		// mutable
		Boolean b=toBoolean(cfc,meta, "readonly",data);
        if(b!=null && b.booleanValue()) el.setAttribute("mutable", "false");
		
		// order-by
		str=toString(cfc,prop,meta, "orderby",data);
		if(!StringUtil.isEmpty(str,true))el.setAttribute("order-by",str);
		
		// element-column
		str=toString(cfc,prop,meta,"elementcolumn",data);
		if(!StringUtil.isEmpty(str,true)){
			Element element = doc.createElement("element");
			el.appendChild(element);
			
			// column
			element.setAttribute("column", formatColumn(str,data));
			
			// type
			str=toString(cfc,prop,meta,"elementtype",data);
			if(!StringUtil.isEmpty(str,true)) element.setAttribute("type", str);
		}
		
        // batch-size
        Integer i=toInteger(cfc,meta, "batchsize",data);
        if(i!=null && i.intValue()>1) el.setAttribute("batch-size", Caster.toString(i.intValue()));
 
		// column
		str=toString(cfc,prop,meta,"fkcolumn",data);
		if(StringUtil.isEmpty(str,true)) str=toString(cfc,prop,meta,"column",data);
		if(!StringUtil.isEmpty(str,true)){
			Element key = doc.createElement("key");
			XMLUtil.setFirst(el,key);
			//el.appendChild(key);
			
			// column
			key.setAttribute("column", formatColumn(str,data));
			
			// property-ref
			str=toString(cfc,prop,meta,"mappedBy",data);
			if(!StringUtil.isEmpty(str,true)) key.setAttribute("property-ref", str);
		}
		
		// cache
		setCacheStrategy(cfc,prop,doc, meta, el,data);
		
		// optimistic-lock
		b=toBoolean(cfc,meta, "optimisticlock",data);
        if(b!=null && !b.booleanValue()) el.setAttribute("optimistic-lock", "false");
        
       
	}
	
	
	private static void setBeforeJoin(Element clazz, Element el) {
		Element join;
		if(clazz.getNodeName().equals("join")) {
			join=clazz;
			clazz = getClazz(clazz);
		}
		else {
			join = getJoin(clazz);
		}
		
		if(join==clazz) clazz.appendChild(el);
		else clazz.insertBefore(el, join);
		
		
	}
	
	private static Element getClazz(Element join) {
		if(join.getNodeName().equals("join")){
			return (Element) join.getParentNode();
		}
		return join;
	}

	private static Element getJoin(Element clazz) {
		if(clazz.getNodeName().equals("subclass")){
			NodeList joins = clazz.getElementsByTagName("join");
			if(joins!=null && joins.getLength()>0)
				return (Element)joins.item(0);
		}
		return clazz;
	}












	private static void createXMLMappingManyToMany(Component cfc,PropertyCollection propColl,Element clazz, PageContext pc,Property prop,DatasourceConnection dc, SessionFactoryData data) throws PageException {
		Element el = createXMLMappingXToMany(propColl,clazz, pc, cfc,prop,data);
		Struct meta = prop.getDynamicAttributes();
		Document doc = XMLUtil.getDocument(clazz);
		Element m2m = doc.createElement("many-to-many");
		el.appendChild(m2m);
		
		// link
		setLink(cfc,prop,el,meta,true,data);
		
		setForeignEntityName(cfc,prop, meta, m2m,true,data);

        // order-by
		String str = toString(cfc,prop,meta,"orderby",data);
		if(!StringUtil.isEmpty(str,true))m2m.setAttribute("order-by", str);
		
		// column
		str=toString(cfc,prop,meta,"inversejoincolumn",data);
		
		// build fkcolumn name
		if(StringUtil.isEmpty(str,true)) {
			Component other = loadForeignCFC(pc, cfc, prop, meta,data);
			if(other!=null){
				boolean isClass=StringUtil.isEmpty(other.getExtends());
				// MZ: Recursive search for persistent mappedSuperclass properties
				Property[] _props=getProperties(pc,other,dc,meta,isClass, true,data);
				PropertyCollection _propColl = splitJoins(cfc,new HashMap<String, PropertyCollection>(), _props,data);
				_props=_propColl.getProperties();
				
				Struct m;
				Property _prop=null;
				for(int i=0;i<_props.length;i++){
					m = _props[i].getDynamicAttributes();
					// fieldtype
					String fieldtype = Caster.toString(m.get(KeyConstants._fieldtype,null),null);
					if("many-to-many".equalsIgnoreCase(fieldtype)) {
						// linktable
						String currLinkTable=Caster.toString(meta.get(LINK_TABLE,null),null);
						String othLinkTable=Caster.toString(m.get(LINK_TABLE,null),null);
						if(currLinkTable.equals(othLinkTable)) {
							// cfc name
							String cfcName=Caster.toString(m.get(CFC,null),null);
							if(cfc.equalTo(cfcName)){
								_prop=_props[i];
							}
						}
					}
				}
				str=createM2MFKColumnName( other, _prop, _propColl,data);
			}
		}
		setColumn(doc, m2m, str,data);
		
		// not-found
		Boolean b=toBoolean(cfc,meta, "missingrowignored",data);
        if(b!=null && b.booleanValue()) m2m.setAttribute("not-found", "ignore");
        
        // property-ref
		str=toString(cfc,prop,meta,"mappedby",data);
		if(!StringUtil.isEmpty(str,true)) m2m.setAttribute("property-ref", str);
		
		// foreign-key
		str=toString(cfc,prop,meta,"foreignKeyName",data);
		if(StringUtil.isEmpty(str,true)) str=toString(cfc,prop,meta,"foreignKey",data);
		if(!StringUtil.isEmpty(str,true)) m2m.setAttribute("foreign-key", str);
	}
	
	private static boolean setLink(Component cfc,Property prop,Element el, Struct meta, boolean linkTableRequired,SessionFactoryData data) throws PageException {
		String str=toString(cfc,prop,meta, "linktable",linkTableRequired,data);
		
		
		if(!StringUtil.isEmpty(str,true)){

			el.setAttribute("table", escape(HibernateUtil.convertTableName(data,str)));
		
			// schema
			str=toString(cfc,prop,meta, "linkschema",data);
			if(StringUtil.isEmpty(str,true)) str=data.getORMConfiguration().getSchema();
			if(!StringUtil.isEmpty(str,true)) el.setAttribute("schema", str);
			
			// catalog
			str=toString(cfc,prop,meta, "linkcatalog",data);
			if(StringUtil.isEmpty(str,true)) str=data.getORMConfiguration().getCatalog();
			if(!StringUtil.isEmpty(str,true)) el.setAttribute("catalog", str);
			return true;
		}
		return false;
	}









	private static void createXMLMappingOneToMany(Component cfc,PropertyCollection propColl,Element clazz, PageContext pc,Property prop, SessionFactoryData data) throws PageException {
		Element el = createXMLMappingXToMany(propColl,clazz, pc, cfc,prop,data);
		Struct meta = prop.getDynamicAttributes();
		Document doc = XMLUtil.getDocument(clazz);
		Element x2m;
		
		
        // order-by
		String str = toString(cfc,prop,meta,"orderby",data);
		if(!StringUtil.isEmpty(str,true))el.setAttribute("order-by", str);
		
		// link
		if(setLink(cfc,prop,el,meta,false,data)){
			x2m = doc.createElement("many-to-many");
			x2m.setAttribute("unique","true");
			
			str=toString(cfc,prop,meta,"inversejoincolumn",data);
			setColumn(doc, x2m, str,data);
		}
		else {
			x2m = doc.createElement("one-to-many");
		}
		el.appendChild(x2m);
		

		// entity-name
		
		setForeignEntityName(cfc,prop,meta,x2m,true,data);
		
	}

	

	
	
	
	
	
	private static Element createXMLMappingXToMany(PropertyCollection propColl,Element clazz, PageContext pc,Component cfc,Property prop, SessionFactoryData data) throws PageException {
		final Struct meta = prop.getDynamicAttributes();
		Document doc = XMLUtil.getDocument(clazz);
		Element el=null;
		
		
		
	// collection type
		String str=prop.getType();
		if(StringUtil.isEmpty(str,true) || "any".equalsIgnoreCase(str) || "object".equalsIgnoreCase(str))str="array";
		else str=str.trim().toLowerCase();
		
		Element mapKey=null;
		// bag
		if("array".equals(str) || "bag".equals(str)){
			el = doc.createElement("bag");
			
		}
		// map
		else if("struct".equals(str) || "map".equals(str)){
			el = doc.createElement("map");
			
			// map-key
			mapKey = doc.createElement("map-key");
			//el.appendChild(mapKey);
			
			// column
			str=toString(cfc,prop,meta,"structKeyColumn",true,data);
			mapKey.setAttribute("column", formatColumn(str,data));
			
			// type
			str=toString(cfc,prop,meta,"structKeyType",data);
			if(!StringUtil.isEmpty(str,true))mapKey.setAttribute("type", str);
			else mapKey.setAttribute("type", "string");// MUST get type dynamicly
		}
		else throw invalidValue(cfc,prop,"collectiontype",str,"array,struct",data);
		//throw new ORMException("invalid value ["+str+"] for attribute [collectiontype], valid values are [array,struct]");
		
		setBeforeJoin(clazz,el);
		

		
		// batch-size
        Integer i=toInteger(cfc,meta, "batchsize",data);
        if(i!=null){
        	if(i.intValue()>1) el.setAttribute("batch-size", Caster.toString(i.intValue()));
        }
 
		// cacheUse
        setCacheStrategy(cfc,prop,doc, meta, el,data);
        
        // column
        str=createFKColumnName(cfc,prop,propColl,data);
		
		if(!StringUtil.isEmpty(str,true)){
			Element key = doc.createElement("key");
			el.appendChild(key);
			
			// column
			setColumn(doc,key,str,data);
			
			// property-ref
			str=toString(cfc,prop,meta,"mappedBy",data);
			if(!StringUtil.isEmpty(str,true)) key.setAttribute("property-ref", str);
		}
		
        // inverse
		Boolean b = toBoolean(cfc,meta, "inverse",data);
        if(b!=null && b.booleanValue()) el.setAttribute("inverse", "true");
        
		
		
		// mutable 
		b = toBoolean(cfc,meta, "readonly",data);
        if(b!=null && b.booleanValue()) el.setAttribute("mutable", "false");
		
		// optimistic-lock
		b=toBoolean(cfc,meta, "optimisticlock",data);
        if(b!=null && !b.booleanValue()) el.setAttribute("optimistic-lock", "false");
        
		// where
		str=toString(cfc,prop,meta,"where",data);
		if(!StringUtil.isEmpty(str,true)) el.setAttribute("where", str);
        
		// add map key
        if(mapKey!=null)el.appendChild(mapKey);
        
        
		createXMLMappingXToX(el, pc,cfc,prop,meta,data);
		
		return el;
	}
	
	
	
	private static String createFKColumnName(Component cfc, Property prop, PropertyCollection propColl, SessionFactoryData data) throws PageException {
		
		
		// fk column from local defintion
		String str=prop==null?null:toString(cfc,prop,prop.getDynamicAttributes(),"fkcolumn",data);
		if(!StringUtil.isEmpty(str))
			return str;
		
		// no local defintion, get from Foreign enity
		Struct meta = prop.getDynamicAttributes();
		String type=toString(cfc,prop,meta,"fieldtype",false,data);
		String otherType;
		if("many-to-one".equalsIgnoreCase(type)) 		otherType="one-to-many";
		else if("one-to-many".equalsIgnoreCase(type)) 	otherType="many-to-one";
		else return createM2MFKColumnName( cfc, prop, propColl,data);
		
		String feName = toString(cfc,prop,meta,"cfc",true,data);
		Component feCFC=data.getEntityByCFCName(feName, false);
		Property[] feProps = feCFC.getProperties(true);
		
		Property p;
		Component _cfc;
		for(int i=0;i<feProps.length;i++){
			p=feProps[i];

			// compare fieldType
			str=toString(feCFC,p,p.getDynamicAttributes(),"fieldtype",false,data);
			if(!otherType.equalsIgnoreCase(str)) continue;
			
			// compare cfc
			str=toString(feCFC,p,p.getDynamicAttributes(),"cfc",false,data);
			if(StringUtil.isEmpty(str)) continue;
			_cfc=data.getEntityByCFCName(str, false);
			if(_cfc==null || !_cfc.equals(cfc))continue;
			
			// get fkcolumn
			str=toString(_cfc,p,p.getDynamicAttributes(),"fkcolumn",data);
			if(!StringUtil.isEmpty(str)) return str;
			
			
		}
		throw new HibernateORMException(data,null,"cannot terminate foreign key column name for component "+cfc.getName(),null);
	}
	
	
	private static String createM2MFKColumnName(Component cfc, Property prop, PropertyCollection propColl,SessionFactoryData data) throws PageException {
		
		String str=prop==null?null:toString(cfc,prop,prop.getDynamicAttributes(),"fkcolumn",data);
		if(StringUtil.isEmpty(str)){
			Property[] ids = getIds(cfc,propColl,data);
			if(ids.length==1) {
				str=toString(cfc,ids[0],ids[0].getDynamicAttributes(),"column",data);
		    	if(StringUtil.isEmpty(str,true)) str=ids[0].getName();
			}
			else if(prop!=null)str=toString(cfc,prop,prop.getDynamicAttributes(),"fkcolumn",true,data);
			else
				throw new HibernateORMException(data,null,"cannot terminate foreign key column name for component "+cfc.getName(),null);
			
			str=HibernateCaster.getEntityName(cfc)+"_"+str;
		}
    	return str;
	}

	private static void setForeignEntityName(Component cfc,Property prop, Struct meta, Element el, boolean cfcRequired, SessionFactoryData data) throws PageException {
		// entity
		String str=cfcRequired?null:toString(cfc,prop,meta,"entityName",data);
		if(!StringUtil.isEmpty(str,true)) {
			el.setAttribute("entity-name", str);
		}
		else {
			// cfc
			//createFKColumnName( cfc, prop, propColl);
			
			str = toString(cfc,prop,meta,"cfc",cfcRequired,data);
			if(!StringUtil.isEmpty(str,true)){
				Component _cfc=data.getEntityByCFCName(str, false);
				str=HibernateCaster.getEntityName(_cfc);
				el.setAttribute("entity-name", str);
			}
		}
	}









	private static void setCacheStrategy(Component cfc,Property prop,Document doc,Struct meta, Element el, SessionFactoryData data) throws ORMException {
		String strategy = toString(cfc,prop,meta,"cacheuse",data);
		
		if(!StringUtil.isEmpty(strategy,true)){
			strategy=strategy.trim().toLowerCase();
			if("read-only".equals(strategy) || "nonstrict-read-write".equals(strategy) || "read-write".equals(strategy) || "transactional".equals(strategy)){
				Element cache = doc.createElement("cache");
				XMLUtil.setFirst(el, cache);
				el.appendChild(cache);
				cache.setAttribute("usage", strategy);
				String name = toString(cfc,prop,meta,"cacheName",data);
				if(!StringUtil.isEmpty(name,true)){
					cache.setAttribute("region", name);
				}
			}	
			else
				throw new HibernateORMException(data,cfc,"invalid value ["+strategy+"] for attribute [cacheuse], valid values are [read-only,nonstrict-read-write,read-write,transactional]",null);
		}
		
	}

	





	private static void setColumn(Document doc, Element el, String columnValue,SessionFactoryData data) throws PageException {
		if(StringUtil.isEmpty(columnValue,true)) return;
		
		String[] arr = ListUtil.toStringArray(ListUtil.listToArray(columnValue, ','));
		if(arr.length==1){
			el.setAttribute("column", formatColumn(arr[0],data));
		}
		else {
			Element column;
			for(int i=0;i<arr.length;i++){
				column=doc.createElement("column");
				el.appendChild(column);
				column.setAttribute("name", formatColumn(arr[i],data));
			}
		}
	}









	private static void createXMLMappingManyToOne(Element clazz, PageContext pc,Component cfc,Property prop, PropertyCollection propColl, SessionFactoryData data) throws PageException {
		Struct meta = prop.getDynamicAttributes();
		Boolean b;
		
		Document doc = XMLUtil.getDocument(clazz);
		clazz=getJoin(clazz);
		
		Element m2o = doc.createElement("many-to-one");
		clazz.appendChild(m2o);
		
		// columns
		String linktable = toString(cfc,prop,meta,"linktable",data);
		String _columns;
		if(!StringUtil.isEmpty(linktable,true)) _columns=toString(cfc,prop,meta,"inversejoincolumn",data);
		else _columns=createFKColumnName(cfc, prop, propColl,data);//toString(cfc,prop,meta,"fkcolumn");
		setColumn(doc, m2o, _columns,data);
		
		// cfc
		setForeignEntityName(cfc,prop,meta,m2o,true,data);
		
		// column
		//String str=toString(prop,meta,"column",true);
		//m2o.setAttribute("column", str);
		
		// insert
		b=toBoolean(cfc,meta, "insert",data);
        if(b!=null && !b.booleanValue()) m2o.setAttribute("insert", "false");
		
        // update
		b=toBoolean(cfc,meta, "update",data);
        if(b!=null && !b.booleanValue()) m2o.setAttribute("update", "false");
		
        // property-ref
		String str=toString(cfc,prop,meta,"mappedBy",data);
		if(!StringUtil.isEmpty(str,true)) m2o.setAttribute("property-ref", str);

        // update
		b=toBoolean(cfc,meta, "unique",data);
        if(b!=null && b.booleanValue()) m2o.setAttribute("unique", "true");

        // not-null
		b=toBoolean(cfc,meta, "notnull",data);
        if(b!=null && b.booleanValue()) m2o.setAttribute("not-null", "true");
		
        // optimistic-lock
		b=toBoolean(cfc,meta, "optimisticLock",data);
        if(b!=null && !b.booleanValue()) m2o.setAttribute("optimistic-lock", "false");
        
        // not-found
		b=toBoolean(cfc,meta, "missingRowIgnored",data);
        if(b!=null && b.booleanValue()) m2o.setAttribute("not-found", "ignore");
        
        // index
		str=toString(cfc,prop,meta,"index",data);
		if(!StringUtil.isEmpty(str,true)) m2o.setAttribute("index", str);
        
        // unique-key
		str=toString(cfc,prop,meta,"uniqueKeyName",data);
		if(StringUtil.isEmpty(str,true))str=toString(cfc,prop,meta,"uniqueKey",data);
		if(!StringUtil.isEmpty(str,true)) m2o.setAttribute("unique-key", str);
		
		// foreign-key
		str=toString(cfc,prop,meta,"foreignKeyName",data);
		if(StringUtil.isEmpty(str,true)) str=toString(cfc,prop,meta,"foreignKey",data);
		if(!StringUtil.isEmpty(str,true)) m2o.setAttribute("foreign-key", str);

		// access
		str=toString(cfc,prop,meta,"access",data);
		if(!StringUtil.isEmpty(str,true)) m2o.setAttribute("access", str);
		
        createXMLMappingXToX(m2o, pc,cfc,prop,meta,data);
        
	}
	
	
	
	
	
	
	
	
	private static String formatColumn(String name,SessionFactoryData data) throws PageException {
        name=name.trim();
		return escape(HibernateUtil.convertColumnName(data,name));
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
	private static void createXMLMappingXToX(Element x2x, PageContext pc, Component cfc,Property prop, Struct meta, SessionFactoryData data) throws ORMException {
		x2x.setAttribute("name",prop.getName());
		
		// cascade
		String str=toString(cfc,prop,meta,"cascade",data);
		if(!StringUtil.isEmpty(str,true)) x2x.setAttribute("cascade", str);
		
		// fetch
		str=toString(cfc,prop,meta,"fetch",data);
		if(!StringUtil.isEmpty(str,true)) {
			str=str.trim().toLowerCase();
			if("join".equals(str) || "select".equals(str))
				x2x.setAttribute("fetch", str);
			else
				throw invalidValue(cfc,prop,"fetch",str,"join,select",data);
			//throw new ORMException("invalid value ["+str+"] for attribute [fetch], valid values are [join,select]");
		}
		
		// lazy
		setLazy(cfc,prop,meta,x2x,data);
		
	}



	

	private static void setLazy(Component cfc,Property prop, Struct meta, Element x2x, SessionFactoryData data) throws ORMException {
		String str = toString(cfc,prop,meta, "lazy",data);
		if(!StringUtil.isEmpty(str,true)){
			str=str.trim();
			String name=x2x.getNodeName();
			Boolean b = Caster.toBoolean(str,null);
			
			// <!ATTLIST many-to-one lazy (false|proxy|no-proxy) #IMPLIED>
			// <!ATTLIST one-to-one lazy (false|proxy|no-proxy) #IMPLIED>
			if("many-to-one".equals(name) || "one-to-one".equals(name)) {
				if(b!=null) x2x.setAttribute("lazy", b.booleanValue()?"proxy":"false");
				else if("proxy".equalsIgnoreCase(str)) x2x.setAttribute("lazy", "proxy");
				else if("no-proxy".equalsIgnoreCase(str)) x2x.setAttribute("lazy", "no-proxy");
				else throw invalidValue(cfc,prop,"lazy",str,"true,false,proxy,no-proxy",data);
			}
			

			// <!ATTLIST many-to-many lazy (false|proxy) #IMPLIED>
			// <!ATTLIST key-many-to-one lazy (false|proxy) #IMPLIED>
			else if("many-to-many".equals(name) || "key-many-to-one".equals(name)) {
				if(b!=null) x2x.setAttribute("lazy", b.booleanValue()?"proxy":"false");
				else if("proxy".equalsIgnoreCase(str)) x2x.setAttribute("lazy", "proxy");
				throw invalidValue(cfc,prop,"lazy",str,"true,false,proxy",data);
				
			}
			
			else {
				if(b!=null)	x2x.setAttribute("lazy", b.booleanValue()?"true":"false");
				else if("extra".equalsIgnoreCase(str)) x2x.setAttribute("lazy", "extra");
				else  throw invalidValue(cfc,prop,"lazy",str,"true,false,extra",data);
			}
		}
	}

	private static void createXMLMappingTimestamp(Element clazz, PageContext pc,Component cfc,Property prop,SessionFactoryData data) throws PageException {
		Struct meta = prop.getDynamicAttributes();
		String str;
		Boolean b;
		

		Document doc = XMLUtil.getDocument(clazz);
		Element timestamp = doc.createElement("timestamp");
		clazz.appendChild(timestamp);
		
		timestamp.setAttribute("name",prop.getName());
		
		 // access
		str=toString(cfc,prop,meta,"access",data);
		if(!StringUtil.isEmpty(str,true))timestamp.setAttribute("access", str);
		
		// column
		str=toString(cfc,prop,meta,"column",data);
		if(StringUtil.isEmpty(str,true)) str=prop.getName();
		timestamp.setAttribute("column",formatColumn(str,data));

		// generated
		b=toBoolean(cfc,meta, "generated",data);
        if(b!=null) timestamp.setAttribute("generated", b.booleanValue()?"always":"never");
        
        // source
        str=toString(cfc,prop,meta,"source",data);
		if(!StringUtil.isEmpty(str,true)) {
			str=str.trim().toLowerCase();
			if("db".equals(str) || "vm".equals(str))
				timestamp.setAttribute("source", str);
			else 
				throw invalidValue(cfc,prop,"source",str,"db,vm",data);
		}
		
		// unsavedValue
        str=toString(cfc,prop,meta,"unsavedValue",data);
		if(!StringUtil.isEmpty(str,true)) {
			str=str.trim().toLowerCase();
			if("null".equals(str) || "undefined".equals(str))
				timestamp.setAttribute("unsaved-value", str);
			else 
				throw invalidValue(cfc,prop,"unsavedValue",str,"null, undefined",data);
				//throw new ORMException("invalid value ["+str+"] for attribute [unsavedValue], valid values are [null, undefined]");
		}
	}

	
	private static ORMException invalidValue(Component cfc,Property prop, String attrName, String invalid, String valid, SessionFactoryData data) {
		String owner = prop.getOwnerName();
		if(StringUtil.isEmpty(owner))return new HibernateORMException(data,cfc,"invalid value ["+invalid+"] for attribute ["+attrName+"] of property ["+prop.getName()+"], valid values are ["+valid+"]",null);
		return new HibernateORMException(data,cfc,"invalid value ["+invalid+"] for attribute ["+attrName+"] of property ["+prop.getName()+"] of Component ["+ListUtil.last(owner,'.')+"], valid values are ["+valid+"]",null);
	}






	private static void createXMLMappingVersion(Element clazz, PageContext pc,Component cfc,Property prop,SessionFactoryData data) throws PageException {
		Struct meta = prop.getDynamicAttributes();
		
		Document doc = XMLUtil.getDocument(clazz);
		Element version = doc.createElement("version");
		clazz.appendChild(version);
		
		
		version.setAttribute("name",prop.getName());
		
		// column
		String str = toString(cfc,prop,meta,"column",data);
		if(StringUtil.isEmpty(str,true)) str=prop.getName();
		version.setAttribute("column",formatColumn(str,data));
		
		 // access
    	str=toString(cfc,prop,meta,"access",data);
		if(!StringUtil.isEmpty(str,true))version.setAttribute("access", str);
		
		// generated
		Object o=meta.get(GENERATED,null);
		if(o!=null){
			Boolean b = Caster.toBoolean(o,null); 
			str=null;
			if(b!=null) {
				str=b.booleanValue()?"always":"never";
			}
			else {
				str=Caster.toString(o,null);
				if("always".equalsIgnoreCase(str))str="always";
				else if("never".equalsIgnoreCase(str))str="never";
				else throw invalidValue(cfc,prop,"generated",o.toString(),"true,false,always,never",data);
				//throw new ORMException("invalid value ["+o+"] for attribute [generated] of property ["+prop.getName()+"], valid values are [true,false,always,never]");
			}
			version.setAttribute( "generated", str);
		}
		
        // insert
        Boolean b = toBoolean(cfc,meta, "insert",data);
        if(b!=null && !b.booleanValue()) version.setAttribute("insert", "false");
        
        // type
        String typeName="dataType";
		str=toString(cfc,prop,meta,typeName,data);
		if(StringUtil.isEmpty(str,true)){
			typeName="ormType";
			str=toString(cfc,prop,meta,typeName,data);
		}
		if(!StringUtil.isEmpty(str,true)) {
			str=str.trim().toLowerCase();
			if("int".equals(str) || "integer".equals(str))
				version.setAttribute("type", "integer");
			else if("long".equals(str))
				version.setAttribute("type", "long");
			else if("short".equals(str))
				version.setAttribute("type", "short");
			else 
				throw invalidValue(cfc,prop,typeName,str,"int,integer,long,short",data);
			//throw new ORMException("invalid value ["+str+"] for attribute ["+typeName+"], valid values are [int,integer,long,short]");
		}
		else 
			version.setAttribute("type", "integer");
		
		// unsavedValue
        str=toString(cfc,prop,meta,"unsavedValue",data);
		if(!StringUtil.isEmpty(str,true)) {
			str=str.trim().toLowerCase();
			if("null".equals(str) || "negative".equals(str) || "undefined".equals(str))
				version.setAttribute("unsaved-value", str);
			else 
				throw invalidValue(cfc,prop,"unsavedValue",str,"null, negative, undefined",data);
			//throw new ORMException("invalid value ["+str+"] for attribute [unsavedValue], valid values are [null, negative, undefined]");
		}
	}   
	
	private static String toString(Component cfc,Property prop,Struct sct, String key, SessionFactoryData data) throws ORMException {
		return toString(cfc,prop,sct, key, false,data);
	}

	private static String toString(Component cfc,Property prop,Struct sct, String key, boolean throwErrorWhenNotExist, SessionFactoryData data) throws ORMException {
		return toString(cfc,prop,sct, KeyImpl.init(key), throwErrorWhenNotExist,data);
	}
	
	private static String toString(Component cfc,Property prop,Struct sct, Collection.Key key, boolean throwErrorWhenNotExist, SessionFactoryData data) throws ORMException {
		Object value = sct.get(key,null);
		if(value==null) {
			if(throwErrorWhenNotExist){
				if(prop==null)throw new HibernateORMException(data,cfc,"attribute ["+key+"] is required",null);
				throw new HibernateORMException(data,cfc,"attribute ["+key+"] of property ["+prop.getName()+"] of Component ["+_getCFCName(prop)+"] is required",null);
			}
			return null;
		}
		
		String str=Caster.toString(value,null);
		if(str==null) {
			if(prop==null)
				throw new HibernateORMException(data,cfc,"invalid type ["+Caster.toTypeName(value)+"] for attribute ["+key+"], value must be a string",null);
			throw new HibernateORMException(data,cfc,"invalid type ["+Caster.toTypeName(value)+"] for attribute ["+key+"] of property ["+prop.getName()+"] of Component ["+_getCFCName(prop)+"], value must be a string",null);
			}
		return str;
	}
	
	private static String _getCFCName(Property prop) {
		String owner = prop.getOwnerName();
		return ListUtil.last(owner,'.');
	}
	
	
	
	
	private static Boolean toBoolean(Component cfc,Struct sct, String key, SessionFactoryData data) throws ORMException {
		Object value = sct.get(KeyImpl.init(key),null);
		if(value==null) return null;
		
		Boolean b=Caster.toBoolean(value,null);
		if(b==null) throw new HibernateORMException(data,cfc,"invalid type ["+Caster.toTypeName(value)+"] for attribute ["+key+"], value must be a boolean",null);
		return b;
	}

	private static Integer toInteger(Component cfc,Struct sct, String key,SessionFactoryData data) throws ORMException {
		Object value = sct.get(KeyImpl.init(key),null);
		if(value==null) return null;
		
		Integer i=Caster.toInteger(value,null);
		if(i==null) throw new HibernateORMException(data,cfc,"invalid type ["+Caster.toTypeName(value)+"] for attribute ["+key+"], value must be a numeric value",null);
		return i;
	}
	
	
}
	
	class PropertyCollection {
		private Property[] properties;
		private String tableName;
		public PropertyCollection(String tableName,Property[] properties) {
			this.tableName=tableName;
			this.properties=properties;
		}
		public PropertyCollection(String tableName, java.util.List<Property> properties) {
			this.tableName=tableName;
			this.properties=properties.toArray(new Property[properties.size()]);
		}
		public Property[] getProperties() {
			return properties;
		}
		public String getTableName() {
			return tableName;
		}

	}
