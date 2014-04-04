<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	public void function test()  {
		savecontent variable="local.c" {
			iterations = 0;
			[1,2,3,4].each(function(v,i) localmode="modern" {
				iterations++;
				writeOutput("#iterations#-");
			});
		} 
		assertEquals("1-2-3-4-",c);
	}

	public void function testPostIncrement(){
		url._testPostIncrement=1;
		assertEquals(1,_testPostIncrement++);
		assertEquals(2,url._testPostIncrement);
	}
	public void function testPreIncrement(){
		url._testPreIncrement=1;
		assertEquals(2,++_testPreIncrement);
		assertEquals(2,url._testPreIncrement);
	}
	public void function testPostDecrement(){
		url._testPostDecrement=1;
		assertEquals(1,_testPostDecrement--);
		assertEquals(0,url._testPostDecrement);
	}
	public void function testPreDecrement(){
		url._testPreDecrement=1;
		assertEquals(0,--_testPreDecrement);
		assertEquals(0,url._testPreDecrement);
	}

	public void function testPlusAssignment(){
		url._testPlusAssignment=1;
		assertEquals(11,_testPlusAssignment+=10);
		//dump(variables);abort;
		assertEquals(11,url._testPlusAssignment);

	} 

	public void function testMinusAssignment(){
		url._testMinusAssignment=1;
		assertEquals(-9,_testMinusAssignment-=10);
		assertEquals(-9,url._testMinusAssignment);
	}
 
	public void function testDivideAssignment(){
		url._testDivAssignment=4;
		assertEquals(2,_testDivAssignment/=2);
		assertEquals(2,url._testDivAssignment); 
	}

	public void function testMultiplyAssignment(){
		url._testMultiplyAssignment=2;
		assertEquals(4,_testMultiplyAssignment*=2);
		assertEquals(4,url._testMultiplyAssignment);
	}

	public void function testConcatAssignment(){
		url._testConcatAssignment="Susi";
		assertEquals("Susi Sorglos",_testConcatAssignment&=" Sorglos");
		assertEquals("Susi Sorglos",url._testConcatAssignment);
	} 

} 
</cfscript>