package railo.runtime.tag;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.commons.lang.SystemOut;
import railo.runtime.PageContextImpl;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.TemplateException;
import railo.runtime.ext.tag.BodyTagImpl;
import railo.runtime.net.http.ReqRspUtil;
import railo.runtime.op.Caster;
import railo.runtime.type.List;

/**
* Defines the MIME type returned by the current page. Optionally, lets you specify the name of a file
*   to be returned with the page.
*
*
*
**/
public final class Content extends BodyTagImpl {

	private static final int RANGE_NONE = 0;
	private static final int RANGE_YES = 1;
	private static final int RANGE_NO = 2;

	/** Defines the File/ MIME content type returned by the current page. */
	private String type;

	/** The name of the file being retrieved */
	private String strFile;

	/** Yes or No. Yes discards output that precedes the call to cfcontent. No preserves the output that precedes the call. Defaults to Yes. The reset 
	** 		and file attributes are mutually exclusive. If you specify a file, the reset attribute has no effect. */
	private boolean reset=true;
	
	private int _range=RANGE_NONE;

	/** Yes or No. Yes deletes the file after the download operation. Defaults to No. 
	** 	This attribute applies only if you specify a file with the file attribute. */
	private boolean deletefile=false;

    private byte[] content;


    /**
    * @see javax.servlet.jsp.tagext.Tag#release()
    */
    public void release()   {
        super.release();
        type=null;
        strFile=null;
        reset=true;
        deletefile=false;
        content=null;
        _range=RANGE_NONE;
    }

	/** set the value type
	*  Defines the File/ MIME content type returned by the current page.
	* @param type value to set
	**/
	public void setType(String type)	{
		this.type=type.trim();
	}
	
	public void setRange(boolean range)	{
		this._range=range?RANGE_YES:RANGE_NO;
	}

    /** set the value file
    *  The name of the file being retrieved
    * @param file value to set
    **/
    public void setFile(String file)    {
        this.strFile=file;
    }

    /** 
    * the content to output as binary
    * @param content value to set
    * @deprecated replaced with <code>{@link #setVariable(String)}</code>
    **/
    public void setContent(byte[] content)    {
        this.content=content;
    }
    
    public void setVariable(Object variable) throws PageException    {
    	if(variable instanceof String)
    		this.content=Caster.toBinary(pageContext.getVariable((String)variable));
    	else
    		this.content=Caster.toBinary(variable);
    }

	/** set the value reset
	*  Yes or No. Yes discards output that precedes the call to cfcontent. No preserves the output that precedes the call. Defaults to Yes. The reset 
	* 		and file attributes are mutually exclusive. If you specify a file, the reset attribute has no effect.
	* @param reset value to set
	**/
	public void setReset(boolean reset)	{
		this.reset=reset;
	}

	/** set the value deletefile
	*  Yes or No. Yes deletes the file after the download operation. Defaults to No. 
	* 	This attribute applies only if you specify a file with the file attribute.
	* @param deletefile value to set
	**/
	public void setDeletefile(boolean deletefile)	{
		this.deletefile=deletefile;
	}


	/**
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	*/
    public int doStartTag() throws PageException   {
        //try {
            return _doStartTag();
        /*} 
        catch (IOException e) {
            throw Caster.toPageException(e);
        }*/
    }
    private int _doStartTag() throws PageException   {
        
		// get response object
		HttpServletResponse rsp = pageContext. getHttpServletResponse();
	    
        // check commited
        if(rsp.isCommitted())
            throw new ApplicationException("content ist already flushed","you can't rewrite head of response after the page is flushed");
        
        // set type
        setContentType(rsp);
        
        Range[] ranges=getRanges();
        boolean hasRanges=ranges!=null && ranges.length>0;
        if(_range==RANGE_YES || hasRanges){
            rsp.setHeader("Accept-Ranges", "bytes");
        }
        else if(_range==RANGE_NO) {
            rsp.setHeader("Accept-Ranges", "none");
            hasRanges=false;
        }	
        
        
        // set content
        if(this.content!=null || !StringUtil.isEmpty(strFile)) {
            pageContext.clear();
        	Resource file=null;
            InputStream is=null;
            OutputStream os=null;
            long length;
            try {
            	os=getOutputStream();
            	
            	if(content!=null) {
            		ReqRspUtil.setContentLength(rsp,content.length);
                    length=content.length;
                     is=new BufferedInputStream(new ByteArrayInputStream(content));  
                }
                else {
                    file = ResourceUtil.toResourceExisting(pageContext,strFile);
                    ReqRspUtil.setContentLength(rsp,file.length());
                    pageContext.getConfig().getSecurityManager().checkFileLocation(file);
                    length=file.length();
                    is=IOUtil.toBufferedInputStream(file.getInputStream());
                }
            	
            	// write
            	if(!hasRanges)
            		IOUtil.copy(is,os,false,false);
            	else {
            		//print.out("do part");
            		//print.out(ranges);
            		long off,len;
            		long to;
            		for(int i=0;i<ranges.length;i++) {
            			off=ranges[i].from;
            			if(ranges[i].to==-1) {
            				len=-1;
            				to=length;
            			}
            			else {
            				len=ranges[i].to-ranges[i].from+1;
            				to=ranges[i].to;
            			}
            			rsp.addHeader("Content-Range", "bytes "+off+"-"+to+"/"+Caster.toString(length));	
            			//print.out("Content-Range: bytes "+off+"-"+to+"/"+Caster.toString(length));
            			IOUtil.copy(is, os,off,len);
            		}
            	}
            } 
            catch(IOException ioe) {}
            finally {
                IOUtil.flushEL(os);
                IOUtil.closeEL(is,os);
                if(deletefile && file!=null) file.delete();
                ((PageContextImpl)pageContext).getRootOut().setClosed(true);
            }
            throw new railo.runtime.exp.Abort(railo.runtime.exp.Abort.SCOPE_REQUEST);
        }
        // clear current content
        else if(reset)pageContext.clear();
        
        return EVAL_BODY_INCLUDE;//EVAL_PAGE;
	}

	private OutputStream getOutputStream() throws PageException, IOException {
        try {
        	return ((PageContextImpl)pageContext).getServletOutputStream();
        } 
        catch(IllegalStateException ise) {
            throw new TemplateException("content is already send to user, flush");
        }
    }

    /**
	* @see javax.servlet.jsp.tagext.Tag#doEndTag()
	*/
	public int doEndTag()	{
		return strFile == null ? EVAL_PAGE : SKIP_PAGE;
	}

	
	/**
	 * set the content type of the side
	 * @param rsp HTTP Servlet Response object
	 */
	private void setContentType(HttpServletResponse rsp) {
        if(!StringUtil.isEmpty(type)) {
        	rsp.setContentType(type);
        }
	}

    /**
     * sets if tag has a body or not
     * @param hasBody
     */
    public void hasBody(boolean hasBody) {
    }
    


	private Range[] getRanges() {
		HttpServletRequest req = pageContext.getHttpServletRequest();
		Enumeration names = req.getHeaderNames();
		if(names==null) return null;
		String name;
		Range[] range;
		while(names.hasMoreElements()) {
			name=(String) names.nextElement();
			//print.out("header:"+name);
			if("range".equalsIgnoreCase(name)){
				range = getRanges(name,req.getHeader(name));
				if(range!=null) return range;
			}
		}
		return null;
	}
	private Range[] getRanges(String name,String range) {
		if(StringUtil.isEmpty(range, true)) return null;
		range=StringUtil.removeWhiteSpace(range);
		if(range.indexOf("bytes=")==0) range=range.substring(6);
		String[] arr=null;
		try {
			arr = List.toStringArray(List.listToArrayRemoveEmpty(range, ','));
		} catch (PageException e) {
			failRange(name,range);
			return null;
		}
		String item;
		int index;
		long from,to;
		
		Range[] ranges=new Range[arr.length];
		for(int i=0;i<ranges.length;i++) {
			item=arr[i].trim();
			index=item.indexOf('-');
			if(index!=-1) {
				from = Caster.toLongValue(item.substring(0,index),0);
				to = Caster.toLongValue(item.substring(index+1),-1);
				if(to!=-1 && from>to){
					failRange(name,range);
					return null;
					//throw new ExpressionException("invalid range definition, from have to bigger than to ("+from+"-"+to+")");
				}
			}
			else {
				from = Caster.toLongValue(item,0);
				to=-1;
			}
			ranges[i]=new Range(from,to);
			
			if(i>0 && ranges[i-1].to>=from){
				PrintWriter err = pageContext.getConfig().getErrWriter();
				SystemOut.printDate(err,"there is a overlapping of 2 ranges ("+ranges[i-1]+","+ranges[i]+")");
				//throw new ExpressionException("there is a overlapping of 2 ranges ("+ranges[i-1]+","+ranges[i]+")");
				return null;
			}
			
		}
		return ranges;
	}

	private void failRange(String name, String range) {
		PrintWriter err = pageContext.getConfig().getErrWriter();
		SystemOut.printDate(err,"fails to parse the header field ["+name+":"+range+"]");
	}
}
class Range {
	long from;
	long to;
	public Range(long from, long len) {
		this.from = from;
		this.to = len;
	}

	public String toString() {
		return from+"-"+to;
	}
}