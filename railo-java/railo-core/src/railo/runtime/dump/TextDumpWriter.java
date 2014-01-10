package railo.runtime.dump;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.engine.ThreadLocalPageContext;

public class TextDumpWriter implements DumpWriter {

	//private static int count=0;

	public void writeOut(PageContext pc,DumpData data, Writer writer, boolean expand) throws IOException {
		writeOut(pc,data, writer, expand, 0);
	}
	private void writeOut(PageContext pc,DumpData data, Writer writer, boolean expand, int level) throws IOException {
		
		if(data==null) return;
		if(!(data instanceof DumpTable)) {
			writer.write(StringUtil.escapeHTML(data.toString()));
			return;
		}
		DumpTable table=(DumpTable) data;
		
		DumpRow[] rows = table.getRows();
		int cols=0;
		for(int i=0;i<rows.length;i++)if(rows[i].getItems().length>cols)cols=rows[i].getItems().length;
		
		
		// header
		if(!StringUtil.isEmpty(table.getTitle(),true)) {
			
			String contextPath="";
			pc = ThreadLocalPageContext.get(pc);
			if(pc!=null){
				contextPath=pc. getHttpServletRequest().getContextPath();
				if(contextPath==null)contextPath="";
			}
			String header=table.getTitle()+ (StringUtil.isEmpty(table.getComment())?"":"\n"+table.getComment());
			writer.write(header+"\n");
			if(level>0)writer.write(StringUtil.repeatString("	", level));
		}
		
		// items
		DumpData value;
		for(int i=0;i<rows.length;i++) {
			DumpData[] items=rows[i].getItems();
			//int comperator=1;
			for(int y=0;y<cols;y++) {
				if(y<=items.length-1) value=items[y];
				else value=new SimpleDumpData("");
				//comperator*=2;
				if(value==null)value=new SimpleDumpData("null");
				writeOut(pc,value, writer,expand,level+1);
				writer.write(" ");
			}
			writer.write("\n");
			if(level>0)writer.write(StringUtil.repeatString("	", level));
		}
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
