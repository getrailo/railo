package railo.runtime.img.interpolation;

public class Blackman implements Interpolation
{
    public double f(double x) {
	return (0.42 + 0.5 * Math.cos(3.141592653589793 * x)
		+ 0.08 * Math.cos(6.283185307179586 * x));
    }
    
    public double getSupport() {
	return 1.0;
    }
}
