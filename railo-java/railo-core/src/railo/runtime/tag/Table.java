package railo.runtime.tag;

import java.io.IOException;

import railo.commons.lang.StringUtil;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.tag.BodyTagTryCatchFinallyImpl;
import railo.runtime.functions.string.CJustify;
import railo.runtime.functions.string.LJustify;
import railo.runtime.functions.string.RJustify;
import railo.runtime.op.Caster;

/**
* Builds a table in a CFML page. Use the cfcol tag to define table column and row 
*   characteristics. The cftable tag renders data either as preformatted text, or, with the HTMLTable 
*   attribute, as an HTML table. Use cftable to create tables if you don't want to write HTML table tag 
*   code, or if your data can be well presented as preformatted text.
*
*
*
**/
public final class Table extends BodyTagTryCatchFinallyImpl {
    
    /**
     * Field <code>ALIGN_LEFT</code>
     */
    public static final short ALIGN_LEFT=0;
    /**
     * Field <code>ALIGN_CENTER</code>
     */
    public static final short ALIGN_CENTER=1;
    /**
     * Field <code>ALIGN_RIGHT</code>
     */
    public static final short ALIGN_RIGHT=2;

	/** Name of the cfquery from which to draw data. */
	private railo.runtime.type.Query query;

	/** Maximum number of rows to display in the table. */
	private int maxrows=Integer.MAX_VALUE;

	/** Specifies the query row from which to start processing. */
	private int startrow=1;

	/** Adds a border to the table. Use only when you specify the HTMLTable attribute for the table. */
	private boolean border;

	/** Displays headers for each column, as specified in the cfcol tag. */
	private boolean colheaders;

	/** Number of spaces to insert between columns 'default is 2'. */
	private int colspacing=2;

	/** Renders the table as an HTML 3.0 table. */
	private boolean htmltable;

	/** Number of lines to use for the table header. The default is 2, which leaves one line between 
	** 		the headers and the first row of the table. */
	private int headerlines=2;

	StringBuffer header=new StringBuffer();
	StringBuffer body=new StringBuffer();

    private int initRow;

    private int count=0;
	private boolean startNewRow;

	/**
	* @see javax.servlet.jsp.tagext.Tag#release()
	*/
	public void release()	{
		super.release();
		query=null;
		maxrows=Integer.MAX_VALUE;
		startrow=1;
		border=false;
		colheaders=false;
		colspacing=2;
		htmltable=false;
		headerlines=2;
		if(header.length()>0)header=new StringBuffer();
		body=new StringBuffer();
		count=0;
	}

	/** set the value query
	*  Name of the cfquery from which to draw data.
	* @param query value to set
	 * @throws PageException
	**/
	public void setQuery(String query) throws PageException	{
		this.query = Caster.toQuery(pageContext.getVariable(query));
	}

	/** set the value maxrows
	*  Maximum number of rows to display in the table.
	* @param maxrows value to set
	**/
	public void setMaxrows(double maxrows)	{
		this.maxrows=(int)maxrows;
	}

	/** set the value startrow
	*  Specifies the query row from which to start processing.
	* @param startrow value to set
	**/
	public void setStartrow(double startrow)	{
		this.startrow=(int)startrow;
		if(this.startrow<=0)this.startrow=1;
	}

	/** set the value border
	*  Adds a border to the table. Use only when you specify the HTMLTable attribute for the table.
	* @param border value to set
	**/
	public void setBorder(boolean border)	{
		this.border=border;
	}

	/** set the value colheaders
	*  Displays headers for each column, as specified in the cfcol tag.
	* @param colheaders value to set
	**/
	public void setColheaders(boolean colheaders)	{
		this.colheaders=colheaders;
	}

	/** set the value colspacing
	*  Number of spaces to insert between columns 'default is 2'.
	* @param colspacing value to set
	**/
	public void setColspacing(double colspacing)	{
		this.colspacing=(int)colspacing;
	}

	/** set the value htmltable
	*  Renders the table as an HTML 3.0 table.
	* @param htmltable value to set
	**/
	public void setHtmltable(boolean htmltable)	{
		this.htmltable=htmltable;
	}

	/** set the value headerlines
	*  Number of lines to use for the table header. The default is 2, which leaves one line between 
	* 		the headers and the first row of the table.
	* @param headerlines value to set
	**/
	public void setHeaderlines(double headerlines)	{
		this.headerlines=(int)headerlines;
		if(this.headerlines<2)this.headerlines=2;
	}


	/**
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	*/
	public int doStartTag() throws PageException	{
		startNewRow=true;
		initRow=query.getRecordcount();
		query.go(startrow);
		pageContext.undefinedScope().addCollection(query);
		return query.getRecordcount()>=startrow?EVAL_BODY_INCLUDE:SKIP_BODY;
	}

	/**
	* @see javax.servlet.jsp.tagext.BodyTag#doInitBody()
	*/
	public void doInitBody()	{
		//if(htmltable) body.append("<tr>\n");
	}

	/**
	 * @see javax.servlet.jsp.tagext.BodyTag#doAfterBody()
	*/
	public int doAfterBody() throws PageException	{
	    if(htmltable) body.append("</tr>\n");
	    else body.append('\n');
	    startNewRow=true;
	    //print.out(query.getCurrentrow()+"-"+query.getRecordcount());
		return ++count<maxrows && query.next()?EVAL_BODY_AGAIN:SKIP_BODY;
	}

	/**
	 * @see javax.servlet.jsp.tagext.Tag#doEndTag()
	*/
	public int doEndTag() throws PageException	{
	    try {
            _doEndTag();
        } catch (IOException e) {
            throw Caster.toPageException(e);
        }
		return EVAL_PAGE;
	}
	private void _doEndTag() throws IOException	{
	    if(htmltable) {
		    pageContext.forceWrite("<table colspacing=\""+colspacing+"\"");
		    if(border)	{
		        pageContext.forceWrite(" border=\"1\"");
		    }
		    pageContext.forceWrite(">\n");
		    if(header.length()>0) {
		        pageContext.forceWrite("<tr>\n");
		        pageContext.forceWrite(header.toString());
		        pageContext.forceWrite("</tr>\n");
		    }
		    pageContext.forceWrite(body.toString());
		    pageContext.forceWrite("</table>");
		}
	    else {
	        pageContext.forceWrite("<pre>");
	        if(header.length()>0) {
		        pageContext.forceWrite(header.toString());
		        pageContext.forceWrite(StringUtil.repeatString("\n",headerlines-1));
		    }
	        pageContext.forceWrite(body.toString());
	        pageContext.forceWrite("</pre>");
	    }
	}

    /**
     * @see javax.servlet.jsp.tagext.TryCatchFinally#doFinally()
     */
    public void doFinally() {
		try {
		    pageContext.undefinedScope().removeCollection();
			if(query!=null)query.go(initRow);
		} 
		catch (PageException e) {	}
    }

    /**
     * @param strHeader
     * @param text
     * @param align
     * @param width 
     * @throws ExpressionException
     */
    public void setCol(String strHeader, String text, short align, int width) throws ExpressionException {
    // HTML
        if(htmltable) {
            if(colheaders && count==0 && strHeader.trim().length()>0) {
                header.append("\t<th");
                addAlign(header,align);
                addWidth(header,width);
                header.append(">");
                header.append(strHeader);
                header.append("</th>\n");
            }
            if(htmltable && startNewRow) {
            	body.append("<tr>\n");
            	startNewRow=false;
            }
            
            body.append("\t<td");
            addAlign(body,align);
            addWidth(body,width);
            body.append(">");
            body.append(text);
            body.append("</td>\n");
        }
    // PRE
        else {
            if(width<0) width=20;
            if(colheaders && count==0 && strHeader.trim().length()>0) {
                addPre(header,align,strHeader,width);
            }
            addPre(body,align,text,width);
            
        }
    }

    private void addAlign(StringBuffer data,short align) {
        data.append(" align=\"");
        data.append(toStringAlign(align));
        data.append("\"");
    }
    
    private void addWidth(StringBuffer data,int width) {
        if(width>=-1) {
            data.append(" width=\"");
            data.append(width);
            data.append("%\"");
        }
    }
    
    private void addPre(StringBuffer data,short align,String value, int length) throws ExpressionException {
        if(align==ALIGN_RIGHT) data.append(RJustify.call(pageContext,value,length));
        else if(align==ALIGN_CENTER) data.append(CJustify.call(pageContext,value,length));
        else data.append(LJustify.call(pageContext,value,length));
        
    }
    
    private String toStringAlign(short align) {
        if(align==ALIGN_RIGHT) return "right";
        if(align==ALIGN_CENTER) return "center";
        return "left";
    }
}