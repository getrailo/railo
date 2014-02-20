package railo.transformer.bytecode;

public class Range {

	public final int from;
	public final int to;

	public Range(int from, int to) {
		this.from=from;
		this.to=to;
	}
	public String toString(){
		return "from:"+from+";to:"+to+";";
	}
}