<cfscript>
component extends="org.railo.cfml.test.RailoTestCase"	{

	//public function setUp(){}

	public void function testRegularFunction(){
		http method="get" result="local.result" url="#createURL("Jira2957/Test.cfc?method=test")#" addtoken="false";
		assertEquals("json",result.responseHeader["Return-Format"]);
	}

	public void function testOnMissingMethodFunction(){
		http method="get" result="local.result" url="#createURL("Jira2957/Test.cfc?method=notexists")#" addtoken="false";
		assertEquals("json",result.responseHeader["Return-Format"]);
	}
	
	private string function createURL(string calledName){
		var baseURL="http://#cgi.HTTP_HOST##getDirectoryFromPath(contractPath(getCurrenttemplatepath()))#";
		return baseURL&""&calledName;
	}
	
} 
</cfscript>