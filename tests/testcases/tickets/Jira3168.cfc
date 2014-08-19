<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{
	
	//public function beforeTests(){}
	
	//public function afterTests(){}
	
	//public function setUp(){}

	public void function testDoWhile(){
		var i=1;
  		do
			writeOutput("");
		while(++i <= 5);
	
		assertEquals(6,i);
	}

	public void function testWhile(){
		var i=1;
  		while(++i <= 5)
			writeOutput("");
		
	
		assertEquals(6,i);
	}

	public void function testFor(){
		var i=1;
  		for(;i<=5;i++)
			writeOutput("");
		
	
		assertEquals(6,i);
	}
} 
</cfscript>