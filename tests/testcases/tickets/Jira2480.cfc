<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{

	public function setUp(){
	}
	
	
	private Jira2480 function _test(boolean returnNull=false){
		if(returnNull)return nullValue(); 
		return this;
	}

	public void function test(){
		_test(false);
		_test(true);
		//assertEquals("",);
	}
} 
</cfscript>