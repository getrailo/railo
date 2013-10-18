<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{

	public function setUp(){
		
	}
	private function returnNull(){ }
	private function returnSusi(){return "Susi";}
	
	
	
	public void function testFunction(){
		assertEquals("Else",returnNull()?:"Else");
		assertEquals("Susi",returnSusi()?:"Else"); 
	}
	
	public void function testFunctionEvaluate(){
		assertEquals("Else",evaluate('returnNull()?:"Else"'));
		assertEquals("Susi",evaluate('returnSusi()?:"Else"')); 
	}

	public void function testVariables(){
		var d="b";
		var a.b.c="abc";
		
		assertEquals("Else",_does._not._exists?:"Else");
		assertEquals("abc",a.b.c?:"Else");
		assertEquals("abc",a[d].c?:"Else");
	}
	
	public void function testVariablesWithEvaluate(){
		var d="b";
		var a.b.c="abc";
		
		assertEquals("Else",evaluate('_does._not._exists?:"Else"'));
		assertEquals("abc",evaluate('a.b.c?:"Else"'));
		assertEquals("abc",evaluate('a[d].c?:"Else"'));
	}
} 
</cfscript>