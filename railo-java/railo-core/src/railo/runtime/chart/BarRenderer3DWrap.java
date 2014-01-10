package railo.runtime.chart;

import org.jfree.chart.renderer.category.BarRenderer3D;

public class BarRenderer3DWrap extends BarRenderer3D {

	//private BarRenderer3D renderer;
	private double xoffset;
	private double yoffset;

	public BarRenderer3DWrap(BarRenderer3D renderer, double xoffset, double yoffset) {
		//this.renderer=renderer;
		this.xoffset=xoffset;
		this.yoffset=yoffset;
	}

	@Override
	public double getXOffset() {
		// TODO Auto-generated method stub
		return xoffset*100;
	}

	@Override
	public double getYOffset() {
		// TODO Auto-generated method stub
		return yoffset*100;
	}

}
