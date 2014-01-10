package railo.runtime.tag;

import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.tag.TagImpl;

/**
* Specifies a data point to be displayed by a cfgraph tag.
*
*
*
**/
public final class GraphData extends TagImpl {

	/** The item label for the data point. The item labels appear on the horizontal axis of Line and 
	** 		Bar charts, the vertical axis of Horizontal Bar charts, and in the legend of Pie charts. */
	private String item;

	/** The color to use when graphing the data point. The default is to use the values from the cfgraph
	** 		tag colorlist attribute or the built-in default list of colors. Line graphs ignore this attribute. */
	private String color;

	/** Value to be represented by the data point. */
	private String value;

	/** A URL to load when the user clicks the data point. This attribute works with Pie, Bar, and 
	** 		HorizontalBar charts. This attribute has an effect only if the graph is in Flash file format. */
	private String url;


	/**
	* constructor for the tag class
	**/
	public GraphData() throws ExpressionException {
		throw new ExpressionException("tag cfgraphdata is deprecated");
	}

	/** set the value item
	*  The item label for the data point. The item labels appear on the horizontal axis of Line and 
	* 		Bar charts, the vertical axis of Horizontal Bar charts, and in the legend of Pie charts.
	* @param item value to set
	**/
	public void setItem(String item)	{
		this.item=item;
	}

	/** set the value color
	*  The color to use when graphing the data point. The default is to use the values from the cfgraph
	* 		tag colorlist attribute or the built-in default list of colors. Line graphs ignore this attribute.
	* @param color value to set
	**/
	public void setColor(String color)	{
		this.color=color;
	}

	/** set the value value
	*  Value to be represented by the data point.
	* @param value value to set
	**/
	public void setValue(String value)	{
		this.value=value;
	}

	/** set the value url
	*  A URL to load when the user clicks the data point. This attribute works with Pie, Bar, and 
	* 		HorizontalBar charts. This attribute has an effect only if the graph is in Flash file format.
	* @param url value to set
	**/
	public void setUrl(String url)	{
		this.url=url;
	}


	@Override
	public int doStartTag()	{
		return SKIP_BODY;
	}

	@Override
	public int doEndTag()	{
		return EVAL_PAGE;
	}

	@Override
	public void release()	{
		super.release();
		item="";
		color="";
		value="";
		url="";
	}
}