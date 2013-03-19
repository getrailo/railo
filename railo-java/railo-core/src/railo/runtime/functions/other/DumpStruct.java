/**
 * Implements the CFML Function dump
 */
package railo.runtime.functions.other;

import java.util.Set;

import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.commons.lang.types.RefBoolean;
import railo.commons.lang.types.RefBooleanImpl;
import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpRow;
import railo.runtime.dump.DumpTable;
import railo.runtime.dump.DumpUtil;
import railo.runtime.dump.SimpleDumpData;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Query;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
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
		return sct;
	}

	private static Struct toStruct(DumpData dd, Object object, RefBoolean hasReference) {
		DumpTable table;
		if(dd instanceof DumpTable) table=(DumpTable) dd;
		else {
			if(dd==null) dd= new SimpleDumpData("null");
			table=new DumpTable("#ffffff","#cccccc","#000000");
			table.appendRow(1,dd);
		}
		return toCFML(table,object,hasReference);
	}
	
	
	private static Object toCFML(DumpData dd, Object object, RefBoolean hasReference) {
		if(dd instanceof DumpTable)return toCFML((DumpTable) dd,object,hasReference);
		if(dd==null) return new SimpleDumpData("null");
		return dd.toString();
	}
	
	private static Struct toCFML(DumpTable dt, Object object, RefBoolean hasReference) {
		Struct sct=new StructImpl();
		StructUtil.setELIgnoreWhenNull(sct,"borderColor", toShortColor(dt.getBorderColor()));
		StructUtil.setELIgnoreWhenNull(sct,KeyConstants._comment, dt.getComment());
		StructUtil.setELIgnoreWhenNull(sct,"fontColor", toShortColor(dt.getFontColor()));
		StructUtil.setELIgnoreWhenNull(sct,KeyConstants._height, dt.getHeight());
		StructUtil.setELIgnoreWhenNull(sct,KeyConstants._width, dt.getWidth());
		StructUtil.setELIgnoreWhenNull(sct,"highLightColor", toShortColor(dt.getHighLightColor()));
		StructUtil.setELIgnoreWhenNull(sct,"normalColor", toShortColor(dt.getNormalColor()));
		StructUtil.setELIgnoreWhenNull(sct,KeyConstants._title, dt.getTitle());
		
		if(!StringUtil.isEmpty(dt.getType()))sct.setEL(KeyConstants._type, dt.getType());
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
				qry.setAtEL("data"+c, r+1, toCFML(items[c-1],object,hasReference));
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

	public static String getContext() {
		//Throwable cause = t.getCause();
		StackTraceElement[] traces = new Exception("Stack trace").getStackTrace();
		
		int line=0;
		String template;
		StackTraceElement trace=null;
		for(int i=0;i<traces.length;i++) {
			trace=traces[i];
			template=trace.getFileName();
			if((line=trace.getLineNumber())<=0 || template==null || ResourceUtil.getExtension(template,"").equals("java")) continue;
			return template+":"+line;
		}
		return null;
	}
		
	private static Object toShortColor(String color) {
		if(color!=null && color.length()==7 && color.startsWith("#")) {
			if(color.charAt(1)==color.charAt(2) && color.charAt(3)==color.charAt(4) && color.charAt(5)==color.charAt(6))
				return "#"+color.charAt(1)+color.charAt(3)+color.charAt(5);
			
			
		} 
		
		
		return color;
	}
}