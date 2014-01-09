<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{

	//public function setUp(){}

	public void function testApplicationCFC(){
		http method="get" result="local.result" url="#createURL("Jira2770/modern/index.cfm")#" addtoken="false";
		assertEquals(500,result.status_code);
	}
	
	public void function testCFApplication(){
		http method="get" result="local.result" url="#createURL("Jira2770/classic/index.cfm")#" addtoken="false";
		assertEquals(500,result.status_code);
	}
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
	
} 
</cfscript>