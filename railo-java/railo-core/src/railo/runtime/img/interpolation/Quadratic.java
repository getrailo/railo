package railo.runtime.img.interpolation;

public class Quadratic implements Interpolation
{
    public double f(double x) {
	if (x < 0.0)
	    x = -x;
	if (x < 0.5)
	    return 0.75 - x * x;
	if (x < 1.5) {
	    x -= 1.5;
	    return 0.5 * x * x;
	}
	return 0.0;
    }
    
    public double getSupport() {
	return 1.5;
    }
}
