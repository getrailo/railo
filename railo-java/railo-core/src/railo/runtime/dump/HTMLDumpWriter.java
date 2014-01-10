package railo.runtime.dump;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.functions.other.Dump;

public class HTMLDumpWriter implements DumpWriter {

	
	private static int count=0;

	public void writeOut(PageContext pc,DumpData data, Writer writer, boolean expand) throws IOException {
		writeOut(pc,data, writer, expand, false);
	}
	private void writeOut(PageContext pc,DumpData data, Writer writer, boolean expand, boolean inside) throws IOException {
		
		if(data==null) return;
		if(!(data instanceof DumpTable)) {
			writer.write(StringUtil.escapeHTML(data.toString()));
			return;
		}
		DumpTable table=(DumpTable) data;
		
		String id="_dump"+(count++);
		// prepare data
		DumpRow[] rows = table.getRows();
		int cols=0;
		for(int i=0;i<rows.length;i++)if(rows[i].getItems().length>cols)cols=rows[i].getItems().length;
		
		if(!inside) {
			writer.write("<script>");
			writer.write("function dumpOC(name){");
			writer.write("var tds=document.all?document.getElementsByTagName('tr'):document.getElementsByName('_'+name);");
			//writer.write("var button=document.images['__btn'+name];");
			writer.write("var s=null;");
			//writer.write("if(button.src.indexOf('plus')==-1) button.src=button.src.replace('minus','plus');");
			//writer.write("else button.src=button.src.replace('plus','minus');");
			writer.write("name='_'+name;");
			writer.write("for(var i=0;i<tds.length;i++) {");
			writer.write("if(document.all && tds[i].name!=name)continue;");
			writer.write("s=tds[i].style;");
			writer.write("if(s.display=='none') s.display='';");
			writer.write("else s.display='none';");
			writer.write("}");
			writer.write("}");
			writer.write("</script>");
			
		}
		
		String context="";
		if(!inside) context=Dump.getContext();
		if(context==null) context="";
		//boolean isSetContext=false;
		
		writer.write("<table"+(table.getWidth()!=null?" width=\""+table.getWidth()+"\"":"")+""+(table.getHeight()!=null?" height=\""+table.getHeight()+"\"":"")+" cellpadding=\"3\" cellspacing=\"1\" style=\"font-family : Verdana, Geneva, Arial, Helvetica, sans-serif;font-size : 11px;color :"+table.getFontColor()+" ;empty-cells:show;\">");
		
		// header
		if(!StringUtil.isEmpty(table.getTitle())) {
			writer.write("<tr><td title=\""+context+"\" onclick=\"dumpOC('"+id+"')\" colspan=\""+cols+"\" bgcolor=\""+table.getHighLightColor()+"\" style=\"border : 1px solid "+table.getBorderColor()+"; empty-cells:show;\">");
			//isSetContext=true;
			String contextPath="";
			pc = ThreadLocalPageContext.get(pc);
			if(pc!=null){
				contextPath=pc. getHttpServletRequest().getContextPath();
				if(contextPath==null)contextPath="";
			}
			
			
			//writer.write("<img src=\""+contextPath+"/railo-context/admin/resources/img/debug_minus.gif.cfm\" style=\"margin:2px 2px 0px 0px;\" name=\"__btn"+id+"\"/>");
			
			
			writer.write("<span style=\"font-weight:bold;\">"+
					(!StringUtil.isEmpty(table.getTitle())?table.getTitle():"")+"</span>"+(!StringUtil.isEmpty(table.getComment())?"<br>"+table.getComment():"")+
                "</td></tr>");
		}
		else id=null;
		
		// items
		DumpData value;
		for(int i=0;i<rows.length;i++) {
			if(id!=null)writer.write("<tr name=\"_"+id+"\">");
			else writer.write("<tr>");
			
			DumpData[] items=rows[i].getItems();
			int hType=rows[i].getHighlightType();
			int comperator=1;
			for(int y=0;y<cols;y++) {
				if(y<=items.length-1) value=items[y];
				else value=new SimpleDumpData("&nbsp;");
				boolean highLightIt=hType==-1 || ((hType&(comperator))>0);
				comperator*=2;
				if(value==null)value=new SimpleDumpData("null");
				//else if(value.equals(""))value="&nbsp;";
				if(!inside){
					writer.write("<td valign=\"top\" title=\""+context+"\"");
				}
				else writer.write("<td valign=\"top\"");
				writer.write(" bgcolor=\""+((highLightIt)?table.getHighLightColor():table.getNormalColor())+"\" style=\"border : 1px solid "+table.getBorderColor()+";empty-cells:show;\">");
				writeOut(pc,value, writer,expand,true);
				writer.write("</td>");
			}
			writer.write("</tr>");
		}
		
		// footer
		writer.write("</table>");
		if(!expand)writer.write("<script>dumpOC('"+id+"');</script>");
	}

	@Override
	public String toString(PageContext pc,DumpData data, boolean expand) {
		StringWriter sw=new StringWriter();
		try {
			writeOut(pc,data, sw,expand);
		} 
		catch (IOException e) {
			return "";
		}
		return sw.toString();
	}

}
