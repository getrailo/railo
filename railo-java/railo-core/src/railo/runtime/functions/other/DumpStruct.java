/**
 * Implements the CFML Function dump
 */
package railo.runtime.functions.other;

import java.util.Set;

import railo.commons.digest.HashUtil;
import railo.commons.lang.StringUtil;
import railo.commons.lang.types.RefBoolean;
import railo.commons.lang.types.RefBooleanImpl;
import railo.runtime.ComponentImpl;
import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpRow;
import railo.runtime.dump.DumpTable;
import railo.runtime.dump.DumpUtil;
import railo.runtime.dump.SimpleDumpData;
import railo.runtime.ext.function.Function;
import railo.runtime.functions.string.Len;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Query;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.scope.Scope;
import railo.runtime.type.util.KeyConstants;
import railo.runtime.type.util.ListUtil;
import railo.runtime.type.util.StructUtil;

public final class DumpStruct implements Function {
	
	public static Struct call(PageContext pc , Object object) {
		return call(pc, object,9999,null,null,9999,true);
	}

	public static Struct call(PageContext pc , Object object, double maxLevel) {
		return call(pc, object,maxLevel,null,null,9999,true,true,null);
	}

	public static Struct call(PageContext pc , Object object, double maxLevel, String show) {
		return call(pc, object,maxLevel,show,null,9999,true,true,null);
	}

	public static Struct call(PageContext pc , Object object, double maxLevel, String show, String hide) {
		return call(pc,object,maxLevel,show,hide,9999,true,true,null);
	}

	public static Struct call(PageContext pc , Object object, double maxLevel, String show, String hide,double keys) {
		return call(pc , object, maxLevel, show, hide,keys,true,true,null);
	}
	public static Struct call(PageContext pc , Object object, double maxLevel, String show, String hide,double keys,boolean metainfo) {
		return call(pc , object, maxLevel, show, hide,keys,metainfo,true,null);	
	}
	
	public static Struct call(PageContext pc , Object object, double maxLevel, String show, String hide,double keys,boolean metainfo, boolean showUDFs) {
		return call(pc , object, maxLevel, show, hide,keys,metainfo,showUDFs,null);	
	}
		
	public static Struct call(PageContext pc,Object object,double maxLevel, String show, String hide,double keys,boolean metainfo, boolean showUDFs, String label) {
		if(show!=null && "all".equalsIgnoreCase(show.trim()))show=null;
		if(hide!=null && "all".equalsIgnoreCase(hide.trim()))hide=null;
		
		Set setShow=(show!=null)?ListUtil.listToSet(show.toLowerCase(),",",true):null;
		Set setHide=(hide!=null)?ListUtil.listToSet(hide.toLowerCase(),",",true):null;
		
		DumpProperties properties=new DumpProperties((int)maxLevel,setShow,setHide,(int)keys,metainfo,showUDFs);
		DumpData dd = DumpUtil.toDumpData(object, pc,(int)maxLevel,properties);
		
		if(!StringUtil.isEmpty(label)) {
			DumpTable table=new DumpTable("#ffffff","#cccccc","#000000");
			table.appendRow(1,new SimpleDumpData(label));
			table.appendRow(0,dd);
			dd=table;
		}
		RefBoolean hasReference=new RefBooleanImpl(false);
		Struct sct = toStruct(dd,object,hasReference);
		sct.setEL("hasReference", hasReference.toBoolean());

		addMetaData(sct, object);

		return sct;
	}

	private static void addMetaData(Struct sct, Object o) {

		String simpleType  = "unknown";                             // simpleType will replace colorId and colors
		String simpleValue = "";

		try {

			if (o == null) {
				simpleType  = "null";
			}
			else if (o instanceof Scope) {
				simpleType  = "struct";
				simpleValue = "Scope (" + getSize(o) + ")";
			}
			else if (Decision.isStruct(o)) {
				simpleType  = "struct";
				simpleValue = "Struct (" + getSize(o) + ")";
			}
			else if (Decision.isArray(o)) {
				simpleType  = "array";
				simpleValue = "Array (" + getSize(o) + ")";
			}
			else if (Decision.isQuery(o)) {
				simpleType  = "query";
				simpleValue = "Query (" + getSize(o) + ")";
			}
			else if (Decision.isComponent(o)) {
				simpleType  = "component";
				simpleValue = "Component: " + ((ComponentImpl)o).getDisplayName();
			}
			else if (Decision.isFunction(o) || Decision.isUserDefinedFunction(o) || Decision.isClosure(o)) {
				simpleType  = "function";
//				simpleValue = "Function: " + ((Function)o).();      // TODO: add signature
			}
			else if (Decision.isDate(o, false)) {
				simpleType  = "date";
				simpleValue = o.toString();
			}
			else if (Decision.isBoolean(o, false)) {
				simpleType  = "boolean";
				simpleValue = o.toString();
			}
			else if (Decision.isInteger(o, false)) {
				simpleType  = "numeric";
				simpleValue = Caster.toInteger(o).toString();
			}
			else if (Decision.isNumeric(o, false)) {
				simpleType  = "numeric";
				simpleValue = o.toString();
			}
			else if (Decision.isSimpleValue(o)) {
				simpleType  = "string";
				simpleValue = Caster.toString(o);
				if (simpleValue.length() > 64)
					simpleValue = "String (" + simpleValue.length() + ")";
			}
			else {
				simpleType  = o.getClass().getSimpleName().toLowerCase();
			}

		}
		catch (Throwable t) {
			simpleValue = "{error}";
		}

		sct.setEL("simpleType", simpleType);
		sct.setEL("simpleValue", simpleValue);
	}

	private static String getSize(Object o) {

		return Caster.toInteger( Len.invoke(o, 0) ).toString();
	}

	private static Struct toStruct(DumpData dd, Object object, RefBoolean hasReference) {
		DumpTable table;
		if(dd instanceof DumpTable) table=(DumpTable) dd;
		else {
			if(dd==null) dd= new SimpleDumpData("null");
			table=new DumpTable("#ffffff","#cccccc","#000000");
			table.appendRow(1,dd);
		}
		return toCFML(table,object,hasReference,null);
	}
	
	
	private static Object toCFML(DumpData dd, Object object, RefBoolean hasReference, Struct colors) {
		if(dd instanceof DumpTable)return toCFML((DumpTable) dd,object,hasReference,colors);
		if(dd==null) return new SimpleDumpData("null");
		return dd.toString();
	}
	
	private static Struct toCFML(DumpTable dt, Object object, RefBoolean hasReference, Struct colors) {
		
		Struct sct=new StructImpl();
		if(colors==null) {
			colors=new StructImpl();
			sct.setEL("colors", colors);
		}
		Collection.Key type;
		if(dt.getType()!=null) type=KeyImpl.init(dt.getType());
		else if(object!=null) type=KeyImpl.init(object.getClass().getName());
		else type=KeyConstants._null;
		
		// colors
		String borderColor = toShortColor(dt.getBorderColor());
		String fontColor = toShortColor(dt.getFontColor());
		String highLightColor = toShortColor(dt.getHighLightColor());
		String normalColor = toShortColor(dt.getNormalColor());
		// create color id
		Key colorId = KeyImpl.init(Long.toString(HashUtil.create64BitHash(new StringBuilder(borderColor)
		.append(':').append(fontColor)
		.append(':').append(highLightColor)
		.append(':').append(normalColor)),Character.MAX_RADIX));
		
		
		if(!colors.containsKey(colorId)) {
			Struct color=new StructImpl();
			StructUtil.setELIgnoreWhenNull(color,"borderColor", borderColor);
			StructUtil.setELIgnoreWhenNull(color,"fontColor", fontColor);
			StructUtil.setELIgnoreWhenNull(color,"highLightColor", highLightColor);
			StructUtil.setELIgnoreWhenNull(color,"normalColor", normalColor);
			colors.setEL(colorId, color);
		}
		

		/*StructUtil.setELIgnoreWhenNull(sct,"borderColor", borderColor);
		StructUtil.setELIgnoreWhenNull(sct,"fontColor", fontColor);
		StructUtil.setELIgnoreWhenNull(sct,"highLightColor", highLightColor);
		StructUtil.setELIgnoreWhenNull(sct,"normalColor", normalColor);
		*/
		StructUtil.setELIgnoreWhenNull(sct,"colorId", colorId.getString());
		StructUtil.setELIgnoreWhenNull(sct,KeyConstants._comment, dt.getComment());
		StructUtil.setELIgnoreWhenNull(sct,KeyConstants._height, dt.getHeight());
		StructUtil.setELIgnoreWhenNull(sct,KeyConstants._width, dt.getWidth());
		StructUtil.setELIgnoreWhenNull(sct,KeyConstants._title, dt.getTitle());
		
		sct.setEL(KeyConstants._type, type.getString());
		if(!StringUtil.isEmpty(dt.getId()))sct.setEL(KeyConstants._id, dt.getId());
			
		if("ref".equals(dt.getType())){
			hasReference.setValue(true);
			sct.setEL(KeyConstants._ref, dt.getRef());
		}
		
		
		DumpRow[] drs = dt.getRows();
		DumpRow dr;
		Query qry=null;
		DumpData[] items;
		for(int r=0;r<drs.length;r++){
			dr=drs[r];
			items = dr.getItems();
			if(qry==null)qry=new QueryImpl(toColumns(items),drs.length,"data");
			for(int c=1;c<=items.length;c++){
				qry.setAtEL("data"+c, r+1, toCFML(items[c-1],object,hasReference,colors));
			}
			qry.setAtEL("highlight", r+1, new Double(dr.getHighlightType()));
			
		}
		if(qry!=null)sct.setEL(KeyConstants._data, qry);
		return sct;
	}

	private static String[] toColumns(DumpData[] items) {
		String[] columns=new String[items.length+1];
		columns[0]="highlight";
		for(int i=1;i<columns.length;i++){
			columns[i]="data"+i;
		}
		return columns;
	}
		
	private static String toShortColor(String color) {
		if(color!=null && color.length()==7 && color.startsWith("#")) {
			if(color.charAt(1)==color.charAt(2) && color.charAt(3)==color.charAt(4) && color.charAt(5)==color.charAt(6))
				return "#"+color.charAt(1)+color.charAt(3)+color.charAt(5);
			
			
		} 
		
		
		return color;
	}
}