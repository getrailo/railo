package railo.runtime.concurrency;

public final class Data<P> {

	public final String output;
	public final Object result;
	public final P passed; 

	public Data(String output, Object result, P passed) {
		this.output=output;
		this.result=result;
		this.passed=passed;
	}
	
}
