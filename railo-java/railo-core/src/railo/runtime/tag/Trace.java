package railo.runtime.tag;

import java.io.IOException;

import railo.commons.io.log.Log;
import railo.commons.io.log.LogAndSource;
import railo.commons.io.log.LogUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageSource;
import railo.runtime.converter.ConverterException;
import railo.runtime.converter.ScriptConverter;
import railo.runtime.debug.DebugTrace;
import railo.runtime.exp.Abort;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.BodyTagImpl;
import railo.runtime.functions.other.Dump;
import railo.runtime.op.Caster;
import railo.runtime.type.dt.DateTimeImpl;

public final class Trace extends BodyTagImpl {
	
	private boolean abort=false;
	private String category;
	private boolean inline=false;
	private String text;
	private int type=Log.LEVEL_INFO;
	private String var;
	
	
	/**
	 *
	 * @see railo.runtime.ext.tag.TagImpl#release()
	 */
	public void release() {
		super.release();
		abort=false;
		category=null;
		inline=false;
		text=null;
		type=Log.LEVEL_INFO;
		var=null;
	}

	/**
	 * @param abort the abort to set
	 */
	public void setAbort(boolean abort) {
		this.abort = abort;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * @param inline the inline to set
	 */
	public void setInline(boolean inline) {
		this.inline = inline;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @param type the type to set
	 * @throws ApplicationException 
	 */
	public void setType(String strType) throws ApplicationException {
		strType = strType.toLowerCase().trim();
		if("info".equals(strType)) 						type=Log.LEVEL_INFO;
		if("information".equals(strType))				type=Log.LEVEL_INFO;
		else if("warn".equals(strType)) 				type=Log.LEVEL_WARN;
		else if("warning".equals(strType)) 				type=Log.LEVEL_WARN;
		else if("error".equals(strType)) 				type=Log.LEVEL_ERROR;
		else if("fatal information".equals(strType))	type=Log.LEVEL_FATAL;
		else if("fatal-information".equals(strType)) 	type=Log.LEVEL_FATAL;
		else if("fatal_information".equals(strType)) 	type=Log.LEVEL_FATAL;
		else if("fatalinformation".equals(strType)) 	type=Log.LEVEL_FATAL;
		else if("fatal info".equals(strType)) 			type=Log.LEVEL_FATAL;
		else if("fatal-info".equals(strType)) 			type=Log.LEVEL_FATAL;
		else if("fatal_info".equals(strType)) 			type=Log.LEVEL_FATAL;
		else if("fatalinfo".equals(strType)) 			type=Log.LEVEL_FATAL;
		else if("fatal".equals(strType)) 				type=Log.LEVEL_FATAL;
		else if("debug".equals(strType)) 				type=Log.LEVEL_DEBUG;
		else if("debugging".equals(strType)) 			type=Log.LEVEL_DEBUG;
		else if("debuging".equals(strType)) 			type=Log.LEVEL_DEBUG;
		else throw new ApplicationException("invalid value ["+strType+"] for attribute [type], valid values are [Debug, Information, Warning, Error, Fatal Information]");
	}

	/**
	 * @param var the var to set
	 */
	public void setVar(String var) {
		this.var = var;
	}

	/**
	 * @param var the var to set
	 */
	public void setVariable(String var) {
		this.var = var;
	}

	/**
	 *
	 * @see railo.runtime.ext.tag.TagImpl#doStartTag()
	 */
	public int doStartTag()	{
		return EVAL_BODY_INCLUDE;
	}
	
	
	/**
	 *
	 * @throws PageException 
	 * @throws IOException 
	 * @see railo.runtime.ext.tag.TagImpl#doEndTag()
	 */
	public int doEndTag() throws PageException {
		try {
			_doEndTag();
		}
		catch (IOException e) {}
		return EVAL_PAGE;
	}
	
	public void _doEndTag() throws IOException, PageException {
		
		PageSource page = pageContext.getCurrentTemplatePageSource();
		
		// var
		String varValue=null;
		Object value=null;
		if(!StringUtil.isEmpty(var)) {
			try {
				value = pageContext.getVariable(var);
				varValue=new ScriptConverter().serialize(pageContext.getVariable(var));
			} 
			catch (PageException e) {
				varValue="(undefined)";
			} 
			catch (ConverterException e) {
				if(value!=null)varValue="("+Caster.toTypeName(value)+")";
			}
		}
		
		DebugTrace trace = pageContext.getDebugger().addTrace(type,category,text,page,var,varValue);
		DebugTrace[] traces = pageContext.getDebugger().getTraces();
		
		String total="(1st trace)";
		if(traces.length>1) {
			long t=0;
			for(int i=0;i<traces.length;i++) {
				t+=traces[i].getTime();
			}
			total="("+t+")";
		}
		
		boolean hasCat=!StringUtil.isEmpty(trace.getCategory());
		boolean hasText=!StringUtil.isEmpty(trace.getText());
		boolean hasVar=!StringUtil.isEmpty(var);
		
		// inline
		if(inline) {
			railo.runtime.format.TimeFormat tf = new railo.runtime.format.TimeFormat(pageContext.getConfig().getLocale());
			StringBuffer sb=new StringBuffer();
			sb.append("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"white\">");
			sb.append("<tr>");
			//sb.append("<td><img src=\"/CFIDE/debug/images/Error_16x16.gif\" alt=\"Error type\">");
			sb.append("<td>");
			sb.append("<font color=\"orange\">");
			sb.append("<b>");
			sb.append(LogUtil.toStringType(trace.getType(),"INFO")+" - ");
			sb.append("[CFTRACE "+tf.format(new DateTimeImpl(pageContext.getConfig()), "hh:mm:ss:l")+"]");
			sb.append("["+trace.getTime()+" ms "+total+"]");
			sb.append("["+trace.getTemplate()+" @ line: "+trace.getLine()+"]");
			if(hasCat || hasText)sb.append(" -");
			if(hasCat)sb.append("  ["+trace.getCategory()+"]");
			if(hasText)sb.append(" <i>"+trace.getText()+"&nbsp;</i>");
			sb.append("</b>");
			sb.append("</font>");
			sb.append("</td>");
			sb.append("</tr>");
			sb.append("</table>");
			pageContext.write(sb.toString());
			
			if(hasVar)Dump.call(pageContext, value, var);
			
		}
		
		// log
		LogAndSource log = pageContext.getConfig().getTraceLogger();
		StringBuffer msg=new StringBuffer();
		msg.append("["+trace.getTime()+" ms "+total+"] ");
		msg.append("["+trace.getTemplate()+" @ line: "+trace.getLine()+"]");
		if(hasCat || hasText || hasVar) msg.append("- ");
		if(hasCat)msg.append("["+trace.getCategory()+"] ");
		if(hasVar)msg.append("["+var+"="+varValue+"] ");
		if(hasText)msg.append(" "+trace.getText()+" ");
		log.log(trace.getType(), "cftrace", msg.toString());
		
		// abort
		if(abort) throw new Abort(Abort.SCOPE_REQUEST);
		
	}
	
	/**
	* @see javax.servlet.jsp.tagext.BodyTag#doInitBody()
	*/
	public void doInitBody()	{
		
	}

	/**
	* @see javax.servlet.jsp.tagext.BodyTag#doAfterBody()
	*/
	public int doAfterBody()	{
		return SKIP_BODY;
	}

	/**
	 * sets if has body or not
	 * @param hasBody
	 */
	public void hasBody(boolean hasBody) {
	    
	}
}
