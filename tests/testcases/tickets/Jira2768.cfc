<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{

	//public function setUp(){}

	public void function test(){
		http method="get" result="local.result" url="#createURL("Jira2768/index.cfm?+")#" addtoken="false";
		assertEquals(200,result.responseheader.status_code);
		/*
		
		try{
			// error
			fail("");
		}
		catch(local.exp){}*/
	}
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
	
} 
</cfscript>