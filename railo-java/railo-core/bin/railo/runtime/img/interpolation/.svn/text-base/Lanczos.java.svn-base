package railo.runtime.img.interpolation;

public class Lanczos implements Interpolation
{
    public double f(double x) {
	if (x < -3.0)
	    return 0.0;
	if (x < 0.0)
	    return sinc(-x) * sinc(-x / 3.0);
	if (x < 3.0)
	    return sinc(x) * sinc(x / 3.0);
	return 0.0;
    }
    
    public double sinc(double x) {
	x *= 3.141592653589793;
	if (x != 0.0)
	    return Math.sin(x) / x;
	return 1.0;
    }
    
    public double getSupport() {
	return 3.0;
    }
}
