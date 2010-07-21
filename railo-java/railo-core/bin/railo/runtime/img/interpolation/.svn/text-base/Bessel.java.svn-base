package railo.runtime.img.interpolation;

public class Bessel implements Interpolation
{
    private double J1(double x) {
	double[] Pone
	    = { 5.811993540016061E20, -6.672106568924916E19,
		2.3164335806340024E18, -3.588817569910106E16,
		2.9087952638347756E14, -1.3229834803321265E12,
		3.4132341823017006E9, -4695753.530642996, 2701.1227108923235 };
	double[] Qone = { 1.1623987080032122E21, 1.185770712190321E19,
			  6.0920613989175216E16, 2.0816612213076075E14,
			  5.2437102621676495E11, 1.013863514358674E9,
			  1501793.5949985855, 1606.9315734814877, 1.0 };
	double p = Pone[8];
	double q = Qone[8];
	for (int i = 7; i >= 0; i--) {
	    p = p * x * x + Pone[i];
	    q = q * x * x + Qone[i];
	}
	return p / q;
    }
    
    private double P1(double x) {
	double[] Pone
	    = { 35224.66491336798, 62758.84524716128, 31353.963110915956,
		4985.4832060594335, 211.15291828539623, 1.2571716929145342 };
	double[] Qone
	    = { 35224.66491336798, 62694.34695935605, 31240.406381904104,
		4930.396490181089, 203.07751891347593, 1.0 };
	double p = Pone[5];
	double q = Qone[5];
	for (int i = 4; i >= 0; i--) {
	    p = p * (8.0 / x) * (8.0 / x) + Pone[i];
	    q = q * (8.0 / x) * (8.0 / x) + Qone[i];
	}
	return p / q;
    }
    
    private double Q1(double x) {
	double[] Pone
	    = { 351.17519143035526, 721.0391804904475, 425.98730116544425,
		83.18989576738508, 4.568171629551227, 0.03532840052740124 };
	double[] Qone
	    = { 7491.737417180912, 15414.177339265098, 9152.231701516992,
		1811.1867005523513, 103.81875854621337, 1.0 };
	double p = Pone[5];
	double q = Qone[5];
	for (int i = 4; i >= 0; i--) {
	    p = p * (8.0 / x) * (8.0 / x) + Pone[i];
	    q = q * (8.0 / x) * (8.0 / x) + Qone[i];
	}
	return p / q;
    }
    
    private double BesselOrderOne(double x) {
	if (x == 0.0)
	    return 0.0;
	double p = x;
	if (x < 0.0)
	    x = -x;
	if (x < 8.0)
	    return p * J1(x);
	double q
	    = (Math.sqrt(2.0 / (3.141592653589793 * x))
	       * (P1(x) * (1.0 / Math.sqrt(2.0) * (Math.sin(x) - Math.cos(x)))
		  - 8.0 / x * Q1(x) * (-1.0 / Math.sqrt(2.0)
				       * (Math.sin(x) + Math.cos(x)))));
	if (p < 0.0)
	    q = -q;
	return q;
    }
    
    public double f(double x) {
	if (x == 0.0)
	    return 0.7853981633974483;
	return BesselOrderOne(3.141592653589793 * x) / (2.0 * x);
    }
    
    public double getSupport() {
	return 3.2383;
    }
}
