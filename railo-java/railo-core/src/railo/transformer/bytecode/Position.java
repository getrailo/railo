package railo.transformer.bytecode;


public class Position {

	public final int line;
	public final int column;
	public final int pos;

	public Position(int line, int column, int position){
		//print.e(line+":"+column+":"+position);
		this.line=line;
		this.column=column;
		this.pos=position;
	}
}
