<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{

	public function setUp(){
		variables.baseURL=createURL("Jira2644/");
		
	}

	public void function testCompositeId(){
		// first call only initialize the data
		http method="get" result="local.result" url="#variables.baseURL#one.cfm" addtoken="false";
		assertEquals(200,result.status_code);
		assertEquals('',trim(result.filecontent));
		
		// now get the result
		http method="get" result="local.result" url="#variables.baseURL#two.cfm" addtoken="false";
		local.res=evaluate(trim(result.filecontent));
		setting showdebugoutput="false";
		
		assertEquals('CA',res.getStateCode()&"");
		assertEquals('US',res.getCountryCode()&"");	
	}
	
	
	public void function testCompositeId2(){
		// first call only initialize the data
		http method="get" result="local.result" url="#variables.baseURL#bestseller.cfm" addtoken="false";
		assertEquals(200,result.status_code);
	}
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
	
} 
</cfscript>