component extends="org.railo.cfml.test.RailoTestCase"	{

	public function setUp(){
		file action="touch" file="#getCurrentTemplatePath()#";
	}

	public void function testPrecisionEvaluateString(){
		
		var p1 = 23.74;
		var p2 = 37.05;

		var pe = precisionEvaluate('p1 + p2');
		assertEquals( pe, 60.79 );
	}
	
	public void function testPrecisionEvaluate(){
		
		var p1 = 23.74;
		var p2 = 37.05;

		var pe = precisionEvaluate(p1 + p2);
		assertEquals( pe, 60.79 );
	}
	
	
	
	public void function testPrecisionEvaluateIncrement(){
		
		var p1 = 22.74;
		var p2 = 37.05;

		var pe = precisionEvaluate(++p1 + p2);
		assertEquals( pe, 60.79 );
	}
	
	
	public void function testPrecisionEvaluateDecrement(){
		
		var p1 = 24.74;
		var p2 = 37.05;

		var pe = precisionEvaluate(--p1 + p2);
		assertEquals( pe, 60.79 );
	}
	
	public void function testPrecisionEvaluateAssign(){
		
		var p1 = 23.74;
		var p2 = 37.05;

		var pe = precisionEvaluate((p3=p1 + p2));
		assertEquals( pe, 60.79 );
	}
	
}