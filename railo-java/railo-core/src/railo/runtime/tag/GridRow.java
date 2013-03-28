package railo.runtime.tag;

import javax.servlet.jsp.tagext.Tag;

import railo.runtime.exp.TagNotSupported;
import railo.runtime.ext.tag.TagImpl;
import railo.runtime.type.util.ListUtil;

/**
* Lets you define a cfgrid that does not use a query as source for row data. If a query attribute is
*   specified in cfgrid, the cfgridrow tags are ignored.
*
*
*
**/
public final class GridRow extends TagImpl {
	

	public GridRow() throws TagNotSupported {
		throw new TagNotSupported("GridRow");
	}

	/** A comma-separated list of column values. If a column value contains a comma character, 
	** 	it must be escaped with a second comma character. */
	private String[] data;

	@Override
	public void release()	{
		super.release();
		data=null;
	}
	
	/** set the value data
	*  A comma-separated list of column values. If a column value contains a comma character, 
	* 	it must be escaped with a second comma character.
	* @param data value to set
	**/
	public void setData(String data)	{
		this.data=ListUtil.listToStringArray(data, ',');
	}


	@Override
	public int doStartTag()	{
		// provide to parent
		Tag parent=this;
		do{
			parent = parent.getParent();
			if(parent instanceof Grid) {
				((Grid)parent).addRow(data);
				break;
			}
		}
		while(parent!=null);
		
		return SKIP_BODY;
	}

	@Override
	public int doEndTag()	{
		return EVAL_PAGE;
	}

}