package railo.commons.lang;

public class IDGenerator {
	
	private static int id;
	
	public static synchronized int intId(){
		id++;
		if(id==Integer.MAX_VALUE) id=0;
		return id;
	}
}
