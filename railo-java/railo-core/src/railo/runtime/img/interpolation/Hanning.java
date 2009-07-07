package railo.runtime.img.interpolation;

public class Hanning implements Interpolation
{
    public double f(double x) {
	return 0.5 + 0.5 * Math.cos(3.141592653589793 * x);
    }
    
    public double getSupport() {
	return 1.0;
    }
}
