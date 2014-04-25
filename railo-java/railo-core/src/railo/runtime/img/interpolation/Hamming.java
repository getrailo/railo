package railo.runtime.img.interpolation;

public class Hamming implements Interpolation
{
    @Override
	public double f(double x) {
	return 0.54 + 0.46 * Math.cos(3.141592653589793 * x);
    }
    
    @Override
	public double getSupport() {
	return 1.0;
    }
}
