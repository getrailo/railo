component extends="org.railo.cfml.test.RailoTestCase"	{


	public void function testPrecisionEvaluateString(){
		
		var p1 = 23.74;
		var p2 = 37.05;

		var pe = precisionEvaluate('p1 + p2');
		assertEquals( pe, 60.79 );
	}
	
	public void function testPrecisionEvaluate(){
		
		var p1 = 23.74;
		var p2 = 37.05;

		var pe = precisionEvaluate('p1 + p2');
		assertEquals( pe, 60.79 );
		
		pe = precisionEvaluate(p1 + p2);
		assertEquals( pe, 60.79 );
	}
	
	
}