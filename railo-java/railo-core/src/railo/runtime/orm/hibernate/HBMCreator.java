package railo.runtime.orm.hibernate;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import railo.commons.lang.StringUtil;
import railo.runtime.Component;
import railo.runtime.PageContext;
import railo.runtime.component.Property;
import railo.runtime.db.DatasourceConnection;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.orm.ORMConfiguration;
import railo.runtime.orm.ORMEngine;
import railo.runtime.orm.ORMException;
import railo.runtime.text.xml.XMLUtil;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.List;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.dt.DateTimeImpl;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.util.ComponentUtil;

public class HBMCreator {
	
	
	private static final Collection.Key PROPERTY = KeyImpl.getInstance("property");
	private static final Collection.Key FIELD_TYPE = KeyImpl.getInstance("fieldType");
	private static final Collection.Key LINK_TABLE = KeyImpl.getInstance("linktable");
	private static final Collection.Key CFC = KeyImpl.getInstance("cfc");
	private static final Collection.Key TYPE = KeyImpl.getInstance("type");
	

	public static void createXMLMapping(PageContext pc,DatasourceConnection dc, Component cfc,ORMConfiguration ormConf,Element hibernateMapping,HibernateORMEngine engine) throws PageException {
		
		// MUST Support for embeded objects 
		Component cfci = ComponentUtil.toComponent(cfc);
		Struct meta = cfci.getMetaData(pc);
		
		String extend = cfc.getExtends();
		boolean isClass=StringUtil.isEmpty(extend);
		
		Property[] _props=getProperties(pc,engine,cfci,dc,ormConf,meta,isClass);
		
		
		
		Map<String, PropertyCollection> joins=new HashMap<String, PropertyCollection>();
		PropertyCollection propColl = splitJoins(engine,cfc,joins, _props);
		
		
		
		// create class element and attach
		Document doc = XMLUtil.getDocument(hibernateMapping);
		
		StringBuilder comment=new StringBuilder();
		comment.append("\nsource:").append(cfci.getPageSource().getDisplayPath());
		comment.append("\ncompilation-time:").append(new DateTimeImpl(ComponentUtil.getCompileTime(pc,cfci.getPageSource()),false)).append("\n");
		
		hibernateMapping.appendChild(doc.createComment(comment.toString()));
		
		//print.e(cfc.getAbsName()+";"+isClass+" -> "+cfci.getBaseAbsName()+":"+cfci.isBasePeristent());
		if(!isClass && !cfci.isBasePeristent()) {
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
			String ext = List.last(extend,'.').trim();
			try {
				Component base = engine.getEntityByCFCName(ext, false);
				ext = HibernateCaster.getEntityName(base);
			}
			catch(Throwable t){}
			
			
			String discriminatorValue = toString(engine,cfc,null,meta,"discriminatorValue");
			if(!StringUtil.isEmpty(discriminatorValue,true)) {
				doTable=false;
				clazz = doc.createElement("subclass");
				hibernateMapping.appendChild(clazz);
		        //addClassAttributes(classNode);
		        clazz.setAttribute("extends", ext);
		        clazz.setAttribute("discriminator-value", discriminatorValue);
		        
		        String joincolumn = toString(engine,cfc,null,meta,"joincolumn",false);
		        if(!StringUtil.isEmpty(joincolumn)){
		        	join = doc.createElement("join");
			        clazz.appendChild(join);
			        doTable=true;
			        Element key = doc.createElement("key");
			        join.appendChild(key);
			        key.setAttribute("column", formatColumn(engine,joincolumn));
		        }
		        
			}
			else {
				clazz = doc.createElement("joined-subclass");
				hibernateMapping.appendChild(clazz);
				clazz.setAttribute("extends",ext);
				Element key = doc.createElement("key");
			    clazz.appendChild(key);
		        key.setAttribute("column", formatColumn(engine,toString(engine,cfc,null,meta,"joincolumn",true)));
			}

		}
		
		//createXMLMappingTuplizer(clazz,pc);

		addGeneralClassAttributes(pc,ormConf,engine,cfc,meta,clazz);
		String tableName=getTableName(engine,pc,meta,cfc);
		
		if(join!=null) clazz=join;
		if(doTable)addGeneralTableAttributes(pc,ormConf,engine,cfc,meta,clazz);
		
		
        
        
        Struct columnsInfo=null;
        if(ormConf.useDBForMapping()){
        	columnsInfo = engine.getTableInfo(dc,getTableName(engine,pc, meta, cfci),engine);
        }

        if(isClass)setCacheStrategy(engine,cfc,null,doc, meta, clazz);
        
		// id
        if(isClass) addId(cfc,doc,clazz,pc,meta,propColl,columnsInfo,tableName,engine);
	      
        // discriminator
        if(isClass) addDiscriminator(engine,cfc,doc,clazz,pc,meta);
        
		// version
        if(isClass)addVersion(cfc,clazz,pc, propColl,columnsInfo,tableName,engine);
		
		// property
		addProperty(cfc,clazz,pc, propColl,columnsInfo,tableName,engine);
		
		// relations
		addRelation(cfc,clazz,pc, propColl,columnsInfo,tableName,engine,ormConf,dc);

		// collection
		addCollection(cfc,clazz,pc, propColl,columnsInfo,tableName,engine,ormConf);
		
		// join
		addJoin(cfc,clazz,pc, joins,columnsInfo,tableName,engine,ormConf,dc);

		
	}
	
	private static Property[] getProperties(PageContext pc, HibernateORMEngine engine, Component cfci, DatasourceConnection dc, ORMConfiguration ormConf, Struct meta, boolean isClass) throws ORMException, PageException {
		Property[] _props = cfci.getProperties(true);
		if(isClass && _props.length==0 && ormConf.useDBForMapping()){
			if(meta==null)meta = cfci.getMetaData(pc);
        	_props=HibernateUtil.createPropertiesFromTable(dc,getTableName(engine,pc, meta, cfci));
        }
		return _props;
	}












	private static void addId(Component cfc,Document doc, Element clazz, PageContext pc, Struct meta, PropertyCollection propColl, Struct columnsInfo, String tableName, HibernateORMEngine engine) throws PageException {
		Property[] _ids = getIds(engine,cfc,propColl);
		
        //Property[] _ids = ids.toArray(new Property[ids.size()]);
        
        if(_ids.length==1) 
        	createXMLMappingId(cfc,clazz,pc, _ids[0],columnsInfo,tableName,engine);
        else if(_ids.length>1) 
        	createXMLMappingCompositeId(cfc,clazz,pc, _ids,columnsInfo,tableName,engine);
        else 
        	throw new HibernateException(engine,cfc,"missing id property for entity ["+HibernateCaster.getEntityName(cfc)+"]");
	}
	

	public static PropertyCollection splitJoins(HibernateORMEngine engine,Component cfc,Map<String, PropertyCollection> joins,Property[] props) {
		Struct sct=new StructImpl();
		ArrayList<Property> others = new ArrayList<Property>();
		java.util.List<Property> list;
		String table;
		Property prop;
		String fieldType;
		boolean isJoin;
		for(int i=0;i<props.length;i++){
			prop=props[i];
			table=getTable(engine,cfc,prop);
			// joins
			if(!StringUtil.isEmpty(table,true)){
				isJoin=true;
				// wrong field type
				try {
					fieldType = toString(engine, cfc, prop, sct, FIELD_TYPE,false);
					
					if("collection".equalsIgnoreCase(fieldType)) isJoin=false;
					else if("primary".equals(fieldType)) isJoin=false;
					else if("version".equals(fieldType)) isJoin=false;
					else if("timestamp".equals(fieldType)) isJoin=false;
				} 
				catch (ORMException e) {}
				
				// missing column
				String columns=null;
				try {
					if(isRelated(props[i])){
			        	columns=toString(engine,cfc,props[i], prop.getMeta(), "fkcolumn");
			        }
			        else {
			        	columns=toString(engine,cfc,props[i], prop.getMeta(), "joincolumn");
			        }
				}
				catch(ORMException e){}
				if(StringUtil.isEmpty(columns)) isJoin=false;
				
				if(isJoin){
					table=table.trim();
					list = (java.util.List<Property>) sct.get(table,null);
					if(list==null){
						list=new ArrayList<Property>();
						sct.setEL(table, list);
					}
					list.add(prop);
					continue;
				}
			}
			others.add(prop);
		}
		
		// fill to joins
		Key[] keys = sct.keys();
		Key key;
		for(int i=0;i<keys.length;i++){
			key=keys[i];
			list=(java.util.List<Property>) sct.get(key,null);
			joins.put(key.getString(), new PropertyCollection(key.getString(),list));
		}
		
		
		
		return new PropertyCollection(null,others);
	}
	
	
	
	

	public static Property[] getIds(HibernateORMEngine engine,Component cfc,PropertyCollection pc) {
		return getIds(engine,cfc,pc.getProperties(), pc.getTableName(),false);
	}
	
	public static Property[] getIds(HibernateORMEngine engine,Component cfc,Property[] props,String tableName, boolean ignoreTableName) {
		ArrayList<Property> ids=new ArrayList<Property>();
        for(int y=0;y<props.length;y++){
        	if(!ignoreTableName && !hasTable(engine,cfc,props[y], tableName)) continue;
        	
        	
        	String fieldType = Caster.toString(props[y].getMeta().get(FIELD_TYPE,null),null);
			if("id".equalsIgnoreCase(fieldType) || List.listFindNoCaseIgnoreEmpty(fieldType,"id",',')!=-1)
				ids.add(props[y]);
		}
        
        // no id field defined
        if(ids.size()==0) {
        	String fieldType;
        	for(int y=0;y<props.length;y++){
        		if(!ignoreTableName && !hasTable(engine,cfc,props[y], tableName)) continue;
            	fieldType = Caster.toString(props[y].getMeta().get(FIELD_TYPE,null),null);
    			if(StringUtil.isEmpty(fieldType,true) && props[y].getName().equalsIgnoreCase("id")){
    				ids.add(props[y]);
    				props[y].getMeta().setEL(FIELD_TYPE, "id");
    			}
    		}
        } 
        
        // still no id field defined
        if(ids.size()==0 && props.length>0) {
        	String owner = props[0].getOwnerName();
			if(!StringUtil.isEmpty(owner)) owner=List.last(owner, '.').trim();
        	
        	String fieldType;
        	if(!StringUtil.isEmpty(owner)){
        		String id=owner+"id";
        		for(int y=0;y<props.length;y++){
        			if(!ignoreTableName && !hasTable(engine,cfc,props[y], tableName)) continue;
                	fieldType = Caster.toString(props[y].getMeta().get(FIELD_TYPE,null),null);
	    			if(StringUtil.isEmpty(fieldType,true) && props[y].getName().equalsIgnoreCase(id)){
	    				ids.add(props[y]);
	    				props[y].getMeta().setEL(FIELD_TYPE, "id");
	    			}
	    		}
        	}
        } 
        return ids.toArray(new Property[ids.size()]);
	}






	private static void addVersion(Component cfc,Element clazz, PageContext pc,PropertyCollection propColl, Struct columnsInfo, String tableName,HibernateORMEngine engine) throws PageException {
    	Property[] props = propColl.getProperties();
		for(int y=0;y<props.length;y++){
			String fieldType = Caster.toString(props[y].getMeta().get(FIELD_TYPE,null),null);
			if("version".equalsIgnoreCase(fieldType))
				createXMLMappingVersion(engine,clazz,pc, cfc,props[y]);
			else if("timestamp".equalsIgnoreCase(fieldType))
				createXMLMappingTimestamp(engine,clazz,pc,cfc, props[y]);
		}
	}



	private static void addCollection(Component cfc,Element clazz, PageContext pc,PropertyCollection propColl, Struct columnsInfo, String tableName,HibernateORMEngine engine, ORMConfiguration ormConf) throws PageException {
		Property[] props = propColl.getProperties();
		for(int y=0;y<props.length;y++){
			String fieldType = Caster.toString(props[y].getMeta().get(FIELD_TYPE,"column"),"column");
			if("collection".equalsIgnoreCase(fieldType))
				createXMLMappingCollection(clazz,pc, cfc,props[y],ormConf,engine);
		}
	}
	
	
	private static void addJoin(Component cfc,Element clazz, PageContext pc,Map<String, PropertyCollection> joins, Struct columnsInfo, String tableName,HibernateORMEngine engine, ORMConfiguration ormConf, DatasourceConnection dc) throws PageException {
        
		Iterator<Entry<String, PropertyCollection>> it = joins.entrySet().iterator();
		Entry<String, PropertyCollection> entry;
		while(it.hasNext()){
			entry = it.next();
			PropertyCollection coll = entry.getValue();
			addJoin(cfc,engine,pc,columnsInfo,ormConf,clazz,entry.getValue(),dc);
		}
		
		
		
    }
	
	private static void addJoin(Component cfc,HibernateORMEngine engine,PageContext pc,Struct columnsInfo,  ORMConfiguration ormConf, Element clazz, PropertyCollection coll, DatasourceConnection dc) throws PageException {
		String table = coll.getTableName();
		Property[] properties = coll.getProperties();
		if(properties.length==0) return;
		
		Document doc = XMLUtil.getDocument(clazz);
		
		Element join = doc.createElement("join");
        clazz.appendChild(join);
		
        join.setAttribute("table", escape(engine.convertTableName(coll.getTableName())));
        //addTableInfo(joinNode, table, schema, catalog);
        
        Property first = properties[0];
        String schema = null, catalog=null, mappedBy=null, columns=null;
        if(isRelated(first)){
        	catalog=toString(engine,cfc,first, first.getMeta(), "linkcatalog");
        	schema=toString(engine,cfc,first, first.getMeta(), "linkschema");
        	columns=toString(engine,cfc,first, first.getMeta(), "fkcolumn");
        	
        }
        else {
        	catalog=toString(engine,cfc,first, first.getMeta(), "catalog");
        	schema=toString(engine,cfc,first, first.getMeta(), "schema");
        	mappedBy=toString(engine,cfc,first, first.getMeta(), "mappedby");
        	columns=toString(engine,cfc,first, first.getMeta(), "joincolumn");
        }

        if(!StringUtil.isEmpty(catalog)) join.setAttribute("catalog", catalog);
        if(!StringUtil.isEmpty(schema)) join.setAttribute("schema", schema);
        
        Element key = doc.createElement("key");
        join.appendChild(key);
        if(!StringUtil.isEmpty(mappedBy)) key.setAttribute("property-ref", mappedBy);
        setColumn(engine,doc, key, columns);
        
        addProperty(cfc,join,pc, coll,columnsInfo,table,engine);
		int count=addRelation(cfc,join,pc, coll,columnsInfo,table,engine, ormConf,dc);
        
		if(count>0) join.setAttribute("inverse", "true");
			
		
	}







	



	private static int addRelation(Component cfc,Element clazz, PageContext pc,PropertyCollection propColl, Struct columnsInfo, String tableName,HibernateORMEngine engine,  ORMConfiguration ormConf, DatasourceConnection dc) throws PageException {
    	Property[] props = propColl.getProperties();
		int count=0;
    	for(int y=0;y<props.length;y++){
			String fieldType = Caster.toString(props[y].getMeta().get(FIELD_TYPE,"column"),"column");
			if("one-to-one".equalsIgnoreCase(fieldType)){
				createXMLMappingOneToOne(clazz,pc, cfc,props[y],engine);
				count++;
			}
			else if("many-to-one".equalsIgnoreCase(fieldType)){
				createXMLMappingManyToOne(clazz,pc, cfc,props[y],engine);
				count++;
			}
			else if("one-to-many".equalsIgnoreCase(fieldType)){
				createXMLMappingOneToMany(cfc,engine,propColl,ormConf,clazz,pc, props[y]);
				count++;
			}
			else if("many-to-many".equalsIgnoreCase(fieldType)){
				createXMLMappingManyToMany(cfc,engine,propColl,clazz,pc, props[y],ormConf,dc);
				count++;
			}
		}
    	return count;
	}
	
	public static boolean isRelated(Property prop) {
		String fieldType = Caster.toString(prop.getMeta().get(FIELD_TYPE,"column"),"column");
		if(StringUtil.isEmpty(fieldType,true)) return false;
		fieldType=fieldType.toLowerCase().trim();
		
		if("one-to-one".equals(fieldType)) 		return true;
		if("many-to-one".equals(fieldType)) 	return true;
		if("one-to-many".equals(fieldType)) 	return true;
		if("many-to-many".equals(fieldType)) 	return true;
		return false;
	}



	private static void addProperty(Component cfc,Element clazz, PageContext pc, PropertyCollection propColl, Struct columnsInfo, String tableName, HibernateORMEngine engine) throws ORMException {
		Property[] props = propColl.getProperties();
		for(int y=0;y<props.length;y++){
			String fieldType = Caster.toString(props[y].getMeta().get(FIELD_TYPE,"column"),"column");
			if("column".equalsIgnoreCase(fieldType))
				createXMLMappingProperty(clazz,pc,cfc, props[y],columnsInfo,tableName,engine);
		}
	}



	private static void addDiscriminator(HibernateORMEngine engine,Component cfc,Document doc,Element clazz, PageContext pc,Struct meta) throws ORMException {
		
    	 String str = toString(engine,cfc,null,meta,"discriminatorColumn");
    	 if(!StringUtil.isEmpty(str,true)){
    		 Element disc = doc.createElement("discriminator");
    		 clazz.appendChild(disc);
    		 disc.setAttribute("column",formatColumn(engine,str));
    	 }
    	 

    	
    	
	}



	private static void addGeneralClassAttributes(PageContext pc, ORMConfiguration ormConf, HibernateORMEngine engine, Component cfc, Struct meta, Element clazz) throws PageException {
    	
    	// name
		clazz.setAttribute("node", HibernateCaster.toComponentName(cfc));
		
    	// entity-name
    	String str=toString(engine,cfc,null,meta,"entityname");
		if(StringUtil.isEmpty(str,true)) str=HibernateCaster.getEntityName(cfc);
		clazz.setAttribute("entity-name",str);
		

        // batch-size
        Integer i = toInteger(engine,cfc,meta,"batchsize");
        if(i!=null && i.intValue()>0)clazz.setAttribute("batch-size",Caster.toString(i));
		
		// dynamic-insert
        Boolean b = toBoolean(engine,cfc,meta,"dynamicinsert");
        if(b!=null && b.booleanValue())clazz.setAttribute("dynamic-insert","true");
        
        // dynamic-update
        b=toBoolean(engine,cfc,meta,"dynamicupdate");
        if(b!=null && b.booleanValue())clazz.setAttribute("dynamic-update","true");
        
		// lazy
        b=toBoolean(engine,cfc,meta,"lazy");
        if(b==null) b=Boolean.TRUE;
        clazz.setAttribute("lazy",Caster.toString(b.booleanValue()));
        
        // select-before-update
        b=toBoolean(engine,cfc,meta,"selectbeforeupdate");
        if(b!=null && b.booleanValue())clazz.setAttribute("select-before-update","true");

        // optimistic-lock
        str=toString(engine,cfc,null,meta,"optimisticLock");
        if(!StringUtil.isEmpty(str,true)) {
        	str=str.trim().toLowerCase();
        	if("all".equals(str) || "dirty".equals(str) || "none".equals(str) || "version".equals(str))
        		clazz.setAttribute("optimistic-lock",str);
        	else
        		throw new HibernateException(engine,cfc,"invalid value ["+str+"] for attribute [optimisticlock] of tag [component], valid values are [all,dirty,none,version]");
        }
        
        // read-only
        b=toBoolean(engine,cfc,meta,"readOnly");
        if(b!=null && b.booleanValue()) clazz.setAttribute("mutable", "false");
        
        // rowid
        str=toString(engine,cfc,null,meta,"rowid");
        if(!StringUtil.isEmpty(str,true)) clazz.setAttribute("rowid",str);
        
        // where
        str=toString(engine,cfc,null,meta,"where");
        if(!StringUtil.isEmpty(str,true)) clazz.setAttribute("where", str);

       
	}
	private static void addGeneralTableAttributes(PageContext pc, ORMConfiguration ormConf, HibernateORMEngine engine, Component cfc, Struct meta, Element clazz) throws PageException {
		 // table
        clazz.setAttribute("table",escape(getTableName(engine,pc,meta,cfc)));
        
        // catalog
        String str = toString(engine,cfc,null,meta,"catalog");
        if(str==null)// empty string is allowed as input
        	str=ormConf.getCatalog();
        if(!StringUtil.isEmpty(str,true)) clazz.setAttribute("catalog", str);
        
        // schema
        str=toString(engine,cfc,null,meta,"schema");
        if(str==null)// empty string is allowed as input
        	str=ormConf.getSchema();
        if(!StringUtil.isEmpty(str,true)) clazz.setAttribute( "schema", str);
        
	}
	private static String escape(String str) {
		if(HibernateUtil.isKeyword(str)) return "`"+str+"`";
		return str;
	}






	private static String getTableName(HibernateORMEngine engine,PageContext pc, Struct meta, Component cfc) throws ORMException {
		String tableName=toString(engine,cfc,null,meta,"table");
		if(StringUtil.isEmpty(tableName,true)) 
			tableName=HibernateCaster.getEntityName(cfc);
		return engine.convertTableName(tableName);
	}
	
	private static String getTable(HibernateORMEngine engine,Component cfc,Property prop) {
		try {
			return engine.convertTableName(toString(engine,cfc,prop, prop.getMeta(), "table"));
		} catch (ORMException e) {
			return null;
		}
	}
	
	private static boolean hasTable(HibernateORMEngine engine,Component cfc,Property prop,String tableName) {
		String t = getTable(engine,cfc,prop);
		boolean left=StringUtil.isEmpty(t,true);
		boolean right=StringUtil.isEmpty(tableName,true);
		if(left && right) return true;
		if(left || right) return false;
		return tableName.trim().equalsIgnoreCase(t.trim());
	}






	private static void createXMLMappingCompositeId(Component cfc,Element clazz, PageContext pc,Property[] props,Struct columnsInfo,String tableName,HibernateORMEngine engine) throws PageException {
		Struct meta;
		
		Document doc = XMLUtil.getDocument(clazz);
		Element cid = doc.createElement("composite-id");
		clazz.appendChild(cid);
		
		//cid.setAttribute("mapped","true");
		
		
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
			
			String str = toString(engine,cfc,prop,meta,"column");
	    	if(StringUtil.isEmpty(str,true)) str=prop.getName();
	    	column.setAttribute("name",formatColumn(engine,str));
	    	ColumnInfo info=getColumnInfo(columnsInfo,tableName,str,engine,null);
	    	
            str = toString(engine,cfc,prop,meta,"sqltype");
	    	if(!StringUtil.isEmpty(str,true)) column.setAttribute("sql-type",str);
	    	str = toString(engine,cfc,prop,meta,"length");
	    	if(!StringUtil.isEmpty(str,true)) column.setAttribute("length",str);
            
	    	/*if(info!=null){
	    		column.setAttribute("sql-type",info.getTypeName());
	    		column.setAttribute("length",Caster.toString(info.getSize()));
	    	}*/
			
	    	 // type
			//str=getType(info,prop,meta,"long"); //MUSTMUST
			//key.setAttribute("type", str);
			
			String generator=toString(engine,cfc,prop,meta,"generator");
			String type = getType(engine,info,cfc,prop,meta,getDefaultTypeForGenerator(generator,"string"));
			if(!StringUtil.isEmpty(type))key.setAttribute("type", type);
			
			
			
            
		}
		
		// many-to-one
		String fieldType;
		for(int y=0;y<props.length;y++){
			prop=props[y];
			meta = prop.getMeta();
			fieldType = toString(engine,cfc,prop,meta,"fieldType");
			if(List.listFindNoCaseIgnoreEmpty(fieldType,"many-to-one",',')==-1)continue;
			
			Element key = doc.createElement("key-many-to-one");
			cid.appendChild(key);
			
			// name
			key.setAttribute("name",prop.getName());
			
			// entity-name
			setForeignEntityName(pc, engine,cfc,prop, meta, key,false);
			
			// fkcolum
			String str=toString(engine,cfc,prop,meta,"fkcolumn");
			setColumn(engine,doc, key, str);
			
			// lazy
			setLazy(engine,cfc,prop,meta,key);
		}
	}
	
	
	private static void createXMLMappingId(Component cfc,Element clazz, PageContext pc,Property prop,Struct columnsInfo,String tableName,HibernateORMEngine engine) throws PageException {
		Struct meta = prop.getMeta();
		String str;
		Integer i;
		
		Document doc = XMLUtil.getDocument(clazz);
		Element id = doc.createElement("id");
		clazz.appendChild(id);
			
        // access
    	str=toString(engine,cfc,prop,meta,"access");
		if(!StringUtil.isEmpty(str,true))id.setAttribute("access", str);
    	
		// name
		id.setAttribute("name",prop.getName());
		
		// column
		Element column = doc.createElement("column");
		id.appendChild(column);

		str=toString(engine,cfc,prop,meta,"column");
    	if(StringUtil.isEmpty(str,true)) str=prop.getName();
    	column.setAttribute("name",formatColumn(engine,str));
    	ColumnInfo info=getColumnInfo(columnsInfo,tableName,str,engine,null);
    	StringBuilder foreignCFC=new StringBuilder();
		String generator=createXMLMappingGenerator(engine,id,pc,cfc,prop,foreignCFC);

		str = toString(engine,cfc,prop,meta,"length");
    	if(!StringUtil.isEmpty(str,true)) column.setAttribute("length",str);
        
		// type    
		String type = getType(engine,info,cfc,prop,meta,getDefaultTypeForGenerator(engine,generator,foreignCFC));
		//print.o(prop.getName()+":"+type+"::"+getDefaultTypeForGenerator(engine,generator,foreignCFC));
		if(!StringUtil.isEmpty(type))id.setAttribute("type", type);
		
		// unsaved-value
		str=toString(engine,cfc,prop,meta,"unsavedValue");  
		if(!StringUtil.isEmpty(str,true))id.setAttribute("unsaved-value", str);
		
	}
	
	private static String getDefaultTypeForGenerator(HibernateORMEngine engine, String generator,StringBuilder foreignCFC) {
		String value = getDefaultTypeForGenerator(generator, null);
		if(value!=null) return value;
		
		if("foreign".equalsIgnoreCase(generator)) {
			if(!StringUtil.isEmpty(foreignCFC)) {
				try {
					Component cfc = engine.getEntityByCFCName(foreignCFC.toString(), false);
					if(cfc!=null){
						Component cfcp = ComponentUtil.toComponent(cfc);
						Property[] ids = getIds(engine,cfc,cfcp.getProperties(true),null,true);
						if(!ArrayUtil.isEmpty(ids)){
							Property id = ids[0];
							id.getMeta();
							Struct meta = id.getMeta();
							if(meta!=null){
								String type=Caster.toString(meta.get(TYPE,null));
								
								if(!StringUtil.isEmpty(type) && (!type.equalsIgnoreCase("any") && !type.equalsIgnoreCase("object"))){
									return type;
								}
								else {
									String g=Caster.toString(meta.get("generator",null));
									if(!StringUtil.isEmpty(g)){
										return getDefaultTypeForGenerator(engine,g,foreignCFC);
									}
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
	
	private static String getType(HibernateORMEngine engine,ColumnInfo info, Component cfc,Property prop,Struct meta,String defaultValue) throws ORMException {
		// ormType
		String type = toString(engine,cfc,prop,meta,"ormType");
		//type=HibernateCaster.toHibernateType(info,type,null);
		
		// dataType
		if(StringUtil.isEmpty(type,true)){
			type=toString(engine,cfc,prop,meta,"dataType");
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









	private static ColumnInfo getColumnInfo(Struct columnsInfo,String tableName,String columnName,ORMEngine engine,ColumnInfo defaultValue) throws ORMException {
		if(columnsInfo!=null) {
	    	ColumnInfo info = (ColumnInfo) columnsInfo.get(KeyImpl.init(columnName),null);
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
					msg+=", column names are ["+List.arrayToList(columnsInfo.keysAsString(), ", ")+"]";
				ORMUtil.printError(msg, engine);
				
				//throw new ORMException(msg);
			}
			return info;
    	}
		return null;
	}*/

	private static String createXMLMappingGenerator(HibernateORMEngine engine,Element id, PageContext pc,Component cfc,Property prop,StringBuilder foreignCFC) throws PageException {
		Struct meta = prop.getMeta();
		
		// generator
		String className=toString(engine,cfc,prop,meta,"generator");
		if(StringUtil.isEmpty(className,true)) return null;
		

		Document doc = XMLUtil.getDocument(id);
		Element generator = doc.createElement("generator");
		id.appendChild(generator);
		
		generator.setAttribute("class", className);
		
		//print.e("generator:"+className);
		
		// params
		Object obj=meta.get("params",null);
		//if(obj!=null){
			Struct sct=null;
			if(obj==null) obj=new StructImpl();
			else if(obj instanceof String) obj=convertToSimpleMap((String)obj);
			
			if(Decision.isStruct(obj)) sct=Caster.toStruct(obj);
			else throw new HibernateException(engine,cfc,"invalid value for attribute [params] of tag [property]");
			className=className.trim().toLowerCase();
			
			// special classes
			if("foreign".equals(className)){
				if(!sct.containsKey(PROPERTY)) sct.setEL(PROPERTY, toString(engine,cfc,prop,meta, PROPERTY,true));
				
				if(sct.containsKey(PROPERTY)){
					String p = Caster.toString(sct.get(PROPERTY),null);
					if(!StringUtil.isEmpty(p))foreignCFC.append(p);
				}
				
				
			}
			else if("select".equals(className)){
				//print.e("select:"+toString(meta, "selectKey",true));
				if(!sct.containsKey("key")) sct.setEL("key", toString(engine,cfc,prop,meta, "selectKey",true));
			}
			else if("sequence".equals(className)){
				if(!sct.containsKey("sequence")) sct.setEL("sequence", toString(engine,cfc,prop,meta, "sequence",true));
			}
			
			Key[] keys = sct.keys();
			Element param;
			for(int y=0;y<keys.length;y++){
				
				param = doc.createElement("param");
				generator.appendChild(param);
				
				param.setAttribute( "name", keys[y].getLowerString());
				param.appendChild(doc.createTextNode(Caster.toString(sct.get(keys[y]))));
				
			}
		//}
		return className;
	}
	
	
	
	

	

	private static void createXMLMappingProperty(Element clazz, PageContext pc,Component cfc,Property prop,Struct columnsInfo,String tableName,HibernateORMEngine engine) throws ORMException {
		Struct meta = prop.getMeta();
		
        
		
		// get table name
		String columnName=toString(engine,cfc,prop,meta,"column");
    	if(StringUtil.isEmpty(columnName,true)) columnName=prop.getName();
    	
    	ColumnInfo info=getColumnInfo(columnsInfo,tableName,columnName,engine,null);
		
		Document doc = XMLUtil.getDocument(clazz);
		Element property = doc.createElement("property");
		clazz.appendChild(property);
		
		//name
		property.setAttribute("name",prop.getName());
		
		// type
		String str = getType(engine,info, cfc,prop, meta, "string");
		property.setAttribute("type",str);
		
		
		
		// formula or column
		str=toString(engine,cfc,prop,meta,"formula");
        Boolean b;
		if(!StringUtil.isEmpty(str,true))	{
        	property.setAttribute("formula","("+str+")");
        }
        else {
        	//property.setAttribute("column",columnName);
        	
        	Element column = doc.createElement("column");
        	property.appendChild(column);
        	column.setAttribute("name", escape(engine.convertColumnName(columnName)));

            // check
            str=toString(engine,cfc,prop,meta,"check");
            if(!StringUtil.isEmpty(str,true)) column.setAttribute("check",str);
            
	        // default
	        str=toString(engine,cfc,prop,meta,"dbDefault");
	        if(!StringUtil.isEmpty(str,true)) column.setAttribute("default",str);
            
            // index
            str=toString(engine,cfc,prop,meta,"index");
            if(!StringUtil.isEmpty(str,true)) column.setAttribute("index",str);
        	
        	// length
            Integer i = toInteger(engine,cfc,meta,"length");
            if(i!=null && i>0) column.setAttribute("length",Caster.toString(i.intValue()));
            
            // not-null
            b=toBoolean(engine,cfc,meta,"notnull");
            if(b!=null && b.booleanValue())column.setAttribute("not-null","true");
            
            // precision
            i=toInteger(engine,cfc,meta,"precision");
            if(i!=null && i>-1) column.setAttribute("precision",Caster.toString(i.intValue()));
            
            // scale
            i=toInteger(engine,cfc,meta,"scale");
            if(i!=null && i>-1) column.setAttribute("scale",Caster.toString(i.intValue()));
            
            // sql-type
            str=toString(engine,cfc,prop,meta,"sqltype");
            if(!StringUtil.isEmpty(str,true)) column.setAttribute("sql-type",str);
            
        // unique
            b=toBoolean(engine,cfc,meta,"unique");
	        if(b!=null && b.booleanValue())column.setAttribute("unique","true");
	        
	        // unique-key
	        str=toString(engine,cfc,prop,meta,"uniqueKey");
	        if(StringUtil.isEmpty(str))str=Caster.toString(meta.get("uniqueKeyName",null),null);
	        if(!StringUtil.isEmpty(str,true)) column.setAttribute("unique-key",str);
	        
	        
        }
        
        // generated
        str=toString(engine,cfc,prop,meta,"generated");
        if(!StringUtil.isEmpty(str,true)){
        	str=str.trim().toLowerCase();
        	
        	if("always".equals(str) || "insert".equals(str) || "never".equals(str))
        		property.setAttribute("generated",str);
        	else
        		throw invalidValue(engine,cfc,prop,"generated",str,"always,insert,never");
				//throw new ORMException(engine,"invalid value ["+str+"] for attribute [generated] of column ["+columnName+"], valid values are [always,insert,never]");
        }
        
        
        // update
        b=toBoolean(engine,cfc,meta,"update");
        if(b!=null && !b.booleanValue())property.setAttribute("update","false");
        
        // insert
        b=toBoolean(engine,cfc,meta,"insert");
        if(b!=null && !b.booleanValue())property.setAttribute("insert","false");
        
        // lazy
        b=toBoolean(engine,cfc,meta,"lazy");
        if(b!=null && b.booleanValue())property.setAttribute("lazy","true");
        
        // optimistic-lock
        b=toBoolean(engine,cfc,meta,"optimisticlock");
        if(b!=null && !b.booleanValue())property.setAttribute("optimistic-lock","false");
        
	}
	
	
	/*
	MUST dies kommt aber nicht hier sondern in verarbeitung in component
	<cfproperty 
    persistent="true|false" 
   >
	 * */
	private static void createXMLMappingOneToOne(Element clazz, PageContext pc,Component cfc,Property prop,HibernateORMEngine engine) throws PageException {
		Struct meta = prop.getMeta();
		
		Boolean b;
		
		Document doc = XMLUtil.getDocument(clazz);
		Element x2o;
		
		// column
		String fkcolumn=toString(engine,cfc,prop,meta,"fkcolumn");
		String linkTable=toString(engine,cfc,prop,meta,"linkTable");
		
		
		if(!StringUtil.isEmpty(linkTable,true) || !StringUtil.isEmpty(fkcolumn,true)) {
			clazz=getJoin(clazz);
			
			x2o= doc.createElement("many-to-one");
			//x2o.setAttribute("column", fkcolumn);
			x2o.setAttribute("unique", "true");
			
			if(!StringUtil.isEmpty(linkTable,true)){
				setColumn(engine,doc, x2o, linkTable);
			}
			else {
				setColumn(engine,doc, x2o, fkcolumn);
			}

			
			
			
			// update
	        b=toBoolean(engine,cfc,meta,"update");
	        if(b!=null)x2o.setAttribute("update",Caster.toString(b.booleanValue()));
	        
	        // insert
	        b=toBoolean(engine,cfc,meta,"insert");
	        if(b!=null)x2o.setAttribute("insert",Caster.toString(b.booleanValue()));
	        
	        // not-null
	        b=toBoolean(engine,cfc,meta,"notNull");
	        if(b!=null)x2o.setAttribute("not-null",Caster.toString(b.booleanValue()));
	        
	        
	        // optimistic-lock
	        b=toBoolean(engine,cfc,meta,"optimisticLock");
	        if(b!=null)x2o.setAttribute("optimistic-lock",Caster.toString(b.booleanValue()));
	        
	        // not-found
			b=toBoolean(engine,cfc,meta, "missingRowIgnored");
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
		String str=toString(engine,cfc,prop,meta,"access");
		if(!StringUtil.isEmpty(str,true)) x2o.setAttribute("access", str);
			
		// constrained
		b=toBoolean(engine,cfc,meta, "constrained");
        if(b!=null && b.booleanValue()) x2o.setAttribute("constrained", "true");
		
		// formula
		str=toString(engine,cfc,prop,meta,"formula");
		if(!StringUtil.isEmpty(str,true)) x2o.setAttribute("formula", str);
		
		// embed-xml
		str=toString(engine,cfc,prop,meta,"embedXml");
		if(!StringUtil.isEmpty(str,true)) x2o.setAttribute("embed-xml", str);
		
		// property-ref
		str=toString(engine,cfc,prop,meta,"mappedBy");
		if(!StringUtil.isEmpty(str,true)) x2o.setAttribute("property-ref", str);
		
		// foreign-key
		str=toString(engine,cfc,prop,meta,"foreignKeyName");
		if(StringUtil.isEmpty(str,true)) str=toString(engine,cfc,prop,meta,"foreignKey");
		if(!StringUtil.isEmpty(str,true)) x2o.setAttribute("foreign-key", str);
        
		setForeignEntityName(pc,engine,cfc,prop,meta,x2o,true);
		
		createXMLMappingXToX(engine,x2o, pc,cfc,prop,meta);
	}
	
	
	
	
	
	private static void setForeignEntityName(PageContext pc,HibernateORMEngine engine,Component cfc,Property prop, Struct meta, Element el, boolean cfcRequired) throws PageException {
		// entity
		String str=cfcRequired?null:toString(engine,cfc,prop,meta,"entityName");
		if(!StringUtil.isEmpty(str,true)) {
			el.setAttribute("entity-name", str);
		}
		else {
			// cfc
			//createFKColumnName(engine, cfc, prop, propColl);
			
			str = toString(engine,cfc,prop,meta,"cfc",cfcRequired);
			if(!StringUtil.isEmpty(str,true)){
				Component _cfc=engine.getEntityByCFCName(str, false);
				str=HibernateCaster.getEntityName(_cfc);
				el.setAttribute("entity-name", str);
			}
		}
	}
	private static Component loadForeignCFC(PageContext pc,HibernateORMEngine engine,Component cfc,Property prop, Struct meta) throws PageException {
		// entity
		String str=toString(engine,cfc,prop,meta,"entityName");
		Component fcfc=null;
		
		if(!StringUtil.isEmpty(str,true)) {
			fcfc = engine.getEntityByEntityName(str, false);
			if(fcfc!=null) return fcfc;
		}
			
		str = toString(engine,cfc,prop,meta,"cfc",false);
		if(!StringUtil.isEmpty(str,true)){
			return engine.getEntityByCFCName(str, false);
		}
		return null;
	}









	private static void createXMLMappingCollection(Element clazz, PageContext pc,Component cfc,Property prop,ORMConfiguration ormConf, HibernateORMEngine engine) throws PageException {
		Struct meta = prop.getMeta();
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
			str=toString(engine,cfc,prop,meta,"structKeyColumn",true);
			if(!StringUtil.isEmpty(str,true)) {
				Element mapKey=doc.createElement("map-key");
				el.appendChild(mapKey);
				mapKey.setAttribute("column", str);
				
				// type
				str=toString(engine,cfc,prop,meta,"structKeyType");
				if(!StringUtil.isEmpty(str,true))mapKey.setAttribute("type", str);
				else mapKey.setAttribute("type", "string");
			}
		}
		else throw invalidValue(engine,cfc,prop,"collectiontype",str,"array,struct");
		//throw new ORMException(engine,"invalid value ["+str+"] for attribute [collectiontype], valid values are [array,struct]");
		
		setBeforeJoin(clazz,el);
		
		// name 
		el.setAttribute("name", prop.getName());
		
		// table
		str=toString(engine,cfc,prop,meta, "table",true);
		el.setAttribute("table",escape(engine.convertTableName(str)));
		
		// catalog
		str=toString(engine,cfc,prop,meta, "catalog");
		if(!StringUtil.isEmpty(str,true))el.setAttribute("catalog",str);
		
		// schema
		str=toString(engine,cfc,prop,meta, "schema");
		if(!StringUtil.isEmpty(str,true))el.setAttribute("schema",str);
		
		// mutable
		Boolean b=toBoolean(engine,cfc,meta, "readonly");
        if(b!=null && b.booleanValue()) el.setAttribute("mutable", "false");
		
		// order-by
		str=toString(engine,cfc,prop,meta, "orderby");
		if(!StringUtil.isEmpty(str,true))el.setAttribute("order-by",str);
		
		// element-column
		str=toString(engine,cfc,prop,meta,"elementcolumn");
		if(!StringUtil.isEmpty(str,true)){
			Element element = doc.createElement("element");
			el.appendChild(element);
			
			// column
			element.setAttribute("column", formatColumn(engine,str));
			
			// type
			str=toString(engine,cfc,prop,meta,"elementtype");
			if(!StringUtil.isEmpty(str,true)) element.setAttribute("type", str);
		}
		
        // batch-size
        Integer i=toInteger(engine,cfc,meta, "batchsize");
        if(i!=null && i.intValue()>1) el.setAttribute("batch-size", Caster.toString(i.intValue()));
 
		// column
		str=toString(engine,cfc,prop,meta,"fkcolumn");
		if(StringUtil.isEmpty(str,true)) str=toString(engine,cfc,prop,meta,"column");
		if(!StringUtil.isEmpty(str,true)){
			Element key = doc.createElement("key");
			XMLUtil.setFirst(el,key);
			//el.appendChild(key);
			
			// column
			key.setAttribute("column", formatColumn(engine,str));
			
			// property-ref
			str=toString(engine,cfc,prop,meta,"mappedBy");
			if(!StringUtil.isEmpty(str,true)) key.setAttribute("property-ref", str);
		}
		
		// cache
		setCacheStrategy(engine,cfc,prop,doc, meta, el);
		
		// optimistic-lock
		b=toBoolean(engine,cfc,meta, "optimisticlock");
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












	private static void createXMLMappingManyToMany(Component cfc,HibernateORMEngine engine,PropertyCollection propColl,Element clazz, PageContext pc,Property prop,ORMConfiguration ormConf, DatasourceConnection dc) throws PageException {
		Element el = createXMLMappingXToMany(engine,propColl,clazz, pc, cfc,prop);
		Struct meta = prop.getMeta();
		Document doc = XMLUtil.getDocument(clazz);
		Element m2m = doc.createElement("many-to-many");
		el.appendChild(m2m);
		
		// link
		setLink(engine,cfc,prop,el,meta,ormConf,true);
		
		setForeignEntityName(pc, engine,cfc,prop, meta, m2m,true);

        // order-by
		String str = toString(engine,cfc,prop,meta,"orderby");
		if(!StringUtil.isEmpty(str,true))m2m.setAttribute("order-by", str);
		
		// column
		str=toString(engine,cfc,prop,meta,"inversejoincolumn");
		
		// build fkcolumn name
		if(StringUtil.isEmpty(str,true)) {
			Component other = (Component) loadForeignCFC(pc, engine, cfc, prop, meta);
			if(other!=null){
				boolean isClass=StringUtil.isEmpty(other.getExtends());
				Property[] _props=getProperties(pc,engine,other,dc,ormConf,meta,isClass);
				PropertyCollection _propColl = splitJoins(engine,cfc,new HashMap<String, PropertyCollection>(), _props);
				_props=_propColl.getProperties();
				
				Struct m;
				Property _prop=null;
				for(int i=0;i<_props.length;i++){
					m = _props[i].getMeta();
					// fieldtype
					String fieldtype = Caster.toString(m.get(FIELD_TYPE,null),null);
					if("many-to-many".equalsIgnoreCase(fieldtype)) {
						// linktable
						String currLinkTable=Caster.toString(meta.get(LINK_TABLE,null),null);
						String othLinkTable=Caster.toString(m.get(LINK_TABLE,null),null);
						if(currLinkTable.equals(othLinkTable)) {
							// cfc name
							Component cfcp=ComponentUtil.toComponent(cfc);
							String cfcName=Caster.toString(m.get(CFC,null),null);
							if(cfcp.equalTo(cfcName)){
								_prop=_props[i];
							}
						}
					}
				}
				str=createM2MFKColumnName(engine, other, _prop, _propColl);
			}
		}
		setColumn(engine,doc, m2m, str);
		
		// not-found
		Boolean b=toBoolean(engine,cfc,meta, "missingrowignored");
        if(b!=null && b.booleanValue()) m2m.setAttribute("not-found", "ignore");
        
        // property-ref
		str=toString(engine,cfc,prop,meta,"mappedby");
		if(!StringUtil.isEmpty(str,true)) m2m.setAttribute("property-ref", str);
		
		// foreign-key
		str=toString(engine,cfc,prop,meta,"foreignKeyName");
		if(StringUtil.isEmpty(str,true)) str=toString(engine,cfc,prop,meta,"foreignKey");
		if(!StringUtil.isEmpty(str,true)) m2m.setAttribute("foreign-key", str);
	}
	
	private static boolean setLink(HibernateORMEngine engine,Component cfc,Property prop,Element el, Struct meta, ORMConfiguration ormConf, boolean linkTableRequired) throws ORMException {
		String str=toString(engine,cfc,prop,meta, "linktable",linkTableRequired);
		
		
		if(!StringUtil.isEmpty(str,true)){

			el.setAttribute("table", escape(engine.convertTableName(str)));
		
			// schema
			str=toString(engine,cfc,prop,meta, "linkschema");
			if(StringUtil.isEmpty(str,true)) str=ormConf.getSchema();
			if(!StringUtil.isEmpty(str,true)) el.setAttribute("schema", str);
			
			// catalog
			str=toString(engine,cfc,prop,meta, "linkcatalog");
			if(StringUtil.isEmpty(str,true)) str=ormConf.getCatalog();
			if(!StringUtil.isEmpty(str,true)) el.setAttribute("catalog", str);
			return true;
		}
		return false;
	}









	private static void createXMLMappingOneToMany(Component cfc,HibernateORMEngine engine,PropertyCollection propColl,ORMConfiguration ormConf,Element clazz, PageContext pc,Property prop) throws PageException {
		Element el = createXMLMappingXToMany(engine,propColl,clazz, pc, cfc,prop);
		Struct meta = prop.getMeta();
		Document doc = XMLUtil.getDocument(clazz);
		Element x2m;
		
		
        // order-by
		String str = toString(engine,cfc,prop,meta,"orderby");
		if(!StringUtil.isEmpty(str,true))el.setAttribute("order-by", str);
		
		// link
		if(setLink(engine,cfc,prop,el,meta,ormConf,false)){
			x2m = doc.createElement("many-to-many");
			x2m.setAttribute("unique","true");
			
			str=toString(engine,cfc,prop,meta,"inversejoincolumn");
			setColumn(engine,doc, x2m, str);
		}
		else {
			x2m = doc.createElement("one-to-many");
		}
		el.appendChild(x2m);
		

		// entity-name
		
		setForeignEntityName(pc,engine,cfc,prop,meta,x2m,true);
		
	}

	

	
	
	
	
	
	private static Element createXMLMappingXToMany(HibernateORMEngine engine,PropertyCollection propColl,Element clazz, PageContext pc,Component cfc,Property prop) throws PageException {
		final Struct meta = prop.getMeta();
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
			str=toString(engine,cfc,prop,meta,"structKeyColumn",true);
			mapKey.setAttribute("column", formatColumn(engine,str));
			
			// type
			str=toString(engine,cfc,prop,meta,"structKeyType");
			if(!StringUtil.isEmpty(str,true))mapKey.setAttribute("type", str);
			else mapKey.setAttribute("type", "string");// MUST get type dynamicly
		}
		else throw invalidValue(engine,cfc,prop,"collectiontype",str,"array,struct");
		//throw new ORMException(engine,"invalid value ["+str+"] for attribute [collectiontype], valid values are [array,struct]");
		
		setBeforeJoin(clazz,el);
		

		
		// batch-size
        Integer i=toInteger(engine,cfc,meta, "batchsize");
        if(i!=null){
        	if(i.intValue()>1) el.setAttribute("batch-size", Caster.toString(i.intValue()));
        }
 
		// cacheUse
        setCacheStrategy(engine,cfc,prop,doc, meta, el);
        
        // column
        str=createM2MFKColumnName(engine,cfc,prop,propColl);
		
		if(!StringUtil.isEmpty(str,true)){
			Element key = doc.createElement("key");
			el.appendChild(key);
			
			// column
			setColumn(engine,doc,key,str);
			
			// property-ref
			str=toString(engine,cfc,prop,meta,"mappedBy");
			if(!StringUtil.isEmpty(str,true)) key.setAttribute("property-ref", str);
		}
		
        // inverse
		Boolean b = toBoolean(engine,cfc,meta, "inverse");
        if(b!=null && b.booleanValue()) el.setAttribute("inverse", "true");
        
		
		
		// mutable 
		b = toBoolean(engine,cfc,meta, "readonly");
        if(b!=null && b.booleanValue()) el.setAttribute("mutable", "false");
		
		// optimistic-lock
		b=toBoolean(engine,cfc,meta, "optimisticlock");
        if(b!=null && !b.booleanValue()) el.setAttribute("optimistic-lock", "false");
        
		// where
		str=toString(engine,cfc,prop,meta,"where");
		if(!StringUtil.isEmpty(str,true)) el.setAttribute("where", str);
        
		// add map key
        if(mapKey!=null)el.appendChild(mapKey);
        
        
		createXMLMappingXToX(engine,el, pc,cfc,prop,meta);
		
		return el;
	}
	
	private static String createM2MFKColumnName(HibernateORMEngine engine, Component cfc, Property prop, PropertyCollection propColl) throws ORMException {
		
		String str=prop==null?null:toString(engine,cfc,prop,prop.getMeta(),"fkcolumn");
		if(StringUtil.isEmpty(str)){
			Property[] ids = getIds(engine,cfc,propColl);
			if(ids.length==1) {
				str=toString(engine,cfc,ids[0],ids[0].getMeta(),"column");
		    	if(StringUtil.isEmpty(str,true)) str=ids[0].getName();
			}
			else if(prop!=null)str=toString(engine,cfc,prop,prop.getMeta(),"fkcolumn",true);
			else
				throw new ORMException(engine, "cannot terminate forgein key column name");
			
			str=HibernateCaster.getEntityName(cfc)+"_"+str;
		}
    	return str;
	}











	private static void setCacheStrategy(HibernateORMEngine engine,Component cfc,Property prop,Document doc,Struct meta, Element el) throws ORMException {
		String strategy = toString(engine,cfc,prop,meta,"cacheuse");
		
		if(!StringUtil.isEmpty(strategy,true)){
			strategy=strategy.trim().toLowerCase();
			if("read-only".equals(strategy) || "nonstrict-read-write".equals(strategy) || "read-write".equals(strategy) || "transactional".equals(strategy)){
				Element cache = doc.createElement("cache");
				XMLUtil.setFirst(el, cache);
				el.appendChild(cache);
				cache.setAttribute("usage", strategy);
				String name = toString(engine,cfc,prop,meta,"cacheName");
				if(!StringUtil.isEmpty(name,true)){
					cache.setAttribute("region", name);
				}
			}	
			else
				throw new HibernateException(engine,cfc,"invalid value ["+strategy+"] for attribute [cacheuse], valid values are [read-only,nonstrict-read-write,read-write,transactional]");
		}
		
	}

	





	private static void setColumn(HibernateORMEngine engine,Document doc, Element el, String columnValue) throws PageException {
		if(StringUtil.isEmpty(columnValue,true)) return;
		
		String[] arr = List.toStringArray(List.listToArray(columnValue, ','));
		if(arr.length==1){
			el.setAttribute("column", formatColumn(engine,arr[0]));
		}
		else {
			Element column;
			for(int i=0;i<arr.length;i++){
				column=doc.createElement("column");
				el.appendChild(column);
				column.setAttribute("name", formatColumn(engine,arr[i]));
			}
		}
	}









	private static void createXMLMappingManyToOne(Element clazz, PageContext pc,Component cfc,Property prop, HibernateORMEngine engine) throws PageException {
		Struct meta = prop.getMeta();
		Boolean b;
		
		Document doc = XMLUtil.getDocument(clazz);
		clazz=getJoin(clazz);
		
		Element m2o = doc.createElement("many-to-one");
		clazz.appendChild(m2o);
		
		// columns
		String linktable = toString(engine,cfc,prop,meta,"linktable");
		String _columns;
		if(!StringUtil.isEmpty(linktable,true)) _columns=toString(engine,cfc,prop,meta,"inversejoincolumn");
		else _columns=toString(engine,cfc,prop,meta,"fkcolumn");
		setColumn(engine,doc, m2o, _columns);
		
		// cfc
		setForeignEntityName(pc,engine,cfc,prop,meta,m2o,true);
		
		// column
		//String str=toString(prop,meta,"column",true);
		//m2o.setAttribute("column", str);
		
		// insert
		b=toBoolean(engine,cfc,meta, "insert");
        if(b!=null && !b.booleanValue()) m2o.setAttribute("insert", "false");
		
        // update
		b=toBoolean(engine,cfc,meta, "update");
        if(b!=null && !b.booleanValue()) m2o.setAttribute("update", "false");
		
        // property-ref
		String str=toString(engine,cfc,prop,meta,"mappedBy");
		if(!StringUtil.isEmpty(str,true)) m2o.setAttribute("property-ref", str);

        // update
		b=toBoolean(engine,cfc,meta, "unique");
        if(b!=null && b.booleanValue()) m2o.setAttribute("unique", "true");

        // not-null
		b=toBoolean(engine,cfc,meta, "notnull");
        if(b!=null && b.booleanValue()) m2o.setAttribute("not-null", "true");
		
        // optimistic-lock
		b=toBoolean(engine,cfc,meta, "optimisticLock");
        if(b!=null && !b.booleanValue()) m2o.setAttribute("optimistic-lock", "false");
        
        // not-found
		b=toBoolean(engine,cfc,meta, "missingRowIgnored");
        if(b!=null && b.booleanValue()) m2o.setAttribute("not-found", "ignore");
        
        // index
		str=toString(engine,cfc,prop,meta,"index");
		if(!StringUtil.isEmpty(str,true)) m2o.setAttribute("index", str);
        
        // unique-key
		str=toString(engine,cfc,prop,meta,"uniqueKeyName");
		if(StringUtil.isEmpty(str,true))str=toString(engine,cfc,prop,meta,"uniqueKey");
		if(!StringUtil.isEmpty(str,true)) m2o.setAttribute("unique-key", str);
		
		// foreign-key
		str=toString(engine,cfc,prop,meta,"foreignKeyName");
		if(StringUtil.isEmpty(str,true)) str=toString(engine,cfc,prop,meta,"foreignKey");
		if(!StringUtil.isEmpty(str,true)) m2o.setAttribute("foreign-key", str);

		// access
		str=toString(engine,cfc,prop,meta,"access");
		if(!StringUtil.isEmpty(str,true)) m2o.setAttribute("access", str);
		
        createXMLMappingXToX(engine,m2o, pc,cfc,prop,meta);
        
	}
	
	
	
	
	
	
	
	
	private static String formatColumn(HibernateORMEngine engine,String name) {
        name=name.trim();
		return escape(engine.convertColumnName(name));
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
	private static void createXMLMappingXToX(HibernateORMEngine engine,Element x2x, PageContext pc, Component cfc,Property prop, Struct meta) throws ORMException {
		x2x.setAttribute("name",prop.getName());
		
		// cascade
		String str=toString(engine,cfc,prop,meta,"cascade");
		if(!StringUtil.isEmpty(str,true)) x2x.setAttribute("cascade", str);
		
		// fetch
		str=toString(engine,cfc,prop,meta,"fetch");
		if(!StringUtil.isEmpty(str,true)) {
			str=str.trim().toLowerCase();
			if("join".equals(str) || "select".equals(str))
				x2x.setAttribute("fetch", str);
			else
				throw invalidValue(engine,cfc,prop,"fetch",str,"join,select");
			//throw new ORMException(engine,"invalid value ["+str+"] for attribute [fetch], valid values are [join,select]");
		}
		
		// lazy
		setLazy(engine,cfc,prop,meta,x2x);
		
	}



	

	private static void setLazy(HibernateORMEngine engine, Component cfc,Property prop, Struct meta, Element x2x) throws ORMException {
		String str = toString(engine,cfc,prop,meta, "lazy");
		if(!StringUtil.isEmpty(str)){
			Boolean b = Caster.toBoolean(str,null);
			if(b!=null)
				x2x.setAttribute("lazy", b.booleanValue()?"true":"false");
			else if("extra".equalsIgnoreCase(str))
				x2x.setAttribute("lazy", "extra");
			else 
				throw invalidValue(engine,cfc,prop,"lazy",str,"true,false,extra");
		}
	}

	private static void createXMLMappingTimestamp(HibernateORMEngine engine,Element clazz, PageContext pc,Component cfc,Property prop) throws PageException {
		Struct meta = prop.getMeta();
		String str;
		Integer i;
		Boolean b;
		

		Document doc = XMLUtil.getDocument(clazz);
		Element timestamp = doc.createElement("timestamp");
		clazz.appendChild(timestamp);
		
		timestamp.setAttribute("name",prop.getName());
		
		 // access
		str=toString(engine,cfc,prop,meta,"access");
		if(!StringUtil.isEmpty(str,true))timestamp.setAttribute("access", str);
		
		// column
		str=toString(engine,cfc,prop,meta,"column");
		if(StringUtil.isEmpty(str,true)) str=prop.getName();
		timestamp.setAttribute("column",formatColumn(engine,str));

		// generated
		b=toBoolean(engine,cfc,meta, "generated");
        if(b!=null) timestamp.setAttribute("generated", b.booleanValue()?"always":"never");
        
        // source
        str=toString(engine,cfc,prop,meta,"source");
		if(!StringUtil.isEmpty(str,true)) {
			str=str.trim().toLowerCase();
			if("db".equals(str) || "vm".equals(str))
				timestamp.setAttribute("source", str);
			else 
				throw invalidValue(engine,cfc,prop,"source",str,"db,vm");
		}
		
		// unsavedValue
        str=toString(engine,cfc,prop,meta,"unsavedValue");
		if(!StringUtil.isEmpty(str,true)) {
			str=str.trim().toLowerCase();
			if("null".equals(str) || "undefined".equals(str))
				timestamp.setAttribute("unsaved-value", str);
			else 
				throw invalidValue(engine,cfc,prop,"unsavedValue",str,"null, undefined");
				//throw new ORMException(engine,"invalid value ["+str+"] for attribute [unsavedValue], valid values are [null, undefined]");
		}
	}

	
	private static ORMException invalidValue(HibernateORMEngine engine,Component cfc,Property prop, String attrName, String invalid, String valid) {
		String owner = prop.getOwnerName();
		if(StringUtil.isEmpty(owner))return new HibernateException(engine,cfc,"invalid value ["+invalid+"] for attribute ["+attrName+"] of property ["+prop.getName()+"], valid values are ["+valid+"]");
		return new HibernateException(engine,cfc,"invalid value ["+invalid+"] for attribute ["+attrName+"] of property ["+prop.getName()+"] of Component ["+List.last(owner,'.')+"], valid values are ["+valid+"]");
	}






	private static void createXMLMappingVersion(HibernateORMEngine engine,Element clazz, PageContext pc,Component cfc,Property prop) throws PageException {
		Struct meta = prop.getMeta();
		
		Document doc = XMLUtil.getDocument(clazz);
		Element version = doc.createElement("version");
		clazz.appendChild(version);
		
		
		version.setAttribute("name",prop.getName());
		
		// column
		String str = toString(engine,cfc,prop,meta,"column");
		if(StringUtil.isEmpty(str,true)) str=prop.getName();
		version.setAttribute("column",formatColumn(engine,str));
		
		 // access
    	str=toString(engine,cfc,prop,meta,"access");
		if(!StringUtil.isEmpty(str,true))version.setAttribute("access", str);
		
		// generated
		Object o=meta.get("generated",null);
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
				else throw invalidValue(engine,cfc,prop,"generated",o.toString(),"true,false,always,never");
				//throw new ORMException(engine,"invalid value ["+o+"] for attribute [generated] of property ["+prop.getName()+"], valid values are [true,false,always,never]");
			}
			version.setAttribute( "generated", str);
		}
		
        // insert
        Boolean b = toBoolean(engine,cfc,meta, "insert");
        if(b!=null && !b.booleanValue()) version.setAttribute("insert", "false");
        
        // type
        String typeName="dataType";
		str=toString(engine,cfc,prop,meta,typeName);
		if(StringUtil.isEmpty(str,true)){
			typeName="ormType";
			str=toString(engine,cfc,prop,meta,typeName);
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
				throw invalidValue(engine,cfc,prop,typeName,str,"int,integer,long,short");
			//throw new ORMException(engine,"invalid value ["+str+"] for attribute ["+typeName+"], valid values are [int,integer,long,short]");
		}
		else 
			version.setAttribute("type", "integer");
		
		// unsavedValue
        str=toString(engine,cfc,prop,meta,"unsavedValue");
		if(!StringUtil.isEmpty(str,true)) {
			str=str.trim().toLowerCase();
			if("null".equals(str) || "negative".equals(str) || "undefined".equals(str))
				version.setAttribute("unsaved-value", str);
			else 
				throw invalidValue(engine,cfc,prop,"unsavedValue",str,"null, negative, undefined");
			//throw new ORMException(engine,"invalid value ["+str+"] for attribute [unsavedValue], valid values are [null, negative, undefined]");
		}
	}   
	
	
	public static Struct convertToSimpleMap(String paramsStr) {
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
            		KeyImpl.init(deleteQuotes(pair.substring(0, index).trim()).trim()), 
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
	
	
	private static String toString(HibernateORMEngine engine,Component cfc,Property prop,Struct sct, String key) throws ORMException {
		return toString(engine,cfc,prop,sct, key, false);
	}

	private static String toString(HibernateORMEngine engine,Component cfc,Property prop,Struct sct, String key, boolean throwErrorWhenNotExist) throws ORMException {
		return toString(engine,cfc,prop,sct, KeyImpl.init(key), throwErrorWhenNotExist);
	}
	
	private static String toString(HibernateORMEngine engine,Component cfc,Property prop,Struct sct, Collection.Key key, boolean throwErrorWhenNotExist) throws ORMException {
		Object value = sct.get(key,null);
		if(value==null) {
			if(throwErrorWhenNotExist){
				if(prop==null)throw new HibernateException(engine,cfc,"attribute ["+key+"] is required");
				throw new HibernateException(engine,cfc,"attribute ["+key+"] of property ["+prop.getName()+"] of Component ["+_getCFCName(prop)+"] is required");
			}
			return null;
		}
		
		String str=Caster.toString(value,null);
		if(str==null) {
			if(prop==null)
				throw new HibernateException(engine,cfc,"invalid type ["+Caster.toTypeName(value)+"] for attribute ["+key+"], value must be a string");
			throw new HibernateException(engine,cfc,"invalid type ["+Caster.toTypeName(value)+"] for attribute ["+key+"] of property ["+prop.getName()+"] of Component ["+_getCFCName(prop)+"], value must be a string");
			}
		return str;
	}
	
	private static String _getCFCName(Property prop) {
		String owner = prop.getOwnerName();
		return List.last(owner,'.');
	}
	
	
	
	
	private static Boolean toBoolean(HibernateORMEngine engine,Component cfc,Struct sct, String key) throws ORMException {
		Object value = sct.get(key,null);
		if(value==null) return null;
		
		Boolean b=Caster.toBoolean(value,null);
		if(b==null) throw new HibernateException(engine,cfc,"invalid type ["+Caster.toTypeName(value)+"] for attribute ["+key+"], value must be a boolean");
		return b;
	}

	private static Integer toInteger(HibernateORMEngine engine,Component cfc,Struct sct, String key) throws ORMException {
		Object value = sct.get(key,null);
		if(value==null) return null;
		
		Integer i=Caster.toInteger(value,null);
		if(i==null) throw new HibernateException(engine,cfc,"invalid type ["+Caster.toTypeName(value)+"] for attribute ["+key+"], value must be a numeric value");
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
