package railo.runtime.img.interpolation;

public class Hermite implements Interpolation {
    public double f(double x) {
	if (x < 0.0)
	    x = -x;
	if (x < 1.0)
	    return (2.0 * x - 3.0) * x * x + 1.0;
	return 0.0;
    }
    
    public double getSupport() {
	return 1.0;
    }
}
