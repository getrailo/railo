<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{

	public function setUp(){
		
	}
	
	public void function test1(){
		assertEquals("true:false",cachedFunction("a;b","c"));
	}
	
	public void function test2(){
		assertEquals("false:true",cachedFunction("a","b;c"));
	}

	private string function cachedfunction(required param1, required param2)  cachedWithin="#createTimeSpan(0,0,0,1)#" {
		return (find(";",param1)>0)&":"&(find(";",param2)>0);
	}
		
} 
</cfscript>
