component extends="org.railo.cfml.test.RailoTestCase"	{


	public void function testPrecisionEvaluate(){
		
		var p1 = 23.74;
		var p2 = 37.05;

		var pe = precisionEvaluate(p1 + p2);
		
		assertEquals( pe, 60.79 );
	}
	
	
}