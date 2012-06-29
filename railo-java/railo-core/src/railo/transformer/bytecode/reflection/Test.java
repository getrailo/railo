package railo.transformer.bytecode.reflection;

public class Test {
	public void testVoid(int a,Integer b){
		Class x=Integer.class;
		if(x==null)return;
	}
	public Class testClass(int a,Integer b){
		return int[].class;
	}
	public void testVoid2(int[] c,Integer[] d) throws InterruptedException{
		wait();
	}
	public void testVoid23(){
		
	}
	public Object testInt(){return true;}
}
