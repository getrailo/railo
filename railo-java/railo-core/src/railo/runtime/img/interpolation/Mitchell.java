package railo.runtime.img.interpolation;

public class Mitchell implements Interpolation
{
    public double f(double x) {
	double b = 0.3333333333333333;
	double c = 0.3333333333333333;
	if (x < 0.0)
	    x = -x;
	if (x < 1.0) {
	    x = ((12.0 - 9.0 * b - 6.0 * c) * (x * x * x)
		 + (-18.0 + 12.0 * b + 6.0 * c) * x * x + (6.0 - 2.0 * b));
	    return x / 6.0;
	}
	if (x < 2.0) {
	    x = ((-1.0 * b - 6.0 * c) * (x * x * x)
		 + (6.0 * b + 30.0 * c) * x * x + (-12.0 * b - 48.0 * c) * x
		 + (8.0 * b + 24.0 * c));
	    return x / 6.0;
	}
	return 0.0;
    }
    
    public double getSupport() {
	return 2.0;
    }
}
