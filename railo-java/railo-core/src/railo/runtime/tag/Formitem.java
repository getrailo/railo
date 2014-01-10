package railo.runtime.tag;

import railo.runtime.exp.TagNotSupported;
import railo.runtime.ext.tag.BodyTagImpl;

public final class Formitem extends BodyTagImpl {

	private int type;
	private String style;
	private int width=-1;
	private int height=-1;
	private boolean enabled=true;
	private boolean visible=true;
	private String tooltip;
	private  String bind;
	

	public Formitem() throws TagNotSupported {
		throw new TagNotSupported("formitem");
		// TODO impl. Tag formItem
	}
	
	@Override
	public void release() {
		super.release();
		style=null;
		width=-1;
		height=-1;
		enabled=true;
		visible=true;
		tooltip=null;
		bind=null;
		
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		//this.type = type;
	}

	/**
	 * @param bind the bind to set
	 */
	public void setBind(String bind) {
		this.bind = bind;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(double height) {
		this.height = (int) height;
	}

	/**
	 * @param style the style to set
	 */
	public void setStyle(String style) {
		this.style = style;
	}

	/**
	 * @param tooltip the tooltip to set
	 */
	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	/**
	 * @param visible the visible to set
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(double width) {
		this.width = (int) width;
	}


}
