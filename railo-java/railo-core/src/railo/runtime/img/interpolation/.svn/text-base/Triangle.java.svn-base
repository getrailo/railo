package railo.runtime.img.interpolation;


public class Triangle implements Interpolation
{
    public double f(double x) {
	if (x < 0.0)
	    x = -x;
	if (x < 1.0)
	    return 1.0 - x;
	return 0.0;
    }
    
    public double getSupport() {
	return 1.0;
    }
}
